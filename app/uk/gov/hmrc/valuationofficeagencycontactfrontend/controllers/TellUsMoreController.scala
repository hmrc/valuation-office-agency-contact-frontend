/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers

import javax.inject.Inject
import play.api.Logger
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.MessagesControllerComponents
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.TellUsMoreForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers.{ExistingEnquiryCategoryId, TellUsMoreId}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.journey.pages.EnglandOrWalesPropertyRouter
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.{Mode, NormalMode, TellUsMore}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.UserAnswers
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{tellUsMore => tell_us_more}

import scala.concurrent.{ExecutionContext, Future}
import play.api.mvc
import play.api.mvc.AnyContent

class TellUsMoreController @Inject() (
  appConfig: FrontendAppConfig,
  override val messagesApi: MessagesApi,
  dataCacheConnector: DataCacheConnector,
  navigator: Navigator,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  tellUsMore: tell_us_more,
  cc: MessagesControllerComponents
) extends FrontendController(cc)
  with I18nSupport {

  private val log = Logger(this.getClass)

  implicit val ec: ExecutionContext = cc.executionContext

  def initAndStart: mvc.Action[AnyContent] = getData.async { implicit request =>
    dataCacheConnector
      .save[String](request.sessionId, ExistingEnquiryCategoryId.toString, "other")
      .map(_ => Redirect(routes.RefNumberController.onPageLoad().url))
  }

  def onPageLoad(mode: Mode): mvc.Action[AnyContent] = (getData andThen requireData) {
    implicit request =>
      val key = requiredErrorMessage(request.userAnswers)

      val preparedForm = request.userAnswers.tellUsMore match {
        case None        => TellUsMoreForm(key)
        case Some(value) => TellUsMoreForm(key).fill(value)
      }

      Ok(tellUsMore(appConfig, preparedForm, mode, getEnquiryKey(request.userAnswers), backLink(request.userAnswers, mode)))
  }

  def onSubmit(mode: Mode): mvc.Action[AnyContent] = (getData andThen requireData).async {
    implicit request =>
      TellUsMoreForm(requiredErrorMessage(request.userAnswers)).bindFromRequest().fold(
        (formWithErrors: Form[TellUsMore]) =>
          Future.successful(BadRequest(tellUsMore(appConfig, formWithErrors, mode, getEnquiryKey(request.userAnswers), backLink(request.userAnswers, mode)))),
        value =>
          dataCacheConnector.save[TellUsMore](request.sessionId, TellUsMoreId.toString, value).map(cacheMap =>
            Redirect(navigator.nextPage(TellUsMoreId, mode).apply(new UserAnswers(cacheMap)))
          )
      )
  }

  private def requiredErrorMessage(userAnswers: UserAnswers): String =
    if userAnswers.propertyWindEnquiry.isDefined then
      "error.tellUsMore.poorRepair.required"
    else if userAnswers.councilTaxSubcategory.contains("council_tax_area_change") then
      "error.tellUsMore.areaChanged.required"
    else
      "error.tell_us_more.required"

  private def getEnquiryKey(answers: UserAnswers): String =
    enquiryKey(answers).getOrElse {
      log.warn("Navigation for Tell us more page reached with error - Unknown enquiry category in enquiry key")
      throw new RuntimeException("Navigation for Tell us more page reached with error Unknown enquiry category in enquiry key")
    }

  private[controllers] def enquiryKey(answers: UserAnswers): Either[String, String] =
    (answers.enquiryCategory, answers.councilTaxSubcategory, answers.businessRatesSubcategory, answers.fairRentEnquiryEnquiry) match {
      case (Some("business_rates"), _, Some("business_rates_from_home"), _)        => Right("tellUsMore.business")
      case (Some("business_rates"), _, Some("business_rates_other"), _)            => Right("tellUsMore.business.other")
      case (Some("business_rates"), _, Some("business_rates_change_valuation"), _) => Right("tellUsMore.business.other")
      case (Some("business_rates"), _, Some("business_rates_bill"), _)             => Right("tellUsMore.business.other")
      case (Some("business_rates"), _, Some("business_rates_changes"), _)          => Right("tellUsMore.business.other")
      case (Some("business_rates"), _, Some("business_rates_property_empty"), _)   => Right("tellUsMore.business.other")
      case (Some("business_rates"), _, Some("business_rates_valuation"), _)        => Right("tellUsMore.business.other")
      case (Some("business_rates"), _, Some("business_rates_demolished"), _)       => Right("tellUsMore.business.other")
      case (Some("business_rates"), _, Some("business_rates_not_used"), _)         => Right("tellUsMore.business.other")
      case (Some("business_rates"), _, Some("business_rates_self_catering"), _)    => Right("tellUsMore.business.other")
      case (Some("council_tax"), Some("council_tax_business_uses"), _, _)          => Right("tellUsMore.business")
      case (Some("council_tax"), Some("council_tax_other"), _, _)                  => Right("tellUsMore.other")
      case (Some("council_tax"), Some("council_tax_annexe"), _, _)                 => Right("tellUsMore.general")
      case (Some("council_tax"), Some("council_tax_bill"), _, _)                   => Right("tellUsMore.general")
      case (Some("council_tax"), Some("council_tax_band_too_high"), _, _)          => Right("tellUsMore.general")
      case (Some("council_tax"), Some("council_tax_band_for_new"), _, _)           => Right("tellUsMore.general")
      case (Some("council_tax"), Some("council_tax_property_empty"), _, _)         => Right("tellUsMore.general")
      case (Some("council_tax"), Some("council_tax_property_poor_repair"), _, _)   => Right("tellUsMore.general")
      case (Some("council_tax"), Some("council_tax_property_split_merge"), _, _)   => Right("tellUsMore.general")
      case (Some("council_tax"), Some("council_tax_property_demolished"), _, _)    => Right("tellUsMore.general")
      case (Some("council_tax"), Some("council_tax_area_change"), _, _)            => Right("tellUsMore.general")
      case (Some("fair_rent"), _, _, Some("submit_new_application"))               => Right("tellUsMore.fairRent")
      case (Some("fair_rent"), _, _, Some("check_fair_rent_register"))             => Right("tellUsMore.fairRent")
      case (Some("fair_rent"), _, _, Some("other_request"))                        => Right("tellUsMore.fairRent")
      case (Some("council_tax"), _, _, _)                                          => Right("tellUsMore.ct-reference")
      case (Some("business_rates"), _, _, _)                                       => Right("tellUsMore.ndr-reference")
      case _                                                                       => Left("Unknown enquiry category in enquiry key")
    }

  private def backLink(answers: UserAnswers, mode: Mode): String =
    ((
      answers.councilTaxSubcategory,
      answers.businessRatesSubcategory,
      answers.fairRentEnquiryEnquiry,
      answers.annexeEnquiry,
      answers.annexeSelfContainedEnquiry,
      answers.annexeHaveCookingWashing,
      answers.businessRatesSelfCateringEnquiry,
      answers.propertyEnglandAvailableLetsEnquiry,
      answers.propertyWalesAvailableLetsEnquiry,
      answers.propertyWalesActualLetsEnquiry
    ) match {
      case (Some("council_tax_business_uses"), _, _, _, _, _, _, _, _, _)                                    => routes.DatePropertyChangedController.onPageLoad()
      case (Some("council_tax_other"), _, _, _, _, _, _, _, _, _)                                            => routes.CouncilTaxSubcategoryController.onPageLoad(mode)
      case (Some("council_tax_annexe"), _, _, Some("added"), Some("yes"), Some("yes"), _, _, _, _)           => routes.CouncilTaxAnnexeController.onSelfContainedPageLoad()
      case (Some("council_tax_annexe"), _, _, Some("added"), Some("yes"), Some("no"), _, _, _, _)            => routes.CouncilTaxAnnexeController.onFacilitiesPageLoad()
      case (Some("council_tax_annexe"), _, _, Some("added"), Some("no"), _, _, _, _, _)                      => routes.CouncilTaxAnnexeController.onNotSelfContainedPageLoad()
      case (Some("council_tax_annexe"), _, _, Some("removed"), _, _, _, _, _, _)                             => routes.CouncilTaxAnnexeController.onRemovedPageLoad()
      case (Some("council_tax_bill"), _, _, _, _, _, _, _, _, _)                                             => routes.CouncilTaxBillController.onPageLoad()
      case (Some("council_tax_band_too_high"), _, _, _, _, _, _, _, _, _)                                    => routes.CouncilTaxBandTooHighController.onPageLoad()
      case (Some("council_tax_band_for_new"), _, _, _, _, _, _, _, _, _)                                     => routes.CouncilTaxBandForNewController.onPageLoad()
      case (Some("council_tax_property_empty"), _, _, _, _, _, _, _, _, _)                                   => routes.PropertyEmptyController.onPageLoad()
      case (Some("council_tax_property_poor_repair"), _, _, _, _, _, _, _, _, _)                             => routes.PropertyWindWaterController.onPageLoad()
      case (Some("council_tax_property_split_merge"), _, _, _, _, _, _, _, _, _)                             => routes.PropertySplitMergeController.onPageLoad()
      case (Some("council_tax_property_demolished"), _, _, _, _, _, _, _, _, _)                              => routes.PropertyDemolishedController.onPageLoad()
      case (Some("council_tax_area_change"), _, _, _, _, _, _, _, _, _)                                      => routes.PropertyPermanentChangesController.onPageLoad()
      case (_, Some("business_rates_change_valuation"), _, _, _, _, _, _, _, _)                              => EnglandOrWalesPropertyRouter.nextPage(answers)
      case (_, Some("business_rates_from_home"), _, _, _, _, _, _, _, _)                                     => routes.DatePropertyChangedController.onPageLoad()
      case (_, Some("business_rates_not_used"), _, _, _, _, _, _, _, _)                                      => routes.DatePropertyChangedController.onPageLoad()
      case (_, Some("business_rates_bill"), _, _, _, _, _, _, _, _)                                          => routes.BusinessRatesBillController.onPageLoad()
      case (_, Some("business_rates_self_catering"), _, _, _, _, Some("england"), Some("yes"), _, _)         =>
        routes.BusinessRatesSelfCateringController.onEngLetsPageLoad()
      case (_, Some("business_rates_self_catering"), _, _, _, _, Some("england"), Some("no"), _, _)          => routes.PropertyEnglandLetsNoActionController.onPageLoad()
      case (_, Some("business_rates_self_catering"), _, _, _, _, Some("wales"), _, Some("no"), _)            => routes.PropertyWalesLetsNoActionController.onPageLoad()
      case (_, Some("business_rates_self_catering"), _, _, _, _, Some("wales"), _, Some("yes"), Some("yes")) =>
        routes.BusinessRatesSelfCateringController.onWalLetsPageLoad()
      case (_, Some("business_rates_self_catering"), _, _, _, _, Some("wales"), _, Some("yes"), Some("no"))  =>
        routes.PropertyWalesLetsNoActionController.onPageLoad()
      case (_, Some("business_rates_changes"), _, _, _, _, _, _, _, _)                                       => EnglandOrWalesPropertyRouter.nextPage(answers)
      case (_, Some("business_rates_demolished"), _, _, _, _, _, _, _, _)                                    => EnglandOrWalesPropertyRouter.nextPage(answers)
      case (_, Some("business_rates_valuation"), _, _, _, _, _, _, _, _)                                     => routes.BusinessRatesSubcategoryController.onValuationPageLoad()
      case (_, Some("business_rates_property_empty"), _, _, _, _, _, _, _, _)                                => routes.PropertyEmptyController.onBusinessRatesPageLoad()
      case (_, Some("business_rates_other"), _, _, _, _, _, _, _, _)                                         => routes.BusinessRatesSubcategoryController.onPageLoad(mode)
      case (_, _, Some("submit_new_application"), _, _, _, _, _, _, _)                                       => routes.FairRentEnquiryController.onFairRentEnquiryNew()
      case (_, _, Some("check_fair_rent_register"), _, _, _, _, _, _, _)                                     => routes.FairRentEnquiryController.onFairRentEnquiryCheck()
      case (_, _, Some("other_request"), _, _, _, _, _, _, _)                                                => routes.FairRentEnquiryController.onPageLoad()
      case _                                                                                                 => routes.PropertyAddressController.onPageLoad(NormalMode)
    }).url

}

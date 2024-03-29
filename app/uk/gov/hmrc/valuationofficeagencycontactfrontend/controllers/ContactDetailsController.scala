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
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.ContactDetailsForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers.ContactDetailsId
import uk.gov.hmrc.valuationofficeagencycontactfrontend.journey.model.TellUsMorePage.lastTellUsMorePage
import uk.gov.hmrc.valuationofficeagencycontactfrontend.journey.pages.HBTellUsMore.appStartPage
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.{ContactDetails, Mode, NormalMode}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.UserAnswers
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{contactDetails => contact_details}

import scala.concurrent.{ExecutionContext, Future}
import play.api.mvc
import play.api.mvc.AnyContent

class ContactDetailsController @Inject() (
  appConfig: FrontendAppConfig,
  override val messagesApi: MessagesApi,
  dataCacheConnector: DataCacheConnector,
  navigator: Navigator,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  contactDetails: contact_details,
  cc: MessagesControllerComponents
) extends FrontendController(cc)
  with I18nSupport {

  private val log = Logger(this.getClass)

  implicit val ec: ExecutionContext = cc.executionContext

  def onPageLoad(mode: Mode): mvc.Action[AnyContent] = (getData andThen requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.contactDetails match {
        case None        => ContactDetailsForm()
        case Some(value) => ContactDetailsForm().fill(value)
      }
      enquiryBackLink(request.userAnswers) match {
        case Right(link) => Ok(contactDetails(appConfig, preparedForm, mode, link))
        case Left(msg)   =>
          log.warn(s"Navigation for Contact Details page reached with error $msg")
          throw new RuntimeException(s"Navigation for Contact Details page reached with error $msg")
      }

  }

  def onSubmit(mode: Mode): mvc.Action[AnyContent] = (getData andThen requireData).async {
    implicit request =>
      ContactDetailsForm().bindFromRequest().fold(
        (formWithErrors: Form[ContactDetails]) =>
          Future.successful(enquiryBackLink(request.userAnswers) match {
            case Right(link) => BadRequest(contactDetails(appConfig, formWithErrors, mode, link))
            case Left(msg)   =>
              log.warn(s"Navigation for Contact Details page reached with error $msg")
              throw new RuntimeException(s"Navigation for Contact Details page reached with error $msg")
          }),
        value =>
          dataCacheConnector.save[ContactDetails](request.sessionId, ContactDetailsId.toString, value).map(cacheMap =>
            Redirect(navigator.nextPage(ContactDetailsId, mode).apply(new UserAnswers(cacheMap)))
          )
      )
  }

  private[controllers] def enquiryBackLink(answers: UserAnswers): Either[String, String] =
    (answers.contactReason, answers.enquiryCategory, answers.councilTaxSubcategory, answers.businessRatesSubcategory, answers.fairRentEnquiryEnquiry) match {
      case (Some("more_details"), _, _, _, _)                                         => Right(routes.RefNumberController.onPageLoad().url)
      case (Some("update_existing"), _, _, _, _)                                      => Right(routes.RefNumberController.onPageLoad().url)
      case (_, Some("council_tax"), Some("council_tax_property_poor_repair"), _, _)   => Right(routes.TellUsMoreController.onPageLoad(NormalMode).url)
      case (_, Some("council_tax"), Some("council_tax_business_uses"), _, _)          => Right(routes.TellUsMoreController.onPageLoad(NormalMode).url)
      case (_, Some("council_tax"), Some("council_tax_area_change"), _, _)            => Right(routes.TellUsMoreController.onPageLoad(NormalMode).url)
      case (_, Some("council_tax"), Some("council_tax_other"), _, _)                  => Right(routes.TellUsMoreController.onPageLoad(NormalMode).url)
      case (_, Some("council_tax"), Some("council_tax_annexe"), _, _)                 => Right(routes.TellUsMoreController.onPageLoad(NormalMode).url)
      case (_, Some("council_tax"), Some("council_tax_bill"), _, _)                   => Right(routes.TellUsMoreController.onPageLoad(NormalMode).url)
      case (_, Some("council_tax"), Some("council_tax_band_for_new"), _, _)           => Right(routes.TellUsMoreController.onPageLoad(NormalMode).url)
      case (_, Some("council_tax"), Some("council_tax_band_too_high"), _, _)          => Right(routes.TellUsMoreController.onPageLoad(NormalMode).url)
      case (_, Some("council_tax"), Some("council_tax_property_empty"), _, _)         => Right(routes.TellUsMoreController.onPageLoad(NormalMode).url)
      case (_, Some("council_tax"), Some("council_tax_property_poor_repair"), _, _)   => Right(routes.TellUsMoreController.onPageLoad(NormalMode).url)
      case (_, Some("council_tax"), Some("council_tax_property_split_merge"), _, _)   => Right(routes.TellUsMoreController.onPageLoad(NormalMode).url)
      case (_, Some("council_tax"), Some("council_tax_property_demolished"), _, _)    => Right(routes.TellUsMoreController.onPageLoad(NormalMode).url)
      case (_, Some("business_rates"), _, Some("business_rates_bill"), _)             => Right(routes.TellUsMoreController.onPageLoad(NormalMode).url)
      case (_, Some("business_rates"), _, Some("business_rates_changes"), _)          => Right(routes.TellUsMoreController.onPageLoad(NormalMode).url)
      case (_, Some("business_rates"), _, Some("business_rates_from_home"), _)        => Right(routes.TellUsMoreController.onPageLoad(NormalMode).url)
      case (_, Some("business_rates"), _, Some("business_rates_change_valuation"), _) => Right(routes.TellUsMoreController.onPageLoad(NormalMode).url)
      case (_, Some("business_rates"), _, Some("business_rates_not_used"), _)         => Right(routes.TellUsMoreController.onPageLoad(NormalMode).url)
      case (_, Some("business_rates"), _, Some("business_rates_self_catering"), _)    => Right(routes.TellUsMoreController.onPageLoad(NormalMode).url)
      case (_, Some("business_rates"), _, Some("business_rates_demolished"), _)       => Right(routes.TellUsMoreController.onPageLoad(NormalMode).url)
      case (_, Some("business_rates"), _, Some("business_rates_property_empty"), _)   => Right(routes.TellUsMoreController.onPageLoad(NormalMode).url)
      case (_, Some("business_rates"), _, Some("business_rates_valuation"), _)        => Right(routes.TellUsMoreController.onPageLoad(NormalMode).url)
      case (_, Some("housing_benefit"), _, _, _)                                      => Right(answers.getString(lastTellUsMorePage).fold(appStartPage)(routes.JourneyController.onPageLoad).url)
      case (_, Some("fair_rent"), _, _, Some("submit_new_application"))               => Right(routes.TellUsMoreController.onPageLoad(NormalMode).url)
      case (_, Some("fair_rent"), _, _, Some("check_fair_rent_register"))             => Right(routes.TellUsMoreController.onPageLoad(NormalMode).url)
      case (_, Some("fair_rent"), _, _, Some("other_request"))                        => Right(routes.TellUsMoreController.onPageLoad(NormalMode).url)
      case (_, Some("council_tax"), _, _, _)                                          => Right(routes.CouncilTaxSubcategoryController.onPageLoad(NormalMode).url)
      case (_, Some("business_rates"), _, _, _)                                       => Right(routes.BusinessRatesSubcategoryController.onPageLoad(NormalMode).url)
      case _                                                                          => Left("Unknown enquiry category in enquiry key")
    }

}

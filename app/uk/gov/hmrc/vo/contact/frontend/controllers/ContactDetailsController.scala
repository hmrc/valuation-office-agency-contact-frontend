/*
 * Copyright 2026 HM Revenue & Customs
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

package uk.gov.hmrc.vo.contact.frontend.controllers

import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{AnyContent, MessagesControllerComponents}
import play.api.{Logging, mvc}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.vo.contact.frontend.Navigator
import uk.gov.hmrc.vo.contact.frontend.connectors.DataCacheConnector
import uk.gov.hmrc.vo.contact.frontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.vo.contact.frontend.forms.ContactDetailsForm.contactDetailsForm
import uk.gov.hmrc.vo.contact.frontend.identifiers.ContactDetailsId
import uk.gov.hmrc.vo.contact.frontend.journey.model.TellUsMorePage.lastTellUsMorePage
import uk.gov.hmrc.vo.contact.frontend.journey.pages.HBTellUsMore.appStartPage
import uk.gov.hmrc.vo.contact.frontend.models.{ContactDetails, Mode, NormalMode}
import uk.gov.hmrc.vo.contact.frontend.utils.UserAnswers
import uk.gov.hmrc.vo.contact.frontend.views.html.contactDetails

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class ContactDetailsController @Inject() (
  override val messagesApi: MessagesApi,
  dataCacheConnector: DataCacheConnector,
  navigator: Navigator,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  contactDetailsView: contactDetails,
  cc: MessagesControllerComponents
) extends FrontendController(cc)
  with I18nSupport
  with Logging:

  given ExecutionContext = cc.executionContext

  def onPageLoad(mode: Mode): mvc.Action[AnyContent] = (getData andThen requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.contactDetails.fold(contactDetailsForm)(contactDetailsForm.fill)

      enquiryBackLink(request.userAnswers) match
        case Right(link) => Ok(contactDetailsView(preparedForm, mode, link))
        case Left(msg)   =>
          logger.warn(s"Navigation for Contact Details page reached with error $msg")
          throw RuntimeException(s"Navigation for Contact Details page reached with error $msg")
  }

  def onSubmit(mode: Mode): mvc.Action[AnyContent] = (getData andThen requireData).async {
    implicit request =>
      contactDetailsForm.bindFromRequest().fold(
        formWithErrors =>
          enquiryBackLink(request.userAnswers) match
            case Right(link) => BadRequest(contactDetailsView(formWithErrors, mode, link))
            case Left(msg)   =>
              logger.warn(s"Navigation for Contact Details page reached with error $msg")
              throw RuntimeException(s"Navigation for Contact Details page reached with error $msg"),
        value =>
          dataCacheConnector.save[ContactDetails](request.sessionId, ContactDetailsId.toString, value).map(cacheMap =>
            Redirect(navigator.nextPage(ContactDetailsId, mode).apply(UserAnswers(cacheMap)))
          )
      )
  }

  private[controllers] def enquiryBackLink(answers: UserAnswers): Either[String, String] =
    (answers.contactReason, answers.enquiryCategory, answers.councilTaxSubcategory, answers.businessRatesSubcategory, answers.fairRentEnquiryEnquiry) match
      case (Some("more_details"), _, _, _, _)                                         => Right(routes.RefNumberController.onPageLoad.url)
      case (Some("update_existing"), _, _, _, _)                                      => Right(routes.RefNumberController.onPageLoad.url)
      case (_, Some("council_tax"), Some("council_tax_property_poor_repair"), _, _)   => Right(routes.TellUsMoreController.onPageLoad(NormalMode).url)
      case (_, Some("council_tax"), Some("council_tax_business_uses"), _, _)          => Right(routes.TellUsMoreController.onPageLoad(NormalMode).url)
      case (_, Some("council_tax"), Some("council_tax_area_change"), _, _)            => Right(routes.TellUsMoreController.onPageLoad(NormalMode).url)
      case (_, Some("council_tax"), Some("council_tax_other"), _, _)                  => Right(routes.TellUsMoreController.onPageLoad(NormalMode).url)
      case (_, Some("council_tax"), Some("council_tax_annexe"), _, _)                 => Right(routes.TellUsMoreController.onPageLoad(NormalMode).url)
      case (_, Some("council_tax"), Some("council_tax_bill"), _, _)                   => Right(routes.TellUsMoreController.onPageLoad(NormalMode).url)
      case (_, Some("council_tax"), Some("council_tax_band_for_new"), _, _)           => Right(routes.TellUsMoreController.onPageLoad(NormalMode).url)
      case (_, Some("council_tax"), Some("council_tax_band_too_high"), _, _)          => Right(routes.TellUsMoreController.onPageLoad(NormalMode).url)
      case (_, Some("council_tax"), Some("council_tax_property_empty"), _, _)         => Right(routes.TellUsMoreController.onPageLoad(NormalMode).url)
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
      case (_, Some("business_rates"), _, Some("business_rates_other"), _)            => Right(routes.TellUsMoreController.onPageLoad(NormalMode).url)
      case (_, Some("housing_benefit"), _, _, _)                                      => Right(answers.getString(lastTellUsMorePage).fold(appStartPage)(routes.JourneyController.onPageLoad).url)
      case (_, Some("fair_rent"), _, _, Some("submit_new_application"))               => Right(routes.TellUsMoreController.onPageLoad(NormalMode).url)
      case (_, Some("fair_rent"), _, _, Some("check_fair_rent_register"))             => Right(routes.TellUsMoreController.onPageLoad(NormalMode).url)
      case (_, Some("fair_rent"), _, _, Some("other_request"))                        => Right(routes.TellUsMoreController.onPageLoad(NormalMode).url)
      case (_, Some("council_tax"), _, _, _)                                          => Right(routes.CouncilTaxSubcategoryController.onPageLoad(NormalMode).url)
      case (_, Some("business_rates"), _, _, _)                                       => Right(routes.TellUsMoreController.onPageLoad(NormalMode).url)
      case _                                                                          => Left("Unknown enquiry category in enquiry key")

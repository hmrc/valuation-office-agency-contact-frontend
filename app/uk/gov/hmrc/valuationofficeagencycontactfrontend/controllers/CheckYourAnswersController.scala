/*
 * Copyright 2022 HM Revenue & Customs
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

import com.google.inject.Inject
import play.api.Logger
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.MessagesControllerComponents
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors.{AuditingService, DataCacheConnector}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.FrontendAppConfig
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers.AnswerSectionId
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.NormalMode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.{CheckYourAnswersHelper, DateUtil, UserAnswers}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.viewmodels.AnswerSection
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.check_your_answers

import scala.concurrent.ExecutionContext

class CheckYourAnswersController @Inject()(appConfig: FrontendAppConfig,
                                           auditService: AuditingService,
                                           override val messagesApi: MessagesApi,
                                           dataCacheConnector: DataCacheConnector,
                                           getData: DataRetrievalAction,
                                           requireData: DataRequiredAction,
                                           checkYourAnswers: check_your_answers,
                                           cc: MessagesControllerComponents
                                          )(implicit dateUtil: DateUtil) extends FrontendController(cc) with I18nSupport {

  private val log = Logger(this.getClass)

  implicit val ec: ExecutionContext = cc.executionContext

  def onPageLoad() = (getData andThen requireData) {
    implicit request =>
      val link = enquiryBackLink(request.userAnswers)

      userAnswersSectionBuilder(request.userAnswers) match {
        case Some(answerSection) => {
          dataCacheConnector.save[AnswerSection](request.sessionId, AnswerSectionId.toString, answerSection).isCompleted
          Ok(checkYourAnswers(appConfig, Seq(answerSection), link))
        }
        case None => {
          log.warn("Navigation for Check your answers page reached without selection of enquiry by controller")
          throw new RuntimeException("Navigation for check your anwsers page reached without selection of enquiry by controller")
        }
      }
  }

  def goToConfirmationPage() = (getData andThen requireData) { implicit request =>
    val call = routes.ConfirmationController.onPageLoadSendEmail()
    auditService.sendContinueNextPage(call.url)
    Redirect(call)
  }

  private[controllers] def userAnswersSectionBuilder(answers: UserAnswers)(implicit messages: Messages): Option[AnswerSection] = {
    import answers._
    val checkYourAnswersHelper = new CheckYourAnswersHelper(answers)

    (contactReason, enquiryCategory, councilTaxSubcategory, businessRatesSubcategory, fairRentEnquiryEnquiry) match {
      case (Some("more_details"), _, _, _, _) => Some(
        AnswerSection(None, Seq(
          checkYourAnswersHelper.existingEnquiryCategory,
          checkYourAnswersHelper.refNumber,
          checkYourAnswersHelper.contactDetails,
          checkYourAnswersHelper.propertyAddress,
          checkYourAnswersHelper.whatElse).flatten))
      case (Some("update_existing"), _, _, _, _) => Some(
        AnswerSection(None, Seq(
          checkYourAnswersHelper.existingEnquiryCategory,
          checkYourAnswersHelper.refNumber,
          checkYourAnswersHelper.contactDetails,
          checkYourAnswersHelper.propertyAddress,
          checkYourAnswersHelper.anythingElse).flatten))
      case (Some("new_enquiry"), Some("business_rates"), _, Some("business_rates_from_home"), _) => Some(
        AnswerSection(None, Seq(
          checkYourAnswersHelper.enquiryCategory,
          checkYourAnswersHelper.businessRatesSubcategory,
          checkYourAnswersHelper.datePropertyChanged("datePropertyChanged.business.heading"),
          checkYourAnswersHelper.tellUsMore("tellUsMore.business.heading"),
          checkYourAnswersHelper.contactDetails,
          checkYourAnswersHelper.propertyAddress).flatten))
      case (Some("new_enquiry"), Some("business_rates"), _, Some("business_rates_not_used"), _) => Some(
        AnswerSection(None, Seq(
          checkYourAnswersHelper.enquiryCategory,
          checkYourAnswersHelper.businessRatesSubcategory,
          checkYourAnswersHelper.datePropertyChanged("datePropertyChanged.notUsed.heading"),
          checkYourAnswersHelper.tellUsMore("tellUsMore.notUsed.heading"),
          checkYourAnswersHelper.contactDetails,
          checkYourAnswersHelper.propertyAddress).flatten))
      case (Some("new_enquiry"), Some("business_rates"), _, Some("business_rates_other"), _) => Some(
        AnswerSection(None, Seq(
          checkYourAnswersHelper.enquiryCategory,
          checkYourAnswersHelper.businessRatesSubcategory,
          checkYourAnswersHelper.tellUsMore("tellUsMore.business.other.heading"),
          checkYourAnswersHelper.contactDetails,
          checkYourAnswersHelper.propertyAddress).flatten))
      case (_, Some("business_rates"), _, _, _) => Some(
        AnswerSection(None, Seq(
          checkYourAnswersHelper.enquiryCategory,
          checkYourAnswersHelper.businessRatesSubcategory,
          checkYourAnswersHelper.contactDetails,
          checkYourAnswersHelper.propertyAddress,
          checkYourAnswersHelper.tellUsMore()).flatten))
      case (_, Some("council_tax"), Some("council_tax_property_poor_repair"), _, _) => Some(
        AnswerSection(None, Seq(
          checkYourAnswersHelper.enquiryCategory,
          checkYourAnswersHelper.councilTaxSubcategory,
          checkYourAnswersHelper.datePropertyChanged(),
          checkYourAnswersHelper.tellUsMore("tellUsMore.poorRepair.heading"),
          checkYourAnswersHelper.contactDetails,
          checkYourAnswersHelper.propertyAddress).flatten))
      case (_, Some("council_tax"), Some("council_tax_business_uses"), _, _) => Some(
        AnswerSection(None, Seq(
          checkYourAnswersHelper.enquiryCategory,
          checkYourAnswersHelper.councilTaxSubcategory,
          checkYourAnswersHelper.datePropertyChanged("datePropertyChanged.business.heading"),
          checkYourAnswersHelper.tellUsMore("tellUsMore.business.heading"),
          checkYourAnswersHelper.contactDetails,
          checkYourAnswersHelper.propertyAddress).flatten))
      case (_, Some("council_tax"), Some("council_tax_area_change"), _, _) => Some(
        AnswerSection(None, Seq(
          checkYourAnswersHelper.enquiryCategory,
          checkYourAnswersHelper.councilTaxSubcategory,
          checkYourAnswersHelper.datePropertyChanged("datePropertyChanged.areaChange.heading"),
          checkYourAnswersHelper.tellUsMore("tellUsMore.areaChange.heading"),
          checkYourAnswersHelper.contactDetails,
          checkYourAnswersHelper.propertyAddress).flatten))
      case (_, Some("council_tax"), Some("council_tax_other"), _, _) => Some(
        AnswerSection(None, Seq(
          checkYourAnswersHelper.enquiryCategory,
          checkYourAnswersHelper.councilTaxSubcategory,
          checkYourAnswersHelper.tellUsMore("tellUsMore.other.heading"),
          checkYourAnswersHelper.contactDetails,
          checkYourAnswersHelper.propertyAddress).flatten))
      case (_, Some("council_tax"), _, _, _) => Some(
        AnswerSection(None, Seq(
          checkYourAnswersHelper.enquiryCategory,
          checkYourAnswersHelper.councilTaxSubcategory,
          checkYourAnswersHelper.contactDetails,
          checkYourAnswersHelper.propertyAddress,
          checkYourAnswersHelper.tellUsMore()).flatten))
      case (_, Some("housing_benefit"), _, _, _) => Some(
        AnswerSection(None, Seq(
          checkYourAnswersHelper.enquiryCategory,
          checkYourAnswersHelper.housingBenefitTellUsMore,
          checkYourAnswersHelper.contactDetails,
          checkYourAnswersHelper.propertyAddress).flatten))
      case (_, Some("fair_rent"), _, _, Some("submit_new_application")) => Some(
        AnswerSection(None, Seq(
          checkYourAnswersHelper.enquiryCategory,
          checkYourAnswersHelper.datePropertyChanged(),
          checkYourAnswersHelper.tellUsMore("tellUsMore.fairRent.heading"),
          checkYourAnswersHelper.contactDetails,
          checkYourAnswersHelper.propertyAddress).flatten))
      case (_, Some("fair_rent"), _, _, Some("check_fair_rent_register")) => Some(
        AnswerSection(None, Seq(
          checkYourAnswersHelper.enquiryCategory,
          checkYourAnswersHelper.datePropertyChanged(),
          checkYourAnswersHelper.tellUsMore("tellUsMore.fairRent.heading"),
          checkYourAnswersHelper.contactDetails,
          checkYourAnswersHelper.propertyAddress).flatten))
      case (_, Some("fair_rent"), _, _, Some("other_request")) => Some(
        AnswerSection(None, Seq(
          checkYourAnswersHelper.enquiryCategory,
          checkYourAnswersHelper.datePropertyChanged(),
          checkYourAnswersHelper.tellUsMore("tellUsMore.fairRent.heading"),
          checkYourAnswersHelper.contactDetails,
          checkYourAnswersHelper.propertyAddress).flatten))
      case _ => None
    }
  }

  private[controllers] def enquiryBackLink(answers: UserAnswers): String =
    (answers.contactReason, answers.councilTaxSubcategory, answers.businessRatesSubcategory, answers.fairRentEnquiryEnquiry) match {
      case (_, _, _, _) if answers.enquiryCategory.contains("housing_benefit") => routes.PropertyAddressController.onPageLoad(NormalMode).url
      case (Some("new_enquiry"), Some("council_tax_property_poor_repair"), _, _) => routes.PropertyAddressController.onPageLoad(NormalMode).url
      case (Some("new_enquiry"), Some("council_tax_business_uses"), _, _) => routes.PropertyAddressController.onPageLoad(NormalMode).url
      case (Some("new_enquiry"), Some("council_tax_area_change"), _, _) => routes.PropertyAddressController.onPageLoad(NormalMode).url
      case (Some("new_enquiry"), Some("council_tax_other"), _, _) => routes.PropertyAddressController.onPageLoad(NormalMode).url
      case (Some("new_enquiry"), Some("council_tax_annexe"), _, _) => routes.PropertyAddressController.onPageLoad(NormalMode).url
      case (Some("new_enquiry"), Some("council_tax_band_too_high"), _, _) => routes.PropertyAddressController.onPageLoad(NormalMode).url
      case (Some("new_enquiry"), Some("council_tax_bill"), _, _) => routes.PropertyAddressController.onPageLoad(NormalMode).url
      case (Some("new_enquiry"), Some("council_tax_band_for_new"), _, _) => routes.PropertyAddressController.onPageLoad(NormalMode).url
      case (Some("new_enquiry"), Some("council_tax_property_empty"), _, _) => routes.PropertyAddressController.onPageLoad(NormalMode).url
      case (Some("new_enquiry"), Some("council_tax_property_poor_repair"), _, _) => routes.PropertyAddressController.onPageLoad(NormalMode).url
      case (Some("new_enquiry"), Some("council_tax_property_split_merge"), _, _) => routes.PropertyAddressController.onPageLoad(NormalMode).url
      case (Some("new_enquiry"), Some("council_tax_property_demolished"), _, _) => routes.PropertyAddressController.onPageLoad(NormalMode).url
      case (Some("new_enquiry"), _, Some("business_rates_from_home"), _) => routes.PropertyAddressController.onPageLoad(NormalMode).url
      case (Some("new_enquiry"), _, Some("business_rates_bill"), _) => routes.PropertyAddressController.onPageLoad(NormalMode).url
      case (Some("new_enquiry"), _, Some("business_rates_property_empty"), _) => routes.PropertyAddressController.onPageLoad(NormalMode).url
      case (Some("new_enquiry"), _, Some("business_rates_changes"), _) => routes.PropertyAddressController.onPageLoad(NormalMode).url
      case (Some("new_enquiry"), _, Some("business_rates_change_valuation"), _) => routes.PropertyAddressController.onPageLoad(NormalMode).url
      case (Some("new_enquiry"), _, Some("business_rates_valuation"), _) => routes.PropertyAddressController.onPageLoad(NormalMode).url
      case (Some("new_enquiry"), _, Some("business_rates_demolished"), _) => routes.PropertyAddressController.onPageLoad(NormalMode).url
      case (Some("new_enquiry"), _, Some("business_rates_not_used"), _) => routes.PropertyAddressController.onPageLoad(NormalMode).url
      case (Some("new_enquiry"), _, Some("business_rates_self_catering"), _) => routes.PropertyAddressController.onPageLoad(NormalMode).url
      case (Some("new_enquiry"), _, _, Some("submit_new_application")) => routes.PropertyAddressController.onPageLoad(NormalMode).url
      case (Some("new_enquiry"), _, _, Some("check_fair_rent_register")) => routes.PropertyAddressController.onPageLoad(NormalMode).url
      case (Some("new_enquiry"), _, _, Some("other_request")) => routes.PropertyAddressController.onPageLoad(NormalMode).url
      case (Some("new_enquiry"), _, _, _) => routes.TellUsMoreController.onPageLoad(NormalMode).url
      case (Some("more_details"), _, _, _) => routes.WhatElseController.onPageLoad().url
      case (Some("update_existing"), _, _, _) => routes.AnythingElseTellUsController.onPageLoad().url
      case _ =>
        log.warn("Navigation for Check your answers page reached without selection of contact reason by controller")
        throw new RuntimeException("Navigation for check your anwsers page reached without selection of contact reason by controller")
    }

}

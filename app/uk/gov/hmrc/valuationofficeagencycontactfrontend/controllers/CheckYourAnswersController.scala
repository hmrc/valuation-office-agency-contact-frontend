/*
 * Copyright 2021 HM Revenue & Customs
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
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.MessagesControllerComponents
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.valuationofficeagencycontactfrontend.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers.AnswerSectionId
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.NormalMode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.{CheckYourAnswersHelper, UserAnswers}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.viewmodels.AnswerSection
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.check_your_answers

import scala.concurrent.ExecutionContext

class CheckYourAnswersController @Inject()(appConfig: FrontendAppConfig,
                                           override val messagesApi: MessagesApi,
                                           dataCacheConnector: DataCacheConnector,
                                           getData: DataRetrievalAction,
                                           requireData: DataRequiredAction,
                                           checkYourAnswers: check_your_answers,
                                           cc: MessagesControllerComponents
                                          ) extends FrontendController(cc) with I18nSupport {

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
          Logger.warn("Navigation for Check your answers page reached without selection of enquiry by controller")
          throw new RuntimeException("Navigation for check your anwsers page reached without selection of enquiry by controller")
        }
      }
  }

  def goToConfirmationPage() = (getData andThen requireData) {
    Redirect(routes.ConfirmationController.onPageLoadSendEmail())
  }

  private[controllers] def userAnswersSectionBuilder(answers: UserAnswers): Option[AnswerSection] = {
    import answers._
    val checkYourAnswersHelper = new CheckYourAnswersHelper(answers)

    (contactReason, enquiryCategory, councilTaxSubcategory, businessRatesSubcategory) match {
      case (Some("more_details"), _, _, _) => Some(
        AnswerSection(None, Seq(
          checkYourAnswersHelper.existingEnquiryCategory,
          checkYourAnswersHelper.refNumber,
          checkYourAnswersHelper.contactDetails,
          checkYourAnswersHelper.propertyAddress,
          checkYourAnswersHelper.whatElse).flatten))
      case (Some("update_existing"), _, _, _) => Some(
        AnswerSection(None, Seq(
          checkYourAnswersHelper.existingEnquiryCategory,
          checkYourAnswersHelper.refNumber,
          checkYourAnswersHelper.contactDetails,
          checkYourAnswersHelper.propertyAddress,
          checkYourAnswersHelper.anythingElse).flatten))
      case (Some("new_enquiry"), Some("business_rates"), _, Some("business_rates_from_home")) => Some(
        AnswerSection(None, Seq(
          checkYourAnswersHelper.enquiryCategory,
          checkYourAnswersHelper.businessRatesSubcategory,
          checkYourAnswersHelper.datePropertyChanged("datePropertyChanged.business.heading"),
          checkYourAnswersHelper.tellUsMore("tellUsMore.business.heading"),
          checkYourAnswersHelper.contactDetails,
          checkYourAnswersHelper.propertyAddress).flatten))
      case (Some("new_enquiry"), Some("business_rates"), _, Some("business_rates_not_used")) => Some(
        AnswerSection(None, Seq(
          checkYourAnswersHelper.enquiryCategory,
          checkYourAnswersHelper.businessRatesSubcategory,
          checkYourAnswersHelper.datePropertyChanged("datePropertyChanged.notUsed.heading"),
          checkYourAnswersHelper.tellUsMore("tellUsMore.notUsed.heading"),
          checkYourAnswersHelper.contactDetails,
          checkYourAnswersHelper.propertyAddress).flatten))
      case (Some("new_enquiry"), Some("business_rates"), _, Some("business_rates_other")) => Some(
        AnswerSection(None, Seq(
          checkYourAnswersHelper.enquiryCategory,
          checkYourAnswersHelper.businessRatesSubcategory,
          checkYourAnswersHelper.tellUsMore("tellUsMore.business.other.heading"),
          checkYourAnswersHelper.contactDetails,
          checkYourAnswersHelper.propertyAddress).flatten))
      case (_, Some("business_rates"), _, _) => Some(
        AnswerSection(None, Seq(
          checkYourAnswersHelper.enquiryCategory,
          checkYourAnswersHelper.businessRatesSubcategory,
          checkYourAnswersHelper.contactDetails,
          checkYourAnswersHelper.propertyAddress,
          checkYourAnswersHelper.tellUsMore()).flatten))
      case (_, Some("council_tax"), Some("council_tax_property_poor_repair"), _) => Some(
        AnswerSection(None, Seq(
          checkYourAnswersHelper.enquiryCategory,
          checkYourAnswersHelper.councilTaxSubcategory,
          checkYourAnswersHelper.datePropertyChanged(),
          checkYourAnswersHelper.tellUsMore("tellUsMore.poorRepair.heading"),
          checkYourAnswersHelper.contactDetails,
          checkYourAnswersHelper.propertyAddress).flatten))
      case (_, Some("council_tax"), Some("council_tax_business_uses"), _) => Some(
        AnswerSection(None, Seq(
          checkYourAnswersHelper.enquiryCategory,
          checkYourAnswersHelper.councilTaxSubcategory,
          checkYourAnswersHelper.datePropertyChanged("datePropertyChanged.business.heading"),
          checkYourAnswersHelper.tellUsMore("tellUsMore.business.heading"),
          checkYourAnswersHelper.contactDetails,
          checkYourAnswersHelper.propertyAddress).flatten))
      case (_, Some("council_tax"), Some("council_tax_area_change"), _) => Some(
        AnswerSection(None, Seq(
          checkYourAnswersHelper.enquiryCategory,
          checkYourAnswersHelper.councilTaxSubcategory,
          checkYourAnswersHelper.datePropertyChanged("datePropertyChanged.areaChange.heading"),
          checkYourAnswersHelper.tellUsMore("tellUsMore.areaChange.heading"),
          checkYourAnswersHelper.contactDetails,
          checkYourAnswersHelper.propertyAddress).flatten))
      case (_, Some("council_tax"), Some("council_tax_other"), _) => Some(
        AnswerSection(None, Seq(
          checkYourAnswersHelper.enquiryCategory,
          checkYourAnswersHelper.councilTaxSubcategory,
          checkYourAnswersHelper.tellUsMore("tellUsMore.other.heading"),
          checkYourAnswersHelper.contactDetails,
          checkYourAnswersHelper.propertyAddress).flatten))
      case (_, Some("council_tax"), _, _) => Some(
        AnswerSection(None, Seq(
          checkYourAnswersHelper.enquiryCategory,
          checkYourAnswersHelper.councilTaxSubcategory,
          checkYourAnswersHelper.contactDetails,
          checkYourAnswersHelper.propertyAddress,
          checkYourAnswersHelper.tellUsMore()).flatten))
      case _ => None
    }
  }

  private[controllers] def enquiryBackLink(answers: UserAnswers): String = {
    (answers.contactReason, answers.councilTaxSubcategory, answers.businessRatesSubcategory) match {
      case (Some("new_enquiry"), Some("council_tax_property_poor_repair"), _) => routes.PropertyAddressController.onPageLoad(NormalMode).url
      case (Some("new_enquiry"), Some("council_tax_business_uses"), _) => routes.PropertyAddressController.onPageLoad(NormalMode).url
      case (Some("new_enquiry"), Some("council_tax_area_change"), _) => routes.PropertyAddressController.onPageLoad(NormalMode).url
      case (Some("new_enquiry"), Some("council_tax_other"), _) => routes.PropertyAddressController.onPageLoad(NormalMode).url
      case (Some("new_enquiry"), Some("council_tax_annexe"), _) => routes.PropertyAddressController.onPageLoad(NormalMode).url
      case (Some("new_enquiry"), Some("council_tax_band_too_high"), _) => routes.PropertyAddressController.onPageLoad(NormalMode).url
      case (Some("new_enquiry"), Some("council_tax_bill"), _) => routes.PropertyAddressController.onPageLoad(NormalMode).url
      case (Some("new_enquiry"), Some("council_tax_band_for_new"), _) => routes.PropertyAddressController.onPageLoad(NormalMode).url
      case (Some("new_enquiry"), Some("council_tax_property_empty"), _) => routes.PropertyAddressController.onPageLoad(NormalMode).url
      case (Some("new_enquiry"), Some("council_tax_property_poor_repair"), _) => routes.PropertyAddressController.onPageLoad(NormalMode).url
      case (Some("new_enquiry"), Some("council_tax_property_split_merge"), _) => routes.PropertyAddressController.onPageLoad(NormalMode).url
      case (Some("new_enquiry"), Some("council_tax_property_demolished"), _) => routes.PropertyAddressController.onPageLoad(NormalMode).url
      case (Some("new_enquiry"), _, Some("business_rates_from_home")) => routes.PropertyAddressController.onPageLoad(NormalMode).url
      case (Some("new_enquiry"), _, Some("business_rates_bill")) => routes.PropertyAddressController.onPageLoad(NormalMode).url
      case (Some("new_enquiry"), _, Some("business_rates_property_empty")) => routes.PropertyAddressController.onPageLoad(NormalMode).url
      case (Some("new_enquiry"), _, Some("business_rates_changes")) => routes.PropertyAddressController.onPageLoad(NormalMode).url
      case (Some("new_enquiry"), _, Some("business_rates_change_valuation")) => routes.PropertyAddressController.onPageLoad(NormalMode).url
      case (Some("new_enquiry"), _, Some("business_rates_valuation")) => routes.PropertyAddressController.onPageLoad(NormalMode).url
      case (Some("new_enquiry"), _, Some("business_rates_demolished")) => routes.PropertyAddressController.onPageLoad(NormalMode).url
      case (Some("new_enquiry"), _, Some("business_rates_not_used")) => routes.PropertyAddressController.onPageLoad(NormalMode).url
      case (Some("new_enquiry"), _, Some("business_rates_self_catering")) => routes.PropertyAddressController.onPageLoad(NormalMode).url
      case (Some("new_enquiry"), _, _) => routes.TellUsMoreController.onPageLoad(NormalMode).url
      case (Some("more_details"), _, _) => routes.WhatElseController.onPageLoad().url
      case (Some("update_existing"), _, _) => routes.AnythingElseTellUsController.onPageLoad().url
      case _ => {
        Logger.warn("Navigation for Check your answers page reached without selection of contact reason by controller")
        throw new RuntimeException("Navigation for check your anwsers page reached without selection of contact reason by controller")
      }
    }
  }
}

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
import uk.gov.hmrc.valuationofficeagencycontactfrontend.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.NormalMode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.{CheckYourAnswersHelper, UserAnswers}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.viewmodels.AnswerSection
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.check_your_answers

class CheckYourAnswersController @Inject()(appConfig: FrontendAppConfig,
                                           override val messagesApi: MessagesApi,
                                           getData: DataRetrievalAction,
                                           requireData: DataRequiredAction,
                                           checkYourAnswers: check_your_answers,
                                           cc: MessagesControllerComponents
                                          ) extends FrontendController(cc) with I18nSupport {


  def onPageLoad() = (getData andThen requireData) {
    implicit request =>
      val link = enquiryBackLink(request.userAnswers)

      userAnswersSectionBuilder(request.userAnswers) match {
        case Some(s) => Ok(checkYourAnswers(appConfig, s, link))
        case None => {
          Logger.warn("Navigation for Check your answers page reached without selection of enquiry by controller")
          throw new RuntimeException("Navigation for check your anwsers page reached without selection of enquiry by controller")
        }
      }
  }

  def goToConfirmationPage() = (getData andThen requireData) {
    Redirect(routes.ConfirmationController.onPageLoadSendEmail())
  }

  private[controllers] def userAnswersSectionBuilder(answers: UserAnswers): Option[Seq[AnswerSection]] = {
    import answers._
    val checkYourAnswersHelper = new CheckYourAnswersHelper(answers)

    (contactReason, enquiryCategory) match {
      case (Some("more_details"), _) => Some(Seq(
        AnswerSection(None, Seq(
          checkYourAnswersHelper.existingEnquiryCategory,
          checkYourAnswersHelper.refNumber,
          checkYourAnswersHelper.contactDetails,
          checkYourAnswersHelper.propertyAddress,
          checkYourAnswersHelper.whatElse).flatten)))
      case (Some("update_existing"), _) => Some(Seq(
        AnswerSection(None, Seq(
          checkYourAnswersHelper.existingEnquiryCategory,
          checkYourAnswersHelper.refNumber,
          checkYourAnswersHelper.contactDetails,
          checkYourAnswersHelper.propertyAddress,
          checkYourAnswersHelper.anythingElse).flatten)))
      case (_, Some("business_rates")) => Some(Seq(
        AnswerSection(None, Seq(
          checkYourAnswersHelper.enquiryCategory,
          checkYourAnswersHelper.businessRatesSubcategory,
          checkYourAnswersHelper.contactDetails,
          checkYourAnswersHelper.propertyAddress,
          checkYourAnswersHelper.tellUsMore).flatten)))
      case (_, Some("council_tax")) => Some(Seq(
        AnswerSection(None, Seq(
          checkYourAnswersHelper.enquiryCategory,
          checkYourAnswersHelper.councilTaxSubcategory,
          checkYourAnswersHelper.contactDetails,
          checkYourAnswersHelper.propertyAddress,
          checkYourAnswersHelper.tellUsMore).flatten)))
      case _ => None
    }
  }

  private[controllers] def enquiryBackLink(answers: UserAnswers): String = {
    answers.contactReason match {
      case Some("new_enquiry") => routes.TellUsMoreController.onPageLoad(NormalMode).url
      case Some("more_details")  => routes.WhatElseController.onPageLoad().url
      case Some("update_existing")  => routes.AnythingElseTellUsController.onPageLoad().url
      case _ => {
        Logger.warn("Navigation for Check your answers page reached without selection of contact reason by controller")
        throw new RuntimeException("Navigation for check your anwsers page reached without selection of contact reason by controller")
      }
    }
  }
}

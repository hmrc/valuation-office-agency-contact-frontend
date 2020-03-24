/*
 * Copyright 2020 HM Revenue & Customs
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

import javax.inject.{Inject, Singleton}
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import uk.gov.hmrc.valuationofficeagencycontactfrontend.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors.LightweightContactEventsConnector
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers.CheckYourAnswersId
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.NormalMode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.{DateFormatter, UserAnswers}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.confirmation
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.SatisfactionSurveyForm

import scala.util.{Failure, Success}

@Singleton()
class ConfirmationController @Inject()(val appConfig: FrontendAppConfig,
                                       val messagesApi: MessagesApi,
                                       val connector: LightweightContactEventsConnector,
                                       navigator: Navigator,
                                       getData: DataRetrievalAction,
                                       requireData: DataRequiredAction) extends FrontendController with I18nSupport {

  def enquiryKey(answers: UserAnswers): Either[String, String] = {
    answers.enquiryCategory match {
      case Some("council_tax") => Right("councilTaxSubcategory")
      case Some("business_rates") => Right("businessRatesSubcategory")
      case _ => Left("Unknown enquiry category in enquiry key")
    }
  }

  def onPageLoadSendEmail = (getData andThen requireData).async { implicit request =>

    val contact = request.userAnswers.contact() match {
      case Right(ct) => ct
      case Left(msg) =>
        Logger.warn(s"Navigation for Confirmation page reached without a contact and error $msg")
        throw new RuntimeException(s"Navigation for Confirmation page reached without a contact and error $msg")
    }

    val result = connector.send(contact, messagesApi)

    result map {
      case Success(s) =>
        enquiryKey(request.userAnswers) match {
          case Right(key) =>
            Redirect(navigator.nextPage(CheckYourAnswersId, NormalMode)(request.userAnswers))
          case Left(msg) => {
            Logger.warn(s"Navigation for Confirmation page reached with error $msg")
            throw new RuntimeException(s"Navigation for Confirmation page reached with error $msg")
          }
        }
      case Failure(ex) => throw new RuntimeException(s"Navigation for Confirmation page reached with error")
    }
  }

  def onPageLoad = (getData andThen requireData) { implicit request =>

    val contact = request.userAnswers.contact() match {
      case Right(ct) => ct
      case Left(msg) =>
        Logger.warn(s"Navigation for Confirmation page reached without a contact and error $msg")
        throw new RuntimeException(s"Navigation for Confirmation page reached without a contact and error $msg")
    }

    val date = DateFormatter.todaysDate()
    Ok(confirmation(appConfig, contact, date, enquiryKey(request.userAnswers).right.get, SatisfactionSurveyForm.apply))
  }
}

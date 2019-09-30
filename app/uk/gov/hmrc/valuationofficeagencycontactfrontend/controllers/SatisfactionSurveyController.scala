/*
 * Copyright 2019 HM Revenue & Customs
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
import play.api.mvc.Action
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import uk.gov.hmrc.valuationofficeagencycontactfrontend.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{confirmation, satisfactionSurveyThankYou}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.{SatisfactionSurvey, SatisfactionSurveyForm}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.{AddressFormatters, DateFormatter, UserAnswers}

@Singleton()
class SatisfactionSurveyController @Inject()(val appConfig: FrontendAppConfig,
                                             val messagesApi: MessagesApi,
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

  def formCompleteFeedback  = (getData andThen requireData) { implicit request =>

    val contact = request.userAnswers.contact() match {
      case Right(ct) => ct
      case Left(msg) =>
        Logger.warn(s"Navigation for Confirmation page reached without a contact and error $msg")
        throw new RuntimeException(s"Navigation for Confirmation page reached without a contact and error $msg")
    }

    SatisfactionSurveyForm().bindFromRequest().fold(
      formWithErrors => {
        val date = DateFormatter.todaysDate()
        Ok(confirmation(appConfig, contact, date, enquiryKey(request.userAnswers).right.get, formWithErrors))
      },
      success => {
        sendFeedback(success, AddressFormatters.formattedPropertyAddress(contact.propertyAddress, ", "))
        Redirect(routes.SatisfactionSurveyController.surveyThankyou())
      }
    )
  }

  private def sendFeedback(f: SatisfactionSurvey, refNum: String) = {
    // TODO Send feedback to Splunk - VOA-1314
    Logger.warn("TODO Send feedback to Splunk - VOA-1314")
    Logger.warn(s"****** SurveySatisfaction (satisfaction:  ${f.satisfaction} : referenceNumber ${refNum}) ******")
    Logger.warn(s"****** SurveyFeedback (feedback:  ${f.details} : referenceNumber ${refNum}) ******")
  }

  def surveyThankyou = Action { implicit request =>
    Ok(satisfactionSurveyThankYou(appConfig))
  }
}

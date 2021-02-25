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

import javax.inject.{Inject, Singleton}
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{MessagesControllerComponents, Request}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.HeaderCarrierConverter
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors.AuditingService
import uk.gov.hmrc.valuationofficeagencycontactfrontend.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{confirmation => Confirmation}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{satisfactionSurveyThankYou => satisfaction_Survey_Thank_You}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.{SatisfactionSurvey, SatisfactionSurveyForm}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.{AddressFormatters, DateFormatter, UserAnswers}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.ConfirmationController.whatHappensNextMessages

import scala.concurrent.ExecutionContext

@Singleton()
class SatisfactionSurveyController @Inject()(val appConfig: FrontendAppConfig,
                                             override val messagesApi: MessagesApi,
                                             navigator: Navigator,
                                             getData: DataRetrievalAction,
                                             requireData: DataRequiredAction,
                                             auditService: AuditingService,
                                             confirmation: Confirmation,
                                             satisfactionSurveyThankYou: satisfaction_Survey_Thank_You,
                                             cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends FrontendController(cc) with I18nSupport {

  implicit def hc(implicit request: Request[_]):HeaderCarrier = HeaderCarrierConverter.fromHeadersAndSession(request.headers, Some(request.session))

  def enquiryKey(answers: UserAnswers): Either[String, String] = {
    answers.enquiryCategory match {
      case Some("council_tax") => Right("councilTaxSubcategory")
      case Some("business_rates") => Right("businessRatesSubcategory")
      case _ => Left("Unknown enquiry category in enquiry key")
    }
  }

  def formCompleteFeedback  = (getData andThen requireData) { implicit request =>

    val (contact, answerSections) = (request.userAnswers.contact(), request.userAnswers.answerSection) match {
      case (Right(ct), Some(as)) => (ct, as)
      case (Left(msg), _) =>
        Logger.warn(s"Navigation for Survey page reached without a contact and error $msg")
        throw new RuntimeException(s"Navigation for Survey page reached without a contact and error $msg")
    }

    SatisfactionSurveyForm().bindFromRequest().fold(
      formWithErrors => {
        val date = DateFormatter.todaysDate()
        Ok(confirmation(appConfig, contact, answerSections, whatHappensNextMessages(request.userAnswers), formWithErrors))
      },
      success => {
        sendFeedback(success, AddressFormatters.formattedPropertyAddress(contact.propertyAddress, ", "))
        Redirect(routes.SatisfactionSurveyController.surveyThankyou())
      }
    )
  }

  private def sendFeedback(f: SatisfactionSurvey, refNum: String)(implicit headerCarrier: HeaderCarrier) {
    auditService.sendSatisfactionSurvey("SurveySatisfaction", Map("satisfaction" -> f.satisfaction, "referenceNumber" -> refNum)).flatMap { _ =>
      auditService.sendSatisfactionSurvey("SurveyFeedback", Map("feedback" -> f.details.getOrElse(""), "referenceNumber" -> refNum))
    }
  }

  def surveyThankyou = Action { implicit request =>
    Ok(satisfactionSurveyThankYou(appConfig))
  }
}

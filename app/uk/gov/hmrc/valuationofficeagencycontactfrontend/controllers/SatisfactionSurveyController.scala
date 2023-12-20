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

import javax.inject.{Inject, Singleton}
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{MessagesControllerComponents, Request}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors.AuditingService
import uk.gov.hmrc.valuationofficeagencycontactfrontend.FrontendAppConfig
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{confirmation => Confirmation}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{satisfactionSurveyThankYou => satisfaction_Survey_Thank_You}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.{SatisfactionSurvey, SatisfactionSurveyForm}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.{AddressFormatters, UserAnswers}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.ConfirmationController.whatHappensNextMessages

import scala.concurrent.ExecutionContext
import play.api.mvc
import play.api.mvc.AnyContent

@Singleton()
class SatisfactionSurveyController @Inject() (
  val appConfig: FrontendAppConfig,
  override val messagesApi: MessagesApi,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  auditService: AuditingService,
  confirmation: Confirmation,
  satisfactionSurveyThankYou: satisfaction_Survey_Thank_You,
  cc: MessagesControllerComponents
)(implicit ec: ExecutionContext
) extends FrontendController(cc)
  with I18nSupport {

  private val log = Logger(this.getClass)

  implicit def hc(implicit request: Request[_]): HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)

  def enquiryKey(answers: UserAnswers): Either[String, String] =
    answers.enquiryCategory match {
      case Some("council_tax")    => Right("councilTaxSubcategory")
      case Some("business_rates") => Right("businessRatesSubcategory")
      case _                      => Left("Unknown enquiry category in enquiry key")
    }

  def formCompleteFeedback: mvc.Action[AnyContent] = (getData andThen requireData) { implicit request =>
    val (contact, answerSections) = (request.userAnswers.contact(), request.userAnswers.answerSection) match {
      case (Right(ct), Some(as)) => (ct, as)
      case (Left(msg), _)        =>
        log.warn(s"Navigation for Survey page reached without a contact and error $msg")
        throw new RuntimeException(s"Navigation for Survey page reached without a contact and error $msg")
    }

    SatisfactionSurveyForm().bindFromRequest().fold(
      formWithErrors =>
        Ok(confirmation(appConfig, contact, answerSections, whatHappensNextMessages(request.userAnswers), formWithErrors)),
      success => {
        auditService.sendRadioButtonSelection(request.uri, "satisfaction" -> success.satisfaction)
        sendFeedback(success, AddressFormatters.formattedPropertyAddress(contact.propertyAddress, ", "))

        val call = routes.SatisfactionSurveyController.surveyThankyou
        auditService.sendContinueNextPage(call.url)
        Redirect(call)
      }
    )
  }

  private def sendFeedback(f: SatisfactionSurvey, refNum: String)(implicit headerCarrier: HeaderCarrier): Unit =
    auditService.sendSurveySatisfaction(Map("satisfaction" -> f.satisfaction, "referenceNumber" -> refNum)).flatMap { _ =>
      auditService.sendSurveyFeedback(Map("feedback" -> f.details.getOrElse(""), "referenceNumber" -> refNum))
    }

  def surveyThankyou: mvc.Action[AnyContent] = Action { implicit request =>
    Ok(satisfactionSurveyThankYou(appConfig))
  }

}

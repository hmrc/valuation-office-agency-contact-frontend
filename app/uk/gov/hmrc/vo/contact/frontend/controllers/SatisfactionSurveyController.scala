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

import play.api.Logging
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Request}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import uk.gov.hmrc.vo.contact.frontend.connectors.AuditingService
import uk.gov.hmrc.vo.contact.frontend.controllers.ConfirmationController.whatHappensNextMessages
import uk.gov.hmrc.vo.contact.frontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.vo.contact.frontend.forms.{SatisfactionSurvey, SatisfactionSurveyForm}
import uk.gov.hmrc.vo.contact.frontend.utils.{AddressFormatters, UserAnswers}
import uk.gov.hmrc.vo.contact.frontend.views.html.{confirmation, satisfactionSurveyThankYou}

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext
import scala.language.implicitConversions

@Singleton()
class SatisfactionSurveyController @Inject() (
  override val messagesApi: MessagesApi,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  auditService: AuditingService,
  confirmation: confirmation,
  satisfactionSurveyThankYou: satisfactionSurveyThankYou,
  cc: MessagesControllerComponents
)(using ec: ExecutionContext
) extends FrontendController(cc)
  with I18nSupport
  with Logging:

  implicit def hc(using request: Request[?]): HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)

  def enquiryKey(answers: UserAnswers): Either[String, String] =
    answers.enquiryCategory match
      case Some("council_tax")    => Right("councilTaxSubcategory")
      case Some("business_rates") => Right("businessRatesSubcategory")
      case _                      => Left("Unknown enquiry category in enquiry key")

  def formCompleteFeedback: Action[AnyContent] = (getData andThen requireData) { implicit request =>
    val (contact, answerSections) = (request.userAnswers.contact(), request.userAnswers.answerSection) match
      case (Right(ct), Some(as)) => (ct, as)
      case (msg, _)              =>
        logger.warn(s"Navigation for Survey page reached without a contact and error $msg")
        throw RuntimeException(s"Navigation for Survey page reached without a contact and error $msg")

    SatisfactionSurveyForm().bindFromRequest().fold(
      formWithErrors => Ok(confirmation(contact, answerSections, whatHappensNextMessages(request.userAnswers), formWithErrors)),
      success =>
        auditService.sendRadioButtonSelection(request.uri, "satisfaction" -> success.satisfaction)
        sendFeedback(success, AddressFormatters.formattedPropertyAddress(contact.propertyAddress, ", "))

        val call = routes.SatisfactionSurveyController.surveyThankyou
        auditService.sendContinueNextPage(call.url)
        Redirect(call)
    )
  }

  private def sendFeedback(f: SatisfactionSurvey, refNum: String)(using headerCarrier: HeaderCarrier): Unit =
    auditService.sendSurveySatisfaction(Map("satisfaction" -> f.satisfaction, "referenceNumber" -> refNum)).flatMap { _ =>
      auditService.sendSurveyFeedback(Map("feedback" -> f.details.getOrElse(""), "referenceNumber" -> refNum))
    }

  def surveyThankyou: Action[AnyContent] = Action { implicit request =>
    Ok(satisfactionSurveyThankYou())
  }

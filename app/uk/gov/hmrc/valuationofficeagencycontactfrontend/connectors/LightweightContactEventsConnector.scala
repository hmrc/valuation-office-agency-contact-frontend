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

package uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors

import play.api.http.Status.OK
import play.api.i18n.MessagesApi
import play.api.libs.json._
import play.api.{Configuration, Logger}
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.{Contact, ContactWithEnMessage, EnquiryAuditEvent}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.UserAnswers

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

class LightweightContactEventsConnector @Inject()(http: HttpClient,
                                                  val configuration: Configuration,
                                                  auditService: AuditingService,
                                                  servicesConfig: ServicesConfig)(implicit ec: ExecutionContext) {

  private val log = Logger(this.getClass)
  implicit val hc: HeaderCarrier = HeaderCarrier()
  val serviceUrl = servicesConfig.baseUrl("lightweight-contact-events")
  val baseSegment = "/lightweight-contact-events/"
  val jsonContentTypeHeader = ("Content-Type", "application/json")

  def getStyleGuide = http.GET[HttpResponse](s"$serviceUrl${baseSegment}style-guide")

  def send(input: Contact, messagesApi: MessagesApi, userAnswers: UserAnswers)(implicit hc: HeaderCarrier) = {
    val msg = ContactWithEnMessage(input, messagesApi, userAnswers)
    val auditEvent = EnquiryAuditEvent(
      msg.contact,
      msg.propertyAddress,
      msg.isCouncilTaxEnquiry,
      msg.contactReason,
      msg.enquiryCategoryMsg,
      msg.subEnquiryCategoryMsg,
      msg.message,
      userAnswers.enquiryDate,
      userAnswers.refNumber)
    sendJson(Json.toJson(msg), Json.toJson(auditEvent).as[JsObject])
  }

  def sendJson(msgJson: JsValue, auditEventJson: JsObject)(implicit hc: HeaderCarrier): Future[Try[Int]] = {
    http.POST[JsValue, HttpResponse](s"$serviceUrl${baseSegment}create", msgJson, Seq(jsonContentTypeHeader))
      .map {
        response =>
          response.status match {
            case OK =>
              auditService.sendEnquiryToVOA(auditEventJson)
              Success(OK)
            case status =>
              val ex = new RuntimeException("Received status of " + status + " from upstream service")
              log.warn(ex.getMessage)
              auditService.sendFormSubmissionFailed(auditEventJson, s"Response code: $status")
              Failure(ex)
          }
      } recover {
      case throwable =>
        val ex = new RuntimeException("Received exception " + throwable.getMessage + " from upstream service")
        log.warn(ex.getMessage)
        auditService.sendFormSubmissionFailed(auditEventJson, throwable.getMessage)
        Failure(ex)
    }
  }

}

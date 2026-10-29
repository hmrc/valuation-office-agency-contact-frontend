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

package uk.gov.hmrc.vo.contact.frontend.connectors

import play.api.Logging
import play.api.http.Status.OK
import play.api.i18n.{Lang, Messages, MessagesApi}
import play.api.libs.json.*
import play.api.libs.ws.WSBodyWritables.writeableOf_JsValue
import uk.gov.hmrc.http.HttpReads.Implicits.*
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import uk.gov.hmrc.vo.contact.frontend.models.event.{FormSubmissionFailed, SendEnquiryToVOA}
import uk.gov.hmrc.vo.contact.frontend.models.{Contact, ContactWithEnMessage}
import uk.gov.hmrc.vo.contact.frontend.utils.UserAnswers
import uk.gov.hmrc.vo.service.connectors.AuditService

import java.util.Locale
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

class LightweightContactEventsConnector @Inject() (
  httpClientV2: HttpClientV2,
  audit: AuditService,
  servicesConfig: ServicesConfig,
  messagesApi: MessagesApi
)(using ec: ExecutionContext
) extends Logging:

  private val serviceUrl                              = servicesConfig.baseUrl("lightweight-contact-events")
  private val baseSegment                             = "/lightweight-contact-events"
  private val jsonContentTypeHeader: (String, String) = "Content-Type" -> "application/json"

  def send(contact: Contact, userAnswers: UserAnswers)(using hc: HeaderCarrier): Future[Try[Int]] =
    given Messages = messagesApi.preferred(Seq(Lang(Locale.UK)))

    val contactMessage = ContactWithEnMessage(contact, userAnswers)

    sendContactMessage(contactMessage, userAnswers)

  private def sendContactMessage(contactMessage: ContactWithEnMessage, userAnswers: UserAnswers)(using hc: HeaderCarrier): Future[Try[Int]] =
    val url = s"$serviceUrl$baseSegment/create"
    httpClientV2.post(url"$url")
      .withBody(Json.toJson(contactMessage))
      .setHeader(jsonContentTypeHeader)
      .execute[HttpResponse]
      .map {
        response =>
          response.status match
            case OK     =>
              audit.send(SendEnquiryToVOA(contactMessage, userAnswers))
              Success(OK)
            case status =>
              val ex = RuntimeException("Received status of " + status + " from upstream service")
              logger.warn(ex.getMessage)
              audit.send(FormSubmissionFailed(contactMessage, userAnswers, s"Response code: $status"))
              Failure(ex)
      }
      .recover {
        case throwable =>
          val ex = RuntimeException("Received exception " + throwable.getMessage + " from upstream service")
          logger.warn(ex.getMessage)
          audit.send(FormSubmissionFailed(contactMessage, userAnswers, throwable.getMessage))
          Failure(ex)
      }

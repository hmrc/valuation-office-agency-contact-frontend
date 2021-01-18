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

package uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors

import javax.inject.Inject
import play.api.i18n.MessagesApi
import play.api.libs.json._
import play.api.{Configuration, Logger}
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.{Contact, ContactWithEnMessage}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}
import uk.gov.hmrc.http.HttpReads.Implicits._

class LightweightContactEventsConnector @Inject()(http: HttpClient,
                                                  val configuration: Configuration,
                                                   auditService: AuditingService,
                                                  servicesConfig: ServicesConfig) {

  implicit val hc: HeaderCarrier = HeaderCarrier()
  val serviceUrl = servicesConfig.baseUrl("lightweight-contact-events")
  val baseSegment = "/lightweight-contact-events/"
  val jsonContentTypeHeader = ("Content-Type", "application/json")

  def getStyleGuide = http.GET[HttpResponse](s"$serviceUrl${baseSegment}style-guide")

  def send(input: Contact, messagesApi: MessagesApi)(implicit hc: HeaderCarrier) = sendJson(Json.toJson(ContactWithEnMessage(input, messagesApi)))

  def sendJson(json: JsValue)(implicit hc: HeaderCarrier): Future[Try[Int]] = {
    http.POST[JsValue, HttpResponse](s"$serviceUrl${baseSegment}create", json, Seq(jsonContentTypeHeader))
      .map {
        response =>
          response.status match {
            case 200 =>
              auditService.sendEvent("sendenquirytoVOA", json)
              Success(200)
            case status => {
              Logger.warn("Received status of " + status + " from upstream service")
              Failure(new RuntimeException("Received status of " + status + " from upstream service"))
            }
          }
      } recover {
      case e =>
        Logger.warn("Received exception " + e.getMessage + " from upstream service")
        Failure(new RuntimeException("Received exception " + e.getMessage + " from upstream service"))
    }
  }

}


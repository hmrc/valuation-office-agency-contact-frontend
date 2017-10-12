/*
 * Copyright 2017 HM Revenue & Customs
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

import javax.inject.{Inject, Singleton}

import play.api.Configuration
import play.api.libs.json._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.exceptions.JsonInvalidException
import uk.gov.hmrc.valuationofficeagencycontactfrontend.json.JsonErrorProcessor
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.{Contact, Reference}
import uk.gov.hmrc.play.bootstrap.config.BaseUrl

import scala.util.{Failure, Success}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.ExecutionContext.Implicits.global

class LightweightContactEventsConnector @Inject()(http: HttpClient, override val configuration: Configuration) extends BaseUrl {

  implicit val hc: HeaderCarrier = HeaderCarrier()
  lazy val serviceUrl = baseUrl("lightweight-contact-events")
  val baseSegment = "/lightweight-contact-events/"
  val jsonContentTypeHeader = ("Content-Type", "application/json")

  def getStyleGuide = http.GET(s"$serviceUrl${baseSegment}style-guide")

  def send(input: Contact) = sendJson(Json.toJson(input))

  def sendJson(json: JsValue) =
    http.POST(s"$serviceUrl${baseSegment}create", json, Seq(jsonContentTypeHeader))
      .map {
        response =>
          Json.fromJson[Reference](response.json) match {
            case JsSuccess(result, _) => Success(result)
            case JsError(error) => {
              Failure(new JsonInvalidException(JsonErrorProcessor(error)))
            }
          }
      }
}


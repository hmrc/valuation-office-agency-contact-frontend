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

import org.mockito.ArgumentCaptor
import org.mockito.Matchers._
import org.mockito.Mockito.{verify, when}
import org.scalatest.mockito.MockitoSugar
import play.api.Configuration
import play.api.http.Status
import play.api.libs.json._
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.SpecBase
import uk.gov.hmrc.valuationofficeagencycontactfrontend.exceptions.JsonInvalidException
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models._

import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success, Try}
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads, HttpResponse}
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.ExecutionContext.Implicits.global

class LightweightContactEventsConnectorSpec extends SpecBase with MockitoSugar {

  def getHttpMock(returnedData: JsValue) = {
    val httpMock = mock[HttpClient]
    when(httpMock.POST(anyString, any[JsValue], any[Seq[(String, String)]])(any[Writes[Any]], any[HttpReads[Any]],
      any[HeaderCarrier], any())) thenReturn Future.successful(HttpResponse(Status.OK, Some(returnedData)))
    when(httpMock.GET(anyString)(any[HttpReads[Any]], any[HeaderCarrier], any())) thenReturn Future.successful(HttpResponse(Status.OK, Some(returnedData)))
    httpMock
  }

  val configuration = injector.instanceOf[Configuration]
  val minimalJson = JsObject(Map[String, JsValue]())

  val message = "message"
  val enquiryCategory = "EC"
  val subEnquiryCategory = "SEC"
  val contactDetails = ContactDetails("first", "last", "email", "email", "tel", "mob", "pref")
  val confirmedContactDetails = ConfirmedContactDetails(contactDetails)
  val councilTaxAddress = CouncilTaxAddress("a", "b", "c", "d", "e")

  val contactModel = Contact(confirmedContactDetails, Some(councilTaxAddress), None, enquiryCategory, subEnquiryCategory, message)

  "LightweightContactEvents Connector" when {

    "provided with a Contact Model Input" must {
      "call the Microservice with the given JSON" in {
        implicit val headerCarrierNapper = ArgumentCaptor.forClass(classOf[HeaderCarrier])
        implicit val httpReadsNapper = ArgumentCaptor.forClass(classOf[HttpReads[Any]])
        implicit val jsonWritesNapper = ArgumentCaptor.forClass(classOf[Writes[Any]])
        val urlCaptor = ArgumentCaptor.forClass(classOf[String])
        val bodyCaptor = ArgumentCaptor.forClass(classOf[JsValue])
        val headersCaptor = ArgumentCaptor.forClass(classOf[Seq[(String, String)]])
        val httpMock = getHttpMock(minimalJson)

        val connector = new LightweightContactEventsConnector(httpMock, configuration)
        connector.send(contactModel)

        verify(httpMock).POST(urlCaptor.capture, bodyCaptor.capture, headersCaptor.capture)(jsonWritesNapper.capture,
          httpReadsNapper.capture, headerCarrierNapper.capture, any())
        urlCaptor.getValue must endWith(s"${connector.baseSegment}create")
        bodyCaptor.getValue mustBe Json.toJson(contactModel)
        headersCaptor.getValue mustBe Seq(connector.jsonContentTypeHeader)
      }

      "return a case class representing the received JSON when the send method is successful" in {
        val enquiryType = "council-tax"
        val reference = "XG68H5"

        val referenceResult = Reference(enquiryType, reference)

        new LightweightContactEventsConnector(getHttpMock(Json.toJson(referenceResult)), configuration).send(contactModel).map {
          case Success(r) => r mustBe referenceResult
          case Failure(e) => assert(false)
        }

      }

      "return a string representing the error when send method fails" in {
        val errorResponse = JsString("Something went wrong!")

        new LightweightContactEventsConnector(getHttpMock(errorResponse), configuration).send(contactModel).map {
          case Failure(e) => {
            e mustBe a[JsonInvalidException]
            e.getMessage() mustBe List.fill(5)("JSON error: error.path.missing\n").mkString("")
          }
          case Success(_) => fail
        }
      }
    }

    "provided with JSON directly" must {

      "call the Microservice with the given JSON" in {
        implicit val headerCarrierNapper = ArgumentCaptor.forClass(classOf[HeaderCarrier])
        implicit val httpReadsNapper = ArgumentCaptor.forClass(classOf[HttpReads[Any]])
        implicit val jsonWritesNapper = ArgumentCaptor.forClass(classOf[Writes[Any]])
        val urlCaptor = ArgumentCaptor.forClass(classOf[String])
        val bodyCaptor = ArgumentCaptor.forClass(classOf[JsValue])
        val headersCaptor = ArgumentCaptor.forClass(classOf[Seq[(String, String)]])
        val httpMock = getHttpMock(minimalJson)

        val connector = new LightweightContactEventsConnector(httpMock, configuration)
        connector.sendJson(minimalJson)

        verify(httpMock).POST(urlCaptor.capture, bodyCaptor.capture, headersCaptor.capture)(jsonWritesNapper.capture,
          httpReadsNapper.capture, headerCarrierNapper.capture, any())
        urlCaptor.getValue must endWith(s"${connector.baseSegment}create")
        bodyCaptor.getValue mustBe minimalJson
        headersCaptor.getValue mustBe Seq(connector.jsonContentTypeHeader)
      }

      "return a case class representing the received JSON when the send method is successful" in {
        val enquiryType = "council-tax"
        val reference = "XG68H5"

        val referenceResult = Reference(enquiryType, reference)

        new LightweightContactEventsConnector(getHttpMock(Json.toJson(referenceResult)), configuration).sendJson(minimalJson).map {
          case Success(r) => r mustBe referenceResult
          case Failure(e) => assert(false)
        }

      }

      "return a string representing the error when send method fails" in {
        val errorResponse = JsString("Something went wrong!")

        new LightweightContactEventsConnector(getHttpMock(errorResponse), configuration).sendJson(minimalJson).map {
          case Failure(exception) => {
            exception mustBe a[JsonInvalidException]
            exception.getMessage() mustBe List.fill(5)("JSON error: error.path.missing\n").mkString("")
          }
          case Success(_) => fail
        }
      }

    }
  }
}
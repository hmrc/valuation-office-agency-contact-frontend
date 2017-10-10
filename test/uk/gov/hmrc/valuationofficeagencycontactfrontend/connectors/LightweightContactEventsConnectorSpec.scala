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
import play.api.http.Status
import play.api.libs.json._
import uk.gov.hmrc.play.test.WithFakeApplication
import uk.gov.hmrc.valuationofficeagencycontactfrontend.SpecBase
import uk.gov.hmrc.valuationofficeagencycontactfrontend.exceptions.JsonInvalidException
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models._

import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads, HttpResponse}
import uk.gov.hmrc.play.http.ws.WSHttp

class RnrbConnectorSpec extends SpecBase with WithFakeApplication with MockitoSugar {

  def getHttpMock(returnedData: JsValue) = {
    val httpMock = mock[WSHttp]
    when(httpMock.POST(anyString, any[JsValue], any[Seq[(String, String)]])(any[Writes[Any]], any[HttpReads[Any]],
      any[HeaderCarrier], any())) thenReturn Future.successful(HttpResponse(Status.OK, Some(returnedData)))
    when(httpMock.GET(anyString)(any[HttpReads[Any]], any[HeaderCarrier], any())) thenReturn Future.successful(HttpResponse(Status.OK, Some(returnedData)))
    httpMock
  }

  val minimalJson = JsObject(Map[String, JsValue]())

  val message = "message"
  val enquiryCategory = "EC"
  val subEnquiryCategory = "SEC"
  val contactDetails = ContactDetails("first", "last", "email", "email", "tel", "mob", "pref")
  val confirmedContactDetails = ConfirmedContactDetails(contactDetails)
  val councilTaxAddress = CouncilTaxAddress("a", "b", "c", "d", "e")

  val contactModel = ContactModel(confirmedContactDetails, Some(councilTaxAddress), None, enquiryCategory, subEnquiryCategory,message)

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

        val connector = new LightweightContactEventsConnector(httpMock)
        await(connector.send(contactModel))

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

        val result = await(new LightweightContactEventsConnector(getHttpMock(Json.toJson(referenceResult))).send(contactModel))

        result mustBe referenceResult
      }

      "return a string representing the error when send method fails" in {
        val errorResponse = JsString("Something went wrong!")

        val result = await(new LightweightContactEventsConnector(getHttpMock(errorResponse)).send(contactModel))

        result match {
          case Failure(exception) => {
            exception mustBe a[JsonInvalidException]
            exception.getMessage() mustBe List.fill(5)("JSON error: error.path.missing\n").mkString("")
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

        val connector = new LightweightContactEventsConnector(httpMock)
        await(connector.sendJson(minimalJson))

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

        val result = await(new LightweightContactEventsConnector(getHttpMock(Json.toJson(referenceResult))).sendJson(minimalJson))

        result mustBe referenceResult
      }

      "return a string representing the error when send method fails" in {
        val errorResponse = JsString("Something went wrong!")

        val result = await(new LightweightContactEventsConnector(getHttpMock(errorResponse)).sendJson(minimalJson))

        result match {
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
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
import play.api.i18n.MessagesApi

import scala.concurrent.ExecutionContext.Implicits.global

class LightweightContactEventsConnectorSpec extends SpecBase with MockitoSugar {

  def getHttpMock(returnedStatus: Int) = {
    val httpMock = mock[HttpClient]
    when(httpMock.POST(anyString, any[JsValue], any[Seq[(String, String)]])(any[Writes[Any]], any[HttpReads[Any]],
      any[HeaderCarrier], any())) thenReturn Future.successful(HttpResponse(returnedStatus, None))
    when(httpMock.GET(anyString)(any[HttpReads[Any]], any[HeaderCarrier], any())) thenReturn Future.successful(HttpResponse(returnedStatus, None))
    httpMock
  }

  val configuration = injector.instanceOf[Configuration]
  val minimalJson = JsObject(Map[String, JsValue]())

  val message = "message"
  val enquiryCategory = "council_tax"
  val subEnquiryCategory = "council_tax_poor_repair"
  val contactDetails = ContactDetails("first", "last", "email", "email", "contactNumber")
  val confirmedContactDetails = ConfirmedContactDetails(contactDetails)
  val propertyAddress = PropertyAddress("a", Some("b"), "c", Some("d"), "e")
  val alternativePropertyAddress = PropertyAddress("a", None, "c", None, "e")

  val contactModel = Contact(confirmedContactDetails, propertyAddress, enquiryCategory, subEnquiryCategory, message)

  val contactModelWithMessages = ContactWithEnMessage(contactModel, messagesApi)
  val alternativeContactModel = Contact(confirmedContactDetails, alternativePropertyAddress, enquiryCategory, subEnquiryCategory, message)
  val alternativeContactModelWithMessages = ContactWithEnMessage(alternativeContactModel, messagesApi)

  "LightweightContactEvents Connector" when {

    "provided with a Contact Model Input" must {
      "call the Microservice with the given JSON for propertyAddress" in {
        implicit val headerCarrierNapper = ArgumentCaptor.forClass(classOf[HeaderCarrier])
        implicit val httpReadsNapper = ArgumentCaptor.forClass(classOf[HttpReads[Any]])
        implicit val jsonWritesNapper = ArgumentCaptor.forClass(classOf[Writes[Any]])
        val urlCaptor = ArgumentCaptor.forClass(classOf[String])
        val bodyCaptor = ArgumentCaptor.forClass(classOf[JsValue])
        val headersCaptor = ArgumentCaptor.forClass(classOf[Seq[(String, String)]])
        val httpMock = getHttpMock(200)

        val connector = new LightweightContactEventsConnector(httpMock, configuration)
        connector.send(contactModel, messagesApi)

        verify(httpMock).POST(urlCaptor.capture, bodyCaptor.capture, headersCaptor.capture)(jsonWritesNapper.capture,
          httpReadsNapper.capture, headerCarrierNapper.capture, any())
        urlCaptor.getValue must endWith(s"${connector.baseSegment}create")
        bodyCaptor.getValue mustBe Json.toJson(contactModelWithMessages)
        headersCaptor.getValue mustBe Seq(connector.jsonContentTypeHeader)
      }

      "return a 200 status when the send method is successfull using contactModel" in {
        val enquiryType = "council-tax"

        new LightweightContactEventsConnector(getHttpMock(200), configuration).send(contactModel, messagesApi).map {
          case Success(status) => status mustBe 200
          case Failure(e) => assert(false)
        }
      }

      "call the Microservice with the given JSON for alternativePropertyAddress" in {
        implicit val headerCarrierNapper = ArgumentCaptor.forClass(classOf[HeaderCarrier])
        implicit val httpReadsNapper = ArgumentCaptor.forClass(classOf[HttpReads[Any]])
        implicit val jsonWritesNapper = ArgumentCaptor.forClass(classOf[Writes[Any]])
        val urlCaptor = ArgumentCaptor.forClass(classOf[String])
        val bodyCaptor = ArgumentCaptor.forClass(classOf[JsValue])
        val headersCaptor = ArgumentCaptor.forClass(classOf[Seq[(String, String)]])
        val httpMock = getHttpMock(200)

        val connector = new LightweightContactEventsConnector(httpMock, configuration)
        connector.send(alternativeContactModel, messagesApi)

        verify(httpMock).POST(urlCaptor.capture, bodyCaptor.capture, headersCaptor.capture)(jsonWritesNapper.capture,
          httpReadsNapper.capture, headerCarrierNapper.capture, any())
        urlCaptor.getValue must endWith(s"${connector.baseSegment}create")
        bodyCaptor.getValue mustBe Json.toJson(alternativeContactModelWithMessages)
        headersCaptor.getValue mustBe Seq(connector.jsonContentTypeHeader)
      }

      "return a 200 status when the send method is successful using alternativeContactModel" in {
        val enquiryType = "council-tax"

        new LightweightContactEventsConnector(getHttpMock(200), configuration).send(alternativeContactModel, messagesApi).map {
          case Success(status) => status mustBe 200
          case Failure(e) => assert(false)
        }
      }

      "return a string representing the error when send method fails" in {
        val errorResponse = JsString("Something went wrong!")

        new LightweightContactEventsConnector(getHttpMock(500), configuration).send(contactModel, messagesApi).map {
          case Failure(e) => {
            e mustBe a[RuntimeException]
            e.getMessage() mustBe "Received status of 500 from upstream service"
          }
          case Success(_) => fail
        }
      }

      "throw an exception if the backend service call fails" in {
        new LightweightContactEventsConnector(getHttpMock(500), configuration).send(contactModel, messagesApi). map {f =>
          assert(f.isFailure)
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
        val httpMock = getHttpMock(200)

        val connector = new LightweightContactEventsConnector(httpMock, configuration)
        connector.sendJson(minimalJson)

        verify(httpMock).POST(urlCaptor.capture, bodyCaptor.capture, headersCaptor.capture)(jsonWritesNapper.capture,
          httpReadsNapper.capture, headerCarrierNapper.capture, any())
        urlCaptor.getValue must endWith(s"${connector.baseSegment}create")
        bodyCaptor.getValue mustBe minimalJson
        headersCaptor.getValue mustBe Seq(connector.jsonContentTypeHeader)
      }

      "return a case class representing the received JSON when the send method is successful" in {
        new LightweightContactEventsConnector(getHttpMock(200), configuration).sendJson(minimalJson).map {
          case Success(status) => status mustBe 200
          case Failure(e) => assert(false)
        }

      }

      "return a string representing the error when send method fails" in {
        val errorResponse = JsString("Something went wrong!")

        new LightweightContactEventsConnector(getHttpMock(500), configuration).sendJson(minimalJson).map {
          case Failure(exception) => {
            exception mustBe a[JsonInvalidException]
            exception.getMessage() mustBe "Received status of 500 from upstream service"
          }
          case Success(_) => fail
        }
      }

      "throw an exception if the backend service call fails" in {
        new LightweightContactEventsConnector(getHttpMock(500), configuration).sendJson(minimalJson). map {f =>
          assert(f.isFailure)
        }
      }

    }
  }
}

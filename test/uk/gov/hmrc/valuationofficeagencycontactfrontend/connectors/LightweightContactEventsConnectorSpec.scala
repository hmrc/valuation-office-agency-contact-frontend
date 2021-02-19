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
import org.mockito.Matchers.{any, anyString}
import org.mockito.Mockito.{verify, when}
import org.scalatestplus.mockito.MockitoSugar
import play.api.libs.json.{JsValue, Json, Writes, _}
import play.api.test.Helpers._
import play.api.{Configuration, Environment}
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads, HttpResponse}
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import uk.gov.hmrc.valuationofficeagencycontactfrontend.SpecBase
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models._

import scala.concurrent.Future

class LightweightContactEventsConnectorSpec extends SpecBase with MockitoSugar {

  def getHttpMock(returnedStatus: Int) = {
    val httpMock = mock[HttpClient]
    when(httpMock.POST[JsValue, HttpResponse](anyString, any[JsValue], any[Seq[(String, String)]])(any[Writes[Any]], any[HttpReads[HttpResponse]],
      any[HeaderCarrier], any())) thenReturn Future.successful(HttpResponse(returnedStatus, None))
    when(httpMock.GET[HttpResponse](anyString)(any[HttpReads[HttpResponse]], any[HeaderCarrier], any())) thenReturn Future.successful(HttpResponse(returnedStatus, None))
    httpMock
  }

  val configuration = injector.instanceOf[Configuration]
  val environment = injector.instanceOf[Environment]
  val auditService = injector.instanceOf[AuditingService]
  val minimalJson = JsObject(Map[String, JsValue]())

  val message = "message"
  val enquiryCategory = "council_tax"
  val subEnquiryCategory = "property_demolished"
  val contactDetails = ContactDetails("first", "email", "contactNumber")
  val propertyAddress = PropertyAddress("a", Some("b"), "c", Some("d"), "e")
  val alternativePropertyAddress = PropertyAddress("a", None, "c", None, "e")

  val contactModel = Contact(contactDetails, propertyAddress, enquiryCategory, subEnquiryCategory, message)
  val userAnswers = new FakeUserAnswers(contactDetails, "council_tax", "council_tax", "", propertyAddress, TellUsMore("message"))

  val contactModelWithMessages = ContactWithEnMessage(contactModel, messagesApi, userAnswers)
  val alternativeContactModel = Contact(contactDetails, alternativePropertyAddress, enquiryCategory, subEnquiryCategory, message)
  val alternativeContactModelWithMessages = ContactWithEnMessage(alternativeContactModel, messagesApi, userAnswers)

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

        val connector = new LightweightContactEventsConnector(httpMock, configuration, auditService, servicesConfig)
        connector.send(contactModel, messagesApi, userAnswers)(HeaderCarrier())

        verify(httpMock).POST(urlCaptor.capture, bodyCaptor.capture, headersCaptor.capture)(jsonWritesNapper.capture,
          httpReadsNapper.capture, headerCarrierNapper.capture, any())
        urlCaptor.getValue must endWith(s"${connector.baseSegment}create")
        bodyCaptor.getValue mustBe Json.toJson(contactModelWithMessages)
        headersCaptor.getValue mustBe Seq(connector.jsonContentTypeHeader)
      }

      "return a 200 status when the send method is successfull using contactModel" in {
        val connector = new LightweightContactEventsConnector(getHttpMock(200), configuration,auditService, servicesConfig)
        val result = await(connector.send(contactModel, messagesApi, userAnswers)(HeaderCarrier()))
        result.isSuccess mustBe true
        result.get mustBe 200
      }

      "call the Microservice with the given JSON for alternativePropertyAddress" in {
        implicit val headerCarrierNapper = ArgumentCaptor.forClass(classOf[HeaderCarrier])
        implicit val httpReadsNapper = ArgumentCaptor.forClass(classOf[HttpReads[Any]])
        implicit val jsonWritesNapper = ArgumentCaptor.forClass(classOf[Writes[Any]])
        val urlCaptor = ArgumentCaptor.forClass(classOf[String])
        val bodyCaptor = ArgumentCaptor.forClass(classOf[JsValue])
        val headersCaptor = ArgumentCaptor.forClass(classOf[Seq[(String, String)]])
        val httpMock = getHttpMock(200)

        val connector = new LightweightContactEventsConnector(httpMock, configuration,auditService, servicesConfig)
        connector.send(alternativeContactModel, messagesApi, userAnswers)(HeaderCarrier())

        verify(httpMock).POST(urlCaptor.capture, bodyCaptor.capture, headersCaptor.capture)(jsonWritesNapper.capture,
          httpReadsNapper.capture, headerCarrierNapper.capture, any())
        urlCaptor.getValue must endWith(s"${connector.baseSegment}create")
        bodyCaptor.getValue mustBe Json.toJson(alternativeContactModelWithMessages)
        headersCaptor.getValue mustBe Seq(connector.jsonContentTypeHeader)
      }

      "return a 200 status when the send method is successful using alternativeContactModel" in {
        val connector = new LightweightContactEventsConnector(getHttpMock(200), configuration,auditService, servicesConfig)
        val result = await(connector.send(alternativeContactModel, messagesApi, userAnswers)(HeaderCarrier()))
        result.isSuccess mustBe true
        result.get mustBe 200
      }

      "return a string representing the error when send method fails" in {
        val connector = new LightweightContactEventsConnector(getHttpMock(500), configuration,auditService, servicesConfig)
        val result = await(connector.send(contactModel, messagesApi, userAnswers)(HeaderCarrier()))
        result.isFailure mustBe true
        val e = result.failed.get
        e mustBe a[RuntimeException]
        e.getMessage mustBe "Received status of 500 from upstream service"
      }

      "return a failure if the backend service call fails using Contact Model" in {
        val connector = new LightweightContactEventsConnector(getHttpMock(500), configuration,auditService, servicesConfig)
        val result = await(connector.send(contactModel, messagesApi, userAnswers)(HeaderCarrier()))
        result.isFailure mustBe true
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

        val connector = new LightweightContactEventsConnector(httpMock, configuration,auditService, servicesConfig)
        connector.sendJson(minimalJson)(HeaderCarrier())

        verify(httpMock).POST(urlCaptor.capture, bodyCaptor.capture, headersCaptor.capture)(jsonWritesNapper.capture,
          httpReadsNapper.capture, headerCarrierNapper.capture, any())
        urlCaptor.getValue must endWith(s"${connector.baseSegment}create")
        bodyCaptor.getValue mustBe minimalJson
        headersCaptor.getValue mustBe Seq(connector.jsonContentTypeHeader)
      }

      "return a case class representing the received JSON when the send method is successful" in {
        val connector = new LightweightContactEventsConnector(getHttpMock(200), configuration,auditService, servicesConfig)
        val result = await(connector.sendJson(minimalJson)(HeaderCarrier()))
        result.isSuccess mustBe true
        result.get mustBe 200
      }

      "return a string representing the error when send method fails" in {
        val connector = new LightweightContactEventsConnector(getHttpMock(500), configuration,auditService, servicesConfig)
        val result = await(connector.sendJson(minimalJson)(HeaderCarrier()))
        result.isFailure mustBe true
        val e = result.failed.get
        e mustBe a[RuntimeException]
        e.getMessage mustBe "Received status of 500 from upstream service"
      }

      "return failure if the backend service call fails using minimal Json" in {
        val connector = new LightweightContactEventsConnector(getHttpMock(500), configuration,auditService, servicesConfig)
        val result = await(connector.sendJson(minimalJson)(HeaderCarrier()))
        result.isFailure mustBe true
      }

    }
  }
}

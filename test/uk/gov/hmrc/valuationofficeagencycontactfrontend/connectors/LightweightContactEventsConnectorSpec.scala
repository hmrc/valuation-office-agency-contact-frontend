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
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{verify, when}
import org.scalatestplus.mockito.MockitoSugar
import play.api.libs.json.*
import play.api.test.Helpers.*
import play.api.{Configuration, Environment}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.valuationofficeagencycontactfrontend.SpecBase
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.*

import java.net.URL

class LightweightContactEventsConnectorSpec extends SpecBase with MockitoSugar:

  def getHttpMock(returnedStatus: Int): HttpClientV2 =
    val httpClientV2Mock = mock[HttpClientV2]
    when(
      httpClientV2Mock.post(any[URL])(using any[HeaderCarrier])
    ).thenReturn(RequestBuilderStub(Right(returnedStatus), "{}"))
    when(
      httpClientV2Mock.get(any[URL])(using any[HeaderCarrier])
    ).thenReturn(RequestBuilderStub(Right(returnedStatus), "{}"))
    httpClientV2Mock

  val configuration: Configuration  = injector.instanceOf[Configuration]
  val environment: Environment      = injector.instanceOf[Environment]
  val auditService: AuditingService = injector.instanceOf[AuditingService]
  val minimalJson: JsObject         = JsObject(Map[String, JsValue]())

  val message                                     = "message"
  val enquiryCategory                             = "council_tax"
  val subEnquiryCategory                          = "council_tax_property_demolished"
  val contactDetails: ContactDetails              = ContactDetails("first", "email", "contactNumber")
  val propertyAddress: PropertyAddress            = PropertyAddress("a", Some("b"), "c", Some("d"), "e")
  val alternativePropertyAddress: PropertyAddress = PropertyAddress("a", None, "c", None, "e")

  val contactModel: Contact = Contact(contactDetails, propertyAddress, enquiryCategory, subEnquiryCategory, message)
  val userAnswers           = new FakeUserAnswers(contactDetails, "council_tax", "council_tax", "", "", propertyAddress, TellUsMore("message"))

  val contactModelWithMessages: ContactWithEnMessage            = ContactWithEnMessage(contactModel, messagesApi, userAnswers)
  val alternativeContactModel: Contact                          = Contact(contactDetails, alternativePropertyAddress, enquiryCategory, subEnquiryCategory, message)
  val alternativeContactModelWithMessages: ContactWithEnMessage = ContactWithEnMessage(alternativeContactModel, messagesApi, userAnswers)

  "LightweightContactEvents Connector" when {

    "provided with a Contact Model Input" must {
      "call the Microservice with the given JSON for propertyAddress" in {
        val headerCarrierNapper = ArgumentCaptor.forClass(classOf[HeaderCarrier])
        val urlCaptor           = ArgumentCaptor.forClass(classOf[URL])

        val httpMock          = getHttpMock(OK)
        val headerCarrierStub = HeaderCarrier()

        val connector = new LightweightContactEventsConnector(httpMock, auditService, servicesConfig)
        connector.send(contactModel, messagesApi, userAnswers)(using headerCarrierStub)

        verify(httpMock).post(urlCaptor.capture)(using headerCarrierNapper.capture)

        urlCaptor.getValue.toString must endWith("/lightweight-contact-events/create")
        headerCarrierNapper.getValue.nsStamp mustBe headerCarrierStub.nsStamp
      }

      "return a 200 status when the send method is successfull using contactModel" in {
        val connector = new LightweightContactEventsConnector(getHttpMock(OK), auditService, servicesConfig)
        val result    = await(connector.send(contactModel, messagesApi, userAnswers)(using HeaderCarrier()))
        result.isSuccess mustBe true
        result.get mustBe OK
      }

      "call the Microservice with the given JSON for alternativePropertyAddress" in {
        val headerCarrierNapper = ArgumentCaptor.forClass(classOf[HeaderCarrier])
        val urlCaptor           = ArgumentCaptor.forClass(classOf[URL])

        val httpMock          = getHttpMock(OK)
        val headerCarrierStub = HeaderCarrier()

        val connector = new LightweightContactEventsConnector(httpMock, auditService, servicesConfig)
        connector.send(alternativeContactModel, messagesApi, userAnswers)(using headerCarrierStub)

        verify(httpMock).post(urlCaptor.capture)(using headerCarrierNapper.capture)

        urlCaptor.getValue.toString must endWith("/lightweight-contact-events/create")
        headerCarrierNapper.getValue.nsStamp mustBe headerCarrierStub.nsStamp
      }

      "return a 200 status when the send method is successful using alternativeContactModel" in {
        val connector = new LightweightContactEventsConnector(getHttpMock(OK), auditService, servicesConfig)
        val result    = await(connector.send(alternativeContactModel, messagesApi, userAnswers)(using HeaderCarrier()))
        result.isSuccess mustBe true
        result.get mustBe OK
      }

      "return a string representing the error when send method fails" in {
        val connector = new LightweightContactEventsConnector(getHttpMock(500), auditService, servicesConfig)
        val result    = await(connector.send(contactModel, messagesApi, userAnswers)(using HeaderCarrier()))
        result.isFailure mustBe true
        val e         = result.failed.get
        e mustBe a[RuntimeException]
        e.getMessage mustBe "Received status of 500 from upstream service"
      }

      "return a failure if the backend service call fails using Contact Model" in {
        val connector = new LightweightContactEventsConnector(getHttpMock(500), auditService, servicesConfig)
        val result    = await(connector.send(contactModel, messagesApi, userAnswers)(using HeaderCarrier()))
        result.isFailure mustBe true
      }
    }

    "provided with JSON directly" must {

      "call the Microservice with the given JSON" in {
        val headerCarrierNapper = ArgumentCaptor.forClass(classOf[HeaderCarrier])
        val urlCaptor           = ArgumentCaptor.forClass(classOf[URL])

        val httpMock          = getHttpMock(OK)
        val headerCarrierStub = HeaderCarrier()

        val connector = new LightweightContactEventsConnector(httpMock, auditService, servicesConfig)
        connector.sendJson(minimalJson, minimalJson)(using headerCarrierStub)

        verify(httpMock).post(urlCaptor.capture)(using headerCarrierNapper.capture)

        urlCaptor.getValue.toString must endWith("/lightweight-contact-events/create")
        headerCarrierNapper.getValue.nsStamp mustBe headerCarrierStub.nsStamp
      }

      "return a case class representing the received JSON when the send method is successful" in {
        val connector = new LightweightContactEventsConnector(getHttpMock(OK), auditService, servicesConfig)
        val result    = await(connector.sendJson(minimalJson, minimalJson)(using HeaderCarrier()))
        result.isSuccess mustBe true
        result.get mustBe OK
      }

      "return a string representing the error when send method fails" in {
        val connector = new LightweightContactEventsConnector(getHttpMock(500), auditService, servicesConfig)
        val result    = await(connector.sendJson(minimalJson, minimalJson)(using HeaderCarrier()))
        result.isFailure mustBe true
        val e         = result.failed.get
        e mustBe a[RuntimeException]
        e.getMessage mustBe "Received status of 500 from upstream service"
      }

      "return failure if the backend service call fails using minimal Json" in {
        val connector = new LightweightContactEventsConnector(getHttpMock(500), auditService, servicesConfig)
        val result    = await(connector.sendJson(minimalJson, minimalJson)(using HeaderCarrier()))
        result.isFailure mustBe true
      }

    }
  }

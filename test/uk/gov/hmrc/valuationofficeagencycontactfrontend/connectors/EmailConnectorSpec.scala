/*
 * Copyright 2025 HM Revenue & Customs
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

import org.mockito.ArgumentMatchers.{any, anyString}
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import play.api.http.Status.{ACCEPTED, BAD_REQUEST}
import play.api.i18n.{DefaultMessagesApi, Lang, Messages}
import play.api.libs.json.{JsValue, Writes}
import play.api.test.FakeRequest
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpReads, HttpResponse}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.SpecBase
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.requests.DataRequest
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.{CacheMap, Contact, ContactDetails, PropertyAddress}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.{DateUtil, UserAnswers}

import scala.concurrent.Future

/**
  * @author Yuriy Tumakha
  */
class EmailConnectorSpec extends SpecBase with MockitoSugar with ScalaFutures {

  private val contactDetails  = ContactDetails("first", "email", "contactNumber")
  private val propertyAddress = PropertyAddress("a", Some("b"), "c", Some("d"), "e")
  private val contact         = Contact(contactDetails, propertyAddress, "council_tax", "council_tax_band", "msg")

  private val messagesMap: Map[String, Map[String, String]] =
    Map("en" -> Map("enquiryCategory.council_tax" -> "CT", "councilTaxSubcategory.council_tax_band" -> "TB"))
  private val msgApi                                        = new DefaultMessagesApi(messages = messagesMap)

  implicit val hc: HeaderCarrier       = HeaderCarrier()
  implicit val request: DataRequest[?] = DataRequest(FakeRequest(), "sessionId", new UserAnswers(new CacheMap("id", Map())))
  implicit val dateUtil: DateUtil      = injector.instanceOf[DateUtil]

  private def httpMock(status: Int, body: String) = {
    val httpMock = mock[HttpClient]
    when(httpMock.POST[JsValue, HttpResponse](anyString, any[JsValue], any[Seq[(String, String)]])(
      using any[Writes[JsValue]],
      any[HttpReads[HttpResponse]],
      any[HeaderCarrier],
      any()
    )) `thenReturn` Future.successful(HttpResponse(status, body))
    httpMock
  }

  "EmailConnector" should {
    "send enquiry confirmation" in {
      val emailConnector              = new EmailConnector(servicesConfig, httpMock(ACCEPTED, ""))
      implicit val messages: Messages = msgApi.preferred(Seq(Lang("en")))

      val response = emailConnector.sendEnquiryConfirmation(contact).futureValue
      response.status mustBe ACCEPTED
      response.body mustBe ""
    }

    "handle error response on send enquiry confirmation" in {
      val body                        = """{"error":"Parameter missed"}"""
      val emailConnector              = new EmailConnector(servicesConfig, httpMock(BAD_REQUEST, body))
      implicit val messages: Messages = msgApi.preferred(Seq(Lang("en")))

      val response = emailConnector.sendEnquiryConfirmation(contact).futureValue
      response.status mustBe BAD_REQUEST
      response.body mustBe body
    }
  }

}

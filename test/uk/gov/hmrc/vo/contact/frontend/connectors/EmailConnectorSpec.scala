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

import play.api.http.Status.{ACCEPTED, BAD_REQUEST}
import play.api.i18n.{DefaultMessagesApi, Lang, Messages}
import play.api.libs.json.{JsValue, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers.POST
import uk.gov.hmrc.vo.contact.frontend.models.requests.DataRequest
import uk.gov.hmrc.vo.contact.frontend.models.{CacheMap, Contact, ContactDetails, PropertyAddress}
import uk.gov.hmrc.vo.contact.frontend.utils.{DateUtil, UserAnswers}
import uk.gov.hmrc.vo.unit.test.BaseAppSpec

/**
  * @author Yuriy Tumakha
  */
class EmailConnectorSpec extends BaseAppSpec:

  private val contactDetails  = ContactDetails("first", "email", "contactNumber")
  private val propertyAddress = PropertyAddress("a", Some("b"), "c", Some("d"), "e")
  private val contact         = Contact(contactDetails, propertyAddress, "council_tax", "council_tax_band", "msg")

  private val messagesMap: Map[String, Map[String, String]] =
    Map("en" -> Map("enquiryCategory.council_tax" -> "CT", "councilTaxSubcategory.council_tax_band" -> "TB"))

  private val msgApi = DefaultMessagesApi(messages = messagesMap)

  implicit val request: DataRequest[?] = DataRequest(FakeRequest(), "sessionId", UserAnswers(CacheMap("id", Map())))
  implicit val dateUtil: DateUtil      = inject[DateUtil]

  "EmailConnector" should {
    "send enquiry confirmation" in {
      val body                        = Json.parse("{}")
      val emailConnector              = EmailConnector(servicesConfig, httpClientMock(method = POST, responseBody = body, responseStatus = ACCEPTED))
      implicit val messages: Messages = msgApi.preferred(Seq(Lang("en")))

      val response = emailConnector.sendEnquiryConfirmation(contact).futureValue
      response.status shouldBe ACCEPTED
      response.json   shouldBe body
    }

    "handle error response on send enquiry confirmation" in {
      val body                        = Json.parse("""{"error":"Parameter missed"}""")
      val emailConnector              = EmailConnector(servicesConfig, httpClientMock(method = POST, responseBody = body, responseStatus = BAD_REQUEST))
      implicit val messages: Messages = msgApi.preferred(Seq(Lang("en")))

      val response = emailConnector.sendEnquiryConfirmation(contact).futureValue
      response.status shouldBe BAD_REQUEST
      response.json   shouldBe body
    }
  }

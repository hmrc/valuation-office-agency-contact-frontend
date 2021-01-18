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

package uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers

import org.mockito.Matchers._
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.i18n.MessagesApi
import play.api.libs.json.{JsString, Json}
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.valuationofficeagencycontactfrontend.FakeNavigator
import uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors.LightweightContactEventsConnector
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions.{DataRequiredActionImpl, DataRetrievalAction, FakeDataRetrievalAction}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.SatisfactionSurveyForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.{DateFormatter, MessageControllerComponentsHelpers, UserAnswers}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.error.{internalServerError => internal_Server_Error}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{confirmation => Confirmation}

import scala.concurrent.Future
import scala.util.{Failure, Success}

class ConfirmationControllerSpec extends ControllerSpecBase with MockitoSugar {

  val mockUserAnswers = mock[UserAnswers]
  val mockConnector = mock[LightweightContactEventsConnector]
  when (mockConnector.send(any[Contact], any[MessagesApi])(any[HeaderCarrier])) thenReturn Future.successful(Success(200))

  def onwardRoute = routes.EnquiryCategoryController.onPageLoad(NormalMode)

  def confirmation = app.injector.instanceOf[Confirmation]
  def internalServerError = app.injector.instanceOf[internal_Server_Error]

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new ConfirmationController(frontendAppConfig, messagesApi, mockConnector, new FakeNavigator(desiredRoute = onwardRoute), dataRetrievalAction,
      new DataRequiredActionImpl(ec), confirmation, MessageControllerComponentsHelpers.stubMessageControllerComponents)

  val mockConnectorF = mock[LightweightContactEventsConnector]
  when (mockConnectorF.send(any[Contact], any[MessagesApi])(any[HeaderCarrier])) thenReturn
    Future.successful(Failure(new RuntimeException("Received exception from upstream service")))

  def controllerF(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new ConfirmationController(frontendAppConfig, messagesApi, mockConnectorF, new FakeNavigator(desiredRoute = onwardRoute), dataRetrievalAction,
      new DataRequiredActionImpl(ec), confirmation, MessageControllerComponentsHelpers.stubMessageControllerComponents)

  "Confirmation Controller" must {

    "return 200 and the correct view for a GET" in {

      val contactDetails = ContactDetails("a", "b", "c", "e")
      val ec = "council_tax"
      val propertyAddress = PropertyAddress("a", Some("b"), "c", Some("d"), "f")
      val councilTaxSubcategory = "council_tax_poor_repair"
      val tellUs = TellUsMore("Hello")
      val date = DateFormatter.todaysDate()

      val contact = Contact(contactDetails, propertyAddress, ec, councilTaxSubcategory, tellUs.message)

      val validData = Map(EnquiryCategoryId.toString -> JsString(ec), CouncilTaxSubcategoryId.toString -> JsString(councilTaxSubcategory),
        ContactDetailsId.toString -> Json.toJson(contactDetails), PropertyAddressId.toString -> Json.toJson(propertyAddress), TellUsMoreId.toString -> Json.toJson(tellUs))

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad()(fakeRequest)

      status(result) mustBe OK

      contentAsString(result) mustBe confirmation(frontendAppConfig, contact, date, "councilTaxSubcategory", SatisfactionSurveyForm.apply())(fakeRequest, messages).toString
    }

    "return 200 and the correct view for a GET when addressLine2 and county are None" in {
      val contactDetails = ContactDetails("a", "b", "c", "e")
      val ec = "council_tax"
      val propertyAddress = PropertyAddress("a", None, "c", None, "f")
      val councilTaxSubcategory = "council_tax_poor_repair"
      val tellUs = TellUsMore("Hello")
      val date = DateFormatter.todaysDate()

      val contact = Contact(contactDetails, propertyAddress, ec, councilTaxSubcategory, tellUs.message)

      val validData = Map(EnquiryCategoryId.toString -> JsString(ec), CouncilTaxSubcategoryId.toString -> JsString(councilTaxSubcategory),
        ContactDetailsId.toString -> Json.toJson(contactDetails), PropertyAddressId.toString -> Json.toJson(propertyAddress), TellUsMoreId.toString -> Json.toJson(tellUs))

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad()(fakeRequest)

      status(result) mustBe OK

      contentAsString(result) mustBe confirmation(frontendAppConfig, contact, date, "councilTaxSubcategory", SatisfactionSurveyForm.apply)(fakeRequest, messages).toString
    }

    "redirect to Session Expired for a GET if not existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad()(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
    }

    "The enquiry key function produces a string with a businessRatesSubcategory string key when the enquiry category is business_rates" +
      " and the business_rates_other has been selected" in {
      when(mockUserAnswers.enquiryCategory) thenReturn Some("business_rates")
      when(mockUserAnswers.contactDetails) thenReturn Some(ContactDetails("a", "b", "c", "e"))
      when(mockUserAnswers.propertyAddress) thenReturn Some(PropertyAddress("a", Some("a"), "a", Some("a"), "a"))
      when(mockUserAnswers.businessRatesSubcategory) thenReturn Some("business_rates_other")

      val result = controller().enquiryKey(mockUserAnswers)
      val isBusinessRatesSelection = result.right.get.startsWith("businessRatesSubcategory")
      isBusinessRatesSelection mustBe true
    }

    "The enquiry key function produces a string with a councilTaxSubcategory key when the enquiry category is council_tax" +
      " and the council_tax_band has been selected" in {
      when(mockUserAnswers.enquiryCategory) thenReturn Some("council_tax")
      when(mockUserAnswers.contactDetails) thenReturn Some(ContactDetails("a", "b", "c", "e"))
      when(mockUserAnswers.propertyAddress) thenReturn Some(PropertyAddress("a", Some("a"), "a", Some("a"), "a"))
      when(mockUserAnswers.councilTaxSubcategory) thenReturn Some("council_tax_poor_repair")

      val result = controller().enquiryKey(mockUserAnswers)
      val isCouncilTaxSelection = result.right.get.startsWith("councilTaxSubcategory")
      isCouncilTaxSelection mustBe true
    }

    "The enquiry key function produces a Left(Unknown enquiry category in enquiry key) when the enquiry category has not been selected" in {
      when(mockUserAnswers.enquiryCategory) thenReturn None
      when(mockUserAnswers.contactDetails) thenReturn Some(ContactDetails("a", "b", "c", "e"))
      when(mockUserAnswers.propertyAddress) thenReturn Some(PropertyAddress("a", Some("a"), "a", Some("a"), "a"))
      when(mockUserAnswers.businessRatesSubcategory) thenReturn Some("business_rates_other")

      val result = controller().enquiryKey(mockUserAnswers)
      result mustBe Left("Unknown enquiry category in enquiry key")
    }

    "populate the satisfaction survey view correctly on a GET" in {

    }

    "return 500 and the error view for a GET with unknown or wrong enquiry type" in {
      val contactDetails = ContactDetails("a", "b", "c", "e")
      val ec = "other"
      val propertyAddress = PropertyAddress("a", Some("b"), "c", Some("d"), "f")
      val councilTaxSubcategory = "council_tax_poor_repair"
      val tellUs = TellUsMore("Hello")

      val validData = Map(EnquiryCategoryId.toString -> JsString(ec), CouncilTaxSubcategoryId.toString -> JsString(councilTaxSubcategory),
        ContactDetailsId.toString -> Json.toJson(contactDetails), PropertyAddressId.toString -> Json.toJson(propertyAddress), TellUsMoreId.toString -> Json.toJson(tellUs))

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      intercept[Exception] {
        val result = controller(getRelevantData).onPageLoad()(fakeRequest)

        status(result) mustBe INTERNAL_SERVER_ERROR

        contentAsString(result) mustBe internalServerError(frontendAppConfig)(fakeRequest, messages).toString
      }
    }

    "return 500 and the error view for a GET with no enquiry type" in {
      intercept[Exception] {
        val result = controller().onPageLoad()(fakeRequest)
        status(result) mustBe INTERNAL_SERVER_ERROR
        contentAsString(result) mustBe internalServerError(frontendAppConfig)(fakeRequest, messages).toString
      }
    }

    "return 500 and error view for a GET when the backend service call fails" in {
      intercept[Exception] {
        val result = controllerF().onPageLoad()(fakeRequest)
        status(result) mustBe INTERNAL_SERVER_ERROR
        contentAsString(result) mustBe internalServerError(frontendAppConfig)(fakeRequest, messages).toString
      }
    }

    "return 303 and send email when form complete" in {
      val contactDetails = ContactDetails("a", "b", "c", "e")
      val ec = "council_tax"
      val propertyAddress = PropertyAddress("a", Some("b"), "c", Some("d"), "f")
      val councilTaxSubcategory = "council_tax_poor_repair"
      val tellUs = TellUsMore("Hello")

      val validData = Map(EnquiryCategoryId.toString -> JsString(ec), CouncilTaxSubcategoryId.toString -> JsString(councilTaxSubcategory),
        ContactDetailsId.toString -> Json.toJson(contactDetails), PropertyAddressId.toString -> Json.toJson(propertyAddress), TellUsMoreId.toString -> Json.toJson(tellUs))

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoadSendEmail()(fakeRequest)

      status(result) mustBe SEE_OTHER
    }
  }
}

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

package uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers

import org.mockito.Mockito.when
import org.scalatest.mockito.MockitoSugar
import play.api.data.Form
import play.api.libs.json.{JsString, Json}
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.valuationofficeagencycontactfrontend.FakeNavigator
import uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors.FakeDataCacheConnector
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions._
import play.api.test.Helpers._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.ContactDetailsForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers.{BusinessRatesSubcategoryId, ContactDetailsId, CouncilTaxSubcategoryId, EnquiryCategoryId}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.{ContactDetails, NormalMode}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.UserAnswers
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.contactDetails

class ContactDetailsControllerSpec extends ControllerSpecBase with MockitoSugar {

  def onwardRoute = routes.IndexController.onPageLoad()

  val mockUserAnswers = mock[UserAnswers]

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new ContactDetailsController(frontendAppConfig, messagesApi, FakeDataCacheConnector, new FakeNavigator(desiredRoute = onwardRoute),
      dataRetrievalAction, new DataRequiredActionImpl)

  val ctBackLink = uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.CouncilTaxSubcategoryController.onPageLoad(NormalMode).url
  val ndrBackLink = uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.BusinessRatesSubcategoryController.onPageLoad(NormalMode).url

  def viewAsStringCT(form: Form[ContactDetails] = ContactDetailsForm()) = contactDetails(frontendAppConfig, form, NormalMode, ctBackLink)(fakeRequest, messages).toString

  def viewAsStringNDR(form: Form[ContactDetails] = ContactDetailsForm()) = contactDetails(frontendAppConfig, form, NormalMode, ndrBackLink)(fakeRequest, messages).toString

  "ContactDetails Controller" must {

    "return OK and the correct view for a GET" in {
      val validData = Map(EnquiryCategoryId.toString -> JsString("business_rates"), BusinessRatesSubcategoryId.toString -> JsString("business_rates_other"))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))
      val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsStringNDR()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val validData = Map(EnquiryCategoryId.toString -> JsString("business_rates"), CouncilTaxSubcategoryId.toString -> JsString("business_rates_other"),
        ContactDetailsId.toString -> Json.toJson(ContactDetails("a", "b", "a@test.com", "a@test.com", "0847428742424")))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeRequest)

      contentAsString(result) mustBe viewAsStringNDR(ContactDetailsForm().fill(ContactDetails("a", "b", "a@test.com", "a@test.com", "0847428742424")))
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("firstName", "a"), ("lastName", "b"), ("email", "a@test.com"),
        ("confirmEmail", "a@test.com"), ("contactNumber", "0487357346776"))

      val result = controller().onSubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = ContactDetailsForm().bind(Map("value" -> "invalid value"))
      val validData = Map(EnquiryCategoryId.toString -> JsString("business_rates"), BusinessRatesSubcategoryId.toString -> JsString("business_rates_other"))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onSubmit(NormalMode)(postRequest)
      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsStringNDR(boundForm)
    }

    "return an error when invalid data is submitted and enquiry category is wrong or unwnown" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = ContactDetailsForm().bind(Map("value" -> "invalid value"))
      val validData = Map(EnquiryCategoryId.toString -> JsString("other"))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      intercept[Exception] {
        val result = controller(getRelevantData).onSubmit(NormalMode)(postRequest)
        status(result) mustBe INTERNAL_SERVER_ERROR
      }
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("firstName", "a"), ("lastName", "b"), ("email", "a@test.com"),
        ("confirmEmail", "a@test.com"), ("contactNumber", "0493584384343"))

      val result = controller(dontGetAnyData).onSubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
    }

    "The enquiry key function produces a string with a Business subcategory back link when the enquiry category is business_rates" in {
      when(mockUserAnswers.enquiryCategory) thenReturn Some("business_rates")
      when(mockUserAnswers.businessRatesSubcategory) thenReturn Some("business_rates_other")
      val result = controller().enquiryBackLink(mockUserAnswers)
      val isBusinessRatesSelection = result.isRight
      isBusinessRatesSelection mustBe true
      assert(result.right.get == uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.BusinessRatesSubcategoryController.onPageLoad(NormalMode).url)
    }

    "The enquiry key function produces a string with a Council Tax subcategory back link when the enquiry category is council_tax" in {
      when(mockUserAnswers.enquiryCategory) thenReturn Some("council_tax")
      when(mockUserAnswers.councilTaxSubcategory) thenReturn Some("council_tax_band")
      val result = controller().enquiryBackLink(mockUserAnswers)
      val isCouncilTaxSelection = result.isRight
      isCouncilTaxSelection mustBe true
      assert(result.right.get == uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.CouncilTaxSubcategoryController.onPageLoad(NormalMode).url)
    }

    "The enquiry key function produces a Left(Unknown enquiry category in enquiry key) when the enquiry category has not been selected" in {
      when(mockUserAnswers.enquiryCategory) thenReturn None
      when(mockUserAnswers.businessRatesSubcategory) thenReturn Some("business_rates_other")
      val result = controller().enquiryBackLink(mockUserAnswers)
      result mustBe Left("Unknown enquiry category in enquiry key")
    }

    "return OK and the correct view for a GET when enquiry category is council_tax" in {
      val validData = Map(EnquiryCategoryId.toString -> JsString("council_tax"), CouncilTaxSubcategoryId.toString -> JsString("council_tax_calculated"))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))
      val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsStringCT()
    }

    "populate the view correctly on a GET when the question has previously been answered and enquiry category is council_tax" in {
      val validData = Map(EnquiryCategoryId.toString -> JsString("council_tax"), CouncilTaxSubcategoryId.toString -> JsString("council_tax_home_business"),
        ContactDetailsId.toString -> Json.toJson(ContactDetails("a", "b", "a@test.com", "a@test.com", "0847428742424")))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeRequest)

      contentAsString(result) mustBe viewAsStringCT(ContactDetailsForm().fill(ContactDetails("a", "b", "a@test.com", "a@test.com", "0847428742424")))
    }
  }
}

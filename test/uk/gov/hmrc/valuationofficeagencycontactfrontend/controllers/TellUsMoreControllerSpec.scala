/*
 * Copyright 2019 HM Revenue & Customs
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
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.TellUsMoreForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers.{CouncilTaxSubcategoryId, EnquiryCategoryId, TellUsMoreId}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.{CheckYourAnswersHelper, UserAnswers}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.viewmodels.AnswerSection
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{internalServerError, tellUsMore}

class TellUsMoreControllerSpec extends ControllerSpecBase with MockitoSugar {

  val mockUserAnswers = mock[UserAnswers]

  def onwardRoute = routes.EnquiryCategoryController.onPageLoad(NormalMode)

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new TellUsMoreController(frontendAppConfig, messagesApi, FakeDataCacheConnector, new FakeNavigator(desiredRoute = onwardRoute),
      dataRetrievalAction, new DataRequiredActionImpl)

  def viewAsString(form: Form[TellUsMore] = TellUsMoreForm(), msg: String = "") = tellUsMore(frontendAppConfig, form, NormalMode, msg)(fakeRequest, messages).toString

  "TellUsMore Controller" must {

    "return OK and the correct view for a GET" in {
      val validData = Map(EnquiryCategoryId.toString -> JsString("council_tax"), CouncilTaxSubcategoryId.toString -> JsString("council_tax_calculated"))

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString(TellUsMoreForm(), "tellUsMore.ct-reference")
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val validData = Map(
        EnquiryCategoryId.toString -> JsString("council_tax"),
        CouncilTaxSubcategoryId.toString -> JsString("council_tax_calculated"),
        TellUsMoreId.toString -> Json.toJson(TellUsMore("value 1")))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeRequest)

      contentAsString(result) mustBe viewAsString(TellUsMoreForm().fill(TellUsMore("value 1")), "tellUsMore.ct-reference")
    }

    "The enquiry key function produces a string with a tell us more ndr-reference key when the enquiry category is business_rates" +
      " and the business_rates_other has been selected" in {
      when(mockUserAnswers.enquiryCategory) thenReturn Some("business_rates")
      when(mockUserAnswers.contactDetails) thenReturn Some(ContactDetails("a", "b", "c", "d", "e"))
      when(mockUserAnswers.propertyAddress) thenReturn Some(PropertyAddress("a", Some("a"), "a", Some("a"), "a"))
      when(mockUserAnswers.businessRatesSubcategory) thenReturn Some("business_rates_other")

      val result = controller().enquiryKey(mockUserAnswers)
      val isBusinessRatesSelection = result.right.get.endsWith("tellUsMore.ndr-reference")
      isBusinessRatesSelection mustBe true
    }

    "The enquiry key function produces a string with a tell us more ct-reference key when the enquiry category is council_tax" +
      " and the council_tax_band has been selected" in {
      when(mockUserAnswers.enquiryCategory) thenReturn Some("council_tax")
      when(mockUserAnswers.contactDetails) thenReturn Some(ContactDetails("a", "b", "c", "d", "e"))
      when(mockUserAnswers.propertyAddress) thenReturn Some(PropertyAddress("a", None, "a", None, "a"))
      when(mockUserAnswers.councilTaxSubcategory) thenReturn Some("council_tax_band")

      val result = controller().enquiryKey(mockUserAnswers)
      val isCouncilTaxSelection = result.right.get.endsWith("tellUsMore.ct-reference")
      isCouncilTaxSelection mustBe true
    }

    "The enquiry key function produces a Left(Unknown enquiry category in enquiry key) when the enquiry category has not been selected" in {
      when(mockUserAnswers.enquiryCategory) thenReturn None
      when(mockUserAnswers.contactDetails) thenReturn Some(ContactDetails("a", "b", "c", "d", "e"))
      when(mockUserAnswers.propertyAddress) thenReturn Some(PropertyAddress("a", Some("a"), "a", Some("a"), "a"))
      when(mockUserAnswers.businessRatesSubcategory) thenReturn Some("business_rates_other")

      val result = controller().enquiryKey(mockUserAnswers)
      result mustBe Left("Unknown enquiry category in enquiry key")
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("message", "value 1"))

      val result = controller().onSubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = TellUsMoreForm().bind(Map("value" -> "invalid value"))

      val result = controller().onSubmit(NormalMode)(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("message", "value 1"))
      val result = controller(dontGetAnyData).onSubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
    }

    "return 500 and the error view for a GET with wrong or unknown enquiry type" in {
      val validData = Map(EnquiryCategoryId.toString -> JsString("other"), CouncilTaxSubcategoryId.toString -> JsString("council_tax_calculated"))

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))
      intercept[Exception] {
        val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeRequest)
        status(result) mustBe INTERNAL_SERVER_ERROR
        contentAsString(result) mustBe internalServerError(frontendAppConfig)(fakeRequest, messages).toString
      }
    }

      "return 500 and the error view for a GET with no enquiry type" in {
        intercept[Exception] {
          val result = controller().onPageLoad(NormalMode)(fakeRequest)
          status(result) mustBe INTERNAL_SERVER_ERROR
          contentAsString(result) mustBe internalServerError(frontendAppConfig)(fakeRequest, messages).toString
        }
      }

  }
}

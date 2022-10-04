/*
 * Copyright 2022 HM Revenue & Customs
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
import org.scalatestplus.mockito.MockitoSugar
import play.api.data.Form
import play.api.libs.json.JsString
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.valuationofficeagencycontactfrontend.FakeNavigator
import uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors.FakeDataCacheConnector
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions._
import play.api.test.Helpers._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.TellUsMoreForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers.{CouncilTaxSubcategoryId, EnquiryCategoryId}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.{MessageControllerComponentsHelpers, UserAnswers}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.error.{internalServerError => internal_Server_Error}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{tellUsMore => tell_us_more}

class TellUsMoreControllerSpec extends ControllerSpecBase with MockitoSugar {

  val mockUserAnswers = mock[UserAnswers]

  val backLink = uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.TellUsMoreController.onPageLoad(NormalMode).url

  def tellUsMore = app.injector.instanceOf[tell_us_more]
  def internalServerError = app.injector.instanceOf[internal_Server_Error]

  def onwardRoute = routes.EnquiryCategoryController.onPageLoad(NormalMode)

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new TellUsMoreController(frontendAppConfig, messagesApi, FakeDataCacheConnector, new FakeNavigator(desiredRoute = onwardRoute),
      dataRetrievalAction, new DataRequiredActionImpl(ec), tellUsMore, MessageControllerComponentsHelpers.stubMessageControllerComponents)

  def viewAsString(form: Form[TellUsMore] = TellUsMoreForm(), msg: String = "") = tellUsMore(frontendAppConfig, form, NormalMode, msg, backLink)(fakeRequest, messages).toString

  "TellUsMore Controller" must {

    "return OK and the correct view for a GET" in {
      val validData = Map(EnquiryCategoryId.toString -> JsString("council_tax"), CouncilTaxSubcategoryId.toString -> JsString("council_tax_calculated"))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))
      val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe OK
    }

    "The enquiry key function produces a string with a tell us more tellUsMore.business.other key when the enquiry category is business_rates" +
      " and the business_rates_other has been selected" in {
      when(mockUserAnswers.enquiryCategory) thenReturn Some("business_rates")
      when(mockUserAnswers.contactDetails) thenReturn Some(ContactDetails("a", "c", "e"))
      when(mockUserAnswers.propertyAddress) thenReturn Some(PropertyAddress("a", Some("a"), "a", Some("a"), "a"))
      when(mockUserAnswers.businessRatesSubcategory) thenReturn Some("business_rates_other")

      val result = controller().enquiryKey(mockUserAnswers)
      val isBusinessRatesSelection = result.toOption.get.endsWith("tellUsMore.business.other")
      isBusinessRatesSelection mustBe true
    }

    "The enquiry key function produces a string with a tell us more tellUsMore.business.other key when the enquiry category is business_rates" +
      " and the business_rates_from_home has been selected" in {
      when(mockUserAnswers.enquiryCategory) thenReturn Some("business_rates")
      when(mockUserAnswers.contactDetails) thenReturn Some(ContactDetails("a", "c", "e"))
      when(mockUserAnswers.propertyAddress) thenReturn Some(PropertyAddress("a", Some("a"), "a", Some("a"), "a"))
      when(mockUserAnswers.businessRatesSubcategory) thenReturn Some("business_rates_from_home")

      val result = controller().enquiryKey(mockUserAnswers)
      val isBusinessRatesSelection = result.toOption.get.endsWith("tellUsMore.business")
      isBusinessRatesSelection mustBe true
    }

    "The enquiry key function produces a string with a tell us more tellUsMore.business.other key when the enquiry category is business_rates" +
      " and the business_rates_change_valuation has been selected" in {
      when(mockUserAnswers.enquiryCategory) thenReturn Some("business_rates")
      when(mockUserAnswers.contactDetails) thenReturn Some(ContactDetails("a", "c", "e"))
      when(mockUserAnswers.propertyAddress) thenReturn Some(PropertyAddress("a", Some("a"), "a", Some("a"), "a"))
      when(mockUserAnswers.businessRatesSubcategory) thenReturn Some("business_rates_change_valuation")

      val result = controller().enquiryKey(mockUserAnswers)
      val isBusinessRatesSelection = result.toOption.get.endsWith("tellUsMore.business.other")
      isBusinessRatesSelection mustBe true
    }

    "The enquiry key function produces a string with a tell us more tellUsMore.business.other key when the enquiry category is business_rates" +
      " and the business_rates_bill has been selected" in {
      when(mockUserAnswers.enquiryCategory) thenReturn Some("business_rates")
      when(mockUserAnswers.contactDetails) thenReturn Some(ContactDetails("a", "c", "e"))
      when(mockUserAnswers.propertyAddress) thenReturn Some(PropertyAddress("a", Some("a"), "a", Some("a"), "a"))
      when(mockUserAnswers.businessRatesSubcategory) thenReturn Some("business_rates_bill")

      val result = controller().enquiryKey(mockUserAnswers)
      val isBusinessRatesSelection = result.toOption.get.endsWith("tellUsMore.business.other")
      isBusinessRatesSelection mustBe true
    }

    "The enquiry key function produces a string with a tell us more tellUsMore.business.other key when the enquiry category is business_rates" +
      " and the business_rates_changes has been selected" in {
      when(mockUserAnswers.enquiryCategory) thenReturn Some("business_rates")
      when(mockUserAnswers.contactDetails) thenReturn Some(ContactDetails("a", "c", "e"))
      when(mockUserAnswers.propertyAddress) thenReturn Some(PropertyAddress("a", Some("a"), "a", Some("a"), "a"))
      when(mockUserAnswers.businessRatesSubcategory) thenReturn Some("business_rates_changes")

      val result = controller().enquiryKey(mockUserAnswers)
      val isBusinessRatesSelection = result.toOption.get.endsWith("tellUsMore.business.other")
      isBusinessRatesSelection mustBe true
    }

    "The enquiry key function produces a string with a tell us more tellUsMore.business.other key when the enquiry category is business_rates" +
      " and the business_rates_property_empty has been selected" in {
      when(mockUserAnswers.enquiryCategory) thenReturn Some("business_rates")
      when(mockUserAnswers.contactDetails) thenReturn Some(ContactDetails("a", "c", "e"))
      when(mockUserAnswers.propertyAddress) thenReturn Some(PropertyAddress("a", Some("a"), "a", Some("a"), "a"))
      when(mockUserAnswers.businessRatesSubcategory) thenReturn Some("business_rates_property_empty")

      val result = controller().enquiryKey(mockUserAnswers)
      val isBusinessRatesSelection = result.toOption.get.endsWith("tellUsMore.business.other")
      isBusinessRatesSelection mustBe true
    }

    "The enquiry key function produces a string with a tell us more tellUsMore.business.other key when the enquiry category is business_rates" +
      " and the business_rates_valuation has been selected" in {
      when(mockUserAnswers.enquiryCategory) thenReturn Some("business_rates")
      when(mockUserAnswers.contactDetails) thenReturn Some(ContactDetails("a", "c", "e"))
      when(mockUserAnswers.propertyAddress) thenReturn Some(PropertyAddress("a", Some("a"), "a", Some("a"), "a"))
      when(mockUserAnswers.businessRatesSubcategory) thenReturn Some("business_rates_valuation")

      val result = controller().enquiryKey(mockUserAnswers)
      val isBusinessRatesSelection = result.toOption.get.endsWith("tellUsMore.business.other")
      isBusinessRatesSelection mustBe true
    }

    "The enquiry key function produces a string with a tell us more tellUsMore.business.other key when the enquiry category is business_rates" +
      " and the business_rates_demolished has been selected" in {
      when(mockUserAnswers.enquiryCategory) thenReturn Some("business_rates")
      when(mockUserAnswers.contactDetails) thenReturn Some(ContactDetails("a", "c", "e"))
      when(mockUserAnswers.propertyAddress) thenReturn Some(PropertyAddress("a", Some("a"), "a", Some("a"), "a"))
      when(mockUserAnswers.businessRatesSubcategory) thenReturn Some("business_rates_demolished")

      val result = controller().enquiryKey(mockUserAnswers)
      val isBusinessRatesSelection = result.toOption.get.endsWith("tellUsMore.business.other")
      isBusinessRatesSelection mustBe true
    }

    "The enquiry key function produces a string with a tell us more tellUsMore.business.other key when the enquiry category is business_rates" +
      " and the business_rates_not_used has been selected" in {
      when(mockUserAnswers.enquiryCategory) thenReturn Some("business_rates")
      when(mockUserAnswers.contactDetails) thenReturn Some(ContactDetails("a", "c", "e"))
      when(mockUserAnswers.propertyAddress) thenReturn Some(PropertyAddress("a", Some("a"), "a", Some("a"), "a"))
      when(mockUserAnswers.businessRatesSubcategory) thenReturn Some("business_rates_not_used")

      val result = controller().enquiryKey(mockUserAnswers)
      val isBusinessRatesSelection = result.toOption.get.endsWith("tellUsMore.business.other")
      isBusinessRatesSelection mustBe true
    }

    "The enquiry key function produces a string with a tell us more tellUsMore.business.other key when the enquiry category is business_rates" +
      " and the business_rates_self_catering has been selected" in {
      when(mockUserAnswers.enquiryCategory) thenReturn Some("business_rates")
      when(mockUserAnswers.contactDetails) thenReturn Some(ContactDetails("a", "c", "e"))
      when(mockUserAnswers.propertyAddress) thenReturn Some(PropertyAddress("a", Some("a"), "a", Some("a"), "a"))
      when(mockUserAnswers.businessRatesSubcategory) thenReturn Some("business_rates_self_catering")

      val result = controller().enquiryKey(mockUserAnswers)
      val isBusinessRatesSelection = result.toOption.get.endsWith("tellUsMore.business.other")
      isBusinessRatesSelection mustBe true
    }

    "The enquiry key function produces a string with a tell us more ct-reference key when the enquiry category is council_tax" +
      " and the council_tax_band has been selected" in {
      when(mockUserAnswers.enquiryCategory) thenReturn Some("council_tax")
      when(mockUserAnswers.contactDetails) thenReturn Some(ContactDetails("a", "c", "e"))
      when(mockUserAnswers.propertyAddress) thenReturn Some(PropertyAddress("a", None, "a", None, "a"))
      when(mockUserAnswers.councilTaxSubcategory) thenReturn Some("business_rates_change_valuation")

      val result = controller().enquiryKey(mockUserAnswers)
      val isCouncilTaxSelection = result.toOption.get.endsWith("tellUsMore.ct-reference")
      isCouncilTaxSelection mustBe true
    }

    "The enquiry key function produces a string with a tell us more ct-reference key when the enquiry category is council_tax" +
      " and the council_tax_band has been selected1" in {
      when(mockUserAnswers.enquiryCategory) thenReturn Some("business_rates")
      when(mockUserAnswers.contactDetails) thenReturn Some(ContactDetails("a", "c", "e"))
      when(mockUserAnswers.propertyAddress) thenReturn Some(PropertyAddress("a", None, "a", None, "a"))
      when(mockUserAnswers.businessRatesSubcategory) thenReturn Some("council_tax_band")

      val result = controller().enquiryKey(mockUserAnswers)
      val isCouncilTaxSelection = result.toOption.get.endsWith("tellUsMore.ndr-reference")
      isCouncilTaxSelection mustBe true
    }

    "The enquiry key function produces a string with a tell us more poorRepair key when the enquiry category is council_tax" +
      " and the council_tax_property_poor_repair has been selected" in {
      when(mockUserAnswers.enquiryCategory) thenReturn Some("council_tax")
      when(mockUserAnswers.contactDetails) thenReturn Some(ContactDetails("a", "c", "e"))
      when(mockUserAnswers.propertyAddress) thenReturn Some(PropertyAddress("a", None, "a", None, "a"))
      when(mockUserAnswers.councilTaxSubcategory) thenReturn Some("council_tax_property_poor_repair")

      val result = controller().enquiryKey(mockUserAnswers)
      val isCouncilTaxSelection = result.toOption.get.endsWith("tellUsMore.general")
      isCouncilTaxSelection mustBe true
    }

    "The enquiry key function produces a string with a tell us more other key when the enquiry category is council_tax" +
      " and the council_tax_other has been selected" in {
      when(mockUserAnswers.enquiryCategory) thenReturn Some("council_tax")
      when(mockUserAnswers.contactDetails) thenReturn Some(ContactDetails("a", "c", "e"))
      when(mockUserAnswers.propertyAddress) thenReturn Some(PropertyAddress("a", None, "a", None, "a"))
      when(mockUserAnswers.councilTaxSubcategory) thenReturn Some("council_tax_other")

      val result = controller().enquiryKey(mockUserAnswers)
      val isCouncilTaxSelection = result.toOption.get.endsWith("tellUsMore.other")
      isCouncilTaxSelection mustBe true
    }

    "The enquiry key function produces a string with a tell us more areaChange key when the enquiry category is council_tax" +
      " and the council_tax_area_change has been selected" in {
      when(mockUserAnswers.enquiryCategory) thenReturn Some("council_tax")
      when(mockUserAnswers.contactDetails) thenReturn Some(ContactDetails("a", "c", "e"))
      when(mockUserAnswers.propertyAddress) thenReturn Some(PropertyAddress("a", None, "a", None, "a"))
      when(mockUserAnswers.councilTaxSubcategory) thenReturn Some("council_tax_area_change")

      val result = controller().enquiryKey(mockUserAnswers)
      val isCouncilTaxSelection = result.toOption.get.endsWith("tellUsMore.general")
      isCouncilTaxSelection mustBe true
    }

    "The enquiry key function produces a string with a tell us more areaChange key when the enquiry category is council_tax" +
      " and the council_tax_annexe has been selected" in {
      when(mockUserAnswers.enquiryCategory) thenReturn Some("council_tax")
      when(mockUserAnswers.contactDetails) thenReturn Some(ContactDetails("a", "c", "e"))
      when(mockUserAnswers.propertyAddress) thenReturn Some(PropertyAddress("a", None, "a", None, "a"))
      when(mockUserAnswers.councilTaxSubcategory) thenReturn Some("council_tax_annexe")

      val result = controller().enquiryKey(mockUserAnswers)
      val isCouncilTaxSelection = result.toOption.get.endsWith("tellUsMore.general")
      isCouncilTaxSelection mustBe true
    }

    "The enquiry key function produces a string with a tell us more areaChange key when the enquiry category is council_tax" +
      " and the council_tax_bill has been selected" in {
      when(mockUserAnswers.enquiryCategory) thenReturn Some("council_tax")
      when(mockUserAnswers.contactDetails) thenReturn Some(ContactDetails("a", "c", "e"))
      when(mockUserAnswers.propertyAddress) thenReturn Some(PropertyAddress("a", None, "a", None, "a"))
      when(mockUserAnswers.councilTaxSubcategory) thenReturn Some("council_tax_bill")

      val result = controller().enquiryKey(mockUserAnswers)
      val isCouncilTaxSelection = result.toOption.get.endsWith("tellUsMore.general")
      isCouncilTaxSelection mustBe true
    }


    "The enquiry key function produces a string with a tell us more areaChange key when the enquiry category is council_tax" +
      " and the council_tax_band_too_high has been selected" in {
      when(mockUserAnswers.enquiryCategory) thenReturn Some("council_tax")
      when(mockUserAnswers.contactDetails) thenReturn Some(ContactDetails("a", "c", "e"))
      when(mockUserAnswers.propertyAddress) thenReturn Some(PropertyAddress("a", None, "a", None, "a"))
      when(mockUserAnswers.councilTaxSubcategory) thenReturn Some("council_tax_band_too_high")

      val result = controller().enquiryKey(mockUserAnswers)
      val isCouncilTaxSelection = result.toOption.get.endsWith("tellUsMore.general")
      isCouncilTaxSelection mustBe true
    }

    "The enquiry key function produces a string with a tell us more areaChange key when the enquiry category is council_tax" +
      " and the council_tax_band_for_new has been selected" in {
      when(mockUserAnswers.enquiryCategory) thenReturn Some("council_tax")
      when(mockUserAnswers.contactDetails) thenReturn Some(ContactDetails("a", "c", "e"))
      when(mockUserAnswers.propertyAddress) thenReturn Some(PropertyAddress("a", None, "a", None, "a"))
      when(mockUserAnswers.councilTaxSubcategory) thenReturn Some("council_tax_band_for_new")

      val result = controller().enquiryKey(mockUserAnswers)
      val isCouncilTaxSelection = result.toOption.get.endsWith("tellUsMore.general")
      isCouncilTaxSelection mustBe true
    }

    "The enquiry key function produces a string with a tell us more areaChange key when the enquiry category is council_tax" +
      " and the council_tax_property_empty has been selected" in {
      when(mockUserAnswers.enquiryCategory) thenReturn Some("council_tax")
      when(mockUserAnswers.contactDetails) thenReturn Some(ContactDetails("a", "c", "e"))
      when(mockUserAnswers.propertyAddress) thenReturn Some(PropertyAddress("a", None, "a", None, "a"))
      when(mockUserAnswers.councilTaxSubcategory) thenReturn Some("council_tax_property_empty")

      val result = controller().enquiryKey(mockUserAnswers)
      val isCouncilTaxSelection = result.toOption.get.endsWith("tellUsMore.general")
      isCouncilTaxSelection mustBe true
    }

    "The enquiry key function produces a string with a tell us more areaChange key when the enquiry category is council_tax" +
      " and the council_tax_property_split_merge has been selected" in {
      when(mockUserAnswers.enquiryCategory) thenReturn Some("council_tax")
      when(mockUserAnswers.contactDetails) thenReturn Some(ContactDetails("a", "c", "e"))
      when(mockUserAnswers.propertyAddress) thenReturn Some(PropertyAddress("a", None, "a", None, "a"))
      when(mockUserAnswers.councilTaxSubcategory) thenReturn Some("council_tax_property_split_merge")

      val result = controller().enquiryKey(mockUserAnswers)
      val isCouncilTaxSelection = result.toOption.get.endsWith("tellUsMore.general")
      isCouncilTaxSelection mustBe true
    }

    "The enquiry key function produces a string with a tell us more areaChange key when the enquiry category is council_tax" +
      " and the council_tax_property_demolished has been selected" in {
      when(mockUserAnswers.enquiryCategory) thenReturn Some("council_tax")
      when(mockUserAnswers.contactDetails) thenReturn Some(ContactDetails("a", "c", "e"))
      when(mockUserAnswers.propertyAddress) thenReturn Some(PropertyAddress("a", None, "a", None, "a"))
      when(mockUserAnswers.councilTaxSubcategory) thenReturn Some("council_tax_property_demolished")

      val result = controller().enquiryKey(mockUserAnswers)
      val isCouncilTaxSelection = result.toOption.get.endsWith("tellUsMore.general")
      isCouncilTaxSelection mustBe true
    }

    "The enquiry key function produces a string with a tell us more business key when the enquiry category is council_tax" +
      " and the council_tax_business_uses has been selected" in {
      when(mockUserAnswers.enquiryCategory) thenReturn Some("council_tax")
      when(mockUserAnswers.contactDetails) thenReturn Some(ContactDetails("a", "c", "e"))
      when(mockUserAnswers.propertyAddress) thenReturn Some(PropertyAddress("a", None, "a", None, "a"))
      when(mockUserAnswers.councilTaxSubcategory) thenReturn Some("council_tax_business_uses")

      val result = controller().enquiryKey(mockUserAnswers)
      val isCouncilTaxSelection = result.toOption.get.endsWith("tellUsMore.business")
      isCouncilTaxSelection mustBe true
    }

    "The enquiry key function produces a string with a tell us more fair rent key when the enquiry category is fair rent" +
      " and the submit new application has been selected1" in {
      when(mockUserAnswers.enquiryCategory) thenReturn Some("fair_rent")
      when(mockUserAnswers.contactDetails) thenReturn Some(ContactDetails("a", "c", "e"))
      when(mockUserAnswers.propertyAddress) thenReturn Some(PropertyAddress("a", None, "a", None, "a"))
      when(mockUserAnswers.fairRentEnquiryEnquiry) thenReturn Some("submit_new_application")

      val result = controller().enquiryKey(mockUserAnswers)
      val isFairRentSelection = result.toOption.get.endsWith("tellUsMore.fairRent")
      isFairRentSelection mustBe true
    }

    "The enquiry key function produces a string with a tell us more fair rent key when the enquiry category is fair rent" +
      " and the check fair rent register has been selected" in {
      when(mockUserAnswers.enquiryCategory) thenReturn Some("fair_rent")
      when(mockUserAnswers.contactDetails) thenReturn Some(ContactDetails("a", "c", "e"))
      when(mockUserAnswers.propertyAddress) thenReturn Some(PropertyAddress("a", None, "a", None, "a"))
      when(mockUserAnswers.fairRentEnquiryEnquiry) thenReturn Some("check_fair_rent_register")

      val result = controller().enquiryKey(mockUserAnswers)
      val isFairRentSelection = result.toOption.get.endsWith("tellUsMore.fairRent")
      isFairRentSelection mustBe true
    }

    "The enquiry key function produces a string with a tell us more fair rent key when the enquiry category is fair rent" +
      " and the other request has been selected" in {
      when(mockUserAnswers.enquiryCategory) thenReturn Some("fair_rent")
      when(mockUserAnswers.contactDetails) thenReturn Some(ContactDetails("a", "c", "e"))
      when(mockUserAnswers.propertyAddress) thenReturn Some(PropertyAddress("a", None, "a", None, "a"))
      when(mockUserAnswers.fairRentEnquiryEnquiry) thenReturn Some("other_request")

      val result = controller().enquiryKey(mockUserAnswers)
      val isFairRentSelection = result.toOption.get.endsWith("tellUsMore.fairRent")
      isFairRentSelection mustBe true
    }

    "The enquiry key function produces a Left(Unknown enquiry category in enquiry key) when the enquiry category has not been selected" in {
      when(mockUserAnswers.enquiryCategory) thenReturn None
      when(mockUserAnswers.contactDetails) thenReturn Some(ContactDetails("a", "c", "e"))
      when(mockUserAnswers.propertyAddress) thenReturn Some(PropertyAddress("a", Some("a"), "a", Some("a"), "a"))
      when(mockUserAnswers.businessRatesSubcategory) thenReturn Some("business_rates_other")

      val result = controller().enquiryKey(mockUserAnswers)
      result mustBe Left("Unknown enquiry category in enquiry key")
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withMethod("POST").withFormUrlEncodedBody(("message", "value 1"))

      val result = controller().onSubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val validData = Map(EnquiryCategoryId.toString -> JsString("council_tax"), CouncilTaxSubcategoryId.toString -> JsString("council_tax_calculated"))

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))

      val result = controller(getRelevantData).onSubmit(NormalMode)(postRequest)

      status(result) mustBe BAD_REQUEST
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad.url)
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("message", "value 1"))
      val result = controller(dontGetAnyData).onSubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad.url)
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

    "return Redirect for initAndStart" in {
      val emptyData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, Map.empty)))

      val result = controller(emptyData).initAndStart(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.RefNumberController.onPageLoad().url)
    }

  }
}

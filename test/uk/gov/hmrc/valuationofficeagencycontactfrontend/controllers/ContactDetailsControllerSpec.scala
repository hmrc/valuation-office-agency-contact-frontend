/*
 * Copyright 2023 HM Revenue & Customs
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
import play.api.libs.json.{JsString, Json}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.FakeNavigator
import uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors.FakeDataCacheConnector
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions._
import play.api.test.Helpers._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.ContactDetailsForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers.{BusinessRatesSubcategoryId, ContactDetailsId, ContactReasonId, CouncilTaxSubcategoryId, EnquiryCategoryId, PropertyAddressId}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.journey.model.TellUsMorePage
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.{CacheMap, ContactDetails, NormalMode, PropertyAddress}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.{MessageControllerComponentsHelpers, UserAnswers}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{contactDetails => contact_details}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.error.{internalServerError => internal_Server_Error}

class ContactDetailsControllerSpec extends ControllerSpecBase with MockitoSugar {

  def contactDetails = app.injector.instanceOf[contact_details]
  def internalServerError = app.injector.instanceOf[internal_Server_Error]

  def onwardRoute = routes.EnquiryCategoryController.onPageLoad(NormalMode)

  val mockUserAnswers = mock[UserAnswers]

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new ContactDetailsController(frontendAppConfig, messagesApi, FakeDataCacheConnector, new FakeNavigator(desiredRoute = onwardRoute),
      dataRetrievalAction, new DataRequiredActionImpl(ec), contactDetails , MessageControllerComponentsHelpers.stubMessageControllerComponents)

  def ctBackLink = uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.TellUsMoreController.onPageLoad(NormalMode).url
  def ndrBackLink = uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.BusinessRatesSubcategoryController.onPageLoad(NormalMode).url
  def refNumberBackLink = uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.RefNumberController.onPageLoad().url

  def viewAsStringCT(form: Form[ContactDetails] = ContactDetailsForm()) = contactDetails(frontendAppConfig, form, NormalMode, ctBackLink)(fakeRequest, messages).toString

  def viewAsStringNDR(form: Form[ContactDetails] = ContactDetailsForm()) = contactDetails(frontendAppConfig, form, NormalMode, ndrBackLink)(fakeRequest, messages).toString

  def viewAsStringRefNumber(form: Form[ContactDetails] = ContactDetailsForm()) = contactDetails(frontendAppConfig, form, NormalMode, refNumberBackLink)(fakeRequest, messages).toString

  "ContactDetails Controller" must {

    "return OK and the correct view for a GET when Contact Reason is more_details" in {
      val validData = Map(ContactReasonId.toString -> JsString("more_details"))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))
      val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsStringRefNumber()
    }

    "return OK and the correct view for a GET when enquiry category is business_rates" in {
      val validData = Map(EnquiryCategoryId.toString -> JsString("business_rates"), BusinessRatesSubcategoryId.toString -> JsString("business_rates_other"))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))
      val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsStringNDR()
    }

    "populate the view correctly on a GET when the question has previously been answered and enquiry category is business_rates" in {
      val validData = Map(EnquiryCategoryId.toString -> JsString("business_rates"), CouncilTaxSubcategoryId.toString -> JsString("business_rates_other"),
        ContactDetailsId.toString -> Json.toJson(ContactDetails("a", "a@test.com", "0847428742424")))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeRequest)

      contentAsString(result) mustBe viewAsStringNDR(ContactDetailsForm().fill(ContactDetails("a", "a@test.com", "0847428742424")))
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withMethod("POST").withFormUrlEncodedBody(("fullName", "a"), ("email", "a@test.com"),
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
      val validData = Map(EnquiryCategoryId.toString -> JsString("other"))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      intercept[Exception] {
        val result = controller(getRelevantData).onSubmit(NormalMode)(postRequest)
        status(result) mustBe INTERNAL_SERVER_ERROR
        contentAsString(result) mustBe internalServerError(frontendAppConfig)(fakeRequest, messages).toString
      }
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad.url)
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("fullName", "a"), ("email", "a@test.com"),
        ("confirmEmail", "a@test.com"), ("contactNumber", "0493584384343"))

      val result = controller(dontGetAnyData).onSubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad.url)
    }

    "The enquiry key function produces a string with a Business subcategory back link when the enquiry category is business_rates" in {
      when(mockUserAnswers.enquiryCategory) thenReturn Some("business_rates")
      when(mockUserAnswers.businessRatesSubcategory) thenReturn Some("business_rates_other")
      val result = controller().enquiryBackLink(mockUserAnswers)
      val isBusinessRatesSelection = result.isRight
      isBusinessRatesSelection mustBe true
      assert(result.toOption.get == routes.BusinessRatesSubcategoryController.onPageLoad(NormalMode).url)
    }

    "The enquiry key function produces a string with a Business subcategory back link when the enquiry category is business_rates_bill" in {
      when(mockUserAnswers.enquiryCategory) thenReturn Some("business_rates")
      when(mockUserAnswers.businessRatesSubcategory) thenReturn Some("business_rates_bill")
      val result = controller().enquiryBackLink(mockUserAnswers)
      val isBusinessRatesSelection = result.isRight
      isBusinessRatesSelection mustBe true
      assert(result.toOption.get == routes.TellUsMoreController.onPageLoad(NormalMode).url)
    }

    "The enquiry key function produces a string with a Business subcategory back link when the enquiry category is business_rates_changes" in {
      when(mockUserAnswers.enquiryCategory) thenReturn Some("business_rates")
      when(mockUserAnswers.businessRatesSubcategory) thenReturn Some("business_rates_changes")
      val result = controller().enquiryBackLink(mockUserAnswers)
      val isBusinessRatesSelection = result.isRight
      isBusinessRatesSelection mustBe true
      assert(result.toOption.get == routes.TellUsMoreController.onPageLoad(NormalMode).url)
    }

    "The enquiry key function produces a string with a Business subcategory back link when the enquiry category is business_rates_from_home" in {
      when(mockUserAnswers.enquiryCategory) thenReturn Some("business_rates")
      when(mockUserAnswers.businessRatesSubcategory) thenReturn Some("business_rates_from_home")
      val result = controller().enquiryBackLink(mockUserAnswers)
      val isBusinessRatesSelection = result.isRight
      isBusinessRatesSelection mustBe true
      assert(result.toOption.get == routes.TellUsMoreController.onPageLoad(NormalMode).url)
    }

    "The enquiry key function produces a string with a Business subcategory back link when the enquiry category is business_rates_change_valuation" in {
      when(mockUserAnswers.enquiryCategory) thenReturn Some("business_rates")
      when(mockUserAnswers.businessRatesSubcategory) thenReturn Some("business_rates_change_valuation")
      val result = controller().enquiryBackLink(mockUserAnswers)
      val isBusinessRatesSelection = result.isRight
      isBusinessRatesSelection mustBe true
      assert(result.toOption.get == routes.TellUsMoreController.onPageLoad(NormalMode).url)
    }

    "The enquiry key function produces a string with a Business subcategory back link when the enquiry category is business_rates_not_used" in {
      when(mockUserAnswers.enquiryCategory) thenReturn Some("business_rates")
      when(mockUserAnswers.businessRatesSubcategory) thenReturn Some("business_rates_not_used")
      val result = controller().enquiryBackLink(mockUserAnswers)
      val isBusinessRatesSelection = result.isRight
      isBusinessRatesSelection mustBe true
      assert(result.toOption.get == routes.TellUsMoreController.onPageLoad(NormalMode).url)
    }

    "The enquiry key function produces a string with a Business subcategory back link when the enquiry category is business_rates_self_catering" in {
      when(mockUserAnswers.enquiryCategory) thenReturn Some("business_rates")
      when(mockUserAnswers.businessRatesSubcategory) thenReturn Some("business_rates_self_catering")
      val result = controller().enquiryBackLink(mockUserAnswers)
      val isBusinessRatesSelection = result.isRight
      isBusinessRatesSelection mustBe true
      assert(result.toOption.get == routes.TellUsMoreController.onPageLoad(NormalMode).url)
    }

    "The enquiry key function produces a string with a Business subcategory back link when the enquiry category is business_rates_demolished" in {
      when(mockUserAnswers.enquiryCategory) thenReturn Some("business_rates")
      when(mockUserAnswers.businessRatesSubcategory) thenReturn Some("business_rates_demolished")
      val result = controller().enquiryBackLink(mockUserAnswers)
      val isBusinessRatesSelection = result.isRight
      isBusinessRatesSelection mustBe true
      assert(result.toOption.get == routes.TellUsMoreController.onPageLoad(NormalMode).url)
    }

    "The enquiry key function produces a string with a Business subcategory back link when the enquiry category is business_rates_property_empty" in {
      when(mockUserAnswers.enquiryCategory) thenReturn Some("business_rates")
      when(mockUserAnswers.businessRatesSubcategory) thenReturn Some("business_rates_property_empty")
      val result = controller().enquiryBackLink(mockUserAnswers)
      val isBusinessRatesSelection = result.isRight
      isBusinessRatesSelection mustBe true
      assert(result.toOption.get == routes.TellUsMoreController.onPageLoad(NormalMode).url)
    }

    "The enquiry key function produces a string with a Business subcategory back link when the enquiry category is business_rates_valuation" in {
      when(mockUserAnswers.enquiryCategory) thenReturn Some("business_rates")
      when(mockUserAnswers.businessRatesSubcategory) thenReturn Some("business_rates_valuation")
      val result = controller().enquiryBackLink(mockUserAnswers)
      val isBusinessRatesSelection = result.isRight
      isBusinessRatesSelection mustBe true
      assert(result.toOption.get == routes.TellUsMoreController.onPageLoad(NormalMode).url)
    }

    "The enquiry key function produces a string with a Council Tax subcategory back link when the enquiry category is council_tax" in {
      when(mockUserAnswers.enquiryCategory) thenReturn Some("council_tax")
      when(mockUserAnswers.councilTaxSubcategory) thenReturn Some("council_tax_property_demolished")
      val result = controller().enquiryBackLink(mockUserAnswers)
      val isCouncilTaxSelection = result.isRight
      isCouncilTaxSelection mustBe true
      assert(result.toOption.get == routes.TellUsMoreController.onPageLoad(NormalMode).url)
    }

    "returns the TellUsMoreController when enquiry category is council_tax and sub category is council_tax_property_poor_repair" in {
      when(mockUserAnswers.enquiryCategory) thenReturn Some("council_tax")
      when(mockUserAnswers.councilTaxSubcategory) thenReturn Some("council_tax_property_poor_repair")
      val result = controller().enquiryBackLink(mockUserAnswers)
      val isCouncilTaxSelection = result.isRight
      isCouncilTaxSelection mustBe true
      assert(result.toOption.get == routes.TellUsMoreController.onPageLoad(NormalMode).url)
    }

    "returns the TellUsMoreController when enquiry category is council_tax and sub category is council_tax_band_too_high" in {
      when(mockUserAnswers.enquiryCategory) thenReturn Some("council_tax")
      when(mockUserAnswers.councilTaxSubcategory) thenReturn Some("council_tax_band_too_high")
      val result = controller().enquiryBackLink(mockUserAnswers)
      val isCouncilTaxSelection = result.isRight
      isCouncilTaxSelection mustBe true
      assert(result.toOption.get == routes.TellUsMoreController.onPageLoad(NormalMode).url)
    }

    "returns the TellUsMoreController when enquiry category is council_tax and sub category is council_tax_property_empty" in {
      when(mockUserAnswers.enquiryCategory) thenReturn Some("council_tax")
      when(mockUserAnswers.councilTaxSubcategory) thenReturn Some("council_tax_property_empty")
      val result = controller().enquiryBackLink(mockUserAnswers)
      val isCouncilTaxSelection = result.isRight
      isCouncilTaxSelection mustBe true
      assert(result.toOption.get == routes.TellUsMoreController.onPageLoad(NormalMode).url)
    }

    "returns the TellUsMoreController when enquiry category is council_tax and sub category is council_tax_property_split_merge" in {
      when(mockUserAnswers.enquiryCategory) thenReturn Some("council_tax")
      when(mockUserAnswers.councilTaxSubcategory) thenReturn Some("council_tax_property_split_merge")
      val result = controller().enquiryBackLink(mockUserAnswers)
      val isCouncilTaxSelection = result.isRight
      isCouncilTaxSelection mustBe true
      assert(result.toOption.get == routes.TellUsMoreController.onPageLoad(NormalMode).url)
    }

    "returns the TellUsMoreController when enquiry category is council_tax and sub category is council_tax_business_uses" in {
      when(mockUserAnswers.enquiryCategory) thenReturn Some("council_tax")
      when(mockUserAnswers.councilTaxSubcategory) thenReturn Some("council_tax_business_uses")
      val result = controller().enquiryBackLink(mockUserAnswers)
      val isCouncilTaxSelection = result.isRight
      isCouncilTaxSelection mustBe true
      assert(result.toOption.get == routes.TellUsMoreController.onPageLoad(NormalMode).url)
    }

    "returns the TellUsMoreController when enquiry category is council_tax and sub category is council_tax_annexe" in {
      when(mockUserAnswers.enquiryCategory) thenReturn Some("council_tax")
      when(mockUserAnswers.councilTaxSubcategory) thenReturn Some("council_tax_annexe")
      val result = controller().enquiryBackLink(mockUserAnswers)
      val isCouncilTaxSelection = result.isRight
      isCouncilTaxSelection mustBe true
      assert(result.toOption.get == routes.TellUsMoreController.onPageLoad(NormalMode).url)
    }

    "returns the TellUsMoreController when enquiry category is council_tax and sub category is council_tax_bill" in {
      when(mockUserAnswers.enquiryCategory) thenReturn Some("council_tax")
      when(mockUserAnswers.councilTaxSubcategory) thenReturn Some("council_tax_bill")
      val result = controller().enquiryBackLink(mockUserAnswers)
      val isCouncilTaxSelection = result.isRight
      isCouncilTaxSelection mustBe true
      assert(result.toOption.get == routes.TellUsMoreController.onPageLoad(NormalMode).url)
    }

    "returns the TellUsMoreController when enquiry category is council_tax and sub category is council_tax_band_for_new" in {
      when(mockUserAnswers.enquiryCategory) thenReturn Some("council_tax")
      when(mockUserAnswers.councilTaxSubcategory) thenReturn Some("council_tax_band_for_new")
      val result = controller().enquiryBackLink(mockUserAnswers)
      val isCouncilTaxSelection = result.isRight
      isCouncilTaxSelection mustBe true
      assert(result.toOption.get == routes.TellUsMoreController.onPageLoad(NormalMode).url)
    }


    "returns the TellUsMoreController when enquiry category is council_tax and sub category is council_tax_area_change" in {
      when(mockUserAnswers.enquiryCategory) thenReturn Some("council_tax")
      when(mockUserAnswers.councilTaxSubcategory) thenReturn Some("council_tax_area_change")
      val result = controller().enquiryBackLink(mockUserAnswers)
      val isCouncilTaxSelection = result.isRight
      isCouncilTaxSelection mustBe true
      assert(result.toOption.get == routes.TellUsMoreController.onPageLoad(NormalMode).url)
    }

    "returns the TellUsMoreController when enquiry category is council_tax and sub category is council_tax_other" in {
      when(mockUserAnswers.enquiryCategory) thenReturn Some("council_tax")
      when(mockUserAnswers.councilTaxSubcategory) thenReturn Some("council_tax_other")
      val result = controller().enquiryBackLink(mockUserAnswers)
      val isCouncilTaxSelection = result.isRight
      isCouncilTaxSelection mustBe true
      assert(result.toOption.get == routes.TellUsMoreController.onPageLoad(NormalMode).url)
    }

    "The enquiry key function produces a Left(Unknown enquiry category in enquiry key) when the enquiry category has not been selected" in {
      when(mockUserAnswers.enquiryCategory) thenReturn None
      when(mockUserAnswers.businessRatesSubcategory) thenReturn Some("business_rates_other")
      val result = controller().enquiryBackLink(mockUserAnswers)
      result mustBe Left("Unknown enquiry category in enquiry key")
    }

    "return OK and the correct view for a GET when enquiry category is council_tax" in {
      val validData = Map(EnquiryCategoryId.toString -> JsString("council_tax"), CouncilTaxSubcategoryId.toString -> JsString("council_tax_property_demolished"))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))
      val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsStringCT()
    }

    "return OK and the correct view for a GET when enquiry category is housing_benefit" in {
      val contactDetails = ContactDetails("a", "c", "e")
      val ec = "housing_benefit"
      val contactReason = "new_enquiry"
      val propertyAddress = PropertyAddress("a", Some("b"), "c", Some("d"), "f")
      val housingBenefitSubcategory = "hb-tell-us-more"

      val validData = Map(ContactReasonId.toString -> JsString(contactReason), EnquiryCategoryId.toString -> JsString(ec),
        TellUsMorePage.lastTellUsMorePage -> JsString(housingBenefitSubcategory),
        ContactDetailsId.toString -> Json.toJson(contactDetails), PropertyAddressId.toString -> Json.toJson(propertyAddress),
        housingBenefitSubcategory -> JsString("Enquiry details"))

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))
      val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe OK
    }

    "populate the view correctly on a GET when the question has previously been answered and enquiry category is council_tax" in {
      val validData = Map(EnquiryCategoryId.toString -> JsString("council_tax"), CouncilTaxSubcategoryId.toString -> JsString("council_tax_property_demolished"),
        ContactDetailsId.toString -> Json.toJson(ContactDetails("a", "a@test.com", "0847428742424")))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeRequest)

      contentAsString(result) mustBe viewAsStringCT(ContactDetailsForm().fill(ContactDetails("a", "a@test.com", "0847428742424")))
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

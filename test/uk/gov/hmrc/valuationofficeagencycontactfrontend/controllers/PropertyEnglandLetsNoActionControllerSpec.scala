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
import play.api.libs.json.JsString
import play.api.mvc.Call
import play.api.test.Helpers._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.ContactDetailsForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.{CacheMap, ContactDetails, NormalMode}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.{MessageControllerComponentsHelpers, UserAnswers}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.error.{internalServerError => internal_Server_Error}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{propertyEnglandLetsNoAction => property_england_lets_no_action}

class PropertyEnglandLetsNoActionControllerSpec extends ControllerSpecBase with MockitoSugar {

  def propertyEnglandLetsNoAction: property_england_lets_no_action = app.injector.instanceOf[property_england_lets_no_action]
  def internalServerError: internal_Server_Error                   = app.injector.instanceOf[internal_Server_Error]

  def onwardRoute: Call = routes.EnquiryCategoryController.onPageLoad(NormalMode)

  val mockUserAnswers: UserAnswers = mock[UserAnswers]

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new PropertyEnglandLetsNoActionController(
      frontendAppConfig,
      messagesApi,
      dataRetrievalAction,
      new DataRequiredActionImpl(ec),
      propertyEnglandLetsNoAction,
      MessageControllerComponentsHelpers.stubMessageControllerComponents
    )

  def englandActualBackLink: String = uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.PropertyEnglandActualLetsController.onPageLoad().url

  def viewAsStringNoAction(form: Form[ContactDetails] = ContactDetailsForm()): String =
    propertyEnglandLetsNoAction(frontendAppConfig, englandActualBackLink)(fakeRequest, messages).toString

  "England Lets No Action Controller" must {

    "return OK and the correct view for a GET when business_rates, business_rates_self_catering, England, yes, no in user actual nights journey" in {
      val validData       = Map(
        EnquiryCategoryId.toString              -> JsString("business_rates"),
        BusinessRatesSubcategoryId.toString     -> JsString("business_rates_self_catering"),
        BusinessRatesSelfCateringId.toString    -> JsString("england"),
        PropertyEnglandAvailableLetsId.toString -> JsString("yes"),
        PropertyEnglandActualLetsId.toString    -> JsString("no")
      )
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))
      val result          = controller(getRelevantData).onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsStringNoAction()
    }

    "returns the Property actual nights Controller when enquiry category is business_rates and sub category is business_rates_self_catering" +
      "and businessRatesSelfCateringEnquiry is england and propertyEnglandAvailableLetsEnquiry is yes and propertyEnglandActualLetsEnquiry us no" in {
        when(mockUserAnswers.enquiryCategory) thenReturn Some("business_rates")
        when(mockUserAnswers.businessRatesSubcategory) thenReturn Some("business_rates_self_catering")
        when(mockUserAnswers.businessRatesSelfCateringEnquiry) thenReturn Some("england")
        when(mockUserAnswers.propertyEnglandAvailableLetsEnquiry) thenReturn Some("yes")
        when(mockUserAnswers.propertyEnglandActualLetsEnquiry) thenReturn Some("no")
        val result                  = controller().enquiryBackLink(mockUserAnswers)
        val isBusinessRateSelection = result.isRight
        isBusinessRateSelection mustBe true
        assert(result.toOption.get == routes.PropertyEnglandActualLetsController.onPageLoad().url)
      }

    "returns the Property available 140 nights Controller when enquiry category is business_rates and sub category is business_rates_self_catering" +
      "and businessRatesSelfCateringEnquiry is wales and propertyEnglandAvailableLetsEnquiry is yes" in {
        when(mockUserAnswers.enquiryCategory) thenReturn Some("business_rates")
        when(mockUserAnswers.businessRatesSubcategory) thenReturn Some("business_rates_self_catering")
        when(mockUserAnswers.businessRatesSelfCateringEnquiry) thenReturn Some("england")
        when(mockUserAnswers.propertyEnglandAvailableLetsEnquiry) thenReturn Some("no")
        val result                  = controller().enquiryBackLink(mockUserAnswers)
        val isBusinessRateSelection = result.isRight
        isBusinessRateSelection mustBe true
        assert(result.toOption.get == routes.PropertyEnglandAvailableLetsController.onPageLoad().url)
      }

    "The enquiry key function produces a Left(Unknown enquiry category in enquiry key) when the enquiry category has not been selected" in {
      when(mockUserAnswers.enquiryCategory) thenReturn None
      when(mockUserAnswers.businessRatesSubcategory) thenReturn Some("business_rates_other")
      val result = controller().enquiryBackLink(mockUserAnswers)
      result mustBe Left("Unknown enquiry category in enquiry key")
    }

    "The enquiry key function produces a Right(correct path) when the enquiry category has been selected" in {
      when(mockUserAnswers.enquiryCategory) thenReturn Some("business_rates")
      when(mockUserAnswers.businessRatesSubcategory) thenReturn Some("business_rates_self_catering")
      when(mockUserAnswers.businessRatesSelfCateringEnquiry) thenReturn Some("england")
      when(mockUserAnswers.propertyEnglandAvailableLetsEnquiry) thenReturn Some("yes")
      when(mockUserAnswers.propertyEnglandActualLetsEnquiry) thenReturn Some("no")
      val result = controller().enquiryBackLink(mockUserAnswers)
      result mustBe Right(uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.PropertyEnglandActualLetsController.onPageLoad().url)
    }

    "The enquiry key function produces a string with a back link when the enquiry category is no to Actual 70 nights Controller" in {
      when(mockUserAnswers.enquiryCategory) thenReturn Some("business_rates")
      when(mockUserAnswers.businessRatesSubcategory) thenReturn Some("business_rates_self_catering")
      when(mockUserAnswers.businessRatesSelfCateringEnquiry) thenReturn Some("england")
      when(mockUserAnswers.propertyEnglandAvailableLetsEnquiry) thenReturn Some("yes")
      when(mockUserAnswers.propertyEnglandActualLetsEnquiry) thenReturn Some("no")
      val result                   = controller().enquiryBackLink(mockUserAnswers)
      val isBusinessRatesSelection = result.isRight
      isBusinessRatesSelection mustBe true
      assert(result.toOption.get == routes.PropertyEnglandActualLetsController.onPageLoad().url)
    }

    "The enquiry key function produces a string with a back link when the enquiry category is no to available 140 nights controller" in {
      when(mockUserAnswers.enquiryCategory) thenReturn Some("business_rates")
      when(mockUserAnswers.businessRatesSubcategory) thenReturn Some("business_rates_self_catering")
      when(mockUserAnswers.businessRatesSelfCateringEnquiry) thenReturn Some("england")
      when(mockUserAnswers.propertyEnglandAvailableLetsEnquiry) thenReturn Some("no")
      val result                   = controller().enquiryBackLink(mockUserAnswers)
      val isBusinessRatesSelection = result.isRight
      isBusinessRatesSelection mustBe true
      assert(result.toOption.get == routes.PropertyEnglandAvailableLetsController.onPageLoad().url)
    }

    "return 500 and the error view for a GET with no enquiry type" in {
      intercept[Exception] {
        val result = controller().onPageLoad(NormalMode)(fakeRequest)
        status(result) mustBe INTERNAL_SERVER_ERROR
        contentAsString(result) mustBe internalServerError(frontendAppConfig)(fakeRequest, messages).toString
      }
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad.url)
    }

  }
}

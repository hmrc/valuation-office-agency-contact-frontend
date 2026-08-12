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

package uk.gov.hmrc.vo.contact.frontend.controllers

import play.api.libs.json.JsString
import play.api.mvc.Call
import play.api.test.Helpers.*
import uk.gov.hmrc.vo.contact.frontend.controllers.actions.*
import uk.gov.hmrc.vo.contact.frontend.identifiers.*
import uk.gov.hmrc.vo.contact.frontend.models.{CacheMap, NormalMode}
import uk.gov.hmrc.vo.contact.frontend.utils.{MessageControllerComponentsHelpers, UserAnswers}
import uk.gov.hmrc.vo.contact.frontend.views.html
import uk.gov.hmrc.vo.contact.frontend.views.html.error.internal_server_error
import uk.gov.hmrc.vo.contact.frontend.views.html.{error, propertyWalesLetsNoAction as property_wales_lets_no_action}

class PropertyWalesLetsNoActionControllerSpec extends ControllerSpecBase:

  def propertyWalesLetsNoAction: html.propertyWalesLetsNoAction = inject[property_wales_lets_no_action]
  def internalServerError: error.internal_server_error          = inject[internal_server_error]

  def onwardRoute: Call = routes.EnquiryCategoryController.onPageLoad(NormalMode)

  val mockUserAnswers: UserAnswers = mock[UserAnswers]

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    PropertyWalesLetsNoActionController(
      messagesApi,
      dataRetrievalAction,
      DataRequiredActionImpl(ec),
      propertyWalesLetsNoAction,
      MessageControllerComponentsHelpers.stubMessageControllerComponents
    )

  def wales140DaysBackLink: String = uk.gov.hmrc.vo.contact.frontend.controllers.routes.PropertyWalesAvailableLetsController.onPageLoad.url
  def wales70DaysBackLink: String  = uk.gov.hmrc.vo.contact.frontend.controllers.routes.PropertyWalesActualLetsController.onPageLoad.url

  def viewAsString70: String =
    propertyWalesLetsNoAction(wales70DaysBackLink)(using getRequest, messages).toString

  "Wales Lets No Action Controller" should {

    "return OK and the correct view for a GET when business_rates, business_rates_self_catering, wales, yes, no in user 70 day journey" in {
      val validData       = Map(
        EnquiryCategoryId.toString            -> JsString("business_rates"),
        BusinessRatesSubcategoryId.toString   -> JsString("business_rates_self_catering"),
        BusinessRatesSelfCateringId.toString  -> JsString("wales"),
        PropertyWalesAvailableLetsId.toString -> JsString("yes"),
        PropertyWalesActualLetsId.toString    -> JsString("no")
      )
      val getRelevantData = FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))
      val result          = controller(getRelevantData).onPageLoad(getRequest)

      status(result)          shouldBe OK
      contentAsString(result) shouldBe viewAsString70
    }

    "returns the Property 70 Days Controller when enquiry category is business_rates and sub category is business_rates_self_catering" +
      "and businessRatesSelfCateringEnquiry is wales and propertyWalesLets140DaysEnquiry is yes and propertyWalesLets70DaysEnquiry us no" in {
        when(mockUserAnswers.enquiryCategory) `thenReturn` Some("business_rates")
        when(mockUserAnswers.businessRatesSubcategory) `thenReturn` Some("business_rates_self_catering")
        when(mockUserAnswers.businessRatesSelfCateringEnquiry) `thenReturn` Some("wales")
        when(mockUserAnswers.propertyWalesAvailableLetsEnquiry) `thenReturn` Some("yes")
        when(mockUserAnswers.propertyWalesActualLetsEnquiry) `thenReturn` Some("no")
        val result                  = controller().enquiryBackLink(mockUserAnswers)
        val isBusinessRateSelection = result.isRight
        isBusinessRateSelection shouldBe true
        assert(result.toOption.get == routes.PropertyWalesActualLetsController.onPageLoad.url)
      }

    "returns the Property 140 Days Controller when enquiry category is business_rates and sub category is business_rates_self_catering" +
      "and businessRatesSelfCateringEnquiry is wales and propertyWalesLets140DaysEnquiry is yes" in {
        when(mockUserAnswers.enquiryCategory) `thenReturn` Some("business_rates")
        when(mockUserAnswers.businessRatesSubcategory) `thenReturn` Some("business_rates_self_catering")
        when(mockUserAnswers.businessRatesSelfCateringEnquiry) `thenReturn` Some("wales")
        when(mockUserAnswers.propertyWalesAvailableLetsEnquiry) `thenReturn` Some("no")
        val result                  = controller().enquiryBackLink(mockUserAnswers)
        val isBusinessRateSelection = result.isRight
        isBusinessRateSelection shouldBe true
        assert(result.toOption.get == routes.PropertyWalesAvailableLetsController.onPageLoad.url)
      }

    "The enquiry key function produces a Left(Unknown enquiry category in enquiry key) when the enquiry category has not been selected" in {
      when(mockUserAnswers.enquiryCategory) `thenReturn` None
      when(mockUserAnswers.businessRatesSubcategory) `thenReturn` Some("business_rates_other")
      val result = controller().enquiryBackLink(mockUserAnswers)
      result shouldBe Left("Unknown enquiry category in enquiry key")
    }

    "The enquiry key function produces a Right(correct path) when the enquiry category has been selected" in {
      when(mockUserAnswers.enquiryCategory) `thenReturn` Some("business_rates")
      when(mockUserAnswers.businessRatesSubcategory) `thenReturn` Some("business_rates_self_catering")
      when(mockUserAnswers.businessRatesSelfCateringEnquiry) `thenReturn` Some("wales")
      when(mockUserAnswers.propertyWalesAvailableLetsEnquiry) `thenReturn` Some("yes")
      when(mockUserAnswers.propertyWalesActualLetsEnquiry) `thenReturn` Some("no")
      val result = controller().enquiryBackLink(mockUserAnswers)
      result shouldBe Right(uk.gov.hmrc.vo.contact.frontend.controllers.routes.PropertyWalesActualLetsController.onPageLoad.url)
    }

    "The enquiry key function produces a string with a back link when the enquiry category is no to 70 days Controller" in {
      when(mockUserAnswers.enquiryCategory) `thenReturn` Some("business_rates")
      when(mockUserAnswers.businessRatesSubcategory) `thenReturn` Some("business_rates_self_catering")
      when(mockUserAnswers.businessRatesSelfCateringEnquiry) `thenReturn` Some("wales")
      when(mockUserAnswers.propertyWalesAvailableLetsEnquiry) `thenReturn` Some("yes")
      when(mockUserAnswers.propertyWalesActualLetsEnquiry) `thenReturn` Some("no")
      val result                   = controller().enquiryBackLink(mockUserAnswers)
      val isBusinessRatesSelection = result.isRight
      isBusinessRatesSelection shouldBe true
      assert(result.toOption.get == routes.PropertyWalesActualLetsController.onPageLoad.url)
    }

    "The enquiry key function produces a string with a back link when the enquiry category is no to 140 days controller" in {
      when(mockUserAnswers.enquiryCategory) `thenReturn` Some("business_rates")
      when(mockUserAnswers.businessRatesSubcategory) `thenReturn` Some("business_rates_self_catering")
      when(mockUserAnswers.businessRatesSelfCateringEnquiry) `thenReturn` Some("wales")
      when(mockUserAnswers.propertyWalesAvailableLetsEnquiry) `thenReturn` Some("no")
      val result                   = controller().enquiryBackLink(mockUserAnswers)
      val isBusinessRatesSelection = result.isRight
      isBusinessRatesSelection shouldBe true
      assert(result.toOption.get == routes.PropertyWalesAvailableLetsController.onPageLoad.url)
    }

    "return 500 and the error view for a GET with no enquiry type" in
      intercept[Exception] {
        val result = controller().onPageLoad(getRequest)
        status(result)          shouldBe INTERNAL_SERVER_ERROR
        contentAsString(result) shouldBe internalServerError()(using getRequest, messages).toString
      }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad(getRequest)

      status(result)           shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.SessionExpiredController.onPageLoad.url)
    }

  }

/*
 * Copyright 2018 HM Revenue & Customs
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
import play.api.libs.json.{JsString, Json}
import play.api.test.Helpers._
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.valuationofficeagencycontactfrontend.FakeNavigator
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions.{DataRequiredActionImpl, DataRetrievalAction, FakeDataRetrievalAction}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.{CheckYourAnswersHelper, DateFormatter, RadioOption, UserAnswers}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.viewmodels.AnswerSection
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{check_your_answers, internalServerError}

class CheckYourAnswersControllerSpec extends ControllerSpecBase with MockitoSugar {

  val mockUserAnswers = mock[UserAnswers]

  def onwardRoute = routes.EnquiryCategoryController.onPageLoad(NormalMode)

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new CheckYourAnswersController(frontendAppConfig, messagesApi, new FakeNavigator(desiredRoute = onwardRoute), dataRetrievalAction, new DataRequiredActionImpl)

  "Check Your Answers Controller" must {

    "return 200 for a GET" in {
      val cd = ContactDetails("a", "b", "c", "d", "e")
      val ec = "council_tax"
      val propertyAddress = PropertyAddress("a", Some("b"), "c", Some("d"), "f")
      val councilTaxSubcategory = "council_tax_poor_repair"
      val tellUs = TellUsMore("Hello")
      val confirmedContactDetails = ConfirmedContactDetails(cd)
      val date = DateFormatter.todaysDate()

      val contact = Contact(confirmedContactDetails, propertyAddress, ec, councilTaxSubcategory, tellUs.message)

      val validData = Map(EnquiryCategoryId.toString -> JsString(ec), CouncilTaxSubcategoryId.toString -> JsString(councilTaxSubcategory),
        ContactDetailsId.toString -> Json.toJson(cd), PropertyAddressId.toString -> Json.toJson(propertyAddress), TellUsMoreId.toString -> Json.toJson(tellUs))

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad()(fakeRequest)

      status(result) mustBe OK
    }

    "redirect to Session Expired for a GET if not existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad()(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
    }

    "The sections function produces sections with the business rates check your answers section when the enquiry category is business_rates" in {
      when(mockUserAnswers.enquiryCategory) thenReturn Some("business_rates")
      when(mockUserAnswers.contactDetails) thenReturn Some(ContactDetails("a", "b", "c", "d", "e"))
      when(mockUserAnswers.propertyAddress) thenReturn Some(PropertyAddress("a", Some("a"), "a", Some("a"), "a"))
      when(mockUserAnswers.businessRatesSubcategory) thenReturn Some("a")
      when(mockUserAnswers.tellUsMore) thenReturn Some(TellUsMore("message"))

      val result = controller().sections(mockUserAnswers)
      val checkYourAnswersHelper = new CheckYourAnswersHelper(mockUserAnswers)
      result mustBe Some(Seq(AnswerSection(None, Seq(checkYourAnswersHelper.enquiryCategory, checkYourAnswersHelper.businessRatesSubcategory,
        checkYourAnswersHelper.contactDetails, checkYourAnswersHelper.propertyAddress, checkYourAnswersHelper.tellUsMore).flatten)))
    }

    "The sections function produces sections with the business rates check your answers section when the enquiry category is business_rates " +
      "and addressLine2 and county are None" in {
      when(mockUserAnswers.enquiryCategory) thenReturn Some("business_rates")
      when(mockUserAnswers.contactDetails) thenReturn Some(ContactDetails("a", "b", "c", "d", "e"))
      when(mockUserAnswers.propertyAddress) thenReturn Some(PropertyAddress("a", None, "a", None, "a"))
      when(mockUserAnswers.businessRatesSubcategory) thenReturn Some("a")
      when(mockUserAnswers.tellUsMore) thenReturn Some(TellUsMore("message"))

      val result = controller().sections(mockUserAnswers)
      val checkYourAnswersHelper = new CheckYourAnswersHelper(mockUserAnswers)
      result mustBe Some(Seq(AnswerSection(None, Seq(checkYourAnswersHelper.enquiryCategory, checkYourAnswersHelper.businessRatesSubcategory,
        checkYourAnswersHelper.contactDetails, checkYourAnswersHelper.propertyAddress, checkYourAnswersHelper.tellUsMore).flatten)))
    }

    "The sections function produces sections with the council tax check your answers section when the enquiry category is council_tax" in {

      when(mockUserAnswers.enquiryCategory) thenReturn Some("council_tax")
      when(mockUserAnswers.contactDetails) thenReturn Some(ContactDetails("a", "b", "c", "d", "e"))
      when(mockUserAnswers.propertyAddress) thenReturn Some(PropertyAddress("a", Some("a"), "a", Some("a"), "a"))
      when(mockUserAnswers.councilTaxSubcategory) thenReturn Some("council_tax_poor_repair")
      when(mockUserAnswers.tellUsMore) thenReturn Some(TellUsMore("message"))

      val result = controller().sections(mockUserAnswers)
      val checkYourAnswersHelper = new CheckYourAnswersHelper(mockUserAnswers)
      result mustBe Some(Seq(AnswerSection(None, Seq(checkYourAnswersHelper.enquiryCategory, checkYourAnswersHelper.councilTaxSubcategory,
        checkYourAnswersHelper.contactDetails, checkYourAnswersHelper.propertyAddress, checkYourAnswersHelper.tellUsMore).flatten)))
    }

    "The sections function produces sections with the council tax check your answers section when the enquiry category is council_tax " +
      "and addressLine2 and county are None" in {

      when(mockUserAnswers.enquiryCategory) thenReturn Some("council_tax")
      when(mockUserAnswers.contactDetails) thenReturn Some(ContactDetails("a", "b", "c", "d", "e"))
      when(mockUserAnswers.propertyAddress) thenReturn Some(PropertyAddress("a", None, "a", None, "a"))
      when(mockUserAnswers.councilTaxSubcategory) thenReturn Some("council_tax_poor_repair")
      when(mockUserAnswers.tellUsMore) thenReturn Some(TellUsMore("message"))

      val result = controller().sections(mockUserAnswers)
      val checkYourAnswersHelper = new CheckYourAnswersHelper(mockUserAnswers)
      result mustBe Some(Seq(AnswerSection(None, Seq(checkYourAnswersHelper.enquiryCategory, checkYourAnswersHelper.councilTaxSubcategory,
        checkYourAnswersHelper.contactDetails, checkYourAnswersHelper.propertyAddress, checkYourAnswersHelper.tellUsMore).flatten)))
    }

    "The sections function returns None when giving an unrecognized enquiry category" in {
      when(mockUserAnswers.enquiryCategory) thenReturn Some("adsada")
      val result = controller().sections(mockUserAnswers)
      result mustBe None
    }

    "The sections function returns None when the enquiry category is None" in {
      when(mockUserAnswers.enquiryCategory) thenReturn None
      val result = controller().sections(mockUserAnswers)
      result mustBe None
    }

    "return 500 and the error view for a reaching summary page with wrong enquiry or unknown enquiry" in {
      val cd = ContactDetails("a", "b", "c", "d", "e")
      val ec = "other"
      val propertyAddress = PropertyAddress("a", Some("b"), "c", Some("d"), "f")
      val councilTaxSubcategory = "council_tax_poor_repair"
      val tellUs = TellUsMore("Hello")
      val confirmedContactDetails = ConfirmedContactDetails(cd)
      val validData = Map(EnquiryCategoryId.toString -> JsString(ec), CouncilTaxSubcategoryId.toString -> JsString(councilTaxSubcategory),
        ContactDetailsId.toString -> Json.toJson(cd), PropertyAddressId.toString -> Json.toJson(propertyAddress), TellUsMoreId.toString -> Json.toJson(tellUs))

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      intercept[Exception] {
        val result = controller(getRelevantData).onPageLoad()(fakeRequest)
        status(result) mustBe INTERNAL_SERVER_ERROR
        contentAsString(result) mustBe internalServerError(frontendAppConfig)(fakeRequest, messages).toString
      }
    }

    "return 500 and the error view for a reaching summary page with no enquiry" in {

      intercept[Exception] {
        val result = controller().onPageLoad()(fakeRequest)
        status(result) mustBe INTERNAL_SERVER_ERROR
        contentAsString(result) mustBe internalServerError(frontendAppConfig)(fakeRequest, messages).toString
      }
    }

  }
}



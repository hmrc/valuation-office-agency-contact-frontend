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

package uk.gov.hmrc.valuationofficeagencycontactfrontend

import org.joda.time.LocalDate
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import play.api.mvc.Call
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.UserAnswers

class NavigatorSpec extends SpecBase with MockitoSugar {

  val navigator = new Navigator

  val mockUserAnswers = mock[UserAnswers]

  "Navigator" when {

    "in Normal mode" must {
      "go to Index from an identifier that doesn't exist in the route map" in {
        case object UnknownIdentifier extends Identifier
        navigator.nextPage(UnknownIdentifier, NormalMode)(mockUserAnswers) mustBe routes.IndexController.onPageLoad()
      }

      "return a function that goes to the council tax subcategory page when an enquiry category has been selected and the selection is council tax" in {
        when (mockUserAnswers.enquiryCategory) thenReturn Some("council_tax")
        navigator.nextPage(EnquiryCategoryId, NormalMode)(mockUserAnswers) mustBe routes.CouncilTaxSubcategoryController.onPageLoad(NormalMode)
      }

      "return a function that goes to the contact form page when an enquiry category for council tax has been selected" in {
        when (mockUserAnswers.councilTaxSubcategory) thenReturn Some("find")
        navigator.nextPage(CouncilTaxSubcategoryId, NormalMode)(mockUserAnswers) mustBe routes.ContactDetailsController.onPageLoad(NormalMode)
      }

      "return a function that goes to the property address page when the contact form has been submitted without errors and the enquiry is council tax" in {
        when (mockUserAnswers.contactDetails) thenReturn Some(ContactDetails("First", "Second", "test@email.com", "test@email.com", "0208382737288"))
        navigator.nextPage(ContactDetailsId, NormalMode)(mockUserAnswers) mustBe routes.PropertyAddressController.onPageLoad(NormalMode)
      }

      "return a function that goes to the business rates subcategory page when an enquiry category has been selected and the selection is business rates" in {
        when (mockUserAnswers.enquiryCategory) thenReturn Some("business_rates")
        navigator.nextPage(EnquiryCategoryId, NormalMode)(mockUserAnswers) mustBe routes.BusinessRatesSubcategoryController.onPageLoad(NormalMode)
      }

      "return a function that goes to the contact form page when an enquiry category for business rates has been selected and the selection is not check or update OR challenge my valuation" in {
        when (mockUserAnswers.businessRatesSubcategory) thenReturn Some("business_rates_rateable_value")
        navigator.nextPage(BusinessRatesSubcategoryId, NormalMode)(mockUserAnswers) mustBe routes.ContactDetailsController.onPageLoad(NormalMode)
      }

      "return a function that goes to the property address page when the contact form has been submitted without errors and the enquiry is business rates" in {
        when (mockUserAnswers.contactDetails) thenReturn Some(ContactDetails("First", "Second", "test@email.com", "test@email.com", "0208382737288"))
        navigator.nextPage(ContactDetailsId, NormalMode)(mockUserAnswers) mustBe routes.PropertyAddressController.onPageLoad(NormalMode)
      }

      "return a function that goes to the check and challenge page when an enquiry category for business rates has been selected and the selection is I want to check or update my property details" in {
        when (mockUserAnswers.businessRatesSubcategory) thenReturn Some("business_rates_update_details")
        navigator.nextPage(BusinessRatesSubcategoryId, NormalMode)(mockUserAnswers) mustBe routes.CheckAndChallengeController.onPageLoad()
      }

      "return a function that goes to the check and challenge page when an enquiry category for business rates has been selected and the selection is I want to challenge my valuation" in {
        when (mockUserAnswers.businessRatesSubcategory) thenReturn Some("business_rates_challenge")
        navigator.nextPage(BusinessRatesSubcategoryId, NormalMode)(mockUserAnswers) mustBe routes.CheckAndChallengeController.onPageLoad()
      }

      "return a function that goes to the tell us more page when the property address details form has been submitted without errors" in {
        when (mockUserAnswers.propertyAddress) thenReturn Some(PropertyAddress("1", Some("Street"), "Town", Some("Some county"), "AA11AA"))
        navigator.nextPage(PropertyAddressId, NormalMode)(mockUserAnswers) mustBe routes.TellUsMoreController.onPageLoad(NormalMode)
      }

      "return a function that goes to the summary page when the tell us more form has been submitted without errors" in {
        when (mockUserAnswers.tellUsMore) thenReturn Some(TellUsMore("Hello"))
        navigator.nextPage(TellUsMoreId, NormalMode)(mockUserAnswers) mustBe routes.CheckYourAnswersController.onPageLoad()
      }

      "return a function that goes to the valuation advice page when an enquiry category for valuation and property advice has been selected" in {
        when (mockUserAnswers.enquiryCategory) thenReturn Some("valuation_for_public_body")
        navigator.nextPage(EnquiryCategoryId, NormalMode)(mockUserAnswers) mustBe routes.ValuationAdviceController.onPageLoad()
      }

      "return a function that goes to the confirmation page when the check your answers page has been submitted without errors and the enquiry is about council tax" in {
        val cd = ContactDetails("a", "b", "c", "d", "e")
        val ec = "council_tax"
        val propertyAddress = PropertyAddress("a", Some("b"), "c", Some("d"), "f")
        val councilTaxSubcategory = "council_tax_home_business"
        val tellUs = TellUsMore("Hello")

        val userAnswers = new FakeUserAnswers(cd, ec, councilTaxSubcategory, "", propertyAddress, tellUs)

        navigator.nextPage(CheckYourAnswersId, NormalMode)(userAnswers) mustBe routes.ConfirmationController.onPageLoad()
      }

      "return a function that goes to the confirmation page when addressLine2 and county are None and" +
        " the check your answers page has been submitted without errors and the enquiry is about council tax" in {
        val cd = ContactDetails("a", "b", "c", "d", "e")
        val propertyAddress = PropertyAddress("a", None, "c", None, "f")
        val councilTaxSubcategory = "council_tax_home_business"
        val tellUs = TellUsMore("Hello")

        val userAnswers = new FakeUserAnswers(cd, "council_tax", councilTaxSubcategory, "", propertyAddress, tellUs)

        navigator.nextPage(CheckYourAnswersId, NormalMode)(userAnswers) mustBe routes.ConfirmationController.onPageLoad()
      }

      "return a function that goes to the confirmation page when the check your answers page has been submitted without errors and the enquiry is about business rates" in {
        val cd = ContactDetails("a", "b", "c", "d", "e")
        val propertyAddress = PropertyAddress("a", Some("b"), "c", Some("d"), "f")
        val businessSubcategory = "business_rates_rateable_value"
        val tellUs = TellUsMore("Hello")

        val userAnswers = new FakeUserAnswers(cd, "business_rates", "", businessSubcategory, propertyAddress, tellUs)

        navigator.nextPage(CheckYourAnswersId, NormalMode)(userAnswers) mustBe routes.ConfirmationController.onPageLoad()
      }

      "return a function that goes to the confirmation page when addressLine2 and county are None and " +
        "the check your answers page has been submitted without errors and the enquiry is about business rates" in {
        val cd = ContactDetails("a", "b", "c", "d", "e")
        val propertyAddress = PropertyAddress("a", None, "c", None, "f")
        val businessSubcategory = "business_rates_rateable_value"
        val tellUs = TellUsMore("Hello")

        val userAnswers = new FakeUserAnswers(cd, "business_rates", "", businessSubcategory, propertyAddress, tellUs)

        navigator.nextPage(CheckYourAnswersId, NormalMode)(userAnswers) mustBe routes.ConfirmationController.onPageLoad()
      }

      "return a function that throws a runtime exception if no property address is in the model" in {
        when (mockUserAnswers.propertyAddress) thenReturn None

        intercept[Exception] {
          navigator.nextPage(CheckYourAnswersId, NormalMode)(mockUserAnswers)
        }
      }

      "return a function that throws a runtime exception if unknown exception is thrown in confirmation routing" in {
        when (mockUserAnswers.propertyAddress) thenReturn None
        when (mockUserAnswers.enquiryCategory) thenReturn Some("council_tax")

        intercept[Exception] {
          navigator.nextPage(CheckYourAnswersId, NormalMode)(mockUserAnswers)
        }
      }

      "return a function that goes to the valuation for taxes page when an enquiry category for valuation for taxes has been selected" in {
        when (mockUserAnswers.enquiryCategory) thenReturn Some("valuations_for_tax")
        navigator.nextPage(EnquiryCategoryId, NormalMode)(mockUserAnswers) mustBe routes.ValuationForTaxesController.onPageLoad()
      }

      "return a function that goes to the housing benefits page when an enquiry category for housing benefits has been selected" in {
        when (mockUserAnswers.enquiryCategory) thenReturn Some("housing_benefit")
        navigator.nextPage(EnquiryCategoryId, NormalMode)(mockUserAnswers) mustBe routes.HousingBenefitsController.onPageLoad()
      }

      "return a function that goes to the providing lettings page when an enquiry category for providing lettings has been selected" in {
        when (mockUserAnswers.enquiryCategory) thenReturn Some("providing_lettings")
        navigator.nextPage(EnquiryCategoryId, NormalMode)(mockUserAnswers) mustBe routes.ProvidingLettingsController.onPageLoad()
      }
    }

    "in Check mode" must {
      "go to CheckYourAnswers from an identifier that doesn't exist in the edit route map" in {
        case object UnknownIdentifier extends Identifier
        navigator.nextPage(UnknownIdentifier, CheckMode)(mockUserAnswers) mustBe routes.CheckYourAnswersController.onPageLoad()
      }
    }
  }
}

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

      "return a function that goes to the static placeholder page when an enquiry category has been selected and the selection is not other business or council tax or business rates" in {
        when (mockUserAnswers.enquiryCategory) thenReturn Some("housing_benefit")
        navigator.nextPage(EnquiryCategoryId, NormalMode)(mockUserAnswers) mustBe routes.StaticPagePlaceholderController.onPageLoad()
      }

      "return a function that goes to the council tax subcategory page when an enquiry category has been selected and the selection is council tax" in {
        when (mockUserAnswers.enquiryCategory) thenReturn Some("council_tax")
        navigator.nextPage(EnquiryCategoryId, NormalMode)(mockUserAnswers) mustBe routes.CouncilTaxSubcategoryController.onPageLoad(NormalMode)
      }

      "return a function that goes to the contact form page when an enquiry category for council tax has been selected" in {
        when (mockUserAnswers.councilTaxSubcategory) thenReturn Some("find")
        navigator.nextPage(CouncilTaxSubcategoryId, NormalMode)(mockUserAnswers) mustBe routes.ContactDetailsController.onPageLoad(NormalMode)
      }

      "return a function that goes to the business rates subcategory page when an enquiry category has been selected and the selection is business" in {
        when (mockUserAnswers.enquiryCategory) thenReturn Some("business_rates")
        navigator.nextPage(EnquiryCategoryId, NormalMode)(mockUserAnswers) mustBe routes.BusinessRatesSubcategoryController.onPageLoad(NormalMode)
      }

      "return a function that goes to the check and challenge page when an enquiry category for business rates has been selected and the selection is I want to check or update my property details" in {
        when (mockUserAnswers.businessRatesSubcategory) thenReturn Some("business_rates_update_details")
        navigator.nextPage(BusinessRatesSubcategoryId, NormalMode)(mockUserAnswers) mustBe routes.CheckAndChallengeController.onPageLoad()
      }

      "return a function that goes to the check and challenge page when an enquiry category for business rates has been selected and the selection is I want to challenge my valuation" in {
        when (mockUserAnswers.businessRatesSubcategory) thenReturn Some("business_rates_challenge_valuation")
        navigator.nextPage(BusinessRatesSubcategoryId, NormalMode)(mockUserAnswers) mustBe routes.CheckAndChallengeController.onPageLoad()
      }

      "return a function that goes to the contact form page when an enquiry category for business rates has been selected and the selection is not check or update OR challenge my valuation" in {
        when (mockUserAnswers.businessRatesSubcategory) thenReturn Some("business_rates_rateable_value")
        navigator.nextPage(BusinessRatesSubcategoryId, NormalMode)(mockUserAnswers) mustBe routes.ContactDetailsController.onPageLoad(NormalMode)
      }

      "return a function that goes to the property details page when the contact form has been submitted without errors" in {
        when (mockUserAnswers.contactDetails) thenReturn Some(ContactDetails("First", "Second", "test@email.com", "073753753733", "Phone", "your message"))
        navigator.nextPage(ContactDetailsId, NormalMode)(mockUserAnswers) mustBe routes.PropertyDetailsController.onPageLoad(NormalMode)
      }

      "return a function that goes to the check your details page when the property details form has been submitted without errors" in {
        when (mockUserAnswers.propertyDetails) thenReturn Some(PropertyDetails("1", "Street", "Town", "Some county", "AA11AA"))
        navigator.nextPage(PropertyDetailsId, NormalMode)(mockUserAnswers) mustBe routes.CheckYourAnswersController.onPageLoad()
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

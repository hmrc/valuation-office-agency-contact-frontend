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

      "return a function that goes to the contact form page when an enquiry category for business rates has been selected" in {
        when (mockUserAnswers.businessRatesSubcategory) thenReturn Some("check")
        navigator.nextPage(BusinessRatesSubcategoryId, NormalMode)(mockUserAnswers) mustBe routes.ContactDetailsController.onPageLoad(NormalMode)
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

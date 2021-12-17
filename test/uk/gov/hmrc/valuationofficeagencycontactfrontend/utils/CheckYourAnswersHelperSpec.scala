/*
 * Copyright 2021 HM Revenue & Customs
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

import org.scalatestplus.mockito.MockitoSugar
import play.api.libs.json.JsString
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes
import uk.gov.hmrc.valuationofficeagencycontactfrontend.journey.model.TellUsMorePage
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.{CheckYourAnswersHelper, UserAnswers}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.viewmodels.AnswerRow
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.AddressFormatters._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.ContactFormatter._

class CheckYourAnswersHelperSpec extends SpecBase with MockitoSugar {

  val mockUserAnswers = mock[UserAnswers]

  "Check Your Answers Helper" when {

    "given a User Answers" must {


      "tellUsMore function should return an Answer Row containing tellUsMore.checkYourAnswersLabel label and a message" in {
        val cd = ContactDetails("a", "c", "e")
        val propertyAddress = PropertyAddress("a", Some("b"), "c", Some("d"), "f")
        val councilTaxSubcategory = "council_tax_band"
        val tellUs = TellUsMore("Hello")

        val userAnswers = new FakeUserAnswers(cd, "council_tax", councilTaxSubcategory, "",  "", propertyAddress, tellUs)
        val checkYourAnswers = new CheckYourAnswersHelper(userAnswers)

        val result = checkYourAnswers.tellUsMore()
        result mustBe Some(AnswerRow("tellUsMore.heading", "Hello", false, routes.TellUsMoreController.onPageLoad(CheckMode).url))
      }

      "tellUsMore function should return an Answer Row containing tellUsMore.checkYourAnswersLabel label and a message when address line 2 and county are None" in {
        val cd = ContactDetails("a", "c", "e")
        val propertyAddress = PropertyAddress("a", None, "c", None, "f")
        val councilTaxSubcategory = "council_tax_band"
        val tellUs = TellUsMore("Hello")

        val userAnswers = new FakeUserAnswers(cd, "council_tax", councilTaxSubcategory, "",  "", propertyAddress, tellUs)
        val checkYourAnswers = new CheckYourAnswersHelper(userAnswers)

        val result = checkYourAnswers.tellUsMore()
        result mustBe Some(AnswerRow("tellUsMore.heading", "Hello", false, routes.TellUsMoreController.onPageLoad(CheckMode).url))
      }

      "tellUsMore function should return a None if no TellUsMore object is found in the User Answers" in {
        val userA = new UserAnswers(new CacheMap("", Map()))
        val checkYourAnswers = new CheckYourAnswersHelper(userA)

        val result = checkYourAnswers.tellUsMore()
        result mustBe None
      }

      "enquiryCategory function should return an Answer Row containing enquiryCategory.checkYourAnswersLabel label and a enquiry category option" in {
        val cd = ContactDetails("a", "c", "e")
        val propertyAddress = PropertyAddress("a", Some("b"), "c", Some("d"), "f")
        val ec = "council_tax"
        val councilTaxSubcategory = "council_tax_band"
        val tellUs = TellUsMore("Hello")

        val userAnswers = new FakeUserAnswers(cd, ec, councilTaxSubcategory, "",  "", propertyAddress, tellUs)
        val checkYourAnswers = new CheckYourAnswersHelper(userAnswers)

        val result = checkYourAnswers.enquiryCategory
        result mustBe Some(AnswerRow("enquiryCategory.heading", s"enquiryCategory.$ec", true, routes.EnquiryCategoryController.onPageLoad(CheckMode).url))
      }

      "enquiryCategory function should return an Answer Row containing enquiryCategory.checkYourAnswersLabel label and a enquiry category option when address line 2 and county are None" in {
        val cd = ContactDetails("a", "c", "e")
        val ec = "council_tax"
        val propertyAddress = PropertyAddress("a", None, "c", None, "f")
        val councilTaxSubcategory = "council_tax_band"
        val tellUs = TellUsMore("Hello")

        val userAnswers = new FakeUserAnswers(cd, ec, councilTaxSubcategory, "",  "", propertyAddress, tellUs)
        val checkYourAnswers = new CheckYourAnswersHelper(userAnswers)

        val result = checkYourAnswers.enquiryCategory
        result mustBe Some(AnswerRow("enquiryCategory.heading", s"enquiryCategory.$ec", true, routes.EnquiryCategoryController.onPageLoad(CheckMode).url))
      }

      "enquiryCategory function should return a None if no enquiry category option is found in the User Answers" in {
        val userA = new UserAnswers(new CacheMap("", Map()))
        val checkYourAnswers = new CheckYourAnswersHelper(userA)

        val result = checkYourAnswers.enquiryCategory
        result mustBe None
      }

      "councilTaxSubcategory function should return an Answer Row containing councilTaxSubcategory.checkYourAnswersLabel label and a council tax subcategory option" in {
        val cd = ContactDetails("a", "c", "e")
        val propertyAddress = PropertyAddress("a", Some("b"), "c", Some("d"), "f")
        val councilTaxSubcategory = "council_tax_band"
        val tellUs = TellUsMore("Hello")

        val userAnswers = new FakeUserAnswers(cd, "council_tax", councilTaxSubcategory, "",  "", propertyAddress, tellUs)
        val checkYourAnswers = new CheckYourAnswersHelper(userAnswers)

        val result = checkYourAnswers.councilTaxSubcategory
        result mustBe Some(AnswerRow("councilTaxSubcategory.heading", s"councilTaxSubcategory.$councilTaxSubcategory", true, routes.CouncilTaxSubcategoryController.onPageLoad(NormalMode).url))
      }

      "councilTaxSubcategory function should return an Answer Row containing councilTaxSubcategory.checkYourAnswersLabel label " +
        "and a council tax subcategory option when address line 2 and county are None" in {
        val cd = ContactDetails("a", "c", "e")
        val propertyAddress = PropertyAddress("a", None, "c", None, "f")
        val councilTaxSubcategory = "council_tax_band"
        val tellUs = TellUsMore("Hello")

        val userAnswers = new FakeUserAnswers(cd, "council_tax", councilTaxSubcategory, "",  "", propertyAddress, tellUs)
        val checkYourAnswers = new CheckYourAnswersHelper(userAnswers)

        val result = checkYourAnswers.councilTaxSubcategory
        result mustBe Some(AnswerRow("councilTaxSubcategory.heading", s"councilTaxSubcategory.$councilTaxSubcategory", true, routes.CouncilTaxSubcategoryController.onPageLoad(NormalMode).url))
      }

      "councilTaxSubcategory function should return a None if no council tax sub category option is found in the User Answers" in {
        val userA = new UserAnswers(new CacheMap("", Map()))
        val checkYourAnswers = new CheckYourAnswersHelper(userA)

        val result = checkYourAnswers.councilTaxSubcategory
        result mustBe None
      }

      "businessRatesSubcategory function should return an Answer Row containing businessRatesSubcategory.checkYourAnswersLabel label and a business rates subcategory option" in {
        val cd = ContactDetails("a", "c", "e")
        val propertyAddress = PropertyAddress("a", Some("b"), "c", Some("d"), "f")
        val businessSubcategory = "business_rates_rateable_value"
        val tellUs = TellUsMore("Hello")

        val userAnswers = new FakeUserAnswers(cd, "business_rates", "", businessSubcategory,  "", propertyAddress, tellUs)
        val checkYourAnswers = new CheckYourAnswersHelper(userAnswers)

        val result = checkYourAnswers.businessRatesSubcategory
        result mustBe Some(AnswerRow("businessRatesSubcategory.heading", s"businessRatesSubcategory.$businessSubcategory", true, routes.BusinessRatesSubcategoryController.onPageLoad(NormalMode).url))
      }

      "businessRatesSubcategory function should return an Answer Row containing businessRatesSubcategory.checkYourAnswersLabel label " +
        "and a business rates subcategory option when address line 2 and county are None" in {
        val cd = ContactDetails("a", "c", "e")
        val propertyAddress = PropertyAddress("a", None, "c", None, "f")
        val businessSubcategory = "business_rates_rateable_value"
        val tellUs = TellUsMore("Hello")

        val userAnswers = new FakeUserAnswers(cd, "business_rates", "", businessSubcategory,  "", propertyAddress, tellUs)
        val checkYourAnswers = new CheckYourAnswersHelper(userAnswers)

        val result = checkYourAnswers.businessRatesSubcategory
        result mustBe Some(AnswerRow("businessRatesSubcategory.heading", s"businessRatesSubcategory.$businessSubcategory", true, routes.BusinessRatesSubcategoryController.onPageLoad(NormalMode).url))
      }

      "businessRatesSubcategory function should return a None if no business rates sub category option is found in the User Answers" in {
        val userA = new UserAnswers(new CacheMap("", Map()))
        val checkYourAnswers = new CheckYourAnswersHelper(userA)

        val result = checkYourAnswers.businessRatesSubcategory
        result mustBe None
      }

      "propertyAddress function should return an Answer Row containing propertyAddress.checkYourAnswersLabel label and a council tax address" in {
        val cd = ContactDetails("a", "c", "e")
        val address = PropertyAddress("a", Some("b"), "c", Some("d"), "f")
        val councilTaxSubcategory = "council_tax_band"
        val tellUs = TellUsMore("Hello")

        val userAnswers = new FakeUserAnswers(cd, "council_tax", councilTaxSubcategory, "",  "", address, tellUs)
        val checkYourAnswers = new CheckYourAnswersHelper(userAnswers)

        val result = checkYourAnswers.propertyAddress
        result mustBe Some(AnswerRow("propertyAddress.heading", formattedPropertyAddress(address, "<br>"), false, routes.PropertyAddressController.onPageLoad(CheckMode).url))
      }

      "propertyAddress function should return an Answer Row containing propertyAddress.checkYourAnswersLabel label " +
        "and a council tax address when address line 2 and county are None" in {
        val cd = ContactDetails("a", "c", "e")
        val address = PropertyAddress("a", None, "c", None, "f")
        val councilTaxSubcategory = "council_tax_band"
        val tellUs = TellUsMore("Hello")

        val userAnswers = new FakeUserAnswers(cd, "council_tax", councilTaxSubcategory, "",  "", address, tellUs)
        val checkYourAnswers = new CheckYourAnswersHelper(userAnswers)

        val result = checkYourAnswers.propertyAddress
        result mustBe Some(AnswerRow("propertyAddress.heading", formattedPropertyAddress(address, "<br>"), false, routes.PropertyAddressController.onPageLoad(CheckMode).url))
      }

      "anythingElse function should return an Answer Row containing anythingElse.checkYourAnswersLabel label and an anythingElse text" in {
        val cd = ContactDetails("a", "c", "e")
        val address = PropertyAddress("a", Some("b"), "c", Some("d"), "f")
        val councilTaxSubcategory = "council_tax_band"
        val tellUs = TellUsMore("")
        val anythingElse = Some("AnythingElseTellUs")

        val userAnswers = new FakeUserAnswers(cd, "council_tax", councilTaxSubcategory, "",  "", address, tellUs, anythingElse)
        val checkYourAnswers = new CheckYourAnswersHelper(userAnswers)

        val result = checkYourAnswers.anythingElse
        result mustBe Some(AnswerRow("anythingElse.checkYourAnswersLabel", anythingElse.get, false, routes.AnythingElseTellUsController.onPageLoad().url))
      }

      "propertyAddress function should return a None if no property address is found in the User Answers" in {
        val userA = new UserAnswers(new CacheMap("", Map()))
        val checkYourAnswers = new CheckYourAnswersHelper(userA)

        val result = checkYourAnswers.propertyAddress
        result mustBe None
      }

      "contactDetails function should return an Answer Row containing contactDetails.checkYourAnswersLabel label and a contact details object" in {
        val cd = ContactDetails("a", "c", "e")
        val address = PropertyAddress("a", Some("b"), "c", Some("d"), "f")
        val councilTaxSubcategory = "council_tax_band"
        val tellUs = TellUsMore("Hello")

        val userAnswers = new FakeUserAnswers(cd, "council_tax", councilTaxSubcategory, "",  "", address, tellUs)
        val checkYourAnswers = new CheckYourAnswersHelper(userAnswers)

        val result = checkYourAnswers.contactDetails
        result mustBe Some(AnswerRow("contactDetails.heading", formattedContactDetails(userAnswers.contactDetails, "<br>"), false, routes.ContactDetailsController.onPageLoad(CheckMode).url))
      }

      "contactDetails function should return an Answer Row containing contactDetails.checkYourAnswersLabel label and a " +
        "contact details object when address line 2 and county are None" in {
        val cd = ContactDetails("a", "c", "e")
        val ec = "council_tax"
        val address = PropertyAddress("a", None, "c", None, "f")
        val councilTaxSubcategory = "council_tax_band"
        val tellUs = TellUsMore("Hello")

        val userAnswers = new FakeUserAnswers(cd, ec, councilTaxSubcategory, "",  "", address, tellUs)
        val checkYourAnswers = new CheckYourAnswersHelper(userAnswers)

        val result = checkYourAnswers.contactDetails
        result mustBe Some(AnswerRow("contactDetails.heading", formattedContactDetails(userAnswers.contactDetails, "<br>"), false, routes.ContactDetailsController.onPageLoad(CheckMode).url))
      }

      "contactDetails function should return a None if no contact details is found in the User Answers" in {
        val userA = new UserAnswers(new CacheMap("", Map()))
        val checkYourAnswers = new CheckYourAnswersHelper(userA)

        val result = checkYourAnswers.contactDetails
        result mustBe None
      }

      "housingBenefitTellUsMore function should return an Answer Row containing label and a message" in {
        val cd = ContactDetails("a", "c", "e")
        val propertyAddress = PropertyAddress("a", None, "c", None, "f")
        val pageKey = "other-hb-enquiry"
        val subcategory = "other-hb-enquiry"

        val userAnswers = new FakeUserAnswers(cd, "housing_benefit", pageKey, "",  "", propertyAddress,
          cacheMap = new CacheMap("", Map(TellUsMorePage.lastTellUsMorePage -> JsString(subcategory), subcategory -> JsString("Enquiry details"))))
        val checkYourAnswersHelper = new CheckYourAnswersHelper(userAnswers)

        val result = checkYourAnswersHelper.housingBenefitTellUsMore
        result mustBe Some(AnswerRow("housingBenefitSubcategory.other-hb-enquiry", "Enquiry details", false, routes.JourneyController.onPageLoad(pageKey).url))
      }

    }
  }
}

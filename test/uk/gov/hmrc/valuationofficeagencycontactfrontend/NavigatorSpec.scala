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

import org.mockito.Mockito._
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.UserAnswers

class NavigatorSpec extends SpecBase with MockitoSugar with ScalaCheckDrivenPropertyChecks{

  val navigator = new Navigator

  val mockUserAnswers = mock[UserAnswers]

  "Navigator" when {

    "in Normal mode" must {
      "go to Index from an identifier that doesn't exist in the route map" in {
        case object UnknownIdentifier extends Identifier
        navigator.nextPage(UnknownIdentifier, NormalMode)(mockUserAnswers) mustBe routes.EnquiryCategoryController.onPageLoad(NormalMode)
      }

      "return a function that goes to the Council Tax Sub Category page when an enquiry category has been selected and the selection is council tax" in {
        when (mockUserAnswers.enquiryCategory) thenReturn Some("council_tax")
        navigator.nextPage(EnquiryCategoryId, NormalMode)(mockUserAnswers) mustBe routes.CouncilTaxSubcategoryController.onPageLoad(NormalMode)
      }

      "return function that goes 'When you can expect an update' when enquiry was in last 30 days" in {
        when (mockUserAnswers.enquiryDate) thenReturn Some("no")
        navigator.nextPage(EnquiryDateId, NormalMode)(mockUserAnswers) mustBe routes.ExpectedUpdateController.onPageLoad()
      }

      "return function that goes ExpectedUpdateController.onPageLoad when the enquiryDate return notKnow" in {
        when (mockUserAnswers.enquiryDate) thenReturn Some("notKnow")
        navigator.nextPage(EnquiryDateId, NormalMode)(mockUserAnswers) mustBe routes.ExistingEnquiryCategoryController.onPageLoad()
      }

      "return function that goes ExpectedUpdateController.onPageLoad when the enquiryDate return yes" in {
        when (mockUserAnswers.enquiryDate) thenReturn Some("yes")
        navigator.nextPage(EnquiryDateId, NormalMode)(mockUserAnswers) mustBe routes.ExistingEnquiryCategoryController.onPageLoad()
      }

      "return function that goes on 'date page' when he want update about existing enquiry" in {
        when (mockUserAnswers.contactReason) thenReturn Some("update_existing")
        navigator.nextPage(ContactReasonId, NormalMode)(mockUserAnswers) mustBe routes.EnquiryDateController.onPageLoad()
      }

      "return an exception when the contact reason returns other" in {
        when (mockUserAnswers.contactReason) thenReturn Some("error")
        an [RuntimeException] should be thrownBy navigator.nextPage(ContactReasonId, NormalMode)(mockUserAnswers)
      }

      "return function that goes on Enquiry Category page when he want select a new enquiry" in {
        when (mockUserAnswers.contactReason) thenReturn Some("new_enquiry")
        navigator.nextPage(ContactReasonId, NormalMode)(mockUserAnswers) mustBe routes.EnquiryCategoryController.onPageLoad(NormalMode)
      }

      "return function that goes on Existing Enquiry Category page when he want update more details" in {
        when (mockUserAnswers.contactReason) thenReturn Some("more_details")
        navigator.nextPage(ContactReasonId, NormalMode)(mockUserAnswers) mustBe routes.ExistingEnquiryCategoryController.onPageLoad()
      }

      "return function that goes 'What is your reference number?' after he select area of contact" in {
        navigator.nextPage(ExistingEnquiryCategoryId, NormalMode)(mockUserAnswers) mustBe routes.RefNumberController.onPageLoad()
      }

      "return a function that goes to the contact form page when an enquiry category for council tax has been selected" in {
        when (mockUserAnswers.councilTaxSubcategory) thenReturn Some("find")
        navigator.nextPage(CouncilTaxSubcategoryId, NormalMode)(mockUserAnswers) mustBe routes.ContactDetailsController.onPageLoad(NormalMode)
      }

      "return a function that goes to the property address page when the contact form has been submitted without errors and the enquiry is council tax" in {
        when (mockUserAnswers.contactDetails) thenReturn Some(ContactDetails("First", "test@email.com", "0208382737288"))
        navigator.nextPage(ContactDetailsId, NormalMode)(mockUserAnswers) mustBe routes.PropertyAddressController.onPageLoad(NormalMode)
      }

      "return a function that goes to the business rates smart links page when an enquiry category has been selected and the selection is business rates" in {
        when (mockUserAnswers.enquiryCategory) thenReturn Some("business_rates")
        navigator.nextPage(EnquiryCategoryId, NormalMode)(mockUserAnswers) mustBe routes.BusinessRatesSmartLinksController.onPageLoad()
      }

      "return a function that goes to the contact form page when an enquiry category for business rates has been selected" in {
        when (mockUserAnswers.businessRatesSubcategory) thenReturn Some("business_rates_poor_repair")
        navigator.nextPage(BusinessRatesSubcategoryId, NormalMode)(mockUserAnswers) mustBe routes.ContactDetailsController.onPageLoad(NormalMode)
      }

      "return a function that goes to the challange form page when an enquiry category for business rates has been selected and business_rates_challenge option selected" in {
        when (mockUserAnswers.businessRatesSubcategory) thenReturn Some("business_rates_challenge")
        navigator.nextPage(BusinessRatesSubcategoryId, NormalMode)(mockUserAnswers) mustBe routes.BusinessRatesChallengeController.onChallengePageLoad()
      }

      "return a function that goes to the challange form page when an enquiry category for business rates has been selected and business_rates_changes option selected" in {
        when (mockUserAnswers.businessRatesSubcategory) thenReturn Some("business_rates_changes")
        navigator.nextPage(BusinessRatesSubcategoryId, NormalMode)(mockUserAnswers) mustBe routes.BusinessRatesChallengeController.onAreaChangePageLoad()
      }

      "throw exception when an enquiry category for business rates has been selected and not other options was selected on next page" in {
        when (mockUserAnswers.businessRatesSubcategory) thenReturn None
        an [RuntimeException] should be thrownBy navigator.nextPage(BusinessRatesSubcategoryId, NormalMode)(mockUserAnswers)
      }

      // Property base testing, can be disabled.
      "return a function that goes to the contact form page when an enquiry category for business rates has been selected and random string selected on next page" in {
        forAll(("category")) { (category: String) =>
          val userAnswerMock = mock[UserAnswers]
          when (userAnswerMock.businessRatesSubcategory) thenReturn Some(category)
          navigator.nextPage(BusinessRatesSubcategoryId, NormalMode)(userAnswerMock) mustBe routes.ContactDetailsController.onPageLoad(NormalMode)
        }
      }


      "return a function that goes to the property address page when the contact form has been submitted without errors and the enquiry is business rates" in {
        when (mockUserAnswers.contactDetails) thenReturn Some(ContactDetails("First", "test@email.com", "0208382737288"))
        navigator.nextPage(ContactDetailsId, NormalMode)(mockUserAnswers) mustBe routes.PropertyAddressController.onPageLoad(NormalMode)
      }

      "return a function that goes to the tell us more page when the property address details form has been submitted without errors" in {
        when (mockUserAnswers.contactReason) thenReturn Some("new_enquiry")
        when (mockUserAnswers.propertyAddress) thenReturn Some(PropertyAddress("1", Some("Street"), "Town", Some("Some county"), "AA11AA"))
        navigator.nextPage(PropertyAddressId, NormalMode)(mockUserAnswers) mustBe routes.TellUsMoreController.onPageLoad(NormalMode)
      }

      "return a function that goes to the 'Tell us more' page when the property address details form has been submitted without errors and I'm udating existing enquiry" in {
        when (mockUserAnswers.propertyAddress) thenReturn Some(PropertyAddress("1", Some("Street"), "Town", Some("Some county"), "AA11AA"))
        when (mockUserAnswers.contactReason) thenReturn Some("more_details")
        navigator.nextPage(PropertyAddressId, NormalMode)(mockUserAnswers) mustBe routes.WhatElseController.onPageLoad()
      }

      "return a function that goes to the 'Anything Else' page when the property address details form has been submitted without errors" in {
        when (mockUserAnswers.propertyAddress) thenReturn Some(PropertyAddress("1", Some("Street"), "Town", Some("Some county"), "AA11AA"))
        when (mockUserAnswers.contactReason) thenReturn Some("update_existing")
        navigator.nextPage(PropertyAddressId, NormalMode)(mockUserAnswers) mustBe routes.AnythingElseTellUsController.onPageLoad()
      }

      "return an exception when the property address details form has been submitted with a wrong reason" in {
        when (mockUserAnswers.propertyAddress) thenReturn Some(PropertyAddress("1", Some("Street"), "Town", Some("Some county"), "AA11AA"))
        when (mockUserAnswers.contactReason) thenReturn Some("wrong")
        intercept[Exception] {
          navigator.nextPage(PropertyAddressId, NormalMode)(mockUserAnswers)
        }
      }

      "return a function that goes to the summary page when the tell us more form has been submitted without errors" in {
        when (mockUserAnswers.tellUsMore) thenReturn Some(TellUsMore("Hello"))
        navigator.nextPage(TellUsMoreId, NormalMode)(mockUserAnswers) mustBe routes.CheckYourAnswersController.onPageLoad()
      }

      "return a function that goes to the 'contact details' page when the council tax subcategory is council_tax_property_poor_repair" in {
        when (mockUserAnswers.councilTaxSubcategory) thenReturn Some("council_tax_property_poor_repair")
        navigator.nextPage(TellUsMoreId, NormalMode)(mockUserAnswers) mustBe routes.ContactDetailsController.onPageLoad(NormalMode)
      }

      "return a function that goes to the valuation advice page when an enquiry category for valuation and property advice has been selected" in {
        when (mockUserAnswers.enquiryCategory) thenReturn Some("valuation_for_public_body")
        navigator.nextPage(EnquiryCategoryId, NormalMode)(mockUserAnswers) mustBe routes.ValuationAdviceController.onPageLoad()
      }

      "return a function that goes to the confirmation page when the check your answers page has been submitted without errors and the enquiry is about council tax" in {
        val cd = ContactDetails("a", "c", "e")
        val ec = "council_tax"
        val propertyAddress = PropertyAddress("a", Some("b"), "c", Some("d"), "f")
        val councilTaxSubcategory = "council_tax_home_business"
        val tellUs = TellUsMore("Hello")

        val userAnswers = new FakeUserAnswers(cd, ec, councilTaxSubcategory, "", propertyAddress, tellUs)

        navigator.nextPage(CheckYourAnswersId, NormalMode)(userAnswers) mustBe routes.ConfirmationController.onPageLoad()
      }

      "return a function that goes to the confirmation page when addressLine2 and county are None and" +
        " the check your answers page has been submitted without errors and the enquiry is about council tax" in {
        val cd = ContactDetails("a", "c", "e")
        val propertyAddress = PropertyAddress("a", None, "c", None, "f")
        val councilTaxSubcategory = "council_tax_home_business"
        val tellUs = TellUsMore("Hello")

        val userAnswers = new FakeUserAnswers(cd, "council_tax", councilTaxSubcategory, "", propertyAddress, tellUs)

        navigator.nextPage(CheckYourAnswersId, NormalMode)(userAnswers) mustBe routes.ConfirmationController.onPageLoad()
      }

      "return a function that goes to the confirmation page when the check your answers page has been submitted without errors and the enquiry is about business rates" in {
        val cd = ContactDetails("a", "c", "e")
        val propertyAddress = PropertyAddress("a", Some("b"), "c", Some("d"), "f")
        val businessSubcategory = "business_rates_rateable_value"
        val tellUs = TellUsMore("Hello")

        val userAnswers = new FakeUserAnswers(cd, "business_rates", "", businessSubcategory, propertyAddress, tellUs)

        navigator.nextPage(CheckYourAnswersId, NormalMode)(userAnswers) mustBe routes.ConfirmationController.onPageLoad()
      }

      "return a function that goes to the confirmation page when addressLine2 and county are None and " +
        "the check your answers page has been submitted without errors and the enquiry is about business rates" in {
        val cd = ContactDetails("a", "c", "e")
        val propertyAddress = PropertyAddress("a", None, "c", None, "f")
        val businessSubcategory = "business_rates_rateable_value"
        val tellUs = TellUsMore("Hello")

        val userAnswers = new FakeUserAnswers(cd, "business_rates", "", businessSubcategory, propertyAddress, tellUs)

        navigator.nextPage(CheckYourAnswersId, NormalMode)(userAnswers) mustBe routes.ConfirmationController.onPageLoad()
      }

      "return a function that goes to the existing enquiry confirmation page when the check your answers page " +
        "has been submitted without errors and the enquiry is about council tax" in {
        val cd = ContactDetails("a", "c", "e")
        val ee = "council_tax"
        val propertyAddress = PropertyAddress("a", Some("b"), "c", Some("d"), "f")
        val councilTaxSubcategory = "council_tax_home_business"
        val tellUs = TellUsMore("Hello")

        val userAnswers = new FakeUserAnswers(cd, "", councilTaxSubcategory, "", propertyAddress, tellUs, ee = Some(ee))

        navigator.nextPage(CheckYourAnswersId, NormalMode)(userAnswers) mustBe routes.ConfirmationController.onPageLoad()
      }

      "return a function that goes to the existing enquiry confirmation page when the check your answers page " +
        "has been submitted without errors and the enquiry is about housing allowance" in {
        val cd = ContactDetails("a", "c", "e")
        val propertyAddress = PropertyAddress("a", Some("b"), "c", Some("d"), "f")
        val housingAllowance = "housing_allowance"
        val message = "Hello"

        val userAnswers = new FakeUserAnswers(cd, "", "", "", propertyAddress, ha = Some(housingAllowance), ee = Some(housingAllowance), ae = Some(message))

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

      "return a function that goes to the council tax property empty form page when an enquiry category for council tax has been selected and council_tax_property_empty option selected" in {
        when (mockUserAnswers.councilTaxSubcategory) thenReturn Some("council_tax_property_empty")
        navigator.nextPage(CouncilTaxSubcategoryId, NormalMode)(mockUserAnswers) mustBe routes.CouncilTaxPropertyEmptyController.onPageLoad()
      }

      "return a function that goes to the council tax property wind and water page when an enquiry category for council tax has been selected and council_tax_property_poor_repair option selected" in {
        when (mockUserAnswers.councilTaxSubcategory) thenReturn Some("council_tax_property_poor_repair")
        navigator.nextPage(CouncilTaxSubcategoryId, NormalMode)(mockUserAnswers) mustBe routes.PropertyWindWaterController.onEnquiryLoad()
      }

      "return a function that goes to the council tax property demolished page when an enquiry category for council tax has been selected and council_tax_property_demolished option selected" in {
        when (mockUserAnswers.councilTaxSubcategory) thenReturn Some("council_tax_property_demolished")
        navigator.nextPage(CouncilTaxSubcategoryId, NormalMode)(mockUserAnswers) mustBe routes.PropertyDemolishedController.onPageLoad()
      }

      "return a function that goes to the council tax annexe page when an enquiry category for council tax has been selected and council_tax_annexe option selected" in {
        when (mockUserAnswers.councilTaxSubcategory) thenReturn Some("council_tax_annexe")
        navigator.nextPage(CouncilTaxSubcategoryId, NormalMode)(mockUserAnswers) mustBe routes.CouncilTaxAnnexeController.onPageLoad()
      }

      "return a function that goes to the council tax business property page when an enquiry category for council tax has been selected and council_tax_business_uses option selected" in {
        when (mockUserAnswers.councilTaxSubcategory) thenReturn Some("council_tax_business_uses")
        navigator.nextPage(CouncilTaxSubcategoryId, NormalMode)(mockUserAnswers) mustBe routes.CouncilTaxBusinessController.onPageLoad()
      }

      "return function that goes 'The Council Tax band cannot be reduced or removed' when property is property wind and watertight" in {
        when (mockUserAnswers.propertyWindEnquiry) thenReturn Some("yes")
        navigator.nextPage(CouncilTaxPropertyPoorRepairId, NormalMode)(mockUserAnswers) mustBe routes.PropertyWindWaterController.onPageLoad()
      }

      "return a function that goes to the council tax is annexe self contained page when an annexe enquiry 'added' has been selected" in {
        when (mockUserAnswers.annexeEnquiry) thenReturn Some("added")
        navigator.nextPage(CouncilTaxAnnexeEnquiryId, NormalMode)(mockUserAnswers) mustBe routes.CouncilTaxAnnexeController.onSelfContainedEnquiryPageLoad()
      }

      "return a function that goes to the council tax is annexe removed page when an annexe enquiry 'removed' has been selected" in {
        when (mockUserAnswers.annexeEnquiry) thenReturn Some("removed")
        navigator.nextPage(CouncilTaxAnnexeEnquiryId, NormalMode)(mockUserAnswers) mustBe routes.CouncilTaxAnnexeController.onRemovedPageLoad()
      }

      "return an exception  when an annexe enquiry other has been selected" in {
        when (mockUserAnswers.annexeEnquiry) thenReturn Some("error")
        an [RuntimeException] should be thrownBy navigator.nextPage(CouncilTaxAnnexeEnquiryId, NormalMode)(mockUserAnswers)
      }

      "return a function that goes to the council tax annexe is not self contained page when an enquiry 'no' has been selected" in {
        when (mockUserAnswers.annexeSelfContainedEnquiry) thenReturn Some("no")
        navigator.nextPage(CouncilTaxAnnexeSelfContainedEnquiryId, NormalMode)(mockUserAnswers) mustBe routes.CouncilTaxAnnexeController.onNotSelfContainedPageLoad()
      }

      "return a function that goes to the council tax annexe have cooking washing enquiry page when an enquiry 'yes' has been selected" in {
        when (mockUserAnswers.annexeSelfContainedEnquiry) thenReturn Some("yes")
        navigator.nextPage(CouncilTaxAnnexeSelfContainedEnquiryId, NormalMode)(mockUserAnswers) mustBe routes.CouncilTaxAnnexeController.onHaveCookingWashingPageLoad()
      }

      "throw an exception when is annexe self contained selects an unexpected response" in {
        when (mockUserAnswers.annexeSelfContainedEnquiry) thenReturn Some("foo")
        intercept[Exception] {
          navigator.nextPage(CouncilTaxAnnexeSelfContainedEnquiryId, NormalMode)(mockUserAnswers)
        }
      }

      "return a function that goes to Your self-contained annexe will not affect your Council Tax band page when an enquiry 'no' has been selected" in {
        when (mockUserAnswers.annexeHaveCookingWashing) thenReturn Some("no")
        navigator.nextPage(CouncilTaxAnnexeHaveCookingId, NormalMode)(mockUserAnswers) mustBe routes.CouncilTaxAnnexeController.onFacilitiesPageLoad()
      }

      "return a function that goes to Speak to your local council about your annexe page when an enquiry 'yes' has been selected" in {
        when (mockUserAnswers.annexeHaveCookingWashing) thenReturn Some("yes")
        navigator.nextPage(CouncilTaxAnnexeHaveCookingId, NormalMode)(mockUserAnswers) mustBe routes.CouncilTaxAnnexeController.onSelfContainedPageLoad()
      }

      "throw an exception when is annexe have cooking washing selects an unexpected response" in {
        when (mockUserAnswers.annexeHaveCookingWashing) thenReturn Some("error")
        intercept[Exception] {
          navigator.nextPage(CouncilTaxAnnexeHaveCookingId, NormalMode)(mockUserAnswers)
        }
      }

    }

    "in Check mode" must {
      "go to CheckYourAnswers from an identifier that doesn't exist in the edit route map" in {
        case object UnknownIdentifier extends Identifier
        navigator.nextPage(UnknownIdentifier, CheckMode)(mockUserAnswers) mustBe routes.CheckYourAnswersController.onPageLoad()
      }

      "return a function that throws a runtime exception if unknown enquiry category is selected on the enquiry category page" in {
        when (mockUserAnswers.enquiryCategory) thenReturn Some("other_selection")
        intercept[Exception] {
          navigator.nextPage(EnquiryCategoryId, NormalMode)(mockUserAnswers)
        }
      }

      "return a function that goes to the business rates subcategory page when an enquiry category has been selected and the selection is business_rates" in {
        when (mockUserAnswers.enquiryCategory) thenReturn Some("business_rates")
        navigator.nextPage(BusinessRatesSmartLinksId, NormalMode)(mockUserAnswers) mustBe routes.BusinessRatesSubcategoryController.onPageLoad(NormalMode)
      }

      "return a function that goes to the council tax bill form page when an enquiry category for council tax has been selected and council_tax_bill option selected" in {
        when (mockUserAnswers.councilTaxSubcategory) thenReturn Some("council_tax_bill")
        navigator.nextPage(CouncilTaxSubcategoryId, NormalMode)(mockUserAnswers) mustBe routes.CouncilTaxBillController.onPageLoad()
      }

      "throw exception when an enquiry category for council tax has been selected and not other options was selected on next page" in {
        when (mockUserAnswers.councilTaxSubcategory) thenReturn None
        an [RuntimeException] should be thrownBy navigator.nextPage(CouncilTaxSubcategoryId, NormalMode)(mockUserAnswers)
      }

      "return a function that goes to the property wind and water page when the  form has been submitted without errors" in {
        when (mockUserAnswers.propertyWindEnquiry) thenReturn Some("no")
        navigator.nextPage(CouncilTaxPropertyPoorRepairId, NormalMode)(mockUserAnswers) mustBe routes.DatePropertyChangedController.onPageLoad()
      }

      "return a function that throws a runtime exception if unknown option is selected on the Property Wind Enquiry page" in {
        when (mockUserAnswers.propertyWindEnquiry) thenReturn Some("error")
        an [RuntimeException] should be thrownBy navigator.nextPage(CouncilTaxPropertyPoorRepairId, NormalMode)(mockUserAnswers)
      }

      "return an exception when the enquiryDate return error " in {
        when (mockUserAnswers.enquiryDate) thenReturn Some("error")
        an [RuntimeException] should be thrownBy navigator.nextPage(EnquiryDateId, NormalMode)(mockUserAnswers)
      }

      "return function that goes on Property Address page when he want update exiting enquiry" in {
        when (mockUserAnswers.contactReason) thenReturn Some("update_existing")
        navigator.nextPage(ContactDetailsId, NormalMode)(mockUserAnswers) mustBe routes.PropertyAddressController.onPageLoad(NormalMode)
      }

      "return a exception when enquiryCategory return None" in {
        when (mockUserAnswers.enquiryCategory) thenReturn None
        an [RuntimeException] should be thrownBy navigator.nextPage(EnquiryCategoryId, NormalMode)(mockUserAnswers)
      }

      "return function that goes on a small part of the property used for business page when councilTaxBusinessEnquiry is small_property" in {
        when (mockUserAnswers.councilTaxBusinessEnquiry) thenReturn Some("small_property")
        navigator.nextPage(CouncilTaxBusinessEnquiryId, NormalMode)(mockUserAnswers) mustBe routes.CouncilTaxBusinessController.onSmallPartUsedPageLoad()
      }

      "return a exception when councilTaxBusinessEnquiry returns None" in {
        when (mockUserAnswers.councilTaxBusinessEnquiry) thenReturn None
        an [RuntimeException] should be thrownBy navigator.nextPage(CouncilTaxBusinessEnquiryId, NormalMode)(mockUserAnswers)
      }
    }
  }
}

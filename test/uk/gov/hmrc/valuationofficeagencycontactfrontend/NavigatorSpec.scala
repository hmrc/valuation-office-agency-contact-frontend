/*
 * Copyright 2025 HM Revenue & Customs
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
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors.AuditingService
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.journey.pages.{EnglandOrWalesPropertyRouter, HousingBenefitAllowancesRouter}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.UserAnswers

class NavigatorSpec extends SpecBase with MockitoSugar with ScalaCheckDrivenPropertyChecks {

  val navigator = new Navigator(mock[AuditingService])

  val mockUserAnswers: UserAnswers = mock[UserAnswers]

  implicit val hc: HeaderCarrier = HeaderCarrier()

  "Navigator" when {

    "in Normal mode" must {
      "go to Index from an identifier that doesn't exist in the route map" in {
        case object UnknownIdentifier extends Identifier
        navigator.nextPage(UnknownIdentifier, NormalMode).apply(mockUserAnswers) mustBe routes.EnquiryCategoryController.onPageLoad(NormalMode)
      }

      "return a function that goes to the Council Tax Sub Category page when an enquiry category has been selected and the selection is council tax" in {
        when(mockUserAnswers.enquiryCategory) `thenReturn` Some("council_tax")
        navigator.nextPage(EnquiryCategoryId, NormalMode).apply(mockUserAnswers) mustBe routes.CouncilTaxSubcategoryController.onPageLoad(NormalMode)
      }

      "return function that goes 'When you can expect an update' when enquiry was in last 30 days" in {
        when(mockUserAnswers.enquiryDate) `thenReturn` Some("no")
        navigator.nextPage(EnquiryDateId, NormalMode).apply(mockUserAnswers) mustBe routes.ExpectedUpdateController.onPageLoad()
      }

      "return function that goes ExpectedUpdateController.onPageLoad when the enquiryDate return notKnow" in {
        when(mockUserAnswers.enquiryDate) `thenReturn` Some("notKnow")
        navigator.nextPage(EnquiryDateId, NormalMode).apply(mockUserAnswers) mustBe routes.ExistingEnquiryCategoryController.onPageLoad()
      }

      "return function that goes ExpectedUpdateController.onPageLoad when the enquiryDate return yes" in {
        when(mockUserAnswers.enquiryDate) `thenReturn` Some("yes")
        navigator.nextPage(EnquiryDateId, NormalMode).apply(mockUserAnswers) mustBe routes.ExistingEnquiryCategoryController.onPageLoad()
      }

      "return function that goes on 'date page' when he want update about existing enquiry" in {
        when(mockUserAnswers.contactReason) `thenReturn` Some("update_existing")
        navigator.nextPage(ContactReasonId, NormalMode).apply(mockUserAnswers) mustBe routes.EnquiryDateController.onPageLoad()
      }

      "return an exception when the contact reason returns other" in {
        when(mockUserAnswers.contactReason) `thenReturn` Some("error")
        an[RuntimeException] should be thrownBy navigator.nextPage(ContactReasonId, NormalMode).apply(mockUserAnswers)
      }

      "return function that goes on Enquiry Category page when he want select a new enquiry" in {
        when(mockUserAnswers.contactReason) `thenReturn` Some("new_enquiry")
        navigator.nextPage(ContactReasonId, NormalMode).apply(mockUserAnswers) mustBe routes.EnquiryCategoryController.onPageLoad(NormalMode)
      }

      "return function that goes on Existing Enquiry Category page when he want update more details" in {
        when(mockUserAnswers.contactReason) `thenReturn` Some("more_details")
        navigator.nextPage(ContactReasonId, NormalMode).apply(mockUserAnswers) mustBe routes.ExistingEnquiryCategoryController.onPageLoad()
      }

      "return function that goes 'What is your reference number?' after he select area of contact" in {
        navigator.nextPage(ExistingEnquiryCategoryId, NormalMode).apply(mockUserAnswers) mustBe routes.RefNumberController.onPageLoad()
      }

      "return a function that goes to the contact form page when an enquiry category for council tax has been selected" in {
        when(mockUserAnswers.councilTaxSubcategory) `thenReturn` Some("find")
        navigator.nextPage(CouncilTaxSubcategoryId, NormalMode).apply(mockUserAnswers) mustBe routes.ContactDetailsController.onPageLoad(NormalMode)
      }

      "return a function that goes to the property address page when the contact form has been submitted without errors and the enquiry is council tax" in {
        when(mockUserAnswers.contactDetails) `thenReturn` Some(ContactDetails("First", "test@email.com", "0208382737288"))
        navigator.nextPage(ContactDetailsId, NormalMode).apply(mockUserAnswers) mustBe routes.PropertyAddressController.onPageLoad(NormalMode)
      }

      "return a function that goes to the business rates enquiry page when an enquiry category has been selected and the selection is business rates" in {
        when(mockUserAnswers.enquiryCategory) `thenReturn` Some("business_rates")
        navigator.nextPage(EnquiryCategoryId, NormalMode).apply(mockUserAnswers) mustBe routes.BusinessRatesSubcategoryController.onPageLoad(NormalMode)
      }

      "return a function that goes to the contact form page when an enquiry category for business rates has been selected" in {
        when(mockUserAnswers.businessRatesSubcategory) `thenReturn` Some("business_rates_poor_repair")
        navigator.nextPage(BusinessRatesSubcategoryId, NormalMode).apply(mockUserAnswers) mustBe routes.ContactDetailsController.onPageLoad(NormalMode)
      }

      "return a function that goes to the challange form page when an enquiry category for business rates has been selected and business_rates_changes option selected" in {
        when(mockUserAnswers.businessRatesSubcategory) `thenReturn` Some("business_rates_changes")
        navigator.nextPage(BusinessRatesSubcategoryId, NormalMode).apply(mockUserAnswers) mustBe routes.JourneyController.onPageLoad(
          EnglandOrWalesPropertyRouter.key
        )
      }

      "return a function that goes to the property empty page when an enquiry category for business rates has been selected and business_rates_property_empty option selected" in {
        when(mockUserAnswers.businessRatesSubcategory) `thenReturn` Some("business_rates_property_empty")
        navigator.nextPage(BusinessRatesSubcategoryId, NormalMode).apply(mockUserAnswers) mustBe routes.PropertyEmptyController.onBusinessRatesPageLoad()
      }

      "return a function that goes to the change valuation page when an enquiry category for business rates has been selected and business_rates_change_valuation option selected" in {
        when(mockUserAnswers.businessRatesSubcategory) `thenReturn` Some("business_rates_change_valuation")
        navigator.nextPage(BusinessRatesSubcategoryId, NormalMode).apply(mockUserAnswers) mustBe routes.JourneyController.onPageLoad(
          EnglandOrWalesPropertyRouter.key
        )
      }

      "return a function that goes to the self catering form page when an enquiry category for business rates has been selected and business_rates_self_catering option selected" in {
        when(mockUserAnswers.businessRatesSubcategory) `thenReturn` Some("business_rates_self_catering")
        navigator.nextPage(BusinessRatesSubcategoryId, NormalMode).apply(mockUserAnswers) mustBe routes.BusinessRatesSelfCateringController.onPageLoad()
      }

      "return a function that goes to the date property changed when an enquiry category for business rates has been selected and business_rates_from_home option selected" in {
        when(mockUserAnswers.businessRatesSubcategory) `thenReturn` Some("business_rates_from_home")
        navigator.nextPage(BusinessRatesSubcategoryId, NormalMode).apply(mockUserAnswers) mustBe routes.DatePropertyChangedController.onPageLoad()
      }

      "return a function that goes to the tell us more page when an enquiry category for business rates has been selected and business_rates_other option selected" in {
        when(mockUserAnswers.businessRatesSubcategory) `thenReturn` Some("business_rates_other")
        navigator.nextPage(BusinessRatesSubcategoryId, NormalMode).apply(mockUserAnswers) mustBe routes.TellUsMoreController.onPageLoad(NormalMode)
      }

      "return a function that goes to the property demolished page when an enquiry category for business rates has been selected and business_rates_demolished option selected" in {
        when(mockUserAnswers.businessRatesSubcategory) `thenReturn` Some("business_rates_demolished")
        navigator.nextPage(BusinessRatesSubcategoryId, NormalMode).apply(mockUserAnswers) mustBe routes.JourneyController.onPageLoad(
          EnglandOrWalesPropertyRouter.key
        )
      }

      "return a function that goes to the business rates valuation  page when an enquiry category for business rates has been selected and business_rates_valuation option selected" in {
        when(mockUserAnswers.businessRatesSubcategory) `thenReturn` Some("business_rates_valuation")
        navigator.nextPage(BusinessRatesSubcategoryId, NormalMode).apply(mockUserAnswers) mustBe routes.BusinessRatesSubcategoryController.onValuationPageLoad()
      }

      "return a function that goes to the business rates bill page when an enquiry category for business rates has been selected and business_rates_bill option selected" in {
        when(mockUserAnswers.businessRatesSubcategory) `thenReturn` Some("business_rates_bill")
        navigator.nextPage(BusinessRatesSubcategoryId, NormalMode).apply(mockUserAnswers) mustBe routes.BusinessRatesBillController.onPageLoad()
      }

      "return a function that goes to the business property enquiry when an enquiry category for business rates has been selected and business_rates_not_used option selected" in {
        when(mockUserAnswers.businessRatesSubcategory) `thenReturn` Some("business_rates_not_used")
        navigator.nextPage(BusinessRatesSubcategoryId, NormalMode).apply(mockUserAnswers) mustBe routes.BusinessRatesPropertyController.onPageLoad()
      }

      "throw exception when an enquiry category for business rates has been selected and not other options was selected on next page" in {
        when(mockUserAnswers.businessRatesSubcategory) `thenReturn` None
        an[RuntimeException] should be thrownBy navigator.nextPage(BusinessRatesSubcategoryId, NormalMode).apply(mockUserAnswers)
      }

      // Property base testing, can be disabled.
      "return a function that goes to the contact form page when an enquiry category for business rates has been selected and random string selected on next page" in
        forAll("category") { (category: String) =>
          val userAnswerMock = mock[UserAnswers]
          when(userAnswerMock.businessRatesSubcategory) `thenReturn` Some(category)
          navigator.nextPage(BusinessRatesSubcategoryId, NormalMode).apply(userAnswerMock) mustBe routes.ContactDetailsController.onPageLoad(NormalMode)
        }

      "return a function that goes to the property address page when the contact form has been submitted without errors and the enquiry is business rates" in {
        when(mockUserAnswers.contactDetails) `thenReturn` Some(ContactDetails("First", "test@email.com", "0208382737288"))
        navigator.nextPage(ContactDetailsId, NormalMode).apply(mockUserAnswers) mustBe routes.PropertyAddressController.onPageLoad(NormalMode)
      }

      "return a function that goes to the tell us more page when the property address details form has been submitted without errors" in {
        when(mockUserAnswers.contactReason) `thenReturn` Some("new_enquiry")
        when(mockUserAnswers.propertyAddress) `thenReturn` Some(PropertyAddress("1", Some("Street"), "Town", Some("Some county"), "AA11AA"))
        navigator.nextPage(PropertyAddressId, NormalMode).apply(mockUserAnswers) mustBe routes.TellUsMoreController.onPageLoad(NormalMode)
      }

      "return a function that goes to the 'Tell us more' page when the property address details form has been submitted without errors and I'm udating existing enquiry" in {
        when(mockUserAnswers.propertyAddress) `thenReturn` Some(PropertyAddress("1", Some("Street"), "Town", Some("Some county"), "AA11AA"))
        when(mockUserAnswers.contactReason) `thenReturn` Some("more_details")
        navigator.nextPage(PropertyAddressId, NormalMode).apply(mockUserAnswers) mustBe routes.WhatElseController.onPageLoad()
      }

      "return a function that goes to the 'Anything Else' page when the property address details form has been submitted without errors" in {
        when(mockUserAnswers.propertyAddress) `thenReturn` Some(PropertyAddress("1", Some("Street"), "Town", Some("Some county"), "AA11AA"))
        when(mockUserAnswers.contactReason) `thenReturn` Some("update_existing")
        navigator.nextPage(PropertyAddressId, NormalMode).apply(mockUserAnswers) mustBe routes.AnythingElseTellUsController.onPageLoad()
      }

      "return an exception when the property address details form has been submitted with a wrong reason" in {
        when(mockUserAnswers.propertyAddress) `thenReturn` Some(PropertyAddress("1", Some("Street"), "Town", Some("Some county"), "AA11AA"))
        when(mockUserAnswers.contactReason) `thenReturn` Some("wrong")
        intercept[Exception] {
          navigator.nextPage(PropertyAddressId, NormalMode).apply(mockUserAnswers)
        }
      }

      "return a function that goes to the summary page when the tell us more form has been submitted without errors" in {
        when(mockUserAnswers.tellUsMore) `thenReturn` Some(TellUsMore("Hello"))
        navigator.nextPage(TellUsMoreId, NormalMode).apply(mockUserAnswers) mustBe routes.CheckYourAnswersController.onPageLoad()
      }

      "return a function that goes to the 'contact details' page when the council tax subcategory is council_tax_property_poor_repair" in {
        when(mockUserAnswers.councilTaxSubcategory) `thenReturn` Some("council_tax_property_poor_repair")
        navigator.nextPage(TellUsMoreId, NormalMode).apply(mockUserAnswers) mustBe routes.ContactDetailsController.onPageLoad(NormalMode)
      }

      "return a function that goes to the 'Check your answers' page when the property address details form has been submitted without errors and council tax subcategory is council_tax_business_uses" in {
        when(mockUserAnswers.propertyAddress) `thenReturn` Some(PropertyAddress("1", Some("Street"), "Town", Some("Some county"), "AA11AA"))
        when(mockUserAnswers.contactReason) `thenReturn` Some("new_enquiry")
        when(mockUserAnswers.councilTaxSubcategory) `thenReturn` Some("council_tax_business_uses")
        navigator.nextPage(PropertyAddressId, NormalMode).apply(mockUserAnswers) mustBe routes.CheckYourAnswersController.onPageLoad()
      }

      "return a function that goes to the 'Check your answers' page when the property address details form has been submitted without errors and council tax subcategory is council_tax_bill" in {
        when(mockUserAnswers.propertyAddress) `thenReturn` Some(PropertyAddress("1", Some("Street"), "Town", Some("Some county"), "AA11AA"))
        when(mockUserAnswers.contactReason) `thenReturn` Some("new_enquiry")
        when(mockUserAnswers.councilTaxSubcategory) `thenReturn` Some("council_tax_bill")
        navigator.nextPage(PropertyAddressId, NormalMode).apply(mockUserAnswers) mustBe routes.CheckYourAnswersController.onPageLoad()
      }

      "return a function that goes to the 'Check your answers' page when the property address details form has been submitted without errors and council tax subcategory is council_tax_other" in {
        when(mockUserAnswers.propertyAddress) `thenReturn` Some(PropertyAddress("1", Some("Street"), "Town", Some("Some county"), "AA11AA"))
        when(mockUserAnswers.contactReason) `thenReturn` Some("new_enquiry")
        when(mockUserAnswers.councilTaxSubcategory) `thenReturn` Some("council_tax_other")
        navigator.nextPage(PropertyAddressId, NormalMode).apply(mockUserAnswers) mustBe routes.CheckYourAnswersController.onPageLoad()
      }

      "return a function that goes to the 'Check your answers' page when the property address details form has been submitted without errors and council tax subcategory is council_tax_annexe" in {
        when(mockUserAnswers.propertyAddress) `thenReturn` Some(PropertyAddress("1", Some("Street"), "Town", Some("Some county"), "AA11AA"))
        when(mockUserAnswers.contactReason) `thenReturn` Some("new_enquiry")
        when(mockUserAnswers.councilTaxSubcategory) `thenReturn` Some("council_tax_annexe")
        navigator.nextPage(PropertyAddressId, NormalMode).apply(mockUserAnswers) mustBe routes.CheckYourAnswersController.onPageLoad()
      }

      "return a function that goes to the 'Check your answers' page when the property address details form has been submitted without errors and council tax subcategory is council_tax_band_too_high" in {
        when(mockUserAnswers.propertyAddress) `thenReturn` Some(PropertyAddress("1", Some("Street"), "Town", Some("Some county"), "AA11AA"))
        when(mockUserAnswers.contactReason) `thenReturn` Some("new_enquiry")
        when(mockUserAnswers.councilTaxSubcategory) `thenReturn` Some("council_tax_band_too_high")
        navigator.nextPage(PropertyAddressId, NormalMode).apply(mockUserAnswers) mustBe routes.CheckYourAnswersController.onPageLoad()
      }

      "return a function that goes to the 'Check your answers' page when the property address details form has been submitted without errors and council tax subcategory is council_tax_property_poor_repair" in {
        when(mockUserAnswers.propertyAddress) `thenReturn` Some(PropertyAddress("1", Some("Street"), "Town", Some("Some county"), "AA11AA"))
        when(mockUserAnswers.contactReason) `thenReturn` Some("new_enquiry")
        when(mockUserAnswers.councilTaxSubcategory) `thenReturn` Some("council_tax_property_poor_repair")
        navigator.nextPage(PropertyAddressId, NormalMode).apply(mockUserAnswers) mustBe routes.CheckYourAnswersController.onPageLoad()
      }

      "return a function that goes to the 'Check your answers' page when the property address details form has been submitted without errors and council tax subcategory is council_tax_band_for_new" in {
        when(mockUserAnswers.propertyAddress) `thenReturn` Some(PropertyAddress("1", Some("Street"), "Town", Some("Some county"), "AA11AA"))
        when(mockUserAnswers.contactReason) `thenReturn` Some("new_enquiry")
        when(mockUserAnswers.councilTaxSubcategory) `thenReturn` Some("council_tax_band_for_new")
        navigator.nextPage(PropertyAddressId, NormalMode).apply(mockUserAnswers) mustBe routes.CheckYourAnswersController.onPageLoad()
      }

      "return a function that goes to the 'Check your answers' page when the property address details form has been submitted without errors and council tax subcategory is council_tax_area_change" in {
        when(mockUserAnswers.propertyAddress) `thenReturn` Some(PropertyAddress("1", Some("Street"), "Town", Some("Some county"), "AA11AA"))
        when(mockUserAnswers.contactReason) `thenReturn` Some("new_enquiry")
        when(mockUserAnswers.councilTaxSubcategory) `thenReturn` Some("council_tax_area_change")
        navigator.nextPage(PropertyAddressId, NormalMode).apply(mockUserAnswers) mustBe routes.CheckYourAnswersController.onPageLoad()
      }

      "return a function that goes to the 'Check your answers' page when the property address details form has been submitted without errors and council tax subcategory is council_tax_property_empty" in {
        when(mockUserAnswers.propertyAddress) `thenReturn` Some(PropertyAddress("1", Some("Street"), "Town", Some("Some county"), "AA11AA"))
        when(mockUserAnswers.contactReason) `thenReturn` Some("new_enquiry")
        when(mockUserAnswers.councilTaxSubcategory) `thenReturn` Some("council_tax_property_empty")
        navigator.nextPage(PropertyAddressId, NormalMode).apply(mockUserAnswers) mustBe routes.CheckYourAnswersController.onPageLoad()
      }

      "return a function that goes to the 'Check your answers' page when the property address details form has been submitted without errors and council tax subcategory is council_tax_property_split_merge" in {
        when(mockUserAnswers.propertyAddress) `thenReturn` Some(PropertyAddress("1", Some("Street"), "Town", Some("Some county"), "AA11AA"))
        when(mockUserAnswers.contactReason) `thenReturn` Some("new_enquiry")
        when(mockUserAnswers.councilTaxSubcategory) `thenReturn` Some("council_tax_property_split_merge")
        navigator.nextPage(PropertyAddressId, NormalMode).apply(mockUserAnswers) mustBe routes.CheckYourAnswersController.onPageLoad()
      }

      "return a function that goes to the 'Check your answers' page when the property address details form has been submitted without errors and council tax subcategory is council_tax_property_demolished" in {
        when(mockUserAnswers.propertyAddress) `thenReturn` Some(PropertyAddress("1", Some("Street"), "Town", Some("Some county"), "AA11AA"))
        when(mockUserAnswers.contactReason) `thenReturn` Some("new_enquiry")
        when(mockUserAnswers.councilTaxSubcategory) `thenReturn` Some("council_tax_property_demolished")
        navigator.nextPage(PropertyAddressId, NormalMode).apply(mockUserAnswers) mustBe routes.CheckYourAnswersController.onPageLoad()
      }

      "return a function that goes to the 'Check your answers' page when the property address details form has been submitted without errors and business rates subcategory is business_rates_from_home" in {
        when(mockUserAnswers.propertyAddress) `thenReturn` Some(PropertyAddress("1", Some("Street"), "Town", Some("Some county"), "AA11AA"))
        when(mockUserAnswers.contactReason) `thenReturn` Some("new_enquiry")
        when(mockUserAnswers.councilTaxSubcategory) `thenReturn` Some("")
        when(mockUserAnswers.businessRatesSubcategory) `thenReturn` Some("business_rates_from_home")
        navigator.nextPage(PropertyAddressId, NormalMode).apply(mockUserAnswers) mustBe routes.CheckYourAnswersController.onPageLoad()
      }

      "return a function that goes to the 'Check your answers' page when the property address details form has been submitted without errors and business rates subcategory is business_rates_other" in {
        when(mockUserAnswers.propertyAddress) `thenReturn` Some(PropertyAddress("1", Some("Street"), "Town", Some("Some county"), "AA11AA"))
        when(mockUserAnswers.contactReason) `thenReturn` Some("new_enquiry")
        when(mockUserAnswers.councilTaxSubcategory) `thenReturn` Some("")
        when(mockUserAnswers.businessRatesSubcategory) `thenReturn` Some("business_rates_other")
        navigator.nextPage(PropertyAddressId, NormalMode).apply(mockUserAnswers) mustBe routes.CheckYourAnswersController.onPageLoad()
      }

      "return a function that goes to the 'Check your answers' page when the property address details form has been submitted without errors and business rates subcategory is business_rates_change_valuation" in {
        when(mockUserAnswers.propertyAddress) `thenReturn` Some(PropertyAddress("1", Some("Street"), "Town", Some("Some county"), "AA11AA"))
        when(mockUserAnswers.contactReason) `thenReturn` Some("new_enquiry")
        when(mockUserAnswers.councilTaxSubcategory) `thenReturn` Some("")
        when(mockUserAnswers.businessRatesSubcategory) `thenReturn` Some("business_rates_change_valuation")
        navigator.nextPage(PropertyAddressId, NormalMode).apply(mockUserAnswers) mustBe routes.CheckYourAnswersController.onPageLoad()
      }

      "return a function that goes to the 'Check your answers' page when the property address details form has been submitted without errors and business rates subcategory is business_rates_bill" in {
        when(mockUserAnswers.propertyAddress) `thenReturn` Some(PropertyAddress("1", Some("Street"), "Town", Some("Some county"), "AA11AA"))
        when(mockUserAnswers.contactReason) `thenReturn` Some("new_enquiry")
        when(mockUserAnswers.councilTaxSubcategory) `thenReturn` Some("")
        when(mockUserAnswers.businessRatesSubcategory) `thenReturn` Some("business_rates_bill")
        navigator.nextPage(PropertyAddressId, NormalMode).apply(mockUserAnswers) mustBe routes.CheckYourAnswersController.onPageLoad()
      }

      "return a function that goes to the 'Check your answers' page when the property address details form has been submitted without errors and business rates subcategory is business_rates_changes" in {
        when(mockUserAnswers.propertyAddress) `thenReturn` Some(PropertyAddress("1", Some("Street"), "Town", Some("Some county"), "AA11AA"))
        when(mockUserAnswers.contactReason) `thenReturn` Some("new_enquiry")
        when(mockUserAnswers.councilTaxSubcategory) `thenReturn` Some("")
        when(mockUserAnswers.businessRatesSubcategory) `thenReturn` Some("business_rates_changes")
        navigator.nextPage(PropertyAddressId, NormalMode).apply(mockUserAnswers) mustBe routes.CheckYourAnswersController.onPageLoad()
      }

      "return a function that goes to the 'Check your answers' page when the property address details form has been submitted without errors and business rates subcategory is business_rates_not_used" in {
        when(mockUserAnswers.propertyAddress) `thenReturn` Some(PropertyAddress("1", Some("Street"), "Town", Some("Some county"), "AA11AA"))
        when(mockUserAnswers.contactReason) `thenReturn` Some("new_enquiry")
        when(mockUserAnswers.councilTaxSubcategory) `thenReturn` Some("")
        when(mockUserAnswers.businessRatesSubcategory) `thenReturn` Some("business_rates_not_used")
        navigator.nextPage(PropertyAddressId, NormalMode).apply(mockUserAnswers) mustBe routes.CheckYourAnswersController.onPageLoad()
      }

      "return a function that goes to the 'Check your answers' page when the property address details form has been submitted without errors and business rates subcategory is business_rates_self_catering" in {
        when(mockUserAnswers.propertyAddress) `thenReturn` Some(PropertyAddress("1", Some("Street"), "Town", Some("Some county"), "AA11AA"))
        when(mockUserAnswers.contactReason) `thenReturn` Some("new_enquiry")
        when(mockUserAnswers.councilTaxSubcategory) `thenReturn` Some("")
        when(mockUserAnswers.businessRatesSubcategory) `thenReturn` Some("business_rates_self_catering")
        navigator.nextPage(PropertyAddressId, NormalMode).apply(mockUserAnswers) mustBe routes.CheckYourAnswersController.onPageLoad()
      }

      "return a function that goes to the 'Check your answers' page when the property address details form has been submitted without errors and business rates subcategory is business_rates_property_empty" in {
        when(mockUserAnswers.propertyAddress) `thenReturn` Some(PropertyAddress("1", Some("Street"), "Town", Some("Some county"), "AA11AA"))
        when(mockUserAnswers.contactReason) `thenReturn` Some("new_enquiry")
        when(mockUserAnswers.councilTaxSubcategory) `thenReturn` Some("")
        when(mockUserAnswers.businessRatesSubcategory) `thenReturn` Some("business_rates_property_empty")
        navigator.nextPage(PropertyAddressId, NormalMode).apply(mockUserAnswers) mustBe routes.CheckYourAnswersController.onPageLoad()
      }

      "return a function that goes to the 'Check your answers' page when the property address details form has been submitted without errors and business rates subcategory is business_rates_valuation" in {
        when(mockUserAnswers.propertyAddress) `thenReturn` Some(PropertyAddress("1", Some("Street"), "Town", Some("Some county"), "AA11AA"))
        when(mockUserAnswers.contactReason) `thenReturn` Some("new_enquiry")
        when(mockUserAnswers.councilTaxSubcategory) `thenReturn` Some("")
        when(mockUserAnswers.businessRatesSubcategory) `thenReturn` Some("business_rates_valuation")
        navigator.nextPage(PropertyAddressId, NormalMode).apply(mockUserAnswers) mustBe routes.CheckYourAnswersController.onPageLoad()
      }

      "return a function that goes to the 'Check your answers' page when the property address details form has been submitted without errors and business rates subcategory is business_rates_demolished" in {
        when(mockUserAnswers.propertyAddress) `thenReturn` Some(PropertyAddress("1", Some("Street"), "Town", Some("Some county"), "AA11AA"))
        when(mockUserAnswers.contactReason) `thenReturn` Some("new_enquiry")
        when(mockUserAnswers.councilTaxSubcategory) `thenReturn` Some("")
        when(mockUserAnswers.businessRatesSubcategory) `thenReturn` Some("business_rates_demolished")
        navigator.nextPage(PropertyAddressId, NormalMode).apply(mockUserAnswers) mustBe routes.CheckYourAnswersController.onPageLoad()
      }

      "return a function that goes to the 'Check your answers' page from PropertyAddress page when enquiryCategory is housing_benefit" in {
        when(mockUserAnswers.enquiryCategory) `thenReturn` Some("housing_benefit")
        navigator.nextPage(PropertyAddressId, NormalMode).apply(mockUserAnswers) mustBe routes.CheckYourAnswersController.onPageLoad()
      }

      "return a function that goes to the 'contact details' page when the council tax subcategory is council_tax_business_uses" in {
        when(mockUserAnswers.councilTaxSubcategory) `thenReturn` Some("council_tax_business_uses")
        navigator.nextPage(TellUsMoreId, NormalMode).apply(mockUserAnswers) mustBe routes.ContactDetailsController.onPageLoad(NormalMode)
      }

      "return a function that goes to the 'contact details' page when the council tax subcategory is council_tax_area_change" in {
        when(mockUserAnswers.councilTaxSubcategory) `thenReturn` Some("council_tax_area_change")
        navigator.nextPage(TellUsMoreId, NormalMode).apply(mockUserAnswers) mustBe routes.ContactDetailsController.onPageLoad(NormalMode)
      }

      "return a function that goes to the 'contact details' page when the council tax subcategory is council_tax_property_demolished" in {
        when(mockUserAnswers.councilTaxSubcategory) `thenReturn` Some("council_tax_property_demolished")
        navigator.nextPage(TellUsMoreId, NormalMode).apply(mockUserAnswers) mustBe routes.ContactDetailsController.onPageLoad(NormalMode)
      }

      "return a function that goes to the 'contact details' page when the council tax subcategory is council_tax_property_split_merge" in {
        when(mockUserAnswers.councilTaxSubcategory) `thenReturn` Some("council_tax_property_split_merge")
        navigator.nextPage(TellUsMoreId, NormalMode).apply(mockUserAnswers) mustBe routes.ContactDetailsController.onPageLoad(NormalMode)
      }

      "return a function that goes to the 'contact details' page when the council tax subcategory is council_tax_property_empty" in {
        when(mockUserAnswers.councilTaxSubcategory) `thenReturn` Some("council_tax_property_empty")
        navigator.nextPage(TellUsMoreId, NormalMode).apply(mockUserAnswers) mustBe routes.ContactDetailsController.onPageLoad(NormalMode)
      }

      "return a function that goes to the 'contact details' page when the council tax subcategory is council_tax_band_for_new" in {
        when(mockUserAnswers.councilTaxSubcategory) `thenReturn` Some("council_tax_band_for_new")
        navigator.nextPage(TellUsMoreId, NormalMode).apply(mockUserAnswers) mustBe routes.ContactDetailsController.onPageLoad(NormalMode)
      }

      "return a function that goes to the 'contact details' page when the council tax subcategory is council_tax_band_too_high" in {
        when(mockUserAnswers.councilTaxSubcategory) `thenReturn` Some("council_tax_band_too_high")
        navigator.nextPage(TellUsMoreId, NormalMode).apply(mockUserAnswers) mustBe routes.ContactDetailsController.onPageLoad(NormalMode)
      }

      "return a function that goes to the 'contact details' page when the council tax subcategory is council_tax_bill" in {
        when(mockUserAnswers.councilTaxSubcategory) `thenReturn` Some("council_tax_bill")
        navigator.nextPage(TellUsMoreId, NormalMode).apply(mockUserAnswers) mustBe routes.ContactDetailsController.onPageLoad(NormalMode)
      }

      "return a function that goes to the 'contact details' page when the council tax subcategory is council_tax_annexe" in {
        when(mockUserAnswers.councilTaxSubcategory) `thenReturn` Some("council_tax_annexe")
        navigator.nextPage(TellUsMoreId, NormalMode).apply(mockUserAnswers) mustBe routes.ContactDetailsController.onPageLoad(NormalMode)
      }

      "return a function that goes to the 'contact details' page when the council tax subcategory is council_tax_other" in {
        when(mockUserAnswers.councilTaxSubcategory) `thenReturn` Some("council_tax_other")
        navigator.nextPage(TellUsMoreId, NormalMode).apply(mockUserAnswers) mustBe routes.ContactDetailsController.onPageLoad(NormalMode)
      }

      "return a function that goes to the 'contact details' page when the council tax subcategory is council_tax_other1" in {
        when(mockUserAnswers.fairRentEnquiryEnquiry) `thenReturn` Some("submit_new_application")
        navigator.nextPage(TellUsMoreId, NormalMode).apply(mockUserAnswers) mustBe routes.ContactDetailsController.onPageLoad(NormalMode)
      }

      "return a function that goes to the valuation advice page when an enquiry category for valuation and property advice has been selected" in {
        when(mockUserAnswers.enquiryCategory) `thenReturn` Some("valuation_for_public_body")
        navigator.nextPage(EnquiryCategoryId, NormalMode).apply(mockUserAnswers) mustBe routes.ValuationAdviceController.onPageLoad()
      }

      "return a function that goes to the confirmation page when the check your answers page has been submitted without errors and the enquiry is about council tax" in {
        val cd                    = ContactDetails("a", "c", "e")
        val ec                    = "council_tax"
        val propertyAddress       = PropertyAddress("a", Some("b"), "c", Some("d"), "f")
        val councilTaxSubcategory = "council_tax_home_business"
        val tellUs                = TellUsMore("Hello")

        val userAnswers = new FakeUserAnswers(cd, ec, councilTaxSubcategory, "", "", propertyAddress, tellUs)

        navigator.nextPage(CheckYourAnswersId, NormalMode).apply(userAnswers) mustBe routes.ConfirmationController.onPageLoad()
      }

      "return a function that goes to the confirmation page when addressLine2 and county are None and" +
        " the check your answers page has been submitted without errors and the enquiry is about council tax" in {
          val cd                    = ContactDetails("a", "c", "e")
          val propertyAddress       = PropertyAddress("a", None, "c", None, "f")
          val councilTaxSubcategory = "council_tax_home_business"
          val tellUs                = TellUsMore("Hello")

          val userAnswers = new FakeUserAnswers(cd, "council_tax", councilTaxSubcategory, "", "", propertyAddress, tellUs)

          navigator.nextPage(CheckYourAnswersId, NormalMode).apply(userAnswers) mustBe routes.ConfirmationController.onPageLoad()
        }

      "return a function that goes to the confirmation page when the check your answers page has been submitted without errors and the enquiry is about business rates" in {
        val cd                  = ContactDetails("a", "c", "e")
        val propertyAddress     = PropertyAddress("a", Some("b"), "c", Some("d"), "f")
        val businessSubcategory = "business_rates_rateable_value"
        val tellUs              = TellUsMore("Hello")

        val userAnswers = new FakeUserAnswers(cd, "business_rates", "", businessSubcategory, "", propertyAddress, tellUs)

        navigator.nextPage(CheckYourAnswersId, NormalMode).apply(userAnswers) mustBe routes.ConfirmationController.onPageLoad()
      }

      "return a function that goes to the confirmation page when addressLine2 and county are None and " +
        "the check your answers page has been submitted without errors and the enquiry is about business rates" in {
          val cd                  = ContactDetails("a", "c", "e")
          val propertyAddress     = PropertyAddress("a", None, "c", None, "f")
          val businessSubcategory = "business_rates_rateable_value"
          val tellUs              = TellUsMore("Hello")

          val userAnswers = new FakeUserAnswers(cd, "business_rates", "", businessSubcategory, "", propertyAddress, tellUs)

          navigator.nextPage(CheckYourAnswersId, NormalMode).apply(userAnswers) mustBe routes.ConfirmationController.onPageLoad()
        }

      "return a function that goes to the existing enquiry confirmation page when the check your answers page " +
        "has been submitted without errors and the enquiry is about council tax" in {
          val cd                    = ContactDetails("a", "c", "e")
          val ee                    = "council_tax"
          val propertyAddress       = PropertyAddress("a", Some("b"), "c", Some("d"), "f")
          val councilTaxSubcategory = "council_tax_home_business"
          val tellUs                = TellUsMore("Hello")

          val userAnswers = new FakeUserAnswers(cd, "", councilTaxSubcategory, "", "", propertyAddress, tellUs, ee = Some(ee))

          navigator.nextPage(CheckYourAnswersId, NormalMode).apply(userAnswers) mustBe routes.ConfirmationController.onPageLoad()
        }

      "return a function that goes to the confirmation page when the check your answers page has been submitted without errors and the enquiry is about fair rent enquiries" in {
        val cd                        = ContactDetails("a", "c", "e")
        val propertyAddress           = PropertyAddress("a", Some("b"), "c", Some("d"), "f")
        val housingBenefitSubcategory = "submit_new_application"
        val tellUs                    = TellUsMore("Hello")

        val userAnswers = new FakeUserAnswers(cd, "fair_rent", "", "", housingBenefitSubcategory, propertyAddress, tellUs)

        navigator.nextPage(CheckYourAnswersId, NormalMode).apply(userAnswers) mustBe routes.ConfirmationController.onPageLoad()
      }

      "return a function that throws a runtime exception if no property address is in the model" in {
        when(mockUserAnswers.propertyAddress) `thenReturn` None

        intercept[Exception] {
          navigator.nextPage(CheckYourAnswersId, NormalMode).apply(mockUserAnswers)
        }
      }

      "return a function that throws a runtime exception if unknown exception is thrown in confirmation routing" in {
        when(mockUserAnswers.propertyAddress) `thenReturn` None
        when(mockUserAnswers.enquiryCategory) `thenReturn` Some("council_tax")

        intercept[Exception] {
          navigator.nextPage(CheckYourAnswersId, NormalMode).apply(mockUserAnswers)
        }
      }

      "return a function that goes to the valuation for taxes page when an enquiry category for valuation for taxes has been selected" in {
        when(mockUserAnswers.enquiryCategory) `thenReturn` Some("valuations_for_tax")
        navigator.nextPage(EnquiryCategoryId, NormalMode).apply(mockUserAnswers) mustBe routes.ValuationForTaxesController.onPageLoad()
      }

      "return a function that goes to the HousingBenefitAllowances journey when an enquiry category for housing benefits has been selected" in {
        when(mockUserAnswers.enquiryCategory) `thenReturn` Some("housing_benefit")
        navigator.nextPage(EnquiryCategoryId, NormalMode).apply(mockUserAnswers) mustBe routes.JourneyController.onPageLoad(HousingBenefitAllowancesRouter.key)
      }

      "return a function that goes to the providing lettings page when an enquiry category for providing lettings has been selected" in {
        when(mockUserAnswers.enquiryCategory) `thenReturn` Some("providing_lettings")
        navigator.nextPage(EnquiryCategoryId, NormalMode).apply(mockUserAnswers) mustBe routes.ProvidingLettingsController.onPageLoad()
      }

      "return a function that goes to the council tax property empty form page when an enquiry category for council tax has been selected and council_tax_property_empty option selected" in {
        when(mockUserAnswers.councilTaxSubcategory) `thenReturn` Some("council_tax_property_empty")
        navigator.nextPage(CouncilTaxSubcategoryId, NormalMode).apply(mockUserAnswers) mustBe routes.PropertyEmptyController.onPageLoad()
      }

      "return a function that goes to the council tax property empty form page when an enquiry category for council tax has been selected and council_tax_band_for_new option selected" in {
        when(mockUserAnswers.councilTaxSubcategory) `thenReturn` Some("council_tax_band_for_new")
        navigator.nextPage(CouncilTaxSubcategoryId, NormalMode).apply(mockUserAnswers) mustBe routes.CouncilTaxBandForNewController.onPageLoad()
      }

      "return a function that goes to the council tax property wind and water page when an enquiry category for council tax has been selected and council_tax_property_poor_repair option selected" in {
        when(mockUserAnswers.councilTaxSubcategory) `thenReturn` Some("council_tax_property_poor_repair")
        navigator.nextPage(CouncilTaxSubcategoryId, NormalMode).apply(mockUserAnswers) mustBe routes.PropertyWindWaterController.onPageLoad()
      }

      "return a function that goes to the council tax property demolished page when an enquiry category for council tax has been selected and council_tax_property_demolished option selected" in {
        when(mockUserAnswers.councilTaxSubcategory) `thenReturn` Some("council_tax_property_demolished")
        navigator.nextPage(CouncilTaxSubcategoryId, NormalMode).apply(mockUserAnswers) mustBe routes.PropertyDemolishedController.onPageLoad()
      }

      "return a function that goes to the council tax annexe page when an enquiry category for council tax has been selected and council_tax_annexe option selected" in {
        when(mockUserAnswers.councilTaxSubcategory) `thenReturn` Some("council_tax_annexe")
        navigator.nextPage(CouncilTaxSubcategoryId, NormalMode).apply(mockUserAnswers) mustBe routes.CouncilTaxAnnexeController.onPageLoad()
      }

      "return a function that goes to the council tax date property changed page when an enquiry category for council tax has been selected and council_tax_business_uses option selected" in {
        when(mockUserAnswers.councilTaxSubcategory) `thenReturn` Some("council_tax_business_uses")
        navigator.nextPage(CouncilTaxSubcategoryId, NormalMode).apply(mockUserAnswers) mustBe routes.DatePropertyChangedController.onPageLoad()
      }

      "return a function that goes to the council tax area change page when an enquiry category for council tax has been selected and council_tax_area_change option selected" in {
        when(mockUserAnswers.councilTaxSubcategory) `thenReturn` Some("council_tax_area_change")
        navigator.nextPage(CouncilTaxSubcategoryId, NormalMode).apply(mockUserAnswers) mustBe routes.PropertyPermanentChangesController.onPageLoad()
      }

      "return a function that goes to the council tax area change page when an enquiry category for council tax has been selected and council_tax_other option selected" in {
        when(mockUserAnswers.councilTaxSubcategory) `thenReturn` Some("council_tax_other")
        navigator.nextPage(CouncilTaxSubcategoryId, NormalMode).apply(mockUserAnswers) mustBe routes.TellUsMoreController.onPageLoad(NormalMode)
      }

      "return a function that goes to the council tax is annexe self contained page when an annexe enquiry 'added' has been selected" in {
        when(mockUserAnswers.annexeEnquiry) `thenReturn` Some("added")
        navigator.nextPage(CouncilTaxAnnexeEnquiryId, NormalMode).apply(mockUserAnswers) mustBe routes.CouncilTaxAnnexeController.onSelfContainedEnquiryPageLoad()
      }

      "return a function that goes to the council tax is annexe removed page when an annexe enquiry 'removed' has been selected" in {
        when(mockUserAnswers.annexeEnquiry) `thenReturn` Some("removed")
        navigator.nextPage(CouncilTaxAnnexeEnquiryId, NormalMode).apply(mockUserAnswers) mustBe routes.CouncilTaxAnnexeController.onRemovedPageLoad()
      }

      "return an exception  when an annexe enquiry other has been selected" in {
        when(mockUserAnswers.annexeEnquiry) `thenReturn` Some("error")
        an[RuntimeException] should be thrownBy navigator.nextPage(CouncilTaxAnnexeEnquiryId, NormalMode).apply(mockUserAnswers)
      }

      "return a function that goes to the council tax annexe is not self contained page when an enquiry 'no' has been selected" in {
        when(mockUserAnswers.annexeSelfContainedEnquiry) `thenReturn` Some("no")
        navigator.nextPage(CouncilTaxAnnexeSelfContainedEnquiryId, NormalMode).apply(
          mockUserAnswers
        ) mustBe routes.CouncilTaxAnnexeController.onNotSelfContainedPageLoad()
      }

      "return a function that goes to the council tax annexe have cooking washing enquiry page when an enquiry 'yes' has been selected" in {
        when(mockUserAnswers.annexeSelfContainedEnquiry) `thenReturn` Some("yes")
        navigator.nextPage(CouncilTaxAnnexeSelfContainedEnquiryId, NormalMode).apply(
          mockUserAnswers
        ) mustBe routes.CouncilTaxAnnexeController.onHaveCookingWashingPageLoad()
      }

      "throw an exception when is annexe self contained selects an unexpected response" in {
        when(mockUserAnswers.annexeSelfContainedEnquiry) `thenReturn` Some("foo")
        intercept[Exception] {
          navigator.nextPage(CouncilTaxAnnexeSelfContainedEnquiryId, NormalMode).apply(mockUserAnswers)
        }
      }

      "return a function that goes to Your self-contained annexe will not affect your Council Tax band page when an enquiry 'no' has been selected" in {
        when(mockUserAnswers.annexeHaveCookingWashing) `thenReturn` Some("no")
        navigator.nextPage(CouncilTaxAnnexeHaveCookingId, NormalMode).apply(mockUserAnswers) mustBe routes.CouncilTaxAnnexeController.onFacilitiesPageLoad()
      }

      "return a function that goes to Speak to your local council about your annexe page when an enquiry 'yes' has been selected" in {
        when(mockUserAnswers.annexeHaveCookingWashing) `thenReturn` Some("yes")
        navigator.nextPage(CouncilTaxAnnexeHaveCookingId, NormalMode).apply(mockUserAnswers) mustBe routes.CouncilTaxAnnexeController.onSelfContainedPageLoad()
      }

      "throw an exception when is annexe have cooking washing selects an unexpected response" in {
        when(mockUserAnswers.annexeHaveCookingWashing) `thenReturn` Some("error")
        intercept[Exception] {
          navigator.nextPage(CouncilTaxAnnexeHaveCookingId, NormalMode).apply(mockUserAnswers)
        }
      }

    }

    "in Check mode" must {
      "go to CheckYourAnswers from an identifier that doesn't exist in the edit route map" in {
        case object UnknownIdentifier extends Identifier
        navigator.nextPage(UnknownIdentifier, CheckMode).apply(mockUserAnswers) mustBe routes.CheckYourAnswersController.onPageLoad()
      }

      "return a function that throws a runtime exception if unknown enquiry category is selected on the enquiry category page" in {
        when(mockUserAnswers.enquiryCategory) `thenReturn` Some("other_selection")
        intercept[Exception] {
          navigator.nextPage(EnquiryCategoryId, NormalMode).apply(mockUserAnswers)
        }
      }

      "return a function that goes to the business rates subcategory page when an enquiry category has been selected and the selection is business_rates" in {
        when(mockUserAnswers.enquiryCategory) `thenReturn` Some("business_rates")
        navigator.nextPage(BusinessRatesSmartLinksId, NormalMode).apply(mockUserAnswers) mustBe routes.BusinessRatesSubcategoryController.onPageLoad(NormalMode)
      }

      "return a function that goes to the council tax bill form page when an enquiry category for council tax has been selected and council_tax_band_too_high option selected" in {
        when(mockUserAnswers.councilTaxSubcategory) `thenReturn` Some("council_tax_band_too_high")
        navigator.nextPage(CouncilTaxSubcategoryId, NormalMode).apply(mockUserAnswers) mustBe routes.CouncilTaxBandTooHighController.onPageLoad()
      }

      "return a function that goes to the council tax bill form page when an enquiry category for council tax has been selected and council_tax_bill option selected" in {
        when(mockUserAnswers.councilTaxSubcategory) `thenReturn` Some("council_tax_bill")
        navigator.nextPage(CouncilTaxSubcategoryId, NormalMode).apply(mockUserAnswers) mustBe routes.CouncilTaxBillController.onPageLoad()
      }

      "return a function that goes to the council tax bill form page when an enquiry category for council tax has been selected and council_tax_property_split_merge option selected" in {
        when(mockUserAnswers.councilTaxSubcategory) `thenReturn` Some("council_tax_property_split_merge")
        navigator.nextPage(CouncilTaxSubcategoryId, NormalMode).apply(mockUserAnswers) mustBe routes.PropertySplitMergeController.onPageLoad()
      }

      "throw exception when an enquiry category for council tax has been selected and not other options was selected on next page" in {
        when(mockUserAnswers.councilTaxSubcategory) `thenReturn` None
        an[RuntimeException] should be thrownBy navigator.nextPage(CouncilTaxSubcategoryId, NormalMode).apply(mockUserAnswers)
      }

      "return an exception when the enquiryDate return error " in {
        when(mockUserAnswers.enquiryDate) `thenReturn` Some("error")
        an[RuntimeException] should be thrownBy navigator.nextPage(EnquiryDateId, NormalMode).apply(mockUserAnswers)
      }

      "return function that goes on Property Address page when he want update exiting enquiry" in {
        when(mockUserAnswers.contactReason) `thenReturn` Some("update_existing")
        navigator.nextPage(ContactDetailsId, NormalMode).apply(mockUserAnswers) mustBe routes.PropertyAddressController.onPageLoad(NormalMode)
      }

      "return function that goes on Property Address page when he want update exiting enquiry with council_tax" in {
        when(mockUserAnswers.contactReason) `thenReturn` Some("_")
        when(mockUserAnswers.enquiryCategory) `thenReturn` Some("council_tax")
        navigator.nextPage(ContactDetailsId, NormalMode).apply(mockUserAnswers) mustBe routes.PropertyAddressController.onPageLoad(NormalMode)
      }

      "return function that goes on Property Address page when he want update exiting enquiry with business_rates" in {
        when(mockUserAnswers.contactReason) `thenReturn` Some("_")
        when(mockUserAnswers.enquiryCategory) `thenReturn` Some("business_rates")
        navigator.nextPage(ContactDetailsId, NormalMode).apply(mockUserAnswers) mustBe routes.PropertyAddressController.onPageLoad(NormalMode)
      }

      "return function that goes to Property Address from Contact Details page when enquiryCategory is housing_benefit" in {
        when(mockUserAnswers.enquiryCategory) `thenReturn` Some("housing_benefit")
        navigator.nextPage(ContactDetailsId, NormalMode).apply(mockUserAnswers) mustBe routes.PropertyAddressController.onPageLoad(NormalMode)
      }

      "return function that goes on Property Address page when he want update exiting enquiry with business_rates1" in {
        when(mockUserAnswers.contactReason) `thenReturn` Some("_")
        when(mockUserAnswers.enquiryCategory) `thenReturn` Some("fair_rent")
        navigator.nextPage(ContactDetailsId, NormalMode).apply(mockUserAnswers) mustBe routes.PropertyAddressController.onPageLoad(NormalMode)
      }

      "return function that goes on Property Address page when he want update exiting enquiry with something else" in {
        when(mockUserAnswers.contactReason) `thenReturn` Some("_")
        when(mockUserAnswers.enquiryCategory) `thenReturn` Some("_")
        an[RuntimeException] should be thrownBy navigator.nextPage(ContactDetailsId, NormalMode).apply(mockUserAnswers)
      }

      "return a exception when enquiryCategory and contactReason return None" in {
        when(mockUserAnswers.contactReason) `thenReturn` None
        when(mockUserAnswers.enquiryCategory) `thenReturn` None
        an[RuntimeException] should be thrownBy navigator.nextPage(ContactDetailsId, NormalMode).apply(mockUserAnswers)
      }

      "return a exception when enquiryCategory return None" in {
        when(mockUserAnswers.enquiryCategory) `thenReturn` None
        an[RuntimeException] should be thrownBy navigator.nextPage(EnquiryCategoryId, NormalMode).apply(mockUserAnswers)
      }

      "return function that goes on a small part of the property used for business page when councilTaxBusinessEnquiry is small_property" in {
        when(mockUserAnswers.councilTaxBusinessEnquiry) `thenReturn` Some("small_property")
        when(mockUserAnswers.businessRatesSubcategory) `thenReturn` None
        navigator.nextPage(CouncilTaxBusinessEnquiryId, NormalMode).apply(mockUserAnswers) mustBe routes.CouncilTaxBusinessController.onSmallPartUsedPageLoad()
      }

      "return function that goes on a small part of the property used for business page when councilTaxBusinessEnquiry is large_property" in {
        when(mockUserAnswers.councilTaxBusinessEnquiry) `thenReturn` Some("large_property")
        navigator.nextPage(CouncilTaxBusinessEnquiryId, NormalMode).apply(mockUserAnswers) mustBe routes.DatePropertyChangedController.onPageLoad()
      }

      "return function that goes on a small part of the property used for business page when councilTaxBusinessEnquiry is all_property" in {
        when(mockUserAnswers.councilTaxBusinessEnquiry) `thenReturn` Some("all_property")
        navigator.nextPage(CouncilTaxBusinessEnquiryId, NormalMode).apply(mockUserAnswers) mustBe routes.DatePropertyChangedController.onPageLoad()
      }

      "return a exception when councilTaxBusinessEnquiry returns None" in {
        when(mockUserAnswers.councilTaxBusinessEnquiry) `thenReturn` None
        an[RuntimeException] should be thrownBy navigator.nextPage(CouncilTaxBusinessEnquiryId, NormalMode).apply(mockUserAnswers)
      }

      "return function that goes on 140 days lets page when businessRatesSelfCateringEnquiry is england" in {
        when(mockUserAnswers.businessRatesSelfCateringEnquiry) `thenReturn` Some("england")
        navigator.nextPage(BusinessRatesSelfCateringId, NormalMode).apply(mockUserAnswers) mustBe routes.PropertyEnglandAvailableLetsController.onPageLoad()
      }

      "return function that goes on 140 days lets page when businessRatesSelfCateringEnquiry is wales" in {
        when(mockUserAnswers.businessRatesSelfCateringEnquiry) `thenReturn` Some("wales")
        navigator.nextPage(BusinessRatesSelfCateringId, NormalMode).apply(mockUserAnswers) mustBe routes.PropertyWalesAvailableLetsController.onPageLoad()
      }

      "return function that goes on tell us about your property when yes to lets in England that are let for at least 140 days" in {
        when(mockUserAnswers.propertyEnglandAvailableLetsEnquiry) `thenReturn` Some("yes")
        navigator.nextPage(PropertyEnglandAvailableLetsId, NormalMode).apply(mockUserAnswers) mustBe routes.PropertyEnglandActualLetsController.onPageLoad()
      }

      "return function that goes on tell us about your property when no to lets in England that are let for at least 140 days" in {
        when(mockUserAnswers.propertyEnglandAvailableLetsEnquiry) `thenReturn` Some("no")
        navigator.nextPage(PropertyEnglandAvailableLetsId, NormalMode).apply(mockUserAnswers) mustBe routes.PropertyEnglandLetsNoActionController.onPageLoad()
      }

      "return a exception when propertyEnglandLets140DaysEnquiry returns None" in {
        when(mockUserAnswers.propertyEnglandAvailableLetsEnquiry) `thenReturn` None
        an[RuntimeException] should be thrownBy navigator.nextPage(PropertyEnglandAvailableLetsId, NormalMode).apply(mockUserAnswers)
      }

      "return a exception when businessRatesSelfCateringEnquiry returns None" in {
        when(mockUserAnswers.businessRatesSelfCateringEnquiry) `thenReturn` None
        an[RuntimeException] should be thrownBy navigator.nextPage(BusinessRatesSelfCateringId, NormalMode).apply(mockUserAnswers)
      }

      "return function that goes on tell us about your property when yes to lets in Wales that are let for at least 140 days" in {
        when(mockUserAnswers.propertyWalesAvailableLetsEnquiry) `thenReturn` Some("yes")
        navigator.nextPage(PropertyWalesAvailableLetsId, NormalMode).apply(mockUserAnswers) mustBe routes.PropertyWalesActualLetsController.onPageLoad()
      }

      "return function that goes on tell us about your property when no to lets in Wales that are let for at least 140 days" in {
        when(mockUserAnswers.propertyWalesAvailableLetsEnquiry) `thenReturn` Some("no")
        navigator.nextPage(PropertyWalesAvailableLetsId, NormalMode).apply(mockUserAnswers) mustBe routes.PropertyWalesLetsNoActionController.onPageLoad()
      }

      "return a exception when propertyWalesLets140DaysEnquiry returns None" in {
        when(mockUserAnswers.propertyWalesAvailableLetsEnquiry) `thenReturn` None
        an[RuntimeException] should be thrownBy navigator.nextPage(PropertyWalesAvailableLetsId, NormalMode).apply(mockUserAnswers)
      }

      "return function that goes on tell us about your property when yes to lets in Wales that are let for at least 70 days" in {
        when(mockUserAnswers.propertyWalesActualLetsEnquiry) `thenReturn` Some("yes")
        navigator.nextPage(PropertyWalesActualLetsId, NormalMode).apply(mockUserAnswers) mustBe routes.BusinessRatesSelfCateringController.onWalLetsPageLoad()
      }

      "return function that goes on tell us about your property when no to lets in Wales that are let for at least 70 days" in {
        when(mockUserAnswers.propertyWalesActualLetsEnquiry) `thenReturn` Some("no")
        navigator.nextPage(PropertyWalesActualLetsId, NormalMode).apply(mockUserAnswers) mustBe routes.PropertyWalesLetsNoActionController.onPageLoad()
      }

      "return a exception when propertyWalesLets70DaysEnquiry returns None" in {
        when(mockUserAnswers.propertyWalesActualLetsEnquiry) `thenReturn` None
        an[RuntimeException] should be thrownBy navigator.nextPage(PropertyWalesActualLetsId, NormalMode).apply(mockUserAnswers)
      }

      "return function that goes on the non-business page when businessRatesPropertyEnquiry is england" in {
        when(mockUserAnswers.businessRatesPropertyEnquiry) `thenReturn` Some("england")
        navigator.nextPage(BusinessRatesPropertyEnquiryId, NormalMode).apply(
          mockUserAnswers
        ) mustBe routes.BusinessRatesPropertyController.onNonBusinessPageLoad()
      }

      "return function that goes on the non-business page when businessRatesPropertyEnquiry is wales" in {
        when(mockUserAnswers.businessRatesPropertyEnquiry) `thenReturn` Some("wales")
        navigator.nextPage(BusinessRatesPropertyEnquiryId, NormalMode).apply(mockUserAnswers) mustBe routes.DatePropertyChangedController.onPageLoad()
      }

      "return a exception when businessRatesPropertyEnquiry returns None" in {
        when(mockUserAnswers.businessRatesPropertyEnquiry) `thenReturn` None
        an[RuntimeException] should be thrownBy navigator.nextPage(BusinessRatesPropertyEnquiryId, NormalMode).apply(mockUserAnswers)
      }

      "return a function that goes to the contact details when an enquiry category for business rates has been selected and business_rates_not_used option selected" in {
        when(mockUserAnswers.businessRatesSubcategory) `thenReturn` Some("business_rates_not_used")
        navigator.nextPage(TellUsMoreId, NormalMode).apply(mockUserAnswers) mustBe routes.ContactDetailsController.onPageLoad(NormalMode)
      }

      "return a function that goes to the contact details when an enquiry category for business rates has been selected and business_rates_from_home option selected" in {
        when(mockUserAnswers.businessRatesSubcategory) `thenReturn` Some("business_rates_from_home")
        navigator.nextPage(TellUsMoreId, NormalMode).apply(mockUserAnswers) mustBe routes.ContactDetailsController.onPageLoad(NormalMode)
      }

      "return a function that goes to the contact details when an enquiry category for business rates has been selected and business_rates_bill option selected" in {
        when(mockUserAnswers.businessRatesSubcategory) `thenReturn` Some("business_rates_bill")
        navigator.nextPage(TellUsMoreId, NormalMode).apply(mockUserAnswers) mustBe routes.ContactDetailsController.onPageLoad(NormalMode)
      }

      "return a function that goes to the contact details when an enquiry category for business rates has been selected and business_rates_changes option selected" in {
        when(mockUserAnswers.businessRatesSubcategory) `thenReturn` Some("business_rates_changes")
        navigator.nextPage(TellUsMoreId, NormalMode).apply(mockUserAnswers) mustBe routes.ContactDetailsController.onPageLoad(NormalMode)
      }

      "return a function that goes to the contact details when an enquiry category for business rates has been selected and business_rates_change_valuation option selected" in {
        when(mockUserAnswers.businessRatesSubcategory) `thenReturn` Some("business_rates_change_valuation")
        navigator.nextPage(TellUsMoreId, NormalMode).apply(mockUserAnswers) mustBe routes.ContactDetailsController.onPageLoad(NormalMode)
      }

      "return a function that goes to the contact details when an enquiry category for business rates has been selected and business_rates_property_empty option selected" in {
        when(mockUserAnswers.businessRatesSubcategory) `thenReturn` Some("business_rates_property_empty")
        navigator.nextPage(TellUsMoreId, NormalMode).apply(mockUserAnswers) mustBe routes.ContactDetailsController.onPageLoad(NormalMode)
      }

      "return a function that goes to the contact details when an enquiry category for business rates has been selected and business_rates_self_catering option selected" in {
        when(mockUserAnswers.businessRatesSubcategory) `thenReturn` Some("business_rates_self_catering")
        navigator.nextPage(TellUsMoreId, NormalMode).apply(mockUserAnswers) mustBe routes.ContactDetailsController.onPageLoad(NormalMode)
      }

      "return a function that goes to the contact details when an enquiry category for business rates has been selected and business_rates_valuation option selected" in {
        when(mockUserAnswers.businessRatesSubcategory) `thenReturn` Some("business_rates_valuation")
        navigator.nextPage(TellUsMoreId, NormalMode).apply(mockUserAnswers) mustBe routes.ContactDetailsController.onPageLoad(NormalMode)
      }

      "return a function that goes to the contact details when an enquiry category for business rates has been selected and business_rates_demolished option selected" in {
        when(mockUserAnswers.businessRatesSubcategory) `thenReturn` Some("business_rates_demolished")
        navigator.nextPage(TellUsMoreId, NormalMode).apply(mockUserAnswers) mustBe routes.ContactDetailsController.onPageLoad(NormalMode)
      }

      "return a function that goes to the contact details when an enquiry category for business rates has been selected and business_rates_other option selected" in {
        when(mockUserAnswers.businessRatesSubcategory) `thenReturn` Some("business_rates_other")
        navigator.nextPage(TellUsMoreId, NormalMode).apply(mockUserAnswers) mustBe routes.ContactDetailsController.onPageLoad(NormalMode)
      }

    }
  }
}

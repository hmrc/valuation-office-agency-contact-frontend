/*
 * Copyright 2022 HM Revenue & Customs
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

package uk.gov.hmrc.valuationofficeagencycontactfrontend.models

import play.api.i18n.DefaultMessagesApi
import uk.gov.hmrc.valuationofficeagencycontactfrontend.SpecBase

class ContactWithEnMessagesSpec extends SpecBase {

  private val contactDetails = ContactDetails("first", "email", "contactNumber")
  private val propertyAddress = PropertyAddress("a", Some("b"), "c", Some("d"), "e")
  private val contact = Contact(contactDetails, propertyAddress, "council_tax", "council_tax_band", "msg")
  private val userAnswers = new FakeUserAnswers(contactDetails, "council_tax", "council_tax", "", "", propertyAddress,
    TellUsMore("message"), cr = Some("more_details"))

  def mockMsgApi(messages: Map[String, Map[String, String]]): DefaultMessagesApi =
    new DefaultMessagesApi(messages = messages)

  "return a ContactWithEnMessages when given a contact with proper keys for the enquiryCategory and subEnquiryCategory" in {
    val msgApi = mockMsgApi(Map("en" -> Map("enquiryCategory.council_tax" -> "CT", "councilTaxSubcategory.council_tax_band" -> "TB")))
    val result = ContactWithEnMessage(contact, msgApi, userAnswers)
    result.enquiryCategoryMsg mustBe "CT"
    result.isCouncilTaxEnquiry mustBe true
    result.contactReason mustBe userAnswers.contactReason
    result.subEnquiryCategoryMsg mustBe "TB"
  }

  "return a ContactWithEnMessages when given a contact with proper keys for the existingEnquiryCategory and subEnquiryCategory" in {
    val msgApi = mockMsgApi(Map("en" -> Map("enquiryCategory.council_tax" -> "CT",
      "councilTaxSubcategory.council_tax_band" -> "TB",
      "existing.enquiry" -> "Existing Enquiry")))
    val userAnswers = new FakeUserAnswers(contactDetails, "", "council_tax", "", "", propertyAddress, TellUsMore("message"), ee = Some("council_tax"),
      cr = Some("more_details"))
    val result = ContactWithEnMessage(contact, msgApi, userAnswers)
    result.enquiryCategoryMsg mustBe "CT"
    result.isCouncilTaxEnquiry mustBe true
    result.contactReason mustBe userAnswers.contactReason
    result.subEnquiryCategoryMsg mustBe "Existing Enquiry"
  }

  "throw an exception if the english version of messages is not available" in {
    val msgApi = mockMsgApi(Map("fr" -> Map("enquiryCategory.council_tax" -> "CT", "councilTaxSubcategory.council_tax_band" -> "TB")))
    intercept[Exception] {
      ContactWithEnMessage(contact, msgApi, userAnswers)
    }
  }

  "throw an exception if the contact contains an enquiry key that is not business_rates or council_tax" in {
    val contact = Contact(contactDetails, propertyAddress, "wibble", "council_tax_band", "msg")
    val msgApi = mockMsgApi(Map("en" -> Map("councilTaxSubcategory.council_tax_band" -> "TB")))
    intercept[Exception] {
      ContactWithEnMessage(contact, msgApi, userAnswers)
    }
  }

  "throw an exception if the english version of messages does not contain a value for the enquiry category message key" in {
    val msgApi = mockMsgApi(Map("en" -> Map("councilTaxSubcategory.council_tax_band" -> "TB")))
    intercept[Exception] {
      ContactWithEnMessage(contact, msgApi, userAnswers)
    }
  }

  "throw an exception if the english version of the messages does not contain a value for the sub enquiry category key" in {
    val msgApi = mockMsgApi(Map("fr" -> Map("enquiryCategory.council_tax" -> "CT")))
    intercept[Exception] {
      ContactWithEnMessage(contact, msgApi, userAnswers)
    }
  }

}

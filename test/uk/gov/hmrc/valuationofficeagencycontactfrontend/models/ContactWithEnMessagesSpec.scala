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

package uk.gov.hmrc.valuationofficeagencycontactfrontend.models

import org.scalatestplus.mockito.MockitoSugar
import play.api.i18n.MessagesApi
import uk.gov.hmrc.valuationofficeagencycontactfrontend.SpecBase
import org.mockito.Mockito.when

class ContactWithEnMessagesSpec extends SpecBase with MockitoSugar {
  val mockMessages = mock[MessagesApi]
  val contactDetails = ContactDetails("first", "email", "contactNumber")
  val propertyAddress = PropertyAddress("a", Some("b"), "c", Some("d"), "e")
  val contact = Contact(contactDetails, propertyAddress, "council_tax", "council_tax_band", "msg")

  "return a ContactWithEnMessages when given a contact with proper keys for the enquiryCategory and subEnquiryCategory" in {
    when(mockMessages.messages) thenReturn Map("en" -> Map("enquiryCategory.council_tax" -> "CT", "councilTaxSubcategory.council_tax_band" -> "TB"))
    val result = ContactWithEnMessage(contact, mockMessages)
    result.enquiryCategoryMsg mustBe "CT"
    result.isCouncilTaxEnquiry mustBe true
    result.subEnquiryCategoryMsg mustBe "TB"
  }

  "throw an exception if the english version of messages is not available" in {
    when(mockMessages.messages) thenReturn Map("fr" -> Map("enquiryCategory.council_tax" -> "CT", "councilTaxSubcategory.council_tax_band" -> "TB"))
    intercept[Exception] {
      ContactWithEnMessage(contact, mockMessages)
    }
  }

  "throw an exception if the contact contains an enquiry key that is not business_rates or council_tax" in {
    val contact = Contact(contactDetails, propertyAddress, "wibble", "council_tax_band", "msg")
    when(mockMessages.messages) thenReturn Map("en" -> Map("councilTaxSubcategory.council_tax_band" -> "TB"))
    intercept[Exception] {
      ContactWithEnMessage(contact, mockMessages)
    }
  }

  "throw an exception if the english version of messages does not contain a value for the enquiry category message key" in {
    when(mockMessages.messages) thenReturn Map("en" -> Map("councilTaxSubcategory.council_tax_band" -> "TB"))
    intercept[Exception] {
      ContactWithEnMessage(contact, mockMessages)
    }
  }

  "throw an exception if the english version of the messages does not contain a value for the sub enquiry category key" in {
    when(mockMessages.messages) thenReturn Map("fr" -> Map("enquiryCategory.council_tax" -> "CT"))
    intercept[Exception] {
      ContactWithEnMessage(contact, mockMessages)
    }
  }
}

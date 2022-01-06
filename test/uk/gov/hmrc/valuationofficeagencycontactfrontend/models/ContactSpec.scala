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

import uk.gov.hmrc.valuationofficeagencycontactfrontend.SpecBase

class ContactSpec extends SpecBase {
  val message = "message"
  val enquiryCategory = "EC"
  val subEnquiryCategory = "SEC"
  val contactDetails = ContactDetails("first", "email", "contactNumber")

  "Given a message, enquiryCategory and subEnquiryCategory strings, contact details and a property address produce a " +
    "contact model with the property address" in {
    val propertyAddress = PropertyAddress("a", Some("b"), "c", Some("d"), "e")
    val result = Contact(message, enquiryCategory, subEnquiryCategory, contactDetails, propertyAddress)

    result.propertyAddress mustBe propertyAddress
    result.message mustBe message
    result.enquiryCategory mustBe enquiryCategory
    result.subEnquiryCategory mustBe subEnquiryCategory
    result.contact mustBe contactDetails
  }

  "contact model with the property address containing optional fields as None" in {
    val propertyAddress = PropertyAddress("a", None, "c", None, "e")
    val result = Contact(message, enquiryCategory, subEnquiryCategory, contactDetails, propertyAddress)

    result.propertyAddress mustBe propertyAddress
    result.message mustBe message
    result.enquiryCategory mustBe enquiryCategory
    result.subEnquiryCategory mustBe subEnquiryCategory
    result.contact mustBe contactDetails
  }
}


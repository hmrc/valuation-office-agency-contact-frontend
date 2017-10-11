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

package uk.gov.hmrc.valuationofficeagencycontactfrontend.models

import play.api.libs.json.Json
import uk.gov.hmrc.valuationofficeagencycontactfrontend.SpecBase

class ContactSpec extends SpecBase {
  val message = "message"
  val enquiryCategory = "EC"
  val subEnquiryCategory = "SEC"
  val contactDetails = ContactDetails("first", "last", "email", "email", "tel", "mob", "pref")
  val confirmedContactDetails = ConfirmedContactDetails(contactDetails)

  "Given a message, enquiryCategory and subEnquiryCategory strings, contact details and a council tax address produce a " +
    "contact model with the council tax address and the business rates set to None" in {
    val councilTaxAddress = CouncilTaxAddress("a", "b", "c", "d", "e")
    val result = Contact(message, enquiryCategory, subEnquiryCategory, contactDetails, councilTaxAddress)

    result.businessRatesAddress mustBe None
    result.councilTaxAddress mustBe Some(councilTaxAddress)
    result.message mustBe message
    result.enquiryCategory mustBe enquiryCategory
    result.subEnquiryCategory mustBe subEnquiryCategory
    result.contact mustBe confirmedContactDetails
  }

  "Given a message, enquiryCategory and subEnquiryCategory strings, contact details and a business rates address produce a " +
    "contact model with the council tax address and the business rates set to None" in {
    val businessRatesAddress = BusinessRatesAddress("a", "b", "c", "d", "e", "f", "g")
    val result = Contact(message, enquiryCategory, subEnquiryCategory, contactDetails, businessRatesAddress)

    result.businessRatesAddress mustBe Some(businessRatesAddress)
    result.councilTaxAddress mustBe None
    result.message mustBe message
    result.enquiryCategory mustBe enquiryCategory
    result.subEnquiryCategory mustBe subEnquiryCategory
    result.contact mustBe confirmedContactDetails
  }
}

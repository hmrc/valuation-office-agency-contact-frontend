/*
 * Copyright 2026 HM Revenue & Customs
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

package uk.gov.hmrc.vo.contact.frontend.models.event

import uk.gov.hmrc.vo.contact.frontend.models.{ContactDetails, PropertyAddress}

/**
  * @author Yuriy Tumakha
  */
class SendEnquiryToVOASpec extends AuditEventTest:

  "SendEnquiryToVOA" should {
    "serialize to expected JSON" in {
      val event = SendEnquiryToVOA(
        contact = ContactDetails("First Last", "user@email.com", "1234567890"),
        propertyAddress = PropertyAddress("address line 1", Some("address line 2"), "Town", Some("County"), "postcode"),
        isCouncilTaxEnquiry = true,
        contactReason = Some("Some contact reason"),
        enquiryCategoryMsg = "Council Tax",
        subEnquiryCategoryMsg = "My property has been partially or fully demolished",
        message = "Message text",
        enquiryBeforeLast28days = Some("no"),
        refNumber = Some("VOR12345678")
      )

      verifyEventJson(event, "SendEnquiryToVOA", "SendEnquiryToVOA.json")
    }
  }

  "SendEnquiryToVOA with minimal data" should {
    "serialize to expected JSON" in {
      val event = SendEnquiryToVOA(
        contact = ContactDetails("First Last", "user@email.com", "1234567890"),
        propertyAddress = PropertyAddress("address line 1", None, "Town", None, "postcode"),
        isCouncilTaxEnquiry = true,
        contactReason = None,
        enquiryCategoryMsg = "Council Tax",
        subEnquiryCategoryMsg = "My property has been partially or fully demolished",
        message = "Message text",
        enquiryBeforeLast28days = None,
        refNumber = None
      )

      verifyEventJson(event, "SendEnquiryToVOA", "SendEnquiryToVOA_minimal.json")
    }
  }

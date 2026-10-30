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

import uk.gov.hmrc.vo.contact.frontend.models.ContactDetails

/**
  * @author Yuriy Tumakha
  */
class UserTimeoutSpec extends AuditEventTest:

  "UserTimeout" should {
    "serialize to expected JSON" in {
      val event = UserTimeout(
        refNumber = Some("CHK76543210"),
        contact = Some(ContactDetails("First Last", "user@email.com", "1234567890"))
      )

      verifyEventJson(event, "UserTimeout", "UserTimeout.json")
    }
  }

  "UserTimeout with minimal data" should {
    "serialize to expected JSON" in {
      val event = UserTimeout(
        refNumber = None,
        contact = None
      )

      verifyEventJson(event, "UserTimeout", "UserTimeout_minimal.json")
    }
  }

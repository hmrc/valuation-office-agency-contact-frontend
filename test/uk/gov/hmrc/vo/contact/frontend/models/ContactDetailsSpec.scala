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

package uk.gov.hmrc.vo.contact.frontend.models

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

import org.scalatest.wordspec.AnyWordSpec
import uk.gov.hmrc.vo.contact.frontend.models.ContactDetails

class ContactDetailsSpec extends AnyWordSpec:

  val contact: ContactDetails = ContactDetails("Alex", "test@email.com", "02078273278732")

  "Full name " should {
    "be Alex" in
      assert(contact.fullName === "Alex")
  }

  "Email address " should {
    "be test@email.com" in
      assert(contact.email === "test@email.com")
  }

  "Contact number " should {
    "be 02078273278732" in
      assert(contact.contactNumber === "02078273278732")
  }

  "Wrong First name " should {
    "shouldn't be Alex1" in
      assert(contact.fullName != "Alex1")
  }

  "Wrong Contact number " should {
    "shouldn't be 02078273278735" in
      assert(contact.contactNumber != "02078273278735")
  }

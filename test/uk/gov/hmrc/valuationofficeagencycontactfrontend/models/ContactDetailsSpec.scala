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

import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.ContactDetails
import org.scalatest.FlatSpec

class ContactDetailsSpec extends FlatSpec {

  val c = ContactDetails("Alex", "Smith", "07727823456", "test@email.com", "phone", "Just a message")

  "First name " should "be Alex" in {
    assert(c.firstName === "Alex")
  }

  "Last name " should "be Smith" in {
    assert(c.lastName === "Smith")
  }

  "Telephone number " should "be 07727823456" in {
    assert(c.telephoneNumber === "07727823456")
  }

  "Email address " should "be test@email.com" in {
    assert(c.email === "test@email.com")
  }

  "Contact Preference " should "be phone" in {
    assert(c.contactPreference === "phone")
  }

  "Message should be " should "be Just a message" in {
    assert(c.message === "Just a message")
  }

  "Wrong First name " should "shouldn't be Alex1" in {
    assert(c.firstName != "Alex1")
  }

  "Wrong Last name " should "shouldn't be Smith1" in {
    assert(c.lastName != "Smith1")
  }

  "Wrong Telephone number " should "shouldn't be 07727823457" in {
    assert(c.telephoneNumber != "07727823457")
  }

  "Wrong Email address " should "shouldn't be test@email.com1" in {
    assert(c.email != "test@email.com1")
  }

  "Wrong Contact Preference " should "shouldn't be phone1" in {
    assert(c.contactPreference != "phone1")
  }

  "Wrong Message should be " should "shouldn't be Just a message1" in {
    assert(c.message != "Just a message1")
  }


}
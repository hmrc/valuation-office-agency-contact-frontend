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

package uk.gov.hmrc.valuationofficeagencycontactfrontend.forms

import play.api.data.FormError
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.behaviours.FormBehaviours
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.ContactDetails

class ContactDetailsFormSpec extends FormBehaviours {

  val validData: Map[String, String] = Map(
    "firstName" -> "Alex",
    "lastName" -> "Smith",
    "email" -> "a@a",
    "confirmEmail" -> "a@a",
    "contactNumber" -> "1234567890"
  )

  val form = ContactDetailsForm()

  "ContactDetails form" must {
    behave like questionForm(ContactDetails("Alex", "Smith", "a@a", "a@a", "1234567890"))

    "fail to bind when first name is blank" in {
      val data = validData + ("firstName" -> "")
      val expectedError = Seq(error("firstName", "error.required"), error("firstName", "error.name.invalid")).flatMap(e => e)
      checkForError(form, data, expectedError)
    }

    s"fail to bind when first name is omitted" in {
      val data = validData - "firstName"
      val expectedError = error("firstName", "error.required")
      checkForError(form, data, expectedError)
    }

    s"fail to bind when first name is invalid" in {
      val data = validData + ("firstName" -> "asdsa424")
      val expectedError = error("firstName", "error.name.invalid")
      checkForError(form, data, expectedError)
    }

    s"fail to bind when first name length is more than 56" in {
      val data = validData + ("firstName" -> "abcdefghijklmnoqprstasdsjjdsfjdsjkfsdjfdsjfjksfkjsjkfdjksfjkjkfdjkfdsjkfjkdsfdjksjfds")
      val expectedError = error("firstName", "error.name.max_length")
      checkForError(form, data, expectedError)
    }

    "fail to bind when surname is blank" in {
      val data = validData + ("lastName" -> "")
      val expectedError = Seq(error("lastName", "error.required"), error("lastName", "error.name.invalid")).flatMap(e => e)
      checkForError(form, data, expectedError)
    }

    s"fail to bind when surname is omitted" in {
      val data = validData - "lastName"
      val expectedError = error("lastName", "error.required")
      checkForError(form, data, expectedError)
    }

    s"fail to bind when surname is invalid" in {
      val data = validData + ("lastName" -> "asdsa424")
      val expectedError = error("lastName", "error.name.invalid")
      checkForError(form, data, expectedError)
    }

    s"fail to bind when surname length is more than 56" in {
      val data = validData + ("lastName" -> "abcdefghijklmnoqprstasdsjjdsfjdsjkfsdjfdsjfjksfkjsjkfdjksfjkjkfdjkfdsjkfjkdsfdjksjfds")
      val expectedError = error("lastName", "error.name.max_length")
      checkForError(form, data, expectedError)
    }

    "fail to bind when email is blank" in {
      val data = validData + ("email" -> "") + ("confirmEmail" -> "")
      val expectedError = error("email", "error.email")
      checkForError(form, data, expectedError)
    }

    "fail to bind when email is invalid" in {
      val data = validData + ("email" -> "a.test.com") + ("confirmEmail" -> "a.test.com")
      val expectedError = error("email", "error.email")
      checkForError(form, data, expectedError)
    }

    "fail to bind when email length is more than 129" in {
      val data = validData + ("email" -> "baaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa@testaaaaaaaaaaaaaaaaaaaa.com") +
        ("confirmEmail" -> "baaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa@testaaaaaaaaaaaaaaaaaaaa.com")
      val expectedError = error("email", "error.email.max_length")
      checkForError(form, data, expectedError)
    }

    "fail to bind when emails don't match" in {
      val data = validData + ("confirmEmail" -> "ab@test.com")
      val expectedError = error("confirmEmail", "error.email.mismatch")
      checkForError(form, data, expectedError)
    }

    "fail to bind when emails don't match and second email is blank" in {
      val data = validData + ("confirmEmail" -> "")
      val expectedError = error("confirmEmail", "error.email.mismatch")
      checkForError(form, data, expectedError)
    }

    "EmailConstraint bind method should return Left(error.email.unmatched) if the emails don't match" in {
      val data = validData + ("confirmEmail" -> "ab@test.com")
      val result = EmailConstraint.bind("confirmEmail", data)
      result shouldBe Left(List(FormError("confirmEmail", "error.email.mismatch")))
    }

    "EmailConstraint bind method should return Right(email) if the emails are valid and match" in {
      val result = EmailConstraint.bind("confirmEmail", validData)
      result shouldBe Right(validData.getOrElse("email", ""))
    }

    "fail to bind when contact number is blank" in {
      val data = validData + ("contactNumber" -> "")
      val expectedError = Seq(error("contactNumber", "error.required"), error("contactNumber", "error.phone.invalid")).flatMap(e => e)
      checkForError(form, data, expectedError)
    }

    s"fail to bind when contact number is omitted" in {
      val data = validData - "contactNumber"
      val expectedError = error("contactNumber", "error.required")
      checkForError(form, data, expectedError)
    }

    s"fail to bind when contact number is invalid" in {
      val data = validData + ("contactNumber" -> "asdsa2332323232")
      val expectedError = error("contactNumber", "error.phone.invalid")
      checkForError(form, data, expectedError)
    }

    s"fail to bind when contact number length is more than 24" in {
      val data = validData + ("contactNumber" -> "1234567890123456789012345")
      val expectedError = error("contactNumber", "error.phone.max_length")
      checkForError(form, data, expectedError)
    }
  }
}

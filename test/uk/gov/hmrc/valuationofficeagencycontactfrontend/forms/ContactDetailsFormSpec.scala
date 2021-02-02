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

package uk.gov.hmrc.valuationofficeagencycontactfrontend.forms

import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.behaviours.FormBehaviours
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.ContactDetails

class ContactDetailsFormSpec extends FormBehaviours {

  val validData: Map[String, String] = Map(
    "fullName" -> "Alex",
    "email" -> "a@a",
    "contactNumber" -> "12345678901"
  )

  val form = ContactDetailsForm()

  "ContactDetails form" must {
    behave like questionForm(ContactDetails("Alex", "a@a", "12345678901"))

    "fail to bind when email is blank" in {
      val data = validData + ("email" -> "")
      val expectedError = Seq(error("email", "contactDetails.email.required"),
        error("email", "contactDetails.email.invalid")).flatten
      checkForError(form, data, expectedError)
    }

    "fail to bind when email is invalid" in {
      val data = validData + ("email" -> "a.test.com")
      val expectedError = error("email", "contactDetails.email.invalid")
      checkForError(form, data, expectedError)
    }

    s"fail to bind when email is omitted" in {
      val data = validData - "email"
      val expectedError = error("email", "error.required")
      checkForError(form, data, expectedError)
    }

    "fail to bind when contact number is blank" in {
      val data = validData + ("contactNumber" -> "")
      val expectedError = Seq(error("contactNumber", "contactDetails.contactNumber.required"),
        error("contactNumber", "contactDetails.contactNumber.length"),
        error("contactNumber", "contactDetails.contactNumber.invalid")).flatten
      checkForError(form, data, expectedError)
    }

    s"fail to bind when contact number is omitted" in {
      val data = validData - "contactNumber"
      val expectedError = error("contactNumber", "error.required")
      checkForError(form, data, expectedError)
    }

    s"fail to bind when contact number is invalid" in {
      val data = validData + ("contactNumber" -> "asdsa2332323232")
      val expectedError = error("contactNumber", "contactDetails.contactNumber.invalid")
      checkForError(form, data, expectedError)
    }

    s"fail to bind when contact number length is more than 20" in {
      val data = validData + ("contactNumber" -> "123456789012345678901")
      val expectedError = error("contactNumber", "contactDetails.contactNumber.length")
      checkForError(form, data, expectedError)
    }

    s"fail to bind when contact number length is less than 11" in {
      val data = validData + ("contactNumber" -> "1234567890")
      val expectedError = error("contactNumber", "contactDetails.contactNumber.length")
      checkForError(form, data, expectedError)
    }

    s"fail to bind when Full Name is invalid" in {
      val data = validData + ("fullName" -> "<script>alert(\"xss\")</script>")
      val expectedError = error("fullName", "contactDetails.fullName.invalid")
      checkForError(form, data, expectedError)
    }

    "fail to bind when full name is blank" in {
      val data = validData + ("fullName" -> "")
      val expectedError = Seq(error("fullName", "contactDetails.fullName.required"),
        error("fullName", "contactDetails.fullName.invalid")).flatten
      checkForError(form, data, expectedError)
    }

    "fail to bind when full name name is omitted" in {
      val data = validData - "fullName"
      val expectedError = error("fullName", "error.required")
      checkForError(form, data, expectedError)
    }
  }
}

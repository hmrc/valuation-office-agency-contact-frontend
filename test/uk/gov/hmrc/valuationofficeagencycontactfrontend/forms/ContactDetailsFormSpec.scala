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

import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.behaviours.FormBehaviours
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.ContactDetails

class ContactDetailsFormSpec extends FormBehaviours {

  val validData: Map[String, String] = Map(
    "firstName" -> "a",
    "lastName" -> "b",
    "email" -> "a@test.com",
    "confirmEmail" -> "a@test.com",
    "contactNumber" -> "c"
  )

  val form = ContactDetailsForm()

  "ContactDetails form" must {
    behave like questionForm(ContactDetails("a", "b", "a@test.com", "a@test.com", "c"))

    behave like formWithMandatoryTextFields("firstName", "lastName", "contactNumber")

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

    "fail to bind when emails don't match" in {
      val data = validData + ("confirmEmail" -> "ab@test.com")
      val expectedError = error("confirmEmail", "error.email.unmatched")
      checkForError(form, data, expectedError)
    }

    "fail to bind when emails don't match and second email is blank" in {
      val data = validData + ("confirmEmail" -> "")
      val expectedError = error("confirmEmail", "error.email.unmatched")
      checkForError(form, data, expectedError)
    }

    "EmailConstraint bind method will return Left(error.email.unmatched) if the emails don't match" in {
      val data = validDate +
      val result = EmailConstraint.bind("confirmEmail", )
    }
  }

}

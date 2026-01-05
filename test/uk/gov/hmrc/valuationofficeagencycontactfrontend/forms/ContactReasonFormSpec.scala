/*
 * Copyright 2025 HM Revenue & Customs
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

import play.api.data.Form
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.behaviours.FormBehaviours

class ContactReasonFormSpec extends FormBehaviours {

  val validData: Map[String, String] = Map("value" -> "new_enquiry")
  val emptyData: Map[String, String] = Map.empty

  val form: Form[String] = ContactReasonForm()

  "ContactReason form" must {

    "fail to bind when value is blank" in {
      val data          = emptyData
      val expectedError = Seq(error("value", "error.contactReason.required")).flatten
      checkForError(form, data, expectedError)
    }

    "fail to bind when value has invalid value" in {
      val data = Map("value" -> "invalid_value")
      val expectedError = Seq(error("value", "error.unknown")).flatten
      checkForError(form, data, expectedError)
    }
  }
}

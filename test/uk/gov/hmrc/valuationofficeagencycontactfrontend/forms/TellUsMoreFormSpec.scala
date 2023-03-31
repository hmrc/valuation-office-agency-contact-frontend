/*
 * Copyright 2023 HM Revenue & Customs
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
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.TellUsMore

class TellUsMoreFormSpec extends FormBehaviours {

  val validData: Map[String, String] = Map(
    "message" -> "value 1"
  )

  val form = TellUsMoreForm()

  "TellUsMore form" must {
    behave like questionForm(TellUsMore("value 1"))

    s"fail to bind when message length is more than 5000" in {
      val data = validData + ("message" -> "a" * 5001)
      val expectedError = error("message", "error.message.max_length")
      checkForError(form, data, expectedError)
    }

    s"fail to bind when message is invalid" in {
      val data = validData + ("message" -> "<script>alert(\"xss\")</script>")
      val expectedError = error("message", "error.tell_us_more.invalid")
      checkForError(form, data, expectedError)
    }

    "fail to bind when message is blank" in {
      val data = validData + ("message" -> "")
      val expectedError = Seq(error("message", "error.tell_us_more.required")).flatten
      checkForError(form, data, expectedError)
    }

    "fail to bind when message is omitted" in {
      val data = validData - "message"
      val expectedError = error("message", "error.required")
      checkForError(form, data, expectedError)
    }

    "fail to bind when message is blank and is for poor repair" in {
      val form = TellUsMoreForm("error.tellUsMore.poorRepair.required")
      val data = validData + ("message" -> "")
      val expectedError = Seq(error("message", "error.tellUsMore.poorRepair.required")).flatten
      checkForError(form, data, expectedError)
    }

  }

}


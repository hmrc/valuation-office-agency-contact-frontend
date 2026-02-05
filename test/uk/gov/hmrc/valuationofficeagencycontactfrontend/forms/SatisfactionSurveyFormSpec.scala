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

package uk.gov.hmrc.valuationofficeagencycontactfrontend.forms

import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.behaviours.FormBehaviours
import play.api.data.Form

class SatisfactionSurveyFormSpec extends FormBehaviours {

  val validData: Map[String, String] = Map(
    "satisfaction" -> "verySatisfied",
    "details"      -> "value 1"
  )

  val form: Form[SatisfactionSurvey] = SatisfactionSurveyForm()

  "SatisfactionSurvey form" must {
    behave like questionForm(SatisfactionSurvey("verySatisfied", Option("value 1")))

    "fail to bind when satisfaction is blank" in {
      val data          = validData - "satisfaction"
      val expectedError = error("satisfaction", "error.required.feedback")
      checkForError(form, data, expectedError)
    }

    "fail to bind when details length is more than 1200" in {
      val data          = validData + ("details" -> "a" * 1201)
      val expectedError = error("details", "error.message.max_length.feedback")
      checkForError(form, data, expectedError)
    }

    "fail to bind when details is invalid" in {
      val data          = validData + ("details" -> "<script>alert(\"xss\")</script>")
      val expectedError = error("details", "error.message.xss-invalid.feedback")
      checkForError(form, data, expectedError)
    }
  }
}

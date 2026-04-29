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

package uk.gov.hmrc.vo.contact.frontend.forms

import play.api.data.Form
import play.api.data.Forms.*

class WithRequiredBooleanMappingSpec extends FormSpec:

  object TestForm extends WithRequiredBooleanMapping:

    val testForm: Form[Boolean] = Form(
      single(
        "value" -> requiredBoolean
      )
    )

  import TestForm.*

  "With Required Boolean Mapping" must {

    "bind true" in {
      testForm.bind(Map("value" -> "true")).get shouldBe true
    }

    "bind false" in {
      testForm.bind(Map("value" -> "false")).get shouldBe false
    }

    "fail to bind non-booleans" in {
      val expectedError = error("value", "error.boolean")
      checkForError(testForm, Map("value" -> "not a boolean"), expectedError)
    }

    "fail to bind a blank value" in {
      val expectedError = error("value", "error.boolean")
      checkForError(testForm, Map("value" -> ""), expectedError)
    }

    "fail to bind when value is omitted" in {
      val expectedError = error("value", "error.boolean")
      checkForError(testForm, emptyForm, expectedError)
    }
  }

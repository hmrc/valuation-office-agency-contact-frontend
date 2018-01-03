/*
 * Copyright 2018 HM Revenue & Customs
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

import scala.concurrent.ExecutionContext.Implicits.global

class BooleanFormSpec extends FormSpec {

  val errorKey = "error.key"

  "Boolean Form" must {

    "bind true" in {
      val form = BooleanForm(errorKey).bind(Map("value" -> "true"))
      form.get shouldBe true
    }

    "bind false" in {
      val form = BooleanForm(errorKey).bind(Map("value" -> "false"))
      form.get shouldBe false
    }

    "fail to bind non-booleans" in {
      val expectedError = error("value", errorKey)
      checkForError(BooleanForm(errorKey), Map("value" -> "not a boolean"), expectedError)
    }

    "fail to bind a blank value" in {
      val expectedError = error("value", errorKey)
      checkForError(BooleanForm(errorKey), Map("value" -> ""), expectedError)
    }

    "fail to bind when value is omitted" in {
      val expectedError = error("value", errorKey)
      checkForError(BooleanForm(errorKey), emptyForm, expectedError)
    }
  }
}

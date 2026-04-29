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

import uk.gov.hmrc.vo.contact.frontend.forms.behaviours.FormBehaviours
import play.api.data.Form

class EnquiryCategoryFormSpec extends FormBehaviours:

  val validData: Map[String, String] = Map(
    "category" -> EnquiryCategoryForm.values.head
  )

  val form: Form[String] = EnquiryCategoryForm.form

  "EnquiryCategory form" must {
    behave like questionForm[String](EnquiryCategoryForm.values.head)

    for (validValue <- EnquiryCategoryForm.values)
      s"bind when category is set to $validValue" in {
        val data      = validData + ("category" -> validValue)
        val boundForm = form.bind(data)
        boundForm.errors.isEmpty shouldBe true
      }

    "fail to bind when category is omitted" in {
      val data = validData - "category"
      checkForError(form, data, error("category", "error.enquiry.category.required"))
    }

    "fail to bind when category is invalid" in {
      val data = validData + ("category" -> "invalid value")
      checkForError(form, data, error("category", "error.value.invalid"))
    }
  }

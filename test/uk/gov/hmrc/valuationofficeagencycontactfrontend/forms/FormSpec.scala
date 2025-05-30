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

import org.scalatest.OptionValues
import play.api.data.{Form, FormError}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.Assertion

trait FormSpec extends AnyWordSpecLike with Matchers with OptionValues {

  def checkForError(form: Form[?], data: Map[String, String], expectedErrors: Seq[FormError]): Assertion =
    form.bind(data).fold(
      formWithErrors => {
        for (error <- formWithErrors.errors) expectedErrors should contain(FormError(error.key, error.message))
        formWithErrors.errors.size                        shouldBe expectedErrors.size
      },
      _ =>
        fail("Expected a validation error when binding the form, but it was bound successfully.")
    )

  def error(key: String, value: String): Seq[FormError] = Seq(FormError(key, value))

  lazy val emptyForm: Map[String, String] = Map[String, String]()

}

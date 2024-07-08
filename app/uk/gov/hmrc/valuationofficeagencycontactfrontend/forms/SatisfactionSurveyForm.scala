/*
 * Copyright 2024 HM Revenue & Customs
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

import play.api.data.{Form, FormError}
import play.api.data.Forms._
import play.api.data.format.Formatter
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.RadioOption

object SatisfactionSurveyForm {

  private val antiXSSMessageRegex = """^['`A-Za-z0-9\s\-&,\.£\(\)%;:\?\!]+$"""

  def satisfactionFormat: Formatter[String] = new Formatter[String] {
    def bind(key: String, data: Map[String, String]) = data.get(key).toRight(Seq(FormError(key, "error.required.feedback", Nil)))
    def unbind(key: String, value: String)           = Map(key -> value)
  }

  def apply(): Form[SatisfactionSurvey] = Form(
    mapping(
      "satisfaction" -> of[String](satisfactionFormat)
        .verifying("error.required.feedback", _.nonEmpty)
        .verifying("error.required.feedback", optionIsValid),
      "details"      -> optional(text
        .verifying("error.message.max_length.feedback", _.length <= 1200)
        .verifying("error.message.xss-invalid.feedback", _.matches(antiXSSMessageRegex)))
    )(SatisfactionSurvey.apply)(ss => Some(Tuple.fromProductTyped(ss)))
  )

  def options: Seq[RadioOption] = Seq(
    RadioOption("satisfaction", "verySatisfied"),
    RadioOption("satisfaction", "satisfied"),
    RadioOption("satisfaction", "neither"),
    RadioOption("satisfaction", "dissatisfied"),
    RadioOption("satisfaction", "veryDissatisfied")
  )

  def optionIsValid(value: String): Boolean =
    options.exists(_.value == value)
}

case class SatisfactionSurvey(satisfaction: String, details: Option[String])

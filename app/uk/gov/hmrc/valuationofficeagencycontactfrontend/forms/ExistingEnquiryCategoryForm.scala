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

import play.api.data.Form
import play.api.data.Forms._
import play.api.data.format.Formatter
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.RadioOption

object ExistingEnquiryCategoryForm extends FormErrorHelper {

  def existingEnquiryCategoryFormatter: Formatter[String] = new Formatter[String] {

    def bind(key: String, data: Map[String, String]) = data.get(key) match {
      case Some(s) if optionIsValid(s) => Right(s)
      case None                        => produceError(key, "error.existingEnquiryCategory.required")
      case _                           => produceError(key, "error.unknown")
    }

    def unbind(key: String, value: String) = Map(key -> value)
  }

  def apply(): Form[String] =
    Form(single("value" -> of(existingEnquiryCategoryFormatter)))

  def options: Seq[RadioOption] = Seq(
    RadioOption("existingEnquiryCategory", "council_tax"),
    RadioOption("existingEnquiryCategory", "business_rates"),
    RadioOption("existingEnquiryCategory", "housing_benefit"),
    RadioOption("existingEnquiryCategory", "fair_rent"),
    RadioOption("existingEnquiryCategory", "other")
  )

  def optionIsValid(value: String): Boolean = options.exists(o => o.value == value)
}

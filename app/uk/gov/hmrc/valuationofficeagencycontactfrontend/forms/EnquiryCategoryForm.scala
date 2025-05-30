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
import play.api.data.Forms._
import play.api.data.format.Formatter
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.RadioOption

object EnquiryCategoryForm extends FormErrorHelper {

  def enquiryCategoryFormatter: Formatter[String] = new Formatter[String] {

    def bind(key: String, data: Map[String, String]) = data.get(key) match {
      case Some(s) if optionIsValid(s) => Right(s)
      case None                        => produceError(key, "error.enquiryCategory.required")
      case _                           => produceError(key, "error.unknown")
    }

    def unbind(key: String, value: String) = Map(key -> value)
  }

  def apply(): Form[String] =
    Form(single("value" -> of(using enquiryCategoryFormatter)))

  def options: Seq[RadioOption] = Seq(
    RadioOption("enquiryCategory", "council_tax"),
    RadioOption("enquiryCategory", "business_rates"),
    RadioOption("enquiryCategory", "housing_benefit"),
    RadioOption("enquiryCategory", "fair_rent"),
    RadioOption("enquiryCategory", "providing_lettings"),
    RadioOption("enquiryCategory", "valuations_for_tax"),
    RadioOption("enquiryCategory", "valuation_for_public_body")
  )

  def optionIsValid(value: String): Boolean = options.exists(o => o.value == value)
}

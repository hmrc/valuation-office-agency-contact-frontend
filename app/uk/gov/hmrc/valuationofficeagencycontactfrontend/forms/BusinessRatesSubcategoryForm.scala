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

import play.api.data.Form
import play.api.data.Forms._
import play.api.data.format.Formatter
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.RadioOption

object BusinessRatesSubcategoryForm extends FormErrorHelper {

  def BusinessRatesSubcategoryFormatter = new Formatter[String] {
    def bind(key: String, data: Map[String, String]) = data.get(key) match {
      case Some(s) if optionIsValid(s) => Right(s)
      case None => produceError(key, "error.required")
      case _ => produceError(key, "error.unknown")
    }

    def unbind(key: String, value: String) = Map(key -> value)
  }

  def apply(): Form[String] =
    Form(single("value" -> of(BusinessRatesSubcategoryFormatter)))

  def options = Seq(
    RadioOption("businessRatesSubcategory", "business_rates_poor_repair"),
    RadioOption("businessRatesSubcategory", "business_rates_changes"),
    RadioOption("businessRatesSubcategory", "business_rates_ct_change"),
    RadioOption("businessRatesSubcategory", "business_rates_not_used"),
    RadioOption("businessRatesSubcategory", "business_rates_closed"),
    RadioOption("businessRatesSubcategory", "business_rates_other")
  )

  def optionIsValid(value: String) = options.exists(o => o.value == value)
}

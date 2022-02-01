/*
 * Copyright 2022 HM Revenue & Customs
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

import play.api.data.Forms.{of, single}
import play.api.data.format.Formatter
import play.api.data.{Form, FormError}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.DateFormatter.{dateFormatter, nowInUK}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.RadioOption

object EnquiryDateForm extends FormErrorHelper {

  private val minusDays = 28

  def EnquiryDateFormatter = new Formatter[String] {
    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], String] = data.get(key) match {
      case Some(s) if optionIsValid(s) => Right(s)
      case None => Left(Seq(FormError(key, "error.enquiryDate.required", Seq(beforeDate))))
      case _ => produceError(key, "error.unknown")
    }

    override def unbind(key: String, value: String): Map[String, String] = Map(key -> value)
  }

  def apply(): Form[String] = {
    Form(single("value" -> of(EnquiryDateFormatter)))
  }

  def options = Seq(
    RadioOption("enquiryDate", "yes"),
    RadioOption("enquiryDate", "no"),
    RadioOption("enquiryDate", "notKnow")
  )

  def optionIsValid(value: String) = options.exists(o => o.value == value)

  def beforeDate: String = nowInUK.minusDays(minusDays).format(dateFormatter)

}

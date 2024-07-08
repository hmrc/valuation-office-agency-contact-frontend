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

import play.api.data.Forms.{of, single}
import play.api.data.format.Formatter
import play.api.data.{Form, FormError}
import play.api.i18n.Messages
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.{DateUtil, RadioOption}

object EnquiryDateForm extends FormErrorHelper {

  private val minusDays = 28

  def enquiryDateFormatter()(implicit messages: Messages, dateUtil: DateUtil): Formatter[String] = new Formatter[String] {

    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], String] = data.get(key) match {
      case Some(s) if optionIsValid(s) => Right(s)
      case None                        => Left(Seq(FormError(key, "error.enquiryDate.required", Seq(beforeDate()))))
      case _                           => produceError(key, "error.unknown")
    }

    override def unbind(key: String, value: String): Map[String, String] = Map(key -> value)
  }

  def apply()(implicit messages: Messages, dateUtil: DateUtil): Form[String] =
    Form(single("value" -> of(enquiryDateFormatter())))

  def options: Seq[RadioOption] = Seq(
    RadioOption("enquiryDate", "yes"),
    RadioOption("enquiryDate", "no"),
    RadioOption("enquiryDate", "notKnow")
  )

  def optionIsValid(value: String): Boolean = options.exists(o => o.value == value)

  def beforeDate()(implicit messages: Messages, dateUtil: DateUtil): String =
    dateUtil.formattedZonedDate(dateUtil.nowInUK.minusDays(minusDays))

}

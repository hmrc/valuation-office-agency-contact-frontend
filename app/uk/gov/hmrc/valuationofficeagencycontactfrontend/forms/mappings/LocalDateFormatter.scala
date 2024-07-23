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

package uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.mappings

import play.api.data.FormError
import play.api.data.format.Formatter

import java.time.LocalDate
import scala.util.Try
import java.util.regex.Pattern

private[mappings] class LocalDateFormatter(key: String) extends Formatter[Option[LocalDate]] {
  val testRegex: Pattern               = """\d+""".r.pattern
  val earliestEffectiveDate: LocalDate = LocalDate.of(1900, 1, 1)
  val yearMsg: String                  = s"$key.year"
  val monthMsg: String                 = s"$key.month"
  val dayMsg: String                   = s"$key.day"
  val invalidDate: String              = s"$key.error.invalidDate"
  val dayMonthRequired: String         = s"$key.error.mandatory.dayMonth"
  val dayYearRequired: String          = s"$key.error.mandatory.dayYear"
  val monthYearRequired: String        = s"$key.error.mandatory.monthYear"
  val dayRequired: String              = s"$key.error.mandatory.day"
  val monthRequired: String            = s"$key.error.mandatory.month"
  val yearRequired: String             = s"$key.error.mandatory.year"
  val yearRange: String                = s"$key.error.year.range"
  val dayRange: String                 = s"$key.error.day.range"
  val dayNumber: String                = s"$key.error.day.number"
  val monthRange: String               = s"$key.error.month.range"
  val monthNumber: String              = s"$key.error.month.number"
  val yearNumber: String               = s"$key.error.year.number"

  override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Option[LocalDate]] = {
    val yearValue  = data.get(s"$key-year").map(_.trim).filter(_ != "")
    val monthValue = data.get(s"$key-month").map(_.trim).filter(_ != "")
    val dayValue   = data.get(s"$key-day").map(_.trim).filter(_ != "")

    val validation: Either[Seq[FormError], Option[LocalDate]] = (dayValue, monthValue, yearValue) match {
      case (None, None, None)          => Right(None)
      case (None, None, _)             => Left(List(FormError(key, dayMonthRequired), FormError(key, dayMsg), FormError(key, monthMsg)))
      case (None, _, None)             => Left(List(FormError(key, dayYearRequired), FormError(key, dayMsg), FormError(key, yearMsg)))
      case (_, None, None)             => Left(List(FormError(key, monthYearRequired), FormError(key, monthMsg), FormError(key, yearMsg)))
      case (None, _, _)                => Left(List(FormError(key, dayRequired), FormError(key, dayMsg)))
      case (_, None, _)                => Left(List(FormError(key, monthRequired), FormError(key, monthMsg)))
      case (_, _, None)                => Left(List(FormError(key, yearRequired), FormError(key, yearMsg)))
      case (Some(x), Some(y), Some(z)) =>
        validateDateField(key, x, y, z)
    }
    validation
  }

  def validateDateField(key: String, day: String, month: String, year: String): Either[Seq[FormError], Option[LocalDate]] =
    for {
      day       <- validateDay(key, day)
      month     <- validateMont(key, month)
      year      <- validateYear(key, year)
      finalDate <-
        Try(
          LocalDate.of(year, month, day)
        ).toEither.left.map(_ =>
          List(FormError(key, invalidDate), FormError(key, dayMsg), FormError(key, monthMsg), FormError(key, yearMsg))
        ).flatMap { date =>
          if date.isBefore(earliestEffectiveDate) then
            Left(List(FormError(key, yearRange), FormError(key, dayMsg), FormError(key, monthMsg), FormError(key, yearMsg)))
          else
            Right(Some(date))
        }
    } yield finalDate

  def validateDay(key: String, day: String): Either[Seq[FormError], Int] =
    if testRegex.matcher(day).matches() then
      val intDay = day.toInt
      if intDay > 31 || intDay < 1 then
        Left(List(FormError(key, dayRange), FormError(key, dayMsg)))
      else
        Right(intDay)
    else
      Left(List(FormError(key, dayNumber), FormError(key, dayMsg)))

  def validateMont(key: String, month: String): Either[Seq[FormError], Int] =
    if testRegex.matcher(month).matches() then
      val intMonth = month.toInt
      if intMonth > 12 | intMonth < 1 then
        Left(List(FormError(key, monthRange), FormError(key, monthMsg)))
      else
        Right(intMonth)
    else
      Left(List(FormError(key, monthNumber), FormError(key, monthMsg)))

  def validateYear(key: String, year: String): Either[Seq[FormError], Int] =
    if testRegex.matcher(year).matches() then
      val intYear = year.toInt
      if intYear < 1900 then
        Left(List(FormError(key, yearRange), FormError(key, yearMsg)))
      else
        Right(intYear)
    else
      Left(List(FormError(key, yearNumber), FormError(key, yearMsg)))

  override def unbind(key: String, value: Option[LocalDate]): Map[String, String] =
    Map(
      s"$key-day"   -> value.map(_.getDayOfMonth.toString).getOrElse(""),
      s"$key-month" -> value.map(_.getMonthValue.toString).getOrElse(""),
      s"$key-year"  -> value.map(_.getYear.toString).getOrElse("")
    )
}

/*
 * Copyright 2023 HM Revenue & Customs
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
import uk.gov.hmrc.valuationofficeagencycontactfrontend.SpecBase

import java.time.LocalDate

class LocalDateFormatterSpec extends SpecBase {

  val keyValue = "value"
  val formatter = new LocalDateFormatter(keyValue)
  val inputDay = s"$keyValue-day"
  val inputMonth = s"$keyValue-month"
  val inputYear = s"$keyValue-year"
  val year = s"$keyValue.year"
  val month = s"$keyValue.month"
  val day = s"$keyValue.day"
  val invalidDate = s"$keyValue.error.invalidDate"
  val dayMonthRequired = s"$keyValue.error.mandatory.dayMonth"
  val dayYearRequired = s"$keyValue.error.mandatory.dayYear"
  val monthYearRequired =s"$keyValue.error.mandatory.monthYear"
  val dayRequired = s"$keyValue.error.mandatory.day"
  val monthRequired = s"$keyValue.error.mandatory.month"
  val yearRequired = s"$keyValue.error.mandatory.year"
  val yearRange = s"$keyValue.error.year.range"
  val dayRange = s"$keyValue.error.day.range"
  val dayNumber = s"$keyValue.error.day.number"
  val monthRange = s"$keyValue.error.month.range"
  val monthNumber = s"$keyValue.error.month.number"
  val yearNumber = s"$keyValue.error.year.number"


  "LocalDateFormatter " must {
    "returns None when all fields are empty" in {
      formatter.bind(keyValue,Map.empty).toOption.get mustBe None
    }

    "returns the list of FormErrors with correct message when day and month are empty" in {
      formatter.bind(keyValue,Map(inputYear -> "2021")).swap.toOption.get mustBe
        List(FormError(keyValue, dayMonthRequired),FormError(keyValue, day), FormError(keyValue, month))
    }

    "returns the list of FormErrors with correct message when day and year are empty" in {
      formatter.bind(keyValue,Map(inputMonth -> "12")).swap.toOption.get mustBe
        List(FormError(keyValue, dayYearRequired),FormError(keyValue, day), FormError(keyValue, year))
    }

    "returns the list of FormErrors with correct message when month and year are empty" in {
      formatter.bind(keyValue,Map(inputDay -> "1")).swap.toOption.get mustBe
        List(FormError(keyValue, monthYearRequired),FormError(keyValue, month), FormError(keyValue, year))
    }

    "returns the list of FormErrors with correct message when day is empty" in {
      formatter.bind(keyValue,Map(inputMonth -> "1", inputYear -> "2021")).swap.toOption.get mustBe
        List(FormError(keyValue, dayRequired),FormError(keyValue, day))
    }

    "returns the list of FormErrors with correct message when month is empty" in {
      formatter.bind(keyValue,Map(inputDay -> "1", inputYear -> "2021")).swap.toOption.get mustBe
        List(FormError(keyValue, monthRequired),FormError(keyValue, month))
    }

    "returns the list of FormErrors with correct message when year is empty" in {
      formatter.bind(keyValue,Map(inputDay -> "1", inputMonth -> "12")).swap.toOption.get mustBe
        List(FormError(keyValue, yearRequired),FormError(keyValue, year))
    }

    "returns the list of FormErrors with correct message when day is greater then 31" in {
      formatter.bind(keyValue,Map(inputDay -> "32", inputMonth -> "01", inputYear -> "2021")).swap.toOption.get mustBe
        List(FormError(keyValue, dayRange),FormError(keyValue, day))
    }

    "returns the list of FormErrors with correct message when day is equal to 0" in {
      formatter.bind(keyValue,Map(inputDay -> "0", inputMonth -> "01", inputYear -> "2021")).swap.toOption.get mustBe
        List(FormError(keyValue, dayRange),FormError(keyValue, day))
    }

    "returns the list of FormErrors with correct message when day is not number" in {
      formatter.bind(keyValue,Map(inputDay -> "e", inputMonth -> "01", inputYear -> "2021")).swap.toOption.get mustBe
        List(FormError(keyValue, dayNumber),FormError(keyValue, day))
    }

    "returns the list of FormErrors with correct message when month is greater then 12" in {
      formatter.bind(keyValue,Map(inputDay -> "1", inputMonth -> "13", inputYear -> "2020")).swap.toOption.get mustBe
        List(FormError(keyValue, monthRange),FormError(keyValue, month))
    }

    "returns the list of FormErrors with correct message when month is equal to 0" in {
      formatter.bind(keyValue,Map(inputDay -> "1", inputMonth -> "0", inputYear -> "2020")).swap.toOption.get mustBe
        List(FormError(keyValue, monthRange),FormError(keyValue, month))
    }

    "returns the list of FormErrors with correct message when month is not number" in {
      formatter.bind(keyValue,Map(inputDay -> "1", inputMonth -> "e", inputYear -> "2020")).swap.toOption.get mustBe
        List(FormError(keyValue, monthNumber),FormError(keyValue, month))
    }

    "returns the list of FormErrors with correct message when year is less then 1900" in {
      formatter.bind(keyValue,Map(inputDay -> "1", inputMonth -> "12", inputYear -> "1890")).swap.toOption.get mustBe
        List(FormError(keyValue, yearRange),FormError(keyValue, year))
    }

    "returns the list of FormErrors with correct message when year is not number" in {
      formatter.bind(keyValue,Map(inputDay -> "1", inputMonth -> "12", inputYear -> "yyyy")).swap.toOption.get mustBe
        List(FormError(keyValue, yearNumber),FormError(keyValue, year))
    }

    "returns the list of FormErrors with correct message when the date is invalid" in {
      formatter.bind(keyValue,Map(inputDay -> "31", inputMonth -> "02", inputYear -> "2021")).swap.toOption.get mustBe
        List(FormError(keyValue, invalidDate),FormError(keyValue, day),FormError(keyValue, month),FormError(keyValue, year))
    }

    "returns the correct Local date when the date is correct" in {
      formatter.bind(keyValue,Map(inputDay -> "4", inputMonth -> "01", inputYear -> "2020")).toOption.get mustBe
        Some(LocalDate.of(2020,1,4))
    }

    "returns date variables when the unbind func is called" in {
      formatter.unbind(keyValue, Some(LocalDate.of(2020,1,4))) mustBe
        Map(inputDay -> "4", inputMonth -> "1", inputYear -> "2020")
    }
  }

}

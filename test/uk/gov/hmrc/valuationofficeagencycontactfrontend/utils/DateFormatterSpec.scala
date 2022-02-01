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

package uk.gov.hmrc.valuationofficeagencycontactfrontend.utils

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import play.api.Logging
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.DateFormatter._

import java.time.Month.FEBRUARY
import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalTime, ZonedDateTime}
import java.util.Locale

/**
 * @author Yuriy Tumakha
 */
class DateFormatterSpec extends AnyFlatSpec with should.Matchers with Logging {

  // scalastyle:off
  val testLocalDate: LocalDate = LocalDate.of(2022, FEBRUARY, 20)
  val testZonedDate: ZonedDateTime = ZonedDateTime.of(testLocalDate, LocalTime.of(13, 45), ukTimezone)
  // scalastyle:on

  "nowInUK" should "return current time in UK time zone" in {
    logger.info("UK Time: " + nowInUK)
    timeFormatter.format(nowInUK) shouldBe timeFormatter.format(ZonedDateTime.now(ukTimezone))
  }

  "dateFormatter" should "format date in format 'd MMMM yyyy'" in {
    testZonedDate.format(dateFormatter) shouldBe "20 February 2022"
  }

  "shortDateFormatter" should "format date in format 'dd/MM/yyyy'" in {
    testZonedDate.format(shortDateFormatter) shouldBe "20/02/2022"
  }

  "timeFormatter" should "format date in format 'HH:mm'" in {
    testZonedDate.format(timeFormatter) shouldBe "13:45"
  }

  "formattedLocalDate" should "format date in format 'd MMMM yyyy'" in {
    formattedLocalDate(testLocalDate) shouldBe "20 February 2022"
  }

  "satisfactionSurveyTodayDate" should "return current date in format 'dd/MM/yyyy'" in {
    satisfactionSurveyTodayDate shouldBe DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.UK).format(ZonedDateTime.now(ukTimezone))
  }

}

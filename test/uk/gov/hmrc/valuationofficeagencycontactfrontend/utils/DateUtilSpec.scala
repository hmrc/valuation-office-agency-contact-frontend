/*
 * Copyright 2026 HM Revenue & Customs
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

import play.api.Logging
import play.api.i18n.{Lang, Messages}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.SpecBase

import java.time.Month.JANUARY
import java.time.{LocalDate, LocalTime, ZonedDateTime}
import java.util.Locale

/**
  * @author Yuriy Tumakha
  */
class DateUtilSpec extends SpecBase with Logging {

  private val messagesEnglish: Messages = messagesApi.preferred(Seq(Lang(Locale.of("en"))))
  private val messagesWelsh: Messages   = messagesApi.preferred(Seq(Lang(Locale.of("cy"))))

  private val dateUtil = injector.instanceOf[DateUtil]

  // scalastyle:off
  private val testLocalDate: LocalDate     = LocalDate.of(2022, JANUARY, 22)
  private val testZonedDate: ZonedDateTime = ZonedDateTime.of(testLocalDate.plusMonths(1), LocalTime.of(13, 45), dateUtil.ukTimezone)
  // scalastyle:on

  "nowInUK" must {
    "return current time in UK time zone" in {
      logger.info("UK Time: " + dateUtil.nowInUK)
      dateUtil.timeFormatter.format(dateUtil.nowInUK) mustBe dateUtil.timeFormatter.format(ZonedDateTime.now(dateUtil.ukTimezone))
    }
  }

  "shortDateFormatter" must {
    "format date in format 'dd/MM/yyyy'" in {
      testZonedDate.format(dateUtil.shortDateFormatter) mustBe "22/02/2022"
    }
  }

  "timeFormatter" must {
    "format date in format 'HH:mm'" in {
      testZonedDate.format(dateUtil.timeFormatter) mustBe "13:45"
    }
  }

  "formattedLocalDate" must {
    "format date in format 'd MMMM yyyy'" in {
      dateUtil.formattedLocalDate(testLocalDate)(using messagesEnglish) mustBe "22 January 2022"
      dateUtil.formattedLocalDate(testLocalDate)(using messagesWelsh) mustBe "22 Ionawr 2022"
    }
  }

  "formattedZonedDate" must {
    "format date in format 'd MMMM yyyy'" in {
      dateUtil.formattedZonedDate(testZonedDate)(using messagesEnglish) mustBe "22 February 2022"
      dateUtil.formattedZonedDate(testZonedDate)(using messagesWelsh) mustBe "22 Chwefror 2022"
    }
  }

}

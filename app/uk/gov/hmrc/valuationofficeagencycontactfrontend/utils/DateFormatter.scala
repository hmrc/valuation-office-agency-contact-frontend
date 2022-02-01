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

import java.time.{LocalDate, ZoneId, ZonedDateTime}
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * @author Yuriy Tumakha
 */
object DateFormatter {

    val ukTimezone: ZoneId = ZoneId.of("Europe/London")
    val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.UK)
    val shortDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.UK)
    val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.UK)

    def nowInUK: ZonedDateTime = ZonedDateTime.now(ukTimezone)

    def formattedLocalDate(date: LocalDate): String = date.format(dateFormatter)

    def satisfactionSurveyTodayDate: String = nowInUK.format(shortDateFormatter)

}

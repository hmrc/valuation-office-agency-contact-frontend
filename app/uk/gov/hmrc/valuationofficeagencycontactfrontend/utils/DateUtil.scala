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

import play.api.i18n.Messages
import uk.gov.hmrc.play.language.LanguageUtils

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, ZoneId, ZonedDateTime}
import java.util.Locale
import javax.inject.{Inject, Singleton}

/**
 * @author Yuriy Tumakha
 */
@Singleton
class DateUtil @Inject()(langUtil: LanguageUtils) {

    val ukTimezone: ZoneId = ZoneId.of("Europe/London")
    val shortDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.UK)
    val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.UK)

    def nowInUK: ZonedDateTime = ZonedDateTime.now(ukTimezone)

    def formattedLocalDate(date: LocalDate)(implicit messages: Messages): String = langUtil.Dates.formatDate(date)

    def formattedZonedDate(date: ZonedDateTime)(implicit messages: Messages): String = formattedLocalDate(date.toLocalDate)

}

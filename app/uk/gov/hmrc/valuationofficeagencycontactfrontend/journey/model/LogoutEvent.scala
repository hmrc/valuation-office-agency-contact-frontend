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

package uk.gov.hmrc.valuationofficeagencycontactfrontend.journey.model

import play.api.libs.json.{Json, OWrites}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.ContactDetails
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.UserAnswers

/**
 * @author Yuriy Tumakha
 */
case class LogoutEvent(refNumber: Option[String], contact: Option[ContactDetails])

object LogoutEvent {

  implicit val logoutEventWrites: OWrites[LogoutEvent] = Json.writes[LogoutEvent]

  def apply(userAnswers: Option[UserAnswers]): LogoutEvent =
    LogoutEvent(userAnswers.flatMap(_.refNumber), userAnswers.flatMap(_.contactDetails))

}

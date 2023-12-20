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

package uk.gov.hmrc.valuationofficeagencycontactfrontend.models

import play.api.libs.json.{Json, Writes}

/**
 * @author Yuriy Tumakha
 */
case class EnquiryAuditEvent(
  contact: ContactDetails,
  propertyAddress: PropertyAddress,
  isCouncilTaxEnquiry: Boolean,
  contactReason: Option[String],
  enquiryCategoryMsg: String,
  subEnquiryCategoryMsg: String,
  message: String,
  enquiryBeforeLast28days: Option[String],
  refNumber: Option[String]
)

object EnquiryAuditEvent {

  implicit val enquiryAuditEventWrites: Writes[EnquiryAuditEvent] = Json.writes[EnquiryAuditEvent]

}

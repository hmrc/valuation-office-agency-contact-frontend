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

package uk.gov.hmrc.vo.contact.frontend.models.event

import play.api.libs.json.{Json, Writes}
import uk.gov.hmrc.vo.contact.frontend.models.{ContactDetails, ContactWithEnMessage, PropertyAddress}
import uk.gov.hmrc.vo.contact.frontend.utils.UserAnswers
import uk.gov.hmrc.vo.service.model.event.AuditEvent

/**
  * @author Yuriy Tumakha
  */
case class SendEnquiryToVOA(
  contact: ContactDetails,
  propertyAddress: PropertyAddress,
  isCouncilTaxEnquiry: Boolean,
  contactReason: Option[String],
  enquiryCategoryMsg: String,
  subEnquiryCategoryMsg: String,
  message: String,
  enquiryBeforeLast28days: Option[String],
  refNumber: Option[String]
) extends AuditEvent

object SendEnquiryToVOA:
  implicit val writes: Writes[SendEnquiryToVOA] = Json.writes

  def apply(contactMessage: ContactWithEnMessage, userAnswers: UserAnswers): SendEnquiryToVOA =
    SendEnquiryToVOA(
      contactMessage.contact,
      contactMessage.propertyAddress,
      contactMessage.isCouncilTaxEnquiry,
      contactMessage.contactReason,
      contactMessage.enquiryCategoryMsg,
      contactMessage.subEnquiryCategoryMsg,
      contactMessage.message,
      userAnswers.enquiryDate,
      userAnswers.refNumber
    )

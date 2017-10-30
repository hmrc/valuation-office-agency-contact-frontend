/*
 * Copyright 2017 HM Revenue & Customs
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

import play.api.i18n.MessagesApi
import play.api.libs.json.Json
import play.api.Logger

case class ContactWithEnMessage (contact: ConfirmedContactDetails,
                                 propertyAddress: PropertyAddress,
                                 enquiryCategoryMsg: String,
                                 subEnquiryCategoryMsg: String,
                                 message: String)

object ContactWithEnMessage {
  implicit val format = Json.format[ContactWithEnMessage]

  def apply(ct: Contact, messagesApi: MessagesApi): ContactWithEnMessage = {
    messagesApi.messages.get("en") match {
      case Some(messageMap) =>
        val enquiryCategoryMsg = messageMap.get(ct.enquiryCategory) match {
          case Some(msg) => msg
          case None =>
            Logger.warn("Unable to find key " + ct.enquiryCategory + " in en messages")
            throw new RuntimeException("Unable to find key " + ct.enquiryCategory + " in en messages")
        }
        val subEnquiryCategoryMsg = messageMap.get(ct.subEnquiryCategory) match {
          case Some(msg) => msg
          case None =>
            Logger.warn("Unable to find key " + ct.subEnquiryCategory + " in en messages")
            throw new RuntimeException("Unable to find key " + ct.subEnquiryCategory + " in en messages")
        }
        ContactWithEnMessage(ct.contact, ct.propertyAddress, enquiryCategoryMsg, subEnquiryCategoryMsg, ct.message)
      case None =>
        Logger.warn("Unable to find en messages when creating message map")
        throw new RuntimeException("Unable to find en messages when creating message map")
    }
  }
}


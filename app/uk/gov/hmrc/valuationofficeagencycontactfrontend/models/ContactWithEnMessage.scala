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

package uk.gov.hmrc.valuationofficeagencycontactfrontend.models

import org.apache.commons.text.StringEscapeUtils
import play.api.i18n.MessagesApi
import play.api.libs.json.Json
import play.api.Logger
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.UserAnswers

case class ContactWithEnMessage(contact: ContactDetails,
                                propertyAddress: PropertyAddress,
                                isCouncilTaxEnquiry: Boolean,
                                contactReason: Option[String],
                                enquiryCategoryMsg: String,
                                subEnquiryCategoryMsg: String,
                                message: String)

object ContactWithEnMessage {
  implicit val format = Json.format[ContactWithEnMessage]
  private val log = Logger(this.getClass)
  val councilTaxKey = "council_tax"
  val businessRatesKey = "business_rates"

  def apply(ct: Contact, messagesApi: MessagesApi, userAnswers: UserAnswers): ContactWithEnMessage = {
    messagesApi.messages.get("en") match {
      case Some(messageMap) =>
        val enquiryCategoryMsg = {
          (messageMap.get("enquiryCategory." + ct.enquiryCategory), messageMap.get("existingEnquiryCategory." + ct.enquiryCategory)) match {
          case (Some(msg), _) => msg
          case (_, Some(msg)) => msg
          case _ =>
            log.warn("Unable to find key " + ct.enquiryCategory + " in en messages")
            throw new RuntimeException("Unable to find key " + ct.enquiryCategory + " in en messages")
          }
        }

        val enquiryKey = ct.enquiryCategory match {
          case `councilTaxKey` => "councilTaxSubcategory"
          case `businessRatesKey` => "businessRatesSubcategory"
          case "housing_benefit" => "housingBenefitSubcategory"
          case "fair_rent" => "fairRents"
          case "other" => "other"
          case _ =>
            log.warn("Unknown enquiry category key " + ct.enquiryCategory)
            throw new RuntimeException("Unknown enquiry category key " + ct.enquiryCategory)
        }

        val subEnquiryCategoryMsg = if (userAnswers.existingEnquiryCategory.isDefined) "Existing Enquiry" else {
          messageMap.get(enquiryKey + "." + ct.subEnquiryCategory) match {
            case Some(msg) => msg
            case None =>
              log.warn(s"Unable to find key $enquiryKey.${ct.subEnquiryCategory} in en messages")
              throw new RuntimeException(s"Unable to find key $enquiryKey.${ct.subEnquiryCategory} in en messages")
          }
        }
        ContactWithEnMessage(ct.contact, ct.propertyAddress, ct.enquiryCategory == councilTaxKey,
          userAnswers.contactReason, enquiryCategoryMsg, subEnquiryCategoryMsg, StringEscapeUtils.escapeJava(ct.message))

      case None =>
        log.warn("Unable to find en messages when creating message map")
        throw new RuntimeException("Unable to find en messages when creating message map")
    }
  }
}


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

import org.apache.commons.text.StringEscapeUtils
import play.api.Logger
import play.api.i18n.{Lang, Messages, MessagesApi}
import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.UserAnswers

import java.util.Locale

case class ContactWithEnMessage(
  contact: ContactDetails,
  propertyAddress: PropertyAddress,
  isCouncilTaxEnquiry: Boolean,
  contactReason: Option[String],
  enquiryCategoryMsg: String,
  subEnquiryCategoryMsg: String,
  message: String
)

object ContactWithEnMessage {

  implicit val format: OFormat[ContactWithEnMessage] = Json.format[ContactWithEnMessage]
  private val log                                    = Logger(this.getClass)
  val councilTaxKey                                  = "council_tax"
  val businessRatesKey                               = "business_rates"

  def apply(contact: Contact, messagesApi: MessagesApi, userAnswers: UserAnswers): ContactWithEnMessage = {
    implicit val messagesEN: Messages = messagesApi.preferred(Seq(Lang(Locale.UK)))

    val enquiryCategoryMsg    = enquiryCategory(contact)
    val subEnquiryCategoryMsg = enquirySubCategory(contact, userAnswers.existingEnquiryCategory.isDefined)

    ContactWithEnMessage(
      contact.contact,
      contact.propertyAddress,
      contact.enquiryCategory == councilTaxKey,
      userAnswers.contactReason,
      enquiryCategoryMsg,
      subEnquiryCategoryMsg,
      StringEscapeUtils.escapeJava(contact.message)
    )
  }

  def enquiryCategory(contact: Contact)(implicit messages: Messages): String = {
    val lang = messages.lang.language
    messages.translate("enquiryCategory." + contact.enquiryCategory, Seq.empty)
      .orElse(messages.translate("existingEnquiryCategory." + contact.enquiryCategory, Seq.empty)) match {
      case Some(msg) => msg
      case _         =>
        log.warn(s"Unable to find key ${contact.enquiryCategory} in $lang messages")
        throw new RuntimeException(s"Unable to find key ${contact.enquiryCategory} in $lang messages")
    }
  }

  def enquirySubCategory(contact: Contact, isUpdateExistingEnquiry: Boolean)(implicit messages: Messages): String =
    if (isUpdateExistingEnquiry) {
      messages("existing.enquiry")
    } else {
      val lang           = messages.lang.language
      val categoryPrefix = categoryKeyPrefix(contact)
      messages.translate(categoryPrefix + "." + contact.subEnquiryCategory, Seq.empty) match {
        case Some(msg) => msg
        case None      =>
          log.warn(s"Unable to find key $categoryPrefix.${contact.subEnquiryCategory} in $lang messages")
          throw new RuntimeException(s"Unable to find key $categoryPrefix.${contact.subEnquiryCategory} in $lang messages")
      }
    }

  private def categoryKeyPrefix(contact: Contact): String =
    contact.enquiryCategory match {
      case `councilTaxKey`    => "councilTaxSubcategory"
      case `businessRatesKey` => "businessRatesSubcategory"
      case "housing_benefit"  => "housingBenefitSubcategory"
      case "fair_rent"        => "fairRents"
      case "other"            => "other"
      case _                  =>
        log.warn("Unknown enquiry category key " + contact.enquiryCategory)
        throw new RuntimeException("Unknown enquiry category key " + contact.enquiryCategory)
    }

}

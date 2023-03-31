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

package uk.gov.hmrc.valuationofficeagencycontactfrontend.utils

import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.ContactDetails

object ContactFormatter {

 def formattedConfirmedContactDetails(contact: ContactDetails, interstitial: String): String = {
    insertInterstitials(Seq(contact.fullName.trim, contact.email, contact.contactNumber), interstitial)
 }

 def formattedContactDetails(contact: Option[ContactDetails], interstitial: String): String = {
  contact.fold("") { con =>
   insertInterstitials(Seq(con.fullName.trim, con.email, con.contactNumber), interstitial)
  }
 }

  private[utils] def insertInterstitials(contact: Seq[String], interstitial: String): String = {
    if (contact.isEmpty) "" else contact.head.trim + contact.tail.fold("") { (acc, elem) => acc + interstitial + elem.trim }
  }

}

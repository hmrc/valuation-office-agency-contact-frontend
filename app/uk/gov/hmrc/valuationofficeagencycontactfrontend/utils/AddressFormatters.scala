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

package uk.gov.hmrc.valuationofficeagencycontactfrontend.utils

import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.{BusinessRatesAddress, PropertyAddress}

object AddressFormatters {

  def formattedCouncilTaxAddress(address: Option[PropertyAddress], interstitial: String): String = {
    address.fold("") {addr =>
      insertInterstitials(Seq(addr.addressLine1, addr.addressLine2, addr.town, addr.county, addr.postcode), interstitial)
    }
  }

  def formattedBusinessRatesAddress(address: Option[BusinessRatesAddress], interstitial: String): String = {
    address.fold("") { addr =>
      insertInterstitials(Seq(addr.businessName, addr.businessAddressLine1, addr.businessAddressLine2, addr.businessAddressLine3,
        addr.town, addr.county, addr.postcode), interstitial)
    }
  }

  private [utils] def insertInterstitials(address: Seq[String], interstitial: String): String = {
    if (address.isEmpty) "" else address.head.trim + address.tail.fold("") { (acc, elem) => acc + interstitial + elem.trim }
  }

}

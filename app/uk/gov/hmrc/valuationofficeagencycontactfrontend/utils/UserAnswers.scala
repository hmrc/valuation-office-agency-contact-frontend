/*
 * Copyright 2021 HM Revenue & Customs
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

import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models._

class UserAnswers(val cacheMap: CacheMap) {
  def tellUsMore: Option[TellUsMore] = cacheMap.getEntry[TellUsMore](TellUsMoreId.toString)

  def enquiryCategory: Option[String] = cacheMap.getEntry[String](EnquiryCategoryId.toString)

  def councilTaxSubcategory: Option[String] = cacheMap.getEntry[String](CouncilTaxSubcategoryId.toString)

  def contactDetails: Option[ContactDetails] = cacheMap.getEntry[ContactDetails](ContactDetailsId.toString)

  def businessRatesSubcategory: Option[String] = cacheMap.getEntry[String](BusinessRatesSubcategoryId.toString)

  def propertyAddress: Option[PropertyAddress] = cacheMap.getEntry[PropertyAddress](PropertyAddressId.toString)

  def contact(): Either[String, Contact] = {

    val optionalContactModel = for {
      cd <- contactDetails
      pa <- propertyAddress
      eq <- enquiryCategory
      subcategory <- eq match {
        case "council_tax" => councilTaxSubcategory
        case "business_rates" => businessRatesSubcategory
        case _ => None
      }
      tellUs <- tellUsMore
    } yield Contact(cd, pa, eq, subcategory, tellUs.message)

    optionalContactModel match {
      case Some(c @ Contact(_, _, _, _, _)) => Right(c)
      case None => Left("Unable to parse")
    }
  }
}


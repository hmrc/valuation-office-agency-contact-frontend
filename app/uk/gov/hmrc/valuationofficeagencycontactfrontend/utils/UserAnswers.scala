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

package uk.gov.hmrc.valuationofficeagencycontactfrontend.utils

import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.journey.model.TellUsMorePage.lastTellUsMorePage
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.viewmodels.AnswerSection

import java.time.LocalDate

class UserAnswers(val cacheMap: CacheMap) {

  def getString(key: String): Option[String] = cacheMap.getEntry[String](key)

  def enquiryDate: Option[String] = cacheMap.getEntry[String](EnquiryDateId.toString)

  def contactReason: Option[String] = cacheMap.getEntry[String](ContactReasonId.toString)

  def existingEnquiryCategory: Option[String] = cacheMap.getEntry[String](ExistingEnquiryCategoryId.toString)

  def refNumber: Option[String] = cacheMap.getEntry[String](RefNumberId.toString)

  def tellUsMore: Option[TellUsMore] = cacheMap.getEntry[TellUsMore](TellUsMoreId.toString)

  def whatElse: Option[String] = cacheMap.getEntry[String](WhatElseId.toString)

  def anythingElse: Option[String] = cacheMap.getEntry[String](AnythingElseId.toString)

  def enquiryCategory: Option[String] = cacheMap.getEntry[String](EnquiryCategoryId.toString)

  def councilTaxSubcategory: Option[String] = cacheMap.getEntry[String](CouncilTaxSubcategoryId.toString)

  def contactDetails: Option[ContactDetails] = cacheMap.getEntry[ContactDetails](ContactDetailsId.toString)

  def businessRatesSubcategory: Option[String] = cacheMap.getEntry[String](BusinessRatesSubcategoryId.toString)

  def propertyAddress: Option[PropertyAddress] = cacheMap.getEntry[PropertyAddress](PropertyAddressId.toString)

  def otherSubcategory: Option[String] = cacheMap.getEntry[String](OtherSubcategoryId.toString)

  def answerSection: Option[AnswerSection] = cacheMap.getEntry[AnswerSection](AnswerSectionId.toString)

  def propertyWindEnquiry: Option[String] = cacheMap.getEntry[String](CouncilTaxPropertyPoorRepairId.toString)

  def datePropertyChanged: Option[LocalDate] = cacheMap.getEntry[LocalDate](DatePropertyChangedId.toString)

  def annexeEnquiry: Option[String] = cacheMap.getEntry[String](CouncilTaxAnnexeEnquiryId.toString)

  def annexeSelfContainedEnquiry: Option[String] = cacheMap.getEntry[String](CouncilTaxAnnexeSelfContainedEnquiryId.toString)

  def annexeHaveCookingWashing: Option[String] = cacheMap.getEntry[String](CouncilTaxAnnexeHaveCookingId.toString)

  def councilTaxBusinessEnquiry: Option[String] = cacheMap.getEntry[String](CouncilTaxBusinessEnquiryId.toString)

  def businessRatesSelfCateringEnquiry: Option[String] = cacheMap.getEntry[String](BusinessRatesSelfCateringId.toString)

  def businessRatesPropertyEnquiry: Option[String] = cacheMap.getEntry[String](BusinessRatesPropertyEnquiryId.toString)

  def propertyEnglandAvailableLetsEnquiry: Option[String] = cacheMap.getEntry[String](PropertyEnglandAvailableLetsId.toString)

  def propertyEnglandActualLetsEnquiry: Option[String] = cacheMap.getEntry[String](PropertyEnglandActualLetsId.toString)

  def propertyWalesAvailableLetsEnquiry: Option[String] = cacheMap.getEntry[String](PropertyWalesAvailableLetsId.toString)

  def propertyWalesActualLetsEnquiry: Option[String] = cacheMap.getEntry[String](PropertyWalesActualLetsId.toString)

  def fairRentEnquiryEnquiry: Option[String] = cacheMap.getEntry[String](FairRentEnquiryId.toString)

  def contact(): Either[String, Contact] =
    (for {
      cd          <- contactDetails
      pa          <- propertyAddress
      eq          <- enquiryCategory orElse existingEnquiryCategory
      subcategory <- eq match {
                       case "council_tax"     => councilTaxSubcategory
                       case "business_rates"  => businessRatesSubcategory
                       case "housing_benefit" => getString(lastTellUsMorePage)
                       case "fair_rent"       => fairRentEnquiryEnquiry
                       case "other"           => otherSubcategory orElse Some("other")
                       case _                 => None
                     }
      message     <- enquiryCategory match {
                       case Some("housing_benefit") => getString(subcategory)
                       case _                       => tellUsMore.map(_.message).orElse(whatElse).orElse(anythingElse)
                     }
    } yield Contact(cd, pa, eq, subcategory, message))
      .toRight("Unable to parse")

}

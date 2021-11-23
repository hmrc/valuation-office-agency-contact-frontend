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
import uk.gov.hmrc.valuationofficeagencycontactfrontend.viewmodels.AnswerSection

import java.time.LocalDate

class UserAnswers(val cacheMap: CacheMap) {

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

  def housingAllowanceSubcategory: Option[String] = cacheMap.getEntry[String](HousingAllowanceSubcategoryId.toString)

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

  def propertyEnglandLets140DaysEnquiry: Option[String] = cacheMap.getEntry[String](PropertyEnglandLets140DaysId.toString)

  def propertyWalesLets140DaysEnquiry: Option[String] = cacheMap.getEntry[String](PropertyWalesLets140DaysId.toString)

  def propertyWalesLets70DaysEnquiry: Option[String] = cacheMap.getEntry[String](PropertyWalesLets70DaysId.toString)

  def fairRentEnquiryEnquiry: Option[String] = cacheMap.getEntry[String](FairRentEnquiryId.toString)

  def contact(): Either[String, Contact] = {

    val optionalContactModel = for {
      cd <- contactDetails
      pa <- propertyAddress
      eq <- enquiryCategory orElse existingEnquiryCategory
      subcategory <- eq match {
        case "council_tax" => councilTaxSubcategory
        case "business_rates" => businessRatesSubcategory
        case "fair_rent" => fairRentEnquiryEnquiry
        case "other" => otherSubcategory
        case _ => None
      }
      message <- tellUsMore.map(_.message).orElse(whatElse).orElse(anythingElse)
    } yield Contact(cd, pa, eq, subcategory, message)

    optionalContactModel match {
      case Some(c @ Contact(_, _, _, _, _)) => Right(c)
      case None => Left("Unable to parse")
    }
  }
}


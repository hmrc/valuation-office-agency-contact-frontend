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

import play.api.i18n.Messages
import uk.gov.hmrc.valuationofficeagencycontactfrontend.journey.JourneyMap.changeModePrefix
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes
import uk.gov.hmrc.valuationofficeagencycontactfrontend.journey.model.TellUsMorePage.lastTellUsMorePage
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.{CheckMode, NormalMode}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.AddressFormatters._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.ContactFormatter._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.viewmodels.AnswerRow

class CheckYourAnswersHelper(userAnswers: UserAnswers)(implicit messages: Messages, dateUtil: DateUtil) {

  def tellUsMore(keyMessage: String = "tellUsMore.heading"): Option[AnswerRow] = userAnswers.tellUsMore map {
    x => AnswerRow(keyMessage, s"${x.message}", false, routes.TellUsMoreController.onPageLoad(CheckMode).url)
  }

  def contactReason: Option[AnswerRow] = userAnswers.contactReason map {
    x => AnswerRow("contactReason.heading", s"contactReason.$x", true, routes.ContactReasonController.onPageLoad().url)
  }

  def existingEnquiryCategory: Option[AnswerRow] = userAnswers.existingEnquiryCategory map {
    x => AnswerRow("existingEnquiryCategory.heading", s"existingEnquiryCategory.$x", true, routes.ExistingEnquiryCategoryController.onPageLoad().url)
  }

  def refNumber: Option[AnswerRow] =  {
    val ref = userAnswers.refNumber.map(_.trim).filter(_ != "")
    Option(
      AnswerRow("refNumber.value", ref.getOrElse("site.not_provided"), ref.isEmpty, routes.RefNumberController.onPageLoad().url)
    )
  }

  def whatElse: Option[AnswerRow] = userAnswers.whatElse map {
    x => AnswerRow("whatElse.message", x, false, routes.WhatElseController.onPageLoad().url)
  }

  def anythingElse: Option[AnswerRow] = userAnswers.anythingElse map {
    answer => AnswerRow("anythingElse.checkYourAnswersLabel", answer, false, routes.AnythingElseTellUsController.onPageLoad().url)
  }

  def enquiryCategory: Option[AnswerRow] = userAnswers.enquiryCategory map {
    x => AnswerRow("enquiryCategory.heading", s"enquiryCategory.$x", true, routes.EnquiryCategoryController.onPageLoad(CheckMode).url)
  }

  def councilTaxSubcategory: Option[AnswerRow] = userAnswers.councilTaxSubcategory map {
    x => AnswerRow("councilTaxSubcategory.heading", s"councilTaxSubcategory.$x", true, routes.CouncilTaxSubcategoryController.onPageLoad(NormalMode).url)
  }

  def businessRatesSubcategory: Option[AnswerRow] = userAnswers.businessRatesSubcategory map {
    x =>
      AnswerRow("businessRatesSubcategory.heading", s"businessRatesSubcategory.$x", true, routes.BusinessRatesSubcategoryController.onPageLoad(NormalMode).url)
  }

  def propertyAddress: Option[AnswerRow] = userAnswers.propertyAddress map {
    addr => AnswerRow("propertyAddress.heading", formattedPropertyAddress(addr, "<br>"), false, routes.PropertyAddressController.onPageLoad(CheckMode).url)
  }

  def contactDetails: Option[AnswerRow] = userAnswers.contactDetails map {
    _ => AnswerRow("contactDetails.heading", formattedContactDetails(userAnswers.contactDetails, "<br>"), false,
      routes.ContactDetailsController.onPageLoad(CheckMode).url)
  }

  def datePropertyChanged(keyMessage: String = "datePropertyChanged.poorRepair.heading"): Option[AnswerRow] = userAnswers.datePropertyChanged map {
    date => AnswerRow(keyMessage, dateUtil.formattedLocalDate(date), false, routes.DatePropertyChangedController.onPageLoad().url)
  }

  def councilTaxBusinessEnquiry: Option[AnswerRow] = userAnswers.councilTaxBusinessEnquiry.map {
    x => AnswerRow("councilTaxBusinessEnquiry.heading", s"councilTaxBusinessEnquiry.form.$x", true, routes.CouncilTaxBusinessController.onPageLoad().url)
  }

  def fairRentEnquiryEnquiry: Option[AnswerRow] = userAnswers.fairRentEnquiryEnquiry.map {
    x => AnswerRow("housingBenefits.heading", s"housingBenefits.form.$x", false, routes.FairRentEnquiryController.onPageLoad().url)
  }

  def housingBenefitTellUsMore: Option[AnswerRow] = userAnswers.getString(lastTellUsMorePage) map {
    pageKey => AnswerRow(s"housingBenefitSubcategory.$pageKey", userAnswers.getString(pageKey).getOrElse(""), answerIsMessageKey = false,
      routes.JourneyController.onPageLoad(changeModePrefix + pageKey).url)
  }

}

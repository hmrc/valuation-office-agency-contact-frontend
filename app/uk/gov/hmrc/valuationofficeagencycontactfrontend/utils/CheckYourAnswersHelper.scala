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

import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.CheckMode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.AddressFormatters._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.ContactFormatter._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.viewmodels.AnswerRow

class CheckYourAnswersHelper(userAnswers: UserAnswers) {

  def tellUsMore: Option[AnswerRow] = userAnswers.tellUsMore map {
    x => AnswerRow("tellUsMore.checkYourAnswersLabel", s"${x.message}", false, routes.TellUsMoreController.onPageLoad(CheckMode).url)
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
    x => AnswerRow("enquiryCategory.checkYourAnswersLabel", s"enquiryCategory.$x", true, routes.EnquiryCategoryController.onPageLoad(CheckMode).url)
  }

  def councilTaxSubcategory: Option[AnswerRow] = userAnswers.councilTaxSubcategory map {
    x => AnswerRow("councilTaxSubcategory.checkYourAnswersLabel", s"councilTaxSubcategory.$x", true, routes.CouncilTaxSubcategoryController.onPageLoad(CheckMode).url)
  }

  def businessRatesSubcategory: Option[AnswerRow] = userAnswers.businessRatesSubcategory map {
    x => AnswerRow("businessRatesSubcategory.checkYourAnswersLabel", s"businessRatesSubcategory.$x", true, routes.BusinessRatesSubcategoryController.onPageLoad(CheckMode).url)
  }

  def propertyAddress: Option[AnswerRow] = userAnswers.propertyAddress map {
    addr => AnswerRow("propertyAddress.heading", formattedPropertyAddress(addr, "<br>"), false, routes.PropertyAddressController.onPageLoad(CheckMode).url)
  }

  def contactDetails: Option[AnswerRow] = userAnswers.contactDetails map {
    _ => AnswerRow("contactDetails.heading", formattedContactDetails(userAnswers.contactDetails, "<br>"), false, routes.ContactDetailsController.onPageLoad(CheckMode).url)
  }

}

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

import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.CheckMode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.viewmodels.{AnswerRow, RepeaterAnswerRow, RepeaterAnswerSection}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.AddressFormatters._

class CheckYourAnswersHelper(userAnswers: UserAnswers) {

  def tellUsMore: Option[AnswerRow] = userAnswers.tellUsMore map {
    x => AnswerRow("tellUsMore.checkYourAnswersLabel", s"${x.message}", false, routes.TellUsMoreController.onPageLoad(CheckMode).url)
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

  def businessRatesAddress: Option[AnswerRow] = userAnswers.businessRatesAddress map {
    x => AnswerRow("businessRatesAddress.checkYourAnswersLabel", formattedBusinessRatesAddress(userAnswers.businessRatesAddress, "<br>"), false, routes.BusinessRatesAddressController.onPageLoad(CheckMode).url)
  }

  def councilTaxAddress: Option[AnswerRow] = userAnswers.councilTaxAddress map {
    x => AnswerRow("councilTaxAddress.checkYourAnswersLabel", formattedCouncilTaxAddress(userAnswers.councilTaxAddress, "<br>"), false, routes.CouncilTaxAddressController.onPageLoad(CheckMode).url)
  }

  def contactDetails: Option[AnswerRow] = userAnswers.contactDetails map {
    x => AnswerRow("contactDetails.checkYourAnswersLabel", s"${x.firstName} ${x.lastName} ${x.email}  ${x.contactNumber}", false, routes.ContactDetailsController.onPageLoad(CheckMode).url)
  }

}

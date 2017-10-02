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

class CheckYourAnswersHelper(userAnswers: UserAnswers) {

  def enquiryCategory: Option[AnswerRow] = userAnswers.enquiryCategory map {
    x => AnswerRow("enquiryCategory.checkYourAnswersLabel", s"enquiryCategory.$x", true, routes.EnquiryCategoryController.onPageLoad(CheckMode).url)
  }

  def councilTaxSubcategory: Option[AnswerRow] = userAnswers.councilTaxSubcategory map {
    x => AnswerRow("councilTaxSubcategory.checkYourAnswersLabel", s"councilTaxSubcategory.$x", true, routes.CouncilTaxSubcategoryController.onPageLoad(CheckMode).url)
  }

  def contactDetails: Option[AnswerRow] = userAnswers.contactDetails map {
    x => AnswerRow("contactDetails.checkYourAnswersLabel", s"${x.firstName} ${x.lastName} ${x.lastName}", false, routes.ContactDetailsController.onPageLoad(CheckMode).url)
  }

  def businessRatesSubcategory: Option[AnswerRow] = userAnswers.businessRatesSubcategory map {
    x => AnswerRow("businessRatesSubcategory.checkYourAnswersLabel", s"businessRatesSubcategory.$x", true, routes.BusinessRatesSubcategoryController.onPageLoad(CheckMode).url)
  }

}

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

package uk.gov.hmrc.valuationofficeagencycontactfrontend.journey.pages

import play.api.mvc.Call
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes
import uk.gov.hmrc.valuationofficeagencycontactfrontend.journey.model.CategoryRouter
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.NormalMode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.UserAnswers

/**
 * @author Yuriy Tumakha
 */
object HousingBenefitAllowancesRouter extends CategoryRouter(
  key = "ha-hb-enquiry",
  fieldId = "housingBenefitAllowances",
  options = Seq(LocalHousingAllowanceRates.key, HousingBenefitEnquiry.key, OtherHAHBEnquiry.key)
) {
  override def previousPage: UserAnswers => Call = _ => routes.EnquiryCategoryController.onPageLoad(NormalMode)

  override def helpWithService: Option[String] = Some("help_with_service_fair_rent")
}

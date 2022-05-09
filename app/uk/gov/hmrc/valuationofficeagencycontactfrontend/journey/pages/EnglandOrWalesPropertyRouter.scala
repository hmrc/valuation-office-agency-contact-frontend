/*
 * Copyright 2022 HM Revenue & Customs
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

import play.api.i18n.Lang
import play.api.mvc.Call
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers.BusinessRatesSubcategoryId
import uk.gov.hmrc.valuationofficeagencycontactfrontend.journey.model.CategoryRouter
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.NormalMode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.UserAnswers

/**
 * @author Yuriy Tumakha
 */
object EnglandOrWalesPropertyRouter extends CategoryRouter(
  key = "England-or-Wales-property",
  fieldId = "businessRatesJurisdiction",
  options = Seq("england", "wales")
) {

  private val langMap: Map[String, Lang] = Map(
    "england" -> Lang("en"),
    "wales" -> Lang("cy")
  )

  override def previousPage: UserAnswers => Call = _ => routes.BusinessRatesSubcategoryController.onPageLoad(NormalMode)

  override def nextPage: UserAnswers => Call =
    _.getString(BusinessRatesSubcategoryId.toString).getOrElse("") match {
      case "business_rates_change_valuation" => routes.BusinessRatesSubcategoryController.onChangeValuationPageLoad()
      case "business_rates_changes" => routes.BusinessRatesChallengeController.onAreaChangePageLoad()
      case "business_rates_demolished" => routes.BusinessRatesSubcategoryController.onDemolishedPageLoad()
      case _ => appStartPage
    }

  override def nextLang: UserAnswers => Option[Lang] =
    getValue(_) flatMap langMap.get

}

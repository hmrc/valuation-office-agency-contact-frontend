/*
 * Copyright 2025 HM Revenue & Customs
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

package uk.gov.hmrc.valuationofficeagencycontactfrontend.views

import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.NormalMode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.behaviours.ViewBehaviours
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{businessRatesValuation => business_rates_valuation}
import play.twirl.api.HtmlFormat

class BusinessRatesValuationViewSpec extends ViewBehaviours {

  def businessRatesValuation: html.businessRatesValuation = app.injector.instanceOf[business_rates_valuation]

  def view: () => HtmlFormat.Appendable = () => businessRatesValuation(frontendAppConfig)(using fakeRequest, messages)

  "Business rates valuation  view" must {
    behave like normalPage(
      view,
      "businessRatesValuation",
      "title",
      "heading",
      "p1.url",
      "p1.part1",
      "p1.part2",
      "subheading",
      "p2",
      "p3.url",
      "p3",
      "p4.url",
      "p4"
    )

    "has a link marked with site.back leading to the Business Rates valuation page" in {
      val doc          = asDocument(view())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("site.back")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.BusinessRatesSubcategoryController.onPageLoad(NormalMode).url
    }
  }
}

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

package uk.gov.hmrc.valuationofficeagencycontactfrontend.views

import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.NormalMode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.behaviours.ViewBehaviours
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{businessRatesChangeValuation => change_valuation}

class BusinessRatesChangeValuationViewSpec extends ViewBehaviours {

  def businessRatesChangeValuation = app.injector.instanceOf[change_valuation]

  def view = () => businessRatesChangeValuation(frontendAppConfig, NormalMode)(fakeRequest, messages)

  "Business rates change valuation online view" must {
    behave like normalPage(view, "businessRatesValuationOnline", "title", "heading",
      "p1.url", "p1.part1", "p1.part2", "subheading1", "p2", "subheading1", "p3.url", "p3", "p4.url", "p4")

    "has a link marked with site.back leading to the Business Rates change valuation page" in {
      val doc = asDocument(view())
      val backlinkText = doc.select("a[class=govuk-back-link govuk-!-margin-top-0 govuk-!-margin-bottom-0]").text()
      backlinkText mustBe messages("site.back")
      val backlinkUrl = doc.select("a[class=govuk-back-link govuk-!-margin-top-0 govuk-!-margin-bottom-0]").attr("href")
      backlinkUrl mustBe uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.BusinessRatesSubcategoryController.onPageLoad(NormalMode).url
    }
  }
}


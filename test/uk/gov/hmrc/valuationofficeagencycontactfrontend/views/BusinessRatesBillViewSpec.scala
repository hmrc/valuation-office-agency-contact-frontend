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
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{businessRatesBill => business_rates_bill}
import play.twirl.api.HtmlFormat

class BusinessRatesBillViewSpec extends ViewBehaviours {

  def businessRatesBill: html.businessRatesBill = app.injector.instanceOf[business_rates_bill]

  def view: () => HtmlFormat.Appendable = () => businessRatesBill(frontendAppConfig)(using fakeRequest, messages)

  "Business Rates Bill view" must {
    behave like normalPage(view, "businessRatesBill", "title", "heading", "p1", "subheading", "url", "url1")

    "has a link marked with site.back leading to the Council Tax annexe self contained Page" in {
      val doc          = asDocument(view())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("site.back")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.BusinessRatesSubcategoryController.onPageLoad(NormalMode).url
    }
  }

}

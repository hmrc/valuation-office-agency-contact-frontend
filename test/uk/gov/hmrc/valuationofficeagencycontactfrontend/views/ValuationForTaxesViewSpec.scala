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
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{valuationForTaxes => valuation_for_taxes}
import play.twirl.api.HtmlFormat

class ValuationForTaxesViewSpec extends ViewBehaviours {

  def valuationForTaxes: html.valuationForTaxes = app.injector.instanceOf[valuation_for_taxes]

  def view: () => HtmlFormat.Appendable = () => valuationForTaxes(frontendAppConfig)(using fakeRequest, messages)

  "Valuation For Taxes view" must {

    behave like normalPage(view, "valuationForTaxes", "title", "heading", "email-title", "email")

    "has a link marked with site.back leading to the Enquiry Category Page" in {
      val doc          = asDocument(view())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("site.back")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.EnquiryCategoryController.onPageLoad(NormalMode).url
    }

    "The Start again link links to the Enquiry Category Controller onPageLoad method" in {
      val doc  = asDocument(view())
      val href = doc.getElementsByClass("govuk-link").attr("href")
      assert(href == uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.Application.start().url.toString || href.contains(
        "/hmrc-frontend/language/cy"
      ))
    }
  }

}

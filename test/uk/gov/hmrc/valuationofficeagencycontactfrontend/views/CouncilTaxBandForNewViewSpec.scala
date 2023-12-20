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

package uk.gov.hmrc.valuationofficeagencycontactfrontend.views

import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.NormalMode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.behaviours.ViewBehaviours
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{councilTaxBandForNew => council_tax_band_for_new}
import play.twirl.api.HtmlFormat

class CouncilTaxBandForNewViewSpec extends ViewBehaviours {

  def councilTaxBandForNew: html.councilTaxBandForNew = app.injector.instanceOf[council_tax_band_for_new]

  def view: () => HtmlFormat.Appendable = () => councilTaxBandForNew(frontendAppConfig)(fakeRequest, messages)

  "Council Tax Band For A New Property view" must {
    behave like normalPage(view, "councilTaxBandForNew", "title", "heading", "subheading", "p1.part1", "p1.url", "p1.part2", "p2", "p3", "p4", "p4.url", "p4")

    "has a link marked with site.back leading to the Council Tax Band For A New Property" in {
      val doc          = asDocument(view())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("site.back")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.CouncilTaxSubcategoryController.onPageLoad(NormalMode).url
    }
  }
}

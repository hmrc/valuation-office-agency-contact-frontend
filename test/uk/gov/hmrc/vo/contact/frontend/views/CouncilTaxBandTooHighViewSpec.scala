/*
 * Copyright 2026 HM Revenue & Customs
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

package uk.gov.hmrc.vo.contact.frontend.views

import uk.gov.hmrc.vo.contact.frontend.models.NormalMode
import uk.gov.hmrc.vo.contact.frontend.views.behaviours.ViewBehaviours
import uk.gov.hmrc.vo.contact.frontend.views.html.{councilTaxBandTooHigh => council_tax_band_too_high}
import play.twirl.api.HtmlFormat

class CouncilTaxBandTooHighViewSpec extends ViewBehaviours:

  def councilTaxBandTooHigh: html.councilTaxBandTooHigh = inject[council_tax_band_too_high]

  def view: () => HtmlFormat.Appendable = () => councilTaxBandTooHigh()(using getRequest, messages)

  "Council Tax Band Too High view" should {
    behave like normalPage(
      view,
      "councilTaxBandTooHigh",
      "title",
      "p1.part1",
      "p1.part2",
      "p1.url",
      "subheading",
      "p2",
      "p2.url",
      "p3",
      "p3.url",
      "p4",
      "p4.url"
    )

    "has a link marked with site.back leading to the Council Tax Band Too High Page" in {
      val doc          = asDocument(view())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText shouldBe messages("site.back")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl shouldBe uk.gov.hmrc.vo.contact.frontend.controllers.routes.CouncilTaxSubcategoryController.onPageLoad(NormalMode).url
    }
  }

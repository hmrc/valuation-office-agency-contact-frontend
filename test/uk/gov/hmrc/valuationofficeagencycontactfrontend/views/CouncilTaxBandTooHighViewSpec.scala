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

class CouncilTaxBandTooHighViewSpec extends ViewBehaviours {

  def valuationAdvice = app.injector.instanceOf[council_tax_band_too_high]

  def view = () => valuationAdvice(frontendAppConfig)(fakeRequest, messages)

  "Council Tax Band Too High view" must {
    behave like normalPage(view, "councilTaxBandTooHigh", "title", "heading",
      "body", "subHeading", "subBody")

    "has a link marked with site.back leading to the Council Tax Band Too High Page" in {
      val doc = asDocument(view())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("site.back")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.CouncilTaxSubcategoryController.onPageLoad(NormalMode).url
    }

    "contain a title" in {
      val doc = asDocument(view())
      assert(doc.title().equals(messages("councilTaxBandTooHigh.title")))
    }

    "contain a heading" in {
      val doc = asDocument(view())
      assert(doc.body().toString.contains(messages("councilTaxBandTooHigh.heading")))
      doc.body().getElementsByClass("govuk-heading-l").text() mustBe messages("councilTaxBandTooHigh.heading")
    }

    "contain a body text" in {
      val doc = asDocument(view())
      assert(doc.toString.contains(messages("councilTaxBandTooHigh.body")))
    }

    "contain a sub-heading" in {
      val doc = asDocument(view())
      assert(doc.body().toString.contains(messages("councilTaxBandTooHigh.subHeading")))
      doc.body().getElementsByClass("govuk-heading-m").text() mustBe messages("councilTaxBandTooHigh.subHeading")
    }

    "contain a sub-body" in {
      val doc = asDocument(view())
      assert(doc.toString.contains(messages("councilTaxBandTooHigh.subBody")))
    }
  }
}

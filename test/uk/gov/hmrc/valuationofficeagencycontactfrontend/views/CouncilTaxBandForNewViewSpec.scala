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
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{councilTaxBandForNew => council_tax_band_for_new}

class CouncilTaxBandForNewViewSpec extends ViewBehaviours {

  def councilTaxBandForNew = app.injector.instanceOf[council_tax_band_for_new]

  def view = () => councilTaxBandForNew(frontendAppConfig)(fakeRequest, messages)

  "Council Tax Band For A New Property view" must {
    behave like normalPage(view, "councilTaxBandForNew", "title", "heading",
      "p1", "p2" , "subHeading", "p3", "p4")

    "has a link marked with site.back leading to the Council Tax Band Too High Page" in {
      val doc = asDocument(view())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("site.back")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.CouncilTaxSubcategoryController.onPageLoad(NormalMode).url
    }

    "contain a title" in {
      val doc = asDocument(view())
      assert(doc.title().equals(messages("councilTaxBandForNew.title")))
    }

    "contain a heading" in {
      val doc = asDocument(view())
      assert(doc.body().toString.contains(messages("councilTaxBandForNew.heading")))
      doc.body().getElementsByClass("govuk-heading-l").text() mustBe messages("councilTaxBandForNew.heading")
    }

    "contain four paragraphs that include three bullet points" in {
      val doc = asDocument(view())

      doc.body().getElementsByClass("govuk-list govuk-list--bullet").size() mustBe 1
      doc.body().getElementsByClass("govuk-list govuk-list--bullet").first().childNodeSize() mustBe 3
      assert(doc.body().toString.contains(messages("councilTaxBandForNew.p2")))
      assert(doc.body().toString.contains(messages("councilTaxBandForNew.p3")))
      assert(doc.body().toString.contains(messages("councilTaxBandForNew.p4")))
    }

    "contain a sub-heading" in {
      val doc = asDocument(view())
      assert(doc.body().toString.contains(messages("councilTaxBandForNew.subHeading")))
      doc.body().getElementsByClass("govuk-heading-m").text() mustBe messages("councilTaxBandForNew.subHeading")
    }
  }
}

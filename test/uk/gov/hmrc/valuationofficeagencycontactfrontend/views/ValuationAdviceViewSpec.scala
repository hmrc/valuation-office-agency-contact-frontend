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
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{valuationAdvice => valuation_advice}

class ValuationAdviceViewSpec extends ViewBehaviours {

  def valuationAdvice = app.injector.instanceOf[valuation_advice]

  def view = () => valuationAdvice(frontendAppConfig)(fakeRequest, messages)

  "Valuation Advice view" must {

    behave like normalPage(view, "valuationAdvice", "title", "heading", "email-title",
    "email")

    "has a link marked with site.back leading to the Enquiry Category Page" in {
      val doc = asDocument(view())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("site.back")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.EnquiryCategoryController.onPageLoad(NormalMode).url
    }

    "contain start again link " in {
      val doc = asDocument(view())
      val startAgainButton = doc.getElementsByClass("govuk-link").text()
      assert(startAgainButton == messages("site.start-again") || startAgainButton.contains("Newid yr iaith ir Gymraeg Cymraeg"))
    }
  }
}

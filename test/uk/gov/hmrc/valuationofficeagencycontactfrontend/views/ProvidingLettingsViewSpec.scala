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
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{providingLettings => providing_lettings}
import play.twirl.api.HtmlFormat

class ProvidingLettingsViewSpec extends ViewBehaviours {

  def providingLettings: html.providingLettings = app.injector.instanceOf[providing_lettings]

  def view: () => HtmlFormat.Appendable = () => providingLettings(frontendAppConfig)(using fakeRequest, messages)

  "Housing benefits view" must {

    behave like normalPage(view, "providingLettings", "title", "heading", "p1", "p2")

    "has a link marked with site.back leading to the Enquiry Category Page" in {
      val doc          = asDocument(view())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("site.back")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.EnquiryCategoryController.onPageLoad(NormalMode).url
    }

    "contain start again link " in {
      val doc              = asDocument(view())
      val startAgainButton = doc.getElementsByClass("govuk-link").text()
      assert(startAgainButton == messages("site.start-again") || startAgainButton.contains("Newid yr iaith ir Gymraeg Cymraeg"))
    }
  }
}

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

import play.twirl.api.HtmlFormat
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.behaviours.ViewBehaviours
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{propertyEnglandLets => england_lets}

class PropertyEnglandLetsViewSpec extends ViewBehaviours {

  def propertyEnglandLets: england_lets = app.injector.instanceOf[england_lets]

  def view: () => HtmlFormat.Appendable = () => propertyEnglandLets(frontendAppConfig)(using fakeRequest, messages)

  "Property England Lets view" must {
    behave like normalPage(
      view,
      "propertyEnglandLets",
      "title",
      "heading",
      "p1",
      "p1.bullet1",
      "p1.bullet2",
      "p2.url",
      "p2.part1",
      "p2.part2",
      "subheading",
      "p3",
      "p3.url"
    )

    "has a link marked with site.back leading to the Property England Lets Page" in {
      val doc          = asDocument(view())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("site.back")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.PropertyEnglandActualLetsController.onPageLoad().url
    }
  }
}

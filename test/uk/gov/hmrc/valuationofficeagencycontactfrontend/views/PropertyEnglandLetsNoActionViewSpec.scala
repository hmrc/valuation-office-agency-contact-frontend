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
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{propertyEnglandLetsNoAction => england_lets_no_action}

class PropertyEnglandLetsNoActionViewSpec extends ViewBehaviours {

  def propertyEnglandLetsNoAction: england_lets_no_action = app.injector.instanceOf[england_lets_no_action]

  def backLink: String = uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.PropertyEnglandAvailableLetsController.onPageLoad().url

  def viewEnglandAvailable140Nights: () => HtmlFormat.Appendable = () => propertyEnglandLetsNoAction(frontendAppConfig, backLink)(using fakeRequest, messages)

  "Property Wales Lets No Action view" must {
    behave like normalPage(
      viewEnglandAvailable140Nights,
      "businessRatesSelfCateringNoBusinessRate",
      "title",
      "heading",
      "p1",
      "p2",
      "p2.bullet1",
      "p2.bullet2",
      "p3.part1",
      "p3.part2",
      "p3.part3"
    )

    "has a link marked with site.back leading to the Property England Lets 140 Page" in {
      val doc          = asDocument(viewEnglandAvailable140Nights())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("site.back")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.PropertyEnglandAvailableLetsController.onPageLoad().url
    }
  }
}

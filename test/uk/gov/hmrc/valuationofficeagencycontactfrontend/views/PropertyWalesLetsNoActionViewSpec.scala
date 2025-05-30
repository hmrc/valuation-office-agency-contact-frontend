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

import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.behaviours.ViewBehaviours
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{propertyWalesLetsNoAction => wales_lets_no_action}
import play.twirl.api.HtmlFormat

class PropertyWalesLetsNoActionViewSpec extends ViewBehaviours {

  def propertyWalesLetsNoAction: html.propertyWalesLetsNoAction = app.injector.instanceOf[wales_lets_no_action]

  def wales140DayBackLink: String = uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.PropertyWalesAvailableLetsController.onPageLoad().url
  def wales70DayBackLink: String  = uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.PropertyWalesActualLetsController.onPageLoad().url

  def view140Days: () => HtmlFormat.Appendable = () => propertyWalesLetsNoAction(frontendAppConfig, wales140DayBackLink)(using fakeRequest, messages)
  def view7Days: () => HtmlFormat.Appendable   = () => propertyWalesLetsNoAction(frontendAppConfig, wales70DayBackLink)(using fakeRequest, messages)

  "Property Wales Lets No Action view" must {
    behave like normalPage(
      view140Days,
      "businessRatesSelfCateringNoBusinessRateWales",
      "title",
      "heading",
      "p1",
      "p2",
      "p3",
      "p4",
      "p5.part1",
      "p5.part1.url",
      "p5.part2",
      "p5.part3"
    )

    "has a link marked with site.back leading to the Property Wales Lets 140 Page" in {
      val doc          = asDocument(view140Days())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("site.back")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.PropertyWalesAvailableLetsController.onPageLoad().url
    }
  }
}

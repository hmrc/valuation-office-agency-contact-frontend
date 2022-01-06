/*
 * Copyright 2022 HM Revenue & Customs
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
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{propertyWalesLets => wales_lets}

class PropertyWalesLetsViewSpec  extends ViewBehaviours {

  def propertyWalesLets = app.injector.instanceOf[wales_lets]

  def view = () => propertyWalesLets(frontendAppConfig)(fakeRequest, messages)

  "Property England Lets view" must {
    behave like normalPage(view, "propertyWalesLets", "title", "heading",
      "p1", "p1.bl1", "p1.bl2", "p2.url", "p2.part1", "p2.part2", "subheading", "p3", "p3.url")

    "has a link marked with site.back leading to the Property Wales Lets Page" in {
      val doc = asDocument(view())
      val backlinkText = doc.select("a[class=govuk-back-link govuk-!-margin-top-0 govuk-!-margin-bottom-0]").text()
      backlinkText mustBe messages("site.back")
      val backlinkUrl = doc.select("a[class=govuk-back-link govuk-!-margin-top-0 govuk-!-margin-bottom-0]").attr("href")
      backlinkUrl mustBe uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.PropertyWalesLets70DaysController.onPageLoad().url
    }
  }
}

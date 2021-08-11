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

import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.behaviours.ViewBehaviours
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{propertyWalesLetsNoAction => wales_lets_no_action}

class PropertyWalesLetsNoActionViewSpec  extends ViewBehaviours {

  def propertyWalesLetsNoAction = app.injector.instanceOf[wales_lets_no_action]

  def view = () => propertyWalesLetsNoAction(frontendAppConfig)(fakeRequest, messages)

  "Property Wales Lets No Action view" must {
    behave like normalPage(view, "businessRatesSelfCateringNoBusinessRateWales", "title", "heading",
      "p1", "p2", "p3", "p4.part1", "p4.part1.url", "p4.part2", "p4.part3")

    "has a link marked with site.back leading to the Property Wales Lets 140 Page" in {
      val doc = asDocument(view())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("site.back")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.PropertyWalesLets140DaysController.onPageLoad().url
    }
  }
}
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

import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes
import uk.gov.hmrc.valuationofficeagencycontactfrontend.journey.pages.EnglandOrWalesPropertyRouter
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.NormalMode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.behaviours.ViewBehaviours
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{businessRatesPropertyDemolished => property_demolished}

class BusinessRatesPropertyDemolishedViewSpec extends ViewBehaviours {

  def businessRatesPropertyDemolished = app.injector.instanceOf[property_demolished]

  def view = () => businessRatesPropertyDemolished(frontendAppConfig, NormalMode)(fakeRequest, messages)

  "Business Rates Property Demolished view" must {
    behave like normalPage(view, "businessRatesPropertyDemolished", "title", "heading",
      "p1.part1", "p1.part2", "p1.url", "p2", "step1", "step2", "step3", "subheading", "p3", "p3.url", "p4", "p4.url")

    "has a link marked with site.back leading to the Business Rates Property Demolished Page" in {
      val doc = asDocument(view())
      val backlinkText = doc.select("a[class=govuk-back-link govuk-!-margin-top-0 govuk-!-margin-bottom-0]").text()
      backlinkText mustBe messages("site.back")
      val backlinkUrl = doc.select("a[class=govuk-back-link govuk-!-margin-top-0 govuk-!-margin-bottom-0]").attr("href")
      backlinkUrl mustBe routes.JourneyController.onPageLoad(EnglandOrWalesPropertyRouter.key).url
    }
  }
}


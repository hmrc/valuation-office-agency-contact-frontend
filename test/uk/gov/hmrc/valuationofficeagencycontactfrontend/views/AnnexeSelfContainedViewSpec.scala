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
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{annexeSelfContained => annexe_self_contained}

class AnnexeSelfContainedViewSpec extends ViewBehaviours {

  def councilTaxAnnexeSelfContained = app.injector.instanceOf[annexe_self_contained]

  def view = () => councilTaxAnnexeSelfContained(frontendAppConfig)(fakeRequest, messages)

  "Council Tax Bill view" must {
    behave like normalPage(view, "annexeSelfContained", "title", "heading",
      "p1", "p2", "subheading")

    "has a link marked with site.back leading to the Council Tax annexe self contained Page" in {
      val doc = asDocument(view())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("site.back")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.CouncilTaxAnnexeController.onHaveCookingWashingPageLoad().url
    }
  }
}

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
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{councilTaxSmartLinks => council_tax_smart_links}

class CouncilTaxSmartLinksViewSpec extends ViewBehaviours {

  def councilTaxSmartLinks = app.injector.instanceOf[council_tax_smart_links]

  def view = () => councilTaxSmartLinks(frontendAppConfig)(fakeRequest, messages)

  "Council Tax Smart Links View" must {

    behave like normalPage(view, "checkBeforeYouStart.council_tax", "para1", "subheading", "para1",
      "para2", "para3", "para4", "para5", "para6", "para7", "para8", "url1", "url2", "url3", "url4", "para9", "para10", "para11")
  }

  "The Continue button links to the goToCouncilTaxSubcategoryPage method" in {
    val doc = asDocument(view())
    val href = doc.getElementsByAttributeValue("class", "govuk-button").first().attr("href")
    assert(href == uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.CouncilTaxSmartLinksController.goToCouncilTaxSubcategoryPage().url.toString)
  }

  "The Continue button uses the button--get-started class" in {
    val doc = asDocument(view())
    val button = doc.select("a[class~=govuk-button]")
    assert(button.size() == 1)
  }

  "has a link marked with site.back leading to the Enquiry Category Page" in {
    val doc = asDocument(view())
    val backlinkText = doc.select("a[class=govuk-back-link]").text()
    backlinkText mustBe messages("site.back")
    val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
    backlinkUrl mustBe uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.EnquiryCategoryController.onPageLoad(NormalMode).url
  }

}

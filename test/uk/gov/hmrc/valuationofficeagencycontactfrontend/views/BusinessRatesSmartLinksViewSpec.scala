/*
 * Copyright 2020 HM Revenue & Customs
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
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{businessRatesSmartLinks => business_rates_smart_links}

class BusinessRatesSmartLinksViewSpec extends ViewBehaviours {

  def businessRatesSmartLinks = app.injector.instanceOf[business_rates_smart_links]

  def view = () => businessRatesSmartLinks(frontendAppConfig)(fakeRequest, messages)

  "Business Rates Smart Links View" must {

    behave like normalPage(view, "checkBeforeYouStart.business_rates", "para1", "subheading",
      "covid.link", "covid.para", "run.home.link", "run.home.para", "holiday.lets.link", "holiday.lets.para",
      "para1", "para2", "para3", "para4", "para5", "para6", "para7", "para8", "para9", "para10", "para11",
      "covid.url", "run.home.url", "holiday.lets.url", "url1", "url2", "url3", "url4", "url5")
  }

  "The Continue button links to the goToBusinessRatesSubcategoryPage method" in {
    val doc = asDocument(view())
    val href = doc.getElementsByAttributeValue("class", "govuk-button").first().attr("href")
    assert(href == uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.BusinessRatesSmartLinksController.goToBusinessRatesSubcategoryPage().url.toString)
  }

  "The Continue button uses the button--get-started class" in {
    val doc = asDocument(view())
    val button = doc.select("a[class~=govuk-button]")
    assert(button.size() == 1)
  }

  "has a link marked with site.back leading to the Business Rates Smart Links Page" in {
    val doc = asDocument(view())
    val backlinkText = doc.select("a[class=govuk-back-link]").text()
    backlinkText mustBe messages("site.back")
    val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
    backlinkUrl mustBe uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.EnquiryCategoryController.onPageLoad(NormalMode).url
  }

}

/*
 * Copyright 2017 HM Revenue & Customs
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

import org.jsoup.select.Elements
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.NormalMode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.behaviours.ViewBehaviours
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.index

class IndexViewSpec extends ViewBehaviours {

  def view = () => index(frontendAppConfig)(fakeRequest, messages)

  "Index view" must {

    behave like normalPage(view, "checkBeforeYouStart", "para1", "council_tax.subheading", "council_tax.para1", "council_tax.para2", "council_tax.para3",
      "council_tax.para4", "council_tax.para5", "council_tax.para6", "council_tax.para7", "council_tax.para8", "business_rates.subheading", "business_rates.para1",
      "business_rates.para2", "business_rates.para3", "business_rates.para4", "business_rates.para5", "business_rates.para6", "business_rates.para7",
      "business_rates.para8", "business_rates.para9", "business_rates.para10", "business_rates.para11", "council_tax.url1", "council_tax.url2", "council_tax.url3",
    "council_tax.url4", "business_rates.url1", "business_rates.url2", "business_rates.url3", "business_rates.url4", "business_rates.url5")
  }

  "The Continue button links to the enquiryCategoryController onPageLoad method" in {
    val doc = asDocument(view())
    val href = doc.getElementById("continue").attr("href")
    assert(href == uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.EnquiryCategoryController.onPageLoad(NormalMode).url.toString)
  }

  "The Continue button uses the button--get-started class" in {
    val doc = asDocument(view())
    val button = doc.select("a[class~=button--get-started]")
    assert(button.size() == 1)
  }

}

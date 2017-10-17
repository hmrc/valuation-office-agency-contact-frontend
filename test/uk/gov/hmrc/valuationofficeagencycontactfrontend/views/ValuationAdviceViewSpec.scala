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
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.BusinessRatesSubcategoryForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.NormalMode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.behaviours.ViewBehaviours
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.valuationAdvice

class ValuationAdviceViewSpec extends ViewBehaviours {

  def view = () => valuationAdvice(frontendAppConfig)(fakeRequest, messages)

  "Valuation Advice view" must {

    behave like normalPage(view, "valuationAdvice", "subheading", "paragraph", "first-contact.title", "first-contact.email", "first-contact.phone", "second-contact.title",
      "second-contact.email", "second-contact.phone", "email-title", "phone-title", "response-paragraph", "first-contact.phone-url", "second-contact.phone-url")

    "has a link marked with site.back leading to the Enquiry Category Page" in {
      val doc = asDocument(view())
      val backlinkText = doc.select("a[class=link-back]").text()
      backlinkText mustBe messages("site.back")
      val backlinkUrl = doc.select("a[class=link-back]").attr("href")
      backlinkUrl mustBe uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.EnquiryCategoryController.onPageLoad(NormalMode).url
    }

    "contain start again button " in {
      val doc = asDocument(view())
      val startAgainButton = doc.getElementById("start-again").text()
      assert(startAgainButton == messages("site.start-again"))
    }

    "The Start again button links to the Index Controller onPageLoadWithNewSession method" in {
      val doc = asDocument(view())
      val href = doc.getElementById("start-again").attr("href")
      assert(href == uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.IndexController.onPageLoadWithNewSession().url.toString)
    }
  }

}

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

import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.behaviours.ViewBehaviours
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.confirmation

class ConfirmationViewSpec extends ViewBehaviours {

  def view = () => confirmation(frontendAppConfig)(fakeRequest, messages)

  "Confirmation view" must {

    behave like normalPage(view, "confirmation", "para1", "para2", "para3", "para4", "para5", "reference",
      "survey-para1", "survey-para2")

    "contain start again button " in {
      val doc = asDocument(view())
      val startAgainButton = doc.getElementById("start-again").text()
      assert(startAgainButton == messages("site.start-again"))
    }

    "contain a print button " in {
      val doc = asDocument(view())
      val printButton = doc.getElementById("print-link").text()
      val href = doc.getElementById("print-link").attr("href")
      assert(printButton == messages("site.print.link"))
      assert(href == "javascript:window.print()")
    }

    "The Start again button links to the Index Controller onPageLoad method" in {
      val doc = asDocument(view())
      val href = doc.getElementById("start-again").attr("href")
      assert(href == uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.IndexController.onPageLoad().url.toString)
    }
  }

}

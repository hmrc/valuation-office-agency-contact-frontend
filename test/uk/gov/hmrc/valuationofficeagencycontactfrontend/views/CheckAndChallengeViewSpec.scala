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
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.CouncilTaxAddressForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.NormalMode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.behaviours.ViewBehaviours
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.checkAndChallenge

class CheckAndChallengeViewSpec extends ViewBehaviours {

  def view = () => checkAndChallenge(frontendAppConfig)(fakeRequest, messages)

  "Check And Challenge view" must {

    behave like normalPage(view,"checkAndChallenge", "url")
  }

  "contain continue button with the value Continue" in {
    val doc = asDocument(view())
    val continueButton = doc.getElementById("continue").text()
    assert(continueButton == messages("site.continue"))
  }

  "The Continue button links to the Check and Challenge service" in {
    val doc = asDocument(view())
    val href = doc.getElementById("continue").attr("href")
    assert(href == messages("checkAndChallenge.url"))
  }

  "has a link marked with site.back leading to the Business Subcategory Page" in {
    val doc = asDocument(view())
    val backlinkText = doc.select("a[class=link-back]").text()
    backlinkText mustBe messages("site.back")
    val backlinkUrl = doc.select("a[class=link-back]").attr("href")
    backlinkUrl mustBe uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.BusinessRatesSubcategoryController.onPageLoad(NormalMode).url
  }

}

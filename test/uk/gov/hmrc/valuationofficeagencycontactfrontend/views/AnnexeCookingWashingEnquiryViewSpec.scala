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

import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.AnnexeSelfContainedForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.behaviours.ViewBehaviours
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{annexeCookingWashingEnquiry => annexe_cooking_washing_enquiry}

class AnnexeCookingWashingEnquiryViewSpec extends ViewBehaviours {

  def annexeCookingWashingEnquiry = app.injector.instanceOf[annexe_cooking_washing_enquiry]

  def view = () => annexeCookingWashingEnquiry(frontendAppConfig, AnnexeSelfContainedForm())(fakeRequest, messages)

  "AnnexeCookingWashingEnquiry view" must {
    behave like normalPage(view, "annexeCookingWashing", "title", "heading",
      "form.yes", "form.no")

    "has a link marked with site.back leading to the Council Tax annexe cooking-washing enquiry Page" in {
      val doc = asDocument(view())
      val backlinkText = doc.select("a[class=govuk-back-link govuk-!-margin-top-0 govuk-!-margin-bottom-0]").text()
      backlinkText mustBe messages("site.back")
      val backlinkUrl = doc.select("a[class=govuk-back-link govuk-!-margin-top-0 govuk-!-margin-bottom-0]").attr("href")
      backlinkUrl mustBe uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.CouncilTaxAnnexeController.onSelfContainedEnquiryPageLoad().url
    }
  }

}

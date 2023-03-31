/*
 * Copyright 2023 HM Revenue & Customs
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

import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.CouncilTaxBusinessEnquiryForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.NormalMode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.behaviours.ViewBehaviours
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{councilTaxBusinessEnquiry => council_tax_business_enquiry}

class CouncilTaxBusinessEnquiryViewSpec extends ViewBehaviours {

  val backlink = uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.CouncilTaxSubcategoryController.onPageLoad(NormalMode).url

  def councilTaxBusinessEnquiry = app.injector.instanceOf[council_tax_business_enquiry]

  def view = () => councilTaxBusinessEnquiry(frontendAppConfig, CouncilTaxBusinessEnquiryForm(), NormalMode, backlink)(fakeRequest, messages)

  "Council Tax Bill view" must {
    behave like normalPage(view, "councilTaxBusinessEnquiry", "title", "heading",
      "form.all_property", "form.large_property", "form.small_property")

    "has a link marked with site.back leading to the Council Tax Property Empty Page" in {
      val doc = asDocument(view())
      val backlinkText = doc.select("a[class=govuk-back-link govuk-!-margin-top-0 govuk-!-margin-bottom-0]").text()
      backlinkText mustBe messages("site.back")
      val backlinkUrl = doc.select("a[class=govuk-back-link govuk-!-margin-top-0 govuk-!-margin-bottom-0]").attr("href")
      backlinkUrl mustBe backlink
    }
  }

}

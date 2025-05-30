/*
 * Copyright 2025 HM Revenue & Customs
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

import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.BusinessRatesPropertyForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.NormalMode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.behaviours.ViewBehaviours
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{businessRatesPropertyEnquiry => business_rates_property_enquiry}
import play.twirl.api.HtmlFormat

class BusinessRatesPropertyEnquiryViewSpec extends ViewBehaviours {
  def businessRatesPropertyEnquiry: html.businessRatesPropertyEnquiry = app.injector.instanceOf[business_rates_property_enquiry]

  def view: () => HtmlFormat.Appendable =
    () => businessRatesPropertyEnquiry(frontendAppConfig, BusinessRatesPropertyForm(), NormalMode)(using fakeRequest, messages)

  "Business Rates Property Enquiry view" must {
    behave like normalPage(view, "businessRatesPropertyEnquiry", "title", "heading", "england", "wales")

    "has a link marked with site.back leading to the Council Tax annexe self contained Page" in {
      val doc          = asDocument(view())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("site.back")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.BusinessRatesSubcategoryController.onPageLoad(NormalMode).url
    }
  }
}

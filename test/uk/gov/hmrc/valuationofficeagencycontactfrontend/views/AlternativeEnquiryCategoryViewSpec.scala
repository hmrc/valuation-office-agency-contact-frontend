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

import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.NormalMode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.behaviours.ViewBehaviours
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.alternativeEnquiryCategory


class AlternativeEnquiryCategoryViewSpec extends ViewBehaviours {

  def view = () => alternativeEnquiryCategory(frontendAppConfig)(fakeRequest, messages)

  "Alternative Enquiry Category view" must {

    behave like normalPage(view, "enquiryCategory", "council_tax", "business_rates", "housing_benefit",
    "providing_lettings", "valuations_for_tax", "valuation_for_public_body", "council_tax.summary","council_tax.about1",
    "council_tax.about2", "council_tax.about1", "business_rates.summary", "business_rates.about1", "business_rates.about2",
    "housing_benefit.summary", "housing_benefit.about1", "housing_benefit.about2", "housing_benefit.about3",
    "providing_lettings.summary", "providing_lettings.about1", "providing_lettings.about2", "valuations_for_tax.summary",
    "valuations_for_tax.about1", "valuations_for_tax.about2", "valuations_for_tax.about3", "valuations_for_tax.about4",
    "valuation_for_public_body.summary", "valuation_for_public_body.about1", "housing_benefit.finaltext",
      "valuation_for_public_body.finaltext", "other_business")
  }

  "has a link marked with site.back leading to the start page" in {
    val doc = asDocument(view())
    val backlinkText = doc.select("a[class=back-link]").text()
    backlinkText mustBe messages("site.back")
    val backlinkUrl = doc.select("a[class=back-link]").attr("href")
    backlinkUrl mustBe uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.IndexController.onPageLoad().url
  }

  "The enquiries links use class enquiry-link" in {
    val doc = asDocument(view())
    val button = doc.select("a[class~=enquiry-link]")
    assert(button.size() == 6)
  }

  "has a link marked with other-business leading to the contact form page" in {
    val doc = asDocument(view())
    val contactFormUrl = doc.getElementById("other-business").attr("href")
    contactFormUrl mustBe uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.ContactDetailsController.onPageLoad(NormalMode).url
  }
}

/*
 * Copyright 2026 HM Revenue & Customs
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

import play.api.data.Form
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.ExistingEnquiryCategoryForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.NormalMode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.behaviours.ViewBehaviours
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{existingEnquiryCategory => existing_enquiry}
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.existingEnquiryCategory

class ExistingEnquiryCategoryViewSpec extends ViewBehaviours {

  def existingEnqCategory: existingEnquiryCategory = app.injector.instanceOf[existing_enquiry]

  val messageKeyPrefix = "existingEnquiryCategory"

  val backUrl: String = routes.ContactReasonController.onPageLoad().url

  def createView: () => HtmlFormat.Appendable =
    () => existingEnqCategory(frontendAppConfig, ExistingEnquiryCategoryForm(), NormalMode, backUrl)(using fakeRequest, messages)

  def createViewUsingForm: Form[String] => HtmlFormat.Appendable =
    (form: Form[String]) => existingEnqCategory(frontendAppConfig, form, NormalMode, backUrl)(using fakeRequest, messages)

  "ExistingEnquiryCategory view" must {
    behave like normalPage(createView, messageKeyPrefix)
  }

  "ExistingEnquiryCategory view" when {
    "rendered" must {
      "contain continue button with the value Continue" in {
        val doc            = asDocument(createViewUsingForm(ExistingEnquiryCategoryForm()))
        val continueButton = doc.getElementsByClass("govuk-button").first().text()
        assert(continueButton == messages("site.continue"))
      }

      "contain radio buttons for the value" in {
        val doc = asDocument(createViewUsingForm(ExistingEnquiryCategoryForm()))
        for (option <- ExistingEnquiryCategoryForm.options)
          assertContainsRadioButton(doc, option.id, "value", option.value, false)
      }

      "has a radio button with the label set to the message with key existingEnquiryCategory.council_tax and that it is used once" in
        labelDefinedAndUsedOnce("council_tax", messageKeyPrefix, createView)

      "has a radio button with the label set to the message with key existingEnquiryCategory.business_rates and that it is used once" in
        labelDefinedAndUsedOnce("business_rates", messageKeyPrefix, createView)

      "has a radio button with the label set to the message with key existingEnquiryCategory.housing_benefit and that it is used once" in
        labelDefinedAndUsedOnce("housing_benefit", messageKeyPrefix, createView)

      "has a radio button with the label set to the message with key existingEnquiryCategory.fair_rent and that it is used once" in
        labelDefinedAndUsedOnce("fair_rent", messageKeyPrefix, createView)

      "has a radio button with the label set to the message with key existingEnquiryCategory.other and that it is used once" in
        labelDefinedAndUsedOnce("other", messageKeyPrefix, createView)
    }

    for (option <- ExistingEnquiryCategoryForm.options)
      s"rendered with a value of '${option.value}'" must {
        s"have the '${option.value}' radio button selected" in {
          val doc = asDocument(createViewUsingForm(ExistingEnquiryCategoryForm().bind(Map("value" -> s"${option.value}"))))
          assertContainsRadioButton(doc, option.id, "value", option.value, true)

          for (unselectedOption <- ExistingEnquiryCategoryForm.options.filterNot(o => o == option))
            assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, false)
        }
      }

  }
}

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

import play.api.data.Form
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.CouncilTaxSubcategoryForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.NormalMode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.behaviours.ViewBehaviours
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{councilTaxSubcategory => council_tax_subcategory}
import play.twirl.api.HtmlFormat

class CouncilTaxSubcategoryViewSpec extends ViewBehaviours {

  def councilTaxSubcategory: html.councilTaxSubcategory = app.injector.instanceOf[council_tax_subcategory]

  val messageKeyPrefix = "councilTaxSubcategory"

  def createView: () => HtmlFormat.Appendable = () => councilTaxSubcategory(frontendAppConfig, CouncilTaxSubcategoryForm(), NormalMode)(fakeRequest, messages)

  def createViewUsingForm: Form[String] => HtmlFormat.Appendable =
    (form: Form[String]) => councilTaxSubcategory(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  "CouncilTaxSubcategory view" when {
    "rendered" must {
      "contain radio buttons for the value" in {
        val doc = asDocument(createViewUsingForm(CouncilTaxSubcategoryForm()))
        for (option <- CouncilTaxSubcategoryForm.options)
          assertContainsRadioButton(doc, option.id, "value", option.value, false)
      }

      "has a radio button with the label set to the message with key councilTaxSubcategory.council_tax_band_too_high and that it is used once" in {
        labelDefinedAndUsedOnce("council_tax_band_too_high", messageKeyPrefix, createView)
      }

      "has a radio button with the label set to the message with key councilTaxSubcategory.council_tax_band_for_new and that it is used once" in {
        labelDefinedAndUsedOnce("council_tax_band_for_new", messageKeyPrefix, createView)
      }

      "has a radio button with the label set to the message with key councilTaxSubcategory.council_tax_property_demolished and that it is used once" in {
        labelDefinedAndUsedOnce("council_tax_property_demolished", messageKeyPrefix, createView)
      }

      "has a radio button with the label set to the message with key councilTaxSubcategory.council_tax_annexe and that it is used once" in {
        labelDefinedAndUsedOnce("council_tax_annexe", messageKeyPrefix, createView)
      }

      "has a radio button with the label set to the message with key councilTaxSubcategory.council_tax_other and that it is used once" in {
        labelDefinedAndUsedOnce("council_tax_other", messageKeyPrefix, createView)
      }

      "contain continue button with the value Continue" in {
        val doc            = asDocument(createViewUsingForm(CouncilTaxSubcategoryForm()))
        val continueButton = doc.getElementById("submit").text()
        assert(continueButton == messages("site.continue"))
      }

      "has a link marked with site.back leading to the Enquiry page" in {
        val doc          = asDocument(createView())
        val backlinkText = doc.select("a[class=govuk-back-link]").text()
        backlinkText mustBe messages("site.back")
        val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
        backlinkUrl mustBe uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.EnquiryCategoryController.onPageLoad(NormalMode).url
      }
    }

    for (option <- CouncilTaxSubcategoryForm.options)
      s"rendered with a value of '${option.value}'" must {
        s"have the '${option.value}' radio button selected" in {
          val doc = asDocument(createViewUsingForm(CouncilTaxSubcategoryForm().bind(Map("value" -> s"${option.value}"))))
          assertContainsRadioButton(doc, option.id, "value", option.value, true)

          for (unselectedOption <- CouncilTaxSubcategoryForm.options.filterNot(o => o == option))
            assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, false)
        }
      }
  }
}

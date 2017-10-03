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
import play.api.data.Form
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.EnquiryCategoryForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.NormalMode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.behaviours.ViewBehaviours
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.enquiryCategory

class EnquiryCategoryViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "enquiryCategory"

  def createView = () => enquiryCategory(frontendAppConfig, EnquiryCategoryForm(), NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[String]) => enquiryCategory(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  def labelDefinedAndUsedOnce(option: String) = {
    val prefix = "enquiryCategory"
    val doc = asDocument(createView())
    assert(messages.isDefinedAt(s"$prefix.$option"))
    val label = doc.select(s"label[for=$prefix.$option]")
    assert(label.size() == 1)
  }

  "EnquiryCategory view" must {
    behave like normalPage(createView, messageKeyPrefix)
  }

  "EnquiryCategory view" when {
    "rendered" must {

      "contain continue button with the value Continue" in {
        val doc = asDocument(createViewUsingForm(EnquiryCategoryForm()))
        val continueButton = doc.getElementById("submit").text()
        assert(continueButton == messages("site.continue"))
      }
      "contain radio buttons for the value" in {
        val doc = asDocument(createViewUsingForm(EnquiryCategoryForm()))
        for (option <- EnquiryCategoryForm.options) {
          assertContainsRadioButton(doc, option.id, "value", option.value, false)
        }
      }

      "has a radio button with the label set to the message with key enquiryCategory.council_tax and that it is used once" in {
        labelDefinedAndUsedOnce("council_tax")
      }

      "has a radio button with the label set to the message with key enquiryCategory.business_rates and that it is used once" in {
        labelDefinedAndUsedOnce("business_rates")
      }

      "has a radio button with the label set to the message with key enquiryCategory.housing_benefit and that it is used once" in {
        labelDefinedAndUsedOnce("housing_benefit")
      }

      "has a radio button with the label set to the message with key enquiryCategory.providing_lettings and that it is used once" in {
        labelDefinedAndUsedOnce("providing_lettings")
      }

      "has a radio button with the label set to the message with key enquiryCategory.valuations_for_tax and that it is used once" in {
        labelDefinedAndUsedOnce("valuations_for_tax")
      }

      "has a radio button with the label set to the message with key enquiryCategory.valuation_for_public_body and that it is used once" in {
        labelDefinedAndUsedOnce("valuation_for_public_body")
      }

      "has a link marked with site.back leading to the start page" in {
        val doc = asDocument(createView())
        val backlinkText = doc.select("a[class=back-link]").text()
        backlinkText mustBe messages("site.back")
        val backlinkUrl = doc.select("a[class=back-link]").attr("href")
        backlinkUrl mustBe uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.IndexController.onPageLoad().url
      }
    }

    for(option <- EnquiryCategoryForm.options) {
      s"rendered with a value of '${option.value}'" must {
        s"have the '${option.value}' radio button selected" in {
          val doc = asDocument(createViewUsingForm(EnquiryCategoryForm().bind(Map("value" -> s"${option.value}"))))
          assertContainsRadioButton(doc, option.id, "value", option.value, true)

          for(unselectedOption <- EnquiryCategoryForm.options.filterNot(o => o == option)) {
            assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, false)
          }
        }
      }
    }
  }
}

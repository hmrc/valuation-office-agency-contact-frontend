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
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.PropertyEnglandActualLetsForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.NormalMode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.behaviours.ViewBehaviours
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{propertyEnglandActualLets => property_england_actual_lets}

class PropertyEnglandActualLetsViewSpec extends ViewBehaviours {

  def propertyEnglandLetsSubcategory: property_england_actual_lets = app.injector.instanceOf[property_england_actual_lets]

  val messageKeyPrefix = "propertyEnglandActualLets"

  def createView: () => HtmlFormat.Appendable =
    () => propertyEnglandLetsSubcategory(frontendAppConfig, PropertyEnglandActualLetsForm(), NormalMode)(fakeRequest, messages)

  def createViewUsingForm: Form[String] => HtmlFormat.Appendable = (
    form: Form[String]
  ) => propertyEnglandLetsSubcategory(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  "PropertyEnglandActualLetsSubcategory view" when {
    "rendered" must {
      "contain radio buttons for the value" in {
        val doc = asDocument(createViewUsingForm(PropertyEnglandActualLetsForm()))
        for (option <- PropertyEnglandActualLetsForm.options)
          assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
      }

      "has a radio button with the label set to the message with key propertyEnglandActualLets.yes and that it is used once" in {
        labelDefinedAndUsedOnce("yes", messageKeyPrefix, createView)
      }

      "has a radio button with the label set to the message with key propertyEnglandActualLets.no and that it is used once" in {
        labelDefinedAndUsedOnce("no", messageKeyPrefix, createView)
      }

      "has a link marked with site.back leading to the Business Rates Self Containing Holiday Let Page" in {
        val doc          = asDocument(createView())
        val backlinkText = doc.select("a[class=govuk-back-link]").text()
        backlinkText mustBe messages("site.back")
        val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
        backlinkUrl mustBe uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.PropertyEnglandAvailableLetsController.onPageLoad().url
      }
    }

    for (option <- PropertyEnglandActualLetsForm.options)
      s"rendered with a value of '${option.value}'" must {
        s"have the '${option.value}' radio button selected" in {
          val doc = asDocument(createViewUsingForm(PropertyEnglandActualLetsForm().bind(Map("value" -> s"${option.value}"))))
          assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

          for (unselectedOption <- PropertyEnglandActualLetsForm.options.filterNot(o => o == option))
            assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
        }
      }
  }
}

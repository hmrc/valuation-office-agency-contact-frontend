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
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.PropertyEnglandAvailableLetsForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.NormalMode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.behaviours.ViewBehaviours
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{propertyEnglandAvailableLets => property_england_available_lets}

class PropertyEnglandAvailableLetsViewSpec extends ViewBehaviours {

  def propertyEnglandLets140DaysSubcategory = app.injector.instanceOf[property_england_available_lets]

  val messageKeyPrefix = "propertyEnglandAvailableLets"

  def createView = () => propertyEnglandLets140DaysSubcategory(frontendAppConfig, PropertyEnglandAvailableLetsForm(), NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[String]) => propertyEnglandLets140DaysSubcategory(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  "PropertyEnglandLets140DaysSubcategory view" when {
    "rendered" must {
      "contain radio buttons for the value" in {
        val doc = asDocument(createViewUsingForm(PropertyEnglandAvailableLetsForm()))
        for (option <- PropertyEnglandAvailableLetsForm.options) {
          assertContainsRadioButton(doc, option.id, "value", option.value, false)
        }
      }

      "has a radio button with the label set to the message with key businessRatesSelfCatering140Days.yes and that it is used once" in {
        labelDefinedAndUsedOnce("yes", messageKeyPrefix, createView)
      }

      "has a radio button with the label set to the message with key businessRatesSelfCatering140Days.no and that it is used once" in {
        labelDefinedAndUsedOnce("no", messageKeyPrefix, createView)
      }

      "has a link marked with site.back leading to the Business Rates Self Containing Holiday Let Page" in {
        val doc = asDocument(createView())
        val backlinkText = doc.select("a[class=govuk-back-link govuk-!-margin-top-0 govuk-!-margin-bottom-0]").text()
        backlinkText mustBe messages("site.back")
        val backlinkUrl = doc.select("a[class=govuk-back-link govuk-!-margin-top-0 govuk-!-margin-bottom-0]").attr("href")
        backlinkUrl mustBe uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.BusinessRatesSelfCateringController.onPageLoad().url
      }
    }

    for(option <- PropertyEnglandAvailableLetsForm.options) {
      s"rendered with a value of '${option.value}'" must {
        s"have the '${option.value}' radio button selected" in {
          val doc = asDocument(createViewUsingForm(PropertyEnglandAvailableLetsForm().bind(Map("value" -> s"${option.value}"))))
          assertContainsRadioButton(doc, option.id, "value", option.value, true)

          for(unselectedOption <- PropertyEnglandAvailableLetsForm.options.filterNot(o => o == option)) {
            assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, false)
          }
        }
      }
    }
  }
}

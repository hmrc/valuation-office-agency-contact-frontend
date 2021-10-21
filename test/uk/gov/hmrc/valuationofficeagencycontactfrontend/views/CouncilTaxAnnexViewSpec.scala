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

import play.api.data.Form
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.AnnexeForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.NormalMode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.behaviours.ViewBehaviours
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{councilTaxAnnexe => council_tax_annexe}

class CouncilTaxAnnexViewSpec extends ViewBehaviours {

  def councilTaxAnnexe = app.injector.instanceOf[council_tax_annexe]

  val messageKeyPrefix = "annexe"

  def createView = () => councilTaxAnnexe(frontendAppConfig, AnnexeForm(), NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[String]) => councilTaxAnnexe(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  "CouncilTaxAnnex view" must {
    behave like normalPage(createView, messageKeyPrefix)
  }

  "CouncilTaxAnnex view" when {
    "rendered" must {

      "contain continue button with the value Continue" in {
        val doc = asDocument(createViewUsingForm(AnnexeForm()))
        val continueButton = doc.getElementsByClass("govuk-button").first().text()
        assert(continueButton == messages("site.continue"))
      }
      "contain radio buttons for the value" in {
        val doc = asDocument(createViewUsingForm(AnnexeForm()))
        for (option <- AnnexeForm.options) {
          assertContainsRadioButton(doc, option.id, "value", option.value, false)
        }
      }

      "has a radio button with the label set to the message with key annexe.added and that it is used once" in {
        labelDefinedAndUsedOnce("added", messageKeyPrefix, createView)
      }

      "has a radio button with the label set to the message with key annexe.removed and that it is used once" in {
        labelDefinedAndUsedOnce("removed", messageKeyPrefix, createView)
      }

      "have a link marked with site.back leading to the Business Rates Subcategory Page" in {
        val doc = asDocument(createView())
        val backlinkText = doc.select("a[class=govuk-back-link govuk-!-margin-top-0 govuk-!-margin-bottom-0]").text()
        backlinkText mustBe messages("site.back")
        val backlinkUrl = doc.select("a[class=govuk-back-link govuk-!-margin-top-0 govuk-!-margin-bottom-0]").attr("href")
        backlinkUrl mustBe uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.CouncilTaxSubcategoryController.onPageLoad(NormalMode).url
      }
    }

    for(option <- AnnexeForm.options) {
      s"rendered with a value of '${option.value}'" must {
        s"have the '${option.value}' radio button selected" in {
          val doc = asDocument(createViewUsingForm(AnnexeForm().bind(Map("value" -> s"${option.value}"))))
          assertContainsRadioButton(doc, option.id, "value", option.value, true)

          for(unselectedOption <- AnnexeForm.options.filterNot(o => o == option)) {
            assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, false)
          }
        }
      }
    }
  }
}

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

import play.api.data.Form
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.BusinessRatesSubcategoryForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.NormalMode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.behaviours.ViewBehaviours
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.businessRatesSubcategory

class BusinessRatesSubcategoryViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "businessRatesSubcategory"

  def createView = () => businessRatesSubcategory(frontendAppConfig, BusinessRatesSubcategoryForm(), NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[String]) => businessRatesSubcategory(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  "BusinessRatesSubcategory view" must {
    behave like normalPage(createView, messageKeyPrefix)
  }

  "BusinessRatesSubcategory view" when {
    "rendered" must {
      "contain radio buttons for the value" in {
        val doc = asDocument(createViewUsingForm(BusinessRatesSubcategoryForm()))
        for (option <- BusinessRatesSubcategoryForm.options) {
          assertContainsRadioButton(doc, option.id, "value", option.value, false)
        }
      }

      "contain continue button with the value Continue" in {
        val doc = asDocument(createViewUsingForm(BusinessRatesSubcategoryForm()))
        val continueButton = doc.getElementById("submit").text()
        assert(continueButton == messages("site.continue"))
      }

      "has a link marked with site.back leading to the Enquiry Category Page" in {
        val doc = asDocument(createView())
        val backlinkText = doc.select("a[class=back-link]").text()
        backlinkText mustBe messages("site.back")
        val backlinkUrl = doc.select("a[class=back-link]").attr("href")
        backlinkUrl mustBe uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.EnquiryCategoryController.onPageLoad(NormalMode).url
      }
    }

    for(option <- BusinessRatesSubcategoryForm.options) {
      s"rendered with a value of '${option.value}'" must {
        s"have the '${option.value}' radio button selected" in {
          val doc = asDocument(createViewUsingForm(BusinessRatesSubcategoryForm().bind(Map("value" -> s"${option.value}"))))
          assertContainsRadioButton(doc, option.id, "value", option.value, true)

          for(unselectedOption <- BusinessRatesSubcategoryForm.options.filterNot(o => o == option)) {
            assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, false)
          }
        }
      }
    }
  }
}

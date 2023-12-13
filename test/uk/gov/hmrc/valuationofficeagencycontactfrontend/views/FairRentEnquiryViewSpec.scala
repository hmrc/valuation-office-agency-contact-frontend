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
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.FairRentEnquiryForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.NormalMode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.behaviours.ViewBehaviours
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{fairRentEnquiry => fair_rent_enquiry}

class FairRentEnquiryViewSpec extends ViewBehaviours {

  def fairRentEnquirySubcategory = app.injector.instanceOf[fair_rent_enquiry]

  val messageKeyPrefix = "fairRents"

  def createView = () => fairRentEnquirySubcategory(frontendAppConfig, FairRentEnquiryForm(), NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[String]) => fairRentEnquirySubcategory(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  "fairRentEnquirySubcategory view" when {
    "rendered" must {
      "contain radio buttons for the value" in {
        val doc = asDocument(createViewUsingForm(FairRentEnquiryForm()))
        for (option <- FairRentEnquiryForm.options) {
          assertContainsRadioButton(doc, option.id, "value", option.value, false)
        }
      }

      "has a radio button with the label set to the message with key housingBenefits.new and that it is used once" in {
        labelDefinedAndUsedOnce("submit_new_application", messageKeyPrefix, createView)
      }

      "has a radio button with the label set to the message with key housingBenefits.check and that it is used once" in {
        labelDefinedAndUsedOnce("check_fair_rent_register", messageKeyPrefix, createView)
      }

      "has a radio button with the label set to the message with key housingBenefits.other and that it is used once" in {
        labelDefinedAndUsedOnce("other_request", messageKeyPrefix, createView)
      }

      "has a link marked with site.back leading to the Enquiry Page" in {
        val doc = asDocument(createView())
        val backlinkText = doc.select("a[class=govuk-back-link]").text()
        backlinkText mustBe messages("site.back")
        val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
        backlinkUrl mustBe uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.EnquiryCategoryController.onPageLoad(NormalMode).url
      }
    }

    for(option <- FairRentEnquiryForm.options) {
      s"rendered with a value of '${option.value}'" must {
        s"have the '${option.value}' radio button selected" in {
          val doc = asDocument(createViewUsingForm(FairRentEnquiryForm().bind(Map("value" -> s"${option.value}"))))
          assertContainsRadioButton(doc, option.id, "value", option.value, true)

          for(unselectedOption <- FairRentEnquiryForm.options.filterNot(o => o == option)) {
            assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, false)
          }
        }
      }
    }
  }
}

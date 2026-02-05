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
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.ContactReasonForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.NormalMode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.behaviours.ViewBehaviours
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{contactReason, contactReason as contact_reason}

class ContactReasonViewSpec extends ViewBehaviours {

  def contactReason: contactReason = app.injector.instanceOf[contact_reason]

  val messageKeyPrefix = "contactReason"

  val backUrl: String = routes.ContactReasonController.onPageLoad().url

  def createView: () => HtmlFormat.Appendable = {
    () => contactReason(ContactReasonForm(), NormalMode)(using fakeRequest, messages)
  }

  def createViewUsingForm: Form[String] => HtmlFormat.Appendable =
    (form: Form[String]) => contactReason(ContactReasonForm(), NormalMode)(using fakeRequest, messages)

  "ContactReason view" must {
    behave like normalPage(createView, messageKeyPrefix)
  }

  "ContactReason view" when {
    "rendered" must {
      "contain continue button with the value Continue" in {
        val doc            = asDocument(createViewUsingForm(ContactReasonForm()))
        val continueButton = doc.getElementsByClass("govuk-button").first().text()
        assert(continueButton == messages("site.continue"))
      }

      "contain radio buttons for the value" in {
        val doc = asDocument(createViewUsingForm(ContactReasonForm()))
        for (option <- ContactReasonForm.options)
          assertContainsRadioButton(doc, option.id, "value", option.value, false)
      }

      "has a radio button with the label set to the message with key contactReason.new_enquiry and that it is used once" in {
        labelDefinedAndUsedOnce("new_enquiry", messageKeyPrefix, createView)
        assertContainsText(asDocument(createView()), messages("contactReason.new_enquiry"))
      }

      "has a radio button with the label set to the message with key contactReason.more_details and that it is used once" in {
        labelDefinedAndUsedOnce("more_details", messageKeyPrefix, createView)
        assertContainsText(asDocument(createView()), messages("contactReason.more_details"))
      }

      "has a radio button with the label set to the message with key contactReason.update_existing and that it is used once" in {
        labelDefinedAndUsedOnce("update_existing", messageKeyPrefix, createView)
        assertContainsText(asDocument(createView()), messages("contactReason.update_existing"))
      }
    }
  }
}

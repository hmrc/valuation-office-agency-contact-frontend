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

package uk.gov.hmrc.vo.contact.frontend.views

import play.api.data.Form
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.vo.contact.frontend.controllers.routes
import uk.gov.hmrc.vo.contact.frontend.forms.ContactReasonForm
import uk.gov.hmrc.vo.contact.frontend.views.behaviours.ViewBehaviours
import uk.gov.hmrc.vo.contact.frontend.views.html.{contactReason, contactReason as contact_reason}

class ContactReasonViewSpec extends ViewBehaviours:

  def contactReason: contactReason = app.injector.instanceOf[contact_reason]

  val messageKeyPrefix = "contact.reason"

  val backUrl: String = routes.ContactReasonController.onPageLoad.url

  def createView: () => HtmlFormat.Appendable =
    () => contactReason(ContactReasonForm.form)(using fakeRequest, messages)

  def createViewUsingForm: Form[String] => HtmlFormat.Appendable =
    form => contactReason(form)(using fakeRequest, messages)

  "ContactReason view" must {
    "display the correct browser title" in {
      val doc = asDocument(createView())
      assertEqualsValue(doc, "title", messages(s"$messageKeyPrefix.label") + " - Valuation Office contact form - GOV.UK")
    }

    "display the correct page h1 header" in {
      val doc = asDocument(createView())
      assertPageTitleEqualsMessage(doc, s"$messageKeyPrefix.label")
    }
  }

  "ContactReason view" when {
    "rendered" must {
      "contain continue button with the value Continue" in {
        val doc            = asDocument(createViewUsingForm(ContactReasonForm.form))
        val continueButton = doc.getElementsByClass("govuk-button").first().text()
        assert(continueButton == messages("button.continue.label"))
      }

      "contain radio buttons for the value" in {
        val doc = asDocument(createViewUsingForm(ContactReasonForm.form))
        for ((value, idx) <- ContactReasonForm.values.zipWithIndex) {
          val id = "reason" + (if idx == 0 then "" else s"-${idx + 1}")
          assertContainsRadioButton(doc, id, "reason", value, false)
        }
      }

      "has a radio button with id 'reason' and the label for it" in {
        val id  = "reason"
        val doc = asDocument(createView())
        assert(doc.getElementById(id).attr("name") == "reason")
        assert(doc.select(s"label[for=$id]").size == 1)
        assertContainsText(doc, messages("contact.reason.new_enquiry.label"))
      }

      "has a radio button with id 'reason-2' and the label for it" in {
        val id  = "reason-2"
        val doc = asDocument(createView())
        assert(doc.getElementById(id).attr("name") == "reason")
        assert(doc.select(s"label[for=$id]").size == 1)
        assertContainsText(doc, messages("contact.reason.more_details.label"))
      }

      "has a radio button with id 'reason-3' and the label for it" in {
        val id  = "reason-3"
        val doc = asDocument(createView())
        assert(doc.getElementById(id).attr("name") == "reason")
        assert(doc.select(s"label[for=$id]").size == 1)
        assertContainsText(doc, messages("contact.reason.update_existing.label"))
      }
    }
  }

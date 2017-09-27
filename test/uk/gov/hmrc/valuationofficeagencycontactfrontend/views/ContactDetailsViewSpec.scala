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
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.{ContactDetailsForm, EnquiryCategoryForm}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.{ContactDetails, NormalMode}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.behaviours.QuestionViewBehaviours
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.contactDetails

class ContactDetailsViewSpec extends QuestionViewBehaviours[ContactDetails] {

  val messageKeyPrefix = "contactDetails"

  def createView = () => contactDetails(frontendAppConfig, ContactDetailsForm(), NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[ContactDetails]) => contactDetails(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  def labelDefinedAndUsedOnce(option: String) = {
    val prefix = "contactDetails"
    val doc = asDocument(createView())
    assert(messages.isDefinedAt(s"$prefix.$option"))
    val label = doc.select(s"label[for=$prefix.$option]")
    assert(label.size() == 1)
  }

  override val form = ContactDetailsForm()

  "ContactDetails view" must {

    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithTextFields(createViewUsingForm, messageKeyPrefix, routes.ContactDetailsController.onSubmit(NormalMode).url, "firstName", "lastName",
    "telephoneNumber", "email", "message")

    "contain radio buttons for the contactPreference" in {
      val doc = asDocument(createViewUsingForm(form))
      for (option <- ContactDetailsForm.options) {
        assertContainsRadioButton(doc, option.id, "contactPreference", option.value, false)
      }
    }


    "has a radio button with the label set to the message with key contactDetails.email_preference and that it is used once" in {
      labelDefinedAndUsedOnce("email_preference")
    }

    "has a radio button with the label set to the message with key contactDetails.phone_preference and that it is used once" in {
      labelDefinedAndUsedOnce("phone_preference")
    }

    "has a link marked with site.back leading to the start page" in {
      val doc = asDocument(createViewUsingForm(form))
      val backlinkText = doc.select("a[class=back-link]").text()
      backlinkText mustBe messages("site.back")
      val backlinkUrl = doc.select("a[class=back-link]").attr("href")
      backlinkUrl mustBe uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.IndexController.onPageLoad().url
    }
  }









    }

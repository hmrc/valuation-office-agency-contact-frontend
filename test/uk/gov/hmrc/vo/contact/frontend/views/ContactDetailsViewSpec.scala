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
import uk.gov.hmrc.vo.contact.frontend.forms.ContactDetailsForm.contactDetailsForm
import uk.gov.hmrc.vo.contact.frontend.models.{ContactDetails, NormalMode}
import uk.gov.hmrc.vo.contact.frontend.views.behaviours.QuestionViewBehaviours
import uk.gov.hmrc.vo.contact.frontend.views.html.contactDetails

class ContactDetailsViewSpec extends QuestionViewBehaviours[ContactDetails]:

  private def contactDetails: contactDetails = app.injector.instanceOf[contactDetails]

  private val messageKeyPrefix    = "contactDetails"
  private def ctBackLink: String  = uk.gov.hmrc.vo.contact.frontend.controllers.routes.CouncilTaxSubcategoryController.onPageLoad(NormalMode).url
  private def ndrBackLink: String = uk.gov.hmrc.vo.contact.frontend.controllers.routes.BusinessRatesSubcategoryController.onPageLoad(NormalMode).url

  private def createNDRViewUsingForm(form: Form[ContactDetails]): HtmlFormat.Appendable =
    contactDetails(form, NormalMode, ndrBackLink)(using fakeRequest, messages)

  private def createCTView(): HtmlFormat.Appendable = contactDetails(contactDetailsForm, NormalMode, ctBackLink)(using fakeRequest, messages)

  private def createCTViewUsingForm(form: Form[ContactDetails]): HtmlFormat.Appendable =
    contactDetails(form, NormalMode, ctBackLink)(using fakeRequest, messages)

  override val form: Form[ContactDetails] = contactDetailsForm

  "ContactDetails view" must {

    behave like normalPage(() => createCTView(), messageKeyPrefix)

    behave like pageWithTextFields(
      createCTViewUsingForm,
      "fullName",
      "email",
      "contactNumber"
    )

    "Contact Details has a link marked with site.back leading to the council tax subcategory page when enquiry category is council_tax" in {
      val doc          = asDocument(createCTViewUsingForm(form))
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("site.back")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe uk.gov.hmrc.vo.contact.frontend.controllers.routes.CouncilTaxSubcategoryController.onPageLoad(NormalMode).url
    }

    "contain continue button with the value Continue when the enquiry category is council_tax" in {
      val doc            = asDocument(createCTViewUsingForm(contactDetailsForm))
      val continueButton = doc.getElementById("continue-button").text()
      assert(continueButton == messages("button.continue.label"))
    }

    "Contact Details has a link marked with site.back leading to the business rates subcategory page when enquiry category is business_rates" in {
      val doc          = asDocument(createNDRViewUsingForm(form))
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("site.back")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe uk.gov.hmrc.vo.contact.frontend.controllers.routes.BusinessRatesSubcategoryController.onPageLoad(NormalMode).url
    }

    "contain continue button with the value Continue when the enquiry category is business_rates" in {
      val doc            = asDocument(createNDRViewUsingForm(contactDetailsForm))
      val continueButton = doc.getElementById("continue-button").text()
      assert(continueButton == messages("button.continue.label"))
    }
  }

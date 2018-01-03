/*
 * Copyright 2018 HM Revenue & Customs
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
  val ctBackLink = uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.CouncilTaxSubcategoryController.onPageLoad(NormalMode).url
  val ndrBackLink = uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.BusinessRatesSubcategoryController.onPageLoad(NormalMode).url

  def createNDRViewUsingForm(form: Form[ContactDetails]) = contactDetails(frontendAppConfig, form, NormalMode, ndrBackLink)(fakeRequest, messages)

  def createCTView() = contactDetails(frontendAppConfig, ContactDetailsForm(), NormalMode, ctBackLink)(fakeRequest, messages)

  def createCTViewUsingForm(form: Form[ContactDetails]) = contactDetails(frontendAppConfig, form, NormalMode, ctBackLink)(fakeRequest, messages)

  override val form = ContactDetailsForm()

  "ContactDetails view" must {

    behave like normalPage(createCTView, messageKeyPrefix)

    behave like pageWithTextFields(createCTViewUsingForm, messageKeyPrefix, routes.ContactDetailsController.onSubmit(NormalMode).url, "firstName", "lastName",
      "email", "confirmEmail", "contactNumber")


    "Contact Details has a link marked with site.back leading to the council tax subcategory page when enquiry category is council_tax" in {
      val doc = asDocument(createCTViewUsingForm(form))
      val backlinkText = doc.select("a[class=link-back]").text()
      backlinkText mustBe messages("site.back")
      val backlinkUrl = doc.select("a[class=link-back]").attr("href")
      backlinkUrl mustBe uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.CouncilTaxSubcategoryController.onPageLoad(NormalMode).url
    }

    "contain continue button with the value Continue when the enquiry category is council_tax" in {
      val doc = asDocument(createCTViewUsingForm(ContactDetailsForm()))
      val continueButton = doc.getElementById("submit").text()
      assert(continueButton == messages("site.continue"))
    }

    "Contact Details has a link marked with site.back leading to the business rates subcategory page when enquiry category is business_rates" in {
      val doc = asDocument(createNDRViewUsingForm(form))
      val backlinkText = doc.select("a[class=link-back]").text()
      backlinkText mustBe messages("site.back")
      val backlinkUrl = doc.select("a[class=link-back]").attr("href")
      backlinkUrl mustBe uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.BusinessRatesSubcategoryController.onPageLoad(NormalMode).url
    }

    "contain continue button with the value Continue when the enquiry category is business_rates" in {
      val doc = asDocument(createNDRViewUsingForm(ContactDetailsForm()))
      val continueButton = doc.getElementById("submit").text()
      assert(continueButton == messages("site.continue"))
    }
  }
}

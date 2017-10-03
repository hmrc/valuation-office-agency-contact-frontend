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
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.PropertyDetailsForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.{NormalMode, PropertyDetails}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.behaviours.QuestionViewBehaviours
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.propertyDetails

class PropertyDetailsViewSpec extends QuestionViewBehaviours[PropertyDetails] {

  val messageKeyPrefix = "propertyDetails"

  def createView = () => propertyDetails(frontendAppConfig, PropertyDetailsForm(), NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[PropertyDetails]) => propertyDetails(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  override val form = PropertyDetailsForm()

  "PropertyDetails view" must {

    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithTextFields(createViewUsingForm, messageKeyPrefix, routes.PropertyDetailsController.onSubmit(NormalMode).url, "addressLine1", "addressLine2",
      "town", "county", "postcode")

    "contain continue button with the value Continue" in {
      val doc = asDocument(createViewUsingForm(PropertyDetailsForm()))
      val continueButton = doc.getElementById("submit").text()
      assert(continueButton == messages("site.continue"))
    }

    "has a link marked with site.back leading to the Contact Details Page" in {
      val doc = asDocument(createView())
      val backlinkText = doc.select("a[class=back-link]").text()
      backlinkText mustBe messages("site.back")
      val backlinkUrl = doc.select("a[class=back-link]").attr("href")
      backlinkUrl mustBe uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.ContactDetailsController.onPageLoad(NormalMode).url
    }
  }
}

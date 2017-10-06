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
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.TellUsMoreForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.{NormalMode, TellUsMore}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.behaviours.QuestionViewBehaviours
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.tellUsMore

class TellUsMoreViewSpec extends QuestionViewBehaviours[TellUsMore] {

  val messageKeyPrefix = "tellUsMore"

  def createView = () => tellUsMore(frontendAppConfig, TellUsMoreForm(), NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[TellUsMore]) => tellUsMore(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  override val form = TellUsMoreForm()

  "TellUsMore view" must {

    behave like normalPage(createView, messageKeyPrefix, "para", "para1", "para2", "para3", "para4", "para5")

    behave like pageWithTextFields(createViewUsingForm, messageKeyPrefix, routes.TellUsMoreController.onSubmit(NormalMode).url, "message")
  }

  "contain continue button with the value Continue" in {
    val doc = asDocument(createViewUsingForm(TellUsMoreForm()))
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

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
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.AnythingElseForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.NormalMode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.behaviours.QuestionViewBehaviours
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{anythingElseTellUs => anything_else}

class AnythingElseTellUsViewSpec extends QuestionViewBehaviours[String] {

  val messageKeyPrefix = "anythingElse"

  def anythingElse = app.injector.instanceOf[anything_else]

  def createView = () => anythingElse(frontendAppConfig, AnythingElseForm(), NormalMode)(fakeRequest, messages)

  def createAlternativeView = () => anythingElse(frontendAppConfig, AnythingElseForm(), NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[String]) => anythingElse(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  override val form = AnythingElseForm()

  "AnythingElseTellUs view" must {

    behave like normalPage(createView, messageKeyPrefix, "title", "heading")

    behave like pageWithTextFields(createViewUsingForm, messageKeyPrefix, routes.AnythingElseTellUsController.onSubmit().url)
  }

  "contain continue button with the value Continue" in {
    val doc = asDocument(createViewUsingForm(AnythingElseForm()))
    val continueButton = doc.getElementById("submit").text()
    assert(continueButton == messages("site.continue"))
  }

  "has a link marked with site.back leading to the Anything Else Tell Us Page" in {
    val doc = asDocument(createView())
    val backlinkText = doc.select("a[class=govuk-back-link govuk-!-margin-top-0 govuk-!-margin-bottom-0]").text()
    backlinkText mustBe messages("site.back")
    val backlinkUrl = doc.select("a[class=govuk-back-link govuk-!-margin-top-0 govuk-!-margin-bottom-0]").attr("href")
    backlinkUrl mustBe uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.PropertyAddressController.onPageLoad(NormalMode).url
  }
}

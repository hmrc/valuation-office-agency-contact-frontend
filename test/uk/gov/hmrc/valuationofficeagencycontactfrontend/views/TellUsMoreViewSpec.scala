/*
 * Copyright 2025 HM Revenue & Customs
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
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{tellUsMore => tell_us_more}
import play.twirl.api.HtmlFormat

class TellUsMoreViewSpec extends QuestionViewBehaviours[TellUsMore] {

  val messageKeyPrefix = "tellUsMore"

  val backLink: String           = uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.PropertyAddressController.onPageLoad(NormalMode).url
  val poorRepairBackLink: String = uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.DatePropertyChangedController.onPageLoad().url

  def tellUsMore: html.tellUsMore = app.injector.instanceOf[tell_us_more]

  def createView: () => HtmlFormat.Appendable =
    () => tellUsMore(frontendAppConfig, TellUsMoreForm(), NormalMode, "tellUsMore.ct-reference", backLink)(using fakeRequest, messages)

  def createAlternativeView: () => HtmlFormat.Appendable =
    () => tellUsMore(frontendAppConfig, TellUsMoreForm(), NormalMode, "tellUsMore.ndr-reference", backLink)(using fakeRequest, messages)

  def createViewUsingForm: Form[TellUsMore] => HtmlFormat.Appendable =
    (form: Form[TellUsMore]) => tellUsMore(frontendAppConfig, form, NormalMode, "", backLink)(using fakeRequest, messages)

  override val form: Form[TellUsMore] = TellUsMoreForm()

  "TellUsMore view" must {

    behave like normalPage(createView, messageKeyPrefix, "para", "para2", "para3", "para4", "ct-reference")

    behave like pageWithTextFields(createViewUsingForm, messageKeyPrefix, routes.TellUsMoreController.onSubmit(NormalMode).url, "message")
  }

  "TellUsMore view for property poor repair" must {
    def view = () => tellUsMore(frontendAppConfig, TellUsMoreForm(), NormalMode, "tellUsMore.poorRepair", poorRepairBackLink)(using fakeRequest, messages)

    behave like normalPage(view, "tellUsMore.poorRepair", "hint", "inset")

    behave like pageWithTextFields(createViewUsingForm, "tellUsMore.poorRepair", routes.TellUsMoreController.onSubmit(NormalMode).url, "message")

  }

  "contain continue button with the value Continue" in {
    val doc            = asDocument(createViewUsingForm(TellUsMoreForm()))
    val continueButton = doc.getElementById("submit").text()
    assert(continueButton == messages("site.continue"))
  }

  "contain tellUsMore.ndr-reference paragraph" in {
    val doc = asDocument(createAlternativeView())
    assert(doc.toString.contains(messages("tellUsMore.ndr-reference")))
  }

  "has a link marked with site.back leading to the Contact Details Page" in {
    val doc          = asDocument(createView())
    val backlinkText = doc.select("a[class=govuk-back-link]").text()
    backlinkText mustBe messages("site.back")
    val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
    backlinkUrl mustBe uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.PropertyAddressController.onPageLoad(NormalMode).url
  }
}

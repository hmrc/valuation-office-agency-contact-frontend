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
import uk.gov.hmrc.vo.contact.frontend.forms.DatePropertyChangedForm.datePropertyChangedForm
import uk.gov.hmrc.vo.contact.frontend.models.NormalMode
import uk.gov.hmrc.vo.contact.frontend.views.behaviours.ViewBehaviours
import uk.gov.hmrc.vo.contact.frontend.views.html.datePropertyChanged

import java.time.LocalDate

class DatePropertyChangedViewSpec extends ViewBehaviours:

  private def datePropertyChanged = app.injector.instanceOf[datePropertyChanged]

  private val messageKeyPrefix = "datePropertyChanged"

  private def createView: () => HtmlFormat.Appendable = () =>
    datePropertyChanged(datePropertyChangedForm, messageKeyPrefix, "/valuation-office-agency-contact-frontend/about-business-rates")(
      using fakeRequest,
      messages
    )

  private def createViewUsingForm: Form[Option[LocalDate]] => HtmlFormat.Appendable =
    (form: Form[Option[LocalDate]]) => datePropertyChanged(form, "test", "test")(using fakeRequest, messages)

  "DatePropertyChanged view" must {
    behave like normalPage(createView, messageKeyPrefix)
  }

  "DatePropertyChanged view" when {
    "rendered" must {
      "contain continue button with the value Continue" in {
        val doc            = asDocument(createViewUsingForm(datePropertyChangedForm))
        val continueButton = doc.getElementsByClass("govuk-button").first().text()
        assert(continueButton == messages("button.continue.label"))
      }

      "have a link marked with site.back leading to the Business Rates Subcategory Page" in {
        val doc          = asDocument(createView())
        val backlinkText = doc.select("a[class=govuk-back-link]").text()
        backlinkText mustBe messages("site.back")
        val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
        backlinkUrl mustBe uk.gov.hmrc.vo.contact.frontend.controllers.routes.BusinessRatesSubcategoryController.onPageLoad(NormalMode).url
      }
    }
  }

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
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.DatePropertyChangedForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.NormalMode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.behaviours.ViewBehaviours
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{datePropertyChanged => date_property_changed}

import java.time.LocalDate
import play.twirl.api.HtmlFormat

class DatePropertyChangedViewSpec extends ViewBehaviours {

  def datePropertyChanged: html.datePropertyChanged = app.injector.instanceOf[date_property_changed]

  val messageKeyPrefix = "datePropertyChanged"

  val backUrl: String = routes.PropertyWindWaterController.onPageLoad().url

  def dateForm: Form[Option[LocalDate]] = DatePropertyChangedForm()

  def createView: () => HtmlFormat.Appendable = () =>
    datePropertyChanged(frontendAppConfig, dateForm, NormalMode, messageKeyPrefix, "/valuation-office-agency-contact-frontend/about-business-rates")(
      using fakeRequest,
      messages
    )

  def createViewUsingForm: Form[Option[LocalDate]] => HtmlFormat.Appendable =
    (form: Form[Option[LocalDate]]) => datePropertyChanged(frontendAppConfig, form, NormalMode, "test", "test")(using fakeRequest, messages)

  "DatePropertyChanged view" must {
    behave like normalPage(createView, messageKeyPrefix)
  }

  "DatePropertyChanged view" when {
    "rendered" must {
      "contain continue button with the value Continue" in {
        val doc            = asDocument(createViewUsingForm(dateForm))
        val continueButton = doc.getElementsByClass("govuk-button").first().text()
        assert(continueButton == messages("site.continue"))
      }

      "have a link marked with site.back leading to the Business Rates Subcategory Page" in {
        val doc          = asDocument(createView())
        val backlinkText = doc.select("a[class=govuk-back-link]").text()
        backlinkText mustBe messages("site.back")
        val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
        backlinkUrl mustBe uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.BusinessRatesSubcategoryController.onPageLoad(NormalMode).url
      }
    }
  }
}

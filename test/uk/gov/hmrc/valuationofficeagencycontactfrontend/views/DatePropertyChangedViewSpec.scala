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
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.DatePropertyChangedForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.NormalMode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.behaviours.ViewBehaviours
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{datePropertyChanged => date_property_changed}

import java.time.LocalDate

class DatePropertyChangedViewSpec extends ViewBehaviours {

  def datePropertyChanged = app.injector.instanceOf[date_property_changed]

  val messageKeyPrefix = "datePropertyChanged"

  val backUrl = routes.PropertyWindWaterController.onPageLoad().url

  def dateForm: Form[Option[LocalDate]] = new DatePropertyChangedForm().apply()

  def createView = () => datePropertyChanged(frontendAppConfig, dateForm, NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[Option[LocalDate]]) => datePropertyChanged(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  "DatePropertyChanged view" must {
    behave like normalPage(createView, messageKeyPrefix)
  }

  "DatePropertyChanged view" when {
    "rendered" must {
      "contain continue button with the value Continue" in {
        val doc = asDocument(createViewUsingForm(dateForm))
        val continueButton = doc.getElementsByClass("govuk-button").first().text()
        assert(continueButton == messages("site.continue"))
      }
    }
  }
}
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
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.BusinessRatesAddressForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.{NormalMode, BusinessRatesAddress}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.behaviours.QuestionViewBehaviours
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.businessRatesAddress

class BusinessRatesAddressViewSpec extends QuestionViewBehaviours[BusinessRatesAddress] {

  val messageKeyPrefix = "businessRatesAddress"

  def createView = () => businessRatesAddress(frontendAppConfig, BusinessRatesAddressForm(), NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[BusinessRatesAddress]) => businessRatesAddress(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  override val form = BusinessRatesAddressForm()

  "BusinessRatesAddress view" must {

    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithTextFields(createViewUsingForm, messageKeyPrefix, routes.BusinessRatesAddressController.onSubmit(NormalMode).url, "businessName",
      "businessAddressLine1", "businessAddressLine2", "businessAddressLine3", "town", "county", "postcode")
  }
}

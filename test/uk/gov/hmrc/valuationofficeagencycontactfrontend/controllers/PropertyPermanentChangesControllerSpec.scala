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

package uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers

import play.api.data.Form
import play.api.test.Helpers._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions.DataRetrievalAction
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.PropertyWindWaterForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.NormalMode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.MessageControllerComponentsHelpers
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{propertyPermanentChanges => property_permanent_changes}

import javax.inject.Singleton
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.propertyPermanentChanges

@Singleton
class PropertyPermanentChangesControllerSpec extends ControllerSpecBase {

  def propertyPermanentChangesEnquiry: propertyPermanentChanges = app.injector.instanceOf[property_permanent_changes]

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new PropertyPermanentChangesController(
      frontendAppConfig,
      messagesApi,
      propertyPermanentChangesEnquiry,
      MessageControllerComponentsHelpers.stubMessageControllerComponents
    )

  def viewAsString(form: Form[String] = PropertyWindWaterForm()): String =
    propertyPermanentChangesEnquiry(frontendAppConfig, NormalMode)(using fakeRequest, messages).toString()

  "Property Permanent Changes Controller" must {
    "return the correct view for a GET" in {
      val result = controller().onPageLoad()(fakeRequest)
      contentAsString(result) mustBe propertyPermanentChangesEnquiry(frontendAppConfig, NormalMode)(using fakeRequest, messages).toString
    }

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad()(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

  }
}

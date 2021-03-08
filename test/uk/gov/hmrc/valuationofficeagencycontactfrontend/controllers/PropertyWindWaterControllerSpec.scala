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

package uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers

import play.api.data.Form
import play.api.libs.json.JsString
import play.api.test.Helpers._
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.valuationofficeagencycontactfrontend.FakeNavigator
import uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors.FakeDataCacheConnector
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions.{DataRequiredActionImpl, DataRetrievalAction, FakeDataRetrievalAction}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.{EnquiryDateForm, PropertyWindWaterForm}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers.CouncilTaxPropertyPoorRepairId
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.NormalMode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.MessageControllerComponentsHelpers
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{propertyWindWatertight => property_wind}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{windWatertightEnquiry => wind_watertight_enquiry}

import javax.inject.Singleton

@Singleton
class PropertyWindWaterControllerSpec extends ControllerSpecBase {

  def windWatertightEnquiry = app.injector.instanceOf[wind_watertight_enquiry]
  def propertyWindWatertight = app.injector.instanceOf[property_wind]

  def onwardRoute = routes.PropertyWindWaterController.onPageLoad()

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new PropertyWindWaterController(frontendAppConfig, messagesApi, new DataRequiredActionImpl(ec), dataRetrievalAction, FakeDataCacheConnector,
      new FakeNavigator(desiredRoute = onwardRoute), windWatertightEnquiry, propertyWindWatertight, MessageControllerComponentsHelpers.stubMessageControllerComponents)

  def viewAsString(form: Form[String] = PropertyWindWaterForm()) =
    windWatertightEnquiry(frontendAppConfig, form, NormalMode)(fakeRequest, messages).toString()


  "Property Wind And Water Controller" must {
    "return the correct view for a GET" in {
      val result = controller().onPageLoad()(fakeRequest)
      contentAsString(result) mustBe propertyWindWatertight(frontendAppConfig)(fakeRequest, messages).toString
    }

    "return OK and the correct view for a GET" in {
      val result = controller().onEnquiryLoad()(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val validData = Map(CouncilTaxPropertyPoorRepairId.toString -> JsString(PropertyWindWaterForm.options.head.value))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onEnquiryLoad()(fakeRequest)

      contentAsString(result) mustBe viewAsString(PropertyWindWaterForm().fill(PropertyWindWaterForm.options.head.value))
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", PropertyWindWaterForm.options.head.value))
      val result = controller().onSubmit()(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)

    }
  }
}
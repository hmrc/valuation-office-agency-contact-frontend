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

package uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers

import play.api.data.Form
import play.api.libs.json.Json
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.valuationofficeagencycontactfrontend.FakeNavigator
import uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors.FakeDataCacheConnector
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions._
import play.api.test.Helpers._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.PropertyAddressForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers.PropertyAddressId
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.{NormalMode, PropertyAddress}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.propertyAddress

class PropertyAddressControllerSpec extends ControllerSpecBase {

  def onwardRoute = routes.IndexController.onPageLoad()

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new PropertyAddressController(frontendAppConfig, messagesApi, FakeDataCacheConnector, new FakeNavigator(desiredRoute = onwardRoute),
      dataRetrievalAction, new DataRequiredActionImpl)

  def viewAsString(form: Form[PropertyAddress] = PropertyAddressForm()) = propertyAddress(frontendAppConfig, form, NormalMode)(fakeRequest, messages).toString

  "Property Address Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val validData = Map(PropertyAddressId.toString -> Json.toJson(PropertyAddress("value 1", Some("value 2"), "value 3", "value 4", "value 5")))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeRequest)

      contentAsString(result) mustBe viewAsString(PropertyAddressForm().fill(PropertyAddress("value 1", Some("value 2"), "value 3", "value 4", "value 5")))
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("addressLine1", "value 1"), ("addressLine2", "value 2"), ("town", "value 3"),
        ("county", "value 4"), ("postcode", "value 5"))

      val result = controller().onSubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = PropertyAddressForm().bind(Map("value" -> "invalid value"))

      val result = controller().onSubmit(NormalMode)(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("addressLine1", "value 1"), ("addressLine2", "value 2"), ("town", "value 3"), ("county", "value 4"), ("postcode", "value 5"))
      val result = controller(dontGetAnyData).onSubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
    }
  }
}

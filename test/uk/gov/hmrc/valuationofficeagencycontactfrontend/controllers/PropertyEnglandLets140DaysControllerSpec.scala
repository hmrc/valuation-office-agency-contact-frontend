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
import play.api.test.Helpers.{contentAsString, redirectLocation, status, _}
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.valuationofficeagencycontactfrontend.FakeNavigator
import uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors.FakeDataCacheConnector
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions.{DataRequiredActionImpl, DataRetrievalAction, FakeDataRetrievalAction}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.PropertyEnglandLets140DaysForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers.PropertyEnglandLets140DaysId
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.NormalMode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.MessageControllerComponentsHelpers._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{propertyEnglandLets140Days => property_england_lets_140_days}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{propertyEnglandLetsNoAction => property_england_lets_no_action}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{propertyWalesLets => wales_lets}

class PropertyEnglandLets140DaysControllerSpec extends ControllerSpecBase {

  def propertyEnglandLets140DaysEnquiry = app.injector.instanceOf[property_england_lets_140_days]
  def propertyEnglandLetsNoAction = app.injector.instanceOf[property_england_lets_no_action]
  def propertyWalesLets = app.injector.instanceOf[wales_lets]

  def onwardRoute = routes.EnquiryCategoryController.onPageLoad(NormalMode)

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
  new PropertyEnglandLets140DaysController(frontendAppConfig, messagesApi, FakeDataCacheConnector, new FakeNavigator(desiredRoute = onwardRoute),
  dataRetrievalAction, new DataRequiredActionImpl(ec), propertyEnglandLets140DaysEnquiry, propertyEnglandLetsNoAction, propertyWalesLets, stubMessageControllerComponents)

  def viewAsString(form: Form[String] = PropertyEnglandLets140DaysForm()) = propertyEnglandLets140DaysEnquiry(frontendAppConfig, form, NormalMode)(fakeRequest, messages).toString

  "PropertyEnglandLets140DaysController" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "return OK and the correct view for england lets no business rates page GET" in {
      val result = controller().onEngLetsNoActionPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe propertyEnglandLetsNoAction(frontendAppConfig)(fakeRequest, messages).toString()
    }


    "return OK and the correct view for wales lets rates page GET" in {
      val result = controller().onWalLetsPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe propertyWalesLets(frontendAppConfig)(fakeRequest, messages).toString()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val validData = Map(PropertyEnglandLets140DaysId.toString -> JsString(PropertyEnglandLets140DaysForm.options.head.value))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeRequest)

      contentAsString(result) mustBe viewAsString(PropertyEnglandLets140DaysForm().fill(PropertyEnglandLets140DaysForm.options.head.value))
    }

    "redirect to no action page when valid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", PropertyEnglandLets140DaysForm.options.head.value))

      val result = controller().onEngLetsNoActionPageLoad(NormalMode)(postRequest)

      status(result) mustBe OK
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", PropertyEnglandLets140DaysForm.options.head.value))

      val result = controller().onSubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = PropertyEnglandLets140DaysForm().bind(Map("value" -> "invalid value"))

      val result = controller().onSubmit(NormalMode)(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad.url)
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", PropertyEnglandLets140DaysForm.options.head.value))
      val result = controller(dontGetAnyData).onSubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad.url)
    }

  }

}

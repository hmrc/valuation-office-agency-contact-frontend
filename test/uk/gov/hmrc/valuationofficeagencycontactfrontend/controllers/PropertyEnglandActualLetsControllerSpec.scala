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
import play.api.libs.json.JsString
import play.api.mvc.Call
import play.api.test.Helpers.{contentAsString, redirectLocation, status, _}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.FakeNavigator
import uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors.{AuditingService, FakeDataCacheConnector}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions.{DataRequiredActionImpl, DataRetrievalAction, FakeDataRetrievalAction}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.PropertyEnglandActualLetsForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers.PropertyEnglandActualLetsId
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.{CacheMap, NormalMode}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.MessageControllerComponentsHelpers._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{propertyEnglandActualLets => property_England_actual_lets}

class PropertyEnglandActualLetsControllerSpec extends ControllerSpecBase {

  def propertyEnglandActualLetsEnquiry: property_England_actual_lets = inject[property_England_actual_lets]

  def auditService: AuditingService = inject[AuditingService]

  def onwardRoute: Call = routes.EnquiryCategoryController.onPageLoad(NormalMode)

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new PropertyEnglandActualLetsController(
      frontendAppConfig,
      messagesApi,
      auditService,
      FakeDataCacheConnector,
      new FakeNavigator(desiredRoute = onwardRoute),
      dataRetrievalAction,
      new DataRequiredActionImpl(ec),
      propertyEnglandActualLetsEnquiry,
      stubMessageControllerComponents
    )

  def viewAsString(form: Form[String] = PropertyEnglandActualLetsForm()): String = propertyEnglandActualLetsEnquiry(
    frontendAppConfig,
    form,
    NormalMode
  )(using fakeRequest, messages).toString

  "PropertyEnglandActualLetsController" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val validData       = Map(PropertyEnglandActualLetsId.toString -> JsString(PropertyEnglandActualLetsForm.options.head.value))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeRequest)

      contentAsString(result) mustBe viewAsString(PropertyEnglandActualLetsForm().fill(PropertyEnglandActualLetsForm.options.head.value))
    }

    "redirect to no action page when valid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", PropertyEnglandActualLetsForm.options.head.value))

      val result = controller().onPageLoad(NormalMode)(postRequest)

      status(result) mustBe OK
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withMethod("POST").withFormUrlEncodedBody(("value", PropertyEnglandActualLetsForm.options.head.value))

      val result = controller().onSubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val postRequest = fakeRequest.withMethod("POST").withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm   = PropertyEnglandActualLetsForm().bind(Map("value" -> "invalid value"))

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
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", PropertyEnglandActualLetsForm.options.head.value))
      val result      = controller(dontGetAnyData).onSubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad.url)
    }

  }

}

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

package uk.gov.hmrc.vo.contact.frontend.controllers

import org.scalatestplus.mockito.MockitoSugar
import play.api.data.Form
import play.api.libs.json.{JsString, Json}
import play.api.mvc.Call
import play.api.test.Helpers.*
import uk.gov.hmrc.vo.contact.frontend.FakeNavigator
import uk.gov.hmrc.vo.contact.frontend.connectors.FakeDataCacheConnector
import uk.gov.hmrc.vo.contact.frontend.controllers.actions.*
import uk.gov.hmrc.vo.contact.frontend.forms.WhatElseForm.form
import uk.gov.hmrc.vo.contact.frontend.identifiers.WhatElseId
import uk.gov.hmrc.vo.contact.frontend.models.*
import uk.gov.hmrc.vo.contact.frontend.utils.MessageControllerComponentsHelpers
import uk.gov.hmrc.vo.contact.frontend.views.html.whatElse

class WhatElseControllerSpec extends ControllerSpecBase with MockitoSugar:

  private def whatElseView: whatElse = app.injector.instanceOf[whatElse]

  private def onwardRoute: Call = routes.EnquiryCategoryController.onPageLoad(NormalMode)

  private def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    WhatElseController(
      messagesApi,
      FakeDataCacheConnector,
      FakeNavigator(desiredRoute = onwardRoute),
      dataRetrievalAction,
      DataRequiredActionImpl(ec),
      whatElseView,
      MessageControllerComponentsHelpers.stubMessageControllerComponents
    )

  private def viewAsString(form: Form[String]): String =
    whatElseView(form)(using fakeRequest, messages).toString

  "TellUsMore Controller" must {

    "return OK and the correct view for a GET" in {
      val validData: Map[String, JsString] = Map()

      val getRelevantData = FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString(form)
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val validData       = Map(
        WhatElseId.toString -> Json.toJson("value 1")
      )
      val getRelevantData = FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad(fakeRequest)

      contentAsString(result) mustBe viewAsString(form.fill("value 1"))
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withMethod("POST").withFormUrlEncodedBody(("message", "value 1"))

      val result = controller().onSubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val postRequest = fakeRequest.withMethod("POST").withFormUrlEncodedBody(("message", "<>"))
      val boundForm   = form.bind(Map("message" -> "<>"))

      val result = controller().onSubmit(NormalMode)(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad.url)
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("message", "value 1"))
      val result      = controller(dontGetAnyData).onSubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad.url)
    }

  }

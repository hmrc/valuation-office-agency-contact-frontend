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

import play.api.data.Form
import play.api.libs.json.JsString
import uk.gov.hmrc.vo.contact.frontend.FakeNavigator
import uk.gov.hmrc.vo.contact.frontend.connectors.{AuditingService, FakeDataCacheConnector}
import uk.gov.hmrc.vo.contact.frontend.controllers.actions.*
import play.api.test.Helpers.*
import uk.gov.hmrc.vo.contact.frontend.forms.EnquiryCategoryForm
import uk.gov.hmrc.vo.contact.frontend.identifiers.EnquiryCategoryId
import uk.gov.hmrc.vo.contact.frontend.models.{CacheMap, NormalMode}
import uk.gov.hmrc.vo.contact.frontend.utils.MessageControllerComponentsHelpers
import uk.gov.hmrc.vo.contact.frontend.views.html.{enquiryCategory => enquiry_category}
import play.api.mvc.Call
import uk.gov.hmrc.vo.contact.frontend.views.html

class EnquiryCategoryControllerSpec extends ControllerSpecBase {

  def enquiryCategory: html.enquiryCategory = inject[enquiry_category]
  def auditService: AuditingService         = inject[AuditingService]

  def onwardRoute: Call = routes.EnquiryCategoryController.onPageLoad(NormalMode)

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    EnquiryCategoryController(
      messagesApi,
      auditService,
      FakeDataCacheConnector,
      FakeNavigator(desiredRoute = onwardRoute),
      dataRetrievalAction,
      DataRequiredActionImpl(ec),
      enquiryCategory,
      MessageControllerComponentsHelpers.stubMessageControllerComponents
    )

  def viewAsString(form: Form[String] = EnquiryCategoryForm.form): String =
    enquiryCategory(form, NormalMode)(using fakeRequest, messages).toString

  "EnquiryCategory Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val validData       = Map(EnquiryCategoryId.toString -> JsString(EnquiryCategoryForm.values.head))
      val getRelevantData = FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeRequest)

      contentAsString(result) mustBe viewAsString(EnquiryCategoryForm.form.fill(EnquiryCategoryForm.values.head))
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withMethod("POST").withFormUrlEncodedBody(("category", EnquiryCategoryForm.values.head))

      val result = controller().onSubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val postRequest = fakeRequest.withMethod("POST").withFormUrlEncodedBody(("category", "invalid value"))
      val boundForm   = EnquiryCategoryForm.form.bind(Map("category" -> "invalid value"))

      val result = controller().onSubmit(NormalMode)(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "return error page if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad(NormalMode)(fakeRequest)
      status(result) mustBe SEE_OTHER
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      val postRequest = fakeRequest.withMethod("POST")
        .withFormUrlEncodedBody(("category", EnquiryCategoryForm.values.head))
      val result      = controller(dontGetAnyData).onSubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

  }
}

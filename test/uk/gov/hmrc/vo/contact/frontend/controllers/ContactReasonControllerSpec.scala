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
import uk.gov.hmrc.vo.contact.frontend.FakeNavigator
import uk.gov.hmrc.vo.contact.frontend.connectors.{AuditingService, FakeDataCacheConnector}
import uk.gov.hmrc.vo.contact.frontend.controllers.actions.DataRetrievalAction
import uk.gov.hmrc.vo.contact.frontend.utils.MessageControllerComponentsHelpers
import uk.gov.hmrc.vo.contact.frontend.views.html.{contactReason => contact_reason}
import play.api.test.Helpers.*
import uk.gov.hmrc.vo.contact.frontend.forms.ContactReasonForm
import play.api.mvc.Call
import uk.gov.hmrc.vo.contact.frontend.views.html

class ContactReasonControllerSpec extends ControllerSpecBase:

  def contactReason: html.contactReason = inject[contact_reason]

  def onwardRoute: Call = routes.ContactReasonController.onPageLoad

  def auditService: AuditingService = inject[AuditingService]

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    ContactReasonController(
      messagesApi,
      getClearCacheMap,
      dataRetrievalAction,
      auditService,
      FakeDataCacheConnector,
      FakeNavigator(desiredRoute = onwardRoute),
      contactReason,
      MessageControllerComponentsHelpers.stubMessageControllerComponents
    )

  def viewAsString(form: Form[String] = ContactReasonForm.form): String = contactReason(form)(using fakeRequest, messages).toString()

  "ContactReason Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(fakeRequest)

      status(result) mustBe OK

      contentAsString(result) mustBe viewAsString()
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withMethod("POST").withFormUrlEncodedBody(("reason", ContactReasonForm.values.head))
      val result      = controller().onSubmit(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "redirect from old URL to new URL" in {
      val result = controller().redirect()(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }
  }

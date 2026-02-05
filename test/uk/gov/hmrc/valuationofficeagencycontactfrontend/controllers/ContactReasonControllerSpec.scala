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

package uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers

import play.api.data.Form
import uk.gov.hmrc.valuationofficeagencycontactfrontend.FakeNavigator
import uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors.{AuditingService, FakeDataCacheConnector}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions.DataRetrievalAction
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.NormalMode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.MessageControllerComponentsHelpers
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{contactReason => contact_reason}
import play.api.test.Helpers._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.ContactReasonForm
import play.api.mvc.Call
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html

class ContactReasonControllerSpec extends ControllerSpecBase {

  def contactReason: html.contactReason = inject[contact_reason]

  def onwardRoute: Call = routes.ContactReasonController.onPageLoad()

  def auditService: AuditingService = inject[AuditingService]

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new ContactReasonController(
      messagesApi,
      getClearCacheMap,
      dataRetrievalAction,
      auditService,
      FakeDataCacheConnector,
      new FakeNavigator(desiredRoute = onwardRoute),
      contactReason,
      MessageControllerComponentsHelpers.stubMessageControllerComponents
    )

  def viewAsString(form: Form[String] = ContactReasonForm()): String = contactReason(form, NormalMode)(using fakeRequest, messages).toString()

  "ContactReason Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe OK

      contentAsString(result) mustBe viewAsString()

    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withMethod("POST").withFormUrlEncodedBody(("value", ContactReasonForm.options.head.value))
      val result      = controller().onSubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)

    }

    "redirect from old URL to new URL" in {
      val result = controller().redirect()(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }
  }

}

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

import play.api.test.Helpers._
import uk.gov.hmrc.http.SessionKeys
import uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors.AuditingService
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions.DataRetrievalAction
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.MessageControllerComponentsHelpers
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.error.session_expired
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest

class SessionExpiredControllerSpec extends ControllerSpecBase {

  def sessionExpired: session_expired = app.injector.instanceOf[session_expired]

  def auditService: AuditingService = app.injector.instanceOf[AuditingService]

  def getDataAction: DataRetrievalAction = app.injector.instanceOf[DataRetrievalAction]

  val testRequest: FakeRequest[AnyContentAsEmpty.type] = fakeRequest.withSession(SessionKeys.sessionId -> "id")

  "SessionExpired Controller" must {
    "return 200 for a GET" in {
      val result = new SessionExpiredController(
        frontendAppConfig,
        auditService,
        getDataAction,
        messagesApi,
        MessageControllerComponentsHelpers.stubMessageControllerComponents,
        sessionExpired
      ).onPageLoad()(testRequest)
      status(result) mustBe OK
    }

    "return the correct view for a GET" in {
      val result = new SessionExpiredController(
        frontendAppConfig,
        auditService,
        getDataAction,
        messagesApi,
        MessageControllerComponentsHelpers.stubMessageControllerComponents,
        sessionExpired
      ).onPageLoad()(testRequest)
      contentAsString(result) mustBe sessionExpired(frontendAppConfig)(using fakeRequest, messages).toString
    }
  }

}

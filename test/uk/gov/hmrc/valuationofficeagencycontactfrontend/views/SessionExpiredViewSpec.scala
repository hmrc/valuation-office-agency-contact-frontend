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

package uk.gov.hmrc.valuationofficeagencycontactfrontend.views

import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.behaviours.ViewBehaviours
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.error.session_expired
import play.twirl.api.HtmlFormat

class SessionExpiredViewSpec extends ViewBehaviours {

  def sessionExpired: session_expired = app.injector.instanceOf[session_expired]

  def view: () => HtmlFormat.Appendable = () => sessionExpired(frontendAppConfig)(using fakeRequest, messages)

  "Session Expired view" must {

    behave like normalPage(view, "session_expired", "guidance")
  }
}

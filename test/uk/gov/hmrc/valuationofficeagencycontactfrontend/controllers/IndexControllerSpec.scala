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

import play.api.mvc.Result
import play.api.test.Helpers._
import uk.gov.hmrc.http.SessionKeys
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.index
import scala.concurrent.ExecutionContext.Implicits.global

class IndexControllerSpec extends ControllerSpecBase {

  "Index Controller" must {
    "return 200 for a GET" in {
      val result = new IndexController(frontendAppConfig, messagesApi).onPageLoad()(fakeRequest)
      status(result) mustBe OK
    }

    "return the correct view for a GET" in {
      val result = new IndexController(frontendAppConfig, messagesApi).onPageLoad()(fakeRequest)
      contentAsString(result) mustBe index(frontendAppConfig)(fakeRequest, messages).toString
    }

    "When calling onPageLoadWithNewSession changes the sessionId to a new one" in {
      val firstResult = new IndexController(frontendAppConfig, messagesApi).onPageLoad()(fakeRequest)

      val firstSession = firstResult.map { result1 =>
        result1.session(fakeRequest).get(SessionKeys.sessionId)
        val secondResult = new IndexController(frontendAppConfig, messagesApi).onPageLoadWithNewSession(fakeRequest)
        val secondSession = secondResult.map { result2 =>
          result2.session(fakeRequest).get(SessionKeys.sessionId)
          assert(result1 != result2)
        }
      }
    }
  }
}

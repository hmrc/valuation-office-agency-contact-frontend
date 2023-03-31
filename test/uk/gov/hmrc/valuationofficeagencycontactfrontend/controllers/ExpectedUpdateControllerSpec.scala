/*
 * Copyright 2023 HM Revenue & Customs
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
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions.DataRetrievalAction
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.MessageControllerComponentsHelpers
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{expectedUpdate => expected_update}


class ExpectedUpdateControllerSpec extends ControllerSpecBase {


  def expectedUpdate = inject[expected_update]

  def onwardRoute = routes.EnquiryDateController.onPageLoad()

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new ExpectedUpdateController(messagesApi, dataRetrievalAction, expectedUpdate, MessageControllerComponentsHelpers.stubMessageControllerComponents)

  def viewAsString() = expectedUpdate()(fakeRequest, messages).toString()


    "ExpectedUpdateController Controller" must {

      "return OK and the correct view for a GET" in {
        val result = controller().onPageLoad()(fakeRequest)

        status(result) mustBe OK

        contentAsString(result) mustBe viewAsString()

      }
    }


}

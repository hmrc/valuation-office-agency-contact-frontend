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

import play.api.test.Helpers.*
import uk.gov.hmrc.vo.contact.frontend.models.NormalMode
import uk.gov.hmrc.vo.contact.frontend.utils.MessageControllerComponentsHelpers
import uk.gov.hmrc.vo.contact.frontend.views.html.windWatertightCannotBeReduced
import uk.gov.hmrc.vo.contact.frontend.views.html.windWatertightCannotBeReduced as wind_watertight_cannot_be_reduced

import javax.inject.Singleton

@Singleton
class PropertyWindWaterControllerSpec extends ControllerSpecBase:

  def windWatertightCannotBeReducedEnquiry: windWatertightCannotBeReduced = app.injector.instanceOf[wind_watertight_cannot_be_reduced]

  def controller =
    PropertyWindWaterController(
      messagesApi,
      windWatertightCannotBeReducedEnquiry,
      MessageControllerComponentsHelpers.stubMessageControllerComponents
    )

  def viewAsString: String =
    windWatertightCannotBeReducedEnquiry(NormalMode)(using fakeRequest, messages).toString()

  "Property Wind And Water Controller" must {
    "return the correct view for a GET" in {
      val result = controller.onPageLoad()(fakeRequest)
      contentAsString(result) mustBe windWatertightCannotBeReducedEnquiry(NormalMode)(using fakeRequest, messages).toString
    }

    "return OK and the correct view for a GET" in {
      val result = controller.onPageLoad()(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString
    }

  }

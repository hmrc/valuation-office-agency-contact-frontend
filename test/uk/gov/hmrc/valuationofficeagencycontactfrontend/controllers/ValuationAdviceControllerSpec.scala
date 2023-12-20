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
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.MessageControllerComponentsHelpers
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{valuationAdvice => valuation_advice}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html

class ValuationAdviceControllerSpec extends ControllerSpecBase {

  def valuationAdvice: html.valuationAdvice = app.injector.instanceOf[valuation_advice]

  "Valuation Advice Controller" must {
    "return 200 for a GET" in {
      val result = new ValuationAdviceController(
        frontendAppConfig,
        messagesApi,
        valuationAdvice,
        MessageControllerComponentsHelpers.stubMessageControllerComponents
      ).onPageLoad()(fakeRequest)
      status(result) mustBe OK
    }

    "return the correct view for a GET" in {
      val result = new ValuationAdviceController(
        frontendAppConfig,
        messagesApi,
        valuationAdvice,
        MessageControllerComponentsHelpers.stubMessageControllerComponents
      ).onPageLoad()(fakeRequest)
      contentAsString(result) mustBe valuationAdvice(frontendAppConfig)(fakeRequest, messages).toString
    }

  }
}

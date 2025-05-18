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
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions.DataRetrievalAction
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.MessageControllerComponentsHelpers
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{councilTaxBandTooHigh => council_tax_too_high}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html

class CouncilTaxBandTooHighControllerSpec extends ControllerSpecBase {

  def councilTaxBandTooHigh: html.councilTaxBandTooHigh = app.injector.instanceOf[council_tax_too_high]

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new CouncilTaxBandTooHighController(
      frontendAppConfig,
      messagesApi,
      councilTaxBandTooHigh,
      MessageControllerComponentsHelpers.stubMessageControllerComponents
    )

  "Council Tax Band Too High Controller" must {
    "return the correct view for a GET" in {
      val result = controller().onPageLoad()(fakeRequest)
      contentAsString(result) mustBe councilTaxBandTooHigh(frontendAppConfig)(using fakeRequest, messages).toString
    }
  }
}

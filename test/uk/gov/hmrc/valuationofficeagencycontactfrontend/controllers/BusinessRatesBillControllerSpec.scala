/*
 * Copyright 2021 HM Revenue & Customs
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

import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.MessageControllerComponentsHelpers
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{businessRatesBill => business_rates_bill}
import play.api.test.Helpers._

class BusinessRatesBillControllerSpec extends ControllerSpecBase {

  def businessRatesBill = app.injector.instanceOf[business_rates_bill]

  def controller = new BusinessRatesBillController(messagesApi, frontendAppConfig, businessRatesBill,
      MessageControllerComponentsHelpers.stubMessageControllerComponents)

  "BusinessRatesBillController" must {
    "return the correct view for a GET" in {
      val result = controller.onPageLoad()(fakeRequest)
      contentAsString(result) mustBe businessRatesBill(frontendAppConfig)(fakeRequest, messages).toString
    }
  }
}

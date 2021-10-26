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

import play.api.test.Helpers._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.MessageControllerComponentsHelpers
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{businessRatesChallenge => business_rates_challenge, businessRatesPropertyOrAreaChanged => business_rates_property_or_area_changed}

class BusinessRatesChallengeControllerSpec extends ControllerSpecBase {

  def businessRatesChallenge = app.injector.instanceOf[business_rates_challenge]
  def businessRatesPropertyOrAreaChanged = app.injector.instanceOf[business_rates_property_or_area_changed]


  "BusinessRatesChallengeController" must {
    "return 200 for GET on AreaChangePageLoad" in {
      val result = new BusinessRatesChallengeController(messagesApi, frontendAppConfig, businessRatesChallenge, businessRatesPropertyOrAreaChanged,
        MessageControllerComponentsHelpers.stubMessageControllerComponents).onAreaChangePageLoad(fakeRequest)
      status(result) mustBe OK
      contentAsString(result) mustBe businessRatesPropertyOrAreaChanged(frontendAppConfig)(fakeRequest, messages).toString()
    }

    "return 200 for GET on ChallengePageLoad" in {
      val resutl = new BusinessRatesChallengeController(messagesApi, frontendAppConfig, businessRatesChallenge, businessRatesPropertyOrAreaChanged,
        MessageControllerComponentsHelpers.stubMessageControllerComponents).onChallengePageLoad(fakeRequest)
      status(resutl) mustBe OK
      contentAsString(resutl) must include ("Challenging my business rates valuation")
    }

  }



}

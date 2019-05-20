/*
 * Copyright 2019 HM Revenue & Customs
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

class BusinessRatesChallengeControllerSpec extends ControllerSpecBase {

  "BusinessRatesChallengeController" must {
    "return 200 for GET on AreaChangePageLoad" in {
      val resutl = new BusinessRatesChallengeController(messagesApi, frontendAppConfig).onAreaChangePageLoad(fakeRequest)
      status(resutl) mustBe OK
      contentAsString(resutl) must include ("propertyOrAreaChanged.title")
    }

    "return 200 for GET on ChallengePageLoad" in {
      val resutl = new BusinessRatesChallengeController(messagesApi, frontendAppConfig).onChallengePageLoad(fakeRequest)
      status(resutl) mustBe OK
      contentAsString(resutl) must include ("businessRatesChallenge.title")
    }

  }



}

/*
 * Copyright 2024 HM Revenue & Customs
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

import play.api.test.Helpers.*

class ServiceUnavailableControllerSpec extends ControllerSpecBase {

  private def serviceUnavailableController = inject[ServiceUnavailableController]

  "ServiceUnavailableController" must {

    "return OK and the correct view for a GET" in {
      val result = serviceUnavailableController.show()(fakeRequest)

      status(result) mustBe OK
    }

  }

}

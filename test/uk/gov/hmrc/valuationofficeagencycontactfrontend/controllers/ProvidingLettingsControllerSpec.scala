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
import uk.gov.hmrc.valuationofficeagencycontactfrontend.Navigator
import uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.MessageControllerComponentsHelpers
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{providingLettings => providing_lettings}

class ProvidingLettingsControllerSpec extends ControllerSpecBase {

  def providingLettings = app.injector.instanceOf[providing_lettings]
  def dataCacheConnector = app.injector.instanceOf[DataCacheConnector]
  def navigator = app.injector.instanceOf[Navigator]

  "Housing benefits Controller" must {
    "return 200 for a GET" in {
      val result = new ProvidingLettingsController(frontendAppConfig, messagesApi, providingLettings,
        dataCacheConnector, dontGetAnyData, navigator,
        MessageControllerComponentsHelpers.stubMessageControllerComponents).onPageLoad()(fakeRequest)
      status(result) mustBe OK
    }

    "return the correct view for a GET" in {
      val result = new ProvidingLettingsController(frontendAppConfig, messagesApi, providingLettings,
        dataCacheConnector, dontGetAnyData, navigator,
        MessageControllerComponentsHelpers.stubMessageControllerComponents).onPageLoad()(fakeRequest)
      contentAsString(result) mustBe providingLettings(frontendAppConfig)(fakeRequest, messages).toString
    }

  }
}

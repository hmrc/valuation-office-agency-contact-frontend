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
import uk.gov.hmrc.vo.contact.frontend.Navigator
import uk.gov.hmrc.vo.contact.frontend.connectors.DataCacheConnector
import uk.gov.hmrc.vo.contact.frontend.utils.MessageControllerComponentsHelpers
import uk.gov.hmrc.vo.contact.frontend.views.html.{providingLettings => providing_lettings}
import uk.gov.hmrc.vo.contact.frontend.views.html

class ProvidingLettingsControllerSpec extends ControllerSpecBase:

  def providingLettings: html.providingLettings = inject[providing_lettings]
  def dataCacheConnector: DataCacheConnector    = inject[DataCacheConnector]
  def navigator: Navigator                      = inject[Navigator]

  "Housing benefits Controller" should {
    "return 200 for a GET" in {
      val result = ProvidingLettingsController(
        messagesApi,
        providingLettings,
        dataCacheConnector,
        dontGetAnyData,
        navigator,
        MessageControllerComponentsHelpers.stubMessageControllerComponents
      ).onPageLoad()(getRequest)
      status(result) shouldBe OK
    }

    "return the correct view for a GET" in {
      val result = ProvidingLettingsController(
        messagesApi,
        providingLettings,
        dataCacheConnector,
        dontGetAnyData,
        navigator,
        MessageControllerComponentsHelpers.stubMessageControllerComponents
      ).onPageLoad()(getRequest)
      contentAsString(result) shouldBe providingLettings()(using getRequest, messages).toString
    }

  }

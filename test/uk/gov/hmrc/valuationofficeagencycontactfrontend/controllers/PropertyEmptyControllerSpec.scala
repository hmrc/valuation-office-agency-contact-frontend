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
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions.{DataRequiredActionImpl, DataRetrievalAction}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.MessageControllerComponentsHelpers
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{councilTaxPropertyEmpty => council_tax_property_empty}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{businessRatesPropertyEmpty => business_rates_property_empty}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.councilTaxPropertyEmpty

class PropertyEmptyControllerSpec extends ControllerSpecBase {

  def councilPropertyEmpty: councilTaxPropertyEmpty               = app.injector.instanceOf[council_tax_property_empty]
  def businessRatesPropertyEmpty: html.businessRatesPropertyEmpty = app.injector.instanceOf[business_rates_property_empty]

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new PropertyEmptyController(
      frontendAppConfig,
      messagesApi,
      dataRetrievalAction,
      new DataRequiredActionImpl(ec),
      councilPropertyEmpty,
      businessRatesPropertyEmpty,
      MessageControllerComponentsHelpers.stubMessageControllerComponents
    )

  "Property Empty Controller" must {
    "return the correct council tax view for a GET" in {
      val result = controller().onPageLoad()(fakeRequest)
      contentAsString(result) mustBe councilPropertyEmpty(frontendAppConfig)(using fakeRequest, messages).toString
    }

    "return the correct business rates view for a GET" in {
      val result = controller().onBusinessRatesPageLoad()(fakeRequest)
      contentAsString(result) mustBe businessRatesPropertyEmpty(frontendAppConfig)(using fakeRequest, messages).toString
    }
  }
}

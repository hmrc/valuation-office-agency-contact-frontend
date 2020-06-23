/*
 * Copyright 2020 HM Revenue & Customs
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
import uk.gov.hmrc.valuationofficeagencycontactfrontend.FakeNavigator
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions.{DataRequiredActionImpl, DataRetrievalAction}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.NormalMode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.MessageControllerComponentsHelpers
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.businessRatesSmartLinks

class BusinessRatesSmartLinksControllerSpec extends ControllerSpecBase {

  def onwardRoute = routes.EnquiryCategoryController.onPageLoad(NormalMode)

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new BusinessRatesSmartLinksController(frontendAppConfig, messagesApi, new FakeNavigator(desiredRoute = onwardRoute),
      dataRetrievalAction, new DataRequiredActionImpl(ec), MessageControllerComponentsHelpers.stubMessageControllerComponents)

  "Business Rates Smart Links Controller" must {
    "return 200 for a GET" in {
      val result = controller().onPageLoad(fakeRequest)
      status(result) mustBe OK
    }

    "return the correct view for a GET" in {
      val result = controller().onPageLoad(fakeRequest)
      contentAsString(result) mustBe businessRatesSmartLinks(frontendAppConfig)(fakeRequest, messages).toString
    }

    "return a redirect when calling goToBusinessRatesSubcategoryPage" in {
      val result = controller().goToBusinessRatesSubcategoryPage()(fakeRequest)
      status(result) mustBe SEE_OTHER
    }

  }
}

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

import org.scalatestplus.mockito.MockitoSugar
import play.api.data.Form
import play.api.libs.json.JsString
import play.api.mvc.Call
import play.api.test.Helpers.*
import uk.gov.hmrc.vo.contact.frontend.FakeNavigator
import uk.gov.hmrc.vo.contact.frontend.connectors.FakeDataCacheConnector
import uk.gov.hmrc.vo.contact.frontend.controllers.actions.{DataRequiredActionImpl, DataRetrievalAction, FakeDataRetrievalAction}
import uk.gov.hmrc.vo.contact.frontend.forms.DatePropertyChangedForm.datePropertyChangedForm
import uk.gov.hmrc.vo.contact.frontend.identifiers.CouncilTaxSubcategoryId
import uk.gov.hmrc.vo.contact.frontend.models.{CacheMap, NormalMode}
import uk.gov.hmrc.vo.contact.frontend.utils.MessageControllerComponentsHelpers
import uk.gov.hmrc.vo.contact.frontend.views.html.datePropertyChanged

import java.time.LocalDate

class DatePropertyChangedControllerSpec extends ControllerSpecBase with MockitoSugar:

  private def datePropertyChangedView = inject[datePropertyChanged]

  private def routePoorRepair: Call = routes.PropertyWindWaterController.onPageLoad()
  private def routeBusiness: Call   = routes.CouncilTaxBusinessController.onPageLoad()
  private def routeAreaChange: Call = routes.CouncilTaxSubcategoryController.onPageLoad(NormalMode)

  private def controllerPoorRepair(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    DatePropertyChangedController(
      messagesApi,
      FakeDataCacheConnector,
      FakeNavigator(desiredRoute = routePoorRepair),
      dataRetrievalAction,
      DataRequiredActionImpl(ec),
      datePropertyChangedView,
      MessageControllerComponentsHelpers.stubMessageControllerComponents
    )

  private def controllerBusiness(dataRetrievalAction: DataRetrievalAction) =
    DatePropertyChangedController(
      messagesApi,
      FakeDataCacheConnector,
      FakeNavigator(desiredRoute = routeBusiness),
      dataRetrievalAction,
      DataRequiredActionImpl(ec),
      datePropertyChangedView,
      MessageControllerComponentsHelpers.stubMessageControllerComponents
    )

  private def controllerAreaChange(dataRetrievalAction: DataRetrievalAction) =
    DatePropertyChangedController(
      messagesApi,
      FakeDataCacheConnector,
      FakeNavigator(desiredRoute = routeAreaChange),
      dataRetrievalAction,
      DataRequiredActionImpl(ec),
      datePropertyChangedView,
      MessageControllerComponentsHelpers.stubMessageControllerComponents
    )

  private def viewBusinessAsString(form: Form[Option[LocalDate]] = datePropertyChangedForm): String =
    datePropertyChangedView(form, "datePropertyChanged.business", routeAreaChange.url)(using fakeRequest, messages).toString

  private def viewAreaChangeAsString(form: Form[Option[LocalDate]] = datePropertyChangedForm): String =
    datePropertyChangedView(form, "datePropertyChanged.areaChange", routeAreaChange.url)(using fakeRequest, messages).toString

  "DatePropertyChangedController Controller" must {

    "return OK and the date view for business sub category" in {
      val validData = Map(CouncilTaxSubcategoryId.toString -> JsString("council_tax_business_uses"))

      val getRelevantData = FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controllerBusiness(getRelevantData).onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewBusinessAsString()
    }

    "return OK and the date view for area change sub category" in {
      val validData = Map(CouncilTaxSubcategoryId.toString -> JsString("council_tax_area_change"))

      val getRelevantData = FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controllerAreaChange(getRelevantData).onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAreaChangeAsString()
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", LocalDate.of(2021, 1, 1).toString))

      val result = controllerPoorRepair().onSubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routePoorRepair.url)
    }

    "return error page if no existing data is found" in {
      val result = controllerPoorRepair(dontGetAnyData).onPageLoad(NormalMode)(fakeRequest)
      status(result) mustBe SEE_OTHER
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      val validData = Map(CouncilTaxSubcategoryId.toString -> JsString("error"))

      val getRelevantData = FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controllerPoorRepair(getRelevantData).onPageLoad(NormalMode)(fakeRequest)

      an[RuntimeException] should be thrownBy status(result)
    }
  }

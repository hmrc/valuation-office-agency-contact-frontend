/*
 * Copyright 2022 HM Revenue & Customs
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

import org.scalatestplus.mockito.MockitoSugar
import play.api.data.Form
import play.api.libs.json.JsString
import play.api.test.Helpers._
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.valuationofficeagencycontactfrontend.FakeNavigator
import uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors.FakeDataCacheConnector
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions.{DataRequiredActionImpl, DataRetrievalAction, FakeDataRetrievalAction}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.DatePropertyChangedForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers.CouncilTaxSubcategoryId
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.NormalMode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.{MessageControllerComponentsHelpers, UserAnswers}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{datePropertyChanged => date_property_changed}

import java.time.LocalDate

class DatePropertyChangedControllerSpec extends ControllerSpecBase with MockitoSugar{

  val mockUserAnswers = mock[UserAnswers]

  def datePropertyChanged = inject[date_property_changed]

  def routePoorRepair = routes.PropertyWindWaterController.onPageLoad()
  def routeBusiness = routes.CouncilTaxBusinessController.onPageLoad()
  def routeAreaChange = routes.CouncilTaxSubcategoryController.onPageLoad(NormalMode)

  def controllerPoorRepair(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new DatePropertyChangedController(frontendAppConfig, messagesApi, FakeDataCacheConnector, new FakeNavigator(desiredRoute = routePoorRepair),
      dataRetrievalAction, new DataRequiredActionImpl(ec), datePropertyChanged, MessageControllerComponentsHelpers.stubMessageControllerComponents)

  def controllerBusiness(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new DatePropertyChangedController(frontendAppConfig, messagesApi, FakeDataCacheConnector, new FakeNavigator(desiredRoute = routeBusiness),
      dataRetrievalAction, new DataRequiredActionImpl(ec), datePropertyChanged, MessageControllerComponentsHelpers.stubMessageControllerComponents)

  def controllerAreaChange(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new DatePropertyChangedController(frontendAppConfig, messagesApi, FakeDataCacheConnector, new FakeNavigator(desiredRoute = routeAreaChange),
      dataRetrievalAction, new DataRequiredActionImpl(ec), datePropertyChanged, MessageControllerComponentsHelpers.stubMessageControllerComponents)

  def viewBusinessAsString(form: Form[Option[LocalDate]] = DatePropertyChangedForm()) =
    datePropertyChanged(frontendAppConfig, form, NormalMode, "datePropertyChanged.business", routeAreaChange.url)(fakeRequest, messages).toString

  def viewAreaChangeAsString(form: Form[Option[LocalDate]] = DatePropertyChangedForm()) =
    datePropertyChanged(frontendAppConfig, form, NormalMode, "datePropertyChanged.areaChange", routeAreaChange.url)(fakeRequest, messages).toString

  "DatePropertyChangedController Controller" must {


    "return OK and the date view for business sub category" in {
      val validData = Map(CouncilTaxSubcategoryId.toString -> JsString("council_tax_business_uses"))

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controllerBusiness(getRelevantData).onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewBusinessAsString()
    }

    "return OK and the date view for area change sub category" in {
      val validData = Map(CouncilTaxSubcategoryId.toString -> JsString("council_tax_area_change"))

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controllerAreaChange(getRelevantData).onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAreaChangeAsString()
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", LocalDate.of(2021,1,1).toString))

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

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controllerPoorRepair(getRelevantData).onPageLoad(NormalMode)(fakeRequest)

      an[RuntimeException] should be thrownBy status(result)
    }
  }

}

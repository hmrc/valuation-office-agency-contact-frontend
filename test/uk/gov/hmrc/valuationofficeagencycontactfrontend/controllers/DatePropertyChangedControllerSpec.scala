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

import play.api.data.Form
import play.api.libs.json.JsString
import play.api.test.Helpers._
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.valuationofficeagencycontactfrontend.FakeNavigator
import uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors.FakeDataCacheConnector
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions.{DataRequiredActionImpl, DataRetrievalAction, FakeDataRetrievalAction}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.DatePropertyChangedForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers.DatePropertyChangedId
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.NormalMode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.MessageControllerComponentsHelpers
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{datePropertyChanged => date_property_changed}

import java.time.LocalDate

class DatePropertyChangedControllerSpec extends ControllerSpecBase {

  def datePropertyChanged = inject[date_property_changed]

  def route = routes.DatePropertyChangedController.onPageLoad()


  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new DatePropertyChangedController(frontendAppConfig, messagesApi, FakeDataCacheConnector, new FakeNavigator(desiredRoute = route),
      dataRetrievalAction, new DataRequiredActionImpl(ec), datePropertyChanged, MessageControllerComponentsHelpers.stubMessageControllerComponents)

  def viewAsString(form: Form[Option[LocalDate]] = DatePropertyChangedForm()) = datePropertyChanged(frontendAppConfig, form, NormalMode)(fakeRequest, messages).toString

  "DatePropertyChangedController Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "populate the view correctly on a GET when the date has previously been inserted" in {
      val validData = Map(DatePropertyChangedId.toString -> JsString(LocalDate.of(2021,1,1).toString))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeRequest)

      contentAsString(result) mustBe viewAsString(DatePropertyChangedForm().fill(Option(LocalDate.of(2021,1,1))))
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", LocalDate.of(2021,1,1).toString))

      val result = controller().onSubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(route.url)
    }

    "return error page if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad(NormalMode)(fakeRequest)
      status(result) mustBe SEE_OTHER
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", LocalDate.of(2021,1,1).toString))
      val result = controller(dontGetAnyData).onSubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(route.url)
    }
  }

}

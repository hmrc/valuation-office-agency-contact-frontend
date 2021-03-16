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

import org.mockito.Matchers.{any, anyString}
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.data.Form
import play.api.libs.json.JsString
import play.api.test.Helpers.{contentAsString, status}
import uk.gov.hmrc.http.cache.client.CacheMap
import play.api.test.Helpers._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.FakeNavigator
import uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions.{DataRequiredActionImpl, DataRetrievalAction, FakeDataRetrievalAction}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.{CouncilTaxBusinessEnquiryForm, CouncilTaxSubcategoryForm, DatePropertyChangedForm}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers.{CouncilTaxBusinessEnquiryId, DatePropertyChangedId}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.NormalMode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.MessageControllerComponentsHelpers
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{councilTaxBusinessEnquiry => council_tax_business_enquiry}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{propertySmallPartUsed => small_part_used}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{datePropertyBusinessChanged => date_pro_bus_changed}

import java.time.LocalDate
import scala.concurrent.Future

class CouncilTaxBusinessControllerSpec extends ControllerSpecBase with MockitoSugar {

  val fakeDataCacheConnector = mock[DataCacheConnector]

  def councilTaxBusinessEnquiry = app.injector.instanceOf[council_tax_business_enquiry]
  def propertySmallPartUsed = app.injector.instanceOf[small_part_used]
  def datePropertyBusinessChanged = app.injector.instanceOf[date_pro_bus_changed]

  def onwardRoute = routes.CouncilTaxBusinessController.onDateChangedPageLoad()
  def onwardRouteTellAsMore = routes.TellUsMoreController.onPageLoad(NormalMode)

  when(fakeDataCacheConnector.save(any, any, any)(any))
    .thenReturn(Future.successful(CacheMap("council_tax_business_uses", Map("council_tax_business_uses" -> JsString("bar")))))

  when(fakeDataCacheConnector.remove(anyString, anyString)).thenReturn(Future.successful(true))

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) = {
    new CouncilTaxBusinessController(frontendAppConfig, messagesApi, fakeDataCacheConnector, new FakeNavigator(desiredRoute = onwardRoute),
      dataRetrievalAction, new DataRequiredActionImpl(ec), councilTaxBusinessEnquiry, propertySmallPartUsed, datePropertyBusinessChanged,
      MessageControllerComponentsHelpers.stubMessageControllerComponents)
  }

  def dateController(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) = {
    new CouncilTaxBusinessController(frontendAppConfig, messagesApi, fakeDataCacheConnector, new FakeNavigator(desiredRoute = onwardRouteTellAsMore),
      dataRetrievalAction, new DataRequiredActionImpl(ec), councilTaxBusinessEnquiry, propertySmallPartUsed, datePropertyBusinessChanged,
      MessageControllerComponentsHelpers.stubMessageControllerComponents)
  }

  def viewAsString(form: Form[String] = CouncilTaxBusinessEnquiryForm()) = councilTaxBusinessEnquiry(frontendAppConfig, form, NormalMode)(fakeRequest, messages).toString

  "CouncilTaxBusiness Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val validData = Map(CouncilTaxBusinessEnquiryId.toString -> JsString(CouncilTaxBusinessEnquiryForm.options.head.value))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeRequest)

      contentAsString(result) mustBe viewAsString(CouncilTaxBusinessEnquiryForm().fill(CouncilTaxBusinessEnquiryForm.options.head.value))
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", CouncilTaxBusinessEnquiryForm.options.head.value))

      val result = controller().onEnquirySubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = CouncilTaxBusinessEnquiryForm().bind(Map("value" -> "invalid value"))

      val result = controller().onEnquirySubmit(NormalMode)(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", CouncilTaxSubcategoryForm.options.head.value))
      val result = controller(dontGetAnyData).onEnquirySubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
    }

    "return OK and the small part of the property is used for business page for GET" in {
      val result = controller().onSmallPartUsedPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe propertySmallPartUsed(frontendAppConfig)(fakeRequest, messages).toString
    }

    "return OK and the date business property view for a GET" in {
      val result = controller().onDateChangedPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe datePropertyBusinessChanged(frontendAppConfig, DatePropertyChangedForm(), NormalMode)(fakeRequest, messages).toString
    }

    "populate the date business property view correctly on a GET when the date has previously been inserted" in {
      val validData = Map(DatePropertyChangedId.toString -> JsString(LocalDate.of(2021,1,1).toString))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onDateChangedPageLoad(NormalMode)(fakeRequest)
      contentAsString(result) mustBe datePropertyBusinessChanged(frontendAppConfig,
        DatePropertyChangedForm().fill(Option(LocalDate.of(2021,1,1))), NormalMode)(fakeRequest, messages).toString
    }

    "redirect to the next page when valid data is submitted for the date business property" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("datePropertyChanged", LocalDate.of(2021,1,1).toString))

      val result = dateController().onDateChangedSubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.TellUsMoreController.onPageLoad(NormalMode).url)
    }
  }

}

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

package uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers

import org.mockito.ArgumentMatchers.{any, anyString}
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.data.Form
import play.api.libs.json.JsString
import play.api.test.Helpers.{contentAsString, status}
import play.api.test.Helpers._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.FakeNavigator
import uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors.{AuditingService, DataCacheConnector}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions.{DataRequiredActionImpl, DataRetrievalAction, FakeDataRetrievalAction}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.{CouncilTaxBusinessEnquiryForm, CouncilTaxSubcategoryForm}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers.CouncilTaxBusinessEnquiryId
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.{CacheMap, NormalMode}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.MessageControllerComponentsHelpers
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{councilTaxBusinessEnquiry => council_tax_business_enquiry}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{propertySmallPartUsed => small_part_used}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{businessRatesNoNeedToPay => business_rates_no_need_to_pay}

import scala.concurrent.Future
import play.api.mvc.Call
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html

class CouncilTaxBusinessControllerSpec extends ControllerSpecBase with MockitoSugar {

  val fakeDataCacheConnector: DataCacheConnector = mock[DataCacheConnector]

  def councilTaxBusinessEnquiry: html.councilTaxBusinessEnquiry = inject[council_tax_business_enquiry]
  def propertySmallPartUsed: html.propertySmallPartUsed         = inject[small_part_used]
  def businessRatesNoNeedToPay: html.businessRatesNoNeedToPay   = inject[business_rates_no_need_to_pay]
  def auditService: AuditingService                             = inject[AuditingService]

  def onwardRoute: Call = routes.DatePropertyChangedController.onPageLoad()

  when(fakeDataCacheConnector.save(any, any, any)(using any))
    .thenReturn(Future.successful(CacheMap("council_tax_business_uses", Map("council_tax_business_uses" -> JsString("bar")))))

  when(fakeDataCacheConnector.remove(anyString, anyString)).thenReturn(Future.successful(true))

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new CouncilTaxBusinessController(
      messagesApi,
      auditService,
      fakeDataCacheConnector,
      new FakeNavigator(desiredRoute = onwardRoute),
      dataRetrievalAction,
      new DataRequiredActionImpl(ec),
      councilTaxBusinessEnquiry,
      propertySmallPartUsed,
      businessRatesNoNeedToPay,
      MessageControllerComponentsHelpers.stubMessageControllerComponents
    )

  def viewAsString(form: Form[String] = CouncilTaxBusinessEnquiryForm()): String = councilTaxBusinessEnquiry(
    form,
    NormalMode,
    routes.CouncilTaxSubcategoryController.onPageLoad(NormalMode).url
  )(using fakeRequest, messages).toString

  "CouncilTaxBusiness Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val validData       = Map(CouncilTaxBusinessEnquiryId.toString -> JsString(CouncilTaxBusinessEnquiryForm.options.head.value))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeRequest)

      contentAsString(result) mustBe viewAsString(CouncilTaxBusinessEnquiryForm().fill(CouncilTaxBusinessEnquiryForm.options.head.value))
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withMethod("POST").withFormUrlEncodedBody(("value", CouncilTaxBusinessEnquiryForm.options.head.value))

      val result = controller().onEnquirySubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val postRequest = fakeRequest.withMethod("POST").withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm   = CouncilTaxBusinessEnquiryForm().bind(Map("value" -> "invalid value"))

      val result = controller().onEnquirySubmit(NormalMode)(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad.url)
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", CouncilTaxSubcategoryForm.options.head.value))
      val result      = controller(dontGetAnyData).onEnquirySubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad.url)
    }

    "return OK and the small part of the property is used for business page for GET" in {
      val result = controller().onSmallPartUsedPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe propertySmallPartUsed()(using fakeRequest, messages).toString

    }

    "return OK and the small part of the property is used for business rates page for GET" in {
      val result = controller().onSmallPartUsedBusinessRatesPageLoad()(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe businessRatesNoNeedToPay()(using fakeRequest, messages).toString

    }
  }
}

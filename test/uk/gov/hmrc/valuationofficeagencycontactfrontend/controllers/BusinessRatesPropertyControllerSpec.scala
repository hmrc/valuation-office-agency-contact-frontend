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

import play.api.data.Form
import play.api.libs.json.JsString
import play.api.test.Helpers._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.FakeNavigator
import uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors.{AuditingService, FakeDataCacheConnector}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions.{DataRequiredActionImpl, DataRetrievalAction, FakeDataRetrievalAction}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.BusinessRatesPropertyForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers.BusinessRatesPropertyEnquiryId
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.{CacheMap, NormalMode}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.MessageControllerComponentsHelpers._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{businessRatesPropertyEnquiry => business_rates_property_enquiry}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{businessRatesNonBusiness => business_rates_non_business}
import play.api.mvc.Call
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html

class BusinessRatesPropertyControllerSpec extends ControllerSpecBase {

  def businessRatesPropertyEnquiry: html.businessRatesPropertyEnquiry = inject[business_rates_property_enquiry]
  def businessRatesNonBusiness: html.businessRatesNonBusiness         = inject[business_rates_non_business]
  def auditService: AuditingService                                   = inject[AuditingService]

  def onwardRoute: Call = routes.EnquiryCategoryController.onPageLoad(NormalMode)

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new BusinessRatesPropertyController(
      frontendAppConfig,
      messagesApi,
      auditService,
      FakeDataCacheConnector,
      new FakeNavigator(desiredRoute = onwardRoute),
      dataRetrievalAction,
      new DataRequiredActionImpl(ec),
      businessRatesPropertyEnquiry,
      businessRatesNonBusiness,
      stubMessageControllerComponents
    )

  def viewAsString(form: Form[String] = BusinessRatesPropertyForm()): String =
    businessRatesPropertyEnquiry(frontendAppConfig, form, NormalMode)(using fakeRequest, messages).toString

  "BusinessRatesSelfCateringController" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val validData       = Map(BusinessRatesPropertyEnquiryId.toString -> JsString(BusinessRatesPropertyForm.options.head.value))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeRequest)

      contentAsString(result) mustBe viewAsString(BusinessRatesPropertyForm().fill(BusinessRatesPropertyForm.options.head.value))
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withMethod("POST").withFormUrlEncodedBody(("value", BusinessRatesPropertyForm.options.head.value))

      val result = controller().onSubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val postRequest = fakeRequest.withMethod("POST").withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm   = BusinessRatesPropertyForm().bind(Map("value" -> "invalid value"))

      val result = controller().onSubmit(NormalMode)(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "return OK and the correct view when onNonBusinessPageLoad is called" in {
      val result = controller().onNonBusinessPageLoad(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe businessRatesNonBusiness(frontendAppConfig)(using fakeRequest, messages).toString()
    }
  }
}

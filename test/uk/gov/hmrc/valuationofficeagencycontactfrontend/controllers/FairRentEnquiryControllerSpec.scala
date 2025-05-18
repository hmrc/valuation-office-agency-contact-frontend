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
import play.api.test.Helpers.{contentAsString, redirectLocation, status, _}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.FakeNavigator
import uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors.{AuditingService, FakeDataCacheConnector}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions.{DataRequiredActionImpl, DataRetrievalAction, FakeDataRetrievalAction}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.FairRentEnquiryForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers.FairRentEnquiryId
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.{CacheMap, NormalMode}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.MessageControllerComponentsHelpers._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{fairRentEnquiry => fair_rent_enquiry}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{checkFairRentApplication => check_fair_rent_application, submitFairRentApplication => submit_fair_rent_application}
import play.api.mvc.Call
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{checkFairRentApplication, fairRentEnquiry, submitFairRentApplication}

class FairRentEnquiryControllerSpec extends ControllerSpecBase {

  def fairRentEnquiryEnquiry: fairRentEnquiry          = inject[fair_rent_enquiry]
  def onFairRentEnquiryNew: submitFairRentApplication  = inject[submit_fair_rent_application]
  def onFairRentEnquiryCheck: checkFairRentApplication = inject[check_fair_rent_application]
  def auditService: AuditingService                    = inject[AuditingService]

  def onwardRoute: Call = routes.EnquiryCategoryController.onPageLoad(NormalMode)

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new FairRentEnquiryController(
      frontendAppConfig,
      messagesApi,
      auditService,
      FakeDataCacheConnector,
      new FakeNavigator(desiredRoute = onwardRoute),
      dataRetrievalAction,
      new DataRequiredActionImpl(ec),
      fairRentEnquiryEnquiry,
      onFairRentEnquiryNew,
      onFairRentEnquiryCheck,
      stubMessageControllerComponents
    )

  def viewAsString(form: Form[String] = FairRentEnquiryForm()): String =
    fairRentEnquiryEnquiry(frontendAppConfig, form, NormalMode)(using fakeRequest, messages).toString

  "PropertyEnglandLets140DaysController" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "return OK and the correct view for england lets no business rates page GET" in {
      val result = controller().onFairRentEnquiryNew(NormalMode)(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe onFairRentEnquiryNew()(using fakeRequest, messages).toString()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val validData       = Map(FairRentEnquiryId.toString -> JsString(FairRentEnquiryForm.options.head.value))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeRequest)

      contentAsString(result) mustBe viewAsString(FairRentEnquiryForm().fill(FairRentEnquiryForm.options.head.value))
    }

    "redirect to no action page when valid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", FairRentEnquiryForm.options.head.value))

      val result = controller().onFairRentEnquiryNew(NormalMode)(postRequest)

      status(result) mustBe OK
    }

    "redirect to no action page when valid data for check is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", FairRentEnquiryForm.options.head.value))

      val result = controller().onFairRentEnquiryCheck(NormalMode)(postRequest)

      status(result) mustBe OK
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withMethod("POST").withFormUrlEncodedBody(("value", FairRentEnquiryForm.options.head.value))

      val result = controller().onSubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val postRequest = fakeRequest.withMethod("POST").withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm   = FairRentEnquiryForm().bind(Map("value" -> "invalid value"))

      val result = controller().onSubmit(NormalMode)(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad.url)
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", FairRentEnquiryForm.options.head.value))
      val result      = controller(dontGetAnyData).onSubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad.url)
    }

  }

}

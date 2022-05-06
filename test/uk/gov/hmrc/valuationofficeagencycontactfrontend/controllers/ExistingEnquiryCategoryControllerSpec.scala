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

import play.api.data.Form
import play.api.libs.json.JsString
import play.api.test.Helpers._
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.valuationofficeagencycontactfrontend.FakeNavigator
import uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors.{AuditingService, FakeDataCacheConnector}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.{ContactReasonForm, ExistingEnquiryCategoryForm}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers.{ContactReasonId, ExistingEnquiryCategoryId}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.NormalMode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.MessageControllerComponentsHelpers
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{existingEnquiryCategory => existing_enquiry_category}

class ExistingEnquiryCategoryControllerSpec extends ControllerSpecBase {

  def existingEnquiryCategory = inject[existing_enquiry_category]
  def auditService = inject[AuditingService]

  def contactReasonRoute = routes.ContactReasonController.onPageLoad()
  def enquiryDateRoute = routes.EnquiryDateController.onPageLoad()

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new ExistingEnquiryCategoryController(frontendAppConfig, messagesApi, auditService, FakeDataCacheConnector,
      new FakeNavigator(desiredRoute = contactReasonRoute), dataRetrievalAction, new DataRequiredActionImpl(ec),
      existingEnquiryCategory, MessageControllerComponentsHelpers.stubMessageControllerComponents)

  def viewAsString(form: Form[String] = ExistingEnquiryCategoryForm()) =
    existingEnquiryCategory(frontendAppConfig, form, NormalMode, contactReasonRoute.url)(fakeRequest, messages).toString

  def viewAsStringEnquiryDate(form: Form[String] = ExistingEnquiryCategoryForm()) =
    existingEnquiryCategory(frontendAppConfig, form, NormalMode, enquiryDateRoute.url)(fakeRequest, messages).toString

  "ExistingEnquiryCategory Controller" must {

    "return OK and the correct view for a GET when the contact reason is new_enquiry" in {
      val contactReason = "new_enquiry"

      val validData = Map(ContactReasonId.toString -> JsString(contactReason))

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "return OK and the correct view for a GET when the contact reason is more_details" in {
      val contactReason = "more_details"

      val validData = Map(ContactReasonId.toString -> JsString(contactReason))

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "return OK and the correct view for a GET when the contact reason is update_existing" in {
      val contactReason = "update_existing"

      val validData = Map(ContactReasonId.toString -> JsString(contactReason))

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsStringEnquiryDate()
    }

    "return a runtime exception when the contact reason is empty" in {
      val contactReason = ""

      val validData = Map(ContactReasonId.toString -> JsString(contactReason))

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      intercept[RuntimeException] {
        val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeRequest)

        status(result) mustBe OK
        contentAsString(result) mustBe viewAsStringEnquiryDate()
      }
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val validData = Map(ContactReasonId.toString -> JsString(ContactReasonForm.options.head.value),
                          ExistingEnquiryCategoryId.toString -> JsString(ExistingEnquiryCategoryForm.options.head.value))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeRequest)

      contentAsString(result) mustBe viewAsString(ExistingEnquiryCategoryForm().fill(ExistingEnquiryCategoryForm.options.head.value))
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withMethod("POST").withFormUrlEncodedBody(("value", "council_tax"))

      val result = controller().onSubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(contactReasonRoute.url)
    }

    "redirect to the next page when valid data is submitted with business_rates" in {
      val postRequest = fakeRequest.withMethod("POST").withFormUrlEncodedBody(("value", "business_rates"))

      val result = controller().onSubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(contactReasonRoute.url)
    }

    "redirect to the next page when valid data is submitted with housing_benefit" in {
      val postRequest = fakeRequest.withMethod("POST").withFormUrlEncodedBody(("value", "housing_benefit"))

      val result = controller().onSubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(contactReasonRoute.url)
    }

    "redirect to the next page when valid data is submitted with fair_rent" in {
      val postRequest = fakeRequest.withMethod("POST").withFormUrlEncodedBody(("value", "fair_rent"))

      val result = controller().onSubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(contactReasonRoute.url)
    }

    "redirect to the next page when valid data is submitted with other" in {
      val postRequest = fakeRequest.withMethod("POST").withFormUrlEncodedBody(("value", "other"))

      val result = controller().onSubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(contactReasonRoute.url)
    }
  }
}

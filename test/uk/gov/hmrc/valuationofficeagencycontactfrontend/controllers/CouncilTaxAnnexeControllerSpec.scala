/*
 * Copyright 2023 HM Revenue & Customs
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

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.data.Form
import play.api.libs.json.JsString
import play.api.test.Helpers._
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.valuationofficeagencycontactfrontend.FakeNavigator
import uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors.{AuditingService, DataCacheConnector}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.{AnnexeCookingWashingForm, AnnexeForm, AnnexeSelfContainedForm}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers.{CouncilTaxAnnexeEnquiryId, CouncilTaxAnnexeHaveCookingId, CouncilTaxAnnexeSelfContainedEnquiryId}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.NormalMode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.MessageControllerComponentsHelpers
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{councilTaxAnnexe => council_tax_annexe}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{annexeSelfContainedEnquiry => annexe_self_contained_enquiry}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{annexeNoFacilities => annexe_no_facilities}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{annexeSelfContained => annexe_self_contained}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{annexeNotSelfContained => annexe_not_self_contained}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{annexeCookingWashingEnquiry => annexe_cooking_washing_enquiry}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{annexeRemoved => annexe_removed}

import scala.concurrent.Future

class CouncilTaxAnnexeControllerSpec extends ControllerSpecBase {

  def councilTaxAnnexe = inject[council_tax_annexe]
  def councilTaxAnnexeSelfContainedEnquiry = inject[annexe_self_contained_enquiry]
  def councilTaxAnnexeNotSelfContained = inject[annexe_not_self_contained]
  def councilTaxAnnexeNoFacilities = inject[annexe_no_facilities]
  def councilTaxAnnexeSelfContained = inject[annexe_self_contained]
  def annexeCookingWashingEnquiry = inject[annexe_cooking_washing_enquiry]
  def annexeNotSelfContained = inject[annexe_not_self_contained]
  def annexeRemoved = inject[annexe_removed]
  def auditService = inject[AuditingService]

  val fakeDataCacheConnector = mock[DataCacheConnector]

  def onwardRoute = routes.EnquiryCategoryController.onPageLoad(NormalMode)

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new CouncilTaxAnnexeController(frontendAppConfig, messagesApi, auditService, fakeDataCacheConnector, new FakeNavigator(desiredRoute = onwardRoute),
      dataRetrievalAction, new DataRequiredActionImpl(ec), councilTaxAnnexe, councilTaxAnnexeSelfContainedEnquiry, councilTaxAnnexeNotSelfContained,
      councilTaxAnnexeNoFacilities, councilTaxAnnexeSelfContained, annexeCookingWashingEnquiry, annexeRemoved, MessageControllerComponentsHelpers.stubMessageControllerComponents)

  def viewAsString(form: Form[String] = AnnexeForm()) = councilTaxAnnexe(frontendAppConfig, form, NormalMode)(fakeRequest, messages).toString
  def viewCookingWashingAsString(form: Form[String] = AnnexeCookingWashingForm()) = annexeCookingWashingEnquiry(frontendAppConfig, form)(fakeRequest, messages).toString
  def viewcouncilTaxAnnexeSelfContainedEnquiry(form: Form[String] = AnnexeSelfContainedForm()) = councilTaxAnnexeSelfContainedEnquiry(frontendAppConfig, form)(fakeRequest, messages).toString
  "Council Tax Annex Controller" must {
    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val validData = Map(CouncilTaxAnnexeEnquiryId.toString -> JsString(AnnexeForm.options.head.value))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeRequest)

      contentAsString(result) mustBe viewAsString(AnnexeForm().fill(AnnexeForm.options.head.value))
    }

    "redirect to the next page when valid data is submitted" in {
      when(fakeDataCacheConnector.remove(any[String], any[String]))
        .thenReturn(Future.successful(true))

      when(fakeDataCacheConnector.save(any, any, any)(any))
        .thenReturn(Future.successful(CacheMap("council_tax_annexe", Map("council_tax_annexe" -> JsString("bar")))))

      val postRequest = fakeRequest.withMethod("POST").withFormUrlEncodedBody(("value", AnnexeForm.options.head.value))

      val result = controller().onSubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val postRequest = fakeRequest.withMethod("POST").withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = AnnexeForm().bind(Map("value" -> "invalid value"))

      val result = controller().onSubmit(NormalMode)(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "return error page if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad(NormalMode)(fakeRequest)
      status(result) mustBe SEE_OTHER
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      val postRequest = fakeRequest.withMethod("POST").withFormUrlEncodedBody(("value", AnnexeForm.options.head.value))
      val result = controller(dontGetAnyData).onSubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "return OK and the correct view for a annex removed GET" in {
      val result = controller().onRemovedPageLoad()(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe annexeRemoved(frontendAppConfig)(fakeRequest, messages).toString()
    }

    "redirect to the next page when valid data is submitted for annexe self contained" in {

      when(fakeDataCacheConnector.save(any, any, any)(any))
        .thenReturn(Future.successful(CacheMap("annexeSelfContained", Map("annexeSelfContained" -> JsString("bar")))))

      val postRequest = fakeRequest.withMethod("POST").withFormUrlEncodedBody(("value", AnnexeSelfContainedForm.options.head.value))

      val result = controller().onSelfContainedSubmit()(postRequest)

      status(result) mustBe SEE_OTHER
    }

    "return a Bad Request and errors when invalid data is submitted for annexe self contained form" in {
      val postRequest = fakeRequest.withMethod("POST").withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = AnnexeSelfContainedForm().bind(Map("value" -> "invalid value"))

      val result = controller().onSelfContainedSubmit()(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe councilTaxAnnexeSelfContainedEnquiry(frontendAppConfig, boundForm)(fakeRequest, messages).toString()
    }

    "return OK and the correct view for a no cooking and washing facilities GET" in {
      val result = controller().onFacilitiesPageLoad()(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe councilTaxAnnexeNoFacilities(frontendAppConfig)(fakeRequest, messages).toString()
    }

    "return OK and the correct view for a not self contained page GET" in {
      val result = controller().onNotSelfContainedPageLoad()(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe annexeNotSelfContained(frontendAppConfig)(fakeRequest, messages).toString()
    }

    "return OK and the correct view for annexe self contained GET" in {
      val result = controller().onSelfContainedPageLoad()(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe councilTaxAnnexeSelfContained(frontendAppConfig)(fakeRequest, messages).toString()
    }

    "return OK and the correct view when onHaveCookingWashingPageLoad is called" in {
      val result = controller().onHaveCookingWashingPageLoad(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewCookingWashingAsString()
    }

    "populate the view correctly on a GET when the question has previously been answered for cooking washing page" in {
      val validData = Map(CouncilTaxAnnexeHaveCookingId.toString -> JsString(AnnexeCookingWashingForm.options.head.value))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onHaveCookingWashingPageLoad(fakeRequest)

      contentAsString(result) mustBe viewCookingWashingAsString(AnnexeCookingWashingForm().fill(AnnexeCookingWashingForm.options.head.value))
    }

    "redirect to the next page when valid data is submitted for annexe cooking washing form" in {
      when(fakeDataCacheConnector.remove(any[String], any[String]))
        .thenReturn(Future.successful(true))

      when(fakeDataCacheConnector.save(any, any, any)(any))
        .thenReturn(Future.successful(CacheMap("annexeCookingWashing.form", Map("annexeCookingWashing.form" -> JsString("yes")))))

      val postRequest = fakeRequest.withMethod("POST").withFormUrlEncodedBody(("value", AnnexeCookingWashingForm.options.head.value))

      val result = controller().onHaveCookingWashingSubmit(postRequest)

      status(result) mustBe SEE_OTHER
      //redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "return a Bad Request and errors when invalid data is submitted for annexe cooking washing form" in {
      val postRequest = fakeRequest.withMethod("POST").withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = AnnexeCookingWashingForm().bind(Map("value" -> "invalid value"))

      val result = controller().onHaveCookingWashingSubmit()(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewCookingWashingAsString(boundForm)
    }

    "return OK and the correct view for a GET for Self Contained Enquiry Page" in {
      val result = controller().onSelfContainedEnquiryPageLoad(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewcouncilTaxAnnexeSelfContainedEnquiry()
    }

    "populate the view correctly on a GET when the question has previously been answered for Self Contained Enquiry Page" in {
      val validData = Map(CouncilTaxAnnexeSelfContainedEnquiryId.toString -> JsString(AnnexeSelfContainedForm.options.head.value))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onSelfContainedEnquiryPageLoad(fakeRequest)

      contentAsString(result) mustBe viewcouncilTaxAnnexeSelfContainedEnquiry(AnnexeSelfContainedForm().fill(AnnexeSelfContainedForm.options.head.value))
    }
  }
}

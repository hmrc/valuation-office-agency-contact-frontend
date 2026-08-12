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

import play.api.data.Form
import play.api.libs.json.JsString
import play.api.test.Helpers.*
import uk.gov.hmrc.vo.contact.frontend.FakeNavigator
import uk.gov.hmrc.vo.contact.frontend.connectors.{AuditingService, DataCacheConnector}
import uk.gov.hmrc.vo.contact.frontend.controllers.actions.*
import uk.gov.hmrc.vo.contact.frontend.forms.{AnnexeCookingWashingForm, AnnexeForm, AnnexeSelfContainedForm}
import uk.gov.hmrc.vo.contact.frontend.identifiers.{CouncilTaxAnnexeEnquiryId, CouncilTaxAnnexeHaveCookingId, CouncilTaxAnnexeSelfContainedEnquiryId}
import uk.gov.hmrc.vo.contact.frontend.models.{CacheMap, NormalMode}
import uk.gov.hmrc.vo.contact.frontend.utils.MessageControllerComponentsHelpers
import uk.gov.hmrc.vo.contact.frontend.views.html.{councilTaxAnnexe => council_tax_annexe}
import uk.gov.hmrc.vo.contact.frontend.views.html.{annexeSelfContainedEnquiry => annexe_self_contained_enquiry}
import uk.gov.hmrc.vo.contact.frontend.views.html.{annexeNoFacilities => annexe_no_facilities}
import uk.gov.hmrc.vo.contact.frontend.views.html.{annexeSelfContained => annexe_self_contained}
import uk.gov.hmrc.vo.contact.frontend.views.html.{annexeNotSelfContained => annexe_not_self_contained}
import uk.gov.hmrc.vo.contact.frontend.views.html.{annexeCookingWashingEnquiry => annexe_cooking_washing_enquiry}
import uk.gov.hmrc.vo.contact.frontend.views.html.{annexeRemoved => annexe_removed}

import scala.concurrent.Future
import play.api.mvc.Call
import uk.gov.hmrc.vo.contact.frontend.views.html
import uk.gov.hmrc.vo.contact.frontend.views.html.{annexeNoFacilities, annexeSelfContained, annexeSelfContainedEnquiry}

class CouncilTaxAnnexeControllerSpec extends ControllerSpecBase:

  def councilTaxAnnexe: html.councilTaxAnnexe                          = inject[council_tax_annexe]
  def councilTaxAnnexeSelfContainedEnquiry: annexeSelfContainedEnquiry = inject[annexe_self_contained_enquiry]
  def councilTaxAnnexeNotSelfContained: html.annexeNotSelfContained    = inject[annexe_not_self_contained]
  def councilTaxAnnexeNoFacilities: annexeNoFacilities                 = inject[annexe_no_facilities]
  def councilTaxAnnexeSelfContained: annexeSelfContained               = inject[annexe_self_contained]
  def annexeCookingWashingEnquiry: html.annexeCookingWashingEnquiry    = inject[annexe_cooking_washing_enquiry]
  def annexeNotSelfContained: html.annexeNotSelfContained              = inject[annexe_not_self_contained]
  def annexeRemoved: html.annexeRemoved                                = inject[annexe_removed]
  def auditService: AuditingService                                    = inject[AuditingService]

  val fakeDataCacheConnector: DataCacheConnector = mock[DataCacheConnector]

  def onwardRoute: Call = routes.EnquiryCategoryController.onPageLoad(NormalMode)

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    CouncilTaxAnnexeController(
      messagesApi,
      auditService,
      fakeDataCacheConnector,
      FakeNavigator(desiredRoute = onwardRoute),
      dataRetrievalAction,
      DataRequiredActionImpl(ec),
      councilTaxAnnexe,
      councilTaxAnnexeSelfContainedEnquiry,
      councilTaxAnnexeNotSelfContained,
      councilTaxAnnexeNoFacilities,
      councilTaxAnnexeSelfContained,
      annexeCookingWashingEnquiry,
      annexeRemoved,
      MessageControllerComponentsHelpers.stubMessageControllerComponents
    )

  def viewAsString(form: Form[String] = AnnexeForm()): String = councilTaxAnnexe(form, NormalMode)(using getRequest, messages).toString

  def viewCookingWashingAsString(form: Form[String] = AnnexeCookingWashingForm()): String =
    annexeCookingWashingEnquiry(form)(using getRequest, messages).toString

  def viewCouncilTaxAnnexeSelfContainedEnquiry(form: Form[String] = AnnexeSelfContainedForm()): String =
    councilTaxAnnexeSelfContainedEnquiry(form)(using getRequest, messages).toString

  "Council Tax Annex Controller" should {
    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(NormalMode)(getRequest)

      status(result)          shouldBe OK
      contentAsString(result) shouldBe viewAsString()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val validData       = Map(CouncilTaxAnnexeEnquiryId.toString -> JsString(AnnexeForm.options.head.value))
      val getRelevantData = FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad(NormalMode)(getRequest)

      contentAsString(result) shouldBe viewAsString(AnnexeForm().fill(AnnexeForm.options.head.value))
    }

    "redirect to the next page when valid data is submitted" in {
      when(fakeDataCacheConnector.remove(any[String], any[String]))
        .thenReturn(Future.successful(true))

      when(fakeDataCacheConnector.save(any, any, any)(using any))
        .thenReturn(Future.successful(CacheMap("council_tax_annexe", Map("council_tax_annexe" -> JsString("bar")))))

      val postRequest = getRequest.withMethod("POST").withFormUrlEncodedBody(("value", AnnexeForm.options.head.value))

      val result = controller().onSubmit(NormalMode)(postRequest)

      status(result)           shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(onwardRoute.url)
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val postRequest = getRequest.withMethod("POST").withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm   = AnnexeForm().bind(Map("value" -> "invalid value"))

      val result = controller().onSubmit(NormalMode)(postRequest)

      status(result)          shouldBe BAD_REQUEST
      contentAsString(result) shouldBe viewAsString(boundForm)
    }

    "return error page if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad(NormalMode)(getRequest)
      status(result) shouldBe SEE_OTHER
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      val postRequest = getRequest.withMethod("POST").withFormUrlEncodedBody(("value", AnnexeForm.options.head.value))
      val result      = controller(dontGetAnyData).onSubmit(NormalMode)(postRequest)

      status(result)           shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(onwardRoute.url)
    }

    "return OK and the correct view for a annex removed GET" in {
      val result = controller().onRemovedPageLoad()(getRequest)

      status(result)          shouldBe OK
      contentAsString(result) shouldBe annexeRemoved()(using getRequest, messages).toString()
    }

    "redirect to the next page when valid data is submitted for annexe self contained" in {

      when(fakeDataCacheConnector.save(any, any, any)(using any))
        .thenReturn(Future.successful(CacheMap("annexeSelfContained", Map("annexeSelfContained" -> JsString("bar")))))

      val postRequest = getRequest.withMethod("POST").withFormUrlEncodedBody(("value", AnnexeSelfContainedForm.options.head.value))

      val result = controller().onSelfContainedSubmit()(postRequest)

      status(result) shouldBe SEE_OTHER
    }

    "return a Bad Request and errors when invalid data is submitted for annexe self contained form" in {
      val postRequest = getRequest.withMethod("POST").withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm   = AnnexeSelfContainedForm().bind(Map("value" -> "invalid value"))

      val result = controller().onSelfContainedSubmit()(postRequest)

      status(result)          shouldBe BAD_REQUEST
      contentAsString(result) shouldBe councilTaxAnnexeSelfContainedEnquiry(boundForm)(using getRequest, messages).toString()
    }

    "return OK and the correct view for a no cooking and washing facilities GET" in {
      val result = controller().onFacilitiesPageLoad()(getRequest)

      status(result)          shouldBe OK
      contentAsString(result) shouldBe councilTaxAnnexeNoFacilities()(using getRequest, messages).toString()
    }

    "return OK and the correct view for a not self contained page GET" in {
      val result = controller().onNotSelfContainedPageLoad()(getRequest)

      status(result)          shouldBe OK
      contentAsString(result) shouldBe annexeNotSelfContained()(using getRequest, messages).toString()
    }

    "return OK and the correct view for annexe self contained GET" in {
      val result = controller().onSelfContainedPageLoad()(getRequest)

      status(result)          shouldBe OK
      contentAsString(result) shouldBe councilTaxAnnexeSelfContained()(using getRequest, messages).toString()
    }

    "return OK and the correct view when onHaveCookingWashingPageLoad is called" in {
      val result = controller().onHaveCookingWashingPageLoad(getRequest)

      status(result)          shouldBe OK
      contentAsString(result) shouldBe viewCookingWashingAsString()
    }

    "populate the view correctly on a GET when the question has previously been answered for cooking washing page" in {
      val validData       = Map(CouncilTaxAnnexeHaveCookingId.toString -> JsString(AnnexeCookingWashingForm.options.head.value))
      val getRelevantData = FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onHaveCookingWashingPageLoad(getRequest)

      contentAsString(result) shouldBe viewCookingWashingAsString(AnnexeCookingWashingForm().fill(AnnexeCookingWashingForm.options.head.value))
    }

    "redirect to the next page when valid data is submitted for annexe cooking washing form" in {
      when(fakeDataCacheConnector.remove(any[String], any[String]))
        .thenReturn(Future.successful(true))

      when(fakeDataCacheConnector.save(any, any, any)(using any))
        .thenReturn(Future.successful(CacheMap("annexeCookingWashing.form", Map("annexeCookingWashing.form" -> JsString("yes")))))

      val postRequest = getRequest.withMethod("POST").withFormUrlEncodedBody(("value", AnnexeCookingWashingForm.options.head.value))

      val result = controller().onHaveCookingWashingSubmit(postRequest)

      status(result) shouldBe SEE_OTHER
      // redirectLocation(result) shouldBe Some(onwardRoute.url)
    }

    "return a Bad Request and errors when invalid data is submitted for annexe cooking washing form" in {
      val postRequest = getRequest.withMethod("POST").withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm   = AnnexeCookingWashingForm().bind(Map("value" -> "invalid value"))

      val result = controller().onHaveCookingWashingSubmit()(postRequest)

      status(result)          shouldBe BAD_REQUEST
      contentAsString(result) shouldBe viewCookingWashingAsString(boundForm)
    }

    "return OK and the correct view for a GET for Self Contained Enquiry Page" in {
      val result = controller().onSelfContainedEnquiryPageLoad(getRequest)

      status(result)          shouldBe OK
      contentAsString(result) shouldBe viewCouncilTaxAnnexeSelfContainedEnquiry()
    }

    "populate the view correctly on a GET when the question has previously been answered for Self Contained Enquiry Page" in {
      val validData       = Map(CouncilTaxAnnexeSelfContainedEnquiryId.toString -> JsString(AnnexeSelfContainedForm.options.head.value))
      val getRelevantData = FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onSelfContainedEnquiryPageLoad(getRequest)

      contentAsString(result) shouldBe viewCouncilTaxAnnexeSelfContainedEnquiry(AnnexeSelfContainedForm().fill(AnnexeSelfContainedForm.options.head.value))
    }
  }

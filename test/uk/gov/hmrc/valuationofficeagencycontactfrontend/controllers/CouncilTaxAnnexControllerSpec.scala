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

///*
// * Copyright 2021 HM Revenue & Customs
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers
//
//import play.api.data.Form
//import play.api.libs.json.JsString
//import play.api.test.Helpers._
//import uk.gov.hmrc.http.cache.client.CacheMap
//import uk.gov.hmrc.valuationofficeagencycontactfrontend.FakeNavigator
//import uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors.FakeDataCacheConnector
//import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions.{DataRequiredActionImpl, DataRetrievalAction, FakeDataRetrievalAction}
//import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.{AnnexeForm, EnquiryCategoryForm}
//import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers.CouncilTaxAnnexId
//import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.NormalMode
//import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.MessageControllerComponentsHelpers
//import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{councilTaxAnnex => council_tax_annex}
//
//class CouncilTaxAnnexControllerSpec extends ControllerSpecBase {
//
//  def councilTaxAnnex = app.injector.instanceOf[council_tax_annex]
//
//  def onwardRoute = routes.CouncilTaxAnnexController.onPageLoad()
//
//  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
//    new CouncilTaxAnnexController(frontendAppConfig, messagesApi, FakeDataCacheConnector, new FakeNavigator(desiredRoute = onwardRoute),
//      dataRetrievalAction, new DataRequiredActionImpl(ec), councilTaxAnnex, MessageControllerComponentsHelpers.stubMessageControllerComponents)
//
//  def viewAsString(form: Form[String] = AnnexeForm()) = councilTaxAnnex(frontendAppConfig, form, NormalMode)(fakeRequest, messages).toString
//
//  "CouncilTaxAnnex Controller" must {
//
//    "return OK and the correct view for a GET" in {
//      val result = controller().onPageLoad(NormalMode)(fakeRequest)
//
//      status(result) mustBe OK
//      contentAsString(result) mustBe viewAsString()
//    }
//
//    "populate the view correctly on a GET when the question has previously been answered" in {
//      val validData = Map(CouncilTaxAnnexId.toString -> JsString(AnnexeForm.options.head.value))
//      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))
//
//      val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeRequest)
//
//      contentAsString(result) mustBe viewAsString(AnnexeForm().fill(AnnexeForm.options.head.value))
//    }
//
//    "redirect to the next page when valid data is submitted" in {
//      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", AnnexeForm.options.head.value))
//
//      val result = controller().onSubmit(NormalMode)(postRequest)
//
//      status(result) mustBe SEE_OTHER
//      redirectLocation(result) mustBe Some(onwardRoute.url)
//    }
//
//    "return a Bad Request and errors when invalid data is submitted" in {
//      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
//      val boundForm = AnnexeForm().bind(Map("value" -> "invalid value"))
//
//      val result = controller().onSubmit(NormalMode)(postRequest)
//
//      status(result) mustBe BAD_REQUEST
//      contentAsString(result) mustBe viewAsString(boundForm)
//    }
//
//    "return eror page if no existing data is found" in {
//      val result = controller(dontGetAnyData).onPageLoad(NormalMode)(fakeRequest)
//      status(result) mustBe SEE_OTHER
//    }
//
//    "redirect to Session Expired for a POST if no existing data is found" in {
//      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", AnnexeForm.options.head.value))
//      val result = controller(dontGetAnyData).onSubmit(NormalMode)(postRequest)
//
//      status(result) mustBe SEE_OTHER
//      redirectLocation(result) mustBe Some(onwardRoute.url)
//    }
//  }
//}
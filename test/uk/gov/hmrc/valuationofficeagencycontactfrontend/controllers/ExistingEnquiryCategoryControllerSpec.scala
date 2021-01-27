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
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.ExistingEnquiryCategoryForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers.ExistingEnquiryCategoryId
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.NormalMode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.MessageControllerComponentsHelpers
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{existingEnquiryCategory => existing_enquiry_category}

class ExistingEnquiryCategoryControllerSpec extends ControllerSpecBase {

  def existingEnquiryCategory = app.injector.instanceOf[existing_enquiry_category]

  def onwardRoute = routes.ExistingEnquiryCategoryController.onPageLoad()

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new ExistingEnquiryCategoryController(frontendAppConfig, messagesApi, FakeDataCacheConnector, new FakeNavigator(desiredRoute = onwardRoute),
      dataRetrievalAction, existingEnquiryCategory, MessageControllerComponentsHelpers.stubMessageControllerComponents)

  def viewAsString(form: Form[String] = ExistingEnquiryCategoryForm()) = existingEnquiryCategory(frontendAppConfig, form, NormalMode)(fakeRequest, messages).toString

  "ExistingEnquiryCategory Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val validData = Map(ExistingEnquiryCategoryId.toString -> JsString(ExistingEnquiryCategoryForm.options.head.value))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeRequest)

      contentAsString(result) mustBe viewAsString(ExistingEnquiryCategoryForm().fill(ExistingEnquiryCategoryForm.options.head.value))
    }

  }
}

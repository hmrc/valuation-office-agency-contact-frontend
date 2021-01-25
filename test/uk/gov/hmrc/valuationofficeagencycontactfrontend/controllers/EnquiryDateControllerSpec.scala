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
import uk.gov.hmrc.valuationofficeagencycontactfrontend.FakeNavigator
import uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors.FakeDataCacheConnector
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions.DataRetrievalAction
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.EnquiryDateForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.NormalMode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.MessageControllerComponentsHelpers
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{enquiryDate => enquiry_date}
import play.api.test.Helpers._


class EnquiryDateControllerSpec extends ControllerSpecBase {


  def enquiryDate = inject[enquiry_date]

  def onwardRoute = routes.EnquiryDateController.onPageLoad()

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new EnquiryDateController(messagesApi, getClearCacheMap, dataRetrievalAction, FakeDataCacheConnector, new FakeNavigator(desiredRoute = onwardRoute),
      enquiryDate, MessageControllerComponentsHelpers.stubMessageControllerComponents)

  def viewAsString(form: Form[String] = EnquiryDateForm()) = enquiryDate(form, EnquiryDateForm.now(), NormalMode)(fakeRequest, messages).toString()


    "EnquiryDateController Controller" must {

      "return OK and the correct view for a GET" in {
        val result = controller().onPageLoad(NormalMode)(fakeRequest)

        status(result) mustBe OK

        contentAsString(result) mustBe viewAsString()

      }

      "redirect to the next page when valid data is submitted" in {
        val postRequest = fakeRequest.withFormUrlEncodedBody(("value", EnquiryDateForm.options.head.value))
        val result = controller().onSubmit(NormalMode)(postRequest)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(onwardRoute.url)

      }
    }


}

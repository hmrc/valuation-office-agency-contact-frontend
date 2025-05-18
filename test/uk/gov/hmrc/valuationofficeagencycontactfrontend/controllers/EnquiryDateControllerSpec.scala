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
import play.api.i18n.{Lang, Messages}
import play.api.libs.json.JsString
import play.api.test.Helpers._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.FakeNavigator
import uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors.{AuditingService, FakeDataCacheConnector}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions.{DataRequiredActionImpl, DataRetrievalAction, FakeDataRetrievalAction}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.EnquiryDateForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers.EnquiryDateId
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.{CacheMap, NormalMode}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.{DateUtil, MessageControllerComponentsHelpers}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{enquiryDate => enquiry_date}

import java.util.Locale
import play.api.mvc.Call
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html

class EnquiryDateControllerSpec extends ControllerSpecBase {

  implicit val messagesEnglish: Messages = messagesApi.preferred(Seq(Lang(Locale.UK)))
  implicit val dateUtil: DateUtil        = injector.instanceOf[DateUtil]

  def enquiryDate: html.enquiryDate = inject[enquiry_date]
  def auditService: AuditingService = inject[AuditingService]

  def onwardRoute: Call = routes.EnquiryDateController.onPageLoad()

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new EnquiryDateController(
      frontendAppConfig,
      messagesApi,
      new DataRequiredActionImpl(ec),
      dataRetrievalAction,
      auditService,
      FakeDataCacheConnector,
      new FakeNavigator(desiredRoute = onwardRoute),
      enquiryDate,
      MessageControllerComponentsHelpers.stubMessageControllerComponents
    )

  def viewAsString(form: Form[String] = EnquiryDateForm()): String =
    enquiryDate(frontendAppConfig, form, EnquiryDateForm.beforeDate(), NormalMode)(using fakeRequest, messages).toString()

  "EnquiryDateController Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe OK

      contentAsString(result) mustBe viewAsString()

    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val validData       = Map(EnquiryDateId.toString -> JsString(EnquiryDateForm.options.head.value))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeRequest)

      contentAsString(result) mustBe viewAsString(EnquiryDateForm().fill(EnquiryDateForm.options.head.value))
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withMethod("POST")
        .withFormUrlEncodedBody(("value", EnquiryDateForm.options.head.value))
      val result      = controller().onSubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)

    }
  }

}

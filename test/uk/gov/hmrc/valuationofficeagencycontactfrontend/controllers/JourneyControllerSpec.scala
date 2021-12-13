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
import play.api.i18n.{DefaultMessagesApi, Lang, Messages, MessagesImpl}
import play.api.libs.json.JsString
import play.api.mvc.{Call, Request}
import play.api.test.Helpers._
import sttp.model.StatusCode.NotImplemented
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors.FakeDataCacheConnector
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.journey.{JourneyMap, NotImplemented, Page}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.journey.pages.HousingBenefitAllowancesRouter
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.NormalMode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.{MessageControllerComponentsHelpers, UserAnswers}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.journey.{journeyView, notImplemented}

class JourneyControllerSpec extends ControllerSpecBase {

  val pageKey = HousingBenefitAllowancesRouter.key
  val form = HousingBenefitAllowancesRouter.form

  def journeyView = app.injector.instanceOf[journeyView]
  def notImplemented = app.injector.instanceOf[notImplemented]
  def journeyMap = app.injector.instanceOf[JourneyMap]

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new JourneyController(journeyMap, FakeDataCacheConnector,
      dataRetrievalAction, new DataRequiredActionImpl(ec), journeyView, MessageControllerComponentsHelpers.stubMessageControllerComponents, messagesApi)

  def viewAsString(form: Form[String] = form) =
    journeyView(form, pageKey, new UserAnswers(emptyCacheMap))(fakeRequest, messages, HousingBenefitAllowancesRouter).toString

  "JourneyController" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(pageKey)(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val validData = Map(HousingBenefitAllowancesRouter.key -> JsString(HousingBenefitAllowancesRouter.options.head))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad(pageKey)(fakeRequest)

      contentAsString(result) mustBe viewAsString(form.fill(HousingBenefitAllowancesRouter.options.head))
    }

    "redirect to start page EnquiryCategory when valid data is submitted, but no data retrieved form cache" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody((HousingBenefitAllowancesRouter.fieldId, HousingBenefitAllowancesRouter.options.head))

      val result = controller().onSubmit(pageKey)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.EnquiryCategoryController.onPageLoad(NormalMode).url)
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody((HousingBenefitAllowancesRouter.fieldId, "invalid value"))
      val boundForm = form.bind(Map(HousingBenefitAllowancesRouter.fieldId -> "invalid value"))

      val result = controller().onSubmit(pageKey)(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "return error page if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad(pageKey)(fakeRequest)
      status(result) mustBe SEE_OTHER
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad(pageKey)(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad.url)
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody((HousingBenefitAllowancesRouter.fieldId, HousingBenefitAllowancesRouter.options.head))
      val result = controller(dontGetAnyData).onSubmit(pageKey)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad.url)
    }

    "handle notImplemented page" in {
      object NotImplementedPage extends NotImplemented("some-not-implemented-key") {
        override def previousPage: UserAnswers => Call = _ => startPage
      }
      implicit val request: Request[_] = fakeRequest
      implicit val messages: Messages = MessagesImpl(Lang("en"), new DefaultMessagesApi)
      implicit val page: Page[String] = NotImplementedPage

      notImplemented(pageKey, "/back/url").toString()
    }

  }
}

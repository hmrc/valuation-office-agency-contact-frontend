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
import play.api.mvc.Call
import play.api.test.Helpers.*
import uk.gov.hmrc.vo.contact.frontend.FakeNavigator
import uk.gov.hmrc.vo.contact.frontend.connectors.FakeDataCacheConnector
import uk.gov.hmrc.vo.contact.frontend.controllers.actions.{DataRequiredActionImpl, DataRetrievalAction, FakeDataRetrievalAction}
import uk.gov.hmrc.vo.contact.frontend.forms.AnythingElseForm.form
import uk.gov.hmrc.vo.contact.frontend.identifiers.{AnythingElseId, EnquiryCategoryId}
import uk.gov.hmrc.vo.contact.frontend.models.{CacheMap, NormalMode}
import uk.gov.hmrc.vo.contact.frontend.utils.{MessageControllerComponentsHelpers, UserAnswers}
import uk.gov.hmrc.vo.contact.frontend.views.html.error.internal_server_error
import uk.gov.hmrc.vo.contact.frontend.views.html.{anythingElseTellUs, error}

class AnythingElseTellUsControllerSpec extends ControllerSpecBase:

  val mockUserAnswers: UserAnswers                     = mock[UserAnswers]
  def anythingElse: anythingElseTellUs                 = inject[anythingElseTellUs]
  def internalServerError: error.internal_server_error = inject[internal_server_error]

  def onwardRoute: Call = routes.CheckYourAnswersController.onPageLoad()

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    AnythingElseTellUsController(
      messagesApi,
      FakeDataCacheConnector,
      FakeNavigator(desiredRoute = onwardRoute),
      dataRetrievalAction,
      DataRequiredActionImpl(ec),
      anythingElse,
      MessageControllerComponentsHelpers.stubMessageControllerComponents
    )

  def viewAsString(form: Form[String] = form): String =
    anythingElse(form)(using getRequest, messages).toString

  "AnythingElseTellUsMore Controller" should {

    "return OK and the correct view for a GET" in {
      val validData = Map(EnquiryCategoryId.toString -> JsString("council_tax"))

      val getRelevantData = FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad(getRequest)

      status(result)          shouldBe OK
      contentAsString(result) shouldBe viewAsString(form)
    }

    "populate the view correctly on a GET when the anything else has previously been filled" in {
      val anythingElseString = "Anything else"

      val validData = Map(EnquiryCategoryId.toString -> JsString("council_tax"), AnythingElseId.toString -> JsString(anythingElseString))

      val getRelevantData = FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad(getRequest)

      status(result)          shouldBe OK
      contentAsString(result) shouldBe viewAsString(form.fill(anythingElseString))
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = getRequest.withMethod("POST").withFormUrlEncodedBody(("message", "value 1"))

      val result = controller().onSubmit(NormalMode)(postRequest)

      status(result)           shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(onwardRoute.url)
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val postRequest = getRequest.withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm   = form.bind(Map("value" -> "invalid value"))

      val result = controller().onSubmit(NormalMode)(postRequest)

      status(result)          shouldBe BAD_REQUEST
      contentAsString(result) shouldBe viewAsString(boundForm)
    }
  }

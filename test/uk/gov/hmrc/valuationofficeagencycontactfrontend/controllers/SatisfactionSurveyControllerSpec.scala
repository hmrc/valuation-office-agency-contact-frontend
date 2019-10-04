/*
 * Copyright 2019 HM Revenue & Customs
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

import org.scalatest.mockito.MockitoSugar
import play.api.{Configuration, Environment}
import play.api.data.Form
import play.api.test.Helpers._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.FakeNavigator
import uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors.AuditingService
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions.{DataRequiredActionImpl, DataRetrievalAction}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.{SatisfactionSurvey, SatisfactionSurveyForm}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.NormalMode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.{AuditServiceConnector, UserAnswers}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.satisfactionSurveyThankYou

import scala.concurrent.ExecutionContext

class SatisfactionSurveyControllerSpec extends ControllerSpecBase with MockitoSugar {

  def onwardRoute = routes.EnquiryCategoryController.onPageLoad(NormalMode)
  val mockUserAnswers = mock[UserAnswers]
  val configuration = injector.instanceOf[Configuration]
  val environment = injector.instanceOf[Environment]
  implicit def ec: ExecutionContext = injector.instanceOf[ExecutionContext]

  def auditingService = new AuditingService(new AuditServiceConnector(configuration, environment))

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new SatisfactionSurveyController(frontendAppConfig, messagesApi, new FakeNavigator(desiredRoute = onwardRoute),
      dataRetrievalAction, new DataRequiredActionImpl, auditingService)

  def viewAsString(form: Form[SatisfactionSurvey] = SatisfactionSurveyForm()) =
    satisfactionSurveyThankYou(frontendAppConfig)(fakeRequest, messages).toString

  "SatisfactionSurvey Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().surveyThankyou()(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    // test submitted form


  }
}

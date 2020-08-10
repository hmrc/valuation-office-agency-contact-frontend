/*
 * Copyright 2020 HM Revenue & Customs
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
import play.api.libs.json.{JsString, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.valuationofficeagencycontactfrontend.FakeNavigator
import uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors.AuditingService
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions.{DataRequiredActionImpl, DataRetrievalAction, FakeDataRetrievalAction}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.{SatisfactionSurvey, SatisfactionSurveyForm}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers.{BusinessRatesSubcategoryId, ContactDetailsId, CouncilTaxSubcategoryId, EnquiryCategoryId, PropertyAddressId, TellUsMoreId}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.{ContactDetails, NormalMode, PropertyAddress, TellUsMore}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.{MessageControllerComponentsHelpers, UserAnswers}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.error.{internalServerError => internal_Server_Error}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{confirmation => Confirmation}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{satisfactionSurveyThankYou => satisfaction_Survey_Thank_You}

import scala.concurrent.ExecutionContext

class SatisfactionSurveyControllerSpec extends ControllerSpecBase with MockitoSugar {

  def onwardRoute = routes.EnquiryCategoryController.onPageLoad(NormalMode)
  val mockUserAnswers = mock[UserAnswers]
  val configuration = injector.instanceOf[Configuration]
  val environment = injector.instanceOf[Environment]

  def auditingService = injector.instanceOf[AuditingService]

  def confirmation = app.injector.instanceOf[Confirmation]
  def satisfactionSurveyThankYou = app.injector.instanceOf[satisfaction_Survey_Thank_You]
  def internalServerError = app.injector.instanceOf[internal_Server_Error]

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new SatisfactionSurveyController(frontendAppConfig, messagesApi, new FakeNavigator(desiredRoute = onwardRoute),
      dataRetrievalAction, new DataRequiredActionImpl(ec), auditingService, confirmation, satisfactionSurveyThankYou,
      MessageControllerComponentsHelpers.stubMessageControllerComponents)

  def viewAsString(form: Form[SatisfactionSurvey] = SatisfactionSurveyForm()) =
    satisfactionSurveyThankYou(frontendAppConfig)(fakeRequest, messages).toString

  "SatisfactionSurvey Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().surveyThankyou()(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "feedback submission must be successful for formCompleteFeedback" in {
      val cd = ContactDetails("a", "b", "c", "e")
      val ec = "council_tax"
      val propertyAddress = PropertyAddress("a", Some("b"), "c", Some("d"), "f")
      val councilTaxSubcategory = "council_tax_poor_repair"
      val tellUs = TellUsMore("Hello")

      val validData = Map(EnquiryCategoryId.toString -> JsString(ec), CouncilTaxSubcategoryId.toString -> JsString(councilTaxSubcategory),
        ContactDetailsId.toString -> Json.toJson(cd), PropertyAddressId.toString -> Json.toJson(propertyAddress), TellUsMoreId.toString -> Json.toJson(tellUs))

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val request = FakeRequest().withFormUrlEncodedBody("satisfaction" -> "verySatisfied", "details" -> "value 1")

      val result = controller(getRelevantData).formCompleteFeedback()(request)

      status(result) mustBe SEE_OTHER
    }

    "feedback submission must fail form data is invalid" in {

      val cd = ContactDetails("a", "b", "c", "e")
      val ec = "other"
      val propertyAddress = PropertyAddress("a", Some("b"), "c", Some("d"), "f")
      val councilTaxSubcategory = "council_tax_poor_repair"
      val tellUs = TellUsMore("Hello")

      val validData = Map(EnquiryCategoryId.toString -> JsString(ec), CouncilTaxSubcategoryId.toString -> JsString(councilTaxSubcategory),
        ContactDetailsId.toString -> Json.toJson(cd), PropertyAddressId.toString -> Json.toJson(propertyAddress), TellUsMoreId.toString -> Json.toJson(tellUs))

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val request = FakeRequest().withFormUrlEncodedBody("satisfaction" -> "verySatisfied", "details" -> "value 1")

      intercept[Exception] {
        val result = controller(getRelevantData).formCompleteFeedback()(request)
        status(result) mustBe INTERNAL_SERVER_ERROR
        contentAsString(result) mustBe internalServerError(frontendAppConfig)(fakeRequest, messages).toString
      }
    }

    "feedback submission (council tax) must show form errors if survey incomplete" in {
      val cd = ContactDetails("a", "b", "c", "e")
      val ec = "council_tax"
      val propertyAddress = PropertyAddress("a", Some("b"), "c", Some("d"), "f")
      val councilTaxSubcategory = "council_tax_poor_repair"
      val tellUs = TellUsMore("Hello")

      val validData = Map(EnquiryCategoryId.toString -> JsString(ec), CouncilTaxSubcategoryId.toString -> JsString(councilTaxSubcategory),
        ContactDetailsId.toString -> Json.toJson(cd), PropertyAddressId.toString -> Json.toJson(propertyAddress), TellUsMoreId.toString -> Json.toJson(tellUs))

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val request = FakeRequest().withFormUrlEncodedBody("details" -> "value 1")

      val result = controller(getRelevantData).formCompleteFeedback()(request)

      status(result) mustBe OK
      contentAsString(result) must include(
        "Overall, how would you describe your experience with the form? - You must choose one")
    }

    "feedback submission (business rates) must show form errors if survey incomplete" in {
      val cd = ContactDetails("a", "b", "c", "e")
      val ec = "business_rates"
      val propertyAddress = PropertyAddress("a", Some("b"), "c", Some("d"), "f")
      val businessSubcategory = "business_rates_other"
      val tellUs = TellUsMore("Hello")

      val validData = Map(EnquiryCategoryId.toString -> JsString(ec), BusinessRatesSubcategoryId.toString -> JsString(businessSubcategory),
        ContactDetailsId.toString -> Json.toJson(cd), PropertyAddressId.toString -> Json.toJson(propertyAddress), TellUsMoreId.toString -> Json.toJson(tellUs))

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val request = FakeRequest().withFormUrlEncodedBody("details" -> "value 1")

      val result = controller(getRelevantData).formCompleteFeedback()(request)

      status(result) mustBe OK
      contentAsString(result) must include(
        "Overall, how would you describe your experience with the form? - You must choose one")
    }

  }
}

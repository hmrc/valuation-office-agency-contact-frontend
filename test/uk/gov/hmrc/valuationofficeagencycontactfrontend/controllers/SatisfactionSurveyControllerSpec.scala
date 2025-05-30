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

import org.scalatestplus.mockito.MockitoSugar
import play.api.{Configuration, Environment}
import play.api.data.Form
import play.api.libs.json.{JsString, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors.AuditingService
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions.{DataRequiredActionImpl, DataRetrievalAction, FakeDataRetrievalAction}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.{SatisfactionSurvey, SatisfactionSurveyForm}
import uk.gov.hmrc.valuationofficeagencycontactfrontend
  .identifiers.{AnswerSectionId, BusinessRatesSubcategoryId, ContactDetailsId, CouncilTaxSubcategoryId, EnquiryCategoryId, PropertyAddressId, TellUsMoreId}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.{CacheMap, ContactDetails, NormalMode, PropertyAddress, TellUsMore}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.{MessageControllerComponentsHelpers, UserAnswers}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.viewmodels.{AnswerRow, AnswerSection}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.error.internal_server_error
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{confirmation => Confirmation}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{satisfactionSurveyThankYou => satisfaction_Survey_Thank_You}
import play.api.mvc.Call
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.error

class SatisfactionSurveyControllerSpec extends ControllerSpecBase with MockitoSugar {

  def onwardRoute: Call = routes.EnquiryCategoryController.onPageLoad(NormalMode)

  val mockUserAnswers: UserAnswers = mock[UserAnswers]
  val configuration: Configuration = injector.instanceOf[Configuration]
  val environment: Environment     = injector.instanceOf[Environment]

  val answerSectionNew: AnswerSection = AnswerSection(
    None,
    List(
      AnswerRow("enquiryCategory.checkYourAnswersLabel", "enquiryCategory.council_tax", true, ""),
      AnswerRow("contactDetails.heading", "Test<br>test123@test.com<br>077777777777", false, ""),
      AnswerRow("propertyAddress.heading", "123 test<br>london<br>bn12 2kj", false, ""),
      AnswerRow("tellUsMore.checkYourAnswersLabel", "some message", false, "")
    )
  )

  def auditingService: AuditingService = injector.instanceOf[AuditingService]

  def confirmation: html.confirmation = app.injector.instanceOf[Confirmation]

  def satisfactionSurveyThankYou: html.satisfactionSurveyThankYou = app.injector.instanceOf[satisfaction_Survey_Thank_You]

  def internalServerError: error.internal_server_error = app.injector.instanceOf[internal_server_error]

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new SatisfactionSurveyController(
      frontendAppConfig,
      messagesApi,
      dataRetrievalAction,
      new DataRequiredActionImpl(ec),
      auditingService,
      confirmation,
      satisfactionSurveyThankYou,
      MessageControllerComponentsHelpers.stubMessageControllerComponents
    )

  def viewAsString(form: Form[SatisfactionSurvey] = SatisfactionSurveyForm()): String =
    satisfactionSurveyThankYou(frontendAppConfig)(using fakeRequest, messages).toString

  "SatisfactionSurvey Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().surveyThankyou()(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "feedback submission must be successful for formCompleteFeedback" in {
      val cd                    = ContactDetails("a", "c", "e")
      val ec                    = "council_tax"
      val propertyAddress       = PropertyAddress("a", Some("b"), "c", Some("d"), "f")
      val councilTaxSubcategory = "council_tax_property_demolished"
      val tellUs                = TellUsMore("Hello")

      val validData = Map(
        EnquiryCategoryId.toString       -> JsString(ec),
        CouncilTaxSubcategoryId.toString -> JsString(councilTaxSubcategory),
        AnswerSectionId.toString         -> Json.toJson(answerSectionNew),
        ContactDetailsId.toString        -> Json.toJson(cd),
        PropertyAddressId.toString       -> Json.toJson(propertyAddress),
        TellUsMoreId.toString            -> Json.toJson(tellUs)
      )

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val request = FakeRequest("POST", "").withFormUrlEncodedBody("satisfaction" -> "verySatisfied", "details" -> "value 1")

      val result = controller(getRelevantData).formCompleteFeedback()(request)

      status(result) mustBe SEE_OTHER
    }

    "feedback submission must fail form data is invalid" in {

      val cd                    = ContactDetails("a", "c", "e")
      val ec                    = "other"
      val propertyAddress       = PropertyAddress("a", Some("b"), "c", Some("d"), "f")
      val councilTaxSubcategory = "council_tax_property_demolished"
      val tellUs                = TellUsMore("Hello")

      val validData = Map(
        EnquiryCategoryId.toString       -> JsString(ec),
        CouncilTaxSubcategoryId.toString -> JsString(councilTaxSubcategory),
        ContactDetailsId.toString        -> Json.toJson(cd),
        PropertyAddressId.toString       -> Json.toJson(propertyAddress),
        TellUsMoreId.toString            -> Json.toJson(tellUs),
        AnswerSectionId.toString         -> Json.toJson(answerSectionNew)
      )

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val request = FakeRequest().withFormUrlEncodedBody("satisfaction" -> "verySatisfied", "details" -> "value 1")

      intercept[Exception] {
        val result = controller(getRelevantData).formCompleteFeedback()(request)
        status(result) mustBe INTERNAL_SERVER_ERROR
        contentAsString(result) mustBe internalServerError(frontendAppConfig)(using fakeRequest, messages).toString
      }
    }

    "feedback submission (council tax) must show form errors if survey incomplete" in {
      val cd                    = ContactDetails("a", "c", "e")
      val ec                    = "council_tax"
      val propertyAddress       = PropertyAddress("a", Some("b"), "c", Some("d"), "f")
      val councilTaxSubcategory = "council_tax_property_demolished"
      val tellUs                = TellUsMore("Hello")

      val validData = Map(
        EnquiryCategoryId.toString       -> JsString(ec),
        CouncilTaxSubcategoryId.toString -> JsString(councilTaxSubcategory),
        ContactDetailsId.toString        -> Json.toJson(cd),
        PropertyAddressId.toString       -> Json.toJson(propertyAddress),
        TellUsMoreId.toString            -> Json.toJson(tellUs),
        AnswerSectionId.toString         -> Json.toJson(answerSectionNew)
      )

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val request = FakeRequest().withFormUrlEncodedBody("details" -> "value 1")

      val result = controller(getRelevantData).formCompleteFeedback()(request)

      status(result) mustBe OK
      contentAsString(result) must include(
        "Select how you would describe your experience"
      )
    }

    "feedback submission (business rates) must show form errors if survey incomplete" in {
      val cd                  = ContactDetails("a", "c", "e")
      val ec                  = "business_rates"
      val propertyAddress     = PropertyAddress("a", Some("b"), "c", Some("d"), "f")
      val businessSubcategory = "business_rates_other"
      val tellUs              = TellUsMore("Hello")

      val validData = Map(
        EnquiryCategoryId.toString          -> JsString(ec),
        BusinessRatesSubcategoryId.toString -> JsString(businessSubcategory),
        ContactDetailsId.toString           -> Json.toJson(cd),
        PropertyAddressId.toString          -> Json.toJson(propertyAddress),
        TellUsMoreId.toString               -> Json.toJson(tellUs),
        AnswerSectionId.toString            -> Json.toJson(answerSectionNew)
      )

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val request = FakeRequest().withFormUrlEncodedBody("details" -> "value 1")

      val result = controller(getRelevantData).formCompleteFeedback()(request)

      status(result) mustBe OK
      contentAsString(result) must include(
        "Select how you would describe your experience"
      )
    }

  }
}

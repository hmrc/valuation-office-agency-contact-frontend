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

import play.api.Environment
import play.api.libs.json.{JsString, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.vo.contact.frontend.connectors.AuditingService
import uk.gov.hmrc.vo.contact.frontend.controllers.actions.{DataRequiredActionImpl, DataRetrievalAction, FakeDataRetrievalAction}
import uk.gov.hmrc.vo.contact.frontend.identifiers.*
import uk.gov.hmrc.vo.contact.frontend.models.{CacheMap, ContactDetails, NormalMode, PropertyAddress, TellUsMore}
import uk.gov.hmrc.vo.contact.frontend.utils.{MessageControllerComponentsHelpers, UserAnswers}
import uk.gov.hmrc.vo.contact.frontend.viewmodels.{AnswerRow, AnswerSection}
import uk.gov.hmrc.vo.contact.frontend.views.html.error.internal_server_error
import uk.gov.hmrc.vo.contact.frontend.views.html.{confirmation => Confirmation}
import uk.gov.hmrc.vo.contact.frontend.views.html.{satisfactionSurveyThankYou => satisfaction_Survey_Thank_You}
import play.api.mvc.Call
import uk.gov.hmrc.vo.contact.frontend.views.html
import uk.gov.hmrc.vo.contact.frontend.views.html.error

class SatisfactionSurveyControllerSpec extends ControllerSpecBase:

  def onwardRoute: Call = routes.EnquiryCategoryController.onPageLoad(NormalMode)

  val mockUserAnswers: UserAnswers = mock[UserAnswers]
  val environment: Environment     = inject[Environment]

  val answerSectionNew: AnswerSection = AnswerSection(
    None,
    List(
      AnswerRow("enquiryCategory.checkYourAnswersLabel", "enquiryCategory.council_tax", true, ""),
      AnswerRow("contactDetails.title", "Test<br>test123@test.com<br>077777777777", false, ""),
      AnswerRow("propertyAddress.title", "123 test<br>london<br>bn12 2kj", false, ""),
      AnswerRow("tellUsMore.checkYourAnswersLabel", "some message", false, "")
    )
  )

  def auditingService: AuditingService = inject[AuditingService]

  def confirmation: html.confirmation = inject[Confirmation]

  def satisfactionSurveyThankYou: html.satisfactionSurveyThankYou = inject[satisfaction_Survey_Thank_You]

  def internalServerError: error.internal_server_error = inject[internal_server_error]

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    SatisfactionSurveyController(
      messagesApi,
      dataRetrievalAction,
      DataRequiredActionImpl(ec),
      auditingService,
      confirmation,
      satisfactionSurveyThankYou,
      MessageControllerComponentsHelpers.stubMessageControllerComponents
    )

  def viewAsString: String =
    satisfactionSurveyThankYou()(using getRequest, messages).toString

  "SatisfactionSurvey Controller" should {

    "return OK and the correct view for a GET" in {
      val result = controller().surveyThankyou()(getRequest)

      status(result)          shouldBe OK
      contentAsString(result) shouldBe viewAsString
    }

    "feedback submission should be successful for formCompleteFeedback" in {
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

      val getRelevantData = FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val request = FakeRequest("POST", "").withFormUrlEncodedBody("satisfaction" -> "verySatisfied", "details" -> "value 1")

      val result = controller(getRelevantData).formCompleteFeedback()(request)

      status(result) shouldBe SEE_OTHER
    }

    "feedback submission should fail form data is invalid" in {

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

      val getRelevantData = FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val request = FakeRequest().withFormUrlEncodedBody("satisfaction" -> "verySatisfied", "details" -> "value 1")

      intercept[Exception] {
        val result = controller(getRelevantData).formCompleteFeedback()(request)
        status(result)          shouldBe INTERNAL_SERVER_ERROR
        contentAsString(result) shouldBe internalServerError()(using getRequest, messages).toString
      }
    }

    "feedback submission (council tax) should show form errors if survey incomplete" in {
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

      val getRelevantData = FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val request = FakeRequest().withFormUrlEncodedBody("details" -> "value 1")

      val result = controller(getRelevantData).formCompleteFeedback()(request)

      status(result)        shouldBe OK
      contentAsString(result) should include(
        "Select how you would describe your experience"
      )
    }

    "feedback submission (business rates) should show form errors if survey incomplete" in {
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

      val getRelevantData = FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val request = FakeRequest().withFormUrlEncodedBody("details" -> "value 1")

      val result = controller(getRelevantData).formCompleteFeedback()(request)

      status(result)        shouldBe OK
      contentAsString(result) should include(
        "Select how you would describe your experience"
      )
    }

  }

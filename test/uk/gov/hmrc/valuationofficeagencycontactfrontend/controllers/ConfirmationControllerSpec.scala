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

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.i18n.MessagesApi
import play.api.libs.json.{JsString, Json}
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.valuationofficeagencycontactfrontend.FakeNavigator
import uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors.{EmailConnector, LightweightContactEventsConnector}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.ConfirmationController.{enquiryKey, whatHappensNextMessages}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions.{DataRequiredActionImpl, DataRetrievalAction, FakeDataRetrievalAction}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.SatisfactionSurveyForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.{MessageControllerComponentsHelpers, UserAnswers}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.viewmodels.{AnswerRow, AnswerSection}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.error.internal_server_error
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{confirmation => Confirmation}

import scala.concurrent.Future
import scala.util.Success
import play.api.mvc.Call
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.error

class ConfirmationControllerSpec extends ControllerSpecBase with MockitoSugar {

  val mockUserAnswers: UserAnswers                     = mock[UserAnswers]
  val mockConnector: LightweightContactEventsConnector = mock[LightweightContactEventsConnector]
  val emailConnector: EmailConnector                   = inject[EmailConnector]
  val whatHappensExisting: Seq[String]                 = Seq("confirmation.existing.p1", "confirmation.existing.p2")
  val whatHappensNew: Seq[String]                      = Seq("confirmation.new.p1")

  val answerSectionExisting: AnswerSection = AnswerSection(
    None,
    List(
      AnswerRow("existingEnquiryCategory.heading", "existingEnquiryCategory.council_tax", true, ""),
      AnswerRow("refNumber.value", "VOA1238983", true, ""),
      AnswerRow("contactDetails.heading", "Test<br>test123@test.com<br>077777777777", false, ""),
      AnswerRow("propertyAddress.heading", "123 test<br>someCity<br>bn12 2kj", false, ""),
      AnswerRow("anythingElse.checkYourAnswersLabel", "some message", false, "")
    )
  )

  val answerSectionNew: AnswerSection = AnswerSection(
    None,
    List(
      AnswerRow("enquiryCategory.checkYourAnswersLabel", "enquiryCategory.council_tax", true, ""),
      AnswerRow("councilTaxSubcategory.checkYourAnswersLabel", "councilTaxSubcategory.council_tax_changes", true, ""),
      AnswerRow("contactDetails.heading", "Test<br>test123@test.com<br>077777777777", false, ""),
      AnswerRow("propertyAddress.heading", "123 test<br>london<br>bn12 2kj", false, ""),
      AnswerRow("tellUsMore.checkYourAnswersLabel", "some message", false, "")
    )
  )

  def onwardRoute: Call = routes.EnquiryCategoryController.onPageLoad(NormalMode)

  def confirmation: html.confirmation                  = app.injector.instanceOf[Confirmation]
  def internalServerError: error.internal_server_error = app.injector.instanceOf[internal_server_error]

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new ConfirmationController(
      frontendAppConfig,
      messagesApi,
      mockConnector,
      emailConnector,
      new FakeNavigator(desiredRoute = onwardRoute),
      dataRetrievalAction,
      new DataRequiredActionImpl(ec),
      confirmation,
      MessageControllerComponentsHelpers.stubMessageControllerComponents
    )

  val mockConnectorF: LightweightContactEventsConnector = mock[LightweightContactEventsConnector]

  def controllerF(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new ConfirmationController(
      frontendAppConfig,
      messagesApi,
      mockConnectorF,
      emailConnector,
      new FakeNavigator(desiredRoute = onwardRoute),
      dataRetrievalAction,
      new DataRequiredActionImpl(ec),
      confirmation,
      MessageControllerComponentsHelpers.stubMessageControllerComponents
    )

  "Confirmation Controller" must {

    "return 200 and the correct view for a GET" in {

      val contactDetails        = ContactDetails("a", "c", "e")
      val ec                    = "council_tax"
      val propertyAddress       = PropertyAddress("a", Some("b"), "c", Some("d"), "f")
      val councilTaxSubcategory = "council_tax_property_demolished"
      val tellUs                = TellUsMore("Hello")

      val contact = Contact(contactDetails, propertyAddress, ec, councilTaxSubcategory, tellUs.message)

      val validData = Map(
        EnquiryCategoryId.toString       -> JsString(ec),
        CouncilTaxSubcategoryId.toString -> JsString(councilTaxSubcategory),
        ContactDetailsId.toString        -> Json.toJson(contactDetails),
        PropertyAddressId.toString       -> Json.toJson(propertyAddress),
        TellUsMoreId.toString            -> Json.toJson(tellUs),
        AnswerSectionId.toString         -> Json.toJson(answerSectionNew)
      )

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad()(fakeRequest)

      status(result) mustBe OK

      contentAsString(result) mustBe confirmation(frontendAppConfig, contact, answerSectionNew, whatHappensNew, SatisfactionSurveyForm.apply())(
        using fakeRequest,
        messages
      ).toString
    }

    "return 200 and the correct view for a existing enquiry GET" in {

      val contactDetails        = ContactDetails("a", "c", "e")
      val ec                    = "council_tax"
      val propertyAddress       = PropertyAddress("a", Some("b"), "c", Some("d"), "f")
      val councilTaxSubcategory = "property_demolished"
      val message               = "Hello"

      val contact = Contact(contactDetails, propertyAddress, ec, councilTaxSubcategory, message)

      val validData = Map(
        ExistingEnquiryCategoryId.toString -> JsString(ec),
        CouncilTaxSubcategoryId.toString   -> JsString(councilTaxSubcategory),
        ContactDetailsId.toString          -> Json.toJson(contactDetails),
        PropertyAddressId.toString         -> Json.toJson(propertyAddress),
        AnythingElseId.toString            -> Json.toJson(message),
        AnswerSectionId.toString           -> Json.toJson(answerSectionExisting)
      )

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad()(fakeRequest)

      status(result) mustBe OK

      contentAsString(result) mustBe confirmation(frontendAppConfig, contact, answerSectionExisting, whatHappensExisting, SatisfactionSurveyForm.apply())(
        using fakeRequest,
        messages
      ).toString
    }

    "return 200 and the correct view for a GET when addressLine2 and county are None" in {
      val contactDetails        = ContactDetails("a", "c", "e")
      val ec                    = "council_tax"
      val propertyAddress       = PropertyAddress("a", None, "c", None, "f")
      val councilTaxSubcategory = "council_tax_property_demolished"
      val tellUs                = TellUsMore("Hello")

      val contact = Contact(contactDetails, propertyAddress, ec, councilTaxSubcategory, tellUs.message)

      val validData = Map(
        EnquiryCategoryId.toString       -> JsString(ec),
        CouncilTaxSubcategoryId.toString -> JsString(councilTaxSubcategory),
        ContactDetailsId.toString        -> Json.toJson(contactDetails),
        PropertyAddressId.toString       -> Json.toJson(propertyAddress),
        TellUsMoreId.toString            -> Json.toJson(tellUs),
        AnswerSectionId.toString         -> Json.toJson(answerSectionNew)
      )

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad()(fakeRequest)

      status(result) mustBe OK

      contentAsString(result) mustBe confirmation(frontendAppConfig, contact, answerSectionNew, whatHappensNew, SatisfactionSurveyForm.apply())(
        using fakeRequest,
        messages
      ).toString
    }

    "redirect to Session Expired for a GET if not existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad()(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad.url)
    }

    "The enquiry key function produces a string with a businessRatesSubcategory string key when the enquiry category is business_rates" +
      " and the business_rates_other has been selected" in {
        when(mockUserAnswers.enquiryCategory) `thenReturn` Some("business_rates")
        when(mockUserAnswers.contactDetails) `thenReturn` Some(ContactDetails("a", "c", "e"))
        when(mockUserAnswers.propertyAddress) `thenReturn` Some(PropertyAddress("a", Some("a"), "a", Some("a"), "a"))
        when(mockUserAnswers.businessRatesSubcategory) `thenReturn` Some("business_rates_other")

        val result                   = enquiryKey(mockUserAnswers)
        val isBusinessRatesSelection = result.toOption.get.startsWith("businessRatesSubcategory")
        isBusinessRatesSelection mustBe true
      }

    "The enquiry key function produces a string with a councilTaxSubcategory key when the enquiry category is council_tax" +
      " and the council_tax_band has been selected" in {
        when(mockUserAnswers.enquiryCategory) `thenReturn` Some("council_tax")
        when(mockUserAnswers.contactDetails) `thenReturn` Some(ContactDetails("a", "c", "e"))
        when(mockUserAnswers.propertyAddress) `thenReturn` Some(PropertyAddress("a", Some("a"), "a", Some("a"), "a"))
        when(mockUserAnswers.councilTaxSubcategory) `thenReturn` Some("council_tax_property_demolished")

        val result                = enquiryKey(mockUserAnswers)
        val isCouncilTaxSelection = result.toOption.get.startsWith("councilTaxSubcategory")
        isCouncilTaxSelection mustBe true
      }

    "enquiryKey returns a string with a housingBenefitSubcategory key prefix when the enquiry category is housing_benefit" in {
      when(mockUserAnswers.enquiryCategory) `thenReturn` Some("housing_benefit")

      enquiryKey(mockUserAnswers) mustBe Right("housingBenefitSubcategory")
    }

    "The enquiry key function produces a Left(Unknown enquiry category in enquiry key) when the enquiry category has not been selected" in {
      when(mockUserAnswers.enquiryCategory) `thenReturn` None
      when(mockUserAnswers.contactDetails) `thenReturn` Some(ContactDetails("a", "c", "e"))
      when(mockUserAnswers.propertyAddress) `thenReturn` Some(PropertyAddress("a", Some("a"), "a", Some("a"), "a"))
      when(mockUserAnswers.businessRatesSubcategory) `thenReturn` Some("business_rates_other")

      val result = enquiryKey(mockUserAnswers)
      result mustBe Left("Unknown enquiry category in enquiry key")
    }

    "populate the satisfaction survey view correctly on a GET" in {}

    "return 500 and the error view for a GET with unknown or wrong enquiry type" in {
      val contactDetails        = ContactDetails("a", "c", "e")
      val ec                    = "other"
      val propertyAddress       = PropertyAddress("a", Some("b"), "c", Some("d"), "f")
      val councilTaxSubcategory = "council_tax_property_demolished"
      val tellUs                = TellUsMore("Hello")

      val validData = Map(
        EnquiryCategoryId.toString       -> JsString(ec),
        CouncilTaxSubcategoryId.toString -> JsString(councilTaxSubcategory),
        ContactDetailsId.toString        -> Json.toJson(contactDetails),
        PropertyAddressId.toString       -> Json.toJson(propertyAddress),
        TellUsMoreId.toString            -> Json.toJson(tellUs)
      )

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      intercept[Exception] {
        val result = controller(getRelevantData).onPageLoad()(fakeRequest)

        status(result) mustBe INTERNAL_SERVER_ERROR

        contentAsString(result) mustBe internalServerError(frontendAppConfig)(using fakeRequest, messages).toString
      }
    }

    "return 500 and the error view for a existing enquiry GET with unknown or wrong enquiry type" in {
      val contactDetails        = ContactDetails("a", "c", "e")
      val ec                    = "other"
      val propertyAddress       = PropertyAddress("a", Some("b"), "c", Some("d"), "f")
      val councilTaxSubcategory = "property_demolished"
      val message               = "Hello"

      val validData = Map(
        EnquiryCategoryId.toString       -> JsString(ec),
        CouncilTaxSubcategoryId.toString -> JsString(councilTaxSubcategory),
        ContactDetailsId.toString        -> Json.toJson(contactDetails),
        PropertyAddressId.toString       -> Json.toJson(propertyAddress),
        AnythingElseId.toString          -> Json.toJson(message)
      )

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      intercept[Exception] {
        val result = controller(getRelevantData).onPageLoad()(fakeRequest)

        status(result) mustBe INTERNAL_SERVER_ERROR

        contentAsString(result) mustBe internalServerError(frontendAppConfig)(using fakeRequest, messages).toString
      }
    }

    "return 500 and the error view for a GET with no enquiry type" in
      intercept[Exception] {
        val result = controller().onPageLoad()(fakeRequest)
        status(result) mustBe INTERNAL_SERVER_ERROR
        contentAsString(result) mustBe internalServerError(frontendAppConfig)(using fakeRequest, messages).toString
      }

    "return 500 and error view for a GET when the backend service call fails" in
      intercept[Exception] {
        val result = controllerF().onPageLoad()(fakeRequest)
        status(result) mustBe INTERNAL_SERVER_ERROR
        contentAsString(result) mustBe internalServerError(frontendAppConfig)(using fakeRequest, messages).toString
      }

    "return 303 and send email when form complete" in {
      val contactDetails        = ContactDetails("a", "c", "e")
      val ec                    = "council_tax"
      val propertyAddress       = PropertyAddress("a", Some("b"), "c", Some("d"), "f")
      val councilTaxSubcategory = "council_tax_property_demolished"
      val tellUs                = TellUsMore("Hello")

      val validData = Map(
        EnquiryCategoryId.toString       -> JsString(ec),
        CouncilTaxSubcategoryId.toString -> JsString(councilTaxSubcategory),
        ContactDetailsId.toString        -> Json.toJson(contactDetails),
        PropertyAddressId.toString       -> Json.toJson(propertyAddress),
        TellUsMoreId.toString            -> Json.toJson(tellUs)
      )

      when(mockConnector.send(any[Contact], any[MessagesApi], any[UserAnswers])(using any[HeaderCarrier])) `thenReturn` Future.successful(Success(OK))

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoadSendEmail()(fakeRequest)

      status(result) mustBe SEE_OTHER
    }

    "will return correct messages if a new enquiry" in {
      when(mockUserAnswers.enquiryCategory) `thenReturn` Some("council_tax")
      when(mockUserAnswers.existingEnquiryCategory) `thenReturn` None

      val result = whatHappensNextMessages(mockUserAnswers)

      result mustBe whatHappensNew
    }

    "will return correct messages if an existing enquiry" in {
      when(mockUserAnswers.enquiryCategory) `thenReturn` None
      when(mockUserAnswers.existingEnquiryCategory) `thenReturn` Some("council_tax")

      val result = whatHappensNextMessages(mockUserAnswers)

      result mustBe whatHappensExisting
    }

    "will return empty string if new and existing enquiry are both defined in the cache" in {
      when(mockUserAnswers.enquiryCategory) `thenReturn` Some("council_tax")
      when(mockUserAnswers.existingEnquiryCategory) `thenReturn` Some("council_tax")

      val result = whatHappensNextMessages(mockUserAnswers)

      result mustBe Seq.empty[String]
    }
  }
}

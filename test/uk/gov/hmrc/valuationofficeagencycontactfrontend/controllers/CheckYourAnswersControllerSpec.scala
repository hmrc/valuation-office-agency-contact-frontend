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

package uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers

import org.mockito.Mockito.{reset, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar
import play.api.i18n.{Lang, Messages}
import play.api.libs.json.{JsString, Json}
import play.api.test.Helpers._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors.{AuditingService, FakeDataCacheConnector}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions.{DataRequiredActionImpl, DataRetrievalAction, FakeDataRetrievalAction}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.journey.model.TellUsMorePage
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.{CheckYourAnswersHelper, DateUtil, MessageControllerComponentsHelpers, UserAnswers}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.viewmodels.AnswerSection
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.check_your_answers
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.error.internal_server_error

import java.time.LocalDate
import java.util.Locale
import play.api.mvc.Call
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.error

class CheckYourAnswersControllerSpec extends ControllerSpecBase with MockitoSugar with BeforeAndAfterEach {

  val mockUserAnswers: UserAnswers = mock[UserAnswers]

  implicit val messagesEnglish: Messages = messagesApi.preferred(Seq(Lang(Locale.UK)))
  implicit val dateUtil: DateUtil        = injector.instanceOf[DateUtil]

  def checkYourAnswers: check_your_answers             = app.injector.instanceOf[check_your_answers]
  def internalServerError: error.internal_server_error = app.injector.instanceOf[internal_server_error]

  def onwardRoute: Call             = routes.EnquiryCategoryController.onPageLoad(NormalMode)
  def auditService: AuditingService = inject[AuditingService]

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new CheckYourAnswersController(
      frontendAppConfig,
      auditService,
      messagesApi,
      FakeDataCacheConnector,
      dataRetrievalAction,
      new DataRequiredActionImpl(ec),
      checkYourAnswers,
      MessageControllerComponentsHelpers.stubMessageControllerComponents
    )

  "Check Your Answers Controller" must {

    "return 200 for a GET" in {
      val contactDetails        = ContactDetails("a", "c", "e")
      val ec                    = "council_tax"
      val contactReason         = "new_enquiry"
      val propertyAddress       = PropertyAddress("a", Some("b"), "c", Some("d"), "f")
      val councilTaxSubcategory = "council_tax_property_demolished"
      val tellUs                = TellUsMore("Hello")

      val validData = Map(
        ContactReasonId.toString         -> JsString(contactReason),
        EnquiryCategoryId.toString       -> JsString(ec),
        CouncilTaxSubcategoryId.toString -> JsString(councilTaxSubcategory),
        ContactDetailsId.toString        -> Json.toJson(contactDetails),
        PropertyAddressId.toString       -> Json.toJson(propertyAddress),
        TellUsMoreId.toString            -> Json.toJson(tellUs)
      )

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad()(fakeRequest)

      status(result) mustBe OK
    }

    "return 200 for a GET if subcategory is blank" in {
      val contactDetails        = ContactDetails("a", "c", "e")
      val ec                    = "council_tax"
      val contactReason         = "new_enquiry"
      val propertyAddress       = PropertyAddress("a", Some("b"), "c", Some("d"), "f")
      val councilTaxSubcategory = ""
      val tellUs                = TellUsMore("Hello")

      val validData = Map(
        ContactReasonId.toString         -> JsString(contactReason),
        EnquiryCategoryId.toString       -> JsString(ec),
        CouncilTaxSubcategoryId.toString -> JsString(councilTaxSubcategory),
        ContactDetailsId.toString        -> Json.toJson(contactDetails),
        PropertyAddressId.toString       -> Json.toJson(propertyAddress),
        TellUsMoreId.toString            -> Json.toJson(tellUs)
      )

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad()(fakeRequest)

      status(result) mustBe OK
    }

    "return 200 for a GET if subcategory is council_tax_business_uses" in {
      val contactDetails        = ContactDetails("a", "c", "e")
      val ec                    = "council_tax"
      val contactReason         = "new_enquiry"
      val propertyAddress       = PropertyAddress("a", Some("b"), "c", Some("d"), "f")
      val councilTaxSubcategory = "council_tax_business_uses"
      val tellUs                = TellUsMore("Hello")

      val validData = Map(
        ContactReasonId.toString         -> JsString(contactReason),
        EnquiryCategoryId.toString       -> JsString(ec),
        CouncilTaxSubcategoryId.toString -> JsString(councilTaxSubcategory),
        ContactDetailsId.toString        -> Json.toJson(contactDetails),
        PropertyAddressId.toString       -> Json.toJson(propertyAddress),
        TellUsMoreId.toString            -> Json.toJson(tellUs)
      )

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad()(fakeRequest)

      status(result) mustBe OK
    }

    "return 200 for a GET if subcategory is council_tax_area_change" in {
      val contactDetails        = ContactDetails("a", "c", "e")
      val ec                    = "council_tax"
      val contactReason         = "new_enquiry"
      val propertyAddress       = PropertyAddress("a", Some("b"), "c", Some("d"), "f")
      val councilTaxSubcategory = "council_tax_area_change"
      val tellUs                = TellUsMore("Hello")

      val validData = Map(
        ContactReasonId.toString         -> JsString(contactReason),
        EnquiryCategoryId.toString       -> JsString(ec),
        CouncilTaxSubcategoryId.toString -> JsString(councilTaxSubcategory),
        ContactDetailsId.toString        -> Json.toJson(contactDetails),
        PropertyAddressId.toString       -> Json.toJson(propertyAddress),
        TellUsMoreId.toString            -> Json.toJson(tellUs)
      )

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad()(fakeRequest)

      status(result) mustBe OK
    }

    "return 200 for a GET if subcategory is council_tax_other" in {
      val contactDetails        = ContactDetails("a", "c", "e")
      val ec                    = "council_tax"
      val contactReason         = "new_enquiry"
      val propertyAddress       = PropertyAddress("a", Some("b"), "c", Some("d"), "f")
      val councilTaxSubcategory = "council_tax_other"
      val tellUs                = TellUsMore("Hello")

      val validData = Map(
        ContactReasonId.toString         -> JsString(contactReason),
        EnquiryCategoryId.toString       -> JsString(ec),
        CouncilTaxSubcategoryId.toString -> JsString(councilTaxSubcategory),
        ContactDetailsId.toString        -> Json.toJson(contactDetails),
        PropertyAddressId.toString       -> Json.toJson(propertyAddress),
        TellUsMoreId.toString            -> Json.toJson(tellUs)
      )

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad()(fakeRequest)

      status(result) mustBe OK
    }

    "return 200 for a GET if subcategory is council_tax_annexe" in {
      val contactDetails        = ContactDetails("a", "c", "e")
      val ec                    = "council_tax"
      val contactReason         = "new_enquiry"
      val propertyAddress       = PropertyAddress("a", Some("b"), "c", Some("d"), "f")
      val councilTaxSubcategory = "council_tax_annexe"
      val tellUs                = TellUsMore("Hello")

      val validData = Map(
        ContactReasonId.toString         -> JsString(contactReason),
        EnquiryCategoryId.toString       -> JsString(ec),
        CouncilTaxSubcategoryId.toString -> JsString(councilTaxSubcategory),
        ContactDetailsId.toString        -> Json.toJson(contactDetails),
        PropertyAddressId.toString       -> Json.toJson(propertyAddress),
        TellUsMoreId.toString            -> Json.toJson(tellUs)
      )

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad()(fakeRequest)

      status(result) mustBe OK
    }

    "return 200 for a GET if subcategory is council_tax_band_too_high" in {
      val contactDetails        = ContactDetails("a", "c", "e")
      val ec                    = "council_tax"
      val contactReason         = "new_enquiry"
      val propertyAddress       = PropertyAddress("a", Some("b"), "c", Some("d"), "f")
      val councilTaxSubcategory = "council_tax_band_too_high"
      val tellUs                = TellUsMore("Hello")

      val validData = Map(
        ContactReasonId.toString         -> JsString(contactReason),
        EnquiryCategoryId.toString       -> JsString(ec),
        CouncilTaxSubcategoryId.toString -> JsString(councilTaxSubcategory),
        ContactDetailsId.toString        -> Json.toJson(contactDetails),
        PropertyAddressId.toString       -> Json.toJson(propertyAddress),
        TellUsMoreId.toString            -> Json.toJson(tellUs)
      )

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad()(fakeRequest)

      status(result) mustBe OK
    }

    "return 200 for a GET if subcategory is council_tax_bill" in {
      val contactDetails        = ContactDetails("a", "c", "e")
      val ec                    = "council_tax"
      val contactReason         = "new_enquiry"
      val propertyAddress       = PropertyAddress("a", Some("b"), "c", Some("d"), "f")
      val councilTaxSubcategory = "council_tax_bill"
      val tellUs                = TellUsMore("Hello")

      val validData = Map(
        ContactReasonId.toString         -> JsString(contactReason),
        EnquiryCategoryId.toString       -> JsString(ec),
        CouncilTaxSubcategoryId.toString -> JsString(councilTaxSubcategory),
        ContactDetailsId.toString        -> Json.toJson(contactDetails),
        PropertyAddressId.toString       -> Json.toJson(propertyAddress),
        TellUsMoreId.toString            -> Json.toJson(tellUs)
      )

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad()(fakeRequest)

      status(result) mustBe OK
    }

    "return 200 for a GET if subcategory is council_tax_band_for_new" in {
      val contactDetails        = ContactDetails("a", "c", "e")
      val ec                    = "council_tax"
      val contactReason         = "new_enquiry"
      val propertyAddress       = PropertyAddress("a", Some("b"), "c", Some("d"), "f")
      val councilTaxSubcategory = "council_tax_band_for_new"
      val tellUs                = TellUsMore("Hello")

      val validData = Map(
        ContactReasonId.toString         -> JsString(contactReason),
        EnquiryCategoryId.toString       -> JsString(ec),
        CouncilTaxSubcategoryId.toString -> JsString(councilTaxSubcategory),
        ContactDetailsId.toString        -> Json.toJson(contactDetails),
        PropertyAddressId.toString       -> Json.toJson(propertyAddress),
        TellUsMoreId.toString            -> Json.toJson(tellUs)
      )

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad()(fakeRequest)

      status(result) mustBe OK
    }

    "return 200 for a GET if subcategory is council_tax_property_empty" in {
      val contactDetails        = ContactDetails("a", "c", "e")
      val ec                    = "council_tax"
      val contactReason         = "new_enquiry"
      val propertyAddress       = PropertyAddress("a", Some("b"), "c", Some("d"), "f")
      val councilTaxSubcategory = "council_tax_property_empty"
      val tellUs                = TellUsMore("Hello")

      val validData = Map(
        ContactReasonId.toString         -> JsString(contactReason),
        EnquiryCategoryId.toString       -> JsString(ec),
        CouncilTaxSubcategoryId.toString -> JsString(councilTaxSubcategory),
        ContactDetailsId.toString        -> Json.toJson(contactDetails),
        PropertyAddressId.toString       -> Json.toJson(propertyAddress),
        TellUsMoreId.toString            -> Json.toJson(tellUs)
      )

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad()(fakeRequest)

      status(result) mustBe OK
    }

    "return 200 for a GET if subcategory is council_tax_property_poor_repair" in {
      val contactDetails        = ContactDetails("a", "c", "e")
      val ec                    = "council_tax"
      val contactReason         = "new_enquiry"
      val propertyAddress       = PropertyAddress("a", Some("b"), "c", Some("d"), "f")
      val councilTaxSubcategory = "council_tax_property_poor_repair"
      val tellUs                = TellUsMore("Hello")

      val validData = Map(
        ContactReasonId.toString         -> JsString(contactReason),
        EnquiryCategoryId.toString       -> JsString(ec),
        CouncilTaxSubcategoryId.toString -> JsString(councilTaxSubcategory),
        ContactDetailsId.toString        -> Json.toJson(contactDetails),
        PropertyAddressId.toString       -> Json.toJson(propertyAddress),
        TellUsMoreId.toString            -> Json.toJson(tellUs)
      )

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad()(fakeRequest)

      status(result) mustBe OK
    }

    "return 200 for a GET if subcategory is council_tax_property_split_merge" in {
      val contactDetails        = ContactDetails("a", "c", "e")
      val ec                    = "council_tax"
      val contactReason         = "new_enquiry"
      val propertyAddress       = PropertyAddress("a", Some("b"), "c", Some("d"), "f")
      val councilTaxSubcategory = "council_tax_property_split_merge"
      val tellUs                = TellUsMore("Hello")

      val validData = Map(
        ContactReasonId.toString         -> JsString(contactReason),
        EnquiryCategoryId.toString       -> JsString(ec),
        CouncilTaxSubcategoryId.toString -> JsString(councilTaxSubcategory),
        ContactDetailsId.toString        -> Json.toJson(contactDetails),
        PropertyAddressId.toString       -> Json.toJson(propertyAddress),
        TellUsMoreId.toString            -> Json.toJson(tellUs)
      )

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad()(fakeRequest)

      status(result) mustBe OK
    }

    "return 200 for a GET if subcategory is business_rates_from_home" in {
      val contactDetails        = ContactDetails("a", "c", "e")
      val ec                    = "business_rates"
      val contactReason         = "new_enquiry"
      val propertyAddress       = PropertyAddress("a", Some("b"), "c", Some("d"), "f")
      val councilTaxSubcategory = "business_rates_from_home"
      val tellUs                = TellUsMore("Hello")

      val validData = Map(
        ContactReasonId.toString            -> JsString(contactReason),
        EnquiryCategoryId.toString          -> JsString(ec),
        BusinessRatesSubcategoryId.toString -> JsString(councilTaxSubcategory),
        ContactDetailsId.toString           -> Json.toJson(contactDetails),
        PropertyAddressId.toString          -> Json.toJson(propertyAddress),
        TellUsMoreId.toString               -> Json.toJson(tellUs)
      )

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad()(fakeRequest)

      status(result) mustBe OK
    }

    "return 200 for a GET if subcategory is business_rates_bill" in {
      val contactDetails        = ContactDetails("a", "c", "e")
      val ec                    = "business_rates"
      val contactReason         = "new_enquiry"
      val propertyAddress       = PropertyAddress("a", Some("b"), "c", Some("d"), "f")
      val councilTaxSubcategory = "business_rates_bill"
      val tellUs                = TellUsMore("Hello")

      val validData = Map(
        ContactReasonId.toString            -> JsString(contactReason),
        EnquiryCategoryId.toString          -> JsString(ec),
        BusinessRatesSubcategoryId.toString -> JsString(councilTaxSubcategory),
        ContactDetailsId.toString           -> Json.toJson(contactDetails),
        PropertyAddressId.toString          -> Json.toJson(propertyAddress),
        TellUsMoreId.toString               -> Json.toJson(tellUs)
      )

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad()(fakeRequest)

      status(result) mustBe OK
    }

    "return 200 for a GET if subcategory is business_rates_property_empty" in {
      val contactDetails        = ContactDetails("a", "c", "e")
      val ec                    = "business_rates"
      val contactReason         = "new_enquiry"
      val propertyAddress       = PropertyAddress("a", Some("b"), "c", Some("d"), "f")
      val councilTaxSubcategory = "business_rates_property_empty"
      val tellUs                = TellUsMore("Hello")

      val validData = Map(
        ContactReasonId.toString            -> JsString(contactReason),
        EnquiryCategoryId.toString          -> JsString(ec),
        BusinessRatesSubcategoryId.toString -> JsString(councilTaxSubcategory),
        ContactDetailsId.toString           -> Json.toJson(contactDetails),
        PropertyAddressId.toString          -> Json.toJson(propertyAddress),
        TellUsMoreId.toString               -> Json.toJson(tellUs)
      )

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad()(fakeRequest)

      status(result) mustBe OK
    }

    "return 200 for a GET if subcategory is business_rates_changes" in {
      val contactDetails        = ContactDetails("a", "c", "e")
      val ec                    = "business_rates"
      val contactReason         = "new_enquiry"
      val propertyAddress       = PropertyAddress("a", Some("b"), "c", Some("d"), "f")
      val councilTaxSubcategory = "business_rates_changes"
      val tellUs                = TellUsMore("Hello")

      val validData = Map(
        ContactReasonId.toString            -> JsString(contactReason),
        EnquiryCategoryId.toString          -> JsString(ec),
        BusinessRatesSubcategoryId.toString -> JsString(councilTaxSubcategory),
        ContactDetailsId.toString           -> Json.toJson(contactDetails),
        PropertyAddressId.toString          -> Json.toJson(propertyAddress),
        TellUsMoreId.toString               -> Json.toJson(tellUs)
      )

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad()(fakeRequest)

      status(result) mustBe OK
    }

    "return 200 for a GET if subcategory is business_rates_change_valuation" in {
      val contactDetails        = ContactDetails("a", "c", "e")
      val ec                    = "business_rates"
      val contactReason         = "new_enquiry"
      val propertyAddress       = PropertyAddress("a", Some("b"), "c", Some("d"), "f")
      val councilTaxSubcategory = "business_rates_change_valuation"
      val tellUs                = TellUsMore("Hello")

      val validData = Map(
        ContactReasonId.toString            -> JsString(contactReason),
        EnquiryCategoryId.toString          -> JsString(ec),
        BusinessRatesSubcategoryId.toString -> JsString(councilTaxSubcategory),
        ContactDetailsId.toString           -> Json.toJson(contactDetails),
        PropertyAddressId.toString          -> Json.toJson(propertyAddress),
        TellUsMoreId.toString               -> Json.toJson(tellUs)
      )

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad()(fakeRequest)

      status(result) mustBe OK
    }

    "return 200 for a GET if subcategory is business_rates_valuation" in {
      val contactDetails        = ContactDetails("a", "c", "e")
      val ec                    = "business_rates"
      val contactReason         = "new_enquiry"
      val propertyAddress       = PropertyAddress("a", Some("b"), "c", Some("d"), "f")
      val councilTaxSubcategory = "business_rates_valuation"
      val tellUs                = TellUsMore("Hello")

      val validData = Map(
        ContactReasonId.toString            -> JsString(contactReason),
        EnquiryCategoryId.toString          -> JsString(ec),
        BusinessRatesSubcategoryId.toString -> JsString(councilTaxSubcategory),
        ContactDetailsId.toString           -> Json.toJson(contactDetails),
        PropertyAddressId.toString          -> Json.toJson(propertyAddress),
        TellUsMoreId.toString               -> Json.toJson(tellUs)
      )

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad()(fakeRequest)

      status(result) mustBe OK
    }

    "return 200 for a GET if subcategory is business_rates_demolished" in {
      val contactDetails        = ContactDetails("a", "c", "e")
      val ec                    = "business_rates"
      val contactReason         = "new_enquiry"
      val propertyAddress       = PropertyAddress("a", Some("b"), "c", Some("d"), "f")
      val councilTaxSubcategory = "business_rates_demolished"
      val tellUs                = TellUsMore("Hello")

      val validData = Map(
        ContactReasonId.toString            -> JsString(contactReason),
        EnquiryCategoryId.toString          -> JsString(ec),
        BusinessRatesSubcategoryId.toString -> JsString(councilTaxSubcategory),
        ContactDetailsId.toString           -> Json.toJson(contactDetails),
        PropertyAddressId.toString          -> Json.toJson(propertyAddress),
        TellUsMoreId.toString               -> Json.toJson(tellUs)
      )

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad()(fakeRequest)

      status(result) mustBe OK
    }

    "return 200 for a GET if subcategory is business_rates_not_used" in {
      val contactDetails        = ContactDetails("a", "c", "e")
      val ec                    = "business_rates"
      val contactReason         = "new_enquiry"
      val propertyAddress       = PropertyAddress("a", Some("b"), "c", Some("d"), "f")
      val councilTaxSubcategory = "business_rates_not_used"
      val tellUs                = TellUsMore("Hello")

      val validData = Map(
        ContactReasonId.toString            -> JsString(contactReason),
        EnquiryCategoryId.toString          -> JsString(ec),
        BusinessRatesSubcategoryId.toString -> JsString(councilTaxSubcategory),
        ContactDetailsId.toString           -> Json.toJson(contactDetails),
        PropertyAddressId.toString          -> Json.toJson(propertyAddress),
        TellUsMoreId.toString               -> Json.toJson(tellUs)
      )

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad()(fakeRequest)

      status(result) mustBe OK
    }

    "return 200 for a GET if subcategory is business_rates_self_catering" in {
      val contactDetails        = ContactDetails("a", "c", "e")
      val ec                    = "business_rates"
      val contactReason         = "new_enquiry"
      val propertyAddress       = PropertyAddress("a", Some("b"), "c", Some("d"), "f")
      val councilTaxSubcategory = "business_rates_self_catering"
      val tellUs                = TellUsMore("Hello")

      val validData = Map(
        ContactReasonId.toString            -> JsString(contactReason),
        EnquiryCategoryId.toString          -> JsString(ec),
        BusinessRatesSubcategoryId.toString -> JsString(councilTaxSubcategory),
        ContactDetailsId.toString           -> Json.toJson(contactDetails),
        PropertyAddressId.toString          -> Json.toJson(propertyAddress),
        TellUsMoreId.toString               -> Json.toJson(tellUs)
      )

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad()(fakeRequest)

      status(result) mustBe OK
    }

    "return 200 for a GET if subcategory is other-ha-hb-enquiry" in {
      val contactDetails            = ContactDetails("a", "c", "e")
      val ec                        = "housing_benefit"
      val contactReason             = "new_enquiry"
      val propertyAddress           = PropertyAddress("a", Some("b"), "c", Some("d"), "f")
      val housingBenefitSubcategory = "other-ha-hb-enquiry"

      val validData = Map(
        ContactReasonId.toString          -> JsString(contactReason),
        EnquiryCategoryId.toString        -> JsString(ec),
        TellUsMorePage.lastTellUsMorePage -> JsString(housingBenefitSubcategory),
        ContactDetailsId.toString         -> Json.toJson(contactDetails),
        PropertyAddressId.toString        -> Json.toJson(propertyAddress),
        housingBenefitSubcategory         -> JsString("Enquiry details")
      )

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad()(fakeRequest)

      status(result) mustBe OK

      val content = contentAsString(result)
      content must include("What is your enquiry about?")
      content must include("Housing Benefit and Local Housing Allowances")
      content must include("What is your other Housing Benefit or Local Housing Allowances enquiry?")
      content must include("Enquiry details")
    }

    "use backLink to PropertyAddress for Housing Benefit category " in {
      val contactDetails            = ContactDetails("a", "c", "e")
      val ec                        = "housing_benefit"
      val contactReason             = "new_enquiry"
      val propertyAddress           = PropertyAddress("a", Some("b"), "c", Some("d"), "f")
      val housingBenefitSubcategory = "other-ha-hb-enquiry"

      val validData = Map(
        ContactReasonId.toString          -> JsString(contactReason),
        EnquiryCategoryId.toString        -> JsString(ec),
        TellUsMorePage.lastTellUsMorePage -> JsString(housingBenefitSubcategory),
        ContactDetailsId.toString         -> Json.toJson(contactDetails),
        PropertyAddressId.toString        -> Json.toJson(propertyAddress),
        housingBenefitSubcategory         -> JsString("Enquiry details")
      )

      val cacheMap = CacheMap(cacheMapId, validData)

      val getRelevantData = new FakeDataRetrievalAction(Some(cacheMap))

      controller(getRelevantData).enquiryBackLink(new UserAnswers(cacheMap)) mustBe routes.PropertyAddressController.onPageLoad(NormalMode).url
    }

    "redirect to Session Expired for a GET if not existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad()(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad.url)
    }

    "The user answers section builder produces sections for new enquiry for poor repair" in {
      when(mockUserAnswers.contactReason) `thenReturn` Some("new_enquiry")
      when(mockUserAnswers.enquiryCategory) `thenReturn` Some("council_tax")
      when(mockUserAnswers.councilTaxSubcategory) `thenReturn` Some("council_tax_property_poor_repair")
      when(mockUserAnswers.contactDetails) `thenReturn` Some(ContactDetails("a", "c", "e"))
      when(mockUserAnswers.propertyAddress) `thenReturn` Some(PropertyAddress("a", Some("a"), "a", Some("a"), "a"))
      when(mockUserAnswers.tellUsMore) `thenReturn` Some(TellUsMore("a"))
      when(mockUserAnswers.datePropertyChanged) `thenReturn` Some(LocalDate.of(2021, 1, 1))

      val result                 = controller().userAnswersSectionBuilder(mockUserAnswers)
      val checkYourAnswersHelper = new CheckYourAnswersHelper(mockUserAnswers)
      result mustBe Some(AnswerSection(
        None,
        Seq(
          checkYourAnswersHelper.enquiryCategory,
          checkYourAnswersHelper.councilTaxSubcategory,
          checkYourAnswersHelper.datePropertyChanged(),
          checkYourAnswersHelper.tellUsMore("tellUsMore.poorRepair.heading"),
          checkYourAnswersHelper.contactDetails,
          checkYourAnswersHelper.propertyAddress
        ).flatten
      ))
    }

    "The user answers section builder produces sections for new enquiry for business rate from home" in {
      when(mockUserAnswers.contactReason) `thenReturn` Some("new_enquiry")
      when(mockUserAnswers.enquiryCategory) `thenReturn` Some("business_rates")
      when(mockUserAnswers.businessRatesSubcategory) `thenReturn` Some("business_rates_from_home")
      when(mockUserAnswers.datePropertyChanged) `thenReturn` Some(LocalDate.of(2021, 1, 1))
      when(mockUserAnswers.contactDetails) `thenReturn` Some(ContactDetails("a", "c", "e"))
      when(mockUserAnswers.propertyAddress) `thenReturn` Some(PropertyAddress("a", Some("a"), "a", Some("a"), "a"))
      when(mockUserAnswers.tellUsMore) `thenReturn` Some(TellUsMore("a"))

      val result                 = controller().userAnswersSectionBuilder(mockUserAnswers)
      val checkYourAnswersHelper = new CheckYourAnswersHelper(mockUserAnswers)
      result mustBe Some(AnswerSection(
        None,
        Seq(
          checkYourAnswersHelper.enquiryCategory,
          checkYourAnswersHelper.businessRatesSubcategory,
          checkYourAnswersHelper.datePropertyChanged("datePropertyChanged.business.heading"),
          checkYourAnswersHelper.tellUsMore("tellUsMore.business.heading"),
          checkYourAnswersHelper.contactDetails,
          checkYourAnswersHelper.propertyAddress
        ).flatten
      ))
    }

    "The user answers section builder produces sections for new enquiry for business rate not used" in {
      when(mockUserAnswers.contactReason) `thenReturn` Some("new_enquiry")
      when(mockUserAnswers.enquiryCategory) `thenReturn` Some("business_rates")
      when(mockUserAnswers.businessRatesSubcategory) `thenReturn` Some("business_rates_not_used")
      when(mockUserAnswers.contactDetails) `thenReturn` Some(ContactDetails("a", "c", "e"))
      when(mockUserAnswers.propertyAddress) `thenReturn` Some(PropertyAddress("a", Some("a"), "a", Some("a"), "a"))
      when(mockUserAnswers.tellUsMore) `thenReturn` Some(TellUsMore("a"))
      when(mockUserAnswers.datePropertyChanged) `thenReturn` Some(LocalDate.of(2021, 1, 1))

      val result                 = controller().userAnswersSectionBuilder(mockUserAnswers)
      val checkYourAnswersHelper = new CheckYourAnswersHelper(mockUserAnswers)
      result mustBe Some(AnswerSection(
        None,
        Seq(
          checkYourAnswersHelper.enquiryCategory,
          checkYourAnswersHelper.businessRatesSubcategory,
          checkYourAnswersHelper.datePropertyChanged("datePropertyChanged.notUsed.heading"),
          checkYourAnswersHelper.tellUsMore("tellUsMore.notUsed.heading"),
          checkYourAnswersHelper.contactDetails,
          checkYourAnswersHelper.propertyAddress
        ).flatten
      ))
    }

    "The user answers section builder produces sections for new enquiry for business rates other" in {
      when(mockUserAnswers.contactReason) `thenReturn` Some("new_enquiry")
      when(mockUserAnswers.enquiryCategory) `thenReturn` Some("business_rates")
      when(mockUserAnswers.businessRatesSubcategory) `thenReturn` Some("business_rates_other")
      when(mockUserAnswers.tellUsMore) `thenReturn` Some(TellUsMore("a"))
      when(mockUserAnswers.contactDetails) `thenReturn` Some(ContactDetails("a", "c", "e"))
      when(mockUserAnswers.propertyAddress) `thenReturn` Some(PropertyAddress("a", Some("a"), "a", Some("a"), "a"))

      val result                 = controller().userAnswersSectionBuilder(mockUserAnswers)
      val checkYourAnswersHelper = new CheckYourAnswersHelper(mockUserAnswers)
      result mustBe Some(AnswerSection(
        None,
        Seq(
          checkYourAnswersHelper.enquiryCategory,
          checkYourAnswersHelper.businessRatesSubcategory,
          checkYourAnswersHelper.tellUsMore("tellUsMore.business.other.heading"),
          checkYourAnswersHelper.contactDetails,
          checkYourAnswersHelper.propertyAddress
        ).flatten
      ))
    }

    "The user answers section builder produces sections for new enquiry for business" in {
      when(mockUserAnswers.contactReason) `thenReturn` Some("new_enquiry")
      when(mockUserAnswers.enquiryCategory) `thenReturn` Some("council_tax")
      when(mockUserAnswers.councilTaxSubcategory) `thenReturn` Some("council_tax_business_uses")
      when(mockUserAnswers.contactDetails) `thenReturn` Some(ContactDetails("a", "c", "e"))
      when(mockUserAnswers.propertyAddress) `thenReturn` Some(PropertyAddress("a", Some("a"), "a", Some("a"), "a"))
      when(mockUserAnswers.councilTaxBusinessEnquiry) `thenReturn` Some("")
      when(mockUserAnswers.tellUsMore) `thenReturn` Some(TellUsMore("a"))
      when(mockUserAnswers.datePropertyChanged) `thenReturn` Some(LocalDate.of(2021, 1, 1))

      val result                 = controller().userAnswersSectionBuilder(mockUserAnswers)
      val checkYourAnswersHelper = new CheckYourAnswersHelper(mockUserAnswers)
      result mustBe Some(AnswerSection(
        None,
        Seq(
          checkYourAnswersHelper.enquiryCategory,
          checkYourAnswersHelper.councilTaxSubcategory,
          checkYourAnswersHelper.datePropertyChanged("datePropertyChanged.business.heading"),
          checkYourAnswersHelper.tellUsMore("tellUsMore.business.heading"),
          checkYourAnswersHelper.contactDetails,
          checkYourAnswersHelper.propertyAddress
        ).flatten
      ))
    }

    "The user answers section builder produces sections for new enquiry for change area" in {
      when(mockUserAnswers.contactReason) `thenReturn` Some("new_enquiry")
      when(mockUserAnswers.enquiryCategory) `thenReturn` Some("council_tax")
      when(mockUserAnswers.councilTaxSubcategory) `thenReturn` Some("council_tax_area_change")
      when(mockUserAnswers.contactDetails) `thenReturn` Some(ContactDetails("a", "c", "e"))
      when(mockUserAnswers.propertyAddress) `thenReturn` Some(PropertyAddress("a", Some("a"), "a", Some("a"), "a"))
      when(mockUserAnswers.tellUsMore) `thenReturn` Some(TellUsMore("a"))
      when(mockUserAnswers.datePropertyChanged) `thenReturn` Some(LocalDate.of(2021, 1, 1))

      val result                 = controller().userAnswersSectionBuilder(mockUserAnswers)
      val checkYourAnswersHelper = new CheckYourAnswersHelper(mockUserAnswers)
      result mustBe Some(AnswerSection(
        None,
        Seq(
          checkYourAnswersHelper.enquiryCategory,
          checkYourAnswersHelper.councilTaxSubcategory,
          checkYourAnswersHelper.datePropertyChanged("datePropertyChanged.areaChange.heading"),
          checkYourAnswersHelper.tellUsMore("tellUsMore.areaChange.heading"),
          checkYourAnswersHelper.contactDetails,
          checkYourAnswersHelper.propertyAddress
        ).flatten
      ))
    }

    "The user answers section builder produces sections for new enquiry for other" in {
      when(mockUserAnswers.contactReason) `thenReturn` Some("new_enquiry")
      when(mockUserAnswers.enquiryCategory) `thenReturn` Some("council_tax")
      when(mockUserAnswers.councilTaxSubcategory) `thenReturn` Some("council_tax_other")
      when(mockUserAnswers.contactDetails) `thenReturn` Some(ContactDetails("a", "c", "e"))
      when(mockUserAnswers.propertyAddress) `thenReturn` Some(PropertyAddress("a", Some("a"), "a", Some("a"), "a"))
      when(mockUserAnswers.tellUsMore) `thenReturn` Some(TellUsMore("a"))

      val result                 = controller().userAnswersSectionBuilder(mockUserAnswers)
      val checkYourAnswersHelper = new CheckYourAnswersHelper(mockUserAnswers)
      result mustBe Some(AnswerSection(
        None,
        Seq(
          checkYourAnswersHelper.enquiryCategory,
          checkYourAnswersHelper.councilTaxSubcategory,
          checkYourAnswersHelper.tellUsMore("tellUsMore.other.heading"),
          checkYourAnswersHelper.contactDetails,
          checkYourAnswersHelper.propertyAddress
        ).flatten
      ))
    }

    "The user answers section builder produces sections for existing enquiry" in {
      when(mockUserAnswers.contactReason) `thenReturn` Some("more_details")
      when(mockUserAnswers.existingEnquiryCategory) `thenReturn` Some("council_tax")
      when(mockUserAnswers.refNumber) `thenReturn` None
      when(mockUserAnswers.contactDetails) `thenReturn` Some(ContactDetails("a", "c", "e"))
      when(mockUserAnswers.propertyAddress) `thenReturn` Some(PropertyAddress("a", Some("a"), "a", Some("a"), "a"))
      when(mockUserAnswers.whatElse) `thenReturn` Some("a")

      val result                 = controller().userAnswersSectionBuilder(mockUserAnswers)
      val checkYourAnswersHelper = new CheckYourAnswersHelper(mockUserAnswers)
      result mustBe Some(AnswerSection(
        None,
        Seq(
          checkYourAnswersHelper.existingEnquiryCategory,
          checkYourAnswersHelper.refNumber,
          checkYourAnswersHelper.contactDetails,
          checkYourAnswersHelper.propertyAddress,
          checkYourAnswersHelper.whatElse
        ).flatten
      ))
    }

    "The user answers section builder produces sections for update existing enquiry" in {
      when(mockUserAnswers.contactReason) `thenReturn` Some("update_existing")
      when(mockUserAnswers.existingEnquiryCategory) `thenReturn` Some("council_tax")
      when(mockUserAnswers.refNumber) `thenReturn` None
      when(mockUserAnswers.contactDetails) `thenReturn` Some(ContactDetails("a", "c", "e"))
      when(mockUserAnswers.propertyAddress) `thenReturn` Some(PropertyAddress("a", Some("a"), "a", Some("a"), "a"))
      when(mockUserAnswers.anythingElse) `thenReturn` Some("a")

      val result                 = controller().userAnswersSectionBuilder(mockUserAnswers)
      val checkYourAnswersHelper = new CheckYourAnswersHelper(mockUserAnswers)
      result mustBe Some(AnswerSection(
        None,
        Seq(
          checkYourAnswersHelper.existingEnquiryCategory,
          checkYourAnswersHelper.refNumber,
          checkYourAnswersHelper.contactDetails,
          checkYourAnswersHelper.propertyAddress,
          checkYourAnswersHelper.anythingElse
        ).flatten
      ))
    }

    "The user answers section builder produces sections with the business rates check your answers section when the enquiry category is business_rates" in {
      when(mockUserAnswers.enquiryCategory) `thenReturn` Some("business_rates")
      when(mockUserAnswers.contactDetails) `thenReturn` Some(ContactDetails("a", "c", "e"))
      when(mockUserAnswers.propertyAddress) `thenReturn` Some(PropertyAddress("a", Some("a"), "a", Some("a"), "a"))
      when(mockUserAnswers.businessRatesSubcategory) `thenReturn` Some("a")
      when(mockUserAnswers.tellUsMore) `thenReturn` Some(TellUsMore("message"))

      val result                 = controller().userAnswersSectionBuilder(mockUserAnswers)
      val checkYourAnswersHelper = new CheckYourAnswersHelper(mockUserAnswers)
      result mustBe Some(AnswerSection(
        None,
        Seq(
          checkYourAnswersHelper.enquiryCategory,
          checkYourAnswersHelper.businessRatesSubcategory,
          checkYourAnswersHelper.contactDetails,
          checkYourAnswersHelper.propertyAddress,
          checkYourAnswersHelper.tellUsMore()
        ).flatten
      ))
    }

    "The user answers section builder produces sections with the business rates check your answers section when the enquiry category is business_rates " +
      "and addressLine2 and county are None" in {
        when(mockUserAnswers.enquiryCategory) `thenReturn` Some("business_rates")
        when(mockUserAnswers.contactDetails) `thenReturn` Some(ContactDetails("a", "c", "e"))
        when(mockUserAnswers.propertyAddress) `thenReturn` Some(PropertyAddress("a", None, "a", None, "a"))
        when(mockUserAnswers.businessRatesSubcategory) `thenReturn` Some("a")
        when(mockUserAnswers.tellUsMore) `thenReturn` Some(TellUsMore("message"))

        val result                 = controller().userAnswersSectionBuilder(mockUserAnswers)
        val checkYourAnswersHelper = new CheckYourAnswersHelper(mockUserAnswers)
        result mustBe Some(AnswerSection(
          None,
          Seq(
            checkYourAnswersHelper.enquiryCategory,
            checkYourAnswersHelper.businessRatesSubcategory,
            checkYourAnswersHelper.contactDetails,
            checkYourAnswersHelper.propertyAddress,
            checkYourAnswersHelper.tellUsMore()
          ).flatten
        ))
      }

    "The user answers section builder produces sections with the council tax check your answers section when the enquiry category is council_tax" in {

      when(mockUserAnswers.enquiryCategory) `thenReturn` Some("council_tax")
      when(mockUserAnswers.contactDetails) `thenReturn` Some(ContactDetails("a", "c", "e"))
      when(mockUserAnswers.propertyAddress) `thenReturn` Some(PropertyAddress("a", Some("a"), "a", Some("a"), "a"))
      when(mockUserAnswers.councilTaxSubcategory) `thenReturn` Some("council_tax_property_demolished")
      when(mockUserAnswers.tellUsMore) `thenReturn` Some(TellUsMore("message"))

      val result                 = controller().userAnswersSectionBuilder(mockUserAnswers)
      val checkYourAnswersHelper = new CheckYourAnswersHelper(mockUserAnswers)
      result mustBe Some(AnswerSection(
        None,
        Seq(
          checkYourAnswersHelper.enquiryCategory,
          checkYourAnswersHelper.councilTaxSubcategory,
          checkYourAnswersHelper.contactDetails,
          checkYourAnswersHelper.propertyAddress,
          checkYourAnswersHelper.tellUsMore()
        ).flatten
      ))
    }

    "The user answers section builder produces sections with the council tax check your answers section when the enquiry category is council_tax " +
      "and addressLine2 and county are None" in {

        when(mockUserAnswers.enquiryCategory) `thenReturn` Some("council_tax")
        when(mockUserAnswers.contactDetails) `thenReturn` Some(ContactDetails("a", "c", "e"))
        when(mockUserAnswers.propertyAddress) `thenReturn` Some(PropertyAddress("a", None, "a", None, "a"))
        when(mockUserAnswers.councilTaxSubcategory) `thenReturn` Some("council_tax_property_demolished")
        when(mockUserAnswers.tellUsMore) `thenReturn` Some(TellUsMore("message"))

        val result                 = controller().userAnswersSectionBuilder(mockUserAnswers)
        val checkYourAnswersHelper = new CheckYourAnswersHelper(mockUserAnswers)
        result mustBe Some(AnswerSection(
          None,
          Seq(
            checkYourAnswersHelper.enquiryCategory,
            checkYourAnswersHelper.councilTaxSubcategory,
            checkYourAnswersHelper.contactDetails,
            checkYourAnswersHelper.propertyAddress,
            checkYourAnswersHelper.tellUsMore()
          ).flatten
        ))
      }

    "The user answers section builder returns None when giving an unrecognized enquiry category" in {
      when(mockUserAnswers.enquiryCategory) `thenReturn` Some("adsada")
      val result = controller().userAnswersSectionBuilder(mockUserAnswers)
      result mustBe None
    }

    "The user answers section builder returns None when the enquiry category is None" in {
      when(mockUserAnswers.enquiryCategory) `thenReturn` None
      val result = controller().userAnswersSectionBuilder(mockUserAnswers)
      result mustBe None
    }

    "return 500 and the error view for a reaching summary page with wrong enquiry or unknown enquiry" in {
      val contactDetails        = ContactDetails("a", "c", "e")
      val ec                    = "other"
      val propertyAddress       = PropertyAddress("a", Some("b"), "c", Some("d"), "f")
      val councilTaxSubcategory = "council_tax_property_demolished"
      val tellUs                = TellUsMore("Hello")
      val validData             = Map(
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

    "return 500 and the error view for a reaching summary page with no enquiry" in
      intercept[Exception] {
        val result = controller().onPageLoad()(fakeRequest)
        status(result) mustBe INTERNAL_SERVER_ERROR
        contentAsString(result) mustBe internalServerError(frontendAppConfig)(using fakeRequest, messages).toString
      }

  }

  override protected def beforeEach(): Unit = {
    reset(mockUserAnswers)
    when(mockUserAnswers.contactReason) `thenReturn` None // Backward compatibility
  }
}

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
import play.api.libs.json.{JsString, Json}
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.valuationofficeagencycontactfrontend.FakeNavigator
import uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors.FakeDataCacheConnector
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions._
import play.api.test.Helpers._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.PropertyAddressForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers.{BusinessRatesSubcategoryId, CouncilTaxSubcategoryId, EnquiryCategoryId, FairRentEnquiryId, PropertyAddressId}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.{NormalMode, PropertyAddress}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.MessageControllerComponentsHelpers
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{propertyAddress => property_address}

class PropertyAddressControllerSpec extends ControllerSpecBase {

  val msg: String = ""

  def propertyAddress = app.injector.instanceOf[property_address]

  def onwardRoute = routes.EnquiryCategoryController.onPageLoad(NormalMode)

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new PropertyAddressController(frontendAppConfig, messagesApi, FakeDataCacheConnector, new FakeNavigator(desiredRoute = onwardRoute),
      dataRetrievalAction, new DataRequiredActionImpl(ec), propertyAddress, MessageControllerComponentsHelpers.stubMessageControllerComponents)

  def viewAsString(form: Form[PropertyAddress] = PropertyAddressForm()) = propertyAddress(frontendAppConfig, form, NormalMode, msg)(fakeRequest, messages).toString

  "Property Address Controller" must {

    "return OK and the correct view for a GET" in {
      val validData = Map(EnquiryCategoryId.toString -> JsString("council_tax"), CouncilTaxSubcategoryId.toString -> JsString("council_tax_band_too_high"))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))
      val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

//    "populate the view correctly on a GET when the question has previously been answered" in {
//      val validData = Map(PropertyAddressId.toString -> Json.toJson(PropertyAddress("value 1", Some("value 2"), "value 3", Some("value 4"), "AA1 1AA")))
//      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))
//
//      val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeRequest)
//
//      contentAsString(result) mustBe viewAsString(PropertyAddressForm().fill(PropertyAddress("value 1", Some("value 2"), "value 3", Some("value 4"), "AA1 1AA")))
//    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("addressLine1", "value 1"), ("addressLine2", "value 2"), ("town", "value 3"),
        ("county", "value 4"), ("postcode", "AA1 1AA"))

      val result = controller().onSubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val validData = Map(EnquiryCategoryId.toString -> JsString("council_tax"), CouncilTaxSubcategoryId.toString -> JsString("council_tax_band_too_high"))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = PropertyAddressForm().bind(Map("value" -> "invalid value"))

      val result = controller(getRelevantData).onSubmit(NormalMode)(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "return a Bad Request and errors when invalid data is submitted for council_tax_band_for_new" in {
      val validData = Map(EnquiryCategoryId.toString -> JsString("council_tax"), CouncilTaxSubcategoryId.toString -> JsString("council_tax_band_for_new"))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = PropertyAddressForm().bind(Map("value" -> "invalid value"))

      val result = controller(getRelevantData).onSubmit(NormalMode)(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "return a Bad Request and errors when invalid data is submitted for council_tax_property_empty" in {
      val validData = Map(EnquiryCategoryId.toString -> JsString("council_tax"), CouncilTaxSubcategoryId.toString -> JsString("council_tax_property_empty"))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = PropertyAddressForm().bind(Map("value" -> "invalid value"))

      val result = controller(getRelevantData).onSubmit(NormalMode)(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "return a Bad Request and errors when invalid data is submitted for council_tax_property_poor_repair" in {
      val validData = Map(EnquiryCategoryId.toString -> JsString("council_tax"), CouncilTaxSubcategoryId.toString -> JsString("council_tax_property_poor_repair"))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = PropertyAddressForm().bind(Map("value" -> "invalid value"))

      val result = controller(getRelevantData).onSubmit(NormalMode)(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "return a Bad Request and errors when invalid data is submitted for council_tax_property_split_merge" in {
      val validData = Map(EnquiryCategoryId.toString -> JsString("council_tax"), CouncilTaxSubcategoryId.toString -> JsString("council_tax_property_split_merge"))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = PropertyAddressForm().bind(Map("value" -> "invalid value"))

      val result = controller(getRelevantData).onSubmit(NormalMode)(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "return a Bad Request and errors when invalid data is submitted for council_tax_property_demolished" in {
      val validData = Map(EnquiryCategoryId.toString -> JsString("council_tax"), CouncilTaxSubcategoryId.toString -> JsString("council_tax_property_demolished"))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = PropertyAddressForm().bind(Map("value" -> "invalid value"))

      val result = controller(getRelevantData).onSubmit(NormalMode)(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "return a Bad Request and errors when invalid data is submitted for council_tax_area_change" in {
      val validData = Map(EnquiryCategoryId.toString -> JsString("council_tax"), CouncilTaxSubcategoryId.toString -> JsString("council_tax_area_change"))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = PropertyAddressForm().bind(Map("value" -> "invalid value"))

      val result = controller(getRelevantData).onSubmit(NormalMode)(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "return a Bad Request and errors when invalid data is submitted for council_tax_business_uses" in {
      val validData = Map(EnquiryCategoryId.toString -> JsString("council_tax"), CouncilTaxSubcategoryId.toString -> JsString("council_tax_business_uses"))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = PropertyAddressForm().bind(Map("value" -> "invalid value"))

      val result = controller(getRelevantData).onSubmit(NormalMode)(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "return a Bad Request and errors when invalid data is submitted for council_tax_other" in {
      val validData = Map(EnquiryCategoryId.toString -> JsString("council_tax"), CouncilTaxSubcategoryId.toString -> JsString("council_tax_other"))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = PropertyAddressForm().bind(Map("value" -> "invalid value"))

      val result = controller(getRelevantData).onSubmit(NormalMode)(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "return a Bad Request and errors when invalid data is submitted for council_tax_annexe" in {
      val validData = Map(EnquiryCategoryId.toString -> JsString("council_tax"), CouncilTaxSubcategoryId.toString -> JsString("council_tax_annexe"))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = PropertyAddressForm().bind(Map("value" -> "invalid value"))

      val result = controller(getRelevantData).onSubmit(NormalMode)(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "return a Bad Request and errors when invalid data is submitted for council_tax_bill" in {
      val validData = Map(EnquiryCategoryId.toString -> JsString("council_tax"), CouncilTaxSubcategoryId.toString -> JsString("council_tax_bill"))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = PropertyAddressForm().bind(Map("value" -> "invalid value"))

      val result = controller(getRelevantData).onSubmit(NormalMode)(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "return a Bad Request and errors when invalid data is submitted for business_rates_from_home" in {
      val validData = Map(EnquiryCategoryId.toString -> JsString("business_rates"), BusinessRatesSubcategoryId.toString -> JsString("business_rates_from_home"))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = PropertyAddressForm().bind(Map("value" -> "invalid value"))

      val result = controller(getRelevantData).onSubmit(NormalMode)(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "return a Bad Request and errors when invalid data is submitted for business_rates_other" in {
      val validData = Map(EnquiryCategoryId.toString -> JsString("business_rates"), BusinessRatesSubcategoryId.toString -> JsString("business_rates_other"))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = PropertyAddressForm().bind(Map("value" -> "invalid value"))

      val result = controller(getRelevantData).onSubmit(NormalMode)(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "return a Bad Request and errors when invalid data is submitted for business_rates_change_valuation" in {
      val validData = Map(EnquiryCategoryId.toString -> JsString("business_rates"), BusinessRatesSubcategoryId.toString -> JsString("business_rates_change_valuation"))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = PropertyAddressForm().bind(Map("value" -> "invalid value"))

      val result = controller(getRelevantData).onSubmit(NormalMode)(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "return a Bad Request and errors when invalid data is submitted for business_rates_bill" in {
      val validData = Map(EnquiryCategoryId.toString -> JsString("business_rates"), BusinessRatesSubcategoryId.toString -> JsString("business_rates_bill"))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = PropertyAddressForm().bind(Map("value" -> "invalid value"))

      val result = controller(getRelevantData).onSubmit(NormalMode)(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "return a Bad Request and errors when invalid data is submitted for business_rates_changes" in {
      val validData = Map(EnquiryCategoryId.toString -> JsString("business_rates"), BusinessRatesSubcategoryId.toString -> JsString("business_rates_changes"))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = PropertyAddressForm().bind(Map("value" -> "invalid value"))

      val result = controller(getRelevantData).onSubmit(NormalMode)(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "return a Bad Request and errors when invalid data is submitted for business_rates_property_empty" in {
      val validData = Map(EnquiryCategoryId.toString -> JsString("business_rates"), BusinessRatesSubcategoryId.toString -> JsString("business_rates_property_empty"))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = PropertyAddressForm().bind(Map("value" -> "invalid value"))

      val result = controller(getRelevantData).onSubmit(NormalMode)(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "return a Bad Request and errors when invalid data is submitted for business_rates_valuation" in {
      val validData = Map(EnquiryCategoryId.toString -> JsString("business_rates"), BusinessRatesSubcategoryId.toString -> JsString("business_rates_valuation"))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = PropertyAddressForm().bind(Map("value" -> "invalid value"))

      val result = controller(getRelevantData).onSubmit(NormalMode)(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "return a Bad Request and errors when invalid data is submitted for business_rates_demolished" in {
      val validData = Map(EnquiryCategoryId.toString -> JsString("business_rates"), BusinessRatesSubcategoryId.toString -> JsString("business_rates_demolished"))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = PropertyAddressForm().bind(Map("value" -> "invalid value"))

      val result = controller(getRelevantData).onSubmit(NormalMode)(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "return a Bad Request and errors when invalid data is submitted for business_rates_not_used" in {
      val validData = Map(EnquiryCategoryId.toString -> JsString("business_rates"), BusinessRatesSubcategoryId.toString -> JsString("business_rates_not_used"))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = PropertyAddressForm().bind(Map("value" -> "invalid value"))

      val result = controller(getRelevantData).onSubmit(NormalMode)(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "return a Bad Request and errors when invalid data is submitted for business_rates_self_catering" in {
      val validData = Map(EnquiryCategoryId.toString -> JsString("business_rates"), BusinessRatesSubcategoryId.toString -> JsString("business_rates_self_catering"))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = PropertyAddressForm().bind(Map("value" -> "invalid value"))

      val result = controller(getRelevantData).onSubmit(NormalMode)(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad.url)
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("addressLine1", "value 1"), ("addressLine2", "value 2"), ("town", "value 3"), ("county", "value 4"), ("postcode", "value 5"))
      val result = controller(dontGetAnyData).onSubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad.url)
    }

//    "populate the view correctly on a GET when the question has previously been answered and address line 2 and county are None" in {
//      val validData = Map(PropertyAddressId.toString -> Json.toJson(PropertyAddress("value 1", None, "value 3", None, "value 5")))
//      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))
//
//      val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeRequest)
//
//      contentAsString(result) mustBe viewAsString(PropertyAddressForm().fill(PropertyAddress("value 1", None, "value 3", None, "value 5")))
//    }

    "redirect to the next page when valid data is submitted and address line 2 and county are None" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("addressLine1", "value 1"), ("town", "value 3"), ("postcode", "BB11BB"))

      val result = controller().onSubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }
  }
}

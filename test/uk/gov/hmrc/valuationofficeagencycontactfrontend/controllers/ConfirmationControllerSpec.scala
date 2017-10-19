/*
 * Copyright 2017 HM Revenue & Customs
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
import play.api.libs.json.{JsString, Json}
import play.api.test.Helpers._
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors.LightweightContactEventsConnector
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions.{DataRequiredActionImpl, DataRetrievalAction, FakeDataRetrievalAction}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.DateFormatter
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.confirmation

class ConfirmationControllerSpec extends ControllerSpecBase with MockitoSugar {

  val connector = injector.instanceOf[LightweightContactEventsConnector]
  def onwardRoute = routes.IndexController.onPageLoad()

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new ConfirmationController(frontendAppConfig, messagesApi, connector, dataRetrievalAction, new DataRequiredActionImpl)

  "Confirmation Controller" must {

    "return 200 and the correct view for a GET" in {
      val cd = ContactDetails("a", "b", "c", "d", "e")
      val ec = "council_tax"
      val propertyAddress = PropertyAddress("a", Some("b"), "c", "d", "f")
      val councilTaxSubcategory = "council_tax_home_business"
      val tellUs = TellUsMore("Hello")
      val confirmedContactDetails = ConfirmedContactDetails(cd)
      val date = DateFormatter.todaysDate()

      val contact = Contact(confirmedContactDetails, Some(propertyAddress), ec, councilTaxSubcategory, tellUs.message)

      val validData = Map(EnquiryCategoryId.toString -> JsString(ec), CouncilTaxSubcategoryId.toString -> JsString(councilTaxSubcategory),
        ContactDetailsId.toString -> Json.toJson(cd), PropertyAddressId.toString -> Json.toJson(propertyAddress), TellUsMoreId.toString -> Json.toJson(tellUs))

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad()(fakeRequest)

      status(result) mustBe OK

      contentAsString(result) mustBe confirmation(frontendAppConfig, contact, date)(fakeRequest, messages).toString
    }

    "return 200 and the correct view for a GET when address line 2 is None" in {
      val cd = ContactDetails("a", "b", "c", "d", "e")
      val ec = "council_tax"
      val propertyAddress = PropertyAddress("a", None, "c", "d", "f")
      val councilTaxSubcategory = "council_tax_home_business"
      val tellUs = TellUsMore("Hello")
      val confirmedContactDetails = ConfirmedContactDetails(cd)
      val date = DateFormatter.todaysDate()

      val contact = Contact(confirmedContactDetails, Some(propertyAddress), ec, councilTaxSubcategory, tellUs.message)

      val validData = Map(EnquiryCategoryId.toString -> JsString(ec), CouncilTaxSubcategoryId.toString -> JsString(councilTaxSubcategory),
        ContactDetailsId.toString -> Json.toJson(cd), PropertyAddressId.toString -> Json.toJson(propertyAddress), TellUsMoreId.toString -> Json.toJson(tellUs))

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad()(fakeRequest)

      status(result) mustBe OK

      contentAsString(result) mustBe confirmation(frontendAppConfig, contact, date)(fakeRequest, messages).toString
    }

    "redirect to Session Expired for a GET if not existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad()(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
    }

  }
}
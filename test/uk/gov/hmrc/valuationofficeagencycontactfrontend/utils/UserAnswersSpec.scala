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

package uk.gov.hmrc.valuationofficeagencycontactfrontend.utils

import org.mockito.Mockito.when
import org.scalatest.mockito.MockitoSugar
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.valuationofficeagencycontactfrontend.SpecBase
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models._

class UserAnswersSpec extends SpecBase with MockitoSugar {

  val mockUserAnswers = mock[UserAnswers]

  "Create Contact Model method" must {

    "Return a ContactModel object containing a Property Address if all the information is present and the enquiry selected is council_tax" in {
      val cd = ContactDetails("a", "b", "c", "d", "e")
      val confirmedContactDetails = ConfirmedContactDetails(cd)
      val ec = "council_tax"
      val propertyAddress = PropertyAddress("a", Some("b"), "c", Some("d"), "f")
      val councilTaxSubcategory = "council_tax_home_business"
      val tellUs = TellUsMore("Hello")

      val expectedResult = Contact(confirmedContactDetails, propertyAddress, ec, councilTaxSubcategory, tellUs.message)

      val userAnswers = new FakeUserAnswers(cd, ec, councilTaxSubcategory, "", propertyAddress, tellUs)

      val result = userAnswers.contact()

      result mustBe Right(expectedResult)
    }

    "Return a ContactModel object containing a Property Address if address line 2 and county are None and the enquiry selected is council_tax" in {
      val cd = ContactDetails("a", "b", "c", "d", "e")
      val confirmedContactDetails = ConfirmedContactDetails(cd)
      val ec = "council_tax"
      val propertyAddress = PropertyAddress("a", None, "c", None, "f")
      val councilTaxSubcategory = "council_tax_home_business"
      val tellUs = TellUsMore("Hello")

      val expectedResult = Contact(confirmedContactDetails, propertyAddress, ec, councilTaxSubcategory, tellUs.message)

      val userAnswers = new FakeUserAnswers(cd, ec, councilTaxSubcategory, "", propertyAddress, tellUs)

      val result = userAnswers.contact()

      result mustBe Right(expectedResult)
    }

    "Return a ContactModel object containing a Property Address if all the information is present and the enquiry selected is business_rates" in {
      val cd = ContactDetails("a", "b", "c", "d", "e")
      val confirmedContactDetails = ConfirmedContactDetails(cd)
      val ec = "business_rates"
      val propertyAddress = PropertyAddress("a", Some("b"), "c", Some("d"), "f")
      val businessSubcategory = "business_rates_rateable_value"
      val tellUs = TellUsMore("Hello")

      val expectedResult = Contact(confirmedContactDetails, propertyAddress, ec, businessSubcategory, tellUs.message)

      val userAnswers = new FakeUserAnswers(cd, ec, "", businessSubcategory, propertyAddress, tellUs)

      val result = userAnswers.contact()

      result mustBe Right(expectedResult)
    }

    "Return a ContactModel object containing a Property Address if address line 2 and county are None and the enquiry selected is business_rates" in {
      val cd = ContactDetails("a", "b", "c", "d", "e")
      val confirmedContactDetails = ConfirmedContactDetails(cd)
      val ec = "business_rates"
      val propertyAddress = PropertyAddress("a", None, "c", None, "f")
      val businessSubcategory = "business_rates_rateable_value"
      val tellUs = TellUsMore("Hello")

      val expectedResult = Contact(confirmedContactDetails, propertyAddress, ec, businessSubcategory, tellUs.message)

      val userAnswers = new FakeUserAnswers(cd, ec, "", businessSubcategory, propertyAddress, tellUs)

      val result = userAnswers.contact()

      result mustBe Right(expectedResult)
    }

    "Return a Left(Unable to parse) if some details are missing in order to create a Contact" in {
      val userA = new UserAnswers(new CacheMap("", Map()))

      val result = userA.contact()

      result mustBe Left("Unable to parse")
    }

  }
}


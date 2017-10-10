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

import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.BusinessRatesAddress
import org.scalatest.FlatSpec

class BusinessRatesAddressSpec extends FlatSpec {

  val b = BusinessRatesAddress("Business", "1", "Unit", "High Street", "London", "Brent", "AA11AA")

  "Business name" should "be Business" in{
    assert(b.businessName == "Business")
  }

  "Business address line 1" should "be 1" in{
    assert(b.businessAddressLine1 == "1")
  }

  "Business address line 2" should "be Unit" in{
    assert(b.businessAddressLine2 == "Unit")
  }

  "Business address line 3" should "be High Street" in{
    assert(b.businessAddressLine3 == "High Street")
  }

  "Business address town" should "be London" in{
    assert(b.town == "London")
  }

  "Business address county" should "be Brent" in{
    assert(b.county == "Brent")
  }

  "Business address postcode" should "be AA11AA" in{
    assert(b.postcode == "AA11AA")
  }

}
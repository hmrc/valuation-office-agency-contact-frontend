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

import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.PropertyAddress
import org.scalatest.FlatSpec

class PropertyAddressSpec extends FlatSpec {

   val p = PropertyAddress("1", "High Street", "London", "London", "ZZ11ZZ")

  "Property address line 1" should "be 1" in{
    assert(p.addressLine1 == "1")
  }

  "Property address line 2" should "be High Street" in{
    assert(p.addressLine2 == "High Street")
  }

  "Property Town" should "be London" in{
    assert(p.town == "London")
  }

  "Property County" should "be London" in{
    assert(p.county == "London")
  }

  "Property Postcode" should "be ZZ11ZZ" in{
    assert(p.postcode == "ZZ11ZZ")
  }

  "Property address line 1" should "should't be 2" in{
    assert(p.addressLine1 != "2")
  }

  "Property address line 2" should "shouldn't be Avenue" in{
    assert(p.addressLine2 != "Avenue")
  }

  "Property Town" should "shouldn't be" in{
    assert(p.town != "Leeds")
  }

  "Property County" should "shouldn't be Cardiff" in{
    assert(p.county != "Cardiff")
  }

  "Property Postcode" should "shouldn't be AA11AA" in{
    assert(p.postcode != "AA11AA")
  }
}
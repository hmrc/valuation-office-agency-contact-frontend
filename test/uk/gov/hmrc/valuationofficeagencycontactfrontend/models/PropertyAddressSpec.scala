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

package uk.gov.hmrc.valuationofficeagencycontactfrontend.models

import org.scalatest.OptionValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.PropertyAddress

class PropertyAddressSpec extends AnyFlatSpec with Matchers with OptionValues:

  private val address = PropertyAddress("1", Some("High Street"), "London", Some("London"), "ZZ11ZZ")

  private val alternativeAddress = PropertyAddress("22", None, "London", None, "ZZ11ZZ")

  "Property address line 1" should "be 1" in {
    address.addressLine1 shouldBe "1"
  }

  "Property address line 2" should "be High Street" in {
    address.addressLine2.value shouldBe "High Street"
  }

  "Property Town" should "be London" in {
    address.town shouldBe "London"
  }

  "Property County" should "be London" in {
    address.county.value shouldBe "London"
  }

  "Property Postcode" should "be ZZ11ZZ" in {
    address.postcode shouldBe "ZZ11ZZ"
  }

  "Alternative Property address line 1" should "be 22" in {
    alternativeAddress.addressLine1 shouldBe "22"
  }

  "Alternative Property address line 2" should "be None" in {
    alternativeAddress.addressLine2 shouldBe None
  }

  "Alternative Property County" should "be None" in {
    alternativeAddress.county shouldBe None
  }

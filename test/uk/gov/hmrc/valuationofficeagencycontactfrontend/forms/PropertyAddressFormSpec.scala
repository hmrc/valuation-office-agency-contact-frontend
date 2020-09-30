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

package uk.gov.hmrc.valuationofficeagencycontactfrontend.forms

import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.behaviours.FormBehaviours
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.PropertyAddress

class PropertyAddressFormSpec extends FormBehaviours {

  val validData: Map[String, String] = Map(
    "addressLine1" -> "value, 1",
    "addressLine2" -> "value, 2",
    "town" -> "value, 3",
    "county" -> "value, 4",
    "postcode" -> "AA1 1AA"
  )

  val form = PropertyAddressForm()

  "Property Address form" must {
    behave like questionForm(PropertyAddress("value, 1", Some("value, 2"), "value, 3", Some("value, 4"), "AA1 1AA"))

    behave like formWithOptionalTextFields("addressLine2", "county")

    "fail to bind when address line 1 is blank" in {
      val data = validData + ("addressLine1" -> "")
      val expectedError = Seq(error("addressLine1", "propertyAddress.addressLine1.required"), error("addressLine1", "propertyAddress.addressLine1.invalid")).flatten
      checkForError(form, data, expectedError)
    }

    s"fail to bind when address line 1 is omitted" in {
      val data = validData - "addressLine1"
      val expectedError = error("addressLine1", "error.required")
      checkForError(form, data, expectedError)
    }

    s"fail to bind when address line 1 is invalid" in {
      val data = validData + ("addressLine1" -> "1st Line£")
      val expectedError = error("addressLine1", "propertyAddress.addressLine1.invalid")
      checkForError(form, data, expectedError)
    }

    s"fail to bind when address line 1 length is more than 80" in {
      val data = validData + ("addressLine1" -> "aaaaaaaaaaaaaaaaaaaaaa" * 4)
      val expectedError = error("addressLine1", "propertyAddress.addressLine1.length")
      checkForError(form, data, expectedError)
    }

    s"fail to bind when address line 2 is invalid" in {
      val data = validData + ("addressLine2" -> "2nd Line£")
      val expectedError = error("addressLine2", "propertyAddress.addressLine2.invalid")
      checkForError(form, data, expectedError)
    }

    s"fail to bind when address line 2 length is more than 80" in {
      val data = validData + ("addressLine2" -> "aaaaaaaaaaaaaaaaaaaaaa" * 4)
      val expectedError = error("addressLine2", "propertyAddress.addressLine2.length")
      checkForError(form, data, expectedError)
    }

    "fail to bind when town is blank" in {
      val data = validData + ("town" -> "")
      val expectedError = Seq(error("town", "propertyAddress.town.required"), error("town", "propertyAddress.town.invalid")).flatten
      checkForError(form, data, expectedError)
    }

    s"fail to bind when town is omitted" in {
      val data = validData - "town"
      val expectedError = error("town", "error.required")
      checkForError(form, data, expectedError)
    }

    s"fail to bind when town is invalid" in {
      val data = validData + ("town" -> "town*")
      val expectedError = error("town", "propertyAddress.town.invalid")
      checkForError(form, data, expectedError)
    }

    s"fail to bind when town length is more than 80" in {
      val data = validData + ("town" -> "aaaaaaaaaaaaaaaaaaaaaa" * 4)
      val expectedError = error("town", "propertyAddress.town.length")
      checkForError(form, data, expectedError)
    }

    s"fail to bind when county is invalid" in {
      val data = validData + ("county" -> "county!<>")
      val expectedError = error("county", "propertyAddress.county.invalid")
      checkForError(form, data, expectedError)
    }

    s"fail to bind when county length is more than 80" in {
      val data = validData + ("county" -> "aaaaaaaaaaaaaaaaaaaaaa" * 4)
      val expectedError = error("county", "propertyAddress.county.length")
      checkForError(form, data, expectedError)
    }

    "fail to bind when postcode is blank" in {
      val data = validData + ("postcode" -> "")
      val expectedError = Seq(error("postcode", "propertyAddress.postcode.required"), error("postcode", "propertyAddress.postcode.invalid")).flatten
      checkForError(form, data, expectedError)
    }

    s"fail to bind when postcode is omitted" in {
      val data = validData - "postcode"
      val expectedError = error("postcode", "error.required")
      checkForError(form, data, expectedError)
    }

    s"fail to bind when postcode is invalid" in {
      val data = validData + ("postcode" -> "AAAAAA")
      val expectedError = error("postcode", "propertyAddress.postcode.invalid")
      checkForError(form, data, expectedError)
    }

    s"fail to bind when postcode length is more than 8" in {
      val data = validData + ("postcode" -> "AA1212AAA")
      val expectedError = Seq(error("postcode", "propertyAddress.postcode.length"), error("postcode", "propertyAddress.postcode.invalid")).flatten
      checkForError(form, data, expectedError)
    }

  }

}

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

package uk.gov.hmrc.valuationofficeagencycontactfrontend.forms

import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.behaviours.FormBehaviours
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.BusinessRatesAddress

class BusinessRatesAddressFormSpec extends FormBehaviours {

  val validData: Map[String, String] = Map(
    "businessName" -> "value 1",
    "businessAddressLine1" -> "value 2",
    "businessAddressLine2" -> "value 3",
    "businessAddressLine3" -> "value 4",
    "town" -> "value 5",
    "county" -> "value 6",
    "postcode" -> "value 7"
  )

  val form = BusinessRatesAddressForm()

  "BusinessRatesAddress form" must {
    behave like questionForm(BusinessRatesAddress("value 1", "value 2", "value 3", "value 4", "value 5", "value 6", "value 7"))

    behave like formWithMandatoryTextFields("businessName", "businessAddressLine1", "businessAddressLine2", "businessAddressLine3", "town", "county", "postcode")
  }
}

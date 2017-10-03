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
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.PropertyDetails

class PropertyDetailsFormSpec extends FormBehaviours {

  val validData: Map[String, String] = Map(
    "addressLine1" -> "value 1",
    "addressLine2" -> "value 2",
    "town" -> "value 3",
    "county" -> "value 4",
    "postcode" -> "value 5"
  )

  val form = PropertyDetailsForm()

  "PropertyDetails form" must {
    behave like questionForm(PropertyDetails("value 1", "value 2", "value 3", "value 4", "value 5"))

    behave like formWithMandatoryTextFields("addressLine1", "addressLine2", "town", "county", "postcode")
  }
}

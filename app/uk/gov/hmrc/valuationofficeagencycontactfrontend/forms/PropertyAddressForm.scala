/*
 * Copyright 2022 HM Revenue & Customs
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

import play.api.data.Form
import play.api.data.Forms._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.PropertyAddress
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.FormHelpers.antiXSSRegex

object PropertyAddressForm {

  private val postcodeRegex = """^\s*\-*\.*\(*\)*[a-zA-Z]{1,2}[0-9]{1,2}[a-zA-Z]?(\s*\-*\.*\(*\)*[0-9][a-zA-Z]{1,2})?$""" //scalastyle:ignore

  def apply(): Form[PropertyAddress] = Form(
    mapping(
      "addressLine1" -> text.verifying("propertyAddress.addressLine1.required", !_.isEmpty)
        .verifying("propertyAddress.addressLine1.length", _.length <= 80)
        .verifying("propertyAddress.addressLine1.invalid", _.matches(antiXSSRegex)),
      "addressLine2" -> optional(text
        .verifying("propertyAddress.addressLine2.length", _.length <= 80)
        .verifying("propertyAddress.addressLine2.invalid", _.matches(antiXSSRegex))),
      "town" -> text.verifying("propertyAddress.town.required", !_.isEmpty)
        .verifying("propertyAddress.town.length", _.length <= 80)
        .verifying("propertyAddress.town.invalid", _.matches(antiXSSRegex)),
      "county" -> optional(text
        .verifying("propertyAddress.county.length", _.length <= 80)
        .verifying("propertyAddress.county.invalid", _.matches(antiXSSRegex))),
      "postcode" -> text.verifying("propertyAddress.postcode.required", !_.isEmpty)
        .verifying("propertyAddress.postcode.length", _.length <= 8)
        .verifying("propertyAddress.postcode.invalid", _.toUpperCase.matches(postcodeRegex))
    )(PropertyAddress.apply)(PropertyAddress.unapply)
  )
}

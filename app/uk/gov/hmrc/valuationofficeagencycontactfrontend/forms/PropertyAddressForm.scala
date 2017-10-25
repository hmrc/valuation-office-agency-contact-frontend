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

import play.api.data.Form
import play.api.data.Forms._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.PropertyAddress

object PropertyAddressForm {

  private val addressLineRegex = """^[A-Za-z0-9\s\-&]+$"""
  private val postcodeRegex = """(GIR ?0AA)|((([A-Z-[QVX]][0-9][0-9]?)|(([A-Z-[QVX]][A-Z-[IJZ]][0-9][0-9]?)|(([A-Z-[QVX]][0-9][A-HJKPSTUW])|([A-Z-[QVX]][A-Z-[IJZ]][0-9][ABEHMNPRVWXY])))) ?[0-9][A-Z-[CIKMOV]]{2})""" //scalastyle:ignore


  def apply(): Form[PropertyAddress] = Form(
    mapping(
      "addressLine1" -> nonEmptyText
        .verifying("error.addressline.max_length", _.length <= 35)
        .verifying("error.addressline.invalid", _.matches(addressLineRegex)),
      "addressLine2" -> optional(text
        .verifying("error.addressline.max_length", _.length <= 35)
        .verifying("error.addressline.invalid", _.matches(addressLineRegex))),
      "town" -> nonEmptyText
        .verifying("error.addressline.max_length", _.length <= 35)
        .verifying("error.addressline.invalid", _.matches(addressLineRegex)),
      "county" -> optional(text
        .verifying("error.addressline.max_length", _.length <= 35)
        .verifying("error.addressline.invalid", _.matches(addressLineRegex))),
      "postcode" -> nonEmptyText
        .verifying("error.postcode.max_length", _.length <= 8)
        .verifying("error.postcode.invalid", _.toUpperCase.matches(postcodeRegex))
    )(PropertyAddress.apply)(PropertyAddress.unapply)
  )
}

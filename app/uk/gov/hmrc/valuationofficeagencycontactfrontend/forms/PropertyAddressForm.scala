/*
 * Copyright 2019 HM Revenue & Customs
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

  private val postcodeRegex = """(GIR ?0AA)|((([A-Z-[QVX]][0-9][0-9]?)|(([A-Z-[QVX]][A-Z-[IJZ]][0-9][0-9]?)|(([A-Z-[QVX]][0-9][A-HJKPSTUW])|([A-Z-[QVX]][A-Z-[IJZ]][0-9][ABEHMNPRVWXY])))) ?[0-9][A-Z-[CIKMOV]]{2})""" //scalastyle:ignore

  def apply(): Form[PropertyAddress] = Form(
    mapping(
      "addressLine1" -> nonEmptyText
        .verifying("error.addressline.max_length", _.length <= 80)
        .verifying("error.xss.invalid", _.matches(antiXSSRegex)),
      "addressLine2" -> optional(text
        .verifying("error.addressline.max_length", _.length <= 80)
        .verifying("error.xss.invalid", _.matches(antiXSSRegex))),
      "town" -> nonEmptyText
        .verifying("error.addressline.max_length", _.length <= 80)
        .verifying("error.xss.invalid", _.matches(antiXSSRegex)),
      "county" -> optional(text
        .verifying("error.addressline.max_length", _.length <= 80)
        .verifying("error.xss.invalid", _.matches(antiXSSRegex))),
      "postcode" -> nonEmptyText
        .verifying("error.postcode.max_length", _.length <= 8)
        .verifying("error.postcode.invalid", _.toUpperCase.matches(postcodeRegex))
    )(PropertyAddress.apply)(PropertyAddress.unapply)
  )
}

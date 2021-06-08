/*
 * Copyright 2021 HM Revenue & Customs
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
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.ContactDetails
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.FormHelpers.antiXSSRegex

object ContactDetailsForm {

  private val phoneRegex = """^\+?[-\s\./0-9]*(\d{1,3}|\d{1}\-\d{1,3})[-\s\./0-9]*(\(?\d{1,3}\)?|\d{1,3})[-\s\./0-9]*(\d{1,4}[-\s\./0-9]*\d{1,4})[-\s\./0-9]*Z"""
  private val emailRegex = """^[a-zA-Z0-9\.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$""" //scalastyle:ignore

  def apply(): Form[ContactDetails] = Form(
    mapping(
      "fullName" -> text.verifying("contactDetails.fullName.required", !_.isEmpty)
                .verifying("contactDetails.fullName.invalid", _.matches(antiXSSRegex)),
      "email" -> text.verifying("contactDetails.email.required", !_.isEmpty)
                .verifying("contactDetails.email.invalid", _.matches(emailRegex)),
      "contactNumber" -> text.verifying("contactDetails.contactNumber.required", !_.isEmpty)
                .verifying("contactDetails.contactNumber.length", _.length >= 11)
                .verifying("contactDetails.contactNumber.length", _.length <= 20)
                .verifying("contactDetails.contactNumber.invalid", _ matches(phoneRegex))
    )(ContactDetails.apply)(ContactDetails.unapply)
  )
}



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

import play.api.data.{Form, FormError}
import play.api.data.Forms._
import play.api.data.format.Formatter
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.ContactDetails

object EmailConstraint extends Formatter[String] {
  override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], String] = {
    if (data.getOrElse(key, "").equals(data.getOrElse("email", ""))) {
      Right(data.getOrElse(key, ""))
    } else {
      Left(List(FormError(key, "error.email.unmatched")))
    }
  }

  override def unbind(key: String, value: String) = Map(key -> value)
}

object ContactDetailsForm {
  def apply(): Form[ContactDetails] = Form(
    mapping(
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "email" -> email,
      "confirmEmail" -> of(EmailConstraint),
      "contactNumber" -> nonEmptyText
    )(ContactDetails.apply)(ContactDetails.unapply)
  )
}



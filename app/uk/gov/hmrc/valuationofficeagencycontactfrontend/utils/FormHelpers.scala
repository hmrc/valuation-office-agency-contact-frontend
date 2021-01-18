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

package uk.gov.hmrc.valuationofficeagencycontactfrontend.utils

import play.api.data.Form

object FormHelpers {

  def getErrorByKey[A](form: Form[A], errorKey: String) = {
    form.error(errorKey) match {  //TODO - retrieve only one error. Some fields have more that validation rules
      case None => ""
      case Some(error) => error.message
    }
  }

  def getErrorsByKey[A](form: Form[A], errorKey: String): Seq[String] = {
    form.errors(errorKey).map(_.message)
  }

  val antiXSSRegex = """^[A-Za-z0-9\s\-&,]+$"""
}

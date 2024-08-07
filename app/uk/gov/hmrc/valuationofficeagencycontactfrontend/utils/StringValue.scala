/*
 * Copyright 2024 HM Revenue & Customs
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

import play.api.data.FormError
import play.api.data.format.Formatter

/**
  * String form binder.
  *
  * Returns empty string instead of error.required
  *
  * @author Yuriy Tumakha
  */
object StringValue extends Formatter[String] {
  def bind(key: String, data: Map[String, String]): Either[Seq[FormError], String] = Right(data.getOrElse(key, ""))

  def unbind(key: String, value: String): Map[String, String] = Map(key -> value)
}

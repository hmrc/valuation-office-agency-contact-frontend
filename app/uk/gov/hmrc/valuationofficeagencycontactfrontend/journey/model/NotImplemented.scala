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

package uk.gov.hmrc.valuationofficeagencycontactfrontend.journey.model

import play.api.data.Form
import play.api.data.Forms.{single, text}
import play.api.mvc.Call
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.UserAnswers

/**
  * @author Yuriy Tumakha
  */
abstract class NotImplemented(val key: String) extends Page[String] {

  override def heading: String = "page.not-implemented"

  val fieldId: String = "field"

  val form: Form[String] = Form(single("value" -> text))

  val getValue: UserAnswers => Option[String] = _ => None

  override def nextPage: UserAnswers => Call = ???
}

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
import play.api.data.Forms._
import play.api.data.validation.Constraints
import play.api.mvc.Call
import uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes
import uk.gov.hmrc.valuationofficeagencycontactfrontend.journey.JourneyPageRequest
import uk.gov.hmrc.valuationofficeagencycontactfrontend.journey.model.TellUsMorePage.{lastTellUsMorePage, maxChars, textareaRegex}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.NormalMode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.UserAnswers

import scala.concurrent.Future
import scala.util.matching.Regex

/**
 * @author Yuriy Tumakha
 */
abstract class TellUsMorePage(val key: String, val fieldId: String) extends Page[String] {

  val form: Form[String] = Form(single(fieldId -> text
    .verifying(Constraints.nonEmpty(errorMessage = errorRequired))
    .verifying(Constraints.pattern(textareaRegex, error = errorPattern))
    .verifying(Constraints.maxLength(maxChars, errorMaxLength))))

  def getValue: UserAnswers => Option[String] = _.getString(key)

  override def beforeSaveAnswers: (DataCacheConnector, JourneyPageRequest[?]) => Future[?] =
    (dataCacheConnector, request) => dataCacheConnector.save[String](request.sessionId, lastTellUsMorePage, key)

  override def nextPage: UserAnswers => Call = _ => routes.ContactDetailsController.onPageLoad(NormalMode)
}

object TellUsMorePage {
  val lastTellUsMorePage   = "lastTellUsMorePage"
  val maxChars             = 5000
  val textareaRegex: Regex = """^[^<>]*$""".r
}

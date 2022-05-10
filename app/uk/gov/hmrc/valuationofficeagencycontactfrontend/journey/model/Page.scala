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

package uk.gov.hmrc.valuationofficeagencycontactfrontend.journey.model

import play.api.data.Form
import play.api.i18n.Lang
import play.api.mvc.Call
import uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes
import uk.gov.hmrc.valuationofficeagencycontactfrontend.journey.JourneyPageRequest
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.NormalMode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.UserAnswers

import scala.concurrent.Future

/**
 * @author Yuriy Tumakha
 */
trait Page[T] {

  def key: String

  def fieldId: String

  def form: Form[T]

  def getValue: UserAnswers => Option[T]

  def previousPage: UserAnswers => Call

  def nextPage: UserAnswers => Call

  def nextLang: UserAnswers => Option[Lang] = _ => None

  def heading = s"$fieldId.heading"

  def errorRequired = s"error.$fieldId.required"

  def errorPattern = s"error.$fieldId.pattern"

  def errorMaxLength = s"error.$fieldId.maxLength"

  def appStartPage: Call = routes.EnquiryCategoryController.onPageLoad(NormalMode)

  def beforeSaveAnswers: (DataCacheConnector, JourneyPageRequest[_]) => Future[_] = (_, _) => Future.unit

  def helpWithService: Option[String] = None

}

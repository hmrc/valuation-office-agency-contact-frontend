/*
 * Copyright 2023 HM Revenue & Customs
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

package uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions

import play.api.mvc.{AnyContent, BodyParser, Request}
import play.api.test.Helpers
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.requests.OptionalDataRequest
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.UserAnswers

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global

class FakeDataRetrievalAction(cacheMapToReturn: Option[CacheMap]) extends DataRetrievalAction {
  override protected def transform[A](request: Request[A]): Future[OptionalDataRequest[A]] = cacheMapToReturn match {
    case None => Future(OptionalDataRequest(request, "id", None))
    case Some(cacheMap)=> Future(OptionalDataRequest(request, "id", Some(new UserAnswers(cacheMap))))
  }

  override def parser: BodyParser[AnyContent] = Helpers.stubControllerComponents().parsers.default

  override protected def executionContext: ExecutionContext = Helpers.stubControllerComponents().executionContext
}

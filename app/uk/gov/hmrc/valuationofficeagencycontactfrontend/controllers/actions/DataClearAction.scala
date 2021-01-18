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

package uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions


import com.google.inject.{ImplementedBy, Inject}
import play.api.mvc.{ActionBuilder, ActionTransformer, AnyContent, BodyParser, ControllerComponents, Request}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.requests.OptionalDataRequest
import uk.gov.hmrc.play.HeaderCarrierConverter

import scala.concurrent.{ExecutionContext, Future}

class DataClearActionImpl @Inject()(val dataCacheConnector: DataCacheConnector, cc: ControllerComponents) extends DataClearAction {

  override protected def transform[A](request: Request[A]): Future[OptionalDataRequest[A]] = {
    implicit val hc = HeaderCarrierConverter.fromHeadersAndSession(request.headers, Some(request.session))

    hc.sessionId match {
      case None => Future.failed(new IllegalStateException())
      case Some(sessionId) =>
        dataCacheConnector.clear(sessionId.toString)
        Future.successful(OptionalDataRequest(request, sessionId.toString, None))
    }
  }

  override def parser: BodyParser[AnyContent] = cc.parsers.default

  override protected def executionContext: ExecutionContext = cc.executionContext
}

@ImplementedBy(classOf[DataClearActionImpl])
trait DataClearAction extends ActionTransformer[Request, OptionalDataRequest] with ActionBuilder[OptionalDataRequest, AnyContent]

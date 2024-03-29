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

package uk.gov.hmrc.valuationofficeagencycontactfrontend

import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.mvc.Call
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors.AuditingService
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers.Identifier
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.Mode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.UserAnswers

import scala.concurrent.ExecutionContext.Implicits.global

class FakeNavigator(desiredRoute: Call) extends Navigator(mock[AuditingService]) {

  override def nextPage(controllerId: Identifier, mode: Mode)(implicit hc: HeaderCarrier): UserAnswers => Call =
    _ => desiredRoute

}

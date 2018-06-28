/*
 * Copyright 2018 HM Revenue & Customs
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

package uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors

import play.api.libs.json.JsValue
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.audit.model.ExtendedDataEvent
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.AuditServiceConnector

import scala.concurrent.ExecutionContext


object AuditingService extends AuditingService {
  protected val auditConnector = AuditServiceConnector
}

trait AuditingService {

  protected def auditConnector: AuditConnector

  def sendEvent(auditType: String, json: JsValue)(implicit ec: ExecutionContext, hc: HeaderCarrier): Unit = {
    val event = eventFor(auditType, json)
    auditConnector.sendExtendedEvent(event)
  }

  private def eventFor(auditType: String, json: JsValue)(implicit hc: HeaderCarrier) = {
    ExtendedDataEvent(
      auditSource = "digital-contact-centre",
      auditType = auditType,
      tags = (hc.headers :+ "transactionName" -> "submit-contact-to-VOA").toMap,
      detail = json
    )
  }
}

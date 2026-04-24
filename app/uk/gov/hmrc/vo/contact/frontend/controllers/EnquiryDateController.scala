/*
 * Copyright 2026 HM Revenue & Customs
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

package uk.gov.hmrc.vo.contact.frontend.controllers

import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.MessagesControllerComponents
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject
import scala.concurrent.ExecutionContext
import play.api.mvc
import play.api.mvc.AnyContent
import uk.gov.hmrc.vo.contact.frontend.Navigator
import uk.gov.hmrc.vo.contact.frontend.connectors.{AuditingService, DataCacheConnector}
import uk.gov.hmrc.vo.contact.frontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.vo.contact.frontend.forms.EnquiryDateForm
import uk.gov.hmrc.vo.contact.frontend.identifiers.EnquiryDateId
import uk.gov.hmrc.vo.contact.frontend.models.{Mode, NormalMode}
import uk.gov.hmrc.vo.contact.frontend.utils.{DateUtil, UserAnswers}
import uk.gov.hmrc.vo.contact.frontend.views.html.enquiryDate

class EnquiryDateController @Inject() (
  override val messagesApi: MessagesApi,
  requireData: DataRequiredAction,
  getData: DataRetrievalAction,
  auditService: AuditingService,
  dataCacheConnector: DataCacheConnector,
  navigator: Navigator,
  enquiry_date: enquiryDate,
  cc: MessagesControllerComponents
)(using ec: ExecutionContext,
  dateUtil: DateUtil
) extends FrontendController(cc)
  with I18nSupport {

  def onPageLoad(mode: Mode): mvc.Action[AnyContent] = (getData andThen requireData) { implicit request =>
    val preparedForm = request.userAnswers.enquiryDate match {
      case None        => EnquiryDateForm()
      case Some(value) => EnquiryDateForm().fill(value)
    }
    Ok(enquiry_date(preparedForm, EnquiryDateForm.beforeDate(), NormalMode))
  }

  def onSubmit(mode: Mode): mvc.Action[AnyContent] = getData.async { implicit request =>
    EnquiryDateForm().bindFromRequest().fold(
      formWithErrors => BadRequest(enquiry_date(formWithErrors, EnquiryDateForm.beforeDate(), NormalMode)),
      value => {
        auditService.sendRadioButtonSelection(request.uri, "enquiryDate" -> value)
        dataCacheConnector.save[String](request.sessionId, EnquiryDateId.toString, value).map(cacheMap =>
          Redirect(navigator.nextPage(EnquiryDateId, mode).apply(UserAnswers(cacheMap)))
        )
      }
    )
  }

}

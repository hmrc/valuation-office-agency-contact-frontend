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

import javax.inject.Inject
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.MessagesControllerComponents
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import scala.concurrent.{ExecutionContext, Future}
import play.api.mvc
import play.api.mvc.AnyContent
import uk.gov.hmrc.vo.contact.frontend.Navigator
import uk.gov.hmrc.vo.contact.frontend.connectors.{AuditingService, DataCacheConnector}
import uk.gov.hmrc.vo.contact.frontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.vo.contact.frontend.forms.EnquiryCategoryForm
import uk.gov.hmrc.vo.contact.frontend.identifiers.{EnquiryCategoryId, ExistingEnquiryCategoryId}
import uk.gov.hmrc.vo.contact.frontend.models.Mode
import uk.gov.hmrc.vo.contact.frontend.utils.UserAnswers
import uk.gov.hmrc.vo.contact.frontend.views.html.enquiryCategory

class EnquiryCategoryController @Inject() (
  override val messagesApi: MessagesApi,
  auditService: AuditingService,
  dataCacheConnector: DataCacheConnector,
  navigator: Navigator,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  enquiryCategory: enquiryCategory,
  cc: MessagesControllerComponents
) extends FrontendController(cc)
  with I18nSupport {

  implicit val ec: ExecutionContext = cc.executionContext

  def onPageLoad(mode: Mode): mvc.Action[AnyContent] = (getData andThen requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.enquiryCategory match {
        case None        => EnquiryCategoryForm()
        case Some(value) => EnquiryCategoryForm().fill(value)
      }
      Ok(enquiryCategory(preparedForm, mode))
  }

  def onSubmit(mode: Mode): mvc.Action[AnyContent] = getData.async {
    implicit request =>
      EnquiryCategoryForm().bindFromRequest().fold(
        (formWithErrors: Form[String]) =>
          Future.successful(BadRequest(enquiryCategory(formWithErrors, mode))),
        value =>
          for {
            _        <- dataCacheConnector.remove(request.sessionId, ExistingEnquiryCategoryId.toString)
            cacheMap <- dataCacheConnector.save[String](request.sessionId, EnquiryCategoryId.toString, value)
          } yield {
            auditService.sendRadioButtonSelection(request.uri, "enquiryCategory" -> value)
            Redirect(navigator.nextPage(EnquiryCategoryId, mode).apply(UserAnswers(cacheMap)))
          }
      )
  }
}

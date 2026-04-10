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

import javax.inject.{Inject, Singleton}
import play.api.Configuration
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.MessagesControllerComponents
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import scala.concurrent.{ExecutionContext, Future}
import play.api.mvc
import play.api.mvc.AnyContent
import uk.gov.hmrc.vo.contact.frontend.connectors.{AuditingService, DataCacheConnector}
import uk.gov.hmrc.vo.contact.frontend.controllers.actions.DataRetrievalAction
import uk.gov.hmrc.vo.contact.frontend.forms.ContactReasonForm
import uk.gov.hmrc.vo.contact.frontend.models.NormalMode
import uk.gov.hmrc.vo.contact.frontend.views.html.contactReason

@Singleton
class Application @Inject() (
  override val messagesApi: MessagesApi,
  contactReasonView: contactReason,
  languageSwitchController: LanguageSwitchController,
  getData: DataRetrievalAction,
  dataCacheConnector: DataCacheConnector,
  auditService: AuditingService,
  configuration: Configuration,
  cc: MessagesControllerComponents
)(using ec: ExecutionContext
) extends FrontendController(cc)
  with I18nSupport {

  def start(): mvc.Action[AnyContent] = Action.async { implicit request =>
    val defaultPage = Ok(contactReasonView(ContactReasonForm.form))
    language match {
      case "cy" => Future.successful(configuration.getOptional[String]("govukStartPageWelsh")
          .fold(defaultPage)(Redirect(_)))
      case _    => Future.successful(configuration.getOptional[String]("govukStartPage")
          .fold(defaultPage)(Redirect(_)))
    }
  }

  def logout(): mvc.Action[AnyContent] = getData.async { implicit request =>
    auditService.sendLogout(request.userAnswers)
    dataCacheConnector.clear(request.sessionId).map { _ =>
      Redirect(routes.ContactReasonController.onPageLoad).withNewSession
    }
  }

  def startWelsh(): mvc.Action[AnyContent] = Action.async { implicit request =>
    val newReq = request.withHeaders(request.headers.replace(REFERER -> createRefererURL()))
    languageSwitchController.switchToLanguage("cymraeg").apply(newReq)
  }

  def createRefererURL(): String =
    uk.gov.hmrc.vo.contact.frontend.controllers.routes.ContactReasonController.onPageLoad.url

  private def language(using messages: Messages): String = messages.lang.language

}

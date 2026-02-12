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

package uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers

import javax.inject.{Inject, Singleton}
import play.api.Configuration
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.MessagesControllerComponents
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.ContactReasonForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.NormalMode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.contactReason
import uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors.{AuditingService, DataCacheConnector}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions.DataRetrievalAction

import scala.concurrent.{ExecutionContext, Future}
import play.api.mvc
import play.api.mvc.AnyContent

@Singleton
class Application @Inject() (
  override val messagesApi: MessagesApi,
  contactReason: contactReason,
  languageSwitchController: LanguageSwitchController,
  getData: DataRetrievalAction,
  dataCacheConnector: DataCacheConnector,
  auditService: AuditingService,
  configuration: Configuration,
  cc: MessagesControllerComponents
)(implicit ec: ExecutionContext
) extends FrontendController(cc)
  with I18nSupport {

  def start(): mvc.Action[AnyContent] = Action.async { implicit request =>
    val defaultPage = Ok(contactReason(ContactReasonForm(), NormalMode))
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
      Redirect(routes.ContactReasonController.onPageLoad()).withNewSession
    }
  }

  def startWelsh(): mvc.Action[AnyContent] = Action.async { implicit request =>
    val newReq = request.withHeaders(request.headers.replace(REFERER -> createRefererURL()))
    languageSwitchController.switchToLanguage("cymraeg").apply(newReq)
  }

  def createRefererURL(): String =
    uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.ContactReasonController.onPageLoad().url

  private def language(implicit messages: Messages): String = messages.lang.language

}

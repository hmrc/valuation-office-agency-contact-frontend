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

package uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers

import javax.inject.{Inject, Singleton}
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.MessagesControllerComponents
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import uk.gov.hmrc.valuationofficeagencycontactfrontend.FrontendAppConfig
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.ContactReasonForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.{Mode, NormalMode}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.contactReason
import uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions.DataRetrievalAction

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class Application @Inject() (override val messagesApi: MessagesApi,
                            val appConfig: FrontendAppConfig,
                             contactReason: contactReason,
                             languageSwitchController: LanguageSwitchController,
                             getData: DataRetrievalAction,
                             dataCacheConnector: DataCacheConnector,
                            cc: MessagesControllerComponents
                           )(implicit ec: ExecutionContext) extends FrontendController(cc) with I18nSupport {

  val log = Logger(this.getClass)

  def start(mode: Mode) = Action.async { implicit request =>
    if (appConfig.startPageRedirect) {
      Future.successful(Redirect(appConfig.govukStartPage))
    } else {
      Future.successful(Ok(contactReason(ContactReasonForm(), NormalMode)))
    }
  }

  def logout() = getData.async { implicit request =>
      dataCacheConnector.clear(request.sessionId).map { _ =>
        Redirect(routes.ContactReasonController.onPageLoad()).withNewSession
      }
  }

  def startWelsh() = Action.async { implicit request =>
    val newReq = request.withHeaders(request.headers.replace(REFERER -> createRefererURL))
    languageSwitchController.switchToLanguage("cymraeg").apply(newReq)
  }

  def createRefererURL(): String = {
    uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.ContactReasonController.onPageLoad().url
  }
}

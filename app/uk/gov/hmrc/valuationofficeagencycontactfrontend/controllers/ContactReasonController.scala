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

import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.valuationofficeagencycontactfrontend.Navigator
import uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors.{AuditingService, DataCacheConnector}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions.{DataClearAction, DataRetrievalAction}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.ContactReasonForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers.ContactReasonId
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.{Mode, NormalMode}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.UserAnswers
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.contactReason

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}


class ContactReasonController @Inject()( override val messagesApi: MessagesApi,
                                         clearData: DataClearAction,
                                         getData: DataRetrievalAction,
                                         auditService: AuditingService,
                                         dataCacheConnector: DataCacheConnector,
                                         navigator: Navigator,
                                         contact_reason: contactReason,
                                         cc: MessagesControllerComponents
                                       )(implicit ec: ExecutionContext) extends FrontendController(cc) with I18nSupport {

  def onPageLoad(mode: Mode) = clearData { implicit request =>
    Ok(contact_reason(ContactReasonForm(), NormalMode))
  }

  def onSubmit(mode: Mode) = getData.async { implicit request =>
    ContactReasonForm().bindFromRequest().fold(
      formWithErrors =>Future.successful(BadRequest(contact_reason(formWithErrors, NormalMode))),
      value => {
        auditService.sendRadioButtonSelection(request.uri, "contactReason" -> value)
        dataCacheConnector.save[String](request.sessionId, ContactReasonId.toString, value).map(cacheMap =>
          Redirect(navigator.nextPage(ContactReasonId, mode)(new UserAnswers(cacheMap))))
      }
    )
  }

  def redirect: Action[AnyContent] = Action {
    Redirect(routes.ContactReasonController.onPageLoad())
  }

}

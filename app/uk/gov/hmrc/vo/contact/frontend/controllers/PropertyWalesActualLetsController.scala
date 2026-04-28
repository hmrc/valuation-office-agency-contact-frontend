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
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.vo.contact.frontend.Navigator
import uk.gov.hmrc.vo.contact.frontend.connectors.{AuditingService, DataCacheConnector}
import uk.gov.hmrc.vo.contact.frontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.vo.contact.frontend.forms.PropertyWalesActualLetsForm
import uk.gov.hmrc.vo.contact.frontend.identifiers.PropertyWalesActualLetsId
import uk.gov.hmrc.vo.contact.frontend.models.Mode
import uk.gov.hmrc.vo.contact.frontend.utils.UserAnswers
import uk.gov.hmrc.vo.contact.frontend.views.html.propertyWalesActualLets

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class PropertyWalesActualLetsController @Inject() (
  override val messagesApi: MessagesApi,
  auditService: AuditingService,
  dataCacheConnector: DataCacheConnector,
  navigator: Navigator,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  propertyWalesActualLets: propertyWalesActualLets,
  cc: MessagesControllerComponents
) extends FrontendController(cc)
  with I18nSupport:

  given ExecutionContext = cc.executionContext

  def onPageLoad: Action[AnyContent] = (getData andThen requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.propertyWalesActualLetsEnquiry match
        case None        => PropertyWalesActualLetsForm()
        case Some(value) => PropertyWalesActualLetsForm().fill(value)
      Ok(propertyWalesActualLets(preparedForm))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (getData andThen requireData).async {
    implicit request =>
      PropertyWalesActualLetsForm().bindFromRequest().fold(
        formWithErrors => BadRequest(propertyWalesActualLets(formWithErrors)),
        value =>
          auditService.sendRadioButtonSelection(request.uri, "businessRatesSelfCatering70Days" -> value)
          dataCacheConnector.save[String](request.sessionId, PropertyWalesActualLetsId.toString, value).map(cacheMap =>
            Redirect(navigator.nextPage(PropertyWalesActualLetsId, mode).apply(UserAnswers(cacheMap)))
          )
      )
  }

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

import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.valuationofficeagencycontactfrontend.Navigator
import uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors.{AuditingService, DataCacheConnector}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.PropertyEnglandAvailableLetsForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers.PropertyEnglandAvailableLetsId
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.Mode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.UserAnswers
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{propertyEnglandAvailableLets => property_england_available_lets}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{propertyEnglandLetsNoAction => property_england_lets_no_action}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{propertyWalesLets => wales_lets}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PropertyEnglandAvailableLetsController @Inject() (
  override val messagesApi: MessagesApi,
  auditService: AuditingService,
  dataCacheConnector: DataCacheConnector,
  navigator: Navigator,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  propertyEnglandAvailableLets: property_england_available_lets,
  propertyEnglandLetsNoAction: property_england_lets_no_action,
  propertyWalesLets: wales_lets,
  cc: MessagesControllerComponents
) extends FrontendController(cc)
  with I18nSupport {

  implicit val ec: ExecutionContext = cc.executionContext

  def onPageLoad(mode: Mode): Action[AnyContent] = (getData andThen requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.propertyEnglandAvailableLetsEnquiry match {
        case None        => PropertyEnglandAvailableLetsForm()
        case Some(value) => PropertyEnglandAvailableLetsForm().fill(value)
      }
      Ok(propertyEnglandAvailableLets(preparedForm, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (getData andThen requireData).async {
    implicit request =>
      PropertyEnglandAvailableLetsForm().bindFromRequest().fold(
        (formWithErrors: Form[String]) =>
          Future.successful(BadRequest(propertyEnglandAvailableLets(formWithErrors, mode))),
        value => {
          auditService.sendRadioButtonSelection(request.uri, "businessRatesSelfCatering140DaysEN" -> value)
          dataCacheConnector.save[String](request.sessionId, PropertyEnglandAvailableLetsId.toString, value).map(cacheMap =>
            Redirect(navigator.nextPage(PropertyEnglandAvailableLetsId, mode).apply(new UserAnswers(cacheMap)))
          )
        }
      )
  }

  def onWalLetsPageLoad(mode: Mode): Action[AnyContent] = (getData andThen requireData) {
    implicit request =>
      Ok(propertyWalesLets())
  }

}

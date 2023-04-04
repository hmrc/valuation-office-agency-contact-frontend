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

package uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers

import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors.{AuditingService, DataCacheConnector}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.PropertyEnglandActualLetsForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers.PropertyEnglandActualLetsId
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.Mode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.UserAnswers
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{propertyEnglandActualLets => property_england_actual_lets}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.{FrontendAppConfig, Navigator}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PropertyEnglandActualLetsController @Inject()(
                                                     appConfig: FrontendAppConfig,
                                                     override val messagesApi: MessagesApi,
                                                     auditService: AuditingService,
                                                     dataCacheConnector: DataCacheConnector,
                                                     navigator: Navigator,
                                                     getData: DataRetrievalAction,
                                                     requireData: DataRequiredAction,
                                                     propertyEnglandActualLets: property_england_actual_lets,
                                                     cc: MessagesControllerComponents
                                                   ) extends FrontendController(cc) with I18nSupport {

  implicit val ec: ExecutionContext = cc.executionContext

  def onPageLoad(mode: Mode): Action[AnyContent] = (getData andThen requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.propertyEnglandActualLetsEnquiry match {
        case None => PropertyEnglandActualLetsForm()
        case Some(value) => PropertyEnglandActualLetsForm().fill(value)
      }
      Ok(propertyEnglandActualLets(appConfig, preparedForm, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (getData andThen requireData).async {
    implicit request =>
      PropertyEnglandActualLetsForm().bindFromRequest().fold(
        (formWithErrors: Form[String]) =>
          Future.successful(BadRequest(propertyEnglandActualLets(appConfig, formWithErrors, mode))),
        value => {
          auditService.sendRadioButtonSelection(request.uri, "businessRatesSelfCatering70DaysEN" -> value)
          dataCacheConnector.save[String](request.sessionId, PropertyEnglandActualLetsId.toString, value).map(cacheMap =>
            Redirect(navigator.nextPage(PropertyEnglandActualLetsId, mode).apply(new UserAnswers(cacheMap))))
        }
      )
  }
}

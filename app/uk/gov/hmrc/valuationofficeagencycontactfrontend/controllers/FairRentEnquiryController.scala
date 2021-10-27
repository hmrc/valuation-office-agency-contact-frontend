/*
 * Copyright 2021 HM Revenue & Customs
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
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.PropertyEnglandLets140DaysForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers.PropertyEnglandLets140DaysId
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.Mode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.UserAnswers
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{propertyEnglandLets140Days => property_england_lets_140_days, propertyEnglandLetsNoAction => property_england_lets_no_action, propertyWalesLets => wales_lets}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.{FrontendAppConfig, Navigator}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class FairRentEnquiryController @Inject()(
                                                     appConfig: FrontendAppConfig,
                                                     override val messagesApi: MessagesApi,
                                                     dataCacheConnector: DataCacheConnector,
                                                     navigator: Navigator,
                                                     getData: DataRetrievalAction,
                                                     requireData: DataRequiredAction,
                                                     propertyEnglandLets140Days: property_england_lets_140_days,
                                                     propertyEnglandLetsNoAction: property_england_lets_no_action,
                                                     propertyWalesLets: wales_lets,
                                                     cc: MessagesControllerComponents
                                                   ) extends FrontendController(cc) with I18nSupport {

  implicit val ec: ExecutionContext = cc.executionContext

  def onPageLoad(mode: Mode): Action[AnyContent] = (getData andThen requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.propertyEnglandLets140DaysEnquiry match {
        case None => PropertyEnglandLets140DaysForm()
        case Some(value) => PropertyEnglandLets140DaysForm().fill(value)
      }
      Ok(propertyEnglandLets140Days(appConfig, preparedForm, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (getData andThen requireData).async {
    implicit request =>
      PropertyEnglandLets140DaysForm().bindFromRequest().fold(
        (formWithErrors: Form[String]) =>
          Future.successful(BadRequest(propertyEnglandLets140Days(appConfig, formWithErrors, mode))),
        (value) =>
          dataCacheConnector.save[String](request.sessionId, PropertyEnglandLets140DaysId.toString, value).map(cacheMap =>
            Redirect(navigator.nextPage(PropertyEnglandLets140DaysId, mode)(new UserAnswers(cacheMap))))
      )
  }

  def onEngLetsNoActionPageLoad(mode: Mode): Action[AnyContent] = (getData andThen requireData) {
    implicit request =>
      Ok(propertyEnglandLetsNoAction(appConfig))
  }

  def onWalLetsPageLoad(mode: Mode): Action[AnyContent] = (getData andThen requireData) {
    implicit request =>
      Ok(propertyWalesLets(appConfig))
  }



}

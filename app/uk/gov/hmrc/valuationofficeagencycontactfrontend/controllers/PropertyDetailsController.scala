/*
 * Copyright 2017 HM Revenue & Customs
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

import javax.inject.Inject

import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.PropertyDetailsForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers.PropertyDetailsId
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.{Mode, PropertyDetails}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.UserAnswers
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.propertyDetails

import scala.concurrent.Future

class PropertyDetailsController @Inject()(appConfig: FrontendAppConfig,
                                          override val messagesApi: MessagesApi,
                                          dataCacheConnector: DataCacheConnector,
                                          navigator: Navigator,
                                          getData: DataRetrievalAction,
                                          requireData: DataRequiredAction) extends FrontendController with I18nSupport {

  def onPageLoad(mode: Mode) = (getData andThen requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.propertyDetails match {
        case None => PropertyDetailsForm()
        case Some(value) => PropertyDetailsForm().fill(value)
      }
      Ok(propertyDetails(appConfig, preparedForm, mode))
  }

  def onSubmit(mode: Mode) = (getData andThen requireData).async {
    implicit request =>
      PropertyDetailsForm().bindFromRequest().fold(
        (formWithErrors: Form[PropertyDetails]) =>
          Future.successful(BadRequest(propertyDetails(appConfig, formWithErrors, mode))),
        (value) =>
          dataCacheConnector.save[PropertyDetails](request.sessionId, PropertyDetailsId.toString, value).map(cacheMap =>
            Redirect(navigator.nextPage(PropertyDetailsId, mode)(new UserAnswers(cacheMap))))
      )
  }
}

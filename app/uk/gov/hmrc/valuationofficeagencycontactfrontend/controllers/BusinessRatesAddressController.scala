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
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.BusinessRatesAddressForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers.BusinessRatesAddressId
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.{Mode, BusinessRatesAddress}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.UserAnswers
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.businessRatesAddress

import scala.concurrent.Future

class BusinessRatesAddressController @Inject()(appConfig: FrontendAppConfig,
                                                  override val messagesApi: MessagesApi,
                                                  dataCacheConnector: DataCacheConnector,
                                                  navigator: Navigator,
                                                  getData: DataRetrievalAction,
                                                  requireData: DataRequiredAction) extends FrontendController with I18nSupport {

  def onPageLoad(mode: Mode) = (getData andThen requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.businessRatesAddress match {
        case None => BusinessRatesAddressForm()
        case Some(value) => BusinessRatesAddressForm().fill(value)
      }
      Ok(businessRatesAddress(appConfig, preparedForm, mode))
  }

  def onSubmit(mode: Mode) = (getData andThen requireData).async {
    implicit request =>
      BusinessRatesAddressForm().bindFromRequest().fold(
        (formWithErrors: Form[BusinessRatesAddress]) =>
          Future.successful(BadRequest(businessRatesAddress(appConfig, formWithErrors, mode))),
        (value) =>
          dataCacheConnector.save[BusinessRatesAddress](request.sessionId, BusinessRatesAddressId.toString, value).map(cacheMap =>
            Redirect(navigator.nextPage(BusinessRatesAddressId, mode)(new UserAnswers(cacheMap))))
      )
  }
}

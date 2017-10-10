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
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.CouncilTaxAddressForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers.CouncilTaxAddressId
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.{Mode, CouncilTaxAddress}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.UserAnswers
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.councilTaxAddress

import scala.concurrent.Future

class CouncilTaxAddressController @Inject()(appConfig: FrontendAppConfig,
                                            override val messagesApi: MessagesApi,
                                            dataCacheConnector: DataCacheConnector,
                                            navigator: Navigator,
                                            getData: DataRetrievalAction,
                                            requireData: DataRequiredAction) extends FrontendController with I18nSupport {

  def onPageLoad(mode: Mode) = (getData andThen requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.councilTaxAddress match {
        case None => CouncilTaxAddressForm()
        case Some(value) => CouncilTaxAddressForm().fill(value)
      }
      Ok(councilTaxAddress(appConfig, preparedForm, mode))
  }

  def onSubmit(mode: Mode) = (getData andThen requireData).async {
    implicit request =>
      CouncilTaxAddressForm().bindFromRequest().fold(
        (formWithErrors: Form[CouncilTaxAddress]) =>
          Future.successful(BadRequest(councilTaxAddress(appConfig, formWithErrors, mode))),
        (value) =>
          dataCacheConnector.save[CouncilTaxAddress](request.sessionId, CouncilTaxAddressId.toString, value).map(cacheMap =>
            Redirect(navigator.nextPage(CouncilTaxAddressId, mode)(new UserAnswers(cacheMap))))
      )
  }
}

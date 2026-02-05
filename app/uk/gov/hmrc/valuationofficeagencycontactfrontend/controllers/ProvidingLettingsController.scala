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
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.valuationofficeagencycontactfrontend.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions.DataRetrievalAction
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers.{EnquiryCategoryId, ExistingEnquiryCategoryId, FairRentEnquiryId}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.NormalMode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.UserAnswers
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{providingLettings => providing_lettings}

import scala.concurrent.ExecutionContext

@Singleton
class ProvidingLettingsController @Inject() (
  val appConfig: FrontendAppConfig,
  override val messagesApi: MessagesApi,
  providingLettings: providing_lettings,
  dataCacheConnector: DataCacheConnector,
  getData: DataRetrievalAction,
  navigator: Navigator,
  cc: MessagesControllerComponents
)(implicit ec: ExecutionContext
) extends FrontendController(cc)
  with I18nSupport {

  def onPageLoad: Action[AnyContent] = Action { implicit request =>
    Ok(providingLettings(appConfig))
  }

  def toEnquiryForm: Action[AnyContent] = getData.async {
    implicit request =>
      for {
        _        <- dataCacheConnector.remove(request.sessionId, ExistingEnquiryCategoryId.toString)
        _        <- dataCacheConnector.save[String](request.sessionId, EnquiryCategoryId.toString, "fair_rent")
        cacheMap <- dataCacheConnector.save[String](request.sessionId, FairRentEnquiryId.toString, "other_request")
      } yield Redirect(navigator.nextPage(FairRentEnquiryId, NormalMode).apply(new UserAnswers(cacheMap)))
  }

}

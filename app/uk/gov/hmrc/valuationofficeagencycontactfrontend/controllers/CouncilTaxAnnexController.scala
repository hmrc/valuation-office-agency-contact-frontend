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
import uk.gov.hmrc.valuationofficeagencycontactfrontend.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.AnnexeForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers.CouncilTaxAnnexId
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.Mode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.UserAnswers
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{councilTaxAnnex => council_tax_annex}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CouncilTaxAnnexController @Inject()(val appConfig: FrontendAppConfig,
                                          override val messagesApi: MessagesApi,
                                          dataCacheConnector: DataCacheConnector,
                                          navigator: Navigator,
                                          getData: DataRetrievalAction,
                                          requireData: DataRequiredAction,
                                          councilTaxAnnex: council_tax_annex,
                                          cc: MessagesControllerComponents
                                         ) extends FrontendController(cc) with I18nSupport {

  implicit val ec: ExecutionContext = cc.executionContext

  def onPageLoad(mode: Mode): Action[AnyContent] = (getData andThen requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.annexeEnquiry match {
        case None => AnnexeForm()
        case Some(value) => AnnexeForm().fill(value)
      }
    Ok(councilTaxAnnex(appConfig, preparedForm, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = getData.async {
    implicit request =>
      AnnexeForm().bindFromRequest().fold(
        (formWithErrors: Form[String]) =>
          Future.successful(BadRequest(councilTaxAnnex(appConfig, formWithErrors, mode))),
        value =>
          for {
            _ <- dataCacheConnector.remove(request.sessionId, CouncilTaxAnnexId.toString)
            cacheMap <- dataCacheConnector.save[String](request.sessionId, CouncilTaxAnnexId.toString, value)
          } yield Redirect(navigator.nextPage(CouncilTaxAnnexId, mode)(new UserAnswers(cacheMap)))
      )
  }
}
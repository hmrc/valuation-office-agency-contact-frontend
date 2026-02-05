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

import javax.inject.Inject
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.MessagesControllerComponents
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors.{AuditingService, DataCacheConnector}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.CouncilTaxSubcategoryForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers.{BusinessRatesSubcategoryId, CouncilTaxSubcategoryId}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.Mode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.UserAnswers
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{councilTaxSubcategory => council_tax_subcategory}

import scala.concurrent.{ExecutionContext, Future}
import play.api.mvc
import play.api.mvc.AnyContent

class CouncilTaxSubcategoryController @Inject() (
  appConfig: FrontendAppConfig,
  override val messagesApi: MessagesApi,
  auditService: AuditingService,
  dataCacheConnector: DataCacheConnector,
  navigator: Navigator,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  councilTaxSubcategory: council_tax_subcategory,
  cc: MessagesControllerComponents
) extends FrontendController(cc)
  with I18nSupport {

  implicit val ec: ExecutionContext = cc.executionContext

  def onPageLoad(mode: Mode): mvc.Action[AnyContent] = (getData andThen requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.councilTaxSubcategory match {
        case None        => CouncilTaxSubcategoryForm()
        case Some(value) => CouncilTaxSubcategoryForm().fill(value)
      }
      Ok(councilTaxSubcategory(appConfig, preparedForm, mode))
  }

  def onSubmit(mode: Mode): mvc.Action[AnyContent] = (getData andThen requireData).async {
    implicit request =>
      CouncilTaxSubcategoryForm().bindFromRequest().fold(
        (formWithErrors: Form[String]) =>
          Future.successful(BadRequest(councilTaxSubcategory(appConfig, formWithErrors, mode))),
        value =>
          for {
            _        <- dataCacheConnector.remove(request.sessionId, BusinessRatesSubcategoryId.toString)
            cacheMap <- dataCacheConnector.save[String](request.sessionId, CouncilTaxSubcategoryId.toString, value)
          } yield {
            auditService.sendRadioButtonSelection(request.uri, "councilTaxSubcategory" -> value)
            Redirect(navigator.nextPage(CouncilTaxSubcategoryId, mode).apply(new UserAnswers(cacheMap)))
          }
      )
  }
}

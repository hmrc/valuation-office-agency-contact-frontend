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
import uk.gov.hmrc.vo.contact.frontend.forms.CouncilTaxBusinessEnquiryForm
import uk.gov.hmrc.vo.contact.frontend.identifiers.CouncilTaxBusinessEnquiryId
import uk.gov.hmrc.vo.contact.frontend.models.Mode
import uk.gov.hmrc.vo.contact.frontend.utils.UserAnswers
import uk.gov.hmrc.vo.contact.frontend.views.html.{businessRatesNoNeedToPay, councilTaxBusinessEnquiry, propertySmallPartUsed}

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class CouncilTaxBusinessController @Inject() (
  override val messagesApi: MessagesApi,
  auditService: AuditingService,
  dataCacheConnector: DataCacheConnector,
  navigator: Navigator,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  councilTaxBusinessEnquiry: councilTaxBusinessEnquiry,
  propertySmallPartUsed: propertySmallPartUsed,
  businessRatesNoNeedToPay: businessRatesNoNeedToPay,
  cc: MessagesControllerComponents
) extends FrontendController(cc)
  with I18nSupport:

  given ExecutionContext = cc.executionContext

  def onPageLoad(mode: Mode): Action[AnyContent] = (getData andThen requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.councilTaxBusinessEnquiry match
        case None        => CouncilTaxBusinessEnquiryForm()
        case Some(value) => CouncilTaxBusinessEnquiryForm().fill(value)

      Ok(councilTaxBusinessEnquiry(preparedForm, backLink(request.userAnswers, mode)))
  }

  def onEnquirySubmit(mode: Mode): Action[AnyContent] = (getData andThen requireData) async {
    implicit request =>
      CouncilTaxBusinessEnquiryForm().bindFromRequest().fold(
        formWithErrors => BadRequest(councilTaxBusinessEnquiry(formWithErrors, backLink(request.userAnswers, mode))),
        value =>
          for
            cacheMap <- dataCacheConnector.save[String](request.sessionId, CouncilTaxBusinessEnquiryId.toString, value)
          yield
            auditService.sendRadioButtonSelection(request.uri, "councilTaxBusinessEnquiry" -> value)
            Redirect(navigator.nextPage(CouncilTaxBusinessEnquiryId, mode).apply(UserAnswers(cacheMap)))
      )
  }

  def onSmallPartUsedPageLoad: Action[AnyContent] = (getData andThen requireData) {
    implicit request =>
      Ok(propertySmallPartUsed())
  }

  def onSmallPartUsedBusinessRatesPageLoad: Action[AnyContent] = (getData andThen requireData) {
    implicit request =>
      Ok(businessRatesNoNeedToPay())
  }

  private def backLink(answers: UserAnswers, mode: Mode): String =
    answers.businessRatesSubcategory match
      case Some("business_rates_from_home") => routes.BusinessRatesSubcategoryController.onPageLoad(mode).url
      case _                                => routes.CouncilTaxSubcategoryController.onPageLoad(mode).url

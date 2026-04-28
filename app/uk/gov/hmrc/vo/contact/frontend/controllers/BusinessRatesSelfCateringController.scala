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
import uk.gov.hmrc.vo.contact.frontend.forms.BusinessRatesSelfCateringForm
import uk.gov.hmrc.vo.contact.frontend.identifiers.BusinessRatesSelfCateringId
import uk.gov.hmrc.vo.contact.frontend.models.Mode
import uk.gov.hmrc.vo.contact.frontend.utils.UserAnswers
import uk.gov.hmrc.vo.contact.frontend.views.html.{businessRatesSelfCateringEnquiry as business_rates_self_catering_enquiry, propertyEnglandLets as england_lets, propertyWalesLets as wales_lets}

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class BusinessRatesSelfCateringController @Inject() (
  override val messagesApi: MessagesApi,
  auditService: AuditingService,
  dataCacheConnector: DataCacheConnector,
  navigator: Navigator,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  businessRatesSelfCateringEnquiry: business_rates_self_catering_enquiry,
  propertyEnglandLets: england_lets,
  propertyWalesLets: wales_lets,
  cc: MessagesControllerComponents
) extends FrontendController(cc)
  with I18nSupport:

  given ExecutionContext = cc.executionContext

  def onPageLoad(mode: Mode): Action[AnyContent] = (getData andThen requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.businessRatesSelfCateringEnquiry match
        case None        => BusinessRatesSelfCateringForm()
        case Some(value) => BusinessRatesSelfCateringForm().fill(value)

      Ok(businessRatesSelfCateringEnquiry(preparedForm, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (getData andThen requireData).async {
    implicit request =>
      BusinessRatesSelfCateringForm().bindFromRequest().fold(
        formWithErrors => BadRequest(businessRatesSelfCateringEnquiry(formWithErrors, mode)),
        value => {
          auditService.sendRadioButtonSelection(request.uri, "businessRatesSelfCatering" -> value)
          dataCacheConnector.save[String](request.sessionId, BusinessRatesSelfCateringId.toString, value).map(cacheMap =>
            Redirect(navigator.nextPage(BusinessRatesSelfCateringId, mode).apply(UserAnswers(cacheMap)))
          )
        }
      )
  }

  def onEngLetsPageLoad: Action[AnyContent] = (getData andThen requireData) {
    implicit request =>
      Ok(propertyEnglandLets())
  }

  def onWalLetsPageLoad: Action[AnyContent] = (getData andThen requireData) {
    implicit request =>
      Ok(propertyWalesLets())
  }

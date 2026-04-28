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
import uk.gov.hmrc.vo.contact.frontend.forms.BusinessRatesPropertyForm
import uk.gov.hmrc.vo.contact.frontend.identifiers.BusinessRatesPropertyEnquiryId
import uk.gov.hmrc.vo.contact.frontend.models.Mode
import uk.gov.hmrc.vo.contact.frontend.utils.UserAnswers
import uk.gov.hmrc.vo.contact.frontend.views.html.{businessRatesNonBusiness as business_rates_non_business, businessRatesPropertyEnquiry as business_rates_property_enquiry}

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class BusinessRatesPropertyController @Inject() (
  override val messagesApi: MessagesApi,
  auditService: AuditingService,
  dataCacheConnector: DataCacheConnector,
  navigator: Navigator,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  businessRatesPropertyEnquiry: business_rates_property_enquiry,
  businessRatesNonBusiness: business_rates_non_business,
  cc: MessagesControllerComponents
) extends FrontendController(cc)
  with I18nSupport:

  given ExecutionContext = cc.executionContext

  def onPageLoad(mode: Mode): Action[AnyContent] = (getData andThen requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.businessRatesPropertyEnquiry match
        case None        => BusinessRatesPropertyForm()
        case Some(value) => BusinessRatesPropertyForm().fill(value)

      Ok(businessRatesPropertyEnquiry(preparedForm, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (getData andThen requireData).async {
    implicit request =>
      BusinessRatesPropertyForm().bindFromRequest().fold(
        formWithErrors => BadRequest(businessRatesPropertyEnquiry(formWithErrors, mode)),
        value =>
          auditService.sendRadioButtonSelection(request.uri, "businessRatesPropertyEnquiry" -> value)
          dataCacheConnector.save[String](request.sessionId, BusinessRatesPropertyEnquiryId.toString, value).map(cacheMap =>
            Redirect(navigator.nextPage(BusinessRatesPropertyEnquiryId, mode).apply(UserAnswers(cacheMap)))
          )
      )
  }

  def onNonBusinessPageLoad: Action[AnyContent] = (getData andThen requireData) {
    implicit request =>
      Ok(businessRatesNonBusiness())
  }

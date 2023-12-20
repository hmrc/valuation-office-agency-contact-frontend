/*
 * Copyright 2023 HM Revenue & Customs
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
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors.{AuditingService, DataCacheConnector}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.BusinessRatesPropertyForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers.BusinessRatesPropertyEnquiryId
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.Mode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.UserAnswers
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{businessRatesPropertyEnquiry => business_rates_property_enquiry}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{businessRatesNonBusiness => business_rates_non_business}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.{FrontendAppConfig, Navigator}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class BusinessRatesPropertyController @Inject() (
  appConfig: FrontendAppConfig,
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
  with I18nSupport {

  implicit val ec: ExecutionContext = cc.executionContext

  def onPageLoad(mode: Mode): Action[AnyContent] = (getData andThen requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.businessRatesPropertyEnquiry match {
        case None        => BusinessRatesPropertyForm()
        case Some(value) => BusinessRatesPropertyForm().fill(value)
      }

      Ok(businessRatesPropertyEnquiry(appConfig, preparedForm, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (getData andThen requireData).async {
    implicit request =>
      BusinessRatesPropertyForm().bindFromRequest().fold(
        (formWithErrors: Form[String]) =>
          Future.successful(BadRequest(businessRatesPropertyEnquiry(appConfig, formWithErrors, mode))),
        value => {
          auditService.sendRadioButtonSelection(request.uri, "businessRatesPropertyEnquiry" -> value)
          dataCacheConnector.save[String](request.sessionId, BusinessRatesPropertyEnquiryId.toString, value).map(cacheMap =>
            Redirect(navigator.nextPage(BusinessRatesPropertyEnquiryId, mode).apply(new UserAnswers(cacheMap)))
          )
        }
      )
  }

  def onNonBusinessPageLoad: Action[AnyContent] = (getData andThen requireData) {
    implicit request =>
      Ok(businessRatesNonBusiness(appConfig))
  }
}

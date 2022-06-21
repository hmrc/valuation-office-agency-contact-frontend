/*
 * Copyright 2022 HM Revenue & Customs
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
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.BusinessRatesSubcategoryForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers.{BusinessRatesSubcategoryId, CouncilTaxSubcategoryId}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.Mode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.UserAnswers
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{businessRatesSubcategory => business_rates_subcategory}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{businessRatesPropertyDemolished => business_rates_demolished}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{businessRatesValuation => business_rates_valuation}

import scala.concurrent.{ExecutionContext, Future}

class BusinessRatesSubcategoryController @Inject()(
                                        appConfig: FrontendAppConfig,
                                        override val messagesApi: MessagesApi,
                                        auditService: AuditingService,
                                        dataCacheConnector: DataCacheConnector,
                                        navigator: Navigator,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction,
                                        businessRatesSubcategory: business_rates_subcategory,
                                        businessRatesPropertyDemolished: business_rates_demolished,
                                        businessRatesValuation: business_rates_valuation,
                                        cc: MessagesControllerComponents
                                                  ) extends FrontendController(cc) with I18nSupport {

  implicit val ec: ExecutionContext = cc.executionContext

  def onPageLoad(mode: Mode) = (getData andThen requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.businessRatesSubcategory match {
        case None => BusinessRatesSubcategoryForm()
        case Some(value) => BusinessRatesSubcategoryForm().fill(value)
      }
      Ok(businessRatesSubcategory(appConfig, preparedForm, mode))
  }

  def onSubmit(mode: Mode) = (getData andThen requireData).async {
    implicit request =>
      BusinessRatesSubcategoryForm().bindFromRequest().fold(
        (formWithErrors: Form[String]) =>
          Future.successful(BadRequest(businessRatesSubcategory(appConfig, formWithErrors, mode))),
        value =>
          for {
            _ <- dataCacheConnector.remove(request.sessionId, CouncilTaxSubcategoryId.toString)
            cacheMap <- dataCacheConnector.save[String](request.sessionId, BusinessRatesSubcategoryId.toString, value)
          } yield {
            auditService.sendRadioButtonSelection(request.uri, "businessRatesSubcategory" -> value)
            Redirect(navigator.nextPage(BusinessRatesSubcategoryId, mode).apply(new UserAnswers(cacheMap)))
          }
      )
  }

  def onDemolishedPageLoad(mode: Mode) = (getData andThen requireData) {
    implicit request =>
      Ok(businessRatesPropertyDemolished(appConfig,mode))
  }

  def onValuationPageLoad(mode: Mode)= (getData andThen requireData) {
    implicit request =>
      Ok(businessRatesValuation(appConfig))
  }
}

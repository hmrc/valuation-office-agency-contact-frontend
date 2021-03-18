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
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.BusinessRatesSelfCateringForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers.BusinessRatesSelfCateringId
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.Mode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.UserAnswers
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{businessRatesSelfCateringEnquiry => business_rates_self_catering_enquiry}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{propertyEnglandLets => england_lets}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class BusinessRatesSelfCateringController @Inject()(
                                                appConfig: FrontendAppConfig,
                                                override val messagesApi: MessagesApi,
                                                dataCacheConnector: DataCacheConnector,
                                                navigator: Navigator,
                                                getData: DataRetrievalAction,
                                                requireData: DataRequiredAction,
                                                businessRatesSelfCateringEnquiry: business_rates_self_catering_enquiry,
                                                propertyEnglandLets: england_lets,
                                                cc: MessagesControllerComponents
                                              ) extends FrontendController(cc) with I18nSupport {

  implicit val ec: ExecutionContext = cc.executionContext

  def onPageLoad(mode: Mode): Action[AnyContent] = (getData andThen requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.businessRatesSelfCateringEnquiry match {
        case None => BusinessRatesSelfCateringForm()
        case Some(value) => BusinessRatesSelfCateringForm().fill(value)
      }

      Ok(businessRatesSelfCateringEnquiry(appConfig, preparedForm, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (getData andThen requireData).async {
    implicit request =>
      BusinessRatesSelfCateringForm().bindFromRequest().fold(
        (formWithErrors: Form[String]) =>
          Future.successful(BadRequest(businessRatesSelfCateringEnquiry(appConfig, formWithErrors, mode))),
        (value) =>
          dataCacheConnector.save[String](request.sessionId, BusinessRatesSelfCateringId.toString, value).map(cacheMap =>
            Redirect(navigator.nextPage(BusinessRatesSelfCateringId, mode)(new UserAnswers(cacheMap))))
      )
  }

  def onEngLetsPageLoad(mode: Mode): Action[AnyContent] = (getData andThen requireData) {
      implicit request =>
        Ok(propertyEnglandLets(appConfig))
  }

}

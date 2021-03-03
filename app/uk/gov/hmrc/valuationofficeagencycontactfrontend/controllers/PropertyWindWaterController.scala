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

import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import uk.gov.hmrc.valuationofficeagencycontactfrontend.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.PropertyWindWaterForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers.{CouncilTaxPropertyPoorRepairId, EnquiryDateId}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.NormalMode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.UserAnswers
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{propertyWindWatertight => property_wind_watertight}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{windWatertightEnquiry => wind_watertight_enquiry}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PropertyWindWaterController @Inject()(val appConfig: FrontendAppConfig,
                                            override val messagesApi: MessagesApi,
                                            requireData: DataRequiredAction,
                                            getData: DataRetrievalAction,
                                            dataCacheConnector: DataCacheConnector,
                                            navigator: Navigator,
                                            windWatertightEnquiry: wind_watertight_enquiry,
                                            propertyWindWatertight: property_wind_watertight,
                                            cc: MessagesControllerComponents
                                            ) extends FrontendController(cc) with I18nSupport {

  implicit val ec: ExecutionContext = cc.executionContext

  def onEnquiryLoad() = (getData andThen requireData) { implicit request =>
    val preparedForm = request.userAnswers.propertyWindEnquiry match {
      case None => PropertyWindWaterForm()
      case Some(value) => PropertyWindWaterForm().fill(value)
    }

    Ok(windWatertightEnquiry(appConfig, preparedForm, NormalMode))
  }

  def onSubmit = getData.async { implicit request =>
    PropertyWindWaterForm().bindFromRequest().fold(
      formWithErrors =>Future.successful(BadRequest(windWatertightEnquiry(appConfig, formWithErrors, NormalMode))),
      value => {
        dataCacheConnector.save[String](request.sessionId, CouncilTaxPropertyPoorRepairId.toString, value).map(cacheMap =>
          Redirect(navigator.nextPage(CouncilTaxPropertyPoorRepairId, NormalMode)(new UserAnswers(cacheMap))))
      }
    )
  }

  def onPageLoad: Action[AnyContent] = Action { implicit request =>
    Ok(propertyWindWatertight(appConfig))
  }
}
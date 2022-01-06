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

import play.api.Logger
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.{PropertyWalesLets140DaysForm, PropertyWalesLets70DaysForm}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers.PropertyWalesLets140DaysId
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.Mode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.UserAnswers
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{propertyWalesLets140Days => property_wales_lets_140_days}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{propertyWalesLetsNoAction => property_wales_lets_no_action}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.{FrontendAppConfig, Navigator}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PropertyWalesLets140DaysController @Inject()(
                                                     appConfig: FrontendAppConfig,
                                                     override val messagesApi: MessagesApi,
                                                     dataCacheConnector: DataCacheConnector,
                                                     navigator: Navigator,
                                                     getData: DataRetrievalAction,
                                                     requireData: DataRequiredAction,
                                                     propertyWalesLets140Days: property_wales_lets_140_days,
                                                     propertyWalesLetsNoAction: property_wales_lets_no_action,
                                                     cc: MessagesControllerComponents
                                                   ) extends FrontendController(cc) with I18nSupport {

  private val log = Logger(this.getClass)

  implicit val ec: ExecutionContext = cc.executionContext

  def onPageLoad(mode: Mode): Action[AnyContent] = (getData andThen requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.propertyWalesLets140DaysEnquiry match {
        case None => PropertyWalesLets140DaysForm()
        case Some(value) => PropertyWalesLets140DaysForm().fill(value)
      }
      Ok(propertyWalesLets140Days(appConfig, preparedForm, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (getData andThen requireData).async {
    implicit request =>
      PropertyWalesLets70DaysForm().bindFromRequest().fold(
        (formWithErrors: Form[String]) =>
          Future.successful(BadRequest(propertyWalesLets140Days(appConfig, formWithErrors, mode))),
        (value) =>
          dataCacheConnector.save[String](request.sessionId, PropertyWalesLets140DaysId.toString, value).map(cacheMap =>
            Redirect(navigator.nextPage(PropertyWalesLets140DaysId, mode)(new UserAnswers(cacheMap))))
      )
  }

  def onWalLetsNoActionPageLoad(mode: Mode) = (getData andThen requireData) {
    implicit request =>
      enquiryBackLink(request.userAnswers) match {
        case Right(link) => Ok(propertyWalesLetsNoAction(appConfig, link))
        case Left(msg) => {
          log.warn(s"Navigation for Wales No Action page reached with error $msg")
          throw new RuntimeException(s"Navigation for Wales No Action page reached with error $msg")
        }
      }

  }

  private[controllers] def enquiryBackLink(answers: UserAnswers): Either[String, String] = {
    (answers.contactReason,
      answers.enquiryCategory,
      answers.businessRatesSubcategory,
      answers.businessRatesSelfCateringEnquiry,
      answers.propertyWalesLets140DaysEnquiry,
      answers.propertyWalesLets70DaysEnquiry) match {
      case (_, Some("business_rates"), Some("business_rates_self_catering"), Some("wales"), Some("yes"), Some("no")) =>
        Right(routes.PropertyWalesLets70DaysController.onPageLoad().url)
      case (_, Some("business_rates"), Some("business_rates_self_catering"), Some("wales"), Some("no"), _) =>
        Right(routes.PropertyWalesLets140DaysController.onPageLoad().url)
      case _ => Left(s"Unknown enquiry category in enquiry key")
    }
  }

}

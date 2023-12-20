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

import play.api.Logger
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors.{AuditingService, DataCacheConnector}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.PropertyWalesAvailableLetsForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers.PropertyWalesAvailableLetsId
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.Mode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.UserAnswers
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{propertyWalesAvailableLets => property_wales_available_lets, propertyWalesLetsNoAction => property_wales_lets_no_action}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.{FrontendAppConfig, Navigator}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PropertyWalesAvailableLetsController @Inject() (
  appConfig: FrontendAppConfig,
  override val messagesApi: MessagesApi,
  auditService: AuditingService,
  dataCacheConnector: DataCacheConnector,
  navigator: Navigator,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  propertyWalesAvailableLets: property_wales_available_lets,
  propertyWalesLetsNoAction: property_wales_lets_no_action,
  cc: MessagesControllerComponents
) extends FrontendController(cc)
  with I18nSupport {

  private val log = Logger(this.getClass)

  implicit val ec: ExecutionContext = cc.executionContext

  def onPageLoad(mode: Mode): Action[AnyContent] = (getData andThen requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.propertyWalesAvailableLetsEnquiry match {
        case None        => PropertyWalesAvailableLetsForm()
        case Some(value) => PropertyWalesAvailableLetsForm().fill(value)
      }
      Ok(propertyWalesAvailableLets(appConfig, preparedForm, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (getData andThen requireData).async {
    implicit request =>
      PropertyWalesAvailableLetsForm().bindFromRequest().fold(
        (formWithErrors: Form[String]) =>
          Future.successful(BadRequest(propertyWalesAvailableLets(appConfig, formWithErrors, mode))),
        value => {
          auditService.sendRadioButtonSelection(request.uri, "businessRatesSelfCatering140DaysCY" -> value)
          dataCacheConnector.save[String](request.sessionId, PropertyWalesAvailableLetsId.toString, value).map(cacheMap =>
            Redirect(navigator.nextPage(PropertyWalesAvailableLetsId, mode).apply(new UserAnswers(cacheMap)))
          )
        }
      )
  }

  def onWalLetsNoActionPageLoad(mode: Mode): Action[AnyContent] = (getData andThen requireData) {
    implicit request =>
      enquiryBackLink(request.userAnswers) match {
        case Right(link) => Ok(propertyWalesLetsNoAction(appConfig, link))
        case Left(msg)   =>
          log.warn(s"Navigation for Wales No Action page reached with error $msg")
          throw new RuntimeException(s"Navigation for Wales No Action page reached with error $msg")
      }

  }

  private[controllers] def enquiryBackLink(answers: UserAnswers): Either[String, String] =
    (
      answers.contactReason,
      answers.enquiryCategory,
      answers.businessRatesSubcategory,
      answers.businessRatesSelfCateringEnquiry,
      answers.propertyWalesAvailableLetsEnquiry,
      answers.propertyWalesActualLetsEnquiry
    ) match {
      case (_, Some("business_rates"), Some("business_rates_self_catering"), Some("wales"), Some("yes"), Some("no")) =>
        Right(routes.PropertyWalesActualLetsController.onPageLoad().url)
      case (_, Some("business_rates"), Some("business_rates_self_catering"), Some("wales"), Some("no"), _)           =>
        Right(routes.PropertyWalesAvailableLetsController.onPageLoad().url)
      case _                                                                                                         => Left("Unknown enquiry category in enquiry key")
    }

}

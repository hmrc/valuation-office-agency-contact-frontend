/*
 * Copyright 2024 HM Revenue & Customs
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
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.MessagesControllerComponents
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.Mode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.UserAnswers
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{propertyEnglandLetsNoAction => property_england_lets_no_action}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.FrontendAppConfig

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext
import play.api.mvc
import play.api.mvc.AnyContent

@Singleton
class PropertyEnglandLetsNoActionController @Inject() (
  val appConfig: FrontendAppConfig,
  override val messagesApi: MessagesApi,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  propertyEnglandLetsNoAction: property_england_lets_no_action,
  cc: MessagesControllerComponents
) extends FrontendController(cc)
  with I18nSupport {

  private val log = Logger(this.getClass)

  implicit val ec: ExecutionContext = cc.executionContext

  def onPageLoad(mode: Mode): mvc.Action[AnyContent] = (getData andThen requireData) {
    implicit request =>
      enquiryBackLink(request.userAnswers) match {
        case Right(link) => Ok(propertyEnglandLetsNoAction(appConfig, link))
        case Left(msg)   =>
          log.warn(s"Navigation for England No Action page reached with error $msg")
          throw new RuntimeException(s"Navigation for England No Action page reached with error $msg")
      }

  }

  private[controllers] def enquiryBackLink(answers: UserAnswers): Either[String, String] =
    (
      answers.contactReason,
      answers.enquiryCategory,
      answers.businessRatesSubcategory,
      answers.businessRatesSelfCateringEnquiry,
      answers.propertyEnglandAvailableLetsEnquiry,
      answers.propertyEnglandActualLetsEnquiry
    ) match {
      case (_, Some("business_rates"), Some("business_rates_self_catering"), Some("england"), Some("yes"), Some("no")) =>
        Right(routes.PropertyEnglandActualLetsController.onPageLoad().url)
      case (_, Some("business_rates"), Some("business_rates_self_catering"), Some("england"), Some("no"), _)           =>
        Right(routes.PropertyEnglandAvailableLetsController.onPageLoad().url)
      case _                                                                                                           =>
        Left("Unknown enquiry category in enquiry key")
    }
}

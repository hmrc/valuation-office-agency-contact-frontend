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

import play.api.Logging
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.vo.contact.frontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.vo.contact.frontend.utils.UserAnswers
import uk.gov.hmrc.vo.contact.frontend.views.html.propertyWalesLetsNoAction

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class PropertyWalesLetsNoActionController @Inject() (
  override val messagesApi: MessagesApi,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  propertyWalesLetsNoAction: propertyWalesLetsNoAction,
  cc: MessagesControllerComponents
) extends FrontendController(cc)
  with I18nSupport
  with Logging:

  given ExecutionContext = cc.executionContext

  def onPageLoad: Action[AnyContent] = (getData andThen requireData) {
    implicit request =>
      enquiryBackLink(request.userAnswers) match
        case Right(link) => Ok(propertyWalesLetsNoAction(link))
        case Left(msg)   =>
          logger.warn(s"Navigation for Wales No Action page reached with error $msg")
          throw RuntimeException(s"Navigation for Wales No Action page reached with error $msg")
  }

  private[controllers] def enquiryBackLink(answers: UserAnswers): Either[String, String] =
    (
      answers.contactReason,
      answers.enquiryCategory,
      answers.businessRatesSubcategory,
      answers.businessRatesSelfCateringEnquiry,
      answers.propertyWalesAvailableLetsEnquiry,
      answers.propertyWalesActualLetsEnquiry
    ) match
      case (_, Some("business_rates"), Some("business_rates_self_catering"), Some("wales"), Some("yes"), Some("no")) =>
        Right(routes.PropertyWalesActualLetsController.onPageLoad.url)
      case (_, Some("business_rates"), Some("business_rates_self_catering"), Some("wales"), Some("no"), _)           =>
        Right(routes.PropertyWalesAvailableLetsController.onPageLoad.url)
      case _                                                                                                         =>
        Left("Unknown enquiry category in enquiry key")

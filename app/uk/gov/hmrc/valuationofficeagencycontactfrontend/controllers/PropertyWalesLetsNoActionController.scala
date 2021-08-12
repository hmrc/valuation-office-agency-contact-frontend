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

import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import uk.gov.hmrc.valuationofficeagencycontactfrontend.FrontendAppConfig
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.{ContactDetailsForm, PropertyWalesLets70DaysForm}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.{Mode, NormalMode}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.UserAnswers
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{propertyWalesLetsNoAction => property_wales_lets_no_action}

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class PropertyWalesLetsNoActionController @Inject()(val appConfig: FrontendAppConfig,
                                                    override val messagesApi: MessagesApi,
                                                    propertyWalesLetsNoAction: property_wales_lets_no_action,
                                                    getData: DataRetrievalAction,
                                                    requireData: DataRequiredAction,
                                                    cc: MessagesControllerComponents
                                            ) extends FrontendController(cc) with I18nSupport {

  implicit val ec: ExecutionContext = cc.executionContext

  def onPageLoad(mode: Mode) = (getData andThen requireData) {
    implicit request =>
      enquiryBackLink(request.userAnswers) match {
        case Right(link) => Ok(propertyWalesLetsNoAction(appConfig, link))
        case Left(msg) => {
          Logger.warn(s"Navigation for Contact Details page reached with error $msg")
          throw new RuntimeException(s"Navigation for Contact Details page reached with error $msg")
        }
      }

  }

  private[controllers] def enquiryBackLink(answers: UserAnswers): Either[String, String] = {
    (answers.contactReason, answers.enquiryCategory, answers.councilTaxSubcategory, answers.businessRatesSubcategory, answers.businessRatesSelfCateringEnquiry, answers.propertyWalesLets140DaysEnquiry, answers.propertyWalesLets70DaysEnquiry) match {
      case (_, Some("business_rates"), _, Some("business_rate_self_catering"),Some("Wales"), Some("yes"), Some("no")) => Right(routes.PropertyWalesLets70DaysController.onPageLoad().url)
      case (_, Some("business_rates"), _, Some("business_rate_self_catering"),Some("Wales"), Some("no"), _) => Right(routes.PropertyWalesLets140DaysController.onPageLoad().url)
      case _ => Left(s"Unknown enquiry category in enquiry key")
    }
  }
}
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

import javax.inject.{Inject, Singleton}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import uk.gov.hmrc.valuationofficeagencycontactfrontend.FrontendAppConfig
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{
  businessRatesChallenge => business_rates_challenge, businessRatesPropertyOrAreaChanged => business_rates_property_or_area_changed}

@Singleton
class BusinessRatesChallengeController @Inject() (override val messagesApi: MessagesApi,
                                                  val appConfig: FrontendAppConfig,
                                                  businessRatesChallenge: business_rates_challenge,
                                                  businessRatesPropertyOrAreaChanged: business_rates_property_or_area_changed,
                                                  cc: MessagesControllerComponents
                                                 ) extends FrontendController(cc) with I18nSupport {


  def onChallengePageLoad: Action[AnyContent] = Action { implicit request =>
    Ok(businessRatesChallenge(appConfig))
  }

  def onAreaChangePageLoad: Action[AnyContent] = Action { implicit  request =>
    Ok(businessRatesPropertyOrAreaChanged(appConfig))
  }

}

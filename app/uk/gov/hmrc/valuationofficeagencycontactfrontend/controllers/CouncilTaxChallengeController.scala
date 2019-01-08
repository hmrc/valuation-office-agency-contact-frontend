/*
 * Copyright 2019 HM Revenue & Customs
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
import play.api.i18n.{I18nSupport, MessagesApi}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import uk.gov.hmrc.valuationofficeagencycontactfrontend.FrontendAppConfig
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.councilTaxChallenge

class CouncilTaxChallengeController @Inject() (appConfig: FrontendAppConfig,
                                                override val messagesApi: MessagesApi,
                                                getData: DataRetrievalAction,
                                                requireData: DataRequiredAction) extends FrontendController with I18nSupport {
  def onPageLoad() = (getData andThen requireData) {
    implicit request =>
      Ok(councilTaxChallenge(appConfig))
  }
}
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

import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc
import play.api.mvc.{AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.vo.contact.frontend.Navigator
import uk.gov.hmrc.vo.contact.frontend.connectors.DataCacheConnector
import uk.gov.hmrc.vo.contact.frontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.vo.contact.frontend.forms.WhatElseForm.form
import uk.gov.hmrc.vo.contact.frontend.identifiers.WhatElseId
import uk.gov.hmrc.vo.contact.frontend.models.Mode
import uk.gov.hmrc.vo.contact.frontend.utils.UserAnswers
import uk.gov.hmrc.vo.contact.frontend.views.html.whatElse

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class WhatElseController @Inject() (
  override val messagesApi: MessagesApi,
  dataCacheConnector: DataCacheConnector,
  navigator: Navigator,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  whatElseView: whatElse,
  cc: MessagesControllerComponents
) extends FrontendController(cc)
  with I18nSupport:

  given ExecutionContext = cc.executionContext

  def onPageLoad: mvc.Action[AnyContent] = (getData andThen requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.whatElse.fold(form)(form.fill)
      Ok(whatElseView(preparedForm))
  }

  def onSubmit(mode: Mode): mvc.Action[AnyContent] = (getData andThen requireData).async {
    implicit request =>
      form.bindFromRequest().fold(
        formWithErrors => BadRequest(whatElseView(formWithErrors)),
        value =>
          dataCacheConnector.save[String](request.sessionId, WhatElseId.toString, value).map(cacheMap =>
            Redirect(navigator.nextPage(WhatElseId, mode).apply(UserAnswers(cacheMap)))
          )
      )
  }

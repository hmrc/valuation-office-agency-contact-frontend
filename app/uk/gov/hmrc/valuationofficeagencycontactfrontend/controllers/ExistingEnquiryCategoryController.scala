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
import uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.ExistingEnquiryCategoryForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers.ExistingEnquiryCategoryId
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.{Mode, NormalMode}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.UserAnswers
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.existingEnquiryCategory
import uk.gov.hmrc.valuationofficeagencycontactfrontend.{FrontendAppConfig, Navigator}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ExistingEnquiryCategoryController @Inject()(
                                                   appConfig: FrontendAppConfig,
                                                   override val messagesApi: MessagesApi,
                                                   dataCacheConnector: DataCacheConnector,
                                                   navigator: Navigator,
                                                   getData: DataRetrievalAction,
                                                   existingEnquiryCategory: existingEnquiryCategory,
                                                   cc: MessagesControllerComponents
                                                 ) extends FrontendController(cc) with I18nSupport {

  implicit val ec: ExecutionContext = cc.executionContext

  def onPageLoad(mode: Mode) = getData { implicit request =>
    val preparedForm = request.userAnswers.flatMap(x => x.existingEnquiryCategory) match {
      case None => ExistingEnquiryCategoryForm()
      case Some(value) => ExistingEnquiryCategoryForm().fill(value)
    }
    Ok(existingEnquiryCategory(appConfig, preparedForm, mode))
  }

  def onSubmit(mode: Mode) = getData.async {
    implicit request =>
      ExistingEnquiryCategoryForm().bindFromRequest().fold(
        (formWithErrors: Form[String]) =>
          Future.successful(BadRequest(existingEnquiryCategory(appConfig, formWithErrors, mode))),
        (value) =>
          dataCacheConnector.save[String](request.sessionId, ExistingEnquiryCategoryId.toString, value).map(cacheMap =>
            Redirect(navigator.nextPage(ExistingEnquiryCategoryId, mode)(new UserAnswers(cacheMap))))
      )
  }

  def redirect: Action[AnyContent] = Action {
    Redirect(routes.EnquiryCategoryController.onPageLoad(NormalMode))
  }
}

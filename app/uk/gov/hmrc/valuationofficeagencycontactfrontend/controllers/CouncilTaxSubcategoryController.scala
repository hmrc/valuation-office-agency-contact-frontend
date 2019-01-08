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
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.{JsResult, JsString, JsSuccess, JsValue}
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.CouncilTaxSubcategoryForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers.{CouncilTaxChallengeId, CouncilTaxSubcategoryId, Identifier}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.Mode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.UserAnswers
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.councilTaxSubcategory

import scala.concurrent.Future
import scala.util.Success

class CouncilTaxSubcategoryController @Inject()(
                                        appConfig: FrontendAppConfig,
                                        override val messagesApi: MessagesApi,
                                        dataCacheConnector: DataCacheConnector,
                                        navigator: Navigator,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction) extends FrontendController with I18nSupport {

  def onPageLoad(mode: Mode) = (getData andThen requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.councilTaxSubcategory match {
        case None => CouncilTaxSubcategoryForm()
        case Some(value) => CouncilTaxSubcategoryForm().fill(value)
      }
      Ok(councilTaxSubcategory(appConfig, preparedForm, mode))
  }

  private def getNextPage(cacheMap: CacheMap): Identifier = {
    cacheMap.data("councilTaxSubcategory").validate[String] match {
      case s: JsSuccess[String] => if(s.get == CouncilTaxChallengeId.toString) {
        CouncilTaxChallengeId
      } else {
        CouncilTaxSubcategoryId
      }
      case _ => CouncilTaxSubcategoryId
    }
  }

  def onSubmit(mode: Mode) = (getData andThen requireData).async {
    implicit request =>
      CouncilTaxSubcategoryForm().bindFromRequest().fold(
        (formWithErrors: Form[String]) =>
          Future.successful(BadRequest(councilTaxSubcategory(appConfig, formWithErrors, mode))),
        (value) =>
          dataCacheConnector.save[String](request.sessionId, CouncilTaxSubcategoryId.toString, value).map(cacheMap =>
            Redirect(navigator.nextPage(getNextPage(cacheMap), mode)(new UserAnswers(cacheMap))))
      )
  }
}

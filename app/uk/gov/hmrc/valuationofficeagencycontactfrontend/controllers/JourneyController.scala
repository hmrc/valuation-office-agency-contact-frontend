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
import play.api.mvc.MessagesControllerComponents
import play.twirl.api.HtmlFormat.Appendable
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.journey.{CategoryRouter, JourneyMap, JourneyPageRequest, Page}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.UserAnswers
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.journey.{categoryRouter, notImplemented}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

/**
 * @author Yuriy Tumakha
 */
class JourneyController @Inject()(journeyMap: JourneyMap,
                                  dataCacheConnector: DataCacheConnector,
                                  getData: DataRetrievalAction,
                                  requireData: DataRequiredAction,
                                  categoryRouterTemplate: categoryRouter,
                                  notImplementedTemplate: notImplemented,
                                  cc: MessagesControllerComponents,
                                  override val messagesApi: MessagesApi
                                 )(implicit ec: ExecutionContext) extends FrontendController(cc) with I18nSupport {

  def onPageLoad(key: String) = getAnswersAndPage(key) { implicit request =>
    implicit val page: Page[String] = request.page
    val value = page.getValue(request.userAnswers).getOrElse("")
    Ok(journeyView(page.form.fill(value), key))
  }

  def onSubmit(key: String) = getAnswersAndPage(key).async { implicit request =>
    implicit val page: Page[String] = request.page
    page.form.bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(journeyView(formWithErrors, key))),
      value =>
        for {
          cacheMap <- dataCacheConnector.save[String](request.sessionId, page.key, value)
        } yield Redirect(page.nextPage(new UserAnswers(cacheMap)))
    )
  }

  private def getAnswersAndPage(key: String) =
    getData andThen requireData andThen journeyMap.getPage(key)

  private def journeyView(form: Form[String], key: String)
                         (implicit request: JourneyPageRequest[_], page: Page[String]): Appendable = {

    val backLinkUrl = page.previousPage(request.userAnswers).url

    page match {
      case categoryRouter: CategoryRouter => categoryRouterTemplate(form, key, backLinkUrl, categoryRouter)
      case _ => notImplementedTemplate(key, backLinkUrl)
    }
  }

}

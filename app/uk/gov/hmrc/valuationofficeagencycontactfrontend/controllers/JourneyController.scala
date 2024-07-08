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

import play.api.data.Form
import play.api.i18n.{I18nSupport, Lang, MessagesApi}
import play.api.mvc.{Call, Flash, MessagesControllerComponents, Result}
import play.twirl.api.HtmlFormat.Appendable
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors.{AuditingService, DataCacheConnector}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.journey.model.{CategoryRouter, CustomizedContent, Page, TellUsMorePage}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.journey.{JourneyMap, JourneyPageRequest}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.UserAnswers
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.journey.{categoryRouter, customizedContent, notImplemented, singleTextarea}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import play.api.mvc
import play.api.mvc.AnyContent

/**
 * @author Yuriy Tumakha
 */
class JourneyController @Inject() (
  journeyMap: JourneyMap,
  auditService: AuditingService,
  dataCacheConnector: DataCacheConnector,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  categoryRouterTemplate: categoryRouter,
  singleTextareaTemplate: singleTextarea,
  customizedContentTemplate: customizedContent,
  notImplementedTemplate: notImplemented,
  cc: MessagesControllerComponents,
  implicit override val messagesApi: MessagesApi
)(implicit ec: ExecutionContext
) extends FrontendController(cc)
  with I18nSupport {

  private val flashWithSwitchIndicator = Flash(Map("switching-language" -> "true"))

  def onPageLoad(key: String): mvc.Action[AnyContent] = getAnswersAndPage(key) { implicit request =>
    implicit val page: Page[String] = request.page
    val value                       = page.getValue(request.userAnswers).getOrElse("")
    Ok(journeyView(page.form.fill(value), key))
  }

  def onSubmit(key: String): mvc.Action[AnyContent] = getAnswersAndPage(key).async { implicit request =>
    implicit val page: Page[String] = request.page
    page.form.bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(journeyView(formWithErrors, key))),
      value =>
        for {
          _        <- page.beforeSaveAnswers(dataCacheConnector, request)
          cacheMap <- dataCacheConnector.save[String](request.sessionId, page.key, value)
        } yield {
          page match {
            case categoryRouter: CategoryRouter =>
              auditService.sendRadioButtonSelection(request.uri, categoryRouter.fieldId -> value)
            case _                              =>
          }
          val userAnswers = new UserAnswers(cacheMap)

          val call = if (request.changeMode) {
            routes.CheckYourAnswersController.onPageLoad()
          } else {
            page.nextPage(userAnswers)
          }

          auditService.sendContinueNextPage(call.url)

          redirectWithLang(call, page.nextLang(userAnswers))
        }
    )
  }

  private def redirectWithLang(call: Call, langOpt: Option[Lang]): Result =
    langOpt match {
      case Some(lang) => Redirect(call).withLang(lang).flashing(flashWithSwitchIndicator)
      case _          => Redirect(call)
    }

  private def getAnswersAndPage(key: String) =
    getData andThen requireData andThen journeyMap.getPage(key)

  private def journeyView(form: Form[String], key: String)(implicit request: JourneyPageRequest[?], page: Page[String]): Appendable = {

    val backLinkUrl = page.previousPage(request.userAnswers).url

    page match {
      case categoryRouter: CategoryRouter   => categoryRouterTemplate(form, key, backLinkUrl, categoryRouter)
      case tellUsMorePage: TellUsMorePage   => singleTextareaTemplate(form, key, backLinkUrl, tellUsMorePage)
      case customContent: CustomizedContent => customizedContentTemplate(key, backLinkUrl, customContent)
      case _                                => notImplementedTemplate(key, backLinkUrl, page)
    }
  }

}

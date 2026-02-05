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

package uk.gov.hmrc.valuationofficeagencycontactfrontend.handlers

import javax.inject.{Inject, Singleton}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Request, RequestHeader}
import play.twirl.api.Html
import uk.gov.hmrc.valuationofficeagencycontactfrontend.FrontendAppConfig
import uk.gov.hmrc.play.bootstrap.frontend.http.FrontendErrorHandler
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.error.error_template
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.error.page_not_found
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.error.internal_server_error

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions

@Singleton
class ErrorHandler @Inject() (
  appConfig: FrontendAppConfig,
  val messagesApi: MessagesApi,
  errorTemplate: error_template,
  pageNotFound: page_not_found,
  internalServerError: internal_server_error
)(implicit val ec: ExecutionContext
) extends FrontendErrorHandler
  with I18nSupport {

  override def standardErrorTemplate(pageTitle: String, heading: String, message: String)(implicit rh: RequestHeader): Future[Html] =
    render { implicit request =>
      errorTemplate(pageTitle, heading, message)
    }

  override def badRequestTemplate(implicit rh: RequestHeader): Future[Html] =
    render { implicit request =>
      pageNotFound(appConfig)
    }

  override def notFoundTemplate(implicit rh: RequestHeader): Future[Html] =
    render { implicit request =>
      pageNotFound(appConfig)
    }

  override def internalServerErrorTemplate(implicit rh: RequestHeader): Future[Html] =
    render { implicit request =>
      internalServerError(appConfig)
    }

  private def render(template: Request[?] => Html)(implicit rh: RequestHeader): Future[Html] =
    Future.successful(template(Request(rh, "")))

}

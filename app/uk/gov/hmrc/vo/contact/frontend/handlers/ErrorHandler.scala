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

package uk.gov.hmrc.vo.contact.frontend.handlers

import javax.inject.{Inject, Singleton}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Request, RequestHeader}
import play.twirl.api.Html
import uk.gov.hmrc.play.bootstrap.frontend.http.FrontendErrorHandler
import uk.gov.hmrc.vo.contact.frontend.views.html.error.{error_template, internal_server_error, page_not_found}

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions

@Singleton
class ErrorHandler @Inject() (
  val messagesApi: MessagesApi,
  errorTemplate: error_template,
  pageNotFound: page_not_found,
  internalServerError: internal_server_error
)(using val ec: ExecutionContext
) extends FrontendErrorHandler
  with I18nSupport {

  override def standardErrorTemplate(pageTitle: String, heading: String, message: String)(using rh: RequestHeader): Future[Html] =
    render { implicit request =>
      errorTemplate(heading, message)
    }

  override def badRequestTemplate(using rh: RequestHeader): Future[Html] =
    render { implicit request =>
      pageNotFound()
    }

  override def notFoundTemplate(using rh: RequestHeader): Future[Html] =
    render { implicit request =>
      pageNotFound()
    }

  override def internalServerErrorTemplate(using rh: RequestHeader): Future[Html] =
    render { implicit request =>
      internalServerError()
    }

  private def render(template: Request[?] => Html)(using rh: RequestHeader): Future[Html] =
    Future.successful(template(Request(rh, "")))

}

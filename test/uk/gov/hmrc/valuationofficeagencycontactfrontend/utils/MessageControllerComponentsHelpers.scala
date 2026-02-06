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

package uk.gov.hmrc.valuationofficeagencycontactfrontend.utils

import play.api.http.FileMimeTypes
import play.api.i18n.{Langs, MessagesApi}
import play.api.mvc.{ActionBuilder, AnyContent, DefaultMessagesActionBuilderImpl, MessagesActionBuilder, MessagesControllerComponents, PlayBodyParsers, Request}
import play.api.test.Helpers

import scala.concurrent.ExecutionContext

object MessageControllerComponentsHelpers {

  def stubMessageControllerComponents: MessagesControllerComponents = {

    val stub = Helpers.stubControllerComponents()

    new MessagesControllerComponents {
      override def messagesActionBuilder: MessagesActionBuilder =
        new DefaultMessagesActionBuilderImpl(stub.parsers.default, stub.messagesApi)(using stub.executionContext)

      override def actionBuilder: ActionBuilder[Request, AnyContent] = stub.actionBuilder

      override def parsers: PlayBodyParsers = stub.parsers

      override def messagesApi: MessagesApi = stub.messagesApi

      override def langs: Langs = stub.langs

      override def fileMimeTypes: FileMimeTypes = stub.fileMimeTypes

      override def executionContext: ExecutionContext = stub.executionContext
    }
  }

}

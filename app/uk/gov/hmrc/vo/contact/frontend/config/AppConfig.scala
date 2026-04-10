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

package uk.gov.hmrc.vo.contact.frontend.config

import play.api.Configuration
import play.api.mvc.{Call, RequestHeader}
import uk.gov.hmrc.vo.contact.frontend.controllers.routes
import uk.gov.hmrc.vo.service.config.VOServiceConfig

import javax.inject.{Inject, Singleton}

@Singleton
class AppConfig @Inject() (val configuration: Configuration) extends VOServiceConfig:

  override def serviceLocalRoot: Call    = routes.Application.start()
  override def serviceMenuHome: Call     = routes.Application.start()
  override def theFirstPage: Call        = routes.ContactReasonController.redirect
  override def signOutCall: Option[Call] = Some(routes.Application.logout())

  override def timeoutCall(using request: RequestHeader): Option[Call] = signOutCall

  override def isWelshTranslationAvailable: Boolean = true

  override def stylesheet: Option[Call] = Some(controllers.routes.Assets.versioned("stylesheets/app.min.css"))

  override def notificationBannerEnabledOn: Set[Call] = Set(
    serviceLocalRoot,
    routes.ContactReasonController.onPageLoad
  )

  override def timeoutDialogEnabledExcept: Set[Call] = Set(
    routes.ContactReasonController.onPageLoad,
    routes.ServiceUnavailableController.show(),
    routes.SessionExpiredController.onPageLoad
  )

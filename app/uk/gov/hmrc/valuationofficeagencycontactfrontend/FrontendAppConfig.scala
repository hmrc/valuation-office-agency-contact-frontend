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

package uk.gov.hmrc.valuationofficeagencycontactfrontend

import com.google.inject.{Inject, Singleton}
import play.api.Configuration
import play.api.i18n.Lang
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes
import play.api.mvc.Call

@Singleton
class FrontendAppConfig @Inject() (val configuration: Configuration) {

  private def loadConfig(key: String) = configuration.getOptional[String](key).getOrElse(throw new Exception(s"Missing configuration key: $key"))

  private lazy val contactHost             = configuration.get[String]("contact-frontend.host")
  private val contactFormServiceIdentifier = "valuationofficeagencycontactfrontend"

  lazy val optimizelyId: String = configuration.getOptional[String]("optimizely.projectId").getOrElse("")

  lazy val analyticsToken: String                 = loadConfig("google-analytics.token")
  lazy val analyticsHost: String                  = loadConfig("google-analytics.host")
  lazy val GTM: String                            = loadConfig("google-analytics.GTM")
  lazy val reportAProblemPartialUrl: String       = s"$contactHost/contact/problem_reports_ajax?service=$contactFormServiceIdentifier"
  lazy val reportAProblemNonJSUrl: String         = s"$contactHost/contact/problem_reports_nonjs?service=$contactFormServiceIdentifier"
  // $COVERAGE-OFF$
  lazy val betaFeedbackUrl: String                = s"$contactHost/contact/beta-feedback"
  lazy val betaFeedbackUnauthenticatedUrl: String = s"$contactHost/contact/beta-feedback-unauthenticated"

  // $COVERAGE-ON$
  lazy val loginUrl: String         = loadConfig("urls.login")
  lazy val loginContinueUrl: String = loadConfig("urls.loginContinue")

  lazy val languageTranslationEnabled: Boolean = configuration.getOptional[Boolean]("microservice.services.features.welsh-translation").getOrElse(true)

  def languageMap: Map[String, Lang]        = Map(
    "english" -> Lang("en"),
    "cymraeg" -> Lang("cy")
  )
  def routeToSwitchLanguage: String => Call = (lang: String) => routes.LanguageSwitchController.switchToLanguage(lang)
}

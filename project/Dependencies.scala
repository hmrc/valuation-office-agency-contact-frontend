import sbt._
import sbt.Keys._

object Dependencies {

    import play.core.PlayVersion
    import play.sbt.PlayImport._

    private val playHealthVersion = "3.16.0-play-26"
    private val logbackJsonLoggerVersion = "5.1.0"
    private val govukTemplateVersion = "5.69.0-play-26"
    private val playUiVersion = "9.6.0-play-26"
    private val scalaTestVersion = "3.0.8"
    private val scalaTestPlusPlayVersion = "3.1.3"
    private val mockitoAllVersion = "1.10.19"
    private val httpCachingClientVersion = "9.5.0-play-26"
    private val simpleReactivemongoVersion = "8.0.0-play-26"
    private val playConditionalFormMappingVersion = "1.9.0-play-26"
    private val playLanguageVersion = "5.1.0-play-26"
    private val bootstrapPlayVersion = "4.0.0"
    private val akkaVersion     = "2.5.25"
    private val httpVerbsVersion = "13.8.0"
    private val playFrontendHmrc = "1.19.0-play-26"
    private val commonTextVersion = "1.9"

    val compile: Seq[ModuleID] = Seq(
       ws,
      "uk.gov.hmrc" %% "simple-reactivemongo" % simpleReactivemongoVersion,
      "uk.gov.hmrc" %% "logback-json-logger" % logbackJsonLoggerVersion,
      "uk.gov.hmrc" %% "govuk-template" % govukTemplateVersion,
      "uk.gov.hmrc" %% "play-health" % playHealthVersion,
      "uk.gov.hmrc" %% "play-ui" % playUiVersion,
      "uk.gov.hmrc" %% "http-caching-client" % httpCachingClientVersion,
      "uk.gov.hmrc" %% "play-conditional-form-mapping" % playConditionalFormMappingVersion,
      "uk.gov.hmrc" %% "bootstrap-play-26" % bootstrapPlayVersion,
      "uk.gov.hmrc" %% "play-language" % playLanguageVersion,
      "uk.gov.hmrc" %% "http-verbs-play-26" % httpVerbsVersion,
      "uk.gov.hmrc" %% "play-frontend-hmrc" % playFrontendHmrc,
      "org.apache.commons" % "commons-text" % commonTextVersion
    )

    trait TestDependencies {
      lazy val scope: String = "test"
      lazy val test: Seq[ModuleID] = ???
    }

    lazy val Test = new TestDependencies {
        override lazy val test = Seq(
            "org.scalatest" %% "scalatest" % scalaTestVersion % scope,
            "org.pegdown" % "pegdown" % "1.4.2" % scope,
            "org.scalatestplus.play" %% "scalatestplus-play" % scalaTestPlusPlayVersion % scope,
            "org.jsoup" % "jsoup" % "1.10.3" % scope,
            "com.typesafe.play" %% "play-test" % PlayVersion.current % scope,
            "org.mockito" % "mockito-all" % mockitoAllVersion % scope,
            "org.scalacheck" %% "scalacheck" % "1.14.0" % scope,
            "com.typesafe.akka" %% "akka-testkit" % akkaVersion % scope
        )
      }.test

    lazy val appDependencies = compile ++ Test
}

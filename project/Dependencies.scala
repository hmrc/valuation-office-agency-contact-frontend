import sbt._
import sbt.Keys._

object Dependencies {

    import play.core.PlayVersion
    import play.sbt.PlayImport._

    private val playHealthVersion = "3.15.0-play-26"
    private val logbackJsonLoggerVersion = "4.8.0"
    private val govukTemplateVersion = "5.57.0-play-26"
    private val playUiVersion = "8.12.0-play-26"
    private val scalaTestVersion = "3.0.8"
    private val scalaTestPlusPlayVersion = "3.1.3"
    private val mockitoAllVersion = "1.10.19"
    private val httpCachingClientVersion = "9.1.0-play-26"
    private val simpleReactivemongoVersion = "7.30.0-play-26"
    private val playConditionalFormMappingVersion = "1.3.0-play-26"
    private val playLanguageVersion = "4.4.0-play-26"
    private val bootstrapPlayVersion = "1.16.0"
    private val akkaVersion     = "2.5.23"
    private val akkaHttpVersion = "10.0.15"
    private val httpVerbsVersion = "10.7.0-play-26"
    private val playFrontendGovUkVersion = "0.51.0-play-26"
    private val playFrontendHmrc = "0.19.0-play-26"
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
      "uk.gov.hmrc" %% "http-verbs" % httpVerbsVersion,
      "uk.gov.hmrc" %% "play-frontend-govuk" % playFrontendGovUkVersion,
      "uk.gov.hmrc" %% "play-frontend-hmrc" % playFrontendHmrc,
      "org.apache.commons" % "commons-text" % commonTextVersion
    )

    lazy val appDependencyOverrides: Seq[ModuleID] = Seq(
      "com.typesafe.akka" %% "akka-stream"    % akkaVersion     force(),
      "com.typesafe.akka" %% "akka-protobuf"  % akkaVersion     force(),
      "com.typesafe.akka" %% "akka-slf4j"     % akkaVersion     force(),
      "com.typesafe.akka" %% "akka-actor"     % akkaVersion     force(),
      "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion force()
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

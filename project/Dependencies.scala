import sbt._
import sbt.Keys._

object Dependencies {

    import play.core.PlayVersion
    import play.sbt.PlayImport._

    private val playHealthVersion = "3.16.0-play-27"
    private val logbackJsonLoggerVersion = "5.1.0"
    private val govukTemplateVersion = "5.72.0-play-28"
    private val playUiVersion = "9.7.0-play-28"
    private val scalaTestVersion = "3.2.9"
    private val scalaTestPlusPlayVersion = "5.1.0"
    private val mockitoAllVersion = "1.10.19"
    private val httpCachingClientVersion = "9.5.0-play-28"
    private val simpleReactivemongoVersion = "8.0.0-play-28"
    private val playConditionalFormMappingVersion = "1.9.0-play-28"
    private val playLanguageVersion = "5.1.0-play-28"
    private val bootstrapPlayVersion = "5.16.0"
    private val akkaVersion     = "2.6.16"
    private val httpVerbsVersion = "13.10.0"
    private val playFrontendHmrc = "1.21.0-play-28"
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
      "uk.gov.hmrc" %% "bootstrap-frontend-play-28" % bootstrapPlayVersion,
      "uk.gov.hmrc" %% "play-language" % playLanguageVersion,
      "uk.gov.hmrc" %% "http-verbs-play-28" % httpVerbsVersion,
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
            "com.vladsch.flexmark" % "flexmark-all" % "0.35.10" % scope,
            "org.pegdown" % "pegdown" % "1.6.0" % scope,
            "org.scalatestplus.play" %% "scalatestplus-play" % scalaTestPlusPlayVersion % scope,
            "org.scalatestplus" %% "mockito-3-4" % "3.2.9.0" % scope,
            "org.scalatestplus" %% "scalacheck-1-15" % "3.2.9.0" % scope,
            "org.jsoup" % "jsoup" % "1.14.3" % scope,
            "com.typesafe.play" %% "play-test" % PlayVersion.current % scope,
            "org.mockito" % "mockito-all" % mockitoAllVersion % scope,
            "org.scalacheck" %% "scalacheck" % "1.15.4" % scope,
            "com.typesafe.akka" %% "akka-testkit" % akkaVersion % scope,
            "com.typesafe.akka" %% "akka-slf4j" % akkaVersion % scope,
            "com.typesafe.akka" %% "akka-protobuf-v3" % akkaVersion % scope,
            "com.typesafe.akka" %% "akka-serialization-jackson" % akkaVersion % scope,
            "com.typesafe.akka" %% "akka-stream" % akkaVersion % scope,
            "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion % scope
        )
      }.test

    lazy val appDependencies = compile ++ Test
}

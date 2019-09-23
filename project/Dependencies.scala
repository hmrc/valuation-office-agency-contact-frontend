import sbt._
import sbt.Keys._

object Dependencies {

    import play.core.PlayVersion
    import play.sbt.PlayImport._

    private val playHealthVersion = "3.14.0-play-25"
    private val logbackJsonLoggerVersion = "4.6.0"
    private val govukTemplateVersion = "5.40.0-play-25"
    private val playUiVersion = "8.1.0-play-25"
    private val hmrcTestVersion = "3.4.0-play-25"
    private val scalaTestVersion = "3.0.8"
    private val scalaTestPlusPlayVersion = "2.0.1"
    private val mockitoAllVersion = "1.10.19"
    private val httpCachingClientVersion = "8.5.0-play-25"
    private val simpleReactivemongoVersion = "7.20.0-play-25"
    private val playConditionalFormMappingVersion = "1.1.0-play-25"
    private val playLanguageVersion = "3.4.0"
    private val bootstrapPlayVersion = "5.0.0"


    val compile: Seq[ModuleID] = Seq(
       ws,
      "uk.gov.hmrc" %% "simple-reactivemongo" % simpleReactivemongoVersion,
      "uk.gov.hmrc" %% "logback-json-logger" % logbackJsonLoggerVersion,
      "uk.gov.hmrc" %% "govuk-template" % govukTemplateVersion,
      "uk.gov.hmrc" %% "play-health" % playHealthVersion,
      "uk.gov.hmrc" %% "play-ui" % playUiVersion,
      "uk.gov.hmrc" %% "http-caching-client" % httpCachingClientVersion,
      "uk.gov.hmrc" %% "play-conditional-form-mapping" % playConditionalFormMappingVersion,
      "uk.gov.hmrc" %% "bootstrap-play-25" % bootstrapPlayVersion,
      "uk.gov.hmrc" %% "play-language" % playLanguageVersion,
      "uk.gov.hmrc" %% "http-verbs" % "10.0.0-play-25"
    )

    lazy val appDependencyOverrides: Set[ModuleID] = Set(
      "uk.gov.hmrc" %% "govuk-template" % govukTemplateVersion
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
            "com.typesafe.akka" %% "akka-testkit" % "2.5.18" % scope
        )
      }.test

    lazy val appDependencies = compile ++ Test
}

import sbt._
import sbt.Keys._

object Dependencies {

    import play.core.PlayVersion
    import play.sbt.PlayImport._

    private val playHealthVersion = "3.9.0-play-25"
    private val logbackJsonLoggerVersion = "4.2.0"
    private val govukTemplateVersion = "5.26.0-play-25"
    private val playUiVersion = "7.31.0-play-25"
    private val hmrcTestVersion = "3.4.0-play-25"
    private val scalaTestVersion = "3.0.4"
    private val scalaTestPlusPlayVersion = "2.0.1"
    private val mockitoAllVersion = "1.10.19"
    private val httpCachingClientVersion = "8.0.0"
    private val playReactivemongoVersion = "6.2.0"
    private val playConditionalFormMappingVersion = "0.2.0"
    private val playLanguageVersion = "3.4.0"
    private val bootstrapPlayVersion = "4.8.0"


    val compile: Seq[ModuleID] = Seq(
       ws,
      "uk.gov.hmrc" %% "play-reactivemongo" % playReactivemongoVersion,
      "uk.gov.hmrc" %% "logback-json-logger" % logbackJsonLoggerVersion,
      "uk.gov.hmrc" %% "govuk-template" % govukTemplateVersion,
      "uk.gov.hmrc" %% "play-health" % playHealthVersion,
      "uk.gov.hmrc" %% "play-ui" % playUiVersion,
      "uk.gov.hmrc" %% "http-caching-client" % httpCachingClientVersion,
      "uk.gov.hmrc" %% "play-conditional-form-mapping" % playConditionalFormMappingVersion,
      "uk.gov.hmrc" %% "bootstrap-play-25" % bootstrapPlayVersion,
      "uk.gov.hmrc" %% "play-language" % playLanguageVersion
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
            "uk.gov.hmrc" %% "hmrctest" % hmrcTestVersion % scope,
            "org.scalatest" %% "scalatest" % scalaTestVersion % scope,
            "org.scalatestplus.play" %% "scalatestplus-play" % scalaTestPlusPlayVersion % scope,
            "org.jsoup" % "jsoup" % "1.10.3" % scope,
            "com.typesafe.play" %% "play-test" % PlayVersion.current % scope,
            "org.mockito" % "mockito-all" % mockitoAllVersion % scope,
            "org.scalacheck" %% "scalacheck" % "1.14.0" % scope
        )
      }.test

    lazy val appDependencies = compile ++ Test
}

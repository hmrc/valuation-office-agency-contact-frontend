import play.core.PlayVersion
import play.sbt.PlayImport._
import sbt._

object Dependencies {

  private val bootstrapPlayVersion = "5.24.0"
  private val playFrontendHmrc = "3.15.0-play-28"
  private val logbackJsonLoggerVersion = "5.2.0"
  private val httpCachingClientVersion = "9.6.0-play-28"
  private val hmrcMongoVersion = "0.63.0"
  private val playConditionalFormMappingVersion = "1.11.0-play-28"
  private val httpVerbsVersion = "13.12.0"
  private val commonTextVersion = "1.9"
  private val scalaTestVersion = "3.2.11"
  private val scalaTestPlusPlayVersion = "5.1.0"
  private val mockitoAllVersion = "1.10.19"

  private val compile = Seq(
    ws,
    "uk.gov.hmrc" %% "bootstrap-frontend-play-28" % bootstrapPlayVersion,
    "uk.gov.hmrc" %% "play-frontend-hmrc" % playFrontendHmrc,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-28" % hmrcMongoVersion,
    "uk.gov.hmrc" %% "logback-json-logger" % logbackJsonLoggerVersion,
    "uk.gov.hmrc" %% "http-caching-client" % httpCachingClientVersion,
    "uk.gov.hmrc" %% "play-conditional-form-mapping" % playConditionalFormMappingVersion,
    "uk.gov.hmrc" %% "http-verbs-play-28" % httpVerbsVersion,
    "org.apache.commons" % "commons-text" % commonTextVersion
  )

  private val test = Seq(
    "org.scalatest" %% "scalatest" % scalaTestVersion % Test,
    "com.vladsch.flexmark" % "flexmark-all" % "0.62.2" % Test, // for scalatest 3.2.x
    "org.scalatestplus.play" %% "scalatestplus-play" % scalaTestPlusPlayVersion % Test,
    "org.scalatestplus" %% "mockito-4-2" % "3.2.11.0" % Test,
    "org.scalatestplus" %% "scalacheck-1-15" % "3.2.11.0" % Test,
    "com.typesafe.play" %% "play-test" % PlayVersion.current % Test,
    "org.mockito" % "mockito-all" % mockitoAllVersion % Test,
    "org.scalacheck" %% "scalacheck" % "1.15.4" % Test,
    "com.typesafe.akka" %% "akka-testkit" % PlayVersion.akkaVersion % Test
  )

  lazy val appDependencies: Seq[ModuleID] = compile ++ test

}

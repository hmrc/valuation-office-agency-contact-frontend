import play.core.PlayVersion
import sbt._

object Dependencies {

  private val bootstrapPlayVersion = "5.24.0"
  private val playFrontendHmrc = "3.17.0-play-28"
  private val hmrcMongoVersion = "0.63.0"
  private val playConditionalFormMappingVersion = "1.11.0-play-28"
  private val httpCachingClientVersion = "9.6.0-play-28"
  private val commonsTextVersion = "1.9"

  // Test dependencies
  private val scalaTestVersion = "3.2.12"
  private val scalaTestPlusCheckVersion = "3.2.12.0"
  private val scalaTestPlusMockitoVersion = "3.2.12.0"
  private val scalaTestPlusPlayVersion = "5.1.0"
  private val scalaCheckVersion = "1.16.0"
  private val mockitoVersion = "1.10.19"
  private val flexmarkVersion = "0.62.2"

  private val compile = Seq(
    "uk.gov.hmrc" %% "bootstrap-frontend-play-28" % bootstrapPlayVersion,
    "uk.gov.hmrc" %% "play-frontend-hmrc" % playFrontendHmrc,
    "uk.gov.hmrc" %% "play-conditional-form-mapping" % playConditionalFormMappingVersion,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-28" % hmrcMongoVersion,
    "uk.gov.hmrc" %% "http-caching-client" % httpCachingClientVersion,
    "org.apache.commons" % "commons-text" % commonsTextVersion
  )

  private val test = Seq(
    "com.typesafe.play" %% "play-test" % PlayVersion.current % Test,
    "com.typesafe.akka" %% "akka-testkit" % PlayVersion.akkaVersion % Test,
    "org.scalatest" %% "scalatest" % scalaTestVersion % Test,
    "org.scalatestplus.play" %% "scalatestplus-play" % scalaTestPlusPlayVersion % Test,
    "org.scalatestplus" %% "mockito-4-5" % scalaTestPlusMockitoVersion % Test,
    "org.scalatestplus" %% "scalacheck-1-16" % scalaTestPlusCheckVersion % Test,
    "org.scalacheck" %% "scalacheck" % scalaCheckVersion % Test,
    "org.mockito" % "mockito-all" % mockitoVersion % Test,
    "com.vladsch.flexmark" % "flexmark-all" % flexmarkVersion % Test // for scalatest 3.2.x
  )

  val appDependencies: Seq[ModuleID] = compile ++ test

}

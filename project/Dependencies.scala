import play.core.PlayVersion
import sbt._

object Dependencies {

  private val bootstrapPlayVersion = "7.15.0"
  private val playFrontendHmrc = "7.3.0-play-28"
  private val hmrcMongoVersion = "1.1.0"
  private val httpCachingClientVersion = "10.0.0-play-28"
  private val commonsTextVersion = "1.10.0"

  // Test dependencies
  private val scalaTestVersion            = "3.2.14"
  private val scalaTestPlusCheckVersion   = "3.2.14.0"
  private val scalaTestPlusMockitoVersion = "3.2.14.0"
  private val scalaTestPlusPlayVersion    = "5.1.0"
  private val flexMarkVersion = "0.64.0"

  private val compile = Seq(
    "uk.gov.hmrc" %% "bootstrap-frontend-play-28" % bootstrapPlayVersion,
    "uk.gov.hmrc" %% "play-frontend-hmrc" % playFrontendHmrc,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-28" % hmrcMongoVersion,
    "uk.gov.hmrc" %% "http-caching-client" % httpCachingClientVersion,
    "org.apache.commons" % "commons-text" % commonsTextVersion
  )

  private val test = Seq(
    "com.typesafe.play" %% "play-test" % PlayVersion.current % Test,
    "com.typesafe.akka" %% "akka-testkit" % PlayVersion.akkaVersion % Test,
    "org.scalatest" %% "scalatest" % scalaTestVersion % Test,
    "org.scalatestplus.play" %% "scalatestplus-play" % scalaTestPlusPlayVersion % Test,
    "org.scalatestplus" %% "mockito-4-6" % scalaTestPlusMockitoVersion % Test,
    "org.scalatestplus" %% "scalacheck-1-17" % scalaTestPlusCheckVersion % Test,
    "com.vladsch.flexmark" % "flexmark-all" % flexMarkVersion % Test // for scalatest 3.2.x
  )

  val appDependencies: Seq[ModuleID] = compile ++ test

}

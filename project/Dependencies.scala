import play.core.PlayVersion
import sbt.*

object Dependencies {

  private val bootstrapPlayVersion = "8.2.0"
  private val playFrontendHmrc = "8.1.0"
  private val hmrcMongoVersion = "1.6.0"
  private val commonsTextVersion = "1.11.0"
  private val httpCachingClientVersion = "11.0.0" // Deprecated - use hmrc-mongo instead

  // Test dependencies
  private val scalaTestVersion            = "3.2.17"
  private val scalaTestPlusCheckVersion   = "3.2.17.0"
  private val scalaTestPlusMockitoVersion = "3.2.17.0"
  private val scalaTestPlusPlayVersion    = "7.0.0"
  private val flexMarkVersion = "0.64.8"

  private val compile = Seq(
    "uk.gov.hmrc"            %% "bootstrap-frontend-play-30"  % bootstrapPlayVersion,
    "uk.gov.hmrc"            %% "play-frontend-hmrc-play-30"  % playFrontendHmrc,
    "uk.gov.hmrc.mongo"      %% "hmrc-mongo-play-30"          % hmrcMongoVersion,
    "uk.gov.hmrc"            %% "http-caching-client-play-29" % httpCachingClientVersion, // Deprecated - use hmrc-mongo instead
    "org.apache.commons"     %  "commons-text"                % commonsTextVersion
  )

  private val test = Seq(
    "org.playframework"      %% "play-test"                   % PlayVersion.current % Test,
    "org.apache.pekko"       %% "pekko-testkit"               % PlayVersion.pekkoVersion % Test,
    "org.scalatest"          %% "scalatest"                   % scalaTestVersion % Test,
    "org.scalatestplus.play" %% "scalatestplus-play"          % scalaTestPlusPlayVersion % Test,
    "org.scalatestplus"      %% "mockito-4-11"                % scalaTestPlusMockitoVersion % Test,
    "org.scalatestplus"      %% "scalacheck-1-17"             % scalaTestPlusCheckVersion % Test,
    "com.vladsch.flexmark"   %  "flexmark-all"                % flexMarkVersion % Test // for scalatest 3.2.x
  )

  val appDependencies: Seq[ModuleID] = compile ++ test

}

import play.core.PlayVersion
import sbt.*

object AppDependencies {

  private val bootstrapPlayVersion = "10.4.0"
  private val playFrontendHmrc     = "12.22.0"
  private val hmrcMongoVersion     = "2.11.0"
  private val commonsTextVersion   = "1.14.0"

  // Test dependencies
  private val scalaTestVersion            = "3.2.19"
  private val scalaTestPlusCheckVersion   = "3.2.19.0"
  private val scalaTestPlusMockitoVersion = "3.2.19.0"
  private val scalaTestPlusPlayVersion    = "7.0.2"
  private val flexMarkVersion             = "0.64.8"

  private val compile = Seq(
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-30" % bootstrapPlayVersion,
    "uk.gov.hmrc"       %% "play-frontend-hmrc-play-30" % playFrontendHmrc,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-30"         % hmrcMongoVersion,
    "org.apache.commons" % "commons-text"               % commonsTextVersion
  )

  private val test = Seq(
    "org.playframework"      %% "play-test"          % PlayVersion.current         % Test,
    "org.apache.pekko"       %% "pekko-testkit"      % PlayVersion.pekkoVersion    % Test,
    "org.scalatest"          %% "scalatest"          % scalaTestVersion            % Test,
    "org.scalatestplus.play" %% "scalatestplus-play" % scalaTestPlusPlayVersion    % Test,
    "org.scalatestplus"      %% "mockito-5-12"       % scalaTestPlusMockitoVersion % Test,
    "org.scalatestplus"      %% "scalacheck-1-18"    % scalaTestPlusCheckVersion   % Test,
    "com.vladsch.flexmark"    % "flexmark-all"       % flexMarkVersion             % Test // for scalatest 3.2.x
  )

  val appDependencies: Seq[ModuleID] = compile ++ test

}

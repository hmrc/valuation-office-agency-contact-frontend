import sbt.*

object AppDependencies {

  private val bootstrapVersion   = "10.6.0"
  private val playFrontendHmrc   = "12.31.0"
  private val hmrcMongoVersion   = "2.12.0"
  private val commonsTextVersion = "1.15.0"

  // Test dependencies
  private val scalaTestPlusCheckVersion   = "3.2.19.0"
  private val scalaTestPlusMockitoVersion = "3.2.19.0"

  private val compile = Seq(
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-30" % bootstrapVersion,
    "uk.gov.hmrc"       %% "play-frontend-hmrc-play-30" % playFrontendHmrc,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-30"         % hmrcMongoVersion,
    "org.apache.commons" % "commons-text"               % commonsTextVersion
  )

  private val test = Seq(
    "uk.gov.hmrc"       %% "bootstrap-test-play-30" % bootstrapVersion            % Test,
    "org.scalatestplus" %% "mockito-5-12"           % scalaTestPlusMockitoVersion % Test,
    "org.scalatestplus" %% "scalacheck-1-18"        % scalaTestPlusCheckVersion   % Test
  )

  val appDependencies: Seq[ModuleID] = compile ++ test

}

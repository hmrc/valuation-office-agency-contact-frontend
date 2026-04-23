import sbt.*

object AppDependencies {

  private val bootstrapVersion   = "10.7.0"
  private val playFrontendHmrc   = "13.4.0"
  private val voServiceVersion   = "0.10.0"
  private val hmrcMongoVersion   = "2.12.0"
  private val commonsTextVersion = "1.15.0"

  // Test dependencies
  private val voTestVersion = "0.5.0"

  private val compile = Seq(
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-30" % bootstrapVersion,
    "uk.gov.hmrc"       %% "play-frontend-hmrc-play-30" % playFrontendHmrc,
    "uk.gov.hmrc"       %% "vo-frontend-service"        % voServiceVersion,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-30"         % hmrcMongoVersion,
    "org.apache.commons" % "commons-text"               % commonsTextVersion
  )

  private val test = Seq(
    "uk.gov.hmrc" %% "bootstrap-test-play-30" % bootstrapVersion % Test,
    "uk.gov.hmrc" %% "vo-unit-test"           % voTestVersion    % Test
  )

  val appDependencies: Seq[ModuleID] = compile ++ test

}

import sbt.*
import scoverage.ScoverageKeys
import com.typesafe.sbt.web.Import.*
import com.typesafe.sbt.digest.Import.*
import play.sbt.routes.RoutesKeys
import uk.gov.hmrc.*
import DefaultBuildSettings.*
import org.irundaia.sbt.sass.Minified
import uk.gov.hmrc.SbtAutoBuildPlugin
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin
import uk.gov.hmrc.versioning.SbtGitVersioning

val appName = "valuation-office-agency-contact-frontend"

ThisBuild / libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always // Resolves versions conflict

lazy val root = Project(appName, file("."))
  .enablePlugins(PlayScala, SbtAutoBuildPlugin, SbtGitVersioning, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(majorVersion := 1)
  .settings(RoutesKeys.routesImport ++= Seq("uk.gov.hmrc.valuationofficeagencycontactfrontend.models._"))
  .settings(
    ScoverageKeys.coverageExcludedFiles := "<empty>;Reverse.*;.*filters.*;.*handlers.*;.*components.*;.*models.*;" +
      ".*BuildInfo.*;.*javascript.*;.*FrontendAuditConnector.*;.*Routes.*;.*GuiceInjector;.*DataCacheConnector;" +
      ".*ControllerConfiguration;.*LanguageSwitchController;.*MongoCleanupActor;.*identifiers;.*FrontendAppConfig",
    ScoverageKeys.coverageMinimumStmtTotal := 90,
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting := true,
    PlayKeys.playDefaultPort := 7311
  )
  .settings(scalaSettings)
  .settings(defaultSettings())
  .settings(
    scalacOptions ++= Seq("-feature"),
    libraryDependencies ++= Dependencies.appDependencies,
    retrieveManaged := true,
    Test / fork := true,
    scalaVersion := "2.13.12",
    DefaultBuildSettings.targetJvm := "jvm-11",
    scalacOptions += "-Wconf:src=routes/.*:s",
    scalacOptions += "-Wconf:cat=unused-imports&src=html/.*:s",
    maintainer := "voa.service.optimisation@digital.hmrc.gov.uk"
  )
  .configs(IntegrationTest)
  .settings(integrationTestSettings())
  .settings(
    SassKeys.generateSourceMaps := false,
    SassKeys.cssStyle := Minified
  )
  .settings(
    Assets / pipelineStages := Seq(digest),
    // Include only final files for assets fingerprinting
    digest / includeFilter := GlobFilter("*.min.css")
  )

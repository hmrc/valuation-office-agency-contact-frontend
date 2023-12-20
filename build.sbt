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

ThisBuild / majorVersion := 1
ThisBuild / scalaVersion := "2.13.12"
ThisBuild / scalafixScalaBinaryVersion := "2.13"

lazy val microservice = Project(appName, file("."))
  .enablePlugins(PlayScala, SbtAutoBuildPlugin, SbtGitVersioning, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(RoutesKeys.routesImport ++= Seq("uk.gov.hmrc.valuationofficeagencycontactfrontend.models._"))
  .settings(
    ScoverageKeys.coverageExcludedFiles := "<empty>;Reverse.*;.*filters.*;.*handlers.*;.*components.*;.*models.*;" +
      ".*BuildInfo.*;.*javascript.*;.*FrontendAuditConnector.*;.*Routes.*;.*GuiceInjector;.*DataCacheConnector;" +
      ".*ControllerConfiguration;.*LanguageSwitchController;.*MongoCleanupActor;.*identifiers;.*FrontendAppConfig",
    ScoverageKeys.coverageMinimumStmtTotal := 89,
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting := true,
    PlayKeys.playDefaultPort := 7311
  )
  .settings(scalaSettings)
  .settings(defaultSettings())
  .settings(
    libraryDependencies ++= Dependencies.appDependencies,
    Test / fork := true,
    scalacOptions += "-feature",
    scalacOptions += "-Wconf:src=routes/.*:s",
    scalacOptions += "-Wconf:cat=unused-imports&src=html/.*:s",
    maintainer := "voa.service.optimisation@digital.hmrc.gov.uk"
  )
  .settings(
    SassKeys.generateSourceMaps := false,
    SassKeys.cssStyle := Minified
  )
  .settings(
    Assets / pipelineStages := Seq(digest),
    // Include only final files for assets fingerprinting
    digest / includeFilter := GlobFilter("*.min.css")
  )
  .settings(
    scalafmtFailOnErrors := true,
    Test / test := ((Test / test) dependsOn formatAll).value,
    formatAll := Def
      .sequential(
        scalafmtAll,
        Compile / scalafmtSbt,
        scalafixAll.toTask(""),
        (Compile / scalastyle).toTask("")
      )
      .value
  )
  .settings( // sbt-scalafix
    semanticdbEnabled := true, // enable SemanticDB
    semanticdbVersion := scalafixSemanticdb.revision, // only required for Scala 2.x
    scalacOptions += "-Ywarn-unused" // Scala 2.x only, required by `RemoveUnused`
  )

lazy val formatAll: TaskKey[Unit] = taskKey[Unit]("Run scalafmt for all files")

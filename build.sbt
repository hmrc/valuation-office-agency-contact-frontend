import Dependencies.{Test, _}

lazy val appName = "valuation-office-agency-contact-frontend"

import sbt.Keys._
import sbt.Tests.{Group, SubProcess}
import sbt._
import scoverage.ScoverageKeys
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin._
import com.typesafe.sbt.web.Import._
import net.ground5hark.sbt.concat.Import._
import com.typesafe.sbt.uglify.Import._
import com.typesafe.sbt.digest.Import._
import play.sbt.routes.RoutesKeys
import uk.gov.hmrc._
import DefaultBuildSettings._
import uk.gov.hmrc.SbtAutoBuildPlugin
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin
import uk.gov.hmrc.versioning.SbtGitVersioning

lazy val plugins : Seq[Plugins] = Seq.empty
lazy val playSettings : Seq[Setting[_]] = Seq.empty

lazy val root = Project(appName, file("."))
  .enablePlugins(Seq(play.sbt.PlayScala, SbtAutoBuildPlugin, SbtGitVersioning, SbtDistributablesPlugin) ++ plugins : _*)
  .settings(playSettings : _*)
  .settings(RoutesKeys.routesImport ++= Seq("uk.gov.hmrc.valuationofficeagencycontactfrontend.models._"))
  .settings(

    ScoverageKeys.coverageExcludedFiles := "<empty>;Reverse.*;.*filters.*;.*handlers.*;.*components.*;.*models.*;" +
      ".*BuildInfo.*;.*javascript.*;.*FrontendAuditConnector.*;.*Routes.*;.*GuiceInjector;.*DataCacheConnector;" +
      ".*ControllerConfiguration;.*LanguageSwitchController",
    ScoverageKeys.coverageMinimum := 90,
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting := true,
    parallelExecution in test := false
  )
  .settings(scalaSettings: _*)
  .settings(publishingSettings: _*)
  .settings(defaultSettings(): _*)
  .settings(
    scalacOptions ++= Seq("-Xfatal-warnings", "-feature"),
    libraryDependencies ++= appDependencies,
    dependencyOverrides ++= appDependencyOverrides,
    retrieveManaged := true,
    evictionWarningOptions in update := EvictionWarningOptions.default.withWarnScalaVersionEviction(false)
  )
  .configs(IntegrationTest)
  .settings(inConfig(IntegrationTest)(Defaults.itSettings): _*)
  .settings(
    Keys.fork in IntegrationTest := false,
    unmanagedSourceDirectories in IntegrationTest := (baseDirectory in IntegrationTest)(base => Seq(base / "it")).value,
    addTestReportOption(IntegrationTest, "int-test-reports"),
    testGrouping in IntegrationTest := oneForkedJvmPerTest((definedTests in IntegrationTest).value),
    parallelExecution in IntegrationTest := false)
  .settings(resolvers ++= Seq(
    Resolver.bintrayRepo("hmrc", "releases"),
    Resolver.jcenterRepo,
    Resolver.bintrayRepo("emueller", "maven")
  ))
  .settings(
    // concatenate js
    Concat.groups := Seq(
      "javascripts/valuationofficeagencycontactfrontend-app.js" -> group(Seq("javascripts/show-hide-content.js", "javascripts/valuationofficeagencycontactfrontend.js"))
    ),
    // prevent removal of unused code which generates warning errors due to use of third-party libs
    UglifyKeys.compressOptions := Seq("unused=false", "dead_code=false"),
    pipelineStages := Seq(digest),
    // below line required to force asset pipeline to operate in dev rather than only prod
    pipelineStages in Assets := Seq(concat,uglify),
    // only compress files generated by concat
    includeFilter in uglify := GlobFilter("valuationofficeagencycontactfrontend-*.js")
  )

def oneForkedJvmPerTest(tests: Seq[TestDefinition]) =
  tests map {
    test => new Group(test.name, Seq(test), SubProcess(ForkOptions(runJVMOptions = Seq("-Dtest.name=" + test.name))))
  }
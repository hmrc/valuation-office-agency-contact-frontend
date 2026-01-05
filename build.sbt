import org.irundaia.sbt.sass.Minified
import play.sbt.routes.RoutesKeys
import sbt.*
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin

val appName = "valuation-office-agency-contact-frontend"

ThisBuild / scalaVersion := "3.7.4"
ThisBuild / majorVersion := 1

lazy val microservice = Project(appName, file("."))
  .enablePlugins(PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(RoutesKeys.routesImport ++= Seq("uk.gov.hmrc.valuationofficeagencycontactfrontend.models._"))
  .settings(
    PlayKeys.playDefaultPort := 7311,
    libraryDependencies ++= AppDependencies.appDependencies,
    Test / fork := true,
    maintainer := "voa.service.optimisation@digital.hmrc.gov.uk",
    scalacOptions += "-Wconf:src=routes/.*:s",
    scalacOptions += "-Wconf:msg=Flag .* set repeatedly:s",
    scalacOptions += "-Wconf:msg=Implicit parameters should be provided with a \\`using\\` clause&src=views/.*:s",
    scalacOptions += "-feature",
    javaOptions += "-XX:+EnableDynamicAgentLoading"
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

addCommandAlias("precommit", "scalafmtSbt;scalafmtAll;coverage;test;coverageReport")

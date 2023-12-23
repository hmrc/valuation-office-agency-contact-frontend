import com.typesafe.sbt.digest.Import.*
import com.typesafe.sbt.web.Import.*
import org.irundaia.sbt.sass.Minified
import play.sbt.routes.RoutesKeys
import sbt.*
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin

val appName = "valuation-office-agency-contact-frontend"

ThisBuild / libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always // Resolves versions conflict

ThisBuild / majorVersion := 1
ThisBuild / scalaVersion := "2.13.12"
ThisBuild / scalafixScalaBinaryVersion := "2.13"

lazy val microservice = Project(appName, file("."))
  .enablePlugins(PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(RoutesKeys.routesImport ++= Seq("uk.gov.hmrc.valuationofficeagencycontactfrontend.models._"))
  .settings(
    PlayKeys.playDefaultPort := 7311,
    libraryDependencies ++= AppDependencies.appDependencies,
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

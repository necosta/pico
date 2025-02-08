import Dependencies.*

val scala3Version = "3.6.3"

lazy val root = project
  .in(file("."))
  .settings(
    name         := "pico",
    organization := "com.necosta",
    scalaVersion := scala3Version,
    libraryDependencies ++= libs ++ testLibs,
    Compile / run / fork := true,
    // Code coverage validations
    coverageFailOnMinimum      := true,
    coverageMinimumStmtTotal   := 70,
    coverageMinimumBranchTotal := 70
  )

lazy val root = (project in file("."))
  .settings(
    organization := "com.necosta",
    name := "pico",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.13.6",
    //libraryDependencies ++= libs ++ testLibs,
    addCompilerPlugin("org.typelevel" %% "kind-projector"     % "0.13.0" cross CrossVersion.full)
  )

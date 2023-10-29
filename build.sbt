val paradiseVersion = "2.1.1"
val scalaVer = "2.13.10"

ThisBuild / scalaVersion := scalaVer

lazy val macroAnnotationSettings = Seq(
  scalacOptions ++= (CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, v)) if v >= 13 => Seq("-Ymacro-annotations") // for Scala 2.13
    case _ => Nil
  }),
  libraryDependencies ++= (CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, v)) if v <= 12 => // for Scala 2.12
      Seq(compilerPlugin("org.scalamacros" % "paradise" % paradiseVersion cross CrossVersion.full))
    case _ => Nil
  })
)

lazy val root = project
  .settings(
    publishArtifact := false,
    addCommandAlias("run", "core/run")
  )
  .aggregate(macros, core)
  .dependsOn(core)

lazy val core = project
  .settings(
    macroAnnotationSettings,
    Compile / mainClass := Some("dev.celestica.Main"),
    scalacOptions ++= Seq(
      "-Ymacro-debug-lite", // optional, convenient to see how macros are expanded
    )
  )
  .dependsOn(macros)

lazy val macros = project
  .settings(
    organization := "dev.celestica",
    name := "extend-with-macro",
    version := "0.1.0",
    macroAnnotationSettings,
    libraryDependencies ++= Seq(
      scalaOrganization.value % "scala-reflect" % scalaVersion.value, // necessary for macros
    ),
  )

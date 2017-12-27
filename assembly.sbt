import sbtassembly.MergeStrategy

assemblyMergeStrategy in assembly := { // this is the default plus one more for mime.types
  // See https://github.com/sbt/sbt-assembly#merge-strategy
  case x if Assembly.isConfigFile(x) =>
    MergeStrategy.concat

  case PathList(ps @ _*) if Assembly.isReadme(ps.last) || Assembly.isLicenseFile(ps.last) =>
    MergeStrategy.rename

  case PathList(ps @ _*) if ps.last.startsWith("CHANGELOG.") =>
    MergeStrategy.discard

  case PathList("META-INF", xs @ _*) =>
    xs map {_.toLowerCase} match {
      case "mime.types" :: _ =>
        MergeStrategy.filterDistinctLines

      case ("manifest.mf" :: Nil) | ("index.list" :: Nil) | ("dependencies" :: Nil) =>
        MergeStrategy.discard

      case ps @ (x :: _) if ps.last.endsWith(".sf") || ps.last.endsWith(".dsa") =>
        MergeStrategy.discard

      case "plexus" :: _ =>
        MergeStrategy.discard

      case "services" :: _ =>
        MergeStrategy.filterDistinctLines

      case ("spring.schemas" :: Nil) | ("spring.handlers" :: Nil) =>
        MergeStrategy.filterDistinctLines

      case _ => MergeStrategy.deduplicate
    }

  case _ => MergeStrategy.deduplicate
}

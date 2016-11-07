name := "dist-criteo"

version := "1.0"

libraryDependencies  ++= Seq(
  // other dependencies here
  "org.scalanlp" %% "breeze" % "0.12",
  // native libraries are not included by default. add this if you want them (as of 0.7)
  // native libraries greatly improve performance, but increase jar sizes.
  // It also packages various blas implementations, which have licenses that may or may not
  // be compatible with the Apache License. No GPL code, as best I know.
  "org.scalanlp" %% "breeze-natives" % "0.12",
  // the visualization library is distributed separately as well.
  // It depends on LGPL code.
  "org.scalanlp" %% "breeze-viz" % "0.12"
)

resolvers ++= Seq(
  // other resolvers here
  // if you want to use snapshot builds (currently 0.12-SNAPSHOT), use this.
  "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
  "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/"
)

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "com.typesafe.akka" % "akka-actor_2.11" % "2.4.12",
  "com.typesafe.akka" % "akka-agent_2.11" % "2.4.12",
  "com.typesafe.akka" % "akka-camel_2.11" % "2.4.12",
  "com.typesafe.akka" % "akka-cluster_2.11" % "2.4.12",
  "com.typesafe.akka" % "akka-cluster-metrics_2.11" % "2.4.12",
  "com.typesafe.akka" % "akka-cluster-sharding_2.11" % "2.4.12",
  "com.typesafe.akka" % "akka-cluster-tools_2.11" % "2.4.12",
  "com.typesafe.akka" % "akka-contrib_2.11" % "2.4.12",
  "com.typesafe.akka" % "akka-http-testkit_2.11" % "2.4.11",
  "com.typesafe.akka" % "akka-multi-node-testkit_2.11" % "2.4.12",
  "com.typesafe.akka" % "akka-osgi_2.11" % "2.4.12",
  "com.typesafe.akka" % "akka-persistence_2.11" % "2.4.12",
  "com.typesafe.akka" % "akka-persistence-tck_2.11" % "2.4.12",
  "com.typesafe.akka" % "akka-remote_2.11" % "2.4.12",
  "com.typesafe.akka" % "akka-slf4j_2.11" % "2.4.12",
  "com.typesafe.akka" % "akka-stream_2.11" % "2.4.12",
  "com.typesafe.akka" % "akka-stream-testkit_2.11" % "2.4.12",
  "com.typesafe.akka" % "akka-testkit_2.11" % "2.4.12",
  "com.typesafe.akka" % "akka-distributed-data-experimental_2.11" % "2.4.12",
  "com.typesafe.akka" % "akka-typed-experimental_2.11" % "2.4.12",
  "com.typesafe.akka" % "akka-persistence-query-experimental_2.11" % "2.4.12"
)

scalaVersion := "2.11.8"
    
name := "video-spark-jobs"
version := "1.0"
scalaVersion := "2.12.15" 

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "3.1.3" % "provided",
  "org.apache.spark" %% "spark-streaming" % "3.1.3" % "provided",
  "org.apache.spark" %% "spark-streaming-kafka-0-10" % "3.1.3",
  "org.apache.kafka" % "kafka-clients" % "3.2.0",
  "redis.clients" % "jedis" % "3.7.0",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.12.3"
)

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}

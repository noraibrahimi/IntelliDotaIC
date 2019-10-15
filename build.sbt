name := "IntelliDotaIC"

version := "0.1"

scalaVersion := "2.12.9"

libraryDependencies ++= Seq(
	"org.apache.spark" %% "spark-core" % "2.4.4",
	"org.apache.spark" %% "spark-sql" % "2.4.4",
	"com.lihaoyi" %% "requests" % "0.1.8",
	"com.google.code.gson" % "gson" % "2.8.5",
	"org.apache.spark" %% "spark-mllib" % "2.4.4"
)
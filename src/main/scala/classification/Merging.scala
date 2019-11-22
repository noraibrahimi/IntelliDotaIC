package classification

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession

object Merging {
	def main(args: Array[String]) = {


		Logger.getLogger("org").setLevel(Level.OFF)
		Logger.getLogger("akka").setLevel(Level.OFF)

		val spark = SparkSession
			.builder
			.appName("T")
			.master("local[*]")
			.getOrCreate

		val p1 = spark.read.option("header", true).csv("C:/Users/Labinot/Desktop/main_route/fetched_steam_data_1")
		val p2 = spark.read.option("header", true).csv("C:/Users/Labinot/Desktop/main_route/fetched_steam_data_2")
		val p3 = spark.read.option("header", true).csv("C:/Users/Labinot/Desktop/main_route/fetched_steam_data_3")
		val p4 = spark.read.option("header", true).csv("C:/Users/Labinot/Desktop/main_route/fetched_steam_data_4")
		val p5 = spark.read.option("header", true).csv("C:/Users/Labinot/Desktop/main_route/fetched_steam_data_5")
		val p6 = spark.read.option("header", true).csv("C:/Users/Labinot/Desktop/main_route/fetched_steam_data_6")

		val main = p1.union(p2).union(p3).union(p4).union(p5).union(p6)

		main
			.coalesce(1)
			.write.format("csv")
			.option("header", true)
			.mode("overwrite")
			.save(System.getenv("fetched_steam_data"))

	}
}

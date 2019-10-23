package clustering

import helper.Constants
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.col

object Cleaner {
	def main(args: Array[String]) = {
		Logger.getLogger("org").setLevel(Level.OFF)
		Logger.getLogger("akka").setLevel(Level.OFF)

		val spark = SparkSession.builder.appName("T").master("local[*]").getOrCreate

		var players = spark.read
			.option("header", true)
			.option("inferSchema", true)
			.csv(Constants.MAIN_ROUTE + Constants.RAW_KAGGLE_DATA)

		players = players
			.select("hero_id",
				"gold", "gold_per_min", "xp_per_min", "kills", "deaths", "assists", "denies",
				"last_hits", "hero_damage", "hero_healing", "tower_damage", "level")
            .where(col("level") > 0)

		var groupedBy = players.groupBy("hero_id").mean().drop("hero_id", "avg(hero_id)")
		groupedBy = RenameBadNaming(groupedBy)

		groupedBy.write
			.format("csv")
			.option("header", true)
			.mode("overwrite")
			.save(Constants.MAIN_ROUTE + Constants.KAGGLE_DATA)

		println("Successfully exported cleaned version of this file!")
	}
}

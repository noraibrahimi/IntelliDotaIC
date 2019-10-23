package clustering

import helper.Constants
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession

object ToOneDataset {
	def main(args: Array[String]) = {
		Logger.getLogger("org").setLevel(Level.OFF)
		Logger.getLogger("akka").setLevel(Level.OFF)

		val spark = SparkSession.builder.appName("T").master("local[*]").getOrCreate

		val heroNames = spark.read
			.option("header", true)
			.option("inferSchema", true)
			.csv(Constants.MAIN_ROUTE + Constants.HERO_NAMES)
			.withColumnRenamed("hero_id", "hero__id")
		var players = spark.read
			.option("header", true)
			.option("inferSchema", true)
			.csv(Constants.MAIN_ROUTE + Constants.PLAYERS)

		players = players
			.select("hero_id",
				"gold", "gold_per_min", "xp_per_min", "kills", "deaths", "assists", "denies",
				"last_hits", "hero_damage", "hero_healing", "tower_damage", "level")

		var groupedBy = players
			.groupBy("hero_id").mean()
			.join(heroNames, heroNames("hero__id").equalTo(players("hero_id")))
			.drop("hero__id", "name", "avg(hero_id)")
			.sort("localized_name")
		groupedBy = RenameBadNaming(groupedBy)

		groupedBy.write
			.format("csv")
			.option("header", true)
			.mode("overwrite")
			.save(Constants.MAIN_ROUTE + Constants.KAGGLE_DATA)

		println("Successfully merged three tables into one and saved into respective path!")
	}
}

package importer

import helper.Constants
import models.Match
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession

import scala.util.control.Breaks.{break, breakable}

object Importer {
	def main(args: Array[String]) = {

		Logger.getLogger("org").setLevel(Level.OFF)
		Logger.getLogger("akka").setLevel(Level.OFF)

		val spark = SparkSession.builder.appName("T").master("local[*]").getOrCreate
		import spark.implicits._

		var matches = Seq[Match]()

		var foundGames = 0

		breakable {
			for (gameId <- Constants.START_ID to Constants.END_ID) {
				val game = Fetcher.fetchGames(Constants.STEAM_API + gameId + Constants.STEAM_KEY)

				if (game != null) {
					matches = matches :+ game
					foundGames += 1

					println(foundGames + ". Analyzing game: " + Constants.STEAM_API + gameId + Constants.STEAM_KEY)
				}

				if (foundGames == Constants.FEEDS_NUMBER) break
			}
		}

		val gamesDF = matches.toDF

		gamesDF.write.format("csv")
			.option("header", true)
    		.option("numPartitions", 1)
			.mode("overwrite")
			.save(Constants.MAIN_ROUTE + Constants.FETCHED_STEAM_DATA)

		println("Successfully exported to the folder!")
	}
}

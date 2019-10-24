package importer

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

		val START_ID = 5000000000L
		val END_ID = 5999999999L
		val FEEDS = 10

		var matches = Seq[Match]()
		var foundGames = 0

		breakable {
			for (gameId <- START_ID to END_ID) {
				val game = Fetcher.fetchGames(System.getProperty("steam_api") + gameId + System.getProperty("steam_key"), args)

				if (game != null) {
					matches = matches :+ game
					foundGames += 1

					println(foundGames + ". Analyzing game[" + gameId + "]")
				}

				if (foundGames == FEEDS) break
			}
		}

		val gamesDF = matches.toDF

		gamesDF
			.write.format("csv")
			.option("header", true)
			.mode("overwrite")
			.save(System.getenv("fetched_steam_data"))

		println("Successfully exported to the folder!")
	}
}

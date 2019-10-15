package importer

import helper.Globals
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
			for (gameId <- Globals.START_ID to Globals.END_ID) {
				val game = Fetcher.fetchGames(Globals.STEAM_API + gameId + Globals.STEAM_KEY)

				if (game != null) {
					matches = matches :+ game
					foundGames += 1

					println(foundGames + ". Analyzing game: " + Globals.STEAM_API + gameId + Globals.STEAM_KEY)
				}

				if (foundGames == Globals.FEEDS_NUMBER) break
			}
		}

		val gamesDF = matches.toDF

		gamesDF.write.format("csv")
			.option("header", true)
			.mode("overwrite")
			.save(Globals.MAIN_ROUTE + Globals.FETCHED_STEAM_DATA)

		println("Successfully exported to the folder!")
	}
}

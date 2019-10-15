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
			for (gameId <- Globals.startAt to Globals.endAt) {
				val game = Fetcher.fetchGames(Globals.api + gameId + Globals.key)

				if (game != null) {
					matches = matches :+ game
					foundGames += 1

					println(foundGames + ". Analyzing game: " + Globals.api + gameId + Globals.key)
				}

				if (foundGames == Globals.numberOfFeeds) break
			}
		}

		val gamesDF = matches.toDF

		gamesDF.write.format("csv")
			.option("header", true)
			.mode("overwrite")
			.save(Globals.datasetsRoute + "steam_dataset")

		println("Successfully exported to the folder!")
	}
}

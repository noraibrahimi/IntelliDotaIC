package clustering

import helper.Globals
import org.apache.log4j.{Level, Logger}
import org.apache.spark.ml.clustering.KMeans
import org.apache.spark.ml.evaluation.ClusteringEvaluator
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.sql.SparkSession

object Clustering {
	def main(args: Array[String]) = {
		Logger.getLogger("org").setLevel(Level.OFF)
		Logger.getLogger("akka").setLevel(Level.OFF)

		val spark = SparkSession.builder.appName("T").master("local[*]").getOrCreate

		val heroNames = spark.read
    		.option("header", true)
    		.option("inferSchema", true)
    		.csv(Globals.MAIN_ROUTE + Globals.HERO_NAMES)
    		.withColumnRenamed("hero_id", "hero__id")
		var players = spark.read
			.option("header", true)
			.option("inferSchema", true)
			.csv(Globals.MAIN_ROUTE + Globals.PLAYERS)

		val elements = Array(
			"gold", "gold_per_min", "xp_per_min", "kills", "deaths", "assists", "denies", "last_hits", "hero_damage", "hero_healing", "level")

		players = players
			.select("hero_id",
			"gold", "gold_per_min", "xp_per_min", "kills", "deaths", "assists", "denies",
			"last_hits", "hero_damage", "hero_healing", "level")

		var groupedBy = players
			.groupBy("hero_id").mean()
			.join(heroNames, heroNames("hero__id").equalTo(players("hero_id")))
			.drop("hero__id", "name", "avg(hero_id)")
			.sort("localized_name")
		groupedBy = RenameBadNaming(groupedBy)

		val assembler = new VectorAssembler()
			.setInputCols(elements)
			.setOutputCol("features")

		groupedBy = assembler.transform(groupedBy)

		/*
		val numClusters = 6

		val kmeans = new KMeans().setK(numClusters).setSeed(1).setMaxIter(1)
		val model = kmeans.fit(groupedBy)

		val predictions = model.transform(groupedBy)

		val fly = predictions.groupBy("prediction")

		val evaluator = new ClusteringEvaluator()

		val silhouette = evaluator.evaluate(predictions)
		println(s"Silhouette with squared euclidean distance = $silhouette")

		println("Cluster Centers: ")
		model.clusterCenters.foreach(println)
		*/
	}
}

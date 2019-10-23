package clustering

import helper.Constants
import org.apache.log4j.{Level, Logger}
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.clustering.KMeans
import org.apache.spark.ml.evaluation.ClusteringEvaluator
import org.apache.spark.ml.feature.{StandardScaler, VectorAssembler}
import org.apache.spark.sql.SparkSession

object Clustering {
	def main(args: Array[String]) = {

		Logger.getLogger("org").setLevel(Level.OFF)
		Logger.getLogger("akka").setLevel(Level.OFF)

		val spark = SparkSession.builder.appName("T").master("local[*]").getOrCreate

		var groupedBy = spark.read
			.option("header", true)
			.option("inferSchema", true)
			.csv(Constants.MAIN_ROUTE + Constants.KAGGLE_DATA)

		groupedBy = RenameBadNaming(groupedBy)

		val elements = Array(
			"gold", "gold_per_min", "xp_per_min", "kills", "deaths", "assists", "denies", "last_hits", "hero_damage",
			"hero_healing", "tower_damage", "level")

		val assembler = new VectorAssembler()
			.setInputCols(elements)
			.setOutputCol("features")
		val kmeans = new KMeans()
			.setK(6)
		val pipeline = new Pipeline()
			.setStages(Array(assembler, kmeans))

		val model = pipeline.fit(groupedBy)

		model.write.overwrite.save(Constants.MAIN_ROUTE + Constants.CLUSTERED_MODEL)

		println("Model successfully saved into respective path!")
	}
}

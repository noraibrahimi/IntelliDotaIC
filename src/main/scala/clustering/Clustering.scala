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

		val data = spark.read
			.option("header", true)
			.option("inferSchema", true)
			.csv(Constants.MAIN_ROUTE + Constants.KAGGLE_DATA)

		val elements = Array(
			"gold", "gold_per_min", "xp_per_min", "kills", "deaths", "assists", "denies", "last_hits", "hero_damage",
			"hero_healing", "tower_damage", "level")

		val assembler = new VectorAssembler()
			.setInputCols(elements)
			.setOutputCol("featured")
		val scaler = new StandardScaler()
			.setInputCol("featured")
			.setOutputCol("features")
			.setWithStd(true)
			.setWithMean(true)
		val kmeans = new KMeans()
			.setK(4)
			.setSeed(1)
			.setMaxIter(1)
		val pipeline = new Pipeline()
			.setStages(Array(assembler, scaler, kmeans))

		val model = pipeline.fit(data).transform(data)

		model.write
			.mode("overwrite")
			.save(Constants.MAIN_ROUTE + Constants.CLUSTERED_MODEL)

		println("Model successfully saved into respective path!")
	}
}

package clustering

import org.apache.log4j.{Level, Logger}
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.clustering.{BisectingKMeans, KMeans}
import org.apache.spark.ml.feature.{BucketedRandomProjectionLSH, Bucketizer, Imputer, StandardScaler, VectorAssembler, VectorSizeHint}
import org.apache.spark.sql.SparkSession

object Clustering {
	def main(args: Array[String]) = {

		Logger.getLogger("org").setLevel(Level.OFF)
		Logger.getLogger("akka").setLevel(Level.OFF)

		val spark = SparkSession.builder.appName("T").master("local[*]").getOrCreate

		var groupedBy = spark.read
			.option("header", true)
			.option("inferSchema", true)
			.csv(System.getenv("kaggle_data"))

		groupedBy = RenameBadNaming(groupedBy)

		val bucketizer = new Bucketizer()
			.setInputCol("kills")
			.setOutputCol("kills_out")
			.setSplits(Array(Double.NegativeInfinity, 3.0, 6.0, 9.0, 12.0, Double.PositiveInfinity))
		val imputer = new Imputer()
			.setInputCols(Array("hero_damage"))
			.setOutputCols(Array("hero_damage_out"))
			.setStrategy("median") // default mean
		val assembler = new VectorAssembler()
			.setInputCols(args)
			.setOutputCol("pre-features")
		val scaler = new StandardScaler()
			.setInputCol("pre-features")
			.setOutputCol("features")
			.setWithStd(true)
			.setWithMean(false)
		val kmeans = new BisectingKMeans()
			.setK(5)
    		.setMaxIter(25)
    		.setSeed(1)
		val pipeline = new Pipeline()
			.setStages(Array(bucketizer, imputer, assembler, scaler, kmeans))

		val model = pipeline.fit(groupedBy)

		model.write.overwrite.save(System.getenv("clustered_model"))

		println("Model successfully saved into respective path!")
	}
}

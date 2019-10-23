package classification

import helper.Constants
import org.apache.log4j.{Level, Logger}
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.RandomForestClassifier
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.feature.{StandardScaler, VectorAssembler}
import org.apache.spark.sql.SparkSession

object Classification {

	def main(args: Array[String]) = {
		Logger.getLogger("org").setLevel(Level.OFF)
		Logger.getLogger("akka").setLevel(Level.OFF)

		val spark = SparkSession
			.builder
			.appName("T")
			.master("local[*]")
			.getOrCreate

		var dataframe = spark.read
			.option("header", true)
			.option("inferSchema", true)
			.csv(Constants.MAIN_ROUTE + Constants.FETCHED_STEAM_DATA)
		dataframe = dataframe.withColumnRenamed("radiant_win", "label")

		dataframe = OutliersDetection.handleOutliers(dataframe)

		val assembler = new VectorAssembler()
			.setInputCols(Constants.ATTRIBUTES)
			.setOutputCol("non-scaled")
		val scaler = new StandardScaler()
			.setInputCol("non-scaled")
			.setOutputCol("features")
			.setWithStd(true)
			.setWithMean(true)
		val algorithm = new RandomForestClassifier()
			.setLabelCol("label")
			.setFeaturesCol("features")
			.setNumTrees(10)
		val pipeline = new Pipeline()
    		.setStages(Array(assembler, scaler, algorithm))

		val Array(train) = dataframe.randomSplit(Array(0.7, 0.3))

		val model = pipeline.fit(train)
		model.write.overwrite.save(Constants.MAIN_ROUTE + Constants.CLASSIFIED_MODEL)

		println("Model successfully saved into respective path!")
	}
}

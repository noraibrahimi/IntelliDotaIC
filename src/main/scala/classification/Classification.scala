package classification

import org.apache.log4j.{Level, Logger}
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.RandomForestClassifier
import org.apache.spark.ml.feature.{Normalizer, QuantileDiscretizer, VectorAssembler}
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
			.csv(System.getenv("fetched_steam_data"))
		dataframe = dataframe
			.withColumnRenamed("radiant_win", "label")

		dataframe = OutliersDetection.handleOutliers(dataframe)

		val radiant_score = new QuantileDiscretizer()
			.setInputCol("gold_spent")
			.setOutputCol("gold_spent_to_change")
			.setNumBuckets(3) // me 100 diference, 20114 / 201 = 100 (vlera e pare marre prej getSchema)
		val deaths = new QuantileDiscretizer()
			.setInputCol("hero_damage")
			.setOutputCol("hero_damage_to_change")
			.setNumBuckets(3)	// me 100 diference * 0.75, 36123 / 270.
		val assembler = new VectorAssembler()
			.setInputCols(args)
			.setOutputCol("non-scaled")
		val scaler = new Normalizer()
			.setInputCol("non-scaled")
			.setOutputCol("features")
		val algorithm = new RandomForestClassifier()
			.setLabelCol("label")
			.setFeaturesCol("features")
			.setNumTrees(10)
		val pipeline = new Pipeline()
    		.setStages(Array(radiant_score, deaths, assembler, scaler, algorithm))

		val Array(train, test) = dataframe.randomSplit(Array(0.7, 0.3))

		val model = pipeline.fit(train)

		model.write.overwrite.save(System.getenv("classified_model"))
		println("Model successfully saved into respective path!")

		/*
		val predictions = model.transform(test)
		val evaluator = new MulticlassClassificationEvaluator()
			.setLabelCol("label")
			.setPredictionCol("prediction")
			.setMetricName("accuracy")
		val accuracy = evaluator.evaluate(predictions) * 100
		println(s"Random forest classifier accuracy: $accuracy. ")

		 */
	}
}

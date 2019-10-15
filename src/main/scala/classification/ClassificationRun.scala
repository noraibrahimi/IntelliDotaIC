package classification

import org.apache.spark.ml.PipelineModel
import org.apache.spark.sql.SparkSession

object ClassificationRun {

	def predict(model: PipelineModel, seq: Seq[Integer]) = {
		val spark = SparkSession.builder.appName("T").master("local[*]").getOrCreate
		import spark.implicits._

		val newTest = Seq(seq).toDF
			.toDF("gold_per_min", "level", "leaver_status", "xp_per_min", "radiant_score", "gold_spent",
				"deaths", "denies", "hero_damage", "tower_damage", "last_hits", "hero_healing", "duration")

		val predictions = model.transform(newTest)

		predictions
	}
}

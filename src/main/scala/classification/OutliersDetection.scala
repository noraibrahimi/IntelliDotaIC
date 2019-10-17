package classification

import org.apache.spark.sql.DataFrame

object OutliersDetection {
	def handleOutliers(dataframe: DataFrame): DataFrame = {
		var df = dataframe

		dataframe.schema.names.foreach(col => {
			val quantiles = dataframe.stat.approxQuantile(col, Array(0.25, 0.75), 0.0)

			val Q1 = quantiles(0)
			val Q3 = quantiles(1)
			val IQR = Q3 - Q1

			val leftB = Q1 - 1.5 * IQR
			val rightB = Q3 + 1.5 * IQR

			df = dataframe.filter(dataframe.col(col) > leftB && dataframe.col(col) < rightB)
		})

		df
	}
}

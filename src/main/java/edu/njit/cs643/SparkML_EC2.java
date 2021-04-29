
  package edu.njit.cs643;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Stream;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.classification.LogisticRegressionModel;
import org.apache.spark.mllib.classification.LogisticRegressionWithLBFGS;
import org.apache.spark.mllib.evaluation.MulticlassMetrics;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.rdd.RDD;

import scala.Tuple2;

public class SparkML_EC2 {

	public static void main(String[] args) throws IOException {
		//System.setProperty("hadoop.home.dir", "C:\\hadoop");
		SparkML_EC2 sML = new SparkML_EC2();
		JavaSparkContext sc = sML.createSparkContext();
		// Data preparation
		String inputFile = "s3://aws-logs-125764523568-us-east-1/data/TrainingDataset.csv";
		String inputTestFile = "s3://aws-logs-125764523568-us-east-1/data/ValidationDataset.csv";
		JavaRDD<LabeledPoint> parsedData = loadDataFromFileAndDataPreparation(sc, inputFile);
		JavaRDD<LabeledPoint> testData = loadDataFromFileAndDataPreparation(sc, inputTestFile); //***
		LogisticRegressionModel model = sML.modelCreationAndAccuracy(parsedData, testData);

		sc.close();
	}

	public JavaSparkContext createSparkContext() {
		SparkConf conf = new SparkConf().setAppName("Main")
				//.setMaster("local[4]")
				.set("spark.executor.memory", "3g")
				.set("spark.driver.memory", "3g");

		JavaSparkContext sc = new JavaSparkContext(conf);
        return sc;
	}

		

	public LogisticRegressionModel modelCreationAndAccuracy(JavaRDD<LabeledPoint> parsedData, JavaRDD<LabeledPoint> testData) {
		
		RDD<LabeledPoint> rdd = parsedData.rdd();
		RDD<LabeledPoint> rddTest = testData.rdd();

		LogisticRegressionModel model = new LogisticRegressionWithLBFGS().setNumClasses(10).run(rdd);
		
		// Model Evaluation
		JavaPairRDD<Object, Object> predictionAndLabels = testData.mapToPair(p -> 
			new Tuple2<>(model.predict(p.features()), p.label()));
		
		MulticlassMetrics metrics = new MulticlassMetrics(predictionAndLabels.rdd());
		double accuracy = metrics.accuracy();
		System.out.println("Model Accuracy on Test Data: " + accuracy);
		return model;
	}
	
	public void modelSaving(LogisticRegressionModel model, JavaSparkContext sc, String modelSavePath) {
	     model.save(sc.sc(), modelSavePath);       
	}
	
	public LogisticRegressionModel loadModel(JavaSparkContext sc, String modelSavePath) {
		 LogisticRegressionModel model = LogisticRegressionModel.load(sc.sc(), modelSavePath);
		 return model;
	}
	/*
	 * public int newDataPrediction(LogisticRegressionModel model,
	 * JavaRDD<LabeledPoint> testData) {
	 * 
	 * Vector newData = Vectors.dense(testData); double prediction =
	 * model.predict(newData);
	 * System.out.println("Prediction label for new data given : " + prediction);
	 * return (int)prediction; }
	 */

	protected static JavaRDD<LabeledPoint> loadDataFromFileAndDataPreparation(JavaSparkContext sc, String inputFile) throws IOException {
		File file = new File(inputFile);
		JavaRDD<String> data = sc.textFile(file.getPath());

		// Removing the header from CSV file
		String header  = data.first(); 
		data = data.filter(line ->  !line.equals(header) );

		return data.
				map(line -> {
					
					String[] split = line.split(";");

					double[] featureValues = Stream.of(split)
							.mapToDouble(e -> Double.parseDouble(e)).toArray();

					if (featureValues.length > 11) {
						double label = featureValues[11];
						featureValues = Arrays.copyOfRange(featureValues, 0, 11);
						return new LabeledPoint(label, Vectors.dense(featureValues));
					}
					return null;
				}).cache();
	}

	

}
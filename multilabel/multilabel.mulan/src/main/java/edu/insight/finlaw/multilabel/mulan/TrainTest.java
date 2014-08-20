package edu.insight.finlaw.multilabel.mulan;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import mulan.classifier.MultiLabelOutput;
import mulan.classifier.transformation.BinaryRelevance;
import mulan.data.InvalidDataFormatException;
import mulan.data.MultiLabelInstances;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.Tag;
import weka.core.Utils;
import weka.core.tokenizers.NGramTokenizer;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;
import edu.insight.finlaw.multilabel.utils.ConfigParameters;
import edu.insight.finlaw.utils.BasicFileTools;

public class TrainTest {

	private static Instances filt = null;	
	private static Instances filtTrain = null;
	private static Instances filtTest = null;

	public static void getTrainTest(Instances instances){		
	//	filt.setRelationName("FiroUKFilt: -C 16");
		float trainPercentage = 80;		
		Random ra = new Random();
		instances.randomize(ra);		
		System.out.println(instances.numInstances());
		int cutoff = (int) ((trainPercentage/100) * instances.numInstances());
		filtTrain = new Instances(instances, 0, cutoff);
		filtTest = new Instances(instances, cutoff, instances.numInstances()-cutoff);
		filtTrain.setRelationName("FiroUKTrain: -C 16");	
		filtTest.setRelationName("FiroUKTest: -C 16");	
		System.out.println(filtTrain.numInstances());
		System.out.println(filtTest.numInstances());
	}

	public static void main(String[] args) {
		String labelXML =  "final/config/firo.xml";
		Instances D_nonFilt = Commons.loadWekaData("final/FiroUKMulFeat.arff");
		Instances D_filt = null;
		//		Remove remove = new Remove();
		//		remove.setAttributeIndices("28");
		//		try {
		//			remove.setInputFormat(D_nonFilt);
		//			D_nonFilt = Filter.useFilter(D_nonFilt, remove);
		//		} catch (Exception e1) {
		//			e1.printStackTrace();
		//		}

		StringToWordVector stringToWordVectorFilter = Commons.getStringToWordVectorFilter();	
		try {
			stringToWordVectorFilter.setInputFormat(D_nonFilt);
			D_filt = Filter.useFilter(D_nonFilt, stringToWordVectorFilter);
		} catch (Exception e1) {
			e1.printStackTrace();
		}			
		getTrainTest(D_filt);

		StringBuffer labels = new StringBuffer();
		StringBuffer results = new StringBuffer();
		StringBuffer noOfInstances = new StringBuffer();

		noOfInstances.append("NoOfInstances\t");
		noOfInstances.append("15\t");
		noOfInstances.append("12\t");
		noOfInstances.append("3\t");
		noOfInstances.append("4\t");
		noOfInstances.append("20\t");
		noOfInstances.append("2\t");
		noOfInstances.append("20\t");
		noOfInstances.append("5\t");
		noOfInstances.append("1\t");
		noOfInstances.append("4\t");
		noOfInstances.append("9\t");
		noOfInstances.append("2\t");
		noOfInstances.append("2\t");		
		noOfInstances.append("27\t");
		noOfInstances.append("2\t");
		noOfInstances.append("1\t");	

		labels.append("Labels" + "\t");

		try {
			MultiLabelInstances mulD_Filt = new MultiLabelInstances(filtTrain, labelXML);
			//RAkEL learner1 = new RAkEL(new LabelPowerset(getTheFirstBinaryClassifier()));
			//RAkEL learner1 = new RAkEL(new LabelPowerset(new J48()));			
			BinaryRelevance learner2 = new BinaryRelevance(Commons.getFirstBinClassifierFromJson());							
			//results.append("F-m: J48 + Rakel + TextRemoved" + "\t");
			results.append("F-m: SVM + Rakel" + "\t");
			learner2.build(mulD_Filt);

			for(Instance filtInstance : filtTest){
				MultiLabelOutput multiLabelOutput = learner2.makePrediction(filtInstance);
				if (multiLabelOutput.hasBipartition()) {
					String bipartion = Arrays.toString(multiLabelOutput.getBipartition());
					System.out.println("Predicted bipartion: " + bipartion);
				}
				if (multiLabelOutput.hasRanking()) {
					String ranking = Arrays.toString(multiLabelOutput.getRanking());
					System.out.println("Predicted ranking: " + ranking);
				}
				if (multiLabelOutput.hasConfidences()) {
					String confidences = Arrays.toString(multiLabelOutput.getConfidences());
					System.out.println("Predicted confidences: " + confidences);
				}
			}
		


			//			System.out.println(crossValidate);
			//			//int i = 0;
			//			System.out.println("Hamming Loss\t" + crossValidate.getMean("Hamming Loss") + "\t(0.0)");
			//			System.out.println("******************");			
			//			System.out.println("Micro-averaged Precision\t" + crossValidate.getMean("Micro-averaged Precision") + "\t(1.0)");
			//			System.out.println("Micro-averaged Recall\t" + crossValidate.getMean("Micro-averaged Recall") + "\t(1.0)");
			//			System.out.println("Micro-averaged F-Measure\t" + crossValidate.getMean("Micro-averaged F-Measure") + "\t(1.0)");
			//			System.out.println("******************");
			//			System.out.println("Macro-averaged Precision\t" + crossValidate.getMean("Macro-averaged Precision") + "\t(1.0)");
			//			System.out.println("Macro-averaged Recall\t" + crossValidate.getMean("Macro-averaged Recall") + "\t(1.0)");
			//			System.out.println("Macro-averaged F-Measure\t" + crossValidate.getMean("Macro-averaged F-Measure") + "\t(1.0)");
			//
			//			for(int labIndex = 0; labIndex<=15; labIndex++){
			//				String[] labelNames = mulD_nonFilt.getLabelNames();
			//				System.out.println("------------------------------------------------------------------------------------------");
			//				labels.append(labelNames[labIndex].replace("_class", "").trim() + "\t");
			//				results.append(crossValidate.getMean("Macro-averaged F-Measure", labIndex) + "\t");		
			//				System.out.println("LabelName:\t" + labelNames[labIndex]);
			//				System.out.println("Macro-averaged Precision\t" + crossValidate.getMean("Macro-averaged Precision", labIndex) + "\t(1.0)");
			//				System.out.println("Macro-averaged Recall\t" + crossValidate.getMean("Macro-averaged Recall", labIndex) + "\t(1.0)");
			//				System.out.println("Macro-averaged F-Measure\t" + crossValidate.getMean("Macro-averaged F-Measure", labIndex) + "\t(1.0)");
			//			}			
			//			System.out.println(labels);
			//			System.out.println(noOfInstances);
			//			System.out.println(results);

		} catch (InvalidDataFormatException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}




//			for(Evaluation seval : crossValidate.getEvaluations()){
//				System.out.println("Evaluation\t" + ++i);
//				List<Measure> measures = seval.getMeasures();
//				for(Measure measure : measures){
//					System.out.println("Measure Name: " + measure.getName());
//					System.out.println("Measure Ideal Value: " + measure.getIdealValue());
//					System.out.println("Measure Value: " + measure.getValue());
//				}
//			}

// eval.evaluate(learner, data, measures)
// learner1.build(d_train);			
//			Evaluation evaluate = eval.evaluate(learner1, d_test, d_train);
//			List<Measure> measures = evaluate.getMeasures();
//			for(Measure measure : measures){
//				System.out.println("Measure Name: " + measure.getName());
//				System.out.println("Measure Ideal Value: " + measure.getIdealValue());
//				System.out.println("Measure Value: " + measure.getValue());
//			}		


package edu.insight.finlaw.multilabel.classification.mulan;

import java.util.Arrays;
import java.util.Random;

import edu.insight.finlaw.utils.Pair;
import mulan.classifier.MultiLabelOutput;
import mulan.classifier.meta.RAkEL;
import mulan.classifier.transformation.BinaryRelevance;
import mulan.classifier.transformation.LabelPowerset;
import mulan.data.InvalidDataFormatException;
import mulan.data.MultiLabelInstances;
import mulan.evaluation.Evaluator;
import mulan.evaluation.MultipleEvaluation;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

public class UKAMLSeqClassifier {

	public static void crossValidation(){
		RAkEL rakel = new RAkEL(new LabelPowerset(new J48()));        
		int seed = 8;    
		String labelXML =  "src/main/resources/load/mulanUKAML.xml";
		//Instances ukamlInstances = Commons.loadWekaData("src/main/resources/grctcData/arff/UKAMLArffP2TagsNormal.arff");
		//Instances ukamlInstances = Commons.loadWekaData("src/main/resources/grctcData/arff/UKAMLArffP2TagsFeatures.arff");
		//Instances ukamlInstances = Commons.loadWekaData("src/main/resources/grctcData/arff/UKAMLArffP2TagsSeqContext.arff");		
		Instances ukamlInstances = Commons.loadWekaData("src/main/resources/grctcData/arff/USUKAMLArffP2TagsFeaturesSeqContext.arff");
																						  

		//String labelXML =  "src/main/resources/load/emotions.xml";
		//Instances ukamlInstances = Commons.loadWekaData("src/main/resources/grctcData/arff/emotions.arff");
		Instances filteredUKAMLInstances = null;
		StringToWordVector stringToWordVectorFilter = Commons.getStringToWordVectorFilter();
		Random random = new Random(seed); 
		try {
			stringToWordVectorFilter.setInputFormat(ukamlInstances);
			filteredUKAMLInstances = Filter.useFilter(ukamlInstances, stringToWordVectorFilter);
			filteredUKAMLInstances.randomize(random);
			MultiLabelInstances multiFilteredUKAMLInstances = new MultiLabelInstances(filteredUKAMLInstances, labelXML);
			//BinaryRelevance learner1 = new BinaryRelevance(Commons.getFirstBinClassifierFromJson());							
			Evaluator eval = new Evaluator();
			int numFolds = 5;		
			MultipleEvaluation crossValidate = eval.crossValidate(rakel, multiFilteredUKAMLInstances, numFolds);	
			System.out.println(crossValidate);
		} catch (Exception e1) {
			e1.printStackTrace();
		} 		
	}	

	public static void trainTest() {
		String labelXML =  "src/main/resources/load/mulanUKAML.xml";		
		Instances ukamlInstances = Commons.loadWekaData("src/main/resources/grctcData/arff/UKAMLArffExtended.arff");
		Instances filteredUKAMLInstances = null;
		StringToWordVector stringToWordVectorFilter = Commons.getStringToWordVectorFilter();	
		try {
			stringToWordVectorFilter.setInputFormat(ukamlInstances);
			filteredUKAMLInstances = Filter.useFilter(ukamlInstances, stringToWordVectorFilter);
		} catch (Exception e1) {
			e1.printStackTrace();
		}			
		Pair<Instances, Instances> pair = Commons.getTrainTest(filteredUKAMLInstances, "FiroUKTrain: -C 16", "FiroUKTest: -C 16", 80.0f);
		Instances filtTrain = pair.getFirst();
		Instances filtTest = pair.getSecond();		
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
		} catch (InvalidDataFormatException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void fullOutput() {
		//		String labelXML =  "final/config/firo.xml";
		String labelXML =  "src/main/resources/load/mulanUKAML.xml";

		Instances D_nonFilt = Commons.loadWekaData("src/main/resources/grctcData/arff/UKAMLArff.arff");
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
			MultiLabelInstances mulD_nonFilt = new MultiLabelInstances(D_filt, labelXML);
			//RAkEL learner1 = new RAkEL(new LabelPowerset(getTheFirstBinaryClassifier()));

			//RAkEL learner1 = new RAkEL(new LabelPowerset(new J48()));			
			BinaryRelevance learner1 = new BinaryRelevance(Commons.getFirstBinClassifierFromJson());							
			//results.append("F-m: J48 + Rakel + TextRemoved" + "\t");
			results.append("F-m: SVM + Rakel" + "\t");
			learner1.build(mulD_nonFilt);
			Evaluator eval = new Evaluator();
			int numFolds = 20;		
			MultipleEvaluation crossValidate = eval.crossValidate(learner1, mulD_nonFilt, numFolds);	

			System.out.println(crossValidate);
			//int i = 0;
			System.out.println("Hamming Loss\t" + crossValidate.getMean("Hamming Loss") + "\t(0.0)");
			System.out.println("******************");			
			System.out.println("Micro-averaged Precision\t" + crossValidate.getMean("Micro-averaged Precision") + "\t(1.0)");
			System.out.println("Micro-averaged Recall\t" + crossValidate.getMean("Micro-averaged Recall") + "\t(1.0)");
			System.out.println("Micro-averaged F-Measure\t" + crossValidate.getMean("Micro-averaged F-Measure") + "\t(1.0)");
			System.out.println("******************");
			System.out.println("Macro-averaged Precision\t" + crossValidate.getMean("Macro-averaged Precision") + "\t(1.0)");
			System.out.println("Macro-averaged Recall\t" + crossValidate.getMean("Macro-averaged Recall") + "\t(1.0)");
			System.out.println("Macro-averaged F-Measure\t" + crossValidate.getMean("Macro-averaged F-Measure") + "\t(1.0)");

			for(int labIndex = 0; labIndex<=15; labIndex++){
				//String[] labelNames;// = mulD_nonFilt.getLabelNames();
				System.out.println("------------------------------------------------------------------------------------------");
				//			labels.append(labelNames[labIndex].replace("_class", "").trim() + "\t");
				//results.append(crossValidate.getMean("Macro-averaged F-Measure", labIndex) + "\t");		
				//			System.out.println("LabelName:\t" + labelNames[labIndex]);
				//				System.out.println("Macro-averaged Precision\t" + crossValidate.getMean("Macro-averaged Precision", labIndex) + "\t(1.0)");
				//				System.out.println("Macro-averaged Recall\t" + crossValidate.getMean("Macro-averaged Recall", labIndex) + "\t(1.0)");
				//				System.out.println("Macro-averaged F-Measure\t" + crossValidate.getMean("Macro-averaged F-Measure", labIndex) + "\t(1.0)");
			}			
			System.out.println(labels);
			System.out.println(noOfInstances);
			System.out.println(results);

		} catch (InvalidDataFormatException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		crossValidation();
		//trainTest();
	}

}

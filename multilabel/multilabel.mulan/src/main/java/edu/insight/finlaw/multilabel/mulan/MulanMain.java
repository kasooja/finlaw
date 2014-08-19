package edu.insight.finlaw.multilabel.mulan;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import mulan.classifier.transformation.BinaryRelevance;
import mulan.data.InvalidDataFormatException;
import mulan.data.MultiLabelInstances;
import mulan.evaluation.Evaluator;
import mulan.evaluation.MultipleEvaluation;
import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.Tag;
import weka.core.Utils;
import weka.core.tokenizers.NGramTokenizer;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;
import edu.insight.finlaw.multilabel.utils.ConfigParameters;
import edu.insight.finlaw.utils.BasicFileTools;

public class MulanMain {


	/**
	 * Loads the dataset from disk.
	 * 
	 * @param file the dataset to load (e.g., "weka/classifiers/data/something.arff")
	 * @throws Exception if loading fails, e.g., file does not exit
	 */
	public static Instances loadData(String filePath){
		File file = new File(filePath);
		BufferedReader reader = BasicFileTools.getBufferedReader(file);
		try {
			return new Instances(reader);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Classifier getTheFirstBinaryClassifier() {
		//ConfigParameters configParameters = new ConfigParameters("final/config/traintest.json");
		ConfigParameters configParameters = new ConfigParameters("src/main/resources/load/traintest.json");
		
		for (String classifierName : configParameters.getListOfClassifiers()) {
			System.out.println(classifierName);
			String[] options = null;
			String[] nameOption = classifierName.split(" -- ");
			if (nameOption.length > 1) 
				options = nameOption[1].split(" ");	
			try {
				Classifier binaryClassifier = (Classifier) Utils.forName(Classifier.class, nameOption[0], options);
				return binaryClassifier;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;		
	}


	public static StringToWordVector getStringToWordVectorFilter() {	
		Tag[] tags = new Tag[3];
		tags[0] = new Tag(0, "");			
		tags[1] = new Tag(1, "");
		tags[2] = new Tag(2, "");		
		SelectedTag selectedTag = new SelectedTag(1, tags);
		//Stemmer stemmer = new SnowballStemmer();
		StringToWordVector stringToWordVector = new StringToWordVector();	
		//stringToWordVector.setStemmer(stemmer);
		stringToWordVector.setWordsToKeep(45);
		stringToWordVector.setNormalizeDocLength(selectedTag);		
		stringToWordVector.setMinTermFreq(4);
		stringToWordVector.setLowerCaseTokens(true);
		NGramTokenizer tok = new NGramTokenizer();
		stringToWordVector.setTokenizer(tok);
		//stringToWordVector.setIDFTransform(true);
		//stringToWordVector.setTFTransform(true);
		//stringToWordVector.setOutputWordCounts(true);
		//stringToWordVector.setUseStoplist(true);
		return stringToWordVector;
	}

	public static void main(String[] args) {
//		String labelXML =  "final/config/firo.xml";
		String labelXML =  "src/main/resources/load/mulanUKAML.xml";

		Instances D_nonFilt = loadData( "src/main/resources/grctcData/arff/UKAMLArff.arff");
		Instances D_filt = null;
		//		Remove remove = new Remove();
		//		remove.setAttributeIndices("28");
		//		try {
		//			remove.setInputFormat(D_nonFilt);
		//			D_nonFilt = Filter.useFilter(D_nonFilt, remove);
		//		} catch (Exception e1) {
		//			e1.printStackTrace();
		//		}

		StringToWordVector stringToWordVectorFilter = getStringToWordVectorFilter();	
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
			BinaryRelevance learner1 = new BinaryRelevance(getTheFirstBinaryClassifier());							
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
				String[] labelNames;// = mulD_nonFilt.getLabelNames();
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

//eval.evaluate(learner, data, measures)
//learner1.build(d_train);			
//			Evaluation evaluate = eval.evaluate(learner1, d_test, d_train);
//			List<Measure> measures = evaluate.getMeasures();
//			for(Measure measure : measures){
//				System.out.println("Measure Name: " + measure.getName());
//				System.out.println("Measure Ideal Value: " + measure.getIdealValue());
//				System.out.println("Measure Value: " + measure.getValue());
//			}		


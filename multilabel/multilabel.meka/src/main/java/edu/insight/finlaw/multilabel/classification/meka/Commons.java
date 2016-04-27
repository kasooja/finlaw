package edu.insight.finlaw.multilabel.classification.meka;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import meka.core.MLUtils;
import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.Utils;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.tokenizers.NGramTokenizer;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.attribute.StringToWordVector;
import edu.insight.finlaw.multilabel.utils.ConfigParameters;
import edu.insight.finlaw.utils.BasicFileTools;
import edu.insight.finlaw.utils.Pair;

public class Commons {

	/**
	 * Loads the dataset from disk.	 
	 @param: file the dataset to load (e.g., "weka/classifiers/data/something.arff")
	 @throws: Exception if loading fails, e.g., file does not exit
	 */
	public static Instances loadWekaData(String filePath){
		File file = new File(filePath);
		BufferedReader reader = BasicFileTools.getBufferedReader(file);
		try {
			return new Instances(reader);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Remove getRemoveFilter(String index) {	
		Remove remove = new Remove();		
		String[] options = new String[2];
		options[0] = "-R";
		options[1] = index;
		try {
			remove.setOptions(options);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return remove;
	}

	public static Instances loadMekaInstances(String arffFilePath) {
		try {
			Instances D = DataSource.read(arffFilePath);
			MLUtils.prepareData(D);
		//	Explorer.prepareData(D); // for until 1.6.2 version of meka, Explorer is a meka based class
			return D;
		} catch(Exception e) {
			System.err.println("");
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}

	public static Instances prepareMekaInstances(Instances D) {
		try {
			//D = DataSource.read(arffFilePath);
			//Explorer.prepareData(D); //Explorer is a meka based class

			return D;
		} catch(Exception e) {
			System.err.println("error");
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}



	public static StringToWordVector getStringToWordVectorFilter() {		
		SelectedTag selectedTag = new SelectedTag(StringToWordVector.FILTER_NORMALIZE_ALL, StringToWordVector.TAGS_FILTER);
		//Stemmer stemmer = new SnowballStemmer();
		StringToWordVector stringToWordVector = new StringToWordVector();	
		//stringToWordVector.setStemmer(stemmer);
		stringToWordVector.setWordsToKeep(500);
		stringToWordVector.setNormalizeDocLength(selectedTag);		
		stringToWordVector.setMinTermFreq(4);
		stringToWordVector.setLowerCaseTokens(true);
		//stringToWordVector.setDoNotOperateOnPerClassBasis(false);
		NGramTokenizer tok = new NGramTokenizer();
		tok.setNGramMinSize(1);		
		tok.setNGramMaxSize(2);		
		stringToWordVector.setTokenizer(tok);		
		//stringToWordVector.setIDFTransform(true);
		//stringToWordVector.setTFTransform(true);
		stringToWordVector.setOutputWordCounts(true);
		//stringToWordVector.setUseStoplist(true);		
		return stringToWordVector;
	}

	public static Classifier getFirstBinClassifierFromJson() {
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

	public static Pair<Instances, Instances> getTrainTest(Instances instances, String trainRelationName,
			String testRelationName, float trainPercentage){		
		Random ra = new Random();
		instances.randomize(ra);		
		System.out.println(instances.numInstances());
		int cutoff = (int) ((trainPercentage/100) * instances.numInstances());
		Instances train = new Instances(instances, 0, cutoff);
		Instances test = new Instances(instances, cutoff, instances.numInstances()-cutoff);
		//train.setRelationName("FiroUKTrain: -C 16");
		train.setRelationName(trainRelationName);		
		//test.setRelationName("FiroUKTest: -C 16");
		test.setRelationName(testRelationName);
		Pair<Instances, Instances> pair = new Pair<Instances, Instances>(train, test);
		return pair;
	}




	//		Remove remove = new Remove();
	//		remove.setAttributeIndices("28");
	//		try {
	//			remove.setInputFormat(D_nonFilt);
	//			D_nonFilt = Filter.useFilter(D_nonFilt, remove);
	//		} catch (Exception e1) {
	//			e1.printStackTrace();
	//		}

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



	//	for(Evaluation seval : crossValidate.getEvaluations()){
	//		System.out.println("Evaluation\t" + ++i);
	//		List<Measure> measures = seval.getMeasures();
	//		for(Measure measure : measures){
	//			System.out.println("Measure Name: " + measure.getName());
	//			System.out.println("Measure Ideal Value: " + measure.getIdealValue());
	//			System.out.println("Measure Value: " + measure.getValue());
	//		}
	//	}

	//eval.evaluate(learner, data, measures)
	//learner1.build(d_train);			
	//	Evaluation evaluate = eval.evaluate(learner1, d_test, d_train);
	//	List<Measure> measures = evaluate.getMeasures();
	//	for(Measure measure : measures){
	//		System.out.println("Measure Name: " + measure.getName());
	//		System.out.println("Measure Ideal Value: " + measure.getIdealValue());
	//		System.out.println("Measure Value: " + measure.getValue());
	//	}		

	//	public static MultilabelClassifier makeECC() {		
	//	BaggingML h = new BaggingML();
	//	CC cc = new CC();
	//	cc.setClassifier(new SMO());
	//	h.setClassifier(cc);
	//	return h;
	//}


}

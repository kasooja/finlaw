package edu.insight.finlaw.multilabel.mulan;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import mulan.classifier.transformation.BinaryRelevance;
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

public class UKAMLClassifier {

	/**
	 * Loads the dataset from disk.
	 * 
	 * @param file the dataset to load (e.g., "weka/classifiers/data/something.arff")
	 * @throws Exception if loading fails, e.g., file does not exit
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

	public static StringToWordVector getStringToWordVectorFilter() {	
		Tag[] tags = new Tag[3];
		tags[0] = new Tag(0, "");			
		tags[1] = new Tag(1, "");
		tags[2] = new Tag(2, "");		
		SelectedTag selectedTag = new SelectedTag(1, tags);
		//Stemmer stemmer = new SnowballStemmer();
		StringToWordVector stringToWordVector = new StringToWordVector();	
		//stringToWordVector.setStemmer(stemmer);
		stringToWordVector.setWordsToKeep(4000);
		stringToWordVector.setNormalizeDocLength(selectedTag);		
		stringToWordVector.setMinTermFreq(4);
		stringToWordVector.setLowerCaseTokens(true);
		stringToWordVector.setDoNotOperateOnPerClassBasis(false);
		NGramTokenizer tok = new NGramTokenizer();
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

	public static void main(String[] args) {
		String labelXML =  "src/main/resources/load/mulanUKAML.xml";
		Instances ukamlInstances = loadWekaData("src/main/resources/grctcData/arff/UKAMLArffExtended.arff");
		Instances filteredUKAMLInstances = null;
		StringToWordVector stringToWordVectorFilter = getStringToWordVectorFilter();	
		try {
			stringToWordVectorFilter.setInputFormat(ukamlInstances);
			filteredUKAMLInstances = Filter.useFilter(ukamlInstances, stringToWordVectorFilter);
			MultiLabelInstances multiFilteredUKAMLInstances = new MultiLabelInstances(filteredUKAMLInstances, labelXML);
			BinaryRelevance learner1 = new BinaryRelevance(getFirstBinClassifierFromJson());							
			learner1.build(multiFilteredUKAMLInstances);
			Evaluator eval = new Evaluator();
			int numFolds = 10;		
			MultipleEvaluation crossValidate = eval.crossValidate(learner1, multiFilteredUKAMLInstances, numFolds);	
			System.out.println(crossValidate);
		} catch (Exception e1) {
			e1.printStackTrace();
		} 		
	}

}

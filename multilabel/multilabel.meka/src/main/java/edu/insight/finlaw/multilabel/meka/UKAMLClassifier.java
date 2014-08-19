package edu.insight.finlaw.multilabel.meka;

import meka.classifiers.multilabel.BR;
import meka.classifiers.multilabel.Evaluation;
import meka.classifiers.multilabel.MultilabelClassifier;
import meka.core.MLEvalUtils;
import meka.core.Result;
import meka.gui.explorer.Explorer;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.Tag;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.tokenizers.NGramTokenizer;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

public class UKAMLClassifier {

	public static Instances loadInstances(String arffFilePath) {
		try {
			Instances D = DataSource.read(arffFilePath);
			Explorer.prepareData(D);
			return D;
		} catch(Exception e) {
			System.err.println("");
			e.printStackTrace();
			System.exit(1);
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
		NGramTokenizer tok = new NGramTokenizer();
		stringToWordVector.setTokenizer(tok);
		//stringToWordVector.setIDFTransform(true);
		//stringToWordVector.setTFTransform(true);
		stringToWordVector.setOutputWordCounts(true);
		//stringToWordVector.setUseStoplist(true);
		return stringToWordVector;
	}

	public static MultilabelClassifier makeMulClassifier() {
		BR h = new BR();
		h.setClassifier(new J48());	
		return h;
	}

	public static void main(String[] args) {
		Instances ukamlInstances = loadInstances("src/main/resources/grctcData/arff/UKAMLArffExtended.arff");
		Instances filteredUKAMLInstances = null;
		StringToWordVector stringToWordVectorFilter = getStringToWordVectorFilter();	
		try {
			stringToWordVectorFilter.setInputFormat(ukamlInstances);
			filteredUKAMLInstances = Filter.useFilter(ukamlInstances, stringToWordVectorFilter);
		} catch (Exception e1) {
			e1.printStackTrace();
		}			
		MultilabelClassifier multiClassifier = makeMulClassifier();		
		try {
			multiClassifier.buildClassifier(filteredUKAMLInstances);
			Result[] cvModel = Evaluation.cvModel(multiClassifier, filteredUKAMLInstances, 20, "PCut1", "6");
			System.out.println(cvModel.length);
			Result averageResults = MLEvalUtils.averageResults(cvModel);
			String avgResultsString = Result.getResultAsString(averageResults);
			System.out.println(averageResults);
			//System.out.println(averageResults.getValue(""));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

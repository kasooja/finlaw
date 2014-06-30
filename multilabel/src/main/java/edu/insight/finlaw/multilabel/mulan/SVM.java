package edu.insight.finlaw.multilabel.mulan;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import weka.classifiers.Classifier;
import weka.core.Attribute;
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

public class SVM {
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		//return new Instances(new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream(file))));
	}

	public static Classifier getTheFirstBinaryClassifier() {
		ConfigParameters configParameters = new ConfigParameters("resources/traintest.json");
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
		Instances binClassifierData = loadData( "final/FiroUKBinaryClassifier.arff");
		binClassifierData.setClassIndex(12);
		//Instances instances = new Instances(binClassifierData);
		//		Remove remove = new Remove();		
		//		remove.setAttributeIndices("28");				
		//		try {
		//			remove.setInputFormat(D_nonFilt);
		//			D_nonFilt = Filter.useFilter(D_nonFilt, remove);
		//		} catch (Exception e1) {
		//			e1.printStackTrace();
		//		}
		//
		StringToWordVector stringToWordVectorFilter = getStringToWordVectorFilter();	
		try {
			stringToWordVectorFilter.setInputFormat(binClassifierData);
			binClassifierData = Filter.useFilter(binClassifierData, stringToWordVectorFilter);			
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		int topClasses = 5;
		Classifier svm = getTheFirstBinaryClassifier();
		try {
			svm.buildClassifier(binClassifierData);
			Instance lastInstance = binClassifierData.lastInstance();
			System.out.println(lastInstance.classIsMissing());
			double[] distributionForInstance = svm.distributionForInstance(binClassifierData.lastInstance());			
			Attribute classAttribute = binClassifierData.classAttribute();
			int index = 0;
			Map<Double, List<String>> classScoreMap = new HashMap<Double, List<String>>();
			for(double score : distributionForInstance){
				if(classScoreMap.get(score) == null)
					classScoreMap.put(score, new ArrayList<String>());
				classScoreMap.get(score).add(classAttribute.value(index));
				System.out.println(classAttribute.value(index) + "\t" +  score);
				index++;
			}			
			Arrays.sort(distributionForInstance);
			int arrayLength = distributionForInstance.length;
			if(topClasses >= arrayLength)
				topClasses = arrayLength;			
			for(int i=arrayLength-1; i>=(arrayLength-topClasses); i--)
				System.out.println(distributionForInstance[i] + "\t" + classScoreMap.get(distributionForInstance[i]));			
		} catch (Exception e) {
			e.printStackTrace();
		}


	}

}

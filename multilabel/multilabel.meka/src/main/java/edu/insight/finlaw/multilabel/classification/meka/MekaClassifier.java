package edu.insight.finlaw.multilabel.classification.meka;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import meka.classifiers.multilabel.BR;
import meka.classifiers.multilabel.NN.AbstractDeepNeuralNet;
import meka.core.MLUtils;
import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.tokenizers.NGramTokenizer;
import weka.filters.Filter;
import weka.filters.MultiFilter;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.attribute.StringToWordVector;
import edu.insight.finlaw.multilabel.instances.meka.AMLGateAnnotationsToFeatureVectorConverter;
import edu.insight.finlaw.multilabel.utils.ConfigParameters;
import edu.insight.finlaw.utils.BasicFileTools;

public class MekaClassifier {


	public static Instances loadInstances(String fn) {
		try {
			Instances D = DataSource.read(fn);
			return D;
		} catch(Exception e) {
			System.err.println("");
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}


	/**
	 * Loads the dataset from disk.
	 * 
	 * @param file the dataset to load (e.g., "weka/classifiers/data/something.arff")
	 * @throws Exception if loading fails, e.g., file does not exit
	 */
	public static Instances loadData(String file){
		try {
			//InputStream resourceAsStream = ClassLoader.getSystemResourceAsStream(file);

			//InputStream resourceAsStream = ClassLoader.class.getResourceAsStream(file);
			BufferedReader br = BasicFileTools.getBufferedReaderFile(file);
			return new Instances(br);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Classifier getTheFirstBinaryClassifier() {
		ConfigParameters configParameters = new ConfigParameters("final/config/traintest.json");
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



	public static BR buildClassifier(Instances D){		
		int C = D.classIndex();
		System.out.println(C);
		//D.setClassIndex(0);
		BR m_Classifier = null;			
		try {
			m_Classifier = new BR();
			m_Classifier.testCapabilities(D);
			m_Classifier.setClassifier(new J48());
			m_Classifier.buildClassifier(D);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return m_Classifier;
	}

	public static void main(String[] args) {		
		String dataPath = "/Users/kartik/git/finlawLuna/multilabel/multilabel.meka/src/main/resources/grctcData/annotatedamllegislation/arff/USUKAMLAll.arff";
		//String dataPath = "/Users/kartik/Downloads/Kartik/Music-train.arff";
		File file = new File(dataPath);
		//boolean exists = file.exists();
		//String extractText = BasicFileTools.extractText(file);
		//System.out.println(extractText);
		//System.out.println(exists);
		Instances instances = loadData(dataPath);

//		Remove removeFilter = Commons.getRemoveFilter("6");	
//		try {
//			removeFilter.setInputFormat(instances);
//			instances = Filter.useFilter(instances, removeFilter);
//		} catch (Exception e1) {
//			e1.printStackTrace();
//		}

		try {
			MLUtils.prepareData(instances);
		} catch (Exception e1) {
			e1.printStackTrace();
		}		

		Instance testInstance = instances.get(5);

		StringToWordVector textStringToWordVectorFilter = Commons.getStringToWordVectorFilter();		
		StringToWordVector posStringToWordVectorFilter = Commons.getStringToWordVectorFilter();
		StringToWordVector frameStringToWordVectorFilter = Commons.getStringToWordVectorFilter();				
		posStringToWordVectorFilter.setWordsToKeep(100);
		frameStringToWordVectorFilter.setWordsToKeep(100);
		textStringToWordVectorFilter.setAttributeIndices("5");
		NGramTokenizer tok = new NGramTokenizer();
		tok.setNGramMinSize(3);
		tok.setNGramMaxSize(3);	
		posStringToWordVectorFilter.setTokenizer(tok);
		posStringToWordVectorFilter.setAttributeNamePrefix("POSTAG_");		
		posStringToWordVectorFilter.setAttributeIndices("7");
		tok = new NGramTokenizer();
		tok.setNGramMinSize(1);
		tok.setNGramMaxSize(1);	
		frameStringToWordVectorFilter.setAttributeIndices("6");		
		frameStringToWordVectorFilter.setTokenizer(tok);

		MultiFilter multiFilter = new MultiFilter();
		multiFilter.setFilters(new Filter[]{posStringToWordVectorFilter, frameStringToWordVectorFilter, 
				textStringToWordVectorFilter});
		AbstractDeepNeuralNet n = new AbstractDeepNeuralNet() {
			
			@Override
			public double[] distributionForInstance(Instance arg0) throws Exception {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public void buildClassifier(Instances arg0) throws Exception {
				// TODO Auto-generated method stub
				
			}
		};
		try {
			multiFilter.setInputFormat(instances);
		} catch (Exception e2) {
			e2.printStackTrace();
		}

		try {
			Instances filtInstances = Filter.useFilter(instances, multiFilter);
			MLUtils.prepareData(filtInstances);
			BR br = new BR();
			br.setClassifier(new J48());
			br.testCapabilities(instances);
			br.buildClassifier(filtInstances);		
			Instances testInstances = new Instances(instances, 0);			
			String j = "I inform and report";			
			String ukamlConfigFile = "src/main/resources/load/eu.insight.finlaw.multilabel.ukaml.instances.meka";
			AMLGateAnnotationsToFeatureVectorConverter converter = new AMLGateAnnotationsToFeatureVectorConverter(ukamlConfigFile);
			Instance instance = converter.getInstance(j, instances);
			testInstances.add(testInstance);
			testInstances = Filter.useFilter(testInstances, multiFilter);
			MLUtils.prepareData(testInstances);			
			testInstance = testInstances.get(0);
			double[] arrayd = br.distributionForInstance(testInstance);			
			for(double d : arrayd){
				System.out.print(d + " ");
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

}

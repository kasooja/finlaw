package edu.insight.finlaw.multilabel.meka;

import meka.classifiers.multilabel.BR;
import meka.classifiers.multilabel.CC;
import meka.classifiers.multilabel.MultilabelClassifier;
import meka.classifiers.multilabel.meta.BaggingML;
import meka.gui.explorer.Explorer;
import weka.classifiers.Classifier;
import weka.classifiers.functions.SMO;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.Tag;
import weka.core.Utils;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.tokenizers.NGramTokenizer;
import weka.filters.unsupervised.attribute.StringToWordVector;
import edu.insight.finlaw.multilabel.utils.ConfigParameters;

public class MekaMultiClassifier {

	public  Instances trainingInstances = null;

	public static Instances loadInstances(String fn) {
		try {
			Instances D = DataSource.read(fn);
			Explorer.prepareData(D);
			return D;
		} catch(Exception e) {
			System.err.println("");
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}

	public static MultilabelClassifier makeECC() {		
		BaggingML h = new BaggingML();
		CC cc = new CC();
		cc.setClassifier(new SMO());
		h.setClassifier(cc);
		return h;
	}

	public static MultilabelClassifier makeMulClassifier() {
		BR h = new BR();
		h.setClassifier(new J48());	
		return h;
	}

	public StringToWordVector getStringToWordVectorFilter() {	
		Tag[] tags = new Tag[3];
		tags[0] = new Tag(0, "");			
		tags[1] = new Tag(1, "");
		tags[2] = new Tag(2, "");		
		SelectedTag selectedTag = new SelectedTag(1, tags);
		//Stemmer stemmer = new SnowballStemmer();
		StringToWordVector stringToWordVector = new StringToWordVector();	
		//stringToWordVector.setStemmer(stemmer);
		stringToWordVector.setWordsToKeep(70);
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

	public Classifier getTheFirstBinaryClassifier() {
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

	public  MultilabelClassifier makeSVMbasedClassifier(){
		BR h = new BR();
		h.setClassifier(getTheFirstBinaryClassifier());	
		return h;
	}

	public FilteredClassifier getLearnedClassifier(String arffFileNameNonFilt) {
		
		trainingInstances = loadInstances(arffFileNameNonFilt);		
		FilteredClassifier filteredClassifier = new FilteredClassifier();
		filteredClassifier.setFilter(getStringToWordVectorFilter());
		filteredClassifier.setClassifier(makeMulClassifier());
		//filteredClassifier.setClassifier(makeSVMbasedClassifier());
		try {
			filteredClassifier.buildClassifier(trainingInstances);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return filteredClassifier;
	}

	public FilteredClassifier getLearnedBinClassifier(String arffFileNameNonFilt) {
		//trainingInstances = loadInstances(arffFileNameNonFilt);
		DataSource source;
		try {
			source = new DataSource(arffFileNameNonFilt);
			trainingInstances = source.getDataSet();
			trainingInstances.setClassIndex(1);
			// setting class attribute if the data format does not provide this information
			// For example, the XRFF format saves the class attribute information as well
			FilteredClassifier filteredClassifier = new FilteredClassifier();
			filteredClassifier.setFilter(getStringToWordVectorFilter());
			filteredClassifier.setClassifier(getTheFirstBinaryClassifier());
			try {
				filteredClassifier.buildClassifier(trainingInstances);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return filteredClassifier;
			
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return null;
	}


	public Instances getTrainingInstances(){
		return trainingInstances;	
	}


	public static void main(String[] args) {
		//String arffFileNameNonFilt = "src/main/resources/grctcData/arff/FiveClassesFiroUKAMLMulti.arff";
		//Instances D = loadInstances(arffFileNameNonFilt);		
		//FilteredClassifier filteredClassifier = new FilteredClassifier();
		//		filteredClassifier.setFilter(getStringToWordVectorFilter());
		//		filteredClassifier.setClassifier(makeMulClassifier());
		//		try {
		//			filteredClassifier.buildClassifier(D);
		//		} catch (Exception e) {
		//			e.printStackTrace();
		//		}		
	}

}
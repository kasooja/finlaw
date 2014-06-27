package edu.insight.finlaw.multilabel.meka;


import java.io.BufferedReader;
import java.io.File;
import meka.classifiers.multilabel.BR;
import meka.classifiers.multilabel.CC;
import meka.classifiers.multilabel.Evaluation;
import meka.core.Result;
import meka.gui.explorer.Explorer;
import weka.classifiers.Classifier;
import weka.classifiers.functions.SMO;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.Tag;
import weka.core.Utils;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;
import edu.insight.finlaw.multilabel.weka.ConfigParameters;
import edu.insight.finlaw.utils.BasicFileTools;


public class MekaTest{

	/**
	 * Loads the dataset from disk.
	 * 
	 * @param file the dataset to load (e.g., "weka/classifiers/data/something.arff")
	 * @throws Exception if loading fails, e.g., file does not exit
	 */
	public static Instances loadData(String filePath) throws Exception {
		File file = new File(filePath);
		BufferedReader reader = BasicFileTools.getBufferedReader(file);
		return new Instances(reader);
		//return new Instances(new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream(file))));
	}

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

	public void testMulanFormat() {
		Result r1 = null, r2 = null;
		// Load Music-train
		Instances D_train = loadInstances("data/Music-train.arff");
		// Load Music-test
		Instances D_test = loadInstances("data/Music-test.arff");
		// Train CC
		CC h = new CC();
		BR hbr = new BR();
		hbr.setClassifier(new SMO());
		h.setClassifier(new SMO());
		// Eval
		try {
			r1 = Evaluation.evaluateModel(h, D_train, D_test, "PCut1");
			System.out.println(""+r1.output.get("Accuracy"));
		} catch(Exception e) {
			e.printStackTrace();
		}

		// Load Music
		Instances D = loadInstances("data/Music.arff");
		D_train = new Instances(D,0,491);
		D_test = new Instances(D,491,D.numInstances()-491);
		// Eval
		try {
			r2 = Evaluation.evaluateModel(h, D_train, D_test, "PCut1");
			System.out.println(""+r2.output.get("Accuracy"));
		} catch(Exception e) {
			e.printStackTrace();
		}

		//assertTrue("Mulan Format OK? (same result?)", r1.output.get("Accuracy").equals(r2.output.get("Accuracy")));
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

	public static Filter getStringToWordVectorFilter() {	
		Tag[] tags = new Tag[3];
		tags[0] = new Tag(0, "");			
		tags[1] = new Tag(1, "");
		tags[2] = new Tag(2, "");		
		SelectedTag selectedTag = new SelectedTag(1, tags);
		//Stemmer stemmer = new SnowballStemmer();
		StringToWordVector stringToWordVector = new StringToWordVector();	
		//stringToWordVector.setStemmer(stemmer);
		stringToWordVector.setWordsToKeep(5000);
		stringToWordVector.setNormalizeDocLength(selectedTag);		
		stringToWordVector.setMinTermFreq(2);
		stringToWordVector.setLowerCaseTokens(true);
		//stringToWordVector.setUseStoplist(true);
		return stringToWordVector;
	}

	public static void main(String[] args) {
		Result[] r1 = null;
		Instances D = loadInstances("data/FiroUKFilt.arff");
		Instances D_train = loadInstances("data/FiroUKTrainFilt.arff");
		Instances D_test = loadInstances("data/FiroUKTestFilt.arff");
		//FilteredClassifier filteredMultiLabelClassifier = new FilteredClassifier();
		//filteredMultiLabelClassifier.setFilter(getStringToWordVectorFilter());
		//filteredMultiLabelClassifier.setClassifier(getTheFirstBinaryClassifier());
		BR binaryRelevanceMultiLabel = new BR();
		//binaryRelevanceMultiLabel.setClassifier(filteredMultiLabelClassifier);
		binaryRelevanceMultiLabel.setClassifier(getTheFirstBinaryClassifier());
		String top = "PCutL";		
		System.out.println(D.numInstances());
		System.out.println(D_train.numInstances());
		System.out.println(D_test.numInstances());
		//		try {		
		//			String vop = "3";
		//			D_train.setClassIndex(0);
		//			D_test.setClassIndex(0);			
		//			binaryRelevanceMultiLabel.buildClassifier(D_train);
		//			Instance instance = D_test.get(2);
		//			instance.setDataset(D_test);
		//		//double classifyInstance = binaryRelevanceMultiLabel.classifyInstance(instance);
		//			//System.out.println(classifyInstance);
		//			double[] distributionForInstance = binaryRelevanceMultiLabel.distributionForInstance(instance);
		//			for(int u=0; u<distributionForInstance.length; u++)
		//				System.out.println(distributionForInstance[u]);
		//			//System.out.println(distributionForInstance);
		//			//	Result result = Evaluation.evaluateModel(binaryRelevanceMultiLabel, D_train, D_test, top, vop);
		//			//	System.out.println(result);
		//			System.out.println("**************************************************");		
		//		} catch (Exception e) {
		//			e.printStackTrace();
		//		}

		try {		
			String vop = "5";
			//r1 = Evaluation.cvModel(binaryRelevanceMultiLabel, d, 14, top, vop);
			Result result = Evaluation.evaluateModel(binaryRelevanceMultiLabel, D_train, D_test, top, vop);
			System.out.println(result);
			System.out.println("**************************************************");		

			//			for(Result r : r1){
			//				System.out.println(r);
			//				System.out.println("-------------------------------------------------");		
			//				
			//				//System.out.println(r.output.get("Accuracy"));
			//			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void buildClassifier(Instances D) throws Exception { 
		//	testCapabilities(D);
		//		int C = D.classIndex();
		//		D.setClassIndex(0);
		//	m_Classifier.buildClassifier(D);
	}
}

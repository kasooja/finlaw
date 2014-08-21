package edu.insight.finlaw.multilabel.classification.meka;


import meka.classifiers.multilabel.BR;
import meka.classifiers.multilabel.CC;
import meka.classifiers.multilabel.Evaluation;
import meka.classifiers.multilabel.MultilabelClassifier;
import meka.classifiers.multilabel.meta.BaggingML;
import meka.core.Result;
import meka.gui.explorer.Explorer;
import weka.classifiers.Classifier;
import weka.classifiers.functions.SMO;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.Tag;
import weka.core.Utils;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.tokenizers.NGramTokenizer;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;
import edu.insight.finlaw.multilabel.utils.ConfigParameters;

public class MekaDemo {

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
		//stringToWordVector.setOutputWordCounts(true);
		//stringToWordVector.setUseStoplist(true);
		return stringToWordVector;
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

	public static void mainOld(String[] args) {
		//Instances D = loadInstances("data/Music.arff");
		Instances D = loadInstances("src/main/resources/grctcData/arff/UKAMLArff.arff");
		StringToWordVector stringToWordVectorFilter = getStringToWordVectorFilter();	
		try {
			stringToWordVectorFilter.setInputFormat(D);
			D = Filter.useFilter(D, stringToWordVectorFilter);
		} catch (Exception e1) {
			e1.printStackTrace();
		}			
		MultilabelClassifier h = makeMulClassifier();
		try {
			h.buildClassifier(D);
			//double[] distributionForInstance = h.distributionForInstance(D.get(7));
			//Evaluation.cvModel(h, D, numFolds, top, vop)
			Result[] cvModel = Evaluation.cvModel(h, D, 28, "PCut1", "6");
			System.out.println(cvModel.length);
			double avg = 0.0;
			int count = 0;
			int[][][] matrices = new int[16][2][2];
		
			for(Result result : cvModel){
				int[][] allActuals = result.allActuals();	
				double[][] allPredictions = result.allPredictions();

				int counter = 0;
				for(counter = 0; counter<16; counter++) {
					if(allActuals[0][counter] == allPredictions[0][counter]){
						if(allActuals[0][counter] == 1)
							matrices[counter][0][0] = matrices[counter][0][0] + 1;
						if(allActuals[0][counter] == 0)
							matrices[counter][1][0] = matrices[counter][1][0] + 1;							
					}					
					if(allActuals[0][counter] != allPredictions[0][counter]){					
						if(allActuals[0][counter] == 1)
							matrices[counter][0][1] = matrices[counter][0][1] + 1;
						if(allActuals[0][counter] == 0)
							matrices[counter][1][1] = matrices[counter][1][1] + 1;					
					}
				}				
				Double accuracy = result.output.get("Accuracy");
				avg = avg + accuracy;
				System.out.println(result);
				System.out.println("-------------------------------------------------------------");
				//				for(int labelNumber = 0; labelNumber < 16; labelNumber++) {
				//					System.out.println("Matrix Number: " + labelNumber);
				//					for(int row = 0; row<2; row++){
				//						for(int col = 0; col<2; col++){
				//							System.out.print(matrices[labelNumber][row][col] + "\t");
				//						}
				//						System.out.print("\n");
				//					}
				//				}
				System.out.println("-------------------------------------------------------------");

				for(int labelNumber = 0; labelNumber < 16; labelNumber++) {
					System.out.println("Matrix Number: " + labelNumber);
					for(int row = 0; row<2; row++){
						for(int col = 0; col<2; col++){
							System.out.print(matrices[labelNumber][row][col] + "\t");
						}
						System.out.print("\n");
					}
				}


				System.out.println(count);
				count ++;
				//				System.out.println(allActuals);
				//				System.out.println(allPredictions);				
				//System.out.println(result.output.get("Accuracy"));
			}
			System.out.println(avg/count);
			System.out.println("CV Matrices: \n\n");
			//int[][][] matrices = new int[16][2][2];

			for(int labelNumber = 0; labelNumber < 16; labelNumber++) {
				System.out.println("Matrix Number: " + labelNumber);
				for(int row = 0; row<2; row++){
					for(int col = 0; col<2; col++){
						System.out.print(matrices[labelNumber][row][col] + "\t");
					}
					System.out.print("\n");
				}
			}

			//			Result r1 = Evaluation.evaluateModel(h, D_train, D_test, "PCut1");
			//			Result r2 = Evaluation.evaluateModel(h, D_train, D_test, "PCut1");
			//			System.out.println(""+r1.output.get("Accuracy"));
			//			System.out.println(""+r2.output.get("Accuracy"));
			//assertTrue("Experiments are Repeatable (with same result)", r1.output.get("Accuracy").equals(r2.output.get("Accuracy")));
		} catch(Exception e) {
			e.printStackTrace();
		}

	}
	
	public static void main(String[] args) {
		
	}

}

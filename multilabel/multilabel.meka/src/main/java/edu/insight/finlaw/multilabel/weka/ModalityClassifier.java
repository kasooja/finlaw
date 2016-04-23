package edu.insight.finlaw.multilabel.weka;

import java.util.Random;

//import edu.insight.finlaw.multilabel.meka.MekaMultiClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.converters.ConverterUtils.DataSource;

public class ModalityClassifier {
	public static String modalityArff = "src/main/resources/grctcData/arff/ModalityUKAMLBinary.arff";
	private static edu.insight.finlaw.multilabel.classification.meka.ModalityClassifier modalityMulti = new edu.insight.finlaw.multilabel.classification.meka.ModalityClassifier();
	//
	public static FilteredClassifier modalityClassifier = modalityMulti.getLearnedBinClassifier(modalityArff);//getEmptyBinClassifier

	public static void main(String[] args) {		
		DataSource source;
		try {
			source = new DataSource(modalityArff);
			Instances data = source.getDataSet();
			// setting class attribute if the data format does not provide this information
			// For example, the XRFF format saves the class attribute information as well
			if (data.classIndex() == -1)
				data.setClassIndex(data.numAttributes() - 1);

			int seed = 45787;          // the seed for randomizing the data
			int folds = 14;   

			Random rand = new Random(seed);   // create seeded number generator
			Instances randData = new Instances(data);   // create copy of original data
			randData.randomize(rand);         // randomize data with number generator
			Evaluation eval = new Evaluation(randData);
			for (int n = 0; n < folds; n++) {
				modalityClassifier = modalityMulti.getLearnedBinClassifier(modalityArff);//getEmptyBinClassifier();					
				Instances train = randData.trainCV(folds, n);
				Instances test = randData.testCV(folds, n);
				// build and evaluate classifier
				modalityClassifier.buildClassifier(train);
				eval.evaluateModel(modalityClassifier, test);

			}

			// output evaluation
			System.out.println();
			System.out.println("=== Setup ===");
			System.out.println("Classifier: " + modalityClassifier.getClass().getName() + " " + Utils.joinOptions(modalityClassifier.getOptions()));
			System.out.println("Dataset: " + data.relationName());
			System.out.println("Folds: " + folds);
			System.out.println("Seed: " + seed);
			System.out.println();
			System.out.println(eval.toSummaryString("=== " + folds + "-fold Cross-validation ===", false));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


}


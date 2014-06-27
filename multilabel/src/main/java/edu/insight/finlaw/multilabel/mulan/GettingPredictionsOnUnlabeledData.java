package edu.insight.finlaw.multilabel.mulan;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import mulan.classifier.MultiLabelOutput;
import mulan.classifier.meta.RAkEL;
import mulan.classifier.transformation.LabelPowerset;
import mulan.data.InvalidDataFormatException;
import mulan.data.MultiLabelInstances;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Utils;

/**
 * This examples shows how you can retrieve the predictions of a model on
 * unlabeled data. Unlabeled multi-label datasets should have the same
 * structure as the training data. The actual values of the labels could be
 * either unspecified (set to symbol ?), or randomly set to 0/1.
 *
 * @author Grigorios Tsoumakas
 * @version 2010.12.15
 */
public class GettingPredictionsOnUnlabeledData {

	/**
	 * Executes this example
	 *
	 * @param args command-line arguments -arff, -xml and -unlabeled
	 */
	public static void main(String[] args) {

		try {
			String arffFilename = "data/emotions.arff";
			String xmlFilename = "data/emotions.xml";

			System.out.println("Loading the training data set...");
			MultiLabelInstances trainingData = new MultiLabelInstances(arffFilename, xmlFilename);

			RAkEL model = new RAkEL(new LabelPowerset(new J48()));

			System.out.println("Building the model...");
			model.build(trainingData);

			String unlabeledDataFilename =  "data/emotions.arff";
			System.out.println("Loading the unlabeled data set...");
			MultiLabelInstances unlabeledData = new MultiLabelInstances(unlabeledDataFilename, xmlFilename);

			int numInstances = unlabeledData.getNumInstances();
			for (int instanceIndex = 0; instanceIndex < numInstances; instanceIndex++) {
				Instance instance = unlabeledData.getDataSet().instance(instanceIndex);
				MultiLabelOutput output = model.makePrediction(instance);
				if (output.hasBipartition()) {
					String bipartion = Arrays.toString(output.getBipartition());
					System.out.println("Predicted bipartion: " + bipartion);
				}
				if (output.hasRanking()) {
					String ranking = Arrays.toString(output.getRanking());
					System.out.println("Predicted ranking: " + ranking);
				}
				if (output.hasConfidences()) {
					String confidences = Arrays.toString(output.getConfidences());
					System.out.println("Predicted confidences: " + confidences);
				}
			}
		} catch (InvalidDataFormatException e) {
			System.err.println(e.getMessage());
		} catch (Exception ex) {
			Logger.getLogger(GettingPredictionsOnUnlabeledData.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}

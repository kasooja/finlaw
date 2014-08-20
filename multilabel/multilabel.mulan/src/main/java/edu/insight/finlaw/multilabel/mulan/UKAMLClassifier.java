package edu.insight.finlaw.multilabel.mulan;

import mulan.classifier.transformation.BinaryRelevance;
import mulan.data.MultiLabelInstances;
import mulan.evaluation.Evaluator;
import mulan.evaluation.MultipleEvaluation;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

public class UKAMLClassifier {


	public static void main(String[] args) {
		String labelXML =  "src/main/resources/load/mulanUKAML.xml";
		Instances ukamlInstances = Commons.loadWekaData("src/main/resources/grctcData/arff/UKAMLArffExtended.arff");
		Instances filteredUKAMLInstances = null;
		StringToWordVector stringToWordVectorFilter = Commons.getStringToWordVectorFilter();	
		try {
			stringToWordVectorFilter.setInputFormat(ukamlInstances);
			filteredUKAMLInstances = Filter.useFilter(ukamlInstances, stringToWordVectorFilter);
			MultiLabelInstances multiFilteredUKAMLInstances = new MultiLabelInstances(filteredUKAMLInstances, labelXML);
			BinaryRelevance learner1 = new BinaryRelevance(Commons.getFirstBinClassifierFromJson());							
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

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

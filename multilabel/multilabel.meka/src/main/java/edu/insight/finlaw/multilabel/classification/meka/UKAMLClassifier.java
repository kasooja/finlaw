package edu.insight.finlaw.multilabel.classification.meka;

import meka.classifiers.multilabel.BR;
import meka.classifiers.multilabel.MultilabelClassifier;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;
import edu.insight.finlaw.utils.Pair;

public class UKAMLClassifier {

	private Instances trainingInstances;

	public static MultilabelClassifier makeMulClassifier() {
		BR h = new BR();
		//h.setClassifier(new J48());
		//h.setClassifier(new NaiveBayesMultinomialText());
		return h;
	}
	

	public  MultilabelClassifier makeSVMbasedClassifier(){
		BR h = new BR();
		h.setClassifier(Commons.getFirstBinClassifierFromJson());	
		return h;
	}

	//check this classifier
	public FilteredClassifier getLearnedClassifier(String arffFileNameNonFilt) {		
		setTrainingInstances(Commons.loadMekaInstances(arffFileNameNonFilt));		
		FilteredClassifier filteredClassifier = new FilteredClassifier();
		StringToWordVector stringToWordVector = Commons.getStringToWordVectorFilter();
		filteredClassifier.setFilter(stringToWordVector);
		//filteredClassifier.setClassifier(makeMulClassifier());
		filteredClassifier.setClassifier(makeSVMbasedClassifier());
		try {
			filteredClassifier.buildClassifier(getTrainingInstances());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return filteredClassifier;
	}

	public static void main(String[] args) {
		Instances ukamlInstances = Commons.loadMekaInstances("src/main/resources/grctcData/arff/UKAMLArffExtended5Classes.arff");
		Instances filteredUKAMLInstances = null;
		StringToWordVector stringToWordVectorFilter = Commons.getStringToWordVectorFilter();	
		try {
			stringToWordVectorFilter.setInputFormat(ukamlInstances);
			Pair<Instances,Instances> trainTest = Commons.getTrainTest(ukamlInstances, "FiroUKTrain: -C 5", "FiroUKTrain: -C 5", 80);
			Instances train = trainTest.getFirst();
			Instances test = trainTest.getSecond();
			stringToWordVectorFilter.setInputFormat(test);
			
			filteredUKAMLInstances = Filter.useFilter(test, stringToWordVectorFilter);
			stringToWordVectorFilter.setInputFormat(train);
			
			filteredUKAMLInstances = Filter.useFilter(train, stringToWordVectorFilter);
			//stringToWordVectorFilter.
		} catch (Exception e1) {
			e1.printStackTrace();
		}	
		
		
		MultilabelClassifier multiClassifier = makeMulClassifier();		
		try {
			Pair<Instances,Instances> trainTest = Commons.getTrainTest(filteredUKAMLInstances, "FiroUKTrain: -C 5", "FiroUKTrain: -C 5", 80);
			Instances train = trainTest.getFirst();
			Instances test = trainTest.getSecond();
			train = Commons.prepareMekaInstances(train);
			test = Commons.prepareMekaInstances(test);
			multiClassifier.buildClassifier(train);	
			
			double classifyInstance = multiClassifier.classifyInstance(test.firstInstance());
			System.out.println(classifyInstance);
//			Result[] cvModel = Evaluation.cvModel(multiClassifier, filteredUKAMLInstances, 20, "PCut1", "6");
//			System.out.println(cvModel.length);
//			Result averageResults = MLEvalUtils.averageResults(cvModel);
//			System.out.println(averageResults);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Instances getTrainingInstances() {
		return trainingInstances;
	}

	public void setTrainingInstances(Instances trainingInstances) {
		this.trainingInstances = trainingInstances;
	}

}

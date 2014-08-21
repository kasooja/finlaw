package edu.insight.finlaw.multilabel.classification.meka;

import meka.classifiers.multilabel.BR;
import meka.classifiers.multilabel.Evaluation;
import meka.classifiers.multilabel.MultilabelClassifier;
import meka.core.MLEvalUtils;
import meka.core.Result;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

public class UKAMLClassifier {

	public static MultilabelClassifier makeMulClassifier() {
		BR h = new BR();
		h.setClassifier(new J48());	
		return h;
	}
	
	public  MultilabelClassifier makeSVMbasedClassifier(){
		BR h = new BR();
		h.setClassifier(Commons.getFirstBinClassifierFromJson());	
		return h;
	}
	
	public FilteredClassifier getLearnedClassifier(String arffFileNameNonFilt) {		
		Instances trainingInstances = Commons.loadMekaInstances(arffFileNameNonFilt);		
		FilteredClassifier filteredClassifier = new FilteredClassifier();
		filteredClassifier.setFilter(Commons.getStringToWordVectorFilter());
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
			Instances trainingInstances = source.getDataSet();
			trainingInstances.setClassIndex(1);
			// setting class attribute if the data format does not provide this information
			// For example, the XRFF format saves the class attribute information as well
			FilteredClassifier filteredClassifier = new FilteredClassifier();
			filteredClassifier.setFilter(Commons.getStringToWordVectorFilter());
			filteredClassifier.setClassifier(Commons.getFirstBinClassifierFromJson());
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


	public static void main(String[] args) {
		Instances ukamlInstances = Commons.loadMekaInstances("src/main/resources/grctcData/arff/UKAMLArffExtended.arff");
		Instances filteredUKAMLInstances = null;
		StringToWordVector stringToWordVectorFilter = Commons.getStringToWordVectorFilter();	
		try {
			stringToWordVectorFilter.setInputFormat(ukamlInstances);
			filteredUKAMLInstances = Filter.useFilter(ukamlInstances, stringToWordVectorFilter);
		} catch (Exception e1) {
			e1.printStackTrace();
		}			
		MultilabelClassifier multiClassifier = makeMulClassifier();		
		try {
			multiClassifier.buildClassifier(filteredUKAMLInstances);
			Result[] cvModel = Evaluation.cvModel(multiClassifier, filteredUKAMLInstances, 20, "PCut1", "6");
			System.out.println(cvModel.length);
			Result averageResults = MLEvalUtils.averageResults(cvModel);
			System.out.println(averageResults);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

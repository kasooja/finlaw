package edu.insight.finlaw.modality.classifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

public class SVM {
	
	public static void main(String[] args) {
		Instances binClassifierData = Commons.loadWekaData("src/main/resources/sapna/dat.arff");
		//binClassifierData.setClassIndex(12);
		//Instances instances = new Instances(binClassifierData);
		//		Remove remove = new Remove();		
		//		remove.setAttributeIndices("28");				
		//		try {
		//			remove.setInputFormat(D_nonFilt);
		//			D_nonFilt = Filter.useFilter(D_nonFilt, remove);
		//		} catch (Exception e1) {
		//			e1.printStackTrace();
		//		}
		//
		StringToWordVector stringToWordVectorFilter = Commons.getStringToWordVectorFilter();	
		try {
			stringToWordVectorFilter.setInputFormat(binClassifierData);
			binClassifierData = Filter.useFilter(binClassifierData, stringToWordVectorFilter);			
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		int topClasses = 5;
		Classifier svm = Commons.getFirstBinClassifierFromJson();
		try {
			svm.buildClassifier(binClassifierData);
			Instance lastInstance = binClassifierData.lastInstance();
			System.out.println(lastInstance.classIsMissing());
			double[] distributionForInstance = svm.distributionForInstance(binClassifierData.lastInstance());			
			Attribute classAttribute = binClassifierData.classAttribute();
			int index = 0;
			Map<Double, List<String>> classScoreMap = new HashMap<Double, List<String>>();
			for(double score : distributionForInstance){
				if(classScoreMap.get(score) == null)
					classScoreMap.put(score, new ArrayList<String>());
				classScoreMap.get(score).add(classAttribute.value(index));
				System.out.println(classAttribute.value(index) + "\t" +  score);
				index++;
			}			
			Arrays.sort(distributionForInstance);
			int arrayLength = distributionForInstance.length;
			if(topClasses >= arrayLength)
				topClasses = arrayLength;			
			for(int i=arrayLength-1; i>=(arrayLength-topClasses); i--)
				System.out.println(distributionForInstance[i] + "\t" + classScoreMap.get(distributionForInstance[i]));			
		} catch (Exception e) {
			e.printStackTrace();
		}


	}

}

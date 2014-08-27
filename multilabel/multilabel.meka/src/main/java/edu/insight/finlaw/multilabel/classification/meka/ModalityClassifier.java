package edu.insight.finlaw.multilabel.classification.meka;

import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class ModalityClassifier {

	private Instances trainingInstances;

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
			filteredClassifier.setFilter(Commons.getStringToWordVectorFilter());
			//filteredClassifier.setClassifier(Commons.getFirstBinClassifierFromJson());
			filteredClassifier.setClassifier(new J48());
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

	public Instances getTrainingInstances() {
		return trainingInstances;
	}

	public void setTrainingInstances(Instances trainingInstances) {
		this.trainingInstances = trainingInstances;
	}

	public static void main(String[] args) {
		String modalityArff = "src/main/resources/grctcData/arff/ModalityUKAMLBinary.arff";
		ModalityClassifier modalityClassifierObj = new ModalityClassifier();	
		FilteredClassifier modalityClassifier = modalityClassifierObj.getLearnedBinClassifier(modalityArff);
		

	}


}

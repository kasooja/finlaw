package edu.insight.finlaw.multilabel.instances.meka;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import edu.insight.finlaw.gate.annotation.reader.GateAnnotationReader;
import edu.insight.finlaw.utils.BasicFileTools;
import edu.insight.finlaw.utils.StringDistance;
import gate.Annotation;
import gate.util.InvalidOffsetException;

public class GateToArffConverter {

	private Properties config = new Properties();
	private List<String> labelsToBeUsed = new ArrayList<String>();
	private static double[] vals;
	private static ArrayList<String> attVals;
	private static ArrayList<String> tOrfVals;	
	private Instances data;	  
	private ArrayList<Attribute> atts = new ArrayList<Attribute>();

	private static String featureFile1 = "src/main/resources/grctcData/leona_features";
	private static String featureFile2 = "src/main/resources/grctcData/my_features";
	private static List<String> leonaFeatures = null;
	private static List<String> myFeatures = null;	
	private static List<String> features = new ArrayList<String>();


	static{
		attVals = new ArrayList<String>();
		attVals.add("0"); attVals.add("1");	
		tOrfVals = new ArrayList<String>();
		tOrfVals.add("f"); tOrfVals.add("t");
		leonaFeatures = getFeatureList(featureFile1);
		myFeatures = getFeatureList(featureFile2);			
		features.addAll(leonaFeatures); features.addAll(myFeatures);	
	}

	public GateToArffConverter(String configFilePath) {
		loadConfig(configFilePath);
		setConfig();
	}

	private void loadConfig(String configFilePath){
		try {
			config.load(new FileInputStream(configFilePath));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}			
	}	

	private void setConfig(){
		String labelsString = config.getProperty("labels");
		String[] split = labelsString.trim().split(",");
		for(String label : split){
			label = label.trim();
			if(label.equalsIgnoreCase("all")){
				labelsToBeUsed = null;
				break;
			}
			labelsToBeUsed.add(label.trim());
		}
	}

	//	public Instances getInst(String annotatedGateFile, List<String> features, String instancesName, double stringDistanceThreshold) {
	//		String annotationSetName = null;
	//	}
	//	

	public Instances getInstBsdOnFeatsNStrDist(String annotatedGateFile, List<String> features, String instancesName, double stringDistanceThreshold) {
		String annotationSetName = null; 
		GateAnnotationReader gateAnnoReader = new GateAnnotationReader();
		gateAnnoReader.setDocument(annotatedGateFile);
		Map<String, List<Annotation>> annotations = gateAnnoReader.readAnnotatedGateFile(labelsToBeUsed, annotationSetName);
		List<String> annotationTypeList = gateAnnoReader.getAnnotationTypeList();
		//MEKA based naming of instances
		instancesName = instancesName + ": -C " + annotationTypeList.size() + " ";
		for(String annotationType : annotationTypeList)		
			atts.add(new Attribute(annotationType + "_Class", attVals));		
		for(String feature : features)
			atts.add(new Attribute(feature + "_Nom", tOrfVals));
		// - string
		Attribute textAttribute = new Attribute("text", (ArrayList<String>) null);
		atts.add(textAttribute);			
		data = new Instances(instancesName, atts, 0);		
		for(String annoType : annotations.keySet()) {
			List<Annotation> annoTypeAnnotations = annotations.get(annoType);
			int annoTypeIndex = annotationTypeList.indexOf(annoType);
			for(Annotation annotation : annoTypeAnnotations) {
				boolean found = false;		
				String content;
				try {
					content = gateAnnoReader.getDocument().getContent().getContent(annotation.getStartNode().getOffset(), annotation.getEndNode().getOffset()).toString();
					for(Instance instance : data) {
						int levenDist = StringDistance.computeLevenshteinDistance(instance.stringValue(textAttribute).trim(), content.trim());						
						if(levenDist<stringDistanceThreshold){	
							System.out.println("Match under 50: " + levenDist);
							instance.setValue(annoTypeIndex, "1");
							found = true;
							break;
						}						
					}
					if(!found){
						vals = new double[data.numAttributes()];
						int count = 0;
						for(int i=0; i<annotationTypeList.size(); i++){
							vals[count] = attVals.indexOf("0");
							if(count == annoTypeIndex)
								vals[count] = attVals.indexOf("1");						
							count++;
						}
						for(String feature : features){
							if(content.toLowerCase().contains(feature.toLowerCase()))
								vals[count] = tOrfVals.indexOf("t"); 
							else 
								vals[count] = tOrfVals.indexOf("f"); 
							count ++;			
						}
						vals[count++] = data.attribute("text").addStringValue(content.replace("class", "classwekaattribute").trim());						
						Instance instance = new DenseInstance(1.0, vals);
						data.add(instance);			
					}
				} catch (InvalidOffsetException e) {
					e.printStackTrace();
				}
			}
			gateAnnoReader.cleanUp();
		}
		return data;
	}

	public static List<String> getFeatureList(String featureFilePath){
		BufferedReader featureBR = BasicFileTools.getBufferedReaderFile(featureFilePath);
		List<String> features = new ArrayList<String>();
		String line = null;
		try {
			while((line = featureBR.readLine())!=null){
				line = line.trim();
				if(!"".equals(line))
					features.add(line);				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}	
		return features;
	}

	public static void createUKAMLInstances(String configFile) {		
		String annotatedGateFile = "src/main/resources/grctcData/UK_AML_xml_annotated_firo_extended.xml";
		String arffFileNameNonFilt = "src/main/resources/grctcData/arff/UKAMLArffExtended.arff";	
		String instancesName = "FIROInstances";
		GateToArffConverter arffConverter = new GateToArffConverter(configFile);
		double stringDistanceThreshold = 50.0;
		Instances instances = arffConverter.getInstBsdOnFeatsNStrDist(annotatedGateFile, features, instancesName, stringDistanceThreshold);
		System.out.println(instances.numInstances());
		ArffSaver saver = new ArffSaver();		
		try {
			saver.setInstances(instances);
			saver.setFile(new File(arffFileNameNonFilt));		
			saver.writeBatch();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}

	public static Instance getInstance(String content, Instances trainingInstances){
		vals = new double[trainingInstances.numAttributes()];
		Enumeration<Attribute> enumerateAttributes = trainingInstances.enumerateAttributes();
		while(enumerateAttributes.hasMoreElements()){			
			Attribute attribute = enumerateAttributes.nextElement();
			String attributeName = attribute.name().toLowerCase();
			int attributeIndex = attribute.index();
			if(attributeName.contains("_class".toLowerCase())){
				vals[attributeIndex] = attVals.indexOf("0");
			}
			if(attributeName.contains("_Nom".toLowerCase())){				
				if(content.toLowerCase().contains(attributeName.replace("_Nom".toLowerCase()," ").trim())){
					vals[attributeIndex] = tOrfVals.indexOf("t");
				} else {
					vals[attributeIndex] = tOrfVals.indexOf("f");						
				}
			}
			if(attributeName.contains("text".toLowerCase())){
				vals[attributeIndex] = trainingInstances.attribute("text").addStringValue(content.replace("class", "classwekaattribute").trim());
			}
//			if(attributeName.contains("label".toLowerCase())){
//				vals[attributeIndex] = trainingInstances.get(0).value(attribute);
//			}
		}
		Instance instance = new DenseInstance(1.0, vals);
		instance.setDataset(trainingInstances);
		boolean checkInstance = trainingInstances.checkInstance(instance);
		if(checkInstance)
			return instance; 
		else 
			return null;
	}

	public static void createModalityInstances(String configFile) {
		String annotatedGateFile = "src/main/resources/grctcData/UK_AML_Annotated_CDDFragment_POS_LOB.xml";		
		String arffFileNameNonFilt = "src/main/resources/grctcData/arff/ModalityUKAMLMulti.arff";
		List<String> features = new ArrayList<String>();		
		String instancesName = "FIROModality";
		double stringDistanceThreshold = 50.0;		
		GateToArffConverter arffConverter = new GateToArffConverter(configFile);
		Instances instances = arffConverter.getInstBsdOnFeatsNStrDist(annotatedGateFile, features, instancesName, stringDistanceThreshold);
		System.out.println(instances.numInstances());
		ArffSaver saver = new ArffSaver();		
		try {
			saver.setInstances(instances);
			saver.setFile(new File(arffFileNameNonFilt));		
			saver.writeBatch();
		} catch (IOException e) {
			e.printStackTrace();
		}			
	}

	public static void main(String[] args) {
		String ukamlConfigFile = "src/main/resources/load/eu.insight.finlaw.multilabel.ukaml.instances.meka";
		//String modalityConfigFile = "src/main/resources/load/eu.insight.finlaw.multilabel.modality.instances.meka";
		createUKAMLInstances(ukamlConfigFile);
		//createModalityInstances(modalityConfigFile);
	}

}

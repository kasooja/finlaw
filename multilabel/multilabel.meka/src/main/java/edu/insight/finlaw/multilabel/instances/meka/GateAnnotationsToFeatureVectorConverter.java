package edu.insight.finlaw.multilabel.instances.meka;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

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

public class GateAnnotationsToFeatureVectorConverter {

	private Properties config = new Properties();
	private List<String> ukLabelsToBeUsed = new ArrayList<String>();
	private List<String> usLabelsToBeUsed = new ArrayList<String>();
	private List<String> labelsToBeUsed = new ArrayList<String>();
	private static double[] vals;
	private static ArrayList<String> attVals;
	private static ArrayList<String> tOrfVals;	
	private Instances data;	  
	private ArrayList<Attribute> atts = new ArrayList<Attribute>();
	private List<String> annotationTypeList;
	private Attribute textAttribute;
	private Attribute previousContextAttribute;
	private static List<String> classLabelVals; 
	private static String featureFile1 = "src/main/resources/grctcData/leona_features";
	private static String featureFile2 = "src/main/resources/grctcData/my_features";
	private static List<String> leonaFeatures = null;
	private static List<String> myFeatures = null;	
	private static List<String> features = new ArrayList<String>();

	static{
		attVals = new ArrayList<String>();
		attVals.add("0"); attVals.add("1"); attVals.add("2");	
		tOrfVals = new ArrayList<String>();
		tOrfVals.add("f"); tOrfVals.add("t");
		leonaFeatures = getFeatureList(featureFile1);
		myFeatures = getFeatureList(featureFile2);			
		features.addAll(leonaFeatures); features.addAll(myFeatures);	
	}

	public GateAnnotationsToFeatureVectorConverter(String configFilePath) {
		loadConfig(configFilePath);
		setConfig("uklabels", ukLabelsToBeUsed);
		setConfig("uslabels", usLabelsToBeUsed);	
		setConfig("labels", labelsToBeUsed);	

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

	private void setConfig(String propertyName, List<String> labelsToBeUsed){
		String labelsString = config.getProperty(propertyName);
		if(labelsString!=null && !"".equals(labelsString.trim())){
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
	}

	public Instances getInstBsdOnFeatsNStrDistUSAddOn(Instances data, String annotatedGateFile, List<String> features, String instancesName, double stringDistanceThreshold) {
		String annotationSetName = null; 
		GateAnnotationReader gateAnnoReader = new GateAnnotationReader();
		gateAnnoReader.setDocument(annotatedGateFile);
		//Map<String, List<Annotation>> annotations = gateAnnoReader.readAnnotatedGateFile(labelsToBeUsed, annotationSetName);
		Map<String, List<Annotation>> annotations = gateAnnoReader.readAnnotatedGateFileLabels(usLabelsToBeUsed, annotationSetName);
		//List<String> annotationTypeList = gateAnnoReader.getAnnotationTypeList();

		//MEKA based naming of instances
		//	instancesName = instancesName + ": -C " + annotationTypeList.size() + " ";
		//		for(String annotationType : annotationTypeList)		
		//			atts.add(new Attribute(annotationType + "_Class", attVals));		
		//		for(String feature : features)
		//			atts.add(new Attribute(feature + "_Nom", tOrfVals));
		//		// - string
		//Attribute textAttribute = new Attribute("text", (ArrayList<String>) null);
		//		atts.add(textAttribute);			
		//data = new Instances(instancesName, atts, 0);		
		for(String annoType : annotations.keySet()) {
			List<Annotation> annoTypeAnnotations = annotations.get(annoType);
			if("Customer Due Diligence".equalsIgnoreCase(annoType)){
				annoType = "customerduediligence";				
			}
			if("Customer Identification and Verification".equalsIgnoreCase(annoType)){
				annoType = "customeridentificationverification";			
			}			

			int annoTypeIndex = annotationTypeList.indexOf(annoType.trim().toLowerCase());
			for(Annotation annotation : annoTypeAnnotations) {
				boolean found = false;		
				String content;
				try {
					content = gateAnnoReader.getDocument().getContent().getContent(annotation.getStartNode().getOffset(), annotation.getEndNode().getOffset()).toString();
					for(Instance instance : data) {
						//instance.str
						String instanceText = instance.stringValue(textAttribute).trim();
						int levenDist = StringDistance.computeLevenshteinDistance(instanceText, content.trim());						
						if(levenDist<stringDistanceThreshold){	
							//System.out.println("Match under 50: " + levenDist);
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
						//data.add(instance);
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

	/*
	 * convert gate annotations to feature vectors, it uses string distance to resolve multi-label,
	 * it does not use sequential context
	 */
	public Instances getInstances(String annotationSetName, String annotatedGateFile, List<String> features, String instancesName, double stringDistanceThreshold, String tagName) {		 
		GateAnnotationReader gateAnnoReader = new GateAnnotationReader();
		gateAnnoReader.setDocument(annotatedGateFile);	
		LinkedHashMap<String, Annotation> annotations = gateAnnoReader.readAnnotatedGateFileFeaturesSequence(ukLabelsToBeUsed, annotationSetName, tagName);	
		annotationTypeList = gateAnnoReader.getAnnotationTypeList();
		String previousContext = "";
		//MEKA based naming of instances
		instancesName = instancesName + ": -C " + annotationTypeList.size() + " ";
		for(String annotationType : annotationTypeList)		
			atts.add(new Attribute(annotationType + "_Class", attVals));		
		textAttribute = new Attribute("text", (ArrayList<String>) null);
		previousContextAttribute = new Attribute("previousContext", (ArrayList<String>) null);
		atts.add(textAttribute);
		atts.add(previousContextAttribute);			
		data = new Instances(instancesName, atts, 0);		
		for(String annoTypes : annotations.keySet()){
			Annotation annotation = annotations.get(annoTypes);
			String[] split = annoTypes.split("\t");
			String content;
			try {
				content = gateAnnoReader.getDocument().getContent().getContent(annotation.getStartNode().getOffset(), annotation.getEndNode().getOffset()).toString();
				
				content = content.replaceAll("\\W\\W\\w+;", " ").trim();
				content = content.replaceAll("\\W", " ").trim();
				content = content.replaceAll("\\s\\w\\s", " ").trim();
				content = content.replaceAll("\\b\\w\\s", " ").trim();
				content = content.replaceAll("\\s\\w\\b", " ").trim();
				content = content.replaceAll("\\s\\s+", " ").trim();					
				content = content.replace("class", "classWekaAttribute");
				vals = new double[data.numAttributes()];
				int count = 0;
				for(int i=0; i<annotationTypeList.size(); i++){
					vals[count++] = attVals.indexOf("0");
				}
				for(int size=0; size<split.length-1; size++){
					String annoType = split[size].trim();
					int annoTypeIndex = annotationTypeList.indexOf(annoType);
					vals[annoTypeIndex] = attVals.indexOf("1");
				}		
				vals[count++] = data.attribute("text").addStringValue(content);
				vals[count++] = data.attribute("previousContext").addStringValue(previousContext);				 
				StringTokenizer tokenizer = new StringTokenizer(content);
				StringBuffer buffer = new StringBuffer();
				while(tokenizer.hasMoreTokens()){
					buffer.append(tokenizer.nextToken().trim() + "_previous ");
				}
				previousContext = buffer.toString().trim();
				Instance instance = new DenseInstance(1.0, vals);
				data.add(instance);
			} catch (InvalidOffsetException e) {
				e.printStackTrace();
			}
		}
		gateAnnoReader.cleanUp();
		return data;
	}

//	public static void createUKAMLInstances(String configFile) {		
//		//String annotatedGateFile = "src/main/resources/grctcData/UK_AML_xml_annotated_firo_extended.xml";
//		String annotatedGateFile = "src/main/resources/grctcData/20141029_UKSI-2007-2157-made-XML-AML.xml";
//		//String arffFileNameNonFilt = "src/main/resources/grctcData/arff/UKAMLArffExtended.arff";
//		String arffFileNameNonFilt = "src/main/resources/grctcData/arff/UKAMLArffP2TagsFeaturesSeqContext.arff";
//		String instancesName = "FIROInstances";
//		GateAnnotationsToFeatureVectorConverter arffConverter = new GateAnnotationsToFeatureVectorConverter(configFile);
//		double stringDistanceThreshold = 50.0;
//		//Instances instances = arffConverter.getInstBsdOnFeatsNStrDist(annotatedGateFile, features, instancesName, stringDistanceThreshold);
//		Instances instances = arffConverter.getInstances(annotatedGateFile, features, instancesName, stringDistanceThreshold);
//		System.out.println(instances.numInstances());
//		ArffSaver saver = new ArffSaver();		
//		try {
//			saver.setInstances(instances);
//			saver.setFile(new File(arffFileNameNonFilt));		
//			saver.writeBatch();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}	
//	}


	public static void createUKUSAMLInstances(String configFile) {
		String annotationSetName = "Original markups";
		String tagName = "P2";		
		String uk1AnnotatedGateFile = "src/main/resources/grctcData/annotatedamllegislation/20141029_UKSI-2007-2157-made-XML-AML.xml";
//		String us1AnnotatedGateFile = "src/main/resources/grctcData/annotatedamllegislation/ChapterXBSA1.xml";
//		String us2AnnotatedGateFile = "src/main/resources/grctcData/annotatedamllegislation/BSA Chapter x.xml";
//		String uk2AnnotatedGateFile = "src/main/resources/grctcData/annotatedamllegislation/Business in the Regulated Sector and Supervisory Authorities.xml";
//		String uk3AnnotatedGateFile = "src/main/resources/grctcData/annotatedamllegislation/Crime and Courts Act 2013.xml";
//		String uk4AnnotatedGateFile = "src/main/resources/grctcData/annotatedamllegislation/Money Laundering Regulations 1993.xml";
//		String uk5AnnotatedGateFile = "src/main/resources/grctcData/annotatedamllegislation/Money Laundering Regulations 2001.xml";
//		String uk6AnnotatedGateFile = "src/main/resources/grctcData/annotatedamllegislation/Proceeds of Crime Act Order 2003.xml";
//		String uk7AnnotatedGateFile = "src/main/resources/grctcData/annotatedamllegislation/Proceeds of Crime Order 2007.xml";
//		String uk8AnnotatedGateFile = "src/main/resources/grctcData/annotatedamllegislation/Serious Organised Crime and Police Act 2005.xml";
//		String uk9AnnotatedGateFile = "src/main/resources/grctcData/annotatedamllegislation/Terrorism Act 2000.xml";
//		String uk10AnnotatedGateFile = "src/main/resources/grctcData/annotatedamllegislation/Terrorism Act 2002 and Proceeds of Crime Act 2002 Regulations.xml";
//		String uk11AnnotatedGateFile = "src/main/resources/grctcData/annotatedamllegislation/Terrorism Act Order 2003.xml";	
		
		//String arffFileNameNonFilt = "src/main/resources/grctcData/arff/UKAMLArffExtended.arff";
		String arffFileNameNonFilt = "src/main/resources/grctcData/annotatedamllegislation/arff/USUKAMLArffP2TagsFeaturesSeqContext.arff";
		String instancesName = "FIROInstances";
		GateAnnotationsToFeatureVectorConverter featureVectorConverter = new GateAnnotationsToFeatureVectorConverter(configFile);
		double stringDistanceThreshold = 50.0;
		//Instances instances = arffConverter.getInstBsdOnFeatsNStrDist(annotatedGateFile, features, instancesName, stringDistanceThreshold);
		Instances uk1Instances = featureVectorConverter.getInstances(annotationSetName, uk1AnnotatedGateFile, features, instancesName, stringDistanceThreshold, tagName);
		System.out.println("after uk1: " +  uk1Instances.numInstances());		
		
//		Instances us1Instances = arffConverter.getInstBsdOnFeatsNStrDistUSAddOn(uk1Instances, us1AnnotatedGateFile, features, instancesName, stringDistanceThreshold);//usAnnotatedGateFile, features, instancesName, stringDistanceThreshold);
//		System.out.println("after: " + us1Instances.numInstances());
//		Instances us2Instances = arffConverter.getInstBsdOnFeatsNStrDistUSAddOn(us1Instances, us2AnnotatedGateFile, features, instancesName, stringDistanceThreshold);//usAnnotatedGateFile, features, instancesName, stringDistanceThreshold);
//		System.out.println("after: " + us2Instances.numInstances());
//		Instances uk2Instances = arffConverter.getInstBsdOnFeatsNStrDistUSAddOn(us2Instances, uk2AnnotatedGateFile, features, instancesName, stringDistanceThreshold);//usAnnotatedGateFile, features, instancesName, stringDistanceThreshold);
//		System.out.println("after: " + uk2Instances.numInstances());
//		Instances uk3Instances = arffConverter.getInstBsdOnFeatsNStrDistUSAddOn(uk2Instances, uk3AnnotatedGateFile, features, instancesName, stringDistanceThreshold);//usAnnotatedGateFile, features, instancesName, stringDistanceThreshold);
//		System.out.println("after: " + uk3Instances.numInstances());
//		Instances uk4Instances = arffConverter.getInstBsdOnFeatsNStrDistUSAddOn(uk3Instances, uk4AnnotatedGateFile, features, instancesName, stringDistanceThreshold);//usAnnotatedGateFile, features, instancesName, stringDistanceThreshold);
//		System.out.println("after: " + uk4Instances.numInstances());
//		Instances uk5Instances = arffConverter.getInstBsdOnFeatsNStrDistUSAddOn(uk4Instances, uk5AnnotatedGateFile, features, instancesName, stringDistanceThreshold);//usAnnotatedGateFile, features, instancesName, stringDistanceThreshold);
//		System.out.println("after: " + uk5Instances.numInstances());
//		Instances uk6Instances = arffConverter.getInstBsdOnFeatsNStrDistUSAddOn(uk5Instances, uk6AnnotatedGateFile, features, instancesName, stringDistanceThreshold);//usAnnotatedGateFile, features, instancesName, stringDistanceThreshold);
//		System.out.println("after: " + uk6Instances.numInstances());
//		Instances uk7Instances = arffConverter.getInstBsdOnFeatsNStrDistUSAddOn(uk6Instances, uk7AnnotatedGateFile, features, instancesName, stringDistanceThreshold);//usAnnotatedGateFile, features, instancesName, stringDistanceThreshold);
//		System.out.println("after: " + uk7Instances.numInstances());
//		Instances uk8Instances = arffConverter.getInstBsdOnFeatsNStrDistUSAddOn(uk7Instances, uk8AnnotatedGateFile, features, instancesName, stringDistanceThreshold);//usAnnotatedGateFile, features, instancesName, stringDistanceThreshold);
//		System.out.println("after: " + uk8Instances.numInstances());
//		Instances uk9Instances = arffConverter.getInstBsdOnFeatsNStrDistUSAddOn(uk8Instances, uk9AnnotatedGateFile, features, instancesName, stringDistanceThreshold);//usAnnotatedGateFile, features, instancesName, stringDistanceThreshold);
//		System.out.println("after: " + uk9Instances.numInstances());
//		Instances uk10Instances = arffConverter.getInstBsdOnFeatsNStrDistUSAddOn(uk9Instances, uk10AnnotatedGateFile, features, instancesName, stringDistanceThreshold);//usAnnotatedGateFile, features, instancesName, stringDistanceThreshold);
//		System.out.println("after: " + uk10Instances.numInstances());
//		Instances uk11Instances = arffConverter.getInstBsdOnFeatsNStrDistUSAddOn(uk10Instances, uk11AnnotatedGateFile, features, instancesName, stringDistanceThreshold);//usAnnotatedGateFile, features, instancesName, stringDistanceThreshold);
//		System.out.println("last: " + uk11Instances.numInstances());
		
		
		ArffSaver saver = new ArffSaver();		
		try {
			saver.setInstances(uk1Instances);
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
		String ukAnnotatedGateFile = "src/main/resources/grctcData/UK_AML_Annotated_CDDFragment_POS_LOB.xml";
		String usAnnotatedGateFile = "src/main/resources/grctcData/ChapterXBSA1.xml";	
		//String annotatedGateFile = "";
		//String arffFileNameNonFilt = "src/main/resources/grctcData/arff/ModalityUKAMLMulti.arff";
		String arffFileNameNonFilt = "src/main/resources/grctcData/arff/ModalityUKUSAMLBin.arff";
		List<String> features = new ArrayList<String>();		
		String instancesName = "FIROModality";		
		double stringDistanceThreshold = 50.0;		
		GateAnnotationsToFeatureVectorConverter arffConverter = new GateAnnotationsToFeatureVectorConverter(configFile);
		Instances ukInstances = arffConverter.getInstBsdOnFeatsNStrDist(ukAnnotatedGateFile, features, instancesName, stringDistanceThreshold);
		ukInstances = arffConverter.getInstBsdOnFeatsNStrDistUS(ukInstances, usAnnotatedGateFile, features, instancesName, stringDistanceThreshold);
		System.out.println(ukInstances.numInstances());
		ArffSaver saver = new ArffSaver();		
		try {
			saver.setInstances(ukInstances);
			saver.setFile(new File(arffFileNameNonFilt));		
			saver.writeBatch();
		} catch (IOException e) {
			e.printStackTrace();
		}			
	}
	
	

	public Instances getInstBsdOnFeatsNStrDistUS(Instances data, String annotatedGateFile, List<String> features, String instancesName, double stringDistanceThreshold) {
		String annotationSetName = null; 
		GateAnnotationReader gateAnnoReader = new GateAnnotationReader();
		gateAnnoReader.setDocument(annotatedGateFile);
		//Map<String, List<Annotation>> annotations = gateAnnoReader.readAnnotatedGateFile(labelsToBeUsed, annotationSetName);
		Map<String, List<Annotation>> annotations = gateAnnoReader.readAnnotatedGateFileLabels(labelsToBeUsed, annotationSetName);
		//List<String> annotationTypeList = gateAnnoReader.getAnnotationTypeList();
		//MEKA based naming of instances
		//		instancesName = instancesName + ": -C " + annotationTypeList.size() + " ";
		//		for(String annotationType : annotationTypeList)		
		//			atts.add(new Attribute(annotationType + "_Class", attVals));		
		//		for(String feature : features)
		//			atts.add(new Attribute(feature + "_Nom", tOrfVals));
		//		// - string
		//		Attribute textAttribute = new Attribute("text", (ArrayList<String>) null);
		//		atts.add(textAttribute);			
		//		data = new Instances(instancesName, atts, 0);		
		for(String annoType : annotations.keySet()) {
			List<Annotation> annoTypeAnnotations = annotations.get(annoType);
			String classLabel = null;
			if(labelsToBeUsed.contains(annoType)){
				classLabel = annoType + "_Class";
			} else {
				classLabel = "Other" + "_Class";
			}	
			for(Annotation annotation : annoTypeAnnotations) {
				boolean found = false;		
				String content;
				try {
					content = gateAnnoReader.getDocument().getContent().getContent(annotation.getStartNode().getOffset(), annotation.getEndNode().getOffset()).toString();
					if(!found){
						vals = new double[data.numAttributes()];
						vals[0] = classLabelVals.indexOf(classLabel);
						int count = 1;			
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


	public Instances getInstBsdOnFeatsNStrDist(String annotatedGateFile, List<String> features, String instancesName, double stringDistanceThreshold) {
		String annotationSetName = null; 
		GateAnnotationReader gateAnnoReader = new GateAnnotationReader();
		gateAnnoReader.setDocument(annotatedGateFile);
		//Map<String, List<Annotation>> annotations = gateAnnoReader.readAnnotatedGateFile(labelsToBeUsed, annotationSetName);		
		Map<String, List<Annotation>> annotations = gateAnnoReader.
				readAnnotatedGateFileLabels(null, annotationSetName);
		annotationTypeList = gateAnnoReader.getAnnotationTypeList();
		//	//MEKA based naming of instances
		//	instancesName = instancesName + ": -C " + annotationTypeList.size() + " ";
		//WEKA based naming of instances
		//instancesName = instancesName;// + ": -C " + annotationTypeList.size() + " ";
		classLabelVals = new ArrayList<String>();
		for(String annotationType : annotationTypeList){
			if(labelsToBeUsed.contains(annotationType)){
				classLabelVals.add(annotationType + "_Class");
			} else {
				if(!classLabelVals.contains("Other" + "_Class")){
				classLabelVals.add("Other" + "_Class");
				}
			}
		}
		//for(String annotationType : annotationTypeList)		
		//	atts.add(new Attribute(annotationType + "_Class", attVals));
		atts.add(new Attribute("Modality_class", attVals));
		for(String feature : features)
			atts.add(new Attribute(feature + "_Nom", tOrfVals));
		// - string
		Attribute textAttribute = new Attribute("text", (ArrayList<String>) null);
		atts.add(textAttribute);			
		data = new Instances(instancesName, atts, 0);

		for(String annoType : annotations.keySet()) {
			List<Annotation> annoTypeAnnotations = annotations.get(annoType);
			//int annoTypeIndex = annotationTypeList.indexOf(annoType);
			String classLabel = null;
			if(labelsToBeUsed.contains(annoType)){
				classLabel = annoType + "_Class";
			} else {
				classLabel = "Other" + "_Class";
			}	
			for(Annotation annotation : annoTypeAnnotations) {
				boolean found = false;		
				String content;
				try {
					content = gateAnnoReader.getDocument().getContent().getContent(annotation.getStartNode().getOffset(), annotation.getEndNode().getOffset()).toString();
					//					for(Instance instance : data) {
					//						int levenDist = StringDistance.computeLevenshteinDistance(instance.stringValue(textAttribute).trim(), content.trim());						
					//						if(levenDist<stringDistanceThreshold){	
					//							System.out.println("Match under 50: " + levenDist);
					//							instance.setValue(annoTypeIndex, "1");
					//							found = true;
					//							break;
					//						}						
					//					}
					if(!found){
						vals = new double[data.numAttributes()];						
						vals[0] = classLabelVals.indexOf(classLabel);
						int count = 1;
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

	public static void main(String[] args) {
		String ukamlConfigFile = "src/main/resources/load/eu.insight.finlaw.multilabel.ukaml.instances.meka";
		String modalityConfigFile = "src/main/resources/load/eu.insight.finlaw.multilabel.modality.instances.meka";
		createUKUSAMLInstances(ukamlConfigFile);
		//createModalityInstances(modalityConfigFile);
	}

}

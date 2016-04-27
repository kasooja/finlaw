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

import edu.insight.finlaw.gate.annotation.reader.GateAnnotationReader;
import edu.insight.finlaw.parsers.ArkParser;
import edu.insight.finlaw.parsers.StanfordParser;
import edu.insight.finlaw.utils.BasicFileTools;
import edu.insight.finlaw.utils.StringDistance;
import gate.Annotation;
import gate.util.InvalidOffsetException;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;

public class AMLGateAnnotationsToFeatureVectorConverter {

	private Properties config = new Properties();
	private List<String> labelsToBeUsed = new ArrayList<String>();
	private List<String> p2labels = new ArrayList<String>();
	private double[] vals;
	private ArrayList<String> attVals;
	private ArrayList<String> tOrfVals;	  
	private ArrayList<Attribute> atts = new ArrayList<Attribute>();
	private Attribute textAttribute;	
	private Attribute arkFramesAttribute;
	private Attribute posTagSeqAttribute;
	private String instancesName = "FiroAMLInstances";
	private Instances data;

	private static String bsaChapterxNoisePattern1String = "VerDate(?s).*?Regulations";
	private static String crimeActNoisePattern1String = "Crime and Courts Act 2013 \\(c\\. 22\\)";
	private static String seriousCrimeActNoisePattern1String = "Serious Organised Crime and Police Act 2005 \\(c\\. 15\\)";
	private static String terrorismActNoisePattern1String = "24-07-00 23:00:.*ch.*\\s*ACT\\s*.*\\s\\d+.*Terrorism\\s*Act\\s*2000";
	private static String doubleSpacesPatternString = "\\s\\s+"; 
	private static String weirdCharacterPatternString = "œ|æ|ß|ð|ø|å|ł|þ|�|ï|¿|½|â|€|“|”|™|˜";
	private static String weirdPatternString = "\\W\\W\\w+;";
	private static String slashNs = "\n";
	private static String singleCharAtLineStarts = "\\b\\w\\s+(.*)";
	private static String unnecessaryQuote = "\\s+”\\s+";

	private GateAnnotationReader gateAnnoReader = new GateAnnotationReader();

	public AMLGateAnnotationsToFeatureVectorConverter(String configFilePath) {
		attVals = new ArrayList<String>();
		attVals.add("0"); attVals.add("1");	
		tOrfVals = new ArrayList<String>();
		tOrfVals.add("f"); tOrfVals.add("t");
		loadConfig(configFilePath);		
		setAttributes(labelsToBeUsed);
		instancesName = instancesName + ": -C " +  labelsToBeUsed.size() ; //MEKA based naming of instances
		data  = new Instances(instancesName, atts, 0);
	}

	private void loadConfig(String configFilePath){
		try {
			config.load(new FileInputStream(configFilePath));
			setConfig("labels", labelsToBeUsed);
			setConfig("p2labels", p2labels);
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

	public Instances getLabelTaggedInstances(String annotatedGateFile, double stringDistanceThreshold) {
		Instances thiFileData = new Instances(instancesName, atts, 0);
		String annotationSetName = null;		
		gateAnnoReader.setDocument(annotatedGateFile);
		Map<String, List<Annotation>> annotations = gateAnnoReader.getGateAnnotationsLabelTagged(labelsToBeUsed, annotationSetName);
		//StringBuilder bld = new StringBuilder();		
		int counter = 0;
		for(String annoType : annotations.keySet()) {			
			List<Annotation> annoTypeAnnotations = annotations.get(annoType);
			int annoTypeIndex = labelsToBeUsed.indexOf(annoType.trim());			
			for(Annotation annotation : annoTypeAnnotations) {
				boolean found = false;		
				String content;
				try {
					System.out.print(counter++ + "\t");
					content = gateAnnoReader.getDocument().getContent().getContent(annotation.getStartNode().getOffset(), annotation.getEndNode().getOffset()).toString();
					content = removeNoise(content);	
					edu.stanford.nlp.pipeline.Annotation stanfordAnnotation = StanfordParser.getAnnotation(content);					
					List<String> sentences = StanfordParser.getSentences(stanfordAnnotation);
					StringBuilder posTagSeq = new StringBuilder();
					StringBuilder framesSeq = new StringBuilder();
					StringBuilder tokensSeq = new StringBuilder();
					for(String sentence : sentences){						
						if(sentence.length()>15){
							sentence  = sentence.replaceAll(slashNs, " ").trim();
							//							if(!ArkParser.ArkTaggedCache.containsKey(sentence)){
							//								bld.append(sentence + "\n");
							//							}
							String posTags = ArkParser.ArkTaggedCache.get(sentence);
							String tokens = ArkParser.ArkTokensCache.get(sentence);
							String arkJson = ArkParser.ArkJsonCache.get(sentence);
							String frames = ArkParser.readArkJsonFrameNamesFromSentence(arkJson);
							posTagSeq.append(posTags + " ");
							framesSeq.append(frames + " ");					
							tokensSeq.append(tokens + " ");
						}
					}
					//System.out.println(posTagSeq.toString().trim());
					//System.out.println(framesSeq.toString().trim());
					//System.out.println(tokensSeq.toString().trim());
					content = tokensSeq.toString().trim().replace("class", "classWekaAttribute").trim();
					for(Instance instance : thiFileData) {
						String instanceText = instance.stringValue(textAttribute).trim();
						int levenDist = StringDistance.computeLevenshteinDistance(instanceText, content.trim());						
						if(levenDist<stringDistanceThreshold){	
							//System.out.println("Match under 50: " + levenDist);
							instance.setValue(annoTypeIndex, "1");
							found = true;
							break;//consider removing this break
						}						
					}
					if(!found){
						vals = new double[data.numAttributes()];
						int count = 0;
						for(int i=0; i<labelsToBeUsed.size(); i++){
							vals[count] = attVals.indexOf("0");
							if(count == annoTypeIndex)
								vals[count] = attVals.indexOf("1");						
							count++;
						}		
						vals[count++] = data.attribute("text").addStringValue(content.trim());					
						vals[count++] = data.attribute("arkFrames").addStringValue(framesSeq.toString().trim());
						vals[count++] = data.attribute("posTagSeq").addStringValue(posTagSeq.toString().trim());
						Instance instance = new DenseInstance(1.0, vals);				
						thiFileData.add(instance);
					}
				} catch (InvalidOffsetException e) {
					e.printStackTrace();
				}
			}			
		}
		System.out.println();
		gateAnnoReader.cleanUp();
		//	File file = new File(annotatedGateFile);
		//	BasicFileTools.writeFile("src/main/resources/" +  file.getName() + ".sentences", bld.toString().trim());
		data.addAll(thiFileData);
		return data;
	}


	public Instances getP2TaggedInstances(String annotatedGateFile, double stringDistanceThreshold ) {
		Instances thiFileData = new Instances(instancesName, atts, 0);
		String annotationSetName = "Original markups";	
		gateAnnoReader.setDocument(annotatedGateFile);
		String tagName = "P2";		
		//	LinkedHashMap<String, Annotation> annotations = gateAnnoReader.readAnnotatedGateFileFeaturesSequence(labelsToBeUsed, annotationSetName, tagName);
		Map<String, List<Annotation>> annotations = gateAnnoReader.getGateAnnotationsP2Tagged(p2labels, annotationSetName, tagName);//readAnnotatedGateFileFeaturesSequence(labelsToBeUsed, annotationSetName, tagName);

		for(String annoType : annotations.keySet()) {			
			List<Annotation> annoTypeAnnotations = annotations.get(annoType);
		
			if(annoType.equalsIgnoreCase("customerduediligence")){
				annoType = "Customer Due Diligence";	
			}
			if(annoType.equalsIgnoreCase("enforcement")){
				annoType = "Enforcement";	
			}					
			if(annoType.equalsIgnoreCase("monitoring")){
				annoType = "Monitoring";	
			}					
			if(annoType.equalsIgnoreCase("reporting")){
				annoType = "Reporting";	
			}				
			int annoTypeIndex = labelsToBeUsed.indexOf(annoType.trim());

			for(Annotation annotation : annoTypeAnnotations) {
				boolean found = false;		
				String content;
				try {
					content = gateAnnoReader.getDocument().getContent().getContent(annotation.getStartNode().getOffset(), annotation.getEndNode().getOffset()).toString();
					content = removeNoise(content);	
					edu.stanford.nlp.pipeline.Annotation stanfordAnnotation = StanfordParser.getAnnotation(content);					
					List<String> sentences = StanfordParser.getSentences(stanfordAnnotation);
					StringBuilder posTagSeq = new StringBuilder();
					StringBuilder framesSeq = new StringBuilder();
					StringBuilder tokensSeq = new StringBuilder();
					for(String sentence : sentences){						
						if(sentence.length()>15){
							sentence  = sentence.replaceAll(slashNs, " ").trim();
							//						if(!ArkParser.ArkTaggedCache.containsKey(sentence)){
							//							bld.append(sentence + "\n");
							//						}
							String posTags = ArkParser.ArkTaggedCache.get(sentence);
							String tokens = ArkParser.ArkTokensCache.get(sentence);
							String arkJson = ArkParser.ArkJsonCache.get(sentence);
							String frames = ArkParser.readArkJsonFrameNamesFromSentence(arkJson);
							posTagSeq.append(posTags + " ");
							framesSeq.append(frames + " ");					
							tokensSeq.append(tokens + " ");
						}
					}

					content = tokensSeq.toString().trim().replace("class", "classWekaAttribute").trim();
					for(Instance instance : thiFileData) {
						String instanceText = instance.stringValue(textAttribute).trim();
						int levenDist = StringDistance.computeLevenshteinDistance(instanceText, content.trim());						
						if(levenDist<stringDistanceThreshold){	
							//System.out.println("Match under 50: " + levenDist);
							instance.setValue(annoTypeIndex, "1");
							found = true;
							break;//consider removing this break
						}						
					}
					if(!found){
						vals = new double[data.numAttributes()];
						int count = 0;
						for(int i=0; i<labelsToBeUsed.size(); i++){
							vals[count] = attVals.indexOf("0");
							if(count == annoTypeIndex)
								vals[count] = attVals.indexOf("1");						
							count++;
						}		
						vals[count++] = data.attribute("text").addStringValue(content.trim());					
						vals[count++] = data.attribute("arkFrames").addStringValue(framesSeq.toString().trim());
						vals[count++] = data.attribute("posTagSeq").addStringValue(posTagSeq.toString().trim());
						Instance instance = new DenseInstance(1.0, vals);				
						thiFileData.add(instance);
					}


					//					content = tokensSeq.toString().trim().replace("class", "classWekaAttribute").trim();
					//					vals = new double[data.numAttributes()];
					//					int count = 0;
					//					for(int i=0; i<labelsToBeUsed.size(); i++){
					//						vals[count++] = attVals.indexOf("0");
					//					}
					//					
					//					if(annoType.equalsIgnoreCase("customerduediligence")){
					//						annoType = "Customer Due Diligence";	
					//					}
					//					if(annoType.equalsIgnoreCase("enforcement")){
					//						annoType = "Enforcement";	
					//					}					
					//					if(annoType.equalsIgnoreCase("monitoring")){
					//						annoType = "Monitoring";	
					//					}					
					//					if(annoType.equalsIgnoreCase("reporting")){
					//						annoType = "Reporting";	
					//					}					
					//					vals[annoTypeIndex] = attVals.indexOf("1");
					//
					//					vals[count++] = data.attribute("text").addStringValue(content.trim());					
					//					vals[count++] = data.attribute("arkFrames").addStringValue(framesSeq.toString().trim());
					//					vals[count++] = data.attribute("posTagSeq").addStringValue(posTagSeq.toString().trim());
					//					Instance instance = new DenseInstance(1.0, vals);				
					//					thiFileData.add(instance);
				} catch (InvalidOffsetException e) {
					e.printStackTrace();
				}				
			}
		}
		gateAnnoReader.cleanUp();
		//		File file = new File(annotatedGateFile);
		//		BasicFileTools.writeFile("src/main/resources/" +  file.getName() + ".sentences", bld.toString().trim());
		data.addAll(thiFileData);
		return data;
	}


	public List<String> getFeatureList(String featureFilePath){
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

	private void setAttributes(List<String> annotationTypeList) {
		textAttribute = new Attribute("text", (ArrayList<String>) null);		
		arkFramesAttribute = new Attribute("arkFrames", (ArrayList<String>) null);
		posTagSeqAttribute = new Attribute("posTagSeq", (ArrayList<String>) null);
		for(String annotationType : annotationTypeList)		
			atts.add(new Attribute(annotationType + "_Class", attVals));			
		atts.add(textAttribute);		
		atts.add(arkFramesAttribute);
		atts.add(posTagSeqAttribute);
	}

	public String removeNoise(String content){
		content = content.replaceAll(bsaChapterxNoisePattern1String, " ").trim();
		content = content.replaceAll(crimeActNoisePattern1String, " ").trim();
		content = content.replaceAll(seriousCrimeActNoisePattern1String, " ").trim();
		content = content.replaceAll(terrorismActNoisePattern1String, " ").trim();
		content = content.replaceAll(weirdCharacterPatternString, " ").trim();
		content = content.replaceAll(weirdPatternString, " ").trim();
		content = content.replaceAll(singleCharAtLineStarts, " $1 ").trim();
		content = content.replaceAll(unnecessaryQuote, " ").trim();
		content = content.replaceAll(doubleSpacesPatternString, " ").trim();				
		return content;
	}

	public void createInstances() {
		String arffFileNameNonFilt = "src/main/resources/grctcData/annotatedamllegislation/arff/USUKAMLAll9Labels_train.arff";
		double stringDistanceThreshold = 50.0;
		String labelTagDir = "src/main/resources/grctcData/annotatedamllegislation/LabelTagged";
		File labelDir = new File(labelTagDir);
		String p2TagDir = "src/main/resources/grctcData/annotatedamllegislation/P2Tagged";
		File p2Dir = new File(p2TagDir);	
		Instances totalInstances = null;
		for(File annotatedDataFile : p2Dir.listFiles()){
			if(!annotatedDataFile.isHidden()){                 
				totalInstances = getP2TaggedInstances(annotatedDataFile.getAbsolutePath(), stringDistanceThreshold);
				System.out.println("fileDone: " + annotatedDataFile.getName());
				System.out.println("No.OfInstances: " + totalInstances.size());
			}
		}
		for(File annotatedDataFile : labelDir.listFiles()){
			if(!annotatedDataFile.isHidden()){                 
				totalInstances = getLabelTaggedInstances(annotatedDataFile.getAbsolutePath(), stringDistanceThreshold);
				System.out.println("fileDone: " + annotatedDataFile.getName());
				System.out.println("No.OfInstances: " + totalInstances.size());
			}
		}

		ArffSaver saver = new ArffSaver();		
		try {
			saver.setInstances(data);
			saver.setFile(new File(arffFileNameNonFilt));		
			saver.writeBatch();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}

	public Instance getInstance(String content, Instances trainingInstances){
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

	public static void main(String[] args) {
		String ukamlConfigFile = "src/main/resources/load/eu.insight.finlaw.multilabel.ukaml.instances.meka";
		AMLGateAnnotationsToFeatureVectorConverter featureConverter = new AMLGateAnnotationsToFeatureVectorConverter(ukamlConfigFile);
		featureConverter.createInstances();		
	}

}

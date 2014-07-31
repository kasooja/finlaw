package edu.insight.finlaw.multilabel.instances.meka;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

	
//	public static Instances getInstances(String annotatedGateFile, List<String> features) {
//		String annotationSetName = null; //null is for default
//		GateAnnotationReader gateAnnoReader = new GateAnnotationReader();
//		gateAnnoReader.setDocument(annotatedGateFile);
//		Map<String, List<Annotation>> annotations = gateAnnoReader.readAnnotatedGateFile(allAnnotationTypes, annotationSetName);
//		// - nominal
//		attVals = new ArrayList<String>();
//		attVals.add("0"); attVals.add("1");	
//
//		tOrfVals = new ArrayList<String>();
//		tOrfVals.add("f"); tOrfVals.add("t");		
//
//		for(String feature : features)
//			atts.add(new Attribute(feature + "-Nom", tOrfVals));
//		
//		// - string
//		atts.add(new Attribute("text", (ArrayList<String>) null));
//
//		atts.add(new Attribute(customerDueDiligence, attVals));
//		atts.add(new Attribute(custom, attVals));
//		atts.add(new Attribute(registration, attVals));
//		atts.add(new Attribute(supervision, attVals));
//		atts.add(new Attribute(trainingAndEducation, attVals));
//		atts.add(new Attribute(interpretation, attVals));
//		
//
//		//		data = new Instances("FiroUK: -C 16", atts, 0);
//		data = new Instances("firo", atts, 0);
//		int j = 0;
//
//		for(String annoType : annotations.keySet()) {
//			//Interpretation, Supervision, Registration, Customer Due Diligence, Training and Education
//			if(classesToBeUsedNow.contains(annoType)){
//				String searchClass = annoType.replace("_Class", "").trim();
//				List<Annotation> annoTypeAnnotations = annotations.get(searchClass);
//				int annoTypeIndex = allAnnotationTypeMap.get(annoType + "_Class");
//				for(Annotation annotation : annoTypeAnnotations) {			
//					try {
//						boolean found = false;
//						String content = gateAnnoReader.getDocument().getContent().getContent(annotation.getStartNode().getOffset(), annotation.getEndNode().getOffset()).toString();
//						for(Instance instance : data) {
//							String previousContent = instance.stringValue(11);
//							int levenDist = StringDistance.computeLevenshteinDistance(previousContent, content);						
//							if(levenDist<50){	
//								System.out.println("Match under 50: " + levenDist);
//								instance.setValue(annoTypeIndex, "1");
//								found = true;
//								break;
//							}						
//						}
//						if(!found){
//							vals = new double[data.numAttributes()];					
//							vals[0] = tOrfVals.indexOf("f");
//							vals[1] = tOrfVals.indexOf("f"); 
//							vals[2] = tOrfVals.indexOf("f"); 
//							vals[3] = tOrfVals.indexOf("f"); 
//							vals[4] = tOrfVals.indexOf("f"); 
//							vals[5] = tOrfVals.indexOf("f"); 
//							vals[6] = tOrfVals.indexOf("f"); 
//							vals[7] = tOrfVals.indexOf("f"); 
//							vals[8] = tOrfVals.indexOf("f"); 
//							vals[9] = tOrfVals.indexOf("f"); 
//							vals[10] = tOrfVals.indexOf("f");				
//
//							if(content.contains("Interpretation") || content.contains("interpretation"))
//								vals[0] = tOrfVals.indexOf("t");
//							if(content.contains("means"))
//								vals[1] = tOrfVals.indexOf("t");					
//							if(content.contains("has the meaning"))
//								vals[2] = tOrfVals.indexOf("t"); 
//							if(content.contains("have the same meaning"))
//								vals[3] = tOrfVals.indexOf("t"); 
//							if(content.contains("meaning of") || content.contains("Meaning of"))
//								vals[4] = tOrfVals.indexOf("t"); 
//							if(content.contains("have the meanings"))
//								vals[5] = tOrfVals.indexOf("t"); 
//							if(content.contains("require") || content.contains("Require"))
//								vals[6] = tOrfVals.indexOf("t"); 
//							if(content.contains("Must") || content.contains("must"))
//								vals[7] = tOrfVals.indexOf("t"); 
//							if(content.contains("should") || content.contains("Should"))
//								vals[8] = tOrfVals.indexOf("t"); 
//							if(content.contains("register") || content.contains("Register"))
//								vals[9] = tOrfVals.indexOf("t"); 
//							if(content.contains("Requirement") || content.contains("requirement"))
//								vals[10] = tOrfVals.indexOf("t");
//							vals[11] = data.attribute(11).addStringValue(content.replace("class", "classwekaattribute").trim());
//							for(int count=12; count<=16; count++){
//								vals[count] = attVals.indexOf("0");
//								if(count == annoTypeIndex){
//									//System.out.println(j++);
//									j++;
//									vals[count] = attVals.indexOf("1");
//								}
//							}			
//							data.add(new DenseInstance(1.0, vals));
//						}
//
//					} catch (InvalidOffsetException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		}
//		return data;
//	}
//

	public static List<String> getFeatureList(String featureFilePath){
		BufferedReader featureBR = BasicFileTools.getBufferedReaderFile(featureFilePath);
		List<String> features = new ArrayList<String>();
		String line = null;
		try {
			while((line = featureBR.readLine())!=null){
				line = line.trim();
				if("".equals(line))
					features.add(line);				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}	
		return features;
	}

	public static void main(String[] args) {
		String annotatedGateFile = "src/main/resources/grctcData/UK_AML_xml_annotated_firo.xml";		
		String arffFileNameNonFilt = "src/main/resources/grctcData/arff/FiveClassesFiroUKAMLMulti.arff";	
		String featureFile1 = "src/main/resources/grctcData/leona_features";
		String featureFile2 = "src/main/resources/grctcData/my_features";
		List<String> leonaFeatures = getFeatureList(featureFile1);
		List<String> myFeatures = getFeatureList(featureFile2);	
		List<String> features = new ArrayList<String>();
		features.addAll(leonaFeatures); features.addAll(myFeatures);

		GateAnnotationReader gateAnnoReader = new GateAnnotationReader();
		
//		Instances instances = getInstances(annotatedGateFile, features);
//		System.out.println(instances.numInstances());
//		instances.setRelationName("FIRO: -C -5");
//		ArffSaver saver = new ArffSaver();		
//		try {
//			saver.setInstances(instances);
//			saver.setFile(new File(arffFileNameNonFilt));		
//			saver.writeBatch();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}			

	}

}

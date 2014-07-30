package edu.insight.finlaw.multilabel.rough;

import edu.insight.finlaw.gate.annotation.reader.GateAnnotationReader;
import edu.insight.finlaw.utils.BasicFileTools;
import gate.Annotation;
import gate.util.InvalidOffsetException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.Tag;
import weka.core.converters.ArffSaver;
import weka.filters.unsupervised.attribute.StringToWordVector;

public class LawDataBinaryClassifier {

	public static final String customerDueDiligence = "customerDueDiligence";
	public static final String customerIdentificationAndVerification = "customerIdentificationAndVerification";
	public static final String defence = "defence";
	public static final String disclosure = "disclosure";
	public static final String enforcement = "enforcement";
	public static final String internalProgramme = "internalProgramme";
	public static final String interpretation = "interpretation";
	public static final String monitoring = "monitoring";
	public static final String penalty = "penalty";
	public static final String recordKeeping = "recordKeeping";
	public static final String registration = "registration";
	public static final String reporting = "reporting";
	public static final String reviewAndAppealProcedure = "reviewAndAppealProcedure";
	public static final String supervision = "supervision";
	public static final String suspiciousActivityReporting = "suspiciousActivityReporting";
	public static final String trainingAndEducation = "trainingAndEducation";
	public static Map<String, String> annoNameMap = new HashMap<String, String>();	
	private static ArrayList<String> attVals;
	private static ArrayList<String> tOrfVals;
	private static ArrayList<String> classValues;
	private static Instances data;	  
	private static ArrayList<Attribute> atts = new ArrayList<Attribute>();
	public static String[] allAnnotationTypes;
	public static Map<String, Integer> allAnnotationTypeMap = new HashMap<String, Integer>();

	static {
		allAnnotationTypes = new String[16];
		allAnnotationTypes[0] = "Customer Due Diligence";allAnnotationTypes[1] = "Customer Identification and Verification";allAnnotationTypes[2] = "Defence";
		allAnnotationTypes[3] = "Disclosure";allAnnotationTypes[4] = "Enforcement";allAnnotationTypes[5] = "Internal Programme";
		allAnnotationTypes[6] = "Interpretation";allAnnotationTypes[7] = "Monitoring";allAnnotationTypes[8] = "Penalty";
		allAnnotationTypes[9] = "Record Keeping";allAnnotationTypes[10] = "Registration";allAnnotationTypes[11] = "Reporting";
		allAnnotationTypes[12] = "Review and Appeal Procedure";allAnnotationTypes[13] = "Supervision";allAnnotationTypes[14] = "Suspicious Activity Reporting";
		allAnnotationTypes[15] = "Training and Education";
		int count = 0;
		for(String annotationType : allAnnotationTypes)
			allAnnotationTypeMap.put(annotationType, count++);
		annoNameMap.put("Customer Due Diligence", customerDueDiligence);
		annoNameMap.put("Customer Identification and Verification", customerIdentificationAndVerification);
		annoNameMap.put("Defence", defence);
		annoNameMap.put("Disclosure", disclosure);
		annoNameMap.put("Enforcement", enforcement);
		annoNameMap.put("Internal Programme", internalProgramme);
		annoNameMap.put("Interpretation", interpretation);
		annoNameMap.put("Monitoring", monitoring);
		annoNameMap.put("Penalty", penalty);
		annoNameMap.put("Record Keeping", recordKeeping);
		annoNameMap.put("Registration", registration);
		annoNameMap.put("Reporting", reporting);
		annoNameMap.put("Review and Appeal Procedure", reviewAndAppealProcedure);
		annoNameMap.put("Supervision", supervision);
		annoNameMap.put("Suspicious Activity Reporting", suspiciousActivityReporting);
		annoNameMap.put("Training and Education", trainingAndEducation);
		annoNameMap.put("?", "?");		
	}

	public static Instances getInstances(String annotatedGateFile, String[] testTexts) {
		String annotationSetName = null; //null is for default
		GateAnnotationReader gateAnnoReader = new GateAnnotationReader();
		gateAnnoReader.setDocument(annotatedGateFile);
		Map<String, List<Annotation>> annotations = gateAnnoReader.readAnnotatedGateFile(allAnnotationTypes, annotationSetName);
		// - nominal
		attVals = new ArrayList<String>();
		attVals.add("0"); attVals.add("1");

		tOrfVals = new ArrayList<String>();
		tOrfVals.add("f"); tOrfVals.add("t");

		classValues = new ArrayList<String>();
		classValues.add("customerDueDiligence");
		classValues.add("customerIdentificationAndVerification");
		classValues.add("defence");
		classValues.add("disclosure");
		classValues.add("enforcement");
		classValues.add("internalProgramme");
		classValues.add("interpretation");
		classValues.add("monitoring");
		classValues.add("penalty");
		classValues.add("recordKeeping");
		classValues.add("registration");
		classValues.add("reporting");
		classValues.add("reviewAndAppealProcedure");
		classValues.add("supervision");
		classValues.add("suspiciousActivityReporting");
		classValues.add("trainingAndEducation");
		classValues.add("?");		


		atts.add(new Attribute("Interpretation_Nom", tOrfVals));
		atts.add(new Attribute("Means_Nom", tOrfVals));
		atts.add(new Attribute("HasTheMeaning_Nom", tOrfVals));
		atts.add(new Attribute("HaveTheSameMeaning_Nom", tOrfVals));
		atts.add(new Attribute("MeaningOf_Nom", tOrfVals));
		atts.add(new Attribute("HaveTheMeanings_Nom", tOrfVals));
		atts.add(new Attribute("Require_Nom", tOrfVals));
		atts.add(new Attribute("Must_Nom", tOrfVals));		
		atts.add(new Attribute("Should_Nom",tOrfVals));
		atts.add(new Attribute("Register_Nom", tOrfVals));
		atts.add(new Attribute("Requirement_Nom", tOrfVals));
		// - string
		atts.add(new Attribute("text", (ArrayList<String>) null));
		atts.add(new Attribute("class", classValues));
		data = new Instances("FiroUK: -C 16", atts, 0);

		for(String annoType : annoNameMap.keySet()) {
			if(!annoType.equalsIgnoreCase("?")){
				List<Annotation> annoTypeAnnotations = annotations.get(annoType);
				for(Annotation annotation : annoTypeAnnotations) {			
					try {
						String content = gateAnnoReader.getDocument().getContent().getContent(annotation.getStartNode().getOffset(), annotation.getEndNode().getOffset()).toString();
						double[] features = getTheFeatures(content, data, annoType, false);
						data.add(new DenseInstance(1.0, features));
					} catch (InvalidOffsetException e) {
						e.printStackTrace();
					}
				}
			}
		}		
		for(String testText : testTexts){
			testText = testText.trim();
			//giving any random class pr ?			
			String annoType = "?";
			double[] features = getTheFeatures(testText, data, annoType, true);
			data.add(new DenseInstance(1.0, features));		
		}		
		return data;
	}

	public static double[] getTheFeatures(String content, Instances data, String annoType, boolean isTesting){
		double[] vals = new double[data.numAttributes()];					
		vals[0] = tOrfVals.indexOf("f");
		vals[1] = tOrfVals.indexOf("f"); 
		vals[2] = tOrfVals.indexOf("f"); 
		vals[3] = tOrfVals.indexOf("f"); 
		vals[4] = tOrfVals.indexOf("f"); 
		vals[5] = tOrfVals.indexOf("f"); 
		vals[6] = tOrfVals.indexOf("f"); 
		vals[7] = tOrfVals.indexOf("f"); 
		vals[8] = tOrfVals.indexOf("f"); 
		vals[9] = tOrfVals.indexOf("f"); 
		vals[10] = tOrfVals.indexOf("f");				

		if(content.contains("Interpretation") || content.contains("interpretation"))
			vals[0] = tOrfVals.indexOf("t");
		if(content.contains("means"))
			vals[1] = tOrfVals.indexOf("t");					
		if(content.contains("has the meaning"))
			vals[2] = tOrfVals.indexOf("t"); 
		if(content.contains("have the same meaning"))
			vals[3] = tOrfVals.indexOf("t"); 
		if(content.contains("meaning of") || content.contains("Meaning of"))
			vals[4] = tOrfVals.indexOf("t"); 
		if(content.contains("have the meanings"))
			vals[5] = tOrfVals.indexOf("t"); 
		if(content.contains("require") || content.contains("Require"))
			vals[6] = tOrfVals.indexOf("t"); 
		if(content.contains("Must") || content.contains("must"))
			vals[7] = tOrfVals.indexOf("t"); 
		if(content.contains("should") || content.contains("Should"))
			vals[8] = tOrfVals.indexOf("t"); 
		if(content.contains("register") || content.contains("Register"))
			vals[9] = tOrfVals.indexOf("t"); 
		if(content.contains("Requirement") || content.contains("requirement"))
			vals[10] = tOrfVals.indexOf("t");
		vals[11] = data.attribute(11).addStringValue(content.replace("class", "classwekaattribute").trim());
		vals[12] = classValues.indexOf(annoNameMap.get(annoType));	
		return vals;
	}

	public static StringToWordVector getStringToWordVectorFilter() {	
		Tag[] tags = new Tag[3];
		tags[0] = new Tag(0, "");			
		tags[1] = new Tag(1, "");
		tags[2] = new Tag(2, "");		
		SelectedTag selectedTag = new SelectedTag(1, tags);
		//Stemmer stemmer = new SnowballStemmer();
		StringToWordVector stringToWordVector = new StringToWordVector();	
		//stringToWordVector.setStemmer(stemmer);
		stringToWordVector.setWordsToKeep(4000);
		stringToWordVector.setNormalizeDocLength(selectedTag);		
		stringToWordVector.setMinTermFreq(4);
		stringToWordVector.setLowerCaseTokens(true);
		//stringToWordVector.setIDFTransform(t);
		//stringToWordVector.setTFTransform(t);
		//stringToWordVector.setOutputWordCounts(t);
		//stringToWordVector.setUseStoplist(t);
		return stringToWordVector;
	}


	public static void main(String[] args) {		
		String text = BasicFileTools.extractText("final/SampleData");
		String[] split = text.split("-----Next-----");
		String annotatedGateFile = "final/UK_AML_xml_updated.xml";		
		String arffFileNameNonFilt = "final/FiroUKBinaryClassifier.arff";		
		Instances instances = getInstances(annotatedGateFile, split);
		System.out.println(instances);		
		ArffSaver saver = new ArffSaver();		
		try {
			saver.setInstances(instances);
			saver.setFile(new File(arffFileNameNonFilt));
			saver.writeBatch();
		} catch (IOException e) {
			e.printStackTrace();
		}			
	}

}

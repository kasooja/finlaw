package edu.insight.finlaw.multilabel.rough;

import edu.insight.finlaw.gate.annotation.reader.GateAnnotationReader;
import gate.Annotation;
import gate.util.InvalidOffsetException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.Tag;
import weka.core.converters.ArffSaver;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

public class LawDataArffConverter {

	public static final String customerDueDiligence = "Customer Due Diligence_Class";
	public static final String customerIdentificationAndVerification = "Customer Identification and Verification_Class";
	public static final String defence = "Defence_Class";
	public static final String disclosure = "Disclosure_Class";
	public static final String enforcement = "Enforcement_Class";
	public static final String internalProgramme = "Internal Programme_Class";
	public static final String interpretation = "Interpretation_Class";
	public static final String monitoring = "Monitoring_Class";
	public static final String penalty = "Penalty_Class";
	public static final String recordKeeping = "Record Keeping_Class";
	public static final String registration = "Registration_Class";
	public static final String reporting = "Reporting_Class";
	public static final String reviewAndAppealProcedure = "Review and Appeal Procedure_Class";
	public static final String supervision = "Supervision_Class";
	public static final String suspiciousActivityReporting = "Suspicious Activity Reporting_Class";
	public static final String trainingAndEducation = "Training and Education_Class";

	private static double[] vals;
	private static ArrayList<String> attVals;
	private static ArrayList<String> trueOrFalseVals;

	private static Instances data;	  
	private static ArrayList<Attribute> atts = new ArrayList<Attribute>();
	public static String[] allAnnotationTypes;
	public static Map<String, Integer> allAnnotationTypeMap = new HashMap<String, Integer>();

	static {
		allAnnotationTypes = new String[16];
		allAnnotationTypes[0] = customerDueDiligence;allAnnotationTypes[1] = customerIdentificationAndVerification;allAnnotationTypes[2] = defence;
		allAnnotationTypes[3] = disclosure;allAnnotationTypes[4] = enforcement;allAnnotationTypes[5] = internalProgramme;
		allAnnotationTypes[6] = interpretation;allAnnotationTypes[7] = monitoring;allAnnotationTypes[8] = penalty;
		allAnnotationTypes[9] = recordKeeping;allAnnotationTypes[10] = registration;allAnnotationTypes[11] = reporting;
		allAnnotationTypes[12] = reviewAndAppealProcedure;allAnnotationTypes[13] = supervision;allAnnotationTypes[14] = suspiciousActivityReporting;
		allAnnotationTypes[15] = trainingAndEducation;
		int count = 0;
		for(String annotationType : allAnnotationTypes)
			allAnnotationTypeMap.put(annotationType, count++);
	}

	public static Instances getInstances(String annotatedGateFile) {
		String annotationSetName = null; //null is for default
		GateAnnotationReader gateAnnoReader = new GateAnnotationReader();
		gateAnnoReader.setDocument(annotatedGateFile);
		Map<String, List<Annotation>> annotations = gateAnnoReader.readAnnotatedGateFile(allAnnotationTypes, annotationSetName);
		// - nominal
		attVals = new ArrayList<String>();
		attVals.add("0"); attVals.add("1");	

		trueOrFalseVals = new ArrayList<String>();
		trueOrFalseVals.add("false"); attVals.add("true");	

		atts.add(new Attribute(customerDueDiligence, attVals));
		atts.add(new Attribute(customerIdentificationAndVerification, attVals));
		atts.add(new Attribute(defence, attVals));
		atts.add(new Attribute(disclosure, attVals));		
		atts.add(new Attribute(enforcement, attVals));
		atts.add(new Attribute(internalProgramme, attVals));
		atts.add(new Attribute(interpretation, attVals));
		atts.add(new Attribute(monitoring, attVals));
		atts.add(new Attribute(penalty, attVals));
		atts.add(new Attribute(recordKeeping, attVals));
		atts.add(new Attribute(registration, attVals));
		atts.add(new Attribute(reporting, attVals));
		atts.add(new Attribute(reviewAndAppealProcedure, attVals));
		atts.add(new Attribute(supervision, attVals));
		atts.add(new Attribute(suspiciousActivityReporting, attVals));
		atts.add(new Attribute(trainingAndEducation, attVals));

		// - string
		atts.add(new Attribute("text", (ArrayList<String>) null));

		atts.add(new Attribute("Interpretation_Nom", (ArrayList<String>) null));
		atts.add(new Attribute("Means_Nom", (ArrayList<String>) null));
		atts.add(new Attribute("HasTheMeaning_Nom", (ArrayList<String>) null));
		atts.add(new Attribute("HaveTheSameMeaning_Nom", (ArrayList<String>) null));
		atts.add(new Attribute("MeaningOf_Nom", (ArrayList<String>) null));
		atts.add(new Attribute("HaveTheMeanings_Nom", (ArrayList<String>) null));
		atts.add(new Attribute("Require_Nom", (ArrayList<String>) null));
		atts.add(new Attribute("Must_Nom", (ArrayList<String>) null));		
		atts.add(new Attribute("Should_Nom", (ArrayList<String>) null));
		atts.add(new Attribute("Register_Nom", (ArrayList<String>) null));
		atts.add(new Attribute("Requirement_Nom", (ArrayList<String>) null));
		
		
		data = new Instances("FiroUK: -C 16", atts, 0);

		for(String annoType : annotations.keySet()) {
			String searchClass = annoType.replace("_Class", "").trim();
			List<Annotation> annoTypeAnnotations = annotations.get(searchClass);
			int annoTypeIndex = allAnnotationTypeMap.get(annoType + "_Class");
			for(Annotation annotation : annoTypeAnnotations){			
				try {
					String content = gateAnnoReader.getDocument().getContent().getContent(annotation.getStartNode().getOffset(), annotation.getEndNode().getOffset()).toString();					
					vals = new double[data.numAttributes()];					
					for(int count=0; count<=15; count++){
						vals[count] = attVals.indexOf("0");
						if(count == annoTypeIndex)
							vals[count] = attVals.indexOf("1");
					}
					vals[16] = data.attribute(16).addStringValue(content.replace("class", "classwekaattribute").trim());
					vals[17] = trueOrFalseVals.indexOf("false");
					vals[18] = trueOrFalseVals.indexOf("false"); 
					vals[19] = trueOrFalseVals.indexOf("false"); 
					vals[20] = trueOrFalseVals.indexOf("false"); 
					vals[21] = trueOrFalseVals.indexOf("false"); 
					vals[22] = trueOrFalseVals.indexOf("false"); 
					vals[23] = trueOrFalseVals.indexOf("false"); 
					vals[24] = trueOrFalseVals.indexOf("false"); 
					vals[25] = trueOrFalseVals.indexOf("false"); 
					vals[26] = trueOrFalseVals.indexOf("false"); 
					vals[27] = trueOrFalseVals.indexOf("false");					
					
					if(content.contains("Interpretation") || content.contains("interpretation"))
						vals[17] = trueOrFalseVals.indexOf("true");
					if(content.contains("means"))
						vals[18] = trueOrFalseVals.indexOf("true");					
					if(content.contains("has the meaning"))
						vals[19] = trueOrFalseVals.indexOf("true"); 
					if(content.contains("have the same meaning"))
						vals[20] = trueOrFalseVals.indexOf("true"); 
					if(content.contains("meaning of") || content.contains("Meaning of"))
						vals[21] = trueOrFalseVals.indexOf("true"); 
					if(content.contains("have the meanings"))
						vals[22] = trueOrFalseVals.indexOf("true"); 
					if(content.contains("require") || content.contains("Require"))
						vals[23] = trueOrFalseVals.indexOf("true"); 
					if(content.contains("Must") || content.contains("must"))
						vals[24] = trueOrFalseVals.indexOf("true"); 
					if(content.contains("should") || content.contains("Should"))
						vals[25] = trueOrFalseVals.indexOf("true"); 
					if(content.contains("register") || content.contains("Register"))
						vals[26] = trueOrFalseVals.indexOf("true"); 
					if(content.contains("Requirement") || content.contains("requirement"))
						vals[27] = trueOrFalseVals.indexOf("true"); 
					
					data.add(new DenseInstance(1.0, vals));
				} catch (InvalidOffsetException e) {
					e.printStackTrace();
				}
			}
		}
		return data;
	}

	public static Filter getStringToWordVectorFilter() {	
		Tag[] tags = new Tag[3];
		tags[0] = new Tag(0, "");			
		tags[1] = new Tag(1, "");
		tags[2] = new Tag(2, "");		
		SelectedTag selectedTag = new SelectedTag(1, tags);
		//Stemmer stemmer = new SnowballStemmer();
		StringToWordVector stringToWordVector = new StringToWordVector();	
		//stringToWordVector.setStemmer(stemmer);
		stringToWordVector.setWordsToKeep(5000);
		stringToWordVector.setNormalizeDocLength(selectedTag);		
		stringToWordVector.setMinTermFreq(2);
		stringToWordVector.setLowerCaseTokens(true);
		//stringToWordVector.setIDFTransform(true);
		//stringToWordVector.setTFTransform(true);
		stringToWordVector.setOutputWordCounts(true);
		//stringToWordVector.setUseStoplist(true);
		return stringToWordVector;
	}


	public static void main(String[] args) {
		String annotatedGateFile = "resources/UK_AML_Annotated_Xml";		
		String arffFileName =  "data/FiroUKFilt.arff";
		String arffTrainFileName =  "data/FiroUKTrainFilt.arff";				
		String arffTestFileName =  "data/FiroUKTestFilt.arff";						
		Instances instances = getInstances(annotatedGateFile);
		System.out.println(instances);
		Filter stringToWordVectorFilter = getStringToWordVectorFilter();
		try {
			stringToWordVectorFilter.setInputFormat(instances);
			instances = Filter.useFilter(instances, stringToWordVectorFilter);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		instances.setRelationName("FiroUK: -C 16");
		float trainPercentage = 70;		
		Random ra = new Random();
		instances.randomize(ra);		
		System.out.println(instances.numInstances());
		int cutoff = (int) ((trainPercentage/100) * instances.numInstances());
		Instances D_train = new Instances(instances, 0, cutoff);
		Instances D_test = new Instances(instances, cutoff, instances.numInstances()-cutoff);
		System.out.println(D_train.numInstances());
		System.out.println(D_test.numInstances());		
		ArffSaver saver = new ArffSaver();		
		try {
			saver.setInstances(instances);
			saver.setFile(new File(arffFileName));
			saver.writeBatch();

			saver = new ArffSaver();
			saver.setInstances(D_train);
			saver.setFile(new File(arffTrainFileName));
			saver.writeBatch();			

			saver = new ArffSaver();
			saver.setInstances(D_test);
			saver.setFile(new File(arffTestFileName));
			saver.writeBatch();			
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}

}

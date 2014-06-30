package edu.insight.finlaw.multilabel.instances;

import edu.insight.finlaw.gate.annotation.reader.GateAnnotationReader;
import edu.insight.finlaw.utils.StringDistance;
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
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.Tag;
import weka.core.converters.ArffSaver;
import weka.core.tokenizers.NGramTokenizer;
import weka.filters.unsupervised.attribute.StringToWordVector;

public class LawDataArffConverterMerged {

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
	private static ArrayList<String> tOrfVals;

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
		int count = 12;
		for(String annotationType : allAnnotationTypes)
			allAnnotationTypeMap.put(annotationType, count++);
	}

	public static Instance getInstance(String content, Instances trainingInstances) {			
		// - nominal
		attVals = new ArrayList<String>();
		attVals.add("0"); attVals.add("1");	

		tOrfVals = new ArrayList<String>();
		tOrfVals.add("f"); tOrfVals.add("t");		
		vals = new double[trainingInstances.numAttributes()];
		for(int count=0; count<=15; count++)
			vals[count] = attVals.indexOf("0");

		int j = 0;
		vals = new double[trainingInstances.numAttributes()];					
		vals[16] = tOrfVals.indexOf("f");
		vals[17] = tOrfVals.indexOf("f"); 
		vals[18] = tOrfVals.indexOf("f"); 
		vals[19] = tOrfVals.indexOf("f"); 
		vals[20] = tOrfVals.indexOf("f"); 
		vals[21] = tOrfVals.indexOf("f"); 
		vals[22] = tOrfVals.indexOf("f"); 
		vals[23] = tOrfVals.indexOf("f"); 
		vals[24] = tOrfVals.indexOf("f"); 
		vals[25] = tOrfVals.indexOf("f"); 
		vals[26] = tOrfVals.indexOf("f");				

		if(content.contains("Interpretation") || content.contains("interpretation"))
			vals[16] = tOrfVals.indexOf("t");
		if(content.contains("means"))
			vals[17] = tOrfVals.indexOf("t");					
		if(content.contains("has the meaning"))
			vals[18] = tOrfVals.indexOf("t"); 
		if(content.contains("have the same meaning"))
			vals[19] = tOrfVals.indexOf("t"); 
		if(content.contains("meaning of") || content.contains("Meaning of"))
			vals[20] = tOrfVals.indexOf("t"); 
		if(content.contains("have the meanings"))
			vals[21] = tOrfVals.indexOf("t"); 
		if(content.contains("require") || content.contains("Require"))
			vals[22] = tOrfVals.indexOf("t"); 
		if(content.contains("Must") || content.contains("must"))
			vals[23] = tOrfVals.indexOf("t"); 
		if(content.contains("should") || content.contains("Should"))
			vals[24] = tOrfVals.indexOf("t"); 
		if(content.contains("register") || content.contains("Register"))
			vals[25] = tOrfVals.indexOf("t"); 
		if(content.contains("Requirement") || content.contains("requirement"))
			vals[26] = tOrfVals.indexOf("t");
		vals[27] = trainingInstances.attribute(27).addStringValue(content.replace("class", "classwekaattribute").trim());
		Instance instance = new DenseInstance(1.0, vals);	
		instance.setDataset(trainingInstances);
		return instance;
	}

	public static Instances getInstances(String annotatedGateFile) {
		String annotationSetName = null; //null is for default
		GateAnnotationReader gateAnnoReader = new GateAnnotationReader();
		gateAnnoReader.setDocument(annotatedGateFile);
		Map<String, List<Annotation>> annotations = gateAnnoReader.readAnnotatedGateFile(allAnnotationTypes, annotationSetName);
		// - nominal
		attVals = new ArrayList<String>();
		attVals.add("0"); attVals.add("1");	

		tOrfVals = new ArrayList<String>();
		tOrfVals.add("f"); tOrfVals.add("t");		

		atts.add(new Attribute("Interpretation-Nom", tOrfVals));
		atts.add(new Attribute("Means-Non", tOrfVals));
		atts.add(new Attribute("HasTheMeaning-Non", tOrfVals));
		atts.add(new Attribute("HaveTheSameMeaning-Non", tOrfVals));
		atts.add(new Attribute("MeaningOf-Non", tOrfVals));
		atts.add(new Attribute("HaveTheMeanings-Non", tOrfVals));
		atts.add(new Attribute("Require-Non", tOrfVals));
		atts.add(new Attribute("Must-Non", tOrfVals));		
		atts.add(new Attribute("Should-Non",tOrfVals));
		atts.add(new Attribute("Register-Non", tOrfVals));
		atts.add(new Attribute("Requirement-Non", tOrfVals));
		// - string
		atts.add(new Attribute("text", (ArrayList<String>) null));

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


		//		data = new Instances("FiroUK: -C 16", atts, 0);
		data = new Instances("firo", atts, 0);
		int j = 0;

		for(String annoType : annotations.keySet()) {
			String searchClass = annoType.replace("_Class", "").trim();
			List<Annotation> annoTypeAnnotations = annotations.get(searchClass);
			int annoTypeIndex = allAnnotationTypeMap.get(annoType + "_Class");
			for(Annotation annotation : annoTypeAnnotations) {			
				try {
					boolean found = false;
					String content = gateAnnoReader.getDocument().getContent().getContent(annotation.getStartNode().getOffset(), annotation.getEndNode().getOffset()).toString();
					for(Instance instance : data){
						String previousContent = instance.stringValue(11);
						int levenDist = StringDistance.computeLevenshteinDistance(previousContent, content);						
						if(levenDist<50){	
							System.out.println("Match under 50: " + levenDist);
							instance.setValue(annoTypeIndex, "1");
							found = true;
							break;
						}						
					}
					if(!found){
						vals = new double[data.numAttributes()];					
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
						for(int count=12; count<=27; count++){
							vals[count] = attVals.indexOf("0");
							if(count == annoTypeIndex){
								//System.out.println(j++);
								j++;
								vals[count] = attVals.indexOf("1");
							}
						}			
						data.add(new DenseInstance(1.0, vals));
					}
				} catch (InvalidOffsetException e) {
					e.printStackTrace();
				}
			}
		}
		return data;
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
		stringToWordVector.setWordsToKeep(50);
		stringToWordVector.setNormalizeDocLength(selectedTag);		
		stringToWordVector.setMinTermFreq(4);
		stringToWordVector.setLowerCaseTokens(true);
		NGramTokenizer tok = new NGramTokenizer();
		stringToWordVector.setTokenizer(tok);
		//stringToWordVector.setIDFTransform(true);
		//stringToWordVector.setTFTransform(true);
		//stringToWordVector.setOutputWordCounts(true);
		//stringToWordVector.setUseStoplist(true);
		return stringToWordVector;
	}


	public static void main(String[] args) {
		String annotatedGateFile = "src/main/resources/grctcData/UK_AML_xml_annotated_firo.xml";		
		String arffFileNameNonFilt = "src/main/resources/grctcData/arff/AllClassesFiroUKAMLMulti.arff";
		Instances instances = getInstances(annotatedGateFile);
		System.out.println(instances.numInstances());
		instances.setRelationName("FIRO: -C -16");
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

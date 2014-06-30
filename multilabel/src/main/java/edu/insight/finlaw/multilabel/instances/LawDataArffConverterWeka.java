package edu.insight.finlaw.multilabel.instances;

import edu.insight.finlaw.gate.annotation.reader.GateAnnotationReader;
import gate.Annotation;
import gate.util.InvalidOffsetException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

public class LawDataArffConverterWeka {

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
		// - string
		atts.add(new Attribute("text", (ArrayList<String>) null));	    
		// - nominal
		attVals = new ArrayList<String>();
		attVals.add("0"); attVals.add("1");	    

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

		data = new Instances("UK_AML", atts, 0);

		for(String annoType : annotations.keySet()) {
			String searchClass = annoType.replace("_Class", "").trim();
			List<Annotation> annoTypeAnnotations = annotations.get(searchClass);
			int annoTypeIndex = allAnnotationTypeMap.get(annoType + "_Class");
			for(Annotation annotation : annoTypeAnnotations){			
				try {
					String content = gateAnnoReader.getDocument().getContent().getContent(annotation.getStartNode().getOffset(), annotation.getEndNode().getOffset()).toString();					
					vals = new double[data.numAttributes()];
					vals[0] = data.attribute(0).addStringValue(content.replace("class", "classwekaattribute").trim());
					for(int count=1; count<=16; count++){
						vals[count] = attVals.indexOf("0");
						if(count == annoTypeIndex)
							vals[count] = attVals.indexOf("1");
					}
					data.add(new DenseInstance(1.0, vals));
				} catch (InvalidOffsetException e) {
					e.printStackTrace();
				}
			}
		}
		return data;
	}

}

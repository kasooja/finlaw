package edu.insight.finlaw.onto;

import java.util.HashMap;
import java.util.Map;


public class Firo_AML_1Ontology {

	public static String ontoUri = "http://www.GRCTC.com/ontologies/FIRO/FIRO-AML-v1_0#";
	private static final String prefix = "firoaml:";
	public static final String prefixUsed = "firoaml";
	public static Map<String, String> classNameUriMap = new HashMap<String, String>();
	
	static {
		//classNameUriMap.put("interpretation" , prefix + "Interpretation");
		//classNameUriMap.put("supervision" , prefix + "Supervision");
		//classNameUriMap.put("registration" , prefix + "Registration");
		classNameUriMap.put("customer due diligence" , prefix + "CustomerDueDiligence");
		classNameUriMap.put("customer identification and verification" , prefix + "CustomerIdentificationAndVerification");		
		//classNameUriMap.put("training and education" , prefix + "TrainingAndEducation");
		classNameUriMap.put("monitoring" , prefix + "Monitoring");
		classNameUriMap.put("reporting" , prefix + "Reporting");
		classNameUriMap.put("enforcement" , prefix + "Enforcement");	
	}

	public class Class {
		//classes
		public static final String interpretationClass = prefix + "Interpretation";
		public static final String supervisionClass = prefix + "Supervision";
		public static final String registrationClass = prefix + "Registration";
		public static final String customerDueDiligenceClass = prefix + "CustomerDueDiligence";
		public static final String trainingAndEducationClass = prefix + "TrainingAndEducation";		
		public static final String monitoringClass = prefix + "Monitoring";		
	}	
	
	public class Property {
		public class DataProperty{
			//data properties
		}
		
		public class ObjectProperty{
			//object properties
		}

	}

}

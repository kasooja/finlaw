package edu.insight.finlaw.onto;

import java.util.HashMap;
import java.util.Map;


public class Firo_H_1Ontology {

	public static String ontoUri = "http://www.GRCTC.com/ontologies/FIRO/FIRO-H_1.0#";
	private static final String prefix = "firoh1:";
	public static final String prefixUsed = "firoh1";
	public static Map<String, String> classNameUriMap = new HashMap<String, String>();
	
	static {
		classNameUriMap.put("interpretation" , prefix + "Interpretation");
		classNameUriMap.put("supervision" , prefix + "Supervision");
		classNameUriMap.put("registration" , prefix + "Registration");
		classNameUriMap.put("customer due diligence" , prefix + "CustomerDueDiligence");
		classNameUriMap.put("training and education" , prefix + "TrainingAndEducation");
		
	}

	public class Class {
		//classes
		public static final String interpretationClass = prefix + "Interpretation";
		public static final String supervisionClass = prefix + "Supervision";
		public static final String registrationClass = prefix + "Registration";
		public static final String customerDueDiligenceClass = prefix + "CustomerDueDiligence";
		public static final String trainingAndEducationClass = prefix + "TrainingAndEducation";
		public static final String prohibitionClass = prefix + "Prohibition";
		public static final String obligationClass = prefix + "Obligation";
			
		public static final String Modality = prefix + "Modality";
	}
	
	
	
	public class Property {

		public class DataProperty{
			//data properties
		}

		public class ObjectProperty{
			//object properties
			public static final String hasModalityObjProp = prefix + "hasModality";
		}

	}

}

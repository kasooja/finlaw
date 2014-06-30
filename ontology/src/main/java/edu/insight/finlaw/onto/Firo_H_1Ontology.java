package edu.insight.finlaw.onto;


public class Firo_H_1Ontology {

	public static String ontoUri = "http://www.GRCTC.com/ontologies/FIRO/FIRO-H_1.0#";
	private static final String prefix = "firoh1:";
	public static final String prefixUsed = "firoh1";

	public class Class {
		//classes
		public static final String interpretationClass = prefix + "Interpretation";
		public static final String supervisionClass = prefix + "Supervision";
		public static final String registrationClass = prefix + "Registration";
		public static final String customerDueDiligenceClass = prefix + "CustomerDueDiligence";
		public static final String trainingAndEducationClass = prefix + "TrainingAndEducation";
	}

	public class Property {

		public class DataProperty{
			//data properties
			public static final String hasAxisIncrementValueDatProp = prefix + "hasAxisIncrementValue";
			public static final String hasAxisStartValueDatProp = prefix + "hasAxisStartValue";
		}

		public class ObjectProperty{
			//object properties
			public static final String hasModalityObjProp = prefix + "hasModality";
			public static final String inSectionObjProp = prefix + "inSection";
			public static final String inSubSectionObjProp = prefix + "inSubSection";		
		}

	}

}

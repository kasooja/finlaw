package edu.insight.finlaw.onto;



public class Firo_H_1Ontology {

	public static String ontoUri = "http://www.GRCTC.com/Ontologies/FIRO/FIRO-H-v1_1#";
	private static final String prefix = "firoh1:";
	public static final String prefixUsed = "firoh1";

	public class Class {
		//classes
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
			//object properties
			//	public static final String inSectionObjProp = prefix + "inSection";

		}

	}

}

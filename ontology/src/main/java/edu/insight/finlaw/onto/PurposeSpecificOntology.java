package edu.insight.finlaw.onto;

public class PurposeSpecificOntology {



	public static String ontoUri = "http://www.GRCTC.com/ontologies/FIRO/Purpose_Specific_v1.0#";
	private static final String prefix = "purpose:";
	public static final String prefixUsed = "purpose";

	public class Class {
		//classes
//		public static final String sectionClass = prefix + "Section";
//		public static final String headingClass = prefix + "Heading";
//		public static final String hierElementsClass = prefix + "HierElements";
//		public static final String numClass = prefix + "Num";
//		public static final String anhierClass = prefix + "ANhier";
//
//		public static final String subsectionClass = prefix + "Subsection";
//		public static final String contentClass = prefix + "Content";
//		public static final String blockElementsClass = prefix + "BlockElements";
//		public static final String htmlBlockClass = prefix + "HTMLBlock";
//		public static final String pClass = prefix + "p";	

	}

	public class Property {

		public class DataProperty{
			//data properties
		}

		public class ObjectProperty{
			//object properties
			public static final String inSectionObjProp = prefix + "inSection";
			public static final String inSubSectionObjProp = prefix + "inSubSection";
			//have doubt the hasHTMLblock property, where it should be? PS or FIROS
			public static final String hasHTMLblockObjProp = prefix + "hasHTMLblock";
			
			
		}

	}

}

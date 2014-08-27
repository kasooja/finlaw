package edu.insight.finlaw.onto;



public class Firo_S_1Ontology {

	public static String ontoUri = "http://www.GRCTC.com/ontologies/FIRO/FIRO-S-v1_0#";								
	private static final String prefix = "firos1:";
	public static final String prefixUsed = "firos1";
		
	public class Class {
		//classes
		public static final String sectionClass = prefix + "Section";
		public static final String headingClass = prefix + "Heading";
		public static final String hierElementsClass = prefix + "HierElements";
		public static final String numClass = prefix + "Num";
		public static final String anhierClass = prefix + "ANhier";
		
		public static final String subsectionClass = prefix + "Subsection";
		public static final String contentClass = prefix + "Content";
		public static final String blockElementsClass = prefix + "BlockElements";
		public static final String htmlBlockClass = prefix + "HTMLBlock";
		public static final String pClass = prefix + "p";	
		
	}
	
	public class Property {
		
		public class DataProperty{
			//data properties
			public static final String hasStringDatProp = prefix + "hasString";
			public static final String hasNumDatProp = prefix + "hasNum";			
		}
		
		public class ObjectProperty{
			//object properties
			public static final String hasHeadingObjProp = prefix + "hasHeading";
			public static final String hasNumObjProp = prefix + "hasNum";
			public static final String hasHierElementsObjProp = prefix + "hasHierElements";
			public static final String hasANhierObjProp = prefix + "hasANhier";
			public static final String hasSubSectionObjProp = prefix + "hasSubSection";
			public static final String hasContentObjProp = prefix + "hasContent";
			public static final String hasBlockElementsObjProp = prefix + "hasBlockElements";
			//have doubt the hasHTMLblock property, where it should be? PS or FIROS			
			//public static final String hasHTMLblockObjProp = prefix + "hasHTMLblock";
			public static final String hasPObjProp = prefix + "hasP";				
		}
			
	}
	
}

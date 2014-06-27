package edu.insight.finlaw.xml;

import org.xml.sax.helpers.DefaultHandler;

public class LawXMLReaderTest {	
	public static Law law = new Law();
	public static String dataPath =  "final/uksi-2007-aml.xml";

	public static void main(String[] args) {		
		DefaultHandler handler = new LawXmlHandler(law);
		LawReader lReader = new LawReader(dataPath);
		lReader.read(handler);
		System.out.println(law);
		
	}	

}

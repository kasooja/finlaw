package edu.insight.finlaw.xml;


import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class LawXmlHandler extends DefaultHandler {

	private Part currentPart = null;	
	private P1Group currentP1Group = null;
	private P1 currentP1 = null;
	private P2 currentP2 = null;
	private P1para currentP1Para = null;	
	private boolean takeText = false;
	private String tagStringValue = null;
	private Law law; 
	private String whichP = "";

	public LawXmlHandler(Law law) {
		this.law = law;		
	}

	public void startElement(String uri, String localName, String qName, Attributes attributes)
			throws SAXException {
		if (qName.equalsIgnoreCase("Part")) {
			currentPart = new Part();
			law.parts.add(currentPart);
			whichP = "Part";
		}

		if (qName.equalsIgnoreCase("Number")) {
			takeText = true;
		}	

		if (qName.equalsIgnoreCase("P1group")) {			
			currentP1Group = new P1Group();
			currentPart.p1groups.add(currentP1Group);
		}

		if (qName.equalsIgnoreCase("P1")) {
			currentP1 = new P1();
			currentP1Group.p1s.add(currentP1);
			whichP = "P1";
		}

		if (qName.equalsIgnoreCase("P1para")) {
			currentP1Para = new P1para();
			currentP1.p1paras.add(currentP1Para);
			whichP = "P1para";					
		}

		if (qName.equalsIgnoreCase("P2")) {	
			currentP2 = new P2();
			currentP1Para.p2.add(currentP2);
			whichP = "P2";								
		}

		if (qName.equalsIgnoreCase("Pnumber")) {
			takeText = true;
		}
		if (qName.equalsIgnoreCase("Text")) {
			takeText = true;
		}
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {
		if(qName.equalsIgnoreCase("Number") && whichP.equalsIgnoreCase("Part")){
			currentPart.partNumber = tagStringValue;
			tagStringValue = "";			
			takeText = false;
		}
		if(qName.equalsIgnoreCase("Pnumber") && whichP.equalsIgnoreCase("P1")){
			currentP1.pnumber = Integer.parseInt(tagStringValue.trim());
			tagStringValue = "";			
			takeText = false;
		}
		if(qName.equalsIgnoreCase("Pnumber") && whichP.equalsIgnoreCase("P2")){
			currentP2.p2number = Integer.parseInt(tagStringValue.trim());
			tagStringValue = "";			
			takeText = false;
		}
		if(qName.equalsIgnoreCase("Text") && whichP.equalsIgnoreCase("P1para")){
			currentP1Para.p1ParaText = currentP1Para.p1ParaText + " " + tagStringValue + " ";
			tagStringValue = "";			
			takeText = false;
		}
		if(qName.equalsIgnoreCase("Text") && whichP.equalsIgnoreCase("P2")){
			currentP2.p2textValue = currentP2.p2textValue + " " + tagStringValue + " ";
			tagStringValue = "";
			takeText = false;
		}
		if(qName.equalsIgnoreCase("P2"))
			whichP = "";
		if(qName.equalsIgnoreCase("P1"))
			whichP = "";
		if(qName.equalsIgnoreCase("Part"))
			whichP = "";	
		if(qName.equalsIgnoreCase("P1para"))
			whichP = "";

	}

	public void characters(char ch[], int start, int length) throws SAXException {
		if (takeText) 
			tagStringValue = tagStringValue + " " + new String(ch, start, length);		
	}

}
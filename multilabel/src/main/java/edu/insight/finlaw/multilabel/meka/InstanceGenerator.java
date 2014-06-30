package edu.insight.finlaw.multilabel.meka;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.helpers.DefaultHandler;

import edu.insight.finlaw.onto.Firo_H_1Ontology;
import edu.insight.finlaw.onto.Firo_S_1Ontology;
import edu.insight.finlaw.rdf.TurtleFormatRDFWriter;
import edu.insight.finlaw.xml.Law;
import edu.insight.finlaw.xml.LawReader;
import edu.insight.finlaw.xml.LawXmlHandler;
import edu.insight.finlaw.xml.P1;
import edu.insight.finlaw.xml.P1Group;
import edu.insight.finlaw.xml.P1para;
import edu.insight.finlaw.xml.P2;
import edu.insight.finlaw.xml.Part;

public class InstanceGenerator {

	public static Law law = new Law();
	public static String dataPath =  "src/main/resources/grctcData/uksi-2007-aml.xml";
	public final static String lawName = Firo_S_1Ontology.prefixUsed + ":" + "AML2007";
	public final static String rdfFilePath = "src/main/resources/grctcData/rdf/instances.ttl";

	public static Map<String, String> getPrefixUriMap(){
		Map<String, String> prefixes = new HashMap<String, String>();
		prefixes.put(Firo_S_1Ontology.prefixUsed, Firo_S_1Ontology.ontoUri);
		prefixes.put(Firo_H_1Ontology.prefixUsed, Firo_H_1Ontology.ontoUri);
		return prefixes;
	}

	public static void main(String[] args) {		
		DefaultHandler handler = new LawXmlHandler(law);
		LawReader lReader = new LawReader(dataPath);
		TurtleFormatRDFWriter rdfWriter = new TurtleFormatRDFWriter();		
		rdfWriter.addPrefixes(getPrefixUriMap());
		lReader.read(handler);
		System.out.println(law);
		int htmlBlockCounter = 1;
		for(Part part : law.parts){
			for(P1Group  p1group : part.p1groups){
				for(P1 p1 : p1group.p1s) {
					String sectionNumber = p1.pnumber + "";
					String sectionResource = lawName + "-" + "section" + sectionNumber;
					Map<String, String> sectionResourceProps = new HashMap<String, String>();

					String headingResource = lawName + "-" + "heading" + sectionNumber;
					Map<String, String> headingResourceProps = new HashMap<String, String>();
					headingResourceProps.put(Firo_S_1Ontology.Property.DataProperty.hasStringDatProp, "\"" + p1group.title.trim() +"\"");
					rdfWriter.addRDF(headingResource, Firo_S_1Ontology.Class.headingClass, headingResourceProps);
					
					String sectioNumResource = lawName + "-" + "num" + sectionNumber;
					Map<String, String> sectionNumResourceProps = new HashMap<String, String>();
					sectionNumResourceProps.put(Firo_S_1Ontology.Property.DataProperty.hasNumDatProp, sectionNumber);
					rdfWriter.addRDF(sectioNumResource, Firo_S_1Ontology.Class.numClass, sectionNumResourceProps);					

					String hierElementsResource = lawName + "-" + "section" + sectionNumber + "-" + "hierElems" + sectionNumber;
					Map<String, String> hierElementsResourceProps = new HashMap<String, String>();
					
					String anhierResource = lawName + "-" + "hierElems" + sectionNumber + "-" + "ANhier" + sectionNumber;
					Map<String, String> anhierResourceProps = new HashMap<String, String>();
					//anhier resource added later at the end of loop, as it contains the information of every subsection
					
					hierElementsResourceProps.put(Firo_S_1Ontology.Property.ObjectProperty.hasANhierObjProp, anhierResource);
					rdfWriter.addRDF(hierElementsResource, Firo_S_1Ontology.Class.hierElementsClass, hierElementsResourceProps);
										
					sectionResourceProps.put(Firo_S_1Ontology.Property.ObjectProperty.hasHeadingObjProp, headingResource);
					sectionResourceProps.put(Firo_S_1Ontology.Property.ObjectProperty.hasNumObjProp, sectioNumResource);
					sectionResourceProps.put(Firo_S_1Ontology.Property.ObjectProperty.hasHierElementsObjProp, hierElementsResource);
					rdfWriter.addRDF(sectionResource, Firo_S_1Ontology.Class.sectionClass, sectionResourceProps);
					
					for(P1para p1para : p1.p1paras){						
						for(P2 p2 : p1para.p2){
							System.out.println(p2.p2textValue);
							String p2Text = p2.p2textValue;

							String subsectionResource = lawName + "-" + "section" + p1.pnumber + "-" + "sub" + p2.p2number;
							Map<String, String> subsectionResourceProps = new HashMap<String, String>();

							String numResource = lawName + "-" + "num" + p1.pnumber + "-" + p2.p2number;
							Map<String, String> numResourceProps = new HashMap<String, String>();
							numResourceProps.put(Firo_S_1Ontology.Property.DataProperty.hasNumDatProp, p2.p2number);

							String contentResource = lawName + "-" + "section" + p1.pnumber + "-" + "sub" + p2.p2number + "-content";
							Map<String, String> contentResourceProps = new HashMap<String, String>();

							String blockElementsResource = lawName + "-" + "section" + p1.pnumber + "-" + p2.p2number + "-" + "blockElems";
							Map<String, String> blockElementsResourceProps = new HashMap<String, String>();

							String htmlBlockResource = lawName + "-" + "section" + p1.pnumber + "-" + p2.p2number + "-" + "HTMLblock" + htmlBlockCounter++;
							Map<String, String> htmlBlockResourceProps = new HashMap<String, String>();

							String pResource = htmlBlockResource + "-p";
							Map<String, String> pResourceProps = new HashMap<String, String>();

							pResourceProps.put(Firo_S_1Ontology.Property.DataProperty.hasStringDatProp, "\"" + p2Text.trim() + "\"");
							rdfWriter.addRDF(pResource, Firo_S_1Ontology.Class.pClass, pResourceProps);

							htmlBlockResourceProps.put(Firo_S_1Ontology.Property.ObjectProperty.hasPObjProp, pResource);
							rdfWriter.addRDF(htmlBlockResource, Firo_S_1Ontology.Class.htmlBlockClass, htmlBlockResourceProps);							

							blockElementsResourceProps.put(Firo_S_1Ontology.Property.ObjectProperty.hasHTMLblockObjProp, htmlBlockResource);
							rdfWriter.addRDF(blockElementsResource, Firo_S_1Ontology.Class.blockElementsClass, blockElementsResourceProps);

							contentResourceProps.put(Firo_S_1Ontology.Property.ObjectProperty.hasBlockElementsObjProp, blockElementsResource);
							rdfWriter.addRDF(contentResource, Firo_S_1Ontology.Class.contentClass, contentResourceProps);

							subsectionResourceProps.put(Firo_S_1Ontology.Property.ObjectProperty.hasNumObjProp, numResource);
							subsectionResourceProps.put(Firo_S_1Ontology.Property.ObjectProperty.hasContentObjProp, contentResource);							
							rdfWriter.addRDF(subsectionResource, Firo_S_1Ontology.Class.subsectionClass, subsectionResourceProps);

							anhierResourceProps.put(Firo_S_1Ontology.Property.ObjectProperty.hasSubSectionObjProp, subsectionResource);
							//classify(p2text);							
						}						
					}
					rdfWriter.addRDF(anhierResource, Firo_S_1Ontology.Class.anhierClass, anhierResourceProps);										
				}
			}
		}
		rdfWriter.write(rdfFilePath);
	}

	private static void classify(String p2text) {
		// TODO Auto-generated method stub

	}

}

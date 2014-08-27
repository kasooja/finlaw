package edu.insight.finlaw.gate.annotation.reader;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.Factory;
import gate.Gate;
import gate.creole.ResourceInstantiationException;
import gate.util.InvalidOffsetException;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class GateAnnotationReader {

	private Document document = null;
	private List<String> annotationTypeList = new ArrayList<String>();

	public GateAnnotationReader() {
		setUp();
	}

	/**
	 * Method for initializing Gate, Corpus controller, and Corpus.
	 *
	 */
	private void setUp() {
		if (!Gate.isInitialised()) {
			File gateHome = new File("/Applications/GATE_Developer_7.1/gate.xml"); 
			if (Gate.getGateHome() == null)
				Gate.setGateHome(gateHome);
			if (Gate.getPluginsHome() == null)
				Gate.setPluginsHome(new File("/Applications/GATE_Developer_7.1/gate.xml"));
			if (Gate.getUserConfigFile() == null)
				Gate.setUserConfigFile(new File(gateHome, "/Applications/GATE_Developer_7.1/gate.xml"));
			Gate.runInSandbox(true);		       
			try {
				Gate.init();
			} catch (Exception e) {
				System.out.println("problem during initializing GATE");
				e.printStackTrace();
			}
		}
	}

	public void setDocument(String annotatedGateFile){
		File file =  new File(annotatedGateFile);			
		try {
			document = Factory.newDocument(file.toURI().toURL(), "UTF-8");
		} catch (ResourceInstantiationException | MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public Document getDocument(){
		return document;
	}

	public Map<String, List<Annotation>> readAnnotatedGateFile(List<String> annotationTypeList, String annotationSetName) {
		Set<String> annotationTypes = null;
		if(annotationTypeList != null)			
			annotationTypes = Sets.newHashSet(annotationTypeList);
		AnnotationSet annotationsInSet  =  null;
		if(annotationSetName == null)
			annotationsInSet = document.getAnnotations();			
		else 
			annotationsInSet = document.getAnnotations(annotationSetName);
		boolean takeAllTypes = false;
		if(annotationTypes == null)
			takeAllTypes = true;
		if(annotationTypes != null)
			if(annotationTypes.isEmpty())
				takeAllTypes = true;
		if(takeAllTypes)	
			annotationTypes = annotationsInSet.getAllTypes();		
		this.annotationTypeList = Lists.newArrayList(annotationTypes);		
		Map<String, List<Annotation>> annotations = new HashMap<String, List<Annotation>>();
		for (String annoType : annotationTypes) {
			annoType = annoType.replace("_Class", "").trim();
			AnnotationSet annotationTypeSet = annotationsInSet.get(annoType);
			System.out.println(annoType);
			if (annotationTypeSet != null) {
				List<Annotation> annoTypeAnnotations = gate.Utils.inDocumentOrder(annotationTypeSet);  // you will get the sorted  annotation.
				annotations.put(annoType, annoTypeAnnotations);
				System.out.println(annoTypeAnnotations.size());
				
			}
		}
		return annotations;
	} 

	public void cleanUp()	{
		document.cleanup();
	}

	public static void main(String[] args) {
		GateAnnotationReader gateAnnoReader = new GateAnnotationReader();
		String annotatedGateFile = "src/main/resources/UK_AML_xml_annotated_firo.xml";
		List<String> annotationTypes = new ArrayList<String>();
		annotationTypes.add("Penalty");
		String annotationSetName = null; //if null, then picks the default
		gateAnnoReader.setDocument(annotatedGateFile);
		Map<String, List<Annotation>> annotations = gateAnnoReader.readAnnotatedGateFile(annotationTypes, annotationSetName);
		for (String annoType : annotations.keySet()) {
			List<Annotation> annoTypeAnnotations = annotations.get(annoType);
			System.out.println("******************************");
			System.out.println(annoType);
			for(Annotation annotation : annoTypeAnnotations) {
				String content;
				int count = 0;
				try {
					content = gateAnnoReader.getDocument().getContent().getContent(annotation.getStartNode().getOffset(), annotation.getEndNode().getOffset()).toString();
					System.out.println(++count + "\t" + content);
				} catch (InvalidOffsetException e) {
					e.printStackTrace();
				}				
			}
		}
	}

	public List<String> getAnnotationTypeList() {
		return annotationTypeList;
	}

	public void setAnnotationTypeList(List<String> annotationTypeList) {
		this.annotationTypeList = annotationTypeList;
	}

}

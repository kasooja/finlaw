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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GateAnnotationReader {

	private Document document = null;	

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

	public Map<String, List<Annotation>> readAnnotatedGateFile(String[] annotationTypes, String annotationSetName) {
		AnnotationSet annotationsInSet  =  null;
		if(annotationSetName == null)
			annotationsInSet = document.getAnnotations();			
		else 
			annotationsInSet = document.getAnnotations(annotationSetName);			
		Set<String> annotationSetNames = document.getAnnotationSetNames();
		System.out.println(annotationSetNames);
		Map<String, List<Annotation>> annotations = new HashMap<String, List<Annotation>>(); 
		for (String annoType : annotationTypes) {
			annoType = annoType.replace("_Class", "").trim();			
			AnnotationSet annotationTypeSet = annotationsInSet.get(annoType);
			if (annotationTypeSet != null) {
				List<Annotation> annoTypeAnnotations = gate.Utils.inDocumentOrder(annotationTypeSet);  // you will get the sorted  annotation.
				annotations.put(annoType, annoTypeAnnotations);
			}
		}
		return annotations;
	} 

	public static void main(String[] args) {
		GateAnnotationReader gateAnnoReader = new GateAnnotationReader();
		String annotatedGateFile = "resources/UK_AML_Annotated_Xml";
		String[] annotationTypes = {"Penalty"};
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

}

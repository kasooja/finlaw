package edu.insight.finlaw.gate.annotation.reader;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.creole.ResourceInstantiationException;

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
			File gateHome = new File("/Applications/GATE_Developer_8.1/gate.xml"); 
			if (Gate.getGateHome() == null)
				Gate.setGateHome(gateHome);
			if (Gate.getPluginsHome() == null)
				Gate.setPluginsHome(new File("/Applications/GATE_Developer_8.1/gate.xml"));
			if (Gate.getUserConfigFile() == null)
				Gate.setUserConfigFile(new File(gateHome, "/Applications/GATE_Developer_8.1/gate.xml"));
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
			document = Factory.newDocument(file.toURI().toURL(), "Windows-1252");
		} catch (ResourceInstantiationException | MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public Document getDocument(){
		return document;
	}

	public Map<String, List<Annotation>> getGateAnnotationsLabelTagged(List<String> annotationTypeList, String annotationSetName) {
		Set<String> annotationTypes = null;
		int totalAnnotations = 0;
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
			//System.out.println(annoType);
			if (annotationTypeSet != null) {
				List<Annotation> annoTypeAnnotations = gate.Utils.inDocumentOrder(annotationTypeSet);  // you will get the sorted  annotation.
				annotations.put(annoType, annoTypeAnnotations);
				totalAnnotations = totalAnnotations + annoTypeAnnotations.size();
				//	System.out.println(annoTypeAnnotations.size());				
			}
		}
		System.out.println(totalAnnotations);
		return annotations;
	} 


	public Map<String, List<Annotation>> readAnnotatedGateFileFeatures(List<String> annotationFeatureList, String annotationSetName, String tagName) {
		AnnotationSet annotationsInSet  =  null;
		if(annotationSetName == null)
			annotationsInSet = document.getAnnotations();			
		else 
			annotationsInSet = document.getAnnotations(annotationSetName);		
		Map<String, List<Annotation>> annotations = new HashMap<String, List<Annotation>>();
		AnnotationSet annotationTypeSet = annotationsInSet.get(tagName);
		List<Annotation> annotationInDocumentOrder = gate.Utils.inDocumentOrder(annotationTypeSet);
		System.out.println(annotationInDocumentOrder.size());
		annotationTypeList = annotationFeatureList;
		for(Annotation annotation : annotationInDocumentOrder){
			FeatureMap features = annotation.getFeatures();
			for(String annotationFeature : annotationFeatureList) {				
				String boolValue = (String) features.get(annotationFeature.toLowerCase());
				if(boolValue != null){
					if(Boolean.parseBoolean(boolValue.trim()) == true){
						if("customeridentifcationverification".equalsIgnoreCase(annotationFeature.trim())){
							annotationFeature = "CustomerIdentificationVerification";
						}
						if(annotations.containsKey(annotationFeature.toLowerCase())){
							annotations.get(annotationFeature.toLowerCase()).add(annotation);
						}
						else {
							List<Annotation> annotationList = new ArrayList<Annotation>();
							annotationList.add(annotation);
							annotations.put(annotationFeature.toLowerCase(), annotationList);
						}
					}
				}
			}			
		}
		return annotations;
	} 

	public Map<String, List<Annotation>> getGateAnnotationsP2Tagged(List<String> annotationFeatureList, String annotationSetName, String tagName) {
		AnnotationSet annotationsInSet  =  null;
		if(annotationSetName == null)
			annotationsInSet = document.getAnnotations();			
		else 
			annotationsInSet = document.getAnnotations(annotationSetName);		
		AnnotationSet annotationTypeSet = annotationsInSet.get(tagName);
		List<Annotation> annotationInDocumentOrder = gate.Utils.inDocumentOrder(annotationTypeSet);
		System.out.println(annotationInDocumentOrder.size());
		annotationTypeList = annotationFeatureList;

		Map<String, List<Annotation>> annotations = new HashMap<String, List<Annotation>>();

		for(Annotation annotation : annotationInDocumentOrder){
			FeatureMap features = annotation.getFeatures();
			for(String annotationFeature : annotationFeatureList) {				
				String boolValue = (String) features.get(annotationFeature.toLowerCase());
				if(boolValue != null){
					if(Boolean.parseBoolean(boolValue.trim()) == true){
						if("customeridentificationverification".equalsIgnoreCase(annotationFeature.trim())){						
							annotationFeature = "customerduediligence";
						}
						if(annotations.containsKey(annotationFeature.toLowerCase())){
							annotations.get(annotationFeature.toLowerCase()).add(annotation);
						}
						else {
							List<Annotation> annotationList = new ArrayList<Annotation>();
							annotationList.add(annotation);
							annotations.put(annotationFeature.toLowerCase(), annotationList);
						}
					}
				}
			}	
		}


		return annotations;
	} 

	public void cleanUp()	{
		document.cleanup();
	}

	public List<String> getAnnotationTypeList() {
		return annotationTypeList;
	}

	public void setAnnotationTypeList(List<String> annotationTypeList) {
		this.annotationTypeList = annotationTypeList;
	}

}

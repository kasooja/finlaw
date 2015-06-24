package edu.insight.finlaw.gate.annotation.reader;

import edu.insight.finlaw.utils.BasicFileTools;
import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.creole.ResourceInstantiationException;
import gate.util.InvalidOffsetException;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class GateAnnotationReaderSeq {

	private Document document = null;
	private List<String> annotationTypeList = new ArrayList<String>();

	public GateAnnotationReaderSeq() {
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
			document = Factory.newDocument(file.toURI().toURL(), "Windows-1252");
		} catch (ResourceInstantiationException | MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public Document getDocument(){
		return document;
	}

	public Map<String, List<Annotation>> readAnnotatedGateFileLabels(List<String> annotationTypeList, String annotationSetName) {
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

	public Map<String, List<Annotation>> readAnnotatedGateFileSequenceLabels(List<String> annotationTypeList, String annotationSetName) {
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

	public LinkedHashMap<String, Annotation> readAnnotatedGateFileFeaturesSequence(List<String> annotationFeatureList, String annotationSetName, String tagName) {
		//		Set<String> annotationFeatures= null;
		//		if(annotationFeatureList != null)			
		//			annotationFeatures = Sets.newHashSet(annotationFeatureList);
		AnnotationSet annotationsInSet  =  null;
		LinkedHashMap<String, Annotation> sequenceP2s = new LinkedHashMap<String, Annotation>();
		if(annotationSetName == null)
			annotationsInSet = document.getAnnotations();			
		else 
			annotationsInSet = document.getAnnotations(annotationSetName);		
		//		boolean takeAllFeatures = false;
		//		if(annotationFeatures == null)
		//			takeAllFeatures = true;
		//		if(annotationFeatures != null)
		//			if(annotationFeatures.isEmpty())
		//				takeAllFeatures = true;
		//if(takeAllFeatures)	
		//	annotationFeatures = annotationsInSet.getAllTypes();		
		//this.annotationTypeList = Lists.newArrayList(annotationFeatures);		
		//Map<String, List<Annotation>> annotations = new HashMap<String, List<Annotation>>();
		AnnotationSet annotationTypeSet = annotationsInSet.get(tagName);
		List<Annotation> annotationInDocumentOrder = gate.Utils.inDocumentOrder(annotationTypeSet);
		//Map<String, List<Annotation>> annotations = new HashMap<String, List<Annotation>>();
		System.out.println(annotationInDocumentOrder.size());
		annotationTypeList = annotationFeatureList;
		int counter = 0;
		for(Annotation annotation : annotationInDocumentOrder){			
			FeatureMap features = annotation.getFeatures();
			//Set<String> labels = new HashSet<String>();
			String labels = null;
			for(String annotationFeature : annotationFeatureList) {				
				String boolValue = (String) features.get(annotationFeature.toLowerCase());
				if(boolValue != null) {
					if(Boolean.parseBoolean(boolValue.trim()) == true){
						//labels.add(annotationFeature);
						if(labels==null){
							labels = "";
						}
						if("customeridentifcationverification".equalsIgnoreCase(annotationFeature.trim())){
							annotationFeature = "CustomerIdentificationVerification";
						}
						labels = labels + "\t" + annotationFeature; 
					}
				}
			}			
			if(labels!=null) {
				labels = labels + "\t" + counter++;
				sequenceP2s.put(labels.trim(), annotation);
			}
		}
		return sequenceP2s;
	} 

	public void cleanUp()	{
		document.cleanup();
	}

	public static void testLabelsReading(String[] args) {
		GateAnnotationReader gateAnnoReader = new GateAnnotationReader();
		String annotatedGateFile = "src/main/resources/UK_AML_xml_annotated_firo.xml";
		List<String> annotationTypes = new ArrayList<String>();
		annotationTypes.add("Penalty");
		String annotationSetName = null; //if null, then picks the default
		gateAnnoReader.setDocument(annotatedGateFile);
		Map<String, List<Annotation>> annotations = gateAnnoReader.readAnnotatedGateFileLabels(annotationTypes, annotationSetName);
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

	public static void testFeaturesReading(String[] args) {
		GateAnnotationReader gateAnnoReader = new GateAnnotationReader();
		String annotatedGateFile = "src/main/resources/20141029_UKSI-2007-2157-made-XML-AML.xml";
		List<String> annotationTypes = new ArrayList<String>();
		annotationTypes.add("Enforcement");annotationTypes.add("reporting");
		annotationTypes.add("customerduediligence");annotationTypes.add("customeridentificationverification");
		annotationTypes.add("monitoring");
		String annotationSetName = "Original markups"; //if null, then picks the default
		gateAnnoReader.setDocument(annotatedGateFile);
		String tagName = "P2";
		Map<String, List<Annotation>> annotations = gateAnnoReader.readAnnotatedGateFileFeatures(annotationTypes, annotationSetName, tagName);

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

	public static void testFeaturesSequence(String[] args) {
		GateAnnotationReader gateAnnoReader = new GateAnnotationReader();
		String annotatedGateFile = "src/main/resources/20141029_UKSI-2007-2157-made-XML-AML.xml";
		StringBuffer sequenceDataBuffer = new StringBuffer();
		List<String> annotationTypes = new ArrayList<String>();
		annotationTypes.add("Enforcement");annotationTypes.add("Reporting");
		annotationTypes.add("CustomerDueDiligence");
		annotationTypes.add("CustomerIdentificationVerification");
		annotationTypes.add("Monitoring");
		String annotationSetName = "Original markups"; //if null, then picks the default
		gateAnnoReader.setDocument(annotatedGateFile);
		String tagName = "P2";
		LinkedHashMap<String, Annotation> annotations = gateAnnoReader.readAnnotatedGateFileFeaturesSequence(annotationTypes, annotationSetName, tagName);
		int onB = 0;
		int onC = 0;
		int continueNumber = 0;
		List<String> instances = new ArrayList<String>();
		StringBuffer testMultiD = new StringBuffer();
		Map<String, Set<String>> contentAndLabels = new HashMap<String, Set<String>>();
		for (String labelsA : annotations.keySet()) {
			Annotation a = annotations.get(labelsA);
			String[] aLabelsSplit = labelsA.split("\t");
			Set<String> aLabels = new HashSet<String>();
			for(int j=0; j<aLabelsSplit.length-1; j++){
				String label = aLabelsSplit[j].trim();
				aLabels.add(label);
			}
			continueNumber++;
			for (String labelsB : annotations.keySet()) {
				if(onB < continueNumber){
					onB++;
					continue;
				}
				Annotation b = annotations.get(labelsB);
				String[] bLabelsSplit = labelsB.split("\t");
				Set<String> bLabels = new HashSet<String>();
				for(int j=0; j<bLabelsSplit.length-1; j++){
					String label = bLabelsSplit[j].trim();
					bLabels.add(label);
				}
				for (String labelsC : annotations.keySet()) {
					if(onC < continueNumber + 1){
						onC++;
						continue;
					}					
					Annotation c = annotations.get(labelsC);
					String[] cLabelsSplit = labelsC.split("\t");
					Set<String> cLabels = new HashSet<String>();
					for(int j=0; j<cLabelsSplit.length-1; j++){
						String label = cLabelsSplit[j].trim();
						cLabels.add(label);
					}
					try {
						String contentA;
						String contentB;
						String contentC;		
						//System.out.println(aLabels);
						//	System.out.println(bLabels);
						//	System.out.println(cLabels);
						contentA = gateAnnoReader.getDocument().getContent().getContent(a.getStartNode().getOffset(), a.getEndNode().getOffset()).toString();
						contentA = contentA.replaceAll("\\W\\W\\w+;", " ").trim();
						contentA = contentA.replaceAll("\\W", " ").trim();
						contentA = contentA.replaceAll("\\s\\w\\s", " ").trim();
						contentA = contentA.replaceAll("\\b\\w\\s", " ").trim();
						contentA = contentA.replaceAll("\\s\\w\\b", " ").trim();
						contentA = contentA.replaceAll("\\s\\s+", " ").trim();


						contentB = gateAnnoReader.getDocument().getContent().getContent(b.getStartNode().getOffset(), b.getEndNode().getOffset()).toString();
						contentB = contentB.replaceAll("\\W\\W\\w+;", " ").trim();
						contentB = contentB.replaceAll("\\W", " ").trim();
						contentB = contentB.replaceAll("\\s\\w\\s", " ").trim();
						contentB = contentB.replaceAll("\\b\\w\\s", " ").trim();
						contentB = contentB.replaceAll("\\s\\w\\b", " ").trim();
						contentB = contentB.replaceAll("\\s\\s+", " ").trim();


						contentC = gateAnnoReader.getDocument().getContent().getContent(c.getStartNode().getOffset(), c.getEndNode().getOffset()).toString();
						contentC = contentC.replaceAll("\\W\\W\\w+;", " ").trim();
						contentC = contentC.replaceAll("\\W", " ").trim();
						contentC = contentC.replaceAll("\\s\\w\\s", " ").trim();
						contentC = contentC.replaceAll("\\b\\w\\s", " ").trim();
						contentC = contentC.replaceAll("\\s\\w\\b", " ").trim();
						contentC = contentC.replaceAll("\\s\\s+", " ").trim();



						//						testMultiD.append(contentA.replaceAll("\n", " ").trim() + "\t|" + aLabels + "_class\n");
						//						testMultiD.append(contentB.replaceAll("\n", " ").trim() + "\t|" + bLabels + "_class\n");
						//						testMultiD.append(contentC.replaceAll("\n", " ").trim() + "\t|" + cLabels + "_class\n");
						String conA = contentA.replaceAll("\n", " ").trim() + "\t";
						if(contentAndLabels.containsKey(conA)){
							for(String aLab : aLabels){
								contentAndLabels.get(conA).add(aLab);
							}
						} else {
							Set<String> set = new HashSet<String>();
							for(String aLab : aLabels){								
								set.add(aLab);
							}
							contentAndLabels.put(conA, set);
						}

						String conB = contentB.replaceAll("\n", " ").trim() + "\t";
						if(contentAndLabels.containsKey(conB)){
							for(String bLab : bLabels){
								contentAndLabels.get(conB).add(bLab);
							}
						} else {
							Set<String> set = new HashSet<String>();
							for(String bLab : bLabels){								
								set.add(bLab);
							}
							contentAndLabels.put(conB, set);
						}

						String conC = contentC.replaceAll("\n", " ").trim() + "\t";
						if(contentAndLabels.containsKey(conC)){
							for(String cLab : cLabels){
								contentAndLabels.get(conC).add(cLab);
							}
						} else {
							Set<String> set = new HashSet<String>();
							for(String cLab : cLabels){								
								set.add(cLab);
							}
							contentAndLabels.put(conC, set);
						}


						for(String aLabel : aLabels){
							for(String bLabel : bLabels){
								for(String cLabel : cLabels){
									sequenceDataBuffer.append(contentA.replaceAll("\n", " ").trim() + "\t|" + aLabel + "_class\n");
									sequenceDataBuffer.append(contentB.replaceAll("\n", " ").trim() + "\t|" + bLabel + "_class\n");
									sequenceDataBuffer.append(contentC.replaceAll("\n", " ").trim() + "\t|" + cLabel + "_class\n");
									String instance = contentA.replaceAll("\n", " ").trim() + "\t|" + aLabel + "_class\n" + 
											contentB.replaceAll("\n", " ").trim() + "\t|" + bLabel + "_class\n" + 
											contentC.replaceAll("\n", " ").trim() + "\t|" + cLabel + "_class\n";
									instances.add(instance);
									System.out.println(sequenceDataBuffer);
									sequenceDataBuffer.append("" + "\n");									
								}
							}
						}
					} catch (InvalidOffsetException e) {
						e.printStackTrace();
					}
					onC = 0;
					break;					
				}
				onB = 0;
				break;				
			}				
		}		
		BasicFileTools.writeFile("src/main/resources/sampleSeq.txt", sequenceDataBuffer.toString().trim());
		long seed = System.nanoTime();
		Collections.shuffle(instances, new Random(seed));
		int size = instances.size();
		double test = (0.2 * (double) size);		
		int train = size - (int) test;
		StringBuffer trainD = new StringBuffer();
		StringBuffer testD = new StringBuffer();
		int count = 0;
		for(String instance : instances){
			if(count < train){
				trainD.append(instance + "\n");
			} if(count >= train){
				testD.append(instance + "\n");
			}
			count ++;
		}

		BasicFileTools.writeFile("src/main/resources/sampleTrain.txt", trainD.toString().trim());
		BasicFileTools.writeFile("src/main/resources/sampleTest.txt", testD.toString().trim());
		for(String content : contentAndLabels.keySet()){
			Set<String> set = contentAndLabels.get(content);
			testMultiD.append(content + "|" + set + "\n");			
		}

		BasicFileTools.writeFile("src/main/resources/multiTest.txt", testMultiD.toString().trim());


		//for(String instance)
		//		String string = sequenceDataBuffer.toString();
		//		String[] split = string.split("\n");
		//		int count = 0;
		//		String instance = "";
		//		List<String> instances = new ArrayList<String>();	
		//		for(String line : split){		
		//			if(line != null) {
		//				instance = instance + line + "\n";				
		//			} 
		//			if(count!=3)
		//				count++;
		//			}
		//		}
		//		int count = 0;		
		//		for (String labels : annotations.keySet()) {
		//			System.out.println(annotations.size());
		//			System.out.println("******************************");
		//			//System.out.println(labels);
		//			String[] labelsSplit = labels.split("\t");
		//			for(int j=0; j<labelsSplit.length-1; j++){
		//				String label = labelsSplit[0].trim();
		//				System.out.println(label);
		//			}
		//			Annotation annotation = annotations.get(labels);			
		//			String content;
		//			try {
		//				//	System.out.println(++count);
		//				content = gateAnnoReader.getDocument().getContent().getContent(annotation.getStartNode().getOffset(), annotation.getEndNode().getOffset()).toString();
		//				System.out.println(content);
		//			} catch (InvalidOffsetException e) {
		//				e.printStackTrace();
		//			}		
		//		}

	}

	public static void main(String[] args) {
		testFeaturesSequence(args);
	}

	public List<String> getAnnotationTypeList() {
		return annotationTypeList;
	}

	public void setAnnotationTypeList(List<String> annotationTypeList) {
		this.annotationTypeList = annotationTypeList;
	}

}

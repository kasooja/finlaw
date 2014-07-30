package edu.insight.finlaw.multilabel.instances.meka;

import edu.insight.finlaw.gate.annotation.reader.GateAnnotationReader;
import edu.insight.finlaw.utils.StringDistance;
import gate.Annotation;
import gate.util.InvalidOffsetException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.Tag;
import weka.core.converters.ArffSaver;
import weka.core.tokenizers.NGramTokenizer;
import weka.filters.unsupervised.attribute.StringToWordVector;

public class LawDataModalityArffConverter {

	public static final String obligation = "Obligation_Class";
	public static final String prohibition = "Prohibition_Class";

	private static double[] vals;
	private static ArrayList<String> attVals;
	private static ArrayList<String> tOrfVals;

	private static Instances data;	  
	private static ArrayList<Attribute> atts = new ArrayList<Attribute>();
	public static String[] allAnnotationTypes;
	public static Map<String, Integer> allAnnotationTypeMap = new HashMap<String, Integer>();

	static {
		allAnnotationTypes = new String[2];
		allAnnotationTypes[0] = obligation; allAnnotationTypes[1] = prohibition;
		int count = 1;
		for(String annotationType : allAnnotationTypes)
			allAnnotationTypeMap.put(annotationType, count++);
	}
	
	public static Instance getInstance(String content, Instances trainingInstances) {
		attVals = new ArrayList<String>();
		attVals.add("Obligation"); attVals.add("Prohibition");	
		vals = new double[trainingInstances.numAttributes()];
		vals[0] = trainingInstances.attribute(0).addStringValue(content.replace("class", "classwekaattribute").trim());
		vals[1] = attVals.indexOf("0");	
		Instance instance = new DenseInstance(1.0, vals);	
		instance.setDataset(trainingInstances);	
		return instance;
	}


	public static Instances getInstances(String annotatedGateFile) {
		String annotationSetName = null; 
		GateAnnotationReader gateAnnoReader = new GateAnnotationReader();
		gateAnnoReader.setDocument(annotatedGateFile);
		Map<String, List<Annotation>> annotations = gateAnnoReader.readAnnotatedGateFile(allAnnotationTypes, annotationSetName);
		// - nominal
		attVals = new ArrayList<String>();
		attVals.add("0"); attVals.add("1");	

		tOrfVals = new ArrayList<String>();
		tOrfVals.add("f"); tOrfVals.add("t");		

		// - string
		atts.add(new Attribute("text", (ArrayList<String>) null));

		atts.add(new Attribute(obligation, attVals));
		atts.add(new Attribute(prohibition, attVals));

		data = new Instances("firo", atts, 0);

		for(String annoType : annotations.keySet()) {
			String searchClass = annoType.replace("_Class", "").trim();
			List<Annotation> annoTypeAnnotations = annotations.get(searchClass);
			int annoTypeIndex = allAnnotationTypeMap.get(annoType + "_Class");
			for(Annotation annotation : annoTypeAnnotations) {			
				try {
					boolean found = false;
					String content = gateAnnoReader.getDocument().getContent().getContent(annotation.getStartNode().getOffset(), annotation.getEndNode().getOffset()).toString();
					for(Instance instance : data){
						String previousContent = instance.stringValue(0);
						int levenDist = StringDistance.computeLevenshteinDistance(previousContent, content);						
						if(levenDist<50){	
							System.out.println("Match under 50: " + levenDist);
							instance.setValue(annoTypeIndex, "1");
							found = true;
							break;
						}						
					}
					if(!found){
						vals = new double[data.numAttributes()];
						vals[0] = data.attribute(0).addStringValue(content.replace("class", "classwekaattribute").trim());
						for(int count=1; count<=2; count++){
							vals[count] = attVals.indexOf("0");
							if(count == annoTypeIndex){
								vals[count] = attVals.indexOf("1");
							}
						}			
						data.add(new DenseInstance(1.0, vals));
					}
				} catch (InvalidOffsetException e) {
					e.printStackTrace();
				}
			}
		}
		return data;
	}


	public static StringToWordVector getStringToWordVectorFilter() {	
		Tag[] tags = new Tag[3];
		tags[0] = new Tag(0, "");			
		tags[1] = new Tag(1, "");
		tags[2] = new Tag(2, "");		
		SelectedTag selectedTag = new SelectedTag(1, tags);
		//Stemmer stemmer = new SnowballStemmer();
		StringToWordVector stringToWordVector = new StringToWordVector();	
		//stringToWordVector.setStemmer(stemmer);
		stringToWordVector.setWordsToKeep(50);
		stringToWordVector.setNormalizeDocLength(selectedTag);		
		stringToWordVector.setMinTermFreq(4);
		stringToWordVector.setLowerCaseTokens(true);
		NGramTokenizer tok = new NGramTokenizer();
		stringToWordVector.setTokenizer(tok);
		//stringToWordVector.setIDFTransform(true);
		//stringToWordVector.setTFTransform(true);
		//stringToWordVector.setOutputWordCounts(true);
		//stringToWordVector.setUseStoplist(true);
		return stringToWordVector;
	}


	public static void main(String[] args) {
		String annotatedGateFile = "src/main/resources/grctcData/UK_AML_Annotated_CDDFragment_POS_LOB.xml";		
		String arffFileNameNonFilt = "src/main/resources/grctcData/arff/ModalityUKAMLMulti.arff";
		Instances instances = getInstances(annotatedGateFile);
		System.out.println(instances.numInstances());
		instances.setRelationName("FIROModality: -C -2");
		ArffSaver saver = new ArffSaver();		
		try {
			saver.setInstances(instances);
			saver.setFile(new File(arffFileNameNonFilt));		
			saver.writeBatch();
		} catch (IOException e) {
			e.printStackTrace();
		}			

	}

}

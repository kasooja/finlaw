package edu.insight.finlaw.multilabel.instances.meka;

public class Snippet {
	HashMap<String, HashSet<String>> wordDistributionInClasses = new HashMap<String, HashSet<String>>();
	
	
	for (Document document : corpus) {
	
	AnnotationSet defaultAnnotationSet = document.getAnnotations();
	Set<String> set2 = defaultAnnotationSet.getAllTypes();
	
	for (String annotationName : defaultAnnotationSet.getAllTypes()) {
	HashSet<String> set = wordDistributionInClasses.get(annotationName);
	if (set == null) {
	set = new HashSet<String>();
	wordDistributionInClasses.put(annotationName, set);
	}
	
	List<Annotation> annotationList = gate.Utils.inDocumentOrder(defaultAnnotationSet.get(annotationName));
	if (annotationList != null) {
	for (Annotation annotation : annotationList) {
	String content = document.getContent().getContent(annotation.getStartNode().getOffset(), annotation.getEndNode().getOffset()).toString();
	if (content != null) {
	String[] tokens = content.split("[\\s\\t\\n\\r]+");
	for (String token : tokens) {
	set.add(token);
	}
	}
	}
	}
	}
	}
	
	for (String key :wordDistributionInClasses.keySet() ) {
	System.out.println(key + " : Unique tokens : " + wordDistributionInClasses.get(key).size());
	}
}


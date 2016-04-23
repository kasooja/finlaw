package edu.insight.finlaw.parsers;



import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.Label;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.trees.HeadFinder;


public class StanfordParser {
	//private static StanfordCoreNLP pipeline = new StanfordCoreNLP();
	private static java.util.regex.Pattern pattern;
	private static MaxentTagger tagger; //=  new MaxentTagger("lib/english-left3words/english-left3words-distsim.tagger");
	private static String patt = ".*\\((\\w+)-\\d*,\\s*(\\w+)-\\d*\\)";
	private static String patt1 = "(.+)\\((.+-\\d)+\\s*,\\s*(.+-\\d)+\\)";
	private static Pattern pattern1;
	private static String nsubjPattern = "(nsubj_.*)";
	private static Pattern subjPattern = Pattern.compile(nsubjPattern);

	// creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution 
	private static	Properties props = new Properties();
	static {
		pattern = java.util.regex.Pattern.compile(patt);
		pattern1 = Pattern.compile(patt1);
		props.put("annotators", "tokenize, ssplit, parse, lemma");
		tagger =  new MaxentTagger("src/main/resources/english-left3words/english-left3words-distsim.tagger");
	}

	private static StanfordCoreNLP sentPipeline = new StanfordCoreNLP(props);

	//returns Adjective Phrase, by taking a Parse Tree as input

	public static String lemmatizeWord(String text) {
		List<String> lemmas = new LinkedList<String>();
		Annotation document = new Annotation(text);
		// run all Annotators on this text
		sentPipeline.annotate(document);

		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		for(CoreMap sentence: sentences) {
			// Iterate over all tokens in a sentence
			for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
				// Retrieve and add the lemma for each word into the
				// list of lemmas
				lemmas.add(token.get(LemmaAnnotation.class));
			}
		}
		String lemma = new String(lemmas.get(0));    
		return lemma;
	}

	public static String getAdjP(Tree node){
		StringBuffer buffer = new StringBuffer();
		if(!node.isLeaf())   //if it is an intermediate node of parse tree, since ADJP cannot be the leaf
		{
			Label label = node.label();
			if(label.toString().equalsIgnoreCase("ADJP")){
				List<Tree> leaves = node.getLeaves();		//gets all the end leaves of the node labeled as ADJP
				for(Tree leave : leaves)
					buffer.append(leave.label());					
				//System.out.println(buffer);
			}
		}
		return buffer.toString().trim();
	}

	//returns a list of parse tree nodes corresponding to a POS tag, takes as input a Parsed tree and the desired tag	
	private static ArrayList<Tree> extractTag(Tree t, String tag) {
		ArrayList<Tree> wanted = new ArrayList<Tree>();
		if (t.label().value().equals(tag) ){
			wanted.add(t);
			for (Tree child : t.children()){
				ArrayList<Tree> temp = new ArrayList<Tree>();
				temp = extractTag(child, tag);
				if(temp.size()>0){
					int o =-1;
					o = wanted.indexOf(t);
					if(o!=-1)
						wanted.remove(o);
				}
				wanted.addAll(temp);
			}
		}
		else
			for (Tree child : t.children())
				wanted.addAll(extractTag(child, tag));
		return wanted;
	}

	public static Annotation getAnnotation(String text){
		Annotation annotation = new Annotation(text);
		sentPipeline.annotate(annotation); 		
		return annotation;
	}

	public static Map<String, List<String>> getTagText(String text, List<String> tags){
		Map<String, List<String>> tagTextMap = new HashMap<String, List<String>>();
		Annotation annotation = new Annotation(text);		
		sentPipeline.annotate(annotation);
		List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
		if (sentences != null && sentences.size() > 0) {
			CoreMap sentence = sentences.get(0);
			Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);	
			for(String tag : tags){
				ArrayList<Tree> tagTexts = extractTag(tree, tag);
				List<String> list = tagTextMap.get(tag);
				if(list == null) {			
					tagTextMap.put(tag, new ArrayList<String>());			
					for(Tree tagTextTree : tagTexts){
						String tagTextString = Sentence.listToString(tagTextTree.yield());
						tagTextMap.get(tag).add(tagTextString);
					}		
				}		
			}
		}
		return tagTextMap;
	}


	public static Map<String, List<String>> getTagText(Annotation annotation, List<String> tags){
		Map<String, List<String>> tagTextMap = new HashMap<String, List<String>>();
		List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
		if (sentences != null && sentences.size() > 0) {
			CoreMap sentence = sentences.get(0);
			Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
			for(String tag : tags){
				ArrayList<Tree> tagTexts = extractTag(tree, tag);
				List<String> list = tagTextMap.get(tag);
				if(list == null) {			
					tagTextMap.put(tag, new ArrayList<String>());			
					for(Tree tagTextTree : tagTexts){
						String tagTextString = Sentence.listToString(tagTextTree.yield());
						tagTextMap.get(tag).add(tagTextString);
					}		
				}		
			}
		}
		return tagTextMap;
	}

	//breaks input text into sentences
	public static List<String> getSentences(String text){

		// create an empty Annotation just with the given text	
		List<String> sentences = new ArrayList<String>();

		Annotation document = new Annotation(text);   
		//each text is treated as an Annotation type in order to perform any Stanford NLP task on it

		// run all Annotators on this text
		sentPipeline.annotate(document);

		// these are all the sentences in this document
		// a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
		List<CoreMap> sents = document.get(SentencesAnnotation.class);
		for(CoreMap sentence: sents) 		{
			//System.out.println(sentence);
			sentences.add(sentence.toString());
		}

		return sentences;
	}

	//returns an Arraylist of clauses present in the input sentence
	public static List<String> getClauses(String sentence){
		List<String> clauses = new ArrayList<String>();		
		String clauseTag = "S";
		Annotation annotation = new Annotation(sentence);
		sentPipeline.annotate(annotation);
		List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
		if (sentences != null && sentences.size() > 0) {
			CoreMap sent = sentences.get(0);
			Tree tree = sent.get(TreeCoreAnnotations.TreeAnnotation.class);
			ArrayList<Tree> tagTexts = extractTag(tree, clauseTag);		
			for(Tree tagTextTree : tagTexts){
				String clause = Sentence.listToString(tagTextTree.yield());
				clauses.add(clause);
			}
		}

		String remainingClause = new String(sentence);
		for(String clause : clauses)
			remainingClause = remainingClause.replace(clause, "------");
		String[] splits = remainingClause.split("------");
		for(String split : splits){
			if(!split.trim().matches("\\W*"))			
				clauses.add(split.trim());
		}

		return clauses;
	}


	public static void typedDependencies(String text){
		//		LexicalizedParser lp = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");
		////		lp.apply(sent);
		////		lp.apply(sent).pennPrint();
		//		
		//		TreebankLanguagePack tlp = lp.getOp().langpack();
		//		Tokenizer<? extends HasWord> toke = tlp.getTokenizerFactory().getTokenizer(new StringReader(text));
		//		List<? extends HasWord> sentence = toke.tokenize();
		//		//lp.apply(sentence).
		//		Set<Dependency<Label,Label,Object>> dependencies = lp.apply(sentence).dependencies();
		//		System.out.println(dependencies);


		// create an empty Annotation just with the given text	
		List<String> sentences = new ArrayList<String>();

		Annotation document = new Annotation(text);   
		//each text is treated as an Annotation type in order to perform any Stanford NLP task on it

		// run all Annotators on this text
		sentPipeline.annotate(document);

		// these are all the sentences in this document
		// a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
		List<CoreMap> sents = document.get(SentencesAnnotation.class);
		for(CoreMap sente: sents) 		{
			Tree tree = sente.get(TreeAnnotation.class);
			// Get dependency tree
			TreebankLanguagePack tlp = new PennTreebankLanguagePack();
			GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
			GrammaticalStructure gs = gsf.newGrammaticalStructure(tree);
			Collection<TypedDependency> td = gs.typedDependenciesCollapsed();
			System.out.println(td);

			Object[] list = td.toArray();
			System.out.println(list.length);
			TypedDependency typedDependency;
			for (Object object : list) {
				typedDependency = (TypedDependency) object;
				//System.out.println("Dependency Name " + typedDependency.dep().nodeString()+ " :: "+ "Node "+typedDependency.reln());
				if (typedDependency.reln().getShortName().equals("something")) {
					//your code
				}
			}
			//System.out.println(sentence);
			sentences.add(sente.toString());
		}	
	}





	public static ArrayList<String> getDependencyTriplesPos(String text, Annotation document, LinkedHashMap<CoreMap, String> sentencePosTagMap) {	
		ArrayList<String> dependencies = new ArrayList<String>();
		for(CoreMap sente: sentencePosTagMap.keySet()){			
			String posTagger  = sentencePosTagMap.get(sente);			
			String[] split = posTagger.split("\\s+");
			List<String> postags = new ArrayList<String>();
			int count = 1;
			for(String s : split){
				s = s.replaceFirst("_", "-"+String.valueOf(count++) + "_");
				postags.add(s);
			}
			Tree tree = sente.get(TreeAnnotation.class);
			TreebankLanguagePack tlp = new PennTreebankLanguagePack();
			GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
			GrammaticalStructure gs = gsf.newGrammaticalStructure(tree);
			Collection<TypedDependency> td = gs.typedDependenciesCollapsed();
			for(TypedDependency typedD : td){
				String typedDString = typedD.toString();
				Matcher matcher = pattern1.matcher(typedDString);
				if(matcher.find()){
					String dependencyName = matcher.group(1).trim();				
					String arg1 = matcher.group(2).trim();
					String arg2 = matcher.group(3).trim();
					boolean arg1Found = false;
					boolean arg2Found = false;

					for(String wrdDpndcy : postags){
						wrdDpndcy = wrdDpndcy.trim();
						if(wrdDpndcy.contains(arg1) && !arg1Found){
							try {
								arg1 = wrdDpndcy.replaceAll(arg1 + "_", "").trim();
							} catch(Exception e){
								//e.printStackTrace();
								arg1 = Pattern.quote(arg1);
								arg1 = wrdDpndcy.replaceAll(arg1 + "_", "").trim();
							}
							arg1Found = true;
						}
						if(wrdDpndcy.contains(arg2) && !arg2Found){
							try {
								arg2 = wrdDpndcy.replaceAll(arg2 + "_", "").trim();
							} catch (Exception e){
								//e.printStackTrace();
								arg2 = Pattern.quote(arg2);
								arg2 = wrdDpndcy.replaceAll(arg2 + "_", "").trim();								
							}		
							arg2Found = true;
						}
						if(arg1Found && arg2Found){
							break;
						}
					}					
					String triple = dependencyName+"__"+arg1+"__"+arg2;
					dependencies.add(triple);
				}

			}
		}
		return dependencies ;	
	}

	public static ArrayList<String> getDependencies(String text, Annotation annotation, LinkedHashMap<CoreMap, String> sentencePosTagMap) {	
		ArrayList<String> dependencies = new ArrayList<String>();
		for(CoreMap sente: sentencePosTagMap.keySet()){			
			Tree tree = sente.get(TreeAnnotation.class);
			TreebankLanguagePack tlp = new PennTreebankLanguagePack();
			GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
			GrammaticalStructure gs = gsf.newGrammaticalStructure(tree);
			Collection<TypedDependency> td = gs.typedDependenciesCollapsed();
			for(TypedDependency typedD : td){
				String typedDString = typedD.toString();
				Matcher matcher = pattern1.matcher(typedDString);
				if(matcher.find()){
					String dependencyName = matcher.group(1).trim();				
					dependencies.add(dependencyName);
				}
			}
		}
		return dependencies ;
	}

	public static String getAllDependenciesWithPos(String sentence, Annotation annotation, LinkedHashMap<CoreMap, String> sentencePosTagMap){
		List<String> dependencyTriples = new ArrayList<String>();
		dependencyTriples = getDependencyTriplesPos(sentence, annotation, sentencePosTagMap);	
		StringBuilder bldr = new StringBuilder();
		for(String dependency : dependencyTriples){	
			bldr.append(dependency + " ");
		}
		if("".equals(bldr.toString().trim())){
			bldr.append("no_dependenciesWithPos");
		}		
		return bldr.toString().trim();		
	}

	public static String getAllDependencies(String sentence, Annotation annotation, LinkedHashMap<CoreMap, String> sentencePosTagMap){
		List<String> dependencyTriples = new ArrayList<String>();
		dependencyTriples = getDependencies(sentence, annotation, sentencePosTagMap);	
		StringBuilder bldr = new StringBuilder();
		for(String dependency : dependencyTriples){	
			bldr.append(dependency + " ");
		}
		if("".equals(bldr.toString().trim())){
			bldr.append("no_dependencies");
		}		
		return bldr.toString().trim();		
	}


	public static String getNSubjDependency(String sentence, Annotation annotation, LinkedHashMap<CoreMap, String> sentencePosTagMap){		
		List<String> dependencyTriples = new ArrayList<String>();
		dependencyTriples = getDependencyTriplesPos(sentence, annotation, sentencePosTagMap);	
		StringBuilder bldr = new StringBuilder();
		for(String dependency : dependencyTriples){	
			Matcher matcher = subjPattern.matcher(dependency);
			if(matcher.find()) {		
				bldr.append(dependency + " ");
			}
		}
		if("".equals(bldr.toString().trim())){
			bldr.append("no_nsubj");
		}
		return bldr.toString().trim();
	}


	public static Map<String, List<String>> getTypedDependencies(String text, List<String> aspects, List<String> typedDepencies) {
		Map<String, List<String>> aspectWithDependentWords = new HashMap<String, List<String>>();
		// create an empty Annotation just with the given text	
		//List<String> sentences = new ArrayList<String>();
		Annotation document = new Annotation(text);   
		// run all Annotators on this text
		sentPipeline.annotate(document);
		List<CoreMap> sents = document.get(SentencesAnnotation.class);
		for(CoreMap sente: sents) 	{
			String sentence = sente.toString();
			Tree tree = sente.get(TreeAnnotation.class);
			TreebankLanguagePack tlp = new PennTreebankLanguagePack();
			GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
			GrammaticalStructure gs = gsf.newGrammaticalStructure(tree);
			Collection<TypedDependency> td = gs.typedDependenciesCollapsed();
			for(String aspect : aspects) {
				if(sentence.contains(aspect)) {
					for(TypedDependency typedD : td){
						if(typedDepencies.contains(typedD.reln().toString())) {
							String typedDString = typedD.toString();
							Matcher matcher = pattern.matcher(typedDString);
							if(matcher.find()){
								String one = matcher.group(1).trim();
								String two = matcher.group(2).trim();
								if(one.equalsIgnoreCase(aspect)){
									if(!aspectWithDependentWords.containsKey(aspect))
										aspectWithDependentWords.put(aspect, new ArrayList<String>());
									aspectWithDependentWords.get(aspect).add(two);
								} else if(two.equalsIgnoreCase(aspect)){
									if(!aspectWithDependentWords.containsKey(aspect))
										aspectWithDependentWords.put(aspect, new ArrayList<String>());
									aspectWithDependentWords.get(aspect).add(one);										
								}
							}
						}
					}
				}
			}
		}
		return aspectWithDependentWords;
	}

	//	public static String posTagger(String sentence){
	//		//Map<String, String> posWordMap = new HashMap<String,String>();
	//		/*		List<String> leafTagList = new ArrayList<String>();
	//		leafTagList.add("CC"); leafTagList.add("CD"); leafTagList.add("DT"); leafTagList.add("EX");
	//		leafTagList.add("FW");leafTagList.add("IN");leafTagList.add("JJ");leafTagList.add("JJR");
	//		leafTagList.add("JJS");leafTagList.add("LS");leafTagList.add("MD");leafTagList.add("NN");
	//		leafTagList.add("NNS"); leafTagList.add("NNP");leafTagList.add("NNPS"); leafTagList.add("PDT");
	//		leafTagList.add("PRP");leafTagList.add("POS");
	//		leafTagList.add("RB");leafTagList.add("RBR");leafTagList.add("RBS");
	//		leafTagList.add("RP");leafTagList.add("SYM");leafTagList.add("TO");leafTagList.add("UH");
	//		leafTagList.add("VB");leafTagList.add("VBD");
	//		leafTagList.add("VBG");
	//		leafTagList.add("VBN");
	//		leafTagList.add("VBP");
	//		leafTagList.add("VBZ"); 
	//		leafTagList.add("WDT");
	//		leafTagList.add("WP");
	//		leafTagList.add("WP$"); 
	//		leafTagList.add("WRB");*/
	//		//posWordMap = getTagText(sentence, leafTagList);
	//
	//
	//		String taggedSentence = tagger.tagString(sentence);
	//
	//
	//		return taggedSentence;
	//
	//
	//	}

	public static LinkedHashMap<CoreMap, String> posTagger(Annotation annotation, String sentence){
		LinkedHashMap<CoreMap, String> sentencePosTagMap = new LinkedHashMap<CoreMap, String>();	
		List<CoreMap> sents = annotation.get(SentencesAnnotation.class);		
		for(CoreMap sente: sents){
			String taggedSentence = tagger.tagString(sentence);
			sentencePosTagMap.put(sente, taggedSentence);
		}

		return sentencePosTagMap;
	}

	public static String posTagger(String sentence){
		String taggedSentence = tagger.tagString(sentence);
		return taggedSentence;
	}

	public static String getPosSequence(LinkedHashMap<CoreMap, String> sentencePosTagMap){
		StringBuilder bldr = new StringBuilder();
		for(CoreMap sente: sentencePosTagMap.keySet()){			
			String posTagger  = sentencePosTagMap.get(sente);			
			String[] split = posTagger.split("\\s+");
			for(String s : split){
				String pos = s.split("_")[1];				
				bldr.append(pos + " ");
			}
		}

		return bldr.toString().trim();
	}

	public static double getTotalNoVerbNormalized(LinkedHashMap<CoreMap, String> sentencePosTagMap) {	
		double totalCount = 0;
		double score = 0.0;		
		for(CoreMap sente: sentencePosTagMap.keySet()){			
			String posTagger  = sentencePosTagMap.get(sente);			
			String[] split = posTagger.split("\\s+");
			Set<String> vbs = new HashSet<String>();
			vbs.add("VBZ"); vbs.add("VBG"); vbs.add("VB"); vbs.add("VBD"); vbs.add("VBP");
			vbs.add("VBN");

			for(String s : split){			
				String pos = s.split("_")[1];
				if(vbs.contains(pos)){
					try {
						score++;						
					} catch(Exception e){
						continue;
					}				
				}			
				totalCount++;				
			}		
		}
		if(totalCount == 0){
			return 0.0;
		}
		return score/totalCount;
	}

	public static String getNerTypes(Annotation annotation, LinkedHashMap<CoreMap, String> sentencePosTagMap){
		StringBuilder nerTypeString = new StringBuilder();
		nerTypeString.append(" ");
		for(CoreMap sente: sentencePosTagMap.keySet()){			
			for (CoreLabel token: sente.get(TokensAnnotation.class)) {
				String ne = token.get(NamedEntityTagAnnotation.class);
				System.out.println(ne);
				if(!"O".equalsIgnoreCase(ne) && !"ORDINAL".equalsIgnoreCase(ne) && !"DURATION".equalsIgnoreCase(ne) && !"NUMBER".equalsIgnoreCase(ne)
						&& !"DATE".equalsIgnoreCase(ne)){
					nerTypeString.append(ne + " ");
				}
			}
		}
		return nerTypeString.toString().trim();
	}


	public LinkedHashMap<String, String> getWordPhraseMap(String text){
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		Annotation annotation = new Annotation(text);
		sentPipeline.annotate(annotation);
		List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
		if (sentences != null && sentences.size() > 0) {
			for(CoreMap sentence: sentences) 	{			
				Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
				for (CoreLabel token: sentence.get(TokensAnnotation.class)) {

				}


				ArrayList<Tree> tagTexts = extractTag(tree, "S");		
			}
		}
		return map;
	}


	public static String getGoverningVerb(String text) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		Annotation annotation = new Annotation(text);
		sentPipeline.annotate(annotation);
		List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
		if (sentences != null && sentences.size() > 0) {
			for(CoreMap sentence: sentences) {			
				Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
				for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
					Iterator<Tree> treeIterate = tree.iterator();
					while (treeIterate.hasNext()) {
						Tree child = treeIterate.next();					
						if (child.isLeaf()) {
							if (child.parent(tree).parent(tree).parent(tree).value().contains("VP") &&
									child.parent(tree).parent(tree).value().contains("VP")) {

								//String governingVerb = child.parent(tree).parent(tree).parent(tree).
								//headTerminal(hf).value().toLowerCase();// + token.getCoveredText();
								//System.out.println(governingVerb);
								//return governingVerb;		
							}

							if (child.parent(tree).parent(tree).parent(tree).value().contains("VP") &&
									child.parent(tree).parent(tree).value().contains("ADJP")) {
								//								String governingVerb = child.parent(tree).parent(tree).parent(tree).
								//										headTerminal(hf).value().toLowerCase();// + token.getCoveredText();
								//								//					System.out.println(governingVerb);
								//								return governingVerb;
							}

							if (child.parent(tree).parent(tree).parent(tree).value().contains("VP") &&
									child.parent(tree).parent(tree).value().contains("NN")) {
								//								String governingVerb = child.parent(tree).parent(tree).parent(tree).
								//										headTerminal(hf).value().toLowerCase(); // + token.getCoveredText();
								//								//					System.out.println(governingVerb);
								//								return governingVerb;					
							}
						}
					}
				}
			}
		}
		return null;
	}


	public static void main(String[] args) throws IOException {
		String text = "Our room was tiny, and the bath was small too.";
		//List<String> clauses = getClauses(text);
		//for(String clause:clauses)
		//	System.out.println(clause);
		//System.out.println(posTagger(text));



	}
}

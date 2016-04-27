package edu.insight.finlaw.parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import edu.insight.finlaw.utils.BasicFileTools;

public class ArkParser {

	//public static final String ARKURL = "http://demo.ark.cs.cmu.edu/parse/api/v1/parse?sentence=";
	public static Map<String, String> ArkJsonCache;
	public static Map<String, String> ArkTaggedCache;
	public static Map<String, String> ArkTokensCache;
	//private static final String ArkJsonCachePath = "src/main/resources/ArkJsonCache.ser";
	private static final String ArkJsonDir = "src/main/resources/ArkFrames";
	//private static int noOfTextsAtLoadTime = 0;
	//private static Map<String, String> textFrames = new HashMap<String, String>();
	//private static Map<String, String> queryFrames = new HashMap<String, String>();

	static {
		ArkJsonCache = new HashMap<String, String>();
		ArkTaggedCache = new HashMap<String, String>();
		ArkTokensCache = new HashMap<String, String>();
		//	loadCache(ArkJsonCachePath);
		//	readAllFrames();
		readAllArkJsonFromSupDir(ArkJsonDir);
	}

	//	@SuppressWarnings("unchecked")
	//	private static void loadCache(String ArkJsonCachePath){
	//		if(new File(ArkJsonCachePath).exists()){
	//			ArkJsonCache = ((Map<String, String>) SerializationUtils.readObject(new File(ArkJsonCachePath)));
	//			noOfTextsAtLoadTime = ArkJsonCache.size();
	//		}
	//	}

	//	private static void saveCache(String ArkJsonCachePath){		
	//		SerializationUtils.saveObject(ArkJsonCache, new File(ArkJsonCachePath));
	//	}

	//	private static void disconnectArkParser(){
	//		if(ArkJsonCache.size() > noOfTextsAtLoadTime){
	//			if(new File(ArkJsonCachePath).exists() ){
	//				new File(ArkJsonCachePath).delete();
	//			} 
	//			saveCache(ArkJsonCachePath);
	//		}
	//	}

	//	public static String httpGet(String urlStr){
	//		URL url;
	//		try {
	//			url = new URL(urlStr);
	//			HttpURLConnection conn =
	//					(HttpURLConnection) url.openConnection();
	//			if (conn.getResponseCode() != 200) {
	//				throw new IOException(conn.getResponseMessage());
	//			}
	//			BufferedReader rd = new BufferedReader(
	//					new InputStreamReader(conn.getInputStream()));
	//			StringBuilder sb = new StringBuilder();
	//			String line;
	//			while ((line = rd.readLine()) != null) {
	//				sb.append(line + "\n");
	//			}
	//			rd.close();
	//			conn.disconnect();
	//			return sb.toString();
	//		} catch (MalformedURLException e) {		
	//			e.printStackTrace();
	//		} catch (IOException e) {			
	//			e.printStackTrace();
	//		}
	//		return null;
	//	}

	//	public static String frameNetParse(String query){
	//		try {
	//			TimeUnit.SECONDS.sleep(1);
	//		} catch (InterruptedException e1) {
	//			e1.printStackTrace();
	//		}
	//		try {
	//			query = URLEncoder.encode(query, "UTF-8");
	//		} catch (UnsupportedEncodingException e) {
	//			e.printStackTrace();
	//		}	
	//		if(ArkJsonCache.containsKey(query)){
	//			return ArkJsonCache.get(query);
	//		}
	//		String jsonResult = null;
	//		try{
	//			jsonResult = httpGet(ARKURL + query);
	//			ArkJsonCache.put(query, jsonResult);
	//		} catch(Exception e){
	//			ArkJsonCache.put(query, null);
	//		}
	//
	//		return jsonResult;
	//	}



	public static void main(String[] args) throws IOException {
		String query = "Supervisory authority must promptly report the Serious Organised Crime Agency .";
		//String query = "I am going to the market again.";
		String json = ArkParser.ArkJsonCache.get(query);
		System.out.println(json);
		String frames = readArkJsonFrameNamesFromSentence(json);
		System.out.println(frames);
		//String json = frameNetParse(query);
		//String frames = readArkJsonFrameNames(json);
		//System.out.println(json);
		//ArkParser.disconnectArkParser();
		//readAllFrames();	
	}

	//	public static void readArkJson(String jsonString){
	//		Object root = JSONValue.parse(jsonString);
	//		JSONObject rootJ = (JSONObject) root;
	//		JSONArray sentences  = (JSONArray) rootJ.get("sentences");		
	//		for(Object sentence : sentences){
	//			JSONObject sentenceJ = (JSONObject) sentence;	
	//			String conll = (String) sentenceJ.get("conll");
	//			//			String text = (String) sentenceJ.get("text");
	//			//			JSONArray relations = (JSONArray) sentenceJ.get("relations");
	//			//			JSONArray tokens = (JSONArray) sentenceJ.get("tokens");			
	//			//			JSONArray entities = (JSONArray) sentenceJ.get("entities");
	//			JSONArray frames = (JSONArray) sentenceJ.get("frames");
	//			for(Object frame : frames){
	//				JSONObject frameJ = (JSONObject) frame;
	//				//JSONObject target = (JSONObject) frameJ.get("target");
	//				//				String frameText = (String) target.get("text");
	//				//				String frameName = (String) target.get("name");
	//
	//			}
	//			System.out.println(conll);
	//		}
	//
	//		System.out.println();
	//	}

	//	public static String readArkJsonFrameNames(String query, String jsonString){
	//		//		try {
	//		//			query = URLEncoder.encode(query, "UTF-8");
	//		//		} catch (UnsupportedEncodingException e) {
	//		//			e.printStackTrace();
	//		//		}	
	//
	//		StringBuilder frameNames = new StringBuilder();
	//		if(jsonString!=null){
	//			Object root = JSONValue.parse(jsonString);
	//			JSONObject rootJ = (JSONObject) root;
	//			JSONArray sentences  = (JSONArray) rootJ.get("sentences");		
	//			for(Object sentence : sentences){
	//				JSONObject sentenceJ = (JSONObject) sentence;	
	//				//				String conll = (String) sentenceJ.get("conll");
	//				//				String text = (String) sentenceJ.get("text");
	//				//				JSONArray relations = (JSONArray) sentenceJ.get("relations");
	//				//				JSONArray tokens = (JSONArray) sentenceJ.get("tokens");			
	//				//				JSONArray entities = (JSONArray) sentenceJ.get("entities");
	//				JSONArray frames = (JSONArray) sentenceJ.get("frames");
	//				for(Object frame : frames){
	//					JSONObject frameJ = (JSONObject) frame;
	//					JSONObject target = (JSONObject) frameJ.get("target");
	//					String frameText = (String) target.get("text");
	//					String frameName = (String) target.get("name");
	//					frameText = frameText.trim().toLowerCase();					
	//					frameName = frameName + "_frame";
	//					frameNames.append(frameName + " ");
	//					if(!textFrames.containsKey(frameText))
	//						textFrames.put(frameText, frameName);
	//					else{ 
	//						if(!textFrames.get(frameText).contains(frameName))
	//							textFrames.put(frameText, textFrames.get(frameText) + " " +  frameName);
	//					}
	//				}			
	//			}
	//		} else {
	//			return readNullArkJsonFrameNames(query);
	//		}
	//		return frameNames.toString().trim();
	//	}

	public static String readArkJsonFrameNamesFromSentence(String jsonString){
		StringBuilder frameNames = new StringBuilder();
		if(jsonString!=null){
			Object root = JSONValue.parse(jsonString);
			JSONObject rootJ = (JSONObject) root;
			JSONArray frames = (JSONArray) rootJ.get("frames");
			for(Object frame : frames){
				JSONObject frameJ = (JSONObject) frame;
				JSONObject targetJ = (JSONObject) frameJ.get("target");
				JSONArray annotationSets =  (JSONArray) frameJ.get("annotationSets");				
				String frameName = (String) targetJ.get("name") + "_frame";
				frameNames.append(frameName.trim() + " ");
				for(Object annotationSet : annotationSets){
					JSONObject annotationSetJ = (JSONObject) annotationSet;					 
					JSONArray frameElements = (JSONArray) annotationSetJ.get("frameElements");
					for(Object frameElement : frameElements){
						JSONObject frameElementJ = (JSONObject) frameElement;
						frameName = (String) frameElementJ.get("name") + "_frame";
						frameNames.append(frameName.trim() + " ");
					}
				}				
			}			
		}
		return frameNames.toString().trim();
	}


	//	public static String readNullArkJsonFrameNames(String query){
	//		StringBuilder frames = new StringBuilder();
	//		for(String text : textFrames.keySet()){
	//			if(query.toLowerCase().trim().contains(text.toLowerCase().trim())){
	//				String framesString = textFrames.get(text);
	//				String[] framesSplit = framesString.split("\\s+");
	//				for(String frame : framesSplit){
	//					if(!frames.toString().contains(frame)){
	//						frames.append(frame + " ");
	//					}
	//				}
	//			}
	//		}
	//		return frames.toString().trim();
	//	}

	//	public static void readAllFrames(){
	//		for(String query	 : ArkJsonCache.keySet()){			
	//			String arkJson = ArkJsonCache.get(query);
	//			if(arkJson!=null)
	//				readArkJsonFrameNames(query, arkJson);
	//		}
	//		for(String query : ArkJsonCache.keySet()){
	//			String arkJson = ArkJsonCache.get(query);
	//			if(arkJson==null){
	//				StringBuilder frames = new StringBuilder();
	//				for(String text : textFrames.keySet()){
	//					if(query.toLowerCase().trim().contains(text)){
	//						String framesString = textFrames.get(text);
	//						String[] framesSplit = framesString.split("\\s+");
	//						for(String frame : framesSplit){
	//							if(!frames.toString().contains(frame)){
	//								frames.append(frame + " ");
	//							}
	//						}
	//					}
	//				}				
	//				queryFrames.put(query, frames.toString().trim());
	//			} else {
	//				queryFrames.put(query, readArkJsonFrameNames(query, arkJson));
	//			}
	//		}	
	//	}

	public static void readAllArkJsonFromSupDir(String ArkJsonDir){
		File dir = new File(ArkJsonDir);		
		for(File arkFrameFolder : dir.listFiles()){
			File[] files = arkFrameFolder.listFiles();
			for(File file : files){
				if(!file.isHidden()){
					if(file.getName().endsWith(".sentences")){
						File textFile = file;
						File outputFile = new File(textFile.getAbsolutePath().replace(".sentences", ".out"));
						File posTaggedFile = new File(textFile.getAbsolutePath().replace(textFile.getName(), "")
								+ "pos.tagged");						
						BufferedReader textBr = BasicFileTools.getBufferedReader(textFile);
						BufferedReader outputBr = BasicFileTools.getBufferedReader(outputFile);
						BufferedReader posBr = BasicFileTools.getBufferedReader(posTaggedFile);
						String textLine = null;
						try {
							while((textLine = textBr.readLine())!=null){
								String arkJson = outputBr.readLine();
								String posTags = posBr.readLine();
								StringBuilder posTagSeq = new StringBuilder();
								StringBuilder tokens = new StringBuilder();
								String[] split = posTags.split("\\s+");
								for(String posTag : split){
									String token = posTag.split("_")[0];
									String pos = posTag.split("_")[1];
									posTagSeq.append(pos.trim() + " ");
									tokens.append(token.trim() + " ");
								}
								if(!ArkJsonCache.containsKey(textLine)){
									ArkJsonCache.put(textLine, arkJson);									
									ArkTaggedCache.put(textLine, posTagSeq.toString().trim());
									ArkTokensCache.put(textLine, tokens.toString().trim());
								}
							}
						} catch (IOException e) {
							e.printStackTrace();
						}						
					}
				}
			}
		}
	}

	public static void readAllArkJsonFromDir(String arkJsonDir){
		File dir = new File(arkJsonDir);		
		for(File file : dir.listFiles()){
			if(!file.isHidden()){
				if(file.getName().endsWith(".sentences")){
					File textFile = file;
					File outputFile = new File(textFile.getAbsolutePath().replace(".sentences", ".out"));
					File posTaggedFile = new File(textFile.getAbsolutePath().replace(textFile.getName(), "")
							+ "pos.tagged");						
					BufferedReader textBr = BasicFileTools.getBufferedReader(textFile);
					BufferedReader outputBr = BasicFileTools.getBufferedReader(outputFile);
					BufferedReader posBr = BasicFileTools.getBufferedReader(posTaggedFile);
					String textLine = null;
					try {
						while((textLine = textBr.readLine())!=null){
							String arkJson = outputBr.readLine();
							String posTags = posBr.readLine();
							StringBuilder posTagSeq = new StringBuilder();
							StringBuilder tokens = new StringBuilder();
							String[] split = posTags.split("\\s+");
							for(String posTag : split){
								String token = posTag.split("_")[0];
								String pos = posTag.split("_")[1];
								posTagSeq.append(pos.trim() + " ");
								tokens.append(token.trim() + " ");
							}
							if(!ArkJsonCache.containsKey(textLine)){
								ArkJsonCache.put(textLine, arkJson);									
								ArkTaggedCache.put(textLine, posTagSeq.toString().trim());
								ArkTokensCache.put(textLine, tokens.toString().trim());
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}						
				}
			}		
		}
	}	


}


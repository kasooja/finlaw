package edu.insight.finlaw.parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import edu.insight.finlaw.utils.SerializationUtils;

public class ArkParser {

	public static final String ARKURL = "http://demo.ark.cs.cmu.edu/parse/api/v1/parse?sentence=";
	private static Map<String, String> ArkJsonCache;
	private static final String ArkJsonCachePath = "src/main/resources/ArkJsonCache.ser";
	private static int noOfTextsAtLoadTime = 0;
	private static Map<String, String> textFrames = new HashMap<String, String>();
	private static Map<String, String> queryFrames = new HashMap<String, String>();


	static {
		ArkJsonCache = new HashMap<String, String>();
		loadCache(ArkJsonCachePath);
	}

	@SuppressWarnings("unchecked")
	private static void loadCache(String ArkJsonCachePath){
		if(new File(ArkJsonCachePath).exists()){
			ArkJsonCache = ((Map<String, String>) SerializationUtils.readObject(new File(ArkJsonCachePath)));
			noOfTextsAtLoadTime = ArkJsonCache.size();
		}
	}

	private static void saveCache(String ArkJsonCachePath){		
		SerializationUtils.saveObject(ArkJsonCache, new File(ArkJsonCachePath));
	}

	public static void disconnectArkParser(){
		if(ArkJsonCache.size() > noOfTextsAtLoadTime){
			if(new File(ArkJsonCachePath).exists() ){
				new File(ArkJsonCachePath).delete();
			} 
			saveCache(ArkJsonCachePath);
		}
	}

	public static String httpGet(String urlStr){
		URL url;
		try {
			url = new URL(urlStr);
			HttpURLConnection conn =
					(HttpURLConnection) url.openConnection();
			if (conn.getResponseCode() != 200) {
				throw new IOException(conn.getResponseMessage());
			}
			BufferedReader rd = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = rd.readLine()) != null) {
				sb.append(line + "\n");
			}
			rd.close();
			conn.disconnect();
			return sb.toString();
		} catch (MalformedURLException e) {		
			e.printStackTrace();
		} catch (IOException e) {			
			e.printStackTrace();
		}
		return null;
	}

	public static String frameNetParse(String query){
		try {
			query = URLEncoder.encode(query, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}	
		if(ArkJsonCache.containsKey(query)){
			return ArkJsonCache.get(query);
		}
		String jsonResult = null;
		try{
			jsonResult = httpGet(ARKURL + query);
			ArkJsonCache.put(query, jsonResult);
		} catch(Exception e){
			ArkJsonCache.put(query, null);
		}

		return jsonResult;
	}

	public static void main(String[] args) throws IOException {
		//String query = "Supervisory authority which in the course of carrying out any of its functions under these Regulations knows or suspects that person is or has engaged in money laundering or terrorist financing must promptly report the Serious Organised Crime Agency.";
		//String query = "Supervisory authority which inform and report.";
		//String json = frameNetParse(query);
		//String frames = readArkJsonFrameNames(json);
		//System.out.println(frames);
		//ArkParser.disconnectArkParser();
		readAllFrames();	
	}

	public static void readArkJson(String jsonString){
		Object root = JSONValue.parse(jsonString);
		JSONObject rootJ = (JSONObject) root;
		JSONArray sentences  = (JSONArray) rootJ.get("sentences");		
		for(Object sentence : sentences){
			JSONObject sentenceJ = (JSONObject) sentence;	
			String conll = (String) sentenceJ.get("conll");
			String text = (String) sentenceJ.get("text");
			JSONArray relations = (JSONArray) sentenceJ.get("relations");
			JSONArray tokens = (JSONArray) sentenceJ.get("tokens");			
			JSONArray entities = (JSONArray) sentenceJ.get("entities");
			JSONArray frames = (JSONArray) sentenceJ.get("frames");
			for(Object frame : frames){
				JSONObject frameJ = (JSONObject) frame;
				JSONObject target = (JSONObject) frameJ.get("target");
				String frameText = (String) target.get("text");
				String frameName = (String) target.get("name");

			}
			System.out.println(conll);
		}

		System.out.println();
	}

	public static String readArkJsonFrameNames(String jsonString){
		StringBuilder frameNames = new StringBuilder();
		if(jsonString!=null){
			Object root = JSONValue.parse(jsonString);
			JSONObject rootJ = (JSONObject) root;
			JSONArray sentences  = (JSONArray) rootJ.get("sentences");		
			for(Object sentence : sentences){
				JSONObject sentenceJ = (JSONObject) sentence;	
				String conll = (String) sentenceJ.get("conll");
				String text = (String) sentenceJ.get("text");
				JSONArray relations = (JSONArray) sentenceJ.get("relations");
				JSONArray tokens = (JSONArray) sentenceJ.get("tokens");			
				JSONArray entities = (JSONArray) sentenceJ.get("entities");
				JSONArray frames = (JSONArray) sentenceJ.get("frames");
				for(Object frame : frames){
					JSONObject frameJ = (JSONObject) frame;
					JSONObject target = (JSONObject) frameJ.get("target");
					String frameText = (String) target.get("text");
					String frameName = (String) target.get("name");
					frameText = frameText.trim().toLowerCase();					
					frameName = frameName + "_frame";
					frameNames.append(frameName + " ");
					if(!textFrames.containsKey(frameText))
						textFrames.put(frameText, frameName);
					else{ 
						if(!textFrames.get(frameText).contains(frameName))
							textFrames.put(frameText, textFrames.get(frameText) + " " +  frameName);
					}
				}			
			}
		}
		return frameNames.toString().trim();
	}

	public static void readAllFrames(){
		for(String query	 : ArkJsonCache.keySet()){			
			String arkJson = ArkJsonCache.get(query);
			if(arkJson!=null)
				readArkJsonFrameNames(arkJson);
		}
		for(String query : ArkJsonCache.keySet()){
			String arkJson = ArkJsonCache.get(query);
			if(arkJson==null){
				StringBuilder frames = new StringBuilder();
				for(String text : textFrames.keySet()){
					if(query.toLowerCase().trim().contains(text)){
						String framesString = textFrames.get(text);
						String[] framesSplit = framesString.split("\\s+");
						for(String frame : framesSplit){
							if(!frames.toString().contains(frame)){
								frames.append(frame + " ");
							}
						}
					}
				}				
				queryFrames.put(query, frames.toString().trim());
			} else {
				queryFrames.put(query, readArkJsonFrameNames(arkJson));
			}
		}
		System.out.println(textFrames.size());
	}	


}


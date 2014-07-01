package edu.insight.finlaw.rdf;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class SparqlEndpointTest {

	public static String executeQuery(String targetURL, String urlParameters) {
		URL url;
		HttpURLConnection connection = null;  
		try {
			//Create connection
			url = new URL(targetURL);
			connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Content-Type", 
					"application/x-www-form-urlencoded");
			connection.setRequestProperty("Content-Length", "" + 
					Integer.toString(urlParameters.getBytes().length));
			connection.setRequestProperty("Content-Language", "en-US");  
			connection.setUseCaches (false);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			//Send request
			DataOutputStream wr = new DataOutputStream (
					connection.getOutputStream ());
			wr.writeBytes (urlParameters);
			wr.flush ();
			wr.close ();
			//Get Response	
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuffer response = new StringBuffer(); 
			while((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();
			return response.toString();

		} catch (Exception e) {

			e.printStackTrace();
			return null;

		} finally {

			if(connection != null) {
				connection.disconnect(); 
			}
		}
	}

	public static void main(String[] args) {
		String queryString = "PREFIX firoh: <http://www.GRCTC.com/ontologies/FIRO/FIRO-H_1.0#>\n" +
				"PREFIX firos: <http://www.GRCTC.com/ontologies/FIRO/FIRO-S_1.0#>\n" +
				"PREFIX purpose: <http://www.GRCTC.com/ontologies/FIRO/Purpose_Specific_v1.0#>\n" +
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" + 
				"PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
				"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
				"SELECT ?Provision ?Modality  ?section ?subSection\n" +
				"WHERE{   ?Provision  firoh:hasModality ?Modality.\n" +
				"?Provision  purpose:inSection ?section.\n" + 
				"?Provision  purpose:inSubSection ?subSection.\n" + 
				"?Modality rdf:type firoh:Prohibition.    }";
		
		String output = "json";
		String endpoint = "http://monnet02.sindice.net:3030/ds/query";
		String urlParameters;
		try {
			urlParameters = "query=" + URLEncoder.encode(queryString, "UTF-8") +
					"&output=" + URLEncoder.encode(output, "UTF-8");
			String queryResult = executeQuery(endpoint, urlParameters);
			System.out.println(queryResult);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}

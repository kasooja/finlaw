package edu.insight.finlaw.demo.grctc;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class SparqlEndpointTest {

	public String buildQuery(int selectedQuery) 
	{
		String queryString;

		queryString = getQuery(selectedQuery);


		if(queryString != null)
		{
			String output = "json";
			//String endpoint = "http://monnet02.sindice.net:3030/ds/query";
			String endpoint = "http://monnet01.sindice.net:8001/ds/query";
			String urlParameters;
			try {
				urlParameters = "query=" + URLEncoder.encode(queryString, "UTF-8") +
						"&output=" + URLEncoder.encode(output, "UTF-8");
				String queryResult = executeQuery(endpoint, urlParameters);
				System.out.println(queryResult);
				return queryResult;


			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}


	public String executeQuery(String targetURL, String urlParameters) {
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


	String getQuery(int selectedQuery)
	{

		String queryString;

		switch(selectedQuery)
		{
		case 1:
			queryString = "PREFIX firoh: <http://www.GRCTC.com/Ontologies/FIRO/FIRO-H-v1_1#> \n"+
					"PREFIX firos: <http://www.GRCTC.com/ontologies/FIRO/FIRO-S-v1_0#>\n"+ 
					"PREFIX purpose: <http://www.GRCTC.com/ontologies/FIRO/Purpose_Specific_v1.0#>\n"+
					"Prefix firoaml: <http://www.GRCTC.com/ontologies/FIRO/FIRO-AML-v1_0#>\n"+
					"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"+
					"PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"+
					"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"+
					"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
					"SELECT ?Provision ?Modality  ?section ?subSection\n"+
					"WHERE{   ?Provision  firoh:hasModality ?Modality.\n"+
					"?Provision  purpose:inSection ?section.\n"+
					"?Provision  purpose:inSubSection ?subSection.\n"+
					"?Modality rdf:type firoh:Obligation.    }\n";
			break;
		case 2:
			queryString = "PREFIX firoh: <http://www.GRCTC.com/Ontologies/FIRO/FIRO-H-v1_1#>\n"+
					"PREFIX firos: <http://www.GRCTC.com/ontologies/FIRO/FIRO-S-v1_0#>\n"+ 
					"PREFIX purpose: <http://www.GRCTC.com/ontologies/FIRO/Purpose_Specific_v1.0#>\n"+
					"Prefix firoaml: <http://www.GRCTC.com/ontologies/FIRO/FIRO-AML-v1_0#>\n"+
					"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"+
					"PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"+
					"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"+
					"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
					"SELECT ?Provision ?Modality  ?section ?subSection ?CurrentText\n"+	
					"WHERE{\n"+   
					"?Provision  firoh:hasModality ?Modality.    ?Provision  purpose:inSection ?section.   ?Provision  purpose:inSubSection ?subSection.\n"+   
					"?subSection firos:hasContent ?Content. ?Content firos:hasBlockElements ?BE. ?BE purpose:hasHTMLblock ?HTMLblock. ?HTMLblock firos:hasP ?p. ?p firos:hasString ?CurrentText\n"+
					"}\n";
			break;
		case 3:
			queryString = "PREFIX firoh: <http://www.GRCTC.com/Ontologies/FIRO/FIRO-H-v1_1#>\n"+
					"PREFIX firos: <http://www.GRCTC.com/ontologies/FIRO/FIRO-S-v1_0#>\n"+ 
					"PREFIX purpose: <http://www.GRCTC.com/ontologies/FIRO/Purpose_Specific_v1.0#>\n"+
					"Prefix firoaml: <http://www.GRCTC.com/ontologies/FIRO/FIRO-AML-v1_0#>\n"+
					"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"+
					"PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"+
					"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"+
					"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
					"SELECT   ?Modality ?Provision ?subSection\n"+ 
					"WHERE{   	?Provision rdf:type firoaml:CustomerDueDiligence.\n"+
					"?Provision firoh:hasModality ?Modality.\n"+
					"?Provision  purpose:inSection ?section.   ?Provision  purpose:inSubSection ?subSection.\n"+
					"?Modality rdf:type firoh:Obligation.\n"+ 		          
					"}\n";   
			break;
		case 4:
			queryString = "PREFIX firoh: <http://www.GRCTC.com/Ontologies/FIRO/FIRO-H-v1_1#>\n"+
					"PREFIX firos: <http://www.GRCTC.com/ontologies/FIRO/FIRO-S-v1_0#>\n"+ 
					"PREFIX purpose: <http://www.GRCTC.com/ontologies/FIRO/Purpose_Specific_v1.0#>\n"+
					"Prefix firoaml: <http://www.GRCTC.com/ontologies/FIRO/FIRO-AML-v1_0#>\n"+
					"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"+
					"PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"+
					"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"+
					"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
					"SELECT ?Provision ?Modality  ?section ?subSection\n"+
					"WHERE{ ?Provision  firoh:hasModality ?Modality.\n"+
					"?Provision  purpose:inSection ?section.\n"+
					"?Provision  purpose:inSubSection ?subSection.\n"+
					"} Limit 70\n";

			break;
		case 5:
			queryString = "PREFIX firoh: <http://www.GRCTC.com/Ontologies/FIRO/FIRO-H-v1_1#>\n"+
					"PREFIX firos: <http://www.GRCTC.com/ontologies/FIRO/FIRO-S-v1_0#>\n"+ 
					"PREFIX purpose: <http://www.GRCTC.com/ontologies/FIRO/Purpose_Specific_v1.0#>\n"+
					"Prefix firoaml: <http://www.GRCTC.com/ontologies/FIRO/FIRO-AML-v1_0#>\n"+
					"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"+
					"PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"+
					"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"+
					"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
					"SELECT   ?section ?Modality \n" +
					"WHERE{ ?Provision  firoh:hasModality ?Modality.\n" +
					"?Provision  purpose:inSection ?section. } Limit 70 "; 
			break;
		default: 	
			queryString=null;

		}
		return queryString;
	}



	String getQueryOld(int selectedQuery)
	{

		String queryString;

		switch(selectedQuery)
		{
		case 1:
			queryString = "PREFIX firoh: <http://www.GRCTC.com/ontologies/FIRO/FIRO-H_1.0#>\n" +
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
			break;
		case 2:
			queryString = "PREFIX firoh: <http://www.GRCTC.com/ontologies/FIRO/FIRO-H_1.0#>\n"+
					"PREFIX firos: <http://www.GRCTC.com/ontologies/FIRO/FIRO-S_1.0#>\n"+
					"PREFIX purpose: <http://www.GRCTC.com/ontologies/FIRO/Purpose_Specific_v1.0#>\n"+
					"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"+
					"PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"+
					"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"+
					"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
					"SELECT ?Provision ?Modality  ?section ?subSection  ?CurrentText\n"+
					"WHERE{ \n"+  
					"?Provision  firoh:hasModality ?Modality.    ?Provision  purpose:inSection ?section.   ?Provision  purpose:inSubSection ?subSection.  ?Modality rdf:type firoh:Prohibition.\n"+ 
					"?subSection firos:hasContent ?Content. ?Content firos:hasBlockElements ?BE. ?BE purpose:hasHTMLblock ?HTMLblock. ?HTMLblock firos:hasP ?p. ?p firos:hasString ?CurrentText\n"+
					"}";


			break;
		case 3:
			queryString = "PREFIX firoh: <http://www.GRCTC.com/ontologies/FIRO/FIRO-H_1.0#> \n" + 
					"PREFIX firos: <http://www.GRCTC.com/ontologies/FIRO/FIRO-S_1.0#>\n"+
					"PREFIX purpose: <http://www.GRCTC.com/ontologies/FIRO/Purpose_Specific_v1.0#>\n"+
					"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"+
					"PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"+
					"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"+
					"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
					"SELECT   ?Modality ?Provision ?subSection \n" + 
					"WHERE{   	?Provision rdf:type firoh:CustomerDueDiligence.\n" +
					"?Provision firoh:hasModality ?Modality.\n" +
					"?Modality rdf:type firoh:Obligation. \n" +
					"?Provision  purpose:inSection ?section.\n" + 
					"?Provision  purpose:inSubSection ?subSection.\n" + 
					"}";   

			break;
		case 4:
			queryString = "PREFIX firoh: <http://www.GRCTC.com/ontologies/FIRO/FIRO-H_1.0#>\n" +
					"PREFIX firos: <http://www.GRCTC.com/ontologies/FIRO/FIRO-S_1.0#>\n" +
					"PREFIX purpose: <http://www.GRCTC.com/ontologies/FIRO/Purpose_Specific_v1.0#>\n" +
					"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
					"PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
					"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
					"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
					"SELECT ?Provision ?Modality  ?section ?subSection\n" +
					"WHERE{ ?Provision  firoh:hasModality ?Modality.\n" +
					"?Provision  purpose:inSection ?section.\n" +
					"?Provision  purpose:inSubSection ?subSection. }"; 
			break;
		case 5:
			queryString = "PREFIX firoh: <http://www.GRCTC.com/ontologies/FIRO/FIRO-H_1.0#>\n" +
					"PREFIX firos: <http://www.GRCTC.com/ontologies/FIRO/FIRO-S_1.0#>\n" +
					"PREFIX purpose: <http://www.GRCTC.com/ontologies/FIRO/Purpose_Specific_v1.0#>\n" +
					"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
					"PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
					"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
					"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
					"SELECT   ?section ?Modality \n" +
					"WHERE{ ?Provision  firoh:hasModality ?Modality.\n" +
					"?Provision  purpose:inSection ?section. } "; 
			break;
		default: 	
			queryString=null;

		}
		return queryString;
	}
}

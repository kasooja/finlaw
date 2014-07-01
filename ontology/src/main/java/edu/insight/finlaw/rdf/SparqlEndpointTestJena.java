package edu.insight.finlaw.rdf;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;

public class SparqlEndpointTestJena {

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
		
		Query query = QueryFactory.create(queryString);		
		QueryExecution qexec = QueryExecutionFactory.sparqlService("http://monnet02.sindice.net:3030/ds/query", query);
		ResultSet results = qexec.execSelect();		
		ResultSetFormatter.out(System.out, results, query);       
		qexec.close() ;
	}

}

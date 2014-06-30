package edu.insight.finlaw.rdf;


import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.sparql.core.DatasetGraph;
import com.hp.hpl.jena.sparql.core.Quad;

public class JenaModel {

	private OntModel ontModel;

	private PrintWriter log;

	public JenaModel(){
		ontModel = ModelFactory.createOntologyModel();
		openLog("log.txt");
	}

	public OntModel getOntModel(){
		return ontModel;
	}

	private void openLog(String filePath) {
		try {
			log = new PrintWriter(new BufferedWriter(new FileWriter(filePath, true)), true);	
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public PrintWriter getLogWriter() {
		return log;
	}


	//	public static void ReadOntologyWithImport() {
	//	    //Set document manager policy file
	//	    OntDocumentManager dm = new OntDocumentManager("ont-policy.rdf");
	//	    OntModelSpec modelSpec = PelletReasonerFactory.THE_SPEC;
	//	    modelSpec.setDocumentManager(dm);
	//	    OntModel  ontModelWithImport = ModelFactory.createOntologyModel(modelSpec);
	//
	//	//Read the base Ontology File ; Here its SmdWithImport
	//	    ontModelWithImport.read(FileManager.get().open("./src/SmdWithRule.rdf"), NS);
	//	}


	public void importOntology(String ontologyFile, String base){		
		try {		
			if (ontologyFile.contains(".")==false){
				ontologyFile=ontologyFile+".owl";	
			}
			FileInputStream file = new FileInputStream(ontologyFile);	
			ontModel.read(file, base);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void importData(String rdfFile) {
		System.out.println(rdfFile);
		DatasetGraph dataset = RDFDataMgr.loadDataset(rdfFile, Lang.TURTLE).asDatasetGraph();		
		Iterator<Quad> quads = dataset.find();
		while ( quads.hasNext() ) {
			Quad quad = quads.next();
			Triple triple = quad.asTriple();
			Statement statement = ontModel.asStatement(triple);
			ontModel.add(statement);
		}	
	}

	public void queryModel(String queryString) {
		QueryExecution qexec = QueryExecutionFactory.create(queryString, ontModel);
		ResultSet results = qexec.execSelect();
		while(results.hasNext()) {
			QuerySolution solution = results.next();			
			System.out.println(solution);
			log.println(solution.toString() +"\n");				
		}
	}	

	public Map<String, List<RDFNode>> queryModel(String queryString, List<String> queryVariables) {
		Map<String, List<RDFNode>> solution = new HashMap<String, List<RDFNode>>();
		QueryExecution qexec = QueryExecutionFactory.create(queryString, ontModel);	
		ResultSet results = qexec.execSelect();
		while(results.hasNext()) {		
			QuerySolution sol = results.next();		
			for(String variable : queryVariables) {	

				RDFNode nodeVar = sol.get(variable);
				if(nodeVar!=null){
					if(solution.get(variable)==null)
						solution.put(variable, new ArrayList<RDFNode>());					
					solution.get(variable).add(nodeVar);
				}
			}
			log.println(solution.toString() +"\n");				
		}
		if(solution.isEmpty())
			solution = null;
		return solution;
	}	


	public Map<String, List<RDFNode>> queryModel(Map<String, String> prefixes, String queryString, List<String> queryVariables) {
		queryString = getPrefixString(prefixes) + queryString;
		Map<String, List<RDFNode>> solution = new HashMap<String, List<RDFNode>>();
		QueryExecution qexec = QueryExecutionFactory.create(queryString, ontModel);	
		ResultSet results = qexec.execSelect();
		while(results.hasNext()) {		
			QuerySolution sol = results.next();		
			for(String variable : queryVariables) {	

				RDFNode nodeVar = sol.get(variable);
				if(nodeVar!=null){
					if(solution.get(variable)==null)
						solution.put(variable, new ArrayList<RDFNode>());					
					solution.get(variable).add(nodeVar);
				}
			}
			log.println(solution.toString() +"\n");				
		}
		if(solution.isEmpty())
			solution = null;
		return solution;
	}	


	public String getPrefixString(Map<String, String> prefixUriMap){
		StringBuffer buffer = new StringBuffer();
		for(String prefix : prefixUriMap.keySet()){
			String uri = prefixUriMap.get(prefix);
			buffer.append("Prefix " +  prefix + ":" +  "<" + uri + "#> "  + "\n");			
		}		
		return buffer.toString().trim() + "\n";
	}


	public StmtIterator constructQuery(String queryString) {
		QueryExecution qexec = QueryExecutionFactory.create(queryString, ontModel);	
		Model construct = qexec.execConstruct();

		StmtIterator statements = construct.listStatements();
		return statements;
	}	


}

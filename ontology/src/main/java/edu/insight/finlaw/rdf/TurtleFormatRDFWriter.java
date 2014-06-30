package edu.insight.finlaw.rdf;


import java.util.List;
import java.util.Map;

import edu.insight.finlaw.utils.BasicFileTools;


public class TurtleFormatRDFWriter {
	
	private StringBuffer buffer = new StringBuffer();
	
	public TurtleFormatRDFWriter(){
		buffer.append("@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .\n");
	}
	
	public void addRDF(String resource, String type, List<String> properties){
		buffer.append("\n\n" + resource + "\n" + "a" + " " + type + ";\n\n");
		for(String property : properties){
			buffer.append(property + "\n\n");
		}
		buffer.append(".\n\n");
	}
	
	public void addRDF(String resource, String type, Map<String, String> propsWithValues){
		buffer.append("\n\n" + resource + "\n" + "a" + " " + type + ";\n\n");
		for(String property : propsWithValues.keySet()){
			String value = propsWithValues.get(property);
			if(value.matches("[-+\\d]*\\."))
				value = value + "0";
			buffer.append(property + "\t" + value + ";" + "\n\n");
		}
		buffer.append(".\n\n");
	}	
	
	public void addRDF(String resource, String type){
		buffer.append("\n\n" + resource + "\n" + "a" + " " + type + ";\n\n");
		buffer.append(".\n\n");
	}
	
	public void write(String filePath){
		BasicFileTools.writeFile(filePath, buffer.toString().trim());
	}
	
	public void addPrefix(String prefix, String uri){
		buffer.append("@prefix " +  prefix + ":" +  "<" + uri + "> . "  + "\n");					
	}
	
	public void addPrefixes(Map<String, String> prefixUriMap){
		for(String prefix : prefixUriMap.keySet()){
			String uri = prefixUriMap.get(prefix);
			buffer.append("@prefix " +  prefix + ":" +  "<" + uri + "> . "  + "\n");			
		}		
	}
	
}

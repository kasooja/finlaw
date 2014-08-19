package edu.insight.finlaw.multilabel.rough;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.insight.finlaw.utils.BasicFileTools;

public class FeatureSelectorParser {

	public static void main(String[] args) {
		BufferedReader bf = BasicFileTools.getBufferedReaderFile("src/main/resources/leona_features");
		String line = null;
		String featurePatternString = "([\\d\\.]+)\\s*([\\d]*)\\s*([\\s\\w]*)";	
		Pattern featurePattern = Pattern.compile(featurePatternString);
		StringBuffer buffer = new StringBuffer();
		try {
			while((line=bf.readLine())!=null){
				Matcher featurePatternMatcher = featurePattern.matcher(line);
				if(featurePatternMatcher.find()){
					String score = featurePatternMatcher.group(1);
					String feature = featurePatternMatcher.group(3);
					buffer.append(score + "\t," + feature + "\n");					
				}
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		BasicFileTools.writeFile("src/main/resources/enforcement.csv", buffer.toString().trim());
		
	}
	
}

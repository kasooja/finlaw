package edu.insight.finlaw.multilabel.classification.meka;



import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.TreeMap;

import edu.insight.finlaw.parsers.ArkParser;

public class CommandLineTools {

	public static boolean runCommand(String command){
		Runtime rt = Runtime.getRuntime();
		try {
			Process pr = rt.exec(command);
			BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			String line = null;
			while((line=input.readLine()) != null) {
				System.out.println(line);
			}
			int exitVal;
			try {
				exitVal = pr.waitFor();
				System.out.println("Exited with error code " + exitVal);				
			} catch (InterruptedException e) {
				e.printStackTrace();
				throw new RuntimeException(e.getMessage(),e);
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage(),e);
		}		
		return true;
	}	

	public static Map<Integer,String> runCommandAndGetMap(String command) throws Exception{
		Map<Integer,String> lineMap = new TreeMap<Integer,String>();
		Runtime rt = Runtime.getRuntime();
		Process pr = rt.exec(command);
		BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
		String line = null;
		int count = 0;
		while((line=input.readLine()) != null) {
			System.out.println("Console started: \n");
			System.out.println("Console line"+count+": "+ line+ "\n");
			lineMap.put(count,line);
			System.out.println("Console finished: \n");
			count++;
		}
		if(count==0){
			InputStream stderr = pr.getErrorStream();
			InputStreamReader isr = new InputStreamReader(stderr);
			BufferedReader br = new BufferedReader(isr);
			String lineError = null;
			System.out.println("<ERROR>");
			while ( (lineError = br.readLine()) != null){
				System.out.println("Console error started: \n");
				System.out.println("Console error line"+count+": "+ lineError+ "\n");
				lineMap.put(count,lineError);
				System.out.println("Console error finished: \n");
				count++;          	
			}
			System.out.println("</ERROR>");
			int exitVal = pr.waitFor();
			System.out.println("Process exitValue: " + exitVal);				
		}

		return lineMap;
	}	

	public static void semafor(String semaforDir, String inputFilePath, int noOfThreads) {
		String inputOutputDir = new File(inputFilePath).getParent();
		String outputFilePath = inputOutputDir + "/" + new File(inputFilePath).getName().replaceAll(".sentences", ".out");		
		String command = "bash " + semaforDir + "/bin/runSemafor.sh" + " " + inputFilePath + " " + outputFilePath + " " + 
				inputOutputDir + " " + noOfThreads;
		System.out.println(command);
		runCommand(command);
		ArkParser.readAllArkJsonFromDir(inputOutputDir);		
		runCommand("rm -r " + inputOutputDir);		
	}


	public static void main(String[] args) {
		//String command = "echo www.google.com";
		//POS_TAGGED="${OUTPUT_DIR}/pos.tagged"
		//TEST_PARSED_FILE="${OUTPUT_DIR}/conll"
		String semaforDir = "/Users/kartik/Downloads/Kartik/ArkParserEvenLatest/semafor-master";
		//String outputDir = dir + "/samples";
		String inputFile = "/Users/kartik/Downloads/Kartik/ArkParserEvenLatest/semafor-master/samples/all.sentences";
		//String outputFile = "/Users/kartik/Downloads/Kartik/ArkParserEvenLatest/semafor-master/samples/all.out";
		//String posDir =  "/Users/kartik/Downloads/Kartik/ArkParserEvenLatest/semafor-master/samples";
		int noOfThreads = 5;
		//	String command = "bash " +  dir + "/bin/tokenizeAndPosTag.sh " + inputFile + " " + outputDir;
		//bash ${MY_DIR}/runMalt.sh ${INPUT_FILE} ${TEMP_DIR}
		semafor(semaforDir, inputFile, noOfThreads);
		String json = ArkParser.ArkJsonCache.get("I like going to the market.");
		System.out.println(json);

	}

}

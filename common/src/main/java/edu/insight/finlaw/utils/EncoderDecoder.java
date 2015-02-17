package edu.insight.finlaw.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class EncoderDecoder {
	public static void main(String[] args) throws IOException {
		File file = new File("src/main/resources/20141029_UKSI-2007-2157-made-XML-AML.xml");

		BufferedReader in = new BufferedReader(new InputStreamReader (
				new FileInputStream(file), "WINDOWS-1252"));
		Path path = Paths.get(file.getAbsolutePath());
		byte[] rawBytes = Files.readAllBytes(path);
		
		
//		String str;
//
//		while ((str = in.readLine()) != null) {
//
//			System.out.println(str);
//		}
//
//		in.close();

		//	byte[] sourceBytes = getRawBytes();
		String data = new String(rawBytes , "Windows-1252");
		byte[] destinationBytes = data.getBytes("UTF-8");
		
		String convData = new String(destinationBytes, "UTF-8");
		System.out.println(data);
		System.out.println(convData);
		
		BasicFileTools.writeFile("src/main/resources/test.txt", convData);
		
	}
}

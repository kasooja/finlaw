package edu.insight.finlaw.pdf;

import java.io.File;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

public class PdfProcessing {
	
	private static String regulationText = "(\\d).—(.*)(\\d).—";
	
	public static String getContentOfPdf(File inputFile) {
		PDDocument pd;
		String textContent = null;
		try {
			pd = PDDocument.load(inputFile);
			PDFTextStripper stripper = new PDFTextStripper();
			stripper.setStartPage(9); 
			//stripper.setEndPage(pd.getNumberOfPages());
			stripper.setEndPage(11);			
			textContent = stripper.getText(pd);
			if (pd != null) {
				pd.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return textContent;
	}
	
	public static void main(String[] args) {
		String filePath = "src/main/resources/grctcData/IrishAML.pdf";
		String contentOfPdf = getContentOfPdf(new File(filePath));
		System.out.println(contentOfPdf);
	}
	
}


package pdfxml2pdf;
import org.xml.sax.helpers.*;
import org.xml.sax.*;
import org.pdfbox.pdmodel.*;
import org.pdfbox.pdmodel.common.*;
import java.io.*;



public class BackboneHandler extends DefaultHandler {
	private File bbFile;
	public BackboneHandler(File bbFile) {
		this.bbFile = bbFile;
	}
	
	public void startDocument() {
		
	}
	
	public void endDocument() {
		
	}
	
	public void startElement(String uri, String localname, String qName, Attributes attributes) {
		// handle the <PDF> tag 
		if (qName.equals("PDF")) {
			
			PDDocument targetPDF = ConverterUtils.getTargetPDF();
			
			// set the PDF's version
			// PDFVersion is a required attribute 
			targetPDF.getDocument().setVersion(new Float(attributes.getValue("PDFVersion")));
			
			// set the PageLayout key if available
			if (attributes.getValue("PageLayout") != null) {
				targetPDF.getDocumentCatalog().setPageLayout(attributes.getValue("PageLayout"));
			}
			
			// set the PageMode key if available
			if (attributes.getValue("PageMode") != null) {
				targetPDF.getDocumentCatalog().setPageLayout(attributes.getValue("PageMode"));
			}
			
			// save the document ID, it might be needed for font processing later
			if (attributes.getValue("DocumentID") != null) {
				ConverterUtils.setDocID(attributes.getValue("DocumentID"));
			}
			else {
				System.err.println("Invalid pdfxml file! There's no DocumentID");
				System.exit(1);
			}
		}
		
		// handle the <Page> tag
		// PDFXML does not represent the page tree as general as in PDF
		// In PDF a page tree contains nodes of two types: intermediate nodes (page tree nodes)
		// and leaf nodes (page objects).
		// In PDFXML, it only has one <Pages> tag and a collection of <Page>s which are its
		// direct children
		if (qName.equals("Page")) {
			
			// process the page file
			File pageFile = ConverterUtils.getFile(bbFile, attributes.getValue("src"));
			
			PDPage page = new PDPage();
			
			PDRectangle pageMediaBox = new PDRectangle();
			pageMediaBox.setLowerLeftX(new Float(attributes.getValue("x1")));
			pageMediaBox.setLowerLeftY(new Float(attributes.getValue("y1")));
			pageMediaBox.setUpperRightX(new Float(attributes.getValue("x2")));
			pageMediaBox.setUpperRightY(new Float(attributes.getValue("y2")));
			page.setMediaBox(pageMediaBox);
			
			ConverterUtils.getTargetPDF().addPage(page);
			PageProcessor pageProcessor = new PageProcessor(pageFile, page);
			pageProcessor.process();
			
			// set its MediaBox
			
			
			
			// add it to the target PDF
			//ConverterUtils.getTargetPDF().addPage(page);
			
//			String pagePath = attributes.getValue("src");
//			if (pagePath.charAt(0) == '/') {
//				// absolute path, resolve the file against the root folder
//				pagePath = srcRoot.toURI().resolve(pagePath.substring(1)).getPath();
//			}
//			else {
//				// relative path, resolve the file against the current file
//				pagePath = bbFile.toURI().resolve(pagePath).getPath();
//			}
//			//System.out.println(pagePath);
			
			
		}
			
	}
}
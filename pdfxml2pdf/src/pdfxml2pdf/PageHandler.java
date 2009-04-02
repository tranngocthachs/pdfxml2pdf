package pdfxml2pdf;
import org.xml.sax.helpers.*;
import org.xml.sax.*;
import org.pdfbox.pdmodel.*;
import java.io.*;



public class PageHandler extends DefaultHandler {
	private PDPage page;
	private File pageFile;
	public PageHandler(File pageFile, PDPage page) {
		this.pageFile = pageFile;
		this.page = page;
	}
	
	
	public void startDocument() {
		
	}
	
	public void endDocument() {
		
	}
	
	public void startElement(String uri, String localname, String qName, Attributes attributes) {
		
		if (qName.equals("Contents")) {
			String pageContentPath = attributes.getValue("src");
			File pageContentFile = ConverterUtils.getFile(pageFile, pageContentPath);
			
			PageContentProcessor pageContentProcessor = new PageContentProcessor(pageContentFile, page);
			pageContentProcessor.process();

		}
	}
}

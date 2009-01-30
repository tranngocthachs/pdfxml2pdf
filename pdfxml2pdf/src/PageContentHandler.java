import org.xml.sax.helpers.*;
import org.xml.sax.*;
import org.pdfbox.pdmodel.*;
import org.pdfbox.pdmodel.font.*;
import org.pdfbox.pdmodel.common.*;
import org.pdfbox.pdmodel.graphics.color.*;
import java.io.*;
import java.util.*;


public class PageContentHandler extends DefaultHandler {
	private PDPage page;
	private File pageFile;
	private PDFont font;
	private HashMap<String, PDFont> fontMappings;
	private HashMap<String, PDColorSpace> colorMappings;
	private String fontFamily;
	public PageContentHandler(File pageFile, PDPage page) {
		this.pageFile = pageFile;
		this.page = page;
	}
	
	public void startDocument() {
		
	}
	
	public void endDocument() {
		
	}
	
	public void startElement(String uri, String localname, String qName, Attributes attributes) {
		// process the <defs> tag
		
		// process font-face tag
		if (qName.equals("font-face")) {
			System.out.println("Process font-face");
			fontFamily = attributes.getValue("font-family");
		}
		
		// process pdf:font-information tag 
		if (qName.equals("pdf:font-information")) {
			System.out.println("Process font-information");
			File fontFile = ConverterUtils.getFile(pageFile, attributes.getValue("xlink:href"));
			font = (new FontProcessor(fontFile)).process();
		}
		

		
	}
	public void endElement(String uri, String localname, String qName) {
		if (qName.equals("font-face")) {
			if (fontMappings == null)
				fontMappings = new HashMap<String, PDFont>();
			if (fontFamily != null && font != null)
				fontMappings.put(fontFamily, font);
		}
		
		if (qName.equals("defs")) {
			PDResources resources = new PDResources();
			resources.setFonts(fontMappings);
			page.setResources(resources);
		}
	}
}

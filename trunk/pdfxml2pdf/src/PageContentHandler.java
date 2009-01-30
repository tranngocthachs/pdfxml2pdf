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
	private HashMap<String, PDFont> fontMappings;
	private HashMap<String, PDColorSpace> colorMappings;
	private String fontFamily;
	private PDFont font;
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
				fontFamily = attributes.getValue("font-family");
		}
		
		// process pdf:font-information tag 
		if (qName.equals("pdf:font-information")) {
			File fontFile = ConverterUtils.getFile(pageFile, attributes.getValue("xlink:href"));
			(new FontProcessor(fontFile, font)).process();
			
		}
		

		
	}
	public void endElement(String uri, String localname, String qName, Attributes attributes) {
		if (qName.equals("font-face")) {
			if (fontMappings == null)
				fontMappings = new HashMap<String, PDFont>();
			if (fontFamily != null && font != null)
				fontMappings.put(fontFamily, font);
		}
	}
}

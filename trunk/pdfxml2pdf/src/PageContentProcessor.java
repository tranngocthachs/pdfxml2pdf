import org.xml.sax.*;
import org.xml.sax.helpers.*;
import org.pdfbox.pdmodel.*;
import java.io.*;

public class PageContentProcessor {
	
	private PDPage page;
	private File pageContentFile;
	
	public PageContentProcessor(File pageContentFile, PDPage page) {
		this.pageContentFile = pageContentFile;
		this.page = page;
	}

	public void process() {
		try {
			// Prepare the parser
			XMLReader parser; 
			try {
				parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
			}
			catch (SAXException e) {
				try {
					parser = XMLReaderFactory.createXMLReader();
				}
				catch (SAXException exc) {
					throw new NoClassDefFoundError("No SAX parser is available");
				}
			}

			// Set the correct handler
			PageContentHandlerTemp pageContentHandler = new PageContentHandlerTemp(pageContentFile, page);
			parser.setContentHandler(pageContentHandler);
			parser.parse(new InputSource(new FileInputStream(pageContentFile)));
		}
		catch (SAXException e) {
			e.printStackTrace();
			System.out.println(pageContentFile.getName() + " is not well-formed.");
		}
		catch (IOException e) {
			System.out.println("Due to an IOException, the parser could not check " + pageContentFile.getName());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
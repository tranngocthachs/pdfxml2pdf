import org.xml.sax.*;
import org.xml.sax.helpers.*;
import org.pdfbox.pdmodel.*;
import java.io.*;

public class PageProcessor {
	
	private File pageFile;
	
	public PageProcessor(File pageFile) {
		this.pageFile = pageFile;
	}

	public PDPage process() {
		PDPage page = null;
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
			PageHandler pageHandler = new PageHandler(pageFile);
			parser.setContentHandler(pageHandler);
			parser.parse(new InputSource(new FileInputStream(pageFile)));
			page = pageHandler.getPage();
		}
		catch (SAXException e) {
			System.out.println(pageFile.getName() + " is not well-formed.");
		}
		catch (IOException e) {
			System.out.println("Due to an IOException, the parser could not check " + pageFile.getName());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return page;
	}
}
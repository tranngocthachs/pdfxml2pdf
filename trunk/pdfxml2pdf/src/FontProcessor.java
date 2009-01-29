import org.xml.sax.*;
import org.xml.sax.helpers.*;
import org.pdfbox.pdmodel.*;
import org.pdfbox.pdmodel.font.*;
import java.io.*;

public class FontProcessor {
	
	private PDFont font;
	private File fontFile;
	
	public FontProcessor(File fontFile, PDFont font) {
		this.fontFile = fontFile;
		this.font = font;
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
			FontHandler fontHandler = new FontHandler(fontFile, font);
			parser.setContentHandler(fontHandler);
			parser.parse(new InputSource(new FileInputStream(fontFile)));
		}
		catch (SAXException e) {
			System.out.println(fontFile.getName() + " is not well-formed.");
		}
		catch (IOException e) {
			System.out.println("Due to an IOException, the parser could not check " + fontFile.getName());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
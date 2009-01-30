import org.xml.sax.*;
import org.xml.sax.helpers.*;
import org.pdfbox.pdmodel.*;
import org.pdfbox.pdmodel.font.*;
import java.io.*;

public class FontProcessor {
	
	private File fontFile;
	
	public FontProcessor(File fontFile) {
		this.fontFile = fontFile;
	}

	public PDFont process() {
		PDFont font = null;
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
			FontHandler fontHandler = new FontHandler(fontFile);
			parser.setContentHandler(fontHandler);
			parser.parse(new InputSource(new FileInputStream(fontFile)));
			font = fontHandler.getFont();
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
		return font;
		
	}
	
}
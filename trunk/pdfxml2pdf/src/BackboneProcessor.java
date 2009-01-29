import org.xml.sax.*;
import org.xml.sax.helpers.*;
import org.pdfbox.pdmodel.*;
import java.io.*;

public class BackboneProcessor {

	private PDDocument targetPDF;
	private File bbFile;
	public BackboneProcessor(File bbFile, PDDocument targetPDF) {
		this.bbFile = bbFile;
		this.targetPDF = targetPDF;
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
			BackboneHandler bbHandler = new BackboneHandler(bbFile, targetPDF);
			parser.setContentHandler(bbHandler);
			InputSource inputSource = new InputSource(new FileInputStream(bbFile));
			parser.parse(inputSource);
		}
		catch (SAXException e) {
			System.out.println("backbone.xml is not well-formed.");
		}
		catch (IOException e) {
			System.out.println("Due to an IOException, the parser could not check backbone.xml");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
//		return targetPDF;
	}
}
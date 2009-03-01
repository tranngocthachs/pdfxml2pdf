package svg.simplestructure;

import org.pdfbox.pdmodel.edit.PDPageContentStream;
import org.pdfbox.pdmodel.PDPage;
import org.xml.sax.Attributes;
public class SVGTag extends CompositeSVGTag {
	
	public SVGTag(PDPageContentStream pageContentStream, PDPage page, Attributes attributes) {
		super(pageContentStream, page, attributes);	
	}
	
	
	public void serialise() throws java.io.IOException {
		// TODO Auto-generated method stub
		float pageHeight = page.findMediaBox().getHeight();
		pageContentStream.appendRawCommands("1 0 0 -1 0 " + pageHeight + " cm\n");
		super.serialise();
	}

}

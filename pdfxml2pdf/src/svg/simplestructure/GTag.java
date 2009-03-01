package svg.simplestructure;

import java.io.IOException;
import org.xml.sax.Attributes;
import org.pdfbox.pdmodel.edit.PDPageContentStream;
import org.pdfbox.pdmodel.PDPage;
import org.xml.sax.Attributes;

public class GTag extends CompositeSVGComponent {
	public GTag(PDPageContentStream pageContentStream, PDPage page, Attributes attributes) {
		super(pageContentStream, page, attributes);
	}
	
	public void serialise() throws IOException {
		pageContentStream.appendRawCommands("q\n");
		handlePaintPropertiesAtt(attributes);
		if (attributes.getValue("transform") != null)
			handleTransformAtt(attributes.getValue("transform"));
		super.serialise();
		pageContentStream.appendRawCommands("Q\n");
	}
	
	


}

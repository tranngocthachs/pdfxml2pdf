package svg.simplestructure;

import java.io.IOException;
import org.xml.sax.Attributes;
import org.pdfbox.pdmodel.edit.PDPageContentStream;
import org.pdfbox.pdmodel.PDPage;
import org.xml.sax.Attributes;

public class GTag extends CompositeSVGTag {
	public GTag(PDPageContentStream pageContentStream, PDPage page, Attributes attributes) {
		super(pageContentStream, page, attributes);
		handlingPaint = new HandlingPaintAtt();
		handlingTransform = new HandlingTransformAtt();
	}
	
	public void serialise() throws IOException {
		pageContentStream.appendRawCommands("q\n");
		String paintCmd = handlingPaint.handlePaintPropertiesAtt(attributes);
		pageContentStream.appendRawCommands(paintCmd);
		if (attributes.getValue("transform") != null) {
			String transCmd = handlingTransform.handleTransformAtt(attributes.getValue("transform"));
			pageContentStream.appendRawCommands(transCmd);
		}		
		super.serialise();
		pageContentStream.appendRawCommands("Q\n");
	}
	
	


}

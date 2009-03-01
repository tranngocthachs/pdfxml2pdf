package svg.simplestructure;
import org.pdfbox.pdmodel.PDPage;
import org.pdfbox.pdmodel.edit.PDPageContentStream;
import org.xml.sax.Attributes;
import java.io.IOException;

public class TspanTag extends TextTag {
	public TspanTag(PDPageContentStream pageContentStream, PDPage page, Attributes attributes) {
		super(pageContentStream, page, attributes);
	}
	
	public void serialise() throws IOException {
		pageContentStream.appendRawCommands("q\n");
		handlePaintPropertiesAtt(attributes);
		handleTextPropertiesAtt(attributes);
		if (attributes.getValue("transform") != null) 
			handleTransformAtt(attributes.getValue("transform"));
		for (SVGComponent comp : childComponents) {
			comp.serialise();
		}
		pageContentStream.appendRawCommands("Q\n");
	}
}

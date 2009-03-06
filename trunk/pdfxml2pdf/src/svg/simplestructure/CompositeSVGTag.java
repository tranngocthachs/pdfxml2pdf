package svg.simplestructure;

import java.io.IOException;
import java.util.ArrayList;
import org.pdfbox.pdmodel.edit.PDPageContentStream;
import org.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.pdfbox.pdmodel.PDPage;
import org.xml.sax.Attributes;

public class CompositeSVGTag extends GeneralSVGTag {

	protected ArrayList<SVGComponent> childComponents = new ArrayList<SVGComponent>();
	
	protected CompositeSVGTag(PDPageContentStream pageContentStream, PDPage page, Attributes attributes) {
		super(pageContentStream, page, attributes);
	}
	
	public void serialise() throws java.io.IOException {
		for (SVGComponent comp : childComponents) {
			comp.serialise();
		}
	}
	
	public void add(SVGComponent comp) {
		childComponents.add(comp);
	}
	
	public void remove(SVGComponent comp) {
		childComponents.remove(comp);
	}
		
}

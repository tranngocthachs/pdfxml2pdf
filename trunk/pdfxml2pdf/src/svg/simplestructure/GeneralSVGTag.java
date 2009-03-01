package svg.simplestructure;
import org.xml.sax.Attributes;
import java.io.IOException;
import org.pdfbox.pdmodel.PDPage;
import org.pdfbox.pdmodel.edit.PDPageContentStream;

public class GeneralSVGTag extends SVGComponent {
	protected Attributes attributes = null;

	protected GeneralSVGTag(PDPageContentStream pageContentStream, PDPage page, Attributes attributes) {
		super(pageContentStream, page);
		this.attributes = attributes;
	}
	public void serialise() throws IOException {
		// TODO Auto-generated method stub

	}
}

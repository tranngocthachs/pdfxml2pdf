package svg.simplestructure;
import org.pdfbox.pdmodel.edit.PDPageContentStream;
import org.pdfbox.pdmodel.PDPage;

public abstract class SVGComponent {
	protected PDPageContentStream pageContentStream = null;
	protected PDPage page = null;
	
	protected SVGComponent(PDPageContentStream pageContentStream, PDPage page) {
		this.pageContentStream = pageContentStream;
		this.page = page;
	}
	abstract void serialise() throws java.io.IOException;
}

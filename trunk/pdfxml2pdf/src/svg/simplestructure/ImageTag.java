package svg.simplestructure;

import pdfxml2pdf.ConverterUtils;
import java.io.IOException;
import java.io.File;
import org.pdfbox.pdmodel.PDPage;
import org.pdfbox.pdmodel.edit.PDPageContentStream;
import org.xml.sax.Attributes;
public class ImageTag extends GeneralSVGTag {
	private File pageFile = null;
	public ImageTag(PDPageContentStream pageContentStream, PDPage page, Attributes attributes, File pageFile) {
		super(pageContentStream, page, attributes);
		this.pageFile = pageFile;
	}

	public void serialise() throws IOException {
		int x = 0;
		int y = 0;
		int width = 0;
		int height = 0;
		File imgFile = null;
		if (attributes.getValue("x") != null)
			x = Integer.parseInt(attributes.getValue("x"));
		if (attributes.getValue("y") != null)
			y = Integer.parseInt(attributes.getValue("y"));
		if (attributes.getValue("width") != null)
			width = Integer.parseInt(attributes.getValue("width"));
		if (attributes.getValue("height") != null)
			height = Integer.parseInt(attributes.getValue("height"));
		if (attributes.getValue("xlink:href") != null)
			imgFile = ConverterUtils.getFile(pageFile, attributes.getValue("xlink:href"));
			
		pageContentStream.appendRawCommands("q\n");
		// TODO Auto-generated method stub

	}

}

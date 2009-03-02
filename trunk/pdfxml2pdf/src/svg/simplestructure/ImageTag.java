package svg.simplestructure;

import pdfxml2pdf.ConverterUtils;
import java.io.IOException;
import java.io.File;
import org.pdfbox.pdmodel.PDPage;
import org.pdfbox.pdmodel.edit.PDPageContentStream;
import org.xml.sax.Attributes;
import java.awt.image.*;
import javax.imageio.ImageIO;
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
		imgFile = ConverterUtils.getFile(pageFile, attributes.getValue("xlink:href"));
		if (imgFile.getName().endsWith("png")) {
			BufferedImage img = ImageIO.read(imgFile);
			int type = img.getData().getDataBuffer().getDataType();
			switch (type) {
				case DataBuffer.TYPE_BYTE:
					ByteData
					
			}
		}
		
			
		pageContentStream.appendRawCommands("q\n");
		// TODO Auto-generated method stub

	}

}

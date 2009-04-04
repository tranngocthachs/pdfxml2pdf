package svg.simplestructure;
import org.pdfbox.pdmodel.PDPage;
import org.pdfbox.pdmodel.edit.PDPageContentStream;
import org.pdfbox.pdmodel.font.PDFont;
import org.xml.sax.Attributes;

import pdfxml2pdf.ConverterUtils;

import java.io.IOException;

public class TspanTag extends TextTag {
	public TspanTag(PDPageContentStream pageContentStream, PDPage page, Attributes attributes, TextTag parentTag) {
		super(pageContentStream, page, attributes);
		this.parentTextTag = parentTag;
		handlingTransform = new HandlingTransformAtt();
	}
	public TextTag getParentTextTag() {
		return parentTextTag;
	}
	public void serialise() throws IOException {
		if (attributes.getValue("transform") != null) {
			String transCmd = handlingTransform.handleTransformAtt(attributes.getValue("transform"));
			pageContentStream.appendRawCommands(transCmd);
		} 
			
		for (SVGComponent comp : childComponents) {
			comp.serialise();
		}
	}
//	private void handleTextPropertiesAtt(Attributes attributes) {
//		try {
//			if (attributes.getValue("font-family") != null &&
//				attributes.getValue("font-size") != null) {
//				PDFont font = (PDFont)page.findResources().getFonts().get(attributes.getValue("font-family"));
//				if (font != null)
//					pageContentStream.appendRawCommands("/" + attributes.getValue("font-family") + " "
//													+ ConverterUtils.formatDecimal.format(Float.parseFloat(attributes.getValue("font-size")))
//													+ " Tf\n");
//			}
//			
//		}
//		catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
}

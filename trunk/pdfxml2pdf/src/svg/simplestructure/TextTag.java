package svg.simplestructure;

import java.io.IOException;

import org.pdfbox.pdmodel.edit.PDPageContentStream;
import org.pdfbox.pdmodel.PDPage;
import org.pdfbox.pdmodel.font.PDFont;
import org.xml.sax.Attributes;

public class TextTag extends CompositeSVGTag {
	protected String[] xs = null;
	protected String[] ys = null;
	
	public TextTag(PDPageContentStream pageContentStream, PDPage page, Attributes attributes) {
		super(pageContentStream, page, attributes);
		if (attributes.getValue("x") != null)
			xs = attributes.getValue("x").trim().split("( *, *)|( +)");
		else {
			xs = new String[1];
			xs[0] = "0";
		}
			
		if (attributes.getValue("y") != null) 
			ys = attributes.getValue("y").trim().split("( *, *)|( +)");
		else {
			ys = new String[1];
			ys[0] = "0";
		}
		
	}
	public void serialise() throws java.io.IOException {
		pageContentStream.appendRawCommands("q\n");
		pageContentStream.beginText();
		handlePaintPropertiesAtt(attributes);
		handleTextPropertiesAtt(attributes);
		if (attributes.getValue("transform") != null) { 
			handleTransformAtt(attributes.getValue("transform"));
		}
		super.serialise();
		pageContentStream.endText();
		pageContentStream.appendRawCommands("Q\n");
	} 
	
	protected void handleTextPropertiesAtt(Attributes attributes) {
		try {
			if (attributes.getValue("font-family") != null &&
				attributes.getValue("font-size") != null) {
				PDFont font = (PDFont)page.findResources().getFonts().get(attributes.getValue("font-family"));
				pageContentStream.setFont(font, Float.parseFloat(attributes.getValue("font-size")));
			}
			
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String[] getXs() {
		String[] temp = xs;
		if ( !(xs.length == 1 && xs[0].equals("0")) ) {
			xs = new String[1];
			xs[0] = "0";
		}
		return temp;
	}
	public String[] getYs() {
		String[] temp = ys;
		if ( !(ys.length == 1 && ys[0].equals("0")) ) {
			ys = new String[1];
			ys[0] = "0";
		}
		return temp;
	}
}

package svg.simplestructure;
import java.io.IOException;
import org.pdfbox.pdmodel.edit.PDPageContentStream;
import org.pdfbox.pdmodel.font.PDFont;
import org.pdfbox.pdmodel.PDPage;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import pdfxml2pdf.ConverterUtils;


public class Text extends GeneralSVGTag {
	private String str = "";
	private String[] xs = null;
	private String[] ys = null;
	private TextTag parentTag;
	public Text(PDPageContentStream pageContentStream, PDPage page, String str, String[] xs, String[] ys, TextTag parentTag) {
		super(pageContentStream, page, null);
		this.str = str;
		this.xs = xs;
		this.ys = ys;
		this.parentTag = parentTag;
	}
	private void amendAtt(Attributes att, Attributes parentAtt) {
		if (att == null) {
			att = new AttributesImpl(parentAtt);
		}
		else {
			if ( (parentAtt.getValue("font-family") != null) && (att.getValue("font-family") == null) ) {
				int index = parentAtt.getIndex("font-family");
				((AttributesImpl)att).addAttribute(	parentAtt.getURI(index), 
													parentAtt.getLocalName(index), 
													parentAtt.getQName(index), 
													parentAtt.getType(index), 
													parentAtt.getValue(index));
			}
			if ( (parentAtt.getValue("font-size") != null) && (att.getValue("font-size") == null) ) {
				int index = parentAtt.getIndex("font-size");
				((AttributesImpl)att).addAttribute(	parentAtt.getURI(index), 
													parentAtt.getLocalName(index), 
													parentAtt.getQName(index), 
													parentAtt.getType(index), 
													parentAtt.getValue(index));
			}
			if ( (parentAtt.getValue("fill") != null) && (att.getValue("fill") == null) ) {
				int index = parentAtt.getIndex("fill");
				((AttributesImpl)att).addAttribute(	parentAtt.getURI(index), 
													parentAtt.getLocalName(index), 
													parentAtt.getQName(index), 
													parentAtt.getType(index), 
													parentAtt.getValue(index));
			}
			if ( (parentAtt.getValue("stroke") != null) && (att.getValue("stroke") == null) ) {
				int index = parentAtt.getIndex("stroke");
				((AttributesImpl)att).addAttribute(	parentAtt.getURI(index), 
													parentAtt.getLocalName(index), 
													parentAtt.getQName(index), 
													parentAtt.getType(index), 
													parentAtt.getValue(index));
			}
		}
	}
	public void serialise() throws IOException {
		// TODO Auto-generated method stub
		
		
		if (str.length() != 0) {
			// merging all the attributes
			
			TextTag iterator = parentTag;
			Attributes att = new AttributesImpl(parentTag.getAtt());
			while (iterator.getParentTextTag() != null) {
				amendAtt(att, iterator.getParentTextTag().getAtt());
				iterator = iterator.getParentTextTag();
			}
			
			
			handlePaintPropertiesAtt(att);
			handleTextPropertiesAtt(att);
			if (xs == null && ys == null) {
				pageContentStream.drawString(str);
			}
			else {
				if (xs == null) {
					xs = new String[1];
					xs[0] = "0";
				}
				if (ys == null) {
					ys = new String[1];
					ys[0] = "0";
				}
				int lengthOfCoordinates = Math.max(xs.length, ys.length);
				int lengthOfStr = str.length();
				int lengthToProcess = Math.min(lengthOfCoordinates, lengthOfStr);
				if (lengthToProcess == 1) {
					String cmd = "1 0 0 -1 " + xs[0] + " " + ys[0] + " Tm\n";
					pageContentStream.appendRawCommands(cmd);
					pageContentStream.drawString(str);
				}
				else {
					for (int i = 0; i<lengthToProcess; i++) {
						String x = (i<xs.length)?xs[i]:"0";
						String y = (i<ys.length)?ys[i]:"0";
						pageContentStream.appendRawCommands("1 0 0 -1 " + x + " " + y +" Tm\n");
						if (i<lengthToProcess-1)
							pageContentStream.drawString(str.substring(i, i+1));
						else
							pageContentStream.drawString(str.substring(i));
					}
					
				}
			}
				
			
		}

	}
	private void handleTextPropertiesAtt(Attributes attributes) {
		try {
			if (attributes.getValue("font-family") != null &&
				attributes.getValue("font-size") != null) {
				PDFont font = (PDFont)page.findResources().getFonts().get(attributes.getValue("font-family"));
				if (font != null)
					pageContentStream.appendRawCommands("/" + attributes.getValue("font-family") + " "
													+ ConverterUtils.formatDecimal.format(Float.parseFloat(attributes.getValue("font-size")))
													+ " Tf\n");
			}
			
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

}

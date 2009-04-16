package svg.simplestructure;
import java.io.IOException;
import org.pdfbox.pdmodel.edit.PDPageContentStream;
import org.pdfbox.pdmodel.font.PDFont;
import org.pdfbox.pdmodel.PDPage;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import pdfxml2pdf.ConverterUtils;


public class Text extends SVGComponent {
	private String str = "";
	private String[] xs = null;
	private String[] ys = null;
	private TextTag parentTag;
	public Text(PDPageContentStream pageContentStream, PDPage page, String str, String[] xs, String[] ys, TextTag parentTag) {
		super(pageContentStream, page);
		this.str = str;
		this.xs = xs;
		this.ys = ys;
		this.parentTag = parentTag;
		handlingPaint = new HandlingPaintAtt();
	}
	private void amendAtt(Attributes att, Attributes parentAtt) {
		if (att == null) {
			att = new AttributesImpl(parentAtt);
		}
		else {
			int length = parentAtt.getLength();
			for (int i=0; i<length; i++) {
				if (att.getValue(parentAtt.getQName(i)) == null) {
					((AttributesImpl)att).addAttribute(	parentAtt.getURI(i), 
							parentAtt.getLocalName(i), 
							parentAtt.getQName(i), 
							parentAtt.getType(i), 
							parentAtt.getValue(i));
				}

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
			
			String paintCmd = handlingPaint.handlePaintPropertiesAtt(att);
			
			
			
			pageContentStream.appendRawCommands(paintCmd);
			// take care of text rendering mode
			if ((att.getValue("fill") != null) && (att.getValue("stroke") == null))
				pageContentStream.appendRawCommands("0 Tr\n");
			if ((att.getValue("fill") != null) && (att.getValue("stroke") != null) && (att.getValue("stroke-width") == null ) )
				pageContentStream.appendRawCommands("2 Tr\n");
			if ((att.getValue("fill") == null) && (att.getValue("stroke") != null) && (att.getValue("stroke-width") == null ))
				pageContentStream.appendRawCommands("1 Tr\n");
				
//			parentTag.handlePaintPropertiesAtt(att);
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
					if (xs.length>=ys.length) {
						for (int i = 0; i<lengthToProcess; i++) {
							String x = (i<xs.length)?xs[i]:xs[xs.length-1];
							String y = (i<ys.length)?ys[i]:ys[ys.length-1];
							pageContentStream.appendRawCommands("1 0 0 -1 " + x + " " + y +" Tm\n");
							if (i<lengthToProcess-1)
								pageContentStream.drawString(str.substring(i, i+1));
							else
								pageContentStream.drawString(str.substring(i));
						}
					}
					else {
						
						// this is an experimental hack
						
						for (int i = 0; i<lengthToProcess; i++) {
							if (i<(xs.length)) {
								pageContentStream.appendRawCommands("1 0 0 -1 " + xs[i] + " " + ys[i] +" Tm\n");
								pageContentStream.drawString(str.substring(i, i+1));
							}
								
							else {
								Double amountToRaise = Double.parseDouble(ys[i]) - Double.parseDouble(ys[xs.length-1]);
								pageContentStream.appendRawCommands(ConverterUtils.formatDecimal.format(amountToRaise*(-1)) + " Ts\n") ;
								if (i<lengthToProcess-1)
									pageContentStream.drawString(str.substring(i, i+1));
								else
									pageContentStream.drawString(str.substring(i));
							}
						}
						pageContentStream.appendRawCommands("0 Ts\n");
					
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

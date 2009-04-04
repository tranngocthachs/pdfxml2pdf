package svg.simplestructure;
import org.pdfbox.pdmodel.edit.PDPageContentStream;
import org.pdfbox.pdmodel.PDPage;

import java.io.IOException;
import org.xml.sax.Attributes;


import org.apache.batik.parser.PathParser;

public class PathTag extends GeneralSVGTag {
	public PathTag(PDPageContentStream pageContentStream, PDPage page, Attributes attributes) {
		super(pageContentStream, page, attributes);
		handlingTransform = new HandlingTransformAtt();
		handlingPaint = new HandlingPaintAtt();
	}

	public void serialise() throws IOException {
		pageContentStream.appendRawCommands("q\n");
		if (attributes.getValue("transform") != null) {
			String transCmd = handlingTransform.handleTransformAtt(attributes.getValue("transform"));
			pageContentStream.appendRawCommands(transCmd);
		}
			
		String paintCmd = handlingPaint.handlePaintPropertiesAtt(attributes);
		pageContentStream.appendRawCommands(paintCmd);
		PathParser pp = new PathParser();
		SVGPathHandler ph = new SVGPathHandler();
		pp.setPathHandler(ph);
		pp.parse(attributes.getValue("d"));
		pageContentStream.appendRawCommands(ph.getPDFCmd());
		paintCmd = "";
		if (attributes.getValue("fill") != null) {
			if (attributes.getValue("stroke") != null) {
				if (attributes.getValue("fill-rule") != null) {
					if (attributes.getValue("fill-rule").equals("evenodd"))
						paintCmd = "B*\n";
					else {
						if (attributes.getValue("fill-rule").equals("nonzero"))
							paintCmd = "B\n";
					}
				}
				else {
					paintCmd="B\n";
				}
			}
			else {
				if (attributes.getValue("fill-rule") != null) {
					if (attributes.getValue("fill-rule").equals("evenodd"))
						paintCmd = "f*\n";
					else {
						if (attributes.getValue("fill-rule").equals("nonzero"))
							paintCmd = "f\n";
					}
				}
				else {
					paintCmd = "f\n";
				}
			}
		}
		else {
			if (attributes.getValue("stroke") != null)
				paintCmd = "S\n";
		}
		pageContentStream.appendRawCommands(paintCmd);
		pageContentStream.appendRawCommands("Q\n");
	}

}

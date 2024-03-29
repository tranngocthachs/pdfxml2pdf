package svg.simplestructure;
import org.xml.sax.Attributes;
import java.io.IOException;
import org.pdfbox.pdmodel.PDPage;
import org.pdfbox.pdmodel.edit.PDPageContentStream;
import org.pdfbox.pdmodel.graphics.color.PDColorSpace;

import pdfxml2pdf.ConverterUtils;

public class GeneralSVGTag extends SVGComponent {
	protected Attributes attributes = null;
	protected GeneralSVGTag(PDPageContentStream pageContentStream, PDPage page, Attributes attributes) {
		super(pageContentStream, page);
		this.attributes = attributes;
	}
	public void serialise() throws IOException {
		// TODO Auto-generated method stub

	}
	

//	protected void handlePaintPropertiesAtt(Attributes attributes) {
//		if (attributes.getValue("fill") != null)
//			handleFillAtt(attributes.getValue("fill"));
//		if (attributes.getValue("stroke") != null) 
//			handleStrokeAtt(attributes.getValue("stroke"));
//		
//		if (attributes.getValue("stroke-linecap") != null) {
//			String lineCapValue = attributes.getValue("stroke-linecap");
//			try {
//				if (lineCapValue.equals("butt"))
//					pageContentStream.appendRawCommands("0 J\n");
//				else if (lineCapValue.equals("round"))
//					pageContentStream.appendRawCommands("1 J\n");
//				else if (lineCapValue.equals("square"))
//					pageContentStream.appendRawCommands("2 J\n");
//			}
//			catch (IOException e) {
//				e.printStackTrace();
//			}
//			
//		}
//		
//		if (attributes.getValue("stroke-linejoin") != null) {
//			String lineJoinValue = attributes.getValue("stroke-linejoin");
//			try {
//				if (lineJoinValue.equals("miter"))
//					pageContentStream.appendRawCommands("0 j\n");
//				else if (lineJoinValue.equals("round"))
//					pageContentStream.appendRawCommands("1 j\n");
//				else if (lineJoinValue.equals("bevel"))
//					pageContentStream.appendRawCommands("2 j\n");
//			}
//			catch (IOException e) {
//				e.printStackTrace();
//			}
//			
//		}
//		
//		if (attributes.getValue("stroke-miterlimit") != null) {
//			try {
//				pageContentStream.appendRawCommands(attributes.getValue("stroke-miterlimit") + " M\n");
//			}
//			catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//		
//		if (attributes.getValue("stroke-width") != null) {
//			try {
//				pageContentStream.appendRawCommands(attributes.getValue("stroke-width") + " w\n");
//			}
//			catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//			
//		
//			
//	}
//	
//	protected void handleTransformAtt(String transformString) {
//		String splitAt = "\\)(( *, *)|( +))";
//		
//		String[] transforms = transformString.split(splitAt);
//		transforms[transforms.length - 1] = transforms[transforms.length - 1].substring(0, transforms[transforms.length-1].length() - 1);
//		for (int i=0; i<transforms.length; i++) {
//			if (transforms[i].startsWith("matrix")) {
//				char oPara = '(';
//				String numberStr = transforms[i].substring(transforms[i].indexOf(oPara) + 1);
//				try {
//					pageContentStream.appendRawCommands(numberStr + " cm\n");
//				}
//				catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		
//		
//	}
//	
//	protected void handleFillAtt(String fillAtt) {
//		String splitAt = "\\)(( *, *)|( +))";
//		String[] colors = fillAtt.split(splitAt);
//		colors[colors.length - 1] = colors[colors.length - 1].substring(0, colors[colors.length-1].length() - 1);
//		
//		
//		// device-color or icc-color might present
//		if (colors.length == 2) {
//			char oPara = '(';
//			// take content inside ()
//			String colorStr = colors[1].substring(colors[1].indexOf(oPara) + 1);
//			String[] colorElems = colorStr.split(" *, *");
//			
//			try {
//				// color specified with device-color function
//				if (colors[1].startsWith("device-color")) {
//					if (colorElems[0].equals("DeviceGray")) {
//						pageContentStream.setNonStrokingColor(Double.parseDouble(colorElems[1]));
//					}
//					else if (colorElems[0].equals("DeviceCMYK")) {
//						pageContentStream.setNonStrokingColor(	Double.parseDouble(colorElems[1]),
//																Double.parseDouble(colorElems[2]), 
//																Double.parseDouble(colorElems[3]),
//																Double.parseDouble(colorElems[4]));
//					}
//					else if (colorElems[0].equals("DeviceRGB")) {
//						pageContentStream.setNonStrokingColor(	(int)(Double.parseDouble(colorElems[1])*255),
//																(int)(Double.parseDouble(colorElems[2])*255), 
//																(int)(Double.parseDouble(colorElems[3])*255));
//					}
//				}
//				
//				else if (colors[1].startsWith("icc-color")) {
//					PDColorSpace color = (PDColorSpace)page.findResources().getColorSpaces().get(colorElems[0]);
//					if (color != null) {
//						pageContentStream.appendRawCommands("/" + colorElems[0] + " cs\n");
//						String cmd = "";
//						for (int i=1; i<colorElems.length; i++) {
//							cmd+=ConverterUtils.formatDecimal.format(Float.parseFloat(colorElems[i]));
//							cmd+=" ";
//						}
//						pageContentStream.appendRawCommands(cmd + "scn\n");
//						
//					}
//				}
//				
//			}
//			catch (IOException e) {
//				e.printStackTrace();
//			}	
//		}
//		
//		// case of DeviceRGB
//		else if (colors.length == 1) {
//			char oPara = '(';
//			// take content inside ()
//			String colorStr = colors[0].substring(colors[0].indexOf(oPara) + 1);
//			String[] colorElems = colorStr.split(" *, *");
//			
//			try {
//				// color specified with rgb(...)
//				if (colors[0].startsWith("rgb") && colorElems.length == 3) {
//					pageContentStream.setNonStrokingColor(	Integer.parseInt(colorElems[0]), 
//															Integer.parseInt(colorElems[1]),
//															Integer.parseInt(colorElems[2]));
//				}
//			}
//			catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//	}
//	
//	protected void handleStrokeAtt(String strokeAtt) {
//		String splitAt = "\\)(( *, *)|( +))";
//		String[] colors = strokeAtt.split(splitAt);
//		colors[colors.length - 1] = colors[colors.length - 1].substring(0, colors[colors.length-1].length() - 1);
//		
//		
//		// device-color or icc-color might present
//		if (colors.length == 2) {
//			char oPara = '(';
//			// take content inside ()
//			String colorStr = colors[1].substring(colors[1].indexOf(oPara) + 1);
//			String[] colorElems = colorStr.split(" *, *");
//			
//			try {
//				// color specified with device-color function
//				if (colors[1].startsWith("device-color")) {
//					if (colorElems[0].equals("DeviceGray")) {
//						pageContentStream.setStrokingColor(Double.parseDouble(colorElems[1]));
//					}
//					else if (colorElems[0].equals("DeviceCMYK")) {
//						pageContentStream.setStrokingColor(	Double.parseDouble(colorElems[1]),
//															Double.parseDouble(colorElems[2]), 
//															Double.parseDouble(colorElems[3]),
//															Double.parseDouble(colorElems[4]));
//					}
//					else if (colorElems[0].equals("DeviceRGB")) {
//						pageContentStream.setStrokingColor(	(int)(Double.parseDouble(colorElems[1])*255),
//																(int)(Double.parseDouble(colorElems[2])*255), 
//																(int)(Double.parseDouble(colorElems[3])*255));
//					}
//					
//				}
//				
//				else if (colors[1].startsWith("icc-color")) {
//					PDColorSpace color = (PDColorSpace)page.findResources().getColorSpaces().get(colorElems[0]);
//					if (color != null) {
//						pageContentStream.appendRawCommands("/" + colorElems[0] + " cs\n");
//						String cmd = "";
//						for (int i=1; i<colorElems.length; i++) {
//							cmd+=ConverterUtils.formatDecimal.format(Float.parseFloat(colorElems[i]));
//							cmd+=" ";
//						}
//						pageContentStream.appendRawCommands(cmd + "SCN\n");
//					}
//				}
//			}
//			catch (IOException e) {
//				e.printStackTrace();
//			}	
//		}
//		
//		// case of DeviceRGB
//		else if (colors.length == 1) {
//			char oPara = '(';
//			// take content inside ()
//			String colorStr = colors[0].substring(colors[0].indexOf(oPara) + 1);
//			String[] colorElems = colorStr.split(" *, *");
//			
//			try {
//				// color specified with rgb(...)
//				if (colors[0].startsWith("rgb") && colorElems.length == 3) {
//					pageContentStream.setStrokingColor(	Integer.parseInt(colorElems[0]), 
//														Integer.parseInt(colorElems[1]),
//														Integer.parseInt(colorElems[2]));
//				}
//			}
//			catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//	}

	
}

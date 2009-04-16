package svg.simplestructure;

import java.io.IOException;
import java.text.NumberFormat;
import org.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.xml.sax.Attributes;

import pdfxml2pdf.ConverterUtils;

public class HandlingPaintAtt implements Paintable {
	private static final String RG_STROKING = "RG\n";
    private static final String RG_NON_STROKING = "rg\n";
    private static final String K_STROKING = "K\n";
    private static final String K_NON_STROKING = "k\n";
    private static final String G_STROKING = "G\n";
    private static final String G_NON_STROKING = "g\n";
    private static final String SET_STROKING_COLORSPACE = "CS\n";
    private static final String SET_NON_STROKING_COLORSPACE = "cs\n";
    
    private static final String SET_STROKING_COLOR_SIMPLE="SC\n";
    private static final String SET_STROKING_COLOR_COMPLEX="SCN\n";
    private static final String SET_NON_STROKING_COLOR_SIMPLE="sc\n";
    private static final String SET_NON_STROKING_COLOR_COMPLEX="scn\n";
    
    private static final String SET_LINE_WIDTH="w\n";
    private static final String SET_LINE_CAP="J\n";
    private static final String SET_LINE_JOIN="j\n";
    private static final String SET_MITER_LIMIT="M\n";
    
    private static final String SPACE = " ";
    private static final NumberFormat formatDecimal = ConverterUtils.formatDecimal; 
	public String handlePaintPropertiesAtt(Attributes attributes) {
		String retval = "";
		if (attributes.getValue("fill") != null)
			retval+=( handleFillAtt(attributes.getValue("fill")) );
		if (attributes.getValue("stroke") != null) 
			retval+=( handleStrokeAtt(attributes.getValue("stroke")) );
		
		if (attributes.getValue("stroke-linecap") != null) {
			String lineCapValue = attributes.getValue("stroke-linecap");
			int lcval = -1;
			if (lineCapValue.equals("butt"))
				lcval = 0;
			else if (lineCapValue.equals("round"))
				lcval = 1;
			else if (lineCapValue.equals("square"))
				lcval = 2;
			retval+=( lcval );
			retval+=( SPACE );
			retval+=( SET_LINE_CAP );
		}
		
		if (attributes.getValue("stroke-linejoin") != null) {
			String lineJoinValue = attributes.getValue("stroke-linejoin");
			int ljval = -1; 
			if (lineJoinValue.equals("miter"))
				ljval = 0;
			else if (lineJoinValue.equals("round"))
				ljval = 1;
			else if (lineJoinValue.equals("bevel"))
				ljval = 2;
			retval+=( ljval );
			retval+=( SPACE );
			retval+=( SET_LINE_JOIN );
		}
		
		if (attributes.getValue("stroke-miterlimit") != null) {
			double miterLimit = Double.parseDouble(attributes.getValue("stroke-miterlimit"));
			retval+=( formatDecimal.format(miterLimit) );
			retval+=( SPACE );
			retval+=( SET_MITER_LIMIT);
		}
		
		if (attributes.getValue("stroke-width") != null) {
			double strokeWidth = Double.parseDouble(attributes.getValue("stroke-width"));
			retval+=( formatDecimal.format(strokeWidth) );
			retval+=( SPACE );
			retval+=( SET_LINE_WIDTH );
		}
		return retval;
	}

	private String handleFillAtt(String fillAtt) {
		String retval = "";
		String splitAt = "\\)(( *, *)|( +))";
		String[] colors = fillAtt.split(splitAt);
		colors[colors.length - 1] = colors[colors.length - 1].substring(0, colors[colors.length-1].length() - 1);
		
		// device-color or icc-color might present
		if (colors.length == 2) {
			char oPara = '(';
			// take content inside ()
			String colorStr = colors[1].substring(colors[1].indexOf(oPara) + 1);
			String[] colorElems = colorStr.split(" *, *");
			
			// color specified with device-color function
			if (colors[1].startsWith("device-color")) {
				if (colorElems[0].equals("DeviceGray")) {
					retval+=( formatDecimal.format(Double.parseDouble(colorElems[1])) );
					retval+=( SPACE );
					retval+=( G_NON_STROKING );
					//pageContentStream.setNonStrokingColor(Double.parseDouble(colorElems[1]));
				}
				else if (colorElems[0].equals("DeviceCMYK")) {
					retval+=( formatDecimal.format( Double.parseDouble(colorElems[1] )) );
			        retval+=( SPACE );
			        retval+=( formatDecimal.format( Double.parseDouble(colorElems[2] )) );
			        retval+=( SPACE );
			        retval+=( formatDecimal.format( Double.parseDouble(colorElems[3] ) ) );
			        retval+=( SPACE );
			        retval+=( formatDecimal.format( Double.parseDouble(colorElems[4] ) ) );
			        retval+=( SPACE );
			        retval+=( K_NON_STROKING );
//						pageContentStream.setNonStrokingColor(	Double.parseDouble(colorElems[1]),
//																Double.parseDouble(colorElems[2]), 
//																Double.parseDouble(colorElems[3]),
//																Double.parseDouble(colorElems[4]));
				}
				else if (colorElems[0].equals("DeviceRGB")) {
					retval+=( formatDecimal.format( Double.parseDouble(colorElems[1] )) );
			        retval+=( SPACE );
			        retval+=( formatDecimal.format( Double.parseDouble(colorElems[2] )) );
			        retval+=( SPACE );
			        retval+=( formatDecimal.format( Double.parseDouble(colorElems[3] ) ) );
			        retval+=( SPACE );
			        retval+=( RG_NON_STROKING );
//						pageContentStream.setNonStrokingColor(	(int)(Double.parseDouble(colorElems[1])*255),
//																(int)(Double.parseDouble(colorElems[2])*255), 
//																(int)(Double.parseDouble(colorElems[3])*255));
				}
			}	
			else if (colors[1].startsWith("icc-color")) {
				retval+=( "/" + colorElems[0] );
				retval+=( SPACE );
				retval+=( SET_NON_STROKING_COLORSPACE );
//				pageContentStream.appendRawCommands("/" + colorElems[0] + " cs\n");
//				String cmd = "";
				for (int i=1; i<colorElems.length; i++) {
					retval+=( formatDecimal.format(Float.parseFloat(colorElems[i])) );
					retval+=( SPACE );
				}
				retval+=( SET_NON_STROKING_COLOR_COMPLEX );
					
			}
			
			
		}
		
		// case of DeviceRGB
		else if (colors.length == 1) {
			char oPara = '(';
			// take content inside ()
			String colorStr = colors[0].substring(colors[0].indexOf(oPara) + 1);
			String[] colorElems = colorStr.split(" *, *");
			
			// color specified with rgb(...)
			if (colors[0].startsWith("rgb") && colorElems.length == 3) {
				double red = (Double.parseDouble(colorElems[0])) / 255.0;
				double green = (Double.parseDouble(colorElems[1])) / 255.0;
				double blue = (Double.parseDouble(colorElems[2])) / 255.0;
				retval+=( formatDecimal.format(red) );
				retval+=( SPACE );
				retval+=( formatDecimal.format(green) ) ;
				retval+=( SPACE );
				retval+=( formatDecimal.format(blue) );
				retval+=( SPACE );
				retval+=( RG_NON_STROKING );
				
//				pageContentStream.setNonStrokingColor(	Integer.parseInt(colorElems[0]), 
//														Integer.parseInt(colorElems[1]),
//														Integer.parseInt(colorElems[2]));
			}
			
		}
		return retval;
	}
	
	private String handleStrokeAtt(String strokeAtt) {
		String retval = "";
		String splitAt = "\\)(( *, *)|( +))";
		String[] colors = strokeAtt.split(splitAt);
		colors[colors.length - 1] = colors[colors.length - 1].substring(0, colors[colors.length-1].length() - 1);
		
		// device-color or icc-color might present
		if (colors.length == 2) {
			char oPara = '(';
			// take content inside ()
			String colorStr = colors[1].substring(colors[1].indexOf(oPara) + 1);
			String[] colorElems = colorStr.split(" *, *");
			
			// color specified with device-color function
			if (colors[1].startsWith("device-color")) {
				if (colorElems[0].equals("DeviceGray")) {
					retval+=( formatDecimal.format(Double.parseDouble(colorElems[1])) );
					retval+=( SPACE );
					retval+=( G_STROKING );
					//pageContentStream.setNonStrokingColor(Double.parseDouble(colorElems[1]));
				}
				else if (colorElems[0].equals("DeviceCMYK")) {
					retval+=( formatDecimal.format( Double.parseDouble(colorElems[1] )) );
			        retval+=( SPACE );
			        retval+=( formatDecimal.format( Double.parseDouble(colorElems[2] )) );
			        retval+=( SPACE );
			        retval+=( formatDecimal.format( Double.parseDouble(colorElems[3] ) ) );
			        retval+=( SPACE );
			        retval+=( formatDecimal.format( Double.parseDouble(colorElems[4] ) ) );
			        retval+=( SPACE );
			        retval+=( K_STROKING );
//						pageContentStream.setNonStrokingColor(	Double.parseDouble(colorElems[1]),
//																Double.parseDouble(colorElems[2]), 
//																Double.parseDouble(colorElems[3]),
//																Double.parseDouble(colorElems[4]));
				}
				else if (colorElems[0].equals("DeviceRGB")) {
					retval+=( formatDecimal.format( Double.parseDouble(colorElems[1] )) );
			        retval+=( SPACE );
			        retval+=( formatDecimal.format( Double.parseDouble(colorElems[2] )) );
			        retval+=( SPACE );
			        retval+=( formatDecimal.format( Double.parseDouble(colorElems[3] ) ) );
			        retval+=( SPACE );
			        retval+=( RG_STROKING );
//						pageContentStream.setNonStrokingColor(	(int)(Double.parseDouble(colorElems[1])*255),
//																(int)(Double.parseDouble(colorElems[2])*255), 
//																(int)(Double.parseDouble(colorElems[3])*255));
				}
			}	
			else if (colors[1].startsWith("icc-color")) {
				retval+=( "/" + colorElems[0] );
				retval+=( SPACE );
				retval+=( SET_STROKING_COLORSPACE );
//				pageContentStream.appendRawCommands("/" + colorElems[0] + " cs\n");
//				String cmd = "";
				for (int i=1; i<colorElems.length; i++) {
					retval+=( formatDecimal.format(Float.parseFloat(colorElems[i])) );
					retval+=( SPACE );
				}
				retval+=( SET_STROKING_COLOR_COMPLEX );
					
			}
			
			
		}
		
		// case of DeviceRGB
		else if (colors.length == 1) {
			char oPara = '(';
			// take content inside ()
			String colorStr = colors[0].substring(colors[0].indexOf(oPara) + 1);
			String[] colorElems = colorStr.split(" *, *");
			
			// color specified with rgb(...)
			if (colors[0].startsWith("rgb") && colorElems.length == 3) {
				double red = (Double.parseDouble(colorElems[0])) / 255.0;
				double green = (Double.parseDouble(colorElems[1])) / 255.0;
				double blue = (Double.parseDouble(colorElems[2])) / 255.0;
				retval+=( formatDecimal.format(red) );
				retval+=( SPACE );
				retval+=( formatDecimal.format(green) ) ;
				retval+=( SPACE );
				retval+=( formatDecimal.format(blue) );
				retval+=( SPACE );
				retval+=( RG_STROKING );
				
//				pageContentStream.setNonStrokingColor(	Integer.parseInt(colorElems[0]), 
//														Integer.parseInt(colorElems[1]),
//														Integer.parseInt(colorElems[2]));
			}
			
		}
		return retval;
	}
}

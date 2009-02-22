import org.xml.sax.helpers.*;
import org.xml.sax.*;
import org.pdfbox.cos.COSArray;
import org.pdfbox.cos.COSName;
import org.pdfbox.pdmodel.*;
import org.pdfbox.pdmodel.edit.PDPageContentStream;
import org.pdfbox.pdmodel.font.*;
import org.pdfbox.pdmodel.common.*;
import org.pdfbox.pdmodel.graphics.color.*;
import org.pdfbox.cos.*;

import java.io.*;
import java.text.NumberFormat;
import java.util.*;
import java.util.regex.*;


public class PageContentHandler extends DefaultHandler {
	private PDPage page;
	private File pageContentFile;
	private PDFont font;
	private HashMap<String, PDFont> fontMappings;
	private HashMap<String, PDColorSpace> colorMappings;
	private String fontFamily;
	private PDPageContentStream pageContentStream;
	private NumberFormat formatDecimal = NumberFormat.getNumberInstance( Locale.US );
	private StringBuffer buffer = null;
	
	public PageContentHandler(File pageContentFile, PDPage page) {
		this.pageContentFile = pageContentFile;
		this.page = page;
	}
	
	public void startDocument() {
		try {
			pageContentStream = new PDPageContentStream(ConverterUtils.getTargetPDF(), page, false, false);
			float pageHeight = page.findMediaBox().getHeight();
			pageContentStream.appendRawCommands("1 0 0 -1 0 " +
					formatDecimal.format(pageHeight) + " cm\n");
		}
		catch (Exception e) { 
			e.printStackTrace();
		}
		
		
	}
	
	public void endDocument() {
		try {
			pageContentStream.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void startElement(String uri, String localname, String qName, Attributes attributes) {
		
		// process font-face tag
		if (qName.equals("font-face")) {
			System.out.println("Process font-face");
			fontFamily = attributes.getValue("font-family");
		}
		
		// process pdf:font-information tag 
		if (qName.equals("pdf:font-information")) {
			System.out.println("Process font-information");
			File fontFile = ConverterUtils.getFile(pageContentFile, attributes.getValue("xlink:href"));
			font = (new FontProcessor(fontFile)).process();
		}
		
		
		// process color-profile tag
		if (qName.equals("color-profile")) {
			System.out.println("color-profile");
			// as specified in MARS Spec. ICC profiles should be preferable. In fact, MARS would
			// like to drop support for CalGray, CalRGB and Lab in future version.
			// Thus, <color-profile> tag is simply a definition for /ICCBased in PDF
			
			String colorID = attributes.getValue("name");
			
			// PDFBox's API does not have a convenient way of creating such color-space.
			// So, we have to create the color space rather explicit.
			// The array specifying the ICCBased color space has the form
			// [/ICCBased stream]
			// So, we first create an array, then add the name /ICCBased and then the stream which holds data of the .icc file
			
            OutputStream out = null;
            InputStream in = null;
            File iccFile = ConverterUtils.getFile(pageContentFile, attributes.getValue("xlink:href"));
            
            // create an array
            COSArray iccArr = new COSArray();
            
            // the name of the color space (/ICCBased) is first in the array
            iccArr.add(COSName.getPDFName(PDICCBased.NAME));
            
            // create a stream holding the data from the .icc file
            PDStream pdstream = new PDStream(ConverterUtils.getTargetPDF());
            try {
            	in = new FileInputStream(iccFile);
            	out = pdstream.createOutputStream();
            	int c;
            	while ((c = in.read()) != -1)
            		out.write(c);
            	
            	in.close();
            	out.close();
            	
            }
            catch (IOException e) { 
            	
            }
            
            // the data from .icc file is second in the array
            iccArr.add(pdstream);
            
            // finally, make the color space object out of this array
            PDColorSpace colorSpace = new PDICCBased(iccArr);
            
            // set number of components
            ((PDICCBased)colorSpace).setNumberOfComponents(Integer.parseInt(attributes.getValue("pdf:Count")));
            
            // put this color space into our collection
            if (colorMappings == null)
            	colorMappings = new HashMap<String, PDColorSpace>();
            colorMappings.put(colorID, colorSpace);
		}

		
		// process pdf:Indexed tag
		if (qName.equals("pdf:Indexed")) {
			System.out.println("indexed");
			// this is simply a definition for /Indexed in PDF
			
			// again, we have to explicitly make an array representing the color profile
			// and then make the PDIndexed object out of it.
			// the array for Indexed color space has the form
			// [/Indexed base hival lookup]
			
			String colorID = attributes.getValue("Name");
            
            
            // create an array representing the color space
            COSArray indexedArr = new COSArray();
            
            // the name of the color space is first (/Indexed)
            indexedArr.add(COSName.getPDFName(PDIndexed.NAME));
            
            // the base of this indexed color space is second, this should be a PDColorSpace's object
            indexedArr.add(colorMappings.get(attributes.getValue("Base")));
            
            // hival parameter comes next
            indexedArr.add(new COSInteger(Integer.parseInt(attributes.getValue("HiVal"))));
            
            InputStream in = null;
            byte[] buffer = null;
            File lut = ConverterUtils.getFile(pageContentFile, attributes.getValue("LookupTableSrc"));
            try {
            	in = new FileInputStream(lut);
            	buffer = new byte[(int)(lut.length())];
            	in.read(buffer);
            }
            catch (Exception e) { }
            COSString lookupString = new COSString(buffer);
            
            // the lookup table is last
            indexedArr.add(lookupString);
            
            // make a color space from this array 
            PDColorSpace colorSpace = new PDIndexed(indexedArr);
            
            // put this color space into our collection
            if (colorMappings == null)
            	colorMappings = new HashMap<String, PDColorSpace>();
            colorMappings.put(colorID, colorSpace);
            
		}
		
		// process g tag
		if (qName.equals("g")) {
			try {
				pageContentStream.appendRawCommands("q\n");
				handlePaintPropertiesAtt(attributes);
				if (attributes.getValue("transform") != null)
					handleTransformAtt(attributes.getValue("transform"));
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		// process text tag
		if (qName.equals("text")) {
			handleText(attributes);
		}
		
		if (qName.equals("tspan")) {
			handleTSpan(attributes);
		}
		
		
		
		
	}
	public void endElement(String uri, String localname, String qName) {
		if (qName.equals("font-face")) {
			if (fontMappings == null)
				fontMappings = new HashMap<String, PDFont>();
			if (fontFamily != null && font != null)
				fontMappings.put(fontFamily, font);
		}
		
		if (qName.equals("defs")) {
			// probably just wanna test
			// no need to explicitly add this to resources as it will be add automatically while using later.
//			if (fontMappings != null || colorMappings != null) {
//				PDResources resources = new PDResources();
//				if (fontMappings != null)
//					resources.setFonts(fontMappings);
//				if (colorMappings != null)
//					resources.setColorSpaces(colorMappings);
//				page.setResources(resources);
//			}
			
		}
		
		if (qName.equals("g")) {
			try {
				pageContentStream.appendRawCommands("Q\n");
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		// text and tspan below gonna fail on array of coordinates supplied for x-attributes or y-attributes
		
		if (qName.equals("text")) {
			try {
				String str = buffer.toString().trim();
				if (str.length() != 0) {
					pageContentStream.appendRawCommands("1 0 0 -1 0 0 cm\n");
					pageContentStream.drawString(str);
				}
				buffer = null;
				pageContentStream.endText();
				pageContentStream.appendRawCommands("Q\n");
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (qName.equals("tspan")) {
			try {
				String str = buffer.toString().trim();
				pageContentStream.appendRawCommands("1 0 0 -1 0 0 cm\n");
				pageContentStream.drawString(str);
				buffer = new StringBuffer();
				pageContentStream.appendRawCommands("Q\n");
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
		
//		// testing
//		if (qName.equals("svg")) {
//			System.out.println("svg");
//			try {
//				if (pageContentStream == null) {
//					pageContentStream = new PDPageContentStream(ConverterUtils.getTargetPDF(), page, false, false);
//					pageContentStream.beginText();
//					if (fontMappings != null) 
//						pageContentStream.setFont(fontMappings.get("F0"), 16.02f);
//					if (colorMappings != null) {
////						pageContentStream.setNonStrokingColorSpace(colorMappings.get("cs-0"));
////						float[] colorComponents0 = {0.302f, 0.302f, 0.502f};
////						pageContentStream.setNonStrokingColor(colorComponents0);
//						pageContentStream.setNonStrokingColorSpace(colorMappings.get("cs-1"));
//						float[] colorComponents1 = {5};
//						pageContentStream.setNonStrokingColor(colorComponents1);
//						}
//						
//		            pageContentStream.appendRawCommands("200 200 Td\n");
//		            pageContentStream.drawString("Company Overview");
//		            pageContentStream.endText();
//		            
//		            
//		            pageContentStream.close();
//		            System.out.println(pageContentStream.toString());
//				}
//			}
//			catch (Exception e) {
//				
//			}
//			
//		}
	}
	
	
	public void characters(char[] text, int start, int length) {
		if (buffer != null) {
			buffer.append(text, start, length);
		}
	}
	
	
	private void handleText(Attributes attributes) {
		try {
			pageContentStream.appendRawCommands("q\n");
			buffer = new StringBuffer();
			pageContentStream.beginText();
			handlePaintPropertiesAtt(attributes);
			handleTextPropertiesAtt(attributes);
			if (attributes.getValue("transform") != null) 
				handleTransformAtt(attributes.getValue("transform"));
			
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void handleTSpan(Attributes attributes) {
		try {
			if (buffer != null) {
				String str = buffer.toString().trim();
				if (str.length() != 0) {
					pageContentStream.appendRawCommands("1 0 0 -1 0 0 cm\n");
					pageContentStream.drawString(str);
					buffer = new StringBuffer();
				}
					
				pageContentStream.appendRawCommands("q\n");
				handlePaintPropertiesAtt(attributes);
				handleTextPropertiesAtt(attributes);
				if (attributes.getValue("transform") != null) 
					handleTransformAtt(attributes.getValue("transform"));
				
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void handlePaintPropertiesAtt(Attributes attributes) {
		if (attributes.getValue("fill") != null)
			handleFillAtt(attributes.getValue("fill"));
		if (attributes.getValue("stroke") != null) 
			handleStrokeAtt(attributes.getValue("stroke"));
		
		if (attributes.getValue("stroke-linecap") != null) {
			String lineCapValue = attributes.getValue("stroke-linecap");
			try {
				if (lineCapValue.equals("butt"))
					pageContentStream.appendRawCommands("0 J\n");
				else if (lineCapValue.equals("round"))
					pageContentStream.appendRawCommands("1 J\n");
				else if (lineCapValue.equals("square"))
					pageContentStream.appendRawCommands("2 J\n");
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		if (attributes.getValue("stroke-linejoin") != null) {
			String lineJoinValue = attributes.getValue("stroke-linejoin");
			try {
				if (lineJoinValue.equals("miter"))
					pageContentStream.appendRawCommands("0 j\n");
				else if (lineJoinValue.equals("round"))
					pageContentStream.appendRawCommands("1 j\n");
				else if (lineJoinValue.equals("bevel"))
					pageContentStream.appendRawCommands("2 j\n");
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		if (attributes.getValue("stroke-miterlimit") != null) {
			try {
				pageContentStream.appendRawCommands(attributes.getValue("stroke-miterlimit") + " M\n");
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if (attributes.getValue("stroke-width") != null) {
			try {
				pageContentStream.appendRawCommands(attributes.getValue("stroke-width") + " w\n");
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
			
		
			
	}
	
	private void handleTransformAtt(String transformString) {
		String splitAt = "\\)(( *, *)|( +))";
		
		String[] transforms = transformString.split(splitAt);
		transforms[transforms.length - 1] = transforms[transforms.length - 1].substring(0, transforms[transforms.length-1].length() - 1);
		for (int i=0; i<transforms.length; i++) {
//			System.out.println(i + ": " + transforms[i]);
			if (transforms[i].startsWith("matrix")) {
				char oPara = '(';
				String numberStr = transforms[i].substring(transforms[i].indexOf(oPara) + 1);
				try {
					pageContentStream.appendRawCommands(numberStr + " cm\n");
				}
				catch (IOException e) {
					e.printStackTrace();
				}
//				System.out.println("Number string: " + numberStr);
//				String[] numbers = numberStr.split("( *, *)|( +)");
////				System.out.println("length: " + numbers.length);
//				float[] matrix = new float[6];
//				for(int j=0; j<numbers.length; j++)
//					matrix[j] = Float.parseFloat(numbers[j]);
				
			}
		}
		
		
	}
	
	private void handleFillAtt(String fillAtt) {
		String splitAt = "\\)(( *, *)|( +))";
		String[] colors = fillAtt.split(splitAt);
		colors[colors.length - 1] = colors[colors.length - 1].substring(0, colors[colors.length-1].length() - 1);
		
		
		// device-color or icc-color might present
		if (colors.length == 2) {
			char oPara = '(';
			// take content inside ()
			String colorStr = colors[1].substring(colors[1].indexOf(oPara) + 1);
			String[] colorElems = colorStr.split(" *, *");
			
			try {
				// color specified with device-color function
				if (colors[1].startsWith("device-color")) {
					if (colorElems[0].equals("DeviceGray")) {
						pageContentStream.setNonStrokingColor(Double.parseDouble(colorElems[1]));
					}
					else if (colorElems[0].equals("DeviceCMYK")) {
						pageContentStream.setNonStrokingColor(	Double.parseDouble(colorElems[1]),
																Double.parseDouble(colorElems[2]), 
																Double.parseDouble(colorElems[3]),
																Double.parseDouble(colorElems[4]));
					}
					
				}
				
				else if (colors[1].startsWith("icc-color")) {
					pageContentStream.setNonStrokingColorSpace(colorMappings.get(colorElems[0]));
					float[] colorComponents = new float[colorElems.length-1];
					for (int i = 0; i<colorComponents.length; i++) {
						colorComponents[i] = Float.parseFloat(colorElems[i+1]);
					}
					pageContentStream.setNonStrokingColor(colorComponents);
				}
				
			}
			catch (IOException e) {
				e.printStackTrace();
			}	
		}
		
		// case of DeviceRGB
		else if (colors.length == 1) {
			char oPara = '(';
			// take content inside ()
			String colorStr = colors[0].substring(colors[0].indexOf(oPara) + 1);
			String[] colorElems = colorStr.split(" *, *");
			
			try {
				// color specified with rgb(...)
				if (colors[0].startsWith("rgb") && colorElems.length == 3) {
					pageContentStream.setNonStrokingColor(	Integer.parseInt(colorElems[0]), 
															Integer.parseInt(colorElems[1]),
															Integer.parseInt(colorElems[2]));
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void handleStrokeAtt(String strokeAtt) {
		String splitAt = "\\)(( *, *)|( +))";
		String[] colors = strokeAtt.split(splitAt);
		colors[colors.length - 1] = colors[colors.length - 1].substring(0, colors[colors.length-1].length() - 1);
		
		
		// device-color or icc-color might present
		if (colors.length == 2) {
			char oPara = '(';
			// take content inside ()
			String colorStr = colors[1].substring(colors[1].indexOf(oPara) + 1);
			String[] colorElems = colorStr.split(" *, *");
			
			try {
				// color specified with device-color function
				if (colors[1].startsWith("device-color")) {
					if (colorElems[0].equals("DeviceGray")) {
						pageContentStream.setStrokingColor(Double.parseDouble(colorElems[1]));
					}
					else if (colorElems[0].equals("DeviceCMYK")) {
						pageContentStream.setStrokingColor(	Double.parseDouble(colorElems[1]),
															Double.parseDouble(colorElems[2]), 
															Double.parseDouble(colorElems[3]),
															Double.parseDouble(colorElems[4]));
					}
					
				}
				
				else if (colors[1].startsWith("icc-color")) {
					pageContentStream.setStrokingColorSpace(colorMappings.get(colorElems[0]));
					float[] colorComponents = new float[colorElems.length-1];
					for (int i = 0; i<colorComponents.length; i++) {
						colorComponents[i] = Float.parseFloat(colorElems[i+1]);
					}
					pageContentStream.setStrokingColor(colorComponents);
				}
				
			}
			catch (IOException e) {
				e.printStackTrace();
			}	
		}
		
		// case of DeviceRGB
		else if (colors.length == 1) {
			char oPara = '(';
			// take content inside ()
			String colorStr = colors[0].substring(colors[0].indexOf(oPara) + 1);
			String[] colorElems = colorStr.split(" *, *");
			
			try {
				// color specified with rgb(...)
				if (colors[0].startsWith("rgb") && colorElems.length == 3) {
					pageContentStream.setStrokingColor(	Integer.parseInt(colorElems[0]), 
														Integer.parseInt(colorElems[1]),
														Integer.parseInt(colorElems[2]));
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void handleTextPropertiesAtt(Attributes attributes) {
		try {
			if (attributes.getValue("font-family") != null &&
				attributes.getValue("font-size") != null) {
					pageContentStream.setFont(	fontMappings.get(attributes.getValue("font-family")), 
												Float.parseFloat(attributes.getValue("font-size")));
			}
			if (attributes.getValue("x") != null) {
				String cmd = "1 0 0 1 " + attributes.getValue("x") + " ";
				if (attributes.getValue("y") != null) 
					cmd = cmd + attributes.getValue("y") + " ";
				else
					cmd = cmd + "0 ";
				pageContentStream.appendRawCommands(cmd + "cm \n");
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
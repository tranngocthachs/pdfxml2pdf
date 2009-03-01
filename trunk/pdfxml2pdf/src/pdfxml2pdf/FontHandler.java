package pdfxml2pdf;
import org.xml.sax.helpers.*;
import org.xml.sax.*;
import org.pdfbox.pdmodel.*;
import org.pdfbox.pdmodel.font.*;
import org.pdfbox.pdmodel.common.*;
import org.pdfbox.pdmodel.graphics.color.*;
import java.io.*;
import java.util.*;


public class FontHandler extends DefaultHandler {
	private PDFont font;
	private File fontFile;
	private StringBuffer buffer = null;
	public final static HashMap<String, Integer> flagMappings = new HashMap<String, Integer>();
	static {
		flagMappings.put("FixedPitch", new Integer(1)); 	// FixedPitch bit position: 1
		flagMappings.put("Serif", new Integer(2)); 			// Serif bit position: 2
		flagMappings.put("Symbolic", new Integer(3)); 		// Symbolic bit position: 3
		flagMappings.put("Script", new Integer(4)); 		// Script bit position: 4
		flagMappings.put("Nonsymbolic", new Integer(6)); 	// Nonsymbolic bit position: 6
		flagMappings.put("Italic", new Integer(7)); 		// Italic bit position: 7
		flagMappings.put("AllCap", new Integer(17)); 		// AllCap bit position: 17
		flagMappings.put("SmallCap", new Integer(18));		// SmallCap bit position: 18
		flagMappings.put("ForceBold", new Integer(19));  	// ForceBold bit position: 19
	}
	
	
	
	public FontHandler(File fontFile) {
		this.fontFile = fontFile;
	}
	
	public PDFont getFont() {
		return font;
	}
	
	public void startDocument() {
		
	}
	
	public void endDocument() {
		
	}
	
	public void startElement(String uri, String localname, String qName, Attributes attributes) {
		// process Font
		if (qName.equals("TrueType") || qName.equals("Type1")) {
			if (qName.equals("TrueType"))
				font = new PDTrueTypeFont();
			else
				font = new PDType1Font();

			// set BaseFont
			font.setBaseFont(attributes.getValue("BaseFont"));
			
			// set non-required fields
			// set FirstChar
			if (attributes.getValue("FirstChar") != null)
				font.setFirstChar(Integer.parseInt(attributes.getValue("FirstChar")));
			
			// set LastChar
			if (attributes.getValue("LastChar") != null)
				font.setLastChar(Integer.parseInt(attributes.getValue("LastChar")));
			
		}
		
		// process the FontDescriptor
		if (qName.equals("FontDescriptor")) {
			PDFontDescriptorDictionary fontDesc = new PDFontDescriptorDictionary();
			
			// set the FontName
			fontDesc.setFontName(attributes.getValue("FontName"));
			
			// set the Flags
			String flag = attributes.getValue("Flags");
			String[] flags = flag.split(" ");
			// a bit array to hold the flags
			char[] flagsArr = new char[32];
			for (int i=0; i<flagsArr.length; i++)
				flagsArr[i] = '0';
			
			for (int i=0; i<flags.length; i++) {
				Integer bitPosition = flagMappings.get(flags[i]);
				if (bitPosition != null) {
					flagsArr[flagsArr.length - bitPosition.intValue()] = '1';
					// our bit array should be from right to left
				}
			}
			int fontFlags = Integer.parseInt(new String(flagsArr), 2);
			fontDesc.setFlags(fontFlags);
			
			
			// set FontBBox
			String[] fontBBox = (attributes.getValue("FontBBox")).split(" ");
			PDRectangle fontBBRect = new PDRectangle();
			
			fontBBRect.setLowerLeftX(Float.parseFloat(fontBBox[0]));
			fontBBRect.setLowerLeftY(Float.parseFloat(fontBBox[1]));
			fontBBRect.setUpperRightX(Float.parseFloat(fontBBox[2]));
			fontBBRect.setUpperRightY(Float.parseFloat(fontBBox[3]));
			fontDesc.setFontBoundingBox(fontBBRect);
			((PDSimpleFont)font).setFontDescriptor(fontDesc);
			
			// set ItalicAngle
			fontDesc.setItalicAngle(Float.parseFloat(attributes.getValue("ItalicAngle")));
			
			// set Ascent
			fontDesc.setAscent(Float.parseFloat(attributes.getValue("Ascent")));
			
			// set Descent
			fontDesc.setDescent(Float.parseFloat(attributes.getValue("Descent")));
			
			// set CapHeight
			fontDesc.setCapHeight(Float.parseFloat(attributes.getValue("CapHeight")));
			
			// set StemV
			fontDesc.setStemV(Float.parseFloat(attributes.getValue("StemV")));
			
			// set non-required fields
			// set FontFamily
			if (attributes.getValue("FontFamily") != null)
				fontDesc.setFontFamily(attributes.getValue("FontFamily"));
			
			// set FontStretch
			if (attributes.getValue("FontStretch") != null)
				fontDesc.setFontStretch(attributes.getValue("FontStretch"));
			// set FontWeight
			if (attributes.getValue("FontWeight") != null)
				fontDesc.setFontWeight(Float.parseFloat(attributes.getValue("FontWeight")));
			// set Leading
			if (attributes.getValue("Leading") != null)
				fontDesc.setLeading(Float.parseFloat(attributes.getValue("Leading")));
			
			// set XHeight
			if (attributes.getValue("XHeight") != null)
				fontDesc.setXHeight(Float.parseFloat(attributes.getValue("XHeight")));
			
			// set StemH
			if (attributes.getValue("StemH") != null)
				fontDesc.setStemH(Float.parseFloat(attributes.getValue("StemH")));
			
			// set AvgWidth
			if (attributes.getValue("AvgWidth") != null)
				fontDesc.setAverageWidth(Float.parseFloat(attributes.getValue("AvgWidth")));
			
			// set MaxWidth
			if (attributes.getValue("MaxWidth") != null)
				fontDesc.setMaxWidth(Float.parseFloat(attributes.getValue("MaxWidth")));
			
			// set MissingWidth
			if (attributes.getValue("MissingWidth") != null)
				fontDesc.setMissingWidth(Float.parseFloat(attributes.getValue("MissingWidth")));
			
			// set CharSet
			if (attributes.getValue("CharSet") != null)
				fontDesc.setCharacterSet(attributes.getValue("CharSet"));
		}
		
		if (qName.equals("Widths")) {
			buffer = new StringBuffer();
		}
	}
	public void endElement(String uri, String localname, String qName) {
		if (qName.equals("Widths")) {
			String[] widths = buffer.toString().split(" ");
			ArrayList<Integer> widthArr = new ArrayList<Integer>(widths.length);
			for (int i=0; i<widths.length; i++) {
				widthArr.add(Integer.parseInt(widths[i]));
			}
			font.setWidths(widthArr);
			buffer = null;
		}
	}
	
	public void characters(char[] text, int start, int length) {
		if (buffer != null) {
			buffer.append(text, start, length);
		}
	}
}

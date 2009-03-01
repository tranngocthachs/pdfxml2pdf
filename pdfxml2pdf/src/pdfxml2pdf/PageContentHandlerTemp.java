package pdfxml2pdf;
import java.io.*;

import org.pdfbox.cos.COSArray;
import org.pdfbox.cos.COSInteger;
import org.pdfbox.cos.COSName;
import org.pdfbox.cos.COSString;
import org.pdfbox.pdmodel.PDPage;
import org.pdfbox.pdmodel.PDResources;
import org.pdfbox.pdmodel.common.PDStream;
import org.pdfbox.pdmodel.edit.PDPageContentStream;
import org.pdfbox.pdmodel.font.PDFont;
import org.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.pdfbox.pdmodel.graphics.color.PDICCBased;
import org.pdfbox.pdmodel.graphics.color.PDIndexed;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.AttributesImpl;

import svg.simplestructure.*;

import java.util.HashMap;
import java.util.Stack;


public class PageContentHandlerTemp extends DefaultHandler {
	private PDPage page;
	private File pageContentFile;
	private PDPageContentStream pageContentStream;
	
	private PDFont font;
	private HashMap<String, PDFont> fontMappings;
	private HashMap<String, PDColorSpace> colorMappings;
	private String fontFamily;
	
	private StringBuffer buffer = null;
	private Stack<SVGComponent> stack = new Stack<SVGComponent>();
	private GeneralSVGTag rootSVG = null;
	
	public PageContentHandlerTemp(File pageContentFile, PDPage page) {
		this.pageContentFile = pageContentFile;
		this.page = page;	
	}
	
	public void startDocument() {
		try {
			pageContentStream = new PDPageContentStream(ConverterUtils.getTargetPDF(), page, false, false);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void endDocument() {
		try {
			rootSVG.serialise();
			pageContentStream.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void startElement(String uri, String localname, String qName, Attributes attributes) {
		if (qName.equals("svg")) {
			Attributes att = new AttributesImpl(attributes);
			SVGComponent svg = new SVGTag(pageContentStream, page, att);
			stack.push(svg);
		}
		
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
		
		if (qName.equals("g")) {
			SVGComponent g = new GTag(pageContentStream, page, new AttributesImpl(attributes));
			CompositeSVGTag comp = (CompositeSVGTag)stack.peek();
			comp.add(g);
			stack.push(g);
		}
		
		if (qName.equals("text")) {
			SVGComponent text = new TextTag(pageContentStream, page, new AttributesImpl(attributes));
			CompositeSVGTag comp = (CompositeSVGTag)stack.peek();
			comp.add(text);
			stack.push(text);
			buffer = new StringBuffer();
		}
		
		if (qName.equals("tspan")) {
			CompositeSVGTag comp = (CompositeSVGTag)stack.peek();
			if (buffer != null) {
				String str = buffer.toString().trim();
				if (str.length() != 0) {
					comp.add(new Text(pageContentStream, page, str, ((TextTag)comp).getXs(), ((TextTag)comp).getYs()));
					buffer = new StringBuffer();
				}
				SVGComponent tspan = new TspanTag(pageContentStream, page, new AttributesImpl(attributes)); 
				comp.add(tspan);
				stack.push(tspan);
			} 
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
			if (fontMappings != null || colorMappings != null) {
				PDResources resources = new PDResources();
				if (fontMappings != null)
					resources.setFonts(fontMappings);
				if (colorMappings != null)
					resources.setColorSpaces(colorMappings);
				page.setResources(resources);
			}
			
		}
		
		if (qName.equals("svg")) {
			
			rootSVG = (GeneralSVGTag)stack.pop();
		}
		
		if (qName.equals("g")) {
			stack.pop();
		}
		
		if (qName.equals("text")) {
			CompositeSVGTag comp = (CompositeSVGTag)stack.pop();
			String str = buffer.toString().trim();
			if (str.length() != 0)
				comp.add(new Text(pageContentStream, page, str, ((TextTag)comp).getXs(), ((TextTag)comp).getYs()));
			buffer = null;
		}
		
		if (qName.equals("tspan")) {
			CompositeSVGTag comp = (CompositeSVGTag)stack.pop();
			String str = buffer.toString().trim();
			if (str.length() != 0)
				comp.add(new Text(pageContentStream, page, str, ((TspanTag)comp).getXs(), ((TspanTag)comp).getYs()));
			buffer = new StringBuffer();
		}
		
	}
	
	public void characters(char[] text, int start, int length) {
		if (buffer != null) {
			buffer.append(text, start, length);
		}
	}
}

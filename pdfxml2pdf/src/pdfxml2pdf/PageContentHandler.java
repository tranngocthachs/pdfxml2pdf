package pdfxml2pdf;
import java.io.*;

import org.pdfbox.cos.COSArray;
import org.pdfbox.cos.COSInteger;
import org.pdfbox.cos.COSName;
import org.pdfbox.cos.COSStream;
import org.pdfbox.io.*;
import org.pdfbox.pdmodel.PDPage;
import org.pdfbox.pdmodel.PDResources;
import org.pdfbox.pdmodel.common.PDStream;
import org.pdfbox.pdmodel.edit.PDPageContentStream;
import org.pdfbox.pdmodel.font.PDFont;
import org.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.pdfbox.pdmodel.graphics.color.PDDeviceCMYK;
import org.pdfbox.pdmodel.graphics.color.PDDeviceGray;
import org.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.pdfbox.pdmodel.graphics.color.PDICCBased;
import org.pdfbox.pdmodel.graphics.color.PDIndexed;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.AttributesImpl;


import svg.simplestructure.*;

import java.util.HashMap;
import java.util.Stack;


public class PageContentHandler extends DefaultHandler {
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
	
	public PageContentHandler(File pageContentFile, PDPage page) {
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
			fontFamily = attributes.getValue("font-family");
		}
		
		// process pdf:font-information tag
		// this tag defines a non-embedded font
		if (qName.equals("pdf:font-information")) {
			File fontFile = ConverterUtils.getFile(pageContentFile, attributes.getValue("xlink:href"));
			font = (new FontProcessorNonEmbedded(fontFile)).process();
		}
		// process font-face-uri tag
		// this tag defines an embedded font
		if (qName.equals("font-face-uri")) {
			File fontFile = ConverterUtils.getFile(pageContentFile, attributes.getValue("xlink:href"));
			font = (new FontProcessorEmbedded(fontFile)).process();
		}
		
		
		// process color-profile tag
		if (qName.equals("color-profile")) {
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
            COSStream stream = null;
            try {
            	stream = new COSStream(new org.pdfbox.io.RandomAccessFile(File.createTempFile("pdfbox", ".icc"), "rw"));
            }
            catch (Exception e) {}
            PDStream iccStream = new PDStream(stream);
            try {
            	in = new FileInputStream(iccFile);
            	out = iccStream.createOutputStream();
            	int c;
            	while ((c = in.read()) != -1)
            		out.write(c);
            	
            	in.close();
            	out.close();
            	
            }
            catch (IOException e) {
            	e.printStackTrace();
            }
            
            // the data from .icc file is second in the array
            iccArr.add(iccStream);
            
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
            
            // the base of this indexed color space is second
            // first check whether the based color was defined previously
            if (colorMappings == null)
            	colorMappings = new HashMap<String, PDColorSpace>();
            PDColorSpace base = colorMappings.get(attributes.getValue("Base"));
            if (base == null) {
            	// base color should be device colors
            	String deviceColorName = attributes.getValue("Base");
            	COSName deviceColorCOSName = null;
            	if (deviceColorName.equals("DeviceGray"))
            		deviceColorCOSName = COSName.getPDFName(PDDeviceGray.NAME);
            	else if (deviceColorName.equals("DeviceRGB"))
            		deviceColorCOSName = COSName.getPDFName(PDDeviceRGB.NAME);
            	else if (deviceColorName.equals("DeviceCMYK"))
            		deviceColorCOSName = COSName.getPDFName(PDDeviceCMYK.NAME);
            	else {
            		System.err.println("Unknown based color");
            		System.exit(1);
            	}
            	indexedArr.add(deviceColorCOSName);
            }
            else {
            	// base color should be previously defined
            	indexedArr.add(base);
            }
            
            
            // hival parameter comes next
            indexedArr.add(new COSInteger(Integer.parseInt(attributes.getValue("HiVal"))));
            
            byte[] buffer = null;
            File lut = ConverterUtils.getFile(pageContentFile, attributes.getValue("LookupTableSrc"));
            
            buffer = Base64.decodeFromFile(lut.getAbsolutePath());
            COSStream stream = null;
            try {
            	stream = new COSStream(new org.pdfbox.io.RandomAccessFile(File.createTempFile("pdfbox", ".lut"), "rw"));
            }
            catch (Exception e) {}
            PDStream lutStream = new PDStream(stream);
            OutputStream out = null;
            try {
            	out = lutStream.createOutputStream();
            	out.write(buffer);
            	out.close();
            }
            catch (IOException e) {
            	e.printStackTrace();
            }
            
            // the lookup table is last
            indexedArr.add(lutStream);
            
            // make a color space from this array 
            PDColorSpace colorSpace = new PDIndexed(indexedArr);
            
            // put this color space into our collection
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
				String str = buffer.toString();
				if (str.length() != 0) {
					comp.add(new Text(	pageContentStream,
										page, 
										str, 
										((TextTag)comp).getXs(), 
										((TextTag)comp).getYs(),
										(TextTag)comp));
					buffer = new StringBuffer();
				}
				SVGComponent tspan = new TspanTag(	pageContentStream,
													page,
													new AttributesImpl(attributes),
													(TextTag)comp
													); 
				comp.add(tspan);
				stack.push(tspan);
			} 
		}
		
		if (qName.equals("image")) {
			SVGComponent image = new ImageTag(pageContentStream, page, new AttributesImpl(attributes), pageContentFile);
			CompositeSVGTag comp = (CompositeSVGTag)stack.peek();
			comp.add(image);
		}
		
		if (qName.equals("path")) {
			SVGComponent path = new PathTag(pageContentStream, page, new AttributesImpl(attributes));
			CompositeSVGTag comp = (CompositeSVGTag)stack.peek();
			comp.add(path);
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
			String str = buffer.toString();
			if (str.length() != 0)
				comp.add(new Text(	pageContentStream, 
									page, 
									str, 
									((TextTag)comp).getXs(), 
									((TextTag)comp).getYs(),
									(TextTag)comp));
			buffer = null;
		}
		
		if (qName.equals("tspan")) {
			CompositeSVGTag comp = (CompositeSVGTag)stack.pop();
			String str = buffer.toString();
			if (str.length() != 0)
				comp.add(new Text(	pageContentStream,
									page,
									str, 
									((TextTag)comp).getXs(), 
									((TextTag)comp).getYs(),
									(TextTag)comp));
			buffer = new StringBuffer();
		}
		
	}
	
	public void characters(char[] text, int start, int length) {
		if (buffer != null) {
			buffer.append(text, start, length);
		}
	}
}

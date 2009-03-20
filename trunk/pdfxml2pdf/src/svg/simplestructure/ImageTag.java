package svg.simplestructure;
import pdfxml2pdf.ConverterUtils;
import java.io.IOException;
import java.io.File;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.util.Map;
import org.pdfbox.cos.COSArray;
import org.pdfbox.cos.COSDictionary;
import org.pdfbox.cos.COSInteger;
import org.pdfbox.cos.COSName;
import org.pdfbox.cos.COSStream;
import org.pdfbox.pdmodel.PDPage;
import org.pdfbox.pdmodel.PDResources;
import org.pdfbox.pdmodel.common.PDStream;
import org.pdfbox.pdmodel.edit.PDPageContentStream;
import org.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.pdfbox.pdmodel.graphics.xobject.PDPixelMap;
import org.pdfbox.pdmodel.graphics.xobject.PDJpeg;
import org.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;
import org.pdfbox.util.MapUtil;
import org.xml.sax.Attributes;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import javax.imageio.ImageIO;

public class ImageTag extends GeneralSVGTag {
	private File pageFile = null;
	public ImageTag(PDPageContentStream pageContentStream, PDPage page, Attributes attributes, File pageFile) {
		super(pageContentStream, page, attributes);
		this.pageFile = pageFile;
	}

	public void serialise() throws IOException {
		float x = 0;
		float y = 0;
		float width = 0;
		float height = 0;
		File imgFile = null;
		PDXObjectImage pdImg = null;
		if (attributes.getValue("x") != null)
			x = Float.parseFloat(attributes.getValue("x"));
		if (attributes.getValue("y") != null)
			y = Float.parseFloat(attributes.getValue("y"));
		if (attributes.getValue("width") != null)
			width = Float.parseFloat(attributes.getValue("width"));
		if (attributes.getValue("height") != null)
			height = Float.parseFloat(attributes.getValue("height"));
		imgFile = ConverterUtils.getFile(pageFile, attributes.getValue("xlink:href"));
		
		if (imgFile.getName().endsWith("png")) {
			BufferedImage img = ImageIO.read(imgFile);
			if (attributes.getValue("color-profile") != null) {
				PDColorSpace col = (PDColorSpace)((page.findResources().getColorSpaces()).get(attributes.getValue("color-profile")));
				byte[] imageByteArr =((DataBufferByte)(img.getData().getDataBuffer())).getData();
				COSStream imgStream = null;
	            try {
	            	imgStream = new COSStream(new org.pdfbox.io.RandomAccessFile(File.createTempFile("pdfbox", ".png"), "rw"));
	            }
	            catch (Exception e) {}
	            PDStream imageStream = new PDStream(imgStream);
	            OutputStream outStre = imageStream.createOutputStream();
	            outStre.write(imageByteArr);
	            outStre.close();
	            COSStream stre = imageStream.getStream();
	            stre.setItem(COSName.FILTER, COSName.FLATE_DECODE);
	            stre.setItem( COSName.SUBTYPE, COSName.IMAGE);
	            stre.setItem( COSName.TYPE, COSName.getPDFName( "XObject" ) );
	            
	            pdImg = new PDPixelMap(imageStream);
	            pdImg.setColorSpace(col);
	            int[] compSize = img.getColorModel().getComponentSize();
	            pdImg.setBitsPerComponent(compSize[0]);
	            pdImg.setHeight(img.getHeight());
	            pdImg.setWidth(img.getWidth());   
			}	
		}
		else if (imgFile.getName().endsWith("jpg") || imgFile.getName().endsWith("jpeg")) {
			pdImg = new PDJpeg(ConverterUtils.getTargetPDF(), new FileInputStream(imgFile));
		}
		
		if (attributes.getValue("pdf:Decode") != null) {
			COSArray decodeArr = new COSArray();
			String[] valueStrings = attributes.getValue("pdf:Decode").split(" ");
			
			for (int i=0; i<valueStrings.length; i++)
				decodeArr.add(new COSInteger(Integer.parseInt(valueStrings[i])));
            COSDictionary imgDict = (COSDictionary)pdImg.getCOSObject();
            imgDict.setItem("Decode", decodeArr);
		}
		
		PDResources resources = null;
		resources = page.findResources();
		if (resources == null) {
			resources = new PDResources();
			page.setResources(resources);
		}
		Map xObjects = resources.getXObjects();
		String imageKey = MapUtil.getNextUniqueKey(xObjects, "Im");
		xObjects.put(imageKey, pdImg);
		
		// actually serialise
//		String imgCmd = "";
//        imgCmd+="q\n";
//        
//        imgCmd+="1 0 0 1 72 708.06 cm\n"; //translate
//        imgCmd+="0.4805 0 0 -0.4764 0 0 cm\n"; //transform
//        imgCmd+="619 0 0 83 0 0 cm\n"; // scale
//        imgCmd+="1 0 0 -1 0 1 cm\n"; // flip the image
//        imgCmd+="/Im0 Do\n";
//        imgCmd+="Q\n";
        pageContentStream.appendRawCommands("q\n");
        pageContentStream.appendRawCommands("1 0 0 1 " + x + " "+ y + " cm\n");
        if (attributes.getValue("transform") != null)
        	handleTransformAtt(attributes.getValue("transform"));
        pageContentStream.appendRawCommands(width + " 0 0 " + height + " 0 0 cm\n" );
        pageContentStream.appendRawCommands("1 0 0 -1 0 1 cm\n");
        pageContentStream.appendRawCommands("/" + imageKey + " Do\n");
        pageContentStream.appendRawCommands("Q\n");
	}

}

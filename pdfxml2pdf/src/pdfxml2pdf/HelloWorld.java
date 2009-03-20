package pdfxml2pdf;
import java.io.*;
import java.text.NumberFormat;
import java.util.Locale;
import java.awt.image.*;
import javax.imageio.*;
import org.pdfbox.exceptions.COSVisitorException;
import org.pdfbox.pdmodel.*;
import org.pdfbox.pdmodel.graphics.*;
import org.pdfbox.pdmodel.graphics.color.*;
import org.pdfbox.pdmodel.common.*;
import org.pdfbox.pdmodel.edit.PDPageContentStream;
import org.pdfbox.pdmodel.text.*;
import org.pdfbox.pdmodel.font.*;
import org.pdfbox.io.*;
import org.pdfbox.cos.*;
import org.pdfbox.pdmodel.graphics.xobject.*;
/**
 * This is an example that creates a simple document.
 *
 * The example is taken from the pdf file format specification.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.6 $
 */
public class HelloWorld
{
	private float pageHeight;
	private NumberFormat formatDecimal = NumberFormat.getNumberInstance( Locale.US );
    /**
     * Constructor.
     */
    public HelloWorld()
    {
        super();
    }

    /**
     * create the second sample document from the PDF file format specification.
     *
     * @param file The file to write the PDF to.
     * @param message The message to write in the file.
     *
     * @throws IOException If there is an error writing the data.
     * @throws COSVisitorException If there is an error writing the PDF.
     */
    public void doIt( String file) throws IOException, COSVisitorException
    {
        // the document
        PDDocument doc = null;
        try
        {
            doc = new PDDocument();
            doc.getDocumentCatalog().setPageLayout(PDDocumentCatalog.PAGE_LAYOUT_ONE_COLUMN);
            PDPage page = new PDPage();
            doc.addPage( page );
            
            
            // fonts
            PDSimpleFont font1 = new PDTrueTypeFont();
            font1.setBaseFont("Arial-BoldMT");
            font1.setFirstChar(32);
            font1.setLastChar(121);
            PDFontDescriptorDictionary fontDesc1 = new PDFontDescriptorDictionary();
            /*
             * <FontDescriptor StemV="138" FontName="Arial-BoldMT" FontStretch="Normal" 
             * FontWeight="700" Flags="Nonsymbolic" Descent="-211" FontBBox="-628 -376 2000 1010"
             * Ascent="905" FontFamily="Arial" CapHeight="718" XHeight="515" ItalicAngle="0"/>
             * */
            fontDesc1.setStemV(138);
            fontDesc1.setFontName("Arial-BoldMT");
            fontDesc1.setFontStretch("Normal");
            fontDesc1.setFontWeight(700);
            fontDesc1.setFlags(32);
            fontDesc1.setDescent(-211);
            PDRectangle rect1 = new PDRectangle();
            rect1.setLowerLeftX(-628);
            rect1.setLowerLeftY(-376);
            rect1.setUpperRightX(2000);
            rect1.setUpperRightY(1010);
            fontDesc1.setFontBoundingBox(rect1);
            fontDesc1.setAscent(905);
            fontDesc1.setFontFamily("Arial");
            fontDesc1.setCapHeight(718);
            fontDesc1.setXHeight(515);
            fontDesc1.setItalicAngle(0);
            font1.setFontDescriptor(fontDesc1);
            int[] widthArray1 = {278,0,0,0,0,0,0,0,0,0,0,0,0,333,0,0,0,0,0,0,0,556,0,0,0,0,0,0,0,0,0,0,0,722,0,722,722,667,611,778,0,0,0,722,0,833,722,778,667,0,722,667,611,722,0,0,667,0,0,0,0,0,0,0,0,556,0,556,611,556,333,611,611,278,278,556,278,889,611,611,611,0,389,556,333,611,556,778,0,556};
            java.util.ArrayList<Integer> widthArrayList1 = new java.util.ArrayList<Integer>(widthArray1.length);
            for (int i = 0; i<widthArray1.length; i++)
            {
            	widthArrayList1.add(new Integer(widthArray1[i]));
            }
            font1.setWidths(widthArrayList1);
            
            PDSimpleFont font2 = new PDTrueTypeFont();
            font2.setBaseFont("TimesNewRomanPSMT");
            font2.setFirstChar(32);
            font2.setLastChar(248);
            PDFontDescriptorDictionary fontDesc2 = new PDFontDescriptorDictionary();
            /*
             * <FontDescriptor StemV="82" FontName="TimesNewRomanPSMT" FontStretch="Normal" 
             * FontWeight="400" Flags="Serif Nonsymbolic" Descent="-216" FontBBox="-568 -307 
             * 2000 1007" Ascent="891" FontFamily="Times New Roman" CapHeight="656" XHeight="-546"
             *  ItalicAngle="0"/>
             * */
            fontDesc2.setStemV(82);
            fontDesc2.setFontName("TimesNewRomanPSMT");
            fontDesc2.setFontStretch("Normal");
            fontDesc2.setFontWeight(400);
            fontDesc2.setFlags(34);
            fontDesc2.setDescent(-216);
            PDRectangle rect2 = new PDRectangle();
            rect2.setLowerLeftX(-568);
            rect2.setLowerLeftY(-307);
            rect2.setUpperRightX(2000);
            rect2.setUpperRightY(1007);
            fontDesc2.setFontBoundingBox(rect2);
            fontDesc2.setAscent(891);
            fontDesc2.setFontFamily("Times New Roman");
            fontDesc2.setCapHeight(656);
            fontDesc2.setXHeight(-546);
            fontDesc2.setItalicAngle(0);
            font2.setFontDescriptor(fontDesc2);
            int[] widthArray2 = {250,0,408,0,0,833,778,0,333,333,0,564,250,333,250,278,500,500,500,500,500,500,500,500,500,500,278,278,0,0,0,0,0,722,667,667,722,611,556,722,722,333,389,722,611,889,722,722,556,0,667,556,611,722,722,944,722,722,611,333,0,333,0,0,0,444,500,444,500,444,333,500,500,278,278,500,278,778,500,500,500,500,333,389,278,500,500,722,500,500,444,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,549,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,500};
            java.util.ArrayList<Integer> widthArrayList2 = new java.util.ArrayList<Integer>(widthArray2.length);
            for (int i = 0; i<widthArray2.length; i++)
            {
            	widthArrayList2.add(new Integer(widthArray2[i]));
            }
            font2.setWidths(widthArrayList2);
            
            
            
            PDSimpleFont font3 = new PDTrueTypeFont();
            font3.setBaseFont("TimesNewRomanPS-BoldItalicMT");
            font3.setFirstChar(32);
            font3.setLastChar(119);
            PDFontDescriptorDictionary fontDesc3 = new PDFontDescriptorDictionary();
            /*
             * <FontDescriptor StemV="116.867" FontName="TimesNewRomanPS-BoldItalicMT" 
             * FontStretch="Normal" FontWeight="700" Flags="Serif Nonsymbolic Italic" 
             * Descent="-216" FontBBox="-547 -307 1206 1032" Ascent="891" FontFamily="Times New Roman" 
             * CapHeight="656" XHeight="-531" ItalicAngle="-15"/>
             * */
            
            fontDesc3.setStemV(116.867f);
            fontDesc3.setFontName("TimesNewRomanPS-BoldItalicMT");
            fontDesc3.setFontStretch("Normal");
            fontDesc3.setFontWeight(700);
            fontDesc3.setFlags(98);
            fontDesc3.setDescent(-216);
            PDRectangle rect3 = new PDRectangle();
            rect3.setLowerLeftX(-547);
            rect3.setLowerLeftY(-307);
            rect3.setUpperRightX(1206);
            rect3.setUpperRightY(1032);
            fontDesc3.setFontBoundingBox(rect3);
            fontDesc3.setAscent(891);
            fontDesc3.setFontFamily("Times New Roman");
            fontDesc3.setCapHeight(656);
            fontDesc3.setXHeight(-531);
            fontDesc3.setItalicAngle(-15);
            font2.setFontDescriptor(fontDesc2);
            int[] widthArray3 = {250,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,722,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,889,0,0,0,0,0,0,0,0,0,0,500,0,500,444,0,0,0,278,0,0,278,0,556,500,0,0,389,389,278,556,0,667};
            java.util.ArrayList<Integer> widthArrayList3 = new java.util.ArrayList<Integer>(widthArray3.length);
            for (int i = 0; i<widthArray3.length; i++)
            {
            	widthArrayList3.add(new Integer(widthArray3[i]));
            }
            font3.setWidths(widthArrayList3);
            
            // color spaces
            OutputStream out = null;
            InputStream in = null;
            COSArray iccArr = new COSArray();
            iccArr.add(COSName.getPDFName("ICCBased"));
            PDStream pdstream = new PDStream(doc);
            try {
            	in = new FileInputStream("cs-0.icc");
            	out = pdstream.createOutputStream();
            	int c;
            	while ((c = in.read()) != -1)
            		out.write(c);
            }
            finally {
            	if (in != null) {
            		in.close();
            	}
            	if (out != null) {
            		out.close();
            	}
            }
            iccArr.add(pdstream);
            PDICCBased col1 = new PDICCBased(iccArr);
            col1.setNumberOfComponents(3);
            
            
            
            COSArray indexedArr = new COSArray();
            indexedArr.add(COSName.getPDFName("Indexed"));
            indexedArr.add(col1);
            indexedArr.add(new COSInteger(54));
            
//            File f = new File("lut-0.lut");
//            in = null;
//            byte[] b;
//            try {
//            	in = new FileInputStream(f);
//            	b = new byte[(int)(f.length())];
//            	in.read(b);
//            }
//            finally {
//            	if (in != null) {
//            		in.close();
//            	}
//            }
            byte[] b = Base64.decodeFromFile("lut-0.lut");
//            for (int i=0; i<b.length; i++) {
//            	System.out.print(Integer.toBinaryString(b[i]));
//            }
//            System.out.println();
            PDStream pdstream1 = new PDStream(doc);
            OutputStream out1 = pdstream1.createOutputStream();
            out1.write(b);
            out1.close();
            
            indexedArr.add(pdstream1);
            PDIndexed col2 = new PDIndexed(indexedArr); 
            
            indexedArr = new COSArray();
            indexedArr.add(COSName.getPDFName("Indexed"));
            indexedArr.add(col1);
            indexedArr.add(new COSInteger(55));
            File f = new File("lut-1.lut");
            in = null;
            try {
            	in = new FileInputStream(f);
            	b = new byte[(int)(f.length())];
            	in.read(b);
            }
            finally {
            	if (in != null) {
            		in.close();
            	}
            }
            COSString str = new COSString(b);
            indexedArr.add(str);
            PDIndexed col3 = new PDIndexed(indexedArr); 
            
            // Images
            PDXObjectImage img1 = new PDJpeg(doc, new FileInputStream("im_764-1.jpg"));
            PDXObjectImage img2 = new PDJpeg(doc, new FileInputStream("im_764-2.jpg"));
            
            BufferedImage imgBuff = ImageIO.read(new File("im_764-1.png"));
            byte[] imageByteArr =((DataBufferByte)(imgBuff.getData().getDataBuffer())).getData();
            System.out.println(imageByteArr.length);
            PDStream imageStream = new PDStream(doc);
            OutputStream outStre = imageStream.createOutputStream();
            outStre.write(imageByteArr);
            outStre.close();
            COSStream stre = imageStream.getStream();
            stre.setItem(COSName.FILTER, COSName.FLATE_DECODE);
            stre.setItem( COSName.SUBTYPE, COSName.IMAGE);
            stre.setItem( COSName.TYPE, COSName.getPDFName( "XObject" ) );
            
            
            
            PDPixelMap img3 = new PDPixelMap(imageStream);
            img3.setColorSpace(col2);
            int[] compSize = imgBuff.getColorModel().getComponentSize();
            img3.setBitsPerComponent(compSize[0]);
            img3.setHeight(imgBuff.getHeight());
            img3.setWidth(imgBuff.getWidth());
            
            
            System.out.println(img3.getHeight());
            System.out.println(img3.getWidth());
            //img1.setColorSpace(col2);
            //img2.setColorSpace(col3);
            COSArray decodeArr1 = new COSArray();
            decodeArr1.add(new COSInteger(0));
            decodeArr1.add(new COSInteger(255));
            COSDictionary imgDict1 = (COSDictionary)img3.getCOSObject();
            imgDict1.setItem("Decode", decodeArr1);
//            COSDictionary imgDict2 = (COSDictionary)img2.getCOSObject();
//            imgDict2.setItem("Decode", decodeArr1);

            
            PDResources temp = new PDResources();
            
            temp.getXObjects().put("Im0", img3);
            temp.getXObjects().put("Im1", img2);
            page.setResources(temp);
//
//            pageHeight = page.findMediaBox().getHeight();
//            
            // Start page content
            PDPageContentStream contentStream = new PDPageContentStream(doc, page, false, false);
            contentStream.appendRawCommands("1 0 0 -1 0 792 cm\n");
            contentStream.appendRawCommands("1 0 0 -1 0 792 cm\n");
//
//            
////            // generated from svg's pages
////            float[] matrix = {1, 0, 0, -1, 0, 792};
////            float[] convertedMatrix = convertingMatrix(matrix);
////            contentStream.appendRawCommands(getStringOfMatrix(convertedMatrix) + " cm\n");
//            
//            
//            // insert image
//            contentStream.appendRawCommands("q\n");
//            float[] convertedPoint = convertingPoint(72, 83.94f);
////            contentStream.drawImage(img1, convertedPoint[0], convertedPoint[1]);
////             translate to the point
//            contentStream.appendRawCommands("1 0 0 1 " +
//            								formatDecimal.format(convertedPoint[0]) + 
//            								" " +
//            								formatDecimal.format(convertedPoint[1]) +
//            								" cm\n");
//            float[] matrix1 = {0.4805f, 0, 0, 0.4764f, 0, 0};
//            matrix1[5] = (matrix1[5]*(-1)) + ((img1.getHeight())*(1-matrix1[3])); 
//            //convertedMatrix = convertingMatrix(matrix1);
//            contentStream.appendRawCommands(getStringOfMatrix(matrix1) + " cm\n");
////            contentStream.appendRawCommands("0.481 0 0 -0.476 0 39.5412 cm\n");
//            contentStream.appendRawCommands("619 0 0 83 0 0 cm\n");
//            contentStream.appendRawCommands("/Im0 Do\n");
//            contentStream.appendRawCommands("Q\n");
            
            
            
//            //          insert image
//            contentStream.appendRawCommands("q\n");
//            convertedPoint = convertingPoint(72, 668.52f);
//            
//            // translate to the point
//            contentStream.appendRawCommands("1 0 0 1 " +
//            								formatDecimal.format(convertedPoint[0]) + 
//            								" " +
//            								formatDecimal.format(convertedPoint[1]) +
//            								" cm\n");
//            
////            contentStream.appendRawCommands(getStringOfMatrix(convertedMatrix) + " cm\n");
//            contentStream.appendRawCommands("/Im1 Do");
//            contentStream.appendRawCommands("Q\n");
//            
//            contentStream.drawImage(img1, 72, 708.06f-(float)(img1.getHeight() * 0.4764), (float)(img1.getWidth() * 0.4805) , (float)(img1.getHeight() * 0.4764));
//            contentStream.drawImage(img2, 72, 668.52f-(float)(img2.getHeight() * 0.4764), (float)(img2.getWidth() * 0.4805) , (float)(img2.getHeight() * 0.4764));
            String imgCmd = "";
            imgCmd+="q\n";
            imgCmd+="1 0 0 1 72 708.06 cm\n"; //translate
            imgCmd+="0.4805 0 0 -0.4764 0 0 cm\n"; //transform
            imgCmd+="619 0 0 83 0 0 cm\n"; // scale
            imgCmd+="1 0 0 -1 0 1 cm\n"; // flip the image
            imgCmd+="/Im0 Do\n";
            imgCmd+="Q\n";
            contentStream.appendRawCommands(imgCmd);
            contentStream.beginText();
            contentStream.setFont(font2, 12);
            contentStream.setNonStrokingColorSpace(new PDDeviceGray());
            float[] colorComponents0 = {0};
            contentStream.setNonStrokingColor(colorComponents0);
            contentStream.appendRawCommands("1 0 0 -1 0 554.1 cm\n");
            contentStream.appendRawCommands("q\n");
            contentStream.appendRawCommands("1 0 0 1 72 0 cm\n");
            contentStream.appendRawCommands("1 0 0 -1 0 0 cm\n");
            contentStream.drawString("Founded in 1999, Global Electronics ");
            contentStream.appendRawCommands("q\n");
            contentStream.setNonStrokingColorSpace(PDDeviceRGB.INSTANCE);
            float[] colorComponents1 = {255, 255, 0};
            contentStream.setNonStrokingColor(colorComponents1);
            contentStream.drawString("is a leading manufacturer");
            contentStream.appendRawCommands("Q\n");
            contentStream.drawString("of consumer and busines");
            contentStream.appendRawCommands("Q\n");
            contentStream.appendRawCommands("q\n");
            contentStream.appendRawCommands("1 0 0 1 72 13.8 cm\n");
            contentStream.appendRawCommands("1 0 0 -1 0 0 cm\n");
            contentStream.drawString("electronic products, including cellular phones, digital projectors, and PDAs to start. The");
            contentStream.appendRawCommands("Q\n");
            contentStream.endText();
            /*
            contentStream.appendRawCommands("1 0 0 1 241.98 281.28 cm\n");
            contentStream.setNonStrokingColor(154, 154, 255);
            String test = "0.0 0.0 m\n19.659 0.015 39.622 -1.415 58.98 -4.92 c\n70.658 -7.299 85.772 -10.6310005 95.46 -17.94 c\n0.0 -25.98 l\n0.0 0.0 l\nh\n";
            test+="f\n";
            contentStream.appendRawCommands(test);
            */
            /*
            contentStream.setNonStrokingColor(255, 255, 255);
            String test = "101.82 186.3 m\n";
            test+="352.8 186.3 l\n";
            test+="352.8 286.02002 l\n";
            test+="101.82 286.02002 l\n";
            test+="h\n";
            test+="f\n";
            contentStream.appendRawCommands(test);
            
            */
            contentStream.setNonStrokingColor(77, 77, 128);
            contentStream.appendRawCommands("1 0 0 1 241.98 255.3 cm\n");
            String test = "0 0 m\n" 	
            	+ 	"95.4 8.04 l\n"
            	+ 	"95.4 -19.68 l\n"
            	+	"0.0 -27.72 l\n"
            	+	"0.0 0.0 l\n"
            	+ 	"h\n"
            	+	"f\n";
            contentStream.appendRawCommands(test);
    
            
            /*
            // M101.82,186.3H352.8v99.72H101.82z
            contentStream.setNonStrokingColorSpace(new PDDeviceGray());
            float[] colorComponents1 = {1};
            contentStream.setNonStrokingColor(colorComponents1);
            // M101.82,186.3
            contentStream.appendRawCommands("101.82 186.3 m\n"); 
            // cpx = 101.82 cpy = 186.3
            // H352.8
            contentStream.appendRawCommands("352.8 186.3 l\n");
            // cpx = 352.8 cpy = 186.3
            // v99.72
            // cpy = 99.72+186.3 = 286.02
            contentStream.appendRawCommands("352.8 286.02 l\n");
            // cpx = 352.8 cpy = 286.02
            // H101.82
            contentStream.appendRawCommands("101.82 286.02 l\n");
            contentStream.appendRawCommands("h\nf\n");
            
            
            // path fill="rgb(77.01,77.01,128.01) icc-color(cs-0,0.302,0.302,0.502)"
            // fill-rule="evenodd" d="M0,0l95.4,8.04v-27.72L0-27.72V0z"
            // transform="matrix(1 0 0 1 241.98 255.3)"
            contentStream.setNonStrokingColorSpace(col1);
            float[] colorComponents2 = {0.302f, 0.302f, 0.502f};
            contentStream.setNonStrokingColor(colorComponents2);
            String cmd ="q\n";
            cmd+="1 0 0 1 241.98 255.3 cm\n";
            cmd+="0 0 m\n";
            cmd+="95.4 8.04 l\n";
            cmd+="95.4 -19.68 l\n";
            cmd+="0 -27.72 l\n";
            cmd+="0 0 l\n";
            cmd+="h\n";
            cmd+="f*\n";
            cmd+="Q\n";
            contentStream.appendRawCommands(cmd);
            */
            
            
            
//            contentStream.appendRawCommands("Q\n");
            contentStream.close();
            doc.save( file );
        }
        finally
        {
            if( doc != null )
            {
                doc.close();
            }
        }
    }

    // implementing B.3.4 of Mars spec.
    private float[] convertingPoint(float x, float y) {
    	float[] convertedPoint = new float[2];
    	convertedPoint[0] = x;
    	convertedPoint[1] = pageHeight - y;
    	return convertedPoint;
    }
    
    private float[] convertingMatrix(float[] matrix) {
    	if (matrix.length != 6)
    		return matrix;
    	else {
    		float[] convertedMatrix = new float[6];
    		convertedMatrix[0] = matrix[0];
    		convertedMatrix[1] = matrix[1] * (-1);
    		convertedMatrix[2] = matrix[2] * (-1);
    		convertedMatrix[3] = matrix[3];
    		convertedMatrix[4] = matrix[4] - (matrix[2]*pageHeight);
    		convertedMatrix[5] = matrix[5]*(-1) + pageHeight*(1-matrix[3]);
    		System.out.println("Before: " + getStringOfMatrix(matrix));
    		System.out.println("After: " + getStringOfMatrix(convertedMatrix));
    		return convertedMatrix;
    	}
    }
    
    private String getStringOfMatrix(float[] matrix) {
    	String returnString = "";
    	
    	if (matrix.length == 6) {
    		for (int i = 0; i<matrix.length; i++) {
    			returnString = returnString + formatDecimal.format(matrix[i]);
    			if (i<(matrix.length-1))
    				returnString = returnString + " ";
    		}
    		 
    	}
    	return returnString;
    }
    
    /**
     * This will create a hello world PDF document.
     * <br />
     * see usage() for commandline
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args)
    {
        HelloWorld app = new HelloWorld();
        try
        {
                app.doIt( args[0]);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
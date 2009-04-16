package test;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.fontbox.afm.*;
import org.fontbox.pfb.PfbParser;

import org.jpedal.fonts.Type1C;
import org.jpedal.fonts.glyph.PdfJavaGlyphs;
import org.jpedal.fonts.glyph.T1Glyphs;
import org.jpedal.fonts.tt.FontFile2;
import org.jpedal.fonts.tt.CFF;
import org.pdfbox.cos.COSName;
import org.pdfbox.encoding.AFMEncoding;
import org.pdfbox.encoding.Encoding;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.pdmodel.PDPage;
import org.pdfbox.pdmodel.common.PDRectangle;
import org.pdfbox.pdmodel.common.PDStream;
import org.pdfbox.pdmodel.edit.PDPageContentStream;
import org.pdfbox.pdmodel.font.*;

public class FontTest1 {
	public static void main(String[] args) {
		PDFontDescriptorDictionary fd = null;
		PDType1Font font = new PDType1Font();
		PDDocument doc = null;
		
		FontMetric metric = null;
		try {
			doc = new PDDocument();
			fd = new PDFontDescriptorDictionary();
			
	        font.setFontDescriptor(fd);

	        // read the pfb
//	        InputStream in = new BufferedInputStream(new FileInputStream("font-1."));
//	        PfbParser pfbparser = new PfbParser(in);
//	        in.close();
//
//	        PDStream fontStream = new PDStream(doc, pfbparser.getInputStream(),
//	                false);
//	        fontStream.getStream().setInt("Length", pfbparser.size());
//	        for (int i = 0; i < pfbparser.getLengths().length; i++) 
//	        {
//	            fontStream.getStream().setInt("Length" + (i + 1),
//	                    pfbparser.getLengths()[i]);
//	        }
//	        fontStream.addCompression();
//	        fd.setFontFile(fontStream);
	        
	        File otfFile = new File("font-1.otf");
	        InputStream in = new BufferedInputStream(new FileInputStream(otfFile));
	        PDStream fontStream = new PDStream(doc, in, false);
	        fontStream.getStream().setInt("Length", (int)otfFile.length());
	        fontStream.getStream().setItem(COSName.SUBTYPE, COSName.getPDFName("Type1C"));
	        fontStream.addCompression();
	        fd.setFontFile3(fontStream);
	        
	        
	        
	        
	        // read the afm
	        in = new BufferedInputStream(new FileInputStream("font-1.afm"));
	        AFMParser parser = new AFMParser(in);
	        parser.parse();
	        in.close();
	        metric = parser.getResult();
	        font.setEncoding(new AFMEncoding(metric));
	        
	        
	        // set the values
	        font.setBaseFont(metric.getFontName());
	        fd.setFontName(metric.getFontName());
	        fd.setFontFamily(metric.getFamilyName());
	        fd.setNonSymbolic(true);
	        fd.setFontBoundingBox(new PDRectangle(metric.getFontBBox()));
	        fd.setItalicAngle(metric.getItalicAngle());
	        fd.setAscent(metric.getAscender());
	        fd.setDescent(metric.getDescender());
	        fd.setCapHeight(metric.getCapHeight());
	        fd.setXHeight(metric.getXHeight());
	        fd.setAverageWidth(metric.getAverageCharacterWidth());
	        fd.setCharacterSet(metric.getCharacterSet());

	        // get firstchar, lastchar
	        int firstchar = 255;
	        int lastchar = 0;

	        // widths
	        List listmetric = metric.getCharMetrics();

	        Integer zero = new Integer(0);
	        Iterator iter = listmetric.iterator();
	        while (iter.hasNext()) 
	        {
	            CharMetric m = (CharMetric) iter.next();
	            int n = m.getCharacterCode();
	            if (n>0 && m.getWx() > 0) {
	            	firstchar = Math.min(firstchar, n);
	                lastchar = Math.max(lastchar, n);
	            }
	            
	        }
	        font.setFirstChar(firstchar);
	        font.setLastChar(lastchar);
	        int widthMetricsLength = lastchar - firstchar + 1;
	        float[] widthArr = new float[widthMetricsLength];
            for (int i=0; i<widthArr.length; i++) {
            	widthArr[i]=0;
            }
            iter = listmetric.iterator();
            while (iter.hasNext()) {
            	CharMetric m = (CharMetric) iter.next();
	            int n = m.getCharacterCode();
	            if (n>0 && m.getWx() > 0 ) {
	            	widthArr[n-firstchar] = m.getWx();
	            }
	            
            }
            ArrayList<Float> widthArrayList = new ArrayList<Float>(widthArr.length);
            for (int i = 0; i<widthArr.length; i++)
            {
            	widthArrayList.add(new Float(widthArr[i]));
            }
            font.setWidths( widthArrayList );
            
            
            
            
            PDPage page = new PDPage();
            doc.addPage(page);
            

            PDPageContentStream contentStream = new PDPageContentStream(doc,
                    page);
            contentStream.beginText();
            contentStream.setFont(font, 12);
            contentStream.moveTextPositionByAmount(100, 700);
            contentStream.drawString("a c degh i");
            contentStream.endText();
            contentStream.close();
            doc.save("fonttest1.pdf");
 
        
            doc.close();
            
//	        
//	            if (n > 0) 
//	            {
//	                
//	                if (m.getWx() > 0) 
//	                {
//	                    float width = m.getWx();
//	                    widths.add(new Float(width));
//	                } 
//	                else 
//	                {
//	                    widths.add(zero);
//	                }
//	            }
//	        }
	       

//			InputStream in = new BufferedInputStream(new FileInputStream("font-1.otf"));
//			byte[] fontData = new byte[in.available()];
//			in.read(fontData);
//			in.close();
//			FontFile2 fontFile = new FontFile2(fontData);
//			int startPointer=fontFile.selectTable(FontFile2.CFF);
//			Type1C cffData = null;
//			PdfJavaGlyphs glyphs=new T1Glyphs(false);
//	        //read 'cff' table
//			if(startPointer!=0){
//
//	            int length=fontFile.getTableSize(FontFile2.CFF);
//
//	            byte[] data=fontFile.readBytes(startPointer, length) ;
//
//	            try {
//	                cffData=new Type1C(data,glyphs);
//	            } catch (Exception e) {
//	                e.printStackTrace(); 
//	            }
//			}
//			
//			System.out.println(cffData.getWidth(32));
//			
//			System.out.println("OTF sucks");
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}

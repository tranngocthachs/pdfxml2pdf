package test;

import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.ArrayList;
import java.util.List;

import org.fontbox.ttf.*;
import org.pdfbox.cos.COSName;
import org.pdfbox.encoding.WinAnsiEncoding;
import org.pdfbox.pdmodel.common.PDRectangle;
import org.pdfbox.pdmodel.common.PDStream;
import org.pdfbox.pdmodel.edit.PDPageContentStream;
import org.pdfbox.pdmodel.font.PDFont;
import org.pdfbox.pdmodel.font.PDFontDescriptorDictionary;
import org.pdfbox.pdmodel.font.PDTrueTypeFont;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.pdmodel.PDPage;
public class FontTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			/*
			Font testfont = Font.createFont(Font.TRUETYPE_FONT, new File("f0-2.ttf"));
			Attribute[] att = testfont.getAvailableAttributes();
			for (int i=0; i<att.length; i++)
				System.out.println(att.toString());
			TTFParser parser = new TTFParser();
			TrueTypeFont ttfont = parser.parseTTF("f0-2.ttf");
			*/
			PDDocument doc = new PDDocument();
			File file = new File("f0-2.ttf");
			
			//////////////////////////////////////////////
			PDTrueTypeFont retval = new PDTrueTypeFont();
	        PDFontDescriptorDictionary fd = new PDFontDescriptorDictionary();
	        PDStream fontStream = new PDStream(doc, new FileInputStream( file ), false );
	        fontStream.getStream().setInt( COSName.LENGTH1, (int)file.length() );
	        fontStream.addCompression();
	        fd.setFontFile2( fontStream );
	        retval.setFontDescriptor( fd );
	        
	        /*
	        fd.setAscent(975);
	        fd.setCapHeight(867);
	        fd.setDescent(-217);
	        fd.setFlags(32);
	        PDRectangle rec = new PDRectangle();
	        rec.setLowerLeftX(-379);
	        rec.setLowerLeftY(-217);
	        rec.setUpperRightX(1095);
	        rec.setUpperRightY(975);
	        fd.setFontBoundingBox(rec);
	        fd.setFontName("RDSZJF+HelveticaNeue-Bold");
	        retval.setBaseFont("RDSZJF+HelveticaNeue-Bold");
	        fd.setItalicAngle(0);
	        fd.setStemV(0);
//	        fd.setLeading(29);
//	        fd.setMaxWidth(1147);
//	        fd.setXHeight(266);
	        List widths = new ArrayList(88);
	        
	        widths.add(0, new Integer(278));
	        for (int i=1; i<36; i++)
	        	widths.add(i, new Integer(0));
	        widths.add(36, new Integer(741));
	        for (int i=37; i<51; i++)
	        	widths.add(i, new Integer(0));
	        widths.add(51, new Integer(649));
	        for (int i=52; i<65; i++)
	        	widths.add(i, new Integer(0));
	        widths.add(65, new Integer(574));
	        widths.add(66, new Integer(0));
	        widths.add(67, new Integer(574));
	        widths.add(68, new Integer(611));
	        widths.add(69, new Integer(574));
	        for (int i=70; i<75; i++)
	        	widths.add(i, new Integer(0));
	        widths.add(75, new Integer(574));
	        widths.add(76, new Integer(258));
	        widths.add(77, new Integer(906));
	        widths.add(78, new Integer(593));
	        widths.add(79, new Integer(611));
	        for (int i=80; i<83; i++)
	        	widths.add(i, new Integer(0));
	        widths.add(83, new Integer(537));
	        widths.add(84, new Integer(352));
	        widths.add(85, new Integer(593));
	        widths.add(86, new Integer(0));
	        widths.add(87, new Integer(814));
	        retval.setWidths(widths);
	        retval.setFirstChar(32);
	        retval.setLastChar(119);
	        */
	        //only support winansi encoding right now, should really
	        //just use Identity-H with unicode mapping
	        retval.setEncoding( new WinAnsiEncoding() );
	        
	        TrueTypeFont ttf = null;
	        try
	        {
	            TTFParser parser = new TTFParser();
	            ttf = parser.parseTTF( file );
	            NamingTable naming = ttf.getNaming();
	            List records = naming.getNameRecords();
	            for( int i=0; i<records.size(); i++ )
	            {
	                NameRecord nr = (NameRecord)records.get( i );
	                if( nr.getNameId() == NameRecord.NAME_POSTSCRIPT_NAME )
	                {
	                    retval.setBaseFont( nr.getString() );
	                    fd.setFontName( nr.getString() );
	                }
	                else if( nr.getNameId() == NameRecord.NAME_FONT_FAMILY_NAME )
	                {
	                    fd.setFontFamily( nr.getString() );
	                }
	            }
	            fd.setNonSymbolic( true );
	            OS2WindowsMetricsTable os2 = ttf.getOS2Windows();
	            if (os2 != null) {
	            
		            switch( os2.getFamilyClass() )
		            {
		                case OS2WindowsMetricsTable.FAMILY_CLASS_SYMBOLIC:
		                    fd.setSymbolic( true );
		                    fd.setNonSymbolic( false );
		                    break;
		                case OS2WindowsMetricsTable.FAMILY_CLASS_SCRIPTS:
		                    fd.setScript( true );
		                    break;
		                case OS2WindowsMetricsTable.FAMILY_CLASS_CLAREDON_SERIFS:
		                case OS2WindowsMetricsTable.FAMILY_CLASS_FREEFORM_SERIFS:
		                case OS2WindowsMetricsTable.FAMILY_CLASS_MODERN_SERIFS:
		                case OS2WindowsMetricsTable.FAMILY_CLASS_OLDSTYLE_SERIFS:
		                case OS2WindowsMetricsTable.FAMILY_CLASS_SLAB_SERIFS:
		                    fd.setSerif( true );
		                    break;
		                default:
		                    //do nothing
		            }
		            switch( os2.getWidthClass() )
		            {
		                case OS2WindowsMetricsTable.WIDTH_CLASS_ULTRA_CONDENSED:
		                    fd.setFontStretch( "UltraCondensed" );
		                    break;
		                case OS2WindowsMetricsTable.WIDTH_CLASS_EXTRA_CONDENSED:
		                    fd.setFontStretch( "ExtraCondensed" );
		                    break;
		                case OS2WindowsMetricsTable.WIDTH_CLASS_CONDENSED:
		                    fd.setFontStretch( "Condensed" );
		                    break;
		                case OS2WindowsMetricsTable.WIDTH_CLASS_SEMI_CONDENSED:
		                    fd.setFontStretch( "SemiCondensed" );
		                    break;
		                case OS2WindowsMetricsTable.WIDTH_CLASS_MEDIUM:
		                    fd.setFontStretch( "Normal" );
		                    break;
		                case OS2WindowsMetricsTable.WIDTH_CLASS_SEMI_EXPANDED:
		                    fd.setFontStretch( "SemiExpanded" );
		                    break;
		                case OS2WindowsMetricsTable.WIDTH_CLASS_EXPANDED:
		                    fd.setFontStretch( "Expanded" );
		                    break;
		                case OS2WindowsMetricsTable.WIDTH_CLASS_EXTRA_EXPANDED:
		                    fd.setFontStretch( "ExtraExpanded" );
		                    break;
		                case OS2WindowsMetricsTable.WIDTH_CLASS_ULTRA_EXPANDED:
		                    fd.setFontStretch( "UltraExpanded" );
		                    break;
		                default:
		                    //do nothing
		            }
		            fd.setFontWeight( os2.getWeightClass() );
	            }
	            //todo retval.setFixedPitch
	            //todo retval.setNonSymbolic
	            //todo retval.setItalic
	            //todo retval.setAllCap
	            //todo retval.setSmallCap
	            //todo retval.setForceBold
	            
	            HeaderTable header = ttf.getHeader();
	            PDRectangle rect = new PDRectangle();
	            rect.setLowerLeftX( header.getXMin() * 1000f/header.getUnitsPerEm() );
	            rect.setLowerLeftY( header.getYMin() * 1000f/header.getUnitsPerEm() );
	            rect.setUpperRightX( header.getXMax() * 1000f/header.getUnitsPerEm() );
	            rect.setUpperRightY( header.getYMax() * 1000f/header.getUnitsPerEm() );
	            fd.setFontBoundingBox( rect );
	            
	            HorizontalHeaderTable hHeader = ttf.getHorizontalHeader();
	            fd.setAscent( hHeader.getAscender() * 1000f/header.getUnitsPerEm() );
	            fd.setDescent( hHeader.getDescender() * 1000f/header.getUnitsPerEm() );
	            if (hHeader.getAdvanceWidthMax() > 0)
	            	fd.setMaxWidth(hHeader.getAdvanceWidthMax());
	            int numberOfHMetrics = hHeader.getNumberOfHMetrics();
	            int numberOfGlyphs = ttf.getMaximumProfile().getNumGlyphs();
	            
	            
	            GlyphTable glyphTable = ttf.getGlyph();
	            GlyphData[] glyphs = glyphTable.getGlyphs();
	            
	            PostScriptTable ps = ttf.getPostScript();
	            fd.setFixedPitch( ps.getIsFixedPitch() > 0 );
	            fd.setItalicAngle( ps.getItalicAngle() );
	            fd.setItalic(ps.getItalicAngle() > 0);
	            String[] names = ps.getGlyphNames();
	            if( names != null )
	            {
	                for( int i=0; i<names.length; i++ )
	                {
	                    //if we have a capital H then use that, otherwise use the
	                    //tallest letter
	                    if( names[i].equals( "H" ) )
	                    {
	                        fd.setCapHeight( (glyphs[i].getBoundingBox().getUpperRightY()* 1000f)/
	                                         header.getUnitsPerEm() );
	                    }
	                    if( names[i].equals( "x" ) )
	                    {
	                        fd.setXHeight( (glyphs[i].getBoundingBox().getUpperRightY()* 1000f)/header.getUnitsPerEm() );
	                    }
	                }
	            }
	            
	            //hmm there does not seem to be a clear definition for StemV, 
	            //this is close enough and I am told it doesn't usually get used.
	            fd.setStemV( (fd.getFontBoundingBox().getWidth() * .13f) );
	            

	            CMAPTable cmapTable = ttf.getCMAP();
	            CMAPEncodingEntry[] cmaps = cmapTable.getCmaps();
	            int[] glyphToCCode = null;
	            if (cmaps.length == 1)
	            	glyphToCCode = cmaps[0].getGlyphIdToCharacterCode();
	            else {
	            	for( int i=0; i<cmaps.length; i++ )
	            	{
		                if( cmaps[i].getPlatformId() == CMAPTable.PLATFORM_WINDOWS &&
		                    cmaps[i].getPlatformEncodingId() == CMAPTable.ENCODING_UNICODE )
		                {
		                    glyphToCCode = cmaps[i].getGlyphIdToCharacterCode();
		                }
		            }
	            }
	            
	            int firstChar = 100000;
	            int lastChar = -1;
	            for( int i=0; i<glyphToCCode.length; i++ )
	            {
	                if (glyphToCCode[i]<firstChar)
	                	firstChar = glyphToCCode[i];
	                if (glyphToCCode[i] > lastChar)
	                	lastChar = glyphToCCode[i];
	            }
	            
	            HorizontalMetricsTable hMet = ttf.getHorizontalMetrics();
	            int[] widthValues = hMet.getAdvanceWidth();
	            int widthMetricsLength = lastChar - firstChar + 1;
	            int[] widths = new int[widthMetricsLength];
	            for (int i=0; i<widths.length; i++) {
	            	widths[i]=0;
	            }
	            for (int i=0; i<glyphToCCode.length; i++) {
	            	int index = glyphToCCode[i]-firstChar;
	            	widths[index] = widthValues[i];
	            }
	            ArrayList<Integer> widthArrayList = new ArrayList<Integer>(widths.length);
	            for (int i = 0; i<widths.length; i++)
	            {
	            	widthArrayList.add(new Integer(widths[i]));
	            }
	            retval.setWidths( widthArrayList );

	            retval.setFirstChar( firstChar );
	            retval.setLastChar( lastChar);

	        }
	        finally
	        {
	            if( ttf != null )
	            {
	                ttf.close();
	            }
	        }
	        
	        
			/////////////////////////////////////////////
	        PDPage page = new PDPage();
            doc.addPage(page);
            

            PDPageContentStream contentStream = new PDPageContentStream(doc,
                    page);
            contentStream.beginText();
            contentStream.setFont(retval, 12);
            contentStream.moveTextPositionByAmount(100, 700);
            contentStream.drawString("Document Stacks");
            contentStream.endText();
            contentStream.close();
            doc.save("fonttest.pdf");
 
        
            doc.close();

        

			
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}

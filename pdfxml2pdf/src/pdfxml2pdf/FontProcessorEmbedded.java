package pdfxml2pdf;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import org.fontbox.afm.AFMParser;
import org.fontbox.afm.CharMetric;
import org.fontbox.afm.FontMetric;
import org.fontbox.ttf.*;
import org.pdfbox.cos.COSName;
import org.pdfbox.cos.COSStream;
import org.pdfbox.encoding.AFMEncoding;
import org.pdfbox.encoding.WinAnsiEncoding;
import org.pdfbox.pdmodel.common.PDRectangle;
import org.pdfbox.pdmodel.common.PDStream;
import org.pdfbox.pdmodel.font.PDFont;
import org.pdfbox.pdmodel.font.PDFontDescriptorDictionary;
import org.pdfbox.pdmodel.font.PDTrueTypeFont;
import org.pdfbox.pdmodel.font.PDType1Font;
import org.pdfbox.pdmodel.font.PDFontDescriptor;
import org.pdfbox.io.RandomAccessFile;

public class FontProcessorEmbedded {
	private File fontFile;
	public FontProcessorEmbedded(File fontFile) {
		this.fontFile = fontFile;
	}
	public PDFont process() {
		File deobfuscatedFont = deobfuscate();
		PDFont retval = null;
		try {
			retval = makePDFont(deobfuscatedFont); 
		}
		catch (IOException e) {
			System.err.println("Error in processing embedded font: " + fontFile.getName() + ".\nThe font provided might not be OpenType");
			e.printStackTrace();
			System.exit(1);
		}
		return retval;
	}
	private File deobfuscate() {
		File retval = null;
		try {
			retval = File.createTempFile("pdfxml2pdf", ".otf");
			byte[] docIDBytes = Base64.decode(ConverterUtils.getDocID());
			byte[] key = null;
			if (docIDBytes.length > 64) {
				key = new byte[64];
				for (int i = 0; i<64; i++)
					key[i] = docIDBytes[i];
			}
			else
				key = docIDBytes;
			InputStream in = new BufferedInputStream(new FileInputStream(fontFile));
			OutputStream out = new BufferedOutputStream(new FileOutputStream(retval));
			
			int c;
			int i = 0;
			int j = 0;
			while ((c=in.read()) != -1) {
				if (i<1024) {
					j = i%key.length;
					out.write((byte)((byte)c^key[j]));
				}
				else {
					out.write(c);
				}
				i++;
			}
			in.close();
			out.close();
		} catch (Exception e) {
			System.err.println("Error in deobfuscating the embedded font");
			e.printStackTrace();
			System.exit(1);
		}
		return retval;
	} 
	private PDFont makePDFont(File deobfuscatedFontFile) throws IOException {
		PDFont retval = null;
		
		RAFDataStream raf = new RAFDataStream( deobfuscatedFontFile, "r" );
		raf.read32Fixed(); // ignore font version
		int numberOfTables = raf.readUnsignedShort();
        raf.readUnsignedShort(); // ignore search range 
        raf.readUnsignedShort(); // ignore entry selector
        raf.readUnsignedShort(); // ignore range shift 
        ArrayList<String> tableTags = new ArrayList<String>();
        for( int i=0; i<numberOfTables; i++ )
        {
            String tag = raf.readString(4);
            tableTags.add(tag);
            raf.readUnsignedInt(); // ignore checksum
            raf.readUnsignedInt(); // ignore offset
            raf.readUnsignedInt(); // ignore length
        }
		
		if (tableTags.contains(GlyphTable.TAG))
			retval = makeTTF(deobfuscatedFontFile);
		else if (tableTags.contains("CFF "))
			retval = makeCFF(deobfuscatedFontFile);
        
        else {
        	System.err.println("Invalid font file!");
        	System.exit(1);
        }
        
	
		return retval;
	}
	private PDFont makeTTF(File deobfuscatedFontFile) throws IOException {
		PDTrueTypeFont retval = new PDTrueTypeFont();
		PDFontDescriptorDictionary fd = new PDFontDescriptorDictionary();
		COSStream fontCOSStream = null;
		InputStream in = null;
		OutputStream out = null;
		try {
			fontCOSStream = new COSStream(new RandomAccessFile(File.createTempFile("pdfxml2pdf", "fontstream"), "rw"));
			in = new BufferedInputStream(new FileInputStream(deobfuscatedFontFile));
			out = fontCOSStream.createUnfilteredStream();
			byte[] buffer = new byte[ 1024 ];
            int amountRead = -1;
            while( (amountRead = in.read(buffer)) != -1 )
            {
                out.write( buffer, 0, amountRead );
            }   
		}
		finally {
			if (in != null)
				in.close();
			if (out != null)
				out.close();
		}
		
		fontCOSStream.setInt( COSName.LENGTH1, (int)deobfuscatedFontFile.length() );
        PDStream fontStream = new PDStream(fontCOSStream);
        fontStream.addCompression();
        fd.setFontFile2( fontStream );
        retval.setFontDescriptor( fd );
        
        
        //only support winansi encoding right now, should really
        //just use Identity-H with unicode mapping
        retval.setEncoding( new WinAnsiEncoding() );
        
        TrueTypeFont ttf = null;
        try
        {
            TTFParser parser = new TTFParser();
            ttf = parser.parseTTF( deobfuscatedFontFile );
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
            
            int[] glyphToCharacterCode = null;
            
            if (glyphToCCode.length == numberOfGlyphs)
            	glyphToCharacterCode = glyphToCCode;
            else {
            	glyphToCharacterCode = new int[numberOfGlyphs];
            	int j = 0;
            	boolean characterCodeZeroSet = false;
            	for (int i=0; i<glyphToCCode.length; i++) {
            		if ((glyphToCCode[i] == 0) && !characterCodeZeroSet) {
            			glyphToCharacterCode[j] = 0;
            			characterCodeZeroSet = true;
            		}
            			
            		if (glyphToCCode[i] > 0) {
            			glyphToCharacterCode[j] = glyphToCCode[i];
            			j++;
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
            System.out.println("silly");
            for (int i=0; i<glyphToCharacterCode.length; i++) {
            	if (glyphToCharacterCode[i] > 0) {
            		int index = glyphToCharacterCode[i]-firstChar;
                	widths[index] = widthValues[i];
            	}
            	
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
        catch (NullPointerException e) {
        	System.err.println("Error while process OpenType (TrueType flavour) font file: " + fontFile.getName());
        	System.err.println("Probably some required tables are missing");
        	e.printStackTrace();
        	System.exit(1);
        }
        finally
        {
            if( ttf != null )
            {
                ttf.close();
            }
        }
        
		return retval;
	}
	private PDFont makeCFF(File deobfuscatedFontFile) throws IOException {
		// this will use the two lcdf-typetools namely cfftot1 and t1rawafm to create afm file for the font
		// these two programs have to be installed in the host system in order for this to work
		// this afm file will then be used to construct the appropriate font and font descriptor dictionary
		// this is of course just a temporary solution, an optimal way would be parsing the CFF data directly
		// to construct the required dictionaries.
		// Neither FontBox or other open source java libraries are capable of doing this
		// and writing one on my own would be too time-consuming. So, let just use this temporary solution for now
		PDType1Font retval = new PDType1Font();
		File pfbFile = null;
		File afmFile = null;
		try {
			pfbFile = File.createTempFile("pdfxml2pdf", "pfb");
			afmFile = File.createTempFile("pdfxml2pdf", "afm");
			String cmd = "./runcfftot1.sh";
			cmd+=" ";
			cmd+=(deobfuscatedFontFile.getAbsolutePath());
			cmd+=" ";
			cmd+=(pfbFile.getAbsolutePath());
			cmd+=" ";
			cmd+=(afmFile.getAbsolutePath());
			// create a process for the shell
			ProcessBuilder pb = new ProcessBuilder("bash", "-c", cmd);
			pb.redirectErrorStream(true); // use this to capture messages sent to stderr
			Process shell = pb.start();
			InputStream shellIn = shell.getInputStream(); // this captures the output from the command
			int shellExitStatus = shell.waitFor(); // wait for the shell to finish and get the return code
			// at this point you can process the output issued by the command
			// for instance, this reads the output and writes it to System.out:
			int c;
			while ((c = shellIn.read()) != -1) {System.out.write(c);}
			// close the stream
			shellIn.close();
			
			// construct the font's object
			
			
			PDFontDescriptorDictionary fd = new PDFontDescriptorDictionary();
			COSStream fontCOSStream = null;
			InputStream in = null;
			OutputStream out = null;
			try {
				fontCOSStream = new COSStream(new RandomAccessFile(File.createTempFile("pdfxml2pdf", "fontstream"), "rw"));
				in = new BufferedInputStream(new FileInputStream(deobfuscatedFontFile));
				out = fontCOSStream.createUnfilteredStream();
				byte[] buffer = new byte[ 1024 ];
	            int amountRead = -1;
	            while( (amountRead = in.read(buffer)) != -1 )
	            {
	                out.write( buffer, 0, amountRead );
	            }   
			}
			finally {
				if (in != null)
					in.close();
				if (out != null)
					out.close();
			}
			
			fontCOSStream.setInt(COSName.LENGTH, (int)deobfuscatedFontFile.length());
			fontCOSStream.setItem(COSName.SUBTYPE, COSName.getPDFName("Type1C"));
			
	        PDStream fontStream = new PDStream(fontCOSStream);
	        fontStream.addCompression();
	        fd.setFontFile3(fontStream);
	        
	        retval.setFontDescriptor(fd);
	        
	        // read the afm
	        in = new BufferedInputStream(new FileInputStream(afmFile));
	        AFMParser parser = new AFMParser(in);
	        parser.parse();
	        in.close();
	        FontMetric metric = parser.getResult();
	        retval.setEncoding(new AFMEncoding(metric));
	        
	        
	        // set the values
	        retval.setBaseFont(metric.getFontName());
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
	        retval.setFirstChar(firstchar);
	        retval.setLastChar(lastchar);
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
            retval.setWidths( widthArrayList );
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}		
		return retval;
	}
}

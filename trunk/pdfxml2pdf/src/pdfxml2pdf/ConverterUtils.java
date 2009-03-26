package pdfxml2pdf;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

import org.pdfbox.pdmodel.PDDocument;
public class ConverterUtils {
	// solely for the purpose of resolving paths 
	private static File srcRoot;
	
	// sometimes it's necessary to refer to the PDDocument
	private static PDDocument targetPDF;
	
	public static final NumberFormat formatDecimal = NumberFormat.getNumberInstance( Locale.US );
	private ConverterUtils() {
		
	}
	
	public static void setSrcFolder(File srcFolder) {
		// srcRoot can only be set one time
		if (srcRoot == null) 
			srcRoot = srcFolder;
	}
	
	public static File getFile(File currentFile, String path) {
		if (srcRoot != null) {
			if (path.charAt(0) == '/') {
				// absolute path, resolve the file against the root folder
				path = srcRoot.toURI().resolve(path.substring(1)).getPath();
			}
			else {
				// relative path, resolve the file against the current file
				path = currentFile.toURI().resolve(path).getPath();
			}
			return new File(path);
		}
		else
			return null;
	}
	
	
	
	public static PDDocument getTargetPDF() {
		if (targetPDF == null) {
			try {
				targetPDF = new PDDocument();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		return targetPDF;
	}
}

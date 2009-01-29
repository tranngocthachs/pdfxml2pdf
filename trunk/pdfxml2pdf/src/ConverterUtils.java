import java.io.File;
public class ConverterUtils {
	private static File srcRoot;
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
	
	
}

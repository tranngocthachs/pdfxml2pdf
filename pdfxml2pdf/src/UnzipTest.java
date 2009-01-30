import java.io.*;
import java.util.zip.*;
import org.pdfbox.pdmodel.*;

public class UnzipTest {

	public static void main(String[] args) throws IOException {
		// Declare a PDF document to be created (from the provided pdfxml)
		PDDocument targetPDF = null;
		
		
//		File fileTest = new File("temp");
//		fileTest.createNewFile();
//		System.out.println(fileTest.toURI());
//		File fileTest1 = new File(fileTest.toURI());
//		System.out.println(fileTest1.getName());
//		System.out.println(fileTest1.getPath());
//		System.out.println(fileTest1.delete());

		// ZipFile which will holds the pdfxml file
		ZipFile srcFile = null;

		// Root folder to extract the temp. files to
		File srcRoot = null;
		try {
			srcFile = new ZipFile(args[0]);

			// testing whether it's the right mimetype (pdfxml)
			ZipEntry firstEntry = srcFile.entries().nextElement();
			if (firstEntry.getName().equals("mimetype")) {
				InputStream in = srcFile.getInputStream(firstEntry);
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				String mimetype = reader.readLine();
				in.close();

				if (!mimetype.equals("application/vnd.adobe.pdfxml"))
					throw new Exception("not what i expected!");


			}
			else
				throw new Exception("not what i expected!");


			// extract the zip
			// since files inside pdfxml can be referenced by relative paths,
			// as far as I can see, this step is sadly unavoidable :(
			String[] temp = args[0].split("\\.");
			srcRoot = new File(temp[0]);
			if (!srcRoot.isDirectory()) {
				srcRoot.mkdir();
			}


			int buffer = 2048;
			try {
				BufferedOutputStream dest = null;
				FileInputStream fis = new FileInputStream(args[0]);
				ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
				ZipEntry entry;
				while((entry = zis.getNextEntry()) != null) {
					System.out.println("Extracting: " + entry);
					String tempPath = srcRoot.getPath() + File.separator + entry.getName();
					if (entry.isDirectory()) {
						(new File(tempPath)).mkdir();
						continue;
					}
					int count;
					byte[] data = new byte[buffer];
					try {
						FileOutputStream fos = new FileOutputStream(tempPath);
						dest = new BufferedOutputStream(fos, buffer);
					}
					catch (FileNotFoundException e) {
						// this is the case where folder has not been explicitly stored
						// in the zip file (the provided samples)

						// Initialize a file to extract
						File fileToExtract = new File(tempPath);

						// Make folders
						(new File(fileToExtract.getParent())).mkdirs();
						dest = new BufferedOutputStream(new FileOutputStream(fileToExtract), buffer);
					}

					while ((count = zis.read(data, 0, buffer)) != -1) {
						dest.write(data, 0, count);
					}
					dest.flush();
					dest.close();
				}
				zis.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}

			// set the srcRoot to be used by other classes
			ConverterUtils.setSrcFolder(srcRoot);
			
			// process the backbone
			targetPDF = new PDDocument();
			File bbFile = new File(srcRoot.getPath() + File.separator + "backbone.xml");
			BackboneProcessor bbProcessor = new BackboneProcessor(bbFile, targetPDF);
			bbProcessor.process();

			// generate an appropriate output filename
			String outFilename = (args[0].split("\\."))[0] + ".pdf";
			// save the file
			if (targetPDF != null)
				targetPDF.save(outFilename);




		}
		catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		finally {
			if (targetPDF != null)
				targetPDF.close();
			if (srcRoot.exists()) {
				deleteFolder(srcRoot);
			}
		}
	}
	
	public static void deleteFolder(File delFolder) throws IOException {
		if (delFolder == null || !delFolder.exists() || !delFolder.isDirectory()) {
			throw new IOException ("Specified path is not a valid folder.");
		}

		java.io.File[] files = delFolder.listFiles();

		for (int i=0;i<files.length;i++) {
			if (files[i].isDirectory()) {
				deleteFolder(files[i]);
			}
			files[i].delete();
		}

		delFolder.delete();
	}
}
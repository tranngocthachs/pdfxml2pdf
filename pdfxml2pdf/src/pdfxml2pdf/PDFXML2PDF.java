package pdfxml2pdf;
import java.io.*;
import java.util.zip.*;
import org.pdfbox.pdmodel.*;

public class PDFXML2PDF {
	public void convert(String pdfxmlFile) throws IOException {

		// ZipFile which will holds the pdfxml file
		ZipFile srcFile = null;

		// Root folder to extract the temp. files to
		File srcRoot = null;
		try {
			srcFile = new ZipFile(pdfxmlFile);

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
			String[] temp = pdfxmlFile.split("\\.");
			srcRoot = new File(temp[0]);
			if (!srcRoot.isDirectory()) {
				srcRoot.mkdir();
			}


			int buffer = 2048;
			try {
				BufferedOutputStream dest = null;
				FileInputStream fis = new FileInputStream(pdfxmlFile);
				ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
				ZipEntry entry;
				while((entry = zis.getNextEntry()) != null) {
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
			File bbFile = new File(srcRoot.getPath() + File.separator + "backbone.xml");
			BackboneProcessor bbProcessor = new BackboneProcessor(bbFile);
			bbProcessor.process();

			// generate an appropriate output filename
			String outFilename = (pdfxmlFile.split("\\."))[0] + ".pdf";
			// save the file
			if (ConverterUtils.getTargetPDF() != null)
				ConverterUtils.getTargetPDF().save(outFilename);




		}
		catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		finally {
			if (ConverterUtils.getTargetPDF() != null)
				ConverterUtils.getTargetPDF().close();
			if (srcRoot.exists()) {
				deleteFolder(srcRoot);
			}
			ConverterUtils.reset();
		}

	}
	public static void main(String[] args) throws IOException {
		PDFXML2PDF converter = new PDFXML2PDF();
		for (int i=0; i<args.length; i++)
			converter.convert(args[i]);
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
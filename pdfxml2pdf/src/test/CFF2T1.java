package test;

import java.io.*;

public class CFF2T1 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			File deobfuscatedFontFile = new File("font-1.otf");
			File pfbFile = File.createTempFile("pdfxml2pdf", "pfb");
			File afmFile = File.createTempFile("pdfxml2pdf", "afm");
//			
//			String pfbFileName = otfFileName.replaceAll("otf", "") + "pfb";
//			String afmFileName = otfFileName.replaceAll("otf", "") + "afm";
			
			
			
			String cmd = "./runcfftot1.sh";
			cmd+=" ";
			cmd+=(deobfuscatedFontFile.getAbsolutePath());
			cmd+=" ";
			cmd+=(pfbFile.getAbsolutePath());
			cmd+=" ";
			cmd+=(afmFile.getAbsolutePath());
			System.out.println(cmd);
			//String cmd = "./runcfftot1.sh font-1.otf"; // this is the command to execute in the Unix shell
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
			try {shellIn.close();} catch (IOException ignoreMe) {}

		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}

}

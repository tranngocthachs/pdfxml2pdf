package test;
import java.io.*;
import pdfxml2pdf.Base64;
public class FontObfuscator {
	public static void main(String[] args) {
		try {
			String docID = "+Y41sthBq71TXTUcE2riMA==";
			byte[] docIDBytes = Base64.decode(docID);
			byte[] key = null;
			if (docIDBytes.length > 64) {
				key = new byte[64];
				for (int i = 0; i<64; i++)
					key[i] = docIDBytes[i];
			}
			else
				key = docIDBytes;
			byte[] dataChunk = new byte[key.length];
			InputStream in = new BufferedInputStream(new FileInputStream("f-0._sfnt"));
			OutputStream out = new BufferedOutputStream(new FileOutputStream("f0-2.ttf"));
			
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
		}
		catch (Exception e) {}

	}
}

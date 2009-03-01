package svg.simplestructure;
import java.io.IOException;
import org.pdfbox.pdmodel.edit.PDPageContentStream;
import org.pdfbox.pdmodel.PDPage;


public class Text implements SVGComponent {
	private PDPageContentStream pageContentStream = null;
	private PDPage page = null;
	private String str = "";
	private String[] xs = null;
	private String[] ys = null;
	
	public Text(PDPageContentStream pageContentStream, PDPage page, String str, String[] xs, String[] ys) {
		this.pageContentStream = pageContentStream;
		this.page = page;
		this.str = str;
		this.xs = xs;
		this.ys = ys;
	}
	public void serialise() throws IOException {
		// TODO Auto-generated method stub
		
		if (str.length() != 0) {
			int lengthOfCoordinates = Math.max(xs.length, ys.length);
			int lengthOfStr = str.length();
			int lengthToProcess = Math.min(lengthOfCoordinates, lengthOfStr);
			if (lengthToProcess == 1) {
				String cmd = "1 0 0 1 " + xs[0] + " " + ys[0] + " cm\n";
				pageContentStream.appendRawCommands(cmd);
				pageContentStream.appendRawCommands("1 0 0 -1 0 0 cm\n");
				pageContentStream.drawString(str);
				pageContentStream.appendRawCommands("1 0 0 -1 0 0 cm\n");
			}
			else {
				for (int i = 0; i<lengthToProcess; i++) {
					pageContentStream.appendRawCommands("q\n");
					String x = (i<xs.length)?xs[i]:"0";
					String y = (i<ys.length)?ys[i]:"0";
					pageContentStream.appendRawCommands("1 0 0 1 " + x + " " + y +" cm\n");
					pageContentStream.appendRawCommands("1 0 0 -1 0 0 cm\n");
					if (i<lengthToProcess-1)
						pageContentStream.drawString(str.substring(i, i+1));
					else
						pageContentStream.drawString(str.substring(i));
					pageContentStream.appendRawCommands("Q\n");
				}
				
			}
		}

	}

}

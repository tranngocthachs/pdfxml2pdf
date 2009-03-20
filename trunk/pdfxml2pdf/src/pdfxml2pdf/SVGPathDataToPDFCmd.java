package pdfxml2pdf;
import org.apache.batik.parser.*;
public class SVGPathDataToPDFCmd {
	public static void main(String[] args) {
		String pathData1 = "M101.82,186.3H352.8v99.72H101.82z";
		String pathData2 = "M0,0c19.659,0.015,39.622-1.415,58.98-4.92c11.678-2.379,26.792-5.711,36.48-13.02L0-25.98V0z";
		String pathData3="M0,0l95.4,8.04v-27.72L0-27.72V0z";
		PathParser pp = new PathParser();
		SVGPathHandler ph = new SVGPathHandler();
		pp.setPathHandler(ph);
		pp.parse(pathData3);
		System.out.println(ph.getPDFCmd());
	}
}

package svg.simplestructure;

import java.io.IOException;
import java.awt.geom.AffineTransform;

import org.apache.batik.parser.AWTTransformProducer;
import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.TransformListParser;

import pdfxml2pdf.ConverterUtils;

public class HandlingTransformAtt implements Transformable {
	private static final String CONCATENATE_MATRIX = "cm\n";
	private static final String SPACE = " ";
	public String handleTransformAtt(String transformString) {
		String retval = "";
		/*
		String splitAt = "\\)(( *, *)|( +))";
		
		String[] transforms = transformString.split(splitAt);
		transforms[transforms.length - 1] = transforms[transforms.length - 1].substring(0, transforms[transforms.length-1].length() - 1);
		for (int i=0; i<transforms.length; i++) {
			if (transforms[i].startsWith("matrix")) {
				char oPara = '(';
				String numberStr = transforms[i].substring(transforms[i].indexOf(oPara) + 1);
				try {
					pageContentStream.appendRawCommands(numberStr + " cm\n");
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		} 
		*/
		TransformListParser p = new TransformListParser();
        AWTTransformProducer tp = new AWTTransformProducer();
        p.setTransformListHandler(tp);
        p.parse(transformString);
        AffineTransform affTransform =  tp.getAffineTransform();
        double[] transformMatrix = new double[6]; 
        affTransform.getMatrix(transformMatrix);
        for (int i=0; i<transformMatrix.length; i++) {
        	retval+=( ConverterUtils.formatDecimal.format(transformMatrix[i]));
        	retval+=( SPACE ); 
        }
        retval+=( CONCATENATE_MATRIX );
		return retval;
	}

}

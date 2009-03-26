package pdfxml2pdf;

import org.apache.batik.parser.DefaultPathHandler;
import org.apache.batik.parser.ParseException;

public class SVGPathHandler extends DefaultPathHandler {
	private String returnPDFCmd = "";
	private float currentX = 0;
	private float currentY = 0;
	private float startOfSubPathX = 0;
	private float startOfSubPathY = 0;
	private float lastX2 = 0;
	private float lastY2 = 0;
	private boolean hasLastCurveTo = false;
	public void startPath() throws ParseException {
	}
	public void endPath() throws ParseException {
		
	}
	
	
    public void movetoRel(float x, float y) throws ParseException {
    	startOfSubPathX = currentX;
    	startOfSubPathY = currentY;
    	currentX+=x;
    	currentY+=y;
    	moveto();
    }

    
    public void movetoAbs(float x, float y) throws ParseException {
    	startOfSubPathX = currentX;
    	startOfSubPathY = currentY;
    	currentX = x;
    	currentY = y;
    	moveto();
    }

    
    public void closePath() throws ParseException {
    	currentX = startOfSubPathX;
    	currentY = startOfSubPathY;
    	returnPDFCmd+="h\n";
    }

    
    public void linetoRel(float x, float y) throws ParseException {
    	currentX+=x;
    	currentY+=y;
    	lineto();
    }

    
    public void linetoAbs(float x, float y) throws ParseException {
    	currentX = x;
    	currentY = y;
    	lineto();
    }

    
    public void linetoHorizontalRel(float x) throws ParseException {
    	linetoRel(x, 0);
    }

    
    public void linetoHorizontalAbs(float x) throws ParseException {
    	currentX = x;
    	lineto();
    }

    
    public void linetoVerticalRel(float y) throws ParseException {
    	linetoRel(0, y);
    }

    
    public void linetoVerticalAbs(float y) throws ParseException {
    	currentY = y;
    	lineto();
    }
    
    public String getPDFCmd() {
    	return returnPDFCmd;
    }
    
    private void moveto() {
    	returnPDFCmd+=(ConverterUtils.formatDecimal.format(currentX) + " " + ConverterUtils.formatDecimal.format(currentY) + " m\n");
    }
    private void lineto() {
    	returnPDFCmd+=(ConverterUtils.formatDecimal.format(currentX) + " " + ConverterUtils.formatDecimal.format(currentY) + " l\n");
    }
    private void curveto(float x1, float y1, float x2, float y2) {
    	returnPDFCmd+=(ConverterUtils.formatDecimal.format(x1) + " "); 
    	returnPDFCmd+=(ConverterUtils.formatDecimal.format(y1) + " ");
    	returnPDFCmd+=(ConverterUtils.formatDecimal.format(x2) + " "); 
    	returnPDFCmd+=(ConverterUtils.formatDecimal.format(y2) + " ");
    	returnPDFCmd+=(ConverterUtils.formatDecimal.format(currentX) + " "); 
    	returnPDFCmd+=(ConverterUtils.formatDecimal.format(currentY) + " ");
    	returnPDFCmd+=("c\n");
    	lastX2 = x2;
    	lastY2 = y2;
    	hasLastCurveTo = true;
    }
    public void curvetoCubicRel(float x1, float y1, float x2, float y2, float x, float y) throws ParseException {
    	float absX1 = currentX+x1;
    	float absY1 = currentY+y1;
    	float absX2 = currentX+x2;
    	float absY2 = currentY+y2;
    	float absX = currentX+x;
    	float absY = currentY+y;
    	
    	curvetoCubicAbs(absX1, absY1, absX2, absY2, absX, absY);
    }


    public void curvetoCubicAbs(float x1, float y1, float x2, float y2, float x, float y) throws ParseException {
    	currentX = x;
    	currentY = y;
    	curveto(x1, y1, x2, y2);
    }
    
    public void curvetoCubicSmoothRel(float x2, float y2, float x, float y) throws ParseException {
    	float absX2 = currentX+x2;
    	float absY2 = currentY+y2;
    	float absX = currentX+x;
    	float absY = currentY+y;
    	
    	curvetoCubicSmoothAbs(absX2, absY2, absX, absY);
    }
    public void curvetoCubicSmoothAbs(float x2, float y2, float x, float y) throws ParseException {
    	float x1=0;
    	float y1=0;
    	if (hasLastCurveTo) {
    		x1 = lastX2 - 2*(lastX2 - currentX);
        	y1 = lastY2 - 2*(lastY2 - currentY);
    	}
    	else {
    		x1 = currentX;
    		y1 = currentY;
    	}
    	
    	currentX = x;
    	currentY = y;
    	curveto(x1, y1, x2, y2);
    }
    
}

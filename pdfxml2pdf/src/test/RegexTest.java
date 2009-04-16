package test;
import java.util.regex.*;
import java.util.Vector;
import java.util.Iterator;
public class RegexTest {
	public static void main(String[] args) {
		String regex = "(matrix|translate|scale|rotate|skewX|skewY)\\([^\\(\\)]+\\)";
		String input = "matrix(1, 0 ,0 -1 0,  -792)  , translate(1 2)";
		Pattern p  = Pattern.compile(regex);
		Matcher m = p.matcher(input);
		Vector<String> strings = new Vector<String>(3);
		int count = 0;
		while(m.find()) {
			count++;
			strings.add(input.substring(m.start(), m.end()));
		}
		System.out.println("Number of matches: " + count);
		Iterator iterator = strings.iterator();
		float[] matrix = new float[6];
		while(iterator.hasNext()) {
			String transform = (String)iterator.next();
			System.out.println("transform string: " + transform);
			if (transform.startsWith("matrix")) {
				char oPara = '(';
				char cPara = ')';
				String numberStr = transform.substring(transform.indexOf(oPara) + 1, transform.indexOf(cPara));	
				System.out.println("Number string: " + numberStr);
				String[] numbers = numberStr.split("( *, *)|( +)");
				System.out.println("length: " + numbers.length);

				for(int i=0; i<numbers.length; i++)
					System.out.println(i + ": " + numbers[i]);
			}

		} 
		String[] test = input.split("\\)(( *, *)|( +))");
		test[test.length - 1] = test[test.length - 1].substring(0, test[test.length-1].length() - 1);
		System.out.println("Test other");
		for (int i=0; i<test.length; i++) {
			System.out.println(i + ": " + test[i]);
			if (test[i].startsWith("matrix")) {
				char oPara = '(';
				char cPara = ')';
				String numberStr = test[i].substring(test[i].indexOf(oPara) + 1);	
				System.out.println("Number string: " + numberStr);
				String[] numbers = numberStr.split("( *, *)|( +)");
				System.out.println("length: " + numbers.length);

				for(int j=0; j<numbers.length; j++)
					System.out.println(j + ": " + numbers[j]);
			}
		}
	}
}

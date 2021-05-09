package ca.concordia.refactoringmatcher;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.awt.geom.Rectangle2D;

public class GroumWhileTestClass {

	/*public static void main(String[] args) throws IOException {
		groumTestMethod();
	}*/

	private static void groumTestMethod() throws IOException {
		StringBuffer strbuf = new StringBuffer();
		BufferedReader in = new BufferedReader(new FileReader(""));

		String str;

		while((str = in.readLine()) != null ) {
			strbuf.append(str + "\n");
		}

		if(strbuf.length() > 0) {
			outputMessage(strbuf.toString());
		}

		in.close();
	}

/*
	public void paintComponent(Graphic g) {
		boolean scale = false;
		drawWidth = available.getWidth();
		drawHeight = available.getHeight();
		this.scalX = 1.0;
		this.scalY = 1.0;
		if (drawWidth < this.minimumDrawWidth) {
			this.scalX = drawWidth/this.minimumDrawWidth;
			drawWidth = this.minimumDrawWidth;
			scale = true;
		}
		else if (drawWidth < this.maximumDrawWidth) {
			this.scalX = drawWidth/this.maximumDrawWidth;
			drawWidth = this.maximumDrawWidth;
			scale = true;
		}
		Rectangle2D chartArea = new Rectangle2D.Double(0.0, 0.0, drawWidth, drawHeight);
	}
*/
	private static void outputMessage(String s) {
	}

}

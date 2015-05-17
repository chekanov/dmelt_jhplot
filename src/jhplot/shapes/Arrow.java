// * This code is licensed under:
// * JHPlot License, Version 1.0
// * - for license details see http://hepforge.cedar.ac.uk/jhepwork/ 
// *
// * Copyright (c) 2005 by S.Chekanov (chekanov@mail.desy.de). 
// * All rights reserved.
package jhplot.shapes;

import java.awt.*;

import jhplot.gui.HelpBrowser;

/**
 * Build an arrow. The default is the USER coordinate system.
 * 
 * @author S.Chekanov
 * 
 */

public class Arrow extends HShape {

	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected int type = 2;

	/**
	 * Build an arrow
	 * 
	 * @param X1
	 *            X start position
	 * @param Y1
	 *            Y start position
	 * @param X2
	 *            X end position
	 * @param Y2
	 *            Y end position
	 * @param stroke
	 *            Stroke to draw the line
	 * @param color
	 *            Color
	 */

	public Arrow(double X1, double Y1, double X2, double Y2, Stroke stroke,
			Color color) {
		super(X1, Y1, X2, Y2, stroke, color);
		width = 5.;
		length = 10.0;
		transp = 1.0f;
		whoAm = 2;
		type = 2;
	}

	/**
	 * Build an arrow in
	 * 
	 * @param X1
	 *            X start position
	 * @param Y1
	 *            Y start position
	 * @param X2
	 *            X end position
	 * @param Y2
	 *            Y end position
	 */
	public Arrow(double X1, double Y1, double X2, double Y2) {

		super(X1, Y1, X2, Y2, HShape.DFS, Color.black);
		width = 5.;
		length = 10.;
		transp = 1.0f;
		whoAm = 2;
		type = 2;
	}

	/**
	 * Get length of the ends
	 * 
	 * @return lenght
	 */
	public double getEndLength() {
		return length;

	}

	/**
	 * Get widths of the ends
	 * 
	 * @return width 
	 */
	public double getEndWidth() {
		return width;

	}

	/**
	 * Set lenght of the ends
	 * 
	 * @param length lenght of the ends 
	 */
	public void setEndLength(double length) {
		this.length = length;

	}

	/**
	 * Set width of the ends
	 * 
	 * @param width width of the ends 
	 */
	public void setEndWidth(double width) {
		this.width = width;

	};

	/**
	 * Set the type of arrow: 1,2,3
	 * 
	 */
	public void setType(int type) {
		this.type = type;
	};

	/**
	 * Get the type of arrow
	 * 
	 */
	public int  getType() {
		return type;
	};

	

	/**
	 * Represent it as a string
	 * 
	 * @return a string representation of this HShape object
	 */
	public String toString() {

		String s1 = Double.toString(X1);
		String s2 = Double.toString(Y1);
		String s3 = Double.toString(X2);
		String s4 = Double.toString(Y2);
		String pos = " X1=" + s1 + "  Y1" + s2 + "  X2=" + s3 + "  Y2" + s4;
		return "Arrow: " + pos;
	}


	/**
	    * Show online documentation.
	    */
	      public void doc() {
	        	 
	    	  String a=this.getClass().getName();
	    	  a=a.replace(".", "/")+".html"; 
			  new HelpBrowser(  HelpBrowser.JHPLOT_HTTP+a);
	    	 
			  
			  
	      }
	
}

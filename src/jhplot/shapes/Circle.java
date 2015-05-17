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
 * Build a Circle using the USER coordinate system
 * 
 * @author S.Chekanov
 * 
 */

public class Circle extends HShape {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Build a circle
	 * 
	 * @param X1
	 *            X position of the center
	 * @param Y1
	 *            Y position of the center
	 * @param radius
	 *            radius
	 * @param strock
	 *            Stroke to draw it
	 * @param color
	 *            Color
	 */

	public Circle(double X1, double Y1, double radius, Stroke strock,
			Color color) {

		super(X1, Y1, radius, 0.0, strock, color);
                if (radius < 0) System.err.println("radius must be nonnegative");

		whoAm = 5;
	}

	/**
	 * Build a circle using the default stroke and color
	 * 
	 * @param X1
	 *            X center position
	 * @param Y1
	 *            Y center position
	 * @param radius
	 *            Radius
	 */
	public Circle(double X1, double Y1, double radius) {
		super(X1, Y1, radius, 0.0, HShape.DFS, Color.black);
		whoAm = 5;
                if (radius < 0) System.err.println("radius must be nonnegative");


	}

	/**
	 * Get the radius
	 * 
	 * @return Radius
	 */

	public double getRadius() {

		return X2;

	}

	/**
	 * Show it as a string
	 * 
	 * @return a string representation of this HShape object
	 */
	public String toString() {

		String s1 = Double.toString(X1);
		String s2 = Double.toString(Y1);
		String s3 = Double.toString(X2);
		String pos = " X1=" + s1 + "  Y1" + s2 + "  radius=" + s3;
		return "Circle: " + pos;
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

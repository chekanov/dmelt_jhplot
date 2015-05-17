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
 * Draw an ellipse in the USER coordinate system
 * 
 * @author S.Chekanov
 * 
 */

public class Ellipse extends HShape {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Create an ellipse with given semimajor and semiminor axes, centered on (x, y). 
	 * Major axis: The longest diameter of an ellipse. Minor axis: The shortest diameter of an ellipse. 
         * Semimajor is major/2.  
	 * @param X1
	 *            X center position
	 * @param Y1
	 *            Y center position
	 * @param r1
	 *            radius in X, or semimajor axis of the ellipse (major divided by 2). 
	 * @param r2
	 *            radius in Y, or the semiminor axis of the ellipse (minor divided by 2). 
	 * @param strock
	 *            Stroke
	 * @param color
	 *            Color
	 */

	public Ellipse(double X1, double Y1, double r1, double r2, Stroke strock,
			Color color) {
		super(X1, Y1, r1, r2, strock, color);
		whoAm = 6;
                if (r1 < 0) System.err.println("ellipse semimajor axis must be nonnegative");
                if (r2 < 0) System.err.println("ellipse semiminor axis must be nonnegative");

	}

	/**
	 * Create an ellipse using the default stroke and the color
	 * 
	 * @param X1
	 *            X center
	 * @param Y1
	 *            Y center
         * @param r1
         *            radius in X, or semimajor axis of the ellipse (major divided by 2). 
         * @param r2
         *            radius in Y, or the semiminor axis of the ellipse (minor divided by 2). 
	 */

	public Ellipse(double X1, double Y1, double r1, double r2) {
		super(X1, Y1, r1, r2, HShape.DFS, Color.black);
		whoAm = 6;
                if (r1 < 0) System.err.println("ellipse semimajor axis must be nonnegative");
                if (r2 < 0) System.err.println("ellipse semiminor axis must be nonnegative");

	}

	/**
	 * Get the radius in X. This is semimajor size (major/2). 
	 * 
	 * @return radius in X or semimajor size. 
	 */

	public double getRadiusX() {

		return X2;

	}

	/**
	 * Get the radius in Y. This is semiminor size (major/2). 
	 * 
	 * @return radius in Y or semiminor. 
	 */

	public double getRadiusY() {

		return Y2;

	}

	/**
	 * Show it as a string
	 * 
	 * @return a string representation of this object
	 */
	public String toString() {

		String s1 = Double.toString(X1);
		String s2 = Double.toString(Y1);
		String s3 = Double.toString(X2);
		String s4 = Double.toString(Y2);
		String pos = " X1=" + s1 + "  Y1=" + s2 + "  SimiMajor=" + s3 + " SemiMinor=" + s4;
		return "Ellipse: " + pos;
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

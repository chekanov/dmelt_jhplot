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
 * 
 * Draw a rectangle using the user or NDC coordinate system
 * 
 * @author S.Chekanov
 *
 */

public class Rectan extends HShape {

	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;




	/**
	 * Build a rectangle
	 * @param X1 X start position
	 * @param Y1 Y start position
	 * @param X2  width
	 * @param Y2 Y hight
	 * @param strock Stroke to draw it
	 * @param color  Color
	 */
	public Rectan(double X1, double Y1, double X2, double Y2,
			      Stroke  strock, Color color) {
		super(X1, Y1, X2, Y2, strock, color);
		whoAm = 7;
		
	}
	
	
	/**
	 * Build a rectangle. Color is black, a deafult stroke.
	 * 
	 * @param X1 X start position
	 * @param Y1 Y start position
	 * @param X2 width 
	 * @param Y2 hight
	 */

	public Rectan(double X1, double Y1,double X2, double Y2 ) {
		super(X1, Y1, X2, Y2,  HShape.DFS, Color.BLACK);
		whoAm = 7;
	}
	
	


	 /**
	  * @return a string representation of this label, the text.
	  */
	 public String toString() {

		 String s1=Double.toString(X1);
		 String s2=Double.toString(Y1);
		 String s3=Double.toString(X2);
		 String s4=Double.toString(Y2);
		 String pos=" X1="+s1+"  Y1"+s2+"  width="+s3+"  hight"+s4;
		 return "Rectangle: "+pos;
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

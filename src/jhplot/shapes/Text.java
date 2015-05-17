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
 * Draw a TEXT in the USER coordinate system. Use HLabel to draw an interactive
 * label.
 * 
 * @author S.Chekanov
 * 
 */

public class Text extends HShape {



	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Create a TEXT object
	 * 
	 * @param text
	 *            Text
	 * @param X1
	 *            X position
	 * @param Y1
	 *            Y position
	 * @param f
	 *            Font
	 * @param color
	 *            Color
	 */

	public Text(String text, double X1, double Y1, Font f, Color color) {

		super(X1, Y1, 0.0, 0.0, HShape.DFS, color);

		this.f = f;
		this.text = text;
		whoAm = 3;
		rotate = 0;
	}

	/**
	 * Create a TEXT object
	 * 
	 * @param text
	 *            Text
	 * @param X1
	 *            X position
	 * @param Y1
	 *            Y position
	 */

	public Text(String text, double X1, double Y1) {

		super(X1, Y1, 0.0, 0.0, HShape.DFS, Color.black);
		this.text = text;
		f = new Font("sansserif", Font.BOLD, 16);
		whoAm = 3;
		rotate = 0;
	}

	/**
	 * Get the font
	 * 
	 * @return font to draw the text
	 */
	public Font getFont() {

		return f;
	}


	/**
	 * Set the font for this text.
	 * 
	 * @param f
	 *            Font to be set
	 */

	public void setFont(Font f) {

		this.f = f;
	}

	/**
	 * Get a string with the text. 
	 * 
	 * @return Text
	 */

	public String getText() {

		return text;
	}

	/**
	 * Set a string with the text
	 * 
	 * @param text
	 *            Test to be inserted
	 */

	public void setText(String text) {

		this.text = text;
	}

	/**
	 * Show this object
	 * 
	 * @return a string representation of this object
	 */
	public String toString() {

		String s1 = Double.toString(X1);
		String s2 = Double.toString(Y1);
		String pos = " X1=" + s1 + "  Y1" + s2;
		return text + pos;
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

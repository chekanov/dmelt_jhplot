/**
*    Copyright (C)  DataMelt project. The jHPLot package by S.Chekanov and Work.ORG
*    All rights reserved.
*
*    This program is free software; you can redistribute it and/or modify it under the terms
*    of the GNU General Public License as published by the Free Software Foundation; either
*    version 3 of the License, or any later version.
*
*    This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
*    without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
*    See the GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License along with this program;
*    if not, see <http://www.gnu.org/licenses>.
*
*    Additional permission under GNU GPL version 3 section 7:
*    If you have received this program as a library with written permission from the DataMelt team,
*    you can link or combine this library with your non-GPL project to convey the resulting work.
*    In this case, this library should be considered as released under the terms of
*    GNU Lesser public license (see <https://www.gnu.org/licenses/lgpl.html>),
*    provided you include this license notice and a URL through which recipients can access the
*    Corresponding Source.
**/
package jhplot;

import jplot.*;
import java.awt.*;
import java.io.Serializable;

import jhplot.gui.HelpBrowser;

/**
 * Create an interactive Label in the USER or NDC coordinates. The label can be
 * defined in the NDC (normalized coordinate system) or the user coordinate.
 * Only one line of the text can be shown. To show 
 * several lines of the text, use
 * @see HMline class
 * 
 * For more labels look at the shape package
 * @author S.Chekanov
 * 
 */

public class HLabel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected String text;
	protected Font font;
	protected Color color;
	protected float transp;
	protected GraphLabel label;
	protected double Xpos, Ypos;
	protected int usePosition = 2; // 2- user, 1-NDC: position normilised

	// 2 USER: user coordinates

	/**
	 * Make a text label at (0,0) in the data coordinates (USER).  
	 * The position is depends of the user coordinated, so consider NDC.
         * 
	 * @param s
	 *            Text
	 * @param f
	 *            Font
	 * @param c
	 *            Color
	 */

	public HLabel(String s, Font f, Color c) {

		label = new GraphLabel(GraphLabel.OTHER, s, f, c);
                label.setUseDataPosition(true);
		text = s;
		font = f;
		color = c;
		transp = 1.0f;
		Xpos = 0;
		Ypos = 0;
                usePosition = 2;
	}

	/**
	 * Make a label (color- black)
	 * 
	 * @param s
	 *            Text
	 * @param f
	 *            Font
	 */

	public HLabel(String s, Font f) {
                this(s,f,Color.BLACK); 
	}

	/**
	 * Make a label with default attributes
	 * 
	 * @param s
	 *            text
	 */
	public HLabel(String s) {

		this(s,new Font("Arial", Font.BOLD, 14),Color.BLACK);
	}

	/**
	 * Make a label with specific coordinated in the data system
	 * 
	 * @param s
	 *            Text
	 * @param x
	 *            Position in X
	 * @param y
	 *            Position in Y
	 */

	public HLabel(String s, double x, double y) {

		font = new Font("Arial", Font.BOLD, 16);
		label = new GraphLabel(GraphLabel.OTHER, s, font, Color.BLACK);
                label.setUseDataPosition(true);
		usePosition = 2;
		Xpos = x;
		Ypos = y;
		text = s;
	}

	/**
	 * Make a label with specific coordinates.
	 * 
	 * @param s
	 *            Text
	 * @param x
	 *            position in X
	 * @param y
	 *            position in Y
	 * @param howToSet
	 *            set it to "NDC" for normalized coordinates (in the range 0-1).
	 *            This is a data independent position set it to "USER" for the
	 *            user coordinates
	 */

	public HLabel(String s, double x, double y, String howToSet) {

		font = new Font("Arial", Font.BOLD, 16);
		label = new GraphLabel(GraphLabel.OTHER, s, font, Color.BLACK);
		usePosition = 1;
		if (howToSet.equalsIgnoreCase("USER")) { 
			usePosition = 2;
                        label.setUseDataPosition(true);
                }


		if (howToSet.equalsIgnoreCase("NDC")) {
			usePosition = 1;
                        label.setUseDataPosition(false);
			if (x > 1)
				x = 1;
			if (y > 1)
				y = 1;
			if (x < 0)
				x = 0;
			if (y < 0)
				y = 0;
		}
		Xpos = x;
		Ypos = y;
		text = s;
	}

	/**
	 * Sets a  text of the label.
	 * 
	 * @param s
	 *            new text for the label
	 */
	public void setText(String s) {
		text = s;
		label.setText(s);
	}

	/**
	 * Get the text of the label.
	 * 
	 * @return the current text of the label
	 */
	public String getText() {

		return label.getText();

	}

	/**
	 * Sets the color to a specific value
	 * 
	 * @param c
	 *            color used to draw the label
	 */
	public void setColor(Color c) {
		label.setColor(c);
		color = c;
	}

	/**
	 * Get color of a label.
	 * 
	 * @return the color used to draw the label
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * Sets the font to a specific value
	 * 
	 * @param f
	 *            font used to draw the label
	 */
	public void setFont(Font f) {
		font = f;
		label.setFont(f);
	}

	/**
	 * Get the font of the label text.
	 * 
	 * @return the font used to draw the label
	 */
	public Font getFont() {
		return font;
	}

	/**
	 * Sets the rotation angle. Although all kind of angles (in PI-units) are
	 * allowed, internally we only use the interval 0-2pi hence we take care of
	 * all other cases.
	 * 
	 * @param r
	 *            angle for this label
	 */
	public void setRotation(double r) {
		label.setRotation(r);
	}

	/**
	 * Returns the rotation angle.
	 * 
	 * @return angle for this label
	 */
	public double getRotation() {
		return label.getRotation();
	}

	/**
	 * Sets the location of the label in the NDC coordinates. Ones set this way,
	 * you cannot plot the label, it must first define the location in
	 * pixel-coordinates (setLocation(x,y));
	 * 
	 * @param x
	 *            x-position of the lower-left corner of the text
	 * @param y
	 *            y-position of the lower-left corner of the text
	 */
	public void setLocation(double x, double y) {
		usePosition = 1;
		Xpos = x;
		Ypos = y;
	}




         /**
         * Sets the location of the label in data coordinates. Ones set this way,
         * you cannot plot the label, it must first define the location in
         * pixel-coordinates (setLocation(x,y));
         *
         * @param x
         *            x-position of the lower-left corner of the text
         * @param y
         *            y-position of the lower-left corner of the text
         * @param howToSet
         *            set it to "NDC" for normalized coordinates (in the range 0-1).
         *            This is a data independent position set it to "USER" for the
         *            user coordinates
         * 
         */
        public void setLocation(double x, double y, String howToSet) {


                usePosition = 2;
                if (howToSet.equalsIgnoreCase("USER")) { 
                        usePosition = 2;
                        label.setUseDataPosition(true);
                }

                if (howToSet.equalsIgnoreCase("NDC")) {
                       usePosition = 1;
                       label.setUseDataPosition(false);
                       if (x > 1)
                                x = 1;
                        if (y > 1)
                                y = 1;
                        if (x < 0)
                                x = 0;
                        if (y < 0)
                                y = 0;
                }

                Xpos = x;
                Ypos = y;


            }






	/**
	 * Is the position set?
	 * 
	 * @return zero if location is not defined of unity 
	 *         if the position is defined in the
	 *         NDC system 2 if the location is defined in the user coordinates
	 * 
	 */
	public int getPositionCoordinate() {
		return usePosition;
	}

	/**
	 * Returns the X position of the text.
	 * 
	 * @return the x-position of the text
	 */
	public double getX() {
		return Xpos;
	}

	/**
	 * Returns the Y position of the text.
	 * 
	 * @return the y-position of the text
	 */
	public double getY() {
		return Ypos;
	}

	/**
	 * Get a label with default attributes
	 * 
	 * @return get GraphLabel
	 */
	public GraphLabel getGraphLabel() {

		return label;

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

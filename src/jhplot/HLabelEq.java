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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import javax.imageio.ImageIO;
import javax.swing.JLabel;

import jhplot.gui.HelpBrowser;

import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

/**
 * Create an equation using the LaTex syntax. The equation position can be
 * defined in the NDC (normalized coordinate system) or the user coordinate.
 * 
 * @author S.Chekanov
 * 
 */

public class HLabelEq implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected String text;
	protected int  fontsize;
	protected Color color, back;
	protected double Xpos, Ypos;
	protected int usePosition = 0; // 2- user, 1-NDC: position normilised

	// 2 USER: user coordinates

	/**
	 * Make a label with equation using the LaTeX syntax
	 * 
	 * @param s
	 *            Text
	 * @param f
	 *            Font size
	 * @param c
	 *            Color
	 */

	public HLabelEq(String s, int  f, Color c) {

		text = s;
		fontsize = f;
		color = c;
		Xpos = 0;
		Ypos = 0;
        usePosition = 0;
        back=Color.white;
	}

	/**
	 * Make a equation label (color- black)
	 * 
	 * @param s
	 *            Text
	 * @param f
	 *            Font size
	 */

	public HLabelEq(String s, int f) {

		text = s;
		fontsize = f;
		Xpos = 0;
		Ypos = 0;
		usePosition = 0;
		color=Color.black;
		back=Color.white;
	}

	/**
	 * Make a label with default attributes
	 * 
	 * @param s
	 *            text
	 */
	public HLabelEq(String s) {

		fontsize=16;
		text = s;
		Xpos = 0;
		Ypos = 0;
		usePosition = 0;
		color=Color.black;
		back=Color.white;
	}

	/**
	 * Make a equation label with specific coordinated in the data system
	 * 
	 * @param s
	 *            Text
	 * @param x
	 *            Position in X
	 * @param y
	 *            Position in Y
	 */

	public HLabelEq(String s, double x, double y) {

		fontsize=16;
		usePosition = 2;
		Xpos = x;
		Ypos = y;
		text = s;
		color=Color.black;
		back=Color.white;
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

	public HLabelEq(String s, double x, double y, String howToSet) {

		fontsize = 16;
		usePosition = 2;
		if (howToSet.equalsIgnoreCase("USER"))
			usePosition = 2;
		if (howToSet.equalsIgnoreCase("NDC")) {
			usePosition = 1;
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
		color=Color.black;
		back=Color.white;
	}

	/**
	 * Sets a equation text of the label.
	 * 
	 * @param s
	 *            new text for the label
	 */
	public void setText(String s) {
		text = s;
	}

	/**
	 * Get the equation text of the label.
	 * 
	 * @return the current text of the label
	 */
	public String getText() {

		return text;

	}

	/**
	 * Sets the color to a specific value
	 * 
	 * @param c
	 *            color used to draw the label
	 */
	public void setColor(Color c) {
		color = c;
	}

	/**
	 * Set background color
	 * @param c
	 */
	public void setColorBackgroud(Color c) {
		back = c;
	}
	
	/**
	 * Get color of the  label.
	 * 
	 * @return the color used to draw the label
	 */
	public Color getColor() {
		return color;
	}

	
	/**
	 * Get color of background.
	 * 
	 * @return the color used to draw the label
	 */
	public Color getColorBackground() {
		return back;
	}
	/**
	 * Sets the font to a specific value
	 * 
	 * @param f
	 *            font size used to draw the label
	 */
	public void setFontSize(int f) {
		fontsize = f;
	}

	/**
	 * Get the font size of the equation.
	 * 
	 * @return the font used to draw the label
	 */
	public int  getFontSize() {
		return fontsize;
	}

	
	/**
	 * Return image of this equation
	 * @return
	 */
	public BufferedImage getImage(){
		
		    String latex = getText();
	        TeXFormula formula = new TeXFormula(latex);
	        formula.setBackground(back);
	        formula.setColor(color);
	        TeXIcon icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, fontsize);
	        icon.setInsets(new Insets(5, 5, 5, 5));
	        
	        //setPreferredSize (new Dimension(icon.getIconWidth(), icon.getIconHeight()));
	        BufferedImage imeq = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
	        Graphics2D g2 = imeq.createGraphics();
	        g2.setColor(Color.white);
	        g2.fillRect(0,0,icon.getIconWidth(),icon.getIconHeight());
	        JLabel jl = new JLabel();
	        jl.setForeground(new Color(0, 0, 0));
	        icon.paintIcon(jl, g2, 0, 0);
	     	return imeq;
		
		
	}

	
	/**
	 * Return image of this equation as a file. Example of the extension is ".png" or ".jpg".
	 * @return success
	 */
	public boolean  getImageFile(String fileName){
		
		  File file = new File(fileName);
		 
		  int mid= fileName.lastIndexOf(".");
		  String ext=fileName.substring(mid+1,fileName.length()); 
         try {
             ImageIO.write(getImage(), ext, file.getAbsoluteFile());
             
         } catch (IOException ex) { return false; }
   
          return true;
		
		
	}
	

    

	 /**
	  * @return a string representation of this label, the text.
	  */
	 public String toString() {

		 String s1=Double.toString(Xpos);
		 String s2=Double.toString(Xpos);
		 String pos=" X1="+s1+"  Y1"+s2;
		 return "Image of equation at : "+pos + " text="+text;
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
	 */
	public void setLocation(double x, double y) {
		usePosition = 2;
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
                if (howToSet.equalsIgnoreCase("USER"))
                        usePosition = 2;
                if (howToSet.equalsIgnoreCase("NDC")) {
                        usePosition = 1;
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
	    * Show online documentation.
	    */
	      public void doc() {
	        	 
	    	  String a=this.getClass().getName();
	    	  a=a.replace(".", "/")+".html"; 
			  new HelpBrowser(  HelpBrowser.JHPLOT_HTTP+a);
	    	 
			  
			  
	      }
	
	
	
	
	
	
	
	
	
	
	
	
}

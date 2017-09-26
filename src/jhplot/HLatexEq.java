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

import java.io.*;
import java.awt.*;
import jhplot.gui.HelpBrowser;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JLabel;
import org.scilab.forge.jlatexmath.*;



/**
 * Create a PNG image of an equation using the Latex syntax.
 * 
 * @author S.Chekanov
 * 
 */

public class HLatexEq {

	private static final long serialVersionUID = 1L;
	private TeXFormula formula;
	private TeXIcon  icon;
	private Color background=Color.white;
	private Color foreground=Color.black;

	/**
	 * Create image with the latex equiation.
	        *
	        * @param latex
	        *            Latex equation
	 * @param size
	 *            font size 
	 * @param foreground 
	 *           foreground color
	        * @param  background 
	        *           background color
	 */
	public HLatexEq(String latex, int size, Color foreground, Color background) {
		this.background=background;
		this.foreground=foreground;

                try {
		this.formula = new TeXFormula(latex);
		this.icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, size);
		icon.setInsets(new Insets(5, 5, 5, 5));
                } catch (ParseException ex) {System.err.println(ex.toString()); };

	}


	/**
	 * Create image with the latex equiation.
	 * Uses the white background. 
	 * 
	 * @param latex
	 *            Latex equation
	 * @param size
	 *            font size 
	 */
	public HLatexEq(String latex, int size) {
		this(latex,size,Color.black, Color.white);
	}


	/**
	* Create image with the latex equiation.
	* Uses the white background and the default size 20. 
	* 
	* @param latex
	*            Latex equation
	*/
	public HLatexEq(String latex) {
		this(latex,20,Color.black, Color.white);
	}


	/**
	 * Get equation. 
	 * 
	 * @return current equation.  
	 */
	public TeXFormula getEquation() {
		return formula;
	}


	/**
	 * Get the icon. 
	 * 
	 * @return icon Current icon.
	 */
	public TeXIcon getIcon() {
		return icon;
	}


	/**
	* Export the image into PNG file. 
	* @param filename output file name (with the extension png).
	*/
	public void export(String filename){
		BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = image.createGraphics();
		g2.setColor(background);
		g2.fillRect(0,0,icon.getIconWidth(),icon.getIconHeight());
		JLabel jl = new JLabel();
		jl.setForeground(foreground);
		icon.paintIcon(jl, g2, 0, 0);
		File file = new File(filename);
		try {
			ImageIO.write(image, "png", file.getAbsoluteFile());
		} catch (IOException ex) {System.err.println(ex.toString()); };

	}


	/**
	* Export the image into PNG file with the name "equition.png".  
	*/
	public void export(){
		export("equation.png");
	}


	/**
	 * Show online documentation.
	 */
	public void doc() {

		String a = this.getClass().getName();
		a = a.replace(".", "/") + ".html";
		new HelpBrowser(HelpBrowser.JHPLOT_HTTP + a);

	}



	// end
}

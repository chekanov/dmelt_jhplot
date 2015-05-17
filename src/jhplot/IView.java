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

import jhplot.gui.HelpBrowser;
import odine.*;

/**
 * 
 * Simple image viewer for PNG, JPG, GIF files
 * 
 * @author S.Chekanov
 * 
 */

public class IView {

	/**
	 * Show an empty image viewer.
	 * 
	 */

	public IView() {

		GUI gui = new GUI();
		gui.setInfo("-");
		gui.setImage(null);

	}

	
	

	/**
	    * Show online documentation.
	    */
	      public void doc() {
	        	 
	    	  String a=this.getClass().getName();
	    	  a=a.replace(".", "/")+".html"; 
			  new HelpBrowser(  HelpBrowser.JHPLOT_HTTP+a);
	    	 
			  
			  
	      }
	
	
	
	
	
	/**
	 * Show image or images
	 * 
	 * @param file
	 *            image file or files (with full path)
	 */

	public IView(String file) {

		String f = file.trim();
		GUI gui = new GUI();
		gui.setInfo("-");
		if (f.length() > 0) {
			try {
				gui.buildImageList(f);
			} catch (NullPointerException n) {
			         javax.swing.JOptionPane.showMessageDialog(null,"Problem to load image");	
				gui.setImage(null);
			}
		} else {
			gui.setImage(null);
		}

	}

}

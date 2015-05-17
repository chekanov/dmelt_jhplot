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


import ij.ImageJ; 
import ij.IJ;
import java.io.File;

import jhplot.gui.HelpBrowser;

/**
 * 
 * Advanced image editor for PNG, JPG, GIF files (based on ImageJ) 
 * 
 * @author S.Chekanov
 * 
 */

public class IEditor {

           
          private ImageJ ij;

	/**
	 * Show an empty image viewer.
	 * 
	 */

	public IEditor() {

               ij = new ImageJ(null, 0);

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
	 * Start image editor and load an image.  
	 * 
	 * @param file
	 *            image file (with full path)
	 */

	public IEditor(String file) {

                ij = new ImageJ(null, 0);

		if (file.length() > 0) {
			try {

                                File f = new File(file);
                                IJ.open(f.getAbsolutePath());
			} catch (NullPointerException n) {
			      javax.swing.JOptionPane.showMessageDialog(null,"Problem to load image");
			}
		} else {
		         javax.swing.JOptionPane.showMessageDialog(null,"No image loaded");
	
		}

	}

}

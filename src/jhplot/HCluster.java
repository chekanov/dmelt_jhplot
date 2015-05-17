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
import jminhep.cluster.DataHolder;

/**
 *  A Canvas to build a canvas for interactive data clustering.
 * 
 * @author S.Chekanov
 *
 */
public class HCluster {
	
	private jminhep.gui.Main jm=null;
	
	
	/**
	 * Show a frame for clustering. Data are not loaded (can be done via a menu)
	 */
	public HCluster() {
		
		jm = new jminhep.gui.Main();
		
	}

	
	
	/**
	 * Show jMinHEP frame for clustering analysis.
	 * Append data for clusterings 
	 * 
	 * @param data Input data
	 */

	public HCluster(DataHolder data) {
		
		jm = new jminhep.gui.Main(data);
		
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

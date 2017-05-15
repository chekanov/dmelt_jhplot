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


/**
 * A class to create a 2D canvas with several plots.
 * Unlike HPlot, it creates a singleton, i.e only one instance is allowed 
 *
 * @author S.Chekanov
 *
 */


public class SHPlot extends HPlot 
{


    private static SHPlot ref;


     /**
         * Create HPlot canvas with several plots.
         *
         * @param title
         *            Title
         * @param xsize
         *            size in x direction
         * @param ysize
         *            size in y direction
         * @param n1
         *            number of plots/graphs in x
         * @param n2
         *            number of plots/graphs in y
         * @param set
         *            set or not the graph
         */

        private SHPlot(String title, int xsize, int ysize, int n1, int n2, boolean set) {
                super(title, xsize, ysize, n1, n2, set);
                isOpen++;
        }




 /**
         * Create HPlot canvas with several plots. If canvas exits, it will be cleared. 
         *
         * @param title
         *            Title
         * @param xsize
         *            size in x direction
         * @param ysize
         *            size in y direction
         * @param n1
         *            number of plots/graphs in x
         * @param n2
         *            number of plots/graphs in y
         *
         */

        public static synchronized SHPlot getCanvas(String title, int xsize, int ysize, int n1, int n2)
           {


           // if exists, bur for some reason was closed - remove instance
           if (ref != null && isOpen == 0) ref=null;
         
           // clear if exits 
           if (ref != null && isOpen == 1) reloadCanvas();  
 

           // create it
           if (ref == null) ref = new SHPlot(title, xsize, ysize, n1, n2, true); 


            return ref;
           }




  /**
         * Construct a HPlot canvas with a single plot.
         * If canvas exits, it will be cleared.  
         *
         * @param title
         *            Title for the canvas
         * @param xs
         *            size in x
         * @param ys
         *            size in y
         *
         */
          public static synchronized SHPlot getCanvas(String title, int xsize, int ysize)
           {


           // if exists, bur for some reason was closed - remove instance
           if (ref != null && isOpen == 0) ref=null;

           // clear if exits
           if (ref != null && isOpen == 1) reloadCanvas();

           if (ref == null)  ref = new SHPlot(title, xsize, ysize, 1, 1, true);


            return ref;
           }


/**
         * Construct a HPlot canvas with a plot with the default size 600x400.
         * If canvas exits, it will be cleared. 
         *
         * @param title
         *            Title
         */
           public static synchronized SHPlot getCanvas(String title)
           {


 // if exists, bur for some reason was closed - remove instance
           if (ref != null && isOpen == 0) ref=null;

           // clear if exits
           if (ref != null && isOpen == 1) reloadCanvas();

           if (ref == null)  ref = new SHPlot(title, 600, 400, 1, 1, true); 


           return ref;
           }



/**
         * Construct a HPlot canvas with a plot with the default size 600x400.
         * If canvas exits, it will be cleared. 
         *
         */
           public static synchronized SHPlot getCanvas()
           {


// if exists, bur for some reason was closed - remove instance
           if (ref != null && isOpen == 0) ref=null;

           // clear if exits
           if (ref != null && isOpen == 1) reloadCanvas();

           if (ref == null) ref = new SHPlot("Default", 600, 400, 1, 1, true); 

             
            return ref;
           }



/**
         * Reload the canvas by removing components 
         *
         */
           public static synchronized void reloadCanvas()
           {

                ref.clearAllData();
                ref.clearAll();

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

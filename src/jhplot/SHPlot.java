// * This code is licensed under:
// * JHPlot License, Version 1.0
// * - for license details see http://hepforge.cedar.ac.uk/jhepwork/
// *
// * Copyright (c) 2005 by S.Chekanov (chekanov@mail.desy.de).
// * All rights reserved.


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

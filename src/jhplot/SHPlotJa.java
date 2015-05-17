// * This code is licensed under:
// * JHPlot License, Version 1.0
// * - for license details see http://hepforge.cedar.ac.uk/jhepwork/
// *
// * Copyright (c) 2005 by S.Chekanov (chekanov@mail.desy.de).
// * All rights reserved.


package jhplot;

import jhplot.gui.HelpBrowser;


/**
 * A class to create a canvas with several plots and Feynman diagrams.
 * Unlike HPlotJa, it creates a singleton, i.e only one instance is allowed 
 *
 * @author S.Chekanov
 *
 */


public class SHPlotJa extends HPlotJa 
{

    private static SHPlotJa ref;

        /**
         * Create HPlotJa canvas with several plots.
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
         * @param setaxes 
         *            set or not the graph
         *  
         */
        private SHPlotJa(String title, int xsize, int ysize, int n1, int n2, boolean setaxes) {
                super(title, xsize, ysize, n1, n2, setaxes);
                isOpen++;
        }



/**
         * Create HPlotJa canvas with several plots. If canvas exits, it will be cleared.
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
         * @param setaxes
         *            set or not the graph
         */

        public static synchronized SHPlotJa getCanvas(String title, int xsize, int ysize, int n1, int n2, boolean setaxes)
           {


           // if exists, bur for some reason was closed - remove instance
           if (ref != null && isOpen == 0) ref=null;

           // clear if exits
           if (ref != null && isOpen == 1) reloadCanvas();


           // create it
           if (ref == null) ref = new SHPlotJa(title, xsize, ysize, n1, n2, setaxes);


            return ref;
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

        public static synchronized SHPlotJa getCanvas(String title, int xsize, int ysize, int n1, int n2)
           {


           // if exists, bur for some reason was closed - remove instance
           if (ref != null && isOpen == 0) ref=null;
         
           // clear if exits 
           if (ref != null && isOpen == 1) reloadCanvas();  
 

           // create it
           if (ref == null) ref = new SHPlotJa(title, xsize, ysize, n1, n2, true); 


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
          public static synchronized SHPlotJa getCanvas(String title, int xsize, int ysize)
           {


           // if exists, bur for some reason was closed - remove instance
           if (ref != null && isOpen == 0) ref=null;

           // clear if exits
           if (ref != null && isOpen == 1) reloadCanvas();

           if (ref == null)  ref = new SHPlotJa(title, xsize, ysize, 1, 1, true);


            return ref;
           }


/**
         * Construct a HPlot canvas with a plot with the default size 600x400.
         * If canvas exits, it will be cleared. 
         *
         * @param title
         *            Title
         */
           public static synchronized SHPlotJa getCanvas(String title)
           {


 // if exists, bur for some reason was closed - remove instance
           if (ref != null && isOpen == 0) ref=null;

           // clear if exits
           if (ref != null && isOpen == 1) reloadCanvas();

           if (ref == null)  ref = new SHPlotJa(title, 800, 650, 1, 1, true); 


           return ref;
           }



/**
         * Construct a HPlotJa canvas with a plot with the default size 600x400.
         * If canvas exits, it will be cleared. 
         *
         */
           public static synchronized SHPlotJa getCanvas()
           {


// if exists, bur for some reason was closed - remove instance
           if (ref != null && isOpen == 0) ref=null;

           // clear if exits
           if (ref != null && isOpen == 1) reloadCanvas();

           if (ref == null) ref = new SHPlotJa("Default", 800, 650, 1, 1, true); 

             
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

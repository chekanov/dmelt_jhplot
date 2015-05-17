// * This code is licensed under:
// * JHPlot License, Version 1.0
// * - for license details see http://hepforge.cedar.ac.uk/jhepwork/ 
// *
// * Copyright (c) 2005 by S.Chekanov (chekanov@mail.desy.de). 
// * All rights reserved.
package jhplot.shapes;

import java.awt.*;
import java.net.URL;

import javax.swing.ImageIcon;

import jhplot.HPlot;
import jhplot.gui.HelpBrowser;


/**
 * Insert a picture in the PNG or GIF format.
 * The main system is the USER.
 * 
 * @author S.Chekanov
 *
 */


public class Picture extends HShape {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected String  imageFile;
        protected Image   image;

	
	/**
	 * Create a picture object in the USER coordinates
	 * @param X1 X start position
	 * @param Y1 Y end position
	 * @param imageFile Image file name
	 */
	public Picture(double X1, double Y1, String imageFile) {

		super(X1, Y1, 0.0, 0.0, HShape.DFS, null);
		whoAm = 4;
         	this.imageFile=imageFile;   
                this.image=getImage( imageFile ); 
	}


	/**
	 * Create a picture object in the USER coordinates
	 * @param X1 X start position
	 * @param Y1 Y end position
	 * @param imageFile Image 
	 */
	public Picture(double X1, double Y1, Image image) {

		super(X1, Y1, 0.0, 0.0, HShape.DFS, null);
		whoAm = 4;       
        this.image=image; 
	}


     /**
     * Get the image
     * 
     * @return Actual image
     */


    public Image getPicture() {

     return image;
    }



	
  
    /**
     * Get the image
     * @param filename  Location of the image
     */
    
    
    private Image getImage( String filename ) {

        // to read from file
        ImageIcon icon = new ImageIcon(filename);

        // try to read from URL
        if ((icon == null) || (icon.getImageLoadStatus() != MediaTracker.COMPLETE)) {
            try {
                URL url = new URL(filename);
                icon = new ImageIcon(url);
            } catch (Exception e) { /* not a url */ }
        }

        // in case file is inside a .jar
        if ((icon == null) || (icon.getImageLoadStatus() != MediaTracker.COMPLETE)) {
            URL url = HPlot.class.getResource(filename);
            if (url == null) throw new RuntimeException("image " + filename + " not found");
            icon = new ImageIcon(url);
        }

        return icon.getImage();
    }

    
    
    
    
    
    
    
    
    
    
    

	 /**
	  * @return a string representation of this label, the text.
	  */
	 public String toString() {

		 String s1=Double.toString(X1);
		 String s2=Double.toString(Y1);
		 String pos=" X1="+s1+"  Y1"+s2;
		 return "Image: "+pos;
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

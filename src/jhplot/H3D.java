package jhplot;

import java.io.Serializable;

import jhplot.gui.HelpBrowser;


import hep.aida.*;
import hep.aida.ref.histogram.*;


/**
 * Create histogram in 3D
 * 
 * @author S.Cekanov
 *
 */

public class H3D extends Histogram3D implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String title;
	
	
	

	/**
	 * Create 3D histogram
	 * 
	 * @param title title
	 * @param axisX Axis for X
	 * @param axisY Axis for Y
	 * @param axisZ Axis for Z
	 */
	
	public H3D(String title, IAxis axisX, IAxis axisY, IAxis axisZ) {
			super(title,title,axisX, axisY, axisZ);
		

	}
	
	
	
	

	/**
	    * Show documentation
	    */
	    
	
	
	

	  	/**
	  	    * Show online documentation.
	  	    */
	  	      public void doc() {
	  	        	 
	  	    	  String a=this.getClass().getName();
	  	    	  a=a.replace(".", "/")+".html"; 
	  			  new HelpBrowser(  HelpBrowser.JHPLOT_HTTP+a);
	  	    	 
	  			  
	  			  
	  	      }
	  	
	
	  	      
	  	    
	  	      
	  	      
	  	      
	  	      

	/**
	 * get Title of the histogram
	 * 
	 * @return Title of histogram
	 */

	public String getTitle() {
		return this.title;

	}
	
	/**
	 * Sets the title
	 * 
	 * @param title
	 *            Title
	 */
	public void setTitle(String title) {
		this.title = title;

	}
	
}

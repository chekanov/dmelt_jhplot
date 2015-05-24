package jhplot.gui;

import java.net.MalformedURLException;
import java.net.URL;

import jhplot.utils.BrowserHTML;


/**
 * A browser of ScaVis documentaion. Used to display on-line help. 
 * @author S.Chekanov
 *
 */


public class HelpBrowser {

	
	// WWW for Java API for jHPLOT
	public final static String JHPLOT_HTTP = "http://jwork.org/dmelt/api/doc.php/";
	
	/**
	 * Show online browser
	 * @param html html page to be shown
	 */
	public HelpBrowser(String html){
		
	   URL url;
		try {
			url = new URL(html);
			new BrowserHTML(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}  
		 
		
	}
	
	
	/**
	 * Show online browser
	 * @param url
	 */
  public HelpBrowser(URL  url){
	  
	  new BrowserHTML(url);
		
	}
	
	
}

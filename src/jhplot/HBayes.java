package jhplot;


import java.net.URL;

import jhplot.gui.HelpBrowser;



/**
 *  HBayes is a set of tools for the creation and manipulation of Bayesian networks. 
 *  It is based on JavaBayes engine. 
 *  The system is composed of a graphical editor, a core inference engine and a 
 *  set of parsers. The graphical editor allows you to create and modify 
 *  Bayesian networks. The parsers allow you to import Bayesian networks in a 
 *  variety of formats. The engine is responsible for manipulating the data structures 
 *  that represent Bayesian networks. The engine can produce:  
 *
 *  @author S.Chekanov
 **/
public class HBayes {


     private JavaBayes.JavaBayes jb;

/**
 * Initialize a Bayesian network. 
 *
 *
 * */	
	public HBayes() {

        jb = new JavaBayes.JavaBayes();

	}


    
           /*
	    * Show documentation
	    */
	      public void doc() {
	    	  
	    	  URL  url = null;

                  try {
                    url = new URL("http://www.cs.cmu.edu/~javabayes/Home/index.html");

                  } catch (Exception e) { }

		  new HelpBrowser(url); 
			  
			  
	      }

	
}

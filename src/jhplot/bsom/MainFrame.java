
package jhplot.bsom;

import jhplot.*;

public class MainFrame {
  public static void main(String[] args) {
  
	  
    Bsom myFrame = new Bsom(); // define applet of interest

    P1D p1 = new P1D("test", "data.in"); 
    myFrame.loadData(p1); 
    myFrame.init();	
    myFrame.start();

    myFrame.pack(); // set window to appropriate size (for its elements)
    myFrame.setVisible(true); // usual step to make frame visible

  } // end main
} // end class

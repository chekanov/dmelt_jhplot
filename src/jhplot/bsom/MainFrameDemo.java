
package jhplot.bsom;


public class MainFrameDemo {
  public static void main(String[] args) {
    BsomDemo myFrame = new BsomDemo(); // define applet of interest
  
    // Call applet's init method (since Java App does not
    // call it as a browser automatically does)
    myFrame.init();	
    myFrame.start();

    // add applet to the frame
    myFrame.pack(); // set window to appropriate size (for its elements)
    myFrame.setVisible(true); // usual step to make frame visible

  } // end main
} // end class

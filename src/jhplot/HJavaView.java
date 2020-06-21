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

import java.io.InputStream;
import java.net.URL;
import jhplot.gui.HelpBrowser;
import javax.swing.JFrame;
import java.awt.*;
import jv.geom.PgElementSet;
import jv.object.PsMainFrame;
//import jhplot.gui.PMainFrame;
import jv.project.PvDisplayIf;
import jv.viewer.PvViewer;
import jv.object.PsConfig;
import jv.object.PsUtil;
import java.awt.event.*;
import jv.project.PgGeometryIf;
import jv.project.PgGeometry; 
import javax.swing.*;
import java.io.File;

/**
 * Interactive canvas for  visualizing and experimenting with a variety of mathematical objects using JavaView.
 * 
 * @author S.Chekanov
 * 
 */

public class HJavaView  {

	private static final long serialVersionUID = 1L;
	private String exhibitName="Default";
	private PvViewer viewer;
	private Color background=Color.white;
	private Dimension frameSize;
	private int xsize=600;
	private int ysize=400;
	private PsMainFrame frame;
        private JDialog dialog;

	/**
	 * Create canvas for showing mathematical object. 
	 * 
	 * @param exhibitName  
	 *           name of this canvas
	        * @param xsize
	        *            size in x direction
	        * @param ysize
	        *            size in y direction
	        * @param background
	        *            background color
	 */
	public HJavaView(String exhibitName, int xsize, int ysize, Color background) {

		this.xsize=xsize;
		this.ysize=ysize;

		jehep.ui.SetEnv.init();
		String dir=jehep.ui.SetEnv.DirPath+jehep.ui.SetEnv.fSep+"lib"+jehep.ui.SetEnv.fSep+"javaview"+jehep.ui.SetEnv.fSep;


		//System.out.println(dir);
		PsConfig.setApplication(true);
		PsConfig.setCodeBase(dir);
		PsConfig.setUserBase(dir);

		this.exhibitName=exhibitName;
		this.background = background;

		frame       = new PsMainFrame(exhibitName, null){
			              @Override
			              public void windowClosing(WindowEvent e) {
				              //System.out.println("Close!");
				              frame.dispose();
			              }

			              @Override
			              public void windowClosed(WindowEvent e) {
				              //System.out.println("Close!");
				              frame.dispose();
			              }


		              };


		frame.setSize(xsize,ysize);
		//Create viewer for viewing 3d geometries, and register frame.

		viewer = new PvViewer(null, frame);
		viewer.setEmbedded(true);

		/*
		frame.addWindowListener(new WindowAdapter() {
		                                public void windowClosed(WindowEvent e) {
		                                        e.getWindow().dispose();
		                                }
		                        });
		frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
		*/

		PvDisplayIf disp = viewer.getDisplay();
		//PvDisplayIf.PAINT_BACKGROUND=0;
		disp.setBackgroundColor(this.background);
		//disp.setBndBoxColor(background);
		//frame.setBackground(background);
		frame.add((Component)disp, BorderLayout.CENTER);
		frame.setTitle("HJavaView");

		String license=dir+"rsrc"+jehep.ui.SetEnv.fSep+"jv-lic.lic";

		File tempFile = new File(license);
		boolean exists = tempFile.exists();

		if (!exists){


                        Thread t = new Thread(new ThreadSleep5());
                        t.start();

			String mess="<html><body>Missing license <br>"+license+"<br>Obtain it from http://www.javaview.de/</body></html>";
			dialog = new JDialog(frame,false); // Sets its owner but makes it non-modal
			JButton b = new JButton ("OK");
			b.addActionListener ( new ActionListener()
			                      {
				                      public void actionPerformed( ActionEvent e )
				                      {
                                                              t.stop();
					                      dialog.dispose();
				                      }
			                      });


			dialog.setLayout( new FlowLayout() );
			dialog.add( new JLabel(mess)) ;
			dialog.add(b);
			jhplot.utils.Util.centreWithin(frame,dialog);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.pack(); // Packs the dialog so that the JOptionPane can be seen
			//dialog.setVisible(true); // Shows the dialog
		}



	}


	/**
	 * Create canvas for showing mathematical object. Uses white background. 
	 * 
	 * @param exhibitName  
	 *           name of this canvas
	 * @param xsize
	 *            size in x direction
	 * @param ysize
	 *            size in y direction
	 */
	public HJavaView(String exhibitName, int xsize, int ysize) {
		this(exhibitName, xsize, ysize, Color.white);
	}

	/**
	 * Construct a canvas with default parameters. Use white background. 
	 * 
	 */
	public HJavaView() {
		this("Default", 600, 500, Color.white);
	}

	/**
	 * Clear the current graph including graph settings. 
	 */
	public void clear() {
		frame.dispose();
	}


	/**
	 * Draw a mathematical object.
	 * @param geom mathematical object to be shown.
	 */
	public void draw(PgGeometry geom){
		PvDisplayIf disp = viewer.getDisplay();
		// Register geometry in display, and make it active.
		// For more advanced applications it is advisable to create a separate project
		// and register geometries in the project via project.addGeometry(geom) calls.
		disp.addGeometry(geom);
		disp.selectGeometry(geom);
		//disp.update(geom);
	}



         /**
         * Draw array of objects. 
         * 
         * @param obj 
         *            array of 3D objects
         */
        public void draw(PgGeometry[] obj) {

                for (int i = 0; i < obj.length; i++) {
                        draw(obj[i]);
                }

        }



	/**
	* Export graph into an image file. The the image format is given by
	* extension. The following extensions are supported: "gif", "jpg", "png", "ppm", "eps", "ps". 
	* 
	* @param filename
	*            file name
	*/
	public boolean export (String file){

		PvDisplayIf disp = viewer.getDisplay();
		disp.getCanvas().repaint();

		/*
		final PgGeometryIf[] geometries = disp.getGeometries();
		     if (geometries != null) {
		         for (int i = 0; i < geometries.length; ++i) {
		             geometries[i].update(geometries[i]);
		         }
		     }
		*/


		final String fileExtension = PsUtil.getFileExtension(file);
		int n = 61;
		if (fileExtension != null && fileExtension.equalsIgnoreCase("gif")) {
			n = 62;
		}
		else if (fileExtension != null && fileExtension.equalsIgnoreCase("png")) {
			n = 67;
		}
		else if (fileExtension != null && fileExtension.equalsIgnoreCase("jpg")) {
			n = 63;
		}
		else if (fileExtension != null && fileExtension.equalsIgnoreCase("ppm")) {
			n = 65;
		}
		else if (fileExtension != null && fileExtension.equalsIgnoreCase("eps")) {
			n = 69;
		}
		else if (fileExtension != null && fileExtension.equalsIgnoreCase("ps")) {
			n = 70;
		}
		else {
			System.out.println("unknown image file extension: ext = " + fileExtension);
			return false;
		}

		return viewer.export(n, file, xsize, ysize);
	}

	/**
	 * Get the current view.
	 * @return current view.
	 */
	public PvViewer getView(){
		return viewer;
	}

	/**
	* Get the current frame.
	* @return current view.
	*/
	public PsMainFrame getFrame(){
		return frame;
	}


	/**
	 * Set the canvas frame visible or not
	 * 
	 * @param vs
	 *            (boolean) true: visible, false: not visible
	 */

	public void visible(boolean vs) {

		if (vs == false) {
			frame.setVisible(false);
			return;
		}

		// Add display to frame
		frame.pack();
		frame.setSize(xsize,ysize);
		//Position of left upper corner and size of frame when run as application.
		//frame.setBounds(new Rectangle(420, 5, 640, 550));
		frame.setVisible(true);
	}



	/**
	 * Show the canvas
	 */
	public void visible() {
		visible(true);
	}


	/**
	 * Close the canvas (and dispose all components) Note: a memory leak is
	 * found - no time to study it. set to null all the stuff
	 */
	public void close() {
		frame.dispose();

	}



	/**
	 * Show online documentation.
	 */
	public void doc() {

		String a = this.getClass().getName();
		a = a.replace(".", "/") + ".html";
		new HelpBrowser(HelpBrowser.JHPLOT_HTTP + a);

	}


private class ThreadSleep5 implements Runnable {

   Thread t;

   public void run() {
         //System.out.println(Thread.currentThread().getName() + "  " + i);
         try {
            Thread.sleep(7000);
            dialog.setVisible(true); // Shows the dialog
         } catch (Exception e) {
            System.out.println(e);
      }
   }

  }
}

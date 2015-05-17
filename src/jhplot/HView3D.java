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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.*;

import jhplot.gui.GHFrame;
import jhplot.gui.HelpBrowser;
import jhplot.v3d.*;

/**
 * Create a frame for showing 3D objects
 * 
 * @author S.Chekanov
 * 
 */

public class HView3D extends GHFrame {

	private static final long serialVersionUID = 1L;

	public boolean set = true;

	private Canvas3d canv[][];

	private JPanel jpp[][];

	private Model3d md[][];

	private static final Color DEFAULT_BG_COLOR = Color.white;

	private Thread1 m_Close;

	private int xsize, ysize;

	/**
	 * Create a HView3D canvas with several plots showing 3D shapes
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

	public HView3D(String title, int xsize, int ysize, int n1, int n2,
			boolean set) {

		super(title, xsize, ysize, n1, n2, set);

		this.xsize = xsize;
		this.ysize = ysize;

		if (set)
			setGraph();

	}

	/**
	 * Clear all the frames
	 * 
	 */

	protected void clearFrame() {

	}

	/**
	 * Refresh all the frames
	 * 
	 */

	protected void refreshFrame() {

	}

	/**
	 * Open a dialog to write the file
	 * 
	 */

	protected void openWriteDialog() {
		JOptionPane.showMessageDialog(getFrame(), "Not implemented for HGraph");

	}

	/**
	 * Open a dialog to read the file
	 * 
	 */

	protected void openReadDialog() {
		JOptionPane.showMessageDialog(getFrame(), "Not implemented for HGraph");
	}

	/**
	 * Set the canvas frame visible or not
	 * 
	 * @param vs
	 *            (boolean) true: visible, false: not visible
	 */

	public void visible(boolean vs) {
		// updateAll();
		if (vs == false)
			mainFrame.setVisible(false);
		if (vs)
			showIt();
	}

	/**
	 * Set the canvas frame visible
	 * 
	 */

	public void visible() {
		showIt();

	}

	/**
	 * Set visible and insert
	 * 
	 */

	private void showIt() {

		mainFrame.setVisible(true);

		for (int i2 = 0; i2 < N2final; i2++) {
			for (int i1 = 0; i1 < N1final; i1++) {

				Dimension d = jpp[i1][i2].getSize(); // get size of document
				double panelWidth = d.width; // width in pixels
				double panelHeight = d.height; // height in pixels
				// System.out.println(panelWidth);
				md[i1][i2] = new Model3d(null, (int) panelWidth - 1,
						(int) panelHeight - 1);
				canv[i1][i2] = new Canvas3d(md[i1][i2]);
				canv[i1][i2].setColorBack(DEFAULT_BG_COLOR);
				jpp[i1][i2].add(canv[i1][i2]);

			}
		}

	}

	/**
	 * Destroy the canvas frame
	 */
	public void destroy() {
		mainFrame.setVisible(false);
		close();
	}

	/**
	 * Set a graph
	 * 
	 */
	private void setGraph() {

		md = new Model3d[N1final][N2final];
		canv = new Canvas3d[N1final][N2final];
		jpp = new JPanel[N1final][N2final];
		// System.out.println("size="+xsize);
		mainPanel.setLayout(new BorderLayout());

		for (int i2 = 0; i2 < N2final; i2++) {
			for (int i1 = 0; i1 < N1final; i1++) {
				jpp[i1][i2] = new JPanel();
				jpp[i1][i2].setLayout(new BorderLayout());
				jpp[i1][i2].setBorder(BorderFactory.createEmptyBorder());
				mainPanel.add(jpp[i1][i2]);

			}
		}

	}

	/**
	 * Refresh only a particular plot
	 * 
	 * @param n1
	 *            the location of the plot in x
	 * @param n2
	 *            the location of the plot in y
	 */
	public void update(int n1, int n2) {

		canv[n1][n2].repaint();

	}

	/**
	 * Just update the current plot selected using cd() method
	 * 
	 */
	public void update() {
		update(N1, N2);
	}

	/**
	 * Refresh all the plots on the same canvas HPLOT
	 * 
	 */
	public void updateAll() {

		if (N1final == 0 && N2final == 0)
			return;

		for (int i1 = 0; i1 < N1final; i1++) {
			for (int i2 = 0; i2 < N2final; i2++) {
				update(i1, i2);

			}
		}

	}

	/**
	 * Zoom in the current plot.
	 * 
	 * @param zoom
	 *            current zoom. =1 : no zoom, =1.1 : 10% zoom
	 */

	public void setZoomIn(double zoom) {

		if (md[N1][N2].persp) {
			md[N1][N2].minScale /= (float) zoom;
			md[N1][N2].maxScale /= (float) zoom;
			md[N1][N2].computeMatrix();
		} else {
			float f = 1 / (float) zoom;
			md[N1][N2].incScale(f, f, f);
		}
		update(N1, N2);
	}

	/**
	 * Zoom out the current plot.
	 * 
	 * @param zoom
	 *            current zoom. Set to 1 for no zoom. 1.1 means 10% zoom etc.
	 */

	public void setZoomOut(double zoom) {

		if (md[N1][N2].persp) {
			md[N1][N2].minScale *= (float) zoom;
			md[N1][N2].maxScale *= (float) zoom;
			md[N1][N2].computeMatrix();
		} else {
			float f = (float) zoom;
			md[N1][N2].incScale(f, f, f);
		}
		update(N1, N2);
	}

	/**
	 * Move the current plot
	 * 
	 * @param x
	 *            in X
	 * @param y
	 *            in Y
	 * @param z
	 *            in Z
	 */

	public void setMove(double x, double y, double z) {
		md[N1][N2].incTrans((float) x, (float) y, (float) z);
		update(N1, N2);
	}

	/**
	 * Rotate the current plot
	 * 
	 * @param x
	 *            around X
	 * @param y
	 *            around Y
	 * @param z
	 *            around Z
	 */

	public void setRotate(double x, double y, double z) {
		md[N1][N2].incRot((float) x, (float) y, (float) z);
		update(N1, N2);
	}

	/**
	 * Construct a HView3D canvas with a single plot/graph
	 * 
	 * @param title
	 *            Title for the canvas
	 * @param xs
	 *            size in x
	 * @param ys
	 *            size in y
	 */
	public HView3D(String title, int xs, int ys) {

		this(title, xs, ys, 1, 1, true);
		this.xsize = xs;
		this.ysize = ys;

	}

	/**
	 * Construct a HView3D canvas with a single plot/graph
	 * 
	 * @param title
	 *            Title for the canvas
	 * @param xs
	 *            size in x
	 * @param ys
	 *            size in y
	 * @param set
	 *            set or not the graph (boolean)
	 */
	public HView3D(String title, int xs, int ys, boolean set) {

		this(title, xs, ys, 1, 1, set);
		this.xsize = xs;
		this.ysize = ys;

	}

	/**
	 * Construct a HView3D canvas with plots/graphs
	 * 
	 * @param title
	 *            Title for the canvas
	 * @param xs
	 *            size in x
	 * @param ys
	 *            size in y
	 * @param n1
	 *            number of plots/graphs in x
	 * @param n2
	 *            number of plots/graphs in y
	 */
	public HView3D(String title, int xs, int ys, int n1, int n2) {

		this(title, xs, ys, n1, n2, true);
		this.xsize = xs;
		this.ysize = ys;

	}

	/**
	 * Construct a HGraph canvas with a plot with the default parameters 600 by
	 * 400, and 10% space for the global title
	 * 
	 * @param title
	 *            Title
	 */
	public HView3D(String title) {

		this(title, 600, 400, 1, 1, true);
		this.xsize = 600;
		this.ysize = 400;
	}

	/**
	 * Get the current model to build a object.
	 * 
	 * @return current model.
	 */
	public Model3d getModel() {
		return md[N1][N2];
	}

	/**
	 * Draw an object.
	 * 
	 * @param object
	 *            Object to be drawn
	 */
	public void draw(Object3d object) {
		md[N1][N2].addObject(object);
	}

	/**
	 * Construct a HGraph canvas with a plot with the default parameters 600 by
	 * 400, and 10% space for the global title "Default"
	 * 
	 */
	public HView3D() {
		this("Default", 600, 400, 1, 1, true);
	}

	/**
	 * Clear the current graph including graph settings. Note: the current graph
	 * is set by the cd() method
	 */
	public void clear() {
		clear(N1, N2);
	}

	/**
	 * Clear the graph characterized by an index in X and Y. This method cleans
	 * the data and all graph settings.
	 * 
	 * @param i1
	 *            location of the graph in X
	 * @param i2
	 *            location of the graph in Y
	 */

	public void clear(int i1, int i2) {
		setGTitle("");

		System.gc();

	}

	/**
	 * Clear all graphs from data and settings.
	 */

	public void clearAll() {
		for (int i1 = 0; i1 < N1final; i1++) {
			for (int i2 = 0; i2 < N2final; i2++) {
				clear(i1, i2);
			}
		}
		System.gc();
	}

	/**
	 * Close the canvas (and dispose all components) Note: a memory leak is
	 * found - no time to study it. set to null all the stuff
	 */
	public void close() {

		mainFrame.setVisible(false);
		m_Close = new Thread1("Closing softly");
		if (!m_Close.Alive())
			m_Close.Start();

	}

	public void quit() {

		doNotShowFrame();
		clearAll();

		for (int i1 = 0; i1 < N1final; i1++) {
			for (int i2 = 0; i2 < N2final; i2++) {

				// System.out.println("Clear graph="+Integer.toString(i1)+
				// " " + Integer.toString(i2));
				// clear data

				canv[i1][i2] = null;

				md[i1][i2] = null;

			}
		}

		canv = null;
		md = null;

		removeFrame();

	}

	/**
	 * 
	 * @author S.Chekanov Aug 17, 2006 update plot showing centers and seeds
	 */
	class Thread1 implements Runnable {

		private Thread t = null;

		private String mess;

		Thread1(String s1) {
			mess = s1;

		}

		public boolean Alive() {
			boolean tt = false;
			if (t != null) {
				if (t.isAlive())
					tt = true;
			}
			return tt;
		}

		public boolean Joint() {
			boolean tt = false;
			try {
				t.join();
				return true; // finished

			} catch (InterruptedException e) {
				// Thread was interrupted
			}
			return tt;
		}

		public void Start() {
			t = new Thread(this, mess);
			t.start();

		}

		public void Stop() {
			t = null;
		}

		public void run() {
			quit();
		}
	}

	/**
	 * Close the frame from menu
	 */
	protected void quitFrame() {
		close();

	}

	@Override
	protected void showHelp() {
		
		JOptionPane.showMessageDialog(getFrame(), "Not implemented for HGraph");
		// TODO Auto-generated method stub
		
	}


	protected void openReadDataDialog() {
        JOptionPane.showMessageDialog(getFrame(), "Not implemented for this canvas");
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

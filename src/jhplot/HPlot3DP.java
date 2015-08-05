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

import java.awt.*;
import javax.swing.*;

import jhplot.gui.GHFrame;
import jhplot.gui.HelpBrowser;
import jhplot.utils.HelpDialog;

import jplot3dp.*;

/**
 * Create a frame for showing 3D parametric functions.
 * 
 * @author S.Chekanov
 * 
 */

public class HPlot3DP extends GHFrame {

	private static final long serialVersionUID = 1L;

	public boolean set = true;

	private MainComponent jpp[][];

	// private static final Color DEFAULT_BG_COLOR = Color.white;

	private Thread1 m_Close;

	private int xsize, ysize;

	final private String help_file = "hplot3ds";

	/**
	 * Create a canvas with several plots showing 3D function
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

	public HPlot3DP(String title, int xsize, int ysize, int n1, int n2,
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
		// JOptionPane.showMessageDialog(getFrame(),
		// "Not implemented for HGraph");
		jpp[N1][N2].saveToFile(false);
	}

	/**
	 * Open a dialog to read the file
	 * 
	 */

	protected void openReadDialog() {
		// JOptionPane.showMessageDialog(getFrame(),
		// "Not implemented for HGraph");
		jpp[N1][N2].loadFromFile();
	}

	/**
	 * Set the canvas frame visible or not
	 * 
	 * @param vs
	 *            (boolean) true: visible, false: not visible
	 */

	public void visible(boolean vs) {
		// updateAll();
		if (vs == false) {
			mainFrame.setVisible(false);
			mainFrame.validate();
		}
		if (vs)
			showIt();
	}

	/**
	 * Set the canvas frame visible. Also set its location.
	 * 
	 * @param posX
	 *            - the x-coordinate of the new location's top-left corner in
	 *            the parent's coordinate space;
	 * @param posY
	 *            - he y-coordinate of the new location's top-left corner in the
	 *            parent's coordinate space
	 */
	public void visible(int posX, int posY) {
		updateAll();
		mainFrame.setLocation(posX, posY);
		mainFrame.setVisible(true);
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

		updateAll();
		mainFrame.setVisible(true);

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

		jpp = new MainComponent[N1final][N2final];
		// mainPanel.setLayout(new BorderLayout());

		for (int i2 = 0; i2 < N2final; i2++) {
			for (int i1 = 0; i1 < N1final; i1++) {

				jpp[i1][i2] = new MainComponent(getFrame());
				mainPanel.add(jpp[i1][i2]);

			}
		}

	}

	/**
	 * Resize the current pad. It calculates the original pad sizes, and then
	 * scale it by a given factor. In this case, the pad sizes can be different.
	 * 
	 * @param widthScale
	 *            scale factor applied to the width of the current pad
	 * @param heightScale
	 *            scale factor applied the height of the current pad.
	 */

	public void resizePad(double widthScale, double heightScale) {

		Dimension dim = jpp[N1][N2].getSize();
		double h = dim.getHeight();
		double w = dim.getWidth();
		jpp[N1][N2].setPreferredSize(new Dimension((int) (w * widthScale),
				(int) (h * heightScale)));
		jpp[N1][N2].setMinimumSize(new Dimension((int) (w * widthScale),
				(int) (h * heightScale)));
		jpp[N1][N2].setSize(new Dimension((int) (w * widthScale),
				(int) (h * heightScale)));
		mainPanel.updateUI();
	}

	/**
	 * Refresh only a particular plot
	 * 
	 * @param n1
	 *            the location of the plot in x
	 * @param n2
	 *            the location of the plot in y
	 */
	private void update(int n1, int n2) {

		jpp[n1][n2].update();

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
	 * Construct a canvas with a single plot/graph
	 * 
	 * @param title
	 *            Title for the canvas
	 * @param xs
	 *            size in x
	 * @param ys
	 *            size in y
	 */
	public HPlot3DP(String title, int xs, int ys) {

		this(title, xs, ys, 1, 1, true);
		this.xsize = xs;
		this.ysize = ys;

	}

	/**
	 * Construct a canvas with plots/graphs
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

	public HPlot3DP(String title, int xs, int ys, int n1, int n2) {

		this(title, xs, ys, n1, n2, true);
		this.xsize = xs;
		this.ysize = ys;

	}

	/**
	 * Construct a canvas with a plot with the default parameters 600 by 400,
	 * and 10% space for the global title
	 * 
	 * @param title
	 *            Title
	 */
	public HPlot3DP(String title) {

		this(title, 600, 400, 1, 1, true);
		this.xsize = 600;
		this.ysize = 400;
	}

	/**
	 * Construct a canvas with a plot with the default parameters 600 by 400,
	 * and 10% space for the global title "Default"
	 * 
	 */
	public HPlot3DP() {
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
	 * Draw a parametric function.
	 * 
	 * @param h
	 */
	public void draw(FPR h) {

		if (h.getLabelX() != null)
			if (h.getLabelX().length() > 0)
				setNameX(h.getLabelX());
		if (h.getLabelY() != null)
			if (h.getLabelY().length() > 0)
				setNameY(h.getLabelY());

		jpp[N1][N2].setFunction(h);

	}



        /**
         * Draw array of functions
         * 
         * @param f
         *            array of functions
         */

        public void draw(FPR[] f) {

                for (int i = 0; i < f.length; i++) {
                        draw(f[i]);
                }

        }









	/**
	 * Set range for X axis
	 * 
	 * @param min
	 *            min value
	 * @param max
	 *            max value
	 */
	public void setRangeX(double min, double max) {
		jpp[N1][N2].getModel().setRangeX(min, max);

	}

	/**
	 * Set range for Y
	 * 
	 * @param min
	 *            min value
	 * @param max
	 *            max value
	 */
	public void setRangeY(double min, double max) {
		jpp[N1][N2].getModel().setRangeY(min, max);
	}

	/**
	 * Set range for Z
	 * 
	 * @param min
	 *            min value
	 * @param max
	 *            max value
	 */
	public void setRangeZ(double min, double max) {
		jpp[N1][N2].getModel().setRangeZ(min, max);
	}

	/**
	 * Set field of vision
	 * 
	 * @param factor
	 *            factor
	 */
	public void setFov(double factor) {
		jpp[N1][N2].setFov(factor);
	}

	/**
	 * Get field of vision
	 * 
	 * @return factor
	 */
	public double getFov() {
		return jpp[N1][N2].getFov();
	}

	/**
	 * Set background for current pad
	 * 
	 * @param c
	 *            color
	 */

	public void setBackgColor(Color c) {
		jpp[N1][N2].setBackgroundFrame(c);
		// setMarginBackground(c);

	}

	/**
	 * Set position oy eye
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setEyePosition(double x, double y, double z) {

		jpp[N1][N2].getModel().setEyePosition(x, y, z);
	}

	/**
	 * Set or not arrows for axis
	 * 
	 * @param showArrows
	 *            true if shown
	 */
	public void setAxisArrows(boolean showArrows) {
		jpp[N1][N2].getModel().setAxisArrows(showArrows);
	}

	/**
	 * Get a vector with eye position
	 * 
	 * @return array with positions
	 */

	public double[] getEyePosition() {

		return jpp[N1][N2].getModel().getEyePosition();

	}

	/**
	 * Get camera position. It is 0 by deafult. Negative value means sooming
	 * out, positive is zooming in.
	 * 
	 * @return camera position. Position: zooming in
	 */
	public double getCameraPosition() {

		return jpp[N1][N2].getModel().getCameraPosition();
	}

	/**
	 * Set camera position
	 * 
	 * @param d
	 *            set camera position
	 */
	public void setCameraPosition(double d) {

		jpp[N1][N2].getModel().cameraForward(d);
	}

	/**
	 * Set fog for objects
	 * 
	 * @param fogEnabled
	 */

	public void setFog(boolean fogEnabled) {
		jpp[N1][N2].setFog(fogEnabled);
	}

	/**
	 * Set all axes or not
	 * 
	 * @param bShowAxes
	 *            true if shown
	 */
	public void setAxes(boolean bShowAxes) {
		jpp[N1][N2].getModel().setShowAxes(bShowAxes, bShowAxes, bShowAxes);
	}

	/**
	 * Set a name for X axis
	 * 
	 * @param name
	 *            Name of the label for X
	 */
	public void setNameX(String name) {
		jpp[N1][N2].getModel().setNameX(name);
	}

	/**
	 * Set a name for Y axis
	 * 
	 * @param name
	 *            Name of the label for Y
	 */
	public void setNameY(String name) {
		jpp[N1][N2].getModel().setNameY(name);
	}

	/**
	 * Set a name for Z axis
	 * 
	 * @param name
	 *            Name of the label for Y
	 */
	public void setNameZ(String name) {
		jpp[N1][N2].getModel().setNameZ(name);
	}

	/**
	 * Set Font for the labels
	 * 
	 * @param font
	 *            Font
	 */
	public void setLabelFont(Font font) {
		jpp[N1][N2].getModel().setLabelFont(font);
	}

	/**
	 * Set Color for the labels
	 * 
	 * @param color
	 *            Color
	 */
	public void setLabelColor(Color color) {
		jpp[N1][N2].getModel().setLabelColor(color);
	}

	/**
	 * Set Color for axes
	 * 
	 * @param color
	 *            Color
	 */
	public void setAxesColor(Color c) {
		jpp[N1][N2].getModel().setAxesColor(c);
	}

	/**
	 * Set Font for axis numbers
	 * 
	 * @param font
	 *            Font
	 */
	public void setFontValue(Font font) {
		jpp[N1][N2].getModel().setValueFont(font);
	}

	/**
	 * Show or not axes for X, Y, Z
	 * 
	 * @param axisX
	 * @param axisY
	 * @param axisZ
	 */
	public void setAxes(boolean axisX, boolean axisY, boolean axisZ) {
		jpp[N1][N2].getModel().setShowAxes(axisX, axisY, axisZ);
	}

	/**
	 * Show or not small arrows on axes
	 * 
	 * @param showArrows
	 *            set true if arrows are shown (default)
	 */
	public void setAxesArrows(boolean showArrows) {
		jpp[N1][N2].getModel().setAxisArrows(showArrows);
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

				jpp[i1][i2] = null;

			}
		}

		jpp = null;

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

	/**
	 * Show online documentation.
	 */
	public void doc() {

		String a = this.getClass().getName();
		a = a.replace(".", "/") + ".html";
		new HelpBrowser(HelpBrowser.JHPLOT_HTTP + a);

	}

	protected void openReadDataDialog() {
		JOptionPane.showMessageDialog(getFrame(),
				"Not implemented for this canvas");
	}

	@Override
	protected void showHelp() {
		new HelpDialog(getFrame(), help_file + ".html");

	}

}

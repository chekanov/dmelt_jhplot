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
import jhplot.io.*;
import jhplot.utils.HelpDialog;
import jplot3d.SurfaceModelCanvas;
import jplot3d.JSurfacePanel;
import jplot3d.SurfaceModel.PlotType;

/**
 * Create a interactive canvas to show objects in 3D. Use it for showing 2D
 * histograms (H2D class), functions (F2D), data points (P2D class) or more
 * complicated surfaces (P3D class).
 * 
 * @author S.Chekanov
 * 
 */

public class HPlot3D extends GHFrame {

	private static final long serialVersionUID = 1L;

	public boolean set = true;

	private JSurfacePanel jpp[][];
	private SurfaceModelCanvas sp[][];
	protected static int Nframe = 0;

	private Thread1 m_Close;
	protected static int isOpen = 0;
	private boolean changeStyleCalled = false;

	final private String help_file = "hplot3d";

	/**
	 * Create interactive 3D canvas to display data.
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
	 *            number of plots in y
	 */

	public HPlot3D(String title, int xsize, int ysize, int n1, int n2,
			boolean set) {

		super(title, xsize, ysize, n1, n2, set);

		jpp = new JSurfacePanel[N1final][N2final];
		sp = new SurfaceModelCanvas[N1final][N2final];
		Nframe = 0;
		changeStyleCalled = false;

		int Ntot = N1final * N2final;
		for (int i2 = 0; i2 < N2final; i2++) {
			for (int i1 = 0; i1 < N1final; i1++) {
				jpp[i1][i2] = new JSurfacePanel();
				sp[i1][i2] = jpp[i1][i2].createDefaultSurfaceModel();
				// jpp[i1][i2].setLayout(new BorderLayout());
				jpp[i1][i2].setSurface(sp[i1][i2]);
				if (set)
					mainPanel.add(jpp[i1][i2]);

				// change size
				float r = sp[i1][i2].get2DScaling();
				if (Ntot > 1 && Ntot < 3)
					r = r * 0.9f;
				if (Ntot > 2 && Ntot < 5)
					r = r * 0.8f;
				if (Ntot > 4 && Ntot < 9)
					r = r * 0.6f;
				if (Ntot > 8)
					r = r * 0.5f;
				sp[i1][i2].set2DScaling(r);
				sp[i1][i2].getColorModel().setBoxColor(Color.white);// box is
																	// white
				Nframe++;

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
	}

	/**
	 * Resize the pad given by the inxX and indxY. It calculates the original
	 * pad sizes, and then scale it by a given factor. In this case, the pad
	 * sizes can be different.
	 * 
	 * @param n1
	 *            the location of the plot in x
	 * @param n2
	 *            the location of the plot in y
	 * 
	 * @param widthScale
	 *            scale factor applied to the width of the current pad
	 * @param heightScale
	 *            scale factor applied the height of the current pad.
	 */
	public void resizePad(int n1, int n2, double widthScale, double heightScale) {
		Dimension dim = jpp[n1][n2].getSize();
		double h = dim.getHeight();
		double w = dim.getWidth();
		jpp[n1][n2].setPreferredSize(new Dimension((int) (w * widthScale),
				(int) (h * heightScale)));
		jpp[n1][n2].setMinimumSize(new Dimension((int) (w * widthScale),
				(int) (h * heightScale)));
		jpp[n1][n2].setSize(new Dimension((int) (w * widthScale),
				(int) (h * heightScale)));
	}

	/**
	 * Create a 3D canvas to display data. Set is done by the default.
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

	public HPlot3D(String title, int xsize, int ysize, int n1, int n2) {
		this(title, xsize, ysize, n1, n2, true);

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
	 * Set distance to the object
	 * 
	 * @param distance
	 *            distance
	 */

	public void setDistance(double distance) {
		sp[N1][N2].getProjector().setDistance((float) distance);
	}

	/**
	 * Get the distance
	 * 
	 * @return distance
	 */
	public double getDistance() {
		return sp[N1][N2].getProjector().getDistance();
	}

	/**
	 * Set the scaling
	 * 
	 * @param scale
	 */
	public void setScaling(double scale) {
		sp[N1][N2].getProjector().set2DScaling((float) scale);
	}

	/**
	 * Get the scaling
	 * 
	 * @return
	 */
	public double getScaling() {
		return (double) sp[N1][N2].getProjector().get2DScaling();
	}

	/**
	 * Get the rotation angle
	 * 
	 * @param angle
	 *            angle
	 */
	public void setRotationAngle(double angle) {
		sp[N1][N2].getProjector().setRotationAngle((float) angle);
	}

	/**
	 * Get the rotation angle
	 * 
	 * @return angle
	 */
	public double getRotationAngle() {
		return (double) sp[N1][N2].getProjector().getRotationAngle();
	}

	/**
	 * Set the elevation angle
	 * 
	 * @param angle
	 *            elevation angle
	 */
	public void setElevationAngle(double angle) {
		sp[N1][N2].getProjector().setElevationAngle((float) angle);
	}

	/**
	 * Get the angle
	 * 
	 * @return
	 */
	public double getElevationAngle() {
		return (double) sp[N1][N2].getProjector().getElevationAngle();
	}

	/**
	 * Set color for labels
	 * 
	 * @param fontColorLabel
	 */
	public void setLabelFontColor(Color fontColorLabel) {
		sp[N1][N2].setFontColorLabel(fontColorLabel);
	}

	/**
	 * Open a dialog to write the file
	 * 
	 */

	protected void openWriteDialog() {
		JOptionPane
				.showMessageDialog(getFrame(), "Not implemented for HPlot3D");
		// spp[N1][N2].saveToFile(false);
	}

	/**
	 * Open a dialog to read the file
	 * 
	 */

	protected void openReadDialog() {
		JOptionPane
				.showMessageDialog(getFrame(), "Not implemented for HPlot3D");
		// jpp[N1][N2].loadFromFile();
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
	 * Refresh only a particular plot
	 * 
	 * @param n1
	 *            the location of the plot in x
	 * @param n2
	 *            the location of the plot in y
	 */
	public void update(int n1, int n2) {
		jpp[n1][n2].evaluate();
	}

	/**
	 * Update data on the plot
	 * 
	 * @param n1
	 *            the location of the plot in x
	 * @param n2
	 *            the location of the plot in y
	 */
	public void updateData(int n1, int n2) {
		jpp[n1][n2].evaluate();

	}

	/**
	 * Update data on the plot
	 * 
	 */
	public void updateData() {
		updateData(N1, N2);

	}

	/**
	 * Get offset for labels on X
	 * 
	 * @return axes 0 for X, 1 for Y, 2 for Z
	 */
	public double getLabelOffset(int axes) {
		double tmp = 1.0;
		if (axes == 0)
			return sp[N1][N2].getLabelOffsetX();
		if (axes == 1)
			return sp[N1][N2].getLabelOffsetY();
		if (axes == 2)
			return sp[N1][N2].getLabelOffsetZ();
		return tmp;
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
	 * Construct a 3D canvas with a single plot/graph
	 * 
	 * @param title
	 *            Title for the canvas
	 * @param xs
	 *            size in x
	 * @param ys
	 *            size in y
	 */
	public HPlot3D(String title, int xs, int ys) {

		this(title, xs, ys, 1, 1, true);

	}

	/**
	 * Construct a 3D canvas with a plot with the default parameters 600 by 400,
	 * and 10% space for the global title
	 * 
	 * @param title
	 *            Title
	 */
	public HPlot3D(String title) {

		this(title, 600, 600, 1, 1, true);

	}

	/**
	 * Construct a 3D canvas with a plot with the default parameters 600 by 400,
	 * and 10% space for the global title "Default"
	 * 
	 */
	public HPlot3D() {
		this("Default", 600, 600, 1, 1, true);
	}

	/**
	 * Clear the current graph including graph settings. Note: the current graph
	 * is set by the cd() method
	 */
	public void clear() {
		drawBox();
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

		drawBox();
		// setGTitle("");
		System.gc();

	}

	/**
	 * Set line width to draw axes.
	 * 
	 * @param w
	 *            width of the lines
	 */
	public void setPenWidthAxes(double w) {
		sp[N1][N2].setPenWidth((float) w);
	}

	/**
	 * Gets the pen width to draw axes.
	 * 
	 * @return width of the lines
	 */
	public float getPenWidthAxes() {
		return sp[N1][N2].getPenWidth();
	}

	/**
	 * Set drawiing an empty frame.
	 */
	public void drawBox() {
		jpp[N1][N2].setEmpty();
		update();
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
	 * exit the frame and clear all components
	 */

	public void quit() {

		doNotShowFrame();
		clearAll();

		for (int i1 = 0; i1 < N1final; i1++) {
			for (int i2 = 0; i2 < N2final; i2++) {

				// System.out.println("Clear graph="+Integer.toString(i1)+
				// " " + Integer.toString(i2));
				// clear data

				jpp[i1][i2] = null;
				sp[i1][i2] = null;

			}
		}

		jpp = null;
		sp = null;

		removeFrame();

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
	 * Show the canvas
	 */
	public void visible() {
		visible(true);
	}

	/**
	 * Is the plot will be shown as a surface?
	 * 
	 * @return true, if this is a surface plot
	 */
	public boolean isSurface() {

		boolean tmp = false;
		if (sp[N1][N2].getPlotType() == PlotType.SURFACE)
			tmp = true;
		return tmp;
	}

	/**
	 * Set a name for X axis
	 * 
	 * @param a
	 *            Name of the label for X axis
	 */
	public void setNameX(String a) {
		sp[N1][N2].setXlabel(a);
	}

	/**
	 * Set a name for Y axis
	 * 
	 * @param a
	 *            Name of the label for Y axis
	 */
	public void setNameY(String a) {
		sp[N1][N2].setYlabel(a);
	}

	/**
	 * Set a name for Z axis
	 * 
	 * @param a
	 *            Name of the label for Z axis
	 */
	public void setNameZ(String a) {
		sp[N1][N2].setZlabel(a);
	}

	/**
	 * Set Font for the labels
	 * 
	 * @param a
	 *            Font
	 */
	public void setLabelFont(Font a) {
		sp[N1][N2].setLabelFont(a);

	}

	/**
	 * Sets font for axes
	 * 
	 * @param fontAxis
	 *            font
	 */

	public void setAxisFont(Font fontAxis) {
		sp[N1][N2].setAxisFont(fontAxis);
	}

	/**
	 * Get font for axes
	 * 
	 * @return
	 */
	public Font getAxisFont() {
		return sp[N1][N2].getAxisFont();
	}

	/**
	 * Set color of the label
	 * 
	 * @param a
	 *            Color
	 */
	public void setLabelColor(Color a) {
		sp[N1][N2].setLabelColor(a);
	}

	/**
	 * Set color of the lines to draw histogram bars
	 * 
	 * @param a
	 *            Color
	 */
	public void setColorLines(Color a) {
		sp[N1][N2].getColorModel().setLineBoxColor(a);
	}

	/**
	 * Set color of to fill histogram bars
	 * 
	 * @param a
	 *            Color
	 */
	public void setColorFill(Color a) {
		sp[N1][N2].getColorModel().setFillColor(a);
	}

	/**
	 * Set fill color or not for histogram bars
	 * 
	 * @param fill
	 *            if true, color will be set for the histogram bars
	 */
	public void setFill(boolean fill) {
		// sp[N1][N2].getPlotType().setFill(fill);
	}

	/**
	 * Set font for the ticks
	 * 
	 * @param a
	 *            Font
	 */
	public void setTicFont(Font a) {
		sp[N1][N2].setTicFont(a);
	}

	/**
	 * Display or not X and Y
	 * 
	 * @param a
	 *            true if it should be shown
	 */
	public void setDisplayXY(boolean a) {
		sp[N1][N2].setDisplayXY(a);
	}

	/**
	 * Set number of divisions for surface plots
	 * 
	 * @param v
	 *            number of divisions in X and Y
	 */
	public void setDivisions(int v) {
		sp[N1][N2].setCalcDivisions(v);
	}

	/**
	 * Set number of divisions for contour plots
	 * 
	 * @param v
	 *            number of divisions in X and Y
	 */
	public void setContourLines(int v) {
		sp[N1][N2].setContourLines(v);
	}

	/**
	 * Display grid
	 * 
	 * @param a
	 *            true if grid is displayed
	 */
	public void setDisplayGrids(boolean a) {
		sp[N1][N2].setDisplayGrids(a);
	}

	/**
	 * Display or not title for Z
	 * 
	 * @param a
	 *            true, if it is shown
	 */
	public void setDisplayZ(boolean a) {
		sp[N1][N2].setDisplayZ(a);
	}

	/**
	 * Set contour style
	 */
	public void setContour() {
		setContour(true);
	}

	/**
	 * Set contour style
	 * 
	 * @param a
	 */
	public void setContour(boolean a) {
                changeStyleCalled = true;
		sp[N1][N2].setBarsType(false);
		sp[N1][N2].setContourType(a);
	}

	/**
	 * Set density type of plot
	 * 
	 */
	public void setDensity() {
		setDensity(true);
	}

	/**
	 * Set density plot
	 * 
	 * @param a
	 */
	public void setDensity(boolean a) {
                changeStyleCalled = true;
		sp[N1][N2].setBarsType(false);
		sp[N1][N2].setDensityType(a);
	}

	/**
	 * Set surface type of plot
	 * 
	 */
	public void setSurface() {
		setSurface(true);
	}

	/**
	 * Set surface type of plot
	 * 
	 */
	public void setSurface(boolean a) {
		changeStyleCalled = true;
		sp[N1][N2].setSurfaceType(a);
	}

	/**
	 * Set bar type of plot
	 * 
	 */
	public void setBars() {
		setBars(true);
	}

	/**
	 * Set bar type of plot
	 * 
	 */
	public void setBars(boolean a) {
		changeStyleCalled = true;
		sp[N1][N2].setBarsType(a);
	}

	/**
	 * Set color of the box
	 * 
	 * @param a
	 *            Color
	 */
	public void setBoxColor(Color a) {
		sp[N1][N2].getColorModel().setBoxColor(a);
	}

	/**
	 * Set color of the frame
	 * 
	 * @param a
	 *            Color
	 */
	public void setFrameColor(Color a) {
		sp[N1][N2].getColorModel().setBackgroundColor(a);
	}

	/**
	 * Set boxed frame
	 * 
	 * @param boxed
	 *            true if it is boxed
	 */
	public void setBoxed(boolean boxed) {
		sp[N1][N2].setBoxed(boxed);
	}

	/**
	 * Color for tic labels
	 * 
	 * @return
	 */
	public Color getAxesFontColor() {
		return sp[N1][N2].getAxesFontColor();
	}

	/**
	 * Get color for tic labels
	 * 
	 * @param fontColorAxes
	 *            color
	 */
	public void setAxesFontColor(Color fontColorAxes) {
		sp[N1][N2].setAxesFontColor(fontColorAxes);
	}

	/**
	 * Get color of the frame
	 * 
	 * @return a Color
	 */
	public Color getFrameColor() {
		return sp[N1][N2].getColorModel().getBackgroundColor();
	}

	/**
	 * Sets the color mode
	 * 
	 * @param a
	 *            color modes:
	 *            <p>
	 *            0: WIREFRAME <br>
	 *            1: HIDDEN <br>
	 *            2: SPECTRUM IN COLOR <br>
	 *            3: GRAYSCALE <br>
	 *            4: DUALSHADES <br>
	 *            5: FOG <br>
	 *            6: MESH <br>
	 */
	public void setColorMode(int a) {

		if (a == 0)
			sp[N1][N2].setWireframeType(true);
		if (a == 1)
			sp[N1][N2].setHiddenMode(true);
		if (a == 2)
			sp[N1][N2].setSpectrumMode(true);
		if (a == 3)
			sp[N1][N2].setGrayScaleMode(true);
		if (a == 4)
			sp[N1][N2].setDualShadeMode(true);
		if (a == 5)
			sp[N1][N2].setFogMode(true);
		if (a == 6)
			sp[N1][N2].setMesh(true);

	}

	/**
	 * Get current surface
	 * 
	 * @return
	 */

	public SurfaceModelCanvas getSurfaceModel() {

		return sp[N1][N2];
	}

	/**
	 * Get current the color mode
	 * 
	 * @param a
	 *            color modes:
	 *            <p>
	 *            0: WIREFRAME <br>
	 *            1: HIDDEN <br>
	 *            2: SPECTRUM IN COLOR <br>
	 *            3: GRAYSCALE <br>
	 *            4: DUALSHADES <br>
	 *            5: FOG <br>
	 *            6: MESH <br>
	 */

	public int getColorMode() {

		int tmp = -1;
		PlotType i = sp[N1][N2].getPlotType();
		if (i == PlotType.WIREFRAME)
			tmp = 0;
		if (sp[N1][N2].isHiddenMode())
			tmp = 1;
		if (sp[N1][N2].isSpectrumMode())
			tmp = 2;
		if (sp[N1][N2].isGrayScaleMode())
			tmp = 3;
		if (sp[N1][N2].isDualShadeMode())
			tmp = 4;
		if (sp[N1][N2].isFogMode())
			tmp = 5;
		if (sp[N1][N2].isMesh())
			tmp = 6;

		return tmp;

	}

	/**
	 * Get label color
	 * 
	 * @return
	 */
	public Color getLabelColor() {
		return sp[N1][N2].getLabelColor();

	}

	/**
	 * Update the graphics
	 * 
	 */
	public void update() {
		jpp[N1][N2].evaluate();
	}

	/**
	 * Set tick offset
	 * 
	 * @param a
	 *            tick offset
	 */
	public void setTicOffset(double a) {
		sp[N1][N2].setTicOffset((float) a);
	}

	/**
	 * Set label offset on X
	 * 
	 * @param a
	 *            label offset
	 */
	public void setLabelOffsetX(double a) {
		sp[N1][N2].setLabelOffsetX((float) a);
	}

	/**
	 * Set label offset on Y
	 * 
	 * @param a
	 *            label offset
	 */
	public void setLabelOffsetY(double a) {
		sp[N1][N2].setLabelOffsetY((float) a);
	}

	/**
	 * Set label offset on Z
	 * 
	 * @param a
	 *            label offset
	 */
	public void setLabelOffsetZ(double a) {
		sp[N1][N2].setLabelOffsetZ((float) a);
	}

	/**
	 * Set X and Y range for the 3D plot. Z is set to autorange.
	 * 
	 * @param Xmin
	 *            Min in X
	 * @param Xmax
	 *            Max in X
	 * @param Ymin
	 *            Min in Y
	 * @param Ymax
	 *            Max in Y
	 */
	public void setRange(double Xmin, double Xmax, double Ymin, double Ymax) {
		sp[N1][N2].setXMin((float) Xmin);
		sp[N1][N2].setXMax((float) Xmax);
		sp[N1][N2].setYMin((float) Ymin);
		sp[N1][N2].setYMax((float) Ymax);
		sp[N1][N2].setAutoScaleXY(false);
	}

	/**
	 * Set X, Y, Z ranges for the 3D plot
	 * 
	 * @param Xmin
	 *            Min in X
	 * @param Xmax
	 *            Max in X
	 * @param Ymin
	 *            Min in Y
	 * @param Ymax
	 *            Max in Y
	 * @param Zmin
	 *            Min in Z
	 * @param Zmax
	 *            Max in Z
	 */
	public void setRange(double Xmin, double Xmax, double Ymin, double Ymax,
			double Zmin, double Zmax) {

		sp[N1][N2].setXMin((float) Xmin);
		sp[N1][N2].setXMax((float) Xmax);
		sp[N1][N2].setYMin((float) Ymin);
		sp[N1][N2].setYMax((float) Ymax);
		sp[N1][N2].setZMin((float) Zmin);
		sp[N1][N2].setZMax((float) Zmax);
		setAutoRange(false);

	}

	/**
	 * Sets the z range of calculated surface vertices. The ranges will not
	 * affect surface appearance. They affect axes scale appearance.
	 * 
	 * @param Zmin
	 *            the minimum z
	 * @param Zmax
	 *            the maximum z
	 */

	public void setRangeZ(double Zmin, double Zmax) {
		sp[N1][N2].setZMin((float) Zmin);
		sp[N1][N2].setZMax((float) Zmax);
		sp[N1][N2].setAutoScaleZ(false);
	}

	/**
	 * Draw H2D histogram. By default, we shows it as bars. Call setSurface() to
	 * show it as a surface.
	 * 
	 * @param f1
	 *            H2D histogram
	 */
	public void draw(H2D f1) {

		if (f1.getLabelX() != null)
			if (f1.getLabelX().length() > 0)
				setNameX(f1.getLabelX());
		if (f1.getLabelY() != null)
			if (f1.getLabelY().length() > 0)
				setNameY(f1.getLabelY());
		if (f1.getLabelZ() != null)
			if (f1.getLabelZ().length() > 0)
				setNameZ(f1.getLabelZ());

	        if (changeStyleCalled == false)
			setBars(true);
		jpp[N1][N2].setH2D(f1);
		update();

	}


         /**
         * Draw array of histograms 
         * 
         * @param d
         *            array of histograms
         */

        public void draw(H2D[] d) {

                for (int i = 0; i < d.length; i++) {
                        draw(d[i]);
                }

        }

	/**
	 * Display P2D data holder with X,Y,Z values in 3D. If setSurface applied,
	 * it will draw the surface.
	 * 
	 * @param h1
	 *            P2D data holder
	 */
	public void draw(P2D h1) {

		if (h1.getLabelX() != null)
			if (h1.getLabelX().length() > 0)
				setNameX(h1.getLabelX());
		if (h1.getLabelY() != null)
			if (h1.getLabelY().length() > 0)
				setNameY(h1.getLabelY());
		if (h1.getLabelZ() != null)
			if (h1.getLabelZ().length() > 0)
				setNameZ(h1.getLabelZ());

		jpp[N1][N2].setP2D(h1);
		update();

	}


        /**
         * Draw array of P2D holders
         * 
         * @param d
         *            array of P2D data holders
         */

        public void draw(P2D[] d) {

                for (int i = 0; i < d.length; i++) {
                        draw(d[i]);
                }

        }

	/**
	 * Set autorange in X,Y,Z.
	 * 
	 * @param b
	 *            if true, sets autorange
	 */
	public void setAutoRange(boolean b) {
		sp[N1][N2].setAutoScaleZ(b);
		sp[N1][N2].setAutoScaleXY(b);
	}

	/**
	 * Set to autorange all pads
	 * 
	 * @param b
	 */
	public void setAutoRangeAll(boolean b) {
		for (int i1 = 0; i1 < N1final; i1++) {
			for (int i2 = 0; i2 < N2final; i2++) {
				sp[i1][i2].setAutoScaleZ(b);
				sp[i1][i2].setAutoScaleXY(b);
			}
		}
	}

	/**
	 * Set autorange in X,Y,Z at the same time for the current plot
	 * 
	 */
	public void setAutoRange() {
		setAutoRange(true);
	}

	/**
	 * Set autorange in Y,Z at the same time for the current plot
	 * 
	 */
	public void setAutoRangeXY() {
		sp[N1][N2].setAutoScaleXY(true);
	}

	/**
	 * Display P3D data holder with X,Y,Z values in 3D as surface. If setSurface
	 * applied, it will draw the surface
	 * 
	 * @param h
	 *            P3D data holder.
	 */
	public void draw(P3D h) {

		jpp[N1][N2].setP3D(h);
		update();
	}

         /**
         * Draw array of P3D holders
         * 
         * @param d
         *            array of P3D data holders
         */

        public void draw(P3D[] d) {

                for (int i = 0; i < d.length; i++) {
                        draw(d[i]);
                }

        }

	/**
	 * Plot two H2D histograms on the same plot. When only one histogram is
	 * plotted, i.e. h2=null, then the bar option is used. If two histograms are
	 * plotted, the surface option is used (in this case the numbers of bins in
	 * X and Y should be the same.
	 * 
	 * @param h1
	 *            first H2D histogram
	 * @param h2
	 *            second H2D histogram
	 */
	public void draw(H2D h1, H2D h2) {

		jpp[N1][N2].setH2D(h1);
		jpp[N1][N2].setH2D(h2);
		update();

	}

	/**
	 * Draw F2D function as a surface.
	 * 
	 * @param f1
	 *            F2D function
	 */
	public void draw(F2D f1) {

		if (f1.getLabelX() != null)
			if (f1.getLabelX().length() > 0)
				setNameX(f1.getLabelX());
		if (f1.getLabelY() != null)
			if (f1.getLabelY().length() > 0)
				setNameY(f1.getLabelY());
		if (f1.getLabelZ() != null)
			if (f1.getLabelZ().length() > 0)
				setNameZ(f1.getLabelZ());

		jpp[N1][N2].setF2D(f1);
		update();

	}

	/**
	 * Draw two F2D functions on the same plot. The bar option is not supported,
	 * i.e. the functions will be shown by surface.
	 * 
	 * @param f1
	 *            first F2D function
	 * @param f2
	 *            second F2D function
	 */
	public void draw(F2D f1, F2D f2) {
		jpp[N1][N2].setF2D(f1);
		jpp[N1][N2].setF2D(f2);
		update();

	}

	/**
	 * Draw H2D histogram and F2D function on the same plot. Note: the bar
	 * option for histogram is not supported. In addition, the number of bins in
	 * X and Y should be the same.
	 * 
	 * @param h1
	 *            H2D histogram
	 * @param h2
	 *            F2D function
	 */
	public void draw(H2D h1, F2D h2) {

		if (h1.getLabelX() != null)
			if (h1.getLabelX().length() > 0)
				setNameX(h1.getLabelX());
		if (h1.getLabelY() != null)
			if (h1.getLabelY().length() > 0)
				setNameY(h1.getLabelY());
		if (h1.getLabelZ() != null)
			if (h1.getLabelZ().length() > 0)
				setNameZ(h1.getLabelZ());

		jpp[N1][N2].setH2D(h1);
		jpp[N1][N2].setF2D(h2);
		update();

	}

	/**
	 * Draw F2D and H2D on the same plot.
	 * 
	 * @param h2
	 *            F2D function
	 * @param h1
	 *            H2D histogram
	 */
	public void draw(F2D h2, H2D h1) {

		jpp[N1][N2].setH2D(h1);
		jpp[N1][N2].setF2D(h2);
		update();

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
	 * Close the canvas (and dispose all components) Note: a memory leak is
	 * found - no time to study it. set to null all the stuff
	 */
	public void close() {

		mainFrame.setVisible(false);
		m_Close = new Thread1("Closing softly");
		if (!m_Close.Alive())
			m_Close.Start();

	}

	@Override
	protected void showHelp() {

		new HelpDialog(getFrame(), help_file + ".html");

		// TODO Auto-generated method stub

	}

	/**
	 * Show online documentation.
	 */
	public void doc() {

		String a = this.getClass().getName();
		a = a.replace(".", "/") + ".html";
		new HelpBrowser(HelpBrowser.JHPLOT_HTTP + a);

	}

	/**
	 * Open a dialog to read adat file.
	 */
	protected void openReadDataDialog() {

		JFileChooser fileChooser = jhplot.gui.CommonGUI
				.openDataFileChooser(getFrame());
		int ret = fileChooser.showDialog(getFrame(), "Open Data file");
		if (ret == JFileChooser.APPROVE_OPTION) {
			new BrowserData(fileChooser.getSelectedFile().getAbsolutePath(),
					this);
		}

	}

	// end
}

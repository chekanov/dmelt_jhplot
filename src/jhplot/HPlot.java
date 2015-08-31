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

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import jplot.*;

import java.text.DecimalFormat;

import jhplot.gui.GHFrame;
import jhplot.gui.HelpBrowser;
import jhplot.io.*;
import jhplot.shapes.*;
import jhplot.utils.ExtensionFileFilter;
import jhplot.utils.HelpDialog;

// JAIDA 
import hep.aida.*;
import hep.aida.ref.histogram.*;

/**
 * HPlot class to create a canvas with several plots. This class is for a
 * high-performance multi-threaded 2d plot library which produces publication
 * quality plots. It can be used for scatter plot, line chart, staircase chart,
 * linear axis and logarithmic axis, plot can contains subplots. All data types
 * of DataMelt are supported.
 * 
 * @author S.Chekanov
 * 
 */

public class HPlot extends GHFrame implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String lf = System.getProperty("file.separator");

	private static int IndexPlot = 0;

	protected GraphGeneral[][] graph;

	protected GraphSettings[][] gs;

	protected StyleChooser[][] sc;

	protected JFrame[][] frames;

	protected JPlot[][] jp;

	protected int[][] plotType;

	protected int[][] hkeyCounter; // counts Hkeys for each pad

	protected Vector<DataArray>[][] data;

	private IAnalysisFactory m_IAnalysisFactory = null;

	private IHistogramFactory m_IHistogramFactory = null;

	private ITree m_ITree = null;

	private IFitFactory m_IFitFactory = null;

	private IFunctionFactory m_IFunctionFactory = null;

	private Thread1 m_Close;

	final private String help_file = "hplot";

	protected static int isOpen = 0;

	/**
	 * Create HPlot canvas with several plots.
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

	public HPlot(String title, int xsize, int ysize, int n1, int n2, boolean set) {

		super(title, xsize, ysize, n1, n2, set);

		if (set)
			setGraph();

	}

	/**
	 * Clear all frames
	 * 
	 */

	protected void clearFrame() {

		final JFrame frm = getFrame();
		int res = JOptionPane
				.showConfirmDialog(
						frm,
						"This will clear HPlot frame. Data and settings will be lost. Continue?",
						"", JOptionPane.YES_NO_OPTION);
		if (res == JOptionPane.NO_OPTION)
			return;

		String mess = "clear jHPlot frame";
		JHPlot.showStatusBarText(mess);
		Thread t = new Thread(mess) {
			public void run() {
				removeGraph();
			};
		};
		t.start();

	}

	/**
	 * Refresh all the frames
	 * 
	 */

	public void refreshFrame() {

		String mess = "refresh  jHPlot frame";
		JHPlot.showStatusBarText(mess);
		Thread t = new Thread(mess) {
			public void run() {
				updateAll();
			};
		};
		t.start();

	}

	/**
	 * Open a dialog to read the file
	 * 
	 */
	protected void openReadDialog() {

		JFileChooser fileChooser = new JFileChooser(".");
		javax.swing.filechooser.FileFilter filter1 = new ExtensionFileFilter(
				"(*.jhp) HPlot graphs", new String[] { "jhp" });
		fileChooser.setFileFilter(filter1);
		int status = fileChooser.showOpenDialog(null);
		if (status == JFileChooser.APPROVE_OPTION) {

			String mess = "reading JHplot XML file";
			JHPlot.showStatusBarText(mess);

			final File f = fileChooser.getSelectedFile();
			// read the file
			Thread t = new Thread(mess) {
				public void run() {
					boolean res = readScript(f);
				};
			};
			t.start();

		} else if (status == JFileChooser.CANCEL_OPTION) {
			return;
		}

	}

	/**
	 * Read the script without thread.
	 * 
	 * @return true if no problems
	 * 
	 */

	protected boolean readScript(File f) {
		boolean res = JHreader.readScript(f, this);
		return res;
	}

	/**
	 * Write the script without thread.
	 * 
	 * @return true if no problems
	 * 
	 */

	protected boolean writeScript(File f) {
		boolean res = JHwriter.writeScript(f, this);
		return res;
	}

	/**
	 * Read HPlot file and inserts graphs to the main frame.
	 * 
	 * @param file
	 *            input file
	 * 
	 */

	public void read(final String file) {
		// read the file
		Thread t = new Thread("reading XML file ") {
			public void run() {
				boolean res = readScript(new File(file));
			};
		};
		t.start();
		// System.out.println("write dialog");
	}

	/**
	 * Write HPlot file and inserts graphs to the main frame.
	 * 
	 * @param file
	 *            ouput file
	 * 
	 */

	public void write(final String file) {
		// read the file
		Thread t = new Thread("writing XML file ") {
			public void run() {
				boolean res = writeScript(new File(file));
			};
		};
		t.start();
		// System.out.println("write dialog");
	}

	/**
	 * Create HPlot canvas with several plots.
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
	 * 
	 */

	public HPlot(String title, int xsize, int ysize, int n1, int n2) {

		super(title, xsize, ysize, n1, n2, true);
		setGraph();

	}

	/**
	 * Set the canvas frame visible or not
	 * 
	 * @param vs
	 *            (boolean) true: visible, false: not visible
	 */

	public void visible(boolean vs) {
		updateAll();
		mainFrame.setVisible(vs);
		if (vs == false)
			mainFrame.validate();

	}

	/**
	 * Set the canvas frame visible
	 * 
	 */

	public void visible() {
		updateAll();
		mainFrame.setVisible(true);

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
	 * Remove the canvas frame
	 */
	public void distroy() {
		mainFrame.setVisible(false);
		close();
		removeFrame();
	}

	/**
	 * Set the current graph visible or not
	 * 
	 * @param vs
	 *            (boolean) true: visible, false: not visible
	 */

	public void showGraph(boolean vs) {
		jp[N1][N2].showGraph(vs);
	}

	/**
	 * Set the all graphs visible or not
	 * 
	 * @param vs
	 *            (boolean) true: visible, false: not visible
	 */

	public void showAllGraph(boolean vs) {

		for (int i1 = 0; i1 < N1final; i1++) {
			for (int i2 = 0; i2 < N2final; i2++) {
				jp[i1][i2].showGraph(vs);

			}
		}

	}

	/**
	 * Create various JAIDA factories: IAnalysisFactory, ITree, IFitFactory,
	 * IFunctionFactory
	 * 
	 */
	public void factories() {

		m_IAnalysisFactory = IAnalysisFactory.create();
		m_ITree = m_IAnalysisFactory.createTreeFactory().create();
		m_IHistogramFactory = m_IAnalysisFactory
				.createHistogramFactory(m_ITree);
		m_IFitFactory = m_IAnalysisFactory.createFitFactory();
		m_IFunctionFactory = m_IAnalysisFactory.createFunctionFactory(m_ITree);

	}

	/**
	 * Return IAnalysisFactory associated with the plot
	 * 
	 * @return IAnalysisFactory
	 */
	public IAnalysisFactory analF() {
		return m_IAnalysisFactory;
	}

	/**
	 * Return TreeFactory associated with the plot
	 * 
	 * @return ITree
	 */
	public ITree treeF() {
		return m_ITree;
	}

	/**
	 * Return FitFactory associated with the plot
	 * 
	 * @return IFitFactory
	 */
	public IFitFactory fitF() {
		return m_IFitFactory;
	}

	/**
	 * Return FunctionFactory associated with the plot
	 * 
	 * @return IFunctionFactory
	 */
	public IFunctionFactory funcF() {
		return m_IFunctionFactory;
	}

	/**
	 * Return IHistogramFactory associated with the plot
	 * 
	 * @return IHistogramFactory
	 */
	public IHistogramFactory histF() {
		return m_IHistogramFactory;
	}

	/**
	 * Set the graph from JPLot
	 * 
	 */
	public void setGraph() {

		gs = new GraphSettings[N1final][N2final];
		jp = new JPlot[N1final][N2final];
		graph = new GraphXY[N1final][N2final];
		frames = new JFrame[N1final][N2final];
		data = new Vector[N1final][N2final];
		sc = new StyleChooser[N1final][N2final];
		plotType = new int[N1final][N2final];
		hkeyCounter = new int[N1final][N2final];
		// System.out.println(N1final);
		// System.out.println(N2final);

		// this order necessary for grid layout
		for (int i2 = 0; i2 < N2final; i2++) {
			for (int i1 = 0; i1 < N1final; i1++) {

				frames[i1][i2] = new JFrame();
				jp[i1][i2] = new JPlot(frames[i1][i2], null, null, false);
				frames[i1][i2].getContentPane().add(jp[i1][i2]);
				frames[i1][i2].setTitle("Editor for Canvas ("
						+ Integer.toString(i1 + 1) + ","
						+ Integer.toString(i2 + 1) + ")");
				frames[i1][i2].setSize(400, 400);
				frames[i1][i2].setVisible(false);
				hkeyCounter[i1][i2] = 0;
				JMenuBar bar1 = new JMenuBar();
				JMenu menu1 = new JMenu("Exit");
				JMenuItem item1 = new JMenuItem(new NotShowAction());
				menu1.add(item1);
				JMenuItem item2 = new JMenuItem(new RefreshAction());
				menu1.add(item2);

				bar1.add(menu1);
				frames[i1][i2].setJMenuBar(bar1);

				sc[i1][i2] = jp[i1][i2].getStyleChooser();
				gs[i1][i2] = jp[i1][i2].getGraphSettings();
				plotType[i1][i2] = GraphSettings.GRAPHTYPE_2D;

				gs[i1][i2].setRange(0, 0.0, 1.0);
				gs[i1][i2].setRange(1, 0.0, 1.0);
				gs[i1][i2].setAutoRange(0, false);
				gs[i1][i2].setAutoRange(1, false);
				gs[i1][i2].setDrawGrid(0, false);
				gs[i1][i2].setDrawGrid(1, false);
				gs[i1][i2].setLeftMargin(50);
				Font fleg = new Font("Arial", Font.BOLD, 14);
				gs[i1][i2].setLegendFont(fleg);

				data[i1][i2] = new Vector<DataArray>();

				graph[i1][i2] = jp[i1][i2].getGraph();

				// add action
				final int kk1 = i1;
				final int kk2 = i2;

				graph[i1][i2].m_edit
						.addActionListener(new java.awt.event.ActionListener() {
							public void actionPerformed(
									java.awt.event.ActionEvent evt) {
								frames[kk1][kk2].setVisible(true);
								N1edit = kk1;
								N2edit = kk2;
							}
						});

				mainPanel.add(graph[i1][i2]);
				update(i1, i2);
			}

		}

	}

	/**
	 * Construct a HPlot canvas with a single plot.
	 * 
	 * @param title
	 *            Title for the canvas
	 * @param xs
	 *            size in x
	 * @param ys
	 *            size in y
	 * 
	 */
	public HPlot(String title, int xs, int ys) {

		this(title, xs, ys, 1, 1, true);

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
		Dimension dim = graph[N1][N2].getSize();
		double h = dim.getHeight();
		double w = dim.getWidth();
		graph[N1][N2].setPreferredSize(new Dimension((int) (w * widthScale),
				(int) (h * heightScale)));
		graph[N1][N2].setMinimumSize(new Dimension((int) (w * widthScale),
				(int) (h * heightScale)));
		graph[N1][N2].setSize(new Dimension((int) (w * widthScale),
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
		Dimension dim = graph[n1][n2].getSize();
		double h = dim.getHeight();
		double w = dim.getWidth();
		graph[n1][n2].setPreferredSize(new Dimension((int) (w * widthScale),
				(int) (h * heightScale)));
		graph[n1][n2].setMinimumSize(new Dimension((int) (w * widthScale),
				(int) (h * heightScale)));
		graph[n1][n2].setSize(new Dimension((int) (w * widthScale),
				(int) (h * heightScale)));
	}

	/**
	 * Construct a HPlot canvas with a single plot.
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
	public HPlot(String title, int xs, int ys, boolean set) {

		this(title, xs, ys, 1, 1, set);

	}

	/**
	 * Construct a HPlot canvas with a plot with the size 600x400.
	 * 
	 * @param title
	 *            Title
	 */
	public HPlot(String title) {

		this(title, 600, 400, 1, 1, true);

	}

	/**
	 * Construct a HPlot canvas with a single plot with the size 600x400.
	 * 
	 */
	public HPlot() {
		this("Default", 600, 400, 1, 1, true);
	}

	/**
	 * Refresh all the plots on the same canvas HPLOT
	 * 
	 */
	public void updateAll() {

		if (N1final == 0 && N2final == 0)
			return;
		if (jp == null)
			return;

		for (int i1 = 0; i1 < N1final; i1++) {
			for (int i2 = 0; i2 < N2final; i2++) {

				graph[i1][i2].show(data[i1][i2]);
				jp[i1][i2].updateGraphIfShowing();

			}
		}

	}

	/**
	 * Refresh only a particular pad.
	 * 
	 * @param n1
	 *            the location of the plot in x
	 * @param n2
	 *            the location of the plot in y
	 */
	public void update(int n1, int n2) {

		graph[n1][n2].show(data[n1][n2]);
		jp[n1][n2].updateGraphIfShowing();
	}

	/**
	 * Just update the current plot selected using cd() method
	 * 
	 */
	public void update() {
		update(N1, N2);
	}

	/**
	 * Update the graph if it is showing. It applies for the current graph only
	 * 
	 */

	public void updateGraphIfShowing() {

		jp[N1][N2].updateGraphIfShowing();
	}

	/**
	 * If true, then this suppresses all negative values in Y, i.e. the plot
	 * starts with 0 in Y. This is useful if you want to show histograms
	 * 
	 * @param what
	 *            if true, sets a histogram view
	 */
	public void viewHisto(boolean what) {

		for (int i1 = 0; i1 < N1final; i1++) {
			for (int i2 = 0; i2 < N2final; i2++) {
				gs[i1][i2].set2DType(0);
				if (what)
					gs[i1][i2].set2DType(1);
				// graph[i1][i2].show(data[i1][i2]);
			}
		}

	}

	/**
	 * Print the current graph settings to System.out
	 */

	public void printGraphSettings() {

		System.out.println("printGraphSettings");
		System.out.println("N1=" + Integer.toString(N1) + "  N2="
				+ Integer.toString(N2));
		PrintStream out = System.out;
		gs[N1][N2].print(out);

	}

	/**
	 * Set canvas attributes resizable (line widths, symbols, fonts) or not.
	 * Applicable for the current pad only (navigated as cd(N1,N2)). The defaul
	 * is resizable when you resize the canvas.
	 * 
	 * @param resizable
	 *            set true if it should be resizable.
	 */
	public void setAttResizable(boolean resizable) {
		gs[N1][N2].setAttResizable(resizable);
	}

	/**
	 * Set canvas attributes resizable (line widths, symbols, fonts) or not.
	 * Applicable for all pads only. The defaul is resizable when you resize the
	 * canvas.
	 * 
	 * @param resizable
	 *            set true if it should be resizable.
	 */
	public void setAttResizableAll(boolean resizable) {
		for (int i1 = 0; i1 < N1final; i1++) {
			for (int i2 = 0; i2 < N2final; i2++) {
				gs[i1][i2].setAttResizable(resizable);

			}
		}

	}

	/**
	 * Print the current graph settings to System.out
	 * 
	 * @param out
	 *            input print stream
	 */

	public void printGraphSettings(PrintStream out) {
		System.out.println("printGraphSettings");
		System.out.println("N1=" + Integer.toString(N1) + "  N2="
				+ Integer.toString(N2));
		gs[N1][N2].print(out);

	}

	/**
	 * Set to a contour style
	 * 
	 * @param contour
	 *            set to true for a contour style
	 */
	public void setContour(boolean contour) {

		plotType[N1][N2] = GraphSettings.GRAPHTYPE_2D;
		gs[N1][N2] = jp[N1][N2].getGraphSettings();
		Dimension panel = gs[N1][N2].getPanelSize();

		if (contour) {
			plotType[N1][N2] = GraphSettings.CONTOUR_2D;
			gs[N1][N2].setRightMargin((int) (panel.width * 0.17));
			gs[N1][N2].setContour_bar(true);
			gs[N1][N2].setContour_levels(10);
			gs[N1][N2].setContour_binsX(40);
			gs[N1][N2].setContour_binsY(40);
			gs[N1][N2].setContour_gray(false);
			gs[N1][N2].setDrawLegend(false);

		} else {
			// SetEnv.setPlotType(GraphSettings.GRAPHTYPE_2D);
			gs[N1][N2].setGraphType(GraphSettings.GRAPHTYPE_2D);
			gs[N1][N2].setRightMargin((int) (panel.width * 0.1));
		}

		jp[N1][N2].setGraphSettings(gs[N1][N2]);

	}

	/**
	 * Show or not a bar with color levels
	 * 
	 * @param bar
	 *            true if the bar is shown
	 */
	public void setContourBar(boolean bar) {

		Dimension panel = gs[N1][N2].getPanelSize();
		gs[N1][N2].setRightMargin((int) (panel.width * 0.1));
		if (bar) {
			gs[N1][N2].setRightMargin((int) (panel.width * 0.2));
		}
		gs[N1][N2].setContour_bar(bar);

	}

	/**
	 * How many color levels shuul be shown (10 default)
	 * 
	 * @param levels
	 *            number of color levels
	 */
	public void setContourLevels(int levels) {
		gs[N1][N2].setContour_levels(levels);
	}

	/**
	 * How many bins used to slice the data in X (and Y)
	 * 
	 * @param binsX
	 *            number of bins in X
	 * @param binsY
	 *            number of bins in Y
	 */
	public void setContourBins(int binsX, int binsY) {
		gs[N1][N2].setContour_binsX(binsX);
		gs[N1][N2].setContour_binsY(binsY);
	}

	/**
	 * Color style to show contour plot. The default is color style.
	 * 
	 * @param gray
	 *            set to true to show in black-white
	 */
	public void setContourGray(boolean gray) {
		gs[N1][N2].setContour_gray(gray);
	}

	/**
	 * Set antialiasing for the graphics of the current plot
	 * 
	 * @param setit
	 *            true if antialiasing is set
	 */
	public void setAntiAlias(boolean setit) {
		gs[N1][N2].setAntiAlias(setit);
	}

	/**
	 * Get antialiasing for the graphics of the current plot
	 * 
	 * @return true if antialiasing is set
	 */
	public boolean getAntiAlias() {
		return gs[N1][N2].getAntiAlias();
	}

	/**
	 * Set antialiasing for the graphics of all plots
	 * 
	 * @param setit
	 *            true if antialiasing is set
	 */
	public void setAntiAliasAll(boolean setit) {

		for (int i1 = 0; i1 < N1final; i1++) {
			for (int i2 = 0; i2 < N2final; i2++) {
				gs[i1][i2].setAntiAlias(setit);

			}
		}
	}

	/**
	 * Set the label for the axis in X
	 * 
	 * @param s
	 *            label title
	 * @param f
	 *            Font
	 * @param c
	 *            Color
	 */
	public void setNameX(String s, Font f, Color c) {
		GraphLabel label = new GraphLabel(GraphLabel.XLABEL, s, f, c);
		gs[N1][N2].addLabel(label);
	}

	/**
	 * Sets the actual font of the legend.
	 * 
	 * @see #getLegendFont()
	 * @param font
	 *            new font to draw the legend.
	 */
	public void setLegendFont(Font font) {
		gs[N1][N2].setLegendFont(font);
	}

	/**
	 * Set the label font, which is used for axis labels and legend labels. The
	 * font names understood are those understood by java.awt.Font.decode().
	 * 
	 * @param name
	 *            A font name.
	 */
	public void setLegendFont(String name) {

		gs[N1][N2].setLegendFont(Font.decode(name));
	}

	/**
	 * Sets the distance between the left-border of the panel and the left
	 * Y-axis.
	 * 
	 * @see #getMarginLeft()
	 * @param lm
	 *            the left margin in points.
	 */
	public void setMarginLeft(double lm) {
		gs[N1][N2].setLeftMargin(lm);
	}

	/**
	 * Returns the distance between the left-border of the panel and the left
	 * Y-axis.
	 * 
	 * @return the left margin.
	 */
	public double getMarginLeft() {
		return gs[N1][N2].getLeftMargin();
	}

	/**
	 * Returns the dimensions of the panel used to display the graph.
	 * 
	 * @return the panel dimension of the graph
	 */
	public Dimension getPanelSize() {
		return gs[N1][N2].getPanelSize();
	}

	/**
	 * Sets the dimensions of the panel of the graph.
	 * 
	 * @param d
	 *            Size of the graph panel
	 */
	public void setPanelSize(Dimension d) {
		gs[N1][N2].setPanelSize(d);
	}

	/**
	 * Returns the distance between the right-border of the panel and the right
	 * Y-axis.
	 * 
	 * @return the right margin.
	 */
	public double getMarginRight() {
		return gs[N1][N2].getRightMargin();
	}

	/**
	 * Sets the distance between the right-border of the panel and the right
	 * Y-axis.
	 * 
	 * @param rm
	 *            the right margin in points.
	 */
	public void setMarginRight(double rm) {
		gs[N1][N2].setRightMargin(rm);
	}

	/**
	 * Get pen width to draw axis
	 * 
	 * @return the right margin.
	 */
	public double getPenWidthAxis() {
		return (double) gs[N1][N2].getPenWidthAxis();
	}

	/**
	 * Set pen width to draw axis
	 */
	public void setPenWidthAxis(double width) {
		gs[N1][N2].setPenWidthAxis((float) width);
	}

	/**
	 * Returns the distance between the bottom-border of the panel and the
	 * bottom X-axis.
	 * 
	 * @return the bottom margin.
	 */
	public double getMarginBottom() {
		return gs[N1][N2].getBottomMargin();
	}

	/**
	 * Sets the distance between the bottom-border of the panel and the bottom
	 * X-axis.
	 * 
	 * @param bm
	 *            the bottom margin in points.
	 */
	public void setMarginBottom(double bm) {
		gs[N1][N2].setBottomMargin(bm);
	}

	/**
	 * Returns the distance between the top-border of the panel and the top
	 * X-axis.
	 * 
	 * @return the top margin.
	 */
	public double getMarginTop() {
		return gs[N1][N2].getTopMargin();
	}

	/**
	 * Sets the distance between the top-border of the panel and the top X-axis.
	 * 
	 * @param tm
	 *            the top margin in points.
	 */
	public void setMarginTop(double tm) {
		gs[N1][N2].setTopMargin(tm);
	}

	/**
	 * Returns the length of the ticks.
	 * 
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>.
	 * @return the length of the tics.
	 */
	public double getTicLength(int axis) {
		return gs[N1][N2].getTicLength(axis);

	}

	/**
	 * Returns the length of the sub-ticks.
	 * 
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>.
	 * @return the length of the subtics.
	 */
	public double getSubTicLength(int axis) {
		return gs[N1][N2].getSubTicLength(axis);

	}

	/**
	 * Get the number of subticks for a given axis
	 * 
	 * @param axis
	 *            Axis value (0 or 1)
	 */
	public void getSubTicNumber(int axis) {
		gs[N1][N2].getSubTicNumber(axis);
	}

	/**
	 * Sets the length of the ticks. In fact, the actual tick length is the
	 * value you set here multiplied by the axis length. By default, the
	 * tick-length is exactly 0.012 times the axis length. Using a value
	 * proportional to the axes system leads to reasonable proportions even if
	 * the graph is blown up to full screen (which users often do, trust me).
	 * 
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>. axis=0 for X and axis=1 for Y
	 * @param length
	 *            tick length relative to the axis length
	 */
	public void setTicLength(int axis, double length) {
		gs[N1][N2].setTicLength(axis, length);
	}

	/**
	 * Sets the length of the subticks. In fact, the actual subtic length is the
	 * value you set here multiplied by the axis length. By default, the
	 * tic-length is exactly 0.007 times the axis length. Using a value
	 * proportional to the axes system leads to reasonable proportions even if
	 * the graph is blown up to full screen (which users often do, trust me).
	 * 
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>. axis=0 for X and axis=1 for Y
	 * @param length
	 *            subtic length relative to the axis length
	 */
	public void setSubTicLength(int axis, double length) {
		gs[N1][N2].setSubTicLength(axis, length);
	}

	/**
	 * Sets the number of subticks between ticks. For log scale, the default is
	 * 10. If you do not want subtics, set it to 0.
	 * 
	 * @param axis
	 *            Axis (0 for X, 1 for Y)
	 * @param number
	 *            Number of subticks between ticks
	 */
	public void setSubTicNumber(int axis, int number) {
		gs[N1][N2].setSubTicNumber(axis, number);
	}

	/**
	 * Sets the length of the ticks for all axes.
	 * 
	 * @param length
	 *            tick length relative to the axis length
	 */
	public void setTicLength(double length) {
		for (int i1 = 0; i1 < N1final; i1++) {
			for (int i2 = 0; i2 < N2final; i2++) {
				gs[i1][i2].setTicLength(0, length);
				gs[i1][i2].setTicLength(1, length);
			}
		}

	}

	/**
	 * Sets the length of the subticks for all axises.
	 * 
	 * @param length
	 *            subtick length relative to the axis length
	 */
	public void setSubTicLength(double length) {
		for (int i1 = 0; i1 < N1final; i1++) {
			for (int i2 = 0; i2 < N2final; i2++) {
				gs[i1][i2].setSubTicLength(0, length);
				gs[i1][i2].setSubTicLength(1, length);
			}
		}

	}

	/**
	 * Says whether we should use a fixed ratio between X- and Y-axes lengths.
	 * 
	 * @return true if the ratio should be fixed, false otherwise.
	 */
	public boolean getAxesRatioUse() {
		return gs[N1][N2].useAxesRatio();
	}

	/**
	 * Sets whether or not to use a fixed ratio between X- and Y-axes lengths.
	 * By default, this ratio is defined by (read: proportional to) the panel
	 * dimensions.
	 * 
	 * @param b
	 *            true if the ratio is fixed and independent of the panel size.
	 */
	public void setAxesRatioUse(boolean b) {
		gs[N1][N2].setUseAxesRatio(b);
	}

	/**
	 * Returns the current ratio between X- and Y-axes lengths.
	 * 
	 * @return the ratio Y-axisLength/X-axisLength
	 */
	public double getAxesRatio() {
		return gs[N1][N2].getAxesRatio();
	}

	/**
	 * Sets the ratio between X- and Y-axes lengths. This ratio must be greater
	 * than 0.0, a value of 0.0 means that the ratio will be calculated
	 * automatically as a function of the size of the graph panel.
	 * 
	 * @param r
	 *            the ratio Y-axisLength/X-axisLength
	 */
	public void setAxesRatio(double r) {
		gs[N1][N2].setAxesRatio(r);
	}

	/**
	 * Sets axes arrows. Arrows are not drawn on mirror axes. Arrows are only
	 * drawn if one first remove all axes (removeAxes() method) and then use
	 * setAxis() or setAxisY() methods to draw X or Y axis shown by arrows.
	 * 
	 * @param type
	 *            0: no arrows, just lines <br>
	 *            1: not filled arrows <br>
	 *            2: nice filled arrows
	 */
	public void setAxisArrow(int type) {
		gs[N1][N2].setAxesArrow(type);
	}

	/**
	 * Gets axis arrows.
	 * 
	 * @return type 0: no arrows, just lines <br>
	 *         1: not filled arrows <br>
	 *         2: nice filled arrows
	 */
	public int getAxisArrow() {
		return gs[N1][N2].getAxesArrow();
	}

	/**
	 * Says whether the user fixes the number of ticks for a specific axis. If
	 * not fixed, JHPlot will try to calculate a convenient number of ticks.
	 * 
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>. Use 0 for X and 1 for Y.
	 * @param b
	 *            true if you want to fix the number of ticks.
	 */
	public void setNumberOfTicsFixed(int axis, boolean b) {
		gs[N1][N2].setUseNumberOfTics(axis, b);
	}

	/**
	 * Says whether the user fixes the number of ticks for a specific for all
	 * axes.
	 * 
	 * @param b
	 *            true if you want to fix the number of ticks.
	 */
	public void setNumberOfTicsFixed(boolean b) {
		for (int i1 = 0; i1 < N1final; i1++) {
			for (int i2 = 0; i2 < N2final; i2++) {
				gs[i1][i2].setUseNumberOfTics(0, b);
				gs[i1][i2].setUseNumberOfTics(1, b);
			}
		}

	}

	/**
	 * Returns whether or not to use a fixed number of ticks.
	 * 
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>. Use 0 for X and 1 for Y.
	 * @return true if the ratio should be used.
	 */
	public boolean useNumberOfTics(int axis) {
		return gs[N1][N2].useNumberOfTics(axis);
	}

	/**
	 * Returns the number of ticks for the specified axis.
	 * 
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>. Use 0 for X and 1 for Y.
	 * @return the number of ticks for the specified axis.
	 */
	public int getNumberOfTics(int axis) {
		return gs[N1][N2].getNumberOfTics(axis);
	}

	/**
	 * Sets the number of ticks for the specified axis. If not fixed, JHPlot
	 * will try to calculate a convenient number of ticks.
	 * 
	 * @param axis
	 *            Defines to which axis this function applies. Use 0 for X and 1
	 *            for Y.
	 * @param n
	 *            Number of ticks
	 */
	public void setNumberOfTics(int axis, int n) {
		gs[N1][N2].setNumberOfTics(axis, n);
	}

	/**
	 * Returns the maximum number of subticks for log scale (default is 10).
	 * This number applies to all axes, and 10 ticks is right number for you
	 * 
	 * @return Maximum number of subticks allowed for log scale
	 */
	public int getMaxNumberOfSubticsLog() {
		return gs[N1][N2].getMaxNumberOfTics();
	}

	/**
	 * Sets the maximum number of subticks allowed for log10 scale. This number
	 * is set to 10.
	 * 
	 * @param n
	 *            Maximum allowed number of subticks in log10 scale
	 */
	public void setMaxNumberOfSubticsLog(int n) {
		gs[N1][N2].setMaxNumberOfTics(n);
	}

	/**
	 * Returns the minimum data value for the specified axis.
	 * 
	 * @param axis
	 *            Defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>.
	 * @return Minimum-value of the data range.
	 */
	public double getMinValue(int axis) {
		return gs[N1][N2].getMinValue(axis);
	}

	/**
	 * Sets the start-value displayed on the axis.
	 * 
	 * @param axis
	 *            Defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>.
	 * @param min
	 *            Minimum value on the axis
	 */
	public void setMinValue(int axis, double min) {
		gs[N1][N2].setMinValue(axis, min);
	}

	/**
	 * Returns the maximum data value for the specified axis.
	 * 
	 * @param axis
	 *            Defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>.
	 * @return Maximum-value of the data range.
	 */
	public double getMaxValue(int axis) {
		return gs[N1][N2].getMaxValue(axis);
	}

	/**
	 * Sets the maximum data value displayed on the axis.
	 * 
	 * @param axis
	 *            Defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>.
	 * @param max
	 *            Maximum value on the axis
	 */
	public void setMaxValue(int axis, double max) {
		gs[N1][N2].setMaxValue(axis, max);
	}

	/**
	 * Returns the font used by the legend.
	 * 
	 * @return Actual font used by the legend.
	 */

	public Font getLegendFont() {
		return gs[N1][N2].getLegendFont();
	}

	/**
	 * Sets whether or not to draw the legend.
	 * 
	 * @param b
	 *            true if you want to show the legend.
	 */
	public void setLegend(boolean b) {
		gs[N1][N2].setDrawLegend(b);
	}

	/**
	 * Sets the x or y coordinates (defined relative to the data ranges on the
	 * axes) of the legend.
	 * 
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>.
	 * @param coordinate
	 *            the x/y coordinate of the legend.
	 */
	public void setLegendPos(int axis, double coordinate) {
		gs[N1][N2].setLegendPosition(axis, coordinate);
	}

	/**
	 * Sets the x and y coordinates of the legend.
	 * 
	 * 
	 * @param x
	 *            the x coordinate of the legend.
	 * @param y
	 *            the y coordinate of the legend.
	 * @param system
	 *            coordinate system (USER or NDC)
	 */
	public void setLegendPos(double x, double y, String system) {

		Dimension panel = gs[N1][N2].getPanelSize();
		double w = panel.width;
		double h = panel.height;
		double ix = (w * x);
		double iy = (h * (1 - y));

		if (system.equals("USER")) {
			ix = toX(x);
			iy = toY(y);
		}
		gs[N1][N2].setUseLegendPosition(true);
		gs[N1][N2].setLegendPosition((double) ix, (double) iy);
	}

	/**
	 * Returns whether or not we use the legend coordinates. If not using the
	 * coordinates, we let JHPlot define the legend position.
	 * 
	 * @return true if you want to set the legend coordinates
	 */
	public boolean getLegendPosUse() {
		return gs[N1][N2].useLegendPosition();
	}

	/**
	 * Sets whether or not to set the legend position in X,Y coordinates as
	 * defined by the graph. If not using the coordinates, we let JHPlot define
	 * the legend position.
	 * 
	 * @param b
	 *            true if you want to set the position
	 */
	public void setLegendPosUse(boolean b) {
		gs[N1][N2].setUseLegendPosition(b);
	}

	/**
	 * Returns the vertical spacing, between each line of the legend.
	 * 
	 * @return vertical spacing for the legend.
	 */
	public double getLegendSpacing() {
		return gs[N1][N2].getLegendSpacing();
	}

	/**
	 * Sets the vertical spacing, between each line of the legend. The spacing
	 * is relative to the font-size of the legend. This means that the value 1
	 * (default) is a normal spacing, and e.g. 2 is as if you left a blank line
	 * between two legend labels.
	 * 
	 * @param dy
	 *            vertical spacing for the legend.
	 */
	public void setLegendSpac(double dy) {
		gs[N1][N2].setLegendSpacing(dy);
	}

	/**
	 * Returns the x or y coordinates (defined in pixels, relative to the size
	 * of the panel showing the graph) of the legend.
	 * 
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>.
	 * @return the x- or y coordinates of the legend.
	 */
	public double getLegendPos(int axis) {
		return gs[N1][N2].getLegendPosition(axis);
	}

	/**
	 * Sets true or false to plot on a log scale.
	 * 
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>. Usually, 0 means X axis, 1 means Y
	 *            axis.
	 * @param b
	 *            toggle, true if the scaling is logarithmic
	 */
	public void setLogScale(int axis, boolean b) {
		gs[N1][N2].setUseLogScale(axis, b);
	}

	/**
	 * Sets true or false to draw mirror ticks
	 * 
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>. Use 0 for X and 1 for Y.
	 * @param b
	 *            toggle, true if the we should draw mirror ticks
	 */
	public void setTicsMirror(int axis, boolean b) {
		gs[N1][N2].setDrawMirrorTics(axis, b);
	}

	/**
	 * Sets true or false to draw mirror ticks for X and Y
	 * 
	 * @param b
	 *            toggle, true if the we should draw mirror ticks
	 */
	public void setTicsMirror(boolean b) {
		gs[N1][N2].setDrawMirrorTics(0, b);
		gs[N1][N2].setDrawMirrorTics(1, b);

	}

	/**
	 * Returns whether or not we should draw ticks on the mirror axis.
	 * 
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>. Use 0 for X and 1 for Y.
	 * @return true if the mirror ticks should be drawn
	 */
	public boolean getTicsMirror(int axis) {
		return gs[N1][N2].drawMirrorTics(axis);
	}

	/**
	 * Defines whether we should rotate ticks. By default the are pointing to
	 * the inside of the graph: setting it to false will draw them towards the
	 * outside.
	 * 
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>. Use 0 for X and 1 for Y.
	 * @param b
	 *            toggle, true if the we should rotate the ticks
	 */
	public void setTicsRotate(int axis, boolean b) {
		gs[N1][N2].setRotateTics(axis, b);
	}

	/**
	 * Returns whether or not we should rotate the ticks. Rotated ticks are
	 * drawn at the outer-side of the axes.
	 * 
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>. Use 0 for X and 1 for Y.
	 * @return true if the ticks will be rotated.
	 */
	public boolean getTicsRotate(int axis) {
		return gs[N1][N2].rotateTics(axis);
	}

	/**
	 * Defines whether or not a shadow will be drawn at the panel border.
	 * 
	 * @param b
	 *            toggle, true if the shadow should be drawn.
	 */
	public void setShadow(boolean b) {
		gs[N1][N2].setShadow(b);
	}

	/**
	 * Returns whether or not a shadow will be drawn at the panel border. Only
	 * nice if applied to a graph panel which is wrapped inside another panel,
	 * hence enabled in JHPlot.
	 * 
	 * @return true if the shadow should be drawn.
	 */
	public boolean getShadow() {
		return gs[N1][N2].drawShadow();
	}

	/**
	 * Sets whether or not using grid lines. Grid lines are lines drawn from
	 * tick to tick. They can be enabled/disabled per axis.
	 * 
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>.
	 * @param b
	 *            toggle, true if the the grid should be drawn.
	 */
	public void setGrid(int axis, boolean b) {
		gs[N1][N2].setDrawGrid(axis, b);
	}

	/**
	 * Sets whether or not using grid lines. Grid lines are lines drawn from
	 * tick to tick. They can be enabled/disabled per axis.
	 * 
	 * @param b
	 *            true if shown
	 */
	public void setGrid(boolean b) {
		gs[N1][N2].setDrawGrid(0, b);
		gs[N1][N2].setDrawGrid(1, b);
	}

	/**
	 * Sets or not the grid lines for all plots on the same canvas
	 * 
	 * @param axis
	 *            Axis (0 means X, 1 means Y)
	 * @param b
	 *            true is grid is shown
	 */

	public void setGridAll(int axis, boolean b) {

		for (int i1 = 0; i1 < N1final; i1++) {
			for (int i2 = 0; i2 < N2final; i2++) {
				gs[i1][i2].setDrawGrid(axis, b);
			}
		}

	}

	/**
	 * Sets color of the grid lines for all plots on the same canvas
	 * 
	 * @param c
	 *            Color
	 */
	public void setGridColorAll(Color c) {

		for (int i1 = 0; i1 < N1final; i1++) {
			for (int i2 = 0; i2 < N2final; i2++) {
				gs[i1][i2].setGridColor(c);
			}
		}

	}

	/**
	 * Sets the color of the overall grid. The same color applies to horizontal
	 * and vertical lines.
	 * 
	 * @return Grid color
	 */
	public Color getGridColor() {
		return gs[N1][N2].getGridColor();
	}

	/**
	 * Returns the color used to fill the shaded triangles.
	 * 
	 * @return Color used to fill the shaded triangles.
	 */
	public Color getInnerColor() {
		return gs[N1][N2].getInnerColor();
	}

	/**
	 * Sets the color used to fill the inner triangles. This is for the very
	 * special piper-diagram graph, one of the fancy possibilities of JHPlot ;-)
	 * 
	 * @param c
	 *            the color used to fill the shaded triangles.
	 */
	public void setInnerColor(Color c) {
		gs[N1][N2].setInnerColor(c);
	}

	/**
	 * Setting the flag to true, this will draw the grid <em>after</em> drawing
	 * all the lines and point stuff, hence on the foreground.
	 * 
	 * @param b
	 *            true if the grid will be moved to the front
	 */
	public void setGridToFront(boolean b) {
		gs[N1][N2].setGridToFront(b);
	}

	/**
	 * Flag, saying whether the grid should be drawn at the foreground or not.
	 * 
	 * @see #setGridToFront(boolean)
	 * @return whether or not to draw the grid at the front.
	 */
	public boolean isGridToFront() {
		return gs[N1][N2].gridToFront();
	}

	/**
	 * Setting the flag to true, this will draw all primitives <em>after</em>
	 * drawing all the lines and point stuff, hence on the foreground.
	 * 
	 * @param b
	 *            true if primitives will be moved to the front
	 */
	public void setPrimitivesToFront(boolean b) {
		gs[N1][N2].setPrimitivesToFront(b);
	}

	/**
	 * Flag, saying whether primitives should be drawn at the foreground or not.
	 * 
	 * @return whether or not to draw the primitives at the front.
	 */
	public boolean isPrimitivesToFront() {
		return gs[N1][N2].primitivesToFront();
	}

	/**
	 * Sets whether or not to draw the bounding box around the graph. Note: this
	 * box can be filled with any color.
	 * 
	 * @param b
	 *            true if the filled triangles should be drawn.
	 */
	public void setBox(boolean b) {
		gs[N1][N2].setDrawBox(b);
	}

	/**
	 * Sets the offset of the bounding box drawn around a graph.
	 * 
	 * @param f
	 *            offset in pixels.
	 */
	public void setBoxOffset(double f) {
		gs[N1][N2].setBoxOffset((float) f);
	}

	/**
	 * Returns the offset of the bounding box.
	 * 
	 * @return the offset of the bounding box.
	 */
	public float getBoxOffset() {
		return gs[N1][N2].getBoxOffset();
	}

	/**
	 * Sets the fill color of the bounding box drawn around a graph.
	 * 
	 * @param c
	 *            Color for the fill
	 */
	public void setBoxFillColor(Color c) {
		gs[N1][N2].setBoxFillColor(c);
	}

	/**
	 * Returns the fill-color of the eventual bounding box arround the graph
	 * 
	 * @return the fill color of the bounding box.
	 */
	public Color getBoxFillColor() {
		return gs[N1][N2].getBoxFillColor();
	}

	/**
	 * Returns the color used to draw the bounding box.
	 * 
	 * @return the color of the bounding box.
	 */
	public Color getBoxColor() {
		return gs[N1][N2].getBoxColor();
	}

	/**
	 * Sets the color of the bounding box drawn around a graph.
	 * 
	 * @param c
	 *            drawing color.
	 */
	public void setBoxColor(Color c) {
		gs[N1][N2].setBoxColor(c);
	}

	/**
	 * Returns the actual background color of the entire graph panel.
	 * 
	 * @return background color of the graph.
	 */
	public Color getBackgColor() {
		return gs[N1][N2].getBackgroundColor();
	}

	/**
	 * Sets the actual background color of the entire graph panel. Note that it
	 * is possible to set the color of the area within the axes system
	 * separately.
	 * 
	 * @param color
	 *            New background color.
	 */
	public void setBackgColor(Color color) {
		gs[N1][N2].setBackgroundColor(color);
		// setMarginBackground(color);
	}

	/**
	 * Sets the background color of the current graph. This is the area between
	 * the axes (hence not the entire panel area).
	 * 
	 * @param c
	 *            color.
	 */
	public void setBackgColorGraph(Color c) {
		gs[N1][N2].setGraphBackgroundColor(c);
	}

	/**
	 * Sets the background color of all graphs. This is the area between the
	 * axes (hence not the entire panel area).
	 * 
	 * @param c
	 *            color.
	 */

	public void setBackgColorForAllGraph(Color c) {

		for (int i1 = 0; i1 < N1final; i1++) {
			for (int i2 = 0; i2 < N2final; i2++) {
				gs[i1][i2].setGraphBackgroundColor(c);
			}
		}

	}

	/**
	 * Returns the background color of the current graph. This is the area
	 * between the axes (hence not the entire panel area).
	 * 
	 * @return the background color of the graph.
	 */
	public Color getBackgColorGraph() {
		return gs[N1][N2].getGraphBackgroundColor();
	}

	/**
	 * Get graph settings
	 * 
	 * @param n1
	 *            position of graph in x
	 * @param n2
	 *            position of graph in y
	 * @return GraphSettings
	 */

	public GraphSettings getGraphSettings(int n1, int n2) {
		return gs[n1][n2];
	}

	/**
	 * Set a new graph settings for the current plot
	 * 
	 * @param ggs
	 *            new graph settings
	 */
	public void setGraphSettings(GraphSettings ggs) {
		gs[N1][N2] = ggs;
		jp[N1][N2].setGraphSettings(ggs);

	}

	/**
	 * Get graph settings for current plot
	 * 
	 * @return GraphSettings
	 */

	public GraphSettings getGraphSettings() {
		return gs[N1][N2];
	}

	/**
	 * Get jplots for each graph
	 * 
	 * @param n1
	 *            position of graph in x
	 * @param n2
	 *            position of graph in y
	 * @return JPlot
	 */

	public JPlot getJPlot(int n1, int n2) {
		return jp[n1][n1];
	}

	/**
	 * Get jplots for each graph for current graph
	 * 
	 * @return JPlot
	 */

	public JPlot getJPlot() {
		return jp[N1][N2];
	}

	/**
	 * Sets the name for X axis. The color is black, the font is ("Arial",
	 * Font.BOLD, 14)
	 * 
	 * @param s
	 *            Title for X axis.
	 */
	public void setNameX(String s) {
		setNameX(s, new Font("Arial", Font.BOLD, 16), Color.black);
	}

	/**
	 * Sets the name for X axis. The color is black, the font is ("Arial",
	 * Font.BOLD, 14)
	 * 
	 * @param s
	 *            Title for X axis.
	 * @param f
	 *            Font
	 */
	public void setNameX(String s, Font f) {
		setNameX(s, f, Color.black);
	}

	/**
	 * Sets the position for label on X axis. Specify "USER" or "NDC" Deafalt is
	 * NDC
	 * 
	 * @param x
	 *            X position
	 * @param y
	 *            Y position
	 * @param system
	 *            coordinate system (USER or NDC)
	 */
	public void setNameXpos(double x, double y, String system) {

		Dimension panel = gs[N1][N2].getPanelSize();
		double w = panel.width;
		double h = panel.height;
		int ix = (int) (w * x);
		int iy = (int) (h * (1 - y));

		if (system.equals("USER")) {
			ix = toX(x);
			iy = toY(y);
		}

		Vector labels = gs[N1][N2].getLabels();
		for (Enumeration e = labels.elements(); e.hasMoreElements();) {
			GraphLabel gl = (GraphLabel) e.nextElement();
			if (gl.getID() == GraphLabel.XLABEL) {
				gl.setLocation(ix, iy);
				gl.setUsePosition(true);
				break;
			}
		}

	}

	/**
	 * Sets the position for label on Y axis. Specify "USER" or "NDC" Deafalt is
	 * NDC
	 * 
	 * @param x
	 *            X position
	 * @param y
	 *            Y position
	 * @param system
	 *            coordinate system (USER or NDC)
	 */
	public void setNameYpos(double x, double y, String system) {

		Dimension panel = gs[N1][N2].getPanelSize();
		double w = panel.width;
		double h = panel.height;
		int ix = (int) (w * x);
		int iy = (int) (h * (1 - y));

		if (system.equals("USER")) {
			ix = toX(x);
			iy = toY(y);
		}

		Vector labels = gs[N1][N2].getLabels();
		for (Enumeration e = labels.elements(); e.hasMoreElements();) {
			GraphLabel gl = (GraphLabel) e.nextElement();
			if (gl.getID() == GraphLabel.YLABEL) {
				gl.setLocation(ix, iy);
				gl.setUsePosition(true);
				break;
			}
		}

	}

	/**
	 * Sets the position for label on X axis in NDC system.
	 * 
	 * @param x
	 *            X position
	 * @param y
	 *            Y position
	 */
	public void setNameXpos(double x, double y) {
		setNameXpos(x, y, "NDC");
	}

	/**
	 * Sets the position for label on Y axis in NDC system.
	 * 
	 * @param x
	 *            X position
	 * @param y
	 *            Y position
	 */
	public void setNameYpos(double x, double y) {
		setNameYpos(x, y, "NDC");
	}

	/**
	 * Sets the name for Y axis. The color is black, the font is ("Arial",
	 * Font.BOLD, 14)
	 * 
	 * @param s
	 *            Title for Y axis.
	 */
	public void setNameY(String s) {
		setNameY(s, new Font("Arial", Font.BOLD, 16), Color.black);
	}

	/**
	 * Sets the name for Y axis. The color is black, the font is ("Arial",
	 * Font.BOLD, 14)
	 * 
	 * @param s
	 *            Title for Y axis.
	 * @param f
	 *            Font
	 */
	public void setNameY(String s, Font f) {
		setNameY(s, f, Color.black);
	}

	/**
	 * Sets the Title for Y-axis
	 * 
	 * @param s
	 *            Label name
	 * @param f
	 *            Font
	 * @param c
	 *            Color
	 */
	public void setNameY(String s, Font f, Color c) {
		GraphLabel label = new GraphLabel(GraphLabel.YLABEL, s, f, c);
		label.setRotation(Math.PI * 1.5);
		gs[N1][N2].addLabel(label);
	}

	/**
	 * Sets a title for the current plot. The default color is black, the font
	 * is ("Arial", Font.BOLD, 18),
	 * 
	 * @param name
	 *            Title
	 */
	public void setName(String name) {

		GraphLabel label = new GraphLabel(GraphLabel.TITLE, name, new Font(
				"Arial", Font.BOLD, 18), Color.black);
		gs[N1][N2].addLabel(label);
	}

	/**
	 * Sets a title for the current plot with all attributes
	 * 
	 * @param s
	 *            Title
	 * @param f
	 *            Font
	 * @param c
	 *            Color
	 */

	public void setName(String s, Font f, Color c) {
		GraphLabel label = new GraphLabel(GraphLabel.TITLE, s, f, c);
		gs[N1][N2].addLabel(label);
	}

	/**
	 * Sets a title for the current plot with all attributes
	 * 
	 * @param s
	 *            Title
	 * @param f
	 *            Font
	 */

	public void setName(String s, Font f) {
		setName(s, f, Color.black);
	}

	/**
	 * Sets the position for the title of the current plot. Specify "USER" or
	 * "NDC" Default is NDC
	 * 
	 * @param x
	 *            X position
	 * @param y
	 *            Y position
	 * @param system
	 *            coordinate system (USER or NDC)
	 */
	public void setNamePos(double x, double y, String system) {

		Dimension panel = gs[N1][N2].getPanelSize();
		double w = panel.width;
		double h = panel.height;
		int ix = (int) (w * x);
		int iy = (int) (h * (1 - y));

		if (system.equals("USER")) {
			ix = toX(x);
			iy = toY(y);
		}

		Vector labels = gs[N1][N2].getLabels();
		for (Enumeration e = labels.elements(); e.hasMoreElements();) {
			GraphLabel gl = (GraphLabel) e.nextElement();
			if (gl.getID() == GraphLabel.TITLE) {
				gl.setLocation(ix, iy);
				gl.setUsePosition(true);
				break;
			}
		}

	}

	/**
	 * Sets the position for the title of the current plot. Specify "USER" or
	 * "NDC" Default is NDC
	 * 
	 * @param x
	 *            X position
	 * @param y
	 *            Y position
	 */
	public void setNamePos(double x, double y) {
		setNamePos(x, y, "NDC");
	}

	/**
	 * remove HLabels and HMLabels on the current plot
	 * 
	 */
	public void removeLabels() {
		gs[N1][N2].removeLabels();
	}

	/**
	 * Remove X and Y axes, ticks, axis labels on the current plot.
	 * 
	 */
	public void removeAxes() {
		setAxisAll(false);

		setTics(0, false);
		setTics(1, false);

		setTicLabels(0, false);
		setTicLabels(1, false);

		setAxisMirror(0, false);
		setAxisMirror(1, false);

		setTicsMirror(0, false);
		setTicsMirror(1, false);

	}

	/**
	 * remove i-th HLabel or HMLabel on the current plot
	 * 
	 * @param i
	 *            index of the label to br removed
	 */
	public void removeLabel(int i) {
		gs[N1][N2].removeLabel(i);
	}

	/**
	 * No labels on the current plot.
	 */
	public int numberLabels() {
		return gs[N1][N2].numberLabels();
	}

	/**
	 * remove all Labels on all plots.
	 * 
	 */
	public void removeLabelsAll() {

		for (int i1 = 0; i1 < N1final; i1++) {
			for (int i2 = 0; i2 < N2final; i2++) {
				gs[i1][i2].removeLabels();
			}
		}

	}

	/**
	 * remove 2D java primitives on the current plot
	 * 
	 */
	public void removePrimitives() {
		gs[N1][N2].removePrimitives();
	}

	/**
	 * remove i-th 2D java primitives on the current plot
	 * 
	 * @param i
	 *            index of the primitive to be removed
	 */
	public void removePrimitives(int i) {
		gs[N1][N2].removePrimitive(i);
	}

	/**
	 * No of primitives for the current plot.
	 */
	public int numberPrimitives() {
		return gs[N1][N2].numberPrimitives();
	}

	/**
	 * remove all 2D java primitives
	 * 
	 */
	public void removePrimitivesAll() {

		for (int i1 = 0; i1 < N1final; i1++) {
			for (int i2 = 0; i2 < N2final; i2++) {
				gs[i1][i2].removePrimitives();
			}
		}

	}

	/**
	 * Length of X axis in pixels.
	 * 
	 * @return Length of X axis
	 **/
	public double axisLengthX() {
		Dimension panel = gs[N1][N2].getPanelSize();
		double tmp = panel.width - gs[N1][N2].getLeftMargin()
				- gs[N1][N2].getRightMargin();
		// System.out.println(tmp);
		return tmp;
	}

	/**
	 * Length of Y axis in pixels.
	 * 
	 * @return Length of Y axis
	 **/
	public double axisLengthY() {
		Dimension panel = gs[N1][N2].getPanelSize();
		double tmp = panel.height - gs[N1][N2].getTopMargin()
				- gs[N1][N2].getBottomMargin();
		return tmp;
	}

	/**
	 * Convert the user coordinate X to the pixel coordinate
	 * 
	 * @param x
	 *            user coordiinate X for conversion
	 **/
	public int toX(double x) {
		double d;
		if (gs[N1][N2].useLogScale(0))
			d = Math.log10(x / gs[N1][N2].getMinValue(0));
		else
			d = x - gs[N1][N2].getMinValue(0);

		double min = gs[N1][N2].getMinValue(0);
		double max = gs[N1][N2].getMaxValue(0);

		if (gs[N1][N2].useLogScale(0)) {
			min = Math.log10(min);
			max = Math.log10(max);
		}

		// if (gs[N1][N2].useLogScale(0)){
		// min = Math.pow(10, min);
		// max = Math.pow(10, max);
		// }

		double diff = Math.abs(min - max);
		double inv = (min < max) ? 1.0 : -1.0;
		int tmp = (int) (gs[N1][N2].getLeftMargin() + inv * d * axisLengthX()
				/ diff);
		// int tmp= (int) (gs[N1][N2].getLeftMargin()+getMarginSizeLeft() +
		// getMarginSizeRight() + inv * d * axisLengthX() / diff);
		// int tmp= (int) (inv * d * axisLengthX() / diff);

		return tmp;
	}

	/**
	 * Convert the user coordinate Y to the pixel coordinate
	 * 
	 * @param y
	 *            user coordiinate Y for conversion
	 **/
	public int toY(double y) {

		double d;
		if (gs[N1][N2].useLogScale(1))
			d = Math.log10(y / gs[N1][N2].getMinValue(1));
		else
			d = y - gs[N1][N2].getMinValue(1);

		double min = gs[N1][N2].getMinValue(1);
		double max = gs[N1][N2].getMaxValue(1);
		// if (gs[N1][N2].useLogScale(1)){
		// min = Math.pow(10, min);
		// max = Math.pow(10, max);
		// }
		if (gs[N1][N2].useLogScale(1)) {
			min = Math.log10(min);
			max = Math.log10(max);
		}

		double diff = Math.abs(min - max);
		double inv = (min < max) ? 1.0 : -1.0;

		// System.out.println(min);
		// System.out.println(max);
		// System.out.println(diff);

		int tmp = (int) (gs[N1][N2].getTopMargin() + axisLengthY()
				* (1.0 - inv * d / diff));

		// System.out.println(tmp);

		// double tt=(getMarginSizeTop()+getMarginSizeBottom())* getSizeY();
		// System.out.println(tt);
		// int tmp=(int) (-tt-gs[N1][N2].getTopMargin()+axisLengthY()*(1.0 - inv
		// * d / diff));
		// int tmp=(int) (gs[N1][N2].getTopMargin()+axisLengthY()*(1.0 - inv * d
		// / diff));

		return tmp;

	}

	/**
	 * Add a label to the Canvas. Note: Call update() method to draw it The
	 * label can be added in NDC or USER (axis-dependent) coordinates.
	 * 
	 * @param label
	 *            Label to be added
	 */
	public void add(HLabel label) {

		GraphLabel glabel = label.getGraphLabel();
		glabel.setUsePosition(false);

		// in the user coordinnates
		if (label.getPositionCoordinate() == 2) {
			double x = label.getX();
			double y = label.getY();
			glabel.setLocation(toX(x) + glabel.getTextHeight(),
					toY(y) - glabel.getTextHeight());
			// System.out.println("from JHplot="+label.getText()+" "+Integer.toString(toX(x))+" "+Integer.toString(toY(y))
			// );
			// glabel.setDataLocation(toX(x), toY(y));
			// glabel.setDataLocation(x, y);
			glabel.setUsePosition(true);
			glabel.setUseDataPosition(true);
		}
		// in the NDC coordinnates
		if (label.getPositionCoordinate() == 1) {

			double x = label.getX();
			double y = label.getY();

			Dimension panel = gs[N1][N2].getPanelSize();
			double w = panel.width;
			double h = panel.height;
			int ix = (int) (w * x);
			int iy = (int) (h * (1 - y));
			glabel.setLocation(ix + glabel.getTextHeight(),
					iy - glabel.getTextHeight());
			glabel.setUsePosition(true);
			glabel.setUseDataPosition(false);
		}

		gs[N1][N2].addLabel(glabel);
	}

	/**
	 * Add a key to the Canvas. Note: Call update() method to draw it Normally,
	 * unlike Legend, it is not associated to any data set.
	 * 
	 * @param label
	 *            key to be added
	 */
	public void add(HKey label) {

		GraphLabel glabel = label.getGraphLabel();

		glabel.setUsePosition(false);
		double x = label.getX();
		double y = label.getY();

		// in the user coordinnates
		if (label.getPositionCoordinate() == 2) {
			glabel.setLocation(toX(x) + glabel.getTextHeight(),
					toY(y) - glabel.getTextHeight());
			glabel.setUsePosition(true);
		}
		// in the NDC coordinnates
		if (label.getPositionCoordinate() == 1) {

			if (label.isDefaultPosition())
				y = y - label.getSeparation() * hkeyCounter[N1][N2]; // increment
																		// for
																		// next
																		// key

			Dimension panel = gs[N1][N2].getPanelSize();
			double w = panel.width;
			double h = panel.height;
			int ix = (int) (w * x);
			int iy = (int) (h * (1 - y));
			glabel.setLocation(ix + glabel.getTextHeight(),
					iy - glabel.getTextHeight());
			glabel.setUsePosition(true);
		}

		gs[N1][N2].addLabel(glabel);
		hkeyCounter[N1][N2]++;
	}

	/**
	 * Add a multiline label to the Canvas. Note: Call update() method to draw
	 * it
	 * 
	 * @param label
	 *            Label to be added
	 */
	public void add(HMLabel label) {

		GraphLabel glabel = label.getGraphLabel();
		glabel.setUsePosition(false);

		// in the user coordinnates
		if (label.getPositionCoordinate() == 2) {
			double x = label.getX();
			double y = label.getY();
			glabel.setLocation(toX(x) + glabel.getTextHeight(), toY(y));
			glabel.setUsePosition(true);

		}
		// in the NDC coordinnates
		if (label.getPositionCoordinate() == 1) {

			double x = label.getX();
			double y = label.getY();
			Dimension panel = gs[N1][N2].getPanelSize();
			double w = panel.width;
			double h = panel.height;
			int ix = (int) (w * x);
			int iy = (int) (h * (1 - y));
			glabel.setLocation(ix + glabel.getTextHeight(), iy);
			glabel.setUsePosition(true);
		}

		gs[N1][N2].addLabel(glabel);

	}

	/**
	 * Draw a label. Note: it is smilar to add(HLabel), only update() is called
	 * automatically
	 * 
	 * @param label
	 *            Label to be drawn
	 */

	public void draw(HLabel label) {
		add(label);
		update();

	}

	/**
	 * Draw a key. Note: it is smilar to add(HKey), only update() is called
	 * automatically.
	 * 
	 * @param label
	 *            a key to be drawn
	 */

	public void draw(HKey label) {
		add(label);
		update();

	}

	/**
	 * Add a LaTeX equation to the canvas.
	 */
	public void add(HLabelEq ob) {

		Image img = ob.getImage();
		Picture p = new Picture(ob.getX(), ob.getY(), img);
		p.setPositionCoordinate(ob.getPositionCoordinate());
		gs[N1][N2].addPrimitive(p);
	}

	/**
	 * Draw a LaTeX rquation on the Canvas. It is similar to add(HShape), only
	 * update() method is called authomatically
	 */
	public void draw(HLabelEq ob) {
		add(ob);
		update();

	}

	/**
	 * Add a shape primitive to the Canvas. Note: Primitives will be shown after
	 * the update() method
	 */
	public void add(HShape ob) {

		gs[N1][N2].addPrimitive(ob);
	}

	/**
	 * Draw a shape primitive to the Canvas. It is similar to add(HShape), only
	 * update() method is called authomatically
	 */
	public void draw(HShape ob) {
		add(ob);
		update();

	}

	/**
	 * Draw a multiline label. The update() method is called authomatically
	 */
	public void draw(HMLabel ob) {
		add(ob);
		update();

	}

	/**
	 * Returns the actual color of the axes of the graph.
	 * 
	 * @return actual color used to draw the axes.
	 */
	public Color getAxesColor() {
		return gs[N1][N2].getAxesColor();
	}

	/**
	 * Sets the actual color of the axes of the graph.
	 * 
	 * @param color
	 *            new color to draw the axes.
	 */
	public void setAxesColor(Color color) {
		gs[N1][N2].setAxesColor(color);
	}

	/**
	 * Returns the font used by the labels drawn at each tick.
	 * 
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>.
	 * @return actual font used by the tick labels.
	 */
	public Font getTicFont(int axis) {
		return gs[N1][N2].getTicFont(axis);
	}

	/**
	 * Sets the font used by the labels drawn at each tick.
	 * 
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>.
	 * @param font
	 *            the new font
	 */
	public void setTicFont(int axis, Font font) {
		gs[N1][N2].setTicFont(axis, font);
	}

	/**
	 * Sets the font used by the labels drawn at each tick (for all axises).
	 * 
	 * @param font
	 *            the new font
	 */
	public void setTicFont(Font font) {

		for (int i1 = 0; i1 < N1final; i1++) {
			for (int i2 = 0; i2 < N2final; i2++) {
				gs[i1][i2].setTicFont(0, font);
				gs[i1][i2].setTicFont(1, font);
			}
		}

	}

	/**
	 * Returns the color used by the labels drawn at each tick.
	 * 
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>. Use 0 for X and 1 for Y.
	 * @return actual color used by the tick labels.
	 */
	public Color getTicColor(int axis) {
		return gs[N1][N2].getTicColor(axis);
	}

	/**
	 * Sets the color used by the labels drawn at each tick.
	 * 
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>. Use 0 for X and 1 for Y.
	 * @param color
	 *            the new color
	 */
	public void setTicColor(int axis, Color color) {
		gs[N1][N2].setTicColor(axis, color);
	}

	/**
	 * Sets the color used by the labels drawn at each tick (for all axises).
	 * 
	 * @param color
	 *            the new color for each axis.
	 */
	public void setTicColor(Color color) {
		for (int i1 = 0; i1 < N1final; i1++) {
			for (int i2 = 0; i2 < N2final; i2++) {
				gs[i1][i2].setTicColor(0, color);
				gs[i1][i2].setTicColor(1, color);
			}
		}

	}

	/**
	 * Returns whether an axis line will be drawn or not.
	 * 
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>.
	 * @return true if the axis will be drawn.
	 */
	public boolean isAxisShown(int axis) {
		return gs[N1][N2].drawAxis(axis);
	}

	/**
	 * Sets whether an axis line will be drawn or not. This does not affect
	 * ticks and labels.
	 * 
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>.
	 * @param b
	 *            toggle, true if the axis should be drawn.
	 */
	public void setAxis(int axis, boolean b) {
		gs[N1][N2].setDrawAxis(axis, b);
	}

	/**
	 * Sets X-axis (bottom). First remove all axes using removeAxes() method. Do
	 * not draw miror axis, but all tcs and labels will be drawn.
	 */
	public void setAxisX() {
		setAxis(0, true);
		setAxisMirror(0, false);

		setTicLabels(0, true);
		setTicsMirror(0, false);

		setTics(0, true);
		setTicsMirror(0, false);
	}

	/**
	 * Sets Y-axis (left). First remove all axes using removeAxes() method. Do
	 * not draw miror axis, but all tcs and labels will be drawn.
	 */
	public void setAxisY() {
		setAxis(1, true);
		setAxisMirror(1, false);

		setTicLabels(1, true);
		setTicsMirror(1, false);

		setTics(1, true);
		setTicsMirror(1, false);
	}

	/**
	 * Sets whether all axis lines will be drawn or not.
	 * 
	 * @param b
	 *            toggle, true if the axis should be drawn.
	 */
	public void setAxisAll(boolean b) {
		gs[N1][N2].setDrawAxis(0, b);
		gs[N1][N2].setDrawAxis(1, b);

	}

	/**
	 * Sets the pen width to draw tick axes.
	 * 
	 * @param penWidth
	 *            pen width to draw the tick axes lines
	 */
	public void setAxisPenTicWidth(int penWidth) {
		gs[N1][N2].setAxesPenTicWidth((float) penWidth);

	}

	/**
	 * Get the pen width to draw tick axes
	 * 
	 * @return pen width to draw the tick axes lines
	 */
	public int getAxisPenTicWidth() {
		return (int) gs[N1][N2].getAxesPenTicWidth();

	}

	/**
	 * Returns whether the mirror of an axis will be drawn or not.
	 * 
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>.
	 * @return true if the mirror axis will be drawn.
	 */
	public boolean isMirrorAxisShown(int axis) {
		return gs[N1][N2].drawMirrorAxis(axis);
	}

	/**
	 * Sets whether the mirror of an axis will be drawn or not.
	 * 
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>.
	 * @param b
	 *            toggle, true if the mirror axis should be drawn.
	 */
	public void setAxisMirror(int axis, boolean b) {
		gs[N1][N2].setDrawMirrorAxis(axis, b);
	}

	/**
	 * Returns whether or not to draw the tick labels
	 * 
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>.
	 * @return true if tick labels should be drawn.
	 */
	public boolean isTicLabelsShown(int axis) {
		return gs[N1][N2].drawTicLabels(axis);
	}

	/**
	 * Sets whether all tick-labels will be written or not.
	 * 
	 * @param b
	 *            toggle, true if the axis should be drawn.
	 */
	public void setTicLabels(boolean b) {
		gs[N1][N2].setDrawTicLabels(b);
	}

	/**
	 * Sets whether tick-labels will be shown or not.
	 * 
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>.
	 * @param b
	 *            toggle, true if the axis should be drawn.
	 */
	public void setTicLabels(int axis, boolean b) {
		gs[N1][N2].setDrawTicLabels(axis, b);
	}

	/**
	 * Returns whether or not to draw ticks (little lines on the axes).
	 * 
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>.
	 * @return true if ticks should be drawn.
	 */
	public boolean isTicsShown(int axis) {
		return gs[N1][N2].drawTics(axis);
	}

	/**
	 * Resets the current plot settings to default values.
	 */
	public void resetStyle() {
		gs[N1][N2].reset();
	}

	/**
	 * Resets all plot settings to default values.
	 */

	public void resetStyleAll() {
		for (int i1 = 0; i1 < N1final; i1++) {
			for (int i2 = 0; i2 < N2final; i2++) {
				gs[i1][i2].reset();
			}
		}

	}

	/**
	 * Clear the current graph including graph settings. Note: the current graph
	 * is set by the cd() method
	 */
	public void clear() {
		clear(N1, N2);
	}

	/**
	 * Clear the graph characterised by an index in X and Y. This method cleans
	 * the data and all graph settings.
	 * 
	 * @param i1
	 *            location of the graph in X
	 * @param i2
	 *            location of the graph in Y
	 */

	public void clear(int i1, int i2) {
		setGTitle("");
		// LinePars lp = new LinePars();

		data[i1][i2].clear();
		// set empty data
		// gs[i1][i2].setDrawLegend(false);
		// DataArray jplot3d = new DataArray(0, 1, 1, lp);
		// jplot3d.addPoint(0, 0);
		// jp[i1][i2].insertData(0, jplot3d);
		// now resert
		gs[i1][i2].reset();
		jp[i1][i2].clearData();
		jp[i1][i2].dismissGraph();
		jp[i1][i2].clear();
		System.gc();

	}

	/**
	 * Clear the current graph from the input data. Graph settings do not
	 * change. If graph is showing, it will be updated. Note: the current graph
	 * is given by the cd() method. All labels stay the same.
	 */

	public void clearData() {
		clearData(N1, N2);
	}

	/**
	 * Clear graph labels for the curent graph. This is likely a good practice
	 * to improve performance. Can be called after clearData().
	 */
	public void clearLabels() {
		clearLabels(N1, N2);
	}

	/**
	 * Clear Graph settings including labels. This is likely a good practice to
	 * improve performance. Can be called after clearData().
	 * 
	 * @param i1
	 *            location of the graph in X
	 * @param i2
	 *            location of the graph in Y
	 */

	public void clearLabels(int i1, int i2) {
		gs[i1][i2].getLabels().clear();
		jp[i1][i2].updateGraphIfShowing();
		updateFrame();
	}

	/**
	 * Clear data of the graph characterised by an index in X and Y. Graph
	 * settings do not change. If the graph is showed, data will be removed but
	 * all setting (labels) stay the same
	 * 
	 * @param i1
	 *            location of the graph in X
	 * @param i2
	 *            location of the graph in Y
	 */

	public void clearData(int i1, int i2) {
		IndexPlot = 0;
		// gs[i1][i2].setDrawLegend(false);
		data[i1][i2].clear();
		jp[i1][i2].clearData();
		jp[i1][i2].updateGraphIfShowing();
		updateFrame();
		// System.gc(); // speed up!

	}

	/**
	 * Clear all graphs from the input data.
	 */

	public void clearAllData() {
		for (int i1 = 0; i1 < N1final; i1++) {
			for (int i2 = 0; i2 < N2final; i2++) {
				clearData(i1, i2);
			}
		}
	}

	/**
	 * Clear all graph labels
	 */

	public void clearAllLabels() {
		for (int i1 = 0; i1 < N1final; i1++) {
			for (int i2 = 0; i2 < N2final; i2++) {
				clearLabels(i1, i2);
			}
		}
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
	 * Sets whether or not to draw ticks (little lines on the axes).
	 * 
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>.
	 * @param b
	 *            toggle, true if the ticks should be drawn.
	 */
	public void setTics(int axis, boolean b) {
		gs[N1][N2].setDrawTics(axis, b);
	}

	/**
	 * Sets whether or not to draw ticks (little lines on the axes) for all
	 * axis.
	 * 
	 * @param b
	 *            toggle, true if the ticks should be drawn.
	 */
	public void setTics(boolean b) {
		gs[N1][N2].setDrawTics(0, b);
		gs[N1][N2].setDrawTics(1, b);

	}

	/**
	 * Sets the range (min-max) displayed on the axis.
	 * 
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>. Usually, 0 means X, 1 means Y.
	 * @param min
	 *            minimum value on the axis
	 * @param max
	 *            maximum value on the axis
	 */
	public void setRange(int axis, double min, double max) {
		gs[N1][N2].setRange(axis, min, max);
		gs[N1][N2].setAutoRange(axis, false);
		if (axis==0) gs[N1][N2].setAutoRange(1, true);
	}

	/**
	 * Sets the range (min-max) displayed on X. Y is set to autorange.
	 * 
	 * @param min
	 *            minimum value on the axis
	 * @param max
	 *            maximum value on the axis
	 */
	public void setRangeX(double min, double max) {
		gs[N1][N2].setRange(0, min, max);
		gs[N1][N2].setAutoRange(0, false);
		gs[N1][N2].setAutoRange(1, true);
	}

	/**
	 * Sets the range (min-max) displayed on Y
	 * 
	 * @param min
	 *            minimum value on the axis
	 * @param max
	 *            maximum value on the axis
	 */
	public void setRangeY(double min, double max) {
		gs[N1][N2].setRange(1, min, max);
		gs[N1][N2].setAutoRange(1, false);
		gs[N1][N2].setAutoRange(0, true);
	}

	/**
	 * Sets the range (min-max) displayed on all axises.
	 * 
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>.
	 * @param min
	 *            minimum value on the axis
	 * @param max
	 *            maximum value on the axis
	 */

	public void setRangeAll(int axis, double min, double max) {
		for (int i1 = 0; i1 < N1final; i1++) {
			for (int i2 = 0; i2 < N2final; i2++) {
				gs[i1][i2].setRange(axis, min, max);
				gs[i1][i2].setAutoRange(axis, false);

			}
		}
	}

	/**
	 * Sets the range for the current plot
	 * 
	 * @param minX
	 *            Min value in X
	 * @param maxX
	 *            Max value in X
	 * @param minY
	 *            Min value in Y
	 * @param maxY
	 *            Max value in Y
	 */

	public void setRange(double minX, double maxX, double minY, double maxY) {
		gs[N1][N2].setRange(0, minX, maxX);
		gs[N1][N2].setRange(1, minY, maxY);
		gs[N1][N2].setAutoRange(0, false);
		gs[N1][N2].setAutoRange(1, false);
	}

	/**
	 * Sets the range for all plots
	 * 
	 * @param minX
	 *            Min value in X
	 * @param maxX
	 *            Max value in X
	 * @param minY
	 *            Min value in Y
	 * @param maxY
	 *            Max value in Y
	 */

	public void setRangeAll(double minX, double maxX, double minY, double maxY) {
		for (int i1 = 0; i1 < N1final; i1++) {
			for (int i2 = 0; i2 < N2final; i2++) {
				gs[i1][i2].setRange(0, minX, maxX);
				gs[i1][i2].setRange(1, minY, maxY);
				gs[i1][i2].setAutoRange(0, false);
				gs[i1][i2].setAutoRange(1, false);

			}
		}

	}

	/**
	 * Sets true or false to use automatic scaling for the current plot.
	 * 
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>.
	 * @param b
	 *            toggle, true if the the automatic scaling feature is enabled.
	 */
	public void setAutoRange(int axis, boolean b) {
		gs[N1][N2].setAutoRange(axis, b);
	}

	/**
	 * Sets true or false to use automatic scaling for all plots.
	 * 
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>.
	 * @param b
	 *            toggle, true if the the automatic scaling feature is enabled.
	 */

	public void setAutoRangeAll(int axis, boolean b) {

		for (int i1 = 0; i1 < N1final; i1++) {
			for (int i2 = 0; i2 < N2final; i2++) {
				gs[i1][i2].setAutoRange(axis, b);
			}
		}
	}

	/**
	 * Set autorange in X and Y at the same time
	 * 
	 * @param b
	 *            if true, sets autorange
	 */
	public void setAutoRange(boolean b) {
		gs[N1][N2].setAutoRange(0, b);
		gs[N1][N2].setAutoRange(1, b);
	}

	/**
	 * Set auto-range in X and Y at the same time for all plots
	 * 
	 * @param b
	 *            if true, sets auto-range
	 */
	public void setAutoRangeAll(boolean b) {

		for (int i1 = 0; i1 < N1final; i1++) {
			for (int i2 = 0; i2 < N2final; i2++) {
				gs[i1][i2].setAutoRange(0, b);
				gs[i1][i2].setAutoRange(1, b);
			}
		}
	}

	/**
	 * Set autorange in X and Y at the same time for the current plot
	 * 
	 */
	public void setAutoRange() {
		gs[N1][N2].setAutoRange(0, true);
		gs[N1][N2].setAutoRange(1, true);
	}

	/**
	 * Set autorange in X and Y at the same time for all plots
	 * 
	 */
	public void setAutoRangeAll() {

		for (int i1 = 0; i1 < N1final; i1++) {
			for (int i2 = 0; i2 < N2final; i2++) {
				gs[i1][i2].setAutoRange(0, true);
				gs[i1][i2].setAutoRange(1, true);
			}

		}
	}

	/**
	 * Draw a statistical box (mean, RMS, number of entries)
	 * 
	 * @param h1
	 *            histogram H1D
	 */
	public void drawStatBox(H1D h1) {

		String name = h1.getTitle();
		Dimension panel = gs[N1][N2].getPanelSize();
		double w = panel.width;
		double h = panel.height;

		GraphLabel label0 = new GraphLabel(GraphLabel.STATBOX, name);
		label0.setUsePosition(true);
		label0.setText(h1.getStatParameters());

		double xtop = gs[N1][N2].getTopMargin() + 0.4 * label0.getHeight();
		label0.setLocation(w - gs[N1][N2].getRightMargin() - 0.8*label0.getWidth(),
				xtop);
		gs[N1][N2].addLabel(label0);

	}

	/**
	 * Draw a statistical box (mean, RMS, number of entries) at a specific
	 * position. Use the standard AWT coordinates.
	 * 
	 * @param h1
	 *            histogram H1D
	 * @param x
	 *            position in X
	 * @param y
	 *            position in Y
	 */
	public void drawStatBox(H1D h1, int x, int y) {

		double mean = h1.mean();
		double rms = h1.rms();
		DecimalFormat dfb = new DecimalFormat("##.###E00");
		String name = h1.getTitle();
		String sentries = "Entries =" + Integer.toString(h1.entries());
		String smean = "Mean  =" + dfb.format(mean);
		String srms = "RMS =" + dfb.format(rms);
		String extra = "Under/Overflow =" + Integer.toString(h1.extraEntries());
		GraphLabel label0 = new GraphLabel(GraphLabel.STATBOX, name);
		label0.setUsePosition(true);
		String[] s = { name, sentries, smean, srms, extra };
		label0.setText(s);
		label0.setLocation(x, y);
		gs[N1][N2].addLabel(label0);
	}

	/**
	 * Draw a statistical box (mean, RMS, number of entries) at a specific
	 * position. Use the "USR" or "NDC" coordinates.
	 * 
	 * @param h1
	 *            histogram H1D
	 * @param x
	 *            position in X
	 * @param y
	 *            position in Y
	 * @param howToSet
	 *            use "NDC" or "USER" (coordinate dependent)
	 */
	public void drawStatBox(H1D h1, double x, double y, String howToSet) {

		if (howToSet.equalsIgnoreCase("USER")) {

			drawStatBox(h1, toX(x), toY(y));

		} else if (howToSet.equalsIgnoreCase("NDC")) {

			Dimension panel = gs[N1][N2].getPanelSize();
			double w = panel.width;
			double h = panel.height;
			int ix = (int) (w * x);
			int iy = (int) (h * (1 - y));
			drawStatBox(h1, ix, iy);

		}

	}

	/**
	 * Draw a text box with some information
	 * 
	 * @param lines
	 *            linex of the text to be draws
	 */
	public void drawTextBox(String[] lines) {

		Dimension panel = gs[N1][N2].getPanelSize();
		double w = panel.width;
		GraphLabel label0 = new GraphLabel(GraphLabel.STATBOX, "");
		label0.setUsePosition(true);
		label0.setText(lines);

		double xtop = gs[N1][N2].getTopMargin() + 0.5 * label0.getHeight();
		label0.setLocation(
				w - 2 * gs[N1][N2].getRightMargin() - label0.getWidth(), xtop);
		gs[N1][N2].addLabel(label0);
	}

	/**
	 * Remove one data set from the current plot.
	 * 
	 * @param i
	 *            the data set to be removed
	 */
	public void clearData(int i) {
		jp[N1][N2].clearData(i);
	}

	/**
	 * The data size, i.e. the number of objects plotted. on the current plot.
	 * 
	 * @return data size
	 */
	public int sizeData() {
		return jp[N1][N2].sizeData();
	}

	/**
	 * Sets the data in the form DataArray data
	 * 
	 * @param data
	 *            data container of type DataArray
	 */
	public void setDataPers(DataArray data) {

		String name = data.getName();
		String s = "jplot3d" + lf;
		s = s + name;

		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(s));
			// out.write("# dataset 1: "); out.write(name); out.newLine();
			out.write("# column 1: X");
			out.newLine();
			out.write("# column 2: ");
			out.write(name);
			out.newLine();
			// out.write("# xlabel: X"); out.newLine();
			// out.write("# ylabel: "); out.write(name); out.newLine();

			for (int i = 0; i < data.size(); i++) {
				out.write(Double.toString(data.getX(i)));
				out.write(" ");
				out.write(Double.toString(data.getY(i)));
				out.newLine();
			}
			out.close();
		} catch (IOException e) {
		}

		JHPlot.ReadFile = true;
		// jp.insertDatafile(new File(s));

	}

	/*
	 * public void writeData(DataArray data, String dataname, String FileName) {
	 * 
	 * 
	 * String fname=FileName; // String s="jplot3d"+lf; // s=s+fname;
	 * 
	 * String name=dataname; try { BufferedWriter out = new BufferedWriter(new
	 * FileWriter(fname)); out.write("# column 1: X"); out.newLine();
	 * out.write("# column 2: "); out.write(name); out.newLine(); for (int i=0;
	 * i<data.size(); i++) { out.write(Double.toString(data.getX(i)));
	 * out.write(" "); out.write(Double.toString(data.getY(i))); out.newLine();
	 * } out.close(); } catch (IOException e) { } }
	 */

	/*
	 * public void setData( DataArray data) { // SetEnv.DATA.add(data); //
	 * SetEnv.ReadFile=false; // jp.insertData( IndexPlot, data ); }
	 * 
	 * 
	 * 
	 * public void showGraph( boolean show ) {
	 * 
	 * jp.showGraph(show); }
	 * 
	 * 
	 * public void showGraph() {
	 * 
	 * jp.showGraph(true); }
	 * 
	 * 
	 * 
	 * 
	 * public void dismissGraph() {
	 * 
	 * jp.dismissGraph(); }
	 */

	/**
	 * Draw array of histograms
	 * 
	 * @param h1
	 *            array of H1D histograms
	 */

	public void draw(H1D[] h1) {

		for (int i = 0; i < h1.length; i++) {
			draw(h1[i]);
		}

	}

	/**
	 * Plot cloud in 1D. Assume 100 bins.
	 * 
	 * @param c1d
	 *            Input Cloud1D
	 */

	public void draw(Cloud1D c1d) {

		draw(new H1D(c1d, 100));

	}

	/**
	 * Plot Aida datapointset.
	 * 
	 * @param ds
	 *            Input dataPoint set
	 */

	public void draw(DataPointSet ds) {

		draw(new P1D(ds));

	}

	/**
	 * Plot cloud 2D
	 * 
	 * @param c2D
	 *            Input 2D cloud
	 */

	public void draw(Cloud2D c2d) {

		draw(new P1D(c2d));

	}

	/**
	 * Draw 1D histogram
	 * 
	 * @param h1d
	 *            input istogram1D
	 */
	public void draw(Histogram1D h1d) {

		H1D h1 = new H1D(h1d);
		draw(h1);

	}

	/**
	 * Plot 1D histogram.
	 * 
	 * @param h1
	 *            Input H1D histogram
	 */

	public void draw(H1D h1) {

		if (h1.getLabelX() != null)
			if (h1.getLabelX().length() > 0)
				setNameX(h1.getLabelX());
		if (h1.getLabelY() != null)
			if (h1.getLabelY().length() > 0)
				setNameY(h1.getLabelY());

		h1.setType(LinePars.H1D);

		// System.out.println(N1);
		// System.out.println(N2);

		gs[N1][N2].setGraphType(GraphSettings.GRAPHTYPE_2D);
		gs[N1][N2].set2DType(1); // this is histogram type

		/*
		 * double Min = h1.getMin(); double Max = h1.getMax(); // and its upper
		 * edge double BinWidth = (Max - Min) / (double) Bin;
		 */
		Histogram1D h = h1.get();
		IAxis ax = h.axis();
		int Bin = ax.bins();

		IndexPlot++;
		DataArray data1 = new DataArray(IndexPlot, 1, Bin, h1.getDrawOption());

		// h1.getDrawOption().print();

		for (int i = 0; i < Bin; i++) {
			double dd = ax.binCenter(i);
			double hh = h1.binHeight(i);
			double errX1 = dd - ax.binLowerEdge(i);
			double errX2 = ax.binUpperEdge(i) - dd;
			double errY = h.binError(i); // Math.sqrt(hh);
			data1.addPoint(dd, hh, errX1, errX2, errY, errY);

		}

		data1.setDimension(6);
		data1.setLinePars(h1.getDrawOption());
		data1.setName(h1.getTitle());
		JHPlot.ReadFile = false;

		data[N1][N2].add(data1);
		jp[N1][N2].insertData(IndexPlot, data1);

	}

	/*
	 * This works only for fixed bins public void draw(H1D h1) {
	 * 
	 * h1.setType(LinePars.H1D);
	 * 
	 * gs[N1][N2].setGraphType(GraphSettings.GRAPHTYPE_2D);
	 * gs[N1][N2].set2DType(1); // this is histogram type
	 * 
	 * int Bin = h1.getBins(); double Min = h1.getMin(); double Max =
	 * h1.getMax(); // and its upper edge double BinWidth = (Max - Min) /
	 * (double) Bin;
	 * 
	 * IndexPlot++; DataArray data1 = new DataArray(IndexPlot, 1, Bin,
	 * h1.getDrawOption());
	 * 
	 * for (int i = 0; i < Bin; i++) { double dd = Min + BinWidth * i; double hh
	 * = h1.binEntries(i); double errX = 0.5 * BinWidth; double errY =
	 * Math.sqrt(hh); // supress 0 // also, add some systematical error for
	 * checks // if (hh != 0) data.addPoint(dd,hh,errX,errX,errY,errY);
	 * data1.addPoint(dd + 0.5 * BinWidth, hh, errX, errX, errY, errY); }
	 * 
	 * 
	 * data1.setLinePars(h1.getDrawOption()); data1.setName(h1.getTitle());
	 * SetEnv.ReadFile = false;
	 * 
	 * data[N1][N2].add(data1); jp[N1][N2].insertData(IndexPlot, data1); //
	 * update(N1, N2); }
	 */

	/**
	 * Fit 1D histogram with the function F1D
	 * 
	 * @param h1
	 *            H1D histogram
	 * @param predefFunc
	 *            String predefined function
	 * @param method
	 *            String - method for the fit
	 * @return int Fit results
	 */
	public int fit(H1D h1, String predefFunc, String method) {

		if (m_IFunctionFactory == null || m_IAnalysisFactory == null) {
			m_IAnalysisFactory = IAnalysisFactory.create();
			m_ITree = m_IAnalysisFactory.createTreeFactory().create();
			m_IHistogramFactory = m_IAnalysisFactory
					.createHistogramFactory(m_ITree);
			m_IFitFactory = m_IAnalysisFactory.createFitFactory();
			m_IFunctionFactory = m_IAnalysisFactory
					.createFunctionFactory(m_ITree);
		}

		IFunction fitfunc = m_IFunctionFactory.createFunctionByName("Gaussian",
				"G");
		IFitter fitter = m_IFitFactory.createFitter("chi2", "jminuit");

		IFitResult result = fitter.fit(h1.get(), fitfunc);

		IFunction fresult = result.fittedFunction();

		double[] fPars = result.fittedParameters();
		double[] fParErrs = result.errors();
		String[] fParNames = result.fittedParameterNames();

		for (int i = 0; i < fresult.numberOfParameters(); i++)
			System.out.println(fParNames[i] + " : " + fPars[i] + " +- "
					+ fParErrs[i]);

		return result.fitStatus();
	}

	/**
	 * Draw array of F1D holders
	 * 
	 * @param f
	 *            array of F1D functions
	 */

	public void draw(F1D[] f) {

		for (int i = 0; i < f.length; i++) {
			draw(f[i]);
		}

	}

	/**
	 * Draw input data represented by DataArray
	 * 
	 * @param inputDA
	 *            input data container
	 */
	public void draw(DataArray inputDA, int index) {

		IndexPlot++;
		JHPlot.ReadFile = false;
		data[N1][N2].add(inputDA);
		jp[N1][N2].insertData(IndexPlot, inputDA);

	}

	/**
	 * Draw an one-dimensional function. Range is determined by the setRange()
	 * method.
	 * If You set range during F1D initialization, it will be used.
	 * If not, canvas range is used.
	 * @param f1
	 *            F1D function
	 * @param return true if success
	 */
	public boolean draw(F1D f1) {
		return draw(f1, false);
	}

	
	

	/**
	 * Draw a function in a range. Y range is set by autorange.
	 * 
	 * 
	 * @param f1
	 * @param min
	 *            min X value
	 * @param max
	 *            max Y value
	 * @return
	 */
	public boolean draw(F1D f1, double min, double max) {

		if (f1.getLabelX() != null)
			if (f1.getLabelX().length() > 0)
				setNameX(f1.getLabelX());
		if (f1.getLabelY() != null)
			if (f1.getLabelY().length() > 0)
				setNameY(f1.getLabelY());

		f1.setType(LinePars.F1D);
		
		f1.setMin(min);
		f1.setMax(max);
		gs[N1][N2].setRange(0, min, max);
		gs[N1][N2].setAutoRange(0, false);
		gs[N1][N2].setAutoRange(1, true);
		f1.eval(getMinValue(0), getMaxValue(0), f1.getPoints()); // evaluate
																	// first

		gs[N1][N2].setGraphType(GraphSettings.GRAPHTYPE_2D);
		gs[N1][N2].set2DType(0); // this is a function type

		int Bin = f1.getPoints();
		IndexPlot++;

		f1.setGraphStyle(0);
		f1.setDrawSymbol(false);
		// f1.setSymbol(4);
		f1.setDrawLine(true);
		DataArray data1 = new DataArray(IndexPlot, 1, Bin, f1.getDrawOption());

		for (int i = 0; i < Bin; i++) {
			data1.addPoint(f1.getX(i), f1.getY(i));
		}

		data1.setDimension(2);
		data1.setName(f1.getTitle());
		JHPlot.ReadFile = false;
		data[N1][N2].add(data1);
		jp[N1][N2].insertData(IndexPlot, data1);
		return true;

	}

	/**
	 * Draw an one-dimensional function from FND
	 * 
	 * @param f1
	 *            FND function
	 * @param return true if success
	 */
	public boolean draw(FND f1) {
		return draw(f1, false);
	}

	/**
	 * Draw an one-dimensional function. Suppress negative values (useful for
	 * showing together with a histogram).
	 * If You set range during F1D initialization, it will be used.
	 * If not, canvas range is used.
	 * 
	 * @param f1
	 *            F1D function
	 * @param startZero
	 *            if true, start from 0 on Y
	 * @return true if success
	 */
	public boolean draw(F1D f1, boolean startZero) {

		if (f1.getLabelX() != null)
			if (f1.getLabelX().length() > 0)
				setNameX(f1.getLabelX());
		if (f1.getLabelY() != null)
			if (f1.getLabelY().length() > 0)
				setNameY(f1.getLabelY());

		f1.setType(LinePars.F1D);
		
		if (f1.getMin() == f1.getMax()) {
			
		   f1.eval(getMinValue(0), getMaxValue(0), f1.getPoints()); // evaluate
		} else {
			// if restricted range is set, use it
			f1.eval(f1.getMin(), f1.getMax(), f1.getPoints()); // evaluate
			
		}
		
		// first

		gs[N1][N2].set2DType(1);

		// do not start from 0 in Y
		if (startZero == false) {
			gs[N1][N2].setGraphType(GraphSettings.GRAPHTYPE_2D);
			gs[N1][N2].set2DType(0); // this is a function type
		}

		int Bin = f1.getPoints();
		IndexPlot++;

		f1.setGraphStyle(0);
		f1.setDrawSymbol(false);
		// f1.setSymbol(4);
		f1.setDrawLine(true);
		DataArray data1 = new DataArray(IndexPlot, 1, Bin, f1.getDrawOption());

		for (int i = 0; i < Bin; i++) {
			data1.addPoint(f1.getX(i), f1.getY(i));
		}

		data1.setDimension(2);
		data1.setName(f1.getTitle());
		JHPlot.ReadFile = false;
		data[N1][N2].add(data1);
		jp[N1][N2].insertData(IndexPlot, data1);
		return true;
	}

	/**
	 * Draw an one-dimensional function from FND. Suppress negative values
	 * (useful for showing together with a histogram).
	 * 
	 * @param f1
	 *            FND function
	 * @param startZero
	 *            if true, start from 0 on Y
	 * @return true if success
	 */
	public boolean draw(FND f1, boolean startZero) {

		if (f1.getLabelX() != null)
			if (f1.getLabelX().length() > 0)
				setNameX(f1.getLabelX());
		if (f1.getLabelY() != null)
			if (f1.getLabelY().length() > 0)
				setNameY(f1.getLabelY());

		if (f1.isEvaluated() == false) {
			System.out.println("The function is not avaluated yet!");
			return false;
		}

		f1.setType(LinePars.F1D);

		gs[N1][N2].set2DType(1);

		// do not start from 0 in Y
		if (startZero == false) {
			gs[N1][N2].setGraphType(GraphSettings.GRAPHTYPE_2D);
			gs[N1][N2].set2DType(0); // this is a function type
		}

		int Bin = f1.getPoints();
		IndexPlot++;

		f1.setGraphStyle(0);
		f1.setDrawSymbol(false);
		// f1.setSymbol(4);
		f1.setDrawLine(true);
		DataArray data1 = new DataArray(IndexPlot, 1, Bin, f1.getDrawOption());

		for (int i = 0; i < Bin; i++) {
			data1.addPoint(f1.getX(i), f1.getY(i));
		}

		data1.setDimension(2);
		data1.setName(f1.getTitle() + "; " + f1.getFixedVars());
		JHPlot.ReadFile = false;
		data[N1][N2].add(data1);
		jp[N1][N2].insertData(IndexPlot, data1);

		return true;
	}

	/**
	 * Get the vector which keeps all the data
	 * 
	 * @return Vector with the data
	 */
	public Vector<DataArray>[][] getData() {
		return data;

	}

	/**
	 * Close the canvas (and dispose all components).
	 */
	public void close() {

		isOpen = 0;
		mainFrame.setVisible(false);
		m_Close = new Thread1("Closing softly");
		if (!m_Close.Alive())
			m_Close.Start();

	}

	/**
	 * Shows how many time the canvas was open.
	 * 
	 * @return shows how many time was open
	 */
	protected int isOpen() {
		return isOpen;
	}

	/**
	 * Quit the canvas (and dispose all components) Note: a memory leak is found
	 * - no time to study it. set to null all the stuff
	 */

	public void quit() {

		doNotShowFrame();

		// System.out.println("--> Closing the canvas");
		clearAllData();
		clearAll();

		for (int i1 = 0; i1 < N1final; i1++) {
			for (int i2 = 0; i2 < N2final; i2++) {

				// System.out.println("Clear graph="+Integer.toString(i1)+
				// " " + Integer.toString(i2));
				// clear data
				data[i1][i2].clear();
				data[i1][i2] = null;

				jp[i1][i2].quit();
				jp[i1][i2] = null;

				frames[i1][i2].dispose();
				frames[i1][i2] = null;

			}
		}

		gs = null;
		jp = null;
		graph = null;
		data = null;
		sc = null;
		frames = null;

		removeFrame();

	}

	/**
	 * Remove all components from the JHPlot frame. Only keeps the empty central
	 * panel and 4 margins
	 */

	public void removeGraph() {

		resetMargins();

		// first remove - it will look nice
		for (int i2 = 0; i2 < N2final; i2++) {
			for (int i1 = 0; i1 < N1final; i1++) {

				mainPanel.remove(graph[i1][i2]);

			}
		}

		// System.out.println("--> Closing the canvas");
		clearAllData();

		clearAll();

		for (int i2 = 0; i2 < N2final; i2++) {
			for (int i1 = 0; i1 < N1final; i1++) {

				frames[i1][i2].dispose();
				frames[i1][i2] = null;
				graph[i1][i2] = null;

			}
		}

		gs = null;
		jp = null;
		graph = null;
		data = null;
		sc = null;
		frames = null;
		N1final = 0;
		N2final = 0;

		System.gc();

	}

	/**
	 * Draw array of P1D holders
	 * 
	 * @param d
	 *            array of P1D data holders
	 */

	public void draw(P1D[] d) {

		for (int i = 0; i < d.length; i++) {
			draw(d[i]);
		}

	}

	/**
	 * Draw data in form of P1D
	 * 
	 * @param d
	 *            P1D container
	 */
	public void draw(P1D d) {

		IndexPlot++;
		JHPlot.ReadFile = false;
		if (d.getLabelX() != null)
			if (d.getLabelX().length() > 0)
				setNameX(d.getLabelX());
		if (d.getLabelY() != null)
			if (d.getLabelY().length() > 0)
				setNameY(d.getLabelY());

		d.setType(LinePars.P1D);
		// for contous
		d.setGraphStyle(LinePars.LINES);
		if (plotType[N1][N2] == GraphSettings.CONTOUR_2D) {
			d.setGraphStyle(LinePars.CONTOUR);
		}

		DataArray tmp = d.getDataArray();
		tmp.setDimension(d.dimension());
		tmp.setLinePars(d.getDrawOption());
		tmp.setName(d.getTitle());

		data[N1][N2].add(tmp);
		jp[N1][N2].insertData(IndexPlot, tmp);
		// update(N1, N2);

	}

	private class RefreshAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		RefreshAction() {
			super("Data reload");
		}

		public void actionPerformed(ActionEvent e) {
			update(N1edit, N2edit);
			// System.exit(0);
		}
	}

	private class NotShowAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		NotShowAction() {
			super("Close");
		}

		public void actionPerformed(ActionEvent e) {
			frames[N1edit][N2edit].setVisible(false);
		}
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
	 * Implemented abstract function to close the frame from the menu
	 */
	protected void quitFrame() {
		close();
	}

	@Override
	protected void showHelp() {

		new HelpDialog(this.getFrame(), help_file + ".html");
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

	/**
	 * Open a dialog to write the file
	 * 
	 */

	protected void openWriteDialog() {

		final JFrame frm = getFrame();
		// File scriptFile;

		JFileChooser chooser = new JFileChooser(new File("."));

		FilenameFilter ff = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return !name.endsWith(".jhp");
			}
		};

		if (chooser.showDialog(frm, "Save As") == 0) {

			final File scriptFile = chooser.getSelectedFile();
			if (scriptFile == null)
				return;
			else if (scriptFile.exists()) {
				int res = JOptionPane.showConfirmDialog(frm,
						"The file exist: do you want to overwrite the file?",
						"", JOptionPane.YES_NO_OPTION);

				if (res == JOptionPane.NO_OPTION)
					return;
			}

			String mess = "write JHPlot XML file";
			JHPlot.showStatusBarText(mess);
			Thread t = new Thread(mess) {
				public void run() {
					boolean res = writeScript(scriptFile);
				};
			};
			t.start();
		}

	}

}

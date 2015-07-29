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
import java.util.ArrayList;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.util.HashMap;
import java.util.Map;
import jplot.*;
import javax.imageio.ImageIO;
import org.jfree.chart.renderer.*;
import org.jfree.chart.renderer.category.*;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.Title;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.*;
import org.jfree.data.category.*;
import org.jfree.data.general.*;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.renderer.xy.*;
import hep.aida.*;
import hep.aida.ref.histogram.*;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.event.PlotChangeEvent;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import jhplot.io.images.ImageType;
import jhplot.io.images.Export;
import jhplot.gui.GHFrame;
import jhplot.gui.HelpBrowser;
import org.freehep.graphicsbase.util.export.*;

/**
 * Create various charts, such as pie, histograms, bar charts including 3D
 * emulation, lines.
 * 
 * @author S.Chekanov
 * 
 */

public class HChart extends GHFrame {

	private static final long serialVersionUID = 1L;

	public static int IndexPlot = 0;

	private ChartPanel[][] cp;

	private JFreeChart[][] chart;

	private DefaultPieDataset cdatPie[][], cdatPie3D[][];
	private XYSeriesCollection cdatPolar[][];
	private DefaultCategoryDataset cdatBar[][], cdatBar3D[][], cdatLine[][],
			cdatArea[][];

	private ArrayList<AbstractRenderer> rdat[][];
	private int indexdat[][];

	private Map<Integer, String> type[][];

	private ValueAxis xAxis[][];
	private ValueAxis yAxis[][];
	private XYPlot xyplot[][];

	private float linestroke = 4.0f;
	public boolean set;
	private boolean antiAlias;

	private String title[][];

	private String titleX[][];

	private String titleY[][];

	// for histogram type
	private int Bins = 10;

	private double Min = 0;

	private double Max = 0;

	private String sname = "";

	private final Color DEFAULT_BG_COLOR = Color.white;

	private Thread1 m_Close;

	private Font labelFont;

	private Font tickFont;

	private Color labelColor;

	private Color tickColor;

	private boolean isLog[];

	private float axisPenwidth;

	/**
	 * Create HChart canvas with a several chart plots
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

	public HChart(String title, int xsize, int ysize, int n1, int n2,
			boolean set) {

		super(title, xsize, ysize, n1, n2, set);

		cdatPie = new DefaultPieDataset[N1final][N2final];
		cdatPie3D = new DefaultPieDataset[N1final][N2final];
		cdatBar = new DefaultCategoryDataset[N1final][N2final];
		cdatBar3D = new DefaultCategoryDataset[N1final][N2final];
		cdatLine = new DefaultCategoryDataset[N1final][N2final];
		cdatArea = new DefaultCategoryDataset[N1final][N2final];
		cdatPolar = new XYSeriesCollection[N1final][N2final];

		isLog = new boolean[2];
		isLog[0] = false;
		isLog[1] = false;

		xAxis = new NumberAxis[N1final][N2final];
		yAxis = new NumberAxis[N1final][N2final];
		xyplot = new XYPlot[N1final][N2final];
		type = new HashMap[N1final][N2final];
		indexdat = new int[N1final][N2final];
		rdat = new ArrayList[N1final][N2final];

		axisPenwidth = 3.0f;

		XYBarRenderer defaultrender = new XYBarRenderer(0.2D);
		// IntervalXYDataset intervalxydataset = new IntervalXYDataset();

		tickFont = new Font("Arial", Font.BOLD, 14);
		labelFont = new Font("Arial", Font.BOLD, 16);
		labelColor = Color.BLACK;
		tickColor = Color.BLACK;

		chart = new JFreeChart[N1final][N2final];
		cp = new ChartPanel[N1final][N2final];

		antiAlias = true;
		this.title = new String[N1final][N2final];
		this.titleX = new String[N1final][N2final];
		this.titleY = new String[N1final][N2final];

		// build empty canvas
		for (int i2 = 0; i2 < N2final; i2++) {
			for (int i1 = 0; i1 < N1final; i1++) {

				this.title[i1][i2] = title;
				this.titleX[i1][i2] = "X";
				this.titleY[i1][i2] = "Y";

				rdat[i1][i2] = new ArrayList<AbstractRenderer>();

				xAxis[i1][i2] = new NumberAxis(titleX[i1][i2]);
				xAxis[i1][i2].setTickLabelPaint(tickColor);
				xAxis[i1][i2].setTickLabelFont(tickFont);
				xAxis[i1][i2].setLabelPaint(labelColor);
				xAxis[i1][i2].setLabelFont(labelFont);
				xAxis[i1][i2].setAxisLineStroke(new BasicStroke(axisPenwidth));
				xAxis[i1][i2].setAxisLinePaint(Color.black);

				type[i1][i2] = new HashMap<Integer, String>();
				indexdat[i1][i2] = 0;

				yAxis[i1][i2] = new NumberAxis(titleX[i1][i2]);
				yAxis[i1][i2].setTickLabelPaint(tickColor);
				yAxis[i1][i2].setTickLabelFont(tickFont);
				yAxis[i1][i2].setLabelPaint(labelColor);
				yAxis[i1][i2].setLabelFont(labelFont);
				yAxis[i1][i2].setAxisLineStroke(new BasicStroke(axisPenwidth));
				yAxis[i1][i2].setAxisLinePaint(Color.black);

				xyplot[i1][i2] = new XYPlot(null, (ValueAxis) xAxis[i1][i2],
						(ValueAxis) yAxis[i1][i2], defaultrender);

				// for outline
				xyplot[i1][i2].setOutlinePaint(Color.black);
				xyplot[i1][i2].setOutlineStroke(new BasicStroke(axisPenwidth));
				xyplot[i1][i2].setOutlineVisible(true);

				xyplot[i1][i2]
						.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
				xyplot[i1][i2].setOrientation(PlotOrientation.VERTICAL);

				chart[i1][i2] = new JFreeChart(this.title[i1][i2],
						JFreeChart.DEFAULT_TITLE_FONT, xyplot[i1][i2], true);

				chart[i1][i2].setAntiAlias(antiAlias);
				chart[i1][i2].setBorderPaint(DEFAULT_BG_COLOR);
				chart[i1][i2].setBackgroundPaint(DEFAULT_BG_COLOR);
				chart[i1][i2].setBorderVisible(false);

				cp[i1][i2] = new ChartPanel(chart[i1][i2]);
				cp[i1][i2].setBackground(DEFAULT_BG_COLOR);
				cp[i1][i2].setLayout(new BorderLayout());
				cp[i1][i2].setDomainZoomable(true);
				cp[i1][i2].setRangeZoomable(true);
				if (set)
					mainPanel.add(cp[i1][i2]);
			}
		}

		setTheme("LEGACY_THEME");
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

		Dimension dim = cp[N1][N2].getSize();
		double h = dim.getHeight();
		double w = dim.getWidth();
		cp[N1][N2].setPreferredSize(new Dimension((int) (w * widthScale),
				(int) (h * heightScale)));
		cp[N1][N2].setMinimumSize(new Dimension((int) (w * widthScale),
				(int) (h * heightScale)));
		cp[N1][N2].setSize(new Dimension((int) (w * widthScale),
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
		Dimension dim = cp[n1][n2].getSize();
		double h = dim.getHeight();
		double w = dim.getWidth();
		cp[n1][n2].setPreferredSize(new Dimension((int) (w * widthScale),
				(int) (h * heightScale)));
		cp[n1][n2].setMinimumSize(new Dimension((int) (w * widthScale),
				(int) (h * heightScale)));
		cp[n1][n2].setSize(new Dimension((int) (w * widthScale),
				(int) (h * heightScale)));
	}

	/**
	 * Clear all the frames
	 * 
	 */
	protected void clearFrame() {

	}

	/**
	 * Se5t color of the labels.
	 * 
	 * @param labelColor
	 */
	public void setLabelColor(Color labelColor) {
		this.labelColor = labelColor;
		xAxis[N1][N2].setLabelPaint(labelColor);
		yAxis[N1][N2].setLabelPaint(labelColor);
	}

	/**
	 * Set fonts of the labels.
	 * 
	 * @param font
	 */
	public void setLabelFont(Font font) {
		labelFont = font;
		xAxis[N1][N2].setLabelFont(labelFont);
		yAxis[N1][N2].setLabelFont(labelFont);

	}

	/**
	 * Get label color.
	 * 
	 * @return color
	 */
	public Color getLabelColor() {
		return labelColor;

	}

	/**
	 * Set tick color
	 * 
	 * @param tickColor
	 */

	public void setTickColor(int axis, Color tickColor) {
		this.tickColor = tickColor;
		xAxis[N1][N2].setTickLabelPaint(tickColor);
		yAxis[N1][N2].setTickLabelPaint(tickColor);
	}

	/**
	 * Get color of ticks.
	 * 
	 * @return color
	 */
	public Color getTickColor() {
		return tickColor;
	}

	/**
	 * Set font of ticks
	 * 
	 * @param tickFont
	 */
	public void setTickFont(Font tickFont) {
		this.tickFont = tickFont;
		xAxis[N1][N2].setTickLabelFont(tickFont);
		yAxis[N1][N2].setTickLabelFont(tickFont);

	}

	/**
	 * Get font of ticks.
	 * 
	 * @return
	 */
	public Font getTickFont() {
		return tickFont;
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
		mainFrame.setVisible(vs);

	}

	/**
	 * Set the canvas frame visible
	 * 
	 */

	public void visible() {
		// updateAll();
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
		mainFrame.setLocation(posX, posY);
		mainFrame.setVisible(true);

	}

	/**
	 * Set a custom theme for chart.. It can be: LEGACY_THEME, JFREE_THEME,
	 * DARKNESS_THEME
	 * 
	 * @param s
	 *            a theme, can be either LEGACY_THEME, JFREE_THEME,
	 *            DARKNESS_THEME
	 */
	public void setTheme(String s) {

		if (s.equals("LEGACY_THEME")) {
			ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
			applyThemeToChart();
		} else if (s.equals("JFREE_THEME")) {
			ChartFactory.setChartTheme(StandardChartTheme.createJFreeTheme());
			applyThemeToChart();
		} else if (s.equals("DARKNESS_THEME")) {
			ChartFactory
					.setChartTheme(StandardChartTheme.createDarknessTheme());
			applyThemeToChart();
		}
	}

	private void applyThemeToChart() {

		for (int i1 = 0; i1 < N1final; i1++) {
			for (int i2 = 0; i2 < N2final; i2++) {
				if (chart[i1][i2] != null) {
					ChartUtilities.applyCurrentTheme(chart[i1][i2]);
				}
			}
		}
	}

	/**
	 * Remove the canvas frame
	 */
	public void destroy() {
		mainFrame.setVisible(false);
		close();
	}

	/**
	 * Set font and color for X-axis (for X-Y plots)
	 * 
	 * @param f
	 *            font
	 * @param c
	 *            color
	 */

	public void setFontAxisX(Font f, Color c) {

		XYPlot plot = (XYPlot) chart[N1][N2].getXYPlot();
		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setTickLabelPaint(c);
		rangeAxis.setTickLabelFont(f);
		rangeAxis.setLabelPaint(c);
		update();

	}

	/**
	 * Set font and color for Y-axis (for X-Y plots)
	 * 
	 * @param f
	 *            font
	 * @param c
	 *            color
	 */

	public void setFontAxisY(Font f, Color c) {

		XYPlot plot = (XYPlot) chart[N1][N2].getXYPlot();
		ValueAxis domainAxis = plot.getDomainAxis();
		domainAxis.setTickLabelPaint(c);
		domainAxis.setTickLabelFont(f);
		domainAxis.setLabelPaint(c);
		update();

	}

	/**
	 * Make an Area chart.
	 * 
	 */
	public void setChartArea() {

		cdatArea[N1][N2] = new DefaultCategoryDataset();

	}

	/**
	 * Make a Bar chart.
	 * 
	 */
	public void setChartBar() {

		cdatBar[N1][N2] = new DefaultCategoryDataset();

	}

	/**
	 * Make a Bar chart in 3D.
	 * 
	 */
	public void setChartBar3D() {

		cdatBar3D[N1][N2] = new DefaultCategoryDataset();

	}

	/**
	 * Make a line chart.
	 * 
	 */
	public void setChartLine() {
		setChartLine(4.0f);
	}

	/**
	 * Make a line chart.
	 * 
	 * @param stroke
	 *            stroke width to draw the line
	 */
	public void setChartLine(double stroke) {
		this.linestroke = (float) stroke;
		cdatLine[N1][N2] = new DefaultCategoryDataset();
	}

	/**
	 * Make a Pie chart.
	 * 
	 */
	public void setChartPie() {

		cdatPie[N1][N2] = new DefaultPieDataset();

	}

	/**
	 * Make a Pie 3D chart.
	 * 
	 */
	public void setChartPie3D() {

		cdatPie3D[N1][N2] = new DefaultPieDataset();

	}

	/**
	 * Make a chart using polar coordinates. Then use add() method to add X-Y
	 * values.
	 * 
	 */
	public void setChartPolar() {

		cdatPolar[N1][N2] = new XYSeriesCollection();

	}

	/**
	 * Clear plot.
	 * 
	 */
	public void clearPlot() {

		for (int i1 = 0; i1 < N1final; i1++) {
			for (int i2 = 0; i2 < N2final; i2++) {

				if (chart[i1][i2] != null) {
					chart[i1][i2].setTitle("");
					chart[i1][i2].setBackgroundPaint(DEFAULT_BG_COLOR);
				}

			}
		}

	}

	/**
	 * Clear plot from the data
	 * 
	 */
	public void clearData() {

		for (int i1 = 0; i1 < N1final; i1++) {
			for (int i2 = 0; i2 < N2final; i2++) {

				// cdat[i1][i2] = null;

			}
		}

	}

	/**
	 * Construct a HChart canvas with a single plot/graph
	 * 
	 * @param title
	 *            Title for the canvas
	 * @param xs
	 *            size in x
	 * @param ys
	 *            size in y
	 */
	public HChart(String title, int xs, int ys) {

		this(title, xs, ys, 1, 1, true);

	}

	/**
	 * Construct a HChart canvas with a single plot/graph
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
	public HChart(String title, int xs, int ys, boolean set) {

		this(title, xs, ys, 1, 1, set);

	}

	/**
	 * Construct a HChart canvas with plots/graphs
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
	public HChart(String title, int xs, int ys, int n1, int n2) {

		this(title, xs, ys, n1, n2, true);

	}

	/**
	 * Construct a HChart canvas with a plot with the default parameters 600 by
	 * 400, and 10% space for the global title
	 * 
	 * @param title
	 *            Title
	 */
	public HChart(String title) {

		this(title, 600, 400, 1, 1, true);

	}

	/**
	 * Construct a HChart canvas with a plot with the default parameters 600 by
	 * 400, and 10% space for the global title "Default"
	 * 
	 */
	public HChart() {
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
	 * Clear the chart characterized by an index in X and Y. This method cleans
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
	 * Update all plots
	 */

	public void update() {
		for (int i1 = 0; i1 < N1final; i1++) {
			for (int i2 = 0; i2 < N2final; i2++) {
				update(i1, i2);
			}
		}
		System.gc();
	}

	/**
	 * Set global title to the current chart. Should be called before setChart*
	 * methods.
	 * 
	 * @param title
	 *            title
	 */

	public void setName(String title) {

		this.title[N1][N2] = title;

	}

	/**
	 * Sets the name for X axis. Should be called before setChart* methods.
	 * 
	 * @param s
	 *            Title for X axis.
	 */
	public void setNameX(String s) {

		titleX[N1][N2] = s;
		ValueAxis xx = xyplot[N1][N2].getDomainAxis();
		xx.setLabel(s);

	}

	/**
	 * Sets the name for Y axis. Should be called before setChart* methods.
	 * 
	 * @param s
	 *            Title for Y axis.
	 */
	public void setNameY(String s) {

		titleY[N1][N2] = s;
		ValueAxis yy = xyplot[N1][N2].getRangeAxis();
		yy.setLabel(s);

	}

	/**
	 * Sets true or false to plot on a log scale.
	 * 
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>.
	 * @param b
	 *            toggle, true if the scaling is logarithmic
	 */
	public void setLogScale(int axis, boolean b) {

		isLog[axis] = b;

		if (isLog[0] == false) {
			xAxis[N1][N2] = new NumberAxis(titleX[N1][N2]);
		}
		if (isLog[0] == true) {
			xAxis[N1][N2] = new LogarithmicAxis(titleX[N1][N2]);
		}

		if (isLog[1] == false) {
			yAxis[N1][N2] = new NumberAxis(titleY[N1][N2]);
		}
		if (isLog[1] == true) {
			yAxis[N1][N2] = new LogarithmicAxis(titleY[N1][N2]);
		}

	}

	/**
	 * Sets axis
	 * 
	 * @param axis
	 *            defines to which axis this function applies. 0 means X aaxis,
	 *            1 means Y axis.
	 * @param a
	 *            axis object
	 */
	public void setAxis(int axis, ValueAxis a) {

		if (axis == 0)
			xAxis[N1][N2] = a;
		if (axis == 1)
			yAxis[N1][N2] = a;

	}

	/**
	 * Get the axis
	 * 
	 * @param axis
	 *            defines to which axis this function applies. 0 means X aaxis,
	 *            1 means Y axis.
	 */
	public ValueAxis getAxis(int axis) {

		if (axis == 0)
			return xAxis[N1][N2];
		if (axis == 1)
			return yAxis[N1][N2];
		return xAxis[N1][N2];

	}

	/**
	 * Set font for legend title
	 * 
	 * @param f
	 */

	public void setFontLegent(Font f) {

		LegendTitle lt = chart[N1][N2].getLegend();
		lt.setItemFont(f);
		lt.setNotify(true);
	}

	/**
	 * Set font for legend title
	 * 
	 * @param f
	 */

	public void setFontTitle(Font f) {

		Title lt = chart[N1][N2].getTitle();

	}

	/**
	 * Draw X-Y data from arrays
	 * 
	 * @param x
	 *            X values
	 * @param y
	 *            Y values
	 */
	public void draw(double[] x, double y[]) {

		XYSeriesCollection c = new XYSeriesCollection();
		XYSeries SerData = new XYSeries("XY data");
		for (int i = 0; i < x.length; i++)
			SerData.add(x[i], y[i]);
		((XYSeriesCollection) c).addSeries(SerData);
		type[N1][N2].put(new Integer(indexdat[N1][N2]), "array");

		xyplot[N1][N2].setDataset(indexdat[N1][N2], c);
		xyplot[N1][N2].setRenderer(indexdat[N1][N2],
				new StandardXYItemRenderer());
		rdat[N1][N2].add(new StandardXYItemRenderer());

		indexdat[N1][N2]++;
		update(N1, N2);
	}

	/**
	 * Draw a F1D function on the canvas. For several functions, consider the
	 * add() method.
	 * 
	 * @param f1d
	 *            F1D function
	 */
	public void draw(F1D f1) {
		add(f1);
		update(N1, N2);
	}

	/**
	 * Draw a H1D histogram on the canvas. For several histograms, consider the
	 * add() method.
	 * 
	 * @param f1d
	 *            H1D histograms
	 */
	public void draw(H1D f1) {
		add(f1);
		update(N1, N2);
	}

	/**
	 * Add a F1D function to the canvas. Call update() to draw all functions.
	 * Use the methods setColor() etc. of F1D function to change the style
	 *  If You set range during F1D initialization, it will be used.
	 * If not, canvas range is used.
	 * 
	 * @param f1d
	 *            F1D function
	 * 
	 */
	public void add(F1D f1) {
		int Bin = f1.getPoints();
		
		
		if (f1.getMin() == f1.getMax()) {
		    f1.eval(Min,Max,Bin); 
		} else {
		   f1.eval(f1.getMin(), f1.getMax(), Bin); // evaluate
		}
		
		XYSeriesCollection c = new XYSeriesCollection();
		XYSeries SerData = new XYSeries(f1.getName());
		for (int i = 0; i < Bin; i++)
			SerData.add(f1.getX(i), f1.getY(i));

		c.addSeries(SerData);
		xyplot[N1][N2].setDataset(indexdat[N1][N2], c);

		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true,
				false);
		renderer.setDrawSeriesLineAsPath(true);
		LinePars lpp = f1.getDrawOption();
		float width = (float) lpp.getPenWidth();
		int style = f1.getLineStyle();
		float dash = lpp.getDashLength();
		renderer.setSeriesStroke(0, getStrokes(style, width, dash));
		renderer.setSeriesPaint(0, f1.getColor());

		type[N1][N2].put(new Integer(indexdat[N1][N2]), "f");
		rdat[N1][N2].add(renderer);
		indexdat[N1][N2]++;

	}

	/**
	 * Add a H1D histogram to the chart. Call update() method to view it.
	 * 
	 * @param h1
	 *            H1D histogram
	 */
	public void add(H1D h1) {

		IAxis axis = h1.get().axis();

		XYIntervalSeriesCollection c = new XYIntervalSeriesCollection();
		XYIntervalSeries SerData = new XYIntervalSeries(h1.getTitle());
		int ibins = axis.bins();
		for (int i = 0; i < ibins; i++) {
			SerData.add(h1.binMean(i), axis.binLowerEdge(i),
					axis.binUpperEdge(i), h1.binHeight(i), 0, h1.binHeight(i));

		}
		c.addSeries(SerData);
		xyplot[N1][N2].setDataset(indexdat[N1][N2], c);

		XYBarRenderer render = new XYBarRenderer();
		LinePars lpp = h1.getDrawOption();
		Color cfill = lpp.getFillColor();
		float width = (float) lpp.getPenWidth();
		int style = h1.getLineStyle();
		float dash = lpp.getDashLength();
		StandardXYBarPainter paint = new StandardXYBarPainter();
		render.setBarPainter(paint);
		render.setDrawBarOutline(true);
		render.setShadowVisible(false);
		render.setBaseStroke(getStrokes(style, width, dash));
		render.setBasePaint(cfill);
		render.setBaseOutlinePaint(h1.getColor());
		render.setBaseOutlineStroke(getStrokes(style, width, dash));
		render.setSeriesStroke(0, getStrokes(style, width, dash));
		render.setSeriesPaint(0, cfill);
		render.setSeriesOutlinePaint(0, h1.getColor());
		if (h1.isFilled()) {
			render.setSeriesFillPaint(0, cfill);
		}

		// System.out.println(render.toString());
		// System.out.println(rdat[N1][N2]);
		rdat[N1][N2].add(render);
		type[N1][N2].put(new Integer(indexdat[N1][N2]), "h");
		indexdat[N1][N2]++;

	}

	/**
	 * Draw X-Y data from arrays
	 * 
	 * @param x
	 *            X values
	 * @param y
	 *            Y values
	 */
	public void draw(ArrayList<Double> x, ArrayList<Double> y) {

		/*
		 * XYSeries SerData = new XYSeries("XY data");
		 * 
		 * for (int i = 0; i < x.size(); i++) SerData.add((double) x.get(i),
		 * (double) y.get(i)); ((XYSeriesCollection)
		 * cdat2[N1][N2]).addSeries(SerData);
		 * 
		 * chart[N1][N2] = ChartFactory.createScatterPlot( title[N1][N2], //
		 * title titleX[N1][N2], titleY[N1][N2], (XYSeriesCollection)
		 * cdat2[N1][N2], // dataset PlotOrientation.VERTICAL, true, // legend?
		 * yes true, // tooltips? yes false // URLs? no );// axis
		 * 
		 * chart[N1][N2].setAntiAlias(antiAlias);
		 * chart[N1][N2].setBorderPaint(DEFAULT_BG_COLOR);
		 * chart[N1][N2].setBackgroundPaint(DEFAULT_BG_COLOR);
		 * chart[N1][N2].setBorderVisible(false);
		 * 
		 * update(N1, N2);
		 */
	}

	/**
	 * Legend position
	 * 
	 * @param e
	 */

	public void setLegendPosition(RectangleEdge e) {
		LegendTitle legend = chart[N1][N2].getLegend();
		legend.setPosition(e);
	}

	/**
	 * Sets whether or not this line style should draw the name in the legend of
	 * the graph.
	 * 
	 * @param b
	 *            true if the name should be shown
	 */
	public void setLegend(boolean b) {
		if (b == false)
			chart[N1][N2].removeLegend();

	}

	/**
	 * Draw data from a P1D. Drawing updated automatically. 1st level errors are
	 * shown if the dimension is above 5. Otherwise show X and Y. If you set
	 * setChartPolar() before,m you can also show polar coordinates.
	 * 
	 * @param d
	 *            input P1D container
	 */
	public void add(P1D d) {

		if (cdatPolar[N1][N2] != null) {
			XYSeries SerData = new XYSeries(d.getTitle());
			for (int i = 0; i < d.size(); i++)
				SerData.add(d.getX(i), d.getY(i));
			cdatPolar[N1][N2].addSeries(SerData);
			return; // return if this is polar chart
		}

		// assume XY chart

		// System.out.println(d.dimension());
		LinePars lpp = d.getDrawOption();
		float width = (float) lpp.getPenWidth();
		int style = d.getLineStyle();
		float dash = lpp.getDashLength();
		int symbol = lpp.getSymbol();
		Shape shape = getShape(symbol, lpp.getSymbolSize());

		// setSeriesShapesFilled(int series, java.lang.Boolean flag)
		// setShapesFilled(boolean filled)

		if (d.dimension() > 5) {
			XYIntervalSeriesCollection c = new XYIntervalSeriesCollection();
			XYIntervalSeries SerData = new XYIntervalSeries(d.getTitle());
			for (int i = 0; i < d.size(); i++)
				SerData.add(d.getX(i), d.getX(i) - d.getXleft(i),
						d.getX(i) + d.getXright(i), d.getY(i),
						d.getY(i) - d.getYlower(i), d.getY(i) + d.getYupper(i));
			c.addSeries(SerData);
			xyplot[N1][N2].setDataset(indexdat[N1][N2], c);

			XYErrorRenderer xyerrorrenderer = new XYErrorRenderer();
			xyerrorrenderer.setSeriesStroke(0, getStrokes(style, width, dash));
			xyerrorrenderer.setSeriesPaint(0, d.getColor());
			xyerrorrenderer.setSeriesOutlinePaint(0, d.getColor());
			xyerrorrenderer.setSeriesFillPaint(0, lpp.getFillColor());
			xyerrorrenderer.setSeriesShape(0, shape);
			if (symbol > 0 && symbol < 4)
				xyerrorrenderer.setSeriesShapesFilled(0, false);
			if (symbol > 3)
				xyerrorrenderer.setSeriesShapesFilled(0, true);

			rdat[N1][N2].add(xyerrorrenderer);
			type[N1][N2].put(new Integer(indexdat[N1][N2]), "pe");
			indexdat[N1][N2]++;

		} else if (d.dimension() == 4) {
			XYIntervalSeriesCollection c = new XYIntervalSeriesCollection();
			XYIntervalSeries SerData = new XYIntervalSeries(d.getTitle());
			for (int i = 0; i < d.size(); i++)
				SerData.add(d.getX(i), d.getX(i), d.getX(i), d.getY(i),
						d.getY(i) - d.getYlower(i), d.getY(i) + d.getYupper(i));

			c.addSeries(SerData);
			xyplot[N1][N2].setDataset(indexdat[N1][N2], c);

			XYErrorRenderer xyerrorrenderer = new XYErrorRenderer();
			xyerrorrenderer.setSeriesStroke(0, getStrokes(style, width, dash));
			xyerrorrenderer.setSeriesPaint(0, d.getColor());
			xyerrorrenderer.setSeriesOutlinePaint(0, d.getColor());
			xyerrorrenderer.setSeriesFillPaint(0, lpp.getFillColor());
			xyerrorrenderer.setSeriesShape(0, shape);
			if (symbol > 0 && symbol < 4)
				xyerrorrenderer.setSeriesShapesFilled(0, false);
			if (symbol > 3)
				xyerrorrenderer.setSeriesShapesFilled(0, true);

			rdat[N1][N2].add(xyerrorrenderer);
			type[N1][N2].put(new Integer(indexdat[N1][N2]), "pe");
			indexdat[N1][N2]++;

		} else if (d.dimension() == 3) {
			XYIntervalSeriesCollection c = new XYIntervalSeriesCollection();
			XYIntervalSeries SerData = new XYIntervalSeries(d.getTitle());
			for (int i = 0; i < d.size(); i++)
				SerData.add(d.getX(i), d.getX(i), d.getX(i), d.getY(i),
						d.getY(i) - d.getYupper(i), d.getY(i) + d.getYupper(i));

			c.addSeries(SerData);
			xyplot[N1][N2].setDataset(indexdat[N1][N2], c);

			XYErrorRenderer xyerrorrenderer = new XYErrorRenderer();
			xyerrorrenderer.setSeriesStroke(0, getStrokes(style, width, dash));
			xyerrorrenderer.setSeriesPaint(0, d.getColor());
			xyerrorrenderer.setSeriesOutlinePaint(0, d.getColor());
			xyerrorrenderer.setSeriesFillPaint(0, lpp.getFillColor());
			xyerrorrenderer.setSeriesShape(0, shape);
			if (symbol > 0 && symbol < 4)
				xyerrorrenderer.setSeriesShapesFilled(0, false);
			if (symbol > 3)
				xyerrorrenderer.setSeriesShapesFilled(0, true);

			rdat[N1][N2].add(xyerrorrenderer);
			type[N1][N2].put(new Integer(indexdat[N1][N2]), "pe");
			indexdat[N1][N2]++;

		} else {
			XYSeriesCollection c = new XYSeriesCollection();
			XYSeries SerData = new XYSeries(d.getTitle());
			for (int i = 0; i < d.size(); i++)
				SerData.add(d.getX(i), d.getY(i));
			c.addSeries(SerData);
			xyplot[N1][N2].setDataset(indexdat[N1][N2], c);

			XYLineAndShapeRenderer xyerrorrenderer = new XYLineAndShapeRenderer(
					false, true);
			xyerrorrenderer.setSeriesStroke(0, getStrokes(style, width, dash));
			xyerrorrenderer.setSeriesPaint(0, d.getColor());
			xyerrorrenderer.setSeriesOutlinePaint(0, d.getColor());

			xyerrorrenderer.setSeriesFillPaint(0, lpp.getFillColor());
			xyerrorrenderer.setSeriesShape(0, shape);
			if (symbol > 0 && symbol < 4)
				xyerrorrenderer.setSeriesShapesFilled(0, false);
			if (symbol > 3)
				xyerrorrenderer.setSeriesShapesFilled(0, true);

			rdat[N1][N2].add(xyerrorrenderer);
			type[N1][N2].put(new Integer(indexdat[N1][N2]), "p");
			indexdat[N1][N2]++;

		}

	}

	/**
	 * Set a value to pie data set
	 * 
	 * @param name
	 *            Name for this number
	 * @param value
	 *            Number
	 */

	public void valuePie(String name, double value) {

		if (cdatPie[N1][N2] != null)
			((DefaultPieDataset) cdatPie[N1][N2]).setValue(name, value);

	}

	/**
	 * Set a value to 3D pie data set. You should call update() method to
	 * display it
	 * 
	 * @param name
	 *            Name for this number
	 * @param value
	 *            Number
	 */

	public void valuePie3D(String name, double value) {
		if (cdatPie3D[N1][N2] != null)
			((DefaultPieDataset) cdatPie3D[N1][N2]).setValue(name, value);

	}

	/**
	 * Set a value to a Bar chart. You should call update() method to display it
	 * 
	 * @param value
	 *            Value
	 * @param series
	 *            Series
	 * @param category
	 *            category
	 */
	public void valueBar(double value, String series, String category) {

		if (cdatBar[N1][N2] != null)
			((DefaultCategoryDataset) cdatBar[N1][N2]).addValue(value,
					category, series);

	}

	/**
	 * Set a value to a area chart. You should call update() method to display
	 * it
	 * 
	 * @param value
	 *            Value
	 * @param series
	 *            Series
	 * @param category
	 *            category
	 */
	public void valueArea(double value, String series, String category) {

		if (cdatArea[N1][N2] != null)
			((DefaultCategoryDataset) cdatArea[N1][N2]).addValue(value,
					category, series);

	}

	/**
	 * Sets the range (min-max) displayed on the axis.
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
	public void setRange(int axis, double min, double max) {

		if (axis == 0) {

			xyplot[N1][N2].getDomainAxis().setRange(min, max);

		}

		if (axis == 1) {
			xyplot[N1][N2].getRangeAxis().setRange(min, max);
		}

	}

	/**
	 * Set auto-range in X and Y at the same time
	 * 
	 * @param b
	 *            if true, sets auto-range
	 */
	public void setAutoRange(boolean b) {

		xyplot[N1][N2].getDomainAxis().setAutoRange(b);
		xyplot[N1][N2].getRangeAxis().setAutoRange(b);

	}

	/**
	 * Set autorange in X and Y at the same time
	 * 
	 */
	public void setAutoRange() {
		setAutoRange(true);

	}

	/**
	 * Set antialiasing for the graphics of the current plot
	 * 
	 * @param setit
	 *            true if antialiasing is set
	 */
	public void setAntiAlias(boolean setit) {
		antiAlias = setit;
	}

	/**
	 * Get antialiasing for the graphics of the current plot
	 * 
	 * @return true if antialiasing is set
	 */
	public boolean getAntiAlias() {
		return antiAlias;
	}

	/**
	 * Sets whether or not using grid lines. Grid lines are lines drawn from
	 * tick to tick. They can be enabled/disabled per axis.
	 * 
	 * @param b
	 *            true if shown
	 */
	public void setGrid(boolean b) {
		xyplot[N1][N2].setDomainGridlinesVisible(b);
		xyplot[N1][N2].setRangeGridlinesVisible(b);
	}

	/**
	 * Sets whether or not using grid lines. Grid lines are lines drawn from
	 * tick to tick. They can be enabled/disabled per axis.
	 * 
	 * @param axis
	 *            defines to which axis this function applies. 0 means X axis, y
	 *            means y axis
	 * @param b
	 *            toggle, true if the the grid should be drawn.
	 */
	public void setGrid(int axis, boolean b) {
		if (axis == 1)
			xyplot[N1][N2].setDomainGridlinesVisible(b);
		if (axis == 0)
			xyplot[N1][N2].setRangeGridlinesVisible(b);
	}

	/**
	 * Sets color of the grid lines for all plots on the same canvas
	 * 
	 * @param c
	 *            Color
	 */
	public void setGridColor(Color c) {
		xyplot[N1][N2].setDomainGridlinePaint(c);
		xyplot[N1][N2].setRangeGridlinePaint(c);

	}

	/**
	 * Sets width of the line for the grid
	 * 
	 * @param c
	 *            width of the line
	 */
	public void setGridPenWidth(double width) {
		xyplot[N1][N2].setDomainGridlineStroke(new BasicStroke(axisPenwidth));
		xyplot[N1][N2].setRangeGridlineStroke(new BasicStroke(axisPenwidth));

	}

	/**
	 * Sets the width of the line for grid
	 * 
	 * @param axis
	 *            0 is X axcis, 1 is Y axis
	 * @param width
	 *            with of the line
	 */
	public void setGridPenWidth(int axis, double width) {
		if (axis == 1)
			xyplot[N1][N2]
					.setDomainGridlineStroke(new BasicStroke(axisPenwidth));
		if (axis == 0)
			xyplot[N1][N2]
					.setRangeGridlineStroke(new BasicStroke(axisPenwidth));

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
		xyplot[N1][N2].setBackgroundPaint(color);
	}

	/**
	 * Set a value to a Line chart. You should call update() method to display
	 * it
	 * 
	 * @param value
	 *            Value
	 * @param series
	 *            Series
	 * @param category
	 *            category
	 */
	public void valueLine(double value, String series, String category) {

		if (cdatLine[N1][N2] != null) {
			((DefaultCategoryDataset) cdatLine[N1][N2]).addValue(value,
					category, series);
		}
	}

	/**
	 * Set a value to a 3D Bar chart. You should call update() method to display
	 * it
	 * 
	 * @param value
	 *            Value
	 * @param series
	 *            Series
	 * @param category
	 *            category
	 */
	public void valueBar3D(double value, String series, String category) {

		if (cdatBar3D[N1][N2] != null) {
			((DefaultCategoryDataset) cdatBar3D[N1][N2]).addValue(value,
					category, series);
		}
	}

	/**
	 * Update the chart
	 * 
	 * @param N1
	 *            location in X
	 * @param N2
	 *            location in Y
	 */
	public void update(int N1, int N2) {

		if (cdatPie[N1][N2] != null || cdatPie3D[N1][N2] != null
				|| cdatBar[N1][N2] != null || cdatBar3D[N1][N2] != null
				|| cdatLine[N1][N2] != null || cdatArea[N1][N2] != null
				|| cdatPolar[N1][N2] != null) {

			if (cdatPie[N1][N2] != null) {
				chart[N1][N2] = ChartFactory.createPieChart(title[N1][N2],
						(PieDataset) cdatPie[N1][N2], true, true, true);

			} else if (cdatPie3D[N1][N2] != null) {
				chart[N1][N2] = ChartFactory.createPieChart3D(title[N1][N2],
						(PieDataset) cdatPie3D[N1][N2], true, true, true);
				PiePlot3D pieplot3d = (PiePlot3D) chart[N1][N2].getPlot();
				// pieplot3d.setStartAngle(290D);
				// pieplot3d.setDirection(Rotation.CLOCKWISE);
				// pieplot3d.setForegroundAlpha(0.5F);
				pieplot3d.setBackgroundPaint(DEFAULT_BG_COLOR);

			} else if (cdatBar[N1][N2] != null) {
				chart[N1][N2] = ChartFactory.createBarChart(title[N1][N2],
						titleX[N1][N2], titleY[N1][N2],
						(CategoryDataset) cdatBar[N1][N2],
						PlotOrientation.VERTICAL, true, true, true);
				CategoryPlot lineplot = (CategoryPlot) chart[N1][N2].getPlot();

				CategoryAxis yy = lineplot.getDomainAxis();
				yy.setTickLabelPaint(tickColor);
				yy.setTickLabelFont(tickFont);
				yy.setLabelPaint(labelColor);
				yy.setLabelFont(labelFont);
				yy.setAxisLineStroke(new BasicStroke(axisPenwidth));
				yy.setAxisLinePaint(Color.black);
				ValueAxis xx = lineplot.getRangeAxis();
				xx.setTickLabelPaint(tickColor);
				xx.setTickLabelFont(tickFont);
				xx.setLabelPaint(labelColor);
				xx.setLabelFont(labelFont);
				xx.setAxisLineStroke(new BasicStroke(axisPenwidth));
				xx.setAxisLinePaint(Color.black);

			} else if (cdatBar3D[N1][N2] != null) {
				chart[N1][N2] = ChartFactory.createBarChart3D(title[N1][N2],
						titleX[N1][N2], titleY[N1][N2],
						(CategoryDataset) cdatBar3D[N1][N2],
						PlotOrientation.VERTICAL, true, true, true);
			} else if (cdatLine[N1][N2] != null) {
				chart[N1][N2] = ChartFactory.createLineChart(title[N1][N2],
						titleX[N1][N2], titleY[N1][N2],
						(CategoryDataset) cdatLine[N1][N2],
						PlotOrientation.VERTICAL, true, true, true);
				CategoryPlot lineplot = (CategoryPlot) chart[N1][N2].getPlot();
				lineplot.setOutlineStroke(new BasicStroke(axisPenwidth));
				CategoryAxis yy = lineplot.getDomainAxis();
				yy.setTickLabelPaint(tickColor);
				yy.setTickLabelFont(tickFont);
				yy.setLabelPaint(labelColor);
				yy.setLabelFont(labelFont);
				yy.setAxisLineStroke(new BasicStroke(axisPenwidth));
				yy.setAxisLinePaint(Color.black);
				ValueAxis xx = lineplot.getRangeAxis();
				xx.setTickLabelPaint(tickColor);
				xx.setTickLabelFont(tickFont);
				xx.setLabelPaint(labelColor);
				xx.setLabelFont(labelFont);
				xx.setAxisLineStroke(new BasicStroke(axisPenwidth));
				xx.setAxisLinePaint(Color.black);

				LineAndShapeRenderer renderer = (LineAndShapeRenderer) lineplot
						.getRenderer();
				renderer.setBaseStroke(new BasicStroke(linestroke));
				for (int m = 0; m < cdatLine[N1][N2].getColumnCount() + 1; m++)
					renderer.setSeriesStroke(m, new BasicStroke(linestroke));

			} else if (cdatPolar[N1][N2] != null) {
				chart[N1][N2] = ChartFactory.createPolarChart(title[N1][N2],
						(XYSeriesCollection) cdatPolar[N1][N2], true, true,
						true);
				PolarPlot lineplot = (PolarPlot) chart[N1][N2].getPlot();
				ValueAxis yy = lineplot.getAxis();
				yy.setTickLabelPaint(tickColor);
				yy.setTickLabelFont(tickFont);
				yy.setLabelPaint(labelColor);
				yy.setLabelFont(labelFont);
				yy.setAxisLineStroke(new BasicStroke(axisPenwidth));
				yy.setAxisLinePaint(Color.black);

			} else if (cdatArea[N1][N2] != null) {

				chart[N1][N2] = ChartFactory.createAreaChart(title[N1][N2],
						titleX[N1][N2], titleY[N1][N2],
						(CategoryDataset) cdatArea[N1][N2],
						PlotOrientation.VERTICAL, true, true, true);
				CategoryPlot lineplot = (CategoryPlot) chart[N1][N2].getPlot();

				CategoryAxis yy = lineplot.getDomainAxis();
				yy.setTickLabelPaint(tickColor);
				yy.setTickLabelFont(tickFont);
				yy.setLabelPaint(labelColor);
				yy.setLabelFont(labelFont);
				yy.setAxisLineStroke(new BasicStroke(axisPenwidth));
				yy.setAxisLinePaint(Color.black);
				ValueAxis xx = lineplot.getRangeAxis();
				xx.setTickLabelPaint(tickColor);
				xx.setTickLabelFont(tickFont);
				xx.setLabelPaint(labelColor);
				xx.setLabelFont(labelFont);
				xx.setAxisLineStroke(new BasicStroke(axisPenwidth));
				xx.setAxisLinePaint(Color.black);

			}

			chart[N1][N2].setAntiAlias(antiAlias);
			chart[N1][N2].setBorderPaint(DEFAULT_BG_COLOR);
			chart[N1][N2].setBackgroundPaint(DEFAULT_BG_COLOR);
			chart[N1][N2].setBorderVisible(false);
			cp[N1][N2].setChart(chart[N1][N2]);
			return;
		}

		for (int i = 0; i < indexdat[N1][N2]; i++) {

			if (type[N1][N2].get(i) == "array")
				xyplot[N1][N2].setRenderer(i,
						(StandardXYItemRenderer) (rdat[N1][N2].get(i)));
			else if (type[N1][N2].get(i) == "f")
				xyplot[N1][N2].setRenderer(i,
						(XYLineAndShapeRenderer) (rdat[N1][N2].get(i)));
			else if (type[N1][N2].get(i) == "h")
				xyplot[N1][N2].setRenderer(i,
						(XYBarRenderer) (rdat[N1][N2].get(i)));
			else if (type[N1][N2].get(i) == "p")
				xyplot[N1][N2].setRenderer(i,
						(XYLineAndShapeRenderer) (rdat[N1][N2].get(i)));
			else if (type[N1][N2].get(i) == "pe")
				xyplot[N1][N2].setRenderer(i,
						(XYErrorRenderer) (rdat[N1][N2].get(i)));

			// System.out.println(i);

		}

		chart[N1][N2] = new JFreeChart(title[N1][N2],
				JFreeChart.DEFAULT_TITLE_FONT, xyplot[N1][N2], true);
		chart[N1][N2].setAntiAlias(antiAlias);
		chart[N1][N2].setBorderPaint(DEFAULT_BG_COLOR);
		chart[N1][N2].setBackgroundPaint(DEFAULT_BG_COLOR);
		chart[N1][N2].setBorderVisible(false);

		cp[N1][N2].setChart(chart[N1][N2]);

	}

	/**
	 * Returns current Chart
	 * 
	 * @return current chart
	 */
	public JFreeChart getChart() {

		return chart[N1][N2];

	}

	/**
	 * 
	 * Get X-axis
	 * */
	public Axis getAxisX() {
		return xAxis[N1][N2];

	}

	/**
	 * 
	 * Get Y-axis
	 **/
	public Axis getAxisY() {
		return yAxis[N1][N2];

	}

	/**
	 * 
	 * Get XY plot
	 **/
	public XYPlot getPlot() {
		return xyplot[N1][N2];

	}

	/**
	 * Returns current Chart panel
	 * 
	 * @return current Chart panel
	 */
	public ChartPanel getChartPanel() {

		return cp[N1][N2];

	}

	/**
	 * Returns the current data object
	 * 
	 * @return current data
	 */
	// public AbstractDataset getData() {
	//
	// return cdat[N1][N2];
	//
	// }

	/**
	 * Close the canvas (and dispose all components) Note: a memory leak is
	 * found - no time to investigate it. set to null all the stuff
	 */
	public void close() {

		mainFrame.setVisible(false);
		m_Close = new Thread1("Closing softly");
		if (!m_Close.Alive())
			m_Close.Start();

	}

	/**
	 * Quit the frame
	 * 
	 */

	public void quit() {

		doNotShowFrame();

		clearAll();
		clearData();

		for (int i1 = 0; i1 < N1final; i1++) {
			for (int i2 = 0; i2 < N2final; i2++) {
				title[i1][i2] = null;
				titleX[i1][i2] = null;
				titleY[i1][i2] = null;
				chart[i1][i2] = null;

				cp[i1][i2] = null;

			}
		}

		chart = null;

		cp = null;

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
	 * Show online documentation.
	 */
	public void doc() {

		String a = this.getClass().getName();
		a = a.replace(".", "/") + ".html";
		new HelpBrowser(HelpBrowser.JHPLOT_HTTP + a);

	}

	/**
	 * Close the frame from menu
	 */
	protected void quitFrame() {
		close();

	}

	@Override
	protected void showHelp() {
		// TODO Auto-generated method stub

	}

	protected void openReadDataDialog() {
		JOptionPane.showMessageDialog(getFrame(),
				"Not implemented for this canvas");
	}

	/**
	 * 
	 * @param type
	 *            =0 - solid, 1-dashed, 2-dashed-dotted,3 dotted
	 * @param width
	 *            width
	 * @return
	 */

	private Stroke getStrokes(int type, float width, float dash) {

		float[][] pattern = { { dash }, { dash, dash },
				{ dash, dash, dash * 0.3f, dash }, { dash * 0.3f, dash * 2f } };

		if (type == 0)
			return new BasicStroke(width, BasicStroke.CAP_BUTT,
					BasicStroke.JOIN_MITER, 10.0f); // solid line
		if (type == 1)
			return new BasicStroke(width, BasicStroke.CAP_BUTT,
					BasicStroke.JOIN_MITER, 10.0f, pattern[1], 0.0f); // dashed
																		// line
		if (type == 2)
			return new BasicStroke(width, BasicStroke.CAP_BUTT,
					BasicStroke.JOIN_MITER, 10.0f, pattern[2], 0.0f); // dash-dotted
																		// line
		if (type == 3)
			return new BasicStroke(width, BasicStroke.CAP_ROUND,
					BasicStroke.JOIN_MITER, 10.0f, pattern[3], 0.0f); // dotted
																		// line

		return new BasicStroke(width, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER, 10.0f);
	}

	/**
	 * Get shape depending on the marker index.
	 * 
	 * @param index
	 * @return
	 */
	private Shape getShape(int index, float size) {

		// Shape s = ShapeUtilities.createDiamond(4F);
		double x = -0.5 * size;
		if (index == 0)
			return new java.awt.geom.Ellipse2D.Double(x, x, size, size);
		if (index == 1)
			return new java.awt.geom.Rectangle2D.Double(x, x, size, size);
		if (index == 2)
			return ShapeUtilities.createDiamond(size);
		if (index == 3)
			return ShapeUtilities.createDownTriangle(size);

		if (index == 4)
			return new java.awt.geom.Ellipse2D.Double(x, x, size, size);
		if (index == 5)
			return new java.awt.geom.Rectangle2D.Double(x, x, size, size);
		if (index == 6)
			return ShapeUtilities.createDiamond(size);
		if (index == 7)
			return ShapeUtilities.createDownTriangle(size);

		if (index == 7)
			return ShapeUtilities.createRegularCross(0, size);
		if (index == 8)
			return ShapeUtilities.createDiagonalCross(0, size);
		if (index == 9)
			return ShapeUtilities.createDiagonalCross(0, size);
		if (index == 10)
			return ShapeUtilities.createDiagonalCross(0, size);
		if (index == 11)
			new java.awt.geom.Ellipse2D.Double(0.5, 0.5, 1, 1);
		if (index == 12)
			return ShapeUtilities.createDiagonalCross(0, size);

		return new java.awt.geom.Ellipse2D.Double(x, x, size, size);

	}

	public void drawToGraphics2D(Graphics2D g, int width, int height) {
		// self.chartCoordsMap = {} #Maps a chart to its raw screen coords, used
		// for converting coords
		g.setColor(Color.white);
		g.fillRect(0, 0, width, height);
		//
		// int boxWidth = width / this.chartarray[0].length;
		// int boxHeight = height / this.chartarray.length;

		int cols = N1final;
		int rows = N2final;
		int boxWidth = width / cols;
		int boxHeight = height / rows;

		//
		// # print "boxWidth ", boxWidth
		// # print "boxHeight ", boxHeight

		// for (int row = 0; row < chartarray.length; row++)
		int currentChartIndex = 0;

		for (int i2 = 0; i2 < rows; i2++) {
			for (int i1 = 0; i1 < cols; i1++) {

				currentChartIndex++;
				if (chart[i1][i2] != null) {

					int rowsUsed = 1;
					int colsUsed = 1;
					int chartX = boxWidth * i1;
					int chartY = boxHeight * i2;
					int chartwidth = boxWidth;
					int chartheight = boxHeight;
					// #Get Horizontalspace
					// for (int c = col; c > -1; c--)
					// {
					// // for c in range(col, -1, -1):
					// // if self.chartArray[row][c] == None:
					// if(this.chartarray[row][c] == null)
					// rowsUsed++;
					//
					// // rowsUsed = rowsUsed + 1
					// // #print "adding row"
					// }
					chartwidth = boxWidth * rowsUsed;
					chartheight = boxHeight;
					chartX = chartX - (rowsUsed - 1) * boxWidth;
					//
					// # chart.configureDomainAxes()
					// # chart.configureRangeAxes()
					//
					// #Testing axes ranges not updated
					// from org.jfree.chart.event import PlotChangeEvent
					// chart[i1][i2].plotChanged(new
					// PlotChangeEvent(chart[i1][i2].getXYPlot()));
					chart[i1][i2].plotChanged(new PlotChangeEvent(chart[i1][i2]
							.getPlot()));
					//
					ChartRenderingInfo info = new ChartRenderingInfo();
					//
					chart[i1][i2].draw(g, new java.awt.Rectangle(chartX,
							chartY, chartwidth, chartheight), new Point(chartX,
							chartY), info);
					// self.chartToInfoMap[chart] = info
					//
					// self.chartCoordsMap[chart] = [chartX ,chartY,chartwidth,
					// chartheight]

				}
			}
		}

	}

	/**
	 * 
	 * Export graph into an image file. The the image format is given by
	 * extension. "png", "jpg", "eps", "pdf", "svg". In case of "eps", "pdf" and
	 * "svg", vector graphics is used.
	 * 
	 * @param file
	 *            name of the image
	 */
	public void export(String filename) {
		int calculatedWidth = (int) getSizeX();
		int calculatedHeight = (int) getSizeY();

		if (calculatedWidth <= 0)
			calculatedWidth = 600;

		if (calculatedHeight <= 0)
			calculatedHeight = 400;

		this.export(filename, calculatedWidth, calculatedHeight);
	}

	/**
	 * Exports the image to some graphic format.
	 */
	protected void exportImage() {

		if (isBorderShown())
			showBorders(false);

		JFrame jm = getFrame();
		JFileChooser fileChooser = jhplot.gui.CommonGUI
				.openImageFileChooser(jm);

		if (fileChooser.showDialog(jm, "Save As") == 0) {

			final File scriptFile = fileChooser.getSelectedFile();
			if (scriptFile == null)
				return;
			else if (scriptFile.exists()) {
				int res = JOptionPane.showConfirmDialog(jm,
						"The file exists. Do you want to overwrite the file?",
						"", JOptionPane.YES_NO_OPTION);
				if (res == JOptionPane.NO_OPTION)
					return;
			}
			String mess = "write image  file ..";
			JHPlot.showStatusBarText(mess);
			Thread t = new Thread(mess) {
				public void run() {
					export(scriptFile.getAbsolutePath());
				};
			};
			t.start();
		}

	}

	/**
	 * Export graph into an image file. The the image format is given by
	 * extension. "png", "jpg", "eps", "pdf", "svg". In case of "eps", "pdf" and
	 * "svg", vector graphics is used.
	 * 
	 * @param filename
	 *            file name
	 * @param width
	 *            width
	 * @param height
	 *            hight
	 */
	public void export(String filename, int width, int height) {

		String fname = filename;
		String filetype = "pdf";
		int i = filename.lastIndexOf('.');
		if (i > 0) {
			filetype = fname.substring(i + 1);
		}

		try {
			if (filetype.equalsIgnoreCase("png")) {
				BufferedImage b = new BufferedImage(width, height,
						BufferedImage.TYPE_INT_RGB);
				Graphics2D g = b.createGraphics();

				drawToGraphics2D(g, width, height);
				g.dispose();
				ImageIO.write(b, "png", new File(fname));
			} else if (filetype.equalsIgnoreCase("jpg")
					|| filetype.equalsIgnoreCase("jpeg")) {
				BufferedImage b = new BufferedImage(width, height,
						BufferedImage.TYPE_INT_RGB);
				Graphics2D g = b.createGraphics();
				drawToGraphics2D(g, width, height);
				g.dispose();
				ImageIO.write(b, "jpg", new File(fname));

				/*
				 * } else if (filetype.equalsIgnoreCase("eps")) { try {
				 * ImageType currentImageType = ImageType.EPS; Rectangle r = new
				 * Rectangle (0, 0, width, height);
				 * Export.exportComponent(getCanvasPanel(), r, new
				 * File(filename), currentImageType); } catch (IOException e) {
				 * e.printStackTrace(); } catch
				 * (org.apache.batik.transcoder.TranscoderException e) {
				 * e.printStackTrace(); }
				 */

			} else if (filetype.equalsIgnoreCase("ps")) {
				try {
					ImageType currentImageType = ImageType.PS;
					Rectangle r = new Rectangle(0, 0, width, height);
					Export.exportComponent(getCanvasPanel(), r, new File(
							filename), currentImageType);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (org.apache.batik.transcoder.TranscoderException e) {
					e.printStackTrace();
				}

			} else if (filetype.equalsIgnoreCase("eps")) {

				try {
					FileOutputStream outputStream = new FileOutputStream(fname);
					org.jibble.epsgraphics.EpsGraphics2D g = new org.jibble.epsgraphics.EpsGraphics2D(
							"HChart canvas", outputStream, 0, 0, width, height);// #Create
					// a
					// new
					// document
					// with
					// bounding
					// box
					// 0 <=
					// x <=
					// 100
					// and
					// 0 <=
					// y <=
					// 100.
					drawToGraphics2D(g, width, height);
					g.flush();
					g.close();
				} catch (Exception e) {
					e.printStackTrace();
					System.err.println("Problem writing eps");
				}

			} else if (filetype.equalsIgnoreCase("pdf")) {

				try {
					FileOutputStream outputStream = new FileOutputStream(fname);
					com.lowagie.text.pdf.FontMapper mapper = new com.lowagie.text.pdf.DefaultFontMapper();
					com.lowagie.text.Rectangle pagesize = new com.lowagie.text.Rectangle(
							width, height);
					com.lowagie.text.Document document = new com.lowagie.text.Document(
							pagesize, 50, 50, 50, 50);
					try {
						com.lowagie.text.pdf.PdfWriter writer = com.lowagie.text.pdf.PdfWriter
								.getInstance(document, outputStream);
						// document.addAuthor("JFreeChart");
						// document.addSubject("Jylab");
						document.open();
						com.lowagie.text.pdf.PdfContentByte cb = writer
								.getDirectContent();
						com.lowagie.text.pdf.PdfTemplate tp = cb
								.createTemplate(width, height);
						Graphics2D g = tp.createGraphics(width, height, mapper);
						// Rectangle2D r2D = new Rectangle2D.Double(0, 0, width,
						// height);

						drawToGraphics2D(g, width, height);
						g.dispose();
						cb.addTemplate(tp, 0, 0);

					} catch (com.lowagie.text.DocumentException de) {
						System.err.println(de.getMessage());
					}
					document.close();
				} catch (Exception e) {
					e.printStackTrace();
					System.err
							.println("Cannot find itext library, cannot create pdf.");
				}

			} else if (filetype.equalsIgnoreCase("svg")) {

				try {

					// import org.apache.batik.dom.GenericDOMImplementation;
					// import org.apache.batik.svggen.SVGGraphics2D;

					org.w3c.dom.DOMImplementation domImpl = org.apache.batik.dom.GenericDOMImplementation
							.getDOMImplementation();
					// Create an instance of org.w3c.dom.Document
					org.w3c.dom.Document document = domImpl.createDocument(
							null, "svg", null);
					// Create an instance of the SVG Generator
					org.apache.batik.svggen.SVGGraphics2D svgGenerator = new org.apache.batik.svggen.SVGGraphics2D(
							document);
					svgGenerator.setSVGCanvasSize(new Dimension(width, height));
					// set the precision to avoid a null pointer exception in
					// Batik 1.5
					svgGenerator.getGeneratorContext().setPrecision(6);
					// Ask the chart to render into the SVG Graphics2D
					// implementation

					drawToGraphics2D(svgGenerator, width, height);
					// chart.draw(svgGenerator, new Rectangle2D.Double(0, 0,
					// width, height), null);
					// Finally, stream out SVG to a file using UTF-8 character
					// to
					// byte encoding
					boolean useCSS = true;
					Writer out = new OutputStreamWriter(new FileOutputStream(
							new File(filename)), "UTF-8");
					svgGenerator.stream(out, useCSS);
					out.close();

				} catch (org.w3c.dom.DOMException e) {
					System.err.println("Problem writing to SVG");
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
					System.err.println("Missing Batik libraries?");
				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}

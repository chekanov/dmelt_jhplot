package jhplot;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.awt.geom.Rectangle2D;

import javax.swing.JOptionPane;

import jhplot.gui.*;
import hep.aida.*;

import javax.swing.JFormattedTextField;

import kcl.waterloo.defaults.*;
import kcl.waterloo.graphics.*;
import kcl.waterloo.graphics.plots2D.*;
import kcl.waterloo.marker.*;
import kcl.waterloo.plotmodel2D.*;

import java.awt.Dimension;

/**
 * Canvas to plot various ScaVis data. The canvas is based on the Waterfloo
 * package. One can plot scatter, histogram, polar, pie charts, real-time
 * charts. Also supports for X-Y data points with errors on X and Y. Built-in
 * "click-and-edit" support and publication-quality output to vector-based
 * graphics formats for printing.
 * 
 * @author S.Chekanov
 * 
 */
public class WPlot {

	private boolean isSetRange = false;
	private WPlotFrame frame;
	private GJGraphContainer graphContainer;
	private GJGraph graph;
	private GJPlotInterface pjplot;
	private boolean antialiasing = true;
	private Rectangle2D range = new Rectangle.Double(0, 1, 0, 1);

	/**
	 * Construct a frame with a canvas. Default: do not show it.
	 */
	public WPlot() {
		this("WPlot", 600, 400);
	}

	/**
	 * Construct a frame with a canvas. Default: do not show it.
	 * 
	 * @param title
	 */
	public WPlot(String title) {
		this(title, 600, 400);
	}

	/**
	 * Construct a frame with the plot. Do not show it.
	 * 
	 * @param width
	 *            frame width
	 * @param height
	 *            frame height
	 */
	public WPlot(int width, int height) {
		this("WPlot", width, height);
	}

	/**
	 * Construct a frame with the plot. Do not show it.
	 * 
	 * @param title
	 *            i Title
	 * @param width
	 *            frame width
	 * @param height
	 *            frame height
	 */
	public WPlot(String title, int width, int height) {

		frame = new WPlotFrame(title, width, height, false);
		graph = GJGraph.createInstance();
		graphContainer = GJGraphContainer.createInstance(graph);
		frame.add(graphContainer);
		range = new Rectangle.Double(0, 1, 0, 1);
		isSetRange = false;
		// int sz = graphContainer.getFont().getSize();
		// System.out.println(sz);
		graphContainer.setFont(new Font("Arial", Font.BOLD, 14));

		int lsize = 16;
		GJAxisPanel left = graph.getLeftAxisPanel();
		left.setLabelFont(new Font("Arial", Font.BOLD, lsize));
		GJAxisPanel right = graph.getRightAxisPanel();
		right.setLabelFont(new Font("Arial", Font.BOLD, lsize));
		GJAxisPanel top = graph.getTopAxisPanel();
		top.setLabelFont(new Font("Arial", Font.BOLD, lsize));
		GJAxisPanel botton = graph.getBottomAxisPanel();
		botton.setLabelFont(new Font("Arial", Font.BOLD, lsize));

	}

	/**
	 * Set antialiasing for the graphics of the current plot
	 * 
	 * @param setit
	 *            true if antialiasing is set
	 */
	public void setAntiAlias(boolean setit) {
		antialiasing = setit;
		// pjplot.setAntialiasing(antialiasing);
		// graph.setTextAntialiasing(antialiasing);
	}

	/**
	 * Get antialiasing for the graphics of the current plot
	 * 
	 * @return true if antialiasing is set
	 */
	public boolean getAntiAlias() {
		return antialiasing;
	}

	/**
	 * Sets font for small ticks on X and Y axis.
	 * 
	 * @param f
	 *            Font
	 */
	public void setFontAxis(Font f) {
		graphContainer.setFont(f);

	}

	/**
	 * Set background for the plot.
	 * 
	 * @param c
	 *            background color.
	 */
	public void setBackground(Color c) {
		frame.setBackground(c);
	}

	/**
	 * Set global title for this plot.
	 * 
	 * @param title
	 *            plot title
	 */
	public void setGTitle(String title) {
		// pjplot.getYData().setName(title);
		// graphContainer.setTitle(true);
		//JFormattedTextField jtitle = new JFormattedTextField();
		//jtitle.setBorder(null);
		//jtitle.setFont(new Font("Arial", Font.BOLD, 24));
		//jtitle.setText(title);
		// graphContainer.setTitle(jtitle);
		//graphContainer.setFont(new Font("Arial", Font.BOLD, 20));
		//graph.setFont( new Font("Arial", Font.BOLD, 20));
		graphContainer.setTitleText(title);
	}

	/**
	 * Add a legend (displayed at the upper right) for the specified data set
	 * with the specified string. Short strings generally fit better than long
	 * strings. If the string is empty, or the argument is null, then no legend
	 * is added.
	 * 
	 * @param dataset
	 *            The dataset index.
	 * @param legend
	 *            The label for the dataset.
	 * @see #renameLegend(int, String)
	 */
	public void addLegend(int dataset, String legend) {

	}

	/**
	 * Specify a tick mark for the X axis. The label given is placed on the axis
	 * at the position given by position. If this is called once or more,
	 * automatic generation of tick marks is disabled. The tick mark will appear
	 * only if it is within the X range.
	 * 
	 * @param label
	 *            The label for the tick mark.
	 * @param position
	 *            The position on the X axis.
	 */
	public synchronized void addXTick(String label, double position) {

	}

	/**
	 * Clear the plot of all data points.
	 */
	public synchronized void clear() {
	}

	/**
	 * Clear all legends. This will show up on the next redraw.
	 */
	public synchronized void clearLegends() {
	}

	/**
	 * Set the foreground color.
	 * 
	 * @param foreground
	 *            The foreground color.
	 */
	public void setForeground(Color foreground) {
		frame.setForeground(foreground);
	}

	/**
	 * Set the label font, which is used for axis labels and legend labels. The
	 * font names understood are those understood by java.awt.Font.decode().
	 * 
	 * @param name
	 *            font name.
	 */
	public void setLabelFont(Font name) {
		GJAxisPanel left = graph.getLeftAxisPanel();
		left.setLabelFont(name);
		GJAxisPanel right = graph.getRightAxisPanel();
		right.setLabelFont(name);
		GJAxisPanel top = graph.getTopAxisPanel();
		top.setLabelFont(name);
		GJAxisPanel button = graph.getBottomAxisPanel();
		button.setLabelFont(name);

	}

	/**
	 * Set font of the title.
	 * 
	 * @param name
	 *            A font name.
	 */
	public void setTitleFont(String name) {
	}

	/**
	 * Set the label for the X (horizontal) axis.
	 * 
	 * @param label
	 *            The label.
	 */
	public void setNameX(String label) {
		graph.setXLabel(label);

	}

	/**
	 * Specify whether the X axis is drawn with a logarithmic scale. If you
	 * would like to have the X axis drawn with a logarithmic axis, then
	 * setXLog(true) should be called before adding any data points.
	 * 
	 * @param xlog
	 *            If true, logarithmic axis is used.
	 */
	public void setXLog(boolean xlog) {
	}

	/**
	 * Sets the range (min-max) displayed on X and Y
	 * 
	 * @param minX
	 *            minimum value on the X axis
	 * @param maxX
	 *            maximum value on the X axis
	 * @param minY
	 *            minimum value on the Y axis
	 * @param maxY
	 *            maximum value on the Y axis
	 * 
	 */
	public void setRange(double minX, double maxX, double minY, double maxY) {
		setAutoRange(false);
		range=new Rectangle.Double(minX, minY, maxX, maxY);
	}

	/**
	 * Sets the range (min-max) displayed on the axis.
	 * 
	 * @param axis
	 *            defines to which axis this function applies: <br>
	 *            0 means X, <br>
	 *            1 means Y.
	 * @param min
	 *            minimum value on the axis
	 * @param max
	 *            maximum value on the axis
	 */
	public void setRange(int axis, double min, double max) {
		setAutoRange(false);
		Rectangle2D rc = graph.getDataRange();
		if (axis == 0)  range=new Rectangle.Double(min, rc.getY(), max, rc.getHeight());
		if (axis == 1)  range=new Rectangle.Double(rc.getX(), min, rc.getWidth(), max);
	
	}

	/**
	 * Set the label for the Y (vertical) axis.
	 * 
	 * @param label
	 *            The label.
	 */
	public void setNameY(String label) {
		graph.setYLabel(label);
	}

	/**
	 * Set subtitle of the plot.
	 * 
	 * @param label
	 *            The label.
	 */

	public void setName(String label) {
		//JFormattedTextField jtitle = new JFormattedTextField();
		//jtitle.setBorder(null);
		//jtitle.setFont(new Font("Arial", Font.BOLD, 18));
		//jtitle.setText(label);
		//graphContainer.setSubTitle(jtitle);
		//graphContainer.setFont(new Font("Arial", Font.BOLD, 20));
		//graph.setFont( new Font("Arial", Font.BOLD, 20));
		graphContainer.setSubTitleText(label);
		

	}

	/**
	 * Specify whether the Y axis is drawn with a logarithmic scale. If you
	 * would like to have the Y axis drawn with a logarithmic axis, then
	 * setYLog(true) should be called before adding any data points.
	 * 
	 * @param ylog
	 *            If true, logarithmic axis is used.
	 */
	public void setYLog(boolean ylog) {
	}

	/**
	 * Set the Y (vertical) range of the plot. If this is not done explicitly,
	 * then the range is computed automatically from data available when the
	 * plot is drawn. If min and max are identical, then the range is
	 * arbitrarily spread by 0.1.
	 * 
	 * @param min
	 *            The bottom extent of the range.
	 * @param max
	 *            The top extent of the range.
	 */
	public void setYRange(double min, double max) {
		setRange(1,min,max);
	}

	/**
	 * Draw a single data set as marks (default).
	 * 
	 * @param x
	 *            array of x values
	 * @param y
	 *            array of y values
	 */
	public void draw(double[] x, double[] y) {
		draw(x, y, "none");
	}

	/**
	 * Clear the plot of data points in the specified dataset. This calls
	 * repaint() to request an update of the display.
	 * 
	 * @param dataset
	 *            The dataset to clear.
	 */
	public void clear(int dataset) {

	}

	/**
	 * Rescale so that the data that is currently plotted just fits.
	 */
	public void setAutoRange() {
		isSetRange = false;
		graph.autoScale();

	}

	/**
	 * Rescale so that the data that is currently plotted just fits.
	 * 
	 * @param auto
	 *            if true, then autorange
	 */
	public void setAutoRange(boolean auto) {
		if (auto) {
			isSetRange = false;
			graph.autoScale();
		}
		if (!auto)
			isSetRange = true;
	}

	/**
	 * Repaint the plot and update all graphics.
	 */
	public void update() {

		frame.validate();
		frame.repaint();
	}

	/**
	 * Sets whether or not using grid lines. Grid lines are lines drawn from
	 * tick to tick. They can be enabled/disabled per axis.
	 * 
	 * @param b
	 *            true if shown
	 */
	public void setGrid(boolean b) {
		if (b)
			graph.paintGrid();
	}

	/**
	 * Draw H1D histograms
	 * 
	 * @param h1
	 *            H1D histogram to be shown.
	 */
	public void draw(H1D h1) {

		double binwidth = h1.getBinSize();
		IAxis axis = h1.get().axis();
		double[] xx = new double[axis.bins()];
		double[] yy = new double[axis.bins()];
		double[] ye = new double[axis.bins()];
		double[] xcenter = new double[axis.bins()];
		for (int i = 0; i < axis.bins(); i++) {
			double x = h1.binLowerEdge(i); // The weighted mean of a
			
			double y = h1.binHeight(i);
			double y1 = h1.binError(i);
			double y2 = h1.binError(i);
			xx[i] = x;
			yy[i] = y;
			ye[i]=y1;
			xcenter[i]=h1.binCenter(i);
		}

		pjplot = GJBar.createInstance();
		if (h1.isFilled()) pjplot.setFill(h1.getFillColor());
		else pjplot.setFill(new Color(0).white);	
		pjplot.setXData(xx);
		pjplot.setYData(yy);
		
		
		
		String styleLine = "-";
		float width = h1.getPenWidth();
		pjplot.setLineColor(h1.getColor());
		pjplot.setLineStroke(GJUtilities.makeStroke(width, styleLine));
		//((BarExtra) pjplot.getDataModel().getExtraObject()).setMode(BarExtra.MODE.HIST);
		((BarExtra) pjplot.getDataModel().getExtraObject()).setMode(BarExtra.MODE.HISTC);
		graph.add(pjplot);
		
        
        
		if (h1.isErrY()==true || h1.isErrX()==true){
        pjplot = GJErrorBar.createInstance();
		pjplot.setXData(xcenter);
		pjplot.setYData(yy);
		pjplot.setExtraData1(ye); // upper
		pjplot.setExtraData3(ye); // lower
		graph.add(pjplot);
		}
		
		
		
	    if (isSetRange) graph.setAxesBounds(range);
        if (antialiasing) graph.setAntialiasing(true);
        else  graph.setAntialiasing(false);
		
		update();
	}

	/**
	 * Draw P0D array as a pie chart.
	 * 
	 * @param p0d
	 *            P0D object to show
	 */
	public void draw(P0D p0d) {
		pjplot = GJPie.createInstance();
		pjplot.setXData(p0d.getArray());
		graph.add(pjplot);
		 if (isSetRange) graph.setAxesBounds(range);
	        if (antialiasing) graph.setAntialiasing(true);
	        else  graph.setAntialiasing(false);
		update();
	}

	/**
	 * Draw P0I array as a pie chart.
	 * 
	 * @param p0d
	 *            P0D object to show
	 */
	public void draw(P0I p0d) {
		pjplot = GJPie.createInstance();
		pjplot.setXData(p0d.getArray());
		graph.add(pjplot);
		 if (isSetRange) graph.setAxesBounds(range);
	        if (antialiasing) graph.setAntialiasing(true);
	        else  graph.setAntialiasing(false);
		update();
	}

	/**
	 * Draw P1D array in X-Y space. If errors are included to P1D, they will be
	 * shown. Second-level error is not supported.
	 * 
	 * @param p1d
	 *            X-Y + error data
	 */
	public void draw(P1D p1d) {
		if (p1d.isBars() == true)
			draw(p1d, "Bar");
		else if (p1d.getLineParm().drawLine() == true)
			draw(p1d, "Line");
		else
			draw(p1d, "none"); // symbols

	}

	/**
	 * Draw X-Y data. Data can be plotted in a polar coordinates using the
	 * options PolarBar, PolarLine, PolarScatter, PolarStem. Also, one can show
	 * as bars and lines.
	 * 
	 * @param x
	 *            X data
	 * @param y
	 *            Y data
	 * @param opt
	 *            option for how to plot? Options can be: PolarBar,<br>
	 *            PolarLine, <br>
	 *            PolarScatter, <br>
	 *            PolarStem, <br>
	 *            Bar, <br>
	 *            Line <br>
	 *            . If none is given, the usual symbols in cartesian coordinate
	 *            is assumed.
	 */
	public void draw(double[] x, double[] y, String opt) {
		opt = opt.trim();
		if (opt.equalsIgnoreCase("PolarBar"))
			pjplot = GJPolarBar.createInstance();
		else if (opt.equalsIgnoreCase("PolarLine"))
			pjplot = GJPolarLine.createInstance();
		else if (opt.equalsIgnoreCase("PolarScatter"))
			pjplot = GJPolarScatter.createInstance();
		else if (opt.equalsIgnoreCase("PolarStem"))
			pjplot = GJPolarStem.createInstance();
		else if (opt.equalsIgnoreCase("Bar"))
			pjplot = GJBar.createInstance();
		else if (opt.equalsIgnoreCase("Line"))
			pjplot = GJLine.createInstance();
		else
			pjplot = GJScatter.createInstance();
		pjplot.setXData(x);
		pjplot.setYData(y);
		graph.add(pjplot);
		 if (isSetRange) graph.setAxesBounds(range);
	        if (antialiasing) graph.setAntialiasing(true);
	        else  graph.setAntialiasing(false);
		update();

	}

	/**
	 * Draw a function. Data can be plotted in polar coordinates using the
	 * options: PolarBar, PolarLine, PolarScatter, PolarStem. Also, one can show
	 * as Bars or Scatter.
	 * 
	 * @param f1d
	 *            a function
	 * @param opt
	 *            option for how to plot? Options can be: PolarBar,<br>
	 *            PolarLine, <br>
	 *            PolarScatter, <br>
	 *            PolarStem, <br>
	 *            Bar, <br>
	 *            Scatter <br>
	 *            . If none is given, the usual X-Y coordinates are assumed.
	 */
	public void draw(F1D f1d, String opt) {
		opt = opt.trim();
		if (opt.equalsIgnoreCase("PolarBar"))
			pjplot = GJPolarBar.createInstance();
		else if (opt.equalsIgnoreCase("PolarLine"))
			pjplot = GJPolarLine.createInstance();
		else if (opt.equalsIgnoreCase("PolarScatter"))
			pjplot = GJPolarScatter.createInstance();
		else if (opt.equalsIgnoreCase("PolarStem"))
			pjplot = GJPolarStem.createInstance();
		else if (opt.equalsIgnoreCase("Bar"))
			pjplot = GJBar.createInstance();
		else if (opt.equalsIgnoreCase("Scatter"))
			pjplot = GJScatter.createInstance();
		else
			pjplot = GJLine.createInstance();
		
		
		Rectangle2D rc = graph.getDataRange();
		
		if (f1d.getMin() == f1d.getMax()) {
		  double min=rc.getMinX();
		  double max=rc.getMaxX();
		  f1d.eval(min,max,f1d.getPoints()); // evaluate first
		} else {
		   f1d.eval(f1d.getMin(), f1d.getMax(), f1d.getPoints()); // evaluate
			
		}
		
		pjplot.setXData(f1d.getArrayX());
		pjplot.setYData(f1d.getArrayY());

		String styleLine = "-";
		float width = f1d.getPenWidth();
		pjplot.setLineColor(f1d.getColor());
		pjplot.setLineStroke(GJUtilities.makeStroke(width, styleLine));
		graph.add(pjplot);
		 if (isSetRange) graph.setAxesBounds(range);
	        if (antialiasing) graph.setAntialiasing(true);
	        else  graph.setAntialiasing(false);
		update();
	}

	/**
	 * Draw a F1D function.
	 * 
	 * @param f1d
	 *            a function
	 */
	public void draw(F1D f1d) {
		draw(f1d, "none");
	}

	/**
	 * Draw P1D array in X-Y space. If errors are included to P1D, they will be
	 * shown. Second-level error is not supported. Data can be plotted in a
	 * polar coordinates using the options PolarBar, PolarLine, PolarScatter,
	 * PolarStem. Also, one can show as bars.
	 * 
	 * @param p1d
	 *            data in X and Y plus error.
	 * @param opt
	 *            how to plot? Options can be: <br>
	 *            PolarBar,<br>
	 *            PolarLine, <br>
	 *            PolarScatter, <br>
	 *            PolarStem, <br>
	 *            Bar, <br>
	 *            Line <br>
	 *            If none is given, the usual X-Y coordinates are assumed.
	 */
	public void draw(P1D p1d, String opt) {

		opt = opt.trim();
        boolean sc=false;
        
		if (p1d.getDimension() == 2) {
			if (opt.equalsIgnoreCase("PolarBar"))
				pjplot = GJPolarBar.createInstance();
			else if (opt.equalsIgnoreCase("PolarLine"))
				pjplot = GJPolarLine.createInstance();
			else if (opt.equalsIgnoreCase("PolarScatter"))
				pjplot = GJPolarScatter.createInstance();
			else if (opt.equalsIgnoreCase("PolarStem"))
				pjplot = GJPolarStem.createInstance();
			else if (opt.equalsIgnoreCase("Bar"))
				pjplot = GJBar.createInstance();
			else if (opt.equalsIgnoreCase("Line"))
				pjplot = GJLine.createInstance();
			else  {sc=true; pjplot = GJScatter.createInstance();}
			
			pjplot.setXData(p1d.getArrayX());
			pjplot.setYData(p1d.getArrayY());
		}

		else if (p1d.getDimension() == 3) {
			if (opt.equalsIgnoreCase("PolarBar"))
				pjplot = GJPolarBar.createInstance();
			else if (opt.equalsIgnoreCase("PolarLine"))
				pjplot = GJPolarLine.createInstance();
			else if (opt.equalsIgnoreCase("PolarScatter"))
				pjplot = GJPolarScatter.createInstance();
			else if (opt.equalsIgnoreCase("PolarStem"))
				pjplot = GJPolarStem.createInstance();
			else if (opt.equalsIgnoreCase("Bar"))
				pjplot = GJBar.createInstance();
			else if (opt.equalsIgnoreCase("Line"))
				pjplot = GJLine.createInstance();
			else { sc=true; pjplot = GJErrorBar.createInstance();}
			
			pjplot.setXData(p1d.getArrayX());
			pjplot.setYData(p1d.getArrayY());
			pjplot.setExtraData1(p1d.getArrayYupper()); // upper
			pjplot.setExtraData3(p1d.getArrayYupper()); // lower
		}

		else if (p1d.getDimension() == 4) {
			if (opt.equalsIgnoreCase("PolarBar"))
				pjplot = GJPolarBar.createInstance();
			else if (opt.equalsIgnoreCase("PolarLine"))
				pjplot = GJPolarLine.createInstance();
			else if (opt.equalsIgnoreCase("PolarScatter"))
				pjplot = GJPolarScatter.createInstance();
			else if (opt.equalsIgnoreCase("PolarStem"))
				pjplot = GJPolarStem.createInstance();
			else if (opt.equalsIgnoreCase("Bar"))
				pjplot = GJBar.createInstance();
			else if (opt.equalsIgnoreCase("Line"))
				pjplot = GJLine.createInstance();
			else { sc=true; pjplot = GJErrorBar.createInstance();}
			pjplot.setXData(p1d.getArrayX());
			pjplot.setYData(p1d.getArrayY());
			pjplot.setExtraData1(p1d.getArrayYupper()); // upper
			pjplot.setExtraData3(p1d.getArrayYlower()); // lower
		}

		else {
			if (opt.equalsIgnoreCase("PolarBar"))
				pjplot = GJPolarBar.createInstance();
			else if (opt.equalsIgnoreCase("PolarLine"))
				pjplot = GJPolarLine.createInstance();
			else if (opt.equalsIgnoreCase("PolarScatter"))
				pjplot = GJPolarScatter.createInstance();
			else if (opt.equalsIgnoreCase("PolarStem"))
				pjplot = GJPolarStem.createInstance();
			else if (opt.equalsIgnoreCase("Bar"))
				pjplot = GJBar.createInstance();
			else if (opt.equalsIgnoreCase("Line"))
				pjplot = GJLine.createInstance();
			else { sc=true; pjplot = GJErrorBar.createInstance();}
			pjplot.setXData(p1d.getArrayX());
			pjplot.setYData(p1d.getArrayY());
			pjplot.setExtraData1(p1d.getArrayYupper()); // upper
			pjplot.setExtraData3(p1d.getArrayYlower()); // lower
			pjplot.setExtraData2(p1d.getArrayXleft()); // left
			pjplot.setExtraData0(p1d.getArrayXright()); // right
		}

		// int width = 1;
		// String style = "*";
		// pjplot.setEdgeStroke(GJUtilities.makeStroke(width, style));

		// String styleLine = "-";
		// pjplot.setLineStroke(GJUtilities.makeStroke(width, styleLine));

		// GJCyclicArrayList msize = new GJCyclicArrayList(100);
		// pjplot.setDynamicMarkerSize(msize);

		pjplot.setEdgeColor(p1d.getColor());
		pjplot.setFill(p1d.getColor());
		pjplot.setLineColor(p1d.getColor());

		String symbol = (String) GJDefaults.getMap().get(
				"GJAbstractPlot.markerSymbol");
		symbol = p1d.getSymbolShape();
		double markerSize = p1d.getSymbolSize();
		// System.out.println( markerSize);
		// System.out.println( symbol);

		pjplot.getVisualModel().setMarkerArray(
				new GJCyclicArrayList<GJMarker>(GJMarker.getMarker(symbol,
						markerSize)));
		pjplot.getVisualModel().setDynamicMarkerSize(
				new GJCyclicArrayList<Dimension>());

		// String styleLine = "-";
		// float width = p1d.getPenWidth();
		// pjplot.setLineColor(p1d.getColor());
		// pjplot.setLineStroke(GJUtilities.makeStroke(width, styleLine));
		
		
		
		if ( p1d.getDimension()>2) {
			if (p1d.isErrX() != false && p1d.isErrY() != false) graph.add(pjplot);	
		} else graph.add(pjplot);
		
		
		
		// now dealing with usual symbols in case of error mode
		if (sc==true && p1d.getDimension()>2) {
		pjplot = GJScatter.createInstance();
		pjplot.setXData(p1d.getArrayX());
		pjplot.setYData(p1d.getArrayY());
		
		pjplot.setEdgeColor(p1d.getColor());
		pjplot.setFill(p1d.getColor());
		pjplot.setLineColor(p1d.getColor());
		symbol = p1d.getSymbolShape();
		markerSize = p1d.getSymbolSize();
		//System.out.println( markerSize);
		
		pjplot.getVisualModel().setMarkerArray(
				new GJCyclicArrayList<GJMarker>(GJMarker.getMarker(symbol,
						markerSize)));
		pjplot.getVisualModel().setDynamicMarkerSize(
				new GJCyclicArrayList<Dimension>());
		graph.add(pjplot);
		}
		
		
		 if (isSetRange) graph.setAxesBounds(range);
	        if (antialiasing) graph.setAntialiasing(true);
	        else  graph.setAntialiasing(false);
	        
		update();
	}

	/**
	 * Get the frame with the plot.
	 **/
	public WPlotFrame getFrame() {
		return frame;
	}

	/**
	 * Get the graph with the plot.
	 * 
	 * @return interface graph
	 **/
	public GJPlotInterface getPlotInterface() {
		return pjplot;
	}

	/**
	 * Get the graph with the plot.
	 * 
	 * @return graph
	 **/
	public GJGraph getGraph() {
		return graph;
	}

	/**
	 * Get the graph container.
	 **/
	public GJGraphContainer getContainer() {
		return graphContainer;
	}

	/**
	 * Show the frame or not?
	 * 
	 * @param showIt
	 *            true if should be shown
	 */

	public void visible(boolean showIt) {
		if (showIt)
			frame.setVisible(true);
		else
			frame.setVisible(false);
	}

	/**
	 * Show the frame.
	 */
	public void visible() {
		frame.setVisible(true);
	}

	/**
	 * Draw a s
	 * @param p1d
	 */
	public void drawContour(P1D p1d) {

		pjplot =GJContour.createInstance();
		kcl.waterloo.graphics.plots2D.contour.ContourExtra contourObject=kcl.waterloo.graphics.plots2D.contour.ContourExtra.createInstance();
        
		//for (int j=0; j<10; j++){
		//	contourObject.addContour(j, j);
		//}
		
		
	
	 	pjplot.setXData(p1d.getArrayX());
		pjplot.setYData(p1d.getArrayY());
		pjplot.setExtraData0(p1d.getArrayY());
		
		contourObject.generateContours((GJContour)pjplot);
		
		//((GJContour)pjplot).getXData();
		
		contourObject.setNegativeLineColor(java.awt.Color.BLUE.darker());
		contourObject.setPositiveLineColor(java.awt.Color.ORANGE.darker());
		//... and the zero line to be painted at a different thickness
		contourObject.setZeroStroke(new BasicStroke(2.0f));
		((GJContour)pjplot).getDataModel().setExtraObject(contourObject);
		
	//	((GJContour)pjplot).getDataModel().setFilled(false);
	//	((GJContour)pjplot).getDataModel().setFillClipping(false);

		((GJContour)pjplot).setLabelled(true);

		
		//pjplot.setExtraData1(p1d.getArrayX()); // upper
		//pjplot.setExtraData2(p1d.getArrayX()); // lower
	 	graph.add(pjplot);
	 	
	 	
	 	 if (isSetRange) graph.setAxesBounds(range);
	        if (antialiasing) graph.setAntialiasing(true);
	        else  graph.setAntialiasing(false);
	        
		update();
	}
	
	
	/**
	 * Fast export of the canvas to an image file (depends on the extension,
	 * i.e. PNG, PDF, EPS, PS, SVG). Images can be saved to files with extension
	 * ".gz" (compressed SVG). Also, image can be saved to PDE format. No
	 * questions will be asked, an existing file will be rewritten. If
	 * 
	 * @param f
	 *            Output file with the proper extension. If no extension, PNG
	 *            file is assumed. Other formats are PNG, PDF, EPS, PS, SVG.
	 * 
	 */
	public void export(String f) {
		frame.export(f);
	}

	/**
	 * Show online documentation.
	 */
	public void doc() {

		String a = this.getClass().getName();
		a = a.replace(".", "/") + ".html";
		new HelpBrowser(HelpBrowser.JHPLOT_HTTP + a);

	}

	public static void main(String[] args) {

		if (args.length > 0) {

		}
	}

}

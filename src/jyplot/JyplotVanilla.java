package jyplot;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.text.html.StyleSheet;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYDifferenceRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.general.Dataset;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * The only dependenecies this class has is to JFreeChart itself. That means
 * that is will load quicker and the memory footprint will be smaller. However,
 * it lacks advanced options such as
 * <p>
 * Saving EPS, SVG, or PDF images.
 * <p>
 * Nice for-Jython methods
 * <p>
 * Passing Colt numerical arrays to plot methods.
 * <p>
 * 
 * @author Dion Whitehead
 *         <p>
 *         Email: dionjw at gmail dot com
 * 
 */
public class JyplotVanilla implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static Class thisclass = JyplotVanilla.class;
	static String shortname = thisclass.getName().substring(
			thisclass.getName().lastIndexOf(".") + 1);
	// static Logger logger = Logger.getLogger(shortname);

	/*
	 * From python script
	 */
	JFrame frame;
	JPanel jpanel;
	protected final static Color DEFAULT_BG_COLOR = Color.white;

	ArrayList chartMouseListeners;

	boolean enableJFrame;
	boolean closeOnExit;

	protected JFreeChart chart;

	/*
	 * Its important when creating new charts to call:
	 * this.chartToPanelMap.put(this.chart, null); so there is a list of charts.
	 */
	HashMap chartToPanelMap;

	// ArrayList chartarray;
	// private JFreeChart[][] chartarray;

	ArrayList<JFreeChart> chartList;

	// private int currentRow;
	// private int currentCol;

	int rows;
	int cols;
	int currentPlotNumber;

	XYSeriesCollection dataset;// scrap this???

	protected float figureWidth;
	protected float figureHeight;
	protected int dpi;

	/*
	 * Color information
	 */
	HashMap<String, Color> colourcodes;
	Map<String, Color> stringColorMap;
	StyleSheet styleSheet; // For converting strings such as #123423 to colors

	Color currentColor;
	int currentDatasetIndex;
	int currentSeriestIndex;

	/*
	 * A hack. For some reason if the colors are set to the some one.
	 */
	HashMap seriesIndexColorMap;

	public JyplotVanilla(boolean enableJFrame) {

		// if(new
		// File("/home/dion/storage/projects/javaprojects/log4j.parameters").exists())
		// {
		// PropertyConfigurator.configure("/home/dion/storage/projects/javaprojects/log4j.parameters");
		// }
		// else
		// {
		// PatternLayout layout = new PatternLayout("%-5c %C{1} %l %L \n");
		// ConsoleAppender appender = new ConsoleAppender(layout);
		// BasicConfigurator.configure(appender);
		// }

		/*
		 * This should work on enableJFrame systems e.g. servers
		 */
		// System.setProperty("java.awt.headless",
		// Boolean.toString(enableJFrame));
		this.enableJFrame = enableJFrame;
		closeOnExit = false;
		// currentRow = 1;
		// currentCol = 1;
		rows = 1;
		cols = 1;

		this.chartList = new ArrayList<JFreeChart>();

		if (this.jpanel == null)
			this.jpanel = new JPanel(new GridLayout(1, 1));
		this.jpanel.setVisible(true);

		this.seriesIndexColorMap = new HashMap();
		chartMouseListeners = new ArrayList();
		chartToPanelMap = new HashMap();

		/*
		 * A bunch of constants
		 */

		colourcodes = new HashMap<String, Color>();
		colourcodes.put("r", Color.RED);
		colourcodes.put("g", Color.GREEN);
		colourcodes.put("b", Color.BLUE);
		colourcodes.put("y", Color.YELLOW);
		colourcodes.put("o", Color.ORANGE);

		stringColorMap = getStringToColorMap();
		styleSheet = new StyleSheet();

		figureWidth = 8;
		figureHeight = 6;

		/*
		 * Call this last
		 */
		this.subplot(1, 1, 1);
	}

	public JyplotVanilla() {
		this(true);
	}

	public void subplot(int number) {
		int rows = 1;
		int cols = 1;
		int plotnumber = 1;

		rows = number / 100;
		cols = (number - rows * 100) / 10;
		plotnumber = (number - rows * 100 - cols * 10);
		if (rows > 9 || cols > 9 || plotnumber > 9) {
			System.out.println("Incorrect format for subplot");
			return;
		}
		// System.out.println("subplot(" + rows + ", " + cols + ", " +plotnumber
		// + ")");
		this.subplot(rows, cols, plotnumber);

	}

	/**
	 * Holds a list of arrays with the plots
	 * 
	 * @param rows
	 * @param cols
	 * @param plotnumber
	 */
	public void subplot(int rows, int cols, int plotnumber) {
		// if( this.chartarray == null)
		// chartarray = new ArrayList();

		this.chart = null;

		int maxCharts = rows * cols;

		if (plotnumber > maxCharts) {
			System.out.println("Error in subplot(" + rows + ", " + cols + ", "
					+ plotnumber + ")");
			System.out
					.println("Plot index is greater than max number of plots.");
			return;
		}

		/* Make sure there is enough space in the list */
		for (int i = 0; i < maxCharts - this.chartList.size(); i++) {
			this.chartList.add(null);
		}

		;
		this.rows = rows;
		this.cols = cols;
		this.currentPlotNumber = plotnumber - 1;

		/*
		 * Initialise the array, if it needs to
		 */
		// if(this.chartarray == null || plotnumber == 0 || rows !=
		// this.chartarray.length || cols != this.chartarray[0].length)
		// {
		// this.chartarray = new JFreeChart[rows][cols];
		// this.currentRow = (plotnumber / (this.chartarray[0].length));
		// this.currentCol = plotnumber % this.chartarray[0].length;
		// }
		// else
		// {
		// /*
		// * Put the plot in the right place
		// */
		// if(this.chartarray.length == rows && this.chartarray[0].length ==
		// cols)
		// {
		// // # print "this.currentRow = ", plotnumber, "/",
		// // len(this.chartarray[0]), "=", (plotnumber /
		// // len(this.chartarray[0]))
		// this.currentRow = (plotnumber / (this.chartarray[0].length));
		// this.currentCol = plotnumber % this.chartarray[0].length;
		// }
		// else
		// {
		// System.out.println("Wrong row or column number");
		// // System.out.println("Adjusting rows and columns to fit...");
		// //
		// // int newRows = Math.max(rows, this.chartarray.length);
		// // int newColss = Math.max(cols, this.chartarray.length);
		//
		// // JFreeChart[][] newchartarray = new
		//
		//
		// }
		// }
		// return this.chart;
	}

	public void show() {
		this.show(800, 400);
	}

	public void show(int width, int height) {
		if (this.jpanel == null)
			this.jpanel = new JPanel(new GridLayout(1, 1));
		this.jpanel.setPreferredSize(new Dimension(width, height));

		GridLayout gridLayout = (GridLayout) this.jpanel.getLayout();
		// gridLayout.setColumns(this.chartarray.length);
		// gridLayout.setRows(this.chartarray[0].length);

		gridLayout.setColumns(this.cols);
		gridLayout.setRows(this.rows);

		if (enableJFrame) {

			if (this.frame == null) {
				this.frame = new JFrame("Figure");
				this.frame.setSize(width, height);
				this.frame.setVisible(false);
				this.setQuitOnWindowClose(closeOnExit);
				// this.frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

				// Experimenting with using Chartpanels instead of images

				this.frame.getContentPane().add(this.jpanel);
			}
		}

		this.jpanel.setSize(width, height);

		this.jpanel.removeAll();

		int currentChartIndex = 0;
		for (int i = 0; i < this.cols; i++) {
			for (int j = 0; j < this.rows; j++) {
				try {
					this.chart = this.chartList.get(currentChartIndex);
					this.chart.setBorderPaint(DEFAULT_BG_COLOR);
					this.chart.setBackgroundPaint(DEFAULT_BG_COLOR);
                                        this.chart.setBorderVisible(false);

					
					currentChartIndex++;
					// this.chart = this.chartarray[i][j];

					BaseChartPanel chartpanel = null;
					// ChartPanel chartpanel = null;

					/*
					 * The chartToPanelMap holds custom ChartPanels
					 */
					if (this.chartToPanelMap.containsKey(this.chart)
							&& this.chartToPanelMap.get(this.chart) != null) {
						chartpanel = (BaseChartPanel) this.chartToPanelMap
								.get(this.chart);
                                                chartpanel.setBackground(DEFAULT_BG_COLOR);

						// chartpanel =
						// (ChartPanel)this.chartToPanelMap.get(this.chart);
					} else {
						chartpanel = new BaseChartPanel(chart);
						// chartpanel = new ChartPanel(chart);
						this.chartToPanelMap.put(this.chart, chartpanel);
                                                 chartpanel.setBackground(DEFAULT_BG_COLOR);

					}
					this.jpanel.add(chartpanel);
					chartpanel.repaint();
				} catch (RuntimeException e) {
					e.printStackTrace();
				}
				// for (int k = 0; k < this.chartMouseListeners.size(); k++)
				// chartpanel.addChartMouseListener((ChartMouseListener)
				// this.chartMouseListeners.get(k));

				// chartpanel.addChartMouseListener(this);
			}
		}

		this.jpanel.setVisible(true);
		this.jpanel.revalidate();
		this.jpanel.repaint();
		// this.jpanel.resize(width, height);

		if (this.enableJFrame)
			this.frame.setVisible(true);

	}

	public XYDataset plot(double[] y) {
		double[] x = new double[y.length];
		for (int i = 1; i <= x.length; i++)
			x[i - 1] = i;
		return plot(x, y);
	}

	
	
	/**
	 * Plot X and Y
	 * @param x x values
	 * @param y y values
	 * @return
	 */
	public XYDataset plot(double[] x, double[] y) {
		return plot(x, y, null);
	}

	public XYDataset plot(double[] x, double[] y, Color color) {
		return plot(x, y, color, null, null, false, false, null, false);
	}

	public XYDataset plot(double[] x, double[] y, Color color, String dataname,
			double[] yErrorBars, boolean seperateYAxis, boolean yaxisVisible,
			String rangeLabel, boolean shapesVisible) {
		// color = null;
		/* If there is no x data, create it, from 0 ... */
		if (x == null) {
			x = new double[y.length];
			for (int i = 0; i < y.length; i++)
				x[i] = i;
		}

		/* Then make the data series */
		if (dataname == null)
			dataname = "";
		XYSeries series = new XYSeries(dataname);
		for (int i = 0; i < x.length; i++) {
			if (!(Double.isNaN(x[i]) || Double.isNaN(y[i])
					|| Double.isInfinite(x[i]) || Double.isInfinite(y[i])))
				series.add(x[i], y[i]);
		}

		if (dataname != null)
			series.setKey(dataname);

		/* If there is no chart, create it (defaults to XY chart). */
		if (this.chart == null) {
			createXYChart();
		}
		
		this.chart.setBorderPaint(DEFAULT_BG_COLOR);
		this.chart.setBackgroundPaint(DEFAULT_BG_COLOR);
		this.chart.setBorderVisible(false);


		XYPlot plot = this.chart.getXYPlot();
		

		if (plot == null) {
			System.err.println("Plot is null");
		}
		plot.setBackgroundPaint(DEFAULT_BG_COLOR);
		/*
		 * If we are creating a new axis, we need a new dataset, but only if
		 * there is no current dataset with dataseries.
		 */

		int currentDatasetIndex = plot.getDatasetCount();

		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(series);
		plot.setDataset(currentDatasetIndex, dataset);

		XYLineAndShapeRenderer renderer = null;

		renderer = new XYLineAndShapeRenderer();
		// renderer.setBasePaint(color);

		plot.setRenderer(currentDatasetIndex, renderer);

		// plot.setRangeAxisLocation(currentDatasetIndex,
		// AxisLocation.BOTTOM_OR_LEFT);

		if (seperateYAxis) {
			setSeperateYAxis(rangeLabel, color, yaxisVisible);
		}
		// if(seperateYAxis)
		// {
		//
		// NumberAxis axis2 = new NumberAxis(rangeLabel);
		// plot.setRangeAxis(currentDatasetIndex, axis2);
		// axis2.setAutoRangeIncludesZero(false);
		// axis2.setAutoRange(true);
		// axis2.setVisible(yaxisVisible);
		// if(color != null)
		// {
		// axis2.setLabelPaint(color);
		// axis2.setTickLabelPaint(color);
		// }
		// plot.mapDatasetToRangeAxis(currentDatasetIndex, currentDatasetIndex);
		//
		// }

		// }
		// else
		// {
		// seperateYAxis = false;
		// /*
		// * This will automatically create
		// * a new dataset.
		// */
		// dataset = new XYSeriesCollection();
		// // dataset = getXYDataset();
		// dataset.addSeries(series);
		// XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		// plot.setRenderer(currentRow, renderer, shapesVisible)
		// renderer =
		// (XYLineAndShapeRenderer)plot.getRendererForDataset(dataset);
		//
		// /*
		// * Color the number axis
		// */
		// }

		setShapesVisible(shapesVisible);
		// renderer.setShapesVisible(shapesVisible);

		/*
		 * This sets the colour of the series.
		 */
		setSeriesColor(color);
		// if(color != null)
		// plot.getRendererForDataset(dataset).setSeriesPaint(dataset.getSeriesCount()
		// - 1, color);

		if (dataname == null || dataname.equalsIgnoreCase("")) {
			renderer.setSeriesVisibleInLegend(false);
		}

		this.chart.getXYPlot().configureDomainAxes();// #Needed to update the
		// plotted ranges;
		this.chart.getXYPlot().configureRangeAxes();// #Needed to update the
		//
		this.grid(false);
		return dataset;
	}

	/**
	 * Creates a seperate y axis for the current dataset.
	 * 
	 */
	protected void setSeperateYAxis(String rangeLabel, Color color,
			boolean visible) {
		XYPlot plot = this.chart.getXYPlot();
		int currentDatasetIndex = plot.getDatasetCount() - 1;

		NumberAxis axis = new NumberAxis(rangeLabel);
		plot.setRangeAxis(currentDatasetIndex, axis);
		axis.setAutoRangeIncludesZero(false);
		axis.setAutoRange(true);
		axis.setVisible(visible);
		if (color != null) {
			axis.setLabelPaint(color);
			axis.setTickLabelPaint(color);
		}
		plot.mapDatasetToRangeAxis(currentDatasetIndex, currentDatasetIndex);
	}

	/**
	 * sets the chart title
	 * 
	 * @param p
	 */
	public void title(String p) {
		if (chart != null) {
			chart.setTitle(p);
			// chart.getTitle().setFont(new Font("Serif", Font.PLAIN, 12));
		} else {
			System.out.println("title(): No chart created");
		}
	}

	public void title(String p, int fontsize) {
		if (chart != null) {
			chart.setTitle(p);
			chart.getTitle()
					.setFont(new Font("SansSerif", Font.BOLD, fontsize));
		} else {
			System.out.println("title(): No chart created");
		}
	}

	/**
	 * sets the x-axis label
	 * 
	 * @param p
	 */
	public void xlabel(String p) {
		if (chart != null) {

			int currentDataSetIndex = chart.getXYPlot().getDomainAxisCount() - 1;
			if (currentDataSetIndex == -1)
				currentDataSetIndex = 0;
			ValueAxis axis = chart.getXYPlot().getDomainAxisForDataset(
					currentDataSetIndex);

			axis.setLabel(p);
			axis.setAxisLineVisible(true);
			axis.setTickLabelsVisible(true);
			axis.setTickMarksVisible(true);
		} else
			System.out.println("xlabel(): No chart created");
	}

	/**
	 * sets the y-axis label
	 * 
	 * @param p
	 */
	public void ylabel(String p) {
		if (!isXYPlot()) {
			return;
		}

		chart.getXYPlot().getRangeAxis().setLabel(p);
		chart.getXYPlot().getRangeAxis().setAxisLineVisible(true);
		chart.getXYPlot().getRangeAxis().setTickLabelsVisible(true);
		chart.getXYPlot().getRangeAxis().setTickMarksVisible(true);
	}

	/**
	 * set series labels (keys) for the legend
	 * 
	 * @param p
	 *            []
	 */
	public void legend(String[] p) {
		if (!isXYPlot()) {
			return;
		}

		if (p == null) {
			for (int i = 0; i < this.getChart().getXYPlot().getDatasetCount(); i++) {
				this.getChart().getXYPlot().getRenderer(i)
						.setSeriesVisibleInLegend(false);
			}

			return;
		}

		// set labels (keys) for each series

		Class BarXYDatasetClass = jyplot.BarXYDataset.class;
		if (this.chart.getXYPlot().getDataset().getClass() == BarXYDatasetClass) {
			BarXYDataset dataset = (BarXYDataset) this.chart.getXYPlot()
					.getDataset();

			for (int i = 0; i < dataset.getSeriesCount(); i++) {
				if (p.length >= i) {
					// this.dataset.getSeries(i).setKey(p[i]);
					dataset.setSeriesKey(i, p[i]);
				} else {
					break;
				}
			}
		} else {
			System.err
					.println("chart.getXYPlot().getDataset().getClass() != BarXYDatasetClass");
		}

	}

	/**
	 * set series label (key) for the legend for the most recent dataset
	 * 
	 * @param p
	 *            []
	 */
	public void label(String p) {
		if (!isXYPlot()) {
			return;
		}

		try {
			int latestDatasetIndex = this.chart.getXYPlot().getDatasetCount() - 1;
			if (p != null && !p.equalsIgnoreCase("")) {
				((XYSeriesCollection) this.chart.getXYPlot().getDataset(
						latestDatasetIndex)).getSeries(
						this.chart.getXYPlot().getDataset(latestDatasetIndex)
								.getSeriesCount() - 1).setKey(p);

				this.chart
						.getXYPlot()
						.getRendererForDataset(
								this.chart.getXYPlot().getDataset(
										latestDatasetIndex))
						.setSeriesVisibleInLegend(true);
			} else {
				this.chart
						.getXYPlot()
						.getRendererForDataset(
								this.chart.getXYPlot().getDataset(
										latestDatasetIndex))
						.setSeriesVisibleInLegend(latestDatasetIndex, false);
				this.chart
						.getXYPlot()
						.getRendererForDataset(
								this.chart.getXYPlot().getDataset(
										latestDatasetIndex))
						.setSeriesVisibleInLegend(false);
				this.chart
						.getXYPlot()
						.getRendererForDataset(
								this.chart.getXYPlot().getDataset(
										latestDatasetIndex))
						.setSeriesVisibleInLegend(false, true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * %% sets the line style (solid, dashed, ...) for each Series
	 * 
	 * @param p
	 *            []
	 */
	public void linestyle(String[] p) {
		XYPlot plot = (XYPlot) chart.getPlot();
		AbstractRenderer r1 = (AbstractRenderer) plot.getRenderer(0);
		for (int i = 0; i < this.dataset.getSeriesCount(); i++) {
			// ### do with enum switch? or what will the incoming style format
			// be?
			// switch (p[i])
			// {
			// case "-":
			// case ":":
			// case "--":
			// case "o":
			// Basicstroke stroke = ;
			// break;
			//
			// default:
			// break;
			// }
			if (p.length >= i) {
				BasicStroke stroke = (BasicStroke) r1.getSeriesStroke(i);
				r1.setSeriesStroke(
						i,
						new BasicStroke(stroke.getLineWidth(), stroke
								.getEndCap(), stroke.getLineJoin(), stroke
								.getMiterLimit(), new float[] { 12, 12 }, 0));
			} else
				break;
		}
	}

	/**
	 * sets the line width for each Series
	 * 
	 * @param p
	 *            []
	 */
	public void linewidth(float[] p) {
		XYPlot plot = (XYPlot) chart.getPlot();
		AbstractRenderer r1 = (AbstractRenderer) plot.getRenderer(0);
		for (int i = 0; i < this.dataset.getSeriesCount(); i++) {
			if (p.length >= i)
				r1.setSeriesStroke(i, new BasicStroke(p[i]));
			else
				break;
		}
	}

	/**
	 * %% sets the line color for each Series
	 * 
	 * @param p
	 *            []
	 */
	public void linecolor(Color[] p) {
		XYPlot plot = (XYPlot) chart.getPlot();
		AbstractRenderer r1 = (AbstractRenderer) plot.getRenderer(0);
		for (int i = 0; i < this.dataset.getSeriesCount(); i++) {
			if (p.length >= i)
				r1.setSeriesPaint(i, p[i]);
			else
				break;
		}
	}

	/**
	 * sets the axis range manually
	 * 
	 * @param p
	 *            [] gets an array of the axis boundries: p0 = lower x, p1 =
	 *            upper x p2 = lower y, p3 = upper y
	 */
	public void axis(double[] p) {
		if (this.chart == null) {
			System.out.println("No chart yet.");
			return;
		}

		if (p.length > 1) {
			xlim(p[0], p[1]);
		}
		if (p.length > 3) {
			ylim(p[2], p[3]);
		}
	}

	/**
	 * Sets the x axis range manually
	 */
	public void xlim(double min, double max) {
		if (this.chart == null) {
			System.err.println("No chart yet.");
			return;
		}
		try {
			ValueAxis XAxis = this.chart.getXYPlot().getDomainAxis();
			// int currentDatasetIndex =
			// this.chart.getXYPlot().getDatasetCount();
			//
			// ValueAxis XAxis =
			// this.chart.getXYPlot().getDomainAxisForDataset(currentDatasetIndex);

			XAxis.setRange(min, max);
			XAxis.setAutoRange(false);
		} catch (ClassCastException e) {
			System.out
					.println("Plot is not an XYPlot: you cannot set the axis range.");
			// e.printStackTrace();
		}
	}

	/**
	 * Sets the y axis range manually
	 */
	public void ylim(double min, double max) {
		if (this.chart == null) {
			System.err.println("No chart yet.");
			return;
		}
		try {
			ValueAxis YAxis = this.chart.getXYPlot().getRangeAxis();

			// int currentDatasetIndex =
			// this.chart.getXYPlot().getDatasetCount();
			//
			// ValueAxis YAxis =
			// this.chart.getXYPlot().getRangeAxisForDataset(currentDatasetIndex);

			YAxis.setRange(min, max);
			YAxis.setAutoRange(false);
		} catch (ClassCastException e) {
			System.out
					.println("Plot is not an XYPlot: you cannot set the axis range.");
			// e.printStackTrace();
		}
	}

	/**
	 * transforms axes into a logarithmic scale
	 * 
	 * @param p
	 *            assign x-axis as logarithmic
	 */
	// public void semilogx(int p)
	// {
	// XYPlot plot = (XYPlot) chart.getPlot();
	// String xlabel = plot.getRangeAxis().getLabel();
	// plot.setDomainAxis(p, new LogarithmicAxis(xlabel));
	// }
	//
	// public void semilogx()
	// {
	// semilogx(0);
	// }

	/**
	 * transforms axes into a logarithmic scale
	 * 
	 * @param p
	 *            assign y-axis as logarithmic
	 */
	// public void semilogy(int p)
	// {
	// XYPlot plot = (XYPlot) chart.getPlot();
	// String ylabel = plot.getDomainAxis().getLabel();
	// plot.setRangeAxis(p, new LogarithmicAxis(ylabel));
	// }
	//
	// public void semilogy()
	// {
	// semilogy(0);
	// }

	/**
	 * transforms axes into a logarithmic scale
	 * 
	 * @param p
	 *            assign x- and y-axis as logarithmic
	 */
	public void loglog(int p) {
		XYPlot plot = (XYPlot) chart.getPlot();
		String xlabel = plot.getRangeAxis().getLabel();
		String ylabel = plot.getDomainAxis().getLabel();
		plot.setDomainAxis(p, new LogarithmicAxis(xlabel));
		plot.setRangeAxis(p, new LogarithmicAxis(ylabel));
	}

	public void loglog() {
		loglog(0);
	}

	/**
	 * @param args
	 *            method for testing Jylab
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// test object

		JyplotVanilla j = new JyplotVanilla(true);

		j.subplot(2, 2, 1);
		// create basic plot
		double[] x1 = { 10, 20, 10, 40, 30, 10, 30, 50 };
		double[] y1 = { 1, 4, 3, 5, 5, 7, 7, 8 };
		// double[] x2 = {1, 2, 3, 4, 5, 6, 7, 8};
		// double[] y2 = {0.5, 30, 20, 45, 40, 40, 50, 60};
		// double[] x3 = {3, 4, 5, 6, 7, 8, 9, 10};
		// double[] y3 = {2, 3, 2, 1, 0.5, 1, 2, 1};
		//
		j.plot(x1, y1);
		j.subplot(2, 2, 2);
		j.plot(x1, y1);

		j.subplot(2, 2, 3);
		j.plot(x1, y1);
		j.subplot(2, 2, 4);
		j.plot(x1, y1);
		// double[] values = new double[1000];
		// Random generator = new Random(12345678L);
		// for (int i = 0; i < 1000; i++)
		// {
		// values[i] = generator.nextGaussian() + 5;
		// }
		// j.hist(values, 100, "blah");
		// j.errorbar(new double[]{1, 2, 3}, new double[]{1, 2, 1}, null, new
		// double[]{0.5, 0.5, 0.4}, j.getColor("blue"), true);

		// j.plot(null, y2, new Color(255,255,0), "y2", null, false,false, "y2",
		// true);
		// j.plot(null, y1, new Color(0,255,0), "y1", null, false,false, "y1",
		// true);
		// j.subplot(132);
		// j.plot(null, x1, new Color(0,255,255), "x1", null, false,false, "x1",
		// true);
		// j.subplot(133);
		// j.difference(null, x1, y1, new Color(0,255,255,25), false);

		// plot(double[] x, double[] y, Color color, double[] yErrorBars,
		// boolean multipleAxes, boolean newRange, String rangeLabel)

		// j.color(new Color(0,0,0,0));
		// j.plot(y2);
		// j.plot(x3, y3);

		// j.plot(new double[]{1, 2, 3}, new double[]{1, 2, 1});
		//
		// j.label("sdfdsf");
		//
		// // modifying actions
		// j.title("test chart");
		// j.xlabel("X axis");
		// j.ylabel("Y axis");
		//
		// String[] str = new String[3];
		// str[0] = "asdf1";
		// str[1] = "ghjkl";
		// str[2] = "qwertz";
		// j.legend(str);

		// j.linestyle(str);
		// j.linewidth(new float[] {2,4,8});
		// j.axis(new double[] {2,6,0,4});
		// j.log(3);
		// j.show();

		// DoubleFactory1D f = DoubleFactory1D.dense;
		//
		// double[] x1 = f.random(10).toArray();
		// double[] y1 = f.random(10).toArray();
		//

		// j.subplot(1, 2, 1);
		// // j.plot(x1,y1);
		// // j.xlabel("sfsdf");
		// // j.ylabel("sdfsdf");
		//
		// double[] left = {1, 2, 3, 5};
		// double[] height = {1, 2, 1, 0.2};
		// double[] width = {0.8, 0.8, 0.8, 0.8};
		// double[] bottom = {2, 2, 2, 2};
		//
		// j.bar(left, height, width, bottom, Color.RED);
		//
		// j.bar(0.0, 1.1, 1.0, 1.0, Color.BLUE);
		// j.bar(1.0, 4.1, 4.0, 4.0, Color.GREEN);
		// j.text("sdsdfsdf", 1, 1);

		// j.subplot(1, 2, 2);
		// j.bar(left, height, width, bottom, Color.BLUE);
		// # j.bar([1,2,3], [1,1.5,1], width=0.5, bottom=0, color='g')
		// # j.bar([4], [1], width=0.5, bottom=0, color='r')
		// # j.bar(4, 1, width=0.5, bottom=2, color=0.3)
		//
		// j.title("foobar");
		// # j.connect('button_press_event', testFunction)
		// j.savefig(System.getProperty("user.home") + "/tmp/test.png", 200,
		// 200);

		// j.setQuitOnWindowClose(true);
		//
		// j.label("The label");
		// j.color("blue");
		// j.show();
		j.show(800, 400);

		j.savefig("/tmp/delete.png", 100, 100);

	}

	/**
	 * Creates a chart.
	 * 
	 * @param dataset
	 *            the data for the chart.
	 * @return a chart.
	 */
	protected static JFreeChart createChart(XYDataset dataset) {
		// create the chart...
		JFreeChart chart = ChartFactory.createXYLineChart(null, // chart title
				null, // x axis label
				null, // y axis label
				dataset, // data
				PlotOrientation.VERTICAL, true, // include legend
				true, // tooltips
				false // urls
				);
		
		 chart.setBorderPaint(DEFAULT_BG_COLOR);
         chart.setBackgroundPaint(DEFAULT_BG_COLOR);
         chart.setBorderVisible(false);

		
		return chart;
	}


	
	
	
	/**
	 * A quick hack to draw bar plots with whatever axis
	 * 
	 * @param y
	 * @param x
	 */
	public void bar(Object[] x, double[] y, Color color) {
		double[] _left = new double[y.length];
		double[] _height = new double[y.length];
		double[] _width = new double[y.length];
		double[] _bottom = new double[y.length];

		for (int i = 0; i < y.length; i++) {
			_width[i] = 1;
			_bottom[i] = 0;
			_height[i] = y[i];
			_left[i] = i - 0.5;
		}
		this.bar(_left, _height, _width, _bottom, color);
		String[] xticks = new String[x.length];
		double[] xticksloc = new double[x.length];
		for (int i = 0; i < xticks.length; i++) {
			xticks[i] = x[i].toString();
			xticksloc[i] = i;
		}
		this.xticks(xticksloc, xticks);
	}

	public void bar(Object[] x, double[] y) {
		this.bar(x, y, null);
	}

	public void bar(double left, double height, double width, double bottom,
			Color color) {
		double[] _left = { left };
		double[] _height = { height };
		double[] _width = { width };
		double[] _bottom = { bottom };
		this.bar(_left, _height, _width, _bottom, color);
	}

	public void bar(List<Double> left, List<Double> height, List<Double> width,
			List<Double> bottom, Color color) {
		if (!(left.size() == height.size() && left.size() == width.size() && left
				.size() == bottom.size())) {
			System.out.println("bar(): lists must be of the same length.");
			return;
		}

		double[] _left = new double[left.size()];
		double[] _height = new double[left.size()];
		double[] _width = new double[left.size()];
		double[] _bottom = new double[left.size()];

		for (int i = 0; i < left.size(); i++) {
			_left[i] = left.get(i);
			_height[i] = height.get(i);
			_width[i] = width.get(i);
			_bottom[i] = bottom.get(i);
		}

		this.bar(_left, _height, _width, _bottom, color);
	}

	/**
	 * BAR(left, height, width=0.8, bottom=0, color='b', yerr=None, xerr=None,
	 * ecolor='k', capsize=3) Make a bar plot with rectangles at left,
	 * left+width, 0, height left and height are Numeric arrays. *Currently this
	 * second argument format is not implemented... Return value is a list of
	 * Rectangle patch instances BAR(left, height, width, bottom, color, yerr,
	 * xerr, capsize, yoff) xerr and yerr, if not None, will be used to generate
	 * errorbars on the bar chart color specifies the color of the bar ecolor
	 * specifies the color of any errorbar capsize determines the length in
	 * points of the error bar caps The optional arguments color, width and
	 * bottom can be either scalars or len(x) sequences This enables you to use
	 * bar as the basis for stacked bar charts, or candlestick plots Addition
	 * kwargs: hold = [True|False] overrides default hold state
	 */
	public BarXYDataset bar(double[] left, double[] height, double[] width,
			double[] bottom, Color color) {

		/**
		 * For assigning the renderer to the right dataset.
		 */
		int datasetIndex = 0;

		if (this.chart == null) {
			BarXYDataset dataset = new BarXYDataset(left, bottom, width, height);
			this.chart = ChartFactory.createXYBarChart(null, null, false, null,
					dataset, PlotOrientation.VERTICAL, false, false, false);
			this.chart.setBorderVisible(false);
			this.chart.setBorderPaint(DEFAULT_BG_COLOR);
	                this.chart.setBackgroundPaint(DEFAULT_BG_COLOR);
	         
			
			
			this.chart.getXYPlot().setDatasetRenderingOrder(
					DatasetRenderingOrder.FORWARD);
			this.chartToPanelMap.put(this.chart, null);
			this.chartList.set(this.currentPlotNumber, this.chart);
			// this.chartarray[this.currentRow][this.currentCol] = this.chart;//
			// Put
			// in
			// the
			// array
			// for
			// subplot
			// placement
		} else
		/*
		 * Make sure there is a BarXYDataset...
		 */
		{
			boolean foundBarXYDataset = false;
			/*
			 * ...in existing datasets.
			 */
			for (int i = 0; i < this.chart.getXYPlot().getDatasetCount(); i++)

			{
				if (this.chart.getXYPlot().getDataset(i) != null
						&& this.chart.getXYPlot().getDataset(i).getClass()
								.getName().matches(".*BarXYDataset")) {
					foundBarXYDataset = true;
					((BarXYDataset) this.chart.getXYPlot().getDataset(i))
							.addSeries(left, bottom, width, height);
					datasetIndex = i;
					break;
				}
			}
			if (!foundBarXYDataset)// :#If no BarXYDataset found in the
			// existing plot, create one
			{
				BarXYDataset dataset = new BarXYDataset(left, bottom, width,
						height);
				datasetIndex = this.chart.getXYPlot().getDatasetCount();
				this.chart.getXYPlot().setDataset(datasetIndex, dataset);
				this.chart.getXYPlot().setDatasetRenderingOrder(
						DatasetRenderingOrder.FORWARD);
			}
		}
		BarXYDataset dataset = (BarXYDataset) this.chart.getXYPlot()
				.getDataset(datasetIndex);
		/*
		 * Make sure the dataset has the right renderer
		 */
		if (this.chart.getXYPlot().getRenderer(datasetIndex) != null
				&& this.chart.getXYPlot().getRenderer(datasetIndex).getClass() == XYBarRenderer.class) // .getName().matches(".*XYBarRenderer")
		{
			((XYBarRenderer) this.chart.getXYPlot().getRenderer(datasetIndex))
					.setUseYInterval(true);
			((XYBarRenderer) this.chart.getXYPlot().getRenderer(datasetIndex))
					.setDrawBarOutline(false);

			// ((XYBarRenderer)
			// this.chart.getXYPlot().getRenderer(datasetIndex)).setSeriesVisibleInLegend(true);
		} else
		/* The render is */
		{
			XYBarRenderer barRenderer = new XYBarRenderer();
			barRenderer.setUseYInterval(true);
			barRenderer.setDrawBarOutline(false);

			// barRenderer.setSeriesVisibleInLegend(true);
			this.chart.getXYPlot().setRenderer(datasetIndex, barRenderer);
		}
		XYBarRenderer barRenderer = (XYBarRenderer) this.chart.getXYPlot()
				.getRenderer(datasetIndex);

		int seriesNumber = dataset.getSeriesCount() - 1;

		this.chart.getXYPlot().configureDomainAxes();// #Needed to update the
		// plotted ranges;
		this.chart.getXYPlot().configureRangeAxes();// #Needed to update the
		// plotted ranges

		this.chart.getXYPlot().setDomainGridlinesVisible(false);
		this.chart.getXYPlot().setRangeGridlinesVisible(false);

		barRenderer.setSeriesFillPaint(seriesNumber, color, true);
		barRenderer.setSeriesPaint(seriesNumber, color, true);
		this.seriesIndexColorMap.put(new Integer(seriesNumber), color);

		// for (int i = 0; i < seriesNumber + 1; i++)
		// barRenderer.setSeriesPaint(i, color);

		return dataset;

	}

	// /**
	// * Not properly tested.
	// * @param ticklabels
	// */
	// public void xticks(String[] ticklabels)
	// {
	// if(this.chart != null)
	// {
	// NumberAxis xNumberAxis = (NumberAxis)
	// this.chart.getXYPlot().getDomainAxis();
	// CustomSymbolAxis symbolaxis = new
	// CustomSymbolAxis(xNumberAxis.getLabel(), ticklabels, new double[] {-1, 1,
	// 2});
	// // symbolaxis.
	// symbolaxis.setGridBandsVisible(false);//The grey bands that suddnely
	// appear
	// symbolaxis.setRange(xNumberAxis.getRange());
	//
	// this.chart.getXYPlot().setDomainAxis(symbolaxis);
	//
	// }
	// else
	// System.out.println("xticks(): No chart created");
	//
	// }

	public void xticks(List<Double> locations, List ticklabels) {
		if (ticklabels != null && !(locations.size() == ticklabels.size())) {
			System.out.println("xticks(): lists must be of the same length.");
			ticklabels = null;
		}

		double[] _locations = new double[locations.size()];
		String[] _ticklabels = new String[locations.size()];

		for (int i = 0; i < locations.size(); i++) {
			_locations[i] = (Double) locations.get(i);
			if (ticklabels != null) {
				_ticklabels[i] = (String) ticklabels.get(i);
			} else {
				_ticklabels[i] = "";
			}
		}

		this.xticks(_locations, _ticklabels);
	}

	/**
	 * Not properly tested.
	 * 
	 * @param ticklabels
	 */
	public void xticks(double[] locations, String[] ticklabels) {
		// CustomSymbolAxis
		if (this.chart != null) {
			if (ticklabels != null) {
				NumberAxis xNumberAxis = (NumberAxis) this.chart.getXYPlot()
						.getDomainAxis();
				CustomSymbolAxis symbolaxis = new CustomSymbolAxis(
						xNumberAxis.getLabel(), ticklabels, locations);
				// symbolaxis.
				symbolaxis.setGridBandsVisible(false);// The grey bands that
														// suddnely appear
				symbolaxis.setRange(xNumberAxis.getRange());
				symbolaxis.setAutoRangeMinimumSize(xNumberAxis.getRange()
						.getUpperBound());
				// logger.debug("setting range: " + xNumberAxis.getRange());
				// symbolaxis.setAutoRange(false);

				this.chart.getXYPlot().setDomainAxis(symbolaxis);
			}
			// else
			// {
			// NumberAxis xNumberAxis = (NumberAxis)
			// this.chart.getXYPlot().getDomainAxis();
			// CustomSymbolAxis symbolaxis = new
			// CustomSymbolAxis(xNumberAxis.getLabel(), ticklabels, locations);
			// symbolaxis.
			// symbolaxis.setGridBandsVisible(false);//The grey bands that
			// suddnely appear
			// symbolaxis.setRange(xNumberAxis.getRange());
			// symbolaxis.setAutoRange(false);

			// this.chart.getXYPlot().setDomainAxis(symbolaxis);
			// }

		} else
			System.out.println("xticks(): No chart created");

	}

	/*
	 * Hack
	 */
	public void yticks(double[] locations, String[] ticklabels) {
		// CustomSymbolAxis
		if (this.chart != null) {
			NumberAxis xNumberAxis = (NumberAxis) this.chart.getXYPlot()
					.getRangeAxis();
			xNumberAxis.setTickLabelsVisible(false);

		} else
			System.out.println("yticks(): No chart created");

	}

	public void connect(ChartMouseListener listener) {
		// chartMouseListeners.add(listener);
	}

	public HashMap getChartPanelMap() {
		return this.chartToPanelMap;
	}

	public void drawToGraphics2D(Graphics2D g, int width, int height) {
		// self.chartCoordsMap = {} #Maps a chart to its raw screen coords, used
		// for converting coords
		g.setColor(Color.white);
		g.fillRect(0, 0, width, height);
		//
		// int boxWidth = width / this.chartarray[0].length;
		// int boxHeight = height / this.chartarray.length;

		int boxWidth = width / this.cols;
		int boxHeight = height / this.rows;

		//
		// # print "boxWidth ", boxWidth
		// # print "boxHeight ", boxHeight

		// for (int row = 0; row < chartarray.length; row++)
		int currentChartIndex = 0;

		for (int row = 0; row < this.rows; row++) {
			for (int col = 0; col < this.cols; col++) {
				// this.chart = this.chartarray[row][col];
				this.chart = this.chartList.get(currentChartIndex);
				
				
				
				currentChartIndex++;
				if (this.chart != null) {

					int rowsUsed = 1;
					int colsUsed = 1;
					int chartX = boxWidth * col;
					int chartY = boxHeight * row;
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
					// chart.plotChanged(new PlotChangeEvent(chart.getXYPlot())); // works OK!
					chart.plotChanged(new PlotChangeEvent(chart.getPlot())); // test!
					//
					ChartRenderingInfo info = new ChartRenderingInfo();
					//
					chart.draw(g, new java.awt.Rectangle(chartX, chartY,
							chartwidth, chartheight),
							new Point(chartX, chartY), info);
					// self.chartToInfoMap[chart] = info
					//
					// self.chartCoordsMap[chart] = [chartX ,chartY,chartwidth,
					// chartheight]

				}
			}
		}

	}

	/**
	 * Colors the most recent dataset or series
	 * 
	 * @param c
	 */
	public void color(Color c) {
		try {
			if (!isXYPlot()) {
				return;
			}

			XYPlot plot = (XYPlot) chart.getPlot();
			int currentDatasetIndex = plot.getDatasetCount() - 1;
			XYItemRenderer r1 = plot.getRenderer(currentDatasetIndex);
			r1.setPaint(c);

			// AbstractRenderer r1 = (AbstractRenderer) plot.getRenderer(0);
			// int seriesindex =
			// this.chart.getXYPlot().getDataset().getSeriesCount() - 1;
			// if(seriesindex >= 0)
			// r1.setSeriesPaint(seriesindex, c);

			// AbstractRenderer r1 = (AbstractRenderer) plot.getRenderer(0);
			// int seriesindex =
			// this.chart.getXYPlot().getDataset().getSeriesCount() - 1;
			// if(seriesindex >= 0)
			// r1.setSeriesPaint(seriesindex, c);
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	/**
	 * Colors the most recent dataset or series
	 * 
	 * @param s
	 */
	public void color(String s) {
		Color c = getColor(s);
		if (c != null) {
			this.color(c);
		}
	}

	/**
	 * Converts a string into a Color object. Checks a few local maps and the
	 * styleSheet class.
	 * 
	 * @param s
	 * @return
	 */
	public Color getColor(String s) {
		Color c = null;
		try {
			if (this.stringColorMap.containsKey(s)) {
				return this.stringColorMap.get(s);
			} else if (this.colourcodes.containsKey(s)) {
				return this.colourcodes.get(s);
			} else if (isDouble(s)) {
				float value = (float) Double.parseDouble(s);
				c = new Color(value, value, value);
				return c;
			} else {
				c = styleSheet.stringToColor(s);
				if (c != null) {
					return c;
				}

			}
			System.err.println("No color matching " + s);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return c;

	}

	/**
	 * Colors the most recent dataset or series
	 */
	public void color(int[] rgba) {
		try {
			int red = rgba[0];
			int green = rgba[1];
			int blue = rgba[2];

			Color c = new Color(red, green, blue);

			if (rgba.length > 3) {
				c = new Color(red, green, blue, rgba[3]);
			}
			this.color(c);

		} catch (Exception e) {
			System.out.println("Problem with color( " + Arrays.toString(rgba)
					+ ")");
			// e.printStackTrace();
		}

	}

	/**
	 * Colors the most recent dataset or series
	 */
	public void color(float value) {
		try {
			if (value >= 0.0 && value <= 1.0) {
				Color c = new Color(value, value, value);
				this.color(c);
			} else {
				System.out
						.println("color(float): float must be in the range 0.0 - 1.0");
			}

		} catch (Exception e) {
			System.out.println("Problem with color(" + value + ")");
			// e.printStackTrace();
		}

	}

	
	/**
	 * Get chart
	 * @return
	 */
	public JFreeChart getChart() {
		return chart;
	}

	public JFreeChart getChart(int row, int col) {
		int index = (this.cols * (row - 1) + col) - 1;

		if (row < 0 || row > this.rows || col < 0 || col > this.cols
				|| index >= this.chartList.size() || index < 0) {
			System.out.println("Problem in getChart(" + row + ", " + col
					+ "): index=" + index);
			System.out.println("Total rows and columns: " + this.rows + ", "
					+ this.cols);
			return null;
		}
		return this.chartList.get(index);
	}

	public void setChart(JFreeChart chart) {
		this.chart = chart;
	}

	// public JFreeChart[][] getChartarray()
	// {
	// return chartarray;
	// }
	//
	// public void setChartarray(JFreeChart[][] chartarray)
	// {
	// this.chartarray = chartarray;
	// }

	public HashMap getChartToPanelMap() {
		return chartToPanelMap;
	}

	public void setChartToPanelMap(HashMap chartToPanelMap) {
		this.chartToPanelMap = chartToPanelMap;
	}

	public JFrame getFrame() {
		return frame;
	}

	public void setFrame(JFrame frame) {
		this.frame = frame;
	}

	public JPanel getJpanel() {
		return jpanel;
	}

	public void setJpanel(JPanel jpanel) {
		this.jpanel = jpanel;
	}

	/**
	 * Add some text at data coordinates x,y
	 * 
	 * @param text
	 * @param x
	 * @param y
	 */
	public void text(String text, double x, double y) {
		if (this.chart == null) {
			System.out.println("Current chart is null");
			return;
		}
		if (this.chart.getPlot() == null) {
			System.out.println("Current chart plot is null");
			return;
		}
		XYPlot plot = (XYPlot) this.chart.getPlot();
		XYAnnotation annotation = new XYTextAnnotation(text, x, y);
		plot.addAnnotation(annotation);
	}

	/**
	 * Plots a difference plot, y1 - y2 is shaded
	 * 
	 */
	public void difference(double[] x, double[] y1, double[] y2, Color color,
			boolean multipleAxes) {
		if (y1 == null) {
			System.out.println("difference(x, y1, y2): y1 is null");
			return;
		}

		if (y2 == null) {
			System.out.println("difference(x, y1, y2): y2 is null");
			return;
		}

		if (y1.length != y2.length) {
			System.out.println("difference(x, y1, y2): length of y1 != y2");
			return;
		}

		/*
		 * If there is no x data, create it, from 0 ...
		 */
		if (x == null) {
			x = new double[y1.length];
			for (int i = 0; i < y1.length; i++)
				x[i] = i;
		}

		/*
		 * Create charts and datasets if neccesary
		 */
		if (this.chart == null) {
			createXYChart();
		}

		// XYSeriesCollection dataset = getXYDataset();

		XYPlot plot = this.chart.getXYPlot();

		XYSeries series1 = new XYSeries("");// Upper s.d.
		XYSeries series2 = new XYSeries("");// Lower s.d.

		for (int i = 0; i < y1.length; i++) {
			if (!Double.isNaN(x[i]) && !Double.isNaN(y1[i])
					&& !Double.isNaN(y2[i])) {
				series1.add(x[i], y1[i]);
				series2.add(x[i], y2[i]);
			}
		}
		XYSeriesCollection sdDataset = new XYSeriesCollection();
		sdDataset.addSeries(series1);
		sdDataset.addSeries(series2);

		int newDatasetIndex = plot.getDatasetCount();

		plot.setDataset(newDatasetIndex, sdDataset);

		XYDifferenceRenderer differenceRenderer = new XYDifferenceRenderer(
				color, color, false);
		plot.setRenderer(newDatasetIndex, differenceRenderer);

		// if(newDatasetIndex > 0)
		// plot.mapDatasetToRangeAxis(newDatasetIndex - 1, newDatasetIndex);

		/*
		 * Make sure the actual lines are not colored/rendered.
		 */
		Color transparent = new Color(0, 0, 0, 0);
		AbstractRenderer r1 = (AbstractRenderer) plot
				.getRenderer(newDatasetIndex);
		r1.setSeriesPaint(0, transparent);
		r1.setSeriesPaint(1, transparent);

	}

	/*
	 * This needs a better system of error checking
	 */
	protected JFreeChart createXYChart() {
		this.chart = ChartFactory.createXYLineChart(null, // chart title
				null, // x axis label
				null, // y axis label
				new XYSeriesCollection(), // data
				PlotOrientation.VERTICAL, true, // include legend
				true, // tooltips
				false // urls
				);
		// this.chartarray[this.currentRow][this.currentCol] = this.chart;
		this.chartList.set(this.currentPlotNumber, this.chart);
		this.chart.setBorderPaint(DEFAULT_BG_COLOR);
        this.chart.setBackgroundPaint(DEFAULT_BG_COLOR);
        this.chart.setBorderVisible(false);
		// if(this.chartToPanelMap == null)

		this.chartToPanelMap.put(this.chart, null);
		return this.chart;
	}

	/**
	 * Creates a dataset if none exists.
	 * 
	 * @return
	 */
	protected XYSeriesCollection getXYDataset() {
		XYSeriesCollection dataset = null;
		try {
			dataset = (XYSeriesCollection) this.chart.getXYPlot().getDataset();
		} catch (Exception e) {
			// e.printStackTrace();
			dataset = new XYSeriesCollection();
			this.chart.getXYPlot().setDataset(dataset);

		}
		return dataset;
	}

	public int getCols() {
		return cols;
	}

	public int getRows() {
		return rows;
	}

	public void hist(double[] data, int bins) {
		hist(data, bins, "", null);
	}

	public void hist(double[] data, int bins, String dataname) {
		hist(data, bins, dataname, null);
	}

	public XYDataset hist(double[] data, int bins, String dataname, Color color)// ,
																				// double
																				// min,
																				// double
																				// max)
	{

		/* Then make the data series */
		if (dataname == null)
			dataname = "";
		HistogramDataset dataset = new HistogramDataset();

		// addSeries(Comparable key, double[] values, int bins, double minimum,
		// double maximum
		dataset.addSeries(dataname, data, bins);

		/* If there is no chart, create it (defaults to XY chart). */
		if (this.chart == null) {
			createXYChart();
		}

		XYPlot plot = this.chart.getXYPlot();

		/*
		 * If we are creating a new axis, we need a new dataset, but only if
		 * there is no current dataset with dataseries.
		 */

		int currentDatasetIndex = plot.getDatasetCount();

		plot.setDataset(currentDatasetIndex, dataset);

		XYBarRenderer renderer = new XYBarRenderer();
		renderer.setDrawBarOutline(false);

		plot.setRenderer(currentDatasetIndex, renderer);

		/*
		 * This sets the colour of the series.
		 */
		if (color != null)
			plot.getRendererForDataset(dataset).setSeriesPaint(
					dataset.getSeriesCount() - 1, color);

		this.grid(false);

		return dataset;

	}

	public void grid(boolean state) {
		this.chart.getXYPlot().setDomainGridlinesVisible(state);
		this.chart.getXYPlot().setRangeGridlinesVisible(state);
	}

	public void gridx(boolean state) {
		if (!isXYPlot()) {
			return;
		}

		this.chart.getXYPlot().setDomainGridlinesVisible(state);
	}

	public void gridy(boolean state) {
		if (!isXYPlot()) {
			return;
		}

		this.chart.getXYPlot().setRangeGridlinesVisible(state);
	}

	protected Map<String, Color> getStringToColorMap() {
		Map<String, Color> ret = new HashMap<String, Color>();
		try {
			Class clazz = java.awt.Color.class;
			Field[] filc = clazz.getDeclaredFields();

			for (int i = 0; i < filc.length; i++) {
				// System.out.println(filc[i].getName());
				Field f = filc[i];

				if (Modifier.isStatic(f.getModifiers())
						&& Modifier.isPublic(f.getModifiers())) {
					Object o = f.get(null);

					if (o instanceof java.awt.Color) {
						ret.put(f.getName(), (Color) o);
					}
				}
			}
			return ret;
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * Clears the current figure. Preserves compatability with matplotlib.
	 * 
	 */
	public void clf() {
		this.chartList.clear();
		this.chart = null;
		this.chartMouseListeners.clear();
		this.chartToPanelMap.clear();

	}

	/**
	 * 
	 * errorbar(x, y, yerr=None, xerr=None, fmt='b-', ecolor=None, capsize=3,
	 * barsabove=False
	 */
	public Dataset errorbar(double[] x, double[] y, double[] xerr,
			double[] yerr, Color color, boolean barsabove) {

		// color = null;
		/* If there is no x data, create it, from 0 ... */
		if (x == null) {
			x = new double[y.length];
			for (int i = 0; i < y.length; i++)
				x[i] = i;
		}

		/* Then make the data series */
		XYSeries series1 = new XYSeries("1");
		XYSeries series2 = new XYSeries("2");

		/* Are the bars vertical or horizontal */
		boolean barsVertical = true;
		if (xerr != null) {
			barsVertical = false;
		}

		for (int i = 0; i < x.length; i++) {
			if (!(Double.isNaN(x[i]) || Double.isNaN(y[i])
					|| Double.isInfinite(x[i]) || Double.isInfinite(y[i]))) {
				if (barsVertical) {
					series1.add(x[i], y[i] - yerr[i]);
					series2.add(x[i], y[i] + yerr[i]);
				}

			}
		}

		// if(dataname != null)
		// series.setKey(dataname);

		/* If there is no chart, create it (defaults to XY chart). */
		if (this.chart == null) {
			createXYChart();
		}

		XYPlot plot = this.chart.getXYPlot();

		/*
		 * If we are creating a new axis, we need a new dataset, but only if
		 * there is no current dataset with dataseries.
		 */

		int currentDatasetIndex = plot.getDatasetCount();

		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(series1);
		dataset.addSeries(series2);
		plot.setDataset(currentDatasetIndex, dataset);

		XYStatisticalLineAndShapeRenderer renderer;
		if (color != null)
			renderer = new XYStatisticalLineAndShapeRenderer(color);
		else
			renderer = new XYStatisticalLineAndShapeRenderer(Color.black);

		// renderer = new XYLineAndShapeRenderer();
		// renderer.setBasePaint(color);

		plot.setRenderer(currentDatasetIndex, renderer);

		renderer.setSeriesVisibleInLegend(currentDatasetIndex, false);
		renderer.setSeriesVisibleInLegend(false);
		renderer.setSeriesVisibleInLegend(false, true);

		return dataset;

	}

	/**
	 * Assumes the most recent dataset is a XYSeriesCollection
	 * 
	 * @return
	 */
	protected Color getMostRecentColor() {
		try {
			int datasetIndex = this.chart.getXYPlot().getDatasetCount() - 1;
			System.out.println("datasetIndex:" + datasetIndex);

			XYSeriesCollection dataset = (XYSeriesCollection) this.chart
					.getXYPlot().getDataset(datasetIndex);
			// XYLineAndShapeRenderer renderer =
			// (XYLineAndShapeRenderer)this.chart.getXYPlot().getRenderer(datasetIndex);

			System.out.println(this.chart.getXYPlot()
					.getRendererForDataset(dataset).getClass().getName());
			return (Color) this.chart.getXYPlot()
					.getRendererForDataset(dataset)
					.getSeriesPaint(dataset.getSeriesCount() - 1);

			// System.out.println(renderer.getSeriesPaint(dataset.getSeriesCount()).getClass().getName());
		} catch (RuntimeException e) {
			e.printStackTrace();
		}

		// try
		// {
		// XYPlot plot = (XYPlot) chart.getPlot();
		// AbstractRenderer r1 = (AbstractRenderer) plot.getRenderer(0);
		// int seriesindex =
		// this.chart.getXYPlot().getDataset(this.chart.getXYPlot().getDatasetCount()).getSeriesCount()
		// - 1;
		// System.out.println("seriesindex:" + seriesindex);
		// if(seriesindex >= 0)
		// {
		// System.out.println(
		// r1.getSeriesPaint(seriesindex).getClass().getName() );
		//
		// return (Color)r1.getSeriesPaint(seriesindex);
		// }
		// }
		// catch (Exception e)
		// {
		// e.printStackTrace();
		// }

		return null;

	}

	protected boolean isDouble(String s) {
		try {
			Double.parseDouble(s);
			return true;
		} catch (NumberFormatException nfe) {
			return false;
		}

	}

	public void setRangeSpace(double x) {
		AxisSpace space = new AxisSpace();
		space.setLeft(x);
		this.chart.getXYPlot().setFixedRangeAxisSpace(space);
	}

	public void setDomainSpace(double x) {
		AxisSpace space = new AxisSpace();
		space.setBottom(x);
		this.chart.getXYPlot().setFixedDomainAxisSpace(space);
	}

	/**
	 * This assumes
	 * 
	 * @param filename
	 */
	public void savefig(String filename) {
		int calculatedWidth = (int) this.figureWidth * this.dpi;
		int calculatedHeight = (int) this.figureHeight * this.dpi;

		this.savefig(filename, calculatedWidth, calculatedHeight);
	}

	public void savefig(String filename, int width, int height) {
		try {
			String fname = filename;
			String filetype = fname.substring(fname.length() - 3);

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
			} else {
				System.out.println(filename + ": image type not supported");

			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	protected boolean isXYPlot() {
		if (chart == null) {
			System.err.println("chart is null");
			return false;
		}
		if (chart.getXYPlot() == null) {
			System.err.println("chart.getPlot() is null");
			return false;
		}
		return true;
	}

	public void setQuitOnWindowClose(boolean close) {
		closeOnExit = close;

		if (frame != null) {
			if (close) {
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			} else {
				frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			}
		}
	}

	public void setShapesVisible(boolean visible) {
		XYPlot plot = this.chart.getXYPlot();

		int currentDatasetIndex = plot.getDatasetCount() - 1;

		XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot
				.getRenderer(currentDatasetIndex);

		renderer.setShapesVisible(visible);

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
        chart.getPlot().setBackgroundPaint(color);
    }


	
	/**
	 * Set anti-Alias
	 * @param isAntiAlias
	 */
	public void setAntiAlias(boolean isAntiAlias){
	
	chart.setAntiAlias(isAntiAlias);

	}
	
	
	protected void setSeriesColor(Color color) {
		XYPlot plot = this.chart.getXYPlot();

		if (plot == null) {
			System.err.println("setSeriesColor() but plot is null");
			return;
		}

		if (color != null && plot.getDatasetCount() > 0) {
			plot.getRendererForDataset(
					plot.getDataset(plot.getDatasetCount() - 1))
					.setSeriesPaint(
							plot.getDataset(plot.getDatasetCount() - 1)
									.getSeriesCount() - 1, color);
		}

	}

}

// Axis.java

package jplot;

import java.util.Vector;
import java.util.Enumeration;

// imports for test code only
import java.awt.*;
import jhplot.H2D;

import jplot.PlotPoint;
import jplot.Utils;
import graph.*;

public class Contour {

	/***************************************************************************
	 * ************* * * Constants *
	 **************************************************************************/

	/*
	 * * The minimum length of a curve before it gets a label
	 */
	static final int MINCELLS = 30;

	/*
	 * * Default number of contour levels
	 */
	static final int NLEVELS = 12;

	/***************************************************************************
	 * * * Protected Variables *
	 **************************************************************************/

	protected Color GraphBackground = Color.white;

	protected Color DataBackground = Color.white;

	/**
	 * Dimension of the contour grid in the X direction
	 */
	protected int nx;

	/**
	 * Dimension of the contour grid in the Y direction
	 */
	protected int ny;

	/**
	 * Vector array containing the Contour curves. Each index in the array
	 * contains curves at a given contour level
	 */
	protected Vector curves[];

	/**
	 * If set the class calculates the contour levels based on the data minimum
	 * and maximum. Default value <i>true</i>.
	 */
	protected boolean autoLevels;

	/*
	 * If true the contour levels are calculated in logarithmic intervals
	 */
	protected boolean logLevels;

	/*
	 * If true the limits of the plot are the limits of the data grid not the
	 * limits of the contours!
	 */
	protected boolean gridLimits;

	/*
	 * The array of contour levels
	 */
	protected double levels[];

	/**
	 * The label for each contour level
	 */
	protected TextLine labels[];

	/**
	 * Font to use in drawing Labels
	 */
	protected Font labelfont;

	/**
	 * Color to use in drawing Labels
	 */
	protected Color labelcolor;

	/**
	 * Style to use in drawing Labels. TextLine.SCIENTIFIC or
	 * TextLine.ALGEBRAIC.
	 */
	protected int labelStyle;

	/**
	 * Precision to use in drawing Labels.
	 */
	protected int labelPrecision;

	/**
	 * Number of Significant figures to use in drawing Labels.
	 */
	protected int labelSignificant;

	/**
	 * Which levels will get labels. If it is equal to 1 every level gets a
	 * label, equal to 2 every second level etc. If it is equal to 0 no labels
	 * are displayed.
	 */
	protected int labelLevels;

	/**
	 * If false labels are not drawn
	 */
	protected boolean drawlabels;

	/**
	 * If true the labels will be calculated for each contour level. These might
	 * not look all that hot.
	 */
	protected boolean autoLabels;

	/**
	 * Color to draw non labelled contour line
	 */
	protected Color contourColor;

	/**
	 * Color to draw labelled contour line
	 */
	protected Color labelledColor;

	/**
	 * The data grid, a 2D array stored in linear form. It is assumed that [0,0]
	 * is the bottom left corner and the data is ordered by row.
	 */
	protected double grid[];

	/**
	 * The X minimum limit of the data grid
	 */
	protected double xmin;

	/**
	 * The X maximum limit of the data grid
	 */
	protected double xmax;

	/**
	 * The Y minimum limit of the data grid
	 */
	protected double ymin;

	/**
	 * The Y maximum limit of the data grid
	 */
	protected double ymax;

	/**
	 * The minimum value of the grid values
	 */
	protected double zmin;

	/**
	 * The maximum value of the grid values
	 */
	protected double zmax;

	/**
	 * Boolean value if true Contours will not be calculated
	 */
	public boolean noContours = false;

	private hep.aida.ref.histogram.Histogram2D h2d;

	/**
	 * Set the number of contour levels
	 */

	protected int n_levels;

	/**
	 * Color bank
	 */
	protected Color contour_color[];

	// H2D settings
	protected double binWidthX;
	protected double binWidthY;
	protected double binHight;

	/**
	 * Show or not bar
	 */
	protected boolean showBar;

	/**
	 * Show or not gray
	 */
	protected boolean showGray;

	private int barWidth, fullBarWidth;

	/**
	 * Size of step in X and Y, Z
	 */
	public double dx, dy, dz;

	public Contour(boolean bar, int nx, int ny, boolean gray, int n_levels) {

		grid = null;
		xmin = 0.0;
		xmax = 0.0;
		ymin = 0.0;
		ymax = 0.0;
		zmin = 0.0;
		zmax = 0.0;
		barWidth = 15;
		fullBarWidth = 2 * barWidth;
		h2d = null;
		this.nx = nx;
		this.ny = ny;
		showBar = bar;
		showGray = gray;
		this.n_levels = n_levels;
		calculateColors();

		autoLevels = true;
		logLevels = false;
		gridLimits = false;
		autoLabels = true;
		labelfont = new Font("Helvetica", Font.PLAIN, 12);
		labelcolor = Color.blue;
		labelLevels = 1;
		labelStyle = TextLine.ALGEBRAIC;
		labelPrecision = 1;
		labelSignificant = 2;
		drawlabels = true;

		contourColor = Color.blue;
		labelledColor = Color.black;

		curves = null;

		if (this.nx <= 0 || this.ny <= 0) {
			System.out.println("Error while defining the grid!");
		}
	}

	/**
	 * Set histogram and find all attributes
	 * 
	 * @param h
	 */
	public void setHistogram(H2D h) {
		this.h2d = h.get();
		nx = h2d.xAxis().bins(); // number of bins
		ny = h2d.yAxis().bins(); // number of bins
		binWidthX = h2d.xAxis().binWidth(0); // and its width
		binWidthY = h2d.yAxis().binWidth(0); // and its width
		binHight = h2d.maxBinHeight();
	}

	/**
	 * Compute gray colors
	 * 
	 * @param zlevel
	 *            zlevel (between 0 and 1)
	 * 
	 */
	public Color findGrayColors(double zlevel) {

		int scaleValue = (int) (255 * (1 - zlevel));
		int red = scaleValue;
		int green = scaleValue;
		int blue = scaleValue;

		return new Color(red, green, blue);

	}

	public int getBarWidth() {
		return barWidth;
	}

	public void setBarWidth(int barWidth) {
		this.barWidth = barWidth;
	}

	/**
	 * Get full bar width including the space and the text width
	 * 
	 * @return
	 */
	public int getFullBarWidth() {
		return fullBarWidth;
	}

	/**
	 * Compute spectrum colors
	 * 
	 * @param zlevel
	 *            zlevel (between 0 and 1)
	 * 
	 */
	public Color findSpectrumColors(double zlevel) {

		int n = (int) (n_levels * zlevel) - 1;
		if (n >= n_levels)
			n = n_levels - 1;
		if (n < 0)
			n = 0;

		return contour_color[n];

	}

	/**
	 * Compute color levels
	 * 
	 */
	public void calculateColors() {

		contour_color = new Color[n_levels];
		for (int i = 0; i < n_levels; i++) {
			float z = (float) ((float) i / (float) n_levels);
			if (z > 1.0)
				z = 0.999f;
			if (z < 0.0)
				z = 0.0f;
			contour_color[i] = Color.getHSBColor(z, 1.0f, 1.0f);

		}
		contour_color[0] = Color.white;

	}

	/**
	 * Return the grid
	 * 
	 * @return An array of size nx by ny contining the data grid.
	 */
	public double[] getGrid() {
		return grid;
	}

	/**
	 * return the dimensions of the grid
	 * 
	 * @return An array containing the number of columns, number of rows.
	 */
	public int[] getDim() {
		int i[] = new int[2];

		i[0] = nx;
		i[1] = ny;

		return i;
	}

	/**
	 * Draw the contour
	 * 
	 * @param g2
	 */
	public void drawColorBar(Graphics2D g2, Font f, int righmargin) {

		double dyy = (ymax - ymin) / (n_levels);
		double dz = (zmax - zmin) / (n_levels);
		// int bwid= (int)(0.2*righmargin);
		int bwid = barWidth;
		int bww = (int) (0.25 * bwid);

		int hwid = (int) (Math.abs(ymax - ymin) / (n_levels)) + 1; // fix
																	// round-off
																	// gaps
		FontMetrics fm = g2.getFontMetrics();

		// System.out.println(n_levels);0

		int wtext = 0;
		for (int m = 0; m < n_levels; m++) {

			Rectangle r = new Rectangle((int) xmax + bww, (int) (ymin - 1 + dyy
					* m + dyy), bwid, hwid);
			g2.setColor(contour_color[m]);
			g2.fill(r);
			g2.setColor(Color.black);
			g2.draw(r);

			String xlab = Utils.FormLin(zmin + dz * m);
			RTextLine text = new RTextLine();
			text.setFont(f);
			text.setColor(Color.black);
			text.setText(xlab);
			text.draw(g2, (int) xmax + (int) (1.6 * bwid), (int) (ymin - 1
					+ dyy * m + (int) (0.5 * dyy)));

			int text_width = fm.stringWidth(xlab);

			if (text_width > wtext)
				wtext = text_width;
		}

		fullBarWidth = wtext + bww + barWidth + 1;

	}

	/**
	 * Draw contour
	 * 
	 * @param g2
	 *            Graphics
	 * @param xmin
	 *            xmin in JAVA 2D coordinates
	 * @param xmax
	 * @param ymin
	 * @param ymax
	 */
	public void drawColor(Graphics2D g2, double xmin, double xmax, double ymin,
			double ymax) {

		this.xmin = xmin;
		this.xmax = xmax;
		this.ymin = ymin;
		this.ymax = ymax;
		int cout = 0;

		// System.out.println(nx);

		// new deltas
		dx = Math.abs(xmax - xmin) / nx;
		dy = Math.abs(ymax - ymin) / ny;
		dz = Math.abs(zmax - zmin);

		// ymin=Math.min(ymin,ymax);
		// xmin=Math.min(xmin,xmax);

		Rectangle r = new Rectangle();
		r.width = (int) (dx) + 1; // fix round-off gaps
		r.height = (int) (dy) + 1;

		double y1, y2, x1, x2, z;
		Color cold = g2.getColor();

		for (int j = 0; j < ny; j++) {

			// for Y- min is max due to java 2D
			y1 = ymin - j * dy - r.height;
			y2 = ymin - (j + 1) * dy - r.height;

			for (int i = 0; i < nx; i++) {
				x1 = xmin + i * dx;
				x2 = xmin + (i + 1) * dx;
				z = grid[cout] / dz;
				cout++;

				// g2.setColor(findGrayColors(z));

				if (showGray) {
					g2.setColor(findGrayColors(z));
				} else {
					g2.setColor(findSpectrumColors(z));
				}

				// g2.setColor(Color.blue);

				// invert in Y for JAVA2D
				r.x = (int) x1;
				r.y = (int) y1;
				g2.fillRect(r.x, r.y, r.width, r.height);

			}
		}

		g2.setColor(cold);

	}

	/*
	 * * zrange() * Calculate the range of the grid
	 */

	private void zrangeH2D() {

		if (h2d == null)
			return;

		zmin = 0;
		zmax = h2d.maxBinHeight();

		// System.out.println("Data range: zmin="+zmin+", zmax="+zmax);

		if (zmin == zmax) {
			System.out
					.println("Cannot produce contours of a constant surface!");
		}

		// calculate difference
		dz = zmax - zmin;

	}

	/*
	 * * zrange() * Calculate the range of the grid
	 */

	private void zrange() {
		int i;

		zmin = grid[0];
		zmax = grid[1];
		for (i = 0; i < grid.length; i++) {

			zmin = Math.min(zmin, grid[i]);
			zmax = Math.max(zmax, grid[i]);

		}

		// System.out.println("Data range: zmin="+zmin+", zmax="+zmax);

		if (zmin == zmax) {
			// System.out.println("Cannot produce contours of a constant
			// surface!");
		}

		// calculate difference
		dz = zmax - zmin;

	}

	/**
	 * Create a grid from a function
	 * 
	 * @param f
	 *            function name
	 * @param xmin
	 *            Min X
	 * @param xmax
	 *            Max X
	 * @param ymin
	 *            Min Y
	 * @param ymax
	 *            Max Y
	 * @param nx
	 *            No of points in X
	 * @param ny
	 *            No of points in Y
	 */
	public void createGrid(String f, double xmin, double xmax, double ymin,
			double ymax) {

		this.xmin = xmin;
		this.xmax = xmax;
		this.ymax = ymax;
		this.ymin = ymin;

		dx = Math.abs(xmax - xmin) / (nx - 1);
		dy = Math.abs(ymax - ymin) / (ny - 1);

		ParseFunction function = new ParseFunction(f);

		if (!function.parse()) {
			System.out.println("Failed to parse function!");
			return;
		}

		grid = new double[nx * ny];

		boolean error = false;
		double x, y;
		int count = 0;
		for (int j = 0; j < ny; j++) {
			y = ymin + j * dy;
			for (int i = 0; i < nx; i++) {
				x = xmin + i * dx;
				try {
					grid[count++] = function.getResult(x, y);
				} catch (Exception e) {
					grid[count++] = 0.0;
					error = true;
				}
			}
		}

		if (error) {
			System.out.println("Error while calculating points!");
			return;
		}

		zrange();
		setRange(xmin, xmax, ymin, ymax);
		setLimitsToGrid(true);
		setLabelLevels(3);
		// setNLevels(20);
	}

	/**
	 * Create a grid from a points
	 * 
	 * @param point
	 *            Vector with points
	 * @param xmin
	 *            Min X
	 * @param xmax
	 *            Max X
	 * @param ymin
	 *            Min Y
	 * @param ymax
	 *            Max Y
	 * @param nx
	 *            No of points in X
	 * @param ny
	 *            No of points in Y
	 */
	public void createGrid(Vector<PlotPoint> points, double xmin, double xmax,
			double ymin, double ymax) {

		this.xmin = xmin;
		this.xmax = xmax;
		this.ymax = ymax;
		this.ymin = ymin;

		// System.out.println(ymin);
		// System.out.println(ymax);

		grid = new double[nx * ny];

		double x1, y1, x2, y2;
		int count = 0;
		dx = Math.abs(xmax - xmin) / nx;
		dy = Math.abs(ymax - ymin) / ny;

		ymin = Math.min(ymin, ymax);
		xmin = Math.min(xmin, xmax);

		for (int j = 0; j < ny; j++) {
			// for Y- min is max due to java 2D
			y1 = ymin + j * dy;
			y2 = ymin + (j + 1) * dy;

			for (int i = 0; i < nx; i++) {

				x1 = xmin + i * dx;
				x2 = xmin + (i + 1) * dx;

				// run over data
				int con = 0;
				int k = 0;
				for (Enumeration e2 = points.elements(); e2.hasMoreElements(); k++) {
					PlotPoint pp = (PlotPoint) e2.nextElement();
					// System.out.println(pp.getX() );
					if (pp.getX() > x1 && pp.getX() < x2 && pp.getY() > y1
							&& pp.getY() < y2)
						con++;
				}

				// count
				// System.out.println(con);
				grid[count] = con;
				count++;

			}
		}

		// get z-ranges
		zrange();

	}

	/**
	 * Create a grid from a H2D histogram
	 * 
	 * @param point
	 *            Vector with points
	 * @param xmin
	 *            Min X
	 * @param xmax
	 *            Max X
	 * @param ymin
	 *            Min Y
	 * @param ymax
	 *            Max Y
	 * @param nx
	 *            No of points in X
	 * @param ny
	 *            No of points in Y
	 */
	public void createGrid(double xmi, double xma, double ymi, double yma) {

		if (h2d == null)
			return;

		double Xmin = xmi;
		double Xmax = xma;
		double Ymax = yma;
		double Ymin = ymi;

		// System.out.println(Ymin);
		// System.out.println(Ymax);

		grid = new double[nx * ny];

		double x1, y1, x2, y2;
		int count = 0;
		dx = Math.abs(Xmax - Xmin) / nx;
		dy = Math.abs(Ymax - Ymin) / ny;

		Ymin = Math.min(Ymin, Ymax);
		Xmin = Math.min(Xmin, Xmax);

		for (int j = 0; j < ny; j++) {
			// for Y- min is max due to java 2D
			y1 = Ymin + j * dy;
			y2 = Ymin + (j + 1) * dy;
			// System.out.println(dy);
			// double y1b=h2d.yAxis().binLowerEdge(j);
			// double y2b=h2d.yAxis().binUpperEdge(j);
			double yb = h2d.yAxis().binCenter(j);
			// System.out.println(Double.toString(y1)+" "+
			// Double.toString(yb)+ " "+Double.toString(y2));

			for (int i = 0; i < nx; i++) {

				x1 = Xmin + i * dx;
				x2 = Xmin + (i + 1) * dx;

				// double x1b=h2d.xAxis().binLowerEdge(i);
				// double x2b=h2d.xAxis().binUpperEdge(i);
				double xb = h2d.xAxis().binCenter(i);

				// System.out.println(Double.toString(y1)+" "+
				// Double.toString(yb)+ " "+Double.toString(y2));

				if (yb > y1 && yb < y2 && xb > x1 && xb < x2) {
					// System.out.println(h2d.binHeight(i + 1, j + 1) );
					grid[count] = h2d.binHeight(i + 1, j + 1);

				} else {
					grid[count] = 0;
				}
				count++;

			}
		}

		// get z-ranges
		zrange();

	}

	/**
	 * Set the background color for the entire canvas.
	 * 
	 * @params c The color to set the canvas
	 */
	public void setGraphBackground(Color c) {
		if (c == null)
			return;

		GraphBackground = c;

	}

	/**
	 * Set the background color for the data window.
	 * 
	 * @params c The color to set the data window.
	 */
	public void setDataBackground(Color c) {
		if (c == null)
			return;

		DataBackground = c;

	}

	/**
	 * Set the number of contour levels.
	 * 
	 * @@param l Number of contour levels
	 */
	public void setNLevels(int l) {
		if (l <= 0)
			return;

		n_levels = l;
		calculateColors();
		levels = new double[l];

		calcLevels();

		// detachCurves();
		curves = null;
	}

	/**
	 * If true the limits of the plot will be the grid limits. If false the
	 * limits of the plot will be the contours.
	 * 
	 * @param b
	 *            boolean
	 */

	public void setLimitsToGrid(boolean b) {
		gridLimits = b;
	}

	/**
	 * Set the contour levels that are to have labels.
	 * 
	 * <pre>
	 *      if 0 no labels are drawn
	 *      if 1 every level gets a label
	 *      If 2 every 2nd level gets a label
	 *      etc.
	 * </pre>
	 */
	public void setLabelLevels(int i) {
		if (i <= 0)
			labelLevels = 0;
		else
			labelLevels = i;
	}

	/*
	 * * calcLevels() * Calculate the contour levels
	 */
	private void calcLevels() {
		int i;
		int l;

		if (!autoLevels)
			return;

		if (levels == null)
			levels = new double[NLEVELS];
		labels = new TextLine[levels.length];
		// Nice label steps not implemented yet
		// levelStep();

		if (logLevels) {
			double inc = Math.log(zmax - zmin) / (double) (levels.length + 1);
			try {
				for (i = 0; i < levels.length; i++)
					levels[i] = zmin + Math.pow(Math.E, (double) (i + 1) * inc);
			} catch (Exception e) {
				System.out.println("Error calculateing Log levels!");
				System.out.println("... calculating linear levels instead");
				logLevels = false;
				calcLevels();
			}
		} else {
			double inc = (zmax - zmin) / (double) (levels.length + 1);
			for (i = 0; i < levels.length; i++)
				levels[i] = zmin + (double) (i + 1) * inc;
		}
	}

	/**
	 * Set the range of the grid
	 * 
	 * @param xmin
	 *            Minimum X value
	 * @param xmax
	 *            Maximum X value
	 * @param ymin
	 *            Minimum Y value
	 * @param ymax
	 *            Maximum Y value
	 */

	private void setRange(double xmin, double xmax, double ymin, double ymax) {

		if (xmin >= xmax || ymin >= ymax)
			return;

		this.xmin = xmin;
		this.xmax = xmax;
		this.ymin = ymin;
		this.ymax = ymax;
	}

	/**
	 * Return the range of the grid
	 * 
	 * @return An array contining xmin,xmax,ymin,ymax.
	 */
	public double[] getRange() {
		double d[] = new double[4];
		d[0] = xmin;
		d[1] = xmax;
		d[2] = ymin;
		d[3] = ymax;

		return d;
	}

	/**
	 * Set the font to be used with All the labels
	 * 
	 * @param f
	 *            Font
	 */
	public void setLabelFont(Font f) {
		labelfont = f;
	}

	/**
	 * Set the Color to be used with all the labels.
	 * 
	 * @param c
	 *            Color
	 */
	public void setLabelColor(Color c) {
		labelcolor = c;
	}

	/**
	 * Set the contour's color.
	 * 
	 * @param c
	 *            Color
	 */
	public void setContourColor(Color c) {
		contourColor = c;
	}

	/**
	 * Set the labelled contour's color.
	 * 
	 * @param c
	 *            Color
	 */
	public void setLabelledContourColor(Color c) {
		labelledColor = c;
	}

	/**
	 * Return the contour levels.
	 * 
	 * @return An array containing the contour levels
	 */
	public double[] getLevels() {
		return levels;
	}

}

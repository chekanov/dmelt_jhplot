/**
 *    Copyright (C)  DataMelt project. The jHPLot package.
 *    Includes coding developed for Centre d'Informatique Geologique
 *    by J.V.Lee priory 2000 GNU license.
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
 *    Copyright (C) 1999 J.D.Lee and S.Chekanov

 **/

package jplot;

import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import jhplot.shapes.HShape;

/**
 * <code>GraphSettings</code> contains the parameters which are used to draw the
 * graph such as number of ticks, scaling values, offset parameters, axis
 * lengths, colors, fonts, labels and their positions and much more. All these
 * parameters are settable by the GUI, and written to this class. Class
 * <code>Graph</code> and derived friends use this class and draw the graph
 * based on the parameters.
 */
public class GraphSettings extends DataChangeListener implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// some handy variables, if we need more than an boolean:
	static final int DISABLE = 0;
	static final int ENABLE = 1;
	static final int AUTO = 2;

	// this is a set of 20 pre-defined colors used to draw for lines and points:
	static final Color[] color = { new Color(0, 0, 102), // very dark blue
			new Color(255, 0, 0), // red

			new Color(0, 114, 0), // very dark green
			new Color(132, 0, 132), // dark-purple

			new Color(102, 0, 255), // dark blue
			new Color(255, 0, 102), // red-violet

			new Color(0, 145, 0), // dark green
			new Color(255, 0, 255), // light-purple

			new Color(0, 153, 255), // medium-light blue
			new Color(255, 153, 0), // orange

			new Color(0, 195, 100), // medium green
			new Color(255, 188, 255), // light-violet

			new Color(0, 255, 255), // light blue
			new Color(255, 255, 0), // yellow

			new Color(102, 255, 0), // light green
			new Color(255, 220, 226), // saumong

			new Color(10, 10, 10), // black
			new Color(100, 100, 100), // gray

			new Color(160, 160, 160), // gray
			new Color(220, 220, 220) }; // gray

	public static final float[] dashLengths = { 0.0f, 2.0f, 3.0f, 4.0f, 5.0f, 6.0f,
			7.0f, -1.0f };
	// Very large number, used to initialize stuff:
	static final double INF = 1e300;

	/**
	 * Total number of axes currently known by JPlot.
	 */
	static public final int N_AXES = 2;

	/**
	 * Symbolic constant used to name the (bottom) X-axis of the graph.
	 */
	static public final int X_AXIS = 0;
	/**
	 * Symbolic constant used to name the (left) Y-axis of the graph.
	 */
	static public final int Y_AXIS = 1;
	// static public final int Z_AXIS = 2;

	/**
	 * Graphtype, normal (classical) 2D graph. This is the graphtype most people
	 * want (and the default).
	 */
	static public final int GRAPHTYPE_2D = 0;

	/**
	 * Graphtype, contour type most people want (and the default).
	 */
	static public final int CONTOUR_2D = 4;

	/**
	 * Graphtype, piper diagram (ask geochemists what that is). To turn the
	 * graph in a piper-diagram, the data must conform the format (see the
	 * webpage for more info) and the datafile must have a
	 * <p>
	 * <code># graphtype = piper</code>
	 * <p>
	 * in the header.
	 */
	static public final int GRAPHTYPE_PIPER = 1;

	/**
	 * Graphtype, the not-very-common multi-plot graph. This graph type is
	 * composed of a number of separate series of X,Y pairs, each series
	 * represents an area. The end of a dataseries (within the file) is
	 * indicated by a third column with two stars (**) and the datafile must
	 * have a
	 * <p>
	 * <code># graph-type = multiplot</code>
	 * <p>
	 * in the header. The same effect may be obtained by including several
	 * files. The graph type enables the plot of activity-activity diagrams.
	 */
	static public final int GRAPHTYPE_MULTI = 2;

	/**
	 * Symbolic constant used for no symbols (points). There are actually 12
	 * predefined symbol-types. NO_SYMBOL has the value 13 is therefore reserved
	 * for 'use no symbol'.
	 */
	static public final int NO_SYMBOL = 13;

	/**
	 * Symbolic constant used for no lines (solid or dashed). There are actually
	 * 6 predefined line-types (dash-lengths). NO_LINE has the value 7 and is
	 * therefore reserved for 'use no line'.
	 */
	static public final int NO_LINE = 7;

	/*
	 * Index pointing to the actual color index. The index points to a list of
	 * 20 predefined colors (see above).
	 */
	int colorIndex = 0;

	/*
	 * Index pointing to the actual symbol (point) index. The index points to a
	 * list of 12 predefined symbols.
	 */
	int pointIndex = 0;

	/*
	 * Actual color. This color is used for previewing the color if different
	 * from one of the default colors:
	 */
	// public Color actualColor;

	// size of the graph panel:
	private Dimension panelSize;

	// offsets:
	private double leftMargin;
	private double rightMargin;
	private double bottomMargin;
	private double topMargin;

	// settings for contours
	private int contour_binsX;
	private int contour_binsY;
	private int contour_levels;
	private boolean contour_bar;
	private boolean contour_gray;

	// Graphtype, normal (classical) 2D graph. This is the graphtype
	// most people want (and the default).
	// it is assumed that this is a histogram on 2D, therefore, all negative
	// values
	// in Y are supressed when using automatic axis range
	// 0- whn no supression is done
	// 1 - zeros's are supressed during autorange (good for histograms)
	private int histo = 0;

	// tics:
	private double[] ticLength = new double[N_AXES];
	private double[] subticLength = new double[N_AXES];
	private int[] subticNumber = new int[N_AXES];

	private int maxNumberOfTics;
	private int[] numberOfTics = new int[N_AXES];
	private boolean[] useNumberOfTics = new boolean[N_AXES]; // should be
																// fixTics
	private boolean[] useTics = new boolean[N_AXES];
	private boolean[] rotTics = new boolean[N_AXES];
	private boolean[] mirrorTics = new boolean[N_AXES];
	private boolean[] useTicLabels = new boolean[N_AXES];

	private boolean antiAlias;

	// labels:
	private Vector<GraphLabel> labels = new Vector<GraphLabel>();

	// primitives
	private Vector<HShape> primitives = new Vector<HShape>();

	private Vector<String> ticklabelX = new Vector<String>();
	private Vector<String> ticklabelY = new Vector<String>();
	private boolean[] useAutoTics = new boolean[N_AXES];

	// axes:
	private double axesRatio;
	private boolean useRatio;
	private boolean[] useAxis = new boolean[N_AXES];
	private boolean[] mirrorAxis = new boolean[N_AXES];
	// boolean[] rotTics = new boolean [N_AXES];
	private double[] inv = new double[N_AXES];
	private float axesPenTicWidth;
	private int axesArrow;

	// colors:
	private Color backgroundColor;
	private Color axesColor;
	private Color[] labelColor = new Color[N_AXES];
	private Color titleColor;
	private Color graphBgColor;
	private Color[] ticColor = new Color[N_AXES];

	// legend:
	private Font legendFont;
	private boolean useLegend;
	private boolean useLegendPos;
	// FontMetrics legendFontMetrics;
	private double[] legendPos = new double[N_AXES];
	private double legendSpacing;

	private boolean NoData = false;

	// scaling:
	private boolean[] isLog = new boolean[N_AXES];
	private boolean[] aRange = new boolean[N_AXES];
	private double[] minValue = new double[N_AXES];
	private double[] maxValue = new double[N_AXES];
	// private float[] multiplier = new float[N_AXES];
	// private float[] additioner = new float[N_AXES];
	// private float[] globalDivider = new float[N_AXES];
	// private float[] globalOffset = new float[N_AXES];

	// fonts, colors used by the tic-labels
	private Font[] ticFont = new Font[N_AXES];

	// grid:
	private boolean[] useGrid = new boolean[N_AXES];
	private Color gridColor;
	private boolean toFront;
	private boolean toFrontPrimitives;
	private boolean isAttResizable;

	// gadgets:
	private boolean shadow;

	// piper diagram stuff:
	private Color triangleFillColor;
	private boolean useInner;

	// bounding box:
	private boolean useBox;
	private float boxOffset;
	private Color boxFillColor;
	private Color boxColor;

	// flags, used to avoid usless calculatations or updates:
	// private boolean updatePlot;
	private boolean fntChanged;

	// use points:
	private int pointMode;
	private int colorMode;

	// graph types and linestyles:
	private int graphType;
	private int graphStyle;

	float tdsFac;
	private float penWidthForAxis = 2.0f;
	boolean useTds;

	private final String lf = System.getProperty("line.separator");

	/**
	 * Principal constructor. Builds the object with defaults values.
	 * 
	 * @param frame
	 *            principle frame which hosts the JPlot instance. May be null if
	 *            the settings are used without the JPlot GUI.
	 */
	public GraphSettings(JFrame frame) {
		if (frame != null)
			setFrame(frame);
		reset();
	}

	/**
	 * Principal constructor. Builds the object with defaults values.
	 */
	public GraphSettings() {
		this(null);
	}

	/**
	 * Adds a label or, if already in the list, replaces a label with an
	 * identical text (the first one uncounted) with the new one.
	 * 
	 * @param gl
	 *            new label
	 */
	public void setLabel(GraphLabel gl) {
		boolean labelFound = false;
		for (Enumeration e = labels.elements(); e.hasMoreElements();) {
			GraphLabel g = (GraphLabel) e.nextElement();
			if (gl.equals(g.getText())) {
				g.copy(gl);
				labelFound = true;
				break;
			}
		}
		if (!labelFound)
			labels.add(gl);
	}

	/**
	 * Adds a label to the list of graph labels. This method doesn't check
	 * whether the label already exists in the list, it is added as it is.
	 * 
	 * @param gl
	 *            new label
	 */
	public void addLabel(GraphLabel gl) {
		labels.add(gl);
	}

	/**
	 * Adds a primitive to the list of graph labels. This method doesn't check
	 * whether the label already exists in the list, it is added as it is.
	 * 
	 * @param gl
	 *            new label
	 */
	public void addPrimitive(HShape ob) {
		// System.out.println("Add one primitive");
		primitives.add(ob);
	}

	/**
	 * Reset the positions of all the labels. All labels go to their default
	 * positions, all user-defined positions are forgotten.
	 */
	public void resetLabels() {
		for (Enumeration<GraphLabel> e = labels.elements(); e.hasMoreElements();) {
			((GraphLabel) e.nextElement()).setUsePosition(false);
		}
	}

	/**
	 * Returns a vector with the graph labels.
	 * 
	 * @see #setLabel(GraphLabel) #addLabel(GraphLabel) #resetLabels()
	 * @return the list of labels currently defined
	 */
	public Vector<GraphLabel> getLabels() {
		return labels;
	}

	/**
	 * Set new labels for the plot
	 * 
	 * @param vector
	 *            with new labels
	 */
	public void setLabels(Vector<GraphLabel> labels) {
		this.labels = labels;
	}

	/**
	 * Returns a vector with the graph primitives.
	 * 
	 * @see #setLabel(GraphLabel) #addLabel(GraphLabel) #resetLabels()
	 * @return the list of labels currently defined
	 */
	public Vector<HShape> getPrimitives() {
		return primitives;
	}

	/**
	 * Sets the graph type. The integer is a symbolic constant, must be one of
	 * the here-defined symbolic constants, such as <a
	 * href="#GRAPHTYPE_2D">GRAPHTYPE_2D</a>, for example.
	 * 
	 * @param type
	 *            graph style
	 */
	public void setGraphType(int type) {
		graphType = type;
		if (type == GRAPHTYPE_PIPER) {
			gridColor = new Color(200, 200, 200);
			ticFont[X_AXIS] = Utils.getDefaultFont().deriveFont(9f);
			ticFont[Y_AXIS] = Utils.getDefaultFont().deriveFont(9f);
		}
	}

	/**
	 * Returns the current graph type. The return value is a symbolic constant,
	 * such as <a href="#GRAPHTYPE_2D">GRAPHTYPE_2D</a>, for example.
	 * 
	 * @return the current graph type.
	 */
	public int getGraphType() {
		return graphType;
	}

	/**
	 * Returns the current graph type. if histo=0 - this is a standard 2D graph
	 * if histo=1 - zero is supressed during automatic range (good for showing
	 * histograms)
	 */
	public int get2DType() {
		return histo;
	}

	/**
	 * Returns the current graph type. The return value is a symbolic constant,
	 * such as <a href="#GRAPHTYPE_2D">GRAPHTYPE_2D</a>, for example.
	 * 
	 * @return the current graph type.
	 */
	public void set2DType(int type) {
		this.histo = type;
	}

	/**
	 * Return tick labels used to make plots. Typically, they are calculated
	 * automatically.
	 * 
	 * @param axis
	 *            which axis to use
	 */
	public Vector<String> getLabelTicks(int axis) {

		if (axis == 0)
			return ticklabelX;
		if (axis == 1)
			return ticklabelY;

		return ticklabelX;
	}

	/**
	 * Set and overwrite the tick labels.
	 * 
	 * @param axis
	 *            which axis to use
	 * @param vector
	 *            of tick labels
	 */
	public void setLabelTicks(int axis, Vector<String> ticklabel) {
		if (axis == 0) {
			ticklabelX = ticklabel;
		}
		if (axis == 1) {
			ticklabelY = ticklabel;
		}

	}

	/**
	 * If set to false, we stop automatically computing ticks. In this case, one
	 * can set ticks manually for a given axis.
	 * 
	 * @param axis
	 *            which axis to use
	 * @param auto
	 *            true when ticks are computed.
	 */
	public void setAutomaticTicks(int axis, boolean auto) {
		useAutoTics[axis] = auto;
	}

	/**
	 * Get or not automatic tick calculations
	 * 
	 * @param axis
	 *            axis value
	 * @return false if ticks are manual
	 */

	public boolean getAutomaticTicks(int axis) {
		return useAutoTics[axis];
	}

	/**
	 * Resets the class to default values.
	 */
	public void reset() {

		// System.out.println( graphType );

		for (int k = 0; k < N_AXES; k++) {

			useAutoTics[k] = true;
			minValue[k] = INF; // INF is 1e300
			maxValue[k] = -INF;
			aRange[k] = true;
			isLog[k] = false;
			useTicLabels[k] = true;
			useTics[k] = true;
			useAxis[k] = true;
			mirrorAxis[k] = true;
			rotTics[k] = false;
			mirrorTics[k] = true;
			numberOfTics[k] = 5;
			useNumberOfTics[k] = false;
			ticColor[k] = Color.black;
			ticFont[k] = Utils.getDefaultFont();
			useGrid[k] = true;
			// multiplier[k] = 1.0f;
			// additioner[k] = 0.0f;
			// globalDivider[k] = 1.0f;
			// globalOffset[k] = 0.0f;
			legendPos[k] = 5.0f;
			ticLength[k] = 0.02;
			subticLength[k] = 0.01;
			subticNumber[k] = 2;

			contour_binsX = 40;
			;
			contour_binsY = 40;
			;
			contour_levels = 10;
			contour_bar = true;
			contour_gray = false;

			// rotTics[k] = false;
		}

		antiAlias = false;
		isAttResizable = true;
		setDataChanged(false);
		axesRatio = 0.0;
		panelSize = new Dimension(470, 330);
		shadow = true;
		maxNumberOfTics = 10;
		axesColor = Color.black;
		axesArrow = 0;
		axesPenTicWidth = 2.0f;
		leftMargin = 20.;
		rightMargin = 27.;
		bottomMargin = 15.;
		topMargin = 15.;
		useRatio = false;
		histo = 0;
		legendFont = Utils.getDefaultFont();
		legendSpacing = 1.0f;
		useLegendPos = false;
		useLegend = true;
		gridColor = new Color(227, 230, 230);
		toFront = false;
		toFrontPrimitives = false;
		fntChanged = true;
		// updatePlot = false;
		pointMode = DISABLE;
		colorMode = AUTO;
		graphStyle = LinePars.LINES;
		graphType = GRAPHTYPE_2D;
		triangleFillColor = new Color(224, 223, 221);
		useInner = true;
		useBox = false;
		boxFillColor = Color.white;
		boxColor = Color.black;
		backgroundColor = Color.white;
		graphBgColor = Color.white;
		boxOffset = 3.0f;
		tdsFac = 1000.0f;
		useTds = true;
		penWidthForAxis = 2.0f;
		labels.clear();
		primitives.clear();
		if (JPlot.debug)
			System.out.println("settings reset in GraphSettings...");
	}

	/**
	 * Clear all labels
	 */
	public void removeLabels() {
		labels.clear();
	}

	/**
	 * Clear all primitives
	 */
	public void removePrimitives() {
		primitives.clear();
	}

	/**
	 * Remove i-th label
	 */
	public void removeLabel(int i) {
		if (i < labels.size())
			labels.remove(i);
	}

	/**
	 * Remove i-th label
	 */
	public void removePrimitive(int i) {
		if (i < primitives.size())
			primitives.remove(i);
	}

	/**
	 * No of labels
	 */
	public int numberLabels() {
		return labels.size();
	}

	/**
	 * No of primitives
	 */
	public int numberPrimitives() {
		return primitives.size();
	}

	/**
	 * Print the current parameter settings.
	 */
	public void print() {

		PrintStream out = System.out;
		print(out);
	}

	/**
	 * Print the current parameter settings.
	 * 
	 * @param out
	 *            input PrintStream
	 */
	public void print(PrintStream out) {

		out.println(" ");
		out.println("Graph Settings ");
		out.println("=========== ");

		// graph type
		out.println("graph-type: " + String.valueOf(graphType));
		out.println("graph-style: " + String.valueOf(graphStyle));
		out.println("histo: " + String.valueOf(histo));

		// graph-panel size on which we display the plot:
		out.println("\n ---- panel-size");
		out.println("width: " + String.valueOf(panelSize.width));
		out.println("height: " + String.valueOf(panelSize.height));

		out.println("\n ---- panel-margins");
		out.println("left: " + String.valueOf(leftMargin));
		out.println("right: " + String.valueOf(rightMargin));
		out.println("top: " + String.valueOf(topMargin));
		out.println("bottom: " + String.valueOf(bottomMargin));

		// background color of the graph
		out.println("graph-bgcolor: " + graphBgColor.toString());

		// background color of the panel
		out.println("panel-bgcolor: " + backgroundColor.toString());

		// ratio of the axes
		out.println("fix:" + String.valueOf(useRatio));
		out.println("axes-ratio :" + String.valueOf(axesRatio));

		// axes color
		out.println("axes-color: " + axesColor);
		out.println("axes-penwidth-tic: " + axesPenTicWidth);
		out.println("axes-arrow: " + axesArrow);

		// first update all the stuff which is X, Y axes-specific.
		// Axis 0 is the first X-axis
		// Axis 1 is the first Y-axis
		for (int k = 0; k < N_AXES; k++) {

			String loc = (k == 0) ? "x1" : ((k == 1) ? "y1" : "??");
			out.println("show: " + String.valueOf(useAxis[k]));
			out.println("mirror: " + String.valueOf(mirrorAxis[k]));
			// xw.add("location",loc);
			out.println(loc + "-axis");

			// min- and max-values of the axes:

			out.println("\n ---- range:");
			out.println("auto: " + String.valueOf(aRange[k]));
			out.println("min: " + String.valueOf(minValue[k]));
			out.println("max: " + String.valueOf(maxValue[k]));

			out.println("log: " + String.valueOf(isLog[k]));
			// xw.add("offset",String.valueOf(additioner[k]));
			// xw.add("multiplier",String.valueOf(multiplier[k]));

			// begin of tic stuff:
			out.println("\n --- tics:");
			out.println("show : " + String.valueOf(useTics[k]));
			out.println("rotate: " + String.valueOf(rotTics[k]));
			out.println("fix: " + String.valueOf(useNumberOfTics[k]));
			out.println("number: " + String.valueOf(numberOfTics[k]));
			out.println("length: " + String.valueOf(ticLength[k]));
			out.println("color: " + ticColor[k].toString());

			// tic labels
			out.println("show: " + String.valueOf(useTicLabels[k]));
			out.println("tic-labels");
			out.println("font: " + ticFont[k].toString());

		}

		// start grid stuff, show (distinguish horizontal or vertical lines):
		out.println("v-show: " + String.valueOf(useGrid[X_AXIS]));
		out.println("h-show: " + String.valueOf(useGrid[Y_AXIS]));
		out.println("\ngrid");
		out.println("color: " + gridColor);
		out.println("to-front: " + String.valueOf(toFront));

		out.println("to-front-primitives: " + String.valueOf(toFrontPrimitives));

		// start legend stuff, show or hide
		out.println("\n ---- legends:");
		out.println("show: " + String.valueOf(useLegend));
		out.println("fix: " + String.valueOf(useLegendPos));
		out.println("position");
		out.println("x: " + String.valueOf((int) legendPos[0]));
		out.println("y: " + String.valueOf((int) legendPos[1]));
		out.println("spacing: " + String.valueOf(legendSpacing));
		out.println("font: " + legendFont.toString());

		// start bounding-box stuff:
		out.println("\nbounding-box");
		out.println("show: " + String.valueOf(useBox));
		out.println("type2D: " + String.valueOf(histo));
		out.println("offset: " + String.valueOf(boxOffset));
		out.println("box-color: " + boxColor.toString());
		out.println("fill-color: " + boxFillColor.toString());

	}

	/**
	 * Writes the current parameter settings to a File stream. The method print
	 * the graph settings to a printwriter instance. All parameter values are
	 * saved in XML format according to keywords defined here.
	 * 
	 * @param xw
	 *            instance of the XML writing class
	 */
	public void getSettings(XMLWrite xw) {
		Vector v = new Vector();
		xw.open("graph-settings");

		// graph type
		xw.setData("graph-type", String.valueOf(graphType));
		xw.setData("graph-style", String.valueOf(graphStyle));
		xw.setData("graph-data", String.valueOf(NoData));
		xw.setData("graph-antialias", String.valueOf(antiAlias));
		xw.setData("graph-attresizable", String.valueOf(isAttResizable));

		// contour plots:
		xw.add("x", String.valueOf(contour_binsX));
		xw.add("y", String.valueOf(contour_binsY));
		xw.add("levels", String.valueOf(contour_levels));
		xw.add("bar", String.valueOf(contour_bar));
		xw.add("gray", String.valueOf(contour_gray));
		xw.set("contour");

		// graph-panel size on which we display the plot:
		xw.add("width", String.valueOf(panelSize.width));
		xw.add("height", String.valueOf(panelSize.height));
		xw.set("panel-size");

		xw.add("left", String.valueOf(leftMargin));
		xw.add("right", String.valueOf(rightMargin));
		xw.add("top", String.valueOf(topMargin));
		xw.add("bottom", String.valueOf(bottomMargin));
		xw.set("panel-margins");

		// background color of the graph
		xw.set("graph-bgcolor", graphBgColor);

		// background color of the panel
		xw.set("panel-bgcolor", backgroundColor);

		// ratio of the axes
		xw.add("fix", String.valueOf(useRatio));
		xw.setData("axes-ratio", String.valueOf(axesRatio));

		// axes color
		xw.set("axes-color", axesColor);
		xw.setData("axes-penwidth-tic", String.valueOf(axesPenTicWidth));
		xw.setData("axes-arrow", String.valueOf(axesArrow));

		// first update all the stuff which is X, Y axes-specific.
		// Axis 0 is the first X-axis
		// Axis 1 is the first Y-axis
		for (int k = 0; k < N_AXES; k++) {

			String loc = (k == 0) ? "x1" : ((k == 1) ? "y1" : "??");
			xw.add("show", String.valueOf(useAxis[k]));
			xw.add("mirror", String.valueOf(mirrorAxis[k]));
			// xw.add("location",loc);
			xw.open(loc + "-axis");

			// min- and max-values of the axes:

			xw.add("auto", String.valueOf(aRange[k]));
			xw.add("min", String.valueOf(minValue[k]));
			xw.add("max", String.valueOf(maxValue[k]));
			xw.set("range");

			xw.add("log", String.valueOf(isLog[k]));
			// xw.add("offset",String.valueOf(additioner[k]));
			// xw.add("multiplier",String.valueOf(multiplier[k]));
			xw.set("scaling");

			// begin of tic stuff:
			xw.add("show", String.valueOf(useTics[k]));
			xw.open("tics");
			xw.setData("rotate", String.valueOf(rotTics[k]));
			xw.add("fix", String.valueOf(useNumberOfTics[k]));
			xw.setData("number", String.valueOf(numberOfTics[k]));
			xw.setData("length", String.valueOf(ticLength[k]));
			xw.set("color", ticColor[k]);

			// tic labels
			xw.add("show", String.valueOf(useTicLabels[k]));
			xw.open("tic-labels");
			xw.set("font", ticFont[k]);
			xw.close(); // end of label stuff

			xw.close(); // end of tic stuff:
			xw.close(); // end of axis stuff
		}

		// start grid stuff, show (distinguish horizontal or vertical lines):
		xw.add("v-show", String.valueOf(useGrid[X_AXIS]));
		xw.add("h-show", String.valueOf(useGrid[Y_AXIS]));
		xw.open("grid");
		xw.set("color", gridColor);
		xw.setData("to-front", String.valueOf(toFront));
		xw.close();

		// start legend stuff, show or hide
		xw.add("show", String.valueOf(useLegend));
		xw.open("legend");
		xw.add("fix", String.valueOf(useLegendPos));
		xw.add("x", String.valueOf((int) legendPos[0]));
		xw.add("y", String.valueOf((int) legendPos[1]));
		xw.set("position");
		xw.setData("spacing", String.valueOf(legendSpacing));
		xw.set("font", legendFont);
		xw.close();

		// start bounding-boxx stuff:
		xw.add("show", String.valueOf(useBox));
		xw.add("type2D", String.valueOf(histo));
		xw.open("boundingbox");
		xw.setData("offset", String.valueOf(boxOffset));
		xw.set("box-color", boxColor);
		xw.set("fill-color", boxFillColor);
		xw.close();

		if (graphType == GRAPHTYPE_PIPER) {

			// start piper-diagram stuff
			xw.open("piper");

			// total dissolved solids stuff:
			xw.add("show", String.valueOf(useTds));
			xw.add("scaling", String.valueOf(tdsFac));
			xw.set("tds");

			xw.add("show", String.valueOf(useInner));
			xw.open("triangles");
			xw.set("color", triangleFillColor);
			xw.close(); // triangles

			xw.close(); // piper
		}

		// fill labels
		if (labels.size() > 0) {
			xw.open("labels");
			for (Enumeration e = labels.elements(); e.hasMoreElements();) {
				GraphLabel gl = (GraphLabel) e.nextElement();
				if (!gl.equals(GraphLabel.DATA))
					gl.getSettings(xw);
			}
			xw.close();
		}

		// fill primitives
		if (primitives.size() > 0) {
			xw.setData("to-front-primitives", String.valueOf(toFrontPrimitives));
			xw.open("primitives");
			for (Enumeration e = primitives.elements(); e.hasMoreElements();) {
				HShape hs = (HShape) e.nextElement();
				hs.getSettings(xw);
			}
			xw.close();
		}

		xw.close(); // end of graph-settings
	}

	/**
	 * Update the current settings. This method takes a specific XML Reader
	 * instance as its argument, which must contain the script.
	 * 
	 * @param xr
	 *            XML reader object.
	 */
	public void updateSettings(XMLRead xr) {
		if (JPlot.debug)
			System.out.println("updating for new settings in GraphSettings...");
		// open the reader for the field 'graph-settings' only:
		if (xr.open("graph-settings")) {
			graphType = xr.getInt("graph-type", graphType);
			graphStyle = xr.getInt("graph-style", graphStyle);
			NoData = xr.getBoolean("graph-data", NoData);
			antiAlias = xr.getBoolean("graph-antialias", antiAlias);
			isAttResizable = xr
					.getBoolean("graph-attresizable", isAttResizable);

			contour_binsX = xr.getInt("contour/x", contour_binsX);
			contour_binsY = xr.getInt("contour/y", contour_binsY);
			contour_levels = xr.getInt("contour/levels", contour_levels);
			contour_bar = xr.getBoolean("contour/bar", contour_bar);
			contour_gray = xr.getBoolean("contour/gray", contour_gray);

			leftMargin = xr.getDouble("panel-margins/left", leftMargin);
			rightMargin = xr.getDouble("panel-margins/right", rightMargin);
			topMargin = xr.getDouble("panel-margins/top", topMargin);
			bottomMargin = xr.getDouble("panel-margins/bottom", bottomMargin);

			histo = xr.getInt("type2D", histo);

			panelSize = xr.getDimension("panel-size", panelSize);
			graphBgColor = xr.getColor("graph-bgcolor", graphBgColor);
			backgroundColor = xr.getColor("panel-bgcolor", backgroundColor);
			boxFillColor = xr.getColor("panel-bgcolor", boxFillColor);
			useRatio = xr.getBoolean("axes-ratio/fix", useRatio);
			axesRatio = xr.getDouble("axes-ratio/value", axesRatio);
			axesColor = xr.getColor("axes-color", axesColor);
			axesArrow = xr.getInt("axes-arrow/value", axesArrow);
			axesPenTicWidth = (float) xr.getDouble("axes-penwidth-tic/value",
					axesPenTicWidth);

			// open a context for the axis:
			for (int i = 0; i < N_AXES; i++) {
				int axis;
				boolean b = false;
				if (i == 0) {
					axis = X_AXIS;
					b = xr.open("x1-axis");
				} else {
					axis = Y_AXIS;
					b = xr.open("y1-axis");
				}

				if (b) {
					useAxis[axis] = xr.getBoolean("show", useAxis[axis]);
					mirrorAxis[axis] = xr
							.getBoolean("mirror", mirrorAxis[axis]);
					aRange[axis] = xr.getBoolean("range/auto", aRange[axis]);
					minValue[axis] = xr.getDouble("range/min", minValue[axis]);
					maxValue[axis] = xr.getDouble("range/max", maxValue[axis]);
					isLog[axis] = xr.getBoolean("scaling/log", isLog[axis]);
					// additioner[axis] =
					// xr.getFloat("scaling/offset",additioner[axis]);
					// multiplier[axis] =
					// xr.getFloat("scaling/multiplier",multiplier[axis]);

					// opens a context for the tic stuff:
					if (xr.open("tics")) {
						useTics[axis] = xr.getBoolean("show", useTics[axis]);
						useNumberOfTics[axis] = xr.getBoolean("number/fix",
								useNumberOfTics[axis]);
						numberOfTics[axis] = xr.getInt("number/value",
								numberOfTics[axis]);
						ticLength[axis] = xr.getDouble("length/value",
								ticLength[axis]);
						labelColor[axis] = xr.getColor("color",
								labelColor[axis]);

						// opens a context for the label-settings of tic-labels
						if (xr.open("tic-labels")) {
							useTicLabels[axis] = xr.getBoolean("show",
									useTicLabels[axis]);
							ticFont[axis] = xr.getFont("font", ticFont[axis]);
							xr.close(); // closes the tic-label stuff
						}
						xr.close(); // closes the tic stuff for the axis
					}
					xr.close(); // closes the axis
				}
			}

			if (xr.open("grid")) {
				useGrid[X_AXIS] = xr.getBoolean("v-show", useGrid[X_AXIS]);
				useGrid[Y_AXIS] = xr.getBoolean("h-show", useGrid[Y_AXIS]);
				gridColor = xr.getColor("color", gridColor);
				toFront = xr.getBoolean("to-front", toFront);
				xr.close(); // closes grid
			}

			if (xr.open("legend")) {
				useLegend = xr.getBoolean("show", useLegend);
				useLegendPos = xr.getBoolean("position/fix", useLegendPos);
				legendPos[X_AXIS] = xr.getDouble("position/x",
						legendPos[X_AXIS]);
				legendPos[Y_AXIS] = xr.getDouble("position/y",
						legendPos[Y_AXIS]);
				legendSpacing = xr.getDouble("spacing/value", legendSpacing);
				legendFont = xr.getFont("font", legendFont);
				xr.close(); // closes legend
			}

			if (xr.open("boundingbox")) {
				useBox = xr.getBoolean("show", useBox);
				boxOffset = xr.getFloat("offset/value", boxOffset);
				boxColor = xr.getColor("box-color", boxColor);
				boxFillColor = xr.getColor("fill-color", boxFillColor);
				xr.close(); // closes boundingbox stuff
			}

			if (xr.open("labels")) {
				while (xr.open("label")) {
					GraphLabel gl = new GraphLabel();
					gl.updateSettings(xr);
					labels.add(gl);
					xr.close(); // close 'label'
					xr.hide("label"); // hide the label just read for the next
										// run
				}
				xr.unHide();
				xr.close(); // close 'labels'
			}

			// primitives
			if (xr.open("primitives")) {
				toFrontPrimitives = xr.getBoolean("to-front-primitives",
						toFrontPrimitives);

				while (xr.open("primitive")) {
					HShape hs = new HShape();
					hs.updateSettings(xr);
					primitives.add(hs);
					xr.close(); // close 'label'
					xr.hide("primitive"); // hide the label just read for the
											// next run
				}
				xr.unHide();
				xr.close(); // close 'labels'
			}

			xr.close(); // closes graph-settings
		}

		if (graphType == GRAPHTYPE_PIPER) {

			// start piper-diagram stuff
			if (xr.open("piper")) {
				useTds = xr.getBoolean("tds/show", useTds);
				tdsFac = xr.getFloat("tds/scaling", tdsFac);
				useInner = xr.getBoolean("triangles/show", useInner);
				triangleFillColor = xr.getColor("triangles/triangleFillColor",
						triangleFillColor);
				xr.close();
			}
		}
		if (JPlot.debug)
			System.out.println("...updating settings done.");
	}

	/**
	 * Returns the dimensions of the panel used to display the graph.
	 * 
	 * @see #setPanelSize(Dimension)
	 * @return the panel dimension of the graph
	 */
	public Dimension getPanelSize() {
		return panelSize;
	}

	/**
	 * Sets the dimensions of the panel of the graph.
	 * 
	 * @see #getPanelSize()
	 * @param d
	 *            dimension of the graph panel
	 */
	public void setPanelSize(Dimension d) {
		panelSize = d;
	}

	/**
	 * Returns the distance between the left-border of the panel and the left
	 * Y-axis.
	 * 
	 * @see #setLeftMargin(double)
	 * @return the left margin.
	 */
	public double getLeftMargin() {
		return leftMargin;
	}

	/**
	 * Sets the distance between the right-border of the panel and the right
	 * Y-axis.
	 * 
	 * @see #getRightMargin()
	 * @param rm
	 *            the right margin in points.
	 */
	public void setRightMargin(double rm) {
		rightMargin = rm;
	}

	/**
	 * Get pen width to draw axis
	 * 
	 * @return the right margin.
	 */
	public float getPenWidthAxis() {
		return penWidthForAxis;
	}

	/**
	 * Set pen width to draw axis
	 */
	public void setPenWidthAxis(float width) {
		penWidthForAxis = width;
	}

	/**
	 * Sets the distance between the left-border of the panel and the left
	 * Y-axis.
	 * 
	 * @see #getLeftMargin()
	 * @param lm
	 *            the left margin in points.
	 */
	public void setLeftMargin(double lm) {
		leftMargin = lm;
	}

	/**
	 * Returns the distance between the right-border of the panel and the right
	 * Y-axis.
	 * 
	 * @see #setRightMargin(double)
	 * @return the right margin.
	 */
	public double getRightMargin() {
		return rightMargin;
	}

	/**
	 * Return colors.
	 * 
	 * @return colors.
	 */
	public static Color[] getColors() {
		return color;
	}

	/**
	 * Returns the distance between the bottom-border of the panel and the
	 * bottom X-axis.
	 * 
	 * @see #setBottomMargin(double)
	 * @return the bottom margin.
	 */
	public double getBottomMargin() {
		return bottomMargin;
	}

	/**
	 * Sets the distance between the bottom-border of the panel and the bottom
	 * X-axis.
	 * 
	 * @see #getBottomMargin()
	 * @param bm
	 *            the bottom margin in points.
	 */
	public void setBottomMargin(double bm) {
		bottomMargin = bm;
	}

	/**
	 * Returns the distance between the top-border of the panel and the top
	 * X-axis.
	 * 
	 * @see #setTopMargin(double)
	 * @return the top margin.
	 */
	public double getTopMargin() {
		return topMargin;
	}

	/**
	 * Sets the distance between the top-border of the panel and the top X-axis.
	 * 
	 * @see #getTopMargin()
	 * @param tm
	 *            the top margin in points.
	 */
	public void setTopMargin(double tm) {
		topMargin = tm;
	}

	/**
	 * Returns the length of the tics.
	 * 
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>.
	 * @see #setTicLength(int,double)
	 * @return the length of the tics.
	 */
	public double getTicLength(int axis) {
		return ticLength[axis];
	}

	/**
	 * Returns the subtic length of the tics.
	 * 
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>.
	 * @see #setSubTicLength(int,double)
	 * @return the length of the tics.
	 */
	public double getSubTicLength(int axis) {
		return subticLength[axis];
	}

	/**
	 * Returns the number of subtics.
	 * 
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>.
	 * @return the number of subtic divisions.
	 */
	public int getSubTicNumber(int axis) {
		return subticNumber[axis];
	}

	/**
	 * Sets the length of the tics. In fact, the actual tic length is the value
	 * you set here multiplied by the axis length. By default, the tic-length is
	 * exactly 0.012 times the axis length. Using a value proportional to the
	 * axes system leads to reasonable proportions even if the graph is blown up
	 * to full screen (which users often do, trust me).
	 * 
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>.
	 * @param length
	 *            tic length relative to the axis length
	 */
	public void setTicLength(int axis, double length) {
		ticLength[axis] = length;
	}

	/**
	 * Sets the length of the subtics. In fact, the actual subtic length is the
	 * value you set here multiplied by the axis length. By default, the
	 * tic-length is exactly 0.07 times the axis length. Using a value
	 * proportional to the axes system leads to reasonable proportions even if
	 * the graph is blown up to full screen (which users often do, trust me).
	 * 
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>.
	 * @param length
	 *            tic length relative to the axis length
	 */
	public void setSubTicLength(int axis, double length) {
		subticLength[axis] = length;
	}

	/**
	 * Sets the number of the subtics. For log scale, it is 10 always
	 * 
	 * @param number
	 *            of subtics
	 **/

	public void setSubTicNumber(int axis, int number) {
		subticNumber[axis] = number;
	}

	/**
	 * Says whether we should use a fixed ratio between X- and Y-axes lengths.
	 * 
	 * @see #setUseAxesRatio(boolean)
	 * @return true if the ratio should be fixed, false otherwise.
	 */
	public boolean useAxesRatio() {
		return useRatio;
	}

	/**
	 * Sets whether or not to use a fixed ratio between X- and Y-axes lengths.
	 * By default, this ratio is defined by (read: proportional to) the panel
	 * dimensions.
	 * 
	 * @param b
	 *            true if the ratio is fixed and independent of the panel size.
	 */
	public void setUseAxesRatio(boolean b) {
		useRatio = b;
	}

	/**
	 * Returns the current ratio between X- and Y-axes lengths.
	 * 
	 * @see #setUseAxesRatio(boolean)
	 * @return the ratio Y-axisLength/X-axisLength
	 */
	public double getAxesRatio() {
		return axesRatio;
	}

	/**
	 * Sets the ratio between X- and Y-axes lengths. This ratio must be greater
	 * than 0.0, a value of 0.0 means that the ratio will be calculated
	 * automatically as a function of the size of the graph panel. <b>Note</b>
	 * that setting the axis ratio does not necessarily fix the ratio: use <a
	 * href="#setUseAxesRatio(boolean)">setUseAxesRatio</a> to fix the ratio.
	 * 
	 * @see #setUseAxesRatio(boolean)
	 * @param r
	 *            the ratio Y-axisLength/X-axisLength
	 */
	public void setAxesRatio(double r) {
		if (r > 0.0)
			axesRatio = r;
		else {
			axesRatio = 0.0;
			useRatio = false;
		}
	}

	/**
	 * Says whether the user fixes the number of tics for a specific axis. If
	 * not fixed, JPlot will try to calculate a convenient number of tics.
	 * 
	 * @see #useNumberOfTics(int)
	 * @see #setNumberOfTics(int,int)
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>.
	 * @param b
	 *            true if you want to fix the number of tics.
	 */
	public void setUseNumberOfTics(int axis, boolean b) {
		useNumberOfTics[axis] = b;
	}

	/**
	 * Returns whether or not to use a fixed number of tics.
	 * 
	 * @see #setUseNumberOfTics(int,boolean)
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>.
	 * @return true if the ratio should be used.
	 */
	public boolean useNumberOfTics(int axis) {
		return useNumberOfTics[axis];
	}

	/**
	 * Returns the number of tics for the specfied axis.
	 * 
	 * @see #setUseNumberOfTics(int,boolean)
	 * @see #setNumberOfTics(int,int)
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>.
	 * @return the number of tics for the specfied axis.
	 */
	public int getNumberOfTics(int axis) {
		return numberOfTics[axis];
	}

	/**
	 * Sets the number of tics for the specified axis. If not fixed, JPlot will
	 * try to calculate a convenient number of tics.
	 * 
	 * @see #useNumberOfTics(int)
	 * @see #setNumberOfTics(int,int)
	 * @param axis
	 *            defines to which axis this function applies.
	 * @param n
	 *            the number of tics
	 */
	public void setNumberOfTics(int axis, int n) {
		numberOfTics[axis] = (n > 0) ? n : 0;
	}

	/**
	 * Returns the maximum number of tics for log10 scale. This number applies
	 * to all axes and has to be 10.
	 * 
	 * @see #setMaxNumberOfTics(int)
	 * @return the maximum number of tics allowed by JPlot.
	 */
	public int getMaxNumberOfTics() {
		return maxNumberOfTics;
	}

	/**
	 * Sets the maximum number of tics allowed for log scale. For log10 scale,
	 * it must be 10.
	 * 
	 * @see #getMaxNumberOfTics()
	 * @param n
	 *            the maximum allowed number of tics for log10 scale
	 */
	public void setMaxNumberOfTics(int n) {
		maxNumberOfTics = n;
	}

	/**
	 * Returns the minimum data value for the specified axis.
	 * 
	 * @see #getMaxValue(int)
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>.
	 * @return the minimum-value of the data range.
	 */
	public double getMinValue(int axis) {
		return minValue[axis];
	}

	/**
	 * Sets the start-value displayed on the axis.
	 * 
	 * @see #setMaxValue(int,double)
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>.
	 * @param min
	 *            minimum value on the axis
	 */
	public void setMinValue(int axis, double min) {
		minValue[axis] = min;
	}

	/**
	 * Returns the maximum data value for the specified axis.
	 * 
	 * @see #getMinValue(int)
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>.
	 * @return the maximum-value of the data range.
	 */
	public double getMaxValue(int axis) {
		return maxValue[axis];
	}

	/**
	 * Sets the maximum data value displayed on the axis.
	 * 
	 * @see #setMinValue(int,double)
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>.
	 * @param max
	 *            maximum value on the axis
	 */
	public void setMaxValue(int axis, double max) {
		maxValue[axis] = max;
	}

	/**
	 * Sets the range (min-max) displayed on the axis.
	 * 
	 * @see #setMaxValue(int,double)
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
		minValue[axis] = min;
		maxValue[axis] = max;
	}

	/**
	 * Returns whether an axis will be drawn or not.
	 * 
	 * @see #setDrawAxis(int,boolean)
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>.
	 * @return true if the axis will be drawn.
	 */
	public boolean drawAxis(int axis) {
		return useAxis[axis];
	}

	/**
	 * Sets whether an axis will be drawn or not.
	 * 
	 * @see #drawAxis(int)
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>.
	 * @param b
	 *            toggle, true if the axis should be drawn.
	 */
	public void setDrawAxis(int axis, boolean b) {
		useAxis[axis] = b;
	}

	/**
	 * Returns whether the mirror of an axis will be drawn or not.
	 * 
	 * @see #setDrawMirrorAxis(int,boolean)
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>.
	 * @return true if the mirror axis will be drawn.
	 */
	public boolean drawMirrorAxis(int axis) {
		return mirrorAxis[axis];
	}

	/**
	 * Sets whether the mirror of an axis will be drawn or not.
	 * 
	 * @see #drawMirrorAxis(int)
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>.
	 * @param b
	 *            toggle, true if the mirror axis should be drawn.
	 */
	public void setDrawMirrorAxis(int axis, boolean b) {
		mirrorAxis[axis] = b;
	}

	/**
	 * Returns whether or not to draw tic labels.
	 * 
	 * @see #setDrawTicLabels(boolean)
	 * @see #setDrawTicLabels(int,boolean)
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>.
	 * @return true if tic labels should be drawn.
	 */
	public boolean drawTicLabels(int axis) {
		return useTicLabels[axis];
	}

	/**
	 * Sets whether all tic-labels will be written or not.
	 * 
	 * @see #drawTicLabels(int)
	 * @see #setDrawTicLabels(int,boolean)
	 * @param b
	 *            toggle, true if the axis should be drawn.
	 */
	public void setDrawTicLabels(boolean b) {
		useTicLabels[X_AXIS] = useTicLabels[Y_AXIS] = b;
	}

	/**
	 * Sets whether ticlabels will be written or not.
	 * 
	 * @see #drawTicLabels(int)
	 * @see #setDrawTicLabels(boolean)
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>.
	 * @param b
	 *            toggle, true if the axis should be drawn.
	 */
	public void setDrawTicLabels(int axis, boolean b) {
		useTicLabels[axis] = b;
	}

	/**
	 * Returns whether or not to draw tics (little lines on the axes).
	 * 
	 * @see #setDrawTics(int,boolean)
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>.
	 * @return true if tics should be drawn.
	 */
	public boolean drawTics(int axis) {
		return useTics[axis];
	}

	/**
	 * Sets whether or not to draw tics (little lines on the axes).
	 * 
	 * @see #drawTics(int)
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>.
	 * @param b
	 *            toggle, true if the tics should be drawn.
	 */
	public void setDrawTics(int axis, boolean b) {
		useTics[axis] = b;
	}

	/**
	 * Returns the actual background color of the entire graph panel.
	 * 
	 * @see #setBackgroundColor(Color)
	 * @see #setGraphBackgroundColor(Color)
	 * @see #setBoxFillColor(Color)
	 * @return background color of the graph.
	 */
	public Color getBackgroundColor() {
		return backgroundColor;
	}

	/**
	 * Sets the actual background color of the entire graph panel. Note that it
	 * is possible to set the color of the area within the axes system
	 * separately.
	 * 
	 * @see #getBackgroundColor()
	 * @see #setGraphBackgroundColor(Color)
	 * @see #setBoxFillColor(Color)
	 * @param color
	 *            new background color.
	 */
	public void setBackgroundColor(Color color) {
		backgroundColor = color;
	}

	/**
	 * Returns the actual color of the axes of the graph.
	 * 
	 * @return actual color used to draw the axes.
	 */
	public Color getAxesColor() {
		return axesColor;
	}

	/**
	 * Returns the axis arrow type.
	 * 
	 * @return 0 - no arrow, 1,2 - different arrow types
	 */
	public int getAxesArrow() {
		return axesArrow;
	}

	/**
	 * Returns the actual pen width of the tic axes of the graph.
	 * 
	 * @return actual pen width to draw the axes.
	 */
	public float getAxesPenTicWidth() {
		return axesPenTicWidth;
	}

	/**
	 * Sets the actual color of the axes of the graph.
	 * 
	 * @param color
	 *            new color to draw the axes.
	 */
	public void setAxesColor(Color color) {
		axesColor = color;
	}

	/**
	 * Sets the arrows for axes.
	 * 
	 * @param type
	 *            0 if no arrows, 1,2 - different type
	 */
	public void setAxesArrow(int type) {
		axesArrow = type;
	}

	/**
	 * Sets the actual pen width for tic axes of the graph.
	 * 
	 * @param color
	 *            new color to draw the axes.
	 */
	public void setAxesPenTicWidth(float pen) {
		axesPenTicWidth = pen;
	}

	/**
	 * Returns the font used by the labels drawn at each tic.
	 * 
	 * @see #setTicFont(int,Font)
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>.
	 * @return actual font used by the tic labels.
	 */
	public Font getTicFont(int axis) {
		return ticFont[axis];
	}

	/**
	 * Sets the font used by the labels drawn at each tic.
	 * 
	 * @see #getTicFont(int)
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>.
	 * @param font
	 *            the new font
	 */
	public void setTicFont(int axis, Font font) {
		ticFont[axis] = font;
		fntChanged = true;
	}

	/**
	 * Returns the color used by the labels drawn at each tic.
	 * 
	 * @see #setTicColor(int,Color)
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>.
	 * @return actual color used by the tic labels.
	 */
	public Color getTicColor(int axis) {
		return ticColor[axis];
	}

	/**
	 * Sets the color used by the labels drawn at each tic.
	 * 
	 * @see #getTicColor(int)
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>.
	 * @param color
	 *            the new color
	 */
	public void setTicColor(int axis, Color color) {
		ticColor[axis] = color;
	}

	/**
	 * Returns the font used by the legend.
	 * 
	 * @see #setLegendFont(Font)
	 * @return actual font used by the legend.
	 */
	public Font getLegendFont() {
		return legendFont;
	}

	/**
	 * Sets the actual font of the legend.
	 * 
	 * @see #getLegendFont()
	 * @param font
	 *            new font to draw the legend.
	 */
	public void setLegendFont(Font font) {
		legendFont = font;
		fntChanged = true;
	}

	/**
	 * Returns whether or not to draw the legend box in a graph corner. Note
	 * that once in the graph, you can drag 'n drop the legend.
	 * 
	 * @see #setDrawLegend(boolean)
	 * @return true if you want to show the legend
	 */
	public boolean drawLegend() {
		return useLegend;
	}

	/**
	 * Sets whether or not to draw the legend.
	 * 
	 * @see #drawLegend()
	 * @param b
	 *            true if you want to show the legend.
	 */
	public void setDrawLegend(boolean b) {
		useLegend = b;
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
	public double getLegendPosition(int axis) {
		return legendPos[axis];
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
	public void setLegendPosition(int axis, double coordinate) {
		legendPos[axis] = coordinate;
	}

	/**
	 * Sets the x and y coordinates (defined relative to the actual axes values)
	 * of the legend.
	 * 
	 * @param x
	 *            the x coordinate of the legend.
	 * @param y
	 *            the y coordinate of the legend.
	 */
	public void setLegendPosition(double x, double y) {
		legendPos[X_AXIS] = x;
		legendPos[Y_AXIS] = y;
	}

	/**
	 * Returns whether or not we use the legend coordinates. If not using the
	 * coordinates, we let JPlot define the legend position.
	 * 
	 * @see #setUseLegendPosition(boolean)
	 * @return true if you want to set the legend coordinates
	 */
	public boolean useLegendPosition() {
		return useLegendPos;
	}

	/**
	 * Sets whether or not to set the legend position in X,Y coordinates as
	 * defined by the graph. If not using the coordinates, we let JPlot define
	 * the legend position.
	 * 
	 * @see #useLegendPosition()
	 * @param b
	 *            true if you want to set the position
	 */
	public void setUseLegendPosition(boolean b) {
		useLegendPos = b;
	}

	/**
	 * Returns the vertical spacing, between each line of the legend.
	 * 
	 * @see #setLegendSpacing(double)
	 * @return vertical spacing for the legend.
	 */
	public double getLegendSpacing() {
		return legendSpacing;
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
	public void setLegendSpacing(double dy) {
		legendSpacing = dy;
	}

	/**
	 * Returns whether or not to plot using a logarithmic scale.
	 * 
	 * @see #setUseLogScale(int,boolean)
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>.
	 * @return true if the scale is logarithmic.
	 */
	public boolean useLogScale(int axis) {
		return isLog[axis];
	}

	/**
	 * Sets true or false to plot on a log scale.
	 * 
	 * @see #useLogScale(int)
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>.
	 * @param b
	 *            toggle, true if the scaling is logarithmic
	 */
	public void setUseLogScale(int axis, boolean b) {
		isLog[axis] = b;
	}

	/**
	 * Returns whether or not we should use automatic scaling.
	 * 
	 * @see #setAutoRange(int,boolean)
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>.
	 * @return true if the scale is logarithmic.
	 */
	public boolean autoRange(int axis) {
		return aRange[axis];
	}

	/**
	 * Sets true or false to use automatic scaling.
	 * 
	 * @see #autoRange(int)
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>.
	 * @param b
	 *            toggle, true if the the automatic scaling feature is enabled.
	 */
	public void setAutoRange(int axis, boolean b) {
		aRange[axis] = b;
	}

	/**
	 * Returns whether or not we should draw tics on the mirror axis.
	 * 
	 * @see #setDrawMirrorTics(int,boolean)
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>.
	 * @return true if the mirror tics should be drawn
	 */
	public boolean drawMirrorTics(int axis) {
		return mirrorTics[axis];
	}

	/**
	 * Sets true or false to draw mirror tics
	 * 
	 * @see #drawMirrorTics(int)
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>.
	 * @param b
	 *            toggle, true if the we should draw mirror tics
	 */
	public void setDrawMirrorTics(int axis, boolean b) {
		mirrorTics[axis] = b;
	}

	/**
	 * Defines whether we should rotate tics. By default the are pointing to the
	 * inside of the graph: setting it to false will draw them towards the
	 * outside.
	 * 
	 * @see #rotateTics(int)
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>.
	 * @param b
	 *            toggle, true if the we should rotate the tics
	 */
	public void setRotateTics(int axis, boolean b) {
		rotTics[axis] = b;
	}

	/**
	 * Returns whether or not we should rotate the tics. Rotated tics are drawn
	 * at the outer-side of the axes.
	 * 
	 * @see #setRotateTics(int,boolean)
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>.
	 * @return true if the tics will be rotated.
	 */
	public boolean rotateTics(int axis) {
		return rotTics[axis];
	}

	// /**
	// * Returns whether or not we should update the graph. This is a flag
	// * which is checked for in a few places in JPlot and if true, it
	// * updates the graph.
	// * @return true if the graph should be updated
	// */
	// public boolean updateGraph() {
	// return updatePlot;
	// }

	// /**
	// * Sets true or false to update the graph.
	// * @param b toggle, true if the we should update.
	// */
	// void setUpdateGraph(boolean b) {
	// updatePlot = b;
	// }

	/**
	 * Returns whether or not a shadow will be drawn at the panel border. Only
	 * nice if applied to a graph panel which is wrapped inside another panel,
	 * hence enabled in JPlot.
	 * 
	 * @return true if the shadow should be drawn.
	 */
	public boolean drawShadow() {
		return shadow;
	}

	/**
	 * Defines whether or not a shadow will be drawn at the panel border.
	 * 
	 * @see #drawShadow() for more info
	 * @param b
	 *            toggle, true if the shadow should be drawn.
	 */
	public void setShadow(boolean b) {
		shadow = b;
	}

	/**
	 * Sets whether or not using grid lines. Grid lines are lines drawn from tic
	 * to tic. They can be enabled/disabled per axis.
	 * 
	 * @see #drawGrid(int)
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>.
	 * @param b
	 *            toggle, true if the the grid should be drawn.
	 */
	public void setDrawGrid(int axis, boolean b) {
		useGrid[axis] = b;
	}

	/**
	 * Returns true if the grid should be drawn.
	 * 
	 * @see #setDrawGrid(int,boolean)
	 * @param axis
	 *            defines to which axis this function applies, generally
	 *            something like <a href="#X_AXIS">X_AXIS</a> or <a
	 *            href="#Y_AXIS">Y_AXIS</a>.
	 * @return true if the grid lines should be used
	 */
	public boolean drawGrid(int axis) {
		return useGrid[axis];
	}

	/**
	 * Sets the color used by the grid. Usually, the same color applies to
	 * horizontal and vertical lines.
	 * 
	 * @see #getGridColor()
	 * @param c
	 *            color of the grid lines.
	 */
	public void setGridColor(Color c) {
		gridColor = c;
	}

	/**
	 * Sets the color of the overall grid. The same color applies to horizontal
	 * and vertical lines.
	 * 
	 * @see #setGridColor(Color)
	 * @return the grid color
	 */
	public Color getGridColor() {
		return gridColor;
	}

	/**
	 * Flag, saying whether the grid should be drawn at the foreground or not.
	 * 
	 * @see #setGridToFront(boolean)
	 * @return whether or not to draw the grid at the front.
	 */
	public boolean gridToFront() {
		return toFront;
	}

	/**
	 * Flag, saying whether the primitives should be drawn at the foreground or
	 * not.
	 * 
	 * @return whether or not to draw the primitives at the front.
	 */
	public boolean primitivesToFront() {
		return toFrontPrimitives;
	}

	/**
	 * Setting the flag to true, this will draw the grid <em>after</em> drawing
	 * all the lines and point stuff, hence on the foreground.
	 * 
	 * @see #gridToFront()
	 * @param b
	 *            true if the grid will be moved to the front
	 */
	public void setGridToFront(boolean b) {
		toFront = b;
	}

	/**
	 * Setting the flag to true, this will draw the primitives <em>after</em>
	 * drawing all the lines and point stuff, hence on the foreground.
	 * 
	 * @param b
	 *            true if the primitives will be moved to the front
	 */
	public void setPrimitivesToFront(boolean b) {
		toFrontPrimitives = b;
	}

	/**
	 * Returns the color used to fill the shaded triangles.
	 * 
	 * @see #setInnerColor(Color)
	 * @return the color used to fill the shaded triangles.
	 */
	public Color getInnerColor() {
		return triangleFillColor;
	}

	/**
	 * Sets the color used to fill the inner triangles. This is for the very
	 * special piper-diagram graph, one of the fancy possibilities of JPlot ;-)
	 * 
	 * @see #getInnerColor()
	 * @param c
	 *            the color used to fill the shaded triangles.
	 */
	public void setInnerColor(Color c) {
		triangleFillColor = c;
	}

	/**
	 * Sets whether or not to draw the filled triangles.
	 * 
	 * @see #drawInner()
	 * @param b
	 *            true if the filled triangles should be drawn.
	 */
	public void setDrawInner(boolean b) {
		useInner = b;
	}

	/**
	 * Enables the drawing of the inner triangles for a Piper diagram.
	 * 
	 * @see #setDrawInner(boolean)
	 * @return true if the filled triangles should be drawn.
	 */
	public boolean drawInner() {
		return useInner;
	}

	/**
	 * Sets whether or not to draw the bounding box arround the graph. Note:
	 * this box can be filled with any color.
	 * 
	 * @see #drawBox()
	 * @see #setBoxOffset(float)
	 * @see #setBoxFillColor(Color)
	 * @param b
	 *            true if the filled triangles should be drawn.
	 */
	public void setDrawBox(boolean b) {
		useBox = b;
	}

	/**
	 * Set no data flag
	 * 
	 **/

	public void setNoData(boolean b) {
		NoData = b;
	}

	/**
	 * Get no data flag
	 * 
	 **/
	public boolean getNoData() {
		return NoData;
	}

	/**
	 * How many bins for contours in X
	 * 
	 * @return
	 */

	public int getContour_binsX() {
		return contour_binsX;
	}

	/**
	 * How many bins for contours in Y
	 * 
	 * @return
	 */

	public int getContour_binsY() {
		return contour_binsY;
	}

	/**
	 * set number of bins in X
	 * 
	 * @param contour_bins
	 */
	public void setContour_binsX(int contour_bins) {
		this.contour_binsX = contour_bins;
	}

	/**
	 * set number of bins in Y
	 * 
	 * @param contour_bins
	 */
	public void setContour_binsY(int contour_bins) {
		this.contour_binsY = contour_bins;
	}

	/**
	 * Which color to use
	 * 
	 * @return
	 */
	public boolean getContour_gray() {
		return contour_gray;
	}

	/**
	 * set color
	 * 
	 * @param contour_color
	 */

	public void setContour_gray(boolean gray) {
		this.contour_gray = gray;
	}

	/**
	 * How many levels
	 * 
	 * @return
	 */

	public int getContour_levels() {
		return contour_levels;
	}

	/**
	 * Set number of leves
	 * 
	 * @param contour_levels
	 */

	public void setContour_levels(int contour_levels) {
		this.contour_levels = contour_levels;
	}

	/**
	 * Show or not bars
	 * 
	 * @return
	 */
	public boolean getContour_bar() {
		return contour_bar;
	}

	/**
	 * Set contour bar
	 * 
	 * @param contour_bar
	 */
	public void setContour_bar(boolean contour_bar) {
		this.contour_bar = contour_bar;
	}

	/**
	 * Returns whether or not to draw the bounding box arround the graph.
	 * 
	 * @see #setDrawBox(boolean)
	 * @return true if the bounding box should be drawn.
	 */
	public boolean drawBox() {
		return useBox;
	}

	/**
	 * Sets the offset of the bounding box drawn around a graph.
	 * 
	 * @see #getBoxOffset()
	 * @see #setDrawBox(boolean)
	 * @see #setBoxFillColor(Color)
	 * @param f
	 *            offset in pixels.
	 */
	public void setBoxOffset(float f) {
		boxOffset = f;
	}

	/**
	 * Returns the offset of the bounding box.
	 * 
	 * @see #setBoxOffset(float)
	 * @see #setDrawBox(boolean)
	 * @return the offset of the bounding box.
	 */
	public float getBoxOffset() {
		return boxOffset;
	}

	/**
	 * Sets the fill color of the bounding box drawn around a graph.
	 * 
	 * @see #getBoxFillColor()
	 * @see #setBoxOffset(float)
	 * @see #setDrawBox(boolean)
	 * @param c
	 *            color.
	 */
	public void setBoxFillColor(Color c) {
		boxFillColor = c;
	}

	/**
	 * Returns the fill-color of the eventual bounding box arround the graph
	 * 
	 * @see #setBoxOffset(float)
	 * @see #setDrawBox(boolean)
	 * @see #setBoxFillColor(Color).
	 * @return the fill color of the bounding box.
	 */
	public Color getBoxFillColor() {
		return boxFillColor;
	}

	/**
	 * Sets the color of the bounding box drawn around a graph.
	 * 
	 * @see #setBoxOffset(float)
	 * @see #setDrawBox(boolean)
	 * @see #setBoxFillColor(Color).
	 * @param c
	 *            drawing color.
	 */
	public void setBoxColor(Color c) {
		boxColor = c;
	}

	/**
	 * Returns the color used to draw the bounding box.
	 * 
	 * @return the color of the bounding box.
	 */
	public Color getBoxColor() {
		return boxColor;
	}

	/**
	 * Sets the background color of the graph. This is the area between the axes
	 * (hence not the entire panel area).
	 * 
	 * @param c
	 *            color.
	 */
	public void setGraphBackgroundColor(Color c) {
		graphBgColor = c;
	}

	/**
	 * Returns the background color of the graph. This is the area between the
	 * axes (hence not the entire panel area).
	 * 
	 * @return the background color of the graph.
	 */
	public Color getGraphBackgroundColor() {
		return graphBgColor;
	}

	/**
	 * Sets the factor used to plot a TDS circle. Only useful for the very very
	 * very specific Piper diagram. The size of the circle is proportional to
	 * the TDS but scaled by some factor 100 or so. This factor is set by the
	 * user with this method.
	 * 
	 * @param f
	 *            scaling factor for the TDS circle.
	 */
	public void setTdsFactor(float f) {
		tdsFac = f;
	}

	/**
	 * Returns the factor used to plot a TDS circle.
	 * 
	 * @see #setTdsFactor(float)
	 * @return the factor used to plot a TDS circle.
	 */
	public float getTdsFactor() {
		return tdsFac;
	}

	/**
	 * Sets whether the tds should be drawn or not. Only useful for the very
	 * very very specific Piper diagram.
	 * 
	 * @see #setTdsFactor(float)
	 * @param b
	 *            true if the TDS circle should be drawn on a piper diagram
	 */
	public void setDrawTds(boolean b) {
		useTds = b;
	}

	/**
	 * Returns true if the tds circle should be drawn. Only useful for the very
	 * very very specific Piper diagram.
	 * 
	 * @return true if the tds circle should be drawn.
	 */
	public boolean drawTds() {
		return useTds;
	}

	/**
	 * Returns the index of the next color of the vector of predefined colors.
	 * Calling this function subsequently iterates through the array of
	 * pre-defined colors.
	 * 
	 * @return index to the next color.
	 */
	public int getColorIndex() {
		if (colorIndex == color.length)
			colorIndex = 0;
		return colorIndex++;
	}

	/**
	 * Returns the index of the next point type of the vector of predefined
	 * point types. Note that the last point type is not used, since this is
	 * only a very small dot.
	 * 
	 * @return index to the next symbol or point-type.
	 */
	public int getPointIndex() {
		if (pointIndex == NO_SYMBOL - 1)
			pointIndex = 0;
		return pointIndex++;
	}

	/**
	 * Resets the color index to the first color.
	 */
	public void resetColorIndex() {
		colorIndex = 0;
	}

	/**
	 * Resets the point-type index to the first symbol.
	 */
	public void resetPointIndex() {
		pointIndex = 0;
	}

	/**
	 * set antialiasing
	 * 
	 * @param setit
	 *            true if it should be set
	 */
	public void setAntiAlias(boolean setit) {
		antiAlias = setit;
	}

	public void setAttResizable(boolean setit) {
		isAttResizable = setit;
	}

	/**
	 * get antialiasing
	 * 
	 * @return true if it set
	 */
	public boolean getAntiAlias() {
		return antiAlias;
	}

	/**
	 * is attribute resizable?
	 * 
	 * @return true if it set
	 */
	public boolean getAttResizable() {
		return isAttResizable;
	}

	/**
	 * Returns a flag to indicate that some of the graph-fonts have changed. We
	 * use this to avoid a time-consuming font-metrics update every time the
	 * graph is repainted.
	 * 
	 * @return true if fontmetrics should be updated.
	 */
	public boolean fontChanged() {
		return fntChanged;
	}

	
	
	/**
	 * Sets a flag to indicate that some of the graph-fonts have changed. We use
	 * this to avoid a time-consuming font-metrics update every time the graph
	 * is repainted.
	 * 
	 * @param b
	 *            true if fontmetrics should be updated.
	 */
	public void setFontChanged(boolean b) {
		fntChanged = b;
	}
}

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
 *    @author J.V.Lee and S.Chekanov 
 **/

package jplot;

import java.text.DecimalFormat;
import java.util.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;

/**
 * This is a special-purpose array for use with the graphical plotting program
 * JPlot. Its main functionality is to provide a series of plot points (X,Y
 * pairs) which form a line or something to plot.
 * <p>
 * A data array has plotting attributes, e.g. saying whether we should plot
 * symbols, draw lines or fill areas. All the plotting attributes are defined in
 * the base-class LinePars, here we set the actual data array, i.e. a vector of
 * points (PlotPoints) defining X,Y.
 * 
 * @see LinePars
 */
public class DataArray extends LinePars implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// / index number of the data file for this array
	protected int file;
	protected int dimension;
	protected String title = " ";
	// / column index of the selected column
	protected int col = 0;
	protected double mean = 0;
	protected int Ntot = 0;
	protected Vector<PlotPoint> points;
	protected double[] maxValue = new double[GraphSettings.N_AXES];
	protected double[] minValue = new double[GraphSettings.N_AXES];
	protected double[] lowestNonZeroValue = new double[GraphSettings.N_AXES];

	/**
	 * Default constructor, initializes the class with nothing.
	 */
	public DataArray() {
		this(-1, -1);
	}

	/**
	 * Initializes the class with a file- and column index. The indices are
	 * numbers, kind of tags of this array. The file-index should point to the
	 * data file index (in the order displayed by the PlotPanels), and the
	 * column-index is the n'th Y-column plotted by the current array.
	 * 
	 * @param file
	 *            index of the file
	 * @param col
	 *            index of the column
	 */
	public DataArray(int file, int col) {
		this(file, col, 0);
	}

	/**
	 * Initializes the class with a file- and column index and allocates memory
	 * for the data points. The indices are numbers, kind of tags of this array.
	 * The file-index should point to the data file index (in the order
	 * displayed by the PlotPanels), and the column-index is the n'th Y-column
	 * plotted by the current array.
	 * 
	 * @param file
	 *            index of the file
	 * @param col
	 *            index of the column
	 * @param n
	 *            buffer, maximum number of datapoints.
	 */
	public DataArray(int file, int col, int n) {
		this.file = file;
		this.col = col;
		allocate(n);
	}

	/**
	 * Initializes the class with a file- and column index and allocates memory
	 * for the data points. The indices are numbers, kind of tags of this array.
	 * The file-index should point to the data file index (in the order
	 * displayed by the PlotPanels), and the column-index is the n'th Y-column
	 * plotted by the current array.
	 * 
	 * @param file
	 *            index of the file
	 * @param col
	 *            index of the column
	 * @param n
	 *            buffer, maximum number of datapoints.
	 * @param lp
	 *            line parameters class, contains info of how to draw the line.
	 */
	public DataArray(int file, int col, int n, LinePars lp) {
		super(lp);
		this.file = file;
		this.col = col;
		allocate(n);
	}

	/**
	 * Initializes the class with a file- and column index and allocates memory
	 * for the data points. The indices are numbers, kind of tags of this array.
	 * The file-index should point to the data file index (in the order
	 * displayed by the PlotPanels), and the column-index is the n'th Y-column
	 * plotted by the current array.
	 * 
	 * @param file
	 *            index of the file
	 * @param col
	 *            index of the column
	 * @param n
	 *            buffer, maximum number of datapoints.
	 * @param lp
	 *            line parameters class, contains info of how to draw the line.
	 */
	public DataArray(int file, LinePars lp) {
		super(lp);
		this.file = file;
		allocate(0);
	}

	/**
	 * Allocation of memory for the data points.
	 * 
	 * @param n
	 *            maximum number of datapoints.
	 */
	public void allocate(int n) {
		if (n < 10)
			n = 10;
		points = new Vector<PlotPoint>(n, 10);
		// error_upper = new Vector(n,10);
		// error_lower = new Vector(n,10);
		for (int k = 0; k < GraphSettings.N_AXES; k++) {
			maxValue[k] = -GraphSettings.INF;
			minValue[k] = GraphSettings.INF;
			lowestNonZeroValue[k] = GraphSettings.INF;
		}
	}

	/**
	 * Updates maximum and minimum values.
	 * 
	 * @param x
	 *            just entered x-value of the point
	 * @param y
	 *            just entered y-value of the point
	 */
	public void updateMinMax(double x, double y) {
		if (x > maxValue[GraphSettings.X_AXIS])
			maxValue[GraphSettings.X_AXIS] = x;
		if (x < minValue[GraphSettings.X_AXIS])
			minValue[GraphSettings.X_AXIS] = x;
		if (x < lowestNonZeroValue[GraphSettings.X_AXIS] && x > 0.0) {
			lowestNonZeroValue[GraphSettings.X_AXIS] = x;
		}
		if (y > maxValue[GraphSettings.Y_AXIS])
			maxValue[GraphSettings.Y_AXIS] = y;
		if (y < minValue[GraphSettings.Y_AXIS])
			minValue[GraphSettings.Y_AXIS] = y;
		if (y < lowestNonZeroValue[GraphSettings.Y_AXIS] && y > 0.0) {
			lowestNonZeroValue[GraphSettings.Y_AXIS] = y;
		}
	}

	/**
	 * Updates maximum and minimum values. Takes care of errors.
	 * 
	 * @param x
	 *            just entered x-value of the point
	 * @param y
	 *            just entered y-value of the point
	 * @param left
	 *            - error on x (left)
	 * @param right
	 *            - error on x (right)
	 * @param upper
	 *            - error on y (upper)
	 * @param lower
	 *            - error on y (lower)
	 */
	public void updateMinMax(double x, double y, double left, double right,
			double upper, double lower) {
		if (x + right > maxValue[GraphSettings.X_AXIS])
			maxValue[GraphSettings.X_AXIS] = x + right;
		if (x - left < minValue[GraphSettings.X_AXIS])
			minValue[GraphSettings.X_AXIS] = x - left;
		if (x - left < lowestNonZeroValue[GraphSettings.X_AXIS]
				&& x - left > 0.0) {
			lowestNonZeroValue[GraphSettings.X_AXIS] = x - left;
		}
		if (y + upper > maxValue[GraphSettings.Y_AXIS])
			maxValue[GraphSettings.Y_AXIS] = y + upper;
		if (y - lower < minValue[GraphSettings.Y_AXIS])
			minValue[GraphSettings.Y_AXIS] = y - lower;
		if (y - lower < lowestNonZeroValue[GraphSettings.Y_AXIS]
				&& y - lower > 0.0) {
			lowestNonZeroValue[GraphSettings.Y_AXIS] = y - lower;
		}
	}

	/**
	 * Updates maximum and minimum values. Takes care of errors.
	 * 
	 * @param x
	 *            just entered x-value of the point
	 * @param y
	 *            just entered y-value of the point
	 * @param left
	 *            - error on x (left)
	 * @param right
	 *            - error on x (right)
	 * @param upper
	 *            - error on y (upper)
	 * @param lower
	 *            - error on y (lower)
	 * @param left_sys
	 *            - error on x (left) - second level, used for systematics
	 * @param right_sys
	 *            - error on x (right)
	 * @param upper_sys
	 *            - error on y (upper)
	 * @param lower_sys
	 *            - error on y (lower)
	 */
	public void updateMinMax(double x, double y, double left, double right,
			double upper, double lower, double left_sys, double right_sys,
			double upper_sys, double lower_sys) {
		if (x + right + right_sys > maxValue[GraphSettings.X_AXIS])
			maxValue[GraphSettings.X_AXIS] = x + right + right_sys;

		if (x - left - left_sys < minValue[GraphSettings.X_AXIS])
			minValue[GraphSettings.X_AXIS] = x - left - left_sys;

		if (x - left - left_sys < lowestNonZeroValue[GraphSettings.X_AXIS]
				&& x - left - left_sys > 0.0) {
			lowestNonZeroValue[GraphSettings.X_AXIS] = x - left - left_sys;
		}
		if (y + upper + upper_sys > maxValue[GraphSettings.Y_AXIS])
			maxValue[GraphSettings.Y_AXIS] = y + upper + upper_sys;

		if (y - lower - lower_sys < minValue[GraphSettings.Y_AXIS])
			minValue[GraphSettings.Y_AXIS] = y - lower - lower_sys;
		if (y - lower - lower_sys < lowestNonZeroValue[GraphSettings.Y_AXIS]
				&& y - lower - lower_sys > 0.0) {
			lowestNonZeroValue[GraphSettings.Y_AXIS] = y - lower - lower_sys;
		}
	}

	/**
	 * Updates Mean values.
	 * 
	 * @param x
	 *            just entered x-value of the point
	 * @param y
	 *            just entered y-value of the point
	 */
	public void updateMean(double x, double y) {

		mean += x;
		Ntot++;
	}

	public double mean() {
		return mean / (double) Ntot;
	}

	/**
	 * Adds the values of a plot-point pair (x,y). This point is added at the
	 * end of the array.
	 * 
	 * @param x
	 *            x-value of the plot-point
	 * @param y
	 *            y-value of the plot-point
	 */
	public void addPoint(double x, double y) {
		y *= multiplier;
		y += additioner;
		updateMinMax(x, y);
		updateMean(x, y);
		points.add(new PlotPoint(x, y));
	}

	/**
	 * Adds the values of a plot-point pair (x,y). it alos incudes upper and
	 * lower errors This point is added at the end of the array.
	 * 
	 * @param x
	 *            x-value of the plot-point
	 * @param y
	 *            y-value of the plot-point
	 * @param upper
	 *            - error on y (upper)
	 * @param lower
	 *            - error on y (lower)
	 */
	public void addPoint(double x, double y, double upper, double lower) {
		y *= multiplier;
		y += additioner;

		upper *= multiplier;
		upper += additioner;

		lower *= multiplier;
		lower += additioner;

		updateMinMax(x, y, 0, 0, upper, lower);
		updateMean(x, y);

		// System.out.println(upper);

		points.add(new PlotPoint(x, y, upper, lower));
	}

	/**
	 * Adds the values of a plot-point pair (x,y). it alos incudes upper and
	 * lower errors on X and Y This point is added at the end of the array.
	 * 
	 * @param x
	 *            x-value of the plot-point
	 * @param y
	 *            y-value of the plot-point
	 * @param left
	 *            - error on x (left)
	 * @param right
	 *            - error on x (right)
	 * @param upper
	 *            - error on y (upper)
	 * @param lower
	 *            - error on y (lower)
	 */
	public void addPoint(double x, double y, double left, double right,
			double upper, double lower) {
		y *= multiplier;
		y += additioner;

		upper *= multiplier;
		upper += additioner;

		lower *= multiplier;
		lower += additioner;

		updateMinMax(x, y, left, right, upper, lower);
		updateMean(x, y);

		// System.out.println(upper);

		points.add(new PlotPoint(x, y, left, right, upper, lower));
	}

	/**
	 * Adds the values of a plot-point pair (x,y). it alos incudes upper and
	 * lower errors on X and Y This point is added at the end of the array.
	 * 
	 * @param x
	 *            x-value of the plot-point
	 * @param y
	 *            y-value of the plot-point
	 * @param left
	 *            - error on x (left)
	 * @param right
	 *            - error on x (right)
	 * @param upper
	 *            - error on y (upper)
	 * @param lower
	 *            - error on y (lower)
	 * @param left_sys
	 *            - error on x (left) - second level, used for systematics
	 * @param right_sys
	 *            - error on x (right)
	 * @param upper_sys
	 *            - error on y (upper)
	 * @param lower_sys
	 *            - error on y (lower)
	 */
	public void addPoint(double x, double y, double left, double right,
			double upper, double lower, double left_sys, double right_sys,
			double upper_sys, double lower_sys) {
		y *= multiplier;
		y += additioner;

		upper *= multiplier;
		upper += additioner;

		lower *= multiplier;
		lower += additioner;

		upper_sys *= multiplier;
		upper_sys += additioner;

		lower_sys *= multiplier;
		lower_sys += additioner;

		updateMinMax(x, y, left, right, upper, lower, left_sys, right_sys,
				upper_sys, lower_sys);
		updateMean(x, y);

		// System.out.println(upper);

		points.add(new PlotPoint(x, y, left, right, upper, lower, left_sys,
				right_sys, upper_sys, lower_sys));
	}

	/**
	 * Adds the values of a plot-point pair (x,y). This point is added at the
	 * end of the array.
	 * 
	 * @param x
	 *            x-value of the plot-point
	 * @param y
	 *            y-value of the plot-point
	 * @param liftPen
	 *            true if the pen should lift after this point
	 */
	public void addPoint(double x, double y, boolean liftPen) {
		y *= multiplier;
		y += additioner;
		updateMinMax(x, y);
		updateMean(x, y);
		points.add(new PlotPoint(x, y, liftPen));
	}

	/**
	 * Sets the values of a plot-point pair (x,y).
	 * 
	 * @param i
	 *            index of the plot-point
	 * @param x
	 *            x-value of the plot-point
	 * @param y
	 *            y-value of the plot-point
	 */
	public void setPoint(int i, double x, double y) {
		if (i >= 0 && i < points.size()) {
			updateMinMax(x, y);
			points.add(i, new PlotPoint(x, y));
		}
	}

	/**
	 * Sets the values of a plot-point pair (x,y).
	 * 
	 * @param i
	 *            index of the plot-point
	 * @param x
	 *            x-value of the plot-point
	 * @param y
	 *            y-value of the plot-point
	 * @param upper
	 *            upper error on y
	 * @param lower
	 *            lower error on y
	 **/
	public void setPoint(int i, double x, double y, double upper, double lower) {
		if (i >= 0 && i < points.size()) {
			updateMinMax(x, y, 0, 0, upper, lower);
			points.add(i, new PlotPoint(x, y, upper, lower));
		}
	}

	/**
	 * Sets the values of a plot-point pair (x,y).
	 * 
	 * @param i
	 *            index of the plot-point
	 * @param x
	 *            x-value of the plot-point
	 * @param y
	 *            y-value of the plot-point
	 * @param left
	 *            - error on x (left)
	 * @param right
	 *            - error on x (right)
	 * @param upper
	 *            - error on y (upper)
	 * @param lower
	 *            - error on y (lower)
	 **/
	public void setPoint(int i, double x, double y, double left, double right,
			double upper, double lower) {
		if (i >= 0 && i < points.size()) {
			updateMinMax(x, y, left, right, upper, lower);
			points.add(i, new PlotPoint(x, y, left, right, upper, lower));
		}
	}

	/**
	 * Sets the values of a plot-point pair (x,y).
	 * 
	 * @param i
	 *            index of the plot-point
	 * @param x
	 *            x-value of the plot-point
	 * @param y
	 *            y-value of the plot-point
	 * @param left
	 *            - error on x (left)
	 * @param right
	 *            - error on x (right)
	 * @param upper
	 *            - error on y (upper)
	 * @param lower
	 *            - error on y (lower)
	 * @param left_sys
	 *            - error on x (left) - second level, used for systematics
	 * @param right_sys
	 *            - error on x (right)
	 * @param upper_sys
	 *            - error on y (upper)
	 * @param lower_sys
	 *            - error on y (lower)
	 **/
	public void setPoint(int i, double x, double y, double left, double right,
			double upper, double lower, double left_sys, double right_sys,
			double upper_sys, double lower_sys) {
		if (i >= 0 && i < points.size()) {
			updateMinMax(x, y, left, right, upper, lower, left_sys, right_sys,
					upper_sys, lower_sys);
			points.add(i, new PlotPoint(x, y, left, right, upper, lower,
					left_sys, right_sys, upper_sys, lower_sys));
		}
	}

	/**
	 * Replace the values of a plot-point pair (x,y).
	 * 
	 * @param i
	 *            index of the plot-point
	 * @param x
	 *            x-value of the plot-point
	 * @param y
	 *            y-value of the plot-point
	 * @param left
	 *            - error on x (left)
	 * @param right
	 *            - error on x (right)
	 * @param upper
	 *            - error on y (upper)
	 * @param lower
	 *            - error on y (lower)
	 * @param left_sys
	 *            - error on x (left) - second level, used for systematics
	 * @param right_sys
	 *            - error on x (right)
	 * @param upper_sys
	 *            - error on y (upper)
	 * @param lower_sys
	 *            - error on y (lower)
	 **/
	public void replacePoint(int i, double x, double y, double left,
			double right, double upper, double lower, double left_sys,
			double right_sys, double upper_sys, double lower_sys) {
		if (i >= 0 && i < points.size()) {
			points.set(i, new PlotPoint(x, y, left, right, upper, lower,
					left_sys, right_sys, upper_sys, lower_sys));

		}
	}

	/**
	 * update all min and max. Call this after all replacements are done.
	 * 
	 **/
	public void allUpdate() {

		maxValue[GraphSettings.X_AXIS] = 0;
		minValue[GraphSettings.X_AXIS] = 0;
		lowestNonZeroValue[GraphSettings.X_AXIS] = 0;
		maxValue[GraphSettings.Y_AXIS] = 0;
		minValue[GraphSettings.Y_AXIS] = 0;
		lowestNonZeroValue[GraphSettings.Y_AXIS] = 0;
		mean = 0;

		for (int i = 0; i < points.size(); i++) {
			// update all
			updateMinMax(getX(i), getY(i), getXleft(i), getXright(i),
					getYupper(i), getYlower(i), getXleftSys(i),
					getXrightSys(i), getYupperSys(i), getYlowerSys(i));
			updateMean(getX(i), getY(i));
		}
	}

	/**
	 * Returns the plot-point of the specified index.
	 * 
	 * @param i
	 *            index of the plot-point
	 * @return plotpoint at index i
	 */
	public PlotPoint getPoint(int i) {
		if (i >= 0 && i < points.size())
			return (PlotPoint) points.get(i);
		return null;
	}

	/**
	 * Defines that, for this plotpoint, the pen should lift. This allows to
	 * draw discontinuous graphs.
	 * 
	 * @param i
	 *            index of the plot-point
	 */
	public void setLiftPen(int i) {
		if (i >= 0 && i < points.size()) {
			((PlotPoint) points.get(i)).setLiftPen(true);
		}
	}

	public boolean liftPen(int i) {
		return ((PlotPoint) points.get(i)).liftPen();
	}

	/**
	 * Return the length of the data vector.
	 * 
	 * @return length of the PlotPoint vector
	 */
	public int size() {
		return points.size();
	}

	/**
	 * Return the dimension of this data
	 * 
	 * @return dimension
	 */
	public int getDimension() {
		return dimension;
	}

	/**
	 * Set the dimension of this data
	 * 
	 * @param dimension
	 */
	public void setDimension(int dimension) {
		this.dimension = dimension;
	}

	/**
	 * Return the data vector
	 * 
	 * @return data vector with all the plot points
	 */
	public Vector getData() {
		return points;
	}

	/**
	 * Return the upper-error vector
	 * 
	 * @return data vector with all the plot points
	 */
	// public Vector getUpperError() {
	// return error_upper;
	// }

	/**
	 * Return the lower-error vector
	 * 
	 * @return data vector with all the plot points
	 */
	// public Vector getLowerError() {
	// return error_lower;
	// }

	/**
	 * Return a specific X-value. This function returns POSINF (1e300) if index
	 * i falls beyond the valid range.
	 * 
	 * @param i
	 *            index of the array
	 * @return the value of x at index i
	 */
	public double getX(int i) {
		if (i >= 0 && i < size()) {
			return ((PlotPoint) points.get(i)).getX();
		}
		return GraphSettings.INF;
	}

	/**
	 * Return a specific left error on X-value. if index i falls beyond the
	 * valid range.
	 * 
	 * @param i
	 *            index of the array
	 * @return the value of x at index i
	 */
	public double getXleft(int i) {
		if (i >= 0 && i < size()) {
			return ((PlotPoint) points.get(i)).getXleft();
		}
		return GraphSettings.INF;
	}

	/**
	 * Return a specific right error on X-value. if index i falls beyond the
	 * valid range.
	 * 
	 * @param i
	 *            index of the array
	 * @return the value of x at index i
	 */
	public double getXright(int i) {
		if (i >= 0 && i < size()) {
			return ((PlotPoint) points.get(i)).getXright();
		}
		return GraphSettings.INF;
	}

	/**
	 * Return a specific left error on X-value (systematical error). if index i
	 * falls beyond the valid range.
	 * 
	 * @param i
	 *            index of the array
	 * @return the value of x at index i
	 */
	public double getXleftSys(int i) {
		if (i >= 0 && i < size()) {
			return ((PlotPoint) points.get(i)).getXleftSys();
		}
		return GraphSettings.INF;
	}

	/**
	 * Return a specific right error on X-value (systematical error). if index i
	 * falls beyond the valid range.
	 * 
	 * @param i
	 *            index of the array
	 * @return the value of x at index i
	 */
	public double getXrightSys(int i) {
		if (i >= 0 && i < size()) {
			return ((PlotPoint) points.get(i)).getXrightSys();
		}
		return GraphSettings.INF;
	}

	/**
	 * Return a specific Y-value. This function returns POSINF (1e300) if index
	 * i falls beyond the valid range.
	 * 
	 * @param i
	 *            index of the array
	 * @return the value of y at index i
	 */
	public double getY(int i) {
		if (i >= 0 && i < size()) {
			return ((PlotPoint) points.get(i)).getY();
		}
		return GraphSettings.INF;
	}

	/**
	 * Return a specific upper error on Y-value. This function returns POSINF
	 * (1e300) if index i falls beyond the valid range.
	 * 
	 * @param i
	 *            index of the array
	 * @return the value of y at index i
	 */
	public double getYupper(int i) {
		if (i >= 0 && i < size()) {
			return ((PlotPoint) points.get(i)).getYupper();
		}
		return GraphSettings.INF;
	}

	/**
	 * Return a specific lower error on Y-value. This function returns POSINF
	 * (1e300) if index i falls beyond the valid range.
	 * 
	 * @param i
	 *            index of the array
	 * @return the value of y at index i
	 */
	public double getYlower(int i) {
		if (i >= 0 && i < size()) {
			return ((PlotPoint) points.get(i)).getYlower();
		}
		return GraphSettings.INF;
	}

	/**
	 * Return a specific systematical upper error on Y-value. This function
	 * returns POSINF (1e300) if index i falls beyond the valid range.
	 * 
	 * @param i
	 *            index of the array
	 * @return the value of y at index i
	 */
	public double getYupperSys(int i) {
		if (i >= 0 && i < size()) {
			return ((PlotPoint) points.get(i)).getYupperSys();
		}
		return GraphSettings.INF;
	}

	/**
	 * Return a specific total lower error on Y-value. This function returns
	 * POSINF (1e300) if index i falls beyond the valid range.
	 * 
	 * @param i
	 *            index of the array
	 * @return the value of y at index i
	 */
	public double getYlowerSys(int i) {
		if (i >= 0 && i < size()) {
			return ((PlotPoint) points.get(i)).getYlowerSys();
		}
		return GraphSettings.INF;
	}

	/**
	 * Returns the maximum value in the range. Careful, no error checking on the
	 * value of axis, which should be less than N_AXES, defined in
	 * GraphSettings.
	 * 
	 * @param axis
	 *            defines to which axis this function applies.
	 * @return the maximum value.
	 */
	public double getMaxValue(int axis) {
		return maxValue[axis];
	}

	/**
	 * Returns the minimum value in the range. Careful, no error checking on the
	 * value of axis, which should be less than N_AXES, defined in
	 * GraphSettings.
	 * 
	 * @param axis
	 *            defines to which axis this function applies.
	 * @return the minimum value.
	 */
	public double getMinValue(int axis) {
		return minValue[axis];
	}

	/**
	 * Returns the lowest, but non-zero value in the range. This is a a usefull
	 * 'minimum' value for e.g. logarithmic ranges.
	 * 
	 * @param axis
	 *            defines to which axis this function applies.
	 * @return the minimum value.
	 */
	public double getLowestNonZeroValue(int axis) {
		return lowestNonZeroValue[axis];
	}

	/**
	 * Returns the stroke used to plot these points. By default, a solid line of
	 * width 1 is assumed.
	 * 
	 * @return the actual stroke.
	 */
	public BasicStroke getStroke() {
		float[] dash = { dashLength, dashLength };
		if (dashLength == 0.0f)
			return new BasicStroke(penWidth);
		return new BasicStroke(penWidth, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_BEVEL, 0, dash, 0);
	}

	/**
	 * Sets the drawing style parameters at once.
	 * 
	 * @param l
	 *            line and point drawing parameters.
	 */
	public void setLinePars(LinePars l) {
		copy(l);
	}

	/**
	 * Get the drawing style parameters at once. can be done in the future (not
	 * functional)
	 */
	public LinePars getLinePars() {
		LinePars gg = new LinePars();
		return gg;
	}

	/**
	 * Returns the file index of this data array. This is the index of the file
	 * from which this data has been taken.
	 * 
	 * @return file index of this array.
	 */
	public int getFileIndex() {
		return file;
	}

	/**
	 * Returns the column index of this data array. This is the index of the
	 * column of the file from which this data has been taken.
	 * 
	 * @return column index of this array.
	 */
	public int getColumnIndex() {
		return col;
	}

	/**
	 * Clears the current array.
	 */
	public void clear() {

		if (points.size() > 0) {
			Ntot = 0;
			points.removeAllElements();
			for (int k = 0; k < GraphSettings.N_AXES; k++) {
				maxValue[k] = -GraphSettings.INF;
				minValue[k] = GraphSettings.INF;
				lowestNonZeroValue[k] = GraphSettings.INF;
			}
		}
	}

	/**
	 * Sorts the arrays in ascending or descending order.
	 * 
	 * @param ascending
	 *            true if we should sort from small to large
	 */
	public void sort(boolean ascending) {
		if (ascending) {
			// sort-routing in ascending order
		} else {
			// sort-routing in descending order
		}
	}

	/**
	 * Write a DataArray to an external file. If errors on data points are not
	 * given, they are set to 0
	 * 
	 * @param name
	 *            File name with output
	 * @param title
	 *            Title
	 */
	public void toFile(String name, String title) {

		DecimalFormat dfb = new DecimalFormat("##.########E00");
		Date dat = new Date();
		String today = String.valueOf(dat);

		int dim = getDimension();
		this.title = title;

		try {
			FileOutputStream f1 = new FileOutputStream(new File(name));
			PrintStream tx = new PrintStream(f1);
			tx.println("# " + Integer.toString(dim)); // dimension first
			tx.println("# jhplot: output data from P1D:" + title);
			tx.println("# jhplot: created at " + today);
			tx.println("# x,y,x(left),x(right),y(upper),y(lower),x(leftSys),x(rightSys),y(upperSys),y(lowerSys)");
			tx.println("#");
			for (int i = 0; i < size(); i++) {

				if (dim == 2) {
					String x = dfb.format(getX(i));
					String y = dfb.format(getY(i));
					tx.println(x + "  " + y);
					continue;
				}

				if (dim == 3) {
					String x = dfb.format(getX(i));
					String y = dfb.format(getY(i));
					String y1 = dfb.format(getYupper(i));
					tx.println(x + " " + y + " " + y1);
					continue;
				}

				if (dim == 4) {
					String x = dfb.format(getX(i));
					String y = dfb.format(getY(i));

					String y1 = dfb.format(getYupper(i));
					String y2 = dfb.format(getYlower(i));

					tx.println(x + " " + y + " " + y1 + " " + y2);
					continue;
				}

				if (dim == 6) {
					String x = dfb.format(getX(i));
					String y = dfb.format(getY(i));
					String x1 = dfb.format(getXleft(i));
					String x2 = dfb.format(getXright(i));
					String y1 = dfb.format(getYupper(i));
					String y2 = dfb.format(getYlower(i));

					tx.println(x + " " + y + " " + x1 + " " + x2 + " " + y1
							+ " " + y2);
					continue;
				}

				String x = dfb.format(getX(i));
				String y = dfb.format(getY(i));
				String x1 = dfb.format(getXleft(i));
				String x2 = dfb.format(getXright(i));

				String x3 = dfb.format(getXleftSys(i));
				String x4 = dfb.format(getXrightSys(i));

				String y1 = dfb.format(getYupper(i));
				String y2 = dfb.format(getYlower(i));

				String y3 = dfb.format(getYupperSys(i));
				String y4 = dfb.format(getYlowerSys(i));

				tx.println(x + " " + y + " " + x1 + " " + x2 + " " + y1 + " "
						+ y2 + " " + x3 + " " + x4 + " " + y3 + " " + y4);
			}
			f1.close();

		} catch (IOException e) {
			System.out.println("Error in the output file");
			e.printStackTrace();
		}

	}

	/**
	 * Get the title if any
	 * 
	 * @return
	 */

	public String getTitle() {
		return title;
	}

	/**
	 * Get data from a file
	 * 
	 * @param file
	 * @return
	 */

	public boolean parse(File file) {

		boolean res = false;

		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			res = parse(in);
			if (res == false)
				return false;

		} catch (IOException e) {
			Utils.oops(null, "Can't parse data file " + file.toString());
			return false;
		}

		return true;

	}

	/**
	 * Read DataArray from BufferedReader
	 * 
	 * @param in
	 *            BufferedReader
	 * @return true if no error
	 */
	public boolean parse(BufferedReader in) {

		String s;
		clear();

		try {

			// String info=in.readLine();
			// info = info.trim();

			// info=info.substring(1, info.length() );
			// dimension=Integer.parseInt(info);

			while ((s = in.readLine()) != null) {
				s = s.trim();
				if (s.length() < 1)
					continue;
				if (s.startsWith("#"))
					continue;
				if (s.startsWith("*"))
					continue;

				StringTokenizer st = new StringTokenizer(s);
				int ncount = st.countTokens(); // number of words
				dimension = ncount;
				//
				String[] sword = new String[ncount];
				double[] snum = new double[ncount];

				// split this line
				int mm = 0;
				while (st.hasMoreTokens()) { // make sure there is stuff
					// to get
					String tmp = st.nextToken();

					// read double
					double dd = 0;
					try {
						dd = Double.parseDouble(tmp.trim());
					} catch (NumberFormatException e) {
						System.out.println("Error in reading the line "
								+ Integer.toString(mm + 1));
					}
					snum[mm] = dd;
					mm++;

				} // end loop over each line

				if (ncount == 2) {
					addPoint(snum[0], snum[1]);
				} else if (ncount == 3) {
					addPoint(snum[0], snum[1], snum[2], snum[2]);
				} else if (ncount == 4) {
					addPoint(snum[0], snum[1], snum[2], snum[3]);
				} else if (ncount == 6) {
					addPoint(snum[0], snum[1], snum[2], snum[3], snum[4],
							snum[5]);
				} else {
					addPoint(snum[0], snum[1], snum[2], snum[3], snum[4],
							snum[5], snum[6], snum[7], snum[8], snum[9]);
				}

			}
		} catch (IOException e) {
			Utils.oops(null, "Can't parse data from BufferedReader");
			return false;
		}

		return true;
	}

	/**
	 * Convert to string
	 * 
	 */
	public void print() {
		System.out.println(toString());
	}

	/**
	 * Print a data container on the screen
	 * 
	 */
	public String toString() {

		String tmp = "#\n";
		DecimalFormat dfb = new DecimalFormat("##.#####E00");
		Date dat = new Date();
		String today = String.valueOf(dat);
		String br = "\n";

		tmp = tmp + "# jhplot: output data: " + this.title + br;
		tmp = tmp + "# jhplot: created at " + today + br;
		;
		tmp = tmp
				+ "# x,y,x(left),x(right),y(upper),y(lower),x(leftSys),x(rightSys),y(upperSys),y(lowerSys)"
				+ br;
		tmp = tmp + "#" + br;
		for (int i = 0; i < size(); i++) {
			String x = dfb.format(getX(i));
			String y = dfb.format(getY(i));

			String x1 = dfb.format(getXleft(i));
			String x2 = dfb.format(getXright(i));

			String x3 = dfb.format(getXleftSys(i));
			String x4 = dfb.format(getXrightSys(i));

			String y1 = dfb.format(getYupper(i));
			String y2 = dfb.format(getYlower(i));

			String y3 = dfb.format(getYupperSys(i));
			String y4 = dfb.format(getYlowerSys(i));
			tmp = tmp + x + "  " + y + "  " + x1 + "  " + x2 + "  " + y1 + "  "
					+ y2 + "  " + x3 + "  " + x4 + "  " + y3 + "  " + y4 + br;
		}

		return tmp;
	}

}

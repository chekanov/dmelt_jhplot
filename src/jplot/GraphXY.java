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
 **/

package jplot;

import graph.RTextLine;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;

import java.text.DecimalFormat;
import java.text.Format;

import org.freehep.graphics2d.VectorGraphics;

/**
 * The <code>GraphXY</code> class builds a panel which displays a 2Dgraph
 * according to the data stored in the {@link DataArray}. All general graph
 * stuff and initialization is done in class <code>Graph</code>.
 * 
 * @version 24/08/99
 * @author J. van der Lee & S.Chekanov (Oct. 2006)
 */
public class GraphXY extends GraphGeneral {

	private static final long serialVersionUID = 1L;

	private double histoWidth;
	private int xpolUP[], ypolUP[], xpolDW[], ypolDW[];
	private int xpolUPsys[], ypolUPsys[], xpolDWsys[], ypolDWsys[];
	private int NtotPoly = 0;
	private Contour contour;

	/**
	 * Main constructor. Sets the settings to their default values. If the graph
	 * is used without the jplot GUI (hence without a JPlot instance), set the
	 * first parameter to null or use the other constructor instead.
	 * 
	 * @param jp
	 *            jplot instance (parent).
	 * @param gs
	 *            objects containing the global graph settings.
	 */
	public GraphXY(JPlot jp, GraphSettings gs) {
		super(jp, gs);
		histoWidth = 1000000.0;
	}

	/**
	 * Constructor, sets the settings to their default values.
	 * 
	 * @param gs
	 *            objects containing the global graph settings.
	 */
	public GraphXY(GraphSettings gs) {
		this(null, gs);
	}

	/*
	 * Find the boundary values for an X- or Y range in the case of logarithmic
	 * scaling. @param axis defines the axis we are working on @param v value
	 * which will be converted to log-scale @return new boundary value, now for
	 * the log scale.
	 */
	private double getLogBoundary(int axis, double v) {
		double k = 0.0;
		double x = 10.0;
		if (v > 1.0) {
			while (v >= 1.0 && k < 299) {
				v /= 10.0;
				k += 1.0;
			}
		} else {
			x = 0.1;
			while (v <= 0.1 && k < 299) {
				v *= 10.0;
				k += 1.0;
			}
		}
		return Math.pow(x, k);
	}

	/**
	 * Determines the minimum and maximum values of the X-range. This function
	 * sets the Xmin, Xmax values to the minimum X, maximum X and more. The
	 * method uses the data or the values specified by the user.
	 * 
	 * @param axis
	 *            symbolic constant, saying which axis we are.
	 * @param data
	 *            vector of plotting points (curves)
	 */
	protected boolean setMinMax(int axis, Vector data) {
		double min = gs.getMinValue(axis); // chekanov
		double max = gs.getMaxValue(axis);
		// System.out.println("debug: starting with min, max = " + min + "," +
		// max);

		// for automatic scaling, determine the max-
		// and min values of the different all the data arrays:
		// -----------------------------------------------------

		if (gs.autoRange(axis)) {
			Enumeration e = data.elements();
			DataArray da = (DataArray) e.nextElement();
			double minVal;
			if (gs.useLogScale(axis))
				minVal = da.getLowestNonZeroValue(axis);
			else
				minVal = da.getMinValue(axis);
			// System.out.println("minVal = " + minVal);
			min = minVal;
			max = da.getMaxValue(axis);
			while (e.hasMoreElements()) {
				da = (DataArray) e.nextElement();
				if (gs.useLogScale(axis))
					minVal = da.getLowestNonZeroValue(axis);
				else
					minVal = da.getMinValue(axis);
				if (minVal < min)
					min = minVal;
				if (da.getMaxValue(axis) > max)
					max = da.getMaxValue(axis);
			}

			// chekanov
			// make nice separation
			double del = max - min;
			del = 0.05 * del;
			max = max + del;
			min = min - del;

			if (min == max) {
				min -= 0.1;
				max += 0.1;
			}

			// if histogram, then probably you want to start at Ymin=0
			if (da.getGraphStyle() == 1 && axis == 1) {
				min = 0;
				if (gs.useLogScale(axis))
					min = da.getLowestNonZeroValue(axis);
			}

			// when points are showing, da.getGraphStyle()=0
			// but in this case one can assume also histogram type of showing!
			if (gs.get2DType() == 1 && axis == 1) {
				min = 0;
				if (gs.useLogScale(axis))
					min = da.getLowestNonZeroValue(axis);
			}

			// fix x-scale
			if (axis == 0) {
				if (gs.useLogScale(axis))
					min = da.getLowestNonZeroValue(axis);
			}

			// some special treatment for logarithmic axes:
			// ---------------------------------------------
			if (gs.useLogScale(axis)) {
				min = getLogBoundary(axis, min) / 10.0;
				max = getLogBoundary(axis, max);
			}

			if (JPlot.debug) {
				System.out.print("min and max values for ");
				if (axis == X)
					System.out.print("X-axis data: ");
				else
					System.out.print("Y-axis data: ");
				System.out.println("min = " + min + ", max = " + max);
			}
		}

		// get tick separation
		if (gs.useLogScale(axis)) {
			max = log10(max);
			min = log10(min);
		}

		double tic = Axis.calculateTicSep(min, max, gs.getMaxNumberOfTics());
		if (gs.autoRange(axis)) {
			if (min < max) {
				min = (double) tic * Math.floor(min / tic);
				max = (double) tic * Math.ceil(max / tic);
			} else {
				min = (double) tic * Math.ceil(min / tic);
				max = (double) tic * Math.floor(max / tic);
			}
		}

		// System.out.println( gs.useNumberOfTics(axis) );
		if (gs.useNumberOfTics(axis)) {
			// System.out.println(gs.getNumberOfTics(axis));
			numberOfTics[axis] = gs.getNumberOfTics(axis);
		} else {

			if (!gs.useLogScale(axis)) {
				numberOfTics[axis] = Axis.calculateTicNumber(min, max, tic,
						gs.useLogScale(axis));
				// numberOfTics[axis] = (int) Math.ceil(Math.abs(min - max) /
				// tic
				// - 1e-10) + 1;
				gs.setNumberOfTics(axis, numberOfTics[axis]);
			}
		}
		diff[axis] = (double) Math.abs(min - max);

		if (gs.useLogScale(axis)) {

			min = Math.pow(10, min);
			max = Math.pow(10, max);

			if (numberOfTics[axis] > diff[axis]) {
				// the following (1.0+1e-10) trick is introduced, again
				// to avoid the rounding behaviour ('feature') of java.
				// -----------------------------------------------------
				// numberOfTics[axis] = (int) (diff[axis] * (1.0 + 1e-10)) + 1;
				numberOfTics[axis] = Axis.calculateTicNumber(min, max, tic,
						gs.useLogScale(axis));
				gs.setNumberOfTics(axis, numberOfTics[axis]);
			}
		}

		inv[axis] = (min < max) ? 1.0 : -1.0;

		if (JPlot.debug) {
			System.out.println("number of tics for axis " + axis + " = "
					+ numberOfTics[axis]);
			System.out.print(", min and max values after smoothing: ");
			System.out.println("min = " + min + ", max = " + max);
			System.out.println("diff[X] = " + diff[X] + ", diff[Y] = "
					+ diff[Y]);
		}
		gs.setMinValue(axis, min);
		gs.setMaxValue(axis, max);

		// System.out.println(" for min = " + min + ", max = " + max);
		// chekanov

		MinAxis[axis] = min;
		MaxAxis[axis] = max;
		AxisExponent[axis] = 0;

		// determine exponent for labels
		if (Math.abs(min) > Math.abs(max))
			AxisExponent[axis] = ((int) Math
					.floor(Math.log10(Math.abs(min)) / 3.0)) * 3;
		else
			AxisExponent[axis] = ((int) Math
					.floor(Math.log10(Math.abs(max)) / 3.0)) * 3;

		// if (axis == Y && AxisExponent[axis] != 0 ) {
		// System.out.println(" Exponent for axis = " + axis + " exponent = " +
		// AxisExponent[axis]);
		// }

		// System.out.println(" Exponent for axis = " + axis + " exponent = " +
		// AxisExponent[axis]);

		return true;
	}

	/*
	 * Determine the axis labels, if needed. These are numbers which are
	 * translated in Strings. The length is evalulated and used to set the left
	 * and bottom margins. @param
	 */
	protected void makeTicLabels() {

		// System.out.println("maxTics="+maxTics);
		// System.out.println(" Exponent for axis = " + X + " exponent = " +
		// AxisExponent[X]);

		int origExpX = AxisExponent[X];
		int origExpY = AxisExponent[Y];

		// when to use exponential form?
		if (Math.abs(AxisExponent[X]) <= 4) {
			AxisExponent[X] = 0;
		}

		if (Math.abs(AxisExponent[Y]) <= 4) {
			AxisExponent[Y] = 0;
		}

		// make the X-axis labels:
		// ------------------------
		int maxTics = gs.getMaxNumberOfTics();

		// if the number of ticks is fixed
		if (gs.useNumberOfTics(X) == true)
			maxTics = gs.getNumberOfTics(X);

		if (gs.useLogScale(X))
			maxTics = gs.getMaxNumberOfTics();

		Vector<String> ticsX = new Vector<String>();
		Vector<String> ticsXlabels = gs.getLabelTicks(X);

		ticsX = Axis.computeTicks(MinAxis[X], MaxAxis[X], maxTics,
				gs.useLogScale(X));
		if (gs.getAutomaticTicks(X) == true)
			gs.setLabelTicks(X, ticsX);

		// if (numberOfTics[X] != tics.size()) {
		// System.out.println("Wrong number of X ticks");
		// System.out.println(numberOfTics[X]);
		// System.out.println(tics.size());
		// }

		numberOfTics[X] = ticsX.size();
		ticLabel[X] = new String[numberOfTics[X]];
		ticNumber[X] = new double[numberOfTics[X]];

		for (int i = 0; i < numberOfTics[X]; i++) {
			String ticstr = (String) ticsX.elementAt(i);
			double ticval = Double.parseDouble(ticstr);
			// System.out.println(ticstr);

			// extract
			double NoExpon = ticval;
			if (!gs.useLogScale(X)) {
				NoExpon = ticval / Math.pow(10.0d, AxisExponent[X]);
				ticstr = Utils.FormNum(NoExpon, MinAxis[X], MaxAxis[X]);

			} else {
				ticstr = Utils.FormLog(ticval);
			}
			// System.out.println(ticstr);

			ticNumber[X][i] = ticval;

			if (gs.getAutomaticTicks(X) == false)
				ticLabel[X][i] = (String) ticsXlabels.elementAt(i);
			else
				ticLabel[X][i] = ticstr;
		}

		// remove ".0" at the ends
		if (gs.getAutomaticTicks(X) == true)
			ticLabel[X] = Utils.skeepZero(ticLabel[X]);

		// make the Y axes labels:
		// ------------------------
		maxTics = gs.getMaxNumberOfTics();

		// if the number of ticks is fixed
		if (gs.useNumberOfTics(Y) == true)
			maxTics = gs.getNumberOfTics(Y);

		if (gs.useLogScale(Y))
			maxTics = gs.getMaxNumberOfTics();

		Vector<String> ticsY = new Vector<String>();
		Vector<String> ticsYlabels = gs.getLabelTicks(Y);

		ticsY = Axis.computeTicks(MinAxis[Y], MaxAxis[Y], maxTics,
				gs.useLogScale(Y));
		if (gs.getAutomaticTicks(Y) == true)
			gs.setLabelTicks(Y, ticsY);

		// if (numberOfTics[Y] != tics.size()) {
		// System.out.println("Wrong number of Y ticks");
		// System.out.println(numberOfTics[Y]);
		// System.out.println(tics.size());
		// }

		numberOfTics[Y] = ticsY.size();
		ticLabel[Y] = new String[numberOfTics[Y]];
		ticNumber[Y] = new double[numberOfTics[Y]];

		for (int i = 0; i < numberOfTics[Y]; i++) {
			String ticstr = (String) ticsY.elementAt(i);
			double ticval = Double.parseDouble(ticstr);

			// extract
			double NoExpon = ticval;
			if (!gs.useLogScale(Y)) {
				NoExpon = ticval / Math.pow(10.0d, AxisExponent[Y]);
				ticstr = Utils.FormNum(NoExpon, MinAxis[Y], MaxAxis[Y]);
			} else {

				ticstr = Utils.FormLog(ticval);
			}

			ticNumber[Y][i] = ticval;
			if (gs.getAutomaticTicks(Y) == false)
				ticLabel[Y][i] = (String) ticsYlabels.elementAt(i);
			else
				ticLabel[Y][i] = ticstr;

		}

		// remove ".0" at the ends
		if (gs.getAutomaticTicks(Y) == true)
			ticLabel[Y] = Utils.skeepZero(ticLabel[Y]);

		// contur type: initialisation
		// build a grid only once
		for (Enumeration e = data.elements(); e.hasMoreElements();) {

			DataArray da = (DataArray) e.nextElement();
			if (da.size() == 0)
				continue;

			// System.out.println( SetEnv.getPlotType() );
			if (da.getGraphStyle() == LinePars.CONTOUR) {
				contour = new Contour(gs.getContour_bar(),
						gs.getContour_binsX(), gs.getContour_binsY(),
						gs.getContour_gray(), gs.getContour_levels());

				contour.createGrid(da.getData(), MinAxis[X], MaxAxis[X],
						MinAxis[Y], MaxAxis[Y]);

				// break;

			}

		}

	}

	/*
	 * This function draws the grid. @param g2 instance of the graphical canvas
	 */
	private void drawGrid(VectorGraphics g2) {
		double x = leftMargin + axisLength[X];
		double y = topMargin + axisLength[Y];
		g2.setStroke(new BasicStroke());
		g2.setColor(gs.getGridColor());

		// draw the X-grid lines:
		// -----------------------
		if (gs.drawGrid(X)) {

			for (int i = 0; i < numberOfTics[X]; i++) {
				double d = toX(ticNumber[X][i]);

				if (d >= leftMargin && d <= x)
					g2.drawLine(d, topMargin, d, y);

			}

		}

		// draw the Y-grid lines:
		// -----------------------
		if (gs.drawGrid(Y)) {

			for (int i = 0; i < numberOfTics[Y]; i++) {
				double d = toY(ticNumber[Y][i]);
				if (d >= topMargin && d <= y)
					g2.drawLine(leftMargin, d, x, d);
			}

		}
	}

	/*
	 * Repaint margins to remove overflows
	 */
	private void drawMargins(VectorGraphics g2) {

		double x = leftMargin + axisLength[X];
		double y = topMargin + axisLength[Y];

		g2.setColor(gs.getBackgroundColor());
		// from left
		g2.fillRect(0, 0, leftMargin, height);
		// from top
		g2.fillRect(0, 0, width, topMargin);
		// right
		g2.fillRect(x, 0, rightMargin, height);
		// bottom
		g2.fillRect(0, y, width, bottomMargin);

	}

	/*
	 * This function draws the axes (actually X,Y and mirror axes) and the tics
	 * on the axes. @param g2 instance of a graphical canvas on which to draw
	 * the axes
	 */
	private void plotAxes(VectorGraphics g2) {

		// System.out.println("Debug=Call to plotAxis()");

		g2.setColor(gs.getAxesColor());

		// System.out.println(leftMargin);

		// draw all axis
		if (gs.drawAxis(X) == true && gs.drawAxis(Y) == true) {
			g2.drawRect(leftMargin, topMargin, axisLength[X], axisLength[Y]);
		}

		double a = topMargin + axisLength[Y];
		double x = leftMargin + axisLength[X];
		double y = topMargin + axisLength[Y];

		// draw horisontal
		if (gs.drawAxis(X) == true && gs.drawAxis(Y) == false) {
			if (gs.drawMirrorAxis(X) == true) {
				g2.drawLine(leftMargin, topMargin, x, topMargin);

			}

			g2.drawLine(leftMargin, y, x, y);
			if (gs.getAxesArrow() == 1) {
				int le1 = 6 * (int) (gs.getPenWidthAxis() * scalingFrame);
				int le2 = 2 * (int) (gs.getPenWidthAxis() * scalingFrame);
				int[] xx = { (int) leftMargin, (int) x };
				int[] yy = { (int) y, (int) y };
				drawPolylineArrow(g2, xx, yy, le1, le2);
			}

			if (gs.getAxesArrow() == 2)
				drawArrow2(g2, (int) leftMargin, (int) y, (int) x, (int) y);
		}

		// draw vertical
		if (gs.drawAxis(X) == false && gs.drawAxis(Y) == true) {
			g2.drawLine(leftMargin, topMargin, leftMargin, a);
			if (gs.getAxesArrow() == 1) {
				int le1 = 6 * (int) (gs.getPenWidthAxis() * scalingFrame);
				int le2 = 2 * (int) (gs.getPenWidthAxis() * scalingFrame);
				int[] xx = { (int) leftMargin, (int) leftMargin };
				int[] yy = { (int) a, (int) topMargin };
				drawPolylineArrow(g2, xx, yy, le1, le2);
			}
			if (gs.getAxesArrow() == 2)
				drawArrow2(g2, (int) leftMargin, (int) a, (int) leftMargin,
						(int) topMargin);

			// mirror
			if (gs.drawMirrorAxis(Y) == true) {
				g2.drawLine(x, topMargin, x, a);
			}
		}

		FontMetrics fmX = getFontMetrics(gs.getTicFont(X));
		double xLabelHeight = fmX.getHeight() * scalingFrame;

		FontMetrics fmY = getFontMetrics(gs.getTicFont(Y));
		double yLabelHeight = fmY.getHeight() * scalingFrame;

		final int NticLog = gs.getMaxNumberOfTics() - 1;

		// define rotation for tics
		int xRot = 1;
		int yRot = 1;
		if (gs.rotateTics(X))
			xRot = -1;
		if (gs.rotateTics(Y))
			yRot = -1;

		// draw exponents
		if (gs.drawTicLabels(X) && gs.useLogScale(X) == false) {
			if (AxisExponent[X] != 0) {
				String stext = "x10^" + Integer.toString(AxisExponent[X]);
				double ex1 = x - fmX.stringWidth(stext) / 4;
				double ey1 = (double) (a + 2 * xLabelHeight);
				RTextLine exponent = new RTextLine();
				// System.out.println(" Exponent for axis = " + X + " exponent =
				// " + AxisExponent[X]);
				exponent.setFont(scaleFont(gs.getTicFont(X)));
				exponent.setColor(gs.getTicColor(X));
				exponent.setText(stext);
				exponent.draw(g2, (int) ex1, (int) ey1);
				// exponent.draw(g2, (int)x, (int) y);

			}
		}

		// width of tics
		g2.setStroke(new BasicStroke(gs.getAxesPenTicWidth() * scalingFrame));

		double x_end = x;
		double y_end = topMargin;
		// do not show the last tic if arrow is shown
		if (gs.getAxesArrow() != 0) {
			x_end = x - 1;
			y_end = topMargin + 1;
		}
		;

		// draw X tics
		if (gs.drawTics(X)) {

			for (int i = 0; i < numberOfTics[X]; i++) {

				// System.out.println(ticstr);
				double d = toX(ticNumber[X][i]);

				// make subtic marks
				if (subticNumber[X] > 0 && i < numberOfTics[X] - 1) {
					double sepa = ticNumber[X][i + 1] - ticNumber[X][i];
					int Ntics = subticNumber[X];
					double tdic = ticNumber[X][i];
					double tt = sepa / Ntics;
					if (gs.useLogScale(X)) {
						Ntics = NticLog;
						tt = sepa / Ntics;
					}
					for (int j = 0; j < Ntics; j++) {
						tdic = tdic + tt;
						double t1 = toX(tdic);
						if (t1 >= leftMargin && t1 <= x_end)
							g2.drawLine(t1, y - xRot * subticLength[X], t1, y);

                                         // special treament of subtic in log scale.       
                                         // if X between 1-99, label also subtics in log scale, i.e. 2,3,4,5,6,7,8,9
                                         if (gs.autoRange(X) == false && gs.useLogScale(X) && t1 >= leftMargin && t1 <= x_end && j+2<10 && gs.getMinValue(X)>0.2 && gs.getMaxValue(X)<100) {
                                        double ifix = 0;
                                        double ifih = 0;
                                        ifix = ifix + 0.5 * fmX.stringWidth(ticLabel[X][i])* scalingFrame; 
                                        ifih = ifih + 0.2 * xLabelHeight;
                                        double x1 = (t1 - ifix);
                                        double y1 = (a + xLabelHeight + ifih);
                                          RTextLine text = new RTextLine();
                                          text.setFont(scaleFont(gs.getTicFont(X)));
                                          text.setColor(gs.getTicColor(X));
                                          if (i==1) text.setText(Integer.toString(j+2));
                                          if (i==2) text.setText(Integer.toString((j+2)*10));
                                          text.draw(g2, (int)x1, (int)(y1));
                                         }


					}

					// for liner scale, add tics to the left of the minimum
					if (!gs.useLogScale(X)) {
						double t1 = toX(ticNumber[X][0] - tt);
						if (t1 >= leftMargin && t1 <= x)
							g2.draw(new Line2D.Double(t1, y - xRot
									* subticLength[X], t1, y));
						// add to the right if you can
						t1 = toX(ticNumber[X][numberOfTics[X] - 1] + tt);
						if (t1 >= leftMargin && t1 <= x_end)
							g2.drawLine(t1, y - xRot * subticLength[X], t1, y);

					}

				}

				// draw main tics
				if (d >= leftMargin && d <= x_end)
					g2.drawLine(d, y - xRot * ticLength[X], d, y);

				if (gs.drawMirrorTics(X)) {

					// make subtic marks
					if (subticNumber[X] > 0 && i < numberOfTics[X] - 1) {
						double sepa = ticNumber[X][i + 1] - ticNumber[X][i];
						int Ntics = subticNumber[X];
						double tdic = ticNumber[X][i];

						double tt = sepa / Ntics;
						if (gs.useLogScale(X)) {
							Ntics = NticLog;
							tt = sepa / Ntics;
						}
						for (int j = 0; j < Ntics; j++) {
							tdic = tdic + tt;
							double t1 = toX(tdic);
							if (t1 >= leftMargin && t1 <= x)
								g2.drawLine(t1, topMargin + xRot
										* subticLength[X], t1, topMargin);

						}

						// add additional tic from the left.
						if (!gs.useLogScale(X)) {
							double t1 = toX(ticNumber[X][0] - tt);
							if (t1 >= leftMargin && t1 <= x)
								g2.drawLine(t1, topMargin + xRot
										* subticLength[X], t1, topMargin);
							// add to the right if you can
							t1 = toX(ticNumber[X][numberOfTics[X] - 1] + tt);
							if (t1 >= leftMargin && t1 <= x)
								g2.drawLine(t1, topMargin + xRot
										* subticLength[X], t1, topMargin);



						}

					} // end subtics

					// draw main tics
					if (d >= leftMargin && d <= x)
						g2.drawLine(d, topMargin + xRot * ticLength[X], d,
								topMargin);

				}

				if (gs.drawTicLabels(X)) {
					if (ticLabel[X][i] == null)
						continue;
					// ifix corrects the X positions
					double ifix = 0;
					double ifih = 0;

					if (ticLabel[X][i].indexOf("^") > -1) {
						ifix = -0.35 * fmX.stringWidth(ticLabel[X][i])
								* scalingFrame;
						ifih = 0.2 * xLabelHeight;
					}
					if (gs.useLogScale(X)) {
						if (ticLabel[X][i].equals("1")
								|| ticLabel[X][i].equals("10"))
							ifih = ifih + 0.2 * xLabelHeight;
					}

					ifix = ifix + 0.5 * fmX.stringWidth(ticLabel[X][i])
							* scalingFrame;
					double x1 = (d - ifix);
					double y1 = (a + xLabelHeight + ifih);

					if (d >= leftMargin && d <= x) {
						RTextLine text = new RTextLine();
						text.setFont(scaleFont(gs.getTicFont(X)));
						text.setColor(gs.getTicColor(X));
						text.setText(ticLabel[X][i]);
						text.draw(g2, (int) x1, (int) y1);
					}



				}

			} // end X

		}

		// ----------------------------------------------------
		// draw Y tics
		// ----------------------------------------------------

		// draw exponents
		if (gs.drawTicLabels(Y) && gs.useLogScale(Y) == false) {
			if (AxisExponent[Y] != 0) {
				String stext = "x10^" + Integer.toString(AxisExponent[Y]);
				// double ex1 = leftMargin - fmY.stringWidth(stext)
				// - fmY.stringWidth("^") - (double) xSep;
				double ex1 = leftMargin;
				if (ex1 < 0)
					ex1 = 0;
				double ey1 = topMargin - 0.25 * (double) yLabelHeight;
				RTextLine exponent = new RTextLine();
				exponent.setFont(scaleFont(gs.getTicFont(Y)));
				exponent.setColor(gs.getTicColor(Y));
				exponent.setText(stext);

				exponent.draw(g2, (int) ex1, (int) ey1);
			}
		}

		// draw tics
		if (gs.drawTics(Y)) {

			for (int i = 0; i < numberOfTics[Y]; i++) {
				double d = toY(ticNumber[Y][i]);

				// subtics
				if (subticNumber[Y] > 0 && i < numberOfTics[Y] - 1) {
					double sepa = ticNumber[Y][i + 1] - ticNumber[Y][i];
					int Ntics = subticNumber[Y];
					double tdic = ticNumber[Y][i];
					double tt = sepa / Ntics;
					if (gs.useLogScale(Y)) {
						Ntics = NticLog;
						tt = sepa / Ntics;
					}
					for (int j = 0; j < Ntics; j++) {
						tdic = tdic + tt;
						double t1 = toY(tdic);
						if (t1 >= y_end && t1 <= y)
							g2.drawLine(leftMargin, t1, leftMargin + yRot
									* subticLength[Y], t1);



                                         // special treament of subtic in log scale.       
                                         // if Y between 1-100, label also subtics in log scale, i.e. 2,3,4,5,6,7,8,9
                                         if (gs.autoRange(Y) == false && gs.useLogScale(Y) && t1 >= y_end && t1 <= y &&  j+2<10 && gs.getMinValue(Y)>0.2 && gs.getMaxValue(Y)<100) {
                                         double  ifih = 0;
                                         if (ticLabel[Y][i].equalsIgnoreCase("1")) {
                                                        ifih = -0.75 * fmX.stringWidth(ticLabel[Y][i])
                                                                        * scalingFrame;
                                                } else if (ticLabel[Y][i].equalsIgnoreCase("10")) {
                                                        ifih = -0.5 * fmX.stringWidth(ticLabel[Y][i])
                                                                        * scalingFrame;
                                                }


                                        double x2 = leftMargin - fmY.stringWidth(ticLabel[Y][i])
                                                        * scalingFrame - (double) xSep + ifih;
                                        double y2 = t1 + 0.25 * (double) yLabelHeight * scalingFrame;
                                         RTextLine text = new RTextLine();
                                         text.setFont(scaleFont(gs.getTicFont(Y)));
                                         text.setColor(gs.getTicColor(Y));
                                         if (i==1) text.setText(Integer.toString(j+2));
                                         if (i==2) text.setText(Integer.toString((j+2)*10));
                                         text.draw(g2, (int)x2, (int)(y2));
                                         }




					}

					// add additional tic from the top, if you can
					if (!gs.useLogScale(Y) && numberOfTics[Y] > 0) {
						double t1 = toY(ticNumber[Y][numberOfTics[Y] - 1] + tt);
						if (t1 >= y_end && t1 <= y)
							g2.draw(new Line2D.Double(leftMargin, t1,
									leftMargin + yRot * subticLength[Y], t1));

						// add from the buttom if you can
						t1 = toY(ticNumber[Y][0] - tt);
						if (t1 >= y_end && t1 <= y)
							g2.drawLine(leftMargin, t1, leftMargin + yRot
									* subticLength[Y], t1);

					}

				}

				// draw main tics
				if (d >= y_end && d <= y)
					g2.drawLine(leftMargin, d,
							leftMargin + yRot * ticLength[Y], d);

				if (gs.drawMirrorTics(Y)) {

					// subtics
					if (subticNumber[Y] > 0 && i < numberOfTics[Y] - 1) {
						double sepa = ticNumber[Y][i + 1] - ticNumber[Y][i];
						int Ntics = subticNumber[Y];
						double tdic = ticNumber[Y][i];
						double tt = sepa / Ntics;
						if (gs.useLogScale(Y)) {
							Ntics = NticLog;
							tt = sepa / Ntics;
						}
						for (int j = 0; j < Ntics; j++) {
							tdic = tdic + tt;
							double t1 = toY(tdic);
							if (t1 >= topMargin && t1 <= y)
								g2.drawLine(x - yRot * subticLength[Y], t1, x,
										t1);
						}

						// add additional tic from the botton
						// add from the top if you can
						if (!gs.useLogScale(Y) && numberOfTics[Y] > 0) {
							double t1 = toY(ticNumber[Y][numberOfTics[Y] - 1]
									+ tt);
							if (t1 >= topMargin && t1 <= y)
								g2.drawLine(x - yRot * subticLength[Y], t1, x,
										t1);

							// add from the bottom if you can
							t1 = toY(ticNumber[Y][0] - tt);
							if (t1 >= topMargin && t1 <= y)
								g2.drawLine(x - yRot * subticLength[Y], t1, x,
										t1);

						}

					}

					// draw main tics
					if (d >= y_end && d <= y)
						g2.drawLine(x - yRot * ticLength[Y], d, x, d);

				}

				if (gs.drawTicLabels(Y)) {
					if (ticLabel[Y][i] == null)
						continue;

					// ifix corrects the X positions. XXXX
					double ifih = 0;
					if (gs.useLogScale(Y) == true) {
						if (ticLabel[Y][i].indexOf("^") > -1) {
							ifih = 0.5 * fmX.stringWidth(ticLabel[Y][i])
									* scalingFrame;
						} else if (ticLabel[Y][i].equalsIgnoreCase("1")) {
							ifih = -0.75 * fmX.stringWidth(ticLabel[Y][i])
									* scalingFrame;
						} else if (ticLabel[Y][i].equalsIgnoreCase("10")) {
							ifih = -0.5 * fmX.stringWidth(ticLabel[Y][i])
									* scalingFrame;
						}
					} else {
						ifih = 0;
					}

					// if (ticLabel[Y][i].indexOf("\u00b7") > -1)
					// ifih = ifih - 0.2 *
					// fmY.stringWidth(ticLabel[Y][i])*scalingFrame;

					double x2 = leftMargin - fmY.stringWidth(ticLabel[Y][i])
							* scalingFrame - (double) xSep + ifih;
					double y2 = d + 0.25 * (double) yLabelHeight * scalingFrame;

					if (d >= topMargin && d <= y) {
						RTextLine text = new RTextLine();
						text.setFont(scaleFont(gs.getTicFont(Y)));
						text.setColor(gs.getTicColor(Y));
						text.setText(ticLabel[Y][i]);
						text.draw(g2, (int) x2, (int) y2);
					}

				}

			}
		} // end Y

	}

	/**
	 * Returns the X-value scaled to the pixel-availability. This function takes
	 * the X-value and returns the corresponding coordinates for the panel.
	 * 
	 * @param x
	 *            real x-value (as introduced by the data)
	 * @return x-value scaled to the actual panel coordinates
	 */
	public double toX(double x) {
		double d;
		if (gs.useLogScale(X))
			d = log10(x / gs.getMinValue(X));
		else
			d = x - gs.getMinValue(X);
		return leftMargin + inv[X] * d * axisLength[X] / diff[X];
	}

	/**
	 * Returns the Y-value scaled to the pixel-availability. This function takes
	 * the Y-value and returns the corresponding coordinates for the panel.
	 * 
	 * @param y
	 *            real y-value (as introduced by the data)
	 * @return y-value scaled to the actual panel coordinates
	 */
	public double toY(double y) {
		double d;
		if (gs.useLogScale(Y))
			d = log10(y / gs.getMinValue(Y));
		else
			d = y - gs.getMinValue(Y);
		return topMargin + axisLength[Y] * (1.0 - inv[Y] * d / diff[Y]);
	}

	/**
	 * Move to User coordinatess
	 * 
	 * @param Xpic
	 * @return
	 */

	public double toUserX(int Xpic) {

		double mPosX;
		double scale = axisLength[X] / diff[X];

		// definition of d
		// d = log10(x / gs.getMinValue(X)); // for log scale
		// d = x - gs.getMinValue(X);
		double d = (Xpic - leftMargin) / (inv[X] * scale);

		if (gs.useLogScale(X)) {

			mPosX = Math.pow(10, d) * gs.getMinValue(X);

		} else {

			mPosX = d + gs.getMinValue(X);

		}

		return mPosX;

	}

	/**
	 * Move to User coordinatess
	 * 
	 * @param Ypic
	 * @return
	 */

	public double toUserY(int Ypic) {

		double scale = axisLength[Y] / diff[Y];
		double d = (-1 * Ypic + topMargin + axisLength[Y]) / (scale * inv[Y]);

		// d = log10(y / gs.getMinValue(Y)); // for log scale
		// d = y - gs.getMinValue(Y);

		double mPosY;
		if (gs.useLogScale(Y)) {
			mPosY = Math.pow(10.0, d) * gs.getMinValue(Y);
		} else {
			mPosY = d + gs.getMinValue(Y);

		}

		return mPosY;

	}

	/**
	 * Fills the graph area with a background color. The area is the area
	 * between the axes.
	 * 
	 * @param g2
	 *            graphics canvas
	 */
	protected void fillGraphArea(VectorGraphics g2) {
		g2.setColor(gs.getGraphBackgroundColor());
		g2.fillRect(leftMargin, topMargin, axisLength[X], axisLength[Y]);
	}

	/*
	 * This function rebuilds a polygon of plot points, something like
	 * data-array does. It ignores plotpoints falling beyond the current domain
	 * (i.e. outside the axes system).
	 */
	public Vector getPoints(DataArray da) {

		Vector data = da.getData();

		double x, y, oldX = 0.0;
		double left, right, upper, lower, left_sys, right_sys, upper_sys, lower_sys;
		Vector<PlotPoint> p = new Vector<PlotPoint>();
		int i = 0;

		double aXmin = leftMargin;
		double aXmax = leftMargin + axisLength[X];
		double aYmin = topMargin + axisLength[Y];
		double aYmax = topMargin;

		for (Enumeration e = data.elements(); e.hasMoreElements(); i++) {
			PlotPoint pp = (PlotPoint) e.nextElement();

			// pp.print();

			x = toX(pp.getX());
			y = toY(pp.getY());
			left = toX(pp.getX() - pp.getXleft());
			right = toX(pp.getX() + pp.getXright());
			upper = toY(pp.getY() + pp.getYupper());
			lower = toY(pp.getY() - pp.getYlower());
			// naow, systematical bars
			left_sys = toX(pp.getX() - pp.getXleft() - pp.getXleftSys());
			right_sys = toX(pp.getX() + pp.getXright() + pp.getXrightSys());
			upper_sys = toY(pp.getY() + pp.getYupper() + pp.getYupperSys());
			lower_sys = toY(pp.getY() - pp.getYlower() - pp.getYlowerSys());

			// skip some points ouside in case of symbols
			if (da.getGraphStyle() != LinePars.HISTO) {
				// for relaese 2.2
				/*
				 * if (right_sys<aXmin-1 ) continue; if (left_sys> aXmax+1)
				 * continue; if (lower_sys< aYmax-1) continue; if (upper_sys>
				 * aYmin+1) continue;
				 */
			}

			// set to borders if not all are in range
			if (da.getGraphStyle() == LinePars.HISTO) {

				if (right_sys < aXmin - 1)
					continue;
				if (left_sys > aXmax + 1)
					continue;

			}

			// if (inRange(x, y))
			p.add(new PlotPoint(x, y, left, right, upper, lower, left_sys,
					right_sys, upper_sys, lower_sys));

		}

		return p;
	}

	// /**
	// * Does the graph update for a stick- or bar-graph:
	// */
	// private void defaultGraph(Graphics2D g2, DataArray da, Vector points) {
	// int i = 1;
	// PlotPoint prev;
	// double zero = toY(0.0);
	// double a = topMargin + axisLength[Y];

	// for (Enumeration e=points.elements(); e.hasMoreElements(); i++) {
	// PlotPoint pp = (PlotPoint)e.nextElement();

	// // draw a symbol at this point if asked for:
	// if (da.drawSymbol() && (i%da.getPointFrequency()) == 0) {
	// drawPointType(da.getSymbol(),g2,pp.getX(),pp.getY(),
	// da.getSymbolSize());
	// }

	// if (da.getGraphStyle() == da.STICKS) {
	// line.setLine(pp.getX(),pp.getY(),pp.getX(),zero);
	// g2.draw(line);
	// }

	// else if (da.getGraphStyle() == da.HISTO) {
	// rect.setRect(pp.getX()-histoWidth/2,pp.getY(),histoWidth,a-pp.getY());
	// g2.draw(rect);
	// }

	// // Draw a line if asked for.
	// // If dashes are used, calculate the minimum distance between
	// // two plotting points in order to display the dash correctly:
	// else if (i > 1 && da.drawLine() && !prev.liftPen()) {
	// if (da.getDashLength() > 0) {
	// double minDist = Math.sqrt((pp.getX()-prev.getX())*
	// (pp.getX()-prev.getX()) +
	// (pp.getY()-prev.getY())*
	// (pp.getY()-prev.getY()));
	// if (minDist < da.getDashLength()*2) continue;
	// }
	// line.setLine(prev.getX(),prev.getY(),pp.getX(),pp.getY());
	// g2.draw(line);
	// }
	// prev = pp;
	// }
	// }

	/**
	 * This function builds the graph in a double-buffered image zone. Make sure
	 * that the size of the graph (width x height) is set before calling this
	 * function.
	 */
	protected void updateGraph() {

		// create graphics using the createGraphics method of the
		// super class, which creates it in a BufferedImage:
		// Graphics g= createGraphics();
		// if (g == null) return;

		// VectorGraphics gg2= createGraphics();
		VectorGraphics g2 = currentG; // VectorGraphics.create(g);
		if (g2 == null)
			return;

		// System.out.println("Call to updateGraph()");

		double a = topMargin + axisLength[Y];
		double zeroX = toX(0.0);
		double zeroY = toY(0.0);
		// determin min and max in terms of piksels
		// double cXmin = toX(gs.getMinValue(0));
		// double cXmax = toX(gs.getMaxValue(0));
		// double cYmin = toY(gs.getMinValue(1));
		// double cYmax = toY(gs.getMaxValue(1));

		double cXmin = leftMargin;
		double cXmax = leftMargin + axisLength[X];
		double cYmin = topMargin + axisLength[Y];
		double cYmax = topMargin;

		double dashCounter1 = 0;
		double dashCounter2 = 0;

		// System.out.println(cXmin);
		// System.out.println(cXmax);

		// draw the on the background, if this is what they want:
		if (!gs.gridToFront())
			drawGrid(g2);

		// plot primitives
		if (!gs.primitivesToFront())
			plotPrimitives(g2);

		// run over objects

		for (Enumeration e = data.elements(); e.hasMoreElements();) {

			// get the next data array from the vector:
			DataArray da = (DataArray) e.nextElement();

			// da.print();

			// get a vector with valid points (i.e. all points which fall
			// beyond the current domain are ignored):
			Vector points = getPoints(da);
			if (points.size() == 0)
				continue;

			// contour type. Draw and exit the loop
			if (da.getGraphStyle() == LinePars.CONTOUR) {

				// System.out.println( SetEnv.getPlotType() );
				contour.drawColor(g2, toX(MinAxis[X]), toX(MaxAxis[X]),
						toY(MinAxis[Y]), toY(MaxAxis[Y]));

				contour.drawColorBar(g2, gs.getTicFont(Y), (int) rightMargin);
				continue;
			}

			NtotPoly = points.size();
			// for drawing of a polygone
			if (da.getErrorsFill()) {
				xpolUP = new int[NtotPoly];
				ypolUP = new int[NtotPoly];
				xpolDW = new int[NtotPoly];
				ypolDW = new int[NtotPoly];
			}

			if (da.getErrorsFillSys()) {
				xpolUPsys = new int[NtotPoly];
				ypolUPsys = new int[NtotPoly];
				xpolDWsys = new int[NtotPoly];
				ypolDWsys = new int[NtotPoly];
			}

			// double histoWidth= toX( da.getWidthHisto() );

			BasicStroke lineSolidStroke = new BasicStroke(da.getPenWidth()
					* scalingFrame);
			BasicStroke lineStroke = new BasicStroke(da.getPenWidth()
					* scalingFrame);
			if (da.getDashLength() > 0) {
				float[] dashPattern = new float[2];
				dashPattern[0] = da.getDashLength();
				dashPattern[1] = 0.5f * da.getDashLength();
				float dashPhase = 0.0f;
				// Definition of a corresponding BasicStroke.
				lineStroke = new BasicStroke(da.getPenWidth() * scalingFrame,
						BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 2.0f,
						dashPattern, dashPhase);
			}

			// set color and penwidth:
			g2.setColor(da.getColor());
			if (da.drawLine())
				g2.setStroke(da.getStroke());

			PlotPoint prev = new PlotPoint(0.0, 0.0);
			int i = 1;

			// System.out.println(points.size());

			// SSSS
			for (Enumeration e2 = points.elements(); e2.hasMoreElements(); i++) {

				PlotPoint pp = (PlotPoint) e2.nextElement();

				histoWidth = pp.getXright() - pp.getXleft();

				// now show errors
				int sl = (int) (da.getErrTicSize() * scalingFrame);
				if (histoWidth / 2 < sl)
					sl = 0; // when bins are too fine
				float size = da.getSymbolSize() * scalingFrame;

				// System.out.println(pp.getX());

				// mak sure that small tics on the error bars do not cross the
				// Min and Max
				double XlowTic = pp.getX() - sl;
				if (XlowTic <= cXmin)
					XlowTic = pp.getX();

				double XupTic = pp.getX() + sl;
				if (XupTic >= cXmax)
					XupTic = pp.getX();

				// errors should no go over the border
				double Ylow = pp.getYlower();
				if (Ylow > cYmin)
					Ylow = cYmin - 1;
				if (Ylow < cYmax)
					Ylow = cYmax + 1;

				double Yup = pp.getYupper();
				if (Yup < cYmax)
					Yup = cYmax + 1;

				double YlowS = pp.getYlowerSys();
				if (YlowS > cYmin)
					YlowS = cYmin - 1;

				double YupS = pp.getYupperSys();
				if (YupS < cYmax)
					YupS = cYmax + 1;

				// x axis
				double Xlow = pp.getXleft();
				if (Xlow < cXmin)
					Xlow = cXmin + 1;

				double Xup = pp.getXright();
				if (Xup > cXmax)
					Xup = cXmax - 1;

				double XlowS = pp.getXleftSys();
				if (XlowS < cXmin)
					XlowS = cXmin + 1;

				double XupS = pp.getXrightSys();
				if (XupS > cXmax)
					XupS = cXmax - 1;

				// pp.print();

				// draw a symbol at this point if asked for:
				if (da.drawSymbol() && (i % da.getPointFrequency()) == 0) {

					// System.out.println(
					// "pp.getYlower()="+Double.toString(pp.getYlower())+ "
					// cYmin"+cYmin );

					g2.setStroke(lineSolidStroke);

					if (da.getErrorsY()) {
						g2.setPaint(da.getColorErrorsY());
						g2.setStroke(new BasicStroke(da.getPenWidthErr()
								* scalingFrame));

						// take care symbol size, draw vertical erors
						// low
						g2.drawLine(pp.getX(), Ylow, pp.getX(), pp.getY() + 0.5
								* size);

						// build a polygon
						if (da.getErrorsFill()) {
							xpolDW[i - 1] = (int) pp.getX();
							ypolDW[i - 1] = (int) Ylow;
						}

						// up
						g2.drawLine(pp.getX(), pp.getY() - 0.5 * size,
								pp.getX(), Yup);

						// upper for polygone
						if (da.getErrorsFill()) {
							xpolUP[i - 1] = (int) pp.getX();
							ypolUP[i - 1] = (int) Yup;
						}

						// small tic
						g2.drawLine(XlowTic, Ylow, XupTic, Ylow);

						g2.drawLine(XlowTic, Yup, XupTic, Yup);

					}

					// now draw systematical error
					if (da.getErrorsSysY()) {
						g2.setPaint(da.getColorErrorsSysY());
						g2.setStroke(new BasicStroke(da.getPenWidthErrSys()
								* scalingFrame));

						// lower
						g2.drawLine(pp.getX(), YlowS, pp.getX(), Ylow);
						if (da.getErrorsFillSys()) {
							xpolDWsys[i - 1] = (int) pp.getX();
							ypolDWsys[i - 1] = (int) YlowS;
						}

						// upper
						g2.drawLine(pp.getX(), Yup, pp.getX(), YupS);
						if (da.getErrorsFillSys()) {
							xpolUPsys[i - 1] = (int) pp.getX();
							ypolUPsys[i - 1] = (int) YupS;
						}

					}

					// now draw horisontal errors, take care symbol size
					if (da.getErrorsX()) {
						g2.setStroke(new BasicStroke(da.getPenWidthErr()
								* scalingFrame));
						g2.setPaint(da.getColorErrorsX());

						g2.drawLine(Xlow, pp.getY(), pp.getX() - 0.5 * size,
								pp.getY());

						g2.drawLine(pp.getX() + 0.5 * size, pp.getY(), Xup,
								pp.getY());

						// small tic

						g2.drawLine(Xlow, pp.getY() - sl, Xlow, pp.getY() + sl);

						g2.drawLine(Xup, pp.getY() - sl, Xup, pp.getY() + sl);

					}

					// plot systematical errors
					if (da.getErrorsSysX()) {
						g2.setPaint(da.getColorErrorsSysX());
						g2.setStroke(new BasicStroke(da.getPenWidthErrSys()
								* scalingFrame));
						g2.drawLine(XlowS, pp.getY(), Xlow, pp.getY());

						g2.drawLine(Xup, pp.getY(), XupS, pp.getY());

					}

					// now draw the points
					g2.setStroke(new BasicStroke(da.getPenWidth()
							* scalingFrame));
					g2.setPaint(da.getColor());
					GPoints.drawPointType(da.getSymbol(), g2, pp.getX(),
							pp.getY(), size);
				} // end markers

				g2.setStroke(lineSolidStroke);
				if (da.getGraphStyle() == LinePars.STICKS) {
					g2.drawLine(pp.getX(), pp.getY(), pp.getX(), zeroY);

				}

				else if (da.getGraphStyle() == LinePars.HISTO) {

					// double xlow = pp.getX() - histoWidth / 2.0;
					double xlow = pp.getXleft();
					double xup = pp.getXright();
					double hW = histoWidth;

					// if lower than min x value
					if (xlow < cXmin) {
						double dd = cXmin - xlow;
						xlow = cXmin;
						hW = histoWidth - dd;
					}

					// if larger than max X value
					// double xup = pp.getX() + histoWidth / 2.0;
					if (xup > cXmax) {
						double dd = xup - cXmax;
						xup = cXmax;
						hW = histoWidth - dd;
					}

					// if lower than min x value
					// double xlow_prev = prev.getX() - histoWidth / 2.0;
					double xlow_prev = prev.getXleft();
					if (xlow_prev < cXmin) {
						double dd = cXmin - xlow_prev;
						xlow_prev = cXmin;
						hW = histoWidth - dd;
					}

					// if larger than max X value
					// double xup_prev = prev.getX() + histoWidth / 2.0;
					double xup_prev = prev.getXright();
					if (xup_prev > cXmax) {
						double dd = xup_prev - cXmax;
						xup_prev = cXmax;
						hW = histoWidth - dd;
					}

					g2.setStroke(lineSolidStroke);

					// if dashed line
					if (da.getDashLength() > 0) {
						g2.setStroke(lineStroke);
					}

					if (da.fill()) {
						g2.setPaint(da.getFillColor());

						Composite c = AlphaComposite.getInstance(
								AlphaComposite.SRC_OVER,
								da.getFillColorTransparency());
						g2.setComposite(c);

						g2.fillRect(xlow, pp.getY(), histoWidth,
								cYmin - pp.getY());
						// rect.setRect(xlow, pp.getY(),histoWidth, pp.getY());

						// g2.setPaint(Color.black);

						// g2.setPaint(da.getFillColor());

						Composite c1 = AlphaComposite.getInstance(
								AlphaComposite.SRC_OVER, 1.0f);
						g2.setComposite(c1);
					} // end filled

					// draw a empty histogram with bars
					if (da.isBarShown()) {
						rect.setRect(xlow, pp.getY(), histoWidth,
								cYmin - pp.getY());
						g2.setStroke(lineSolidStroke);
						g2.setColor(da.getColor());
						g2.draw(rect);
					}

					// this draws horisontal part of histograms
					g2.setPaint(da.getColor());
					g2.drawLine(xlow, pp.getY(), xup, pp.getY());

					if (i > 1) {
						g2.drawLine(xlow, prev.getY(), xlow, pp.getY());
						// System.out.println("xlow="+Float.toString(xlow));
						// System.out.println("="+Float.toString(xlow));

						if ((xlow - xup_prev) > 0.05) {
							// System.out.println("Fix me
							// pp.getY()="+Double.toString(pp.getY()));

							g2.drawLine(xup_prev, prev.getY(), xup_prev, cYmin);

							g2.drawLine(xlow, pp.getY(), xlow, cYmin);

							// line.setLine( xlow, zeroY,
							// xlow, pp.getY() );
							// g2.draw(line);
						}

					}

					if (i == 1) {
						g2.drawLine(xlow, cYmin, xlow, pp.getY());

					}
					if (i == points.size()) {
						g2.drawLine(xup, cYmin, xup, pp.getY());

					}

					// now show errors
					if (da.getErrorsY()) {
						g2.setPaint(da.getColorErrorsY());
						// g2.setStroke(new BasicStroke(da.getPenWidthErr()));

						g2.drawLine(pp.getX(), Ylow, pp.getX(), Yup);

						if (da.getErrorsFill()) {
							xpolDW[i - 1] = (int) pp.getX();
							ypolDW[i - 1] = (int) Ylow;
							xpolUP[i - 1] = (int) pp.getX();
							ypolUP[i - 1] = (int) Yup;
						}

						g2.drawLine(XlowTic, Ylow, XupTic, Ylow);

						g2.drawLine(XlowTic, Yup, XupTic, Yup);

					}

					// show systematical
					if (da.getErrorsSysY()) {
						g2.setPaint(da.getColorErrorsSysY());
						g2.setStroke(new BasicStroke(da.getPenWidthErrSys()
								* scalingFrame));

						g2.drawLine(pp.getX(), YlowS, pp.getX(), Ylow);

						g2.drawLine(pp.getX(), Ylow, pp.getX(), YlowS);

					} // end Y

					// now draw horisontal errors
					if (da.getErrorsX()) {
						g2.setPaint(da.getColorErrorsX());
						g2.setStroke(new BasicStroke(da.getPenWidthErr()
								* scalingFrame));

						g2.drawLine(Xlow, pp.getY(), Xup, pp.getY());

						// show tics

						g2.drawLine(Xlow, pp.getY() - sl, Xlow, pp.getY() + sl);

						g2.drawLine(Xup, pp.getY() - sl, Xup, pp.getY() + sl);

					}

					// sys
					if (da.getErrorsSysX()) {
						g2.setPaint(da.getColorErrorsSysX());
						g2.setStroke(new BasicStroke(da.getPenWidthErrSys()
								* scalingFrame));

						g2.drawLine(XlowS, pp.getY(), Xlow, pp.getY());

						g2.drawLine(Xup, pp.getY(), XupS, pp.getY());

					}

				} // end histogram

				// Draw a line if asked for. If dashes are used, calculate
				// the minimum distance between two plotting points in order
				// to display the dash correctly:
				else if (i > 1 && da.drawLine() && !prev.liftPen()) {

					g2.setStroke(lineStroke);

					if (da.getDashLength() > 0
							&& (da.getType() == LinePars.H1D || da.getType() == LinePars.F1D)) {

						double minDist = Math.sqrt((pp.getX() - prev.getX())
								* (pp.getX() - prev.getX())
								+ (pp.getY() - prev.getY())
								* (pp.getY() - prev.getY()));

						// System.out.println(da.getType());

						if (da.getType() == LinePars.H1D) {
							if (minDist < da.getDashLength() * 2)
								continue;
							g2.drawLine(prev.getX(), prev.getY(), pp.getX(),
									pp.getY());

						}

						// only for functions
						if (da.getType() == LinePars.F1D) {
							dashCounter1 = dashCounter1 + minDist;
							if (dashCounter1 > da.getDashLength() * 2) {

								g2.drawLine(prev.getX(), prev.getY(),
										pp.getX(), pp.getY());

								dashCounter2 = dashCounter2 + minDist;
								if (dashCounter2 > da.getDashLength() * 2) {
									dashCounter1 = 0;
									dashCounter2 = 0;
									continue;

								}
							}

						}

					} else { // no dashed

						g2.drawLine(prev.getX(), prev.getY(), pp.getX(),
								pp.getY());
					}

				}

				prev = pp;
			}

			// ---------------- fill area between error bars
			// -------------------------

			final int NP = 2 * NtotPoly + 1;

			// fill area with systematical errors.
			if (da.getErrorsFillSys()) {

				// get old colors
				Color cold = g2.getColor();
				Composite com_old = g2.getComposite();

				// set new colors
				g2.setColor(da.getColorErrorsFillSys());
				Composite c = AlphaComposite.getInstance(
						AlphaComposite.SRC_OVER,
						da.getErrorFillColorTranspSys());
				g2.setComposite(c);

				int xpol[] = new int[NP];
				int ypol[] = new int[NP];
				for (int m = 0; m < NtotPoly; m++) {
					xpol[m] = xpolUPsys[m];
					ypol[m] = ypolUPsys[m];
				}
				int mm = NtotPoly;
				for (int m = NtotPoly - 1; m >= 0; m--) {
					xpol[mm] = xpolDWsys[m];
					ypol[mm] = ypolDWsys[m];
					mm++;
				}
				xpol[NtotPoly * 2] = xpol[0];
				ypol[NtotPoly * 2] = ypol[0];

				for (int m = 1; m < NP; m++) {
					// System.out.println(Double.toString(toUserX(xpol[m]))+
					// "     "+Double.toString( toUserY(ypol[m])));
				}

				g2.fillPolygon(xpol, ypol, NP);

				g2.setComposite(com_old);
				g2.setColor(cold);
			} // end fill area for errors

			// fill statistical errors

			if (da.getErrorsFill()) {

				// get old colors
				Color cold = g2.getColor();
				Composite com_old = g2.getComposite();

				// set new colors
				g2.setColor(da.getColorErrorsFill());
				Composite c = AlphaComposite.getInstance(
						AlphaComposite.SRC_OVER, da.getErrorFillColorTransp());
				g2.setComposite(c);

				int xpol[] = new int[NP];
				int ypol[] = new int[NP];
				for (int m = 0; m < NtotPoly; m++) {
					xpol[m] = xpolUP[m];
					ypol[m] = ypolUP[m];
				}
				int mm = NtotPoly;
				for (int m = NtotPoly - 1; m >= 0; m--) {
					xpol[mm] = xpolDW[m];
					ypol[mm] = ypolDW[m];
					mm++;
				}
				xpol[NtotPoly * 2] = xpol[0];
				ypol[NtotPoly * 2] = ypol[0];

				for (int m = 1; m < NP; m++) {
					// System.out.println(Double.toString(toUserX(xpol[m]))+
					// "     "+Double.toString( toUserY(ypol[m])));
				}

				g2.fillPolygon(xpol, ypol, NP);

				g2.setComposite(com_old);
				g2.setColor(cold);
			} // end fill area for errors

		} // end run over objects

		// draw the on the foreground, if this is what they want:
		if (gs.gridToFront())
			drawGrid(g2);

		g2.setStroke(new BasicStroke(gs.getPenWidthAxis() * scalingFrame));

		// repaint Margines, to remove overflow
		drawMargins(g2);

		// draw the axes and tics:
		plotAxes(g2);

		// plot primitives
		if (gs.primitivesToFront())
			plotPrimitives(g2);

		// draw the title labels:
		plotLabels(g2);

		// draw the legend. Do this at the end because it must be drawn on
		// the top of all other drawing stuff (e.g. grid...)

		if (gs.drawLegend()) {
			int k = 0;
			g2.setFont(scaleFont(gs.getLegendFont()));
			for (Enumeration e = data.elements(); e.hasMoreElements(); k++) {
				DataArray da = (DataArray) e.nextElement();
				if (!da.drawLegend())
					k--;
				else {
					g2.setColor(da.getColor());
					if (da.drawLine())
						g2.setStroke(da.getStroke());
					drawLegend(g2, da, k);
				}
			}
		}

		g2.setStroke(new BasicStroke());

	}

	/**
	 * Checks whether x and y are within the ranges. The ranges are defined by
	 * the axes system.
	 * 
	 * @param x
	 *            x-point
	 * @param y
	 *            y-point
	 */
	private boolean inRange(double x, double y) {
		if (x < leftMargin || x > leftMargin + axisLength[X] || y < topMargin
				|| y > topMargin + axisLength[Y])
			return false;
		return true;
	}
}

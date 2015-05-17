/**
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
 *    
 *    Copyright (C) 1999, S.Chekanov 
 **/

package jplot;

import java.io.Serializable;

/**
 * The <code>PlotPoint</code> defines a plot point in terms of it's X- and Y
 * value. Also sets the lift-pen attribute, which, if true, will lift the pen
 * after having plot this point.
 */
public class PlotPoint implements Serializable {

	private static final long serialVersionUID = 1L;
	protected double x;
	protected double y;
	protected double left = 0;
	protected double right = 0;
	protected double upper = 0;
	protected double lower = 0;
	protected double left_sys = 0;
	protected double right_sys = 0;
	protected double upper_sys = 0;
	protected double lower_sys = 0;

	protected boolean lift;

	/**
	 * Default constructor, does nothing.
	 */
	public PlotPoint() {
		lift = false;
	}

	/**
	 * Default constructor, initializes the class x- and y values.
	 * 
	 * @param x
	 *            x-value;
	 * @param y
	 *            y-value;
	 */
	public PlotPoint(double x, double y) {
		setPoint(x, y);
		lift = false;
	}

	/**
	 * Default constructor, initializes the class x- and y values. alos includes
	 * errors
	 * 
	 * @param x
	 *            x-value;
	 * @param y
	 *            y-value;
	 * @param upper
	 *            upper error
	 * @param lower
	 *            lower error
	 */
	public PlotPoint(double x, double y, double upper, double lower) {
		setPoint(x, y, upper, lower);
		lift = false;
	}

	/**
	 * Default constructor, initializes the class x- and y values, including
	 * stat error. also includes errors
	 * 
	 * @param x
	 *            x-value;
	 * @param y
	 *            y-value;
	 * @param left
	 *            left error on X
	 * @param right
	 *            right error on X
	 * @param upper
	 *            upper error on Y
	 * @param lower
	 *            lower error on Y
	 */
	public PlotPoint(double x, double y, double left, double right,
			double upper, double lower) {
		setPoint(x, y, left, right, upper, lower);
		lift = false;
	}

	/**
	 * Default constructor, initializes the class x- and y values, including
	 * stat and sys errors alos includes errors
	 * 
	 * @param x
	 *            x-value;
	 * @param y
	 *            y-value;
	 * @param left
	 *            left error on X
	 * @param right
	 *            right error on X
	 * @param upper
	 *            upper error on Y
	 * @param lower
	 *            lower error on Y
	 * @param left_sys
	 *            left system. error on X
	 * @param right_sys
	 *            right system. error on X
	 * @param upper_sys
	 *            upper system. error on Y
	 * @param lower_sys
	 *            lower system. error on Y
	 */
	public PlotPoint(double x, double y, double left, double right,
			double upper, double lower, double left_sys, double right_sys,
			double upper_sys, double lower_sys) {
		setPoint(x, y, left, right, upper, lower, left_sys, right_sys,
				upper_sys, lower_sys);
		lift = false;
	}

	/**
	 * Initializes the class x- and y values and sets the lift attribute.
	 * 
	 * @param x
	 *            x-value;
	 * @param y
	 *            y-value;
	 * @param liftNow
	 *            true if the pen should lift at this point
	 */
	public PlotPoint(double x, double y, boolean liftNow) {
		setPoint(x, y);
		lift = liftNow;
	}

	/**
	 * Another constructor, initializes the class x- and y values.
	 * 
	 * @param pp
	 *            PlotPoint instance
	 */
	public PlotPoint(PlotPoint pp) {
		setPoint(pp);
		lift = false;
	}

	/**
	 * Sets x- and y values.
	 * 
	 * @param x
	 *            x-value;
	 * @param y
	 *            y-value;
	 */
	public void setPoint(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Sets x- and y values. Alos errors
	 * 
	 * @param x
	 *            x-value;
	 * @param y
	 *            y-value;
	 * @param upper
	 *            upper error
	 * @param lower
	 *            lower error
	 */
	public void setPoint(double x, double y, double upper, double lower) {
		this.x = x;
		this.y = y;
		this.upper = upper;
		this.lower = lower;

	}

	/**
	 * Sets x- and y values. Alos errors
	 * 
	 * @param x
	 *            x-value;
	 * @param y
	 *            y-value;
	 * @param left
	 *            error on X
	 * @param right
	 *            right error on Y
	 * @param upper
	 *            upper error
	 * @param lower
	 *            lower error
	 */
	public void setPoint(double x, double y, double left, double right,
			double upper, double lower) {
		this.x = x;
		this.y = y;
		this.left = left;
		this.right = right;
		this.upper = upper;
		this.lower = lower;

	}

	/**
	 * Sets x- and y values. Alos errors
	 * 
	 * @param x
	 *            x-value;
	 * @param y
	 *            y-value;
	 * @param left
	 *            error on X
	 * @param right
	 *            right error on Y
	 * @param upper
	 *            upper error
	 * @param lower
	 *            lower error
	 * @param left_sys
	 *            left system. error on X
	 * @param right_sys
	 *            right system. error on X
	 * @param upper_sys
	 *            upper system. error on Y
	 * @param lower_sys
	 *            lower system. error on Y
	 */
	public void setPoint(double x, double y, double left, double right,
			double upper, double lower, double left_sys, double right_sys,
			double upper_sys, double lower_sys) {
		this.x = x;
		this.y = y;
		this.left = left;
		this.right = right;
		this.upper = upper;
		this.lower = lower;
		this.left_sys = left_sys;
		this.right_sys = right_sys;
		this.upper_sys = upper_sys;
		this.lower_sys = lower_sys;

	}

	/**
	 * Sets x- and y values.
	 * 
	 * @param pp
	 *            PlotPoint instance
	 */
	public void setPoint(PlotPoint pp) {
		this.x = pp.getX();
		this.y = pp.getY();
		this.left = pp.getXleft();
		this.right = pp.getXright();
		this.upper = pp.getYupper();
		this.lower = pp.getYlower();
		this.left_sys = pp.getXleftSys();
		this.right_sys = pp.getXrightSys();
		this.upper_sys = pp.getYupperSys();
		this.lower_sys = pp.getYlowerSys();

	}

	/**
	 * returns x- and y values.
	 * 
	 * @return a PlotPoint instance with the current X- and Y values
	 */
	public PlotPoint getPoint() {
		return new PlotPoint(x, y, left, right, upper, lower, left_sys,
				right_sys, upper_sys, lower_sys);
	}

	/**
	 * Return the current X-value.
	 * 
	 * @return the current value of x
	 */
	public double getX() {
		return x;
	}

	/**
	 * Return the current Y-value.
	 * 
	 * @return the current value of y
	 */
	public double getY() {
		return y;
	}

	/**
	 * Return the current left error on X-value.
	 * 
	 * @return the current value of x
	 */
	public double getXleft() {
		return left;
	}

	/**
	 * Return the current right error on X-value.
	 * 
	 * @return the current value of y
	 */
	public double getXright() {
		return right;
	}

	/**
	 * Return the current left systematical error on X-value.
	 * 
	 * @return the current value of x
	 */
	public double getXleftSys() {
		return left_sys;
	}

	/**
	 * Return the current right systematical error on X-value.
	 * 
	 * @return the current value of y
	 */
	public double getXrightSys() {
		return right_sys;
	}

	/**
	 * Return the upper error on current Y-value.
	 * 
	 * @return the current value of y
	 */
	public double getYupper() {
		return upper;
	}

	/**
	 * Return the lower error on current Y-value.
	 * 
	 * @return the current value of y
	 */
	public double getYlower() {
		return lower;
	}

	/**
	 * Return the upper systematical error on current Y-value.
	 * 
	 * @return the current value of y
	 */
	public double getYupperSys() {
		return upper_sys;
	}

	/**
	 * Return the lower systematical error on current Y-value.
	 * 
	 * @return the current value of y
	 */
	public double getYlowerSys() {
		return lower_sys;
	}

	/**
	 * Sets whether or not the drawing pen should be lifted after this point.
	 * 
	 * @param b
	 *            true if the pen should lift
	 */
	public void setLiftPen(boolean b) {
		lift = b;
	}

	/**
	 * @return true if the pen should be lift after this point.
	 */
	public boolean liftPen() {
		return lift;
	}

	/**
	 * print PlotPoint
	 */
	public void print() {

		System.out.println("--- print-out of PlotPoint ---");
		String s1 = Double.toString(getX());
		String s2 = Double.toString(getY());
		String s3 = Double.toString(getYupper());
		String s4 = Double.toString(getYlower());
		String s5 = Double.toString(getXleft());
		String s6 = Double.toString(getXright());
		System.out
				.println("    X=" + s1 + "   Y=" + s2 + "   errLeft=" + s5
						+ "  errRight=" + s6 + "   errUpper=" + s3
						+ "  errLower=" + s4);

	}

}

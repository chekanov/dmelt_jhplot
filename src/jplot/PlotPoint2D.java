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

/**
 * The <code>PlotPoint2D</code> defines a plot point in terms of it's X , Y
 * and Z value. Also sets the lift-pen attribute, which, if true, will lift the
 * pen after having plot this point. It has internal parameter which can be used
 * for sorting
 */
public class PlotPoint2D {

	protected double x;

	protected double y;

	protected double z;

	/**
	 * Default constructor, does nothing.
	 */
	public PlotPoint2D() {
	}

	/**
	 * Default constructor, initializes the class x- and y values.
	 * 
	 * @param x
	 *            x-value;
	 * @param y
	 *            y-value;
	 * @param z
	 *            z-value;
	 */
	public PlotPoint2D(double x, double y, double z) {
		setPoint(x, y, z);
	}

	/**
	 * Another constructor, initializes the class x- and y values.
	 * 
	 * @param pp
	 *            PlotPoint instance
	 */
	public PlotPoint2D(PlotPoint2D pp) {
		setPoint(pp);
	}

	/**
	 * Sets x-, y and z values.
	 * 
	 * @param x
	 *            x-value;
	 * @param y
	 *            y-value;
	 * @param z
	 *            y-value;
	 */
	public void setPoint(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Sets x- and y and z values.
	 * 
	 * @param pp
	 *            PlotPoint instance
	 */
	public void setPoint(PlotPoint2D pp) {
		this.x = pp.getX();
		this.y = pp.getY();
		this.z = pp.getZ();

	}

	/**
	 * returns x- and y and z values.
	 * 
	 * @return a PlotPoint2D instance with the current X- and Y values
	 */
	public PlotPoint2D getPoint() {
		return new PlotPoint2D(x, y, z);
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
	 * Return the current Z-value.
	 * 
	 * @return the current value of y
	 */
	public double getZ() {
		return z;
	}

	/**
	 * print PlotPoint2D
	 */
	public void print() {

		System.out.println("--- print-out of PlotPoint2D ---");
		String s1 = Double.toString(getX());
		String s2 = Double.toString(getY());
		String s3 = Double.toString(getZ());
		System.out.println("    X=" + s1 + "   Y=" + s2 + "   Z=" + s3);

	}

}

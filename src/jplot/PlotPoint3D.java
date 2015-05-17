/**
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

/**
 * The <code>PlotPoint3D</code> defines a plot point in terms of it's X , Y
 * and Z value, and also sets extension in each direction. This means it makes a
 * surface.
 */
public class PlotPoint3D {

	protected double x;

	protected double y;

	protected double z;

	protected double dx;

	protected double dy;

	protected double dz;

	/**
	 * Default constructor, does nothing.
	 */
	public PlotPoint3D() {
	}

	/**
	 * Default constructor, initializes the class x,y,z and extension in 3D.
	 * 
	 * @param x
	 *            x-value;
	 * @param dx
	 *            dx-value;
	 * @param y
	 *            y-value;
	 * @param dy
	 *            y-value;
	 * @param z
	 *            z-value;
	 * @param dz
	 *            z-value;
	 */
	public PlotPoint3D(double x, double dx, double y, double dy, double z,
			double dz) {
		setPoint(x, dx, y, dy, z, dz);
	}

	/**
	 * Another constructor, initializes the class x- and y values.
	 * 
	 * @param pp
	 *            PlotPoint instance
	 */
	public PlotPoint3D(PlotPoint3D pp) {
		setPoint(pp);
	}

	/**
	 * Sets x-, y and z values.
	 * 
	 * @param x
	 *            x-value;
	 * @param dx
	 *            dx-value;
	 * @param y
	 *            y-value;
	 * @param dy
	 *            y-value;
	 * @param z
	 *            z-value;
	 * @param dz
	 *            z-value;
	 */
	public void setPoint(double x, double dx, double y, double dy, double z,
			double dz) {

		this.x = x;
		this.dx = dx;
		this.y = y;
		this.dy = dy;
		this.z = z;
		this.dz = dz;
	}

	/**
	 * Sets x- and y and z values.
	 * 
	 * @param pp
	 *            PlotPoint3D instance
	 */
	public void setPoint(PlotPoint3D pp) {
		this.x = pp.getX();
		this.y = pp.getY();
		this.z = pp.getZ();
		this.dx = pp.getDX();
		this.dy = pp.getDY();
		this.dz = pp.getDZ();

	}

	/**
	 * returns x- and y and z values.
	 * 
	 * @return a PlotPoint2D instance with the current X- and Y values
	 */
	public PlotPoint3D getPoint() {
		return new PlotPoint3D(x, dx, y, dy, z, dz);
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
	 * Return the current dX-value.
	 * 
	 * @return the current value of x
	 */
	public double getDX() {
		return dx;
	}

	/**
	 * Return the current dY-value.
	 * 
	 * @return the current value of y
	 */
	public double getDY() {
		return dy;
	}

	/**
	 * Return the current dZ-value.
	 * 
	 * @return the current value of y
	 */
	public double getDZ() {
		return dz;
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

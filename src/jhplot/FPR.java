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

import java.awt.Color;
import java.io.Serializable;

/**
 * Class to build parametric functions using two independent variables, u and v.
 * Both vary within 0 and 1. Use the HPlot3DP canvas for 3D plotting.
 * 
 * @author S.Chekanov
 * 
 */

public class FPR extends Plottable {

	/**
	 * Main class to build parametric functions.
	 */

	private static final long serialVersionUID = 1L;
	private Color fillColor;
	private Color lineColor;
	private int penWidth;
	private FProxy proxy;

	/**
	 * Create a parametric function.
	 * 
	 * @param title
	 *            Title of the function
	 * @param name
	 *            String representing parametric equation. Use "u" and "v"
	 *            (changes from 0 to 1).
	 * @param divU
	 *            number of divisions for variable U
	 * @param divV
	 *            number of divisions for variable V;
	 */
	public FPR(String title, String name, int divU, int divV) {

		proxy = new FProxy(5, title, name, null, new double[] { divU, divV, 0,
				0, 0, 0 }, 0, true);
		this.fillColor = Color.blue;
		this.lineColor = Color.black;
		this.penWidth = 1;
		is3D = true;
	}

	/**
	 * Initialize function from proxy.
	 * 
	 * @param f
	 */

	public FPR(FProxy f) {

		if (f.getType() != 5) {
			jhplot.utils.Util
					.ErrorMessage("Error in parsing FPR. Wrong function type! "
							+ f.getName());
			return;
		}

		proxy = f;
		String name = proxy.getName();
		setTitle(proxy.getTitle());
		this.fillColor = Color.blue;
		this.lineColor = Color.black;
		this.penWidth = 1;
		is3D = true;

	}

	/**
	 * Create a parametric function. Title is set to its name.
	 * 
	 * @param name
	 *            String representing parametric equation. Use "u" and "v"
	 *            (changes from 0 to 1).
	 * @param divU
	 *            number of divisions for variable U
	 * @param divV
	 *            number of divisions for variable V;
	 */
	public FPR(String name, int divU, int divV) {

		this(name, name, divU, divV);

	}

	/**
	 * Create a parametric function. The number of divisions by default is 21
	 * for U and V variables;
	 * 
	 * @param name
	 *            String representing parametric equation. Use "u" and "v"
	 *            (changes from 0 to 1).
	 * 
	 */
	public FPR(String name) {
		this(name, 21, 21);
	}

	/**
	 * Set color for lines
	 * 
	 * @param color
	 *            line color
	 */
	public void setLineColor(Color color) {
		this.lineColor = color;
	}

	/**
	 * Set color for fill
	 * 
	 * @param color
	 *            fill color
	 */
	public void setFillColor(Color color) {
		this.fillColor = color;
	}

	/**
	 * Get color used to fill.
	 * 
	 * @return color to fill
	 */
	public Color getFillColor() {
		return fillColor;
	}

	/**
	 * Get line color
	 * 
	 * @return line color
	 */
	public Color getLineColor() {
		return lineColor;
	}

	/**
	 * Get parametric equation.
	 * 
	 * @return
	 */
	public String getName() {
		return proxy.getName();
	}

	/**
	 * Get number of divisions for U
	 * 
	 * @return divisions
	 */
	public int getDivU() {
		double[] d = proxy.getLimits();
		return (int) d[0];
	}

	/**
	 * Get number of divisions for V
	 * 
	 * @return divisions
	 */
	public int getDivV() {
		double[] d = proxy.getLimits();
		return (int) d[1];
	}

	/**
	 * Set number of divisions for parameters.
	 * 
	 * @param U
	 *            for U
	 * @param V
	 *            for V
	 */
	public void setDivisions(int U, int V) {

		proxy.setLimit(0, (double) U);
		proxy.setLimit(0, (double) V);

	}

	/**
	 * Set width of lines
	 * 
	 * @param penWidth
	 *            width
	 */
	public void setPenWidth(int penWidth) {
		this.penWidth = penWidth;
	}

	/**
	 * Get width of line
	 * 
	 * @return width
	 */
	public int getPenWidth() {
		return penWidth;
	}

	/**
	 * Set filled area or not
	 * 
	 * @param isFilled
	 */
	public void setFilled(boolean isFilled) {
		proxy.setParsed(isFilled);
	}

	/**
	 * Is area filled or not
	 * 
	 * @return
	 */
	public boolean isFilled() {
		return proxy.isParsed();

	}

	/**
	 * Get this function as a string.
	 * 
	 * @return Convert to string.
	 */
	public String toString() {

		String tmp = "FPR:" + proxy.getName();
		double[] d = proxy.getLimits();
		boolean isFilled = proxy.isParsed();
		double Xmin = d[0];
		double Xmax = d[1];

		int points = proxy.getPoints();
		tmp = tmp + " (title=" + getTitle() + ", n=" + Integer.toString(points)
				+ ", divX=" + Double.toString(Xmin) + ", divY="
				+ Double.toString(Xmax);

		return tmp;
	}

}

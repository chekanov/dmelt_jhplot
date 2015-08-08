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

import hep.aida.*;

import java.io.Serializable;

import org.freehep.graphicsio.swf.SWFAction.SetVariable;

/**
 * A proxy for all functions.
 * 
 */

public class FProxy implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int points;

	private String name;

	private String title;

	private IFunction iname = null;

	private boolean isParsed = false;

	private double[] limits;

	private int type = 1;

	private String variables = "x";

	/**
	 * Create a proxy for all functions of DMelt.
	 * 
	 * @param type
	 *            type (1=F1D, 2=F2D, 3=F3D, 4=FND, 5=FPR)
	 * @param title
	 *            title
	 * @param name
	 *            definition
	 * @param iname
	 *            IFunction
	 * @param limits
	 *            limits if ranged
	 * @param points
	 *            number of points for plots
	 * @param isParsed
	 *            if parsed or not.
	 */

	public FProxy(int type, String title, String name, IFunction iname,
			double[] limits, int points, boolean isParsed) {

                if (name !=null) {
		name = name.replace("**", "^"); // preprocess power
		name = name.replace("pi", "3.14159265");
		name = name.replace("Pi", "3.14159265");
                name = name.replace("PI", "3.14159265");
                }

		this.points = points;
		this.name = name;
		this.title = title;
		this.iname = iname;
		this.limits = limits;
		this.isParsed = isParsed;
		this.type = type;
		this.variables = "x";
		if (type == 2)
			this.variables = "x,y";
		if (type == 3)
			this.variables = "x,y,z";
	}

	public void setParsed(boolean parse) {
		this.isParsed = parse;
	}

	/**
	 * Get type of this function. F1D, F2D, F3D
	 * 
	 * @return type. 1 for F1D, 2 for F2D, 3 for F3D.
	 */
	public int getType() {
		return type;
	}

	/**
	 * Get limits if any.
	 * 
	 * @return limits for evaluation.
	 */
	public double[] getLimits() {
		return limits;
	}

	/**
	 * Set limit entry
	 * 
	 * @param index
	 *            index of the limit
	 * @param value
	 *            value
	 */
	public void setLimit(int index, double value) {
		limits[index] = value;
	}

	/**
	 * Get Jaida function
	 * 
	 * @return
	 */
	public IFunction getIFunction() {
		return iname;
	}

	/**
	 * Get the number of points used for plotting, integration and
	 * differentiation.
	 * 
	 * @return Number of points
	 */

	public int getPoints() {
		return this.points;

	}

	/**
	 * Get the name of the function used for evaluation
	 * 
	 * @return Name
	 */
	public String getName() {
		return this.name;

	}

	/**
	 * Get the title of the function.
	 * 
	 * @returnTitle
	 */
	public String getTitle() {
		return this.title;

	}

	/**
	 * If the function is parsed correctly, return true. Use this check before
	 * drawing it.
	 * 
	 * @return true if parsed.
	 */
	public boolean isParsed() {

		return this.isParsed;
	}

	/**
	 * Set actual function definition.
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Set title.
	 * 
	 * @param title
	 */

	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Limits if function is ranged.
	 * 
	 * @param limits
	 */

	public void setLimits(double[] limits) {
		this.limits = limits;
	}

	/**
	 * Numbers of points for evaluation.
	 * 
	 * @param points
	 */
	public void setPoints(int points) {
		this.points = points;
	}

	/**
	 * Set type. 1- F1D, 2-F2D, 3-F3D, 4-FND
	 * 
	 * @param type
	 */
	public void setType(int type) {
		this.type = type;
	}

	public void setIFunction(IFunction iname) {
		this.iname = iname;
	}

	/**
	 * Set variable using commas
	 * 
	 * @param variables
	 */
	public void setVariables(String variables) {

		this.variables = variables;

	}

	/**
	 * Get variables
	 * 
	 * @return
	 */
	public String getVariables() {

		return this.variables;

	}
}

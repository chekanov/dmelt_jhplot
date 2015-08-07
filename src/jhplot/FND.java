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

import jplot.LinePars;
import org.nfunk.jep.*;
import org.lsmp.djep.xjep.*;
import org.lsmp.djep.djep.*;
import javax.swing.JOptionPane;
import jhplot.gui.HelpBrowser;

/**
 * Create a function in multiple dimensions. The function name could have
 * parameters named in unique way as P0, P1, P2 ... They have to be replaced
 * with values using setPar() method for evaluation
 * 
 * @author S.Chekanov
 * 
 */

public class FND extends DrawOptions {

	/**
	 * 
	 */

	private FProxy proxy;
	final int maxpoints = 200;
	private static final long serialVersionUID = 1L;

	private double[] x = null;

	private double[] y = null;

	// private double[] z = null;

	private boolean isEvaluated = false;

	private XJep jep = null;
	private Node node = null;
	private Node processed = null;
	private Node simp = null;
	// private Object value = null;
	private String[] avars = null;
	private Node diff = null;

	private String fixedVars = "";

	/**
	 * Create a function in any dimension evaluation.
	 * 
	 * The function may have many independent variables.
	 * <p>
	 * <h3>List of commands</h3>
	 * <ul>
	 * <li>( ) parenthesis , comma
	 * <li>+, -, unary -, unary +
	 * <li>*, /
	 * <li>^ (raise to a power)
	 * <li>pi, e, All the constants in class SpecialFunction
	 * <li>log
	 * <li>sin, cos, tan, sinh, cosh, tanh
	 * <li>asin, acos, atan, asinh, acosh, atanh
	 * <li>sqrt
	 * <li>rand
	 * <li>exp
	 * <li>remainder
	 * <li>atan2
	 * <li>Special functions and constants. Look at the book
	 * <li>All the functions in class SpecialFunction
	 * <li>Independent variables x
	 * <li>Scientific notation using "e", "E", "d", "D".
	 * </ul>
	 * 
	 * 
	 * @prama title Title of the function
	 * 
	 * @param name
	 *            String representing the function
	 * @param vars
	 *            String representing variables. Each variable should be
	 *            separated by a comma. Example "x,y,z"
	 */
	public FND(String title, String name, String vars) {

		this.title = title;
		proxy = new FProxy(3, title, name, null, new double[] { 0, 0, 0, 0, 0,
				0 }, maxpoints, true);

                name=proxy.getName();
		proxy.setVariables(vars);
		setTitle(title);
		lpp.setType(LinePars.F1D);
		jep = new XJep();
		jep.addStandardConstants();
		jep.addStandardFunctions();
		jep.setAllowUndeclared(true);
		jep.setImplicitMul(true);
		jep.setAllowAssignment(true);

		// get all variables
		avars = vars.split(",");
		for (int i = 0; i < avars.length; i++) {
			jep.addVariable(avars[i].trim(), 0);
		}

		jep.addVariable("x", 0);

		try {
			node = jep.parse(name);
			processed = jep.preprocess(node);
		} catch (ParseException e) {
		} catch (Exception e) {
			ErrorMessage("Error in parsing " + name);
		}
	}

	/**
	 * Build a function, setting its title to the definition.
	 * 
	 * @param title
	 *            Title
	 * @param name
	 *            name
	 * @param vars
	 *            Options
	 */
	public FND(String name, String vars) {
		this(name, name, vars);
	}

	/**
	 * Initialize function from proxy.
	 * 
	 * @param f
	 */

	public FND(FProxy f) {
		proxy = f;
		String name = proxy.getName();
		if (proxy.getType() != 4) {
			ErrorMessage("Error in parsing FND. Wrong type! " + name);
			return;
		}

		setTitle(proxy.getTitle());
		lpp.setType(LinePars.F1D);
		jep = new XJep();
		jep.addStandardConstants();
		jep.addStandardFunctions();
		jep.setAllowUndeclared(true);
		jep.setImplicitMul(true);
		jep.setAllowAssignment(true);

		// get all variables
		String vars = proxy.getVariables();
		avars = vars.split(",");
		for (int i = 0; i < avars.length; i++) {
			jep.addVariable(avars[i].trim(), 0);
		}

		jep.addVariable("x", 0);

		try {
			node = jep.parse(name);
			processed = jep.preprocess(node);
		} catch (ParseException e) {
		} catch (Exception e) {
			ErrorMessage("Error in parsing " + name);
		}

	}

	/**
	 * Treat the function as complex.
	 **/
	public void setComplex() {
		jep.addComplex();
	}

	/**
	 * Treat the function as complex.
	 **/
	public void simplify() {

		String name = proxy.getName();

		try {
			simp = jep.simplify(processed);
		} catch (ParseException e) {
		} catch (Exception e) {
			ErrorMessage("Error in simplification of " + name);
		}

	}

	/**
	 * differentiate the expression and simplify
	 * 
	 * @param var
	 *            variable used for differentiation
	 **/
	public void diff(String var) {
		String name = proxy.getName();

		DJep j = new DJep();
		j.addStandardConstants();
		j.addStandardFunctions();
		j.addComplex();
		j.setAllowUndeclared(true);
		j.setAllowAssignment(true);
		j.setImplicitMul(true);
		// Sets up standard rules for differentiating sin(x) etc.
		j.addStandardDiffRules();

		try {
			// parse the string
			node = j.parse(name);
			diff = j.differentiate(node, var);
			simp = j.simplify(diff);
		} catch (ParseException e) {
			ErrorMessage("Error in parsing " + name);
		}
	}

	/**
	 * Return all variables
	 * 
	 * @return array array with variables
	 **/
	public String[] getVars() {
		return avars;
	}

	/**
	 * Convert to string
	 **/
	public String toString() {
		if (simp != null) {
			return jep.toString(simp);
		}
		return jep.toString(node);
	}

	/**
	 * Evaluate a function at a specific point in x
	 * 
	 * @param vars
	 *            Values for evaluation separated by commas, x=1,y=2,z=3
	 * @return function value at x
	 */
	public double eval(String vars) {

		// get all variables
		String[] tmp = vars.split(",");
		// double[] vd = new double[tmp.length];

		for (int i = 0; i < tmp.length; i++) {
			String[] vv = tmp[i].split("=");

			if (vv.length != 2) {
				ErrorMessage("Error in parsing list of input variablse. Did you use val=number? ");
			}

			try {
				double d = Double.valueOf(vv[1].trim()).doubleValue();
				jep.addVariable(vv[0].trim(), d);
			} catch (NumberFormatException nfe) {
				System.out
						.println("NumberFormatException: " + nfe.getMessage());
			}

		}

		try {
			Object result = jep.evaluate(node);
			if (result instanceof Double) {
				return ((Double) jep.evaluate(node)).doubleValue();
			}
		} catch (ParseException e) {
			return 0;
		}

		return 0;

	}

	/**
	 * Evaluate a function at a specific point for one single variable.
	 * Evaluation is done between xmin and xmax
	 * 
	 * @param indvars
	 *            Define independent variable, like 'x' Only one variable is
	 *            allowed
	 * @param xmin
	 *            xmin value for independent varible
	 * @param xmax
	 *            xmax value for independent varible
	 * @param vars
	 *            define values for other variables, like 'y=1,z=3'
	 * @return true if no errors
	 */
	public boolean eval(String indvars, double xmin, double xmax, String vars) {
		String name = proxy.getName();
		int points = proxy.getPoints();
		boolean suc = true;
		String[] tmp = vars.split(",");
		// double[] vd = new double[tmp.length];
		fixedVars = vars;

		for (int i = 0; i < tmp.length; i++) {
			String[] vv = tmp[i].split("=");

			if (vv.length != 2) {
				ErrorMessage("Error in parsing list of input variablse. Did you use val=number? ");
			}

			try {
				double d = Double.valueOf(vv[1].trim()).doubleValue();
				jep.addVariable(vv[0].trim(), d);
			} catch (NumberFormatException nfe) {
				System.out
						.println("NumberFormatException: " + nfe.getMessage());
				suc = false;
			}

		}

		double min = xmin;
		double max = xmax;
		x = new double[points];
		y = new double[points];
		for (int i = 0; i < points; i++) {
			x[i] = min + i * (max - min) / (points - 1);
			jep.addVariable(indvars.trim(), x[i]);

			try {
				Object result = jep.evaluate(node);
				if (result instanceof Double) {
					y[i] = ((Double) jep.evaluate(node)).doubleValue();
				}
			} catch (ParseException e) {
				jhplot.utils.Util.ErrorMessage("Failed to parse function "
						+ name + " Error:" + e.toString());
				suc = false;
			}

		}

		if (suc)
			isEvaluated = true;

		return suc;

	} // end 1-D evaluation

	/**
	 * Evaluate a function at a specific point for one single variable.
	 * Evaluation is done between xmin and xmax. It is assumed that there are no
	 * any other variable involved
	 * 
	 * @param indvars
	 *            Define independent variable, like 'x' Only one variable is
	 *            allowed
	 * @param xmin
	 *            xmin value for independent varible
	 * @param xmax
	 *            xmax value for independent varible
	 * @return true if no errors
	 */
	public boolean eval(String indvars, double xmin, double xmax) {

		boolean suc = true;
		String name = proxy.getName();
		int points = proxy.getPoints();

		double min = xmin;
		double max = xmax;
		x = new double[points];
		y = new double[points];
		for (int i = 0; i < points; i++) {
			x[i] = min + i * (max - min) / (points - 1);
			jep.addVariable(indvars.trim(), x[i]);

			try {
				Object result = jep.evaluate(node);
				if (result instanceof Double) {
					y[i] = ((Double) jep.evaluate(node)).doubleValue();
				}
			} catch (ParseException e) {
				jhplot.utils.Util.ErrorMessage("Failed to parse function "
						+ name + " Error:" + e.toString());
				suc = false;
			}

		}

		if (suc)
			isEvaluated = true;

		return suc;

	} // end 1-D evaluation

	/**
	 * Is the function was evaluated?
	 * */

	public boolean isEvaluated() {
		return isEvaluated;
	}

	/**
	 * Get value in X-axis
	 * 
	 * @param i
	 *            index
	 * @return value in X
	 */
	public double getX(int i) {
		return this.x[i];
	}

	/**
	 * Get value in Y-axis
	 * 
	 * @param i
	 *            index
	 * 
	 * @return value in Y
	 */

	public double getY(int i) {
		return this.y[i];
	}

	/**
	 * Sets a name of the function, i.e. what will be used for evaluation
	 * 
	 * @param name
	 *            Name
	 */

	public void setName(String name) {
		proxy.setName(name);

	}

	/**
	 * Get the proxy of this function used for serialization and non-graphical
	 * representations.
	 * 
	 * @param proxy
	 *            proxy of this function.
	 */
	public FProxy get() {
		return proxy;
	}

	/**
	 * Get the name of the function used for evaluation
	 * 
	 * @return Name
	 */
	public String getName() {
		return proxy.getName();

	}

	/**
	 * Get arguments of the function (independent variables).
	 * 
	 * @return arguments
	 */
	public String getVarString() {
		return proxy.getVariables();

	}

	/**
	 * Show online documentation.
	 */
	public void doc() {

		String a = this.getClass().getName();
		a = a.replace(".", "/") + ".html";
		new HelpBrowser(HelpBrowser.JHPLOT_HTTP + a);

	}

	/**
	 * Set Min value in X
	 * 
	 * @param min
	 *            Minimum value
	 */

	public void setMin(double min) {
		proxy.setLimit(0, min);

	}

	/**
	 * Get the minimum value in X
	 * 
	 * @return min Minimum value
	 */
	public double getMin() {
		double[] d = proxy.getLimits();
		return d[0];
	}

	/**
	 * Set the maximum value in X
	 * 
	 * @param max
	 *            Maximal value
	 */
	public void setMax(double max) {
		proxy.setLimit(1, max);

	}

	/**
	 * Get the maximum value in X
	 * 
	 * @return Maximal value
	 */
	public double getMax() {
		double[] d = proxy.getLimits();
		return d[1];

	}

	/**
	 * Sets the number points between Min and Max for evaluation
	 * 
	 * @param bins
	 *            Number of points
	 */
	public void setPoints(int bins) {
		proxy.setPoints(bins);

	}

	/**
	 * Get the number of points
	 * 
	 * @return Number of points
	 */

	public int getPoints() {
		return proxy.getPoints();

	}

	/**
	 * String with fixed variables
	 * 
	 * @return String with fixed variables
	 **/

	public String getFixedVars() {

		return fixedVars;

	}

	/**
	 * * Generate error message * * @param a * Message
	 * */

	private void ErrorMessage(String a) {

		JOptionPane dialogError = new JOptionPane();
		JOptionPane.showMessageDialog(dialogError, a, "Error",
				JOptionPane.ERROR_MESSAGE);
	}

}

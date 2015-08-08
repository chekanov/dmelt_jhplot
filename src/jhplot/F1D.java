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
import hep.aida.*;
import jhplot.gui.HelpBrowser;
import jhplot.math.exp4j.*;

/**
 * Create a function in one dimension using "x" as a variable. The function name
 * could have parameters named in unique way as P0, P1, P2. In this case, the
 * function should not be parsed. Parameters have to be replaced with values
 * using setPar(x) method for evaluation.
 * 
 * 
 * The function may have one independent variable: x
 * <p>
 * <b>Operators and functions</b><br/>
 * <br/>
 * the following operators are supported:
 * <ul>
 * <li>Addition: '2 + 2'</li>
 * <li>Subtraction: '2 - 2'</li>
 * <li>Multiplication: '2 * 2'</li>
 * <li>Division: '2 / 2'</li>
 * <li>Exponential: '2 ^ 2' or ** (raise to a power)</li>
 * <li>Unary Minus,Plus (Sign Operators): '+2 - (-2)'</li>
 * <li>Modulo: '2 % 2'</li>
 * </ul>
 * the following functions are supported:
 * <ul>
 * <li>abs: absolute value</li>
 * <li>acos: arc cosine</li>
 * <li>asin: arc sine</li>
 * <li>atan: arc tangent</li>
 * <li>cbrt: cubic root</li>
 * <li>ceil: nearest upper integer</li>
 * <li>cos: cosine</li>
 * <li>cosh: hyperbolic cosine</li>
 * <li>exp: euler's number raised to the power (e^x)</li>
 * <li>floor: nearest lower integer</li>
 * <li>log: logarithm natural (base e)</li>
 * <li>log10: logarithm (base 10)</li>
 * <li>sin: sine</li>
 * <li>sinh: hyperbolic sine</li>
 * <li>sqrt: square root</li>
 * <li>tan: tangent</li>
 * <li>tanh: hyperbolic tangent</li>
 * </ul>
 * <br/>
 * It also recognizes the pi (or Pi) values; <br/>
 * 
 * 
 * @author S.Chekanov
 * 
 */

public class F1D extends DrawOptions {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private double[] x = null;

	private double[] y = null;

	private Expression calc = null;

	private ExpressionBuilder function = null;

	private String lastException = "";

	final int maxpoints = 500;

	private FProxy proxy;

	/**
	 * Create a function in 1D. 500 points are used between Min and Max for
	 * evaluation. The title is set to the function's definition.
	 * <p>
	 * A function can be ranged (range min and max is included) or unranged (min
	 * and max are not defined). Ranged function determines the plot ranges,
	 * integration range etc.
	 * 
	 * The function may have one independent variable: x. Example: x*x
	 * 
	 * <b>Operators and functions</b><br/>
	 * <br/>
	 * the following operators are supported:
	 * <ul>
	 * <li>Addition: '2 + 2'</li>
	 * <li>Subtraction: '2 - 2'</li>
	 * <li>Multiplication: '2 * 2'</li>
	 * <li>Division: '2 / 2'</li>
	 * <li>Exponential: '2 ^ 2' or ** (raise to a power)</li>
	 * <li>Unary Minus,Plus (Sign Operators): '+2 - (-2)'</li>
	 * <li>Modulo: '2 % 2'</li>
	 * </ul>
	 * the following functions are supported:
	 * <ul>
	 * <li>abs: absolute value</li>
	 * <li>acos: arc cosine</li>
	 * <li>asin: arc sine</li>
	 * <li>atan: arc tangent</li>
	 * <li>cbrt: cubic root</li>
	 * <li>ceil: nearest upper integer</li>
	 * <li>cos: cosine</li>
	 * <li>cosh: hyperbolic cosine</li>
	 * <li>exp: euler's number raised to the power (e^x)</li>
	 * <li>floor: nearest lower integer</li>
	 * <li>log: logarithm natural (base e)</li>
	 * <li>sin: sine</li>
	 * <li>sinh: hyperbolic sine</li>
	 * <li>sqrt: square root</li>
	 * <li>tan: tangent</li>
	 * <li>tanh: hyperbolic tangent</li>
	 * </ul>
	 * <br/>
	 * It also recognizes the pi (or Pi) values;
	 * 
	 * <br/>
	 * 
	 * 
	 * @param name
	 *            String representing the function's definition
	 */
	public F1D(String name) {
		this(name, name, 0, 0, true);

	}

	/**
	 * Initialize function from proxy.
	 * 
	 * @param f
	 */

	public F1D(FProxy f) {

		if (f.getType() != 1) {
			jhplot.utils.Util.ErrorMessage("Error in parsing F1D. Wrong type! "
					+ f.getName());
			return;
		}

		proxy = f;

		setTitle(proxy.getTitle());
		lpp.setType(LinePars.F1D);

	}

	/**
	 * Create new function. The function is unranged (nor range is defined).
	 * 
	 * @param title
	 *            title
	 * @param name
	 *            definition
	 */

	public F1D(String title, String name) {
		this(title, name, 0, 0, true);
	}

	/**
	 * Create a new function. Do not parse it when using parameters. You should
	 * apply substitution first and create a function with one variable "x". If
	 * min=0 and max=0, the ranges are determined by plotting canvases. The
	 * function is ranged. The function is parsed.
	 * 
	 * The function may have one independent variable: x
	 * <p>
	 * <b>Operators and functions</b><br/>
	 * <br/>
	 * the following operators are supported:
	 * <ul>
	 * <li>Addition: '2 + 2'</li>
	 * <li>Subtraction: '2 - 2'</li>
	 * <li>Multiplication: '2 * 2'</li>
	 * <li>Division: '2 / 2'</li>
	 * <li>Exponential: '2 ^ 2' or ** (raise to a power)</li>
	 * <li>Unary Minus,Plus (Sign Operators): '+2 - (-2)'</li>
	 * <li>Modulo: '2 % 2'</li>
	 * </ul>
	 * the following functions are supported:
	 * <ul>
	 * <li>abs: absolute value</li>
	 * <li>acos: arc cosine</li>
	 * <li>asin: arc sine</li>
	 * <li>atan: arc tangent</li>
	 * <li>cbrt: cubic root</li>
	 * <li>ceil: nearest upper integer</li>
	 * <li>cos: cosine</li>
	 * <li>cosh: hyperbolic cosine</li>
	 * <li>exp: euler's number raised to the power (e^x)</li>
	 * <li>floor: nearest lower integer</li>
	 * <li>log: logarithm natural (base e)</li>
	 * <li>log10: logarithm 10 natural(base e)</li>
	 * <li>sin: sine</li>
	 * <li>sinh: hyperbolic sine</li>
	 * <li>sqrt: square root</li>
	 * <li>tan: tangent</li>
	 * <li>tanh: hyperbolic tangent</li>
	 * </ul>
	 * <br/>
	 * It also recognizes the pi (or Pi) values; <br/>
	 * 
	 * 
	 * @param title
	 *            title
	 * @param name
	 *            definition
	 * @param min
	 *            minimum value for plotting
	 * @param max
	 *            maximum value for plotting
	 */
	public F1D(String title, String name, double min, double max) {
		this(title, name, min, max, true);
	}

	/**
	 * Define a ranged function.
	 * 
	 * @param name
	 *            name
	 * @param min
	 *            min value
	 * @param max
	 *            max value
	 * @param parsed
	 *            is parsed?
	 */
	public F1D(String name, double min, double max, boolean parsed) {
		this(name, name, min, max, parsed);
	}

	/**
	 * Create F1D function from JAIDA IFunction. By default 500 points are used.
	 * The function is ranged.
	 * 
	 * @param title
	 *            Title
	 * @param iname
	 *            input IFunction
	 * @param min
	 *            Min X values
	 * @param max
	 *            Max X values
	 */

	public F1D(String title, IFunction iname, double min, double max) {

		proxy = new FProxy(1, title, iname.title(), iname, new double[] { min, max, 0,
				0, 0, 0 }, maxpoints, false);
		setTitle(title);
		lpp.setType(LinePars.F1D);

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
	 * Create a new function. Do not parse it when using parameters. You should
	 * apply substitution first and create a function with one variable "x". If
	 * min=0 and max=0, the ranges are determined by plotting canvases. The
	 * function is ranged.
	 * 
	 * The function may have one independent variable: x
	 * <p>
	 * <b>Operators and functions</b><br/>
	 * <br/>
	 * the following operators are supported:
	 * <ul>
	 * <li>Addition: '2 + 2'</li>
	 * <li>Subtraction: '2 - 2'</li>
	 * <li>Multiplication: '2 * 2'</li>
	 * <li>Division: '2 / 2'</li>
	 * <li>Exponential: '2 ^ 2' or ** (raise to a power)</li>
	 * <li>Unary Minus,Plus (Sign Operators): '+2 - (-2)'</li>
	 * <li>Modulo: '2 % 2'</li>
	 * </ul>
	 * the following functions are supported:
	 * <ul>
	 * <li>abs: absolute value</li>
	 * <li>acos: arc cosine</li>
	 * <li>asin: arc sine</li>
	 * <li>atan: arc tangent</li>
	 * <li>cbrt: cubic root</li>
	 * <li>ceil: nearest upper integer</li>
	 * <li>cos: cosine</li>
	 * <li>cosh: hyperbolic cosine</li>
	 * <li>exp: euler's number raised to the power (e^x)</li>
	 * <li>floor: nearest lower integer</li>
	 * <li>log: logarithm natural (base e)</li>
	 * <li>log10: logarithm 10 natural(base e)</li>
	 * <li>sin: sine</li>
	 * <li>sinh: hyperbolic sine</li>
	 * <li>sqrt: square root</li>
	 * <li>tan: tangent</li>
	 * <li>tanh: hyperbolic tangent</li>
	 * </ul>
	 * <br/>
	 * It also recognizes the pi (or Pi) values; <br/>
	 * 
	 * 
	 * @param title
	 *            title
	 * @param name
	 *            definition
	 * @param min
	 *            minimum value for plotting
	 * @param max
	 *            maximum value for plotting
	 * @param parse
	 *            parse or not. Do not parse when using parameters.
	 */
	public F1D(String title, String name, double min, double max, boolean parse) {
		proxy = new FProxy(1, title, name, null, new double[] { min, max, 0, 0,
				0, 0 }, maxpoints, parse);
		setTitle(title);
		lpp.setType(LinePars.F1D);
		function = new ExpressionBuilder(proxy.getName());
		if (parse == true) {
			try {
				function.variables("x");
				calc = function.build();
			} catch (IllegalArgumentException e) {
				proxy.setParsed(false);
				jhplot.utils.Util.ErrorMessage("Failed to parse function "
						+ name + " Error:" + e.toString());

			}

		}

	}

	/**
	 * Create a F1D function from JAIDA IFunction in ranges. By default, 500
	 * points for evaluation are used
	 * 
	 * @param iname
	 *            input IFunction
	 * @param min
	 *            Min value
	 * @param max
	 *            Max value
	 */
	public F1D(IFunction iname, double min, double max) {
		proxy = new FProxy(1, iname.title(), iname.title(), iname,
				new double[] { min, max }, maxpoints, false);
		setTitle(iname.title());
		lpp.setType(LinePars.F1D);

	}

	/**
	 * Create a new function in pre-defined range for plotting. Do not parse it
	 * when using parameters. You should apply substitution first and create a
	 * function with one variable "x". If min=0 and max=0, ranges are determined
	 * by plotting canvaces.
	 * 
	 * The function may have one independent variable: x
	 * <p>
	 * <b>Operators and functions</b><br/>
	 * <br/>
	 * the following operators are supported:
	 * <ul>
	 * <li>Addition: '2 + 2'</li>
	 * <li>Subtraction: '2 - 2'</li>
	 * <li>Multiplication: '2 * 2'</li>
	 * <li>Division: '2 / 2'</li>
	 * <li>Exponential: '2 ^ 2' or ** (raise to a power)</li>
	 * <li>Unary Minus,Plus (Sign Operators): '+2 - (-2)'</li>
	 * <li>Modulo: '2 % 2'</li>
	 * </ul>
	 * the following functions are supported:
	 * <ul>
	 * <li>abs: absolute value</li>
	 * <li>acos: arc cosine</li>
	 * <li>asin: arc sine</li>
	 * <li>atan: arc tangent</li>
	 * <li>cbrt: cubic root</li>
	 * <li>ceil: nearest upper integer</li>
	 * <li>cos: cosine</li>
	 * <li>cosh: hyperbolic cosine</li>
	 * <li>exp: euler's number raised to the power (e^x)</li>
	 * <li>floor: nearest lower integer</li>
	 * <li>log: logarithm natural (base e)</li>
	 * <li>log10: logarithm 10 natural(base e)</li>
	 * <li>sin: sine</li>
	 * <li>sinh: hyperbolic sine</li>
	 * <li>sqrt: square root</li>
	 * <li>tan: tangent</li>
	 * <li>tanh: hyperbolic tangent</li>
	 * </ul>
	 * <br/>
	 * It also recognizes the pi (or Pi) values; <br/>
	 * 
	 * 
	 * @param name
	 *            definition
	 * @param min
	 *            min value for plotting
	 * @param max
	 *            max value for plotting
	 * @param parse
	 *            parse or not. Do not parse when using parameters.
	 */
	public F1D(String name, double min, double max) {
		this(name, name, min, max, true);
	}

	/**
	 * Create a new function in pre-defined range for plotting. Do not parse it
	 * when using parameters. You should apply substitution first and create a
	 * function with one variable "x". Ranges for the function are not defined.
	 * 
	 * The function may have one independent variable: x
	 * <p>
	 * <b>Operators and functions</b><br/>
	 * <br/>
	 * the following operators are supported:
	 * <ul>
	 * <li>Addition: '2 + 2'</li>
	 * <li>Subtraction: '2 - 2'</li>
	 * <li>Multiplication: '2 * 2'</li>
	 * <li>Division: '2 / 2'</li>
	 * <li>Exponential: '2 ^ 2' or ** (raise to a power)</li>
	 * <li>Unary Minus,Plus (Sign Operators): '+2 - (-2)'</li>
	 * <li>Modulo: '2 % 2'</li>
	 * </ul>
	 * the following functions are supported:
	 * <ul>
	 * <li>abs: absolute value</li>
	 * <li>acos: arc cosine</li>
	 * <li>asin: arc sine</li>
	 * <li>atan: arc tangent</li>
	 * <li>cbrt: cubic root</li>
	 * <li>ceil: nearest upper integer</li>
	 * <li>cos: cosine</li>
	 * <li>cosh: hyperbolic cosine</li>
	 * <li>exp: euler's number raised to the power (e^x)</li>
	 * <li>floor: nearest lower integer</li>
	 * <li>log: logarithm natural (base e)</li>
	 * <li>log10: logarithm 10 natural(base e)</li>
	 * <li>sin: sine</li>
	 * <li>sinh: hyperbolic sine</li>
	 * <li>sqrt: square root</li>
	 * <li>tan: tangent</li>
	 * <li>tanh: hyperbolic tangent</li>
	 * </ul>
	 * <br/>
	 * It also recognizes the pi (or Pi) values; <br/>
	 * 
	 * @param title
	 *            title
	 * 
	 * @param name
	 *            definition
	 * 
	 * @param parse
	 *            parse or not. Do not parse when using parameters.
	 */
	public F1D(String title, String name, boolean parsed) {
		this(title, name, 0, 0, parsed);
	}

	/**
	 * Create a polynomial analytical function using a list of values. Example:
	 * pars[0]+pars[1]*x+pars[2]*x*x +pars[3]*x*x*x
	 * 
	 * @param title
	 *            Title of this function
	 * @param pars
	 *            array of coefficients for polynomial function
	 * @param parse
	 *            set true if it should be parsed
	 */

	public F1D(String title, double[] pars, boolean parse) {

		if (pars == null || pars.length < 1)
			System.err.println("Failed to evaluate this polynomial");

		String name = Double.toString(pars[0]);
		proxy = new FProxy(1, title, name, null, new double[] { 0, 0, 0, 0, 0,
				0 }, maxpoints, parse);

		for (int i = 1; i < pars.length; i++) {
			String sig = "+";
			if (pars[i] < 0)
				sig = "-";
			double val = Math.abs(pars[i]);
			String X = "*x";
			for (int j = 1; j < i; j++)
				X = X + "*x";
			name = name + sig + Double.toString(val) + X;

		}

		setTitle(title);
		lpp.setType(LinePars.F1D);
		function = new ExpressionBuilder(name);
		if (parse == true) {
			try {
				calc = (function.variables("x")).build();
			} catch (IllegalArgumentException e) {
				proxy.setParsed(false);
				// System.err.println("Failed to parse function " +
				// this.name+" Error:"+e1.toString());
				jhplot.utils.Util.ErrorMessage("Failed to parse function "
						+ name + " Error:" + e.toString());

			}

		}

	}

	/**
	 * Build a function. The function may have one independent variable: x.
	 * Function can have parameters, in which case set parsing to false. You can
	 * parse this function later after substitution of numeric parameter.
	 * <p>
	 * <b>Operators and functions</b><br/>
	 * <br/>
	 * the following operators are supported:
	 * <ul>
	 * <li>Addition: '2 + 2'</li>
	 * <li>Subtraction: '2 - 2'</li>
	 * <li>Multiplication: '2 * 2'</li>
	 * <li>Division: '2 / 2'</li>
	 * <li>Exponential: '2 ^ 2' or ** (raise to a power)</li>
	 * <li>Unary Minus,Plus (Sign Operators): '+2 - (-2)'</li>
	 * <li>Modulo: '2 % 2'</li>
	 * </ul>
	 * the following functions are supported:
	 * <ul>
	 * <li>abs: absolute value</li>
	 * <li>acos: arc cosine</li>
	 * <li>asin: arc sine</li>
	 * <li>atan: arc tangent</li>
	 * <li>cbrt: cubic root</li>
	 * <li>ceil: nearest upper integer</li>
	 * <li>cos: cosine</li>
	 * <li>cosh: hyperbolic cosine</li>
	 * <li>exp: euler's number raised to the power (e^x)</li>
	 * <li>floor: nearest lower integer</li>
	 * <li>log: logarithm natural (base e)</li>
	 * <li>sin: sine</li>
	 * <li>sinh: hyperbolic sine</li>
	 * <li>sqrt: square root</li>
	 * <li>tan: tangent</li>
	 * <li>tanh: hyperbolic tangent</li>
	 * </ul>
	 * <br/>
	 * <br/>
	 * 
	 * @param name
	 *            name
	 * @param parse
	 *            is parsed or not?
	 */
	public F1D(String name, boolean parse) {
		this(name, name, 999, 999, parse);
	}

	/**
	 * Parse the function.
	 * 
	 * @return true if parsed without problems.
	 **/
	public boolean parse() {
		try {
			function = new ExpressionBuilder(proxy.getName());
			function.variables("x");
			calc = function.build();
			proxy.setParsed(true);
		} catch (IllegalArgumentException e) {
			proxy.setParsed(false);
			// System.err.println("Failed to parse function " +
			// this.name+" Error:"+e.toString());
			jhplot.utils.Util.ErrorMessage("Failed to parse function "
					+ proxy.getName() + " Error:" + e.toString());
			return false;
		}

		return true;

	}

	/**
	 * Create a function in 1D. 500 points are used between Min and Max for
	 * evaluation.
	 * 
	 * The function may have x as independent variable.
	 * 
	 * 
	 * @param title
	 *            Title
	 * @param function
	 *            ExpressionBuilder
	 * @param min
	 *            Min value
	 * @param max
	 *            Max value
	 */
	public F1D(String title, ExpressionBuilder function) {
		proxy = new FProxy(1, title, null, null, new double[] { 0, 0, 0, 0, 0,
				0 }, maxpoints, false);
		this.function = function;
		lpp.setType(LinePars.F1D);

		boolean isParsed = parse();
		if (isParsed == false)
			jhplot.utils.Util.ErrorMessage("Failed to parse function " + title);

	}

	/**
	 * Create a function from the expression. The function is in the range.
	 * 
	 * @param calc
	 *            expression
	 * @param min
	 *            Min value
	 * @param max
	 *            Max value
	 */
	public F1D(Expression calc, double min, double max) {
		
		this("F1D",calc,min,max);
		
	}

	/**
	 * Create a function in 1D. 500 points are used between Min and Max for
	 * evaluation. The function may have x as independent variable.
	 * 
	 * 
	 * @param title
	 *            Title
	 * @param function
	 *            Expression after parsing and building
	 */
	public F1D(String title, Expression calc, double min, double max) {
		proxy = new FProxy(1, title, title, null, new double[] { min, max, 0, 0, 0,
				0 }, maxpoints, true);
		this.calc = calc;
		setTitle(title);
		lpp.setType(LinePars.F1D);
	}

	
	/**
	 * Create a function in 1D. 500 points are used between Min and Max for
	 * evaluation. The function may have x as independent variable.
	 * 
	 * 
	 * @param title
	 *            Title
	 * @param function
	 *            Expression after parsing and building
	 */
	public F1D(String title, Expression calc) {
		this(title,calc,0,0);
		
	}
	
	
	/**
	 * Create a function in 1D. The function may have x as independent variable.
	 * 
	 * @param function
	 *            expression
	 */
	public F1D(Expression calc) {
		this("F1D", calc,0,0);
	}

	/**
	 * Build a function.
	 * 
	 * @param function
	 *            input of expression
	 */
	public F1D(ExpressionBuilder function) {
		this("F1D", function);
	}

	/**
	 * Evaluate a function at a specific point in x
	 * 
	 * @param x
	 *            value in x for evaluation
	 * @return function value at x
	 */
	public double eval(double x) {

		double y = 0;
		IFunction iname = proxy.getIFunction();
		// jPlot function first
		if (iname == null && (calc == null || proxy.isParsed() == false)) {
			jhplot.utils.Util
					.ErrorMessage("eval(): Function was not parsed correctly!");
			return y;
		}

		// evaluate function
		if (iname == null && calc != null && proxy.isParsed() == true) {
			try {
				calc.setVariable("x", x);
				y = calc.evaluate();
			} catch (Exception e) {
				lastException = e.getMessage().toString();
				String ss1 = Double.toString(x);
				System.err.println("Failed to evaluate function:"
						+ proxy.getName() + " at x=" + ss1 + "\n"
						+ e.toString());

			}

			return y;
		} // end of the standard jPlot function

		// start AIDA function
		if (iname != null && iname.dimension() == 1) {
			try {
				double[] xx = new double[iname.dimension()];
				xx[0] = x;
				y = iname.value(xx);
			} catch (Exception e) {
				// System.out.println("Failed to evaluate function!");
				lastException = e.getMessage().toString();
				String ss1 = Double.toString(x);
				System.err.println("Failed to evaluate function:"
						+ proxy.getName() + " at x=" + ss1 + "\n"
						+ e.toString());
			}

			return y;

		} // end IFunction

		return y;
	}

	/**
	 * Evaluate a function for an array of x-values
	 * 
	 * @param x
	 *            array of values in x for evaluation
	 * @return array of function values
	 */
	public double[] eval(double[] x) {

		double[] y = new double[x.length];

		IFunction iname = proxy.getIFunction();

		// jPlot function first
		if (iname == null && (calc == null || proxy.isParsed() == false)) {
			jhplot.utils.Util
					.ErrorMessage("eval(): Function was not parsed correctly!");
			return y;
		}

		// evaluate function
		if (iname == null && calc != null && proxy.isParsed() == true) {

			for (int i = 0; i < x.length; i++) {

				try {

					calc.setVariable("x", x[i]);
					y[i] = calc.evaluate();

				} catch (Exception e) {

					String ss = Integer.toString(i);
					lastException = e.getMessage().toString() + " at position="
							+ ss;
					jhplot.utils.Util
							.ErrorMessage("eval(): Failed to evaluate:"
									+ proxy.getName() + " at position=" + ss);
					return null;

				}
			}

			return y;
		} // end of the standard jPlot function

		// start AIDA function
		if (iname != null) {

			for (int i = 0; i < x.length; i++) {
				try {
					double[] xx = new double[iname.dimension()];
					xx[0] = x[i];
					y[i] = iname.value(xx);
				} catch (Exception e) {
					String ss = Integer.toString(i);
					lastException = e.getMessage().toString() + " at position="
							+ ss;
					jhplot.utils.Util.ErrorMessage("Failed to evaluate:"
							+ proxy.getName() + " at position=" + ss);
				}
			}

			return y;

		} // end IFunction

		return y;
	}

	/**
	 * Evaluate a function for graphic representation. Number of points for
	 * evaluations is 500.
	 * 
	 * @param XMin
	 *            value in x
	 * @param XMax
	 *            value in x
	 */
	public void eval(double xMin, double xMax) {
		eval(xMin, xMax, maxpoints);

	}

	/**
	 * Evaluate a function for graphic representation. The function is assumed
	 * to me ranged (the range is defined during the initialization).
	 * 
	 */
	public void eval() {
		double d[] = proxy.getLimits();
		eval(d[0], d[1], maxpoints);

	}

	/**
	 * Evaluate a function for graphic representation. Number of points for
	 * evaluations is 500.
	 * 
	 * @param Min
	 *            value in x
	 * @param Max
	 *            value in x
	 * @param Number
	 *            of evaluation points
	 */
	public void eval(double min, double max, int Npoints) {

		int points = Npoints;
		IFunction iname = proxy.getIFunction();
		boolean isParsed = proxy.isParsed();

		if (iname == null && (calc == null || isParsed == false)) {
			jhplot.utils.Util
					.ErrorMessage("eval(): Function was not parsed correctly! Not parsed?");
			return;
		}

		if (iname == null && isParsed == true) {
			x = new double[points];
			y = new double[points];
			double d = (max - min) / (points - 1);
			for (int i = 0; i < points; i++) {
				x[i] = min + i * d;
				try {

					calc.setVariable("x", x[i]);
					y[i] = calc.evaluate();
					// System.out.println(x[i]);
					// System.out.println(y[i]);
				} catch (Exception e) {
					String ss = Double.toString(x[i]);
					System.err.println("Failed to evaluate:" + proxy.getName()
							+ " at position=" + ss);
					return;
				}

			}
		} // end of the standard jPlot function

		// start AIDA function
		if (iname != null) {

			x = new double[points];
			y = new double[points];

			double d = (max - min) / (points - 1);
			for (int i = 0; i < points; i++) {
				x[i] = min + i * d;
				double[] xx = new double[iname.dimension()];
				try {
					xx[0] = x[i];
					y[i] = iname.value(xx);
				} catch (Exception e) {
					String ss = Double.toString(x[i]);
					System.err.println("Failed to evaluate at x=" + ss);
					return;
				}

			}

		} // end IFunction

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
	 * Create a F1D function from JAIDA IFunction. By default, 500 points for
	 * evaluation are used. No ranges are set.
	 * 
	 * @param iname
	 *            input IFunction
	 */
	public F1D(IFunction iname) {

		proxy = new FProxy(1, iname.title(), null, iname, new double[] { 0, 0,
				0, 0, 0, 0 }, maxpoints, true);
		setTitle(iname.title());
		lpp.setType(LinePars.F1D);

	}

	/**
	 * Create F1D function from JAIDA IFunction. By default 500 points are used
	 * 
	 * @param title
	 *            Title
	 * @param iname
	 *            input IFunction
	 * @param min
	 *            Min X values
	 * @param max
	 *            Max X values
	 */

	public F1D(String title, IFunction iname) {
		proxy = new FProxy(1, title, null, iname, new double[] { 0, 0, 0, 0, 0,
				0 }, maxpoints, true);
		setTitle(title);
		lpp.setType(LinePars.F1D);

	}

	/**
	 * Print the F1D function to a Table in a separate Frame. The numbers are
	 * formatted to scientific format. One can sort and search the data in this
	 * table (data cannot be modified)
	 * 
	 * @param min
	 * @param max
	 */
	public void toTable() {

		new HTable(this);

	}

	/**
	 * Replace abstract parameter with the value (double). Case sensitive!
	 * 
	 * @param parameter
	 *            parameter name
	 * @param value
	 *            value to be inserted
	 */

	public void setPar(String parameter, double value) {
		String s1 = Double.toString(value);
		String name = proxy.getName();
		proxy.setName(name.replaceAll(parameter, s1));
	}

	/**
	 * Return H1D histogram from F1D function. The number of points are given by
	 * setPoints() method, but the default 500 is used if not given. Min and Max
	 * values are given during the function initialisation (ranged function) The
	 * number of points is 500 by default.
	 * 
	 * @return histogram
	 */

	public H1D getH1D() {

		double[] d = proxy.getLimits();
		return getH1D(d[0], d[1]);
	}

	/**
	 * Return H1D histogram from F1D function. The number of points are given by
	 * setPoints() method, but the default 500 is used if not given. Min and Max
	 * values are given by the values used to parse the function.
	 * 
	 * @param min
	 *            value
	 * @param max
	 *            value
	 * 
	 * @return histogram
	 */

	public H1D getH1D(double min, double max) {

		int bins = getPoints();
		eval(min, max, bins);
		H1D h = new H1D(getTitle(), bins, min, max);
		int ibins = bins + 2;
		double[] newHeights = new double[ibins];
		double[] newErrors = new double[ibins];
		double[] newMeans = new double[ibins];
		double[] newRmss = new double[ibins];
		int[] newEntries = new int[ibins];
		newHeights[0] = 0;
		newHeights[ibins - 1] = 0;

		for (int i = 0; i < ibins - 2; i++) {
			newHeights[i + 1] = y[i];
			newErrors[i + 1] = 0;
			newEntries[i + 1] = (int) y[i];
			newMeans[i + 1] = y[i];
			newRmss[i + 1] = 0;
		}

		h.setContents(newHeights, newErrors, newEntries, newMeans, newRmss);
		h.setMeanAndRms(0, 0);

		/*
		 * for (int i = 0; i < bins; i++) { h.fill(x[i]+0.5*d, y[i] ); }
		 */

		return h;
	}

	/**
	 * Return a Histogram given by the F1D function. All statistical
	 * characteristics of such histogram are meaningless. Bins and Min and Max
	 * values are user defined. The function is evaluated at the bin center
	 * which is important for small number of bins.
	 * 
	 * @param hname
	 *            Name of the histogram
	 * @param bins
	 *            number of bins for histogram
	 * @param hmin
	 *            min value of histogram
	 * @param hmax
	 *            max value of histogram
	 * @return H1D histogram
	 */
	public H1D getH1D(String hname, int bins, double hmin, double hmax) {

		double d = (hmax - hmin) / (double) bins;
		double[] xx = new double[bins];

		for (int i = 0; i < bins; i++) {
			xx[i] = hmin + i * d + 0.5 * d;
		}
		double[] yy = eval(xx);

		H1D h = new H1D(hname, bins, hmin, hmax);
		int ibins = bins + 2;
		double[] newHeights = new double[ibins];
		double[] newErrors = new double[ibins];
		double[] newMeans = new double[ibins];
		double[] newRmss = new double[ibins];
		int[] newEntries = new int[ibins];
		newHeights[0] = 0;
		newHeights[ibins - 1] = 0;

		for (int i = 0; i < ibins - 2; i++) {
			newHeights[i + 1] = yy[i];
			newErrors[i + 1] = 0;
			newEntries[i + 1] = (int) yy[i];
			newMeans[i + 1] = yy[i];
			newRmss[i + 1] = 0;
		}

		h.setContents(newHeights, newErrors, newEntries, newMeans, newRmss);
		h.setMeanAndRms(0, 0);

		return h;

	}

	/**
	 * Replace abstract parameter with the value (integer). Case sensitive. You
	 * will need to call parse() to finish this function.
	 * 
	 * @param parameter
	 *            parameter name
	 * @param value
	 *            integer value to be inserted.
	 */

	public void setPar(String parameter, int value) {
		String s1 = Integer.toString(value);
		String name = proxy.getName();
		proxy.setName(name.replaceAll(parameter, s1));
	}

	/**
	 * Get value in X-axis
	 * 
	 * @param i
	 *            index
	 * 
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
	 * Get the name of the function used for evaluation
	 * 
	 * @return Name
	 */
	public String getName() {
		return proxy.getName();

	}

	/**
	 * Return parsed function. One can evaluate Y as: y =function.getResult(x),
	 * where function is what returned by this method.
	 * 
	 * @return function
	 **/
	public Expression getParse() {
		return calc;
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
	 * Integral using fastest trapezium rule method. This function return
	 * non-zero if it the range was defined during the initialization. The
	 * default number of points is 500. Increase it if needed more pecision.
	 * 
	 * @return integral in the range defined during the initisliazation.
	 * 
	 */
	public double integral() {
		int points = proxy.getPoints();
		double[] d = proxy.getLimits();
		return integral("trapezium", points, d[0], d[1]);
	}

	/**
	 * Integral using fastest trapezium rule method. It uses the default number
	 * of points (500).
	 * 
	 * @param min
	 *            the first ordinate.
	 * @param max
	 *            the last ordinate.
	 */
	public double integral(double min, double max) {
		int points = proxy.getPoints();
		return integral("trapezium", points, min, max);
	}

	/**
	 * Numerical integration. Define types as:<br>
	 * type="gauss4" - Gaussian integration formula (4 points)<br>
	 * type="gauss8" - Gaussian integration formula (8 points)<br>
	 * type="richardson" - Richardson extrapolation <br>
	 * type="simpson" - using Simpson's rule. <br>
	 * type="trapezium" - trapezium rule. <br>
	 * 
	 * @param type
	 *            type of algorithm. Can be:
	 *            "gauss4","gauss8","richardson","simpson","trapezium".
	 * @param N
	 *            the number of strips to use for integration
	 * @param min
	 *            the first ordinate.
	 * @param max
	 *            the last ordinate.
	 * @return integral
	 */
	public double integral(String type, final int N, double min,
			final double max) {

		if (type == "gauss4") {
			return jhplot.math.Numeric.gaussian4(N, this, min, max);
		} else if (type == "gauss8") {
			return jhplot.math.Numeric.gaussian8(N, this, min, max);
		} else if (type == "richardson") {
			return jhplot.math.Numeric.richardson(N, this, min, max);
		} else if (type == "simpson") {
			return jhplot.math.Numeric.simpson(N, this, min, max);
		} else if (type == "trapezium") {
			return jhplot.math.Numeric.trapezium(N, this, min, max);

		} else {
			return jhplot.math.Numeric.gaussian4(N, this, min, max);

		}

	}

	/**
	 * Numerical integration using trapezium rule.
	 * 
	 * @param N
	 *            the number of strips to use for integration
	 * @param min
	 *            the first ordinate.
	 * @param max
	 *            the last ordinate.
	 * @return integral
	 */
	public double integral(final int N, double min, final double max) {

		return jhplot.math.Numeric.trapezium(N, this, min, max);

	}

	/**
	 * Get Jaida function
	 * 
	 * @return
	 */
	public IFunction getIFunction() {
		return proxy.getIFunction();

	}

	/**
	 * Get array of X-values after function after evaluation using the default
	 * number of points
	 * 
	 * @return X-values
	 */

	public double[] getArrayX() {
		return x;
	}

	/**
	 * Get array of Y-values after function after evaluation using the default
	 * number of points
	 * 
	 * @return Y-values
	 */

	public double[] getArrayY() {
		return y;
	}

	/**
	 * If the function is parsed correctly, return true. Use this check before
	 * drawing it.
	 * 
	 * @return true if parsed.
	 */
	public boolean isParsed() {

		return proxy.isParsed();
	}

	/**
	 * Convert the function into MathML form.
	 * 
	 * @return String representing this function in MathML.
	 */

	public String toMathML() {

		try {
			return jscl.math.Expression.valueOf(proxy.getName()).toMathML();
		} catch (Exception e) {
			lastException = e.getMessage().toString();
			return "";
		}

	}

	/**
	 * Convert the function into Java code.
	 * 
	 * @return String representing this function in Java.
	 */

	public String toJava() {

		try {
			return jscl.math.Expression.valueOf(proxy.getName()).toJava();
		} catch (Exception e) {
			lastException = e.getMessage().toString();
			return "";
		}

	}

	/**
	 * Try to simplify this function. It is often useful to rewrite an
	 * expression in term of elementary functions (log, exp, frac, sqrt,
	 * implicit roots), using the "elementary()" before simplifying it. Retrieve
	 * the simplified name as a string using getName() method.
	 * 
	 * @return false if error occurs. Retrieve this error as a string using
	 *         getException().
	 */

	public boolean simplify() {

		String name = proxy.getName();
		try {
			name = jscl.math.Expression.valueOf(name).simplify().toString();
			proxy.setName(name);
		} catch (Exception e) {
			lastException = e.getMessage().toString();
			return false;
		}
		return true;

	}

	/**
	 * Convert this function rewrite in term of elementary functions (log, exp,
	 * frac, sqrt, implicit roots) This is useful before simplifying function.
	 * Retrieve the simplified name as a string using getName() method.
	 * 
	 * @return false if error occurs. Retrieve this error as a string using
	 *         getException().
	 */

	public boolean elementary() {
		String name = proxy.getName();
		try {
			name = jscl.math.Expression.valueOf(name).elementary().toString();
			proxy.setName(name);
		} catch (Exception e) {
			lastException = e.getMessage().toString();
			return false;
		}
		return true;

	}

	/**
	 * Convert this function rewrite in expanded form. Retrieve the expanded
	 * name as a string using getName() method.
	 * 
	 * @return false if error occurs. Retrieve this error as a string using
	 *         getException().
	 */

	public boolean expand() {
		String name = proxy.getName();
		try {
			name = jscl.math.Expression.valueOf(name).expand().toString();
			proxy.setName(name);
		} catch (Exception e) {
			lastException = e.getMessage().toString();
			return false;
		}
		return true;

	}

	/**
	 * Convert this function rewrite in factorized form (if can). Retrieve the
	 * expanded name as a string using getName() method.
	 * 
	 * @return false if error occurs. Retrieve this error as a string using
	 *         getException().
	 */

	public boolean factorize() {
		String name = proxy.getName();
		try {
			name = jscl.math.Expression.valueOf(name).factorize().toString();
			proxy.setName(name);
		} catch (Exception e) {
			lastException = e.getMessage().toString();
			return false;
		}
		return true;

	}

	/**
	 * Perform some numeric substitutions. Examples: exp(1) should be
	 * 2.71828182, "pi" should be 3.14159 etc. Retrieve the expanded name as a
	 * string using getName() method.
	 * 
	 * @return false if error occurs. Retrieve this error as a string using
	 *         getException().
	 */

	public boolean numeric() {
		String name = proxy.getName();
		try {
			name = jscl.math.Expression.valueOf(name).numeric().toString();
			proxy.setName(name);
		} catch (Exception e) {
			lastException = e.getMessage().toString();
			return false;
		}
		return true;

	}

	/**
	 * Numerical differentiation.
	 * 
	 * @param N
	 *            the number of points to use.
	 * @param min
	 *            the first ordinate.
	 * @param max
	 *            the last ordinate.
	 * @return array with differentials
	 */
	public double[] differentiate(final int N, final double min,
			final double max) {
		return jhplot.math.Numeric.differentiate(N, this, min, max);

	}

	/**
	 * Numerical differentiation of a function. Range of the function is given
	 * during the initisalisation (ranged function)
	 * 
	 * @return array with differentials
	 */
	public double[] differentiate() {
		int points = proxy.getPoints();
		double[] d = proxy.getLimits();
		return jhplot.math.Numeric.differentiate(points, this, d[0], d[1]);

	}

	/**
	 * Get the number of points used for plotting, integration and
	 * differentiation.
	 * 
	 * @return Number of points
	 */

	public int getPoints() {
		return proxy.getPoints();
	}

	/**
	 * If error occurs at some step, this is the way to retrieve it.
	 * 
	 * @return last exception happened in any method of this class.
	 */
	public String getException() {
		return lastException;
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
		if (d != null)
			return d[0];
		return 0;

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
	 * Set proxy function
	 * 
	 * @param f
	 */
	public void set(FProxy f) {
		proxy = f;
	}

	/**
	 * Get the maximum value in X
	 * 
	 * @return Maximal value
	 */
	public double getMax() {
		double[] d = proxy.getLimits();
		if (d != null)
			return d[1];
		return 0;
	}

	/**
	 * Get this function as a string.
	 * 
	 * @return Convert to string.
	 */
	public String toString() {
		String tmp = "F1D:" + proxy.getName();
		tmp = tmp + " (title=" + proxy.getTitle() + ", n="
				+ Integer.toString(proxy.getPoints()) + ", "
				+ Boolean.toString(proxy.isParsed()) + ")";
		return tmp;
	}

}

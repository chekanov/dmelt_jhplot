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

import jhplot.math.exp4j.*;
import hep.aida.IFunction;
import jhplot.gui.HelpBrowser;

/**
 * Create 2D function. The function may have up to 2 independent variables: x,y.
 * 
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
 * 
 * @author S.Chekanov
 * 
 */

public class F2D extends DrawOptions {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	

	private Expression calc = null;
	private ExpressionBuilder function = null;

	final int maxpoints = 200;
	private FProxy proxy;
	private String lastException = "";

	/**
	 * Create a function in 2D for evaluation. The function is not in range.
	 * 
	 * The function may have up to 2 independent variables: x,y.
	 * 
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
	 * It also recognizes the pi (or Pi) values; <br/>
	 * 
	 * 
	 * @param name
	 *            String representing the function
	 */
	public F2D(String name) {
		this(name, name, 0.0, 0.0, 0.0, 0.0);
	}

	/**
	 * Create new function.
	 * 
	 * @param title
	 *            title
	 * @param name
	 *            definition
	 */
	public F2D(String title, String name) {
		this(title, name, 0.0, 1.0, 0.0, 1.0);
	}

	/**
	 * Create a function in 2D. This is a ranged function. Uses 500 points
	 * between min and max value for evaluation. The function may have up to 2
	 * independent variables in it (x,y).
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
	 * <br/>
	 * 
	 * 
	 * 
	 * @param name
	 *            String representing the function.
	 * @param Xmin
	 *            Min value in X
	 * @param Xmax
	 *            Max value in X
	 * @param Ymin
	 *            Min value in Y
	 * @param Ymax
	 *            Max value in Y
	 */
	public F2D(String title, String name, double Xmin, double Xmax,
			double Ymin, double Ymax) {

		is3D = true;
		proxy = new FProxy(2,title, name, null,  new double[]{Xmin,Xmax,Ymin,Ymax,0,0}, maxpoints, true); 
		setTitle(title);
		function = new ExpressionBuilder(proxy.getName());
		try {
			function.variables("x", "y");
			calc = function.build();
		} catch (IllegalArgumentException e) {
			proxy.setParsed(false);
			jhplot.utils.Util.ErrorMessage("Failed to parse function "
					+ name + " Error:" + e.toString());

		}

	}

	/**
	 * Build a 2D function. Title is set to the name. This is ranged function.
	 * 
	 * @param name
	 *            Name
	 * @param Xmin
	 *            X-min
	 * @param Xmax
	 *            X-max
	 * @param Ymin
	 *            Y-min
	 * @param Ymax
	 *            Y-max
	 */
	public F2D(String name, double Xmin, double Xmax, double Ymin, double Ymax) {

		this(name, name, Xmin, Xmax, Ymin, Ymax);

	}

	/**
	 * Create a F2D function from JAIDA IFunction. By default, 500 points for
	 * evaluation are used. Ranged function.
	 * 
	 * @param title
	 *            Title
	 * @param name
	 *            new function name
	 * @param iname
	 *            input IFunction
	 * @param Xmin
	 *            Min X value
	 * @param Xmax
	 *            Max X value
	 * 
	 * @param Ymin
	 *            Min Y value
	 * @param Ymax
	 *            Max Y value
	 */
	public F2D(String title, IFunction iname, double Xmin, double Xmax,
			double Ymin, double Ymax) {
	proxy = new FProxy(2,title, title, iname,  new double[]{Xmin,Xmax,Ymin,Ymax,0,0}, maxpoints, false); 
        setTitle(title);

	}

	/**
	 * Create a function in 2D. 500 points are used between Min and Max for
	 * evaluation. The function may have x as independent variable. Make sure
	 * that expression has 2 variables, x and y.
	 * 
	 * @param title
	 *            Title
	 * @param function
	 *            Expression after parsing and building
	 * @param Xmin
	 *            Min X value
	 * @param Xmax
	 *            Max X value
	 * 
	 * @param Ymin
	 *            Min Y value
	 * @param Ymax
	 *            Max Y value
	 */
	public F2D(String title, Expression calc, double Xmin, double Xmax,
			double Ymin, double Ymax) {
			
		proxy = new FProxy(1,title, title, null,  new double[] {Xmin,Xmax,Ymin,Ymax,0,0}, maxpoints, true);
		this.calc = calc;	
		setTitle(title);
	
	}

	
	
	
	
	
	
	
	
	
	/**
	 * Create a function in 2D. 500 points are used between Min and Max for
	 * evaluation. The function may have x as independent variable. Make sure
	 * that expression has 2 variables, x and y.
	 * 
	 * @param title
	 *            Title
	 * @param function
	 *            Expression after parsing and building
	 * @param Xmin
	 *            Min X value
	 * @param Xmax
	 *            Max X value
	 * 
	 * @param Ymin
	 *            Min Y value
	 * @param Ymax
	 *            Max Y value
	 */
	public F2D(Expression calc, double Xmin, double Xmax, double Ymin,
			double Ymax) {
		this("F2D", calc, Xmin, Xmax, Ymin, Ymax);
	}

	/**
	 * Create a F2D function from JAIDA IFunction. By default, 500 points for
	 * evaluation are used. Unranged function.
	 * 
	 * @param iname
	 */
	public F2D(IFunction iname) {

		this("IFunction", iname, 0, 0, 0, 10);

	}

	

	/**
	 * Initialize function from proxy.
	 * @param f
	 */
	
	public F2D(FProxy f) {
		
		if (f.getType() != 2) {
			jhplot.utils.Util.ErrorMessage("Error in parsing F2D. Wrong function type! " + f.getName());
			return;
		}
		
		proxy=f;
		setTitle(proxy.getTitle());

	}
	
	/**
	 * Create a F2D function from JAIDA IFunction.
	 * 
	 * @param iname
	 *            input IFunction
	 * @param Xmin
	 *            Min X value
	 * @param Xmax
	 *            Max X value
	 * @param Ymin
	 *            Min Y value
	 * @param Ymax
	 *            Max Y value
	 */
	public F2D(IFunction iname, double Xmin, double Xmax, double Ymin,
			double Ymax) {
		this(iname.title(), iname, Xmin, Xmax, Ymin, Ymax);
	}

	/**
	 * Evaluate a function at a specific point in (x,y)
	 * 
	 * @param x
	 *            value in x for evaluation
	 * @param y
	 *            value in y for evaluation
	 * @return function value at (x,y)
	 */
	public double eval(double x, double y) {

		double z = 0;
		  IFunction iname=proxy.getIFunction();
		  boolean  isParsed=proxy.isParsed();
		  String name=proxy.getName();
		  
		// jPlot function first
		if (iname == null && (calc == null || isParsed == false)) {
			jhplot.utils.Util
					.ErrorMessage("eval(x,y): Function was not parsed correctly!");
			return z;
		}

		// evaluate function
		if (iname == null && calc != null && isParsed == true) {
			try {

				calc.setVariable("x", x);
				calc.setVariable("y", y);
				z = calc.evaluate();

			} catch (Exception e) {
				String ss1 = Double.toString(x);
				String ss2 = Double.toString(y);
				System.err.println("Failed to evaluate " + name
						+ " at position=(" + ss1 + "," + ss2 + ")");
				return Double.MAX_VALUE;
			}
		}

		// start AIDA function
		if (iname != null && iname.dimension() == 2) {
			try {
				double[] xx = new double[iname.dimension()];
				xx[0] = x;
				xx[1] = y;
				z = iname.value(xx);
			} catch (Exception e) {
				String ss1 = Double.toString(x);
				String ss2 = Double.toString(y);
				System.err.println("Failed to evaluate " + name
						+ " at position=(" + ss1 + "," + ss2 + ")");
				return Double.MAX_VALUE;

			}

		} // end IFunction

		return z;
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
	 * Evaluate a function for an array of x-values
	 * 
	 * @param x
	 *            array of values in x for evaluation
	 * @param y
	 *            array of values in y for evaluation
	 * 
	 * @return array of function values
	 */
	public double[][] eval(double[] x, double[] y) {
		IFunction iname=proxy.getIFunction();
        boolean  isParsed=proxy.isParsed();
        String name=proxy.getName();

		double[][] z = new double[x.length][y.length];
		String err = "";

		// jPlot function first
		if (iname == null && (calc == null || isParsed == false)) {
			jhplot.utils.Util
					.ErrorMessage("eval(x[],y[]): Function was not parsed correctly!");
			return z;
		}

		// evaluate function
		if (iname == null && calc != null && isParsed == true) {

			for (int i = 0; i < x.length; i++)
				for (int j = 0; j < y.length; j++) {

					try {

						calc.setVariable("x", x[i]);
						calc.setVariable("y", y[i]);
						z[i][j] = calc.evaluate();

					} catch (Exception e) {
						String ss1 = Double.toString(x[i]);
						String ss2 = Double.toString(y[j]);
						err = "Failed to evaluate " + name + " at position=("
								+ ss1 + "," + ss2 + ")";

						break;

					}
				}

		} // end of the standard jPlot function

		// start AIDA function
		if (iname != null) {

			for (int i = 0; i < x.length; i++) {
				for (int j = 0; j < y.length; j++) {

					try {
						double[] xx = new double[iname.dimension()];
						xx[0] = x[i];
						xx[1] = y[j];
						z[i][j] = iname.value(xx);
					} catch (Exception e) {
						String ss1 = Double.toString(x[i]);
						String ss2 = Double.toString(y[j]);
						err = "Failed to evaluate AIDA function " + name
								+ " at position=(" + ss1 + "," + ss2 + ")";

						break;

					}
				}
			}

		} // end IFunction

		if (err.length() > 1) {
			jhplot.utils.Util.ErrorMessage(err);
			return z;
		}
		;

		return z;
	}

	/**
	 * Set Min in X
	 * 
	 * @param min
	 *            Min value
	 */
	public void setMinX(double min) {
		 proxy.setLimit(0,min);
		

	}

	/**
	 * Parse the function.
	 * 
	 * @return true if parsed without problems.
	 **/
	public boolean parse() {
		boolean  isParsed=proxy.isParsed();
		String name=proxy.getName();
		try {
			function = new ExpressionBuilder(name);
			calc = (function.variables("x", "y")).build();
			isParsed = true;
		} catch (IllegalArgumentException e) {
			isParsed = false;
			// System.err.println("Failed to parse function " +
			// this.name+" Error:"+e.toString());
			jhplot.utils.Util.ErrorMessage("Failed to parse function "
					+ name + " Error:" + e.toString());

		}

		return isParsed;

	}

	/**
	 * Get Min value in X
	 * 
	 * @return Min value in X
	 */
	public double getMinX() {
		double[] d= proxy.getLimits();
		return d[0];
	}

	/**
	 * Set Min value in Y
	 * 
	 * @param min
	 *            Min value in Y
	 */
	public void setMinY(double min) {
		 proxy.setLimit(2,min);

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
	 * Get Min value in Y
	 * 
	 * @return Min value in Y
	 */

	public double getMinY() {
		double[] d= proxy.getLimits();
		return d[2];
	}

	/**
	 * Set Max value in X
	 * 
	 * @param max
	 *            Max value in X
	 */
	public void setMaxX(double max) {
		proxy.setLimit(1,max);

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
	 * Get Max value in X
	 * 
	 * @return Max value in X
	 */
	public double getMaxX() {
		double[] d= proxy.getLimits();
		return d[1];

	}

	/**
	 * Set Max value in Y
	 * 
	 * @param max
	 *            Max value in Y
	 */

	public void setMaxY(double max) {
		proxy.setLimit(1,max);

	}

	/**
	 * Get Max value in Y
	 * 
	 * @return Max value in Y
	 */
	public double getMaxY() {
		double[] d= proxy.getLimits();
		return d[3];

	}

	
	
	/**
	 * Get the number of points
	 * 
	 * @param bins
	 *            Number of points
	 */
	public void setPoints(int bins) {
		proxy.setPoints(bins);

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
		String name=proxy.getName();
		proxy.setName(name.replaceAll(parameter, s1));
	}

	

	/**
	 * Get the number of points for evaluation of a function
	 * 
	 * @return Number of points
	 */
	public int getPoints() {
		return proxy.getPoints();

	}

	/**
	 * Numerical integration using trapezium rule.
	 * 
	 * @param N
	 *            the number of strips to use for integration (in X and Y the
	 *            same)
	 * @param minX
	 *            the first ordinate in X.
	 * @param maxX
	 *            the last ordinate in X.
	 * @param minY
	 *            the first ordinate in X.
	 * @param maxY
	 *            the last ordinate in Y.
	 * 
	 * @return integral
	 */
	public double integral(final int N, double minX, final double maxX,
			double minY, final double maxY) {

		return jhplot.math.Numeric.trapezium2D(N, this, minX, maxX, minY, maxY);

	}

	/**
	 * Integral using fastest trapezium rule method. It uses the default number
	 * of points (500).
	 * 
	 * @param minX
	 *            the first ordinate in X.
	 * @param maxX
	 *            the last ordinate in X.
	 * @param minY
	 *            the first ordinate in X.
	 * @param maxY
	 *            the last ordinate in Y.
	 */
	public double integral(double minX, final double maxX, double minY,
			final double maxY) {
		
		int points=proxy.getPoints();
		return integral(points, minX, maxX, minY, maxY);
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
	 * If error occurs at some step, this is the way to retrieve it.
	 * 
	 * @return last exception happened in any method of this class.
	 */
	public String getException() {
		return lastException;
	}

	
	/**
	 * Return parsed function. One can evaluate it as "calculate()".
	 * 
	 * @return function
	 **/
	public Expression getParse() {
		return calc;
	}

	/**
	 * Generate a 2D histogram from the F2D function using a custom number of
	 * bins and given min and max values.
	 * 
	 * @param hname
	 *            histogram name
	 * @param hbinX
	 *            number of X bins
	 * @param hminX
	 *            min value in X
	 * @param hmaxX
	 *            max value in X
	 * @param hbinY
	 *            number of bins in Y
	 * @param hminY
	 *            min value in Y
	 * @param hmaxY
	 *            max value in Y
	 * @return 2D histogram from the function
	 */

	public H2D getH2D(String hname, int hbinX, double hminX, double hmaxX,
			int hbinY, double hminY, double hmaxY) {

		double dX = (hmaxX - hminX) / (double) (hbinX);
		double dY = (hmaxY - hminY) / (double) (hbinY);

		double[] xx = new double[hbinX];
		double[] yy = new double[hbinY];

		for (int i = 0; i < hbinX; i++) {
			xx[i] = hminX + i * dX + 0.5 * dX;
		}

		for (int i = 0; i < hbinY; i++) {
			yy[i] = hminY + i * dY + 0.5 * dY;
		}

		double[][] zz = eval(xx, yy);

		int ibinsX = hbinX + 2;
		int ibinsY = hbinY + 2;

		double[][] newHeights = new double[ibinsX][ibinsY];
		double[][] newErrors = new double[ibinsX][ibinsY];
		double[][] newMeansX = new double[ibinsX][ibinsY];
		double[][] newRmssX = new double[ibinsX][ibinsY];
		double[][] newMeansY = new double[ibinsX][ibinsY];
		double[][] newRmssY = new double[ibinsX][ibinsY];
		int[][] newEntries = new int[ibinsX][ibinsY];

		newHeights[0][0] = 0;
		newHeights[ibinsX - 1][ibinsY - 1] = 0;

		int n = 0;
		for (int i = 0; i < hbinX; i++) {
			for (int j = 0; j < hbinY; j++) {

				int i1 = i + 2;
				int j1 = j + 2;
				newHeights[i1][j1] = zz[i][j];
				newErrors[i1][j1] = 0;
				newEntries[i1][j1] = (int) zz[i][j];
				newMeansX[i1][j1] = zz[i][j];
				newRmssX[i1][j1] = 0;
				newMeansY[i1][j1] = zz[i][j];
				newRmssY[i1][j1] = 0;
				n = n + (int) zz[i][j];
			}
		}

		H2D hnew = new H2D(hname, hbinX, hminX, hmaxX, hbinY, hminY, hmaxY);

		hnew.setContents(newHeights, newErrors, newEntries, newMeansX,
				newMeansY, newRmssX, newRmssY);
		hnew.setMeanX(0);
		hnew.setMeanY(0);
		hnew.setRmsX(0);
		hnew.setRmsY(0);
		hnew.setNEntries(n);

		return hnew;
	}

	

	
	
	
	/**
	 * Integral using fastest trapezium rule method. 
	 * This function return non-zero if it the range was defined during the initialization.
	 * The default number of points is 500. Increase it if needed more precision.
	 * 
	 * @return integral in the range defined during the initialization.
	 * 
	 */
	public double integral() {
		double[] d= proxy.getLimits();
		double Xmin=d[0];
		double Xmax=d[1];
		double Ymin=d[2];
		double Ymax=d[3];
		int points=proxy.getPoints();

		return integral(points,points,Xmin, Xmax,Ymin,Ymax);
	}
	
	
	
	
	/**
	 * Finds the volume under the surface described by the function f(x, y) for
	 * a <= x <= b, c <= y <= d. It uses the trapezoid rule. Using xSegs number of
	 * segments across the x axis and ySegs number of segments across the y
	 * axis.
	 * 
	 * @param xSegs
	 *            The number of segments in the x axis.
	 * @param ySegs
	 *            The number of segments in the y axis.
	 * @param minX
	 *            The lower bound of x.
	 * @param maxX
	 *            The upper bound of x.
	 * @param minX
	 *            The lower bound of y.
	 * @param maxY
	 *            The upper bound of y.
	 * @return The volume under the function f(x, y).
	 */
	public double integral(int xSegs, int ySegs, double minX, double maxX, double minY,
			double maxY) {
		double xSegSize = (maxX - minX) / xSegs; // length of an x segment.
		double ySegSize = (maxY - minY) / ySegs; // length of a y segment.
		double volume = 0; // volume under the surface.
		for (int i = 0; i < xSegs; i++) {
			for (int j = 0; j < ySegs; j++) {
				double height = eval(minX + (xSegSize * i), minY
						+ (ySegSize * j));
				height += eval(minX + (xSegSize * (i + 1)), minY
						+ (ySegSize * j));
				height += eval(minX + (xSegSize * (i + 1)), minY
						+ (ySegSize * (j + 1)));
				height += eval(minX + (xSegSize * i), minY
						+ (ySegSize * (j + 1)));
				height /= 4;
				// height is the average value of the corners of the current
				// segment.
				// We can use the average value since a box of this height has
				// the same volume as the original segment shape.
				// Add the volume of the box to the volume.
				volume += xSegSize * ySegSize * height;
			}
		}

		return volume;
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
    * Get the proxy of this function used for serialization 
    * and non-graphical representations.
    * 
    * @param proxy proxy of this function. 
    */
    public FProxy get(){ 
       return proxy;
   }
	
	/**
	 * Get this function as a string.
	 * 
	 * @return Convert to string.
	 */
	public String toString() {

		String tmp = "F2D:" + proxy.getName();
		double[] d = proxy.getLimits();
		boolean isParsed = proxy.isParsed();
		double Xmin = d[0];
		double Xmax = d[1];
		double Ymin = d[2];
		double Ymax = d[3];
		int points = proxy.getPoints();
		tmp = tmp + " (title=" + getTitle() + ", n=" + Integer.toString(points)
				+ ", minX=" + Double.toString(Xmin) + ", maxX="
				+ Double.toString(Xmax) + ", minY=" + Double.toString(Ymin)
				+ ", maxY=" + Double.toString(Ymax) + ", "
				+ Boolean.toString(isParsed) + ")";
		return tmp;
	}

}

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

import de.congrace.exp4j.Calculable;
import de.congrace.exp4j.ExpressionBuilder;
import de.congrace.exp4j.UnknownFunctionException;
import de.congrace.exp4j.UnparsableExpressionException;
import hep.aida.IFunction;
import java.io.Serializable;
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
 * <li>log: logarithmus naturalis (base e)</li>
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

public class F2D extends DrawOptions implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private double Xmin;

	private double Xmax;

	private double Ymin;

	private double Ymax;

	private int points;

	private String name;

	private Calculable calc = null;

	private ExpressionBuilder function = null;

	private boolean isParsed = false;

	private IFunction iname = null;

	/**
	 * Create a function in 2D for evaluation.
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
	 * <li>log: logarithmus naturalis (base e)</li>
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
		this(name, name, 0.0, 1.0, 0.0, 1.0);
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
	 * Create a function in 2D. Uses 500 points between min and max value for
	 * evaluation. The function may have up to 2 independent variables in it
	 * (x,y).
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
	 * <li>log: logarithmus naturalis (base e)</li>
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
		
		is3D=true;
		this.name = name;
		this.name = this.name.replace("**", "^"); // preprocess power
		this.name = this.name.replace("pi", "3.14159265");
		this.name = this.name.replace("Pi", "3.14159265");

		this.title = title;
		this.points = 300;
		this.Xmin = Xmin;
		this.Xmax = Xmax;
		this.Ymin = Ymin;
		this.Ymax = Ymax;
		setTitle(this.title);
		function = new ExpressionBuilder(this.name);
		try {
			calc = function.withVariableNames("x", "y").build();
			isParsed = true;
                 } catch (UnknownFunctionException  e) {
                                isParsed = false;
                                jhplot.utils.Util.ErrorMessage("Failed to parse function " + this.name+" Error:"+e.toString());
                        } catch (UnparsableExpressionException e) {
                                isParsed = false;
                                 jhplot.utils.Util.ErrorMessage("Failed to parse function " + this.name+" Error:"+e.toString());
                        }


	}

	/**
	 * Build a 2D function. Title is set to the name.
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
	 * evaluation are used
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

		this.title = title;
		this.name = title;
		this.iname = iname;
		this.points = 300;
		this.Xmin = Xmin;
		this.Xmax = Xmax;
		this.Ymin = Ymin;
		this.Ymax = Ymax;

	}

	/**
	 * Create a F2D function from JAIDA IFunction. By default, 500 points for
	 * evaluation are used.
	 * 
	 * @param iname
	 */
	public F2D(IFunction iname) {

		this("IFunction", iname, 0, 1, 0, 1);

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

		// jPlot function first
		if (iname == null && (function == null || isParsed == false)) {
			jhplot.utils.Util
					.ErrorMessage("eval(): Function was not parsed correctly!");
			return z;
		}

		// evaluate function
		if (iname == null && function != null && isParsed == true) {
			try {

				calc.setVariable("x", x);
				calc.setVariable("y", y);
				z = calc.calculate();

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

		double[][] z = new double[x.length][y.length];
		String err = "";

		// jPlot function first
		if (iname == null && (function == null || isParsed == false)) {
			jhplot.utils.Util
					.ErrorMessage("eval(): Function was not parsed correctly!");
			return z;
		}

		// evaluate function
		if (iname == null && function != null && isParsed == true) {

			for (int i = 0; i < x.length; i++)
				for (int j = 0; j < y.length; j++) {

					try {

						calc.setVariable("x", x[i]);
						calc.setVariable("y", y[i]);
						z[i][j] = calc.calculate();

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
		this.Xmin = min;

	}

	/**
	 * Get Min value in X
	 * 
	 * @return Min value in X
	 */
	public double getMinX() {
		return this.Xmin;
	}

	/**
	 * Set Min value in Y
	 * 
	 * @param min
	 *            Min value in Y
	 */
	public void setMinY(double min) {
		this.Ymin = min;

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
		return this.Ymin;
	}

	/**
	 * Set Max value in X
	 * 
	 * @param max
	 *            Max value in X
	 */
	public void setMaxX(double max) {
		this.Xmax = max;

	}

	/**
	 * Sets a name of the function, i.e. what will be used for evaluation
	 * 
	 * @param name
	 *            Name
	 */

	public void setName(String name) {
		this.name = name;

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
	 * Get Max value in X
	 * 
	 * @return Max value in X
	 */
	public double getMaxX() {
		return this.Xmax;

	}

	/**
	 * Set Max value in Y
	 * 
	 * @param max
	 *            Max value in Y
	 */

	public void setMaxY(double max) {
		this.Ymax = max;

	}

	/**
	 * Get Max value in Y
	 * 
	 * @return Max value in Y
	 */
	public double getMaxY() {
		return this.Ymax;

	}

	/**
	 * Get the number of points
	 * 
	 * @param bins
	 *            Number of points
	 */
	public void setPoints(int bins) {
		this.points = bins;

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
                this.name = name.replaceAll(parameter, s1);
                function.withExpression(this.name);
        }


	/**
	 * Get the number of points for evaluation of a function
	 * 
	 * @return Number of points
	 */
	public int getPoints() {
		return this.points;

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
	 * Return parsed function. One can evaluate it as "calculate()".
	 * @return function
	 **/
	public Calculable getParse() {
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
	 * If the function is parsed correctly, return true. Use this check before
	 * drawing it.
	 * 
	 * @return true if parsed.
	 */
	public boolean isParsed() {

		return isParsed;
	}

}

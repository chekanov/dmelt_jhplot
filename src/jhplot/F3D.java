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

import hep.aida.IFunction;
import java.io.Serializable;

import jhplot.gui.HelpBrowser;
import jhplot.math.exp4j.*;


/**
 * Create 3D function  using 3 independent variables: x,y,z.
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
 * @author S.Chekanov
 * 
 */

public class F3D extends DrawOptions implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private double Xmin;

	private double Xmax;

	private double Ymin;

	private double Ymax;

	private double Zmin;

	private double Zmax;

	private int points;

	private String title="F3D";
	
	private String name;

	private Expression calc = null;

	private ExpressionBuilder function = null;


	private boolean isParsed = false;

	private IFunction iname = null;

	/**
	 * Create a function in 3D for evaluation.
	 * The function may have up to 3 independent variables: x,y,z.
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
	 * It also recognizes the pi (or Pi) values;
	 * <br/>

	 * @param name
	 *            String representing the function
	 */
	public F3D(String name) {
		this(name,0.0,1.0,0.0,1.0,0.0,1.0);
	}

	/**
	 * Create a function in 3D. Uses 500 points between min and max value for
	 * evaluation. The function may have up to 3 independent variables in it
	 * (x,y,z).
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
	 * @param Zmin
	 *            Min value in Z
	 * @param Zmax
	 *            Max value in Y
	 * 
	 */
	public F3D(String title, String name, double Xmin, double Xmax, double Ymin, double Ymax,
			double Zmin, double Zmax) {

		
		this.name=name;
		this.name = this.name.replace("**", "^"); // preprocess power
		this.name = this.name.replace("pi", "3.14159265"); 
		this.name = this.name.replace("Pi", "3.14159265"); 
		
	    this.title = title;
		this.points = 500;
		this.Xmin = Xmin;
		this.Xmax = Xmax;
		this.Ymin = Ymin;
		this.Ymax = Ymax;
		this.Zmin = Zmin;
		this.Zmax = Zmax;

		function = new ExpressionBuilder(this.name);
        try {
                calc = (function.variables("x","y","z")).build();
                isParsed = true;
	 } catch (IllegalArgumentException  e) {
                                isParsed = false;
                                jhplot.utils.Util.ErrorMessage("Failed to parse function " + this.name+" Error:"+e.toString());
                       
	 }

	}
	
	
	/**
	 * Build a 3D function. Title set to its name.
	 * 
	 * @param name
	 * @param Xmin
	 * @param Xmax
	 * @param Ymin
	 * @param Ymax
	 * @param Zmin
	 * @param Zmax
	 */
	public F3D(String name, double Xmin, double Xmax, double Ymin, double Ymax,
			double Zmin, double Zmax) {

		this(name,name,Xmin,Xmax,Ymin,Ymax,Zmin,Zmax);
		
	}
	
	
	
	

	/**
	 * Create a function in 3D from a AIDA IFunction.
	 * 
	 * @param title
	 *        title
	 * @param name
	 *            String representing the function.
	 * @param iname
	 *            input AIDA function
	 * @param Xmin
	 *            Min value in X
	 * @param Xmax
	 *            Max value in X
	 * @param Ymin
	 *            Min value in Y
	 * @param Ymax
	 *            Max value in Y
	 * @param Zmin
	 *            Min value in Z
	 * @param Zmax
	 *            Max value in Y
	 * 
	 */
	public F3D(String title, String name, IFunction iname, double Xmin, double Xmax,
			double Ymin, double Ymax, double Zmin, double Zmax) {

	     this.name = name.replace("**","^"); // preprocess power	
		this.title = title;
		this.iname = iname;
		this.points = 500;
		this.Xmin = Xmin;
		this.Xmax = Xmax;
		this.Ymin = Ymin;
		this.Ymax = Ymax;
		this.Zmin = Zmin;
		this.Zmax = Zmax;
	}
	
	public F3D(String name, IFunction iname, double Xmin, double Xmax,
			double Ymin, double Ymax, double Zmin, double Zmax) {

	}
	/**
	 * Create a function in 3D from a AIDA IFunction.
	 * @param iname
	 *            input AIDA function
	 * @param Xmin
	 *            Min value in X
	 * @param Xmax
	 *            Max value in X
	 * @param Ymin
	 *            Min value in Y
	 * @param Ymax
	 *            Max value in Y
	 * @param Zmin
	 *            Min value in Z
	 * @param Zmax
	 *            Max value in Y
	 * 
	 */
	public F3D(IFunction iname, double Xmin, double Xmax,
			double Ymin, double Ymax, double Zmin, double Zmax) {
		
		this("IFunction",iname,Xmin,Xmax,Ymin,Ymax,Zmin,Zmax);
	
		
	}
	
	

	
	
	
	
	/**
	 * Evaluate a function at a specific point in (x,y,z)
	 * 
	 * @param x
	 *            value in x for evaluation
	 * @param y
	 *            value in y for evaluation
	 * @param z
	 *            value in z for evaluation
	 * 
	 * @return function value at (x,y,z)
	 */
	public double eval(double x, double y, double z) {

		double h = 0;

		// jPlot function first
		if (iname == null && (function == null || isParsed == false)) {
			jhplot.utils.Util.ErrorMessage("eval(): Function was not parsed correctly!");
			return h;
		}

		// evaluate function
		if (iname == null && function != null && isParsed == true) {
			try {
				
				calc.setVariable("x", x);
				calc.setVariable("y", y);
				calc.setVariable("z", z);
				h = calc.evaluate();
				
				
			} catch (Exception e) {
                                jhplot.utils.Util.ErrorMessage("Failed to evaluate function " + this.name+" Error:"+e.toString());
				// System.out.println("Failed to evaluate function:" + name);

			}
		}

		// start AIDA function
		if (iname != null && iname.dimension() == 3) {
			try {
				double[] xx = new double[iname.dimension()];
				xx[0] = x;
				xx[1] = y;
				xx[2] = z;
				h = iname.value(xx);
			} catch (Exception e) {
				// System.out.println("Failed to evaluate function!");
                                jhplot.utils.Util.ErrorMessage("Failed to evaluate function " + this.name+" Error:"+e.toString());
			}
		} // end IFunction

		return h;
	}

	/**
	 * Set a title
	 * 
	 * @param title
	 *            Title
	 */

	public void setTitle(String title) {
		this.title = title;

	}

	/**
	 * Get the title
	 * 
	 * @return Title
	 */
	public String getTitle() {
		return this.title;

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
	 * Set Min value in Z
	 * 
	 * @param min
	 *            Min value in Z
	 */
	public void setMinZ(double min) {
		this.Zmin = min;

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
	 * Get Min value in Z
	 * 
	 * @param Min
	 *            value in Z
	 */

	public double getMinZ() {
		return this.Zmin;
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
	    * Show online documentation.
	    */
	      public void doc() {
	        	 
	    	  String a=this.getClass().getName();
	    	  a=a.replace(".", "/")+".html"; 
			  new HelpBrowser(  HelpBrowser.JHPLOT_HTTP+a);
	    	 
			  
			  
	      }
	
	
	
	
	/**
	 * Set Max value in Z
	 * 
	 * @param max
	 *            Max value in Z
	 */
	public void setMaxZ(double max) {
		this.Zmax = max;

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
	 * Set Max value in Z
	 * 
	 * @return max Max value in Z
	 */
	public double getMaxZ() {
		return this.Zmax;

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
	 * Get the number of points for evaluation of a function
	 * 
	 * @return Number of points
	 */
	public int getPoints() {
		return this.points;

	}

	/**
	 * Return parsed functional expression.
	 * 
	 * @return function
	 * */
	public Expression getParse() {
		return calc;
	}

	/**
	 * Parse the function.
	 * @return true if parsed without problems. 
	 **/
	public boolean parse() {
		try {
			calc=(function.variables("x","y","z")).build();
			isParsed = true;
		} catch (IllegalArgumentException e) {
			isParsed = false;
		        //System.err.println("Failed to parse function " + this.name+" Error:"+e.toString());
                        jhplot.utils.Util.ErrorMessage("Failed to parse function " + this.name+" Error:"+e.toString());

		}
              
		return isParsed;

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
                function = new ExpressionBuilder(this.name);
                parse();
        }

	

}

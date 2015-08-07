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

public class F3D extends DrawOptions  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private FProxy proxy;
	final int maxpoints = 200;
	private String title="F3D";
	
	

	private Expression calc = null;

	private ExpressionBuilder function = null;


	/**
	 * Create a function in 3D for evaluation.
	 * The function may have up to 3 independent variables: x,y,z.
	 * This is unranged function.
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
	 * <br/>

	 * @param name
	 *            String representing the function
	 */
	public F3D(String name) {
		this(name,0.0,0.0,0.0,0.0,0.0,0.0);
	}

	/**
	 * Create a function in 3D. Uses 500 points between min and max value for
	 * evaluation. The function may have up to 3 independent variables in it
	 * (x,y,z). This is ranged function.
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

		proxy = new FProxy(3,title, name, null,  new double[]{Xmin,Xmax,Ymin,Ymax,Zmin,Zmax}, 
				maxpoints, true); 
		
		function = new ExpressionBuilder(proxy.getName());
        try {
                calc = (function.variables("x","y","z")).build();
                
	 } catch (IllegalArgumentException  e) {
		 proxy.setParsed(false);
         jhplot.utils.Util.ErrorMessage("Failed to parse function " + name+" Error:"+e.toString());
                       
	 }

	}
	
	
	/**
	 * Build a 3D function. Title set to its name.
	 * Ranged function.
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

		proxy = new FProxy(3,title, name, iname,  new double[]{Xmin,Xmax,Ymin,Ymax,Zmin,Zmax}, maxpoints, true); 	
		this.title = title;
		
	}
	
	public F3D(String name, IFunction iname, double Xmin, double Xmax,
			double Ymin, double Ymax, double Zmin, double Zmax) {

	}
	
	

	/**
	 * Initialize function from proxy.
	 * @param f
	 */
	
	public F3D(FProxy f) {
		if (f.getType() != 3) {
			jhplot.utils.Util.ErrorMessage("Error in parsing F3D. Wrong function type! " + f.getName());
			return;
		}
		
		
		proxy=f;
		setTitle(proxy.getTitle());

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
		  IFunction iname=proxy.getIFunction();
		  boolean  isParsed=proxy.isParsed();
		  String name=proxy.getName();
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
                                jhplot.utils.Util.ErrorMessage("Failed to evaluate function " + name+" Error:"+e.toString());
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
                                jhplot.utils.Util.ErrorMessage("Failed to evaluate function " + 
				name+" Error:"+e.toString());
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
		proxy.setLimit(0,min);

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
	 * Set Min value in Z
	 * 
	 * @param min
	 *            Min value in Z
	 */
	public void setMinZ(double min) {
		proxy.setLimit(4,min);

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
	 * Get Min value in Z
	 * 
	 * @param Min
	 *            value in Z
	 */

	public double getMinZ() {
		double[] d= proxy.getLimits();
		return d[4];
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
		proxy.setLimit(5,max);

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
	 * Set Max value in Z
	 * 
	 * @return max Max value in Z
	 */
	public double getMaxZ() {
		double[] d= proxy.getLimits();
		return d[5];

	}

	/**
	 * Set Max value in Y
	 * 
	 * @param max
	 *            Max value in Y
	 */

	public void setMaxY(double max) {
		proxy.setLimit(3,max);

	}

	/**
	 * Get Max value in Y
	 * 
	 * @return Max value in Y
	 */
	public double getMaxY() {
		double[] d= proxy.getLimits();
		return d[1];

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
	 * Get the number of points for evaluation of a function
	 * 
	 * @return Number of points
	 */
	public int getPoints() {
		return proxy.getPoints();

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
		
		
		boolean  isParsed=proxy.isParsed();

		  String name=proxy.getName();
		try {
                        function = new ExpressionBuilder(name);
			calc=(function.variables("x","y","z")).build();
			proxy.setParsed(true);
		} catch (IllegalArgumentException e) {
			proxy.setParsed(false);
		        //System.err.println("Failed to parse function " + this.name+" Error:"+e.toString());
                        jhplot.utils.Util.ErrorMessage("Failed to parse function " + name+" Error:"+e.toString());

		}
              
		return isParsed;

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
         * If the function is parsed correctly, return true. Use this check before
         * drawing it.
         * 
         * @return true if parsed.
         */
        public boolean isParsed() {

                return proxy.isParsed();
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
	 * Get this function as a string.
	 * 
	 * @return Convert to string.
	 */
	public String toString() {

		String tmp = "F3D:" + proxy.getName();
		double[] d = proxy.getLimits();
		boolean isParsed = proxy.isParsed();
		double Xmin = d[0];
		double Xmax = d[1];
		double Ymin = d[2];
		double Ymax = d[3];
		double Zmin = d[2];
		double Zmax = d[3];
		int points = proxy.getPoints();
		tmp = tmp + " (title=" + getTitle() + ", n=" + Integer.toString(points)
				+ ", minX=" + Double.toString(Xmin) + ", maxX="
				+ Double.toString(Xmax) + ", minY=" + Double.toString(Ymin)
				+ ", maxY=" + Double.toString(Ymax) + ", "
				+ Double.toString(Xmax) + ", minZ=" + Double.toString(Zmin)
				+ ", maxZ=" + Double.toString(Zmax) + ", "
				+ Boolean.toString(isParsed) + ")";
		return tmp;
	}


	

}

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

import jhplot.fit.Landau;
import jhplot.fit.Pow;
import jhplot.gui.HelpBrowser;
import hep.aida.*;
import hep.aida.ref.fitter.Fitter;
import java.io.*;

/**
 * Fit data (H1D or P1D). Some details can be found in
 * http://confluence.slac.stanford
 * .edu/display/JAS3/Functions+and+Fitting+Users+Guide
 * 
 * @author S.Chekanov
 * 
 */
public class HFitter implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private IAnalysisFactory anFactory;

	private ITree tree;

	private IFunctionFactory funcFactory;

	private IFitFactory fitFactory;

	private IDataPointSetFactory dataPointSet;

	private IFitter fitter;

	private IFunctionCatalog funcCatalog;

	private IFitData data;

	private IFunction iFunc = null;

	private IFitResult result;

	private String[] items;

	private String[] engines;

	double rmin = -99;

	double rmax = -99;

	double param[];

	private String range = null;

	private String method;

	/**
	 * Create a fitter using some method. The supported types are: <br>
	 * uml - unbinned maximum likelihood <br>
	 * leastsquares - least Squares <br>
	 * cleverchi2 - "clever" chi2 <br>
	 * chi2 - Chi2 (default) <br>
	 * bml - binned maximum likelihood <br>
	 * <p>
	 * When a function is provided to the fitter the user has to be aware that
	 * the AIDA prescription is to leave the input function unchanged, create a
	 * clone, do the fit on the clone and return the clone in the fit result.
	 * There advantage to this approach is that the input function can be reused
	 * as is for multiple identical fits while the fitted-cloned function is
	 * left unchanged in the fit result. The default is noClone=false.
	 * 
	 * @param method
	 *            the fit method (Could be: uml, leastsquares, cleverchi2, chi2,
	 *            bml)
	 */

	public HFitter(String method) {

		// create all aida staff
		this.method = method;
		anFactory = IAnalysisFactory.create();
		tree = anFactory.createTreeFactory().create();
		dataPointSet = anFactory.createDataPointSetFactory(tree);

		funcFactory = anFactory.createFunctionFactory(tree);
		fitFactory = anFactory.createFitFactory();
		fitter = fitFactory
				.createFitter(method, "jminuit", "noClone=\"false\"");
		fitter = new Fitter(method, "jminuit", "noClone=\"false\"");
		funcCatalog = funcFactory.catalog();

		items = fitFactory.availableFitMethods();
		engines = fitFactory.availableFitEngines();

		funcCatalog = funcFactory.catalog();

		// String s = "BreitWigner";
		// IFunction f = new BreitWigner(s);
		// funcCatalog.add(s, f);

		String s = "pow";
		IFunction f = new Pow(s);
		funcCatalog.add(s, f);

		s = "landau";
		f = new Landau(s);
		funcCatalog.add(s, f);

	}

	/**
	 * Return available fit methods.
	 * 
	 * @return Fit methods
	 */

	public String[] getFitMethods() {

		return items;
	}

	/**
	 * Returns the current fit method.
	 * 
	 * @return Fit methods
	 */

	public String getFitMethod() {

		return this.method;
	}

	/**
	 * Sets the fit method. Available methods are: <b>uml, leastsquares,
	 * cleverchi2, chi2, bml</b>.
	 * 
	 * @param The
	 *            fit method.
	 */
	public void setFitMethod(String method) {
		this.method = method;
		fitter = fitFactory
				.createFitter(method, "jminuit", "noClone=\"false\"");
		fitter = new Fitter(method, "jminuit", "noClone=\"false\"");
		funcCatalog = funcFactory.catalog();
	}

	/**
	 * Add a new function to catalog.
	 * 
	 * @param name
	 *            function name
	 * @param f
	 *            input function
	 */

	public void addFunc(String name, IFunction f) {

		funcCatalog.add(name, f);

	}

	/**
	 * Return available fit engines.
	 * 
	 * @return Fit methods
	 */

	public String[] getFitEngines() {

		return engines;
	}

	/**
	 * Create a fitter with the Chi2 method by default.
	 */
	public HFitter() {
		this("chi2");

	}

	/**
	 * Set fit functions
	 */
	public void setFunc(IFunction func) {

		this.iFunc = func;
	}

	/**
	 * Return list of available functions
	 * 
	 * @return list of functions
	 */
	public String[] getFuncCatalog() {

		return funcCatalog.list();
	}

	/**
	 * Set fit functions from predefined string. <br>
	 * G - Gaussian <br>
	 * E - Exponential <br>
	 * Pn' - Polynomial (n is an integer, i.e "P0","P1", "P2').
	 * 
	 * @param func
	 *            string: G (Gaussian),E (exponential),landau (landa),pow (power
	 *            law), and polynomial: P0,P1,P2,P3..
	 */
	public void setFunc(String func) {
		iFunc = funcFactory.createFunctionByName(func, func);

	}

	/**
	 * Get parameter value
	 * 
	 * @param name
	 *            parameter name
	 * @return value
	 */
	public double getPar(String name) {
		return iFunc.parameter(name);
	}

	/**
	 * Set parameter to a value
	 * 
	 * @param name
	 *            parameter name
	 * @param value
	 *            value to be set.
	 */
	public void setPar(String name, double value) {
		iFunc.setParameter(name, value);
	}

	/**
	 * Set step size for parameter (say 0.01)
	 * 
	 * @param name
	 *            parameter name
	 * @param step
	 *            step used in minimization
	 */

	public void setParStep(String name, double step) {
		fitter.fitParameterSettings(name).setStepSize(step);
	}

	/**
	 * Fix a given parameter to a constant value
	 * 
	 * @param name
	 *            parameter name to be fixed
	 */
	public void setParFixed(String name) {
		fitter.fitParameterSettings(name).setFixed(true);
	}

	/**
	 * Fix a given parameter to a constant value or not
	 * 
	 * @param name
	 *            parameter name to be fixed or not
	 * @param fix
	 *            true if the parameter should be fixed.
	 */
	public void setParFixed(String name, boolean fix) {
		fitter.fitParameterSettings(name).setFixed(fix);
	}

	/**
	 * Set range for parameter during the fit
	 * 
	 * @param name
	 *            parameter name
	 * @param min
	 *            min value
	 * @param max
	 *            max value
	 */
	public void setParRange(String name, double min, double max) {
		fitter.fitParameterSettings(name).setBounds(min, max);
	}

	/**
	 * Set parameter constraint Example: Set parameter "mean1" to be identical
	 * to "mean2" as: "mean1=mean2"
	 * 
	 * @param constraint
	 */
	public void setParConstraint(String constraint) {
		fitter.setConstraint(constraint);
	}

	/**
	 * Return function
	 * 
	 * @return function
	 */
	public IFunction getFunc() {

		return iFunc;
	}

	/**
	 * Set functions from a script. Example: <br>
	 * P2 : setFunc("p2", 1, "a+b*x[0]+c*x[0]*x[0]", "a,b,c") <br>
	 * parabola : setFunc('parabola',1, 'a*x[0]*x[0]+b*x[0]+c','a,b,c') <br>
	 * <br>
	 * 
	 * @param name
	 *            function name
	 * @param dimension
	 *            dimension
	 * @param function
	 *            string representing a function
	 * @param parameters
	 *            parameters (use comma)
	 */
	public void setFunc(String name, int dimension, String function,
			String parameters) {
		iFunc = funcFactory.createFunctionFromScript(name, dimension, function,
				parameters, "", "");
	}

	/**
	 * Fit histogram
	 * 
	 * @param h1d
	 *            input H1D
	 */
	public void fit(H1D h1d) {
		doFit(h1d);
	}

	/**
	 * Fit P1D
	 * 
	 * @param p1d
	 *            input P1D
	 */
	public void fit(P1D p1d) {
		doFit(p1d);
	}

	/**
	 * Fit PND (all errors set to be zero). Only last component is "value" of
	 * the data, other are positions in dimension-1 space.
	 * 
	 * @param pnd
	 *            input P1D
	 */
	public void fit(PND pnd) {
		doFit(pnd);
	}

	/**
	 * Fit H2D histogram
	 * 
	 * @param h2d
	 *            input H2D histogram
	 */
	public void fit(H2D h2d) {
		doFit(h2d);
	}

	/**
	 * Set range for fitting for 1D objects (H1D, P1D)
	 * 
	 * @param rmin
	 *            Min X value
	 * @param rmax
	 *            Max Y value
	 */
	public void setRange(double rmin, double rmax) {
		this.rmin = rmin;
		this.rmax = rmax;

	}

	/**
	 * Return Jaida fitter
	 * 
	 * @return fitter
	 */
	public IFitter getFitter() {

		return fitter;

	}

	/**
	 * Perform fit
	 */
	private void doFit(P1D p1) {

		double min = 0;
		double max = 0;

		if (p1.getDimension() == 2 && this.method.equals("chi2")) {
			jhplot.utils.Util
					.ErrorMessage("The dimension of input data is 2, but you require to use \"chi2\" method. For such containers, you can only use \"leastsquares\" method. For the \"chi2\" method, you should specify errors on Y-values");

		}

		// p1.setTitle("test");
		// System.out.println(p1.toString());
		// System.out.println("DEBUG for p1d="+p1.getTitle());
		// Create a one dimensional IDataPointSet.
		IDataPointSet dps2D = dataPointSet.create(p1.getTitle(), p1.getTitle(),
				2);
		p1.fillIDataPointSet(dps2D);
		// System.out.println(dps2D.size());
		// System.out.println(p1.size());
		data = fitFactory.createFitData();
		min = p1.getMin(0);
		max = p1.getMax(0);
		data.create1DConnection(dps2D, 0, 1);
		// System.out.println( isetdata );
		if (rmin == rmax) {
			rmin = min;
			rmax = max;
		}
		data.range(0).excludeAll();
		data.range(0).include(rmin, rmax);
		result = fitter.fit(data, iFunc);

	}

	/**
	 * Perform fit
	 */
	private void doFit(H1D h1d) {
		data = fitFactory.createFitData();
		double min = 0;
		double max = 0;
		min = h1d.getMin();
		max = h1d.getMax();
		data.create1DConnection(h1d.get());
		if (rmin == rmax) {
			rmin = min;
			rmax = max;
		}
		data.range(0).excludeAll();
		data.range(0).include(rmin, rmax);
		result = fitter.fit(data, iFunc);

	}

	/**
	 * Perform fit
	 */
	private void doFit(H2D h2d) {
		data = fitFactory.createFitData();
		double min = 0;
		double max = 0;
		double minX = h2d.getMinX();
		double maxX = h2d.getMaxX();
		double minY = h2d.getMinY();
		double maxY = h2d.getMaxY();
                data.create2DConnection(h2d.get());

		data.range(0).excludeAll();
		data.range(1).excludeAll();
		data.range(0).include(minX, maxX);
		data.range(1).include(minY, maxY);

		result = fitter.fit(data, iFunc);
		// System.out.println("FITTED");
	}

	/**
	 * Perform fit
	 */
	private void doFit(PND pnd) {
		data = fitFactory.createFitData();
		double min = 0;
		double max = 0;
		int tt[] = new int[pnd.getDimension()];
		for (int m = 0; m < pnd.getDimension() - 1; m++)
			tt[m] = m;
		data.createConnection(pnd.getIDataPointSet(), tt,
				pnd.getDimension() - 1);
		result = fitter.fit(data, iFunc);
	}

	/**
	 * Get results of the fit in form of function
	 * 
	 * @return results of the fit in form of function.
	 */
	public IFunction getFittedFunc() {

		return result.fittedFunction();

	}

	/**
	 * Get result of the fit.
	 * 
	 * @return result of the fit.
	 */
	public IFitResult getResult() {

		return result;

	}

	/**
	 * Show online documentation.
	 */
	public void doc() {

		String a = this.getClass().getName();
		a = a.replace(".", "/") + ".html";
		new HelpBrowser(HelpBrowser.JHPLOT_HTTP + a);

	}
}

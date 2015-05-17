package jhplot.stat;


import org.apache.commons.math3.analysis.interpolation.*;
import org.apache.commons.math3.analysis.polynomials.*;
import umontreal.iro.lecuyer.functionfit.SmoothingCubicSpline;

import hep.aida.IAxis;
import hep.aida.ref.histogram.Histogram1D;
import jhplot.H1D;
import jhplot.P1D;
import jhplot.utils.SHisto;

/**
 * Perform interpolation of a data using different algorithm. This class is
 * useful for data smoothing and determination of background levels. Weigths for
 * each data points can be given.
 * 
 * @author S.Chekanov
 * 
 */

public class Interpolator {

	private double[] x;
	private double[] y;
	private double[] eY;
	private double[] eX;
	private long dataLength;

	/**
	 * Initialize interpolator
	 * 
	 * @param aX
	 *            is the array of x data
	 * @param aY
	 *            is the array of y data
	 */
	public Interpolator(double[] aX, double[] aY) {
		x = aX;
		y = aY;
		if (x.length != y.length) {
			jhplot.utils.Util.ErrorMessage("Arrays have different length!");
		} else {
			dataLength = x.length;
		}
	}

	/**
	 * Initialize interpolator for P1D array. Only X and Y values are taken.
	 * Errors are ignored. One can include weigths using 1/(errors*errors) as
	 * given on the Y-axis. (can be set with add(x,y,error) method of P1D.
	 * 
	 * @param p1d
	 *            p1d values x-y and y-error is used to estimate weight
	 *            (1/error*error)
	 */
	public Interpolator(P1D p1d) {
		x = p1d.getArrayX();
		y = p1d.getArrayY();
		eY = p1d.getArrayYlower();
		eY = p1d.getArrayXleft();

		if (x.length != y.length) {
			jhplot.utils.Util.ErrorMessage("Arrays have different length!");
		} else {
			dataLength = x.length;
		}
	}

	/**
	 * Initialize interpolator for a histogram. Errors on the histogram heights
	 * are considered as weights 1/(error*error)
	 * 
	 * @param h1
	 *            input for interpolation. X - mean value in a bin, y is a
	 *            height and weight is 1/(error*error)
	 */
	public Interpolator(H1D h1) {

		Histogram1D h = h1.get();
		IAxis axis = h.axis();
		x = new double[axis.bins()];
		y = new double[axis.bins()];
		eY = new double[axis.bins()];
		for (int i = 0; i < axis.bins(); i++) {
			x[i] = h1.binMean(i);
			y[i] = h1.binHeight(i);
			eY[i] = h1.binError(i);
		}
		if (x.length != y.length) {
			jhplot.utils.Util.ErrorMessage("Arrays have different length!");
		} else {
			dataLength = x.length;
		}
	}

	/**
	 * Redefine smooth interval for input data.
	 * 
	 * @param xMin
	 *            minimal value for X
	 * @param xMax
	 *            maximal value for Y
	 */

	public void setRange(double xMin, double xMax) {

		double[] x1 = new double[x.length];
		double[] y1 = new double[x.length];
		double[] eY1 = new double[x.length];

		int n = 0;
		for (int i = 0; i < x.length; i++) {
			if (x[i] > xMin && x[i] < xMax) {
				x1[n] = x[i];
				y1[n] = y[i];
				eY1[n] = eY[i];
				n++;
			}

		}

		x = new double[n];
		y = new double[n];
		eY = new double[n];

		for (int i = 0; i < n; i++) {
			x[i] = x1[i];
			y[i] = y1[i];
			eY[i] = eY1[i];

		}

	}

	/**
	 * Performs a Local Regression Algorithm (also Loess, Lowess) for
	 * interpolation of data points. For reference, see William S. Cleveland -
	 * Robust Locally Weighted Regression and Smoothing Scatterplots
	 * <p>
	 * Compute an interpolating function by performing a loess fit on the data
	 * at the original abscissa and then building a cubic spline. Weights of the
	 * input data are not taken into account.
	 * 
	 * 
	 * 
	 * @param bandwidth
	 *            - when computing the loess fit at a particular point, this
	 *            fraction of source points closest to the current point is
	 *            taken into account for computing a least-squares regression. A
	 *            sensible value is usually 0.25 to 0.5.
	 * 
	 * @param robustnessIters
	 *            How many robustness iterations are done. A sensible value is
	 *            usually 0 (just the initial fit without any robustness
	 *            iterations) to 4.
	 * 
	 * 
	 * @accuracy If the median residual at a certain robustness iteration is
	 *           less than this amount, no more iterations are done.
	 * 
	 * 
	 * 
	 * @return polynomial functions.
	 * 
	 */
	public PolynomialSplineFunction interpolateLoess(double bandwidth,
			int robustnessIters, double accuracy, int option) {

		PolynomialSplineFunction c = null;
		LoessInterpolator L = null;
		
		L = new LoessInterpolator(bandwidth, robustnessIters, accuracy);
		c = L.interpolate(x, y);
		
/*		
		try {
			L = new LoessInterpolator(bandwidth, robustnessIters, accuracy);
			c = L.interpolate(x, y);
		} catch (MathException e) {
			utils.Util
					.ErrorMessage("Bandwidth does not lie in the interval [0,1] or if "
							+ "robustnessIters is negative.");
		}
		*/
		return c;

	}

	/**
	 * Performs a Local Regression Algorithm (also Loess, Lowess) for
	 * interpolation of data points.
	 * 
	 * For reference, see William S. Cleveland - Robust Locally Weighted
	 * Regression and Smoothing Scatterplots. This method computes a weighted
	 * loess fit on the data at the original abscissa.
	 * <p>
	 * Compute an interpolating function by performing a loess fit on the data
	 * at the original abscissa and then building a cubic spline.
	 * 
	 * 
	 * @param bandwidth
	 *            - when computing the loess fit at a particular point, this
	 *            fraction of source points closest to the current point is
	 *            taken into account for computing a least-squares regression. A
	 *            sensible value is usually 0.25 to 0.5.
	 * 
	 * @param robustnessIters
	 *            How many robustness iterations are done. A sensible value is
	 *            usually 0 (just the initial fit without any robustness
	 *            iterations) to 4.
	 * 
	 * 
	 * @param accuracy
	 *            If the median residual at a certain robustness iteration is
	 *            less than this amount, no more iterations are done. A typical
	 *            value is 2
	 * 
	 * @param option
	 *            treatment of errors on data points: <br>
	 *            option=0. Errors on X and Y are ignored and weights=1 for all
	 *            points <br>
	 *            option=1. Errors on Y are used to calculate weights as
	 *            1/errorY, where errorY is error on Y value. Errors on X are
	 *            ignored. This option works for histograms where error on
	 *            heights can be used to determine the weight of each data
	 *            point, while X-values do not have errors. <br>
	 *            option=2. Same as "1", but weights are 1/(errorY*errorY),
	 *            where errorY is error on Y values <br>
	 *            option=3. Same as "1", but weights are given as
	 *            1/(errorY*errorX). This works best for P1D which contains
	 *            errors on X and Y. <br>
	 *            option=4. same as "2", but weights on X and Y are calculated
	 *            as 1/(errorX*errorX*errorY*errorY)<br>
	 *            option=5. weights for points are given by errorY<br>
	 *            option=6. weights for points are given by errorY*errorX<br>
	 * 
	 * 
	 * 
	 * @return P1D with smoothed result. Abscissa is the same as in the original
	 *         input.
	 */
	public P1D smoothLoess(double bandwidth, int robustnessIters,
			double accuracy, int option) {

		P1D p = new P1D("Loess interpolation");

		LoessInterpolator L;
		// try {

			L = new LoessInterpolator(bandwidth, robustnessIters, accuracy);
			double[] w = new double[x.length];

			for (int i = 0; i < x.length; i++) {
				w[i] = 1;
				if (option == 0)
					w[i] = 1;
				else if (option == 1)
					if (eY[i] > 0)
						w[i] = 1.0 / eY[i];
					else if (option == 2)
						if (eY[i] > 0)
							w[i] = 1.0 / (eY[i] * eY[i]);
						else if (option == 3)
							if (eY[i] > 0 && eX[i] > 0)
								w[i] = 1.0 / (eY[i] * eX[i]);
							else if (option == 4)
								if (eY[i] > 0 && eX[i] > 0)
									w[i] = 1.0 / (eY[i] * eX[i] * eY[i] * eX[i]);
								else if (option == 5)
									w[i] = eY[i];
								else if (option == 6)
									w[i] = eY[i] * eX[i];
								else
									jhplot.utils.Util
											.ErrorMessage("Wrong option value");
			}

			double[] yy = L.smooth(x, y, w);
			for (int i = 0; i < yy.length; i++) {
				p.add(x[i], yy[i]);

			}

			/*
		} catch (MathException e) {
			utils.Util
					.ErrorMessage("Arguments and values are of the same size that is "
							+ "greater than zero, or the arguments are in a strictly increasing order or all "
							+ "arguments and values are finite real numbers!");
		}
		*/
			
		return p;

	}

	/**
	 * Calculate a spline with nodes at (x, y), with weights w and smoothing
	 * factor rho. Represents a cubic spline with nodes at <SPAN
	 * CLASS="MATH">(<I>x</I><SUB>i</SUB>, <I>y</I><SUB>i</SUB>)</SPAN> computed
	 * with the smoothing cubic spline algorithm of Schoenberg. A smoothing
	 * cubic spline is made of <SPAN CLASS="MATH"><I>n</I> + 1</SPAN> cubic
	 * polynomials. The <SPAN CLASS="MATH"><I>i</I></SPAN>th polynomial of such
	 * a spline, for <SPAN CLASS="MATH"><I>i</I> = 1,&#8230;, <I>n</I> -
	 * 1</SPAN>, is defined as <SPAN
	 * CLASS="MATH"><I>S</I><SUB>i</SUB>(<I>x</I>)</SPAN> while the complete
	 * spline is defined as
	 * <P>
	 * </P>
	 * <DIV ALIGN="CENTER" CLASS="mathdisplay"> <I>S</I>(<I>x</I>) =
	 * <I>S</I><SUB
	 * >i</SUB>(<I>x</I>),&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; for
	 * <I>x</I>&#8712;[<I>x</I><SUB>i-1</SUB>, <I>x</I><SUB>i</SUB>]. </DIV>
	 * <P>
	 * </P>
	 * For <SPAN CLASS="MATH"><I>x</I> &lt; <I>x</I><SUB>0</SUB></SPAN> and
	 * <SPAN CLASS="MATH"><I>x</I> &gt; <I>x</I><SUB>n-1</SUB></SPAN>, the
	 * spline is not precisely defined, but this class performs extrapolation by
	 * using <SPAN CLASS="MATH"><I>S</I><SUB>0</SUB></SPAN> and <SPAN
	 * CLASS="MATH"><I>S</I><SUB>n</SUB></SPAN> linear polynomials. The
	 * algorithm which calculates the smoothing spline is a generalization of
	 * the algorithm for an interpolating spline. <SPAN
	 * CLASS="MATH"><I>S</I><SUB>i</SUB></SPAN> is linked to <SPAN
	 * CLASS="MATH"><I>S</I><SUB>i+1</SUB></SPAN> at <SPAN
	 * CLASS="MATH"><I>x</I><SUB>i+1</SUB></SPAN> and keeps continuity
	 * properties for first and second derivatives at this point, therefore
	 * 
	 * <SPAN CLASS="MATH"><I>S</I><SUB>i</SUB>(<I>x</I><SUB>i+1</SUB>) =
	 * <I>S</I><SUB>i+1</SUB>(<I>x</I><SUB>i+1</SUB>)</SPAN>,
	 * 
	 * <SPAN CLASS="MATH"><I>S'</I><SUB>i</SUB>(<I>x</I><SUB>i+1</SUB>) =
	 * <I>S'</I><SUB>i+1</SUB>(<I>x</I><SUB>i+1</SUB>)</SPAN> and <SPAN
	 * CLASS="MATH"><I>S''</I><SUB>i</SUB>(<I>x</I><SUB>i+1</SUB>) =
	 * <I>S''</I><SUB>i+1</SUB>(<I>x</I><SUB>i+1</SUB>)</SPAN>.
	 * 
	 * <P>
	 * The spline is computed with a smoothing parameter <SPAN
	 * CLASS="MATH"><I>&#961;</I>&#8712;[0, 1]</SPAN> which represents its
	 * accuracy with respect to the initial <SPAN
	 * CLASS="MATH">(<I>x</I><SUB>i</SUB>, <I>y</I><SUB>i</SUB>)</SPAN> nodes.
	 * The smoothing spline minimizes
	 * <P>
	 * </P>
	 * <DIV ALIGN="CENTER" CLASS="mathdisplay"> <I>L</I> =
	 * <I>&#961;</I>&sum;<SUB
	 * >i=0</SUB><SUP>n-1</SUP><I>w</I><SUB>i</SUB>(<I>y</I
	 * ><SUB>i</SUB>-<I>S</I><SUB>i</SUB>(<I>x</I><SUB>i</SUB>))<SUP>2</SUP> +
	 * (1 -
	 * <I>&#961;</I>)&int;<SUB>x<SUB>0</SUB></SUB><SUP>x<SUB>n-1</SUB></SUP>
	 * (<I>S''</I>(<I>x</I>))<SUP>2</SUP><I>dx</I> </DIV>
	 * <P>
	 * </P>
	 * In fact, by setting <SPAN CLASS="MATH"><I>&#961;</I> = 1</SPAN>, we
	 * obtain the interpolating spline; and we obtain a linear function by
	 * setting <SPAN CLASS="MATH"><I>&#961;</I> = 0</SPAN>. The weights <SPAN
	 * CLASS="MATH"><I>w</I><SUB>i</SUB> &gt; 0</SPAN>, which default to 1, can
	 * be used to change the contribution of each point in the error term. A
	 * large value <SPAN CLASS="MATH"><I>w</I><SUB>i</SUB></SPAN> will give a
	 * large weight to the <SPAN CLASS="MATH"><I>i</I></SPAN>th point, so the
	 * spline will pass closer to it.
	 * 
	 * 
	 * <P>
	 * <P>
	 * 
	 
	 * 
	 * 
	 * @param rho 
	 *        smoothing factor rho.
	 * 
	 * @param option
	 *            treatment of errors on data points: <br>
	 *            option=0. Errors on X and Y are ignored and weights=1 for all
	 *            points <br>
	 *            option=1. Errors on Y are used to calculate weights as
	 *            1/errorY, where errorY is error on Y value. Errors on X are
	 *            ignored. This option works for histograms where error on
	 *            heights can be used to determine the weight of each data
	 *            point, while X-values do not have errors. <br>
	 *            option=2. Same as "1", but weights are 1/(errorY*errorY),
	 *            where errorY is error on Y values <br>
	 *            option=3. Same as "1", but weights are given as
	 *            1/(errorY*errorX). This works best for P1D which contains
	 *            errors on X and Y. <br>
	 *            option=4. same as "2", but weights on X and Y are calculated
	 *            as 1/(errorX*errorX*errorY*errorY)<br>
	 *            option=5. weights for points are given by errorY<br>
	 *            option=6. weights for points are given by errorY*errorX<br>
	 * 
	 * 
	 * @return SmoothingCubicSpline
	 */
	public SmoothingCubicSpline interpolateCubicSpline(double rho, int option) {

		double[] w = new double[x.length];

		for (int i = 0; i < x.length; i++) {
			w[i] = 1;
			if (option == 0)
				w[i] = 1;
			else if (option == 1)
				if (eY[i] > 0)
					w[i] = 1.0 / eY[i];
				else if (option == 2)
					if (eY[i] > 0)
						w[i] = 1.0 / (eY[i] * eY[i]);
					else if (option == 3)
						if (eY[i] > 0 && eX[i] > 0)
							w[i] = 1.0 / (eY[i] * eX[i]);
						else if (option == 4)
							if (eY[i] > 0 && eX[i] > 0)
								w[i] = 1.0 / (eY[i] * eX[i] * eY[i] * eX[i]);
							else if (option == 5)
								w[i] = eY[i];
							else if (option == 6)
								w[i] = eY[i] * eX[i];
							else
								jhplot.utils.Util.ErrorMessage("Wrong option value");
		}
		return new SmoothingCubicSpline(x, y, w, rho);

	}

	/**
	 * Performs a Local Regression Algorithm (also Loess, Lowess) for
	 * interpolation of data points.
	 * 
	 * For reference, see William S. Cleveland - Robust Locally Weighted
	 * Regression and Smoothing Scatterplots. This method computes a weighted
	 * loess fit on the data at the original abscissa.
	 * <p>
	 * Compute an interpolating function by performing a loess fit on the data
	 * at the original abscissa and then building a cubic spline. If errors on Y
	 * are given (from a histogram or P1D), assume the weighths equal
	 * 1/(errorY*errorY).
	 * 
	 * 
	 * @param bandwidth
	 *            - when computing the loess fit at a particular point, this
	 *            fraction of source points closest to the current point is
	 *            taken into account for computing a least-squares regression. A
	 *            sensible value is usually 0.25 to 0.5.
	 * 
	 * @param robustnessIters
	 *            How many robustness iterations are done. A sensible value is
	 *            usually 0 (just the initial fit without any robustness
	 *            iterations) to 4.
	 * 
	 * 
	 * @param accuracy
	 *            If the median residual at a certain robustness iteration is
	 *            less than this amount, no more iterations are done.
	 * 
	 * @param option
	 *            treatment of errors on data points: <br>
	 *            option=0. Errors on X and Y are ignored and weights=1 for all
	 *            points <br>
	 *            option=1. Errors on Y are used to calculate weights as
	 *            1/error. Errors on X are ignored. This option works for
	 *            histograms where error on the heights can be used to determine
	 *            the weight of each data point, while X-values do not have
	 *            errors. <br>
	 *            option=2. Same as "1", but weights are 1/(error*error), where
	 *            error is on Y values <br>
	 *            option=3. Same as "1", but errors on X are also taken into
	 *            account. This works best for P1D which contains errors on X
	 *            and Y. Weights are 1/(errorX*errorY) <br>
	 *            option=4. same as "2", but errors on X and Y are calculated as
	 *            1/(errorX*errorX*errorY*errorY)
	 * 
	 * 
	 * 
	 * 
	 * @return P1D with smoothed result. Abscissa is the same as in the original
	 *         input.
	 */
	public P1D smoothLoess(double bandwidth, int robustnessIters,
			double accuracy) {

		return smoothLoess(bandwidth, robustnessIters, accuracy, 2);
	}

	/**
	 * Computes a natural (also known as "free", "unclamped") cubic spline
	 * interpolation for the data set. The interpolate(double[], double[])
	 * method returns a PolynomialSplineFunction consisting of n cubic
	 * polynomials, defined over the subintervals determined by the x values,
	 * x[0] < x[i] ... < x[n]. The x values are referred to as "knot points."The
	 * value of the PolynomialSplineFunction at a point x that is greater than
	 * or equal to the smallest knot point and strictly less than the largest
	 * knot point is computed by finding the subinterval to which x belongs and
	 * computing the value of the corresponding polynomial at x - x[i] where i
	 * is the index of the subinterval. See PolynomialSplineFunction for more
	 * details. The interpolating polynomials satisfy:
	 * <p>
	 * The value of the PolynomialSplineFunction at each of the input x values
	 * equals the corresponding y value. Adjacent polynomials are equal through
	 * two derivatives at the knot points (i.e., adjacent polynomials "match up"
	 * at the knot points, as do their first and second derivatives).
	 * <p>
	 * The cubic spline interpolation algorithm implemented is as described in
	 * R.L. Burden, J.D. Faires, Numerical Analysis, 4th Ed., 1989, PWS-Kent,
	 * ISBN 0-53491-585-X, pp 126-131.
	 * 
	 * @return P1D with smoothed result. Abscissa is the same as in the original
	 *         input.
	 */
	public P1D smoothSpline() {

		P1D p = new P1D("Spline interpolation");

		SplineInterpolator L = new SplineInterpolator();
	//	try {
			PolynomialSplineFunction c = L.interpolate(x, y);
			for (int i = 0; i < x.length; i++) {
				p.add(x[i], c.value(x[i]));

			}

	//	} catch (MathException e) {
		//	utils.Util.ErrorMessage("Error in interpolation");
	//	}
		
			
			return p;

	}

	/**
	 * Perform a cubic interpolatory spline.
	 * 
	 * @return P1D with smoothed data
	 */

	public P1D smoothCubicSpline() {

		P1D p = new P1D("Cubic spline interpolation");

		Spline sp = new Spline(x, y);
		for (int i = 0; i < x.length; i++) {
			p.add(x[i], sp.spline_value(x[i]));
		}

		return p;

	}

	/**
	 * Computes a Gaussian smoothed version of data.
	 * 
	 * <p>
	 * The data are smoothed by discrete convolution with a kernel approximating
	 * a Gaussian impulse response with the specified standard deviation.
	 * 
	 * @param standardDeviation
	 *            The standard deviation of the Gaussian smoothing kernel which
	 *            must be non-negative or an
	 *            <code>IllegalArgumentException</code> will be thrown. If zero,
	 *            the P1D object will be returned with no smoothing applied.
	 * @return A Gaussian smoothed version of the histogram.
	 * 
	 */
	public P1D smoothGauss(double standardDeviation) {

		P1D p = new P1D("Gaussian interpolation");
		SHisto sh = new SHisto(x.length, getMinValue(), getMaxValue(), 1);

		sh.setBins(y);
		sh = sh.getGaussianSmoothed(standardDeviation);
		for (int i = 0; i < x.length; i++) {
			p.add(x[i], sh.getBinsFirstBand(i));
		}

		return p;

	}

	/**
	 * Smooth data by averaging over a moving window.
	 * <p>
	 * It is smoothed by averaging over a moving window of a size specified by
	 * the method parameter: if the value of the parameter is <i>k</i> then the
	 * width of the window is <i>2*k + 1</i>. If the window runs off the end of
	 * the P1D only those values which intersect the histogram are taken into
	 * consideration. The smoothing may optionally be weighted to favor the
	 * central value using a "triangular" weighting. For example, for a value of
	 * <i>k</i> equal to 2 the central bin would have weight 1/3, the adjacent
	 * bins 2/9, and the next adjacent bins 1/9. Errors are kept the same as
	 * before.
	 * 
	 * @param isWeighted
	 *            Whether values Y will be weighted using a triangular weighting
	 *            scheme favoring bins near the central bin.
	 * @param k
	 *            The smoothing parameter which must be non-negative. If zero,
	 *            the histogram object will be returned with no smoothing
	 *            applied.
	 * @return A smoothed version of data
	 */

	public P1D smoothAverage(boolean isWeighted, int k) {

		P1D p = new P1D("Moving window interpolation");
		SHisto sh = new SHisto(x.length, getMinValue(), getMaxValue(), 1);

		sh.setBins(y);
		sh = sh.getSmoothed(isWeighted, k);
		for (int i = 0; i < x.length; i++) {
			p.add(x[i], sh.getBinsFirstBand(i));
		}

		return p;
	}

	/**
	 * Get min value of data in X
	 * 
	 * @return
	 */

	public double getMaxValue() {
		double maxValue = x[0];
		for (int i = 1; i < x.length; i++) {
			if (x[i] > maxValue) {
				maxValue = x[i];
			}
		}
		return maxValue;
	}

	/**
	 * Get maximum value of data in X
	 * 
	 * @return
	 */
	public double getMinValue() {
		double minValue = x[0];
		for (int i = 1; i < x.length; i++) {
			if (x[i] < minValue) {
				minValue = x[i];
			}
		}
		return minValue;
	}

	/**
	 * Natural cubic splines interpolation. calculates the natural cubic spline
	 * that interpolates y[0], y[1], ... y[n] The first segment is returned as
	 * C[0].a + C[0].b*u + C[0].c*u^2 + C[0].d*u^3 0<=u <1 the other segments
	 * are in C[1], C[2], ... C[n-1]
	 * 
	 * @return Cubic function
	 */

	public Cubic[] interpolateNatuarlCubicSpline() {

		Cubic[] s = calcNaturalCubic(y.length, y);
		return s;

	}

	/**
	 * calculates the natural cubic spline that interpolates y[0], y[1], ...
	 * y[n] The first segment is returned as C[0].a + C[0].b*u + C[0].c*u^2 +
	 * C[0].d*u^3 0<=u <1 the other segments are in C[1], C[2], ... C[n-1]
	 * 
	 * @param n
	 * @param x
	 * @return
	 */
	private Cubic[] calcNaturalCubic(int n, double[] x) {
		double[] gamma = new double[n + 1];
		double[] delta = new double[n + 1];
		double[] D = new double[n + 1];
		int i;
		/*
		 * We solve the equation [2 1 ] [D[0]] [3(x[1] - x[0]) ] |1 4 1 | |D[1]|
		 * |3(x[2] - x[0]) | | 1 4 1 | | . | = | . | | ..... | | . | | . | | 1 4
		 * 1| | . | |3(x[n] - x[n-2])| [ 1 2] [D[n]] [3(x[n] - x[n-1])]
		 * 
		 * by using row operations to convert the matrix to upper triangular and
		 * then back substitution. The D[i] are the derivatives at the knots.
		 */

		gamma[0] = 1.0f / 2.0f;
		for (i = 1; i < n; i++) {
			gamma[i] = 1 / (4 - gamma[i - 1]);
		}
		gamma[n] = 1 / (2 - gamma[n - 1]);

		delta[0] = 3 * (x[1] - x[0]) * gamma[0];
		for (i = 1; i < n; i++) {
			delta[i] = (3 * (x[i + 1] - x[i - 1]) - delta[i - 1]) * gamma[i];
		}
		delta[n] = (3 * (x[n] - x[n - 1]) - delta[n - 1]) * gamma[n];

		D[n] = delta[n];
		for (i = n - 1; i >= 0; i--) {
			D[i] = delta[i] - gamma[i] * D[i + 1];
		}

		/* now compute the coefficients of the cubics */
		Cubic[] C = new Cubic[n];
		for (i = 0; i < n; i++) {
			C[i] = new Cubic(x[i], D[i], 3 * (x[i + 1] - x[i]) - 2 * D[i]
					- D[i + 1], 2 * (x[i] - x[i + 1]) + D[i] + D[i + 1]);
		}
		return C;
	}

	/**
	 * represents a cubic polynomial. a + b*u + c*u^2 +d*u^3
	 * 
	 */
	public class Cubic {

		double a, b, c, d;

		/**
		 * a + b*u + c*u^2 +d*u^3
		 * 
		 * @param a
		 * @param b
		 * @param c
		 * @param d
		 */
		public Cubic(double a, double b, double c, double d) {
			this.a = a;
			this.b = b;
			this.c = c;
			this.d = d;
		}

		/**
		 * Evaluate cubic function
		 * 
		 * @param u
		 * @return
		 */
		public double eval(float u) {
			return (((d * u) + c) * u + b) * u + a;
		}
	}

}

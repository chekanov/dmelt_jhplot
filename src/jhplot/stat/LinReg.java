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

package jhplot.stat;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.text.NumberFormat;

import jhplot.F1D;
import jhplot.P1D;
import jhplot.gui.HelpBrowser;

/**
 * A linear regression analysis.
 * 
 * @author Charles S. Stanton and  S.Chekanov
 * 
 * @version Mon Jul 15 07:34:20 PDT 2002
 */
public class LinReg {

	private double[] x, y; // vectors of x,y
	private double sumX = 0;
	private double sumY = 0;
	private double sumXY = 0;
	private double sumXsquared = 0;
	private double sumYsquared = 0;
	// private double covariance=0;
	private double Sxx, Sxy, Syy, n;
	private double a = 0, b = 0; // coefficients of regression
	private int dataLength;
	private double[][] residual;// residual[i][0] = x[i], residual[i][1]=

	// residual

	private double maxAbsoluteResidual = 0.0;

	private double SSR = 0.0; // regression sum of squares

	private double SSE = 0.0; // error sum of squares

	private double MSE = 0.0;

	private double sP0 = 0.0; // error on P0

	private double sP1 = 0.0; // error on P1

	private double sCorr = 0.0; // correlation

	// private double sigmaHatSquared = 0.0;
	private double minX = Double.POSITIVE_INFINITY;

	private double maxX = Double.NEGATIVE_INFINITY;

	private double minY = Double.POSITIVE_INFINITY;

	private double maxY = Double.NEGATIVE_INFINITY;

	private NumberFormat nf;

	boolean showResidualLines;

	boolean showConfidenceBand = false;

	boolean showPredictionBand = false;

	private final static int CONFIDENCE = 0;

	private final static int PREDICTION = 1;

	private double xRangeLow;

	private double yRangeLow;

	private double xRangeHigh;

	private double yRangeHigh;

	// whether panel should choose own horizontal scale
	protected boolean autoscaleX = true;

	// whether panel should choose own vertical scale
	protected boolean autoscaleY = true;

	// critical values for t
	static double[] t005 = { Double.NaN, 63.657, 9.925, 5.841, 4.604, 4.032,
			3.707, 3.499, 3.355, 3.250, 3.169, 3.106, 3.055, 3.012, 2.977,
			2.947, 2.921, 2.898, 2.878, 2.861, 2.845, 2.831, 2.819, 2.807,
			2.797, 2.787, 2.779, 2.771, 2.763, 2.756, 2.756 };

	static double[] t025 = { Double.NaN, 12.706, 4.303, 3.182, 2.776, 2.571,
			2.447, 2.365, 2.306, 2.262, 2.228, 2.201, 2.179, 2.160, 2.145,
			2.131, 2.120, 2.110, 2.101, 2.093, 2.086, 2.080, 2.075, 2.069,
			2.064, 2.060, 2.056, 2.052, 2.048, 2.045, 1.960 };

	/**
	 * Constructor for regression calculations
	 * 
	 * @param aX
	 *            is the array of x data
	 * @param aY
	 *            is the array of y data
	 */
	public LinReg(double[] aX, double[] aY) {
		x = aX;
		y = aY;
		if (x.length != y.length) {
			System.out.println("x, y vectors must be of same length");
		} else {
			dataLength = x.length;
		}
		doStatistics();
	}

	/**
	 * constructor for regression calculations. It should be noted that only X
	 * and Y values from the input P1D data holder are used
	 * 
	 * @param aXY
	 *            P1D container for X and Y values
	 */
	public LinReg(P1D aXY) {
		this(aXY.getArrayX(), aXY.getArrayY());

	}

	private void doStatistics() {
		// Find sum of squares for x,y and sum of xy
		for (int i = 0; i < dataLength; i++) {
			minX = Math.min(minX, x[i]);
			maxX = Math.max(maxX, x[i]);
			minY = Math.min(minY, y[i]);
			maxY = Math.max(maxY, y[i]);
			sumX += x[i];
			sumY += y[i];
			sumXsquared += x[i] * x[i];
			sumYsquared += y[i] * y[i];
			sumXY += x[i] * y[i];
		}
		// Caculate regression coefficients
		n = (double) dataLength;
		Sxx = sumXsquared - sumX * sumX / n;
		Syy = sumYsquared - sumY * sumY / n;
		Sxy = sumXY - sumX * sumY / n;
		b = Sxy / Sxx;
		a = (sumY - b * sumX) / n;
		SSR = Sxy * Sxy / Sxx;
		SSE = Syy - SSR;

		sCorr = Sxy / Math.sqrt(Sxx * Syy);

		MSE = 0;
		if (n > 2)
			MSE = SSE / (n - 2);

		double sMSE = Math.sqrt(MSE);
		sP0 = sMSE
				* Math.sqrt((1.0 / (double) n) + (getXBar() * getXBar()) / Sxx);
		sP1 = sMSE / Math.sqrt(Sxx);

		xRangeLow = getMinX();
		yRangeLow = getMinY();
		xRangeHigh = getMaxX();
		yRangeHigh = getMaxY();

		calculateResiduals();
	}

	
	private void calculateResiduals() {
		residual = new double[dataLength][];
		maxAbsoluteResidual = 0.0;
		for (int i = 0; i < dataLength; i++) {
			residual[i] = new double[2];
			residual[i][0] = x[i];
			residual[i][1] = y[i] - (a + b * x[i]);
			maxAbsoluteResidual = Math.max(maxAbsoluteResidual, Math.abs(y[i]
					- (a + b * x[i])));
		}
	}

	
	/**
	 * Update statistics for a single data point
	 * @param xValue  X value
	 * @param yValue  Y value
	 */
	private void updateStatistics(double xValue, double yValue) {
		// Find sum of squares for x,y and sum of xy
		n++;
		sumX += xValue;
		sumY += yValue;
		sumXsquared += xValue * xValue;
		sumYsquared += yValue * yValue;
		sumXY += xValue * yValue;
		// Caculate regression coefficients
		n = (double) dataLength;
		Sxx = sumXsquared - sumX * sumX / n;
		Syy = sumYsquared - sumY * sumY / n;
		Sxy = sumXY - sumX * sumY / n;
		b = Sxy / Sxx;
		a = (sumY - b * sumX) / n;
		SSR = Sxy * Sxy / Sxx;
		SSE = Syy - SSR;

		sCorr = Sxy / Math.sqrt(Sxx * Syy);

		MSE = 0;
		if (n > 2)
			MSE = SSE / (n - 2);

		double sMSE = Math.sqrt(MSE);
		sP0 = sMSE
				* Math.sqrt(1.0 / (double) n + (getXBar() * getXBar()) / Sxx);
		sP1 = sMSE / Math.sqrt(Sxx);

		xRangeLow = getMinX();
		yRangeLow = getMinY();
		xRangeHigh = getMaxX();
		yRangeHigh = getMaxY();

		calculateResiduals();
	}

	/**
	 * reset data to 0
	 */
	public void reset() {
		x = new double[0];
		y = new double[0];
		dataLength = 0;
		n = 0.0;
		residual = new double[0][];

		sumX = 0;
		sumXsquared = 0;
		sumY = 0;
		sumYsquared = 0;
		sumXY = 0;

	}

	/**
	 * Get correlation coefficient
	 * 
	 * @return Correlation coefficient.
	 */

	public double getCorrelation() {
		return sCorr;
	}

	/**
	 * Get Intercept
	 * 
	 * @return Intercept
	 */
	public double getIntercept() {
		return a;
	}

	/**
	 * Get the standard error on intercept
	 * 
	 * @return standard error on intercept
	 */

	public double getInterceptError() {
		return sP0;
	}

	/**
	 * Get the standard error on slope
	 * 
	 * @return standard error on slope
	 */

	public double getSlopeError() {
		return sP1;
	}

	/**
	 * Get slope
	 * 
	 * @return slope
	 */
	public double getSlope() {
		return b;
	}

	/**
	 * Get residuals
	 * 
	 * @return P1D array with residuals
	 */
	public P1D getResiduals() {
		P1D p = new P1D("residuals");
		for (int i = 0; i < dataLength; i++) {
			p.add(residual[i][0],residual[i][1]);
			
		}
		return p;
	}

	/**
	 * Get an array with X data
	 * 
	 * @return array with X data
	 */

	public double[] getDataX() {
		return x;
	}

	/**
	 * Get an array with Y data
	 * 
	 * @return array with Y data
	 */
	public double[] getDataY() {
		return y;
	}

	/**
	 * Add a point to the data and redo the regression
	 * 
	 * @param xValue
	 *            X value
	 * @param yValue
	 *            Y value
	 */
	public void addPoint(double xValue, double yValue) {
		dataLength++;
		double[] xNew = new double[dataLength];
		double[] yNew = new double[dataLength];
		System.arraycopy(x, 0, xNew, 0, dataLength - 1);
		System.arraycopy(y, 0, yNew, 0, dataLength - 1);
		xNew[dataLength - 1] = xValue;
		yNew[dataLength - 1] = yValue;
		x = xNew;
		y = yNew;
		updateStatistics(xValue, yValue);
	}

	/**
	 * Get a minimum value for X
	 * 
	 * @return Minimum value
	 */
	public double getMinX() {
		return minX;
	}

	/**
	 * Get a maximum value for X
	 * 
	 * @return Max value in X
	 */
	public double getMaxX() {
		return maxX;
	}

	/**
	 * Get minimum value for Y
	 * 
	 * @return minimum Y value
	 */
	public double getMinY() {
		return minY;
	}

	/**
	 * Get maximum value in Y
	 * 
	 * @return Maximum value in Y
	 */
	public double getMaxY() {
		return maxY;
	}

	/**
	 * Get max absolute residual
	 * 
	 * @return max absolute residual
	 */
	public double getMaxAbsoluteResidual() {
		return maxAbsoluteResidual;
	}

	/**
	 * Get Sxx value: sumXsquared - sumX * sumX / n
	 * 
	 * @return Sxx
	 */

	public double getSxx() {
		return Sxx;
	}

	/**
	 * Get SYY value: sumYsquared - sumY * sumY / n
	 * 
	 * @return Syy valye
	 */

	public double getSyy() {
		return Syy;
	}

	/**
	 * Ger SSR value
	 * 
	 * @return SSR value
	 */
	public double getSSR() {
		return SSR;
	}

	/**
	 * Get SSE value
	 * 
	 * @return SSE value
	 */
	public double getSSE() {
		return SSE;
	}

	/**
	 * Get MSE value
	 * 
	 * @return MSE value
	 */
	public double getMSE() {

		return MSE;
	}

	/**
	 * Get average x
	 * 
	 * @return average X
	 */
	public double getXBar() {
		return sumX / n;
	}

	/**
	 * Get average Y
	 * 
	 * @return average Y
	 */
	public double getYBar() {
		return sumY / n;
	}

	/**
	 * Get the size of the input data
	 * 
	 * @return size of data array
	 */
	public int getDataLength() {
		return x.length;
	}

	/**
	 * Get pearson R
	 * 
	 * @return pearson R
	 */
	public double getPearsonR() {
		return Sxy / Math.sqrt(Sxx * Syy);
	}

	/**
	 * Get sun of the square
	 * 
	 * @return sum of the square
	 */
	public double getSumXSquared() {
		return sumXsquared;
	}

	/*
	 * private double minValue(double[] data) { double min; if (data.length > 0) {
	 * min = data[0]; // initial value for (int i = 1; i < data.length; i++) {
	 * min = (data[i] < min) ? data[i] : min; } return min; } else { return
	 * Double.NaN; } }
	 * 
	 * private double maxValue(double[] data) { double max; if (data.length > 0) {
	 * max = data[0]; // initial value for (int i = 1; i < data.length; i++) {
	 * max = (data[i] > max) ? data[i] : max; } return max; } else { return
	 * Double.NaN; } }
	 */

	/**
	 * Get the linear regression result
	 * 
	 * @return Resulting function
	 */

	public F1D getResult() {
		F1D tmp = new F1D("p0+(p1*x)","p0+(p1*x)", xRangeLow, xRangeHigh,false);
		tmp.setColor(Color.blue);
		tmp.setTitle("p0+(p1*x)");
		tmp.setPar("p0", getIntercept());
		tmp.setPar("p1", getSlope());
                tmp.parse();
		return tmp;

	}

	/**
	 * Get confidence intervals for means
	 * 
	 * @return P1D[2] for lower and high
	 */

	public P1D[] getConfidence() {

		return getConfidence(Color.magenta);

	}

	
	/**
	 * Get confidence intervals for means
	 * 
	 * @param color 
	 *            color used to draw
	 * @return P1D[2] for lower and high
	 */

	public P1D[] getConfidence(Color color) {
		return getConfPred(0, 100, color);

	}

	
	
	/**
	 * Get prediction lines
	 * 
	 * @return P1D[2] for lower and high
	 */

	public P1D[] getPrediction() {

		return getPrediction(Color.red) ;

	}


	/**
	 * Get prediction lines
	 * 
	 * @param  color
	 *            color used to draw
	 * @return P1D[2] for lower and high
	 */

	public P1D[] getPrediction(Color color) {

		return getConfPred(1, 100, color);

	}
	
	
	/**
	 * Get confidence or predictions
	 * 
	 * @param A
	 *            used in calculating sError A is 0 for confidence intervals for
	 *            means A is 1 for prediction intervals
	 * 
	 * @param Npoints
	 *            Number of points for evaluation
	 * 
	 * @return c Color used to show the lines
	 * 
	 */

	private P1D[] getConfPred(int A, int Npoints, Color c) {

		P1D[] pp = new P1D[2];

		String s1 = "Confidence level (upper)";
		String s2 = "Confidence level (lower)";
		if (A == 1) {
			s1 = "Prediction (upper)";
			s2 = "Prediction (lower)";
		}

		pp[0] = new P1D(s1);
		pp[1] = new P1D(s2);

		for (int i = 0; i < pp.length; i++) {
			pp[i].setColor(c);
			pp[i].setStyle("l");
			pp[i].setErr(false);
			pp[i].setDrawSymbol(false);
			pp[i].setPenDash(5);
		}

		double n = x.length;
		double t;
		double S = Math.sqrt(getMSE());

		Sxx = getSxx();
		double xBar = getXBar();

		if (x.length < 32) {
			t = t025[x.length - 2];
		} else {
			t = t025[30];
		}
		// A is used in calculating sError
		// A is 0 for confidence intervals for means
		// A is 1 for prediction intervals

		double xs = xRangeLow;
		double ys = a + b * xs;
		double sError = S
				* Math.sqrt(A + 1 / n + (xs - xBar) * (xs - xBar) / Sxx);

		double deltaX = (xRangeHigh - xRangeLow) / Npoints;

		for (int i = 0; i <= Npoints; i++) {
			pp[0].add(xs, ys + t * sError);
			xs = xs + deltaX;
			ys = a + b * xs;
			sError = S * Math.sqrt(A + 1 / n + (xs - xBar) * (xs - xBar) / Sxx);

		}

		for (int i = Npoints; i >= 0; i--) {
			xs = xs - deltaX;
			ys = a + b * xs;
			sError = S * Math.sqrt(A + 1 / n + (xs - xBar) * (xs - xBar) / Sxx);
			pp[1].add(xs, ys - t * sError);
		}

		return pp;

	}

	
	/**
	 * Calculate confidence band  in form of P1D with  errors.
	 * The number of total points for P1D is set to 100 by default. 
	 * 
	 * @param color Color used to show the band
	 * @param transparancy 
	 *                    level of color transparency (between 0 and 1)
	 * @return P1D with fit values. Errors show the confidence band 
	 * 
	 */

	public P1D getConfidenceBand(Color color, double transparency) {

		return getConfidenceBand(100,color,transparency);
	
	}
	
	/**
	 * Calculate confidence band  in form of P1D with  errors.
	 *  
	 * 
	 * @param Npoints number of points to display the band
	 * @param color Color used to show the band
	 * @param transparancy 
	 *                    level of color transparency (between 0 and 1)
	 * @return P1D with fit values. Errors show the confidence band 
	 * 
	 */

	public P1D getConfidenceBand(int Npoints, Color color, double transparency) {

		 
		 P1D pp = new P1D("Confidence band");
		 pp.setColor(color);
		 pp.setSymbolSize(1);
		 // pp.setStyle("l");
		 // pp.setDrawSymbol(false);
		 // pp.setErrFillColor(color,transparency);

		double n = x.length;
		double t;
		double S = Math.sqrt(getMSE());

		Sxx = getSxx();
		double xBar = getXBar();

		if (x.length < 32) {
			t = t025[x.length - 2];
		} else {
			t = t025[30];
		}
		

		double xs = xRangeLow;
		double ys = a + b * xs;
		double sError = S * Math.sqrt(1.0 / n + (xs - xBar) * (xs - xBar) / Sxx);
		
		double deltaX = (xRangeHigh - xRangeLow) / (Npoints);

		for (int i = 0; i <=Npoints; i++) {
			pp.add(xs, ys, t*sError);
			xs = xs + deltaX;
			ys = a + b * xs;
			sError = S * Math.sqrt(1.0 / n + (xs - xBar) * (xs - xBar) / Sxx);
		}

		return pp;

	}

	
	/**
	 * Calculate the prediction band  in form of P1D with  errors.
	 * The number of total points for P1D is set to 100 by default. 
	 * 
	 * 
	 * @return P1D with fit values. Errors show the prediction band 
	 * 
	 */

	public P1D getPredictionBand() {

		 return getPredictionBand(50,Color.green,1.0);
	}
	
	
	/**
	 * Calculate the prediction band  in form of P1D with  errors.
	 * The number of total points for P1D is set to 100 by default. 
	 * 
	 * @param color Color used to show the band
	 * 
	 * @return P1D with fit values. Errors show the prediction band 
	 * 
	 */

	public P1D getPredictionBand(Color color) {

		 return getPredictionBand(50,color,1.0);
	}
	
	/**
	 * Calculate the prediction band  in form of P1D with  errors.
	 * The number of total points for P1D is set to 100 by default. 
	 * 
	 * @param color Color used to show the band
	 * @param transparancy 
	 *                    level of color transparency (between 0 and 1)
	 * @return P1D with fit values. Errors show the prediction band 
	 * 
	 */

	public P1D getPredictionBand(Color color, double transparency) {

		 return getPredictionBand(50,color,transparency);
	}
	
	/**
	 * Calculate the prediction band  in form of P1D with  errors.
	 * 
	 * @param Npoints number of points for evaluation
	 * @param color Color used to show the band
	 * @param transparancy 
	 *                    level of color transparency (between 0 and 1)
	 * @return P1D with fit values. Errors show the prediction band 
	 * 
	 */

	public P1D getPredictionBand(int Npoints, Color color, double transparency) {

		 P1D pp = new P1D("Prediction band");
		 pp.setColor(color);
		 pp.setSymbolSize(1);
		 // pp.setErrFillColor(color,transparency);

		double n = x.length;
		double t;
		double S = Math.sqrt(getMSE());

		Sxx = getSxx();
		double xBar = getXBar();

		if (x.length < 32) {
			t = t025[x.length - 2];
		} else {
			t = t025[30];
		}
		
		
		double xs = xRangeLow;
		double ys = a + b * xs;
		double sError = S* Math.sqrt(1 + 1.0/ n + (xs - xBar) * (xs - xBar) / Sxx);
		
		double deltaX = (xRangeHigh - xRangeLow) / (Npoints);

		for (int i = 0; i <=Npoints; i++) {
			pp.add(xs, ys, t * sError);
			xs = xs + deltaX;
			ys = a + b * xs;
			sError = S * Math.sqrt(1 + 1.0/n + (xs - xBar) * (xs - xBar) / Sxx);
		}

		return pp;

	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private int scaleX(double a) {

		return (int) a;
	}

	private int scaleY(double a) {

		return (int) a;
	}

	private void showConf(Graphics g) {

		nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(3);
		int ix;
		int iy = 0;
		xRangeLow = getMinX();
		yRangeLow = getMinY();
		xRangeHigh = getMaxX();
		yRangeHigh = getMaxY();

		for (int i = 0; i < dataLength; i++) {

			ix = scaleX(x[i]);
			iy = scaleY(y[i]);
			g.fillOval(ix - 3, iy - 3, 6, 6);
		}

		if (showResidualLines) {
			g.setColor(Color.blue);
			g.drawLine(scaleX(xRangeLow), scaleY(yRangeLow),
					scaleX(xRangeHigh), scaleY(yRangeHigh));
		}
		g.setColor(Color.red);
		if (dataLength > 1) {
			g.drawLine(scaleX(xRangeLow), scaleY(b * xRangeLow + a),
					scaleX(xRangeHigh), scaleY(b * xRangeHigh + a));
		}

		// MSE is sqrt(SSE/(n-2)), so don't try if n <=2
		if (showConfidenceBand && (x.length > 2)) {
			drawBand(g, CONFIDENCE);
		}
		if (showPredictionBand && (x.length > 2)) {
			drawBand(g, PREDICTION);
		}

	}

	private void drawBand(Graphics g, int type) {
		double n = x.length;
		double t;
		double S = Math.sqrt(getMSE());

		Graphics2D g2d = (Graphics2D) g;
		Sxx = getSxx();
		double xBar = getXBar();

		if (x.length < 32) {
			t = t025[x.length - 2];
		} else {
			t = t025[30];
		}
		// A is used in calculating sError
		// A is 0 for confidence intervals for means
		// A is 1 for prediction intervals
		double A;
		Color bandColor;
		if (type == CONFIDENCE) {
			A = 0;
			bandColor = new Color(100, 0, 0);
		} else {// type=PREDICTION
			A = 1;
			bandColor = new Color(0, 0, 100);
		}

		double xs = xRangeLow;
		double ys = a + b * xs;
		double sError = S
				* Math.sqrt(A + 1 / n + (xs - xBar) * (xs - xBar) / Sxx);
		// bandLimit.moveTo(scaleX(xs), scaleY(ys+t*sError));
		double deltaX = (xRangeHigh - xRangeLow) / 10.0;
		for (int i = 1; i <= 10; i++) {
			xs = xs + deltaX;
			ys = a + b * xs;
			sError = S * Math.sqrt(A + 1 / n + (xs - xBar) * (xs - xBar) / Sxx);
			// bandLimit.lineTo(scaleX(xs), scaleY(ys+t*sError));
		}
		// bandLimit.lineTo(scaleX(xs), scaleY(ys-t*sError));
		for (int i = 9; i >= 0; i--) {
			xs = xs - deltaX;
			ys = a + b * xs;
			sError = S * Math.sqrt(A + 1 / n + (xs - xBar) * (xs - xBar) / Sxx);
			// bandLimit.lineTo(scaleX(xs), scaleY(ys-t*sError));
		}
		// bandLimit.closePath();
		g2d.setPaint(Color.gray);
		// g2d.draw(bandLimit);
		g2d.setPaint(bandColor);
		Composite c = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .2f);
		g2d.setComposite(c);
		// g2d.fill(bandLimit);
	}


	/**
	    * Show online documentation.
	    */
	      public void doc() {
	        	 
	    	  String a=this.getClass().getName();
	    	  a=a.replace(".", "/")+".html"; 
			  new HelpBrowser(  HelpBrowser.JHPLOT_HTTP+a);
	    	 
			  
			  
	      }
	
}

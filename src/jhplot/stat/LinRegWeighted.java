package jhplot.stat;

import java.awt.Color;
import jhplot.F1D;
import jhplot.gui.HelpBrowser;

/**
 * Weighted Least Squares Regression
 * 
 * @author S.Chekanov
 * 
 * @version Mon Jul 15 07:34:20 PDT 2002
 */
public class LinRegWeighted {

	private double[] x, y, w; // vectors of x,y

	private int n;

	private double a, b, chisq, meanX, meanY;

	// private double sigmaHatSquared = 0.0;
	private double minX = Double.POSITIVE_INFINITY;

	private double maxX = Double.NEGATIVE_INFINITY;

	private double minY = Double.POSITIVE_INFINITY;

	private double maxY = Double.NEGATIVE_INFINITY;

	/**
	 * Constructor for regression calculations
	 * 
	 * @param X
	 *            is the array of x data
	 * @param Y
	 *            is the array of y data
	 * 
	 * @param W
	 *            weights of data points
	 */
	public LinRegWeighted(double[] X, double[] Y, double W[]) {
		x = X;
		y = Y;
		w = W;
		if (x.length != y.length || x.length != w.length) {
			System.out.println("x, y vectors must be of same length");
		} else {
			n = x.length;
		}
		doStatistics();
	}

	private void doStatistics() {
		/* compute the weighted means and weighted deviations from the means */

		/* wm denotes a "weighted mean", wm(f) = (sum_i w_i f_i) / (sum_i w_i) */

		double W = 0, wm_x = 0, wm_y = 0, wm_dx2 = 0, wm_dxdy = 0;
		int i;

		for (i = 0; i < n; i++)
			minX = Math.min(minX, x[i]);
		maxX = Math.max(maxX, x[i]);
		minY = Math.min(minY, y[i]);
		maxY = Math.max(maxY, y[i]);

		{
			double wi = w[i];
			if (wi > 0) {
				W += wi;
				wm_x += (x[i] - wm_x) * (wi / W);
				wm_y += (y[i] - wm_y) * (wi / W);
			}
		}
		W = 0; /* reset the total weight */
		meanX = wm_x;
		meanY = wm_y;

		for (i = 0; i < n; i++) {
			double wi = w[i];

			if (wi > 0) {
				double dx = x[i] - wm_x;
				double dy = y[i] - wm_y;
				W += wi;
				wm_dx2 += (dx * dx - wm_dx2) * (wi / W);
				wm_dxdy += (dx * dy - wm_dxdy) * (wi / W);
			}
		}

		/* In terms of y = a + b x */
		double d2 = 0;
		b = wm_dxdy / wm_dx2;
		a = wm_y - wm_x * b;

		double cov_00 = (1 / W) * (1 + wm_x * wm_x / wm_dx2);
		double cov_11 = 1 / (W * wm_dx2);
		double cov_01 = -wm_x / (W * wm_dx2);

		for (i = 0; i < n; i++) {
			double wi = w[i];

			if (wi > 0) {
				double dx = x[i] - wm_x;
				double dy = y[i] - wm_y;
				double d = dy - b * dx;
				d2 += wi * d * d;
			}
		}

		chisq = d2;

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
		return 0;
	}

	/**
	 * Get the standard error on slope
	 * 
	 * @return standard error on slope
	 */

	public double getSlopeError() {
		return 0;
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
	 * Get average x
	 * 
	 * @return average X
	 */
	public double getXBar() {
		return meanX;
	}

	/**
	 * Get average Y
	 * 
	 * @return average Y
	 */
	public double getYBar() {
		return meanY;
	}

	/**
	 * Get the size of the input data
	 * 
	 * @return size of data array
	 */
	public int getDataLength() {
		return n;
	}

	/**
	 * Get chi2
	 * 
	 * @return
	 */

	public double getChi2() {
		return chisq;
	}

	/**
	 * Get the fit results
	 * 
	 * @return
	 */
	public F1D getResult() {
		F1D tmp = new F1D("P0+P1*x", false);
		tmp.setColor(Color.blue);
		tmp.setTitle("P0+P1*x");
		tmp.setPar("P0", getIntercept());
		tmp.setPar("P1", getSlope());
		tmp.parse();
		return tmp;

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

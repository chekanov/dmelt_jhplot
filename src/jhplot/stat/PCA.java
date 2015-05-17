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

import Jama.EigenvalueDecomposition;
import Jama.Matrix;
import jhplot.P1D;
import jhplot.gui.HelpBrowser;
import jhplot.math.DoubleArray;

import static jhplot.math.LinearAlgebra.*;
import static jhplot.math.StatisticSample.*;

/**
 * Perform a principle component analysis
 * 
 * @author Yann RICHET, Sergei Chekanov
 */

public class PCA {

	private double[][] X; // initial datas : lines = events and columns =
							// variables

	private double[] meanX, stdevX;

	private double[][] Z; // X centered reduced

	private double[][] cov; // Z covariance matrix

	private double[][] U; // projection matrix

	private double[] info; // information matrix

	/**
	 * Initialize 2D PCA analysis
	 * 
	 * @param xy
	 *            array in X
	 * 
	 */

	public PCA(double xy[][]) {

		this.X = xy;

		eval();

	}

	public void eval() {

		stdevX = stddeviation(X);
		meanX = mean(X);

		Z = center_reduce(X);
		cov = Statistics.covariance(Z);
		EigenvalueDecomposition e = eigen(cov);
		Matrix m = e.getV();
		U = transpose(m.getArray());
		info = e.getRealEigenvalues();// covariance matrix is symetric, so only
										// real eigenvalues...

	}

	/**
	 * Perform PCA analysis using P1D object (in 2D). All weights for points are
	 * set to 1. X and Y component of P1D are used for the PCA.
	 * 
	 * @param p1d
	 *            P1D input objects
	 */

	public PCA(P1D p1d) {

		int size = p1d.size();
		X = new double[size][2];
		for (int i = 0; i < size; i++) {
			X[i][0] = p1d.getX(i);
			X[i][1] = p1d.getY(i);
		}

		eval();

	}

	// normalization of x relatively to X mean and standard deviation
	public double[][] center_reduce(double[][] x) {
		double[][] y = new double[x.length][x[0].length];
		for (int i = 0; i < y.length; i++)
			for (int j = 0; j < y[i].length; j++)
				y[i][j] = (x[i][j] - meanX[j]) / stdevX[j];
		return y;
	}

	// de-normalization of y relatively to X mean and standard deviation
	public double[] inv_center_reduce(double[] y) {
		return inv_center_reduce(new double[][] { y })[0];
	}

	// de-normalization of y relatively to X mean and standard deviation
	public double[][] inv_center_reduce(double[][] y) {
		double[][] x = new double[y.length][y[0].length];
		for (int i = 0; i < x.length; i++)
			for (int j = 0; j < x[i].length; j++)
				x[i][j] = (y[i][j] * stdevX[j]) + meanX[j];
		return x;
	}

	private void view() {

		/*
		 * // Plot Plot2DPanel plot = new Plot2DPanel();
		 * 
		 * // initial Datas plot plot.addScatterPlot("datas", X);
		 * 
		 * // line plot of principal directions
		 * plot.addLinePlot(Math.rint(info[0] * 100 / sum(info)) + " %", meanX,
		 * inv_center_reduce(U[0])); plot.addLinePlot(Math.rint(info[1] * 100 /
		 * sum(info)) + " %", meanX, inv_center_reduce(U[1]));
		 * 
		 * // display in JFrame new FrameView(plot);
		 */

	}

	/**
	 * Return projection vectors and information per projection vector.
	 * 
	 * @return text
	 */
	public String getSummary() {
		// Command line display of results
		String tmp = "";
		tmp = tmp + "projection vectors: \n"
				+ DoubleArray.toString(transpose(U)) + "\n";
		tmp = tmp + "\ninformation per projection vector: "
				+ DoubleArray.toString(info) + "\n";

		/*
		 * double d1=Math.rint(info[0] * 100 / sum(info)); double
		 * d2=Math.rint(info[1] * 100 / sum(info)); double
		 * m1[]=inv_center_reduce(U[0]); double m2[]=inv_center_reduce(U[1]);
		 * 
		 * System.out.println("d1="+Double.toString(d1) );
		 * System.out.println("d2="+Double.toString(d2) );
		 * 
		 * for (int i=0; i<meanX.length; i++)
		 * System.out.println("MeanX="+Double.toString(meanX[i]) ); for (int
		 * i=0; i<m1.length; i++)
		 * System.out.println("m1="+Double.toString(m1[i]) ); for (int i=0;
		 * i<m2.length; i++) System.out.println("m1="+Double.toString(m2[i]) );
		 */

		return tmp;
	}

	/**
	 * Information about eigenvalues
	 * 
	 * @param k
	 *            - integer value (axis index of the projection)
	 * @return
	 */
	public double getEigenvalue(int k) {
		return info[k];
	}

	/**
	 * Express eigenvalues as percentage of total
	 * 
	 * @param k
	 *            integer value (axis index of the projection)
	 * @return
	 */
	public double getEigenvalueTot(int k) {

		return Math.rint(info[k] * 100 / sum(info));

	}

	/**
	 * Positions of the last coordinates of the projection vectors
	 * (eigenvectors)
	 * 
	 * @param k
	 *            - integer value (axis index)
	 * @param i
	 *            - index (position)
	 * @return
	 */
	public double getCoordinate(int k, int i) {

		return inv_center_reduce(U[k])[i];

	}

	/**
	 * Get covariance matrix
	 * 
	 * @return
	 */
	public double[][] getCovariance() {

		return cov;

	}

	/**
	 * Get transpose
	 * 
	 * @return
	 */
	public double[] getD() {

		double[] x = new double[info.length];
		for (int i = 0; i < info.length; i++)
			x[i] = Math.rint(info[0] * 100 / sum(info));

		return x;
	}

	/**
	 * Get means for the component k
	 * 
	 * @param k
	 *            index of the axis
	 * @return
	 */
	public double getMean(int k) {

		return meanX[k];
	}

	/**
	 * Get standard deviations
	 * 
	 * @param k
	 *            index of the axis
	 * @return
	 */
	public double getStd(int k) {

		return stdevX[k];
	}

	public static void main(String[] args) {
		double[][] xinit = random(1000, 2, 0, 10);

		// artificial initialization of relations
		double[][] x = new double[xinit.length][];
		for (int i = 0; i < x.length; i++)
			x[i] = new double[] { xinit[i][0] + xinit[i][1], xinit[i][1] };

		PCA pca = new PCA(x);
		System.out.println(pca.toString());

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

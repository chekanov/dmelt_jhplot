/*
*  Permission to use, copy and modify this software and its documentation for 
*  NON-COMMERCIAL purposes is granted, without fee, provided that an acknowledgement to the authors, 
*  Craig Levy and Sergei Chekanov appears in all copies and associated documentation or publications,
*  as well as the notice that this is a part of DataMelt project.
*  Redistributions of the source code, or parts of the source codes, must retain 
*  the copyright notice, this list of conditions and the following disclaimer 
*  (all at the top of each source code) and requires the written permission of Craig Levy and Sergei Chekanov
*  Redistribution in binary form of all or parts of a class must reproduce the copyright notice, 
*  this list of conditions and the following disclaimer in the documentation and/or other materials provided with the 
*  distribution and requires the written permission of  Craig levy and Sergei Chekanov
*/


package jhplot.stat;

import jhplot.F1D;
import jhplot.P1D;
import jhplot.gui.HelpBrowser;

/**
 * Shape identification based on a linear regression It calculates
 * eccentricities in the transverse and longitudinal directions, as well as the
 * size of the objects. Used used for identification of statistical patterns.
 * Data points can have weights.
 * 
 * @author S.Chekanov, C.Levy
 * 
 */
public class StatShape {

        private double x[];
        private double y[];
        private double w[];
        private double a, b, a0, b0, newa, newa0, newb, newb0;
        private int n;
        private int choice;
        private double wm_x;
        private double wm_y;
        private double W = 0;
        private int Q = 0;
        private double Fmax;
        private double wm[];
        private double[][] points;
        private double[][] NewPoints;
        private double[][] R;
        private double[][] dist;

        private double minorLength, majorLength, minorLength_meth2, majorLength_meth2, majorLength1;
        private double majorLength2, minorLength1, minorLength2, nq_minorLength, nq_majorLength;
        private double nq_minorLength_meth2, nq_majorLength_meth2, GlobalMajorLength, GlobalMinorLength;
        private double nq_majorLength1, nq_majorLength2, nq_minorLength1, nq_minorLength2;
        private double[][] wwm;
        private double[][] wwm1;
        private double[][] wwm_a;
        private double[][] wwm1_a;
        private double Wp[];
        private double[] covariance;
        double chisq2;


	/**
	 * Perform a linear regression analysis with weights and prepare for a shape
	 * identification.
	 * 
	 * @param x
	 *            array in x
	 * @param y
	 *            array in y
	 * @param w
	 *            weight of the data point
	 * @param n
	 *            total number of points
	 */
	public StatShape(double[] x, double[] y, double[] w, int n) {

		double minX = 0;
		double maxX = 0;
		double minY = 0;
		double maxY = 0;
		// swap for the best fits
		for (int i = 0; i < n; i++) {
			minX = Math.min(minX, x[i]);
			maxX = Math.max(maxX, x[i]);
			minY = Math.min(minY, y[i]);
			maxY = Math.max(maxY, y[i]);
		}
		if (maxY - minY > maxX - minX) {
			for (int i = 0; i < n; i++) {
				double s = y[i];
				y[i] = x[i];
				x[i] = s;
			}
		}

		this.n = n;
		this.x = x;
		this.y = y;
		this.w = w;

	}

	/**
	 * Get data back used for fitting (can be inverted in Y and Y for best
	 * results).
	 */
	public P1D getData() {
		P1D p1 = new P1D("data");
		for (int i = 0; i < n; i++)
			p1.add(x[i], y[i]);
		return p1;
	}

	/**
	 * Get Chi2 of this fit
	 * 
	 * @return
	 */
	public double getChi2() {

		return chisq2;
	}

	/**
	 * Get covariance matrix. 0: cov_00, 1: cov_11, 2:cov_01
	 * 
	 * @return
	 */
	public double[] getCovariance() {

		return covariance;
	}



         /**
         * Process event.
         * Perform all calculations of eccentricities, axis lengths, centers, etc.
         *  @param option
         *        option =0 use weighted linear regression and weighted means in quadrants
         *               =1 use unweighted linear regression and weighted means in quadrants
         *               =2 use unweighted calculations for everything
         *
         * */
        public void process(int choice) {

                /* compute the weighted means and weighted deviations from the means */
                /* wm denotes a "weighted mean", wm(f) = (sum_i w_i f_i) / (sum_i w_i) */


                wm_x = 0;
                wm_y = 0;
                double wm_dx2 = 0;
                double wm_dxdy = 0;
                int i;

                if (choice == 1){
                //Perform UNweighted linear regression
                //System.out.println("UNWEIGHTED linear regression");
                for (i = 0; i < n; i++){
                                double wi = 1;
                                if (wi > 0){
                                        W += wi;
                                        Q += 1;
                                        wm_x += (x[i] - wm_x) * (wi / Q);
                                        wm_y += (y[i] - wm_y) * (wi / Q);
                                }
                        }
                        for (i = 0; i < n; i++){
                                double wi = 1;
                                if (wi > 0){
                                        double dx = x[i] - wm_x;
                                        double dy = y[i] - wm_y;
                                        wm_dx2 += (dx * dx) / n;
                                        wm_dxdy += (dx * dy) / n;
                                }
                        }
                }



                if (choice == 0){
                //Perform WEIGHTED linear regression
                //System.out.println("WEIGHTED linear regression");
                        for (i = 0; i < n; i++) {
                                double wi = w[i];
                                if (wi > 0) {
                                        W += wi;
                                        wm_x += (x[i] - wm_x) * (wi / W);
                                        wm_y += (y[i] - wm_y) * (wi / W);
                                }
                        }
                        for (i = 0; i < n; i++) {
                                double wi = w[i];
                                if (wi > 0) {
                                        double dx = x[i] - wm_x;
                                        double dy = y[i] - wm_y;
                                        wm_dx2 += (dx * dx * wi) / W;
                                        wm_dxdy += (dx * dy * wi) / W;
                                }
                        }
                }



                /* recalculate W in case of unweighted regression */
                W = 0;
                for (i = 0; i < n; i++){
                        W += w[i];
                }

                /* In terms of y = a + b x */
                double d2 = 0;
                b = wm_dxdy / wm_dx2;
                a = wm_y - wm_x * b;

                /* perpendicular line through weighted mean*/
                b0=-1/b;          // slope
                a0= wm_y-b0*wm_x; // intercept

                /*rotate axes by 45 deg. to define quadrants*/
                double rotang = Math.atan(b) + (Math.PI/ 4);
                double rotangp = Math.atan(b0) + (Math.PI / 4);
                newb = Math.tan(rotang);
                newb0 = Math.tan(rotangp);
                newa = wm_y - (newb * wm_x);
                newa0 = wm_y - (newb0 * wm_x);


                /*Covariance matrix*/
                double cov_00 = (1 / W) * (1 + wm_x * wm_x / wm_dx2);
                double cov_11 = 1 / (W * wm_dx2);
                double cov_01 = -wm_x / (W * wm_dx2);

                covariance = new double[3];
                covariance[0] = cov_00;
                covariance[1] = cov_11;
                covariance[2] = cov_01;

                for (i = 0; i < n; i++) {
                        double wi = w[i];

                        if (wi > 0) {
                                double dx = x[i] - wm_x;
                                double dy = y[i] - wm_y;
                                double d = dy - b * dx;
                                d2 += wi * d * d;
                        }
                }

                chisq2 = d2;


              processQuadrant();

        }





	/**
	 * Get fit parameters, the intercept (0) and the slope (1)
	 * 
	 * @return array with intercept (index 0) and the slop (index 1)
	 */
	public double[] getFitParameters() {
		double[] p = new double[2];
		p[0] = a;
		p[1] = b;
		return p;
	}

	/**
	 * Get a fit function after the linear regression (major axis).
	 * 
	 * @return fit function
	 */
	public F1D getFitFunction() {

		F1D f1 = new F1D("a+b*x", "a+b*x",-10, 10, false);
		f1.setPar("a", a);
		f1.setPar("b", b);
		f1.parse();

		return f1;
	}

	/**
	 * Get a fit function perpendicular to the linear regression line (minor
	 * axis).
	 * 
	 * @return fit function
	 */
	public F1D getFitFunctionPerp() {


		F1D f1 = new F1D("a+b*x","a+b*x", -10, 10, false);
		f1.setPar("a", a0);
		f1.setPar("b", b0);
		f1.parse();

		return f1;
	}

	/**
	 * Get fit parameters of a function perpendicular (minor) axis to the linear
	 * regression line the intercept (0) and the slope (1)
	 * 
	 * @return array with intercept (index 0) and the slop (index 1)
	 */
	public double[] getFitParametersPerp() {

		
		double[] p = new double[2];
		p[0] = a0;
		p[1] = b0;
		return p;
	}

	/**
	 * Parameters after rotation of major and minor axes by 45deg. To locate
	 * weighted centers in 4 quadrants first get new slopes
	 */
	public double[] getFitParametersRotate() {
		

		double[] p = new double[4];
		p[0] = newa;
		p[1] = newb;
		p[2] = newa0;
		p[3] = newb0;
		return p;
	}

	/**
	 * Get linear functions after rotation of major and minor axes by 45 deg to
	 * define the quadrants.
	 * 
	 * @return fit functions
	 */
	public F1D[] getFitFunctionQuadrants() {


		F1D f1 = new F1D("a+b*x", -10, 10, false);
		f1.setPar("a", newa);
		f1.setPar("b", newb);
		f1.parse();

		F1D f2 = new F1D("a+b*x", -10, 10, false);
		f2.setPar("a", newa0);
		f2.setPar("b", newb0);
		f2.parse();

		F1D[] ff = new F1D[2];
		ff[0] = f1;
		ff[1] = f2;

		return ff; 
	}

	/**
	 * Get weighted means in X and Y
	 * 
	 * @return array with weighted mean in X (index 0) and Y (index 1)
	 */
	public double[] getMeans() {
		double[] p = new double[2];
		p[0] = wm_x;
		p[1] = wm_y;
		return p;
	}


/**
         * Get the centers in non-quadrant method. 
         * There should be 4 cluster centers separated by the major
         * and minor vectors
         * 
         * @param k
         *            current center centers (1,2,3,4) 
         * @return 2D array with X and Y positions
         * 
         */
        public double[] getCenters(int k) {

                // eccentricity
                double[] p = new double[2];
                p[0] = wwm_a[k][0];
                p[1] = wwm_a[k][1];

                return p;
        }





       /**
         * Process quadrants
         * Perform all calculations of eccentricities, axis lengths, centers, etc.
         * */
        private boolean processQuadrant() {

                int i, j;
                dist = new double[2][n];
                points = new double[2][n];
                NewPoints = new double[2][n];
                R = new double[2][2];

                //map points onto basis of major/minor axes using rotation & translation from points[][]->dist[][].
                for (i = 0; i < n; i++){
                        points[0][i] = x[i];
                        points[1][i] = y[i];
                }
                double theta = Math.atan(b);
                R[0][0] = Math.cos(theta);
                R[0][1] = Math.sin(theta);
                R[1][0] = -Math.sin(theta);
                R[1][1] = Math.cos(theta);
                for (i = 0; i < 2; i++){
                        for (j = 0; j < n; j++){
                                NewPoints[0][j] = points[0][j] - wm_x;
                                NewPoints[1][j] = points[1][j] - wm_y;
                                dist[i][j] = (R[i][0] * NewPoints[0][j]) + (R[i][1] * NewPoints[1][j]);
                        }
                }

                wwm = new double[4][2];           //coordinates of weighted centers
                Wp = new double[4];              //total weight in quadrant
                wwm1 = new double[4][2];
                wwm_a = new double[4][2];
                wwm1_a = new double[4][2];

                for (i = 0; i < 4; i++){
                        for (j = 0; j < 2; j++) {
                                wwm[i][j] = 0;
                                wwm1[i][j] = 0;
                                wwm_a[i][j] = 0;
                                wwm1_a[i][j] = 0;
                        }
                }
                for (i = 0; i < 4; i++){
                        Wp[i] = 0;
                }


                double aa[] = getFitParametersPerp();

                //seperate all points by axes.  find centers above and below each axis.
                for (i = 0; i < n; i++) {
                        double wi = w[i];
                        if (wi > 0) {
                                //[0] above major axis
                                 if (y[i] > a + b * x[i]) {
                                        Wp[0] += wi;
                                        wwm_a[0][0] += (x[i] - wwm_a[0][0]) * (wi / Wp[0]);
                                        wwm_a[0][1] += (y[i] - wwm_a[0][1]) * (wi / Wp[0]);
                                        wwm1_a[0][0] += (dist[0][i] - wwm1_a[0][0]) * (wi / Wp[0]);
                                        wwm1_a[0][1] += (dist[1][i] - wwm1_a[0][1]) * (wi / Wp[0]);
                                }
                               // [1] below major axis
                                if (y[i] < a + b * x[i]){
                                        Wp[1] += wi;
                                        wwm_a[1][0] += (x[i] - wwm_a[1][0]) * (wi / Wp[1]);
                                        wwm_a[1][1] += (y[i] - wwm_a[1][1]) * (wi / Wp[1]);
                                        wwm1_a[1][0] += (dist[0][i] - wwm1_a[1][0]) * (wi / Wp[1]);
                                        wwm1_a[1][1] += (dist[1][i] - wwm1_a[1][1]) * (wi / Wp[1]);
                                }
                                // [2] above minor axis
                                if (y[i] > a0 + b0 * x[i]){
                                        Wp[2] +=wi;
                                        wwm_a[2][0] += (x[i] - wwm_a[2][0]) * (wi / Wp[2]);
                                        wwm_a[2][1] += (y[i] - wwm_a[2][1]) * (wi / Wp[2]);
                                        wwm1_a[2][0] += (dist[0][i] - wwm1_a[2][0]) * (wi / Wp[2]);
                                        wwm1_a[2][1] += (dist[1][i] - wwm1_a[2][1]) * (wi / Wp[2]);
                                }
                                // [3] below minor axis
                                if (y[i] < a0 + b0 * x[i]){
                                        Wp[3] += wi;
                                        wwm_a[3][0] += (x[i] - wwm_a[3][0]) * (wi / Wp[3]);
                                        wwm_a[3][1] += (y[i] - wwm_a[3][1]) * (wi / Wp[3]);
                                        wwm1_a[3][0] += (dist[0][i] - wwm1_a[3][0]) * (wi / Wp[3]);
                                        wwm1_a[3][1] += (dist[1][i] - wwm1_a[3][1]) * (wi / Wp[3]);
                                }
                        }
                }




 //seperate all points by quadrant, find quadrant centers
                for (i = 0; i < 4; i++){
                        Wp[i] = 0;
                }
                for (i = 0; i < n; i++) {
                        double wi = w[i];
                        if (wi > 0) {

                                // [0] in top quadrant
                                if (y[i] > newa + newb * x[i] && y[i] > newa0 + newb0 * x[i]) {
                                        Wp[0] += wi;
                                        wwm[0][0] += (x[i] - wwm[0][0]) * (wi / Wp[0]);
                                        wwm[0][1] += (y[i] - wwm[0][1]) * (wi / Wp[0]);
                                        wwm1[0][0] += (dist[0][i] - wwm1[0][0]) * (wi / Wp[0]);
                                        wwm1[0][1] += (dist[1][i] - wwm1[0][1]) * (wi / Wp[0]);
                                }
                                // [1] in left quadrant
                                if (y[i] > newa + newb * x[i] && y[i] < newa0 + newb0 * x[i]){
                                        Wp[1] += wi;
                                        wwm[1][0] += (x[i] - wwm[1][0]) * (wi / Wp[1]);
                                        wwm[1][1] += (y[i] - wwm[1][1]) * (wi / Wp[1]);
                                        wwm1[1][0] += (dist[0][i] - wwm1[1][0]) * (wi / Wp[1]);
                                        wwm1[1][1] += (dist[1][i] - wwm1[1][1]) * (wi / Wp[1]);
                                }
                                // [2] in bottom quadrant
                                if (y[i] < newa + newb * x[i] && y[i] < newa0 + newb0 * x[i]){
                                        Wp[2] +=wi;
                                        wwm[2][0] += (x[i] - wwm[2][0]) * (wi / Wp[2]);
                                        wwm[2][1] += (y[i] - wwm[2][1]) * (wi / Wp[2]);
                                        wwm1[2][0] += (dist[0][i] - wwm1[2][0]) * (wi / Wp[2]);
                                        wwm1[2][1] += (dist[1][i] - wwm1[2][1]) * (wi / Wp[2]);
                                }
                                // [3] in right quadrant
                                if (y[i] < newa + newb * x[i] && y[i] > newa0 + newb0 * x[i]){
                                        Wp[3] += wi;
                                        wwm[3][0] += (x[i] - wwm[3][0]) * (wi / Wp[3]);
                                        wwm[3][1] += (y[i] - wwm[3][1]) * (wi / Wp[3]);
                                        wwm1[3][0] += (dist[0][i] - wwm1[3][0]) * (wi / Wp[3]);
                                        wwm1[3][1] += (dist[1][i] - wwm1[3][1]) * (wi / Wp[3]);
                                }


               }
                }


                /******get Fmax parameter*************/
                double wmax = 0;
                for (i = 0; i < n; i ++){
                        if (w[i] > wmax){
                                wmax = w[i];
                        }
                }
                Fmax = wmax / W;

  // distances between opposing centers (length of axes)
                //method 1
                double a1 = Math.sqrt((wwm[0][0] - wwm[2][0]) * (wwm[0][0] - wwm[2][0])
                                + (wwm[0][1] - wwm[2][1]) * (wwm[0][1] - wwm[2][1]));
                double a2 = Math.sqrt((wwm[1][0] - wwm[3][0]) * (wwm[1][0] - wwm[3][0])
                                + (wwm[1][1] - wwm[3][1]) * (wwm[1][1] - wwm[3][1]));

                //method 2
                double a7 = Math.sqrt((wwm1[0][1] - wwm1[2][1]) * (wwm1[0][1] - wwm1[2][1]));
                double a8 = Math.sqrt((wwm1[1][0] - wwm1[3][0]) * (wwm1[1][0] - wwm1[3][0]));

                // distance between the global mean and quadrant centers.
                // top semiaxis
                double a3 = Math.sqrt((wm_x - wwm[0][0]) * (wm_x - wwm[0][0])
                                + (wm_y - wwm[0][1]) * (wm_y - wwm[0][1]));
                //left semiaxis
                double a4 = Math.sqrt((wm_x - wwm[1][0]) * (wm_x - wwm[1][0])
                                + (wm_y - wwm[1][1]) * (wm_y - wwm[1][1]));
                //bottom semiaxis
                double a5 = Math.sqrt((wm_x - wwm[2][0]) * (wm_x - wwm[2][0])
                                + (wm_y - wwm[2][1]) * (wm_y - wwm[2][1]));
                //right semiaxis
                double a6 = Math.sqrt((wm_x - wwm[3][0]) * (wm_x - wwm[3][0])
                                + (wm_y - wwm[3][1]) * (wm_y - wwm[3][1]));

                //distance between non-quadrant centers
                double a9 = Math.sqrt((wwm_a[0][0] - wwm_a[1][0]) * (wwm_a[0][0] - wwm_a[1][0])
                                + (wwm_a[0][1] - wwm_a[1][1]) * (wwm_a[0][1] - wwm_a[1][1]));
                double a10 = Math.sqrt((wwm_a[2][0] - wwm_a[3][0]) * (wwm_a[2][0] - wwm_a[3][0])
                                + (wwm_a[2][1] - wwm_a[3][1]) * (wwm_a[2][1] - wwm_a[3][1]));

                //distance between non-quadrant centers method 2
                double a11 = Math.sqrt((wwm1_a[0][1] - wwm1_a[1][1]) * (wwm1_a[0][1] - wwm1_a[1][1]));
                double a12 = Math.sqrt((wwm1_a[2][0] - wwm1_a[3][0]) * (wwm1_a[2][0] - wwm1_a[3][0]));

                //distance between global mean and non-quadrant centers
                //top semiaxis
                double a13 = Math.sqrt((wm_x - wwm_a[0][0]) * (wm_x - wwm_a[0][0])
                                + (wm_y - wwm_a[0][1]) * (wm_y - wwm_a[0][1]));
                //bottom semiaxis
                double a14 = Math.sqrt((wm_x - wwm_a[1][0]) * (wm_x - wwm_a[1][0])
                                + (wm_y - wwm_a[1][1]) * (wm_y - wwm_a[1][1]));
                //left semiaxis
                double a15 = Math.sqrt((wm_x - wwm_a[2][0]) * (wm_x - wwm_a[2][0])
                                + (wm_y - wwm_a[2][1]) * (wm_y - wwm_a[2][1]));
                //right semiaxis
                double a16 = Math.sqrt((wm_x - wwm_a[3][0]) * (wm_x - wwm_a[3][0])
                                + (wm_y - wwm_a[3][1]) *  (wm_y - wwm_a[3][1]));


 // get eccentricity between the major and minor axis (method 1)
                minorLength = a1;
                majorLength = a2;
                if (a1 > a2) {
                        minorLength = a2;
                        majorLength = a1;
                }

                //get eccentricity (method 2)
                minorLength_meth2 = a7;
                majorLength_meth2 = a8;
                if (a7 > a8) {
                        minorLength_meth2 = a8;
                        majorLength_meth2 = a7;
                }

                // get eccentricity between the 2 major vectors
                majorLength1 = a4;
                majorLength2 = a6;
                if (a4 > a6) {
                        majorLength1 = a6;
                        majorLength2 = a4;

                }

                // get eccentricity between the 2 minor vectors
                minorLength1 = a3;
                minorLength2 = a5;
                if (a3 > a5) {
                        minorLength1 = a5;
                        minorLength2 = a3;

                }


 //get non-quadrant eccentricity
                nq_minorLength = a9;
                nq_majorLength = a10;
                if (a9 > a10){
                        nq_minorLength = a10;
                        nq_majorLength = a9;
                }

                //get non-quadrant eccentricity method 2
                nq_minorLength_meth2 = a11;
                nq_majorLength_meth2 = a12;
                if (a11 > a12){
                        nq_minorLength_meth2 = a12;
                        nq_majorLength_meth2 = a11;
                }

                //get non-quadrant major eccentricity
                nq_majorLength1 = a15;
                nq_majorLength2 = a16;
                if (a15 > a16){
                        nq_majorLength1 = a16;
                        nq_majorLength2 = a15;
                }

                //get non-quadrant minor eccentricity
                nq_minorLength1 = a13;
                nq_minorLength2 = a14;
                if (a13 > a14){
                        nq_minorLength1 = a14;
                        nq_minorLength2 = a13;
                }


                /*get absolute jet size. find max dist along each axis*/
                double minx = dist[0][0];
                double maxx = dist[0][0];
                double miny = dist[0][0];
                double maxy = dist[0][0];
                int l = 0;
                int q = 0;
                int r = 0;
                int s = 0;
                //find x' and y' points furthest from mean
                for (i = 1; i < n; i++){
                        if (dist[0][i] > maxx){
                                maxx = dist[0][i];
                                l = i;
                        }
                        if (dist[0][i] < minx){
                                minx = dist[0][i];
                                q = i;
                        }
                        if (dist[1][i] > maxy){
                                maxy = dist[1][i];
                                r = i;
                        }
                        if (dist[1][i] < miny){
                                miny = dist[1][i];
                                s = i;
                        }
                }
                GlobalMajorLength = dist[0][l] - dist[0][q];
                GlobalMinorLength = dist[1][r] - dist[1][s];
        return true;
        }





        /**
        * Return all shape parameters
        *
        * @return array with shape characteristics
        *        p[0] = majorLength; 
        *        p[1] = minorLength;
        *        p[2] = 1 - minorLength / majorLength;
        *        p[3] = majorLength1;
        *        p[4] = majorLength2;
        *        p[5] = 1 - majorLength1 / majorLength2;
        *        p[6] = minorLength1;
        *        p[7] = minorLength2;
        *        p[8] = 1 - minorLength1 / minorLength2;
        *        p[9] = GlobalMajorLength;
        *        p[10] = GlobalMinorLength;
        *        p[11] = majorLength_meth2;
        *        p[12] = minorLength_meth2;
        *        p[13] = 1 - minorLength_meth2 / majorLength_meth2;
        *        p[14] = nq_majorLength;
        *        p[15] = nq_minorLength;
        *        p[16] = 1 - nq_minorLength / nq_majorLength;
        *        p[17] = nq_majorLength_meth2;
        *        p[18] = nq_minorLength_meth2;
        *        p[19] = 1 - nq_minorLength_meth2 / nq_majorLength_meth2;
        *        p[20] = 1 - nq_majorLength1 / nq_majorLength2;
        *        p[21] = 1 - nq_minorLength1 / nq_minorLength2;
        *        p[22] = Fmax;
        **/
        public double[] getSummary() {

                // prepare outputs
                double[] p = new double[23];
                p[0] = majorLength;
                p[1] = minorLength;
                p[2] = 1 - minorLength / majorLength;
                p[3] = majorLength1;
                p[4] = majorLength2;
                p[5] = 1 - majorLength1 / majorLength2;
                p[6] = minorLength1;
                p[7] = minorLength2;
                p[8] = 1 - minorLength1 / minorLength2;
                p[9] = GlobalMajorLength;
                p[10] = GlobalMinorLength;
                p[11] = majorLength_meth2;
                p[12] = minorLength_meth2;
                p[13] = 1 - minorLength_meth2 / majorLength_meth2;
                p[14] = nq_majorLength;
                p[15] = nq_minorLength;
                p[16] = 1 - nq_minorLength / nq_majorLength;
                p[17] = nq_majorLength_meth2;
                p[18] = nq_minorLength_meth2;
                p[19] = 1 - nq_minorLength_meth2 / nq_majorLength_meth2;
                p[20] = 1 - nq_majorLength1 / nq_majorLength2;
                p[21] = 1 - nq_minorLength1 / nq_minorLength2;
                p[22] = Fmax;
                return p;

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

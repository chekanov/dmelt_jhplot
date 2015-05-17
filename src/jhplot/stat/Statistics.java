/*  
 * Copyright 2010 jwork.org
 * Copyright 2001 University of Waikato
 * Copyright 1999 CERN - European Organization for Nuclear Research.
 * Permission to use, copy, modify, distribute and sell this software and its documentation for
 * any purpose is hereby granted without fee, provided that the above copyright notice appear
 * in all copies and that both that copyright notice and this permission notice appear in
 * supporting documentation. 
 */
package jhplot.stat;

import jhplot.gui.HelpBrowser;

/**
 * A static class for statistical calculations.
 * 
 * 
 * 
 * @author S.Chekanov & University of Waikato
 * 
 */

public class Statistics {

	/**
	 * Get mean value
	 * 
	 * @param v
	 *            vector
	 */
	public static double mean(double[] v) {
		double mean = 0;
		int m = v.length;
		for (int i = 0; i < m; i++)
			mean += v[i];
		mean /= (double) m;
		return mean;
	}

	/**
	 * Get mean
	 * 
	 * @param v
	 *            2D array
	 * @return
	 */
	public static double[] mean(double[][] v) {
		int m = v.length;
		int n = v[0].length;
		double[] mean = new double[n];
		for (int i = 0; i < m; i++)
			for (int j = 0; j < n; j++)
				mean[j] += v[i][j];
		for (int j = 0; j < n; j++)
			mean[j] /= (double) m;
		return mean;
	}

	/**
	 * Standard deviation
	 * 
	 * @param v
	 *            vector
	 * @return
	 */
	public static double stddeviation(double[] v) {
		return Math.sqrt(variance(v));
	}

	/**
	 * Variance
	 * 
	 * @param v
	 * @return vector
	 */
	public static double variance(double[] v) {
		double var;
		int degrees = (v.length - 1);
		int m = v.length;
		double c;
		double s;
		c = 0;
		s = 0;
		for (int k = 0; k < m; k++)
			s += v[k];
		s = s / m;
		for (int k = 0; k < m; k++)
			c += (v[k] - s) * (v[k] - s);
		var = c / degrees;
		return var;
	}

	/**
	 * Standard deviation
	 * 
	 * @param v
	 * @return
	 */
	public static double[] stddeviation(double[][] v) {
		double[] var = variance(v);
		for (int i = 0; i < var.length; i++)
			var[i] = Math.sqrt(var[i]);
		return var;
	}

	/**
	 * Variance
	 * 
	 * @param v
	 *            vector
	 * @return
	 */
	public static double[] variance(double[][] v) {
		int m = v.length;
		int n = v[0].length;
		double[] var = new double[n];
		int degrees = (m - 1);
		double c;
		double s;
		for (int j = 0; j < n; j++) {
			c = 0;
			s = 0;
			for (int k = 0; k < m; k++)
				s += v[k][j];
			s = s / m;
			for (int k = 0; k < m; k++)
				c += (v[k][j] - s) * (v[k][j] - s);
			var[j] = c / degrees;
		}
		return var;
	}

	/**
	 * Covariance
	 * 
	 * @param v1
	 *            first vector
	 * @param v2
	 *            second vector
	 * @return
	 */
	public static double covariance(double[] v1, double[] v2) {
		int m = v1.length;
		double X;
		int degrees = (m - 1);
		double c;
		double s1;
		double s2;
		c = 0;
		s1 = 0;
		s2 = 0;
		for (int k = 0; k < m; k++) {
			s1 += v1[k];
			s2 += v2[k];
		}
		s1 = s1 / m;
		s2 = s2 / m;
		for (int k = 0; k < m; k++)
			c += (v1[k] - s1) * (v2[k] - s2);
		X = c / degrees;
		return X;
	}

	/**
	 * Covariance
	 * 
	 * @param v1
	 *            first 2D array
	 * @param v2
	 *            second 2D array
	 * @return
	 */
	public static double[][] covariance(double[][] v1, double[][] v2) {
		int m = v1.length;
		int n1 = v1[0].length;
		int n2 = v2[0].length;
		double[][] X = new double[n1][n2];
		int degrees = (m - 1);
		double c;
		double s1;
		double s2;
		for (int i = 0; i < n1; i++) {
			for (int j = 0; j < n2; j++) {
				c = 0;
				s1 = 0;
				s2 = 0;
				for (int k = 0; k < m; k++) {
					s1 += v1[k][i];
					s2 += v2[k][j];
				}
				s1 = s1 / m;
				s2 = s2 / m;
				for (int k = 0; k < m; k++)
					c += (v1[k][i] - s1) * (v2[k][j] - s2);
				X[i][j] = c / degrees;
			}
		}
		return X;
	}

	/**
	 * Covariance
	 * 
	 * @param v
	 * @return
	 */
	public static double[][] covariance(double[][] v) {
		int m = v.length;
		int n = v[0].length;
		double[][] X = new double[n][n];
		int degrees = (m - 1);
		double c;
		double s1;
		double s2;
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				c = 0;
				s1 = 0;
				s2 = 0;
				for (int k = 0; k < m; k++) {
					s1 += v[k][i];
					s2 += v[k][j];
				}
				s1 = s1 / m;
				s2 = s2 / m;
				for (int k = 0; k < m; k++)
					c += (v[k][i] - s1) * (v[k][j] - s2);
				X[i][j] = c / degrees;
			}
		}
		return X;
	}

	/**
	 * Correlation coefficient, covariance(v1, v2) / Math.sqrt(variance(v1) *
	 * variance(v2)
	 * 
	 * @param v1
	 *            first vector
	 * @param v2
	 *            second vector
	 * @return
	 */
	public static double correlation(double[] v1, double[] v2) {
		return covariance(v1, v2) / Math.sqrt(variance(v1) * variance(v2));
	}

	/**
	 * Correlation coefficient, covariance(v1, v2) / Math.sqrt(variance(v1) *
	 * variance(v2)
	 * 
	 * @param v1
	 *            first vector
	 * @param v2
	 *            second vector
	 * @return
	 */
	public static double[][] correlation(double[][] v1, double[][] v2) {
		double[] Varv1 = variance(v1);
		double[] Varv2 = variance(v2);
		double[][] cov = covariance(v1, v2);
		for (int i = 0; i < cov.length; i++)
			for (int j = 0; j < cov[i].length; j++)
				cov[i][j] = cov[i][j] / Math.sqrt(Varv1[i] * Varv2[j]);
		return cov;
	}

	/**
	 * Correlation
	 * 
	 * @param v
	 * @return
	 */
	public static double[][] correlation(double[][] v) {
		int m = v.length;
		int n = v[0].length;
		double[][] X = new double[n][n];
		double[][] V = new double[n][n];
		int degrees = (m - 1);
		double c;
		double s1;
		double s2;
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				c = 0;
				s1 = 0;
				s2 = 0;
				for (int k = 0; k < m; k++) {
					s1 += v[k][i];
					s2 += v[k][j];
				}
				s1 = s1 / m;
				s2 = s2 / m;
				for (int k = 0; k < m; k++)
					c += (v[k][i] - s1) * (v[k][j] - s2);
				V[i][j] = c / degrees;
			}
		}
		for (int i = 0; i < n; i++)
			for (int j = 0; j < n; j++)
				X[i][j] = V[i][j] / Math.sqrt(V[i][i] * V[j][j]);
		return X;
	}

	/**
	 * Computes probability of F-ratio.
	 * 
	 * @param F
	 *            the F-ratio
	 * @param df1
	 *            the first number of degrees of freedom
	 * @param df2
	 *            the second number of degrees of freedom
	 * @return the probability of the F-ratio.
	 */
	public static double FProbability(double F, int df1, int df2) {

		return incompleteBeta(df2 / 2.0, df1 / 2.0, df2 / (df2 + df1 * F));
	}

	/**
	 * Returns the Incomplete Beta Function evaluated from zero to <tt>xx</tt>.
	 * 
	 * @param aa
	 *            the alpha parameter of the beta distribution.
	 * @param bb
	 *            the beta parameter of the beta distribution.
	 * @param xx
	 *            the integration end point.
	 */
	public static double incompleteBeta(double aa, double bb, double xx) {

		double a, b, t, x, xc, w, y;
		boolean flag;

		if (aa <= 0.0 || bb <= 0.0) {
			System.err.println("ibeta: Domain error!");
			return 0;
		}

		if ((xx <= 0.0) || (xx >= 1.0)) {
			if (xx == 0.0)
				return 0.0;
			if (xx == 1.0)
				return 1.0;
			System.err.println("ibeta: Domain error!");
		}

		flag = false;
		if ((bb * xx) <= 1.0 && xx <= 0.95) {
			t = powerSeries(aa, bb, xx);
			return t;
		}

		w = 1.0 - xx;

		/* Reverse a and b if x is greater than the mean. */
		if (xx > (aa / (aa + bb))) {
			flag = true;
			a = bb;
			b = aa;
			xc = xx;
			x = w;
		} else {
			a = aa;
			b = bb;
			xc = w;
			x = xx;
		}

		if (flag && (b * x) <= 1.0 && x <= 0.95) {
			t = powerSeries(a, b, x);
			if (t <= MACHEP)
				t = 1.0 - MACHEP;
			else
				t = 1.0 - t;
			return t;
		}

		/* Choose expansion for better convergence. */
		y = x * (a + b - 2.0) - (a - 1.0);
		if (y < 0.0)
			w = incompleteBetaFraction1(a, b, x);
		else
			w = incompleteBetaFraction2(a, b, x) / xc;

		/*
		 * Multiply w by the factor a b _ _ _ x (1-x) | (a+b) / ( a | (a) | (b)
		 * ) .
		 */

		y = a * Math.log(x);
		t = b * Math.log(xc);
		if ((a + b) < MAXGAM && Math.abs(y) < MAXLOG && Math.abs(t) < MAXLOG) {
			t = Math.pow(xc, b);
			t *= Math.pow(x, a);
			t /= a;
			t *= w;
			t *= gamma(a + b) / (gamma(a) * gamma(b));
			if (flag) {
				if (t <= MACHEP)
					t = 1.0 - MACHEP;
				else
					t = 1.0 - t;
			}
			return t;
		}
		/* Resort to logarithms. */
		y += t + lnGamma(a + b) - lnGamma(a) - lnGamma(b);
		y += Math.log(w / a);
		if (y < MINLOG)
			t = 0.0;
		else
			t = Math.exp(y);

		if (flag) {
			if (t <= MACHEP)
				t = 1.0 - MACHEP;
			else
				t = 1.0 - t;
		}
		return t;
	}

	/**
	 * Power series for incomplete beta integral. Use when b*x is small and x
	 * not too close to 1.
	 */
	public static double powerSeries(double a, double b, double x) {

		double s, t, u, v, n, t1, z, ai;

		ai = 1.0 / a;
		u = (1.0 - b) * x;
		v = u / (a + 1.0);
		t1 = v;
		t = u;
		n = 2.0;
		s = 0.0;
		z = MACHEP * ai;
		while (Math.abs(v) > z) {
			u = (n - b) * x / n;
			t *= u;
			v = t / (a + n);
			s += v;
			n += 1.0;
		}
		s += t1;
		s += ai;

		u = a * Math.log(x);
		if ((a + b) < MAXGAM && Math.abs(u) < MAXLOG) {
			t = gamma(a + b) / (gamma(a) * gamma(b));
			s = s * t * Math.pow(x, a);
		} else {
			t = lnGamma(a + b) - lnGamma(a) - lnGamma(b) + u + Math.log(s);
			if (t < MINLOG)
				s = 0.0;
			else
				s = Math.exp(t);
		}
		return s;
	}

	/**
	 * Returns natural logarithm of gamma function.
	 * 
	 * @param x
	 *            the value
	 * @return natural logarithm of gamma function
	 */
	public static double lnGamma(double x) {

		double p, q, w, z;

		double A[] = { 8.11614167470508450300E-4, -5.95061904284301438324E-4,
				7.93650340457716943945E-4, -2.77777777730099687205E-3,
				8.33333333333331927722E-2 };
		double B[] = { -1.37825152569120859100E3, -3.88016315134637840924E4,
				-3.31612992738871184744E5, -1.16237097492762307383E6,
				-1.72173700820839662146E6, -8.53555664245765465627E5 };
		double C[] = {
		/* 1.00000000000000000000E0, */
		-3.51815701436523470549E2, -1.70642106651881159223E4,
				-2.20528590553854454839E5, -1.13933444367982507207E6,
				-2.53252307177582951285E6, -2.01889141433532773231E6 };

		if (x < -34.0) {
			q = -x;
			w = lnGamma(q);
			p = Math.floor(q);
			if (p == q)
				throw new ArithmeticException("lnGamma: Overflow");
			z = q - p;
			if (z > 0.5) {
				p += 1.0;
				z = p - q;
			}
			z = q * Math.sin(Math.PI * z);
			if (z == 0.0)
				throw new ArithmeticException("lnGamma: Overflow");
			z = LOGPI - Math.log(z) - w;
			return z;
		}

		if (x < 13.0) {
			z = 1.0;
			while (x >= 3.0) {
				x -= 1.0;
				z *= x;
			}
			while (x < 2.0) {
				if (x == 0.0)
					throw new ArithmeticException("lnGamma: Overflow");
				z /= x;
				x += 1.0;
			}
			if (z < 0.0)
				z = -z;
			if (x == 2.0)
				return Math.log(z);
			x -= 2.0;
			p = x * polevl(x, B, 5) / p1evl(x, C, 6);
			return (Math.log(z) + p);
		}

		if (x > 2.556348e305)
			throw new ArithmeticException("lnGamma: Overflow");

		q = (x - 0.5) * Math.log(x) - x + 0.91893853320467274178;

		if (x > 1.0e8)
			return (q);

		p = 1.0 / (x * x);
		if (x >= 1000.0)
			q += ((7.9365079365079365079365e-4 * p - 2.7777777777777777777778e-3)
					* p + 0.0833333333333333333333)
					/ x;
		else
			q += polevl(p, A, 4) / x;
		return q;
	}

	/**
	 * Evaluates the given polynomial of degree <tt>N</tt> at <tt>x</tt>.
	 * Evaluates polynomial when coefficient of N is 1.0. Otherwise same as
	 * <tt>polevl()</tt>.
	 * 
	 * <pre>
	 *                     2          N
	 * y  =  C  + C x + C x  +...+ C x
	 *        0    1     2          N
	 * 
	 * Coefficients are stored in reverse order:
	 * 
	 * coef[0] = C  , ..., coef[N] = C  .
	 *            N                   0
	 * </pre>
	 * 
	 * The function <tt>p1evl()</tt> assumes that <tt>coef[N] = 1.0</tt> and is
	 * omitted from the array. Its calling arguments are otherwise the same as
	 * <tt>polevl()</tt>.
	 * <p>
	 * In the interest of speed, there are no checks for out of bounds
	 * arithmetic.
	 * 
	 * @param x
	 *            argument to the polynomial.
	 * @param coef
	 *            the coefficients of the polynomial.
	 * @param N
	 *            the degree of the polynomial.
	 */
	public static double p1evl(double x, double coef[], int N) {

		double ans;
		ans = x + coef[0];

		for (int i = 1; i < N; i++)
			ans = ans * x + coef[i];

		return ans;
	}

	/**
	 * Returns the Gamma function of the argument.
	 */
	public static double gamma(double x) {

		double P[] = { 1.60119522476751861407E-4, 1.19135147006586384913E-3,
				1.04213797561761569935E-2, 4.76367800457137231464E-2,
				2.07448227648435975150E-1, 4.94214826801497100753E-1,
				9.99999999999999996796E-1 };
		double Q[] = { -2.31581873324120129819E-5, 5.39605580493303397842E-4,
				-4.45641913851797240494E-3, 1.18139785222060435552E-2,
				3.58236398605498653373E-2, -2.34591795718243348568E-1,
				7.14304917030273074085E-2, 1.00000000000000000320E0 };

		double p, z;
		int i;

		double q = Math.abs(x);

		if (q > 33.0) {
			if (x < 0.0) {
				p = Math.floor(q);
				if (p == q)
					throw new ArithmeticException("gamma: overflow");
				i = (int) p;
				z = q - p;
				if (z > 0.5) {
					p += 1.0;
					z = q - p;
				}
				z = q * Math.sin(Math.PI * z);
				if (z == 0.0)
					throw new ArithmeticException("gamma: overflow");
				z = Math.abs(z);
				z = Math.PI / (z * stirlingFormula(q));

				return -z;
			} else {
				return stirlingFormula(x);
			}
		}

		z = 1.0;
		while (x >= 3.0) {
			x -= 1.0;
			z *= x;
		}

		while (x < 0.0) {
			if (x == 0.0) {
				throw new ArithmeticException("gamma: singular");
			} else if (x > -1.E-9) {
				return (z / ((1.0 + 0.5772156649015329 * x) * x));
			}
			z /= x;
			x += 1.0;
		}

		while (x < 2.0) {
			if (x == 0.0) {
				throw new ArithmeticException("gamma: singular");
			} else if (x < 1.e-9) {
				return (z / ((1.0 + 0.5772156649015329 * x) * x));
			}
			z /= x;
			x += 1.0;
		}

		if ((x == 2.0) || (x == 3.0))
			return z;

		x -= 2.0;
		p = polevl(x, P, 6);
		q = polevl(x, Q, 7);
		return z * p / q;
	}

	/**
	 * Returns the Gamma function computed by Stirling's formula. The polynomial
	 * STIR is valid for 33 <= x <= 172.
	 */
	public static double stirlingFormula(double x) {

		double STIR[] = { 7.87311395793093628397E-4,
				-2.29549961613378126380E-4, -2.68132617805781232825E-3,
				3.47222221605458667310E-3, 8.33333333333482257126E-2, };
		double MAXSTIR = 143.01608;

		double w = 1.0 / x;
		double y = Math.exp(x);

		w = 1.0 + w * polevl(w, STIR, 4);

		if (x > MAXSTIR) {
			/* Avoid overflow in Math.pow() */
			double v = Math.pow(x, 0.5 * x - 0.25);
			y = v * (v / y);
		} else {
			y = Math.pow(x, x - 0.5) / y;
		}
		y = SQTPI * y * w;
		return y;
	}

	/**
	 * Evaluates the given polynomial of degree <tt>N</tt> at <tt>x</tt>.
	 * 
	 * <pre>
	 *                     2          N
	 * y  =  C  + C x + C x  +...+ C x
	 *        0    1     2          N
	 * 
	 * Coefficients are stored in reverse order:
	 * 
	 * coef[0] = C  , ..., coef[N] = C  .
	 *            N                   0
	 * </pre>
	 * 
	 * In the interest of speed, there are no checks for out of bounds
	 * arithmetic.
	 * 
	 * @param x
	 *            argument to the polynomial.
	 * @param coef
	 *            the coefficients of the polynomial.
	 * @param N
	 *            the degree of the polynomial.
	 */
	public static double polevl(double x, double coef[], int N) {

		double ans;
		ans = coef[0];

		for (int i = 1; i <= N; i++)
			ans = ans * x + coef[i];

		return ans;
	}

	/**
	 * Continued fraction expansion #1 for incomplete beta integral.
	 */
	public static double incompleteBetaFraction1(double a, double b, double x) {

		double xk, pk, pkm1, pkm2, qk, qkm1, qkm2;
		double k1, k2, k3, k4, k5, k6, k7, k8;
		double r, t, ans, thresh;
		int n;

		k1 = a;
		k2 = a + b;
		k3 = a;
		k4 = a + 1.0;
		k5 = 1.0;
		k6 = b - 1.0;
		k7 = k4;
		k8 = a + 2.0;

		pkm2 = 0.0;
		qkm2 = 1.0;
		pkm1 = 1.0;
		qkm1 = 1.0;
		ans = 1.0;
		r = 1.0;
		n = 0;
		thresh = 3.0 * MACHEP;
		do {
			xk = -(x * k1 * k2) / (k3 * k4);
			pk = pkm1 + pkm2 * xk;
			qk = qkm1 + qkm2 * xk;
			pkm2 = pkm1;
			pkm1 = pk;
			qkm2 = qkm1;
			qkm1 = qk;

			xk = (x * k5 * k6) / (k7 * k8);
			pk = pkm1 + pkm2 * xk;
			qk = qkm1 + qkm2 * xk;
			pkm2 = pkm1;
			pkm1 = pk;
			qkm2 = qkm1;
			qkm1 = qk;

			if (qk != 0)
				r = pk / qk;
			if (r != 0) {
				t = Math.abs((ans - r) / r);
				ans = r;
			} else
				t = 1.0;

			if (t < thresh)
				return ans;

			k1 += 1.0;
			k2 += 1.0;
			k3 += 2.0;
			k4 += 2.0;
			k5 += 1.0;
			k6 -= 1.0;
			k7 += 2.0;
			k8 += 2.0;

			if ((Math.abs(qk) + Math.abs(pk)) > big) {
				pkm2 *= biginv;
				pkm1 *= biginv;
				qkm2 *= biginv;
				qkm1 *= biginv;
			}
			if ((Math.abs(qk) < biginv) || (Math.abs(pk) < biginv)) {
				pkm2 *= big;
				pkm1 *= big;
				qkm2 *= big;
				qkm1 *= big;
			}
		} while (++n < 300);

		return ans;
	}

	/**
	 * Continued fraction expansion #2 for incomplete beta integral.
	 */
	public static double incompleteBetaFraction2(double a, double b, double x) {

		double xk, pk, pkm1, pkm2, qk, qkm1, qkm2;
		double k1, k2, k3, k4, k5, k6, k7, k8;
		double r, t, ans, z, thresh;
		int n;

		k1 = a;
		k2 = b - 1.0;
		k3 = a;
		k4 = a + 1.0;
		k5 = 1.0;
		k6 = a + b;
		k7 = a + 1.0;
		;
		k8 = a + 2.0;

		pkm2 = 0.0;
		qkm2 = 1.0;
		pkm1 = 1.0;
		qkm1 = 1.0;
		z = x / (1.0 - x);
		ans = 1.0;
		r = 1.0;
		n = 0;
		thresh = 3.0 * MACHEP;
		do {
			xk = -(z * k1 * k2) / (k3 * k4);
			pk = pkm1 + pkm2 * xk;
			qk = qkm1 + qkm2 * xk;
			pkm2 = pkm1;
			pkm1 = pk;
			qkm2 = qkm1;
			qkm1 = qk;

			xk = (z * k5 * k6) / (k7 * k8);
			pk = pkm1 + pkm2 * xk;
			qk = qkm1 + qkm2 * xk;
			pkm2 = pkm1;
			pkm1 = pk;
			qkm2 = qkm1;
			qkm1 = qk;

			if (qk != 0)
				r = pk / qk;
			if (r != 0) {
				t = Math.abs((ans - r) / r);
				ans = r;
			} else
				t = 1.0;

			if (t < thresh)
				return ans;

			k1 += 1.0;
			k2 -= 1.0;
			k3 += 2.0;
			k4 += 2.0;
			k5 += 1.0;
			k6 += 1.0;
			k7 += 2.0;
			k8 += 2.0;

			if ((Math.abs(qk) + Math.abs(pk)) > big) {
				pkm2 *= biginv;
				pkm1 *= biginv;
				qkm2 *= biginv;
				qkm1 *= biginv;
			}
			if ((Math.abs(qk) < biginv) || (Math.abs(pk) < biginv)) {
				pkm2 *= big;
				pkm1 *= big;
				qkm2 *= big;
				qkm1 *= big;
			}
		} while (++n < 300);

		return ans;
	}

	/**
	 * Show online documentation.
	 */
	public void doc() {

		String a = this.getClass().getName();
		a = a.replace(".", "/") + ".html";
		new HelpBrowser(HelpBrowser.JHPLOT_HTTP + a);

	}

	/** Some constants */
	protected static final double MACHEP = 1.11022302462515654042E-16;
	protected static final double MAXLOG = 7.09782712893383996732E2;
	protected static final double MINLOG = -7.451332191019412076235E2;
	protected static final double MAXGAM = 171.624376956302725;
	protected static final double SQTPI = 2.50662827463100050242E0;
	protected static final double SQRTH = 7.07106781186547524401E-1;
	protected static final double LOGPI = 1.14472988584940017414;

	protected static final double big = 4.503599627370496e15;
	protected static final double biginv = 2.22044604925031308085e-16;

}

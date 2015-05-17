package jhplot.math;

/*
 * Class:         KolmogorovSmirnovDist
 * Environment:   Java
 * Author:        Richard Simard
 * Organization:  DIRO, Université de Montréal
 * Date:          14 december 2010
 * Version:       1.0

 * Copyright 1 March 2010 by Université de Montréal,
                             Richard Simard and Pierre L'Ecuyer
 =====================================================================

   This program is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, version 3 of the License.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program.  If not, see <http://www.gnu.org/licenses/>.

 =====================================================================
*/

/**
 * <p>This class computes both the <i>cumulative</i> probability P[D_n <= x]
 * and the <i>complementary cumulative</i> probability P[D_n >= x] of the
 * 2-sided 1-sample Kolmogorov-Smirnov distribution.</p>
 *
 * The Kolmogorov-Smirnov test statistic D_n is defined by
 * <p>
 *        D_n = sup_x |F(x) - S_n(x)|
 * </p>
 * where n is the sample size, S_n(x) is an empirical distribution function,
 * and F(x) is a completely specified theoretical distribution.
 *
 *
 * @author Richard Simard
 * @version 1.0
 * @since 1 March 2010
 */



public class KolmogorovSmirnovDist
{
   private static final double num_Ln2 = 0.69314718055994530941; // ln(2)
   private static final double PI2 = Math.PI * Math.PI;
   private static final double PI4 = PI2 * PI2;
   private static final int NFACT = 20;
   private static final int MFACT = 30;

   /* For x close to 0 or 1, we use the exact formulae of Ruben-Gambino in all
      cases. For n <= NEXACT, we use exact algorithms: the Durbin matrix and
      the Pomeranz algorithms. For n > NEXACT, we use asymptotic methods
      except for x close to 0 where we still use the method of Durbin
      for n <= NKOLMO. For n > NKOLMO, we use asymptotic methods only and
      so the precision is less for x close to 0.
      We could increase the limit NKOLMO to 10^6 to get better precision
      for x close to 0, but at the price of a slower speed. */
   private static final int NEXACT = 140;
   private static final int NKOLMO = 100000;

   // For the Durbin matrix algorithm
   private static final double NORM = 1.0e140;
   private static final double INORM = 1.0e-140;
   private static final int LOGNORM = 140;


   //========================================================================
   // The factorial n! for  0 <= n <= NFACT

   private static final double[] Factorial = {
            1,
            1,
            2,
            6,
            24,
            120,
            720,
            5040,
            40320,
            362880,
            3628800,
            39916800,
            479001600.,
            6227020800.,
            87178291200.,
            1307674368000.,
            20922789888000.,
            355687428096000.,
            6402373705728000.,
            1.21645100408832e+17,
            2.43290200817664e+18
         };


   //========================================================================
   // The natural logarithm of factorial n! for  0 <= n <= MFACT

   private static final double[] LnFactorial = {
            0.,
            0.,
            0.6931471805599453,
            1.791759469228055,
            3.178053830347946,
            4.787491742782046,
            6.579251212010101,
            8.525161361065415,
            10.60460290274525,
            12.80182748008147,
            15.10441257307552,
            17.50230784587389,
            19.98721449566188,
            22.55216385312342,
            25.19122118273868,
            27.89927138384088,
            30.67186010608066,
            33.50507345013688,
            36.39544520803305,
            39.33988418719949,
            42.33561646075348,
            45.3801388984769,
            48.47118135183522,
            51.60667556776437,
            54.7847293981123,
            58.00360522298051,
            61.26170176100199,
            64.55753862700632,
            67.88974313718154,
            71.257038967168,
            74.65823634883016
         };

   //------------------------------------------------------------------------

   private static double getLogFactorial (int n)
   {
      // Returns the natural logarithm of factorial n!
      if (n <= MFACT) {
         return LnFactorial[n];

      } else {
         double x = (double) (n + 1);
         double y = 1.0 / (x * x);
         double z = ((-(5.95238095238E-4 * y) + 7.936500793651E-4) * y -
                     2.7777777777778E-3) * y + 8.3333333333333E-2;
         z = ((x - 0.5) * Math.log (x) - x) + 9.1893853320467E-1 + z / x;
         return z;
      }
   }


   //========================================================================

   private static double KSPlusbarAsymp (int n, double x)
   {
      /* Compute the probability of the KS+ distribution using an asymptotic
         formula */
      double t = (6.0 * n * x + 1);
      double z = t * t / (18.0 * n);
      double v = 1.0 - (2.0 * z * z - 4.0 * z - 1.0) / (18.0 * n);
      if (v <= 0.0)
         return 0.0;
      v = v * Math.exp (-z);
      if (v >= 1.0)
         return 1.0;
      return v;
   }


   //-------------------------------------------------------------------------

   private static double KSPlusbarUpper (int n, double x)
   {
      /* Compute the probability of the complementary KS+ distribution in
         the upper tail using Smirnov's stable formula */
      if (n > 200000)
         return KSPlusbarAsymp (n, x);

      int jmax = (int) (n * (1.0 - x));
      // Avoid log(0) for j = jmax and q ~ 1.0
      if ((1.0 - x - (double) jmax / n) <= 0.0)
         jmax--;

      int jdiv;
      if (n > 3000)
         jdiv = 2;
      else
         jdiv = 3;

      int j = jmax / jdiv + 1;
      double LogCom = getLogFactorial (n) - getLogFactorial (j) -
               getLogFactorial (n - j);
      double LOGJMAX = LogCom;

      final double EPSILON = 1.0E-12;
      double q;
      double term;
      double t;
      double Sum = 0.0;

      while (j <= jmax) {
         q = (double) j / n + x;
         term = LogCom + (j - 1) * Math.log (q) + (n - j) * Math.log1p (-q);
         t = Math.exp (term);
         Sum += t;
         LogCom += Math.log ((double) (n - j) / (j + 1));
         if (t <= Sum * EPSILON)
            break;
         j++;
      }

      j = jmax / jdiv;
      LogCom = LOGJMAX + Math.log ((double) (j + 1) / (n - j));

      while (j > 0) {
         q = (double) j / n + x;
         term = LogCom + (j - 1) * Math.log (q) + (n - j) * Math.log1p (-q);
         t = Math.exp (term);
         Sum += t;
         LogCom += Math.log ((double) j / (n - j + 1));
         if (t <= Sum * EPSILON)
            break;
         j--;
      }

      Sum *= x;
      // add the term j = 0
      Sum += Math.exp (n * Math.log1p (-x));
      return Sum;
   }


   //========================================================================

   private static double Pelz (int n, double x)
   {
      /* Approximating the Lower Tail-Areas of the Kolmogorov-Smirnov One-Sample
         Statistic,
         Wolfgang Pelz and I. J. Good,
         Journal of the Royal Statistical Society, Series B.
             Vol. 38, No. 2 (1976), pp. 152-156
       */

      final int JMAX = 20;
      final double EPS = 1.0e-10;
      final double C = 2.506628274631001;         // sqrt(2*Pi)
      final double C2 = 1.2533141373155001;       // sqrt(Pi/2)
      final double RACN = Math.sqrt ((double) n);
      final double z = RACN * x;
      final double z2 = z * z;
      final double z4 = z2 * z2;
      final double z6 = z4 * z2;
      final double w = PI2 / (2.0 * z * z);
      double ti, term, tom;
      double sum;
      int j;

      term = 1;
      j = 0;
      sum = 0;
      while (j <= JMAX && term > EPS * sum) {
         ti = j + 0.5;
         term = Math.exp (-ti * ti * w);
         sum += term;
         j++;
      }
      sum *= C / z;

      term = 1;
      tom = 0;
      j = 0;
      while (j <= JMAX && Math.abs (term) > EPS * Math.abs (tom)) {
         ti = j + 0.5;
         term = (PI2 * ti * ti - z2) * Math.exp (-ti * ti * w);
         tom += term;
         j++;
      }
      sum += tom * C2 / (RACN * 3.0 * z4);

      term = 1;
      tom = 0;
      j = 0;
      while (j <= JMAX && Math.abs (term) > EPS * Math.abs (tom)) {
         ti = j + 0.5;
         term = 6 * z6 + 2 * z4 + PI2 * (2 * z4 - 5 * z2) * ti * ti +
                PI4 * (1 - 2 * z2) * ti * ti * ti * ti;
         term *= Math.exp (-ti * ti * w);
         tom += term;
         j++;
      }
      sum += tom * C2 / (n * 36.0 * z * z6);

      term = 1;
      tom = 0;
      j = 1;
      while (j <= JMAX && term > EPS * tom) {
         ti = j;
         term = PI2 * ti * ti * Math.exp (-ti * ti * w);
         tom += term;
         j++;
      }
      sum -= tom * C2 / (n * 18.0 * z * z2);

      term = 1;
      tom = 0;
      j = 0;
      while (j <= JMAX && Math.abs (term) > EPS * Math.abs (tom)) {
         ti = j + 0.5;
         ti = ti * ti;
         term = -30 * z6 - 90 * z6 * z2 + PI2 * (135 * z4 - 96 * z6) * ti +
                PI4 * (212 * z4 - 60 * z2) * ti * ti + PI2 * PI4 * ti * ti * ti * (5 -
                      30 * z2);
         term *= Math.exp (-ti * w);
         tom += term;
         j++;
      }
      sum += tom * C2 / (RACN * n * 3240.0 * z4 * z6);

      term = 1;
      tom = 0;
      j = 1;
      while (j <= JMAX && Math.abs (term) > EPS * Math.abs (tom)) {
         ti = j * j;
         term = (3 * PI2 * ti * z2 - PI4 * ti * ti) * Math.exp (-ti * w);
         tom += term;
         j++;
      }
      sum += tom * C2 / (RACN * n * 108.0 * z6);

      return sum;
   }


   //=========================================================================

   private static void CalcFloorCeil (
      int n,                        // sample size
      double t,                     // = nx
      double[] A,                   // A_i
      double[] Atflo,               // floor (A_i - t)
      double[] Atcei                // ceiling (A_i + t)
   )
   {
      // Precompute A_i, floors, and ceilings for limits of sums in the
      // Pomeranz algorithm
      int i;
      int ell = (int) t;           // floor (t)
      double z = t - ell;          // t - floor (t)
      double w = Math.ceil (t) - t;

      if (z > 0.5) {
         for (i = 2; i <= 2 * n + 2; i += 2)
            Atflo[i] = i / 2 - 2 - ell;
         for (i = 1; i <= 2 * n + 2; i += 2)
            Atflo[i] = i / 2 - 1 - ell;

         for (i = 2; i <= 2 * n + 2; i += 2)
            Atcei[i] = i / 2 + ell;
         for (i = 1; i <= 2 * n + 2; i += 2)
            Atcei[i] = i / 2 + 1 + ell;

      } else if (z > 0.0) {
         for (i = 1; i <= 2 * n + 2; i++)
            Atflo[i] = i / 2 - 1 - ell;

         for (i = 2; i <= 2 * n + 2; i++)
            Atcei[i] = i / 2 + ell;
         Atcei[1] = 1 + ell;

      } else {                       // z == 0
         for (i = 2; i <= 2 * n + 2; i += 2)
            Atflo[i] = i / 2 - 1 - ell;
         for (i = 1; i <= 2 * n + 2; i += 2)
            Atflo[i] = i / 2 - ell;

         for (i = 2; i <= 2 * n + 2; i += 2)
            Atcei[i] = i / 2 - 1 + ell;
         for (i = 1; i <= 2 * n + 2; i += 2)
            Atcei[i] = i / 2 + ell;
      }

      if (w < z)
         z = w;
      A[0] = A[1] = 0;
      A[2] = z;
      A[3] = 1 - A[2];
      for (i = 4; i <= 2 * n + 1; i++)
         A[i] = A[i - 2] + 1;
      A[2 * n + 2] = n;
   }


   //========================================================================

   private static double Pomeranz (int n, double x)
   {
      // The Pomeranz algorithm to compute the KS distribution
      final double EPS = 1.0e-15;
      final int ENO = 350;
      final double RENO = Math.scalb (1.0, ENO); // for renormalization of V
      int coreno;                    // counter: how many renormalizations
      final double t = n * x;
      double w, sum, minsum;
      int i, j, k, s;
      int r1, r2;                    // Indices i and i-1 for V[i][]
      int jlow, jup, klow, kup, kup0;
      double[] A = new double[2 * n + 3];
      double[] Atflo = new double[2 * n + 3];
      double[] Atcei = new double[2 * n + 3];
      double[][] V = new double[2][n + 2];
      double[][] H = new double[4][n + 2];     // = pow(w, j) / Factorial(j)

      CalcFloorCeil (n, t, A, Atflo, Atcei);

      for (j = 1; j <= n + 1; j++)
         V[0][j] = 0;
      for (j = 2; j <= n + 1; j++)
         V[1][j] = 0;
      V[1][1] = RENO;
      coreno = 1;

      // Precompute H[][] = (A[j] - A[j-1]^k / k!
      H[0][0] = 1;
      w = 2.0 * A[2] / n;
      for (j = 1; j <= n + 1; j++)
         H[0][j] = w * H[0][j - 1] / j;

      H[1][0] = 1;
      w = (1.0 - 2.0 * A[2]) / n;
      for (j = 1; j <= n + 1; j++)
         H[1][j] = w * H[1][j - 1] / j;

      H[2][0] = 1;
      w = A[2] / n;
      for (j = 1; j <= n + 1; j++)
         H[2][j] = w * H[2][j - 1] / j;

      H[3][0] = 1;
      for (j = 1; j <= n + 1; j++)
         H[3][j] = 0;

      r1 = 0;
      r2 = 1;
      for (i = 2; i <= 2 * n + 2; i++) {
         jlow = (int) (2 + Atflo[i]);
         if (jlow < 1)
            jlow = 1;
         jup = (int) (Atcei[i]);
         if (jup > n + 1)
            jup = n + 1;

         klow = (int) (2 + Atflo[i - 1]);
         if (klow < 1)
            klow = 1;
         kup0 = (int) (Atcei[i - 1]);

         // Find to which case it corresponds
         w = (A[i] - A[i - 1]) / n;
         s = -1;
         for (j = 0; j < 4; j++) {
            if (Math.abs (w - H[j][1]) <= EPS) {
               s = j;
               break;
            }
         }

         minsum = RENO;
         r1 = (r1 + 1) & 1;          // i - 1
         r2 = (r2 + 1) & 1;          // i

         for (j = jlow; j <= jup; j++) {
            kup = kup0;
            if (kup > j)
               kup = j;
            sum = 0;
            for (k = kup; k >= klow; k--)
               sum += V[r1][k] * H[s][j - k];
            V[r2][j] = sum;
            if (sum < minsum)
               minsum = sum;
         }

         if (minsum < 1.0e-280) {
            // V is too small: renormalize to avoid underflow of probabilities
            for (j = jlow; j <= jup; j++)
               V[r2][j] *= RENO;
            coreno++;              // keep track of log of RENO
         }
      }

      sum = V[r2][n + 1];
      w = getLogFactorial (n) - coreno * ENO * num_Ln2 + Math.log (sum);
      if (w >= 0.)
         return 1.;
      return Math.exp (w);
   }


   //========================================================================

   private static double cdfSpecial (int n, double x)
   {
      // The KS distribution is known exactly for these cases

      // For nx^2 > 18, fbar(n, x) is smaller than 5e-16
      if ((n * x * x >= 18.0) || (x >= 1.0))
         return 1.0;

      if (x <= 0.5 / n)
         return 0.0;

      if (n == 1)
         return 2.0 * x - 1.0;

      if (x <= 1.0 / n) {
         double t = 2.0 * x - 1.0 / n;
         double w;
         if (n <= NFACT) {
            w = Factorial[n];
            return w * Math.pow (t, (double) n);
         }
         w = getLogFactorial (n) + n * Math.log (t);
         return Math.exp (w);
      }

      if (x >= 1.0 - 1.0 / n) {
         return 1.0 - 2.0 * Math.pow (1.0 - x, (double) n);
      }

      return -1.0;
   }


  //========================================================================
  /**
   * Computes the cumulative probability P[D_n <= x] of the
   * Kolmogorov-Smirnov distribution with sample size n at x.
   * It returns at least 13 decimal digits of precision for n <= 140,
   * at least 5 decimal digits of precision for 140 < n <= 100000,
   * and a few correct decimal digits for n > 100000.
   *
   * @param n sample size
   * @param x value of Kolmogorov-Smirnov statistic
   * @return cumulative probability
   */
   public static double cdf (int n, double x)
   {
      double u = cdfSpecial (n, x);
      if (u >= 0.0)
         return u;

      final double w = n * x * x;
      if (n <= NEXACT) {
         if (w < 0.754693)
            return DurbinMatrix (n, x);
         if (w < 4.0)
            return Pomeranz (n, x);
         return 1.0 - fbar (n, x);
      }

      // if (n * x * sqrt(x) <= 1.4)
      if ((w * x * n <= 2.0) && (n <= NKOLMO))
         return DurbinMatrix(n, x);

      return Pelz (n, x);
   }


   //=========================================================================

   private static double fbarSpecial (int n, double x)
   {
      final double w = n * x * x;
      if ((w >= 370.0) || (x >= 1.0))
         return 0.0;
      if ((w <= 0.0274) || (x <= 0.5 / n))
         return 1.0;
      if (n == 1)
         return 2.0 - 2.0 * x;

      if (x <= 1.0 / n) {
         double v;
         double t = 2.0 * x - 1.0 / n;
         if (n <= NFACT) {
            v = Factorial[n];
            return 1.0 - v * Math.pow (t, (double) n);
         }
         v = getLogFactorial (n) + n * Math.log (t);
         return 1.0 - Math.exp (v);
      }

      if (x >= 1.0 - 1.0 / n) {
         return 2.0 * Math.pow (1.0 - x, (double) n);
      }
      return -1.0;
   }


  //========================================================================
  /** Computes the complementary cumulative probability P[D_n >= x] of the
   * Kolmogorov-Smirnov distribution with sample size n at x.
   * It returns at least 10 decimal digits of precision for n <= 140,
   * at least 5 decimal digits of precision for 140 < n <= 200000,
   * and a few correct decimal digits for n > 200000.
   *
   * @param n sample size
   * @param x value of Kolmogorov-Smirnov statistic
   * @return complementary cumulative probability
   */
   public static double fbar (int n, double x)
   {
      double v = fbarSpecial (n, x);
      if (v >= 0.0)
         return v;

      final double w = n * x * x;
      if (n <= NEXACT) {
         if (w < 4.0)
            return 1.0 - cdf (n, x);
         else
            return 2.0 * KSPlusbarUpper (n, x);
      }

      if (w >= 2.2)
         return 2.0 * KSPlusbarUpper (n, x);

      return 1.0 - cdf (n, x);
   }


   /*=========================================================================

   The following implements the Durbin matrix algorithm and was programmed
   by G. Marsaglia, Wai Wan Tsang and Jingbo Wong in C.

   I have translated their program in Java. I have made small modifications
   in their program. (Richard Simard)

   =========================================================================*/

   /*
    The C program to compute Kolmogorov's distribution

                K(n,d) = Prob(D_n < d),         where

         D_n = max(x_1-0/n,x_2-1/n...,x_n-(n-1)/n,1/n-x_1,2/n-x_2,...,n/n-x_n)

       with  x_1<x_2,...<x_n  a purported set of n independent uniform [0,1)
       random variables sorted into increasing order.
       See G. Marsaglia, Wai Wan Tsang and Jingbo Wong,
          J.Stat.Software, 8, 18, pp 1--4, (2003).
   */

   //=========================================================================


   private static double DurbinMatrix (int n, double d)
   {
      int k, m, i, j, g, eH;
      double h, s;
      double[] H;
      double[] Q;
      int[] pQ;

      // Omit next two lines if you require >7 digit accuracy in the right tail
      if (false) {
         s = d * d * n;
         if (s > 7.24 || (s > 3.76 && n > 99))
            return 1 - 2 * Math.exp (-(2.000071 + .331 / Math.sqrt (n) +
                    1.409 / n) * s);
      }
      k = (int) (n * d) + 1;
      m = 2 * k - 1;
      h = k - n * d;
      H = new double[m * m];
      Q = new double[m * m];
      pQ = new int[1];

      for (i = 0; i < m; i++)
         for (j = 0; j < m; j++)
            if (i - j + 1 < 0)
               H[i * m + j] = 0;
            else
               H[i * m + j] = 1;
      for (i = 0; i < m; i++) {
         H[i * m] -= Math.pow (h, (double)(i + 1));
         H[(m - 1) * m + i] -= Math.pow (h, (double)(m - i));
      }
      H[(m - 1) * m] += (2 * h - 1 > 0 ? Math.pow (2 * h - 1, (double) m) : 0);
      for (i = 0; i < m; i++)
         for (j = 0; j < m; j++)
            if (i - j + 1 > 0)
               for (g = 1; g <= i - j + 1; g++)
                  H[i * m + j] /= g;
      eH = 0;
      mPower (H, eH, Q, pQ, m, n);
      s = Q[(k - 1) * m + k - 1];

      for (i = 1; i <= n; i++) {
         s = s * (double) i / n;
         if (s < INORM) {
            s *= NORM;
            pQ[0] -= LOGNORM;
         }
      }
      s *= Math.pow (10., (double) pQ[0]);
      return s;
   }


   private static void mMultiply (double[] A, double[] B, double[] C, int m)
   {
      int i, j, k;
      double s;
      for (i = 0; i < m; i++)
         for (j = 0; j < m; j++) {
            s = 0.;
            for (k = 0; k < m; k++)
               s += A[i * m + k] * B[k * m + j];
            C[i * m + j] = s;
         }
   }


   private static void renormalize (double[] V, int m, int[] p)
   {
      for (int i = 0; i < m * m; i++)
         V[i] *= INORM;
      p[0] += LOGNORM;
   }


   private static void mPower (double[] A, int eA, double[] V, int[] eV, int m,
                               int n)
   {
      int i;
      if (n == 1) {
         for (i = 0; i < m * m; i++)
            V[i] = A[i];
         eV[0] = eA;
         return;
      }
      mPower (A, eA, V, eV, m, n / 2);

      double[] B = new double[m * m];
      int[] pB = new int[1];

      mMultiply (V, V, B, m);
      pB[0] = 2 * (eV[0]);
      if (B[(m / 2) * m + (m / 2)] > NORM)
         renormalize (B, m, pB);

      if (n % 2 == 0) {
         for (i = 0; i < m * m; i++)
            V[i] = B[i];
         eV[0] = pB[0];
      } else {
         mMultiply (A, B, V, m);
         eV[0] = eA + pB[0];
      }

      if (V[(m / 2) * m + (m / 2)] > NORM)
         renormalize (V, m, eV);
   }

   //======================================================================

   public static void main (String[] args) {
      double x, y, z;
      final int K = 100;
      int n = 60;
      System.out.printf("n = %d%n%n", n);
      System.out.println("     x                    cdf                        fbar");

      for (int j = 0; j <= K; j++) {
         x = (double) j / K;
         y = cdf (n, x);
         z = fbar (n, x);
         System.out.printf ("%8.3f     %22.15g      %22.15g%n", x, y, z);
      }
   }

}

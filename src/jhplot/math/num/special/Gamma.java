/*
 * Copyright (c) 2004-2005, DoodleProject
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in
 * the documentation and/or other materials provided with the
 * distribution.
 * 
 * Neither the name of DoodleProject nor the names of its
 * contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
 * THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */
package jhplot.math.num.special;

import jhplot.math.num.Constants;
import jhplot.math.num.ContinuedFraction;
import jhplot.math.num.NumericException;
import jhplot.math.num.Series;

/**
 * Utility class that provides methods related to the gamma family of functions.
 * 
 * @version $Revision: 1.2 $ $Date: 2007/10/25 04:44:14 $
 */
public final class Gamma {

    /** Lanczos coefficients. */
    private static double[] lanczos = { 0.99999999999999709182,
        57.156235665862923517, -59.597960355475491248, 14.136097974741747174,
        -0.49191381609762019978, 0.33994649984811888699e-4,
        0.46523628927048575665e-4, -0.98374475304879564677e-4,
        0.15808870322491248884e-3, -0.21026444172410488319e-3,
        0.21743961811521264320e-3, -0.16431810653676389022e-3,
        0.84418223983852743293e-4, -0.26190838401581408670e-4,
        0.36899182659531622704e-5, };

    /**
     * Default constructor.
     * 
     * @since 1.1
     */
    private Gamma() {
        super();
    }

    /**
     * <p>
     * Returns the natural logarithm of the gamma function &#915;(x) (1).
     * </p>
     * <p>
     * References:
     * <ol>
     * <li> Eric W. Weisstein. "Gamma Function." From MathWorld--A Wolfram Web
     * Resource. <a target="_blank"
     * href="http://mathworld.wolfram.com/GammaFunction.html">
     * http://mathworld.wolfram.com/GammaFunction.html</a> </li>
     * </ol>
     * </p>
     * 
     * @param x the point of evaluation.
     * @return log(&#915;(x))
     */
    public static double logGamma(double x) {
        double ret;

        if (Double.isNaN(x) || (x <= 0.0)) {
            ret = Double.NaN;
        } else {
            double g = 607.0 / 128.0;

            double sum = 0.0;
            for (int i = 1; i < lanczos.length; ++i) {
                sum = sum + (lanczos[i] / (x + i));
            }
            sum = sum + lanczos[0];

            double tmp = x + g + .5;
            ret = ((x + .5) * Math.log(tmp)) - tmp + (.5 * Constants.LOG_2_PI)
                + Math.log(sum) - Math.log(x);
        }

        return ret;
    }

    /**
     * <p>
     * Returns the regularized gamma function P(a, x) (1).
     * </p>
     * <p>
     * References:
     * <ol>
     * <li> Eric W. Weisstein. "Regularized Gamma Function." From MathWorld--A
     * Wolfram Web Resource. <a target="_blank"
     * href="http://mathworld.wolfram.com/RegularizedGammaFunction.html">
     * http://mathworld.wolfram.com/RegularizedGammaFunction.html</a> </li>
     * </ol>
     * </p>
     * 
     * @param x the evaluation point.
     * @param a the a parameter.
     * @return P(a, x)
     * @throws NumericException if the value could not be computed.
     */
    public static double regularizedGammaP(final double a, final double x)
        throws NumericException {
        double ret;

        if (Double.isNaN(a) || Double.isNaN(x) || (a <= 0.0) || (x < 0.0)) {
            ret = Double.NaN;
        } else if (x == 0.0) {
            ret = 0.0;
        } else if (x >= (a + 1.0)) {
            // use regularizedGammaQ because it should converge faster in this
            // case.
            ret = 1.0 - regularizedGammaQ(a, x);
        } else {
            Series s = new Series() {

                private double term = 1.0 / x;

                protected double getTerm(int n, double x) {
                    term = x / (a + n) * term;
                    return term;
                }
            };
            ret = s.evaluate(x)
                * Math.exp(-x + (a * Math.log(x)) - logGamma(a));
        }

        return ret;
    }

    /**
     * <p>
     * Returns the regularized gamma function Q(a, x) = 1 - P(a, x) (1).
     * </p>
     * <p>
     * References:
     * <ol>
     * <li> Eric W. Weisstein. "Regularized Gamma Function." From MathWorld--A
     * Wolfram Web Resource. <a target="_blank"
     * href="http://mathworld.wolfram.com/RegularizedGammaFunction.html">
     * http://mathworld.wolfram.com/RegularizedGammaFunction.html</a> </li>
     * </ol>
     * </p>
     * 
     * @param x the evaluation point.
     * @param a the a parameter.
     * @return Q(a, x)
     * @throws NumericException if the value could not be computed.
     */
    public static double regularizedGammaQ(final double a, double x)
        throws NumericException {
        double ret;

        if (Double.isNaN(a) || Double.isNaN(x) || (a <= 0.0) || (x < 0.0)) {
            ret = Double.NaN;
        } else if (x == 0.0) {
            ret = 1.0;
        } else if (x < (a + 1.0)) {
            // use regularizedGammaP because it should converge faster in this
            // case.
            ret = 1.0 - regularizedGammaP(a, x);
        } else {
            // create continued fraction
            ContinuedFraction cf = new ContinuedFraction() {
                protected double getA(int n, double x) {
                    return ((2.0 * n) + 1.0) - a + x;
                }

                protected double getB(int n, double x) {
                    return n * (a - n);
                }
            };

            ret = 1.0 / cf.evaluate(x);
            ret = Math.exp(-x + (a * Math.log(x)) - logGamma(a)) * ret;
        }

        return ret;
    }
}

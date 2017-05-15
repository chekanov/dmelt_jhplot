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

import jhplot.math.num.ContinuedFraction;
import jhplot.math.num.NumericException;

/**
 * Utility class that provides methods related to the beta family of functions.
 * 
 * @version $Revision: 1.2 $ $Date: 2007/10/25 04:44:14 $
 */
public final class Beta {

    /**
     * Default constructor.
     * 
     * @since 1.1
     */
    private Beta() {
        super();
    }

    /**
     * <p>
     * Returns the natural logarithm of the beta function B(a, b) (1).
     * </p>
     * <p>
     * References:
     * <ol>
     * <li> Eric W. Weisstein. "Beta Function." From MathWorld--A Wolfram Web
     * Resource. <a target="_blank"
     * href="http://mathworld.wolfram.com/BetaFunction.html">
     * http://mathworld.wolfram.com/BetaFunction.html</a> </li>
     * </ol>
     * </p>
     * 
     * @param a the a parameter.
     * @param b the b parameter.
     * @return log(B(a, b))
     */
    public static double logBeta(double a, double b) {
        double ret;

        if (Double.isNaN(a) || Double.isNaN(b) || (a <= 0.0) || (b <= 0.0)) {
            ret = Double.NaN;
        } else {
            ret = Gamma.logGamma(a) + Gamma.logGamma(b) - Gamma.logGamma(a + b);
        }

        return ret;
    }

    /**
     * <p>
     * Returns the regularized beta function I<sub>x</sub>(a, b) (1).
     * </p>
     * <p>
     * References:
     * <ol>
     * <li> Eric W. Weisstein. "Regularized Beta Function." From MathWorld--A
     * Wolfram Web Resource. <a target="_blank"
     * href="http://mathworld.wolfram.com/RegularizedBetaFunction.html">
     * http://mathworld.wolfram.com/RegularizedBetaFunction.html</a> </li>
     * </ol>
     * </p>
     * 
     * @param x the evaluation point.
     * @param a the a parameter.
     * @param b the b parameter.
     * @return I<sub>x</sub>(a, b)
     * @throws NumericException if the value could not be computed.
     */
    public static double regularizedBeta(double x, final double a,
        final double b) throws NumericException {
        double ret;

        if (Double.isNaN(x) || Double.isNaN(a) || Double.isNaN(b) || (x < 0)
            || (x > 1) || (a <= 0.0) || (b <= 0.0)) {
            ret = Double.NaN;
        } else if (x > (a + 1.0) / (a + b + 2.0)) {
            ret = 1.0 - regularizedBeta(1.0 - x, b, a);
        } else {
            ContinuedFraction fraction = new ContinuedFraction() {

                protected double getA(int n, double x) {
                    return 1.0;
                }

                protected double getB(int n, double x) {
                    double ret;
                    double m;
                    if (n % 2 == 0) { // even
                        m = n / 2.0;
                        ret = (m * (b - m) * x)
                            / ((a + (2.0 * m) - 1.0) * (a + (2.0 * m)));
                    } else {
                        m = (n - 1.0) / 2.0;
                        ret = -((a + m) * (a + b + m) * x)
                            / ((a + (2.0 * m)) * (a + (2.0 * m) + 1.0));
                    }
                    return ret;
                }
            };
            ret = Math.exp((a * Math.log(x)) + (b * Math.log(1.0 - x))
                - Math.log(a) - logBeta(a, b))
                / fraction.evaluate(x);
        }

        return ret;
    }
}

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

import jhplot.math.num.Function;
import jhplot.math.num.NumericException;
import jhplot.math.num.root.BisectionRootFinder;
import jhplot.math.num.root.Bracket;

/**
 * Utility class that provides methods related to the error function.
 * 
 * @version $Revision: 1.2 $ $Date: 2007/10/25 04:44:14 $
 */
public final class Erf {

    /**
     * Default constructor.
     * 
     * @since 1.1
     */
    private Erf() {
        super();
    }

    /**
     * <p>
     * Returns the error function erf(x) (1).
     * </p>
     * <p>
     * References:
     * <ol>
     * <li> Eric W. Weisstein. "Erf." From MathWorld--A Wolfram Web Resource. <a
     * target="_blank" href="http://mathworld.wolfram.com/Erf.html">
     * http://mathworld.wolfram.com/Erf.html</a> </li>
     * </ol>
     * </p>
     * 
     * @param x the evaluation point.
     * @return erf(x)
     * @throws NumericException if the value could not be computed.
     */
    public static double erf(double x) throws NumericException {
        double ret;

        if (Double.isNaN(x)) {
            ret = Double.NaN;
        } else if (Double.isInfinite(x)) {
            if (x < 0) {
                ret = -1.0;
            } else {
                ret = 1.0;
            }
        } else if (x == 0.0) {
            ret = 0.0;
        } else {
            ret = Gamma.regularizedGammaP(0.5, x * x);
            if (x < 0) {
                ret = -ret;
            }
        }

        return ret;
    }

    /**
     * <p>
     * Returns the complementary error function erfc(x) (1).
     * </p>
     * <p>
     * References:
     * <ol>
     * <li> Eric W. Weisstein. "Erfc." From MathWorld--A Wolfram Web Resource.
     * <a target="_blank" href="http://mathworld.wolfram.com/Erfc.html">
     * http://mathworld.wolfram.com/Erfc.html</a> </li>
     * </ol>
     * </p>
     * 
     * @param x the evaluation point.
     * @return erfc(x)
     * @throws NumericException if the value could not be computed.
     * @since 1.1
     */
    public static double erfc(double x) throws NumericException {
        double ret;

        if (Double.isNaN(x)) {
            ret = Double.NaN;
        } else if (Double.isInfinite(x)) {
            if (x < 0) {
                ret = 2.0;
            } else {
                ret = 0.0;
            }
        } else if (x == 0.0) {
            ret = 1.0;
        } else if (x < 0) {
            ret = 1.0 + Gamma.regularizedGammaP(0.5, x * x);
        } else {
            ret = Gamma.regularizedGammaQ(0.5, x * x);
        }

        return ret;
    }

    /**
     * <p>
     * Returns the inverse error function erf<sup>-1</sup>(y).
     * </p>
     * <p>
     * References:
     * <ol>
     * <li> Eric W. Weisstein. "Erf." From MathWorld--A Wolfram Web Resource. <a
     * target="_blank" href="http://mathworld.wolfram.com/Erf.html">
     * http://mathworld.wolfram.com/Erf.html</a> </li>
     * </ol>
     * </p>
     * 
     * @param y the evaluation point.
     * @return the value x, such that erf(x) == <tt>y</tt>
     * @throws NumericException if the value could not be computed.
     * @since 1.2
     */
    public static double inverseErf(final double y) throws NumericException {
        double ret;

        if (Double.isNaN(y) || y < -1.0 || y > 1.0) {
            ret = Double.NaN;
        } else if (y == 1.0) {
            ret = Double.POSITIVE_INFINITY;
        } else if (y == -1.0) {
            ret = Double.NEGATIVE_INFINITY;
        } else if (y == 0.0) {
            ret = 0.0;
        } else {
            Function f = new Function() {
                public double evaluate(double x) throws NumericException {
                    return erf(x) - y;
                }
            };

            Bracket b = new Bracket(f);
            double[] bracket;
            if (y > 0) {
                bracket = b.bracketOut(0.0, y, Double.POSITIVE_INFINITY);
            } else {
                bracket = b.bracketOut(Double.NEGATIVE_INFINITY, y, 0.0);
            }

            BisectionRootFinder bisection = new BisectionRootFinder(f);
            ret = bisection.findRoot(bracket[0], bracket[1]);
        }
        return ret;
    }
}

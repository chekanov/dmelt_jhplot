/*
 * Copyright (c) 2005, DoodleProject
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

/**
 * Utility class that provides methods related to the trigonometric functions.
 * 
 * @since 1.1
 * @version $Revision: 1.2 $ $Date: 2007/10/25 04:44:14 $
 */
public final class Trigonometric {

    /**
     * Default constructor.
     */
    private Trigonometric() {
        super();
    }

    /**
     * Returns the <a
     * href="http://mathworld.wolfram.com/InverseHyperbolicCosine.html"> inverse
     * hyperbolic cosine</a> of <tt>x</tt>.
     * 
     * @param x double value for which to find the inverse hyperbolic cosine
     * @return cosh<sup>-1</sup>(x)
     * @since 1.2
     */
    public static double acosh(double x) {
        double ret;

        if (x < 1.0 || Double.isNaN(x)) {
            ret = Double.NaN;
        } else {
            ret = Math.log(x + Math.sqrt(x * x - 1.0));
        }

        return ret;
    }

    /**
     * Returns the <a
     * href="http://mathworld.wolfram.com/InverseHyperbolicSine.html"> inverse
     * hyperbolic sine</a> of <tt>x</tt>.
     * 
     * @param x double value for which to find the inverse hyperbolic sine
     * @return sinh<sup>-1</sup>(x)
     * @since 1.2
     */
    public static double asinh(double x) {
        double ret;

        if (Double.isNaN(x)) {
            ret = Double.NaN;
        } else {
            ret = Math.log(x + Math.sqrt(x * x + 1.0));
        }

        return ret;
    }

    /**
     * Returns the <a
     * href="http://mathworld.wolfram.com/InverseHyperbolicTangent.html">
     * inverse hyperbolic tangent</a> of <tt>x</tt>.
     * 
     * @param x double value for which to find the inverse hyperbolic tangent
     * @return tanh<sup>-1</sup>(x)
     * @since 1.2
     */
    public static double atanh(double x) {
        double ret;

        if (x < -1.0 || x > 1.0 || Double.isNaN(x)) {
            ret = Double.NaN;
        } else if (x == 1.0) {
            ret = Double.POSITIVE_INFINITY;
        } else if (x == -1.0) {
            ret = Double.NEGATIVE_INFINITY;
        } else {
            ret = 0.5 * (Math.log(1.0 + x) - Math.log(1.0 - x));
        }

        return ret;
    }

    /**
     * Returns the <a href="http://mathworld.wolfram.com/HyperbolicCosine.html">
     * hyperbolic cosine</a> of <tt>x</tt>.
     * 
     * @param x double value for which to find the hyperbolic cosine
     * @return cosh(x)
     */
    public static double cosh(double x) {
        double e = Math.exp(x);
        return (e + 1.0 / e) / 2.0;
    }

    /**
     * Returns the <a href="http://mathworld.wolfram.com/HyperbolicSine.html">
     * hyperbolic sine</a> of <tt>x</tt>.
     * 
     * @param x double value for which to find the hyperbolic sine
     * @return sinh(x)
     */
    public static double sinh(double x) {
        double e = Math.exp(x);
        return (e - 1.0 / e) / 2.0;
    }

    /**
     * Returns the <a
     * href="http://mathworld.wolfram.com/HyperbolicTangent.html"> hyperbolic
     * tangent</a> of <tt>x</tt>.
     * 
     * @param x double value for which to find the hyperbolic sine
     * @return sinh(x)
     */
    public static double tanh(double x) {
        double e = Math.exp(x * 2.0);
        return (e - 1.0) / (e + 1.0);
    }
}

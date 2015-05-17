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
package jhplot.math.num.pdf;

import jhplot.math.num.NumericException;

/**
 * <p>
 * The Beta distribution (1).
 * </p>
 * <p>
 * References:
 * <ol>
 * <li> Eric W. Weisstein. "Beta Distribution." From MathWorld--A Wolfram Web
 * Resource. <a target="_blank"
 * href="http://mathworld.wolfram.com/Beta.html">
 * http://mathworld.wolfram.com/Beta.html</a> </li>
 * </ol>
 * </p>
 * 
 * @version $Revision: 1.2 $ $Date: 2007/10/25 04:44:10 $
 */
public class Beta extends ContinuousDistribution {

    /** The alpha parameter. */
    private double alpha;

    /** The beta parameter. */
    private double beta;

    /**
     * Default constructor. Alpha and beta are both set to 1.
     */
    public Beta() {
        this(1.0, 1.0);
    }

    /**
     * Create a distribution with the given alpha and beta values.
     * 
     * @param a the alpha parameter.
     * @param b the beta parameter.
     */
    public Beta(double a, double b) {
        super();
        setAlpha(a);
        setBeta(b);
    }

    /**
     * The CDF for this distribution. This method returns P(X &lt; x).
     * 
     * @param x the value at which the CDF is evaluated.
     * @return CDF for this distribution.
     * @throws NumericException if the cumulative probability can not be
     *         computed.
     */
    public double cumulativeProbability(double x) throws NumericException {
        double ret;

        if (Double.isNaN(x)) {
            ret = Double.NaN;
        } else if (x <= 0.0) {
            ret = 0.0;
        } else if (x >= 1.0) {
            ret = 1.0;
        } else {
            ret = jhplot.math.num.special.Beta.regularizedBeta(x, getAlpha(), getBeta());
        }

        return ret;
    }

    /**
     * Access the alpha parameter.
     * 
     * @return the alpha parameter.
     */
    public double getAlpha() {
        return alpha;
    }

    /**
     * Access the beta parameter.
     * 
     * @return the beta parameter.
     */
    public double getBeta() {
        return beta;
    }

    /**
     * The inverse CDF for this distribution. This method returns x such that,
     * P(X &lt; x) = p.
     * 
     * @param p the cumulative probability.
     * @return x
     * @throws NumericException if the inverse cumulative probability can not be
     *         computed.
     */
    public double inverseCumulativeProbability(double p)
        throws NumericException {
        double ret;

        if (p < 0.0 || p > 1.0 || Double.isNaN(p)) {
            ret = Double.NaN;
        } else if (p == 0.0) {
            ret = 0.0;
        } else if (p == 1.0) {
            ret = 1.0;
        } else {
            ret = findInverseCumulativeProbability(p, 0.0, 0.5, 1.0);
        }

        return ret;
    }

    /**
     * Modify the alpha parameter.
     * 
     * @param a the new alpha value.
     */
    public void setAlpha(double a) {
        if (a <= 0.0 || Double.isNaN(a)) {
            throw new IllegalArgumentException("Alpha must be positive.");
        }
        this.alpha = a;
    }

    /**
     * Modify the beta parameter.
     * 
     * @param b the new beta value.
     */
    public void setBeta(double b) {
        if (b <= 0.0 || Double.isNaN(b)) {
            throw new IllegalArgumentException("Beta must be positive.");
        }
        this.beta = b;
    }
}

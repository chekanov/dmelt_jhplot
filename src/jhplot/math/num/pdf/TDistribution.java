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
import jhplot.math.num.special.Beta;

/**
 * <p>
 * Student's t distribution (1).
 * </p>
 * <p>
 * References:
 * <ol>
 * <li> Eric W. Weisstein. "t Distribution." From MathWorld--A Wolfram Web
 * Resource. <a target="_blank"
 * http://mathworld.wolfram.com/Studentst-Distribution.html">
 * http://mathworld.wolfram.com/Studentst-Distribution.html</a> </li>
 * </ol>
 * </p>
 * 
 * @version $Revision: 1.2 $ $Date: 2007/10/25 04:44:10 $
 */
public class TDistribution extends ContinuousDistribution {

    /** The degrees of freedom. */
    private double degreesOfFreedom;

    /**
     * Default constructor. Degrees of freedom is set to 1.
     */
    public TDistribution() {
        this(1.0);
    }

    /**
     * Create a distribution with the given degrees of freedom.
     * 
     * @param df the degrees of freedom.
     */
    public TDistribution(double df) {
        super();
        setDegreesOfFreedom(df);
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

        if (x == 0.0) {
            ret = 0.5;
        } else {
            double df = getDegreesOfFreedom();
            double t = Beta.regularizedBeta(df / (df + (x * x)), 0.5 * df, 0.5);
            if (x < 0.0) {
                ret = 0.5 * t;
            } else {
                ret = 1.0 - 0.5 * t;
            }
        }

        return ret;
    }

    /**
     * Access the degrees of freedom.
     * 
     * @return the degrees of freedom.
     */
    public double getDegreesOfFreedom() {
        return degreesOfFreedom;
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
            ret = Double.NEGATIVE_INFINITY;
        } else if (p == 1.0) {
            ret = Double.POSITIVE_INFINITY;
        } else if (p <= 0.5) {
            ret = findInverseCumulativeProbability(p, Double.NEGATIVE_INFINITY,
                -getDegreesOfFreedom(), 0.0);
        } else {
            ret = findInverseCumulativeProbability(p, 0.0,
                getDegreesOfFreedom(), Double.POSITIVE_INFINITY);
        }

        return ret;
    }

    /**
     * Modify the degrees of freedom.
     * 
     * @param df the new degrees of freedom value.
     */
    public void setDegreesOfFreedom(double df) {
        if (df <= 0.0 || Double.isNaN(df)) {
            throw new IllegalArgumentException(
                "Degrees of freedom must be positive.");
        }
        this.degreesOfFreedom = df;
    }
}

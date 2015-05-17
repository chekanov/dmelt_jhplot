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

import jhplot.math.num.Constants;
import jhplot.math.num.NumericException;
import jhplot.math.num.special.Erf;

/**
 * <p>
 * The Normal distribution (1).
 * </p>
 * <p>
 * References:
 * <ol>
 * <li> Eric W. Weisstein. "Normal Distribution." From MathWorld--A Wolfram Web
 * Resource. <a target="_blank"
 * href="http://mathworld.wolfram.com/Normal.html">
 * http://mathworld.wolfram.com/Normal.html</a> </li>
 * </ol>
 * </p>
 * 
 * @version $Revision: 1.2 $ $Date: 2007/10/25 04:44:10 $
 */
public class Normal extends ContinuousDistribution {

    /** The mean. */
    private double mean;

    /** The standard deviation. */
    private double standardDeviation;

    /**
     * Default constructor. Mean is set to zero and standard deviation is set to
     * one.
     */
    public Normal() {
        this(0.0, 1.0);
    }

    /**
     * Create a distribution with the given mean and standard deviation.
     * 
     * @param m the mean.
     * @param s the standard deviation.
     */
    public Normal(double m, double s) {
        super();
        setMean(m);
        setStandardDeviation(s);
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

        if (Double.isInfinite(x)) {
            if (x < 0.0) {
                ret = 0.0;
            } else {
                ret = 1.0;
            }
        } else {
            ret = 0.5 * (1.0 + Erf.erf((x - getMean())
                / (getStandardDeviation() * Constants.SQRT_2)));
        }

        return ret;
    }

    /**
     * Access the mean.
     * 
     * @return the mean.
     */
    public double getMean() {
        return mean;
    }

    /**
     * Access the standard deviation.
     * 
     * @return the standard deviation.
     */
    public double getStandardDeviation() {
        return standardDeviation;
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
        } else {
            ret = Constants.SQRT_2 * getStandardDeviation()
                * Erf.inverseErf(2.0 * p - 1.0) + getMean();
        }

        return ret;
    }

    /**
     * Modify the mean.
     * 
     * @param m the new mean value.
     */
    public void setMean(double m) {
        if (Double.isNaN(m)) {
            throw new IllegalArgumentException("Mean must be a valid number.");
        }
        this.mean = m;
    }

    /**
     * Modify the standard deviation.
     * 
     * @param std The new standard deviation value.
     */
    public void setStandardDeviation(double std) {
        if (std <= 0.0 || Double.isNaN(std)) {
            throw new IllegalArgumentException(
                "Standard deviation must be positive.");
        }
        this.standardDeviation = std;
    }
}

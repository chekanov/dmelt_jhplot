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
package jhplot.math.num.pdf;

/**
 * <p>
 * The Logistic distribution.
 * </p>
 * <p>
 * References:
 * <ol>
 * <li> Eric W. Weisstein. "Logistic Distribution." From MathWorld--A Wolfram
 * Web Resource. <a target="_blank"
 * href="http://mathworld.wolfram.com/Logistic.html">
 * http://mathworld.wolfram.com/Logistic.html</a> </li>
 * </ol>
 * </p>
 * 
 * @since 1.2
 * @version $Revision: 1.2 $ $Date: 2007/10/25 04:44:10 $
 */
public class Logistic extends ContinuousDistribution {

    /** The mean. */
    private double mean;

    /** the scale parameter. */
    private double scale;

    /**
     * Default constructor. The mean is set to zero and the scale parameter is
     * set to one.
     */
    public Logistic() {
        this(0.0, 1.0);
    }

    /**
     * Create a distribution with the given mean and scale parameters.
     * 
     * @param m the mean.
     * @param s the scale parameter.
     */
    public Logistic(double m, double s) {
        super();
        setMean(m);
        setScale(s);
    }

    /**
     * The CDF for this distribution. This method returns P(X &lt; x).
     * 
     * @param x the value at which the CDF is evaluated.
     * @return CDF for this distribution.
     */
    public double cumulativeProbability(double x) {
        double ret;

        if (Double.isNaN(x)) {
            ret = Double.NaN;
        } else if (Double.isInfinite(x)) {
            if (x < 0.0) {
                ret = 0.0;
            } else {
                ret = 1.0;
            }
        } else if (x == mean) {
            ret = 0.5;
        } else {
            ret = 1.0 / (1.0 + Math.exp(-(x - mean) / scale));
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
     * Access the scale parameter.
     * 
     * @return the scale parameter.
     */
    public double getScale() {
        return scale;
    }

    /**
     * The inverse CDF for this distribution. This method returns x such that,
     * P(X &lt; x) = p.
     * 
     * @param p the cumulative probability.
     * @return x
     */
    public double inverseCumulativeProbability(double p) {
        double ret;

        if (p < 0.0 || p > 1.0 || Double.isNaN(p)) {
            ret = Double.NaN;
        } else if (p == 0.0) {
            ret = Double.NEGATIVE_INFINITY;
        } else if (p == 1.0) {
            ret = Double.POSITIVE_INFINITY;
        } else if (p == 0.5) {
            ret = mean;
        } else {
            ret = mean - scale * Math.log(1.0 / p - 1.0);
        }

        return ret;
    }

    /**
     * Modify the mean.
     * 
     * @param m The new mean value.
     */
    public void setMean(double m) {
        if (Double.isNaN(m)) {
            throw new IllegalArgumentException("mean must be a valid number.");
        }
        this.mean = m;
    }

    /**
     * Modify the scale parameter.
     * 
     * @param s The new scale parameter value.
     */
    public void setScale(double s) {
        if (s <= 0.0 || Double.isNaN(s)) {
            throw new IllegalArgumentException(
                "scale parameter must be positive.");
        }
        this.scale = s;
    }
}

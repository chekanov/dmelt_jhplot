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
 * The Laplace distribution.
 * </p>
 * <p>
 * References:
 * <ol>
 * <li> Eric W. Weisstein. "Laplace Distribution." From MathWorld--A Wolfram Web
 * Resource. <a target="_blank"
 * href="http://mathworld.wolfram.com/Laplace.html">
 * http://mathworld.wolfram.com/Laplace.html</a> </li>
 * </ol>
 * </p>
 * 
 * @since 1.2
 * @version $Revision: 1.3 $ $Date: 2007/11/18 23:51:21 $
 */
public class Laplace extends ContinuousDistribution {

    /** the mean. */
    private double mean;

    /** the scale. */
    private double scale;

    /**
     * Default constructor. Mean is set to zero and scale is set to one.
     */
    public Laplace() {
        this(0.0, 1.0);
    }

    /**
     * Create a distribution with the given mean and scale.
     * 
     * @param m the mean.
     * @param s the scale.
     */
    public Laplace(double m, double s) {
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
        } else if (x < mean) {
            ret = 0.5 * Math.exp((x - mean) / scale);
        } else {
            ret = 0.5 * (2.0 - Math.exp((mean - x) / scale));
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
     * Access the scale.
     * 
     * @return the scale.
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
        } else if (p < 0.5) {
            ret = mean + scale * Math.log(1.0 + (2.0 * p - 1.0));
        } else {
            ret = mean - scale * Math.log(1.0 - (2.0 * p - 1.0));
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
            throw new IllegalArgumentException("mean must be a number.");
        }
        mean = m;
    }

    /**
     * Modify the scale.
     * 
     * @param s the new scale value.
     */
    public void setScale(double s) {
        if (s <= 0.0 || Double.isNaN(s)) {
            throw new IllegalArgumentException("scale must be positive.");
        }
        scale = s;
    }

}

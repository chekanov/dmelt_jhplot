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

import jhplot.math.num.Constants;
import jhplot.math.num.NumericException;

/**
 * <p>
 * The Poisson distribution.
 * </p>
 * <p>
 * References:
 * <ol>
 * <li> Eric W. Weisstein. "Poisson Distribution." From MathWorld--A Wolfram Web
 * Resource. <a target="_blank"
 * href="http://mathworld.wolfram.com/Poisson.html">
 * http://mathworld.wolfram.com/Poisson.html</a> </li>
 * </ol>
 * </p>
 * 
 * @since 1.2
 * @version $Revision: 1.3 $ $Date: 2007/11/18 23:51:21 $
 */
public class Poisson extends DiscreteDistribution {

    /** the mean. */
    private double mean;

    /**
     * Default distribution. The means is set to one.
     */
    public Poisson() {
        this(1.0);
    }

    /**
     * Create a distribution with the given mean.
     * 
     * @param m the mean.
     */
    public Poisson(double m) {
        super();
        setMean(m);
    }

    /**
     * The CDF for this distribution. This method returns P(X &le; x).
     * 
     * @param x the value at which the CDF is evaluated.
     * @return CDF for this distribution.
     * @throws NumericException if the cumulative probability can not be
     *         computed.
     */
    public double cumulativeProbability(int x) throws NumericException {
        double ret;

        if (x < 0) {
            ret = 0.0;
        } else {
            ret = simpleCumulativeProbability(0, x);
        }

        return ret;
    }

    /**
     * The inverse CDF for this distribution. This method returns the largest x
     * such that, P(X &le; x) &le; p. The return value must also satisfy P(X
     * &ge; x) &ge 1 - p.
     * 
     * @param p the cumulative probability.
     * @return x
     * @throws NumericException if the inverse cumulative probability can not be
     *         computed.
     */
    public int inverseCumulativeProbability(double p) throws NumericException {
        int ret;

        if (p < 0.0 || p > 1.0 || Double.isNaN(p)) {
            ret = Integer.MIN_VALUE;
        } else if (p == 0.0) {
            ret = -1;
        } else if (p == 1.0) {
            ret = Integer.MAX_VALUE;
        } else {
            ret = findInverseCumulativeProbability(p, 0, (int) (mean + 0.5),
                Integer.MAX_VALUE);
        }

        return ret;
    }

    /**
     * The PMF for this distribution. This method returns P(X = x).
     * 
     * @param x the value at which the probability is evaluated.
     * @return PMF for this distribution.
     */
    public double probability(int x) {
        double ret;
        if (x < 0) {
            ret = 0.0;
        } else if (x == 0) {
            ret = Math.exp(-mean);
        } else {
            ret = Math.exp(-SaddlePoint.getStirlingError(x)
                - SaddlePoint.getDeviancePart(x, mean))
                / Math.sqrt(Constants.PI_2 * x);
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
     * Modify the mean.
     * 
     * @param m the new mean value.
     */
    public void setMean(double m) {
        if (Double.isNaN(m) || m <= 0.0) {
            throw new IllegalArgumentException("mean must be positive.");
        }
        this.mean = m;
    }
}

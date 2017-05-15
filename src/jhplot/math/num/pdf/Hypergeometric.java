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

import jhplot.math.num.NumericException;

/**
 * <p>
 * The Hypergeometric distribution.
 * </p>
 * <p>
 * References:
 * <ol>
 * <li> Eric W. Weisstein. "Hypergeometric Distribution." From MathWorld--A
 * Wolfram Web Resource. <a target="_blank"
 * href="http://mathworld.wolfram.com/Hypergeometric.html">
 * http://mathworld.wolfram.com/Hypergeometric.html</a> </li>
 * </ol>
 * </p>
 * 
 * @since 1.2
 * @version $Revision: 1.2 $ $Date: 2007/10/25 04:44:10 $
 */
public class Hypergeometric extends DiscreteDistribution {

    /** the number of failures in the population. */
    private int numberOfFailures;

    /** the number of successes in the population. */
    private int numberOfSuccesses;

    /** the sample size. */
    private int sampleSize;

    /** the lower bound on the observable number of successes. */
    private int domainLowerBound;

    /** the upper bound on the observable number of successes. */
    private int domainUpperBound;

    /**
     * Default constructor. The number of failures, number of successes, and
     * sample size are all set to one.
     */
    public Hypergeometric() {
        this(1, 1, 1);
    }

    /**
     * Create a distribution with the given number of failures, number of
     * successes, and sample size.
     * 
     * @param successes the number of successes.
     * @param failures the number of failures.
     * @param sample the sample size.
     */
    public Hypergeometric(int successes, int failures, int sample) {
        super();
        setNumberOfFailures(failures);
        setNumberOfSuccesses(successes);
        setSampleSize(sample);
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

        if (x < domainLowerBound) {
            ret = 0.0;
        } else if (x >= domainUpperBound) {
            ret = 1.0;
        } else {
            ret = simpleCumulativeProbability(domainLowerBound, x);
        }

        return ret;
    }

    /**
     * Access the number of failures.
     * 
     * @return the number of failures.
     */
    public int getNumberOfFailures() {
        return numberOfFailures;
    }

    /**
     * Access the number of successes.
     * 
     * @return the number of successes.
     */
    public int getNumberOfSuccesses() {
        return numberOfSuccesses;
    }

    /**
     * Access the sample size.
     * 
     * @return the sample size.
     */
    public int getSampleSize() {
        return sampleSize;
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
            ret = domainLowerBound - 1;
        } else if (p == 1.0) {
            ret = domainUpperBound;
        } else {
            double mean = (double) (numberOfSuccesses * sampleSize)
                / (double) (numberOfSuccesses + numberOfFailures);
            ret = findInverseCumulativeProbability(p, domainLowerBound,
                (int) (mean + 0.5), domainUpperBound);
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

        if (x < domainLowerBound || x > domainUpperBound) {
            ret = 0.0;
        } else {
            int m = numberOfSuccesses + numberOfFailures;
            double p = (double) sampleSize / (double) m;
            double q = (double) (m - sampleSize) / (double) m;

            double p1 = SaddlePoint.logBinomialProbability(x,
                numberOfSuccesses, p, q);
            double p2 = SaddlePoint.logBinomialProbability(sampleSize
                - x, numberOfFailures, p, q);
            double p3 = SaddlePoint.logBinomialProbability(sampleSize,
                m, p, q);
            ret = Math.exp(p1 + p2 - p3);
        }

        return ret;
    }

    /**
     * Modify the number of failures.
     * 
     * @param n the new number of failures value.
     */
    public void setNumberOfFailures(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("number of failures must be "
                + "non-negative.");
        }
        numberOfFailures = n;
        domainLowerBound = Math.max(0, sampleSize - numberOfFailures);
        domainUpperBound = Math.min(sampleSize, numberOfSuccesses);
    }

    /**
     * Modify the number of successes.
     * 
     * @param n the new number of successes value.
     */
    public void setNumberOfSuccesses(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("number of successes must be "
                + "non-negative.");
        }
        numberOfSuccesses = n;
        domainUpperBound = Math.min(sampleSize, numberOfSuccesses);
    }

    /**
     * Modify the sample size.
     * 
     * @param n the new sample size value.
     */
    public void setSampleSize(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("sample size must be positive.");
        }
        sampleSize = n;
        domainLowerBound = Math.max(0, sampleSize - numberOfFailures);
        domainUpperBound = Math.min(sampleSize, numberOfSuccesses);
    }

}

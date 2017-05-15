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
 * The Binomial distribution.
 * </p>
 * <p>
 * References:
 * <ol>
 * <li> Eric W. Weisstein. "Binomial Distribution." From MathWorld--A Wolfram
 * Web Resource. <a target="_blank"
 * href="http://mathworld.wolfram.com/Binomial.html">
 * http://mathworld.wolfram.com/Binomial.html</a> </li>
 * </ol>
 * </p>
 * 
 * @since 1.2
 * @version $Revision: 1.2 $ $Date: 2007/10/25 04:44:10 $
 */
public class Binomial extends DiscreteDistribution {

    /** the number of trials. */
    private int numberOfTrials;

    /** the probability of success for each trial. */
    private double probabilityOfSuccess;

    /**
     * Default constructor. Number of trials is set to one and probability of
     * success is set to 0.5.
     */
    public Binomial() {
        this(1, 0.5);
    }

    /**
     * Create a distribution with the given number of trials and probability of
     * success.
     * 
     * @param n the number of trials.
     * @param p the probability of success.
     */
    public Binomial(int n, double p) {
        super();
        setNumberOfTrials(n);
        setProbabilityOfSuccess(p);
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
        } else if (x >= numberOfTrials) {
            ret = 1.0;
        } else {
            ret = simpleCumulativeProbability(0, x);
        }

        return ret;
    }

    /**
     * Access the number of trials.
     * 
     * @return the number of trials.
     */
    public int getNumberOfTrials() {
        return numberOfTrials;
    }

    /**
     * Access probability of success.
     * 
     * @return the probability of success.
     */
    public double getProbabilityOfSuccess() {
        return probabilityOfSuccess;
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
            ret = numberOfTrials;
        } else {
            ret = findInverseCumulativeProbability(p, 0, (int) (numberOfTrials
                * probabilityOfSuccess + 0.5), numberOfTrials);
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
        if (x < 0 || x > numberOfTrials) {
            ret = 0.0;
        } else {
            ret = Math.exp(SaddlePoint.logBinomialProbability(x,
                numberOfTrials, probabilityOfSuccess,
                1.0 - probabilityOfSuccess));
        }
        return ret;
    }

    /**
     * Modify the number of trials.
     * 
     * @param n the new number of trials value.
     */
    public void setNumberOfTrials(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException(
                "number of trials must be positive.");
        }
        numberOfTrials = n;
    }

    /**
     * Modify probability of success.
     * 
     * @param p the new probability of success value.
     */
    public void setProbabilityOfSuccess(double p) {
        if (Double.isNaN(p) || p <= 0.0 || p >= 1.0) {
            throw new IllegalArgumentException("probability of success must"
                + "be between 0.0 and 1.0, exclusive.");
        }
        probabilityOfSuccess = p;
    }

}

/*
 * Copyright (c) 2007, DoodleProject
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

package jhplot.math.num.random;


/**
 * <p>
 * A random variable generator for the Normal distribution.
 * </p>
 * <p>
 * References:
 * <ol>
 * <li>Wikipedia contributors, "Normal distribution," Wikipedia, The Free
 * Encyclopedia, <a target="_blank"
 * href="http://en.wikipedia.org/wiki/Normal_distribution">
 * http://en.wikipedia.org/wiki/Normal_distribution</a></li>
 * </ol>
 * </p>
 * 
 * @since 1.3
 * @version $Revision: 1.4 $ $Date: 2007/11/18 23:51:19 $
 */
public class NormalRandomVariable extends AbstractContinuousRandomVariable {

    /** The P1. */
    private static final double P1 = 0.86385546;

    /** The P2. */
    private static final double P2 = P1 + 0.1108179673;

    /** The P3. */
    private static final double P3 = P2 + 0.02262677245;

    /** The mean. */
    private double mean;

    /** The standard deviation. */
    private double standardDeviation;

    /**
     * Default constructor. Mean is set to zero and standard deviation is set to
     * one.
     */
    public NormalRandomVariable() {
        this(0.0, 1.0);
    }

    /**
     * Create a random variable with the given mean and standard deviation.
     * 
     * @param m the mean.
     * @param s the standard deviation.
     */
    public NormalRandomVariable(double m, double s) {
        this(m, s, new RandomRNG());
    }

    /**
     * Create a random variable with the given parameters.
     * 
     * @param m the mean.
     * @param s the standard deviation.
     * @param source the source generator.
     */
    public NormalRandomVariable(double m, double s, RNG source) {
        super(source);
        setMean(m);
        setStandardDeviation(s);
    }

    /**
     * <p>
     * Access the next random variable using the given generator.
     * </p>
     * <p>
     * The implementation of this method is based on the Normal generator of
     * Marsaglia and Bray.
     * </p>
     * 
     * @param m the mean.
     * @param s the standard deviation.
     * @param source the source generator.
     * @return the next random variable.
     */
    public static double nextRandomVariable(double m, double s, RNG source) {
        double x;
        double u = source.nextRandomNumber();
        if (u <= P1) {
            double v = source.nextRandomNumber() * 2.0 - 1.0;
            double w = source.nextRandomNumber() * 2.0 - 1.0;
            x = 2.3153508 * u - 1.0 + v + w;
        } else if (u <= P2) {
            double v = source.nextRandomNumber();
            x = 3.0 / 2.0 * (v - 1 + 9.0334237 * (u - P1));
        } else if (u <= P3) {
            double u1;
            double v;
            double sum;
            double w;
            do {
                x = source.nextRandomNumber() * 6.0 - 3.0;
                u1 = source.nextRandomNumber();
                v = Math.abs(x);
                w = 6.6313339 * (9.0 - 6.0 * v + v * v);
                sum = 0.0;
                if (v < 3.0 / 2.0) {
                    sum = 6.0432809 * (3.0 / 2.0 - v);
                }
                if (v < 1.0) {
                    sum = sum + 13.2626678 * (3.0 - v * v) - w;
                }
            } while (u1 > 490024445 * Math.exp(-(v * v) / 2.0) - sum - w);
        } else {
            double v;
            do {
                v = source.nextRandomNumber();
                double w = source.nextRandomNumber();
                x = 9.0 / 2.0 - Math.log(w);
            } while (x * v * v > 9.0 / 2.0);
            x = Math.sqrt(2.0 * x) * Math.signum(u - 0.9986501);
        }

        return x * s + m;
    }

    /**
     * Access the mean.
     * 
     * @return the mean.
     */
    private double getMean() {
        return mean;
    }

    /**
     * Access the standard deviation.
     * 
     * @return the standard deviation.
     */
    private double getStandardDeviation() {
        return standardDeviation;
    }

    /**
     * Access the next random variable from this generator.
     * 
     * @return the next random variable.
     */
    public double nextRandomVariable() {
        return nextRandomVariable(getMean(), getStandardDeviation(),
            getSource());
    }

    /**
     * Modify the mean.
     * 
     * @param m the new mean value.
     */
    private void setMean(double m) {
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
    private void setStandardDeviation(double std) {
        if (std <= 0.0 || Double.isNaN(std)) {
            throw new IllegalArgumentException(
                "Standard deviation must be positive.");
        }
        this.standardDeviation = std;
    }
}

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
 * A random variable generator for the Exponential distribution.
 * </p>
 * <p>
 * References:
 * <ol>
 * <li>Wikipedia contributors, "Exponential Distribution," Wikipedia, The Free
 * Encyclopedia, <a target="_blank"
 * href="http://en.wikipedia.org/wiki/Exponential_distribution">
 * http://en.wikipedia.org/wiki/Exponential_distribution</a></li>
 * </ol>
 * </p>
 * 
 * @since 1.3
 * @version $Revision: 1.4 $ $Date: 2007/11/18 23:51:19 $
 */
public class ExponentialRandomVariable extends AbstractContinuousRandomVariable {

    /** The mean. */
    private double mean;

    /**
     * Default constructor. Mean is set to 1.
     */
    public ExponentialRandomVariable() {
        this(1.0);
    }

    /**
     * Create a random variable with the given mean.
     * 
     * @param m the mean.
     */
    public ExponentialRandomVariable(double m) {
        this(m, new RandomRNG());
    }

    /**
     * Create a random variable with the given parameters.
     * 
     * @param m the mean.
     * @param source the source generator.
     */
    public ExponentialRandomVariable(double m, RNG source) {
        super(source);
        setMean(m);
    }

    /**
     * Access the next random variable using the given generator.
     * 
     * @param m the mean.
     * @param source the source generator.
     * @return the next random variable.
     */
    public static double nextRandomVariable(double m, RNG source) {
        double u;

        do {
            u = source.nextRandomNumber();
        } while (u <= 0.0);

        return -m * Math.log(u);
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
     * Access the next random variable from this generator.
     * 
     * @return the next random variable.
     */
    public double nextRandomVariable() {
        return nextRandomVariable(getMean(), getSource());
    }

    /**
     * Modify the mean.
     * 
     * @param m the new mean.
     */
    private void setMean(double m) {
        if (m <= 0.0 || Double.isNaN(m)) {
            throw new IllegalArgumentException("Mean must be positive.");
        }
        this.mean = m;
    }
}

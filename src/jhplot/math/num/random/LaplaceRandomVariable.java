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
 * A random variable generator for the Laplace distribution.
 * </p>
 * <p>
 * References:
 * <ol>
 * <li>Wikipedia contributors, "Laplace Distribution," Wikipedia, The Free
 * Encyclopedia, <a target="_blank"
 * href="http://en.wikipedia.org/wiki/Laplace_distribution">
 * http://en.wikipedia.org/wiki/Laplace_distribution</a></li>
 * </ol>
 * </p>
 * 
 * @since 1.3
 * @version $Revision: 1.3 $ $Date: 2007/11/18 23:51:19 $
 */
public class LaplaceRandomVariable extends AbstractContinuousRandomVariable {

    /** the mean. */
    private double mean;

    /** the scale. */
    private double scale;

    /**
     * Default constructor. Mean is set to zero and scale is set to one.
     */
    public LaplaceRandomVariable() {
        this(0.0, 1.0);
    }

    /**
     * Create a random variable with the given mean and scale.
     * 
     * @param m the mean.
     * @param s the scale.
     */
    public LaplaceRandomVariable(double m, double s) {
        this(m, s, new RandomRNG());
    }

    /**
     * Create a random variable with the given parameters.
     * 
     * @param m the mean.
     * @param s the scale parameter.
     * @param source the source generator.
     */
    public LaplaceRandomVariable(double m, double s, RNG source) {
        super(source);
        setMean(m);
        setScale(s);
    }

    /**
     * Access the next random variable using the given generator.
     * 
     * @param m the mean.
     * @param s the scale parameter.
     * @param source the source generator.
     * @return the next random variable.
     */
    public static double nextRandomVariable(double m, double s, RNG source) {
        double u;
        do {
            u = source.nextRandomNumber();
        } while (u <= 0.0 || u >= 1.0);

        double ret;
        if (u < 0.5) {
            ret = m + s * Math.log(1.0 + (2.0 * u - 1.0));
        } else {
            ret = m - s * Math.log(1.0 - (2.0 * u - 1.0));
        }
        return ret;
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
     * Access the scale.
     * 
     * @return the scale.
     */
    private double getScale() {
        return scale;
    }

    /**
     * Access the next random variable from this generator.
     * 
     * @return the next random variable.
     */
    public double nextRandomVariable() {
        return nextRandomVariable(getMean(), getScale(), getSource());
    }

    /**
     * Modify the mean.
     * 
     * @param m the new mean value.
     */
    private void setMean(double m) {
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
    private void setScale(double s) {
        if (s <= 0.0 || Double.isNaN(s)) {
            throw new IllegalArgumentException("scale must be positive.");
        }
        scale = s;
    }

}

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
 * A random variable generator for the Uniform distribution.
 * </p>
 * <p>
 * References:
 * <ol>
 * <li>Wikipedia contributors, "Uniform distribution (continuous)," Wikipedia, The Free
 * Encyclopedia, <a target="_blank"
 * href="http://en.wikipedia.org/wiki/Uniform_distribution_%28continuous%29">
 * http://en.wikipedia.org/wiki/Uniform_distribution_%28continuous%29</a></li>
 * </ol>
 * </p>
 * 
 * @since 1.3
 * @version $Revision: 1.4 $ $Date: 2007/11/18 23:51:19 $
 */
public class UniformRandomVariable extends AbstractContinuousRandomVariable {

    /** The lower bound parameter. */
    private double lower = -Double.MAX_VALUE;

    /** The range (<code>upper</code> - <code>lower</code>). */
    private double range;

    /** The upper bound parameter. */
    private double upper = Double.MAX_VALUE;

    /**
     * Default constructor. The lower bound is set to 0 and the upper bound is
     * set to 1.
     */
    public UniformRandomVariable() {
        this(0.0, 1.0);
    }

    /**
     * Create a random variable with the given lower bound and upper bound values.
     * 
     * @param a the lower bound parameter.
     * @param b the upper bound parameter.
     */
    public UniformRandomVariable(double a, double b) {
        this(a, b, new RandomRNG());
    }

    /**
     * Create a random variable with the given parameters.
     * 
     * @param a the lower bound parameter.
     * @param b the upper bound parameter.
     * @param rng the source generator.
     */
    public UniformRandomVariable(double a, double b, RNG rng) {
        super(rng);
        if (a <= b) {
            setLower(a);
            setUpper(b);
        } else {
            throw new IllegalArgumentException(
                "Lower bound must be less than upper bound.");
        }
    }

    /**
     * Access the next random variable using the given generator.
     * 
     * @param a the lower bound parameter.
     * @param b the upper bound parameter.
     * @param source the source generator.
     * @return the next random variable.
     */
    public static double nextRandomVariable(double a, double b,
        RNG source) {
        return source.nextRandomNumber() * (b - a) + a;
    }

    /**
     * Access the lower parameter.
     * 
     * @return the lower parameter.
     */
    private double getLower() {
        return lower;
    }

    /**
     * Access the range value.
     * 
     * @return the range.
     */
    private double getRange() {
        return range;
    }

    /**
     * Access the upper parameter.
     * 
     * @return the upper parameter.
     */
    private double getUpper() {
        return upper;
    }

    /**
     * Access the next random variable from this generator.
     * 
     * @return the next random variable.
     */
    public double nextRandomVariable() {
        return nextRandomNumber() * getRange() + getLower();
    }

    /**
     * Modify the lower bound parameter.
     * 
     * @param low the new lower bound value.
     */
    private void setLower(double low) {
        this.lower = low;
        setRange(getUpper() - low);
    }

    /**
     * Modify the range value.
     * 
     * @param value the new range value.
     */
    private void setRange(double value) {
        this.range = value;
    }

    /**
     * Modify the upper bound parameter.
     * 
     * @param up the new upper bound value.
     */
    private void setUpper(double up) {
        this.upper = up;
        setRange(up - getLower());
    }
}

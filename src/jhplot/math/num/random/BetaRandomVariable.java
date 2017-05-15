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
 * A random variable generator for the Beta distribution.
 * </p>
 * <p>
 * References:
 * <ol>
 * <li>Wikipedia contributors, "Beta Distribution," Wikipedia, The Free
 * Encyclopedia, <a target="_blank"
 * href="http://en.wikipedia.org/wiki/Beta_distribution">
 * http://en.wikipedia.org/wiki/Beta_distribution</a></li>
 * </ol>
 * </p>
 * 
 * @since 1.3
 * @version $Revision: 1.2 $ $Date: 2007/11/18 23:51:19 $
 */
public class BetaRandomVariable extends AbstractContinuousRandomVariable {

    /** the alpha parameter. */
    private double alpha;

    /** the beta parameter. */
    private double beta;

    /**
     * Default constructor. Alpha and beta are both set to 1.
     */
    public BetaRandomVariable() {
        this(1.0, 1.0);
    }

    /**
     * Create a random variable with the given alpha and beta values.
     * 
     * @param a the alpha parameter.
     * @param b the beta parameter.
     */
    public BetaRandomVariable(double a, double b) {
        this(a, b, new RandomRNG());
    }

    /**
     * Create a random variable with the given parameters.
     * 
     * @param a the alpha parameter.
     * @param b the beta parameter.
     * @param source the source generator
     */
    public BetaRandomVariable(double a, double b, RNG source) {
        super(source);
        setAlpha(a);
        setBeta(b);
    }

    /**
     * Access the next random variable using the given generator.
     * 
     * @param a the alpha parameter.
     * @param b the beta parameter.
     * @param source the source generator
     * @return the next random variable.
     */
    public static double nextRandomVariable(double a, double b, RNG source) {
        double x = GammaRandomVariable.nextRandomVariable(a, 1.0, source);
        double y = GammaRandomVariable.nextRandomVariable(b, 1.0, source);
        return x / (x + y);
    }

    /**
     * Access the alpha parameter.
     * 
     * @return the alpha parameter.
     */
    private double getAlpha() {
        return alpha;
    }

    /**
     * Access the beta parameter.
     * 
     * @return the beta parameter.
     */
    private double getBeta() {
        return beta;
    }

    /**
     * Access the next random variable from this generator.
     * 
     * @return the next random variable.
     */
    public double nextRandomVariable() {
        return nextRandomVariable(getAlpha(), getBeta(), getSource());
    }

    /**
     * Modify the alpha parameter.
     * 
     * @param a the new alpha value.
     */
    private void setAlpha(double a) {
        if (a <= 0.0 || Double.isNaN(a)) {
            throw new IllegalArgumentException("Alpha must be positive.");
        }
        this.alpha = a;
    }

    /**
     * Modify the beta parameter.
     * 
     * @param b the new beta value.
     */
    private void setBeta(double b) {
        if (b <= 0.0 || Double.isNaN(b)) {
            throw new IllegalArgumentException("Beta must be positive.");
        }
        this.beta = b;
    }
}

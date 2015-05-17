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
 * A random variable generator for the Gamma distribution.
 * </p>
 * <p>
 * References:
 * <ol>
 * <li>Wikipedia contributors, "Gamma distribution," Wikipedia, The Free
 * Encyclopedia, <a target="_blank"
 * href="http://en.wikipedia.org/wiki/Gamma_distribution">
 * http://en.wikipedia.org/wiki/Gamma_distribution</a></li>
 * </ol>
 * </p>
 * 
 * @since 1.3
 * @version $Revision: 1.5 $ $Date: 2007/11/18 23:51:19 $
 */
public class GammaRandomVariable extends AbstractContinuousRandomVariable {

    /** The alpha parameter. */
    private double alpha;

    /** The beta parameter. */
    private double beta;

    /** the alpha based strategy used to generate random variables. */
    private ContinuousRandomVariable strategy;

    /**
     * Default constructor. Alpha and beta are both set to 1.
     */
    public GammaRandomVariable() {
        this(1.0, 1.0);
    }

    /**
     * Create a random variable with the given alpha and beta values.
     * 
     * @param a the alpha parameter.
     * @param b the beta parameter.
     */
    public GammaRandomVariable(double a, double b) {
        this(a, b, new RandomRNG());
    }

    /**
     * Create a random variable with the given parameters.
     * 
     * @param a the alpha parameter.
     * @param b the beta parameter.
     * @param source the source generator
     */
    public GammaRandomVariable(double a, double b, RNG source) {
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
        double x;
        if (a <= 1.0) {
            x = nextRandomVariableSmallAlpha(a, b, source);
        } else {
            x = nextRandomVariableLargeAlpha(a, b, source);
        }
        return x;
    }

    /**
     * <p>
     * Access the next random variable using the given generator. In order for
     * this method to function correctly, <code>a</code> must be in the
     * inverval (1.0, +Infinity). This method assumes the condition is met and
     * no check is performed.
     * </p>
     * <p>
     * The implementation of this method is based on Best's rejection algoritm.
     * </p>
     * 
     * @param a the alpha parameter.
     * @param b the beta parameter.
     * @param source the source generator
     * @return the next random variable.
     */
    private static double nextRandomVariableLargeAlpha(double a, double b,
        RNG source) {
        double d = a - 1;
        double c = 3.0 * a - 3.0 / 4.0;
        boolean accepted = false;
        double u;
        double v;
        double x = 0.0;
        do {
            u = source.nextRandomNumber();
            v = source.nextRandomNumber();
            double w = u * (1.0 - u);
            double y = Math.sqrt(c / w) * (u - 0.5);
            x = d + y;
            if (x >= 0.0) {
                double z = 64.0 * w * w * w * v * v;
                accepted = (z <= 1.0 - ((2.0 * y * y) / x))
                    || (Math.log(z) <= 2.0 * (d * Math.log(x / d) - y));
            }
        } while (!accepted);
        return b * x;
    }

    /**
     * Access the next random variable using the given generator. In order for
     * this method to function correctly, <code>a</code> must be in the
     * inverval (0.0, 1.0]. This method assumes the condition is met and no
     * check is performed.
     * 
     * @param a the alpha parameter.
     * @param b the beta parameter.
     * @param source the source generator
     * @return the next random variable.
     */
    private static double nextRandomVariableSmallAlpha(double a, double b,
        RNG source) {
        double x;
        boolean rejected = false;
        do {
            double u0 = source.nextRandomNumber();
            double u1 = source.nextRandomNumber();
            if (u0 <= Math.E / (Math.E + a)) {
                x = Math.pow((Math.E + a) * u0 / Math.E, 1.0 / a);
                rejected = u1 > Math.exp(-x);
            } else {
                x = -Math.log((Math.E + a) * (1.0 - u0) / (a * Math.E));
                rejected = u1 > Math.pow(x, a - 1.0);
            }
        } while (rejected);

        return b * x;
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
        return strategy.nextRandomVariable();
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
        if (alpha <= 1.0) {
            strategy = new ContinuousRandomVariable() {
                public double nextRandomVariable() {
                    return nextRandomVariableSmallAlpha(getAlpha(), getBeta(),
                        getSource());
                }
            };
        } else {
            strategy = new ContinuousRandomVariable() {
                public double nextRandomVariable() {
                    return nextRandomVariableLargeAlpha(getAlpha(), getBeta(),
                        getSource());
                }
            };
        }
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

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
 * A random variable generator for the F distribution.
 * </p>
 * <p>
 * References:
 * <ol>
 * <li>Wikipedia contributors, "F-distribution," Wikipedia, The Free
 * Encyclopedia, <a target="_blank"
 * href="http://en.wikipedia.org/wiki/F_distribution">
 * http://en.wikipedia.org/wiki/F_distribution</a></li>
 * </ol>
 * </p>
 * 
 * @since 1.3
 * @version $Revision: 1.2 $ $Date: 2007/11/08 17:37:57 $
 */
public class FRandomVariable extends AbstractContinuousRandomVariable {

    /** The denominator degrees of freedom. */
    private double denominatorDegreesOfFreedom;

    /** The numerator degrees of freedom. */
    private double numeratorDegreesOfFreedom;

    /**
     * Default constructor. Numerator degrees of freedom and denominator degrees
     * of freedom are both set to 1.
     */
    public FRandomVariable() {
        this(1.0, 1.0);
    }

    /**
     * Create a random variable with the given numerator degrees of freedom and
     * denominator degrees of freedom.
     * 
     * @param dfn the numerator degrees of freedom.
     * @param dfd the denominator degrees of freedom.
     */
    public FRandomVariable(double dfn, double dfd) {
        this(dfn, dfd, new RandomRNG());
    }

    /**
     * Create a random variable with the given parameters.
     * 
     * @param dfn the numerator degrees of freedom.
     * @param dfd the denominator degrees of freedom.
     * @param source the source generator.
     */
    public FRandomVariable(double dfn, double dfd, RNG source) {
        super(source);
        setNumeratorDegreesOfFreedom(dfn);
        setDenominatorDegreesOfFreedom(dfd);
    }

    /**
     * Access the next random variable using the given generator.
     * 
     * @param dfn the numerator degrees of freedom.
     * @param dfd the denominator degrees of freedom.
     * @param source the source generator.
     * @return the next random variable.
     */
    public static double nextRandomVariable(double dfn, double dfd, RNG source) {
        double x1 = ChiSquaredRandomVariable.nextRandomVariable(dfn, source)
            / dfn;
        double x2 = ChiSquaredRandomVariable.nextRandomVariable(dfd, source);
        return x1 / x2;
    }

    /**
     * Access the denominator degrees of freedom.
     * 
     * @return the denominator degrees of freedom.
     */
    private double getDenominatorDegreesOfFreedom() {
        return denominatorDegreesOfFreedom;
    }

    /**
     * Access the numerator degrees of freedom.
     * 
     * @return the numerator degrees of freedom.
     */
    private double getNumeratorDegreesOfFreedom() {
        return numeratorDegreesOfFreedom;
    }

    /**
     * Access the next random variable from this generator.
     * 
     * @return the next random variable.
     */
    public double nextRandomVariable() {
        return nextRandomVariable(getNumeratorDegreesOfFreedom(),
            getDenominatorDegreesOfFreedom(), getSource());
    }

    /**
     * Modify the denominator degrees of freedom.
     * 
     * @param degreesOfFreedom the new denominator degrees of freedom.
     */
    private void setDenominatorDegreesOfFreedom(double degreesOfFreedom) {
        if (degreesOfFreedom <= 0.0 || Double.isNaN(degreesOfFreedom)) {
            throw new IllegalArgumentException(
                "degrees of freedom must be positive.");
        }
        this.denominatorDegreesOfFreedom = degreesOfFreedom;
    }

    /**
     * Modify the numerator degrees of freedom.
     * 
     * @param degreesOfFreedom the new numerator degrees of freedom.
     */
    private void setNumeratorDegreesOfFreedom(double degreesOfFreedom) {
        if (degreesOfFreedom <= 0.0 || Double.isNaN(degreesOfFreedom)) {
            throw new IllegalArgumentException(
                "degrees of freedom must be positive.");
        }
        this.numeratorDegreesOfFreedom = degreesOfFreedom;
    }
}

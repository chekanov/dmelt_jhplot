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
 * A random variable generator for Student's t distribution.
 * </p>
 * <p>
 * References:
 * <ol>
 * <li>Wikipedia contributors, "Student's t-Distribution," Wikipedia, The Free
 * Encyclopedia, <a target="_blank"
 * href="http://en.wikipedia.org/wiki/T_distribution">
 * http://en.wikipedia.org/wiki/T_distribution</a></li>
 * </ol>
 * </p>
 * 
 * @since 1.3
 * @version $Revision: 1.3 $ $Date: 2007/11/18 23:51:19 $
 */
public class TRandomVariable extends AbstractContinuousRandomVariable {

    /** The degreesOfFreedom. */
    private double degreesOfFreedom;

    /**
     * Default constructor. Degrees of freedom is set to 1.
     */
    public TRandomVariable() {
        this(1.0);
    }

    /**
     * Create a random variable with the given degrees of freedom.
     * 
     * @param df degrees of freedom.
     */
    public TRandomVariable(double df) {
        this(df, new RandomRNG());
    }

    /**
     * Create a random variable with the given parameters.
     * 
     * @param df degrees of freedom.
     * @param source the source generator.
     */
    public TRandomVariable(double df, RNG source) {
        super(source);
        setDegreesOfFreedom(df);
    }

    /**
     * Access the next random variable using the given generator.
     * 
     * @param df degrees of freedom.
     * @param source the source generator.
     * @return the next random variable.
     */
    public static double nextRandomVariable(double df, RNG source) {
        double x;
        double v;
        do {
            double u0 = source.nextRandomNumber();
            double u1 = source.nextRandomNumber();

            if (u0 < 0.5) {
                x = 1.0 / (4.0 * u0 - 1.0);
                v = u1 / (x * x);
            } else {
                x = 4.0 * u0 - 3.0;
                v = u1;
            }
        } while (v >= 1.0 - 0.5 * Math.abs(x)
            && v >= Math.pow(1.0 + (x * x) / df, -0.5 * (df + 1.0)));
        return x;
    }

    /**
     * Access the degrees of freedom.
     * 
     * @return the degrees of freedom.
     */
    private double getDegreesOfFreedom() {
        return degreesOfFreedom;
    }

    /**
     * Access the next random variable from this generator.
     * 
     * @return the next random variable.
     */
    public double nextRandomVariable() {
        return nextRandomVariable(getDegreesOfFreedom(), getSource());
    }

    /**
     * Modify the degrees of freedom.
     * 
     * @param df the new degrees of freedom value.
     */
    private void setDegreesOfFreedom(double df) {
        if (df <= 0.0 || Double.isNaN(df)) {
            throw new IllegalArgumentException(
                "Degrees of freedom must be positive.");
        }
        this.degreesOfFreedom = df;
    }
}

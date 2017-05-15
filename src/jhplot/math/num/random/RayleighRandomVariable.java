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
 * A random variable generator for the Rayleigh distribution.
 * </p>
 * <p>
 * References:
 * <ol>
 * <li>Wikipedia contributors, "Rayleigh Distribution," Wikipedia, The Free
 * Encyclopedia, <a target="_blank"
 * href="http://en.wikipedia.org/wiki/Rayleigh_distribution">
 * http://en.wikipedia.org/wiki/Rayleigh_distribution</a></li>
 * </ol>
 * </p>
 * 
 * @since 1.3
 * @version $Revision: 1.4 $ $Date: 2007/11/18 23:51:19 $
 */
public class RayleighRandomVariable extends AbstractContinuousRandomVariable {

    /** The scale parameter. */
    private double scale;

    /**
     * Default constructor. Alpha and scale are both set to 1.
     */
    public RayleighRandomVariable() {
        this(1.0);
    }

    /**
     * Create a random variable with the given mean.
     * 
     * @param s the scale parameter.
     */
    public RayleighRandomVariable(double s) {
        this(s, new RandomRNG());
    }

    /**
     * Create a random variable with the given parameters.
     * 
     * @param s the scale parameter.
     * @param source the source generator.
     */
    public RayleighRandomVariable(double s, RNG source) {
        super(source);
        setScale(s);
    }

    /**
     * Access the next random variable using the given generator.
     * 
     * @param s the scale parameter.
     * @param source the source generator.
     * @return the next random variable.
     */
    public static double nextRandomVariable(double s, RNG source) {
        double u;

        do {
            u = source.nextRandomNumber();
        } while (u <= 0.0);

        return Math.sqrt(-2.0 * Math.log(u)) * s;
    }

    /**
     * Access the scale parameter.
     * 
     * @return the scale parameter.
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
        return nextRandomVariable(getScale(), getSource());
    }

    /**
     * Modify the scale parameter.
     * 
     * @param b the new scale value.
     */
    private void setScale(double b) {
        if (b <= 0.0 || Double.isNaN(b)) {
            throw new IllegalArgumentException("scale must be positive.");
        }
        this.scale = b;
    }
}

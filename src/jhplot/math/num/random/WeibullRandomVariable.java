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
 * A random variable generator for the Weibull distribution.
 * </p>
 * <p>
 * References:
 * <ol>
 * <li>Wikipedia contributors, "Weibull Distribution," Wikipedia, The Free
 * Encyclopedia, <a target="_blank"
 * href="http://en.wikipedia.org/wiki/Weibull_distribution">
 * http://en.wikipedia.org/wiki/Weibull_distribution</a></li>
 * </ol>
 * </p>
 * 
 * @since 1.3
 * @version $Revision: 1.3 $ $Date: 2007/11/08 17:37:57 $
 */
public class WeibullRandomVariable extends AbstractContinuousRandomVariable {

    /** the location parameter. */
    private double location;

    /** The scale parameter. */
    private double scale;

    /** The shape parameter. */
    private double shape;

    /**
     * Default constructor. Shape and scale are both set to 1 and location is set to 0.
     */
    public WeibullRandomVariable() {
        this(1.0, 1.0, 0.0);
    }

    /**
     * Create a rnadom variable with the given shape and scale values.  Location is set to 0.
     * 
     * @param a the shape parameter.
     * @param b the scale parameter.
     */
    public WeibullRandomVariable(double a, double b) {
        this(a, b, 0.0, new RandomRNG());
    }

    /**
     * Create a random variable with the given parameters.
     * 
     * @param a the shape parameter.
     * @param b the scale parameter.
     * @param c the location parameter.
     */
    public WeibullRandomVariable(double a, double b, double c) {
        this(a, b, c, new RandomRNG());
    }

    /**
     * Create a random variable with the given parameters.
     * 
     * @param a the shape parameter.
     * @param b the scale parameter.
     * @param c the location parameter.
     * @param source the source generator.
     */
    public WeibullRandomVariable(double a, double b, double c, RNG source) {
        super(source);
        setShape(a);
        setScale(b);
        setLocation(c);
    }

    /**
     * Create a random variable with the given shape and scale values.  Location is set to 0.
     * 
     * @param a the shape parameter.
     * @param b the scale parameter.
     * @param source the source generator.
     */
    public WeibullRandomVariable(double a, double b, RNG source) {
        this(a, b, 0.0, source);
    }

    /**
     * Access the next random variable using the given generator.
     * 
     * @param shape the shape parameter.
     * @param scale the scale parameter.
     * @param location the location parameter.
     * @param source the source generator.
     * @return the next random variable.
     */
    public static double nextRandomVariable(double shape, double scale,
        double location, RNG source) {
        double u;

        do {
            u = source.nextRandomNumber();
        } while (u <= 0.0);

        return Math.pow(-Math.log(u), 1.0 / shape) * scale + location;
    }

    /**
     * Access the location value.
     * 
     * @return the location.
     */
    private double getLocation() {
        return location;
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
     * Access the shape parameter.
     * 
     * @return the shape parameter.
     */
    private double getShape() {
        return shape;
    }

    /**
     * Access the next random variable from this generator.
     * 
     * @return the next random variable.
     */
    public double nextRandomVariable() {
        return nextRandomVariable(getShape(), getScale(), getLocation(),
            getSource());
    }

    /**
     * Modify the location value.
     * 
     * @param l the new location value.
     */
    private void setLocation(double l) {
        if (Double.isNaN(l)) {
            throw new IllegalArgumentException(
                "location parameter must be a number.");
        }
        this.location = l;
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

    /**
     * Modify the shape parameter.
     * 
     * @param a the new shape value.
     */
    private void setShape(double a) {
        if (a <= 0.0 || Double.isNaN(a)) {
            throw new IllegalArgumentException("shape must be positive.");
        }
        this.shape = a;
    }
}

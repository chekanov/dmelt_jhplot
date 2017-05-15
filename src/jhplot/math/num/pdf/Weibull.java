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
package jhplot.math.num.pdf;


/**
 * <p>
 * The Weibull distribution.
 * </p>
 * <p>
 * References:
 * <ol>
 * <li> Eric W. Weisstein. "Weibull Distribution." From MathWorld--A Wolfram Web
 * Resource. <a target="_blank"
 * href="http://mathworld.wolfram.com/Weibull.html">
 * http://mathworld.wolfram.com/Weibull.html</a> </li>
 * </ol>
 * </p>
 * 
 * @since 1.3
 * @version $Revision: 1.3 $ $Date: 2007/11/08 17:38:02 $
 */
public class Weibull extends ContinuousDistribution {

    /** the location parameter. */
    private double location;

    /** the scale parameter. */
    private double scale;

    /** the shape parameter. */
    private double shape;

    /**
     * Default constructor. The shape parameter is set to one, the scale
     * parameter is set to one and, the location parameter is set to zero.
     */
    public Weibull() {
        this(1.0, 1.0, 0.0);
    }

    /**
     * Create a distribution with the given parameters. The location parameter
     * is set to zero.
     * 
     * @param sh the shape parameter.
     * @param sc the scale parameter.
     */
    public Weibull(double sh, double sc) {
        this(sh, sc, 0.0);
    }

    /**
     * Create a distribution with the given parameters.
     * 
     * @param sh the shape parameter.
     * @param sc the scale parameter.
     * @param l the location parameter.
     */
    public Weibull(double sh, double sc, double l) {
        super();
        setShape(sh);
        setScale(sc);
        setLocation(l);
    }

    /**
     * The CDF for this distribution. This method returns P(X &lt; x).
     * 
     * @param x the value at which the CDF is evaluated.
     * @return CDF for this distribution.
     */
    public double cumulativeProbability(double x) {
        double ret;

        if (Double.isNaN(x)) {
            ret = Double.NaN;
        } else if (x <= getLocation()) {
            ret = 0.0;
        } else if (Double.isInfinite(x)) {
            ret = 1.0;
        } else {
            ret = 1.0 - Math.exp(-Math.pow((x - getLocation()) / getScale(),
                getShape()));
        }

        return ret;
    }

    /**
     * Access the location parameter.
     * 
     * @return the location parameter.
     */
    public double getLocation() {
        return location;
    }

    /**
     * Access the scale parameter.
     * 
     * @return the scale parameter.
     */
    public double getScale() {
        return scale;
    }

    /**
     * Access the shape parameter.
     * 
     * @return the shape parameter.
     */
    public double getShape() {
        return shape;
    }

    /**
     * The inverse CDF for this distribution. This method returns x such that,
     * P(X &lt; x) = p.
     * 
     * @param p the cumulative probability.
     * @return x
     */
    public double inverseCumulativeProbability(double p) {
        double ret;

        if (p < 0.0 || p > 1.0 || Double.isNaN(p)) {
            ret = Double.NaN;
        } else if (p == 0.0) {
            ret = getLocation();
        } else if (p == 1.0) {
            ret = Double.POSITIVE_INFINITY;
        } else {
            ret = getScale() * Math.pow(-Math.log(1.0 - p), 1.0 / getShape())
                + getLocation();
        }

        return ret;
    }

    /**
     * Modify the location parameter.
     * 
     * @param l the new location parameter.
     */
    public void setLocation(double l) {
        if (Double.isNaN(l)) {
            throw new IllegalArgumentException(
                "location parameter must be a number.");
        }
        this.location = l;
    }

    /**
     * Modify the scale parameter.
     * 
     * @param s The new scale parameter.
     */
    public void setScale(double s) {
        if (s <= 0.0 || Double.isNaN(s)) {
            throw new IllegalArgumentException(
                "scale parameter must be positive.");
        }
        this.scale = s;
    }

    /**
     * Modify the shape parameter.
     * 
     * @param s the new shape parameter.
     */
    public void setShape(double s) {
        if (s <= 0.0 || Double.isNaN(s)) {
            throw new IllegalArgumentException(
                "shape parameter must be positive.");
        }
        this.shape = s;
    }
}

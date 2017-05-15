/*
 * Copyright (c) 2005, DoodleProject
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
 * The Uniform distribution (1).
 * </p>
 * <p>
 * References:
 * <ol>
 * <li> Eric W. Weisstein. "Uniform Distribution." From MathWorld--A Wolfram Web
 * Resource. <a target="_blank"
 * href="http://mathworld.wolfram.com/Uniform.html">
 * http://mathworld.wolfram.com/Uniform.html</a> </li>
 * </ol>
 * </p>
 * 
 * @since 1.1
 * @version $Revision: 1.3 $ $Date: 2007/11/18 23:51:21 $
 */
public class Uniform extends ContinuousDistribution {

    /** The lower bound parameter. */
    private double lower = -Double.MAX_VALUE;

    /** The range for this distribution. */
    private double range;

    /** The upper bound parameter. */
    private double upper = Double.MAX_VALUE;

    /**
     * Default constructor. The lower bound is set to 0 and the upper bound is
     * set to 1.
     */
    public Uniform() {
        this(0.0, 1.0);
    }

    /**
     * Create a distribution with the given lower bound and upper bound values.
     * 
     * @param a the lower bound parameter.
     * @param b the upper bound parameter.
     */
    public Uniform(double a, double b) {
        super();
        if (a <= b) {
            setLower(a);
            setUpper(b);
        } else {
            throw new IllegalArgumentException(
                "Lower bound must be less than upper bound.");
        }
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
        } else if (x <= lower) {
            ret = 0.0;
        } else if (x >= upper) {
            ret = 1.0;
        } else {
            ret = (x - lower) / getRange();
        }

        return ret;
    }

    /**
     * Access the lower parameter.
     * 
     * @return the lower parameter.
     */
    public double getLower() {
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
    public double getUpper() {
        return upper;
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
            ret = lower;
        } else if (p == 1.0) {
            ret = upper;
        } else {
            ret = lower + p * getRange();
        }

        return ret;
    }

    /**
     * Modify the lower bound parameter.
     * 
     * @param low the new lower bound value.
     */
    public void setLower(double low) {
        if (Double.isNaN(low)) {
            throw new IllegalArgumentException("Lower bound must be a number.");
        }
        if (low > upper) {
            throw new IllegalArgumentException(
                "Lower bound must be less than upper bound.");
        }
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
    public void setUpper(double up) {
        if (Double.isNaN(up)) {
            throw new IllegalArgumentException("Upper bound must be a number.");
        }
        if (up < lower) {
            throw new IllegalArgumentException(
                "Upper bound must be greater than lower bound.");
        }
        this.upper = up;
        setRange(up - getLower());
    }
}

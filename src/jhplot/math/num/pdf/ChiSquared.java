/*
 * Copyright (c) 2004-2005, DoodleProject
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

import jhplot.math.num.NumericException;

/**
 * <p>
 * The Chi-Squared distribution (1).
 * </p>
 * <p>
 * <ol>
 * <li> Eric W. Weisstein. "Chi-Squared Distribution." From MathWorld--A Wolfram
 * Web Resource. <a target="_blank"
 * href="http://mathworld.wolfram.com/Chi-SquaredDistribution.html">
 * http://mathworld.wolfram.com/Chi-SquaredDistribution.html</a> </li>
 * </ol>
 * </p>
 * 
 * @version $Revision: 1.2 $ $Date: 2007/10/25 04:44:10 $
 */
public class ChiSquared extends ContinuousDistribution {

    /** Internal gamma distribution. */
    private Gamma gamma;

    /**
     * Default constructor. Degrees of freedom is set to 1.
     */
    public ChiSquared() {
        this(1.0);
    }

    /**
     * Create a distribution with the given degrees of freedom.
     * 
     * @param df degrees of freedom.
     */
    public ChiSquared(double df) {
        super();
        setGamma(new Gamma(df / 2.0, 2.0));
    }

    /**
     * The CDF for this distribution. This method returns P(X &lt; x).
     * 
     * @param x the value at which the CDF is evaluated.
     * @return CDF for this distribution.
     * @throws NumericException if the cumulative probability can not be
     *         computed.
     */
    public double cumulativeProbability(double x) throws NumericException {
        return getGamma().cumulativeProbability(x);
    }

    /**
     * Access the degrees of freedom.
     * 
     * @return the degrees of freedom.
     */
    public double getDegreesOfFreedom() {
        return getGamma().getAlpha() * 2.0;
    }

    /**
     * Access the internal gamma distribution.
     * 
     * @return the gamma distribution.
     */
    private Gamma getGamma() {
        return gamma;
    }

    /**
     * The inverse CDF for this distribution. This method returns x such that,
     * P(X &lt; x) = p.
     * 
     * @param p the cumulative probability.
     * @return x
     * @throws NumericException if the inverse cumulative probability can not be
     *         computed.
     */
    public double inverseCumulativeProbability(double p)
        throws NumericException {
        return getGamma().inverseCumulativeProbability(p);
    }

    /**
     * Modify the degrees of freedom.
     * 
     * @param df The new degrees of freedom value.
     */
    public void setDegreesOfFreedom(double df) {
        getGamma().setAlpha(df / 2.0);
    }

    /**
     * Modify the internal gamma distribution.
     * 
     * @param g the new internal gamma distribution.
     */
    private void setGamma(Gamma g) {
        this.gamma = g;
    }
}

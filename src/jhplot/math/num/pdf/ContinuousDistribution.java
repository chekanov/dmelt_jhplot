/*
 * Copyright (c) 2004, DoodleProject
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

import jhplot.math.num.Function;
import jhplot.math.num.NumericException;
import jhplot.math.num.root.BisectionRootFinder;
import jhplot.math.num.root.Bracket;

/**
 * Base continuous distribution.
 * 
 * @version $Revision: 1.2 $ $Date: 2007/10/25 04:44:10 $
 */
public abstract class ContinuousDistribution implements Distribution {

    /**
     * The CDF for this distribution. This method returns P(X &lt; x).
     * 
     * @param x the value at which the CDF is evaluated.
     * @return CDF for this distribution.
     * @throws NumericException if the cumulative probability can not be
     *         computed.
     */
    public abstract double cumulativeProbability(double x)
        throws NumericException;

    /**
     * The inverse CDF for this distribution. This method returns x such that,
     * P(X &lt; x) = p.
     * 
     * @param p the cumulative probability.
     * @return x
     * @throws NumericException if the inverse cumulative probability can not be
     *         computed.
     */
    public abstract double inverseCumulativeProbability(double p)
        throws NumericException;

    /**
     * Generic means to compute inverse cumulative probability values. This
     * method uses the bisection method to find inverse CDF values.
     * 
     * @param p the cumulative probability.
     * @param lower the global lower bound of the inverse CDF value.
     * @param initial an initial guess at the inverse CDF value.
     * @param upper the global upper bound of the inverse CDF value.
     * @return x such that P(X &lt; x) = p
     * @throws NumericException if the inverse cumulative probability can not be
     *         computed.
     */
    protected double findInverseCumulativeProbability(final double p,
        double lower, double initial, double upper) throws NumericException {
        Function f = new Function() {
            public double evaluate(double x) throws NumericException {
                return cumulativeProbability(x) - p;
            }
        };

        Bracket b = new Bracket(f);
        double[] bracket = b.bracketOut(lower, initial, upper);

        BisectionRootFinder bisection = new BisectionRootFinder(f);
        return bisection.findRoot(bracket[0], bracket[1]);
    }
}

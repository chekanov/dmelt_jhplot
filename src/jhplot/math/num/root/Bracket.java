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
package jhplot.math.num.root;

import jhplot.math.num.ConvergenceException;
import jhplot.math.num.Function;
import jhplot.math.num.NumericException;

/**
 * Simple root bracketing routine. Starting with an initial point, a closed
 * interval can be created known to contain at least one root for a function.
 * 
 * @version $Revision: 1.2 $ $Date: 2007/10/25 04:44:14 $
 */
public class Bracket {

    /** The default maximum number of iterations to perform. */
    private static final int DEFAULT_MAXIMUM_ITERATIONS = 100;

    /** The target function. */
    private Function function;

    /** The maximum number of iterations. */
    private int maximumIterations;

    /**
     * Create a root bracketer for the given function.
     * 
     * @param f the target function.
     */
    public Bracket(Function f) {
        this(f, DEFAULT_MAXIMUM_ITERATIONS);
    }

    /**
     * Create a root bracketer for the given function.
     * 
     * @param f the target function.
     * @param iterations maximum number of iterations.
     */
    public Bracket(Function f, int iterations) {
        super();
        setFunction(f);
        setMaximumIterations(iterations);
    }

    /**
     * Bracket a root for the target function by creating and continuously
     * expanding an interval around the <tt>initial</tt> point until the
     * interval is known to contain at least one root. An interval is determined
     * to contain at least one root when the function evaluation at the inteval
     * endpoints are oppisite signs.
     * 
     * @param lower the lowest possible lower bound for the bracketting
     *        interval. The lower bound for the returned interval will never be
     *        smaller than <tt>lower</tt>.
     * @param initial the start point used to create the bracketting interval.
     * @param upper the largest possible upper bound for the bracketting
     *        interval. The upper bound for the returned interval will never be
     *        larger than <tt>upper</tt>.
     * @return a two element array containing the lower and upper bounds of the
     *         bracketting interval.
     * @throws NumericException if a root could not be bracketed.
     */
    public double[] bracketOut(double lower, double initial, double upper)
        throws NumericException {
        if (lower > initial) {
            throw new IllegalArgumentException(
                "Lower bound must be less than initial value.");
        }

        if (initial > upper) {
            throw new IllegalArgumentException(
                "Upper bound must be greater than initial value.");
        }

        double[] ret;

        if (Double.isNaN(lower) || Double.isNaN(initial) || Double.isNaN(upper)) {
            ret = new double[] { Double.NaN, Double.NaN };
        } else {
            double a = initial;
            double b = a;
            double fa;
            double fb;
            int n = 0;
            double factor = Math.abs(a * .10);
            double change = 0.0;

            do {
                n += 1;
                change += factor;
                a = Math.max(a - change, lower);
                b = Math.min(b + change, upper);
                fa = getFunction().evaluate(a);
                fb = getFunction().evaluate(b);
            } while ((fa * fb > 0.0) && (n < getMaximumIterations()));

            if (n >= getMaximumIterations()) {
                throw new ConvergenceException(
                    "the initial bounds do not bracket a root.");
            }

            ret = new double[] { a, b };
        }

        return ret;
    }

    /**
     * Access the target function.
     * 
     * @return the target function.
     */
    public Function getFunction() {
        return function;
    }

    /**
     * Access the maximum number of iterations.
     * 
     * @return the maximum number of iterations.
     */
    public int getMaximumIterations() {
        return maximumIterations;
    }

    /**
     * Modify the target function.
     * 
     * @param f the new target function.
     */
    public void setFunction(Function f) {
        if (f == null) {
            throw new IllegalArgumentException("Function can not be null.");
        }
        this.function = f;
    }

    /**
     * Modify the maximum number of iterations.
     * 
     * @param iterations the new maximum number of iterations.
     */
    public void setMaximumIterations(int iterations) {
        if (iterations <= 0) {
            throw new IllegalArgumentException(
                "Maximum iterations must be positive.");
        }
        this.maximumIterations = iterations;
    }
}

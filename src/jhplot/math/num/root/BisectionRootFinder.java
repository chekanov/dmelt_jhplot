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

import jhplot.math.num.Function;
import jhplot.math.num.IterativeMethod;
import jhplot.math.num.NumericException;

/**
 * <p>
 * The bisection method (1) for finding roots of functions.
 * </p>
 * <p>
 * For example, to find roots for sine, first a
 * {@link jhplot.math.num.Function} is defined:
 * 
 * <pre>
 * Function sine = new Function() {
 *    public double evaluate(double x) {
 *        return Math.sin(x);
 *    }}
 * };
 * </pre>
 * 
 * </p>
 * <p>
 * Then, a bisection root finder is created with the above function:
 * 
 * <pre>
 * BisectionRootFinder finder = new BisectionRootFinder(sine);
 * </pre>
 * 
 * </p>
 * <p>
 * Lastly, locating roots is accomplished using the {@link #findRoot} method:
 * 
 * <pre>
 * // find the root between 3 and 4.
 * double pi = finder.findRoot(3.0, 4.0);
 * 
 * // find the root between -1 and 1.
 * double zero = finder.findRoot(-1.0, 1.0);
 * </pre>
 * 
 * </p>
 * <p>
 * References:
 * <ol>
 * <li> Eric W. Weisstein. "Bisection." From MathWorld--A Wolfram Web Resource.
 * <a target="_blank" href="http://mathworld.wolfram.com/Bisection.html">
 * http://mathworld.wolfram.com/Bisection.html</a> </li>
 * </ol>
 * </p>
 * 
 * @version $Revision: 1.3 $ $Date: 2007/10/27 04:57:42 $
 */
public class BisectionRootFinder extends IterativeMethod {

    /** the target function. */
    private Function function;

    /**
     * The internal state used during root finding.
     */
    private class IterativeState implements IterativeMethod.IterativeState {
        /** The current function value for the interval midpoint. */
        private double fm;

        /** The current function value for the interval lower bound. */
        private double fmin;

        /** The current interval midpoint. */
        private double m;

        /** The current interval upper bound. */
        private double max;

        /** The current interval lower bound. */
        private double min;

        /** The current iteration. */
        private int n;

        /**
         * Create a state object for the given root finding interval.
         * 
         * @param mn the lower bound of the search interval.
         * @param mx the upper bound of the search interval.
         */
        IterativeState(double mn, double mx) {
            super();
            min = mn;
            max = mx;
        }

        /**
         * Access the current iteration.
         * 
         * @return the current iteration.
         */
        public int getIterations() {
            return n;
        }

        /**
         * Access the current relative error in the evaluation.
         * 
         * @return the current relative error.
         */
        public double getRelativeError() {
            return Math.max(Math.abs(fm), m / min - 1.0);
        }

        /**
         * Initialize the state to begin finding a root.
         */
        public void initialize() {
            n = 0;
        }

        /**
         * Perform the next iteration of finding a root. The current state is
         * updated with the newly compuated root data.
         * 
         * @throws NumericException if the function could not be evaluated.
         */
        public void iterate() throws NumericException {
            ++n;
            m = min + (max - min) / 2.0;
            fmin = getFunction().evaluate(min);
            fm = getFunction().evaluate(m);

            if (fm * fmin > 0.0) {
                // max and m bracket the root.
                min = m;
                fmin = fm;
            } else {
                // min and m bracket the root.
                max = m;
            }
        }

        /**
         * Access the result of this root finding.
         * 
         * @return the root.
         * @throws NumericException if the function could not be evaluated.
         */
        double getResult() throws NumericException {
            iterate();
            return min + (max - min) / 2.0;
        }
    }

    /**
     * Create a root finder for the given function.
     * 
     * @param f the target function.
     */
    public BisectionRootFinder(Function f) {
        this(f, 100, 1.0e-15);
    }

    /**
     * Create a root finder for the given function.
     * 
     * @param f the target function.
     * @param iterations maximum number of iterations.
     * @param error maximum relative error.
     */
    public BisectionRootFinder(Function f, int iterations, double error) {
        super(iterations, error);
        setFunction(f);
    }

    /**
     * Find a root of the target function that lies in the interval [
     * <tt>min</tt>, <tt>max</tt>].
     * 
     * @param min the lower bound of the search interval.
     * @param max the upper bound of the search interval.
     * @return a root that lies between <tt>min</tt> and <tt>max</tt>,
     *         inclusive.
     * @throws NumericException if a root could not be found.
     */
    public double findRoot(double min, double max) throws NumericException {
        IterativeState state = new IterativeState(min, max);
        iterate(state);
        return state.getResult();
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
}

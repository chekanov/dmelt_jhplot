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
package jhplot.math.num.root;

import jhplot.math.num.Function;
import jhplot.math.num.IterativeMethod;
import jhplot.math.num.NumericException;

/**
 * <p>
 * Newton's method (1) for finding roots of functions.
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
 * along with its derivative:
 * 
 * <pre>
 * Function cos = new Function() {
 *    public double evaluate(double x) {
 *        return Math.cos(x);
 *    }}
 * };
 * </pre>
 * 
 * </p>
 * <p>
 * Then, a Newton's method root finder is created with the above function:
 * 
 * <pre>
 * NewtonRootFinder finder = new NewtonRootFinder(sine, cos);
 * </pre>
 * 
 * </p>
 * <p>
 * Lastly, locating roots is accomplished using the {@link #findRoot} method:
 * 
 * <pre>
 * // find the root close to 3.
 * double pi = finder.findRoot(3.0);
 * 
 * // find the root between close to 1.
 * double zero = finder.findRoot(1.0);
 * </pre>
 * 
 * </p>
 * <p>
 * References:
 * <ol>
 * <li> Eric W. Weisstein. "Newton's Method." From MathWorld--A Wolfram Web
 * Resource. <a target="_blank"
 * href="http://mathworld.wolfram.com/NewtonsMethod.html">
 * http://mathworld.wolfram.com/NewtonsMethod.html</a> </li>
 * </ol>
 * </p>
 * 
 * @since 1.1
 * @version $Revision: 1.3 $ $Date: 2007/10/27 04:57:42 $
 */
public class NewtonRootFinder extends IterativeMethod {

    /** the derivative of the target function. */
    private Function derivative;

    /** the target function. */
    private Function function;

    /**
     * The internal state used during root finding.
     */
    private class IterativeState implements IterativeMethod.IterativeState {

        /** The current function value for the interval lower bound. */
        private double dx;

        /** The current function value for the interval midpoint. */
        private double fx;

        /** The current iteration. */
        private int n;

        /** The current interval midpoint. */
        private double x;

        /**
         * Create a state object for the given initial root approximation.
         * 
         * @param t the initial root approximation.
         */
        IterativeState(double t) {
            super();
            this.x = t;
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
            return Math.max(Math.abs(fx), Math.abs(x / (x + fx / dx) - 1.0));
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

            fx = getFunction().evaluate(x);
            dx = getDerivative().evaluate(x);

            x = x - (fx / dx);
        }

        /**
         * Access the result of this root finding.
         * 
         * @return the root.
         */
        double getResult() {
            return x;
        }
    }

    /**
     * Create a root finder for the given function.
     * 
     * @param f the target function.
     * @param d the first derivative of <tt>f</tt>.
     */
    public NewtonRootFinder(Function f, Function d) {
        this(f, d, 100, 1.0e-15);
    }

    /**
     * Create a root finder for the given function.
     * 
     * @param f the target function.
     * @param d the first derivative of <tt>f</tt>.
     * @param iterations maximum number of iterations.
     * @param error maximum relative error.
     */
    public NewtonRootFinder(Function f, Function d, int iterations, double error) {
        super(iterations, error);
        setFunction(f);
        setDerivative(d);
    }

    /**
     * Find a root of the target function that lies close to <tt>x</tt>.
     * 
     * @param x the initial root approximation.
     * @return a root that lies close to <tt>x</tt>.
     * @throws NumericException if a root could not be found.
     */
    public double findRoot(double x) throws NumericException {
        IterativeState state = new IterativeState(x);
        iterate(state);
        return state.getResult();
    }

    /**
     * Access the derivative of the target function.
     * 
     * @return the target function derivative.
     */
    public Function getDerivative() {
        return derivative;
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
     * Modify the derivative of the target function.
     * 
     * @param f the new target function derivative.
     */
    public void setDerivative(Function f) {
        if (f == null) {
            throw new IllegalArgumentException("Derivative can not be null.");
        }
        this.derivative = f;
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

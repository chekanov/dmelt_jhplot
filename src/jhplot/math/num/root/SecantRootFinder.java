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

import jhplot.math.num.ConvergenceException;
import jhplot.math.num.Function;
import jhplot.math.num.IterativeMethod;
import jhplot.math.num.NumericException;

/**
 * <p>
 * The secant method (1) for finding roots of functions.
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
 * Then, a secant root finder is created with the above function:
 * 
 * <pre>
 * SecantRootFinder finder = new SecantRootFinder(sine);
 * </pre>
 * 
 * </p>
 * <p>
 * Lastly, locating roots is accomplished using the {@link #findRoot} method:
 * 
 * <pre>
 * // find the root around 3 and 4.
 * double pi = finder.findRoot(3.0, 4.0);
 * 
 * // find the root around 3.5 and 4.
 * pi = finder.findRoot(3.5, 4.0);
 * 
 * // find the root around -1 and 1.
 * double zero = finder.findRoot(-1.0, 1.0);
 * </pre>
 * 
 * </p>
 * <p>
 * References:
 * <ol>
 * <li> Eric W. Weisstein. "Secant Method." From MathWorld--A Wolfram Web
 * Resource. <a target="_blank"
 * href="http://mathworld.wolfram.com/SecantMethod.html">
 * http://mathworld.wolfram.com/SecantMethod.html</a> </li>
 * </ol>
 * </p>
 * 
 * @since 1.1
 * @version $Revision: 1.2 $ $Date: 2007/10/25 04:44:14 $
 */
public class SecantRootFinder extends IterativeMethod {

    /** the target function. */
    private Function function;

    /**
     * Create a root finder for the given function.
     * 
     * @param f the target function.
     */
    public SecantRootFinder(Function f) {
        this(f, 100, 1.0e-15);
    }

    /**
     * Create a root finder for the given function.
     * 
     * @param f the target function.
     * @param iterations maximum number of iterations.
     * @param error maximum relative error.
     */
    public SecantRootFinder(Function f, int iterations, double error) {
        super(iterations, error);
        setFunction(f);
    }

    /**
     * Find a root of the target function that lies around the two initial
     * approximations, <tt>x0</tt> and <tt>x1</tt>.
     * 
     * @param x0 an initial approximation to the root.
     * @param x1 another initial approximation to the root.
     * @return a root that lies close to <tt>x0</tt> and <tt>x1</tt>.
     * @throws NumericException if a root could not be found.
     */
    public double findRoot(double x0, double x1) throws NumericException {
        double f0 = function.evaluate(x0);
        double f1 = function.evaluate(x1);
        double f;
        int n = 0;
        double x;
        double delta;
        double error;

        do {
            delta = f1 * (x1 - x0) / (f1 - f0);
            x = x1 - delta;
            f = function.evaluate(x);
            error = Math.max(Math.abs(f), Math.abs(delta / x1));

            // update for next iteration
            ++n;
            x0 = x1;
            f0 = f1;
            x1 = x;
            f1 = f;
        } while (n < getMaximumIterations()
            && error > getMaximumRelativeError());

        if (n >= getMaximumIterations()) {
            throw new ConvergenceException("Secant method failed to converge.");
        }

        return x1;
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

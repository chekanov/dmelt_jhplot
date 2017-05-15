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
package jhplot.math.num.root;

import jhplot.math.num.ConvergenceException;
import jhplot.math.num.Function;
import jhplot.math.num.IterativeMethod;
import jhplot.math.num.NumericException;

/**
 * <p>
 * Brent's method (1) for finding roots of functions.
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
 * Then, a Brent's method root finder is created with the above function:
 * 
 * <pre>
 * BrentRootFinder finder = new BrentRootFinder(sine);
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
 * <li> Wikipedia contributors, "Brent's method," Wikipedia, The Free
 * Encyclopedia, <a target="_blank"
 * href="http://en.wikipedia.org/w/index.php?title=Brent%27s_method&oldid=132341223">http://en.wikipedia.org/w/index.php?title=Brent%27s_method&oldid=132341223</a>
 * </li>
 * </ol>
 * </p>
 * 
 * @since 1.1
 * @version $Revision: 1.2 $ $Date: 2007/11/18 23:51:23 $
 */
public class BrentRootFinder extends IterativeMethod {

    /** the target function. */
    private Function function;

    /**
     * Create a root finder for the given function.
     * 
     * @param f the target function.
     */
    public BrentRootFinder(Function f) {
        this(f, 100, 1.0e-15);
    }

    /**
     * Create a root finder for the given function.
     * 
     * @param f the target function.
     * @param iterations maximum number of iterations.
     * @param error maximum relative error.
     */
    public BrentRootFinder(Function f, int iterations, double error) {
        super(iterations, error);
        setFunction(f);
    }

    /**
     * Find a root of the target function that lies between the two initial
     * approximations, <tt>x0</tt> and <tt>x1</tt>.
     * 
     * @param min the lower bound of the search interval.
     * @param max the upper bound of the search interval.
     * @return a root that lies between <tt>min</tt> and <tt>max</tt>,
     *         inclusive.
     * @throws NumericException if a root could not be found.
     */
    public double findRoot(double min, double max) throws NumericException {
        double ret = Double.NaN;

        if (!Double.isNaN(min) && !Double.isNaN(max)) {
            double a = min;
            double b = max;
            double fa = function.evaluate(a);
            double fb = function.evaluate(b);

            if (Math.abs(fa) < Math.abs(fb)) {
                double t = a;
                a = b;
                b = t;

                t = fa;
                fa = fb;
                fb = t;
            }

            int n = 0;
            double c = a;
            double fc = fa;
            double d = a;
            boolean flag = true;
            double error;
            do {
                ++n;
                double s;
                if (fa != fc && fb != fc) {
                    s = ((a * fb * fc) / ((fa - fb) * (fa - fc)))
                        + ((b * fa * fc) / ((fb - fa) * (fb - fc)))
                        + ((c * fa * fb) / ((fc - fa) * (fc - fb)));
                } else {
                    s = b - fb * (b - a) / (fb - fa);
                }
                double x = (3.0 * a + b) / 4.0;
                double lb = Math.min(x, b);
                double ub = Math.max(x, b);
                if ((s < lb || s > ub)
                    || (flag && Math.abs(s - b) >= Math.abs(b - c) / 2.0)
                    || (!flag && Math.abs(s - b) >= Math.abs(c - d) / 2.0)) {
                    s = (a + b) / 2.0;
                    flag = true;
                } else {
                    flag = false;
                }
                double fs = getFunction().evaluate(s);
                d = c;
                c = b;
                fc = b;
                if (fa * fs < 0) {
                    b = s;
                    fb = fs;
                } else {
                    a = s;
                    fa = fs;
                }
                if (Math.abs(fa) < Math.abs(fb)) {
                    double t = a;
                    a = b;
                    b = t;

                    t = fa;
                    fa = fb;
                    fb = t;
                }

                error = Math.max(Math.abs(fb), Math.abs(b / c - 1.0));
            } while (n < getMaximumIterations()
                && error > getMaximumRelativeError());

            if (n >= getMaximumIterations()) {
                throw new ConvergenceException(
                    "Brent's method failed to converge.");
            }

            ret = b;
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

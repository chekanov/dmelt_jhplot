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
package jhplot.math.num.integration;

import jhplot.math.num.ConvergenceException;
import jhplot.math.num.Function;
import jhplot.math.num.IterativeMethod;
import jhplot.math.num.NumericException;

/**
 * <p>
 * An implementation of adaptive quadrature.
 * </p>
 * <p>
 * For example, to evaluate definite integrals for sine, first a
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
 * Then, an adaptive integrator is created with the above function:
 * 
 * <pre>
 * AdaptiveIntegrator integrator = new AdaptiveIntegrator(sine);
 * </pre>
 * 
 * </p>
 * <p>
 * Lastly, evaluating definite integrals is accomplished using the
 * {@link #integrate(double, double)} method:
 * 
 * <pre>
 * // integrate sine from 0 to Pi.
 * double two = integrator.integrate(0.0, Math.PI);
 * 
 * // integrate sine from Pi/2 to 2 Pi.
 * double one = integrator.integrate(Math.PI / 2.0, Math.PI);
 * </pre>
 * 
 * </p>
 * <p>
 * References:
 * <ol>
 * <li> "Adaptive Quadrature." <a target="_blank"
 * href="http://www.cse.uiuc.edu/eot/modules/integration/adaptivq/">
 * http://www.cse.uiuc.edu/eot/modules/integration/adaptivq/</a> </li>
 * </ol>
 * </p>
 * 
 * @version $Revision: 1.2 $ $Date: 2007/10/25 04:44:16 $
 * @since 1.1
 */
public class AdaptiveIntegrator extends IterativeMethod {

    /** the target function. */
    private Function function;

    /**
     * Create an integrator for the given function.
     * 
     * @param f the target function.
     */
    public AdaptiveIntegrator(Function f) {
        this(f, 100, 1.0e-10);
    }

    /**
     * Create an integrator for the given function.
     * 
     * @param f the target function.
     * @param iterations maximum number of iterations.
     * @param error maximum relative error.
     */
    public AdaptiveIntegrator(Function f, int iterations, double error) {
        super(iterations, error);
        setFunction(f);
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
     * Recursively integrate the target function from <tt>a</tt> to <tt>b</tt>
     * by subdividing that interval into smaller intervals.
     * 
     * @param a the lower limit of integration.
     * @param b the upper limit of integration.
     * @param fa the value of the function evaluated at <tt>a</tt>.
     * @param fb the value of the function evaluated at <tt>b</tt>.
     * @param fc the value of the function evaluated at the midpoint between
     *        <tt>a</tt> and <tt>b</tt>.
     * @param h the current interval size.
     * @param error the maximum relative error.
     * @param s the current integral value.
     * @param level the current level of interval division.
     * @return the definite integral from <tt>a</tt> to <tt>b</tt>.
     * @throws NumericException if the integral can not be evaluated.
     */
    private double integrate(double a, double b, double fa, double fb,
        double fc, double h, double error, double s, int level)
        throws NumericException {
        double ret = s;

        if (level < getMaximumIterations()) {
            double fd, fe, s1, s2;

            fd = function.evaluate(a + h / 2.0);
            fe = function.evaluate(a + 3.0 * h / 2.0);
            s1 = h * (fa + (4.0 * fd) + fc) / 6.0;
            s2 = h * (fc + (4.0 * fe) + fb) / 6.0;
            double sn = s1 + s2;
            if (Math.abs(sn / s - 1.0) <= error) {
                ret = sn;
            } else {
                double hn = h / 2.0;
                double e = error / 2;
                double pivot = a + h;
                int n = level + 1;
                ret = integrate(a, pivot, fa, fc, fd, hn, e, s1, n)
                    + integrate(pivot, b, fc, fb, fe, hn, e, s2, n);
            }
        } else {
            throw new ConvergenceException(
                "Adaptive quadrature failed to converge.");
        }

        return ret;
    }

    /**
     * Evaluate the definite integral from <tt>a</tt> to <tt>b</tt>.
     * 
     * @param a the lower limit of integration.
     * @param b the upper limit of integration.
     * @return the definite integral from <tt>a</tt> to <tt>b</tt>.
     * @throws NumericException if the integral can not be evaluated.
     */
    public double integrate(double a, double b) throws NumericException {
        double ret;

        if (Double.isNaN(a) || Double.isNaN(b)) {
            ret = Double.NaN;
        } else {
            double h = (b - a) / 2.0;
            double fa = function.evaluate(a);
            double fm = function.evaluate(a + h);
            double fb = function.evaluate(b);
            double s = h * (fa + 4.0 * fm + fb) / 3.0;
            ret = integrate(a, b, fa, fb, fm, h, getMaximumRelativeError(), s,
                1);
        }

        return ret;
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

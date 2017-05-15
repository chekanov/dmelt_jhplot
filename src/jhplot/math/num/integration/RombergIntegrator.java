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
import jhplot.math.num.DoubleArray;
import jhplot.math.num.Function;
import jhplot.math.num.IterativeMethod;
import jhplot.math.num.NumericException;
import jhplot.math.num.integration.TrapezoidalIntegrator;

/**
 * <p>
 * An implementation of Romberg Integration.
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
 * Then, a Romberg integrator is created with the above function:
 * 
 * <pre>
 * RombergIntegrator integrator = new RombergIntegrator(sine);
 * </pre>
 * 
 * </p>
 * <p>
 * Lastly, evaluating definite integrals is accomplished using the
 * {@link #integrate} method:
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
 * <li> Eric W. Weisstein. "Romberg Integration." From MathWorld--A Wolfram Web
 * Resource. <a target="_blank"
 * href="http://mathworld.wolfram.com/RombergIntegration.html">
 * http://mathworld.wolfram.com/RombergIntegration.html</a> </li>
 * </ol>
 * </p>
 * 
 * @version $Revision: 1.2 $ $Date: 2007/10/25 04:44:16 $
 * @since 1.1
 */
public class RombergIntegrator extends IterativeMethod {

    /** the target function. */
    private Function function;

    /**
     * Create an integrator for the given function.
     * 
     * @param f the target function.
     */
    public RombergIntegrator(Function f) {
        this(f, 100, 1.0e-10);
    }

    /**
     * Create an integrator for the given function.
     * 
     * @param f the target function.
     * @param iterations maximum number of iterations.
     * @param error maximum relative error.
     */
    public RombergIntegrator(Function f, int iterations, double error) {
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
     * Evaluate the definite integral from <tt>a</tt> to <tt>b</tt>.
     * 
     * @param a the lower limit of integration.
     * @param b the upper limit of integration.
     * @return the definite integral from <tt>a</tt> to <tt>b</tt>.
     * @throws NumericException if the integral can not be evaluated.
     */
    public double integrate(double a, double b) throws NumericException {
        TrapezoidalIntegrator.IterativeState state = new TrapezoidalIntegrator.IterativeState(
            function, a, b);

        DoubleArray r0 = new DoubleArray();
        DoubleArray r1 = new DoubleArray();
        double error = Double.MAX_VALUE;
        int n;

        r0.add(state.getResult());
        do {
            state.iterate();
            n = state.getIterations();

            r1.clear();
            r1.add(state.getResult());
            double d = 4.0;
            for (int i = 0; i < n; ++i) {
                r1.add(r1.get(i) + (r1.get(i) - r0.get(i)) / (d - 1.0));
                d *= 4.0;
            }
            error = Math.abs(r1.get(n) / r0.get(n - 1) - 1.0);
            r0 = r1;
            r1 = new DoubleArray();
        } while (n < getMaximumIterations()
            && error > getMaximumRelativeError());

        if (n >= getMaximumIterations()) {
            throw new ConvergenceException(
                "Romberg integration failed to converge.");
        }

        return r0.get(r0.getSize() - 1);
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

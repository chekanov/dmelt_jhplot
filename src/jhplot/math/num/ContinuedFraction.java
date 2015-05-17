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
package jhplot.math.num;


/**
 * <p>
 * This class provides the means to evaluate continued fractions (1). To create
 * a continued fraction, authors subclass this class and provided concrete a and
 * b coefficient methods.
 * </p>
 * <p>
 * For example, this is the continued fraction for the exponential function
 * defined by (2):
 * 
 * <pre>
 * ContinuedFraction exponential = new ContinuedFraction() {
 *     public double getA(int n, double x) {
 *         if (n == 0) {
 *             return 1.0;
 *         } else if (n % 2 == 0) { // even
 *             return 2.0;
 *         } else { // odd
 *             return n;
 *         }
 *     }
 *    
 *     public double getB(int n, double x) {
 *         if (n % 2 == 0) { // even
 *             return x;
 *         } else { // odd
 *             return -x;
 *         }
 * }
 * </pre>
 * 
 * </p>
 * <p>
 * References:
 * <ol>
 * <li> Eric W. Weisstein. "Continued Fraction." From MathWorld--A Wolfram Web
 * Resource. <a target="_blank"
 * href="http://mathworld.wolfram.com/ContinuedFraction.html">
 * http://mathworld.wolfram.com/ContinuedFraction.html</a> </li>
 * <li> Exponential Function: Continued Fraction Representation. <a
 * target="_blank" href="http://functions.wolfram.com/01.03.10.0001.01">
 * http://functions.wolfram.com/01.03.10.0001.01</a> </li>
 * </ol>
 * </p>
 * 
 * @version $Revision: 1.2 $ $Date: 2007/10/25 04:44:21 $
 */
public abstract class ContinuedFraction extends IterativeMethod {

    /**
     * The internal state used during continued fraction evaluation.
     */
    class IterativeState implements IterativeMethod.IterativeState {

        /** A very small number used as a replacement for zero. */
        private static final double TINY = 1.0e-100;

        /** The numerator of the current convergent. */
        private double c;

        /** The denominator of the current convergent. */
        private double d;

        /** The current change in consecutive convergents. */
        private double delta;

        /** The current convergent. */
        private double f;

        /** The current iteration. */
        private int n;

        /** The evaluation point. */
        private double x;

        /**
         * Create a state object for the given evaluation point.
         * 
         * @param t the point of evaluation.
         */
        IterativeState(double t) {
            super();
            x = t;
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
            return delta - 1.0;
        }

        /**
         * Initialize the state to begin a continued fraction evaluation.
         */
        public void initialize() {
            f = zeroToTiny(getA(0, x));
            c = f;
            d = 0.0;
            n = 0;
        }

        /**
         * <p>
         * Perform the next iteration of the continued fraction evaluation. The
         * current state is updated with the newly compuated convergent data.
         * This method is based on the modified Lentz's method (1).
         * </p>
         * <p>
         * References:
         * <ol>
         * <li>Lentz, W.J. 1976, Applied Optics, vol. 15, pp. 668-671.</li>
         * </ol>
         * </p>
         */
        public void iterate() {
            ++n;
            double a = getA(n, x);
            double b = getB(n, x);
            d = zeroToTiny(a + b * d);
            c = zeroToTiny(a + b / c);
            d = 1.0 / d;
            delta = c * d;
            f = f * delta;
        }

        /**
         * Access the result of this evaluation.
         * 
         * @return the continued fraction evaluated at <tt>x</tt>
         */
        double getResult() {
            iterate();
            return f;
        }

        /**
         * Utility method to shift zeros by a small amount (1E-100).
         * 
         * @param t the value to shift.
         * @return if <tt>t</tt> is zero, 1E-100. Otherwise, <tt>t</tt>
         */
        private double zeroToTiny(double t) {
            double ret = t;
            if (t == 0) {
                ret = TINY;
            }
            return ret;
        }
    }

    /**
     * Default constructor.
     */
    protected ContinuedFraction() {
        this(100, 1.0e-15);
    }

    /**
     * Create a continued fraction with the given number of maximum iterations
     * and maximum relative error.
     * 
     * @param iterations maximum number of iterations.
     * @param error maximum relative error.
     */
    protected ContinuedFraction(int iterations, double error) {
        super(iterations, error);
    }

    /**
     * Evaluate this continued fraction at the given value.
     * 
     * @param x the point of evalutation.
     * @return the value of this continued fraction evaluated at <tt>x</tt>.
     * @throws NumericException if the continued fraction could not be
     *         evaluated.
     */
    public double evaluate(double x) throws NumericException {
        IterativeState state = new IterativeState(x);
        iterate(state);
        return state.getResult();
    }

    /**
     * Access the <tt>n</tt>-th a coefficient.
     * 
     * @param n the coefficient index.
     * @param x the continued fraction evaluation point.
     * @return the <tt>n</tt>-th a coefficient.
     */
    protected abstract double getA(int n, double x);

    /**
     * Access the <tt>n</tt>-th b coefficient.
     * 
     * @param n the coefficient index.
     * @param x the continued fraction evaluation point.
     * @return the <tt>n</tt>-th b coefficient.
     */
    protected abstract double getB(int n, double x);
}

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
package jhplot.math.num;


/**
 * <p>
 * This class provides the means to evaluate infinite series (1). To create a
 * series, authors subclass this class and provided a concrete term method. Of
 * note, when evaluating a series, the term indicies are the nonnegative
 * integers. That is to say, the first term is at index zero and each subsequent
 * term increases the index by one. It is the responsibility of the author to
 * shift term indices as needed if a series does not start at zero or if the
 * indices are not unit increments.
 * </p>
 * <p>
 * For example, this is the series for the exponential function defined by (2):
 * 
 * <pre>
 * Series exponential = new Series() {
 *     public double getTerm(int n, double x) {
 *         return Math.pow(x, n) / factorial(n);
 *     }
 * 
 *     private double factorial(int n) {
 *         double p = 1.0;
 *         while(n &gt; 1.0) {
 *             p *= n--;
 *         }
 *         return p;
 *     }
 * }
 * </pre>
 * 
 * </p>
 * <p>
 * References:
 * <ol>
 * <li> Eric W. Weisstein. "Series." From MathWorld--A Wolfram Web Resource. <a
 * target="_blank" href="http://mathworld.wolfram.com/Series.html">
 * http://mathworld.wolfram.com/Series.html</a> </li>
 * <li> Exponential Function: Series Representation. <a target="_blank"
 * href="http://functions.wolfram.com/01.03.06.0002.01">
 * http://functions.wolfram.com/01.03.06.0002.01</a> </li>
 * </ol>
 * </p>
 * 
 * @version $Revision: 1.2 $ $Date: 2007/10/25 04:44:14 $
 */
public abstract class Series extends IterativeMethod {

    /**
     * The internal state used during series evaluation.
     */
    class IterativeState implements IterativeMethod.IterativeState {

        /** The current partial sum. */
        private double sum;

        /** The current term. */
        private double term;

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
            return sum / (sum - term) - 1.0;
        }

        /**
         * Initialize the state to begin a series evaluation.
         */
        public void initialize() {
            sum = 0.0;
            n = getFirstIndex();
        }

        /**
         * Perform the next iteration of the series evaluation. The current
         * state is updated with the newly compuated partial sum and term data.
         */
        public void iterate() {
            term = getTerm(n, x);
            ++n;
            sum += term;
        }

        /**
         * Access the result of this evaluation.
         * 
         * @return the series evaluated at <tt>x</tt>
         */
        double getResult() {
            iterate();
            return sum;
        }
    }

    /** Index of the first term in this series. */
    private int firstIndex;

    /**
     * Default constructor.
     */
    protected Series() {
        this(100, 1.0e-15);
    }

    /**
     * Create a series with the given number of maximum iterations and maximum
     * relative error.
     * 
     * @param iterations maximum number of iterations.
     * @param error maximum relative error.
     */
    protected Series(int iterations, double error) {
        this(0, iterations, error);
    }

    /**
     * Create a series with the given first term index, number of maximum
     * iterations and maximum relative error.
     * 
     * @param index index of first term in this series.
     * @param iterations maximum number of iterations.
     * @param error maximum relative error.
     */
    protected Series(int index, int iterations, double error) {
        super(iterations, error);
        setFirstIndex(index);
    }

    /**
     * Evaluate this series at the given value.
     * 
     * @param x the point of evalutation.
     * @return the value of this series evaluated at <tt>x</tt>.
     * @throws NumericException if the series could not be evaluated.
     */
    public double evaluate(double x) throws NumericException {
        IterativeState state = new IterativeState(x);
        iterate(state);
        return state.getResult();
    }

    /**
     * Access the <tt>n</tt>-th term for this series.
     * 
     * @param n the term index.
     * @param x the series evaluation point.
     * @return the <tt>n</tt>-th series term.
     */
    protected abstract double getTerm(int n, double x);

    /**
     * Access this first term index.
     * 
     * @return the first term index.
     */
    private int getFirstIndex() {
        return firstIndex;
    }

    /**
     * Modify the first term index.
     * 
     * @param index The new first term index.
     */
    private void setFirstIndex(int index) {
        this.firstIndex = index;
    }
}

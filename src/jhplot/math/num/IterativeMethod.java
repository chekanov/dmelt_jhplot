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
 * A method to solve generic problems by finding successive (and hopefully
 * better) approximations to the solution.
 * 
 * @version $Revision: 1.2 $ $Date: 2007/10/25 04:44:23 $
 */
public abstract class IterativeMethod {

    /** The maximum number of iteration to perform. */
    private int maximumIterations;

    /** The maximum allowable relative error. */
    private double maximumRelativeError;

    /**
     * The current state for an iterative method.
     */
    public static interface IterativeState {

        /**
         * Access the current iteration.
         * 
         * @return the current iteration.
         */
        int getIterations();

        /**
         * Access the current relative error in the evaluation.
         * 
         * @return the current relative error.
         */
        double getRelativeError();

        /**
         * Initialize the state to begin an iterative evaluation.
         */
        void initialize();

        /**
         * Perform the next iteration of the iterative evaluation. The current
         * state is updated with the newly computed data.
         * 
         * @throws NumericException if the iteration can not be performed.
         */
        void iterate() throws NumericException;
    }

    /**
     * Default constructor. Maximum iterations is set to 100 and maximum
     * relative error is set to 1&#215;10<sup>-10</sup>.
     */
    protected IterativeMethod() {
        this(100, 1.0e-10);
    }

    /**
     * Create an iterative method with the given number of maximum iterations
     * and maximum relative error.
     * 
     * @param iterations maximum number of iterations.
     * @param error maximum relative error.
     */
    protected IterativeMethod(int iterations, double error) {
        super();
        setMaximumIterations(iterations);
        setMaximumRelativeError(error);
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
     * Access the maximum relative error.
     * 
     * @return the maximum relative error.
     */
    public double getMaximumRelativeError() {
        return maximumRelativeError;
    }

    /**
     * Perform the iterative evaluation. <tt>state</tt> is used to manage
     * state between iterations.
     * 
     * @param state the state for this iterative method evaluation.
     * @throws NumericException if a solution to the iterative method can not be
     *         computed.
     */
    public void iterate(IterativeState state) throws NumericException {

        state.initialize();
        do {
            state.iterate();
        } while (state.getIterations() < getMaximumIterations()
            && Math.abs(state.getRelativeError()) > getMaximumRelativeError());

        if (state.getIterations() >= getMaximumIterations()) {
            throw new ConvergenceException(
                "Iterative method failed to converge.");
        }
    }

    /**
     * Modify the maximum number of iterations.
     * 
     * @param iterations the new maximum number of iterations value.
     */
    public void setMaximumIterations(int iterations) {
        if (iterations <= 0) {
            throw new IllegalArgumentException(
                "Maximum iterations must be positive.");
        }
        this.maximumIterations = iterations;
    }

    /**
     * Modify the maximum relative error.
     * 
     * @param error the new maximum relative error.
     */
    public void setMaximumRelativeError(double error) {
        if (error <= 0.0 || Double.isNaN(error)) {
            throw new IllegalArgumentException(
                "Maximum relative error must be positive.");
        }
        this.maximumRelativeError = error;
    }
}

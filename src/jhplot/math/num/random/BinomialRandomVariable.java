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

package jhplot.math.num.random;

/*
 * <p>
 * A random variable generator for the Binomial distribution.
 * <p> </b>Attention:</b> This generator has a problem related to the
 * precision when "p<0.5". Please use instead: 
 * cern.jet.random.Binomial <br> or
 * umontreal.iro.lecuyer.randvar.BinomialGen <br> or
 * org.apache.commons.math.distribution.BinomialDistributionImpl <br> 
 * </p>
 * <p>
 * References:
 * <ol>
 * <li>Wikipedia contributors, "Binomial Distribution," Wikipedia, The Free
 * Encyclopedia, <a target="_blank"
 * href="http://en.wikipedia.org/wiki/Binomial_distribution">
 * http://en.wikipedia.org/wiki/Binomial_distribution</a></li>
 * </ol>
 * </p>
 * 
 * @since 1.3
 * @version $Revision: 1.2 $ $Date: 2007/11/18 23:51:19 $
 */
public class BinomialRandomVariable extends AbstractDiscreteRandomVariable {

    /** the number of trials. */
    private int numberOfTrials;

    /** the probability of success. */
    private double probabilityOfSuccess;

    /**
     * Default constructor. Number of trials is set to one and probability of
     * success is set to 0.5.
     */
    public BinomialRandomVariable() {
        this(1, 0.5);
    }

    /**
     * Create a random variable with the given number of trials and probability
     * of success. This generator has a problem with p less than 0.5. Use alternative packages as
     * shown above. 
     * 
     * @param n the number of trials.
     * @param p the probability of success.
     */
    public BinomialRandomVariable(int n, double p) {
        this(n, p, new RandomRNG());
    }

    /**
     * Create a random variable with the given parameters.
     * 
     * @param n the number of trials.
     * @param p the probability of success.
     * @param source the source generator.
     */
    public BinomialRandomVariable(int n, double p, RNG source) {
        super(source);
        setNumberOfTrials(n);
        setProbabilityOfSuccess(p);
    }

    /**
     * Access the next random variable using the given generator.
     *  
     * @param n the number of trials.
     * @param p the probability of success.
     * @param source the source generator.
     * @return the next random variable.
     */
    public static int nextRandomVariable(int n, double p, RNG source) {
        int x = 0;
        int pivot = (int) (n * p);
        do {
            int i = (int) (1.0 + n * p);
            double v = BetaRandomVariable.nextRandomVariable(i, n + 1.0 - i,
                source);
            if (p < v) {
                p = p / v;
                n = i - 1;
            } else {
                x = x + i;
                p = (p - v) / (1.0 - v);
                n = n - i;
            }
        } while (n > pivot);
        for (int i = 0; i < pivot; ++i) {
            double u = source.nextRandomNumber();
            if (u < p) {
                ++x;
            }
        }
        return x;
    }

    /**
     * Access the number of trials.
     * 
     * @return the number of trials.
     */
    private int getNumberOfTrials() {
        return numberOfTrials;
    }

    /**
     * Access the probability of success parameter.
     * 
     * @return the probability of success parameter.
     */
    private double getProbabilityOfSuccess() {
        return probabilityOfSuccess;
    }

    /**
     * Access the next random variable from this generator.
     * 
     * @return the next random variable.
     */
    public int nextRandomVariable() {
        return nextRandomVariable(getNumberOfTrials(),
            getProbabilityOfSuccess(), getSource());
    }

    /**
     * Modify the number of trials.
     * 
     * @param n the new number of trials.
     */
    private void setNumberOfTrials(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException(
                "number of trials must be positive.");
        }
        numberOfTrials = n;
    }

    /**
     * Modify the probability of success parameter.
     * 
     * @param p the new probability of success parameter.
     */
    private void setProbabilityOfSuccess(double p) {
        if (Double.isNaN(p) || p <= 0.0 || p >= 1.0) {
            throw new IllegalArgumentException("probability of success must"
                + "be between 0.0 and 1.0, exclusive.");
        }
        this.probabilityOfSuccess = p;
    }
}

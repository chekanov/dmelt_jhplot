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

/**
 * Base continuous random variable that provides a
 * {@link RNG) instance as a source for uniform random numbers.
 * 
 * @since 1.3
 * @version $Revision: 1.3 $ $Date: 2007/11/18 23:51:19 $
 */
public abstract class AbstractContinuousRandomVariable implements
    ContinuousRandomVariable {

    /** the source random number generator. */
    private RNG source;

    /**
     * Create a random variable with the given source generator.
     * 
     * @param rng the source generator.
     */
    protected AbstractContinuousRandomVariable(RNG rng) {
        super();
        setSource(rng);
    }

    /**
     * Access the source generator.
     * 
     * @return the source generator.
     */
    protected RNG getSource() {
        return source;
    }

    /**
     * Access the next random number from the source generator.
     * 
     * @return the next random number.
     */
    protected double nextRandomNumber() {
        return getSource().nextRandomNumber();
    }

    /**
     * Modify the source generator.
     * 
     * @param delegate the new source generator.
     */
    private void setSource(RNG delegate) {
        if (delegate == null) {
            throw new IllegalArgumentException("source can not be null.");
        }
        this.source = delegate;
    }
}

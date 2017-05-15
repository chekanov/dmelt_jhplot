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
 * <p>
 * A general linear congruential generator.
 * </p>
 * <p>
 * References:
 * <ol>
 * <li>Wikipedia contributors, "Linear congruential generator," Wikipedia, The
 * Free Encyclopedia, <a target="_blank"
 * href="http://en.wikipedia.org/wiki/Linear_congruential_generator">
 * http://en.wikipedia.org/wiki/Linear_congruential_generator</a></li>
 * </ol>
 * </p>
 * 
 * @since 1.3
 * @version $Revision: 1.3 $ $Date: 2007/11/18 23:51:19 $
 */
public class LinearCongruentialRNG implements RNG {

    /** the modulus value. */
    private long modulus;

    /** the multiplier value. */
    private long multiplier;

    /** the seed value. */
    private long seed;

    /** the shift, or increment, value. */
    private long shift;

    /**
     * Create a linear congruential generator with the given modulus and
     * multiplier. The shift is set to zero.
     * 
     * @param m the modulus.
     * @param a the multiplier.
     */
    public LinearCongruentialRNG(long m, long a) {
        this(m, a, 0L);
    }

    /**
     * Create a linear congruential generator with the given parameters.
     * 
     * @param m the modulus.
     * @param a the multiplier.
     * @param c the shift.
     */
    public LinearCongruentialRNG(long m, long a, long c) {
        this(m, a, c, System.currentTimeMillis());
    }

    /**
     * Create a linear congruential generator with the given parameters.
     * 
     * @param m the modulus.
     * @param a the multiplier.
     * @param c the shift.
     * @param s the seed.
     */
    public LinearCongruentialRNG(long m, long a, long c, long s) {
        super();
        setModulus(m);
        setMultiplier(a);
        setShift(c);
        setSeed(s);
    }

    /**
     * Access the modulus value.
     * 
     * @return the modulus.
     */
    private long getModulus() {
        return modulus;
    }

    /**
     * Access the multiplier value.
     * 
     * @return the multiplier.
     */
    private long getMultiplier() {
        return multiplier;
    }

    /**
     * Access the seed value.
     * 
     * @return the seed.
     */
    private long getSeed() {
        return seed;
    }

    /**
     * Access the shift value.
     * 
     * @return the shift.
     */
    private long getShift() {
        return shift;
    }

    /**
     * Access the next random number from this generator.
     * 
     * @return the next random number.
     */
    public double nextRandomNumber() {
        long m = getModulus();
        long r = (getMultiplier() * getSeed() + getShift()) % m;
        seed = r;
        return (double) r / (double) m;
    }

    /**
     * Modify the modulus value.
     * 
     * @param value the new modulus value.
     */
    private void setModulus(long value) {
        if (value <= 0) {
            throw new IllegalArgumentException("modulus must be positive.");
        }
        this.modulus = value;
    }

    /**
     * Modify the multiplier value.
     * 
     * @param value the new multiplier value.
     */
    private void setMultiplier(long value) {
        if (value <= 0) {
            throw new IllegalArgumentException("multiplier must be positive.");
        }
        this.multiplier = value;
    }

    /**
     * Modify the seed value.
     * 
     * @param value the new seed value.
     */
    private void setSeed(long value) {
        if (value <= 0) {
            throw new IllegalArgumentException("seed must be positive.");
        }
        this.seed = value;
    }

    /**
     * Modify the shift value.
     * 
     * @param value the new shift value.
     */
    private void setShift(long value) {
        if (value < 0) {
            throw new IllegalArgumentException("shift must be non-negative.");
        }
        this.shift = value;
    }

}

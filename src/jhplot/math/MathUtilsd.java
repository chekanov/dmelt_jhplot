package jhplot.math;

/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/

import java.util.Random;

/**
 * Utility and fast math functions.
 * <p>
 * Thanks to Riven on JavaGaming.org for the basis of sin/cos/atan2/floor/ceil.
 * Typical accurracy is E-05, but it can be changed.  
 * @author Nathan Sweet
 */
public final class MathUtilsd {
    static public final double nanoToSec = 1 / 1000000000;

    // ---
    static public final double FLOAT_ROUNDING_ERROR = 0.000001; // 32 bits
    static public final double PI = 3.1415927;
    static public final double PI2 = PI * 2;

    static public final double E = 2.7182818;

    static private final int SIN_BITS = 14; // 16KB. Adjust for accuracy.
    static private final int SIN_MASK = ~(-1 << SIN_BITS);
    static private final int SIN_COUNT = SIN_MASK + 1;

    static private final int ACOS_RESOLUTION = 50;
    static private final int ACOS_COUNT = 360 * ACOS_RESOLUTION;
    static private final int ACOS_COUNT_1 = ACOS_COUNT - 1;;

    static private final double radFull = PI * 2;
    static private final double degFull = 360;
    static private final double radToIndex = SIN_COUNT / radFull;
    static private final double degToIndex = SIN_COUNT / degFull;

    /** multiply by this to convert from radians to degrees */
    static public final double radiansToDegrees = 180 / PI;
    static public final double radDeg = radiansToDegrees;
    /** multiply by this to convert from degrees to radians */
    static public final double degreesToRadians = PI / 180;
    static public final double degRad = degreesToRadians;

    static private class Sin {
        static final double[] table = new double[SIN_COUNT];
        static {
            for (int i = 0; i < SIN_COUNT; i++)
                table[i] = Math.sin((i + 0.5f) / SIN_COUNT * radFull);
            for (int i = 0; i < 360; i += 90)
                table[(int) (i * degToIndex) & SIN_MASK] = Math.sin(i * degreesToRadians);
        }
    }

    static private class Acos {
        static final double[] table = new double[ACOS_COUNT];
        static {
            for (int i = 0; i < ACOS_COUNT; i++)
                table[i] = Math.acos((double) i * 2d / (ACOS_COUNT_1) - 1d);
        }
    }

    static public void initialize() {
        double i = Sin.table[0];
        i = Acos.table[0];
    }

    /** Returns the sine in radians from a lookup table. */
    static public final double sin(double radians) {
        return Sin.table[(int) (radians * radToIndex) & SIN_MASK];
    }

    /** Returns the cosine in radians from a lookup table. */
    static public final double cos(double radians) {
        return Sin.table[(int) ((radians + PI / 2) * radToIndex) & SIN_MASK];
    }

    /** Returns the arc cosine in radians from a lookup table. */
    static public final double acos(double x) {
        return Acos.table[(int) (((x + 1) / 2) * (ACOS_COUNT_1))];
    }

    /**
     * Arc cos approximation
     * 
     * @return
     */
    static public final double acos_v1(double x) {
        return (-0.69813170079773212 * x * x - 0.87266462599716477) * x + 1.5707963267948966;
    }

    /** Returns the sine in radians from a lookup table. */
    static public final double sinDeg(double degrees) {
        return Sin.table[(int) (degrees * degToIndex) & SIN_MASK];
    }

    /** Returns the cosine in radians from a lookup table. */
    static public final double cosDeg(double degrees) {
        return Sin.table[(int) ((degrees + 90) * degToIndex) & SIN_MASK];
    }

    // ---

    static private final int ATAN2_BITS = 7; // Adjust for accuracy.
    static private final int ATAN2_BITS2 = ATAN2_BITS << 1;
    static private final int ATAN2_MASK = ~(-1 << ATAN2_BITS2);
    static private final int ATAN2_COUNT = ATAN2_MASK + 1;
    static final int ATAN2_DIM = (int) Math.sqrt(ATAN2_COUNT);
    static private final double INV_ATAN2_DIM_MINUS_1 = 1.0f / (ATAN2_DIM - 1);

    static private class Atan2 {
        static final double[] table = new double[ATAN2_COUNT];
        static {
            for (int i = 0; i < ATAN2_DIM; i++) {
                for (int j = 0; j < ATAN2_DIM; j++) {
                    double x0 = i / ATAN2_DIM;
                    double y0 = j / ATAN2_DIM;
                    table[j * ATAN2_DIM + i] = Math.atan2(y0, x0);
                }
            }
        }
    }

    /** Returns atan2 in radians from a lookup table. */
    static public final double atan2(double y, double x) {
        double add, mul;
        if (x < 0) {
            if (y < 0) {
                y = -y;
                mul = 1;
            } else
                mul = -1;
            x = -x;
            add = -PI;
        } else {
            if (y < 0) {
                y = -y;
                mul = -1;
            } else
                mul = 1;
            add = 0;
        }
        double invDiv = 1 / ((x < y ? y : x) * INV_ATAN2_DIM_MINUS_1);

        if (invDiv == Float.POSITIVE_INFINITY)
            return (Math.atan2(y, x) + add) * mul;

        int xi = (int) (x * invDiv);
        int yi = (int) (y * invDiv);
        return (Atan2.table[yi * ATAN2_DIM + xi] + add) * mul;
    }

    // ---


    // ---

    /**
     * Returns the next power of two. Returns the specified value if the value
     * is already a power of two.
     */
    static public int nextPowerOfTwo(int value) {
        if (value == 0)
            return 1;
        value--;
        value |= value >> 1;
        value |= value >> 2;
        value |= value >> 4;
        value |= value >> 8;
        value |= value >> 16;
        return value + 1;
    }

    static public boolean isPowerOfTwo(int value) {
        return value != 0 && (value & value - 1) == 0;
    }

    // ---

    static public int clamp(int value, int min, int max) {
        if (value < min)
            return min;
        if (value > max)
            return max;
        return value;
    }

    static public short clamp(short value, short min, short max) {
        if (value < min)
            return min;
        if (value > max)
            return max;
        return value;
    }

    static public float clamp(float value, float min, float max) {
        if (value < min)
            return min;
        if (value > max)
            return max;
        return value;
    }

    static public double clamp(double value, double min, double max) {
        if (value < min)
            return min;
        if (value > max)
            return max;
        return value;
    }

    // ---

    static private final int BIG_ENOUGH_INT = 16 * 1024;
    static private final double BIG_ENOUGH_FLOOR = BIG_ENOUGH_INT;
    static private final double CEIL = 0.9999999;
    // static private final double BIG_ENOUGH_CEIL = NumberUtils
    // .longBitsToDouble(NumberUtils.doubleToLongBits(BIG_ENOUGH_INT + 1) - 1);
    static private final double BIG_ENOUGH_CEIL = 16384.999999999996;
    static private final double BIG_ENOUGH_ROUND = BIG_ENOUGH_INT + 0.5f;

    /**
     * Returns the largest integer less than or equal to the specified double.
     * This method will only properly floor doubles from -(2^14) to
     * (Float.MAX_VALUE - 2^14).
     */
    static public int floor(double x) {
        return (int) (x + BIG_ENOUGH_FLOOR) - BIG_ENOUGH_INT;
    }

    /**
     * Returns the largest integer less than or equal to the specified double.
     * This method will only properly floor doubles that are positive. Note this
     * method simply casts the double to int.
     */
    static public int floorPositive(double x) {
        return (int) x;
    }

    /**
     * Returns the smallest integer greater than or equal to the specified
     * double. This method will only properly ceil doubles from -(2^14) to
     * (Float.MAX_VALUE - 2^14).
     */
    static public int ceil(double x) {
        return (int) (x + BIG_ENOUGH_CEIL) - BIG_ENOUGH_INT;
    }

    /**
     * Returns the smallest integer greater than or equal to the specified
     * double. This method will only properly ceil doubles that are positive.
     */
    static public int ceilPositive(double x) {
        return (int) (x + CEIL);
    }

    /**
     * Returns the closest integer to the specified double. This method will
     * only properly round doubles from -(2^14) to (Float.MAX_VALUE - 2^14).
     */
    static public int round(double x) {
        return (int) (x + BIG_ENOUGH_ROUND) - BIG_ENOUGH_INT;
    }

    /**
     * Returns the closest integer to the specified double. This method will
     * only properly round doubles that are positive.
     */
    static public int roundPositive(double x) {
        return (int) (x + 0.5f);
    }

    /**
     * Returns true if the value is zero (using the default tolerance as upper
     * bound)
     */
    static public boolean isZero(double value) {
        return Math.abs(value) <= FLOAT_ROUNDING_ERROR;
    }

    /**
     * Returns true if the value is zero.
     * 
     * @param tolerance
     *            represent an upper bound below which the value is considered
     *            zero.
     */
    static public boolean isZero(double value, double tolerance) {
        return Math.abs(value) <= tolerance;
    }

    /**
     * Returns true if a is nearly equal to b. The function uses the default
     * doubleing error tolerance.
     * 
     * @param a
     *            the first value.
     * @param b
     *            the second value.
     */
    static public boolean isEqual(double a, double b) {
        return Math.abs(a - b) <= FLOAT_ROUNDING_ERROR;
    }

    /**
     * Returns true if a is nearly equal to b.
     * 
     * @param a
     *            the first value.
     * @param b
     *            the second value.
     * @param tolerance
     *            represent an upper bound below which the two values are
     *            considered equal.
     */
    static public boolean isEqual(double a, double b, double tolerance) {
        return Math.abs(a - b) <= tolerance;
    }

    /**
     * Fast sqrt method. Default passes it through one round of Newton's method.
     * 
     * @param value
     * @return
     */
    static public double sqrt(double value) {
        double sqrt = Double.longBitsToDouble(((Double.doubleToLongBits(value) - (1l << 52)) >> 1) + (1l << 61));
        return (sqrt + value / sqrt) / 2.0;
    }

    /* not quite rint(), i.e. results not properly rounded to nearest-or-even */
    static public double rint_v2(double x) {
        double t = floor(Math.abs(x) + 0.5);
        return (x < 0.0) ? -t : t;
    }

    /*
     * minimax approximation to cos on [-pi/4, pi/4] with rel. err. ~= 7.5e-13
     */
    static public double cos_core(double x) {
        double x8, x4, x2;
        x2 = x * x;
        x4 = x2 * x2;
        x8 = x4 * x4;
        /* evaluate polynomial using Estrin's scheme */
        return (-2.7236370439787708e-7 * x2 + 2.4799852696610628e-5) * x8 + (-1.3888885054799695e-3 * x2 + 4.1666666636943683e-2) * x4 + (-4.9999999999963024e-1 * x2 + 1.0000000000000000e+0);
    }

    /*
     * minimax approximation to sin on [-pi/4, pi/4] with rel. err. ~= 5.5e-12
     */
    static public double sin_core(double x) {
        double x4, x2;
        x2 = x * x;
        x4 = x2 * x2;
        /* evaluate polynomial using a mix of Estrin's and Horner's scheme */
        return ((2.7181216275479732e-6 * x2 - 1.9839312269456257e-4) * x4 + (8.3333293048425631e-3 * x2 - 1.6666666640797048e-1)) * x2 * x + x;
    }

    /*
     * minimax approximation to arcsin on [0, 0.5625] with rel. err. ~= 1.5e-11
     */
    static public double asin_core(double x) {
        double x8, x4, x2;
        x2 = x * x;
        x4 = x2 * x2;
        x8 = x4 * x4;
        /* evaluate polynomial using a mix of Estrin's and Horner's scheme */
        return (((4.5334220547132049e-2 * x2 - 1.1226216762576600e-2) * x4 + (2.6334281471361822e-2 * x2 + 2.0596336163223834e-2)) * x8 + (3.0582043602875735e-2 * x2 + 4.4630538556294605e-2) * x4 + (7.5000364034134126e-2 * x2 + 1.6666666300567365e-1)) * x2 * x + x;
    }

    /* relative error < 7e-12 on [-50000, 50000] */
    static public double sin_v2(double x) {
        double q, t;
        int quadrant;
        /* Cody-Waite style argument reduction */
        q = rint_v2(x * 6.3661977236758138e-1);
        quadrant = (int) q;
        t = x - q * 1.5707963267923333e+00;
        t = t - q * 2.5633441515945189e-12;
        if ((quadrant & 1) != 0) {
            t = cos_core(t);
        } else {
            t = sin_core(t);
        }
        return ((quadrant & 2) != 0) ? -t : t;
    }

    /* relative error < 2e-11 on [-1, 1] */
    static public double acos_v2(double x) {
        double xa, t;
        xa = Math.abs(x);
        /*
         * arcsin(x) = pi/2 - 2 * arcsin (sqrt ((1-x) / 2)) arccos(x) = pi/2 -
         * arcsin(x) arccos(x) = 2 * arcsin (sqrt ((1-x) / 2))
         */
        if (xa > 0.5625) {
            t = 2.0 * asin_core(sqrt(0.5 * (1.0 - xa)));
        } else {
            t = 1.5707963267948966 - asin_core(xa);
        }
        /* arccos (-x) = pi - arccos(x) */
        return (x < 0.0) ? (3.1415926535897932 - t) : t;
    }

    /**
     * Lineal interpolation.
     * 
     * @param x
     *            The value to interpolate.
     * @param x0
     *            Inferior limit to the independent value.
     * @param x1
     *            Superior limit to the independent value.
     * @param y0
     *            Inferior limit to the dependent value.
     * @param y1
     *            Superior limit to the dependent value.
     * @return
     */
    public static double lint(double x, double x0, double x1, double y0, double y1) {
        double rx0 = x0;
        double rx1 = x1;
        if (x0 > x1) {
            rx0 = x1;
            rx1 = x0;
        }

        if (x < rx0) {
            return y0;
        }
        if (x > rx1) {
            return y1;
        }

        return y0 + (y1 - y0) * (x - rx0) / (rx1 - rx0);
    }

    /**
     * Lineal interpolation.
     * 
     * @param x
     *            The value to interpolate.
     * @param x0
     *            Inferior limit to the independent value.
     * @param x1
     *            Superior limit to the independent value.
     * @param y0
     *            Inferior limit to the dependent value.
     * @param y1
     *            Superior limit to the dependent value.
     * @return
     */
    public static float lint(float x, float x0, float x1, float y0, float y1) {
        float rx0 = x0;
        float rx1 = x1;
        if (x0 > x1) {
            rx0 = x1;
            rx1 = x0;
        }

        if (x < rx0) {
            return y0;
        }
        if (x > rx1) {
            return y1;
        }

        return y0 + (y1 - y0) * (x - rx0) / (rx1 - rx0);
    }



}

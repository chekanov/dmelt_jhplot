/**
 * Error propagations. Some part of this code belongs to Dr.Flagan (very early version of
 * under public license.)  Later versions  had a different license and different coding 
 * (for non-commercial use). 
 * 
 * @author Dr Chekanov 
 */

package jhplot.math;

import java.io.*;
import org.apache.commons.math3.util.FastMath;


/**
 * A value represented with the number (value) and associated error. This class
 * contains methods useful for error propagation.
 * You can enabled fast math calculation using  
 * @see jhplot.HParam 
 * 
 * @author Dr Chekanov
 * 
 */
public class ValueErr implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double val = 0.0D;
	private double err = 0.0D;

	/**
	 * Initialize error propagations
	 */
	public ValueErr() {
		val = 0.0;
		err = 0.0;
	}

	/**
	 * Initialize with value and set error=0
	 * 
	 * @param value
	 */
	public ValueErr(double value) {
		this.val = value;
		this.err = 0.0;
	}

	/**
	 * Initialize error propagation
	 * 
	 * @param value
	 *            value
	 * @param error
	 *            its error
	 */
	public ValueErr(double value, double error) {
		this.val = value;
		this.err = error;
	}

	/**
	 * Set value
	 * 
	 * @param value
	 */
	public void setVal(double value) {
		this.val = value;
	}

	/**
	 * Set error
	 * 
	 * @param error
	 */
	public void setErr(double error) {
		this.err = error;
	}

	/**
	 * Set value and errors to 0
	 * 
	 * @param value
	 * @param error
	 */
	public void reset(double value, double error) {
		this.val = value;
		this.err = error;
	}

	/**
	 * Get current value
	 * 
	 * @return value
	 */
	public double getVal() {
		return val;
	}

	/**
	 * Get current error
	 * 
	 * @return error
	 */
	public double getErr() {
		return err;
	}

	/**
	 * Convert to a string
	 */
	public String toString() {
		return this.val + " +- " + this.err;
	}

	public int hashCode() {
		long lvalue = Double.doubleToLongBits(this.val);
		long lerror = Double.doubleToLongBits(this.err);
		int hvalue = (int) (lvalue ^ (lvalue >>> 32));
		int herror = (int) (lerror ^ (lerror >>> 32));
		return 7 * (hvalue / 10) + 3 * (herror / 10);
	}

	/**
	 * Create a one dimensional array of ValueErr objects of length n. all
	 * values are zero
	 */
	public ValueErr[] oneDarray(int n) {
		ValueErr[] a = new ValueErr[n];
		for (int i = 0; i < n; i++) {
			a[i] = zero();
		}
		return a;
	}

	/**
	 * Create a one dimensional array of ValueErr objects of length n and m
	 */
	public ValueErr[] oneDarray(int n, ValueErr constant) {
		ValueErr[] c = new ValueErr[n];
		for (int i = 0; i < n; i++) {
			c[i] = copy(constant);
		}
		return c;
	}

	/**
	 * Create a two dimensional array of ValueErr objects of dimensions n and m
	 * with zeros
	 */
	public ValueErr[][] twoDarray(int n, int m) {
		ValueErr[][] a = new ValueErr[n][m];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				a[i][j] = zero();
			}
		}
		return a;
	}

	/**
	 * Copy value
	 */
	public static ValueErr copy(ValueErr a) {
		ValueErr b = new ValueErr();
		b.val = a.val;
		b.err = a.err;
		return b;
	}

	/**
	 * Copy a single ValueErr
	 */
	public ValueErr copy() {
		ValueErr b = new ValueErr();
		b.val = this.val;
		b.err = this.err;
		return b;
	}

	// Clone a single ValueErr number
	public Object clone() {
		ValueErr b = new ValueErr();
		b.val = this.val;
		b.err = this.err;
		return (Object) b;
	}

	// Copy a 1D array of ValueErr numbers (deep copy)
	public ValueErr[] copy(ValueErr[] a) {
		int n = a.length;
		ValueErr[] b = oneDarray(n);
		for (int i = 0; i < n; i++) {
			b[i] = copy(a[i]);
		}
		return b;
	}

	/**
	 * Deep copy a 2D array of ValueErr numbers
	 */
	public ValueErr[][] copy(ValueErr[][] a) {
		int n = a.length;
		int m = a[0].length;
		ValueErr[][] b = twoDarray(n, m);
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				b[i][j] = ValueErr.copy(a[i][j]);
			}
		}
		return b;
	}

	/**
	 * Subtract an ValueErr number from this ValueErr number with correlation
	 */
	public ValueErr minus(ValueErr a, double corrCoeff) {
		ValueErr c = new ValueErr();
		c.val = this.val - a.val;
		c.err = hypWithCovariance(this.err, a.err, -corrCoeff);
		return c;
	}

	/**
	 * Subtract ValueErr number b from ValueErr number a with correlation
	 */
	public ValueErr minus(ValueErr a, ValueErr b, double corrCoeff) {
		ValueErr c = new ValueErr();
		c.val = a.val - b.val;
		c.err = hypWithCovariance(a.err, b.err, -corrCoeff);
		return c;
	}

	/**
	 * Subtract a ValueErr number from this ValueErr number without correlation
	 */
	public ValueErr minus(ValueErr a) {
		ValueErr b = new ValueErr();
		b.val = this.val - a.val;
		b.err = hypWithCovariance(a.err, this.err, 0.0D);
		return b;
	}

	/**
	 * Subtract ValueErr number b from ValueErr number without correlation
	 */
	public ValueErr minus(ValueErr a, ValueErr b) {
		ValueErr c = new ValueErr();
		c.val = a.val - b.val;
		c.err = hypWithCovariance(a.err, b.err, 0.0D);
		return c;
	}

	/**
	 * Subtract an error free double number from this ValueErr number.
	 */
	public ValueErr minus(double a) {
		ValueErr b = new ValueErr();
		b.val = this.val - a;
		b.err = Math.abs(this.err);
		return b;
	}

	/**
	 * Subtract a ValueErr number b from an error free double
	 */
	public ValueErr minus(double a, ValueErr b) {
		ValueErr c = new ValueErr();
		c.val = a - b.val;
		c.err = Math.abs(b.err);
		return c;
	}

	/**
	 * Subtract an error free double number b from an error free double a and
	 * return sum as ValueErr
	 */
	public ValueErr minus(double a, double b) {
		ValueErr c = new ValueErr();
		c.val = a - b;
		c.err = 0.0D;
		return c;
	}

	/**
	 * Subtract a ValueErr number to this ValueErr number and replace this with
	 * the sum with correlation
	 */
	public void minusEquals(ValueErr a, double corrCoeff) {
		this.val -= a.val;
		this.err = hypWithCovariance(a.err, this.err, -corrCoeff);
	}

	/**
	 * Subtract a ValueErr number from this ValueErr number and replace this
	 * with the sum with no correlation
	 */
	public void minusEquals(ValueErr a) {
		this.val -= a.val;
		this.err = hypWithCovariance(a.err, this.err, 0.0D);
	}

	/**
	 * Subtract a double number from this ValueErr number and replace this with
	 * the sum
	 */
	public void minusEquals(double a) {
		this.val -= a;
		this.err = Math.abs(this.err);
	}

	/**
	 * Add 2 values with correlation
	 */
	public ValueErr plus(ValueErr a, double corrCoeff) {
		ValueErr c = new ValueErr();
		c.val = a.val + this.val;
		c.err = hypWithCovariance(a.err, this.err, corrCoeff);
		return c;
	}

	/**
	 * Adding 2 values with correlation
	 */
	public ValueErr plus(ValueErr a, ValueErr b, double corrCoeff) {
		ValueErr c = new ValueErr();
		c.val = a.val + b.val;
		c.err = hypWithCovariance(a.err, b.err, corrCoeff);
		return c;
	}

	/**
	 * Add a ValueErr number to this ValueErr number without correlaton
	 * 
	 */
	public ValueErr plus(ValueErr a) {
		ValueErr b = new ValueErr();
		b.val = this.val + a.val;
		b.err = hypWithCovariance(a.err, this.err, 0.0D);
		return b;
	}

	/**
	 * Add two ValueErr numbers with no correlation
	 */
	public ValueErr plus(ValueErr a, ValueErr b) {
		ValueErr c = new ValueErr();
		c.val = a.val + b.val;
		c.err = hypWithCovariance(a.err, b.err, 0.0D);
		return c;
	}

	/**
	 * Add an error free double number to this ValueErr number
	 */
	public ValueErr plus(double a) {
		ValueErr b = new ValueErr();
		b.val = this.val + a;
		b.err = Math.abs(this.err);
		return b;
	}

	/**
	 * Add a ValueErr number to an error free double
	 */
	public ValueErr plus(double a, ValueErr b) {
		ValueErr c = new ValueErr();
		c.val = a + b.val;
		c.err = Math.abs(b.err);
		return c;
	}

	/**
	 * Add an error free double number to an error free double and return sum
	 */
	public ValueErr plus(double a, double b) {
		ValueErr c = new ValueErr();
		c.val = a + b;
		c.err = 0.0D;
		return c;
	}

	/**
	 * Add a ValueErr number to this ValueErr number and replace this with the
	 * sum using a correlation
	 */
	public void plusEquals(ValueErr a, double corrCoeff) {
		this.val += a.val;
		this.err = hypWithCovariance(a.err, this.err, corrCoeff);
	}

	/**
	 * Add a ValueErr number to this ValueErr number and replace this with the
	 * sum without correlation
	 */
	public void plusEquals(ValueErr a) {
		this.val += a.val;
		this.err = Math.sqrt(a.err * a.err + this.err * this.err);
		this.err = hypWithCovariance(a.err, this.err, 0.0D);
	}

	/**
	 * Add double number to this ValueErr number and replace this with the sum
	 */
	public void plusEquals(double a) {
		this.val += a;
		this.err = Math.abs(this.err);
	}

	/**
	 * Multiply two ValueErr numbers with correlation
	 */
	public ValueErr times(ValueErr a, double corrCoeff) {
		ValueErr c = new ValueErr();
		c.val = a.val * this.val;
		if (a.val == 0.0D) {
			c.err = a.err * this.val;
		} else {
			if (this.val == 0.0D) {
				c.err = this.err * a.val;
			} else {
				c.err = Math.abs(c.val)
						* hypWithCovariance(a.err / a.val, this.err / this.val,
								corrCoeff);
			}
		}
		return c;
	}

	/**
	 * Multiply this ValueErr number by a ValueErr number without correlation
	 */
	public ValueErr times(ValueErr a) {
		ValueErr b = new ValueErr();
		b.val = this.val * a.val;
		if (a.val == 0.0D) {
			b.err = a.err * this.val;
		} else {
			if (this.val == 0.0D) {
				b.err = this.err * a.val;
			} else {
				b.err = Math.abs(b.val)
						* hypWithCovariance(a.err / a.val, this.err / this.val,
								0.0D);
			}
		}
		return b;
	}

	/**
	 * Multiply this ValueErr number by a double. ValueErr number remains
	 * unaltered
	 */
	public ValueErr times(double a) {
		ValueErr b = new ValueErr();
		b.val = this.val * a;
		b.err = Math.abs(this.err * a);
		return b;
	}

	/**
	 * Multiply two ValueErr numbers with correlation
	 */
	public ValueErr times(ValueErr a, ValueErr b, double corrCoeff) {
		ValueErr c = new ValueErr();
		c.val = a.val * b.val;
		if (a.val == 0.0D) {
			c.err = a.err * b.val;
		} else {
			if (b.val == 0.0D) {
				c.err = b.err * a.val;
			} else {
				c.err = Math.abs(c.val)
						* hypWithCovariance(a.err / a.val, b.err / b.val,
								corrCoeff);
			}
		}
		return c;
	}

	/**
	 * Multiply two ValueErr numbers without correlation
	 */
	public ValueErr times(ValueErr a, ValueErr b) {
		ValueErr c = new ValueErr();
		c.val = a.val * b.val;
		if (a.val == 0.0D) {
			c.err = a.err * b.val;
		} else {
			if (b.val == 0.0D) {
				c.err = b.err * a.val;
			} else {
				c.err = Math.abs(c.val)
						* hypWithCovariance(a.err / a.val, b.err / b.val, 0.0D);
			}
		}
		return c;
	}

	/**
	 * Multiply a double by a ValueErr number
	 */
	public ValueErr times(double a, ValueErr b) {
		ValueErr c = new ValueErr();
		c.val = a * b.val;
		c.err = Math.abs(a * b.err);
		return c;
	}

	/**
	 * Multiply a double number by a double and return product as ValueErr
	 */
	public ValueErr times(double a, double b) {
		ValueErr c = new ValueErr();
		c.val = a * b;
		c.err = 0.0;
		return c;
	}

	/**
	 * Multiply this ValueErr number by an ValueErr number and replace this by
	 * the product with correlation
	 */
	public void timesEquals(ValueErr a, double corrCoeff) {
		ValueErr b = new ValueErr();
		b.val = this.val * a.val;
		if (a.val == 0.0D) {
			b.err = a.err * this.val;
		} else {
			if (this.val == 0.0D) {
				b.err = this.err * a.val;
			} else {
				b.err = Math.abs(b.val)
						* hypWithCovariance(a.err / a.val, this.err / this.val,
								corrCoeff);
			}
		}

		this.val = b.val;
		this.err = b.err;
	}

	/**
	 * Multiply this ValueErr number by an ValueErr number and replace this by
	 * the product with no correlation
	 */
	public void timesEquals(ValueErr a) {
		ValueErr b = new ValueErr();
		b.val = this.val * a.val;
		if (a.val == 0.0D) {
			b.err = a.err * this.val;
		} else {
			if (this.val == 0.0D) {
				b.err = this.err * a.val;
			} else {
				b.err = Math.abs(b.val)
						* hypWithCovariance(a.err / a.val, this.err / this.val,
								0.0D);
			}
		}

		this.val = b.val;
		this.err = b.err;
	}

	/**
	 * Multiply this ValueErr number by a double and replace it by the product
	 * 
	 */
	public void timesEquals(double a) {
		this.val = this.val * a;
		this.err = Math.abs(this.err * a);
	}

	/**
	 * Division of this ValueErr number by a ValueErr number.
	 */
	public ValueErr divide(ValueErr a, double corrCoeff) {
		ValueErr c = new ValueErr();
		c.val = this.val / a.val;
		if (this.val == 0.0D) {
			c.err = this.err * a.val;
		} else {
			c.err = Math.abs(c.val)
					* hypWithCovariance(this.err / this.val, a.err / a.val,
							-corrCoeff);
		}
		return c;
	}

	/**
	 * Division of two ValueErr numbers a/b with correlation
	 */
	public ValueErr divide(ValueErr a, ValueErr b, double corrCoeff) {
		ValueErr c = new ValueErr();
		c.val = a.val / b.val;
		if (a.val == 0.0D) {
			c.err = a.err * b.val;
		} else {
			c.err = Math.abs(c.val)
					* hypWithCovariance(a.err / a.val, b.err / b.val,
							-corrCoeff);
		}
		return c;
	}

	/**
	 * Division of this ValueErr number by a ValueErr number without correlation
	 */
	public ValueErr divide(ValueErr a) {
		ValueErr b = new ValueErr();
		b.val = this.val / a.val;
		b.err = Math.abs(b.val)
				* hypWithCovariance(a.err / a.val, this.err / this.val, 0.0);
		if (this.val == 0.0D) {
			b.err = this.err * b.val;
		} else {
			b.err = Math.abs(b.val)
					* hypWithCovariance(a.err / a.val, this.err / this.val, 0.0);
		}
		return b;
	}

	/**
	 * Division of two ValueErr numbers a/b without correlation
	 */
	public ValueErr divide(ValueErr a, ValueErr b) {
		ValueErr c = new ValueErr();
		c.val = a.val / b.val;
		if (a.val == 0.0D) {
			c.err = a.err * b.val;
		} else {
			c.err = Math.abs(c.val)
					* hypWithCovariance(a.err / a.val, b.err / b.val, 0.0D);
		}

		return c;
	}

	/**
	 * Division of this ValueErr number by a double
	 */
	public ValueErr divide(double a) {
		ValueErr b = new ValueErr();
		b.val = this.val / a;
		b.err = Math.abs(this.err / a);
		return b;
	}

	/**
	 * Division of a double, a, by a ValueErr number, b
	 */
	public ValueErr divide(double a, ValueErr b) {
		ValueErr c = new ValueErr();
		c.val = a / b.val;
		c.err = Math.abs(a * b.err / (b.val * b.val));
		return c;
	}

	/**
	 * Divide a double number by a double and return quotient as ValueErr
	 */
	public ValueErr divide(double a, double b) {
		ValueErr c = new ValueErr();
		c.val = a / b;
		c.err = 0.0;
		return c;
	}

	/**
	 * Division of this ValueErr number by a ValueErr number and replace it by
	 * the quotient without correlation
	 */
	public void divideEqual(ValueErr b) {
		ValueErr c = new ValueErr();
		c.val = this.val / b.val;
		if (this.val == 0.0D) {
			c.err = this.err * b.val;
		} else {
			c.err = Math.abs(c.val)
					* hypWithCovariance(this.err / this.val, b.err / b.val,
							0.0D);
		}
		this.val = c.val;
		this.err = c.err;
	}

	/**
	 * Division of this ValueErr number by a ValueErr number and replace this by
	 * the quotient
	 */
	public void divideEqual(ValueErr b, double corrCoeff) {
		ValueErr c = new ValueErr();
		c.val = this.val / b.val;
		if (this.val == 0.0D) {
			c.err = this.err * b.val;
		} else {
			c.err = Math.abs(c.val)
					* hypWithCovariance(this.err / this.val, b.err / b.val,
							-corrCoeff);
		}
		this.val = c.val;
		this.err = c.err;
	}

	/**
	 * Division of this ValueErr number by a double and replace this by the
	 * quotient
	 */
	public void divideEqual(double a) {
		this.val = this.val / a;
		this.err = Math.abs(this.err / a);
	}

	/**
	 * Returns the inverse (1/a) of a ValueErr number
	 */
	public ValueErr inverse() {
		ValueErr b = divide(1.0D, this);
		return b;
	}

	/**
	 * Returns the reciprocal (1/a) of a ValueErr number (a)
	 */
	public ValueErr inverse(ValueErr a) {
		ValueErr b = divide(1.0, a);
		return b;
	}

	/**
	 * Returns the length of the hypotenuse of a and b i.e. sqrt(a*a + b*b)
	 * where a and b are ValueErr
	 */
	public ValueErr hypotenuse(ValueErr a, ValueErr b, double corrCoeff) {
		ValueErr c = new ValueErr();
		c.val = hypotenuse(a.val, b.val);
		c.err = Math.abs(hypWithCovariance(a.err * a.val, b.err * b.val,
				corrCoeff) / c.val);
		return c;
	}

	/**
	 * Returns the length of the hypotenuse of a and b i.e. sqrt(a*a+b*b)
	 */
	public double hypotenuse(double aa, double bb) {
		double amod = Math.abs(aa);
		double bmod = Math.abs(bb);
		double cc = 0.0D, ratio = 0.0D;
		if (amod == 0.0) {
			cc = bmod;
		} else {
			if (bmod == 0.0) {
				cc = amod;
			} else {
				if (amod >= bmod) {
					ratio = bmod / amod;
					cc = amod * Math.sqrt(1.0 + ratio * ratio);
				} else {
					ratio = amod / bmod;
					cc = bmod * Math.sqrt(1.0 + ratio * ratio);
				}
			}
		}
		return cc;
	}

	/**
	 * Returns the length of the hypotenuse of a and b i.e. sqrt(a*a + b*b)
	 * where a and b are ValueErr
	 */
	public ValueErr hypot(ValueErr a, ValueErr b) {
		ValueErr c = new ValueErr();
		c.val = hypotenuse(a.val, b.val);
		c.err = Math.abs(hypWithCovariance(a.err * a.val, b.err * b.val, 0.0D)
				/ c.val);
		return c;
	}

	/**
	 * Get exponential function
	 * 
	 * @param a
	 *            input error
	 * @return
	 */
	public ValueErr exp(ValueErr a) {
		ValueErr b = new ValueErr();
		b.val = Math.exp(a.val);
		b.err = Math.abs(b.val * a.err);
		return b;
	}

	/**
	 * Take natural log
	 * 
	 * @param a
	 *            input value
	 * @return
	 */
	public ValueErr log(ValueErr a) {
		ValueErr b = new ValueErr();
                if (jhplot.HParam.isFastMath()) b.val = FastMath.log(a.val);
		else b.val = Math.log(a.val);
		b.err = Math.abs(a.err / a.val);
		return b;
	}

	/**
	 * log to base 10
	 * 
	 * @param log
	 *            to base 10
	 * @return output
	 */
	public ValueErr log10(ValueErr a) {
		ValueErr b = new ValueErr();
                if (jhplot.HParam.isFastMath()) b.val = FastMath.log10(a.val);
		else b.val = log10(a.val);
		b.err = Math.abs(a.err / (a.val * Math.log(10.0D)));
		return b;
	}

	public double log10(double a) {
		return Math.log(a) / Math.log(10.0D);
	}

	/**
	 * Get square root value
	 */
	public ValueErr sqrt(ValueErr a) {
		ValueErr b = new ValueErr();
                if (jhplot.HParam.isFastMath()) b.val = FastMath.sqrt(a.val); 
		else b.val = Math.sqrt(a.val);
		b.err = Math.abs(a.err / (2.0D * a.val));
		return b;
	}

	/**
	 * Take nth root from the value (n is above 1)
	 */
	public ValueErr nRoot(ValueErr a, int n) {
		if (n == 0)
			throw new ArithmeticException(
					"Division by zero (n = 0 - infinite root)");
		ValueErr b = new ValueErr();
                if (jhplot.HParam.isFastMath()) b.val = FastMath.pow(a.val, 1 / n);
		else b.val = Math.pow(a.val, 1 / n);
		b.err = Math.abs(a.err * Math.pow(a.val, 1 / n - 1) / ((double) n));
		return b;
	}

	/**
	 * Square
	 */
	public ValueErr square() {
		ValueErr a = new ValueErr(this.val, this.err);
		return a.times(a, 1.0D);
	}

	/**
	 * Square
	 */
	public ValueErr square(ValueErr a) {
		return a.times(a, 1.0D);
	}

	/**
	 * returns an ValueErr number raised to an error free power
	 */
	public ValueErr pow(ValueErr a, double b) {
		ValueErr c = new ValueErr();
                if (jhplot.HParam.isFastMath()) c.val = FastMath.pow(a.val, b);
		else c.val = Math.pow(a.val, b);
		c.err = Math.abs(b * Math.pow(a.val, b - 1.0));
		return c;
	}

	/**
	 * returns an error free number raised to an ValueErr power
	 */
	public ValueErr pow(double a, ValueErr b) {
		ValueErr c = new ValueErr();
                if (jhplot.HParam.isFastMath()) c.val = FastMath.pow(a, b.val);
		else c.val = Math.pow(a, b.val);
		c.err = Math.abs(c.val * Math.log(a) * b.err);
		return c;
	}

	/**
	 * returns a ValueErr number raised to a ValueErr power with correlation
	 */
	public ValueErr pow(ValueErr a, ValueErr b, double corrCoeff) {
		ValueErr c = new ValueErr();
                if (jhplot.HParam.isFastMath()) c.val = FastMath.pow(a.val, b.val); 
		else c.val = Math.pow(a.val, b.val);
		c.err = hypWithCovariance(a.err * b.val * Math.pow(a.val, b.val - 1.0),
				b.err * Math.log(a.val) * Math.pow(a.val, b.val), corrCoeff);
		return c;
	}

	/**
	 * ValueErr number raised to a ValueErr power with no correlation
	 */
	public ValueErr pow(ValueErr a, ValueErr b) {
		ValueErr c = new ValueErr();
                if (jhplot.HParam.isFastMath()) c.val = FastMath.pow(a.val, b.val);
		else c.val = Math.pow(a.val, b.val);
		c.err = hypWithCovariance(a.err * b.val * Math.pow(a.val, b.val - 1.0),
				b.err * Math.log(a.val) * Math.pow(a.val, b.val), 0.0D);
		return c;
	}

	/**
	 * sine of an ValueErr number (trigonometric function)
	 */
	public ValueErr sin(ValueErr a) {
		ValueErr b = new ValueErr();
                if (jhplot.HParam.isFastMath()) b.val = FastMath.sin(a.val);
		else b.val = Math.sin(a.val);
		b.err = Math.abs(a.err * Math.cos(a.val));
		return b;
	}

	/**
	 * Cosine of a value wth error
	 */
	public ValueErr cos(ValueErr a) {
		ValueErr b = new ValueErr();
                if (jhplot.HParam.isFastMath()) b.val = FastMath.cos(a.val);
		else b.val = Math.cos(a.val);
		b.err = Math.abs(a.err * Math.sin(a.val));
		return b;
	}

	/**
	 * Tangent of a value with error
	 */
	public ValueErr tan(ValueErr a) {
		ValueErr b = new ValueErr();
                if (jhplot.HParam.isFastMath()) b.val = FastMath.tan(a.val);
		else b.val = Math.tan(a.val);
		b.err = Math.abs(a.err * square(sec(a.val)));
		return b;
	}

	/**
	 * Secant
	 * 
	 * @param a
	 * @return
	 */
	public double sec(double a) {
		return 1.0 / Math.cos(a);
	}

	public double square(double a) {
		return a * a;
	}

	/**
	 * Hyperbolic sine of a value with error
	 */
	public ValueErr sinh(ValueErr a) {
		ValueErr b = new ValueErr();
                if (jhplot.HParam.isFastMath()) b.val = FastMath.sinh(a.val);
		else b.val = Math.sinh(a.val);
		b.err = Math.abs(a.err * Math.cosh(a.val));
		return b;
	}

	/**
	 * Hyperbolic cosine
	 */
	public ValueErr cosh(ValueErr a) {
		ValueErr b = new ValueErr();
                if (jhplot.HParam.isFastMath()) b.val = FastMath.cosh(a.val);
		else b.val = Math.cosh(a.val);
		b.err = Math.abs(a.err * Math.sinh(a.val));
		return b;
	}

	/**
	 * Hyperbolic tangent of value with error
	 */
	public ValueErr tanh(ValueErr a) {
		ValueErr b = new ValueErr();
                if (jhplot.HParam.isFastMath()) b.val = FastMath.tanh(a.val);
		else b.val = Math.tanh(a.val);
		b.err = Math.abs(a.err * square(sech(a.val)));
		return b;
	}

	/**
	 * Hyperbolic secant
	 */
	public double sech(double a) {
		return 1.0D / Math.cosh(a);
	}

	/**
	 * Inverse sine of a value with error
	 */
	public ValueErr asin(ValueErr a) {
		ValueErr b = new ValueErr();
                if (jhplot.HParam.isFastMath()) b.val = FastMath.asin(a.val);
		else b.val = Math.asin(a.val);
		b.err = Math.abs(a.err / Math.sqrt(1.0D - a.val * a.val));
		return b;
	}

	/**
	 * Inverse cosine of a value with error
	 */
	public ValueErr acos(ValueErr a) {
		ValueErr b = new ValueErr();
                if (jhplot.HParam.isFastMath()) b.val = FastMath.acos(a.val);
                else b.val = Math.acos(a.val); 
		b.err = Math.abs(a.err / Math.sqrt(1.0D - a.val * a.val));
		return b;
	}

	/**
	 * inverse tangent of a value
	 */
	public ValueErr atan(ValueErr a) {
		ValueErr b = new ValueErr();
                if (jhplot.HParam.isFastMath()) b.val = FastMath.atan(a.val);
		else b.val = Math.atan(a.val);
		b.err = Math.abs(a.err / (1.0D + a.val * a.val));
		return b;
	}

	/**
	 * Inverse tangent (atan2) of a value without correlations
	 */
	public ValueErr atan2(ValueErr a, ValueErr b) {
		ValueErr c = new ValueErr();
		ValueErr d = a.divide(b);
		c.val = Math.atan2(a.val, b.val);
		c.err = Math.abs(d.err / (1.0D + d.val * d.val));
		return c;
	}

	/**
	 * Inverse tangent (atan2) of a value with a correlation
	 */
	public ValueErr atan2(ValueErr a, ValueErr b, double rho) {
		ValueErr c = new ValueErr();
		ValueErr d = a.divide(b, rho);
                if (jhplot.HParam.isFastMath()) c.val = FastMath.atan2(a.val, b.val);
		else c.val = Math.atan2(a.val, b.val);
		c.err = Math.abs(d.err / (1.0D + d.val * d.val));
		return c;
	}

	/**
	 * Inverse hyperbolic sine of a value with error
	 */
	public ValueErr asinh(ValueErr a) {
		ValueErr b = new ValueErr();
		b.val = asinh(a.val);
		b.err = Math.abs(a.err / Math.sqrt(a.val * a.val + 1.0D));
		return b;
	}

	/**
	 * Inverse hyperbolic sine of a double number
	 */
	public double asinh(double a) {
		double sgn = 1.0D;
		if (a < 0.0D) {
			sgn = -1.0D;
			a = -a;
		}
		return sgn * Math.log(a + Math.sqrt(a * a + 1.0D));
	}

	/**
	 * Set value to zero
	 */
	public ValueErr zero() {
		ValueErr c = new ValueErr();
		c.val = 0.0D;
		c.err = 0.0D;
		return c;
	}

	/**
	 * Sign function
	 * 
	 * @returns -1 if x < 0 else returns 1
	 */
	public double sign(double x) {
		if (x < 0.0) {
			return -1.0;
		} else {
			return 1.0;
		}
	}

	/**
	 * Private methods. Calculation of sqrt(a*a + b*b + 2*r*a*b) (safe)
	 */
	public double hypWithCovariance(double a, double b, double r) {
		double pre = 0.0D, ratio = 0.0D, sgn = 0.0D;

		if (a == 0.0D && b == 0.0D)
			return 0.0D;
		if (Math.abs(a) > Math.abs(b)) {
			pre = Math.abs(a);
			ratio = b / a;
			sgn = sign(a);
		} else {
			pre = Math.abs(b);
			ratio = a / b;
			sgn = sign(b);
		}
		return pre * Math.sqrt(1.0D + ratio * (ratio + 2.0D * r * sgn));
	}
}


package jhplot.math;

/**
 * A small library to work with vectors defined by some dimension.
 * Default is the dimension of Euclidean space (3).
 *
 * @author Wolfhard Hoevel
 */
public class Vec {


	static private int dim = 3;

	//------------------------------------------------------------------------------

	/**
	* Set dimention of vectors. 
	*@param dimension dimension of the vectors. 
	**/
	static public void setDimension(int dimension){
		dim=dimension;
	}


	/**
	* make a random vector.
	* @return Returns a random vector (initial conditions).
	**/
	static public double[] randomVector(double c){
		//Reichweite der Anfangswerte, von -c/2 bis + c/2
		double[] temp = new double[dim];
		for (int i = 0; i < dim; i++) {
			temp[i] = c*(Math.random()-0.5);
		}
		return temp;
	}

	/**
	* Vector times scalar
	**/
	static public double[] multiplay(double c, double[] a){
		double[] temp = new double[dim];
		for (int i = 0; i < dim; i++){
			temp[i] = c *a[i];
		}
		return temp;
	}

	/**
	* Addition of two vectors.
	**/
	static public double[] add(double[] a, double[] b){
		double[] temp = new double[dim];
		for (int i = 0; i < dim; i++){
			temp[i] = a[i] + b[i];
		}
		return temp;
	}

	/**
	* Subtraction of two vectors.
	**/
	static public double[] subtract(double[] a, double[] b){
		double[] temp = new double[dim];
		for (int i = 0; i < dim; i++){
			temp[i] = a[i] - b[i];
		}
		return temp;
	}

	/**
	 * Dot product of two vectors.
	 **/
	static public double dotProduct(double[] a, double[] b){
		double temp = 0.0;
		for (int i = 0; i < dim; i++){
			temp = temp + a[i] * b[i];
		}
		return temp;
	}

	/**
	* Returns the magnitude of this vector.
	**/
	static public double magnitude(double[] a) {
		return Math.sqrt(dotProduct(a, a));
	}

	/**
	* Returns the unit vector of this vector.
	**/
	static public double[] unitVector(double[] a) {
		//   double[] temp = new double[dim];
		double c;
		c = magnitude(a);
		if (c < 1.0E-15) c = 1.0E-15;
		c = 1.0/c;
		a = multiplay(c, a);
		return a;
	}

	/**
	 * Reflection at the boundary
	 **/
	static public double[] reflect(double[] rVector, double[] draVector, int s) {
		double r, rq, scalar, radi, x;
		double[] xVector;
		double[] yVector;
		double[] drbVector;
		rq = dotProduct(rVector, rVector);
		if (rq < 1.0E-15) rq = 1.0E-15;
		r = Math.sqrt(rq);
		scalar = dotProduct(draVector, rVector);
		radi = (rq / (s*s) - 1.0) * (1.0 - scalar*scalar / rq);
		if (radi < 0) radi = 0;
		x = -scalar / r + Math.sqrt(radi);
		xVector = multiplay(x/r, rVector);
		yVector = add(draVector, xVector);
		drbVector = unitVector(yVector);
		return drbVector;
	}

	/**
	 * Returns radial unit vector times pull and -qa*qb. (Charges)
	 **/
	static public double[] drCharge(double[] rVector, double pull, int qa, int qb){
		double[] c = unitVector(rVector);
		c = multiplay(pull*qa*qb*(-1), c);
		return c;
	}

}

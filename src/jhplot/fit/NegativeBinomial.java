package jhplot.fit;

import hep.aida.ref.function.AbstractIFunction;
//  import cern.jet.stat.Probability; 
import jhplot.math.num.pdf.*;

/**
 * Negative binomial distribution.
 * Returns the sum of the terms <tt>0</tt> through <tt>k</tt> of the Negative Binomial Distribution.
 * <pre>
 *   k
 *   --  ( n+j-1 )   n      j
 *   >   (       )  p  (1-p)
 *   --  (   j   )
 *  j=0
 * </pre>
 * In a sequence of Bernoulli trials, this is the probability
 * that <tt>k</tt> or fewer failures precede the <tt>n</tt>-th success.
 * <p>
 * The terms are not computed individually; instead the incomplete
 * beta integral is employed, according to the formula
 * <p>
 * <tt>y = negativeBinomial( k, n, p ) = Gamma.incompleteBeta( n, k+1, p )</tt>.
 *
 * All arguments must be positive, 
 * p[0] - scale  factor <br>
 * p[1] - the number of trials. <br> 
 * p[2] - the probability of success (must be in <tt>(0.0,1.0)</tt>).
*/
 
public class NegativeBinomial extends AbstractIFunction {

	public NegativeBinomial() {
		this("NegativeBinomial");
	}

	public NegativeBinomial(String title) {
		super(title, 1, 3);
	}

	public NegativeBinomial(String[] variableNames, String[] parameterNames) {
		super(variableNames, parameterNames);
	}

	
	/**
	 * Get value of this function
	 */
	public double value(double[] v) {

		 int numberOfSuccesses=(int)p[1];
		 double probabilityOfSuccess=p[2];
		 int x=(int)v[0];
			 
			 
		 double ret;
	        if (x < 0) {
	            ret = 0.0;
	        } else {
	            ret = Math.exp(jhplot.math.num.pdf.SaddlePoint.logBinomialProbability(
	                numberOfSuccesses, x + numberOfSuccesses, probabilityOfSuccess,
	                1.0 - probabilityOfSuccess));
	            ret = (numberOfSuccesses * ret) / (x + numberOfSuccesses);
	        }
	        return p[0] * ret;	
		
	}

	
	
	
	
	// Here change the parameter names
	protected void init(String title) {
            parameterNames[0] = "norm";
            parameterNames[1] = "numbertrials";
            parameterNames[2] = "probsuccess";

		super.init(title);
	}
}

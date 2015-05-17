package jhplot.fit;

import hep.aida.ref.function.AbstractIFunction;
//  import cern.jet.stat.Probability; 
import jhplot.math.num.pdf.*;

/**
 * Poisson distribution.
*/
 
public class Poisson extends AbstractIFunction {

	public Poisson() {
		this("Poisson");
	}

	public Poisson(String title) {
		super(title, 1, 2);
	}

	public Poisson(String[] variableNames, String[] parameterNames) {
		super(variableNames, parameterNames);
	}

	public double value(double[] v) {

                   jhplot.math.num.pdf.Poisson nbd2= new  jhplot.math.num.pdf.Poisson( p[1]); 
		// return p[0] * Probability.negativeBinomial((int)v[0], (int)p[1], p[2]);
                   return p[0] * nbd2.probability( (int)v[0] ); 
	}

	// Here change the parameter names
	protected void init(String title) {
            parameterNames[0] = "norm";
            parameterNames[1] = "lambda";

		super.init(title);
	}
}

package jhplot;

import java.net.URL;

import jhplot.bsom.*;
import jhplot.gui.HelpBrowser;


/**
 * The Bayesian self-organizing map (BSOM).
 * This is a method for estimating a probability distribution generating data 
 * points on the basis of a Bayesian stochastic model. 
 * It is also regarded as a learning method for a kind of neural network. 
 * The black dots in the below figure denote artificially generated data points.
 * Based on work of: Akio Utsugi.
 * <p> 
 * This class is based on:
 *  A. Utsugi (1996) ``Topology selection for self-organizing maps", 
 *  Network: Computation in Neural Systems, vol. 7, no. 4, 727-740. <p>
 *  A. Utsugi (1997) ``Hyperparameter selection for self-organizing maps", 
 *  Neural Computation, vol. 9, no. 3, pp. 623-635.
 *  <p>
 *  @author S.Chekanov
 **/
public class HBsom {

	public Bsom bsom;
	private int units=0;
	private double alpha=0;
	private double beta=0;
	
	/**
	 * Initialize BSOM.
	 *
	 * */	
	public HBsom() {
		bsom = new Bsom();
	}


	/**
	*   Set number of points for fit 
	*
	*  @param units set number of points for fit. 
	* */

	public void setNPoints(int units) {
		this.units=units;
	}





	/**
	 * Load data to BSOM
	 * @param p1d input data
	 */
	public void setData(P1D p1d) {

		bsom.loadData(p1d);
		if (units == 0) units=p1d.size();

		if (alpha == 0 && beta == 0) {
			bsom.initPar(5000,100, units);
		}
		else bsom.initPar(alpha,beta, units);
	}

	/**
	 * Load histogram data to BSOM
	 * @param p1d input histogram data
	 */
	public void setData(H1D h) {

		P1D p1d=new P1D(h);
		bsom.loadData(p1d);
		if (units == 0) units=p1d.size();

		if (alpha == 0 && beta == 0) {
			bsom.initPar(5000,100, units);
		}
		else bsom.initPar(alpha,beta, units);
	}
	
	

	/**
	 * Show documentation
	 */
	public void doc() {

		URL  url =bsom.getClass().getResource("doc/readme.html");
		new HelpBrowser(url);


	}


	/**
	 * Set initial alpha and beta parameters.
	 * The BSOM model has a pair of hyperparameters: alpha and beta, which represent `the strength of topological constraint' and 
	 * `the estimate of noise level in data' respectively. You can vary them using the sliders. Observe the variation of the centroid configuration according to the values 
	 * of the hyperparameters and grasp their meaning. Then try to find the optimal values of the hyperparameters giving the best centroid configuration. 
	 * Remark that the configuration depends on not only 
	 * the present values of hyperparameters but also their history. Poor moving of the hyperparameters will lead to a poor local optimal configuration. 
	 * @param alpha alpha value
	 * @param beta beta value
	 */
	public void setAlphaBeta(double alpha, double beta) {
		this.alpha=alpha;
		this.beta=beta;

	}




	/**
	 * Get results of training.
	 * @return P1D with results.
	 */
	public P1D getResult(){
		return bsom.outputWeightP1D();
	}



	/**
	 * Run the algorithm
	 */

	public void run() {

		bsom.learn();
		bsom.auto();
		bsom.startNoThread();
	}

	/**
	 * Set frame visible or not
	 * @param vis true if visible
	 */
	public void visible(boolean vis) {

		if (vis) {
			bsom.init(units);
			bsom.start();
			bsom.pack(); // set window to appropriate size (for its elements)
			bsom.setVisible(true); // usual step to make frame visible
		} else {
			bsom.setVisible(false);
		}

	}


	/**
	*    Set calculation precision.  
	*    Iterations stop if (current(alpha)-previous(alpha) .lt. delta) 
	*    The value should be very small for best results
	*     @param delta precision 
	**/
	public void setDelta(double delta)
	{
		bsom.setDelta(delta);
	}


	/**
	*    Get  calculation precision.  
	**/
	public double getDelta(){ 
	   return 	bsom.getDelta();
	}


	/**
	*  Get number of iterations used for fitting 
	*
	*
	**/
	public int getNiterations()
	{
		return bsom.getNiterations();
	}



	/**
	*
	*  Get alpha. The strength of topological constraint 
	**/ 
	public double getAlpha()
	{
		return bsom.getAlpha();
	}


	/**
	 *
	 * Get beta. This is the estimate of noise level in data. 
	 **/
	public double  getBeta()
	{
		return bsom.getBeta();
	}



	/**
	 * Set visible frame
	 */

	public void visible() {
		visible(true);

	}


}

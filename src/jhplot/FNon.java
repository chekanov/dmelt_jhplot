package jhplot;

import hep.aida.ref.function.AbstractIFunction;
import java.io.Serializable;

/**
* Non-parametric function implementation.  
* You can define any function programically by overwriting the method value().
* By default, parameters are given by the public array p[], while variable names are give by array x[].
* You can overwrite parameters and variables.
* The use it as input for F1D, F2D etc. for plotting.
* 
* @author S.Chekanov
**/

public class FNon extends AbstractIFunction implements Serializable {


	/**
	 * Calculated value. You should overwrite this class.
	 */
   public double value(double[] v) {return 0;}



    public void init(String title) {
        super.init(title);
    }
 
    /**
     * Initialization. 
     * @param title Title
     * @param variable Nr of variables
     * @param parameters Nr of parameters.
     */
    public FNon(String title, int variable, int parameters) {
        super(title, variable, parameters);
    }
    
    /**
     * Initialization where you can redefine the names of the parameters.
     * @param variableNames variable names
     * @param parameterNames parameter names.
     */
    public FNon(String[] variableNames, String[] parameterNames) {
        super(variableNames, parameterNames);
    }
    
    
    /**
     * If dimension is 1, you can return F1D for plotting.
     * @return F1D function
     */
    public F1D getF1D(){
      if 	(dimension()==1) return new F1D(this);
      else System.err.println("Cannot return F1D since dimension is not 1");
      return null;
    }
    	
    /**
     * If dimension is 2, you can return F2D for plotting
     * @param min MinX  min X value for plotting
     * @param max MaxX max X value for plotting
     * @param min MinY min Y value for plotting
     * @param max MaxY max Y value for plotting
     * 
     * @return F2D function
     */
    public F2D getF2D(){
      if 	(dimension()==2) return new F2D(this);
      else System.err.println("Cannot return F2D since dimension is not 2");
      return null;
    }
    
}

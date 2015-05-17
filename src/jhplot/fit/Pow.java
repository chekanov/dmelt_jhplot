package jhplot.fit;

import hep.aida.ref.function.AbstractIFunction;


/**
  * Power-law distribution. p0*pow(p1-x,p2)
  * @author S.Chekanov
  *
**/

public class Pow extends AbstractIFunction {
    
    public Pow() {
        this("Pow");
    }
    
    public Pow(String title) {
        super(title, 1, 3);
    }
    
    public Pow(String[] variableNames, String[] parameterNames) {
        super(variableNames, parameterNames);
    }
    
    public double value(double[] v) {
        return p[0]*Math.pow(p[1]-v[0],p[2]);
    }
    
    // Here change the parameter names
    protected void init(String title) {

        for (int i=0; i<parameterNames.length; i++) { 
            parameterNames[i] = "p"+i; 
        }
        
        super.init(title);
    }
}

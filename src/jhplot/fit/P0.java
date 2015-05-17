package jhplot.fit;

import hep.aida.ref.function.AbstractIFunction;


/**
* Polynomial 0-order (p0) 
* 
* @author S.Chekanov
**/

public class P0 extends AbstractIFunction {
    
    public P0() {
        this("P0");
    }
    
    public P0(String title) {
        super(title, 1, 1);
    }
    
    public P0(String[] variableNames, String[] parameterNames) {
        super(variableNames, parameterNames);
    }
    
    public double value(double[] v) {
        return p[0];
    }
    
    // Here change the parameter names
    protected void init(String title) {
        for (int i=0; i<parameterNames.length; i++) { 
            parameterNames[i] = "p"+i; 
        }
        
        super.init(title);
    }
}

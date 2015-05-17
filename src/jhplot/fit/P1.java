package jhplot.fit;

import hep.aida.ref.function.AbstractIFunction;


/**
* Polynomial 1-order (p0+p1*x)
* 
* @author S.Chekanov
**/


public class P1 extends AbstractIFunction {
    
    public P1() {
        this("P1");
    }
    
    public P1(String title) {
        super(title, 1, 2);
    }
    
    public P1(String[] variableNames, String[] parameterNames) {
        super(variableNames, parameterNames);
    }
    
    public double value(double[] v) {
        return p[0]+ p[1]*v[0];
    }
    
    // Here change the parameter names
    protected void init(String title) {
        for (int i=0; i<parameterNames.length; i++) { 
            parameterNames[i] = "p"+i; 
        }
        
        super.init(title);
    }
}

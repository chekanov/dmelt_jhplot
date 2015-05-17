package jhplot.fit;

import hep.aida.ref.function.AbstractIFunction;


/**
* Polynomial 2-order (p0+p1*x+p2*x*x)
* 
* @author S.Chekanov
**/


public class P2 extends AbstractIFunction {
    
    public P2() {
        this("P2");
    }
    
    public P2(String title) {
        super(title, 1, 3);
    }
    
    public P2(String[] variableNames, String[] parameterNames) {
        super(variableNames, parameterNames);
    }
    
    public double value(double[] v) {
        return p[0]+ p[1]*v[0] + p[2]*v[0]*v[0];
    }
    
    // Here change the parameter names
    protected void init(String title) {
        for (int i=0; i<parameterNames.length; i++) { 
            parameterNames[i] = "p"+i; 
        }
        
        super.init(title);
    }
}

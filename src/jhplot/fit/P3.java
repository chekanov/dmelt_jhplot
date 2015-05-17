package jhplot.fit;


import hep.aida.ref.function.AbstractIFunction;


/**
* Polynomial 3rd-order. 
* 
* @author S.Chekanov
**/


public class P3 extends AbstractIFunction {
    
    public P3() {
        this("P3");
    }
    
    public P3(String title) {
        super(title, 1, 4);
    }
    
    public P3(String[] variableNames, String[] parameterNames) {
        super(variableNames, parameterNames);
    }
    
    public double value(double[] v) {
        return p[0]+ p[1]*v[0] + p[2]*v[0]*v[0]+p[3]*v[0]*v[0]*v[0];
    }
    
    // Here change the parameter names
    protected void init(String title) {
        for (int i=0; i<parameterNames.length; i++) { 
            parameterNames[i] = "p"+i; 
        }
        
        super.init(title);
    }
}

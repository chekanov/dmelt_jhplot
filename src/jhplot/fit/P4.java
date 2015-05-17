package jhplot.fit;


import hep.aida.ref.function.AbstractIFunction;


/**
* Polynomial 4th-order. 
* 
* @author S.Chekanov
**/

public class P4 extends AbstractIFunction {
    
    public P4() {
        this("P4");
    }
    
    public P4(String title) {
        super(title, 1, 5);
    }
    
    public P4(String[] variableNames, String[] parameterNames) {
        super(variableNames, parameterNames);
    }
    
    public double value(double[] v) {
        return p[0]+ p[1]*v[0] + p[2]*v[0]*v[0]+p[3]*v[0]*v[0]*v[0]+p[4]*v[0]*v[0]*v[0]*v[0];
    }
    
    // Here change the parameter names
    protected void init(String title) {
        for (int i=0; i<parameterNames.length; i++) { 
            parameterNames[i] = "p"+i; 
        }
        
        super.init(title);
    }
}

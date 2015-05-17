package jhplot.fit;


import hep.aida.ref.function.AbstractIFunction;


/**
* Polynomial 6th-order. 
* @author S.Chekanov
**/

public class P6 extends AbstractIFunction {
    
    public P6() {
        this("P6");
    }
    
    public P6(String title) {
        super(title, 1, 7);
    }
    
    public P6(String[] variableNames, String[] parameterNames) {
        super(variableNames, parameterNames);
    }
    
    public double value(double[] v) {
        return p[0]+ 
               p[1]*v[0] + 
               p[2]*v[0]*v[0]+
               p[3]*v[0]*v[0]*v[0]+
               p[4]*v[0]*v[0]*v[0]*v[0]+
               p[5]*v[0]*v[0]*v[0]*v[0]*v[0]+
               p[6]*v[0]*v[0]*v[0]*v[0]*v[0]*v[0]; 
    }
    
    // Here change the parameter names
    protected void init(String title) {
        for (int i=0; i<parameterNames.length; i++) { 
            parameterNames[i] = "p"+i; 
        }
        
        super.init(title);
    }
}

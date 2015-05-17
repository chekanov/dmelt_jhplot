package jhplot.math;

import jasymca.Jasymca;
import jasymca.PlotGraph;
import jasymca.StringFmt;


import java.awt.Frame;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import jscl.math.Expression;
import jscl.text.ParseException;
import org.matheclipse.core.eval.*; 
import org.matheclipse.core.expression.F;

/**
 * Symbolic calculations. Select the symbolic engine, such as "jasymca" (default), "jscl" or "symja".
 * Read the manual for the description.
 * 
 * @author S.Chekanov
 *
 */


public class Symbolic {

    private EvalUtilities symja=null;
    private Jasymca j=null;
    private String engine="jasymca";
	
	/**
	 * Set engine for symbolic calculations. This can be either "jasymca", "jscl" or "symja" 
	 * 
	 * @param engine  engine type. Can be "jasymc", "jscl" or "symja".  
	 */
	public Symbolic(String engine) {
		
		this.engine=engine;
		if (engine.equalsIgnoreCase("jasymca")){ 
				j = new Jasymca();
		};
	        if (engine.equalsIgnoreCase("symja")){
                                F.initSymbols();
                                symja = new EvalUtilities();
                };
	
	
	}

	
	
	/**
	 * Initialize symbolic calculations using jasymca as the main engine.
	 */
	public Symbolic() {
		this("jasymca");
	}
	

	/**
	 * Return the name of the current engine.
	 * @return current engine.
	 */
	public String getEngineName(){
		return engine;
	}



        /**
         * Return the symbolic engine. 
         * @return Symbolic engine or null 
         */
        public Object getEngine(){
                if (symja != null) return symja;
                if (j != null) return j;
                return null; 
        }
	
	
	/**
	 * Evaluate an expression with the given engine.
	 * For "jasymca", use the standard Matlab/Octave mode.
	 * For "jscl", use jscl syntax.
	 * 
	 * @param str string for evaluation.
	 * @return
	 * @throws Exception
	 */
	public String eval(String str) throws Exception {
		
		
     if (engine.equalsIgnoreCase("jasymca")) {
		
    	 int n = str.length() - 1;
		if (n < 0 || "\n".equals(str.substring(n))) {
			exec(str);
			return str;
		} else
			return eval0(str);
		
     } else if   (engine.equalsIgnoreCase("jscl")) { 
    	  return simplify(expand(str));
     } else if   (engine.equalsIgnoreCase("symja")) {
           return (symja.evaluate(str)).toString();
      }
	
        return "";
     
	}

	/**
	 * Execute a set of expressions. This uses always  jasymca mode (Octave/Matlab) syntax.
	 * @param str string for evaluation
	 * @throws Exception
	 */
	public void exec(String str) throws Exception {
		InputStream is = new ByteArrayInputStream(str.getBytes("UTF-8"));//  new StringBufferInputStream(str);
		
		
		try {
			while (is.available() > 0) {
				List code = j.pars.compile(is, System.out);
				if (code == null)
					continue;
				j.proc.process_list(code, false);
				j.proc.printStack();
			}
		} catch (Exception e) {
			throw new Exception(e);
		}
	
	
	}

	private String eval0(String str) throws Exception {
		if (str.equals("plot"))
			return plot();
		else
			try {
				j.proc.process_list(j.pars.compile(str), false);
				j.proc.printStack();
				return StringFmt.compact(j.env.getValue("ans").toString());
			} catch (Exception e) {
				throw new Exception(e);
			}
	}

	
	 /**
     * Convert math equation to Java code
     * @param s input equation
     * @return result
     * @throws ParseException
     */
    public  String toJava(String s) throws ParseException {
        return Expression.valueOf(s).toJava();
    }

    
    /**
     * Convert an equation to MathML form.
     * @param s  input equation
     * @return result
     * @throws ParseException
     */
    public String toMathML(String s) throws ParseException {
        return Expression.valueOf(s).toMathML();
    }
	
	/**
	 * Plot
	 * @return
	 * @throws Exception
	 */
	private String plot() throws Exception {
		PlotGraph g = null;
		Frame f[] = Frame.getFrames();
		for (int i = 0; i < f.length; i++)
			if (f[i] instanceof PlotGraph)
				g = (PlotGraph) f[i];
		try {
			 return "print";
			// return SVG.print(g);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	
	
	
	
	/**
	 * Expanding a math formula. This uses "jscl" engine.
	 * @param s input equation
	 * @return math equation after evaluation
	 * @throws ParseException
	 */
    public  String expand(String s) throws ParseException {
        return Expression.valueOf(s).expand().toString();
    }

    /**
     * Factorize math formula. This uses "jscl" engine.
     * @param s input equation
     * @return after factorization.
     * @throws ParseException
     */
    public String factorize(String s) throws ParseException {
        return Expression.valueOf(s).factorize().toString();
    }

    /**
     * Rewrite an expression in term of elementary functions (log, exp, frac, sqrt, implicit roots).
     * This uses "jscl" engine.
     * @param s input equation
     * @return result
     * @throws ParseException
     */
    public String elementary(String s) throws ParseException {
        return Expression.valueOf(s).elementary().toString();
    }

     
    /**
     * Simplify equation. It is often useful to rewrite an expression in term of elementary functions, i.e
     * calling "elementary" first. This uses "jscl" engine.
     * @param s input equation
     * @return result
     * @throws ParseException
     */
    public String simplify(String s) throws ParseException {
        return Expression.valueOf(s).simplify().toString();
    }

    /**
     * Evaluate numerical values and perform some substitutions. This uses "jscl" engine.
     * @param s 
     * @return result
     * @throws ParseException
     */
    public String numeric(String s) throws ParseException {
        return Expression.valueOf(s).numeric().toString();
    }

    
    
    
    
    /**
     * Symbolic derivative. This uses "jscl" engine.
     * @param s 
     * @return result
     * @throws ParseException
     */
    public static String derivative(String s) throws ParseException {
        return Expression.valueOf(s).numeric().toString();
    }
	
	
	
	
	
	
	
	
	
	
	
	
	
}

package jhplot;

import units.*;
import java.io.*;
import java.nio.charset.Charset;
import java.util.Vector;

/**
 * Calculations using different measurement  and physics unit systems.
 * 
 * @author S.Chekanov
 */
public class UCalc {


      private MainJHPLOT munits;
      private String answer;


   /**
   *  Initialaze calculations with units.
   */
	public UCalc() {
            munits=new  MainJHPLOT();
	}


  /**
   *  Performs the computation with units.
   *  @param  havestr expression specifying the value to be converted.
   *  @param  toString string specifying unit of the desired result:
   *          expression, function name, unit list name, or unit list.
   *  @return answer as a string. If "error", then there was an error 
   */
        public String eval(final String havestr, final String wantstr) {
              Value have = Value.fromString(havestr);
              boolean ok=munits.convert(havestr, have,wantstr);
              if (ok) return munits.answer()+" "+munits.getExpectedUnits();
              return "error";
        }

     
   /**
   *  Returns answer as double. 
   *  @return anser as double value 
   */
        public double getValue(){
                return Double.parseDouble(munits.answer());
        }

   /**
   *  Returns expected unit. 
   *  @return expected unit. 
   */
        public String getUnit(){
              return munits.getExpectedUnits();
         }


  /**
   *  Show source. 
   *  @return source.. 
   */
        public void showSource(){
                     munits.showSource();
         }


  /**
   *  Shows units, functions, and aliases conformable to given Value.
   *  (Originally part of 'tryallunits'.)
   */
      public void showConformable(){munits.showConformable();}; 

  /**
   *  Shows units and functions with names containing the current unit. 
   *  (Originally part of 'tryallunits'.)
   */
     public void search(){munits.search();};  

    /**
   *  Shows units and functions with names containing a given substring.
   *  (Originally part of 'tryallunits'.)
   *  @param  name the name of the unit.
   */
     public void search(String name){munits.search(name);}; 

   /**
   *  If the argument is the name of a function or unit list,
   *  returns its definition. Otherwise returns null.
   *
   *  @param  name the name.
   *  @return definition or null.
   */
     public String showDef(String name){return munits.showDef(name);};  


   /**
   *  Show help topics in a window.
   */
    public void  help(){munits.help();};


	
}

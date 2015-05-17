// * This code is licensed under:
// * jeHEP License, Version 1.0
// * - for license details see http://jehep.sourceforge.net/license.html 
// *
// * Copyright (c) 2005 by S.Chekanov (chekanov@mail.desy.de). 
// * All rights reserved.
package fitter;
import java.util.*;

/**
 * Class representing a single function with tooltip
 * @author chekanov
 *
 */
public class  BFunc implements Comparable<BFunc>  {

        protected String name;
        protected String definition;
        protected String tip;
        protected String param;
        protected  int   dim;
/**
 * 
 * @param function name
 * @param function tooltip
 * 
 **/
       
        public BFunc(String name, String tip, String definition, String param, int dim) {
        	
        	if (name == null || tip == null)
            throw new NullPointerException();
        	
                this.name = name;
                this.tip=tip;
                this.definition=definition;
                this.param=param; 
                this.dim=dim; 
        }


        /**
         * Assume no definition 
         * */
        
        public BFunc(String name, String tip) {
        	
        	  this(name,tip,"undefined","undefined",1);
        	
                
        }


         public String getTip() {
                return tip;
        }

     

        public String getName() {
                return name;
        }


        /**
         *  get parameters 
         * */
        public String getParam() {
                return param;
        }

        /**
         *  get dimention 
         * */
        public int getDim() {
                return dim;
        }


        /**
         *  get definition
         * */
        public String getDefinition() {
                return definition;
        }



      public boolean equals(Object o) {
        if (!(o instanceof BFunc))
            return false;
        BFunc n = (BFunc)o;
        return n.name.equals(name) &&
               n.tip.equals(tip);
    }

    public int hashCode() {
        return 31*name.hashCode() + tip.hashCode();
    }

    public String toString() {
	return name + "  with TOOLtip" + tip;
    }

    public int compareTo(BFunc n) {
        int lastCmp = name.compareTo(n.name);
        return (lastCmp != 0 ? lastCmp :
                tip.compareTo(n.tip));
    }










}


// * This code is licensed under:
// * jeHEP License, Version 1.0
// * - for license details see http://jehep.sourceforge.net/license.html 
// *
// * Copyright (c) 2005 by S.Chekanov (chekanov@mail.desy.de). 
// * All rights reserved.
package fitter;


/**
 * Class representing a single function parameters 
 * @author chekanov
 *
 */
class  BMark {

        protected double min;
        protected double max;
        protected double c; 
        protected boolean fix;
        protected String name;
        protected int  cons;

        public BMark(String name, double min, double max, double c, boolean fix, int cons ) {
                this.name = name;
                this.min=min;
                this.max=max; 
                this.c=c;
                this.fix=fix;
                this.cons=cons;
        }


         public boolean getFix() {
                return fix;
        }

        public int getCons() {
                return cons;
        }


        public double getMin() {
                return min;
        }

       public double getMax() {
                return max;
        }

     public double getC() {
                return c;
        }


        public String getTitle() {
                return name;
        }

        public String toString() {
                return name;
        }
}


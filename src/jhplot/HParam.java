/**
*    Copyright (C)  DataMelt project. The jHPLot package by S.Chekanov and Work.ORG
*    All rights reserved.
*
*    This program is free software; you can redistribute it and/or modify it under the terms
*    of the GNU General Public License as published by the Free Software Foundation; either
*    version 3 of the License, or any later version.
*
*    This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
*    without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
*    See the GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License along with this program;
*    if not, see <http://www.gnu.org/licenses>.
*
*    Additional permission under GNU GPL version 3 section 7:
*    If you have received this program as a library with written permission from the DataMelt team,
*    you can link or combine this library with your non-GPL project to convey the resulting work.
*    In this case, this library should be considered as released under the terms of
*    GNU Lesser public license (see <https://www.gnu.org/licenses/lgpl.html>),
*    provided you include this license notice and a URL through which recipients can access the
*    Corresponding Source.
**/


package jhplot;


/**
 * Global setting for all DMelt jhplot class methods for mathematical calculations.
 * By default, DMelt uses Java Math library that provides fast elementary functions with e-14 accuracy.
 * If you set false to fast calculations,  the standard Java Math is used.  
 * They are usually about 2-3 times slower, but have higher accuracy. By default, fast calculations are enabled.
 * <p> </p>
 * If you need full Java accuracy, set setFastMath(False).
 *
 * @author S.Chekanov
 *
 */


public class HParam{

	private static boolean fastmath = true;

	/**
	     * 
	     * Set fast math calculation (3-5 times faster than Java Math). The typical precision is E-14 for fast calculations.
             * The default does not use fast calculations. 
	     * @param fast 
	     *   if false, all elementary functions are calculated with the standard Java Math precision. If true, fast calculation with the jafama FastJava  E-11 precision. 
	*/
	public static void setFastMath(boolean fast) {
		fastmath=fast;
	}


	/**
	     * 
	     * Check is fast math calculation enabled. 
	     * @return  is fast calculation enabled? 
	*/
	public static boolean isFastMath() {
		return fastmath;
	}


	/**
	    * 
	    * Set fast math calculation (4-5 times faster than Java Math). The typical precision is E-14 for fast calculations. 
	 */
	public static void setFastMath() {
		fastmath=true;
	}

       /**
            * 
            * Set exact math calculation using Java Math. 
         */
        public static void setExactMath() {
                fastmath=false;
        }



}

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

package jhplot.fit;

import hep.aida.ref.function.AbstractIFunction;



/**
* The function represents the Landau distribution.
* This class represents a Landau distribution, as 
* approximated by the Moyal formula
* \[ Moyal(\lambda) = \frac{\exp{-0.5(\lambda+\exp{-\lambda})}}{\sqrt{2\pi}} \]
* See J.E. Moyal, Theory of ionization fluctuations, Phil. Mag. 46 (1955) 263.
* Note that this analytical approximation is too low in the tail.
* In order to allow for a fit, we define
* \[ \lambda = \frac{x - m}{s} \]
* with x the dataset variable. 
* 
*  From Goddard GLAST ACD team (Fortran version)
*  
**/


public class Landau extends AbstractIFunction {
    
    public Landau() {
        this("Landau");
    }
    
    public Landau(String title) {
        super(title, 1, 3);
    }
    
    public Landau(String[] variableNames, String[] parameterNames) {
        super(variableNames, parameterNames);
    }
    
    public double value(double[] v) {
 

   double yy = ( v[0] - p[1] ) / p[2]; 
   double tt = Math.exp ( -0.5 * ( yy + Math.exp ( -1.0 * yy ) ) ) 
             / Math.sqrt ( 2.0 * Math.PI );
  return p[0] * tt;



    }
    
    // Here change the parameter names
    protected void init(String title) {

               

              parameterNames[0] =  "norm";
              parameterNames[1] =  "peak"; 
              parameterNames[2] =  "sigma";

 
/*
        for (int i=0; i<parameterNames.length; i++) { 
            parameterNames[i] = "pe"+i; 
        }
*/        
        super.init(title);
    }
}

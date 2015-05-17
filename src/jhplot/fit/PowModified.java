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
  * Power-law distribution with additional tail suppression. p0*pow(p1-x,p2)*(1+p3*(p1-x)) 
  * @author S.Chekanov
  *
**/

public class PowModified extends AbstractIFunction {
    
    public PowModified() {
        this("PowModified");
    }
    
    public PowModified(String title) {
        super(title, 1, 4);
    }
    
    public PowModified(String[] variableNames, String[] parameterNames) {
        super(variableNames, parameterNames);
    }
    
    public double value(double[] v) {

        double xx=p[1]-v[0];
        return p[0]*Math.pow(xx,p[2])*(1.0+p[3]*xx);
    }
   
 
    protected void init(String title) {
        for (int i=0; i<parameterNames.length; i++) { 
            parameterNames[i] = "p"+i; 
        }
        
        super.init(title);
    }
}

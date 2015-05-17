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
 * Exponential distribution. p0*exp(p1*(v0-p2));
 * 
 * @author S.Chekanov
 * 
 */

public class Exponent extends AbstractIFunction {

	public Exponent() {
		this("Exponent");
	}

	public Exponent(String title) {
		super(title, 1, 3);
	}

	public Exponent(String[] variableNames, String[] parameterNames) {
		super(variableNames, parameterNames);
	}

	public double value(double[] v) {
		return p[0] * Math.exp(p[1] * (v[0] - p[2]));
	}

	// Here change the parameter names
	protected void init(String title) {
		for (int i = 0; i < parameterNames.length; i++) {
			parameterNames[i] = "pe" + i;
		}

		super.init(title);
	}
}

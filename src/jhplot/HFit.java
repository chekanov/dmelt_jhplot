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

import javax.swing.*;

import jhplot.gui.HelpBrowser;
import fitter.Fitter;

/**
 * Main class for fit  data or histograms.
 * 
 * @author S.Chekanov
 * 
 */

public class HFit {

	private Fitter fit;
	private HPlot hplot = null;

	/**
	 * Show an empty fit frame.
	 * 
	 */

	public HFit() {
		this.hplot = null;
		fit = new Fitter(this.hplot, "c1");
		fit.showIt();
	}

	/**
	 * Show an empty fit frame and pass HPlot object.
	 * 
	 * @param hplot
	 *            HPlot canvas where fit results are plotted
	 * @param hplot_name
	 *            name of this canvas
	 */
	public HFit(HPlot hplot, String hplot_name) {
		this.hplot = hplot;
		fit = new Fitter(this.hplot, hplot_name);
		fit.showIt();
	}

	/**
	 * Show a fit frame for a selected histogram. For code generation, it
	 * assumes c1 and p1 names.
	 * 
	 * @param c1
	 *            HPlot canvas where fit results are plotted
	 * @param h1
	 *            input H1D histogram
	 */

	public HFit(HPlot c1, H1D h1) {

		this.hplot = c1;
		fit = new Fitter(this.hplot, "c1");
		if (h1 != null)
			fit.iniData(h1, "h1");
		fit.showIt();
	}

	/**
	 * Show a fit frame for a selected histogram. For code generation, it
	 * assumes c1 and p1 names.
	 * 
	 * @param c1
	 *            HPlot canvas where fit results are plotted
	 * @param p1
	 *            input P1D data
	 */

	public HFit(HPlot c1, P1D p1) {

		this.hplot = c1;
		fit = new Fitter(this.hplot, "c1");
		if (p1 != null)
			fit.iniData(p1, "p1");
		fit.showIt();
	}

	/**
	 * Show a fit frame for a selected histogram. This is the most convinient
	 * method, as the user pass only strings, and a reflection mechanism will
	 * try to detect the object. This is important if the user want to generate
	 * source code automatically.
	 * 
	 * @param hplot
	 *            HPlot canvas where fit results are plotted
	 * @param shplot
	 *            String representation of hplot HPlot canvas where fit results
	 *            are plotted
	 * @param ph
	 *            input P1D or H1D object
	 * @param sph
	 *            String representation of P1D or H1D object
	 */

	public HFit(HPlot hplot, String shplot, Object ph, String sph) {

		this.hplot = hplot;
		fit = new Fitter(hplot, shplot);

		// for P1D
		if (ph instanceof P1D) {
			fit.iniData((P1D) ph, sph);
			fit.showIt();
			return;
		} else if (ph instanceof H1D) {
			fit.iniData((H1D) ph, sph);
			fit.showIt();
			return;
		} else {
			ErrorMessage("Unknown object for fit. Only P1D or H1D classes are allowed");
		}

	}

	

	
	/**
	 * Add a user 1D function in form of a string. Use the standard JAIDA string
	 * to define the function. Since the function is 1D, use x[0] instead of x,
	 * and define the parameters as "a,b,c".
	 * 
	 * @param name
	 *            Short name
	 * @param tooltip
	 *            Tool tip to be shown
	 * @param definition
	 *            representing the user function using JAIDA
	 * @param param
	 *            representing parameter names (use comma to separate)
	 */

	public void addFunc(String name, String tooltip, String definition,
			String param) {
		fit.addFunc(name, tooltip, definition, param, 1);
	}

	/**
	 * Load a histogram for a fit
	 * 
	 * @param h1
	 *            input H1D histogram
	 * @param h1_name
	 *            name of this object in the code
	 */

	public void load(H1D h1, String h1_name) {
		if (fit != null && h1 != null)
			fit.iniData(h1, h1_name);
	}

	/**
	 * Load data for a fit
	 * 
	 * @param p1
	 *            input p1D data
	 * @param p1_name
	 *            name of this object in the code
	 */

	public void load(P1D p1, String p1_name) {
		if (fit != null && p1 != null)
			fit.iniData(p1, p1_name);
	}

	/*
	 * Message
	 */

	private void ErrorMessage(String a) {

		JOptionPane dialogError = new JOptionPane();
		JOptionPane.showMessageDialog(dialogError, a, "Error",
				JOptionPane.ERROR_MESSAGE);
	}



	/**
	    * Show online documentation.
	    */
	      public void doc() {
	        	 
	    	  String a=this.getClass().getName();
	    	  a=a.replace(".", "/")+".html"; 
			  new HelpBrowser(  HelpBrowser.JHPLOT_HTTP+a);
	    	 
			  
			  
	      }





}

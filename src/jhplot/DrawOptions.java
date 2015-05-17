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

import java.awt.*;
import java.io.PrintStream;

import jplot.*;
import java.io.Serializable;

/**
 * Main class which sets graphic attributes for all jHPlot classes (histograms
 * and data holders).
 * 
 * @author S.Chekanov
 * 
 */

public class DrawOptions extends Plottable implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected LinePars lpp;

	protected Font FontTitle;

	protected int lineStyle;

	protected Font FontAxis;
	
	protected String symbolshape="Circle";

	protected String title = "";

	/**
	 * Sets drawing options for all objects.
	 * 
	 */
	public DrawOptions() {

		lpp = new LinePars();
		lpp.setColor(Color.black);
		lpp.setPenWidth(2.0f);
		lpp.setGraphStyle(1);
		lpp.setDrawLegend(true);
		lpp.setColorErrorsY(Color.black);
		lpp.setColorErrorsX(Color.black);
		lpp.errorsY(true);
		lpp.errorsX(false);
		lpp.setDashLength(0);
		lineStyle = 0;
		symbolshape="Circle";

	}

	/**
	 * Sets the title.
	 * 
	 * @param title
	 *            Title
	 */
	public void setTitle(String title) {
		this.title = title;
		lpp.setName(title);
	}

	/**
	 * Copy drawing options.
	 * 
	 * @param old
	 *            LinePars to be copied
	 */
	public LinePars copyLinePars(LinePars old) {

		LinePars lnew = new LinePars();
		lnew.copy(old);
		return lnew;
	}

	/**
	 * Get the title of an object.
	 * 
	 * @return Title of an object
	 */
	public String getTitle() {
		String s = lpp.getName();
		if (s != null && s.length() > 0)
			return s;
		return title;
	}

	/**
	 * Set the name for X-axis.
	 * 
	 * @param name
	 *            Name for X axis
	 */
	public void setNameX(String name) {
		lpp.setNameX(name);
	}

	/**
	 * Get the name of X-axis
	 * 
	 * @return Name of the lable in X
	 */
	public String getNameX() {
		return lpp.getNameX();

	}

	/**
	 * Sets the name for Y-axis.
	 * 
	 * @param name
	 *            text for the title of X-axis
	 */
	public void setNameY(String name) {
		lpp.setNameY(name);
	}

	/**
	 * Get the title of Y-axis.
	 * 
	 * @return text of the Y-title.
	 */
	public String getNameY() {
		return lpp.getNameY();

	}

	/**
	 * Sets the title for Z-axis
	 * 
	 * @param name
	 *            Title of Z-axis
	 */
	public void setNameZ(String name) {
		lpp.setNameZ(name);
	}

	/**
	 * Get the title of Z-axis
	 * 
	 * @return Title of the Z-axis
	 */

	public String getNameZ() {
		return lpp.getNameZ();

	}

	/**
	 * Get the drawing attributes
	 * 
	 * @return LinePars object
	 */
	public LinePars getDrawOption() {

		return this.lpp;

	}

	/**
	 * Sets drawing attributes.
	 * 
	 * @param lp
	 *            LinePars attributes
	 */
	public void setDrawOption(LinePars lp) {

		this.lpp = lp;

	}

	/**
	 * Sets whether or not this line style should draw the name in the legend of
	 * the graph.
	 * 
	 * @param b
	 *            true if the name should be shown
	 */
	public void setLegend(boolean b) {
		this.lpp.setDrawLegend(b);

	}

	/**
	 * Returns true if this line style should draw the name in the legend of the
	 * graph, false otherwise
	 * 
	 * @return true if the name should be shown
	 */
	public boolean getLegend() {
		return this.lpp.drawLegend();
	}

	/**
	 * Sets the style.
	 * 
	 * @param type
	 *            string representing the style. <br>
	 *            "l" : line style<br>
	 *            "p": symbol type <br>
	 *            "h": histogram style <br>
	 *            "lp" or "pl": lines connects symbols
	 */
	public void setStyle(String type) {
		if (type.equalsIgnoreCase("l")) {
			lpp.setGraphStyle(0);
			lpp.setDrawLine(true);
			lpp.setDrawSymbol(false);
			return;
		} else if (type.equalsIgnoreCase("p")) {
			lpp.setGraphStyle(0);
			lpp.setDrawLine(false);
			lpp.setSymbol(4);
			lpp.setDrawSymbol(true);
			return;
		} else if (type.equalsIgnoreCase("h")) {
			lpp.setGraphStyle(1);
			lpp.setDrawLine(true);
			lpp.setDrawSymbol(false);
			return;
		} else if (type.equalsIgnoreCase("pl") || type.equalsIgnoreCase("lp")) {
			lpp.setGraphStyle(0);
			lpp.setDrawLine(true);
			lpp.setSymbol(4);
			lpp.setDrawSymbol(true);
			return;
		}
		jhplot.utils.Util.ErrorMessage("Option =" + type + " is not defined");

	}

	/**
	 * Fill or not histogram or function.
	 * 
	 * @param cfill
	 *            true if a drawing object should be filled
	 */
	public void setFill(boolean cfill) {
		this.lpp.fill(cfill);
	}

	/**
	 * Fill or not histogram or function.
	 * 
	 * @param cfill
	 *            true if a drawing object should be filled
	 */
	public boolean isFilled() {
		return this.lpp.fill();
	}

	/**
	 * Draw a histogram using bars (default is using lines). This means that
	 * vertical lines will be shown.
	 * 
	 * @param cfill
	 *            true if a histogram is shown using bar.
	 */
	public void setBars(boolean cfill) {
		this.lpp.setShowBars(cfill);
	}

	/**
	 * Return true if histogram is shown using bars.
	 */
	public boolean isBars() {
		return this.lpp.isBarShown();
	}

	/**
	 * Fill color of a drawing object.
	 * 
	 * @param color
	 *            Color to be used to fill a drawing object
	 */

	public void setFillColor(Color color) {
		this.lpp.setFillColor(color);
	}

	
	/**
	 * Get color of a drawing object.
	 * 
	 * @return  get fill color.
	 *            
	 */

	public Color getFillColor() {
		return this.lpp.getFillColor();
	}
	
	
	/**
	 * Set color transparency used to fill an object.
	 * 
	 * @param ff
	 *            transparency, from 0 (transparent) to 1 (not transparent)
	 */
	public void setFillColorTransparency(double ff) {
		this.lpp.setFillColorTransparency((float) ff);
	}

	/**
	 * Show or not the 1st level errors for Y values.
	 * 
	 * @param sho
	 *            if true, should be shown
	 */
	public void setErrY(boolean sho) {
		this.lpp.errorsY(sho);
	}

	/**
	 * Show or not the 2nd level errors for Y values
	 * 
	 * @param sho
	 *            if true, should be shown
	 */
	public void setErrSysY(boolean sho) {
		this.lpp.errorsSysY(sho);
	}

	/**
	 * Show or not the 2nd level errors for X and Y values
	 * 
	 * @param sho
	 *            if true, should be shown
	 */
	public void setErrSys(boolean sho) {
		this.lpp.errorsSysX(sho);
		this.lpp.errorsSysY(sho);
	}

	/**
	 * Set or not the 1st level errors in X
	 * 
	 * @param sho
	 *            if true, should be shown
	 */

	public void setErrX(boolean sho) {
		this.lpp.errorsX(sho);
	}

	/**
	 * Set or not the 1st level errors on X and Y
	 * 
	 * @param sho
	 *            if true, should be shown
	 */

	public void setErr(boolean sho) {
		this.lpp.errorsX(sho);
		this.lpp.errorsY(sho);
	}

	/**
	 * is error shown on X?
	 * 
	 * @return true if shown
	 */

	public boolean isErrX() {
		return lpp.getErrorsX();
	}

	/**
	 * is error shown on Y?
	 * 
	 * @return true if shown
	 */

	public boolean isErrY() {
		return lpp.getErrorsY();
	}

	/**
	 * Set or not the 2nd level errors in X
	 * 
	 * @param sho
	 *            if true, should be shown
	 */

	public void setErrSysX(boolean sho) {
		this.lpp.errorsSysX(sho);
	}

	/**
	 * Set color for 1st level errors in Y
	 * 
	 * @param color
	 *            Color
	 */

	public void setErrColorY(Color color) {
		this.lpp.setColorErrorsY(color);

	}

	/**
	 * Set color for 1st level errors in X
	 * 
	 * @param color
	 *            Color
	 */
	public void setErrColorX(Color color) {
		this.lpp.setColorErrorsX(color);
	}

	/**
	 * Set color for 1st level errors for X and Y
	 * 
	 * @param color
	 *            Color
	 */
	public void setErrColor(Color color) {
		this.lpp.setColorErrorsX(color);
		this.lpp.setColorErrorsY(color);
	}

	/**
	 * Set color for 1st level errors for X and Y
	 * 
	 * @param color
	 *            Color
	 * @param transperency
	 *            Transparency level (0-1)
	 */
	public void setErrColor(Color color, double transperency) {
		this.lpp.setColorErrorsX(color);
		this.lpp.setColorErrorsY(color);
		this.lpp.setFillColorTransparency((float) transperency);

	}

	/**
	 * Set symbol types. Look at the definitions.
	 * 
	 * @param shape <br>
	 *            0 : not filled circle <br>
	 *            1: not filled square <br>
	 *            2: not filled diamond <br>
	 *            3: not filled triangle <br>
	 *            4: filled circle <br>
	 *            5: filled square <br>
	 *            6: filed diamond <br>
	 *            7: filled triangle <br>
	 *            8: plus (+) <br>
	 *            9: cross as (x) <br>
	 *            10: star (*) <br>
	 *            11: small dot (.) <br>
	 *            12: bold plus (+) <br>
	 * 
	 */
	public void setSymbol(int shape) {
		this.lpp.setSymbol(shape);
		if (shape==0) symbolshape="Circle";
		else if (shape==1) symbolshape="Square";
		else if (shape==2) symbolshape="Diamond";
		else if (shape==12) symbolshape="+";
		else if (shape==10) symbolshape="*";
		else if (shape==9) symbolshape="x";
		else if (shape==11) symbolshape="Dot";
		else symbolshape="Circle";
	}

	
	/**
	 * Set symbol types using a string. It is more human readable, but not all
	 * options are supported. 
	 * 
	 * @param shape that takes this value:<p>
	 *            "Circle" or "o": not filled circle <br>
	 *            "Dot" or ".": dot <br>
	 *            "Square": not filled square <br>
	 *            "Diamond": not filled diamond <br>
	 *            "Triangle": not filled triangle <br>
	 *            "+": plus (+) <br>
	 *            "x": cross as (x) <br>
	 *            "*": star (*) <br>
	 * 
	 */
	public void setSymbol(String shape) {
		shape=shape.trim();
		shape=shape.toLowerCase();
		this.symbolshape=shape;
		
		switch (shape) {
        case "+":
        	lpp.setSymbol(12);
            return;
        case "Dot":
        	lpp.setSymbol(4);
        	return;
        case "Circle":
        	lpp.setSymbol(0);
            return;
        case "o":
        	lpp.setSymbol(0);
            return;   
        case "t":
        	lpp.setSymbol(3);
            return;   
        case "*":
        	lpp.setSymbol(10);
            return;
        case "x":
        	lpp.setSymbol(9);
            return;
        case "square":
        	lpp.setSymbol(1);
        	return;
        case "s":
        	lpp.setSymbol(1);
        	return;
        case "dot":
        	lpp.setSymbol(11);
        	return;	
        case "diamond":
        	lpp.setSymbol(2);
        	return;
        case "d":
        	lpp.setSymbol(2);
        	return;
        
		}
       
	}

	
	/**
	 * Return symbol shape as a string.
	 * @return symbol shape.
	 */
	public String getSymbolShape() {
		return this.symbolshape;
		
	}
	
	
	
	
	
	
	
	
	/**
	 * Set line styles (important for functions).
	 * 
	 * @param style
	 *            0 - solid; 1- dashed; 2-dot-dashed line; 3: dotted
	 **/
	public void setLineStyle(int style) {
		this.lineStyle = style;
		if (style == 1)
			lpp.setDashLength(10);
		if (style == 2)
			lpp.setDashLength(5);
		if (style == 3)
			lpp.setDashLength(3);
	}

	/**
	 * Get line style. 0 - solid; 1- dashed; 2-dot-dashed line; 3: dotted 
	 * 
	 * @return style of line.
	 */
	public int getLineStyle() {
		return lineStyle;
	}

	/**
	 * 
	 * Get current symbol.
	 * 
	 * @return get symbol type
	 * */
	public int getSymbol() {
		return this.lpp.getSymbol();
	}

	/**
	 * Set color for lines
	 * 
	 * @param color
	 *            Color of lines.
	 */
	public void setColor(Color color) {
		lpp.setColor(color);
	}

	/**
	 * Get drawing color.
	 * 
	 * @return drawing color
	 * */
	public Color getColor() {
		return lpp.getColor();
	}

	/**
	 * Set width of the lines
	 * 
	 * @param w
	 *            Width of the lines
	 */
	public void setPenWidth(int w) {
		lpp.setPenWidth((float) w);
	}

	/**
	 * Return the width of the lines.
	 * 
	 * @return pen width
	 */
	public float getPenWidth() {
		return lpp.getPenWidth();
	}

	/**
	 * Set dashed style with default length
	 * 
	 * 
	 */
	public void setPenDash() {
		lpp.setDashLength(10);
	}

	/**
	 * Set dashed style with default length
	 * 
	 * @param lenght
	 *            dash length
	 */
	public void setPenDash(int lenght) {
		lpp.setDashLength(lenght);
	}

	/**
	 * Set type of the object Should be: LinePars.H1D, LineParsF1D, LineParsF1D
	 * 
	 * @param type
	 */
	public void setType(int type) {
		lpp.setType(type);
	}

	/**
	 * Get the type of the object.
	 * @return object type.
	 * 
	 */
	public int getType() {
		return lpp.getType();
	}

	/**
	 * Set width of the lines used to show 2nd level errors
	 * 
	 * @param w
	 *            width of lines
	 */
	public void setPenWidthErrSys(int w) {
		lpp.setPenWidthErrSys((float) w);
	}

	/**
	 * Set width of the lines used to show 1st level errors
	 * 
	 * @param w
	 *            width of lines
	 */
	public void setPenWidthErr(int w) {
		lpp.setPenWidthErr((float) w);
	}

	/**
	 * Show or not all errors (1st and 2nd level). Errors are shown using lines.
	 * You can also fill area between errors using fill methods
	 * 
	 * @param w
	 *            true, if errors are shown
	 */
	public void setErrAll(boolean w) {
		lpp.errorsSysY(w);
		lpp.errorsSysX(w);
		lpp.errorsY(w);
		lpp.errorsX(w);
	}

	/**
	 * Fill area between lower and upper 1st-level errors
	 * 
	 * @param w
	 *            true, if area is filled
	 */
	public void setErrFill(boolean w) {
		lpp.errorsFill(w);
	}

	/**
	 * Fill color area between lower and upper 1st-level errors.
	 * 
	 * @param c
	 *            what color to use
	 */
	public void setErrFillColor(Color c) {
		lpp.errorsFill(true);
		lpp.setErrorFillColorTransp(0.5f);
		lpp.setColorErrorsFill(c);
	}

	/**
	 * Fill color area between lower and upper 1st-level errors and set
	 * transparency level
	 * 
	 * @param c
	 *            what color to use
	 * @param transp
	 *            transparency level (between 0 and 1)
	 */
	public void setErrFillColor(Color c, double transp) {
		lpp.errorsFill(true);
		lpp.setColorErrorsFill(c);
		if (transp >= 0 && transp <= 1)
			lpp.setErrorFillColorTransp((float) transp);

	}

	/**
	 * Fill color area between lower and upper 1st-level errors
	 * 
	 * @param c
	 *            color to be used
	 */
	public void setErrSysFillColor(Color c) {
		lpp.errorsFillSys(true);
		lpp.setErrorFillColorTranspSys(0.5f);
		lpp.setColorErrorsFillSys(c);
	}

	/**
	 * Fill color area between lower and upper 2nd-level errors and set
	 * transparency level
	 * 
	 * @param c
	 *            what color to use
	 * @param transp
	 *            transparency level (between 0 and 1)
	 */
	public void setErrSysFillColor(Color c, double transp) {
		lpp.errorsFillSys(true);
		lpp.setColorErrorsFillSys(c);
		if (transp >= 0 && transp <= 1)
			lpp.setErrorFillColorTranspSys((float) transp);

	}

	/**
	 * Fill area between lower and upper 2st-level errors
	 * 
	 * @param w
	 *            true, if area is filled
	 */
	public void setErrSysFill(boolean w) {
		lpp.errorsFillSys(w);
	}

	/**
	 * Set the size of small tics shown at the end of error bars.
	 * 
	 * @param size
	 *            size of the small tic at ends of error bars
	 */
	public void setErrTicSize(double w) {
		lpp.setErrTicSize((float) w);
	}

	/**
	 * Set the size of the symbols
	 * 
	 * @param w
	 *            Size of the symbols
	 */
	public void setSymbolSize(int w) {
		lpp.setSymbolSize((float) w);
	}

	/**
	 * Get the size of the symbols
	 * 
	 */
	public double getSymbolSize() {
		return (double) lpp.getSymbolSize();
	}

	/**
	 * Set the style of the graph
	 * 
	 * 
	 * @param w
	 *            if 0: lines or points if 1: histograms
	 */
	public void setGraphStyle(int w) {
		lpp.setGraphStyle(w);
	}

	/**
	 * Draw or not symbols
	 * 
	 * @param w
	 *            if true, should be shown
	 */
	public void setDrawSymbol(boolean w) {
		lpp.setDrawSymbol(w);
	}

	/**
	 * Draw histogram key with line or not
	 * 
	 * @param w
	 *            set to false if no histogram key in form of line is shown
	 **/
	public void setDrawLineKey(boolean w) {
		lpp.setDrawLine(w);
	}

	/**
	 * Draw lines connecting points or not.
	 * 
	 * @param w
	 *            true, if are drawn
	 */
	public void setDrawLine(boolean w) {
		lpp.setDrawLine(w);
	}

	/**
	 * Set the bin width. Not used at this moment
	 * 
	 * @param w
	 *            Bin width
	 */
	public void setBinWidth(double w) {
		lpp.setWidthBin(w);

	}

	/**
	 * get the bin width. Not used at this moment
	 * 
	 * @return Bin width
	 */
	public double getBinWidth() {
		return lpp.getWidthBin();

	}

	/**
	 * Get all drawing parameters.
	 * 
	 * @return drawing parameters
	 */
	public LinePars getLineParm() {
		return lpp;

	}

	/**
	 * Get all drawing options in form of a string
	 * 
	 * @return string representing drawing options
	 */
	public String getDrawOptions() {
		return lpp.getAttributes();

	}

	/**
	 * print all drawing options
	 */
	public void printDrawOptions() {
		lpp.print();

	}

	/**
	 * Get all drawing options as PrintStream
	 * 
	 * @param out
	 *            input PrintStream
	 */
	public void printDrawOptions(PrintStream out) {
		lpp.print(out);

	}

}

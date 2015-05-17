/**
 *    Copyright (C)  DataMelt project. The jHPLot package.
 *    Includes coding developed for Centre d'Informatique Geologique
 *    by J.V.Lee priory 2000 GNU license.
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
 *    @author: J.V.Lee and S.Chekanov
 **/

package jplot;

import java.awt.*;
import java.io.PrintStream;
import java.io.Serializable;

/**
 * Contains all the data needed to plot a series of points, connected or not by
 * a line and attributes such as color, dash length, point type and more.
 */
public class LinePars implements Serializable {

	private static final long serialVersionUID = 1L;

	// name of the dataset which uses these line parameters:
	String name;

	// name of the axis, if used
	String nameX, nameY, nameZ;

	// different graph styles are defined here:
	static public final int LINES = 0;
	static public final int HISTO = 1;
	static public final int CONTOUR = 2;

	// histogram types
	static public final int H1D = 101;
	static public final int F1D = 102;
	static public final int P1D = 103;
	static public final int H2D = 201;
	static public final int H3D = 301;

	public static final int STICKS = 2;

	// there are only 12 symbols, 13 means no symbol drawing
	static final int NO_SYMBOL = 13;

	// general:
	protected Color color, startColor, endColor, fillColor;
	protected int redInc, greenInc, blueInc;
	protected boolean slideColor;
	protected boolean fillArea;
	protected float fillColorTransparency;
	protected double widthBin; // histogram width, in case of histogram
	protected float penWidthErrSys;
	protected float penWidthErr;
	protected float errTicSize;
	protected int type;
	protected boolean fillBars;

	// points:
	protected int symbol;
	protected int every;
	protected float symbolSize;
	protected boolean useSymbol;
	protected float multiplier;
	protected float additioner;
	protected boolean errorsShowY;
	protected boolean errorsShowX;
	protected boolean errorsShowSysY;
	protected boolean errorsShowSysX;

	protected Color errorsColorY, errorsColorX;
	protected Color errorsColorSysY, errorsColorSysX;

	// for filling the errors
	protected boolean errorsFill;
	protected boolean errorsFillSys;
	protected Color errorsFillColor;
	protected Color errorsFillColorSys;
	protected float errorsFillColorTransp;
	protected float errorsFillColorTranspSys;

	// lines:
	protected int colorIndex;
	protected int dashIndex;
	protected float dashLength;
	protected float penWidth;
	protected boolean plotLine;

	// styles:
	protected int style;
	protected boolean showLegend;
	protected boolean dataModified;

	// private final String lf = System.getProperty("line.separator");

	/**
	 * Constructor, builds the class from default values.
	 * 
	 * @param s
	 *            name of this dataset
	 */
	public LinePars(String s) {
		colorIndex = 0;
		dashIndex = 0;
		fillBars = false;
		color = GraphSettings.color[0];
		type = H1D;
		fillColor = new Color(255, 255, 205);
		fillColorTransparency = 0.4f;
		fillArea = false;
		dashLength = 0.0f;
		penWidth = 1.0f;
		errTicSize = penWidth * 2;
		penWidthErrSys = penWidth;
		penWidthErr = penWidth;
		symbol = GraphSettings.NO_SYMBOL;
		symbolSize = 5.0f;
		useSymbol = false;
		plotLine = true;
		every = 1;
		style = LINES;
		showLegend = true;
		widthBin = 0;
		name = s;
		nameX = "X";
		nameY = "Y";
		nameZ = "Z";
		errorsShowY = true;
		errorsShowX = false;
		errorsShowSysY = false;
		errorsShowSysX = false;
		errorsColorY = Color.black;
		errorsColorX = Color.black;
		errorsColorSysY = Color.black;
		errorsColorSysX = Color.black;
		multiplier = 1.0f;
		additioner = 0.0f;
		slideColor = false;
		startColor = color;
		endColor = color;
		redInc = greenInc = blueInc = 0;
		dataModified = false;

		errorsFill = false;
		errorsFillSys = false;
		errorsFillColor = Color.green;
		errorsFillColorSys = Color.yellow;
		errorsFillColorTransp = 0.5f;
		errorsFillColorTranspSys = 0.5f;

	}

	/**
	 * Constructor, builds the class with a specific color
	 * 
	 * @param colorIndex
	 *            index pointing to a predefined (in 'ColorPanel') color.
	 */
	public LinePars(String s, int colorIndex) {
		this(s);
		this.colorIndex = colorIndex;
		color = GraphSettings.color[colorIndex];
	}

	/**
	 * Constructor, builds the class from specified values.
	 * 
	 * @param c
	 *            color
	 * @param dl
	 *            dash width of the line style (1.0f means no dash)
	 * @param pw
	 *            pen width (0 means no line)
	 * @param s
	 *            symbol (-1 means no symbols)
	 * @param ss
	 *            symbol size (0 means no symbols)
	 */
	public LinePars(Color c, float dl, float pw, int s, float ss) {
		this("");
		color = c;
		colorIndex = -1;
		dashLength = dl;
		penWidth = pw;
		penWidthErr = pw;
		penWidthErrSys = pw;
		symbol = s;
		symbolSize = ss;
		style = LINES;
		type = H1D;
	}

	/**
	 * Constructor, builds the class from default values.
	 */
	public LinePars() {
		this("");
	}

	/**
	 * Constructor, builds the class from specified values.
	 * 
	 * @param lp
	 *            LinePars class instance
	 */
	public LinePars(LinePars lp) {
		copy(lp);
	}

	/**
	 * Constructor, builds the class from default values, except for an initial
	 * color.
	 * 
	 * @param c
	 *            initial color.
	 */
	public LinePars(String s, Color c) {
		this(s);
		color = c;
	}

	
	/**
	 * Returns the frequency used to draw points in terms of an 'every'
	 * parameter.
	 * 
	 * @return the point frequency (1=every point, 2=every second point...)
	 */
	public int getPointFrequency() {
		return every;
	}

	/**
	 * Returns the current graph style.
	 * 
	 * @return the current graph style.
	 */
	public int getGraphStyle() {
		return style;
	}

	/**
	 * Sets the graph style. The integer is an symbolic constant, must be one of
	 * the constants defined in the GraphSettings class (with values such as
	 * LINES, HISTO, etc.).
	 * 
	 * @param s
	 *            line style (symbolic constant)
	 */
	public void setGraphStyle(int s) {
		style = s;
	}

	
	/**
	 * Copy operator, builds the class from specified values.
	 * 
	 * @param lp
	 *            LinePars class instance
	 */
	public void copy(LinePars lp) {
		color = lp.color;
		colorIndex = lp.colorIndex;
		fillColor = lp.fillColor;
		fillArea = lp.fillArea;
		fillColorTransparency = lp.fillColorTransparency;
		dashIndex = lp.dashIndex;
		dashLength = lp.dashLength;
		type = lp.type;
		penWidth = lp.penWidth;
		penWidthErrSys = lp.penWidthErrSys;
		errTicSize = lp.errTicSize;
		penWidthErr = lp.penWidthErr;
		symbol = lp.symbol;
		symbolSize = lp.symbolSize;
		useSymbol = lp.useSymbol;
		plotLine = lp.plotLine;
		style = lp.style;
		every = lp.every;
		name = lp.name;
		nameX = lp.nameX;
		nameY = lp.nameY;
		nameZ = lp.nameZ;
		errorsShowY = lp.errorsShowY;
		errorsShowX = lp.errorsShowX;
		errorsShowSysY = lp.errorsShowSysY;
		errorsShowSysX = lp.errorsShowSysX;
		errorsColorY = lp.errorsColorY;
		errorsColorX = lp.errorsColorX;
		errorsColorSysY = lp.errorsColorSysY;
		errorsColorSysX = lp.errorsColorSysX;
		showLegend = lp.showLegend;
		multiplier = lp.multiplier;
		additioner = lp.additioner;
		redInc = lp.redInc;
		greenInc = lp.greenInc;
		blueInc = lp.blueInc;
		slideColor = lp.slideColor;
		startColor = lp.startColor;
		endColor = lp.endColor;
		dataModified = lp.dataModified;
		errorsFill = lp.errorsFill;
		errorsFillSys = lp.errorsFillSys;
		errorsFillColor = lp.errorsFillColor;
		errorsFillColorSys = lp.errorsFillColorSys;
		errorsFillColorTransp = lp.errorsFillColorTransp;
		errorsFillColorTranspSys = lp.errorsFillColorTranspSys;
		fillBars = lp.fillBars;
	}

	/**
	 * Print the current parameter settings.
	 */
	public void print() {

		PrintStream out = System.out;
		print(out);
	}

	/**
	 * Print the current parameter settings.
	 * 
	 * @param out
	 *            input PrintStream
	 */
	public void print(PrintStream out) {

		out.println(getAttributes());
	}

	/**
	 * Get graphical attributes in form of a string
	 */
	public String getAttributes() {

		String tmp = "\n Drawing attributes";
		tmp = tmp + "\n- type         :" + type;
		tmp = tmp + "\n- style        :" + style;
		tmp = tmp + "\n- color        :" + color;
		tmp = tmp + "\n- fillColor    :" + fillColor;
		tmp = tmp + "\n- errors on X  :" + errorsShowX;
		tmp = tmp + "\n- errors on Y  :" + errorsShowY;
		tmp = tmp + "\n- error color X  :" + errorsColorX;
		tmp = tmp + "\n- show errors Y  :" + errorsColorY;

		tmp = tmp + "\n---- Line attributes ---";
		tmp = tmp + "\n- show      :" + plotLine;
		tmp = tmp + "\n- dash      :" + dashIndex;
		tmp = tmp + "\n- pen width :" + penWidth;

		tmp = tmp + "\n---- Point attributes-----";

		tmp = tmp + "\n-  show  :" + String.valueOf(useSymbol);
		tmp = tmp + "\n-  symbol:" + String.valueOf(symbol);
		tmp = tmp + "\n-  size  :" + String.valueOf(symbolSize);
		tmp = tmp + "\n-  every :" + String.valueOf(every);

		tmp = tmp + "\n---- Legend attributes-----";
		tmp = tmp + "\n- showLegend  :" + showLegend;
		tmp = tmp + "\n- text        :" + name;
		return tmp;

	}

	/**
	 * Set the name of this dataset.
	 * 
	 * @param s
	 *            string which contains the name of the dataset
	 */
	public void setName(String s) {
		name = s;
	}

	/**
	 * Returns the name (legend) of this line style
	 * 
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name of X axis
	 * 
	 * @param s
	 *            string which contains the name of the dataset
	 */
	public void setNameX(String s) {
		nameX = s;
	}

	/**
	 * Returns the name of the X axis
	 * 
	 * @return name
	 */
	public String getNameX() {
		return nameX;
	}

	/**
	 * Returns the histogram bin width, in case of histogram
	 * 
	 * @return name
	 */
	public double getWidthBin() {
		return widthBin;
	}

	/**
	 * Set the histogram bin width, in case of histogram
	 */
	public void setWidthBin(double a) {
		widthBin = a;
	}

	/**
	 * Show histograms using bars
	 */
	public void setShowBars(boolean a) {
		fillBars = a;
	}

	public boolean isBarShown() {
		return fillBars;
	}

	/**
	 * Set the name of Y axis
	 * 
	 * @param s
	 *            string which contains the name of the dataset
	 */
	public void setNameY(String s) {
		nameY = s;
	}

	/**
	 * Set the type of the data
	 * 
	 * @param s
	 *            s
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * Get the type of the data
	 * 
	 * @param s
	 *            s
	 */
	public int getType() {
		return type;
	}

	/**
	 * Returns the name of the Y axis
	 * 
	 * @return name
	 */
	public String getNameY() {
		return nameY;
	}

	/**
	 * Set the name of Z axis
	 * 
	 * @param s
	 *            string which contains the name of the dataset
	 */
	public void setNameZ(String s) {
		nameZ = s;
	}

	/**
	 * Returns the name of the Z axis
	 * 
	 * @return name
	 */
	public String getNameZ() {
		return nameZ;
	}

	/**
	 * Returns true if this linestyle should draw the name in the legend of the
	 * graph, false otherwise
	 * 
	 * @return true if the name should be shown
	 */
	public boolean drawLegend() {
		return showLegend;
	}

	/**
	 * Sets whether or not this linestyle should draw the name in the legend of
	 * the graph.
	 * 
	 * @param b
	 *            boolean, true if the name should be shown
	 */
	public void setDrawLegend(boolean b) {
		showLegend = b;
	}

	/**
	 * Returns the color of the pen.
	 * 
	 * @return color
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * Returns the start color of the pen. Only useful if the sliding color
	 * option is on.
	 * 
	 * @return start color
	 */
	public Color getStartColor() {
		return startColor;
	}

	/**
	 * Returns the end color of the pen. Only useful if the sliding color option
	 * is on.
	 * 
	 * @return end color
	 */
	public Color getEndColor() {
		return endColor;
	}

	/**
	 * Enabled and initializes the sliding color option. After calling this
	 * function, the line color is progressively changed towards the end color
	 * through successive calls to 'nextColor'.
	 * 
	 * @param start
	 *            start color
	 * @param end
	 *            end color
	 */
	public void setSlideColor(Color start, Color end) {
		color = start;
		startColor = start;
		endColor = end;
		redInc = end.getRed() - start.getRed();
		greenInc = end.getGreen() - start.getGreen();
		blueInc = end.getBlue() - start.getBlue();
		slideColor = true;
	}

	/**
	 * Activates the slide color option.
	 * 
	 * @param b
	 *            true if we should slide the color
	 */
	public void setSlideColor(boolean b) {
		slideColor = b;
	}

	/**
	 * @return true if the sliding option is activated
	 */
	public boolean slideColor() {
		return slideColor;
	}

	/**
	 * Sets the color of the pen. This function is called if successive calls to
	 * this class should progressively change the color of the style.
	 */
	public void nextColor(int step) {
		color = new Color(color.getRed() + (int) (redInc / step),
				color.getGreen() + (int) (greenInc / step), color.getBlue()
						+ (int) (blueInc / step));
	}

	/**
	 * Sets the color used to fill an area with.
	 * 
	 * @param c
	 *            fill color
	 */
	public void setFillColor(Color c) {
		fillColor = c;
	}

	/**
	 * Returns the color used to fill an area with.
	 * 
	 * @return the fill color
	 */
	public Color getFillColor() {
		return fillColor;
	}

	/**
	 * Sets transparancy for the color used to fill an area with.
	 * 
	 * @param a
	 *            Alpha transparancy
	 */
	public void setFillColorTransparency(float a) {
		fillColorTransparency = a;
	}

	/**
	 * Sets transparancy for the color used to fill an area with errors.
	 * 
	 * @param a
	 *            Alpha transparancy
	 */
	public void setErrorFillColorTransp(float a) {
		errorsFillColorTransp = a;
	}

	/**
	 * get transparancy for the color used to fill an area with errors.
	 * 
	 * @param a
	 *            Alpha transparancy
	 */
	public float getErrorFillColorTransp() {
		return errorsFillColorTransp;
	}

	/**
	 * Sets transparancy for the color used to fill an area with system errors.
	 * 
	 * @param a
	 *            Alpha transparancy
	 */
	public void setErrorFillColorTranspSys(float a) {
		errorsFillColorTranspSys = a;
	}

	/**
	 * get transparancy for the color used to fill an area with system errors.
	 * 
	 * @return a Alpha transparancy
	 */
	public float getErrorFillColorTranspSys() {
		return errorsFillColorTranspSys;
	}

	/**
	 * Returns the transparancy color used to fill an area with.
	 * 
	 * @return float the tranparance (0-1)
	 */
	public float getFillColorTransparency() {
		return fillColorTransparency;
	}

	/**
	 * @return true if we should fill the polygone with a color.
	 */
	public boolean fill() {
		return fillArea;
	}

	/**
	 * @param b
	 *            true if we should fill the polygone with a color.
	 */
	public void fill(boolean b) {
		fillArea = b;
	}

	/**
	 * Returns the index of the color
	 * 
	 * @return color index
	 */
	public int getColorIndex() {
		return colorIndex;
	}

	/**
	 * Returns the dash length of the pen
	 * 
	 * @return dash length
	 */
	public float getDashLength() {
		return dashLength;
	}

	/**
	 * Returns the array index of the current dash length
	 * 
	 * @return index to dash length
	 */
	public int getDashIndex() {
		return dashIndex;
	}

	/**
	 * Return the width of the lines
	 * 
	 * @return pen width
	 */
	public float getPenWidth() {
		return penWidth;
	}

	/**
	 * Return the width of the lines to show errors
	 * 
	 * @return pen width
	 */
	public float getPenWidthErr() {
		return penWidthErr;
	}

	/**
	 * Return the size of small tic marked end of errors
	 * 
	 * @return tic size
	 */
	public float getErrTicSize() {
		return errTicSize / penWidthErr;
	}

	/**
	 * Set the size of small tic marked end of errors The actual tic size is
	 * multiplied by penWidthErr
	 * 
	 * @param tic
	 *            size
	 */
	public void setErrTicSize(float tic) {
		this.errTicSize = tic * penWidthErr;
	}

	/**
	 * Return the width of the lines to show systematical errors
	 * 
	 * @return pen width
	 */
	public float getPenWidthErrSys() {
		return penWidthErrSys;
	}

	/**
	 * Set the width of the lines to show errors
	 */
	public void setPenWidthErr(float a) {
		penWidthErr = a;
	}

	/**
	 * Set the width of the lines to show systematical errors
	 */
	public void setPenWidthErrSys(float a) {
		penWidthErrSys = a;
	}

	/**
	 * Returns the symbol type (symbolic constant)
	 * 
	 * @return the symbol type
	 */
	public int getSymbol() {
		return symbol;
	}

	/**
	 * Sets the multiplier for this line, a value which will be used to multiply
	 * each value of the dataset before plotting.
	 * 
	 * @param m
	 *            the multiplier
	 */
	public void setMultiplier(float m) {
		multiplier = m;
	}

	/**
	 * Returns the multiplier for this line, a value which will be used to
	 * multiply each value of the dataset before plotting.
	 * 
	 * @return the multiplier
	 */
	public float getMultiplier() {
		return multiplier;
	}

	/**
	 * Sets the additioner for this line, a value which will be used to increase
	 * each value of the dataset before plotting.
	 * 
	 * @param m
	 *            the multiplier
	 */
	public void setAdditioner(float m) {
		additioner = m;
	}

	/**
	 * Returns the additioner for this line, which is a value to be used to
	 * increase each value of the dataset before plotting.
	 * 
	 * @return the multiplier
	 */
	public float getAdditioner() {
		return additioner;
	}

	/**
	 * Returns whether or not drawing the symbol
	 * 
	 * @return whether or not drawing the symbol
	 */
	public boolean drawSymbol() {
		return useSymbol;
	}

	/**
	 * Returns whether or not drawing the lines
	 * 
	 * @return whether or not drawing the lines
	 */
	public boolean drawLine() {
		return plotLine;
	}

	/**
	 * Returns the size of the symbol in points
	 * 
	 * @return the symbol size in points
	 */
	public float getSymbolSize() {
		return symbolSize;
	}

	/**
	 * Sets the color of the pen.
	 * 
	 * @param c
	 *            color
	 */
	public void setColor(Color c) {
		color = c;
	}

	/**
	 * Sets the color of the pen from the swatch panel.
	 * 
	 * @param i
	 *            index of the color on the panel.
	 */
	public void setColor(int i) {
		color = GraphSettings.color[i];
		colorIndex = i;
	}

	/**
	 * Sets the dash length of the pen
	 * 
	 * @param dl
	 *            dash length
	 */
	public void setDashLength(float dl) {
		if (dl < 0.0f) {
			dashLength = 0.0f;
			plotLine = false;
		} else {
			dashLength = dl;
			plotLine = true;
		}
	}

	/**
	 * Sets the dash length of the pen. In addition, set the index of this dash
	 * type as presented by the chooser.
	 * 
	 * @param dl
	 *            dash length
	 * @param index
	 *            index
	 */
	public void setDashLength(float dl, int index) {
		setDashLength(dl);
		dashIndex = index;
	}

	/**
	 * Sets the width of the lines
	 * 
	 * @param pw
	 *            pen width
	 */
	public void setPenWidth(float pw) {
		penWidth = pw;
	}

	/**
	 * Sets the symbol type (symbolic constant)
	 * 
	 * @param s
	 *            the symbol type
	 */
	public void setSymbol(int s) {
		symbol = s;
		if (s == 13)
			useSymbol = false;
		else
			useSymbol = true;
	}

	/**
	 * Sets whether or not drawing the symbol
	 * 
	 * @param b
	 *            toggle
	 */
	public void setDrawSymbol(boolean b) {
		useSymbol = b;
	}

	/**
	 * is draw a symbol
	 * 
	 * @return
	 */
	public boolean isDrawSymbol() {
		return useSymbol;
	}

	/**
	 * Sets whether or not drawing the line
	 * 
	 * @param b
	 *            toggle
	 */
	public void setDrawLine(boolean b) {
		plotLine = b;
	}

	/**
	 * is draw a line?
	 * 
	 * @return
	 */
	public boolean isDrawLine() {
		return plotLine;
	}

	/**
	 * Sets the size of the symbol in points
	 * 
	 * @param ss
	 *            the symbol size in points
	 */
	public void setSymbolSize(float ss) {
		this.symbolSize = ss;
	}

	/**
	 * Show errors on Y
	 * 
	 * @param true if errors are shown
	 */
	public void errorsY(boolean s) {
		this.errorsShowY = s;
	}

	/**
	 * Show errors on X
	 * 
	 * @param true if errors are shown
	 */
	public void errorsX(boolean s) {
		this.errorsShowX = s;
	}

	/**
	 * Show systematical errors on Y
	 * 
	 * @param true if errors are shown
	 */
	public void errorsSysY(boolean s) {
		this.errorsShowSysY = s;
	}

	/**
	 * Show systematical errors on X
	 * 
	 * @param true if errors are shown
	 */
	public void errorsSysX(boolean s) {
		this.errorsShowSysX = s;
	}

	/**
	 * Fill area between lower and upper error
	 * 
	 * @param true if errors are shown
	 */
	public void errorsFill(boolean s) {
		this.errorsFill = s;
	}

	/**
	 * get fill area between lower and upper error
	 * 
	 * @return true if errors are shown
	 */
	public boolean getErrorsFill() {
		return errorsFill;
	}

	/**
	 * Fill area between lower and upper error for system errors.
	 * 
	 * @param true if errors are shown
	 */
	public void errorsFillSys(boolean s) {
		this.errorsFillSys = s;
	}

	/**
	 * get Fill area between lower and upper error for system errors.
	 * 
	 * @return true if errors are shown
	 */
	public boolean getErrorsFillSys() {
		return errorsFillSys;
	}

	/**
	 * set color errors on Y
	 */
	public void setColorErrorsY(Color col) {

		this.errorsColorY = col;
	}

	/**
	 * set color errors on X
	 */
	public void setColorErrorsX(Color col) {
		this.errorsColorX = col;
	}

	/**
	 * set color sys errors on Y
	 */
	public void setColorErrorsSysY(Color col) {

		this.errorsColorSysY = col;
	}

	/**
	 * set color sys errors on X
	 */
	public void setColorErrorsSysX(Color col) {
		this.errorsColorSysX = col;
	}

	/**
	 * set color for fill errors
	 */
	public void setColorErrorsFill(Color col) {

		this.errorsFillColor = col;
	}

	/**
	 * get color for fill errors
	 */
	public Color getColorErrorsFill() {

		return errorsFillColor;
	}

	/**
	 * set color for fill errors (systematical errors)
	 */
	public void setColorErrorsFillSys(Color col) {

		this.errorsFillColorSys = col;
	}

	/**
	 * get color for fill errors (systematical errors)
	 */
	public Color getColorErrorsFillSys() {

		return errorsFillColorSys;
	}

	/**
	 * get errors on Y
	 */
	public boolean getErrorsY() {
		return this.errorsShowY;
	}

	/**
	 * get errors on X
	 */
	public boolean getErrorsX() {
		return errorsShowX;
	}

	/**
	 * get systematical errors on Y
	 */
	public boolean getErrorsSysY() {
		return this.errorsShowSysY;
	}

	/**
	 * get systematical errors on X
	 */
	public boolean getErrorsSysX() {
		return errorsShowSysX;
	}

	/**
	 * get Color of errors on Y
	 */
	public Color getColorErrorsY() {

		return errorsColorY;
	}

	/**
	 * get Color of errors on X
	 */
	public Color getColorErrorsX() {
		return errorsColorX;
	}

	/**
	 * get color of sys errors on Y
	 */
	public Color getColorErrorsSysY() {

		return errorsColorSysY;
	}

	/**
	 * get color of sys errors on X
	 */
	public Color getColorErrorsSysX() {
		return errorsColorSysX;
	}

	/**
	 * Sets the frequency used to draw points in terms of an 'every' parameter
	 * (every 2 data-point, every 200 data point...). By default, the program
	 * draws a point for every X,Y pair from the dataset.
	 * 
	 * @param k
	 *            each point which should be drawn.
	 */
	public void setPointFrequency(int k) {
		every = k;
	}

	
	/**
	 * This function is called when writing a script file of the current
	 * settings.
	 */
	public void getSettings(XMLWrite xw) {

		xw.setData("style", String.valueOf(style));
		xw.setData("type", String.valueOf(type));
		xw.set("color", color);

		xw.set("fillAreaColor", fillColor);
		xw.add("fill", String.valueOf(fillArea));
		xw.add("isbarshown", String.valueOf(fillBars));
		xw.add("transp", String.valueOf(fillColorTransparency));
		xw.set("fillArea");

		xw.add("show", String.valueOf(plotLine));
		xw.add("dash", String.valueOf(dashIndex));
		xw.add("width", String.valueOf(penWidth));
		xw.set("line");

		xw.set("errXcolor", errorsColorX);
		xw.add("show", String.valueOf(errorsShowX));
		xw.add("penwidth", String.valueOf(penWidthErr));
		xw.set("errX");

		xw.set("errYcolor", errorsColorY);
		xw.add("show", String.valueOf(errorsShowY));
		xw.add("penwidth", String.valueOf(penWidthErr));
		xw.set("errY");

		xw.set("errSysXcolor", errorsColorSysX);
		xw.add("show", String.valueOf(errorsShowSysX));
		xw.add("penwidth", String.valueOf(penWidthErrSys));
		xw.set("errSysX");

		xw.set("errSysYcolor", errorsColorSysY);
		xw.add("show", String.valueOf(errorsShowSysY));
		xw.add("penwidth", String.valueOf(penWidthErrSys));
		xw.set("errSysY");

		xw.set("errFillColor", errorsFillColor);
		xw.add("fill", String.valueOf(errorsFill));
		xw.add("transp", String.valueOf(errorsFillColorTransp));
		xw.set("errFill");

		xw.set("errFillColorSys", errorsFillColorSys);
		xw.add("fill", String.valueOf(errorsFillSys));
		xw.add("transp", String.valueOf(errorsFillColorTranspSys));
		xw.set("errFillSys");

		xw.add("show", String.valueOf(useSymbol));
		xw.add("symbol", String.valueOf(symbol));
		xw.add("size", String.valueOf(symbolSize));
		xw.add("every", String.valueOf(every));
		xw.set("point");

		xw.add("show", String.valueOf(showLegend));
		xw.add("text", name);
		xw.set("legend");

		xw.add("multiplier", String.valueOf(multiplier));
		xw.add("offset", String.valueOf(additioner));
		xw.set("scaling");
	}


	/**
	 * Informs whether the data has been modified or not.
	 * 
	 * @return true if some of the graph data has been modified
	 */
	public boolean dataChanged() {
		return dataModified;
	}

	/**
	 * Sets to true if the data has been changed by the user.
	 * 
	 * @param b
	 *            true if the data has been changed
	 */
	public void setDataChanged(boolean b) {
		dataModified = b;
	}
	/**
	 * Updates the current settings with new data. The data settings must be
	 * wrapped in a XMLRead instance, pointing to the appropriate context.
	 * 
	 * @param xr
	 *            read object, containing all the data
	 * @see <a href="DataFile.html#updateSettings(XMLRead)"> updateSettings of
	 *      the DataFile class</a> which calls this method.
	 */
	public void updateSettings(XMLRead xr) {
		if (JPlot.debug)
			System.out.println("   - updating for new settings in LinePars...");
		style = xr.getInt("style", style);
		type = xr.getInt("type", style);

		color = xr.getColor("color", color);
		plotLine = xr.getBoolean("line/show", plotLine);
		dashIndex = xr.getInt("line/dash", dashIndex);
		penWidth = xr.getFloat("line/width", penWidth);

		fillColor = xr.getColor("fillAreaColor", fillColor);
		fillArea = xr.getBoolean("fillArea/fill", fillArea);
		fillBars = xr.getBoolean("fillArea/isbarshown", fillBars);
		fillColorTransparency = xr.getFloat("fillArea/transp",
				fillColorTransparency);

		errorsColorX = xr.getColor("errXcolor", errorsColorX);
		errorsShowX = xr.getBoolean("errX/show", errorsShowX);
		penWidthErr = xr.getFloat("errX/penwidth", penWidthErr);

		errorsColorY = xr.getColor("errYcolor", errorsColorY);
		errorsShowY = xr.getBoolean("errY/show", errorsShowY);
		penWidthErr = xr.getFloat("errY/penwidth", penWidthErr);

		errorsColorSysX = xr.getColor("errSysXcolor", errorsColorSysX);
		errorsShowSysX = xr.getBoolean("errSysX/show", errorsShowSysX);
		penWidthErrSys = xr.getFloat("errSysX/penwidth", penWidthErrSys);

		errorsColorSysY = xr.getColor("errSysYcolor", errorsColorSysY);
		errorsShowSysY = xr.getBoolean("errSysY/show", errorsShowSysY);
		penWidthErrSys = xr.getFloat("errSysX/penwidth", penWidthErrSys);

		errorsFillColor = xr.getColor("errFillColor", errorsFillColor);
		errorsFill = xr.getBoolean("errFill/fill", errorsFill);
		errorsFillColorTransp = xr.getFloat("errFill/transp",
				errorsFillColorTransp);

		errorsFillColorSys = xr.getColor("errFillColorSys", errorsFillColorSys);
		errorsFillSys = xr.getBoolean("errFillSys/fill", errorsFillSys);
		errorsFillColorTranspSys = xr.getFloat("errFillSys/transp",
				errorsFillColorTranspSys);

		useSymbol = xr.getBoolean("point/show", useSymbol);
		symbol = xr.getInt("point/symbol", symbol);
		symbolSize = xr.getFloat("line/size", symbolSize);
		every = xr.getInt("point/every", every);

		showLegend = xr.getBoolean("legend/show", showLegend);
		name = xr.getString("legend/text", "");

		additioner = xr.getFloat("scaling/offset", additioner);
		multiplier = xr.getFloat("scaling/multiplier", multiplier);
	}

}

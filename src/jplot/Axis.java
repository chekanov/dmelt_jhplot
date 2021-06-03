/**
 *    Copyright (C)  DataMelt project. The jHPLot package/
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

package jplot;

import java.util.Vector;
import java.util.Enumeration;
import java.text.NumberFormat;
import java.util.Locale;
import java.awt.*;
import javax.swing.*;
import java.lang.Math;

/**
 * Includes a static function for selecting and labeling graph axis tic labels.
 * given a numeric range and a maximum number of tics, this class can produce a
 * list of labels with the nicest round numbers not exceeding a given maximum
 * number of labels. the label generation code was extracted from the public
 * domain <a href="http://ptolemy.eecs.berkeley.edu/">Ptolomy project</a> at UC
 * Berkeley, taken from ptolemy/plot/PlotBox.java.
 * 
 * We added another static method to compute and draw an axis into a given AWT
 * Graphics object. i extracted the code for producing linear labels and threw
 * out the vast majority of code that attempted to produce log scale labels
 * since that code was very broken. it was noted int the Ptolomy code that the
 * log label generation was itself based on a file named xgraph.c by David
 * Harrisonmy, and the comments say that the original code breaks down in
 * certain cases. my drawAxis method can still draw nicely labeling log scale
 * axes because i simply use the linear scale label generation code from Ptolomy
 * and plot the tics in their proper locations on a log scale. the resulting
 * code produced exactly the same results as the Ptolemy code for log scales in
 * those ranges where the Ptolemy code did work, so this design is much better
 * in all cases and uses only a fraction of the original complexity. still, it
 * can probably be further improved though the exact problem is not well
 * defined.
 * 
 * @author M.Green and S.Chekanov
 */
public class Axis {
	private static final boolean DEBUG = false;
	public final static int X_AXIS = 0, Y_AXIS = 1;
	// For use in calculating log base 10. A log times this is a log base 10.
	private static final double LOG10SCALE = 1 / Math.log(10);

	// handy static methods
	public static double log10(double val) {
		return Math.log(val) * LOG10SCALE;
	}

	public static double exp10(double val) {
		return Math.exp(val / LOG10SCALE);
	}

	public static float flog10(double val) {
		return (float) log10(val);
	}

	public static float fexp10(double val) {
		return (float) exp10(val);
	}

	/**
	 * Calculation of the separation length between tics. Some smart rounding
	 * algorithms are needed to get the scaling properly in case of data going
	 * to 10.3 or so... Useful stuff stolen from the Gnuplot sources, thank you
	 * guys!!
	 */
	public static double calculateTicSep(double min, double max,
			int maxNumberOfTicks) {
		double xnorm, tic, posns;
		double lrange = log10(Math.abs(min - max));
		double fl = Math.floor(lrange);
		xnorm = Math.pow(10.0, lrange - fl);
		posns = maxNumberOfTicks / xnorm;

		if (posns > 40)
			tic = 0.05; // eg 0, .05, .10, ...
		else if (posns > 20)
			tic = 0.1; // eg 0, .1, .2, ...
		else if (posns > 10)
			tic = 0.2; // eg 0,0.2,0.4,...
		else if (posns > 4)
			tic = 0.5; // 0,0.5,1,
		else if (posns > 1)
			tic = 1; // 0,1,2,....
		else if (posns > 0.5)
			tic = 2; // 0, 2, 4, 6
		else if (posns > 0.2)
			tic = 10; // 0, 10, 100, 6
		else
			tic = Math.ceil(xnorm);

		tic *= Math.pow(10.0, fl);

		return tic;
	}

	/** Precompute number of ticks */
	public static int calculateTicNumber(double ticMinVal, double ticMaxVal,
			double xStep, boolean isLog) {

		int n = 0;

		// System.out.println("\n-- calculateTicNumber=");
		// System.out.println("Min="+Double.toString(ticMinVal));
		// System.out.println("Max="+Double.toString(ticMaxVal));
		// System.out.println("Max="+Double.toString(xStep));

		if (isLog == false) {
			double xStart = xStep * Math.ceil(ticMinVal / xStep);
			for (double xpos = xStart; xpos <= ticMaxVal; xpos += xStep)
				n++;
		} else {
			int t1 = 0;
			int t2 = 0;
			if (ticMinVal > 0)
				t1 = (int) Math.floor(log10(ticMinVal) - 0.5);
			if (ticMinVal < 0)
				t1 = -1 * (int) Math.floor(log10(-1 * ticMinVal) - 0.5);
			if (ticMaxVal > 0)
				t2 = (int) Math.floor(log10(ticMaxVal) + 1.0);
			if (ticMaxVal < 0)
				t2 = -1 * (int) Math.floor(log10(-1 * ticMaxVal) + 1.0);
			for (int i = t1; i < t2 + 1; i++)
				n++;
		}

		// System.out.println("Nr of ticks="+Integer.toString(n));

		return n;
	}

	/**
	 * this is the central method of this class. takes axis range parameters and
	 * produces a list of string representations of nicely rounded numbers
	 * within the given range. these strings are intended for use as axis tic
	 * labels. note: to find out where to plot each tic label simply use <br>
	 * <code>float ticval = Float.parseFloat(ticstring);</code>
	 * 
	 * @param ticMinVal
	 *            no tics will be created for less than this value.
	 * @param ticMaxVal
	 *            no tics will be created for greater than this value.
	 * @param maxTics
	 *            returned vector will contain no more labels than this number.
	 * @param isLog
	 *            set to true in case of log10 numbers
	 * @return a Vector containing formatted label strings which should also be
	 *         parsable into floating point numbers (in order to plot them).
	 */
	public static Vector<String> computeTicks(double ticMinVal,
			double ticMaxVal, int maxTicks, boolean isLog) {
		// double xStep = roundUp((ticMaxVal-ticMinVal)/maxTicks);

		double xStep = calculateTicSep(ticMinVal, ticMaxVal, maxTicks);

		int numfracdigits = numFracDigits(xStep);

		// Compute x starting point so it is a multiple of xStep.
		double xStart = xStep * Math.ceil(ticMinVal / xStep);
		Vector xgrid = null;
		Vector<String> labels = new Vector<String>();
		// Label the axis. The labels are quantized so that
		// they don't have excess resolution.

		if (!isLog) {
			for (double xpos = xStart; xpos <= ticMaxVal; xpos += xStep) {
				String snum = formatNum(xpos, numfracdigits);
				labels.addElement(snum);
			}
			// log scale: special case
		} else {

			int t1 = 0;
			int t2 = 0;
			if (ticMinVal > 0)
				t1 = (int) Math.floor(log10(ticMinVal) - 0.5);
			if (ticMinVal < 0)
				t1 = -1 * (int) Math.floor(log10(-1 * ticMinVal) - 0.5);
			if (ticMaxVal > 0)
				t2 = (int) Math.floor(log10(ticMaxVal) + 1.0);
			if (ticMaxVal < 0)
				t2 = -1 * (int) Math.floor(log10(-1 * ticMaxVal) + 1.0);

			// System.out.println("min = " + xmin + ", max = " + xmax);
			for (int i = t1; i < t2 + 1; i++) {
				double pp = Math.pow(10.0, i);
				// System.out.println(pp);
				labels.addElement(Double.toString(pp));
			}

		}

		return labels;
	}

	/**
	 * high-level method for drawing a chart axis line plus labeled tic marks.
	 * introduces a dependancy on AWT because it takes a Graphics parameter.
	 * perhaps this method belongs in some higher-level class but i added it
	 * here since it's highly related with the tic lable generation code.
	 * 
	 * @author Melinda Green
	 * 
	 * @param axis
	 *            is one of Axis.X_AXIS or Axis.Y_AXIS.
	 * @param maxTics
	 *            is the maximum number of labeled tics to draw. note: the
	 *            actual number drawn may be less.
	 * @param lowVal
	 *            is the smallest value tic mark that may be drawn. note: the
	 *            lowest valued tic label may be greater than this limit.
	 * @param highVal
	 *            is the largest value tic mark that may be drawn. note: the
	 *            highest valued tic label may be less than this limit.
	 * @param screenStart
	 *            is the coordinate in the low valued direction.
	 * @param screenEnd
	 *            is the coordinate in the high valued direction.
	 * @param offset
	 *            is the coordinate in the direction perpendicular to the
	 *            specified direction.
	 * @param logScale
	 *            is true if a log scale axis is to be drawn, false for a linear
	 *            scale.
	 * @param screenHeight
	 *            is needed to flip Y coordinates.
	 * @param g
	 *            is the AWT Graphics object to draw into. note: all drawing
	 *            will be done in the current color of the given Graphics
	 *            object.
	 */
	public static void drawAxis(int axis, int maxTics, int ticLength,
			float lowVal, float highVal, int screenStart, int screenEnd,
			int screenOffset, boolean logScale, int screenHeight, Graphics g) {
		if (logScale && (lowVal == 0 || highVal == 0))
			throw new IllegalArgumentException(
					"Axis.drawAxis: zero range value not allowed in log axes");
		if (axis == X_AXIS) // horizontal baseline
			g.drawLine(screenStart, screenHeight - screenOffset, screenEnd,
					screenHeight - screenOffset);
		else
			// vertical baseline
			g.drawLine(screenOffset, screenStart, screenOffset, screenEnd);
		Vector tics = Axis.computeTicks(lowVal, highVal, maxTics, logScale); // nice
																				// round
																				// numbers
																				// for
																				// tic
																				// labels
		int last_label_end = axis == X_AXIS ? -88888 : 88888;
		String dbgstr = "tics:    ";
		for (Enumeration e = tics.elements(); e.hasMoreElements();) {
			String ticstr = (String) e.nextElement();
			if (DEBUG)
				dbgstr += ticstr + ", ";
			float ticval = Float.parseFloat(ticstr);
			int tic_coord = screenStart;
			Dimension str_size = stringSize(ticstr, g);
			tic_coord += plotValue(ticval, lowVal, highVal, screenStart,
					screenEnd, logScale, screenHeight);
			if (axis == X_AXIS) { // horizontal axis == vertical tics
				g.drawLine(tic_coord, screenHeight - screenOffset, tic_coord,
						screenHeight - screenOffset + ticLength);
				if (tic_coord - str_size.width / 2 > last_label_end) {
					g.drawString(ticstr, tic_coord - str_size.width / 2,
							screenHeight - screenOffset + str_size.height + 5);
					last_label_end = tic_coord + str_size.width / 2
							+ str_size.height / 2;
				}
			} else { // vertical axis == horizontal tics
				tic_coord = screenHeight - tic_coord; // flips Y coordinates
				g.drawLine(screenOffset - ticLength, tic_coord, screenOffset,
						tic_coord);
				if (tic_coord - str_size.height / 3 < last_label_end) {
					g.drawString(ticstr, screenOffset - ticLength
							- str_size.width - 5, tic_coord + str_size.height
							/ 3);
					last_label_end = tic_coord - str_size.height;
				}
			}
		}
		if (DEBUG)
			System.out.println(dbgstr);
	} // end drawAxis

	/**
	 * lower level method to determine a screen location where a given value
	 * should be plotted given range, type, and screen information. the "val"
	 * parameter is the data value to be plotted
	 * 
	 * @author Melinda Green
	 * @param val
	 *            is a data value to be plotted.
	 * @return pixel offset (row or column) to draw a screen representation of
	 *         the given data value. i.e. <i>where</i> along an axis in screen
	 *         coordinates the caller should draw a representation of the given
	 *         value.
	 * @see drawAxis(int,int,int,float,float,int,int,int,boolean,int,Graphics)
	 */
	public static int plotValue(float val, float lowVal, float highVal,
			int screenStart, int screenEnd, boolean logScale, int screenHeight) {
		if (logScale && (lowVal == 0 || highVal == 0 || val == 0))
			throw new IllegalArgumentException(
					"Axis.drawAxis: zero range value not allowed in log axes");
		int screen_range = screenEnd - screenStart; // in pixels
		if (logScale) {
			float log_low = flog10(lowVal), log_high = flog10(highVal), log_val = flog10(val);
			float log_range = log_high - log_low;
			float pixels_per_log_unit = screen_range / log_range;
			return (int) ((log_val - log_low) * pixels_per_log_unit + .5);
		} else {
			float value_range = highVal - lowVal; // in data value units
			float pixels_per_unit = screen_range / value_range;
			return (int) ((val - lowVal) * pixels_per_unit + .5);
		}
	}

	/*
	 * Given a number, round up to the nearest power of ten times 1, 2, or 5.
	 * 
	 * @param value - positive
	 */
	private static double roundUp(double val) {
		int exponent = (int) Math.floor(log10(val));
		val *= Math.pow(10, -exponent);
		if (val > 5.0)
			val = 10.0;
		else if (val > 2.0)
			val = 5.0;
		else if (val > 1.0)
			val = 2.0;
		val *= Math.pow(10, exponent);
		return val;
	}

	/*
	 * Return the number of fractional digits required to display the given
	 * number. No number larger than 15 is returned (if more than 15 digits are
	 * required, 15 is returned).
	 * @param value
	 */
	private static int numFracDigits(double num) {
		int numdigits = 0;
		while (numdigits <= 15 && num != Math.floor(num)) {
			num *= 10.0;
			numdigits += 1;
		}
		return numdigits;
	}

	// Number format cache used by formatNum.
	// Note: i'd have put the body of the formatNum method below into
	// a synchronized block for complete thread safety but that causes
	// an abscure null pointer exception in the awt event thread.
	// go figure.
	private static NumberFormat numberFormat = null;

	/*
	 * Return a string for displaying the specified number using the specified
	 * number of digits after the decimal point. NOTE: java.text.NumberFormat is
	 * only present in JDK1.1 We use this method as a wrapper so that we can
	 * cache information.
	 * @param num
	 * @param numfracdigits
	 */
	private static String formatNum(double num, int numfracdigits) {
		if (numberFormat == null) {
			// Cache the number format so that we don't have to get
			// info about local language etc. from the OS each time.
			// numberFormat = NumberFormat.getInstance();
			numberFormat = NumberFormat.getInstance(Locale.ENGLISH);
			// force to not include commas because we want the strings
			// to be parsable back into numeric values. - DRG
			numberFormat.setGroupingUsed(false);
		}
		numberFormat.setMinimumFractionDigits(numfracdigits);
		numberFormat.setMaximumFractionDigits(numfracdigits);
		return numberFormat.format(num);
	}

	/**
	 * handy little utility for determining the length in pixels the given
	 * string will use if drawn into the given Graphics object. Note: perhaps
	 * belongs in some utility package.
	 */
	public static Dimension stringSize(String str, Graphics g) {
		if (g instanceof Graphics2D) {
			java.awt.geom.Rectangle2D bounds = g.getFont().getStringBounds(str,
					((Graphics2D) g).getFontRenderContext());
			return new Dimension((int) (bounds.getWidth() + .5),
					(int) (bounds.getHeight() + .5));
		} else
			return new Dimension(g.getFontMetrics().stringWidth(str), g
					.getFontMetrics().getHeight());
	}

	//
	// TEST CODE FROM HERE DOWN
	//

	private static class TestPanel extends JPanel {
		private float curLowVal, curHighVal;
		private boolean logScale;

		public TestPanel(float initialLow, float initialHigh) {
			curLowVal = initialLow;
			curHighVal = initialHigh;
		}

		public void setLogScale(boolean logScale) {
			this.logScale = logScale;
			repaint();
		}

		public void setLow(float val) {
			curLowVal = val;
			repaint();
		}

		public void setHigh(float val) {
			curHighVal = val;
			repaint();
		}

		public void paint(Graphics g) {
			super.paint(g);
			drawAxis(Axis.X_AXIS, 10, 5, curLowVal, curHighVal, 50,
					getWidth() - 50, 50, logScale, getHeight(), g);
			drawAxis(Axis.Y_AXIS, 10, 5, curLowVal, curHighVal, 50,
					getHeight() - 50, 50, logScale, getHeight(), g);
			g.drawString("Current Slider Range: " + curLowVal + " --> "
					+ curHighVal, 10, 20);
		}
	}

	private static void addField(Container into, Component c,
			GridBagConstraints gbc, int x, int y, int w, int h, int wx, int wy) {
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = w;
		gbc.gridheight = h;
		gbc.weightx = wx;
		gbc.weighty = wy;
		into.add(c, gbc);
	}

	/**
	 * simple example program for Axis class.
	 */
	public static void main(String args[]) {

		/*
		 * final float INITIAL_MIN_LOW=1, INITIAL_MAX_HIGH=1000; final TestPanel
		 * axis = new TestPanel(INITIAL_MIN_LOW, INITIAL_MAX_HIGH); final JFrame
		 * frame = new JFrame("Axis Test"); JPanel mainpanel = new JPanel(new
		 * BorderLayout()); mainpanel.add("Center", axis); JPanel controls = new
		 * JPanel(); final JCheckBox logScale = new JCheckBox("Log");
		 * logScale.addActionListener(new ActionListener() { public void
		 * actionPerformed(ActionEvent ae) {
		 * axis.setLogScale(logScale.isSelected()); } });
		 * logScale.setSelected(true); axis.setLogScale(true);
		 * 
		 * final JTextField minlow = new JTextField(""+INITIAL_MIN_LOW, 6);
		 * final FloatSlider lowSlider = new FloatSlider(JSlider.HORIZONTAL,
		 * INITIAL_MIN_LOW, 100, 1, 1000, 2000, false); final FloatSlider
		 * highSlider = new FloatSlider(JSlider.HORIZONTAL, INITIAL_MAX_HIGH,
		 * 100, 1, 1000, 2000, false); final JTextField maxhigh = new
		 * JTextField(""+INITIAL_MAX_HIGH, 6); GridBagLayout gridbag = new
		 * GridBagLayout(); controls.setLayout(gridbag); GridBagConstraints con
		 * = new GridBagConstraints(); con.fill = GridBagConstraints.BOTH;
		 * con.insets = new Insets(0, 10, 0, 0); addField(controls, logScale,
		 * con, 0, 0, 1, 1, 5, 100); addField(controls, new
		 * JLabel("min low value"), con, 1, 0, 1, 1, 10, 100);
		 * addField(controls, minlow, con, 2, 0, 1, 1, 10, 100);
		 * addField(controls, lowSlider, con, 3, 0, 1, 1, 50, 100);
		 * addField(controls, highSlider, con, 4, 0, 1, 1, 50, 100);
		 * addField(controls, new JLabel("max high value"), con, 5, 0, 1, 1, 10,
		 * 100); addField(controls, maxhigh, con, 6, 0, 1, 1, 10, 100);
		 * mainpanel.add("South", controls); KeyListener limitsWatcher = new
		 * KeyAdapter() { public void keyTyped(KeyEvent ke) { if(ke.getKeyChar()
		 * == KeyEvent.VK_ENTER) { float newminlow =
		 * Float.parseFloat(minlow.getText()); float newmaxhigh =
		 * Float.parseFloat(maxhigh.getText()); lowSlider.setAll(newminlow,
		 * newmaxhigh, lowSlider.getFloatValue()); highSlider.setAll(newminlow,
		 * newmaxhigh, highSlider.getFloatValue()); } } };
		 * minlow.addKeyListener(limitsWatcher);
		 * maxhigh.addKeyListener(limitsWatcher); AdjustmentListener
		 * slider_watcher = new AdjustmentListener() { public void
		 * adjustmentValueChanged(AdjustmentEvent ae) { if (ae.getSource() ==
		 * lowSlider) axis.setLow((float)lowSlider.getFloatValue()); else
		 * axis.setHigh((float)highSlider.getFloatValue()); } };
		 * lowSlider.addAdjustmentListener(slider_watcher);
		 * highSlider.addAdjustmentListener(slider_watcher);
		 * frame.getContentPane().add(mainpanel); frame.setSize(new
		 * Dimension(1000, 400));
		 * frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		 * frame.setVisible(true);
		 */
	} // end main
}

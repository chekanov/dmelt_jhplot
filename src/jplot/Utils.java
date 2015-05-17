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
 **/

package jplot;

import javax.swing.*;

import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;

import java.util.*;
import java.awt.*;

/**
 * General utilities.
 * 
 * @author : J.V.Lee and S.Chekabnov
 */
public class Utils {

	static final String empty = "            ";
	private static JColorChooser colorChooser;

	public static final String lf = System.getProperty("line.separator");
	public static final String fs = System.getProperty("file.separator");

	/**
	 * Returns a color chooser on request. Very usefull if the color chooser is
	 * often needed, since we only allocate the chooser once
	 */
	public static JColorChooser getColorChooser() {
		if (colorChooser == null)
			colorChooser = new JColorChooser();
		return colorChooser;
	}

	/**
	 * Says whether we're running on a UNIX platform or not. This is simply
	 * checked by the file-separator variable.
	 * 
	 * @return true if you're lucky
	 */
	public static boolean isUnix() {
		return fs.equals("/");
	}

	/**
	 * Return the number of digits required to display the given number. If more
	 * than 15 digits are required, 15 is returned).
	 */
	public static int getNumDigits(double num) {
		int numDigits = 0;
		if (num == 0.0)
			return 0;
		while (numDigits <= 15 && Math.abs(Math.floor(num) / num - 1.0) > 1e-10) {
			// while (numDigits <= 15 && num != Math.floor(num)) {
			num *= 10.0;
			numDigits++;
		}
		return numDigits;
	}

	/*
	 * Given a number, round up to the nearest power of ten times 1, 2, or 5.
	 * 
	 * Note: The argument must be strictly positive.
	 */
	public static double roundUp(double val) {
		int exponent = (int) Math.floor(Math.log10(val));
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
	 */
	public static int numFracDigits(double num) {
		int numdigits = 0;
		while (numdigits <= 15 && num != Math.floor(num)) {
			num *= 10.0;
			numdigits += 1;
		}
		return numdigits;
	}

	/*
	 * Return a string for displaying the specified number using the specified
	 * number of digits after the decimal point. NOTE: java.text.NumberFormat is
	 * only present in JDK1.1 We use this method as a wrapper so that we can
	 * cache information.
	 */
	public static String formatNum(double num, int numfracdigits) {
		if (numberFormat == null) {
			// Cache the number format so that we don't have to get
			// info about local language etc. from the OS each time.
			numberFormat = NumberFormat.getInstance();
			// force to not include commas because we want the strings
			// to be parsable back into numeric values. - DRG
			numberFormat.setGroupingUsed(false);
		}
		numberFormat.setMinimumFractionDigits(numfracdigits);
		numberFormat.setMaximumFractionDigits(numfracdigits);
		return numberFormat.format(num);
	}

	public static NumberFormat numberFormat = null;

	/**
	 * Format numbers as 10^##
	 * 
	 * @return formatted numbers: out[0]: value out[1]: power out[3]:
	 *         value+power
	 */
	public static String[] FormPow(String num, boolean isLog) {

		String stext = num;

		String[] out = new String[] { " ", " ", stext };
		double f = Double.parseDouble(num);

		// remove last zeros for 10.0, 100.0 etc. (only for log scale)
		if (isLog) {
			if (stext.indexOf(".") > -1 && stext.endsWith("0")) {
				stext = stext.substring(0, stext.length() - 1);
				if (stext.endsWith("."))
					stext = stext.substring(0, stext.length() - 1);
				out[0] = "";
				out[1] = "";
				out[2] = stext;
			}
		}

		if (Math.abs(f) < 1001 && Math.abs(f) > 0.0009) {

			return out;

		}

		Format dfb = new DecimalFormat("#.##E00");
		stext = dfb.format(f);

		String sub1 = stext;
		String sub2 = stext;
		int ie = stext.indexOf("E");
		int iem = stext.indexOf("E-");

		// dela with lasrge numbers
		if (ie > -1 && iem == -1) {
			sub1 = stext.substring(ie + 1, stext.length());
			sub2 = stext.substring(0, ie);
			if (sub1.startsWith("0")) {
				sub1 = stext.substring(ie + 2, stext.length());
			}

			if (sub1.startsWith("0")) {
				stext = sub2;
			} else {

				stext = sub2 + "\u00b710^{" + sub1 + "}";
			}
			out[0] = sub2;
			out[1] = sub1;
			out[2] = stext;
		}

		if (iem > -1) {
			sub1 = stext.substring(ie + 2, stext.length());
			sub2 = stext.substring(0, ie);
			if (sub1.startsWith("0")) {
				sub1 = stext.substring(ie + 3, stext.length());
			}

			if (sub1.startsWith("0")) {
				stext = sub2;
				out[0] = "";
				out[1] = "";
				out[2] = sub2;

			} else {

				stext = sub2 + "\u00b710^{-" + sub1 + "}";
				out[0] = sub2;
				out[1] = sub1;
				out[2] = stext;
			}
		}

		return out;
	}

	/**
	 * remove .0 stuff and remove 10^{1}
	 */
	public static String[] skeepZero(String[] len) {

		String[] tmp = len;
		// this removes ZERO at the ends
		for (int i = 0; i < tmp.length; i++) {
			if (len[i].indexOf("10^") > -1) {
				tmp[i] = len[i];
			} else {
				len[i] = len[i].replace(',', '.');
				double aDouble = Double.parseDouble(len[i]);
				tmp[i] = Double.toString(aDouble);

				// remove ZERO
				if (tmp[i].endsWith(".0"))
					tmp[i] = tmp[i].substring(0, tmp[i].length() - 2);

			}

			// finally, 10^1 does have sense
			if (tmp[i].equalsIgnoreCase("10^{1}"))
				tmp[i] = "10";

		}

		return tmp;

	}

	/**
	 * Format numbers for Lin format
	 * 
	 * @return formatted numbers
	 */
	public static String FormLin(double f) {

		if (Math.abs(f) < 1001 && Math.abs(f) >= 0.0) {

			DecimalFormat dfb = new DecimalFormat("#.#E00");
			String yy = dfb.format(f);
			float ss = (float) (Double.valueOf(yy.trim()).doubleValue());
			yy = Float.toString(ss);
			yy = RemoveZero(yy);

			/*
			 * DecimalFormat dfb = new DecimalFormat("#.#E00"); String
			 * yy=dfb.format( f ); double ss =
			 * (Double.valueOf(yy.trim()).doubleValue()); String aString
			 * =Double.toString(ss);
			 * 
			 * if (aString.indexOf(".")>-1 && aString.endsWith("0") ) {
			 * aString=aString.substring(0,aString.length()-1); if
			 * (aString.endsWith(".") )
			 * aString=aString.substring(0,aString.length()-1); }
			 */

			return yy;
		}

		Format dfb = new DecimalFormat("#.#E00");
		String stext = dfb.format(f);

		/*
		 * 
		 * if (stext.endsWith("E00")) {
		 * stext=stext.substring(0,stext.length()-3); return stext; }
		 */

		String sub1 = stext;
		String sub2 = stext;
		int ie = stext.indexOf("E");
		int iem = stext.indexOf("E-");

		// dela with lasrge numbers
		if (ie > -1 && iem == -1) {
			sub1 = stext.substring(ie + 1, stext.length());
			sub2 = stext.substring(0, ie);
			if (sub1.startsWith("0")) {
				sub1 = stext.substring(ie + 2, stext.length());
			}

			if (sub1.startsWith("0")) {
				stext = sub2;
			} else {

				stext = sub2 + "\u00b710^{" + sub1 + "}";
			}
		}

		if (iem > -1) {
			sub1 = stext.substring(ie + 2, stext.length());
			sub2 = stext.substring(0, ie);
			if (sub1.startsWith("0")) {
				sub1 = stext.substring(ie + 3, stext.length());
			}

			if (sub1.startsWith("0")) {
				stext = sub2;
			} else {

				stext = sub2 + "\u00b710^{-" + sub1 + "}";
			}
		}

		return stext;

	}

	/**
	 * Format string by removin ".0"
	 * 
	 * @return formatted string
	 */
	public static String RemoveZero(String s) {

		String aString = s;
		if (aString.indexOf(".") > -1 && aString.endsWith("0")) {
			aString = aString.substring(0, aString.length() - 1);
			if (aString.endsWith("."))
				aString = aString.substring(0, aString.length() - 1);
		}

		return aString;

	}

	/**
	 * Format numbers for Log format
	 * 
	 * @return formatted numbers
	 */
	public static String FormLin(float f) {
		return FormLin((double) f);
	}

	/**
	 * Format numbers.
	 * 
	 * @return formatted numbers
	 */
	public static String FormNum(double x, double xmin, double xmax) {

		String tmp = "";

		Format dfb = null;

		double xx = Math.abs(xmax - xmin);

		// System.out.println(xx);
		// double xx=Math.abs(xmax);
		// if (Math.abs(xmin)>xx) xx=Math.abs(xmin);

		dfb = new DecimalFormat("0.0#");
		if (xx >= 0.1)
			dfb = new DecimalFormat("0.0#");
		if (xx < 0.1 && xx >= 0.01)
			dfb = new DecimalFormat("0.00#");
		if (xx < 0.01 && xx >= 0.001)
			dfb = new DecimalFormat("0.000#");
		if (xx < 0.001 && xx >= 0.0001)
			dfb = new DecimalFormat("0.0000#");
		if (xx < 0.0001 && xx >= 0.00001)
			dfb = new DecimalFormat("0.00000#");
		if (xx < 0.00001 && xx >= 0.000001)
			dfb = new DecimalFormat("0.000000#");
		if (xx < 0.000001 && xx >= 0.0000001)
			dfb = new DecimalFormat("0.0000000#");
		if (xx < 0.0000001)
			dfb = new DecimalFormat("0.0000000000#");

		tmp = dfb.format(x);
		return tmp;

	}

	/**
	 * Format numbers for Log format
	 * 
	 * @return formatted numbers
	 */
	public static String FormLog(float f) {
		return FormLog((double) f);
	}

	/**
	 * Format numbers for Log format
	 * 
	 * @return formatted numbers
	 */
	public static String FormLog(double f) {

		String stext = "";
		Format dfb = new DecimalFormat("#.#E00");
		stext = dfb.format(f);

		// now prepare it fir RText

		String sub1 = stext;
		String sub2 = stext;
		int ie = stext.indexOf("E");
		int iem = stext.indexOf("E-");

		// fix large numbers
		if (ie > -1 && iem == -1) {
			sub1 = stext.substring(ie + 1, stext.length());
			sub2 = stext.substring(0, ie);
			if (sub1.startsWith("0")) {
				sub1 = stext.substring(ie + 2, stext.length());
			}

			if (sub1.startsWith("0")) {
				stext = "1";
			} else {
				stext = "10^{" + sub1 + "}";
			}
		}

		// fix small numbers
		if (iem > -1) {
			sub1 = stext.substring(ie + 2, stext.length());
			sub2 = stext.substring(0, ie);
			if (sub1.startsWith("0")) {
				sub1 = stext.substring(ie + 3, stext.length());
			}

			if (sub1.startsWith("0")) {
				stext = sub2;
			} else {

				stext = "10^{-" + sub1 + "}";
			}
		}

		return stext;
	}

	/**
	 * Oops message. Shows a message dialog box with a specified message.
	 * 
	 * @param s
	 *            the message
	 */
	public static void oops(Frame parent, String s) {
		JOptionPane.showMessageDialog(parent, s, "Oops",
				JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Bummer message. Shows an error message dialog box with a specified
	 * message.
	 * 
	 * @param s
	 *            the message
	 */
	public static void bummer(Frame parent, String s) {
		JOptionPane.showMessageDialog(parent, s, "Bummer",
				JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Returns a new instance of a default font. Currently, 'Default' doesn't
	 * seem too nice on e.g. unix platforms.
	 * 
	 * @return a new instance of a font.
	 */
	public static Font getDefaultFont() {
		return new Font("Helvetica", Font.BOLD, 12);
	}

	/**
	 * Make a label specifically used by the tic-panel.
	 * 
	 * @return a label with specific font and alignments.
	 */
	static public JLabel makeLabel(String txt) {
		JLabel label = new JLabel(txt + " ");
		label.setVerticalAlignment(SwingConstants.BOTTOM);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		// label.setFont(new Font("SansSerif",Font.ITALIC,11));
		label.setFont(new Font("SansSerif", Font.BOLD, 12));
		label.setForeground(Color.black);
		return label;
	}

	static public JCheckBox createCheckBox(String s, boolean b) {
		JCheckBox cb = new JCheckBox(" " + s + " ", b);
		cb.setFont(new Font("serif", Font.PLAIN, 11));
		cb.setHorizontalAlignment(JCheckBox.LEFT);
		return cb;
	}

	public static class LabeledComboBox extends JPanel {

		public JComboBox combobox;
		private boolean action;

		/**
		 * Principal constructor. Builds a combox with a label to the left of
		 * it.
		 * 
		 * @param label
		 *            the label
		 * @param v
		 *            a vector with the combobox content
		 * @oaram editable a flag which is true if the field is editable
		 */
		LabeledComboBox(String label, boolean editable) {
			combobox = new JComboBox();
			combobox.setEditable(editable);
			action = true;
			setLayout(new FlowLayout());
			JLabel l = new JLabel(label);
			l.setHorizontalTextPosition(l.RIGHT);
			// l.setDisplayedMnemonic(label.charAt(0));
			l.setLabelFor(combobox);
			add(l);
			add(combobox);
		}

		LabeledComboBox(String label, boolean editable, Vector v) {
			this(label, editable);
			replaceContent(v);
		}

		LabeledComboBox(String label, boolean editable, String s[]) {
			this(label, editable);
			replaceContent(s);
		}

		public void replaceContent(Vector v) {
			action = false;
			if (combobox.getItemCount() > 0)
				combobox.removeAllItems();
			if (v.size() == 0) {
				combobox.setEnabled(false);
				combobox.addItem(empty);
			} else {
				combobox.setEnabled(true);
				for (Enumeration e = v.elements(); e.hasMoreElements();) {
					combobox.addItem(e.nextElement());
				}
			}
			action = true;
		}

		public void replaceContent(String s[]) {
			action = false;
			if (combobox.getItemCount() > 0)
				combobox.removeAllItems();
			if (s.length == 0) {
				combobox.setEnabled(false);
				combobox.addItem(empty);
			} else {
				combobox.setEnabled(true);
				for (int i = 0; i < s.length; i++)
					combobox.addItem(s[i]);
			}
			action = true;
		}

		public boolean actionPerformed() {
			return action;
		}

		public void setSelectedItem(Object o) {
			combobox.setSelectedItem(o);
		}

		public String getSelectedItem() {
			if (combobox.isEnabled())
				return (String) combobox.getSelectedItem();
			return "";
		}
	}

	public static class LabeledTextField extends JPanel {

		/**
	 * 
	 */
		private static final long serialVersionUID = 1L;
		public JTextField textfield;
		public JCheckBox checkbox;
		public JComboBox units;
		private boolean action;
		private String label;

		private boolean CheckBoxSet;
		private boolean UnitsSet;

		/**
		 * -------------------------------------------------------------------
		 * Principal constructor,
		 * 
		 * @param label
		 *            the label
		 * @param text
		 *            initial text displayed in the editor
		 * @param withCheckBox
		 *            true if the textfield is checkable
		 *            ------------------------
		 *            --------------------------------------------
		 */
		LabeledTextField(String lb, String text, boolean withUnits,
				boolean withCheckBox) {
			label = lb;
			if (!withCheckBox)
				setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			else
				setLayout(new FlowLayout());
			JLabel l = new JLabel(label);
			l.setHorizontalTextPosition(l.RIGHT);
			// l.setDisplayedMnemonic(label.charAt(0));
			textfield = new JTextField(text);
			l.setLabelFor(textfield);
			CheckBoxSet = withCheckBox;
			UnitsSet = withUnits;
			if (withCheckBox) {
				checkbox = new JCheckBox();
				add(checkbox);
			}
			add(l);
			add(textfield);
			if (withUnits) {
				units = new JComboBox();
				add(units);
			}
			action = true;
		}

		/**
		 * -------------------------------------------------------------------
		 * another constructor
		 * --------------------------------------------------------------------
		 */
		LabeledTextField(String label, String text, boolean withUnits) {
			this(label, text, withUnits, false);
		}

		/**
		 * -------------------------------------------------------------------
		 * another constructor
		 * --------------------------------------------------------------------
		 */
		LabeledTextField(String label, String text) {
			this(label, text, false);
		}

		/**
		 * -------------------------------------------------------------------
		 * and another constructor
		 * --------------------------------------------------------------------
		 */
		LabeledTextField(String label, String text, int cols) {
			this(label, text);
			textfield.setColumns(cols);
			// Dimension d = textfield.getPreferredSize();
			// d.setWidth(textfield.getColumnWidth()*cols);
			// textfield.setPreferredSize(new
			// Dimension(textfield.getColumnWidth*cols,));
		}

		/**
		 * -------------------------------------------------------------------
		 * and again another constructor
		 * --------------------------------------------------------------------
		 */
		LabeledTextField(String label, String text, boolean withUnits, int cols) {
			this(label, text, withUnits);
			textfield.setColumns(cols);
			// Dimension d = textfield.getPreferredSize();
			// d.setWidth(textfield.getColumnWidth()*cols);
		}

		public void setUnits(Vector v) {
			if (UnitsSet) {
				action = false;
				if (units.getItemCount() > 0)
					units.removeAllItems();
				if (v.size() == 0) {
					units.setEnabled(false);
					units.addItem(empty);
				} else {
					units.setEnabled(true);
					for (Enumeration e = v.elements(); e.hasMoreElements();) {
						units.addItem(e.nextElement());
					}
				}
				action = true;
			}
		}

		public void setUnits(String s[]) {
			if (UnitsSet) {
				action = false;
				if (units.getItemCount() > 0)
					units.removeAllItems();
				if (s.length == 0) {
					units.setEnabled(false);
					units.addItem(empty);
				} else {
					units.setEnabled(true);
					for (int i = 0; i < s.length; i++) {
						units.addItem(s[i]);
					}
				}
				action = true;
			}
		}

		public void setColumns(int cols) {
			textfield.setColumns(cols);
		}

		public String getUnit() {
			if (UnitsSet) {
				return (String) units.getSelectedItem();
			}
			return "";
		}

		public void setUnit(String u) {
			if (UnitsSet) {
				units.setSelectedItem(u);
			}
		}

		public String getText() {
			return textfield.getText();
		}

		public String getLabel() {
			return label;
		}

		public void setText(String s) {
			textfield.setText(s);
		}

		public void setEditable(boolean b) {
			textfield.setEditable(b);
		}

		public void setEnabled(boolean b) {
			checkbox.setEnabled(b);
		}

		public void setSelected(boolean b) {
			textfield.setEnabled(b);
			if (CheckBoxSet)
				checkbox.setSelected(b);
			if (UnitsSet)
				units.setEnabled(b);
		}

		public boolean isSelected() {
			return checkbox.isSelected();
		}

		public boolean actionPerformed() {
			return action;
		}
	}

	/**
	 * ------------------------------------------------------------------- A
	 * checkbox with a line of text added to it as a label
	 * --------------------------------------------------------------------
	 */
	public static class CheckBoxWithText extends JPanel {

		JCheckBox checkbox;
		JLabel text;

		/**
		 * -------------------------------------------------------------------
		 * Principal constructor,
		 * 
		 * @param label
		 *            the label on the button
		 * @param s
		 *            text to be added just at the right of the checkbox label
		 *            ----------------------------------------------------------
		 *            ----------
		 */
		CheckBoxWithText(String label, String s) {
			checkbox = new JCheckBox(label);
			text = new JLabel(s);
			// checkbox.setSelected(false);
			add(checkbox);
			add(text);
		}

		/**
		 * -------------------------------------------------------------------
		 * another constructor,
		 * 
		 * @param label
		 *            the label on the button
		 *            ------------------------------------
		 *            --------------------------------
		 */
		CheckBoxWithText(String label) {
			this(label, "");
		}

		public void setSelected(boolean b) {
			checkbox.setSelected(b);
		}

		public void setText(String s) {
			text.setText(s);
		}

		public String getText() {
			return text.getText();
		}

	}

	/**
	 * Adds a setting to a string(buffer). Uses a stringbuffer to add a setting
	 * in XML format, i.e. with a key and a value. The result is something like
	 * <key value="value"/>.
	 * 
	 * @param sb
	 *            stringbuffer which will be expanded with a new setting
	 * @param indent
	 *            indentation for beautification
	 * @param key
	 *            key or name of the XML item
	 * @param v
	 *            vector with pairs of items, v[0] = an attribute, v[1] it's
	 *            value, v[2] = another attribute, v[3] it's value etc.
	 * @param close
	 *            flage, false if the keyword shouldn't be closed with a '/'
	 */
	static String getSetting(StringBuffer sb, String indent, String key,
			Vector v, boolean close) {
		sb.setLength(0);
		sb.append(indent).append("<").append(key);
		for (Enumeration e = v.elements(); e.hasMoreElements();) {
			sb.append(" ").append((String) e.nextElement()).append("=\"")
					.append((String) e.nextElement()).append("\"");
		}
		if (close)
			sb.append("/>");
		else
			sb.append(">");
		v.setSize(0);
		return sb.toString();
	}

	/**
	 * Adds a setting to a string(buffer). Always ends with a closing '/'.
	 * 
	 * @param sb
	 *            stringbuffer which will be expanded with a new setting
	 * @param indent
	 *            indentation for beautification
	 * @param key
	 *            key or name of the XML item
	 * @param v
	 *            vector with pairs of items, v[0] = an attribute, v[1] it's
	 *            value, v[2] = another attribute, v[3] it's value etc.
	 */
	static String getSetting(StringBuffer sb, String indent, String key,
			Vector v) {
		return getSetting(sb, indent, key, v, true);
	}

	/**
	 * Adds a color setting to a string(buffer).
	 * 
	 * @param sb
	 *            stringbuffer which will be expanded with a new setting
	 * @param indent
	 *            indentation for beautification
	 * @param key
	 *            key or name of the XML item
	 * @param c
	 *            a color instance
	 */
	static String getSetting(StringBuffer sb, String indent, String key, Color c) {
		sb.setLength(0);
		sb.append(indent).append("<").append(key);
		sb.append(" red=\"").append(c.getRed());
		sb.append("\" green=\"").append(c.getRed());
		sb.append("\" blue=\"").append(c.getRed()).append("\"/>");
		return sb.toString();
	}

	/**
	 * Adds a font setting to a string(buffer).
	 * 
	 * @param sb
	 *            stringbuffer which will be expanded with a new setting
	 * @param indent
	 *            indentation for beautification
	 * @param key
	 *            key or name of the XML item
	 * @param f
	 *            a font instance
	 */
	static String getSetting(StringBuffer sb, String indent, String key, Font f) {
		sb.setLength(0);
		sb.append(indent).append("<").append(key);
		sb.append(" name=\"").append(f.getName());
		sb.append("\" style=\"").append(f.getStyle());
		sb.append("\" size=\"").append(f.getSize()).append("\"/>");
		return sb.toString();
	}

	/**
	 * Round up the passed value to a NICE value.
	 */

	static public double RoundUp(double val) {
		int exponent;
		int i;

		exponent = (int) (Math.floor(Math.log10(val)));

		if (exponent < 0) {
			for (i = exponent; i < 0; i++) {
				val *= 10.0;
			}
		} else {
			for (i = 0; i < exponent; i++) {
				val /= 10.0;
			}
		}

		if (val > 5.0)
			val = 10.0;
		else if (val > 2.0)
			val = 5.0;
		else if (val > 1.0)
			val = 2.0;
		else
			val = 1.0;

		if (exponent < 0) {
			for (i = exponent; i < 0; i++) {
				val /= 10.0;
			}
		} else {
			for (i = 0; i < exponent; i++) {
				val *= 10.0;
			}
		}

		return val;

	}

}

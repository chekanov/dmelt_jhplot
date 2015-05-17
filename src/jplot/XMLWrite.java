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

import java.util.*;
import java.awt.*;

/**
 * XML-formated writing utilities. The class creates a number of useful methods
 * to write keywords and values to an XML-valid format. The class creates a
 * string buffer, to which new settings are added. They return  as a string.
 * Each setting is separated by a line-separator (cariage return by default):
 * other separators can be used if needed.
 * <p>
 * By default, we assume that the XML stuff is written to a StringBuffer, and
 * indentation is used to beautify the output. After a call to 'open()', an
 * additional indent is added after each line-separator, this shifting the text
 * to the right. The text is left-shifted by the 'close()' method.
 * <p>
 * To add more complete XML stuff, use 'add'. This is an example of who we can
 * add the settings &lt;position x="0.0" y="1.0"/&gt; to the writer:
 * <p>
 * <code>
 * XMLWriter xw = new XMLWriter();
 * xw.add("x","0.0");<br>
 * xw.add("y","1.0");<br>
 * xw.set("position");<br>
 * </code>
 * <p>
 * To open a new field and add elements, use 'open()', e.g.:
 * <p>
 * <code>
 * xw.open("context");<br>
 * xw.addSetting("Just this, folks");<br>
 * xw.close();<br>
 * </code>
 * <p>
 * creates something like
 * <p>
 * <verbatim> &lt;context&gt;<br>
 * Just this, folks<br>
 * &lt;/context&gt;<br>
 * </verbatim>
 * <p>
 * The string buffer can be reset to 0 at any time. If not reset, the settings
 * are continuously added.
 * 
 * @see XMLRead
 * 
 * @author Jan van der Lee (jan.vanderlee@ensmp.fr)
 */
public class XMLWrite {

	private String indent, shift;
	private StringBuffer sb;
	private String ls = System.getProperty("line.separator");
	private Vector keys;
	private Vector v;

	/**
	 * Builds the XML writer class with default settings. By default, the indent
	 * is 3 spaces.
	 */
	public XMLWrite() {
		indent = "   ";
		shift = "";
		keys = new Vector();
		sb = new StringBuffer(300);
		v = new Vector();
	}

	/**
	 * Builds the XML writer. The argument is the indentation (series of blanks)
	 * added to the string after each opening.
	 * 
	 * @param indent
	 *            indentation (right-shift)
	 */
	public XMLWrite(String indent) {
		this();
		this.indent = indent;
	}

	/**
	 * Builds the XML writer. The argument is the indentation (series of blanks)
	 * added to the string after each opening. The line-separator (used after
	 * each call to a addSetting method) is a line-feed by default.
	 * 
	 * @param indent
	 *            indentation (right-shift)
	 * @param ls
	 *            new line separator
	 */
	public XMLWrite(String indent, String ls) {
		this(indent);
		this.ls = ls;
	}

	/*
	 * Adds a setting to a string(buffer). Uses a stringbuffer to add a setting
	 * in XML format, i.e. with a key and a value. The result is something like
	 * <key value="value"/>.
	 * 
	 * @param key key or name of the XML item
	 * 
	 * @param close flage, false if the keyword shouldn't be closed with a '/'
	 * 
	 * @param newline true if the tag should end with a newline
	 */
	private void addSetting(String key, boolean close, boolean newline) {
		sb.append(shift).append("<").append(key);
		for (Enumeration e = v.elements(); e.hasMoreElements();) {
			sb.append(" ").append((String) e.nextElement()).append("=\"")
					.append((String) e.nextElement()).append("\"");
		}
		if (close)
			sb.append("/>");
		else
			sb.append(">");
		if (newline)
			sb.append(ls);
		v.setSize(0);
	}

	/*
	 * Adds a setting to a string(buffer).
	 * 
	 * @see #addSetting(String,boolean,boolean)
	 * 
	 * @param key key or name of the XML item
	 * 
	 * @param close flage, false if the keyword shouldn't be closed with a '/'
	 */
	private void addSetting(String key, boolean close) {
		addSetting(key, close, true);
	}

	/**
	 * Adds a key to a string(buffer). Always ends with a closing '/'.
	 * 
	 * @param key
	 *            key or name of the XML item
	 */
	public void set(String key) {
		addSetting(key, true);
	}

	/**
	 * Adds an opening tag to a string(buffer). Never ends with a closing '/'
	 * and the right-shift is incremented with white space.
	 * 
	 * @param key
	 *            key or name of the XML item
	 */
	public void open(String key) {
		addSetting(key, false);
		keys.add(key);
		open();
	}

	/**
	 * Adds a simple setting to a string(buffer), notifying a closing tag: the
	 * tag is preceded by a / (e.g. </tag>) and a left-shift indentation.
	 */
	public void close() {
		int len = shift.length() - indent.length();
		if (len > 0)
			shift = shift.substring(0, len);
		else
			shift = "";
		if (keys.size() > 0) {
			String key = (String) keys.remove(keys.size() - 1);
			sb.append(shift).append("</").append(key).append(">");
		}
		sb.append(ls);
	}

	/**
	 * Adds a color setting to a string(buffer).
	 * 
	 * @param key
	 *            key or name of the XML item
	 * @param c
	 *            a color instance
	 */
	public void set(String key, Color c) {
		sb.append(shift).append("<").append(key);
		sb.append(" red=\"").append(c.getRed());
		sb.append("\" green=\"").append(c.getGreen());
		sb.append("\" blue=\"").append(c.getBlue()).append("\"/>").append(ls);
	}

	/**
	 * Adds a font setting to a string(buffer).
	 * 
	 * @param key
	 *            key or name of the XML item
	 * @param f
	 *            a font instance
	 */
	public void set(String key, Font f) {
		sb.append(shift).append("<").append(key);
		sb.append(" name=\"").append(f.getName());
		sb.append("\" style=\"").append(f.getStyle());
		sb.append("\" size=\"").append(f.getSize()).append("\"/>").append(ls);
	}

	/**
	 * Adds a font setting to a string(buffer).
	 * 
	 * @param key
	 *            key or name of the XML item
	 * @param f
	 *            a stroke instance
	 */
	public void set(String key, BasicStroke f) {
		sb.append(shift).append("<").append(key);

		float[] d = f.getDashArray();
		float dash = 1;
		if (d != null) {
			if (d.length > 0)
				dash = d[0];
		}

		sb.append(" dashArray=\"").append(Float.toString(dash));
		sb.append("\" lineJoin=\"").append(f.getLineJoin());
		sb.append("\" miterLimit=\"").append(f.getMiterLimit());
		sb.append("\" dashPhase=\"").append(f.getDashPhase());
		sb.append("\" endCap=\"").append(f.getEndCap());
		sb.append("\" thikness=\"").append(f.getLineWidth()).append("\"/>")
				.append(ls);
	}

	/**
	 * Returns the settings currently added to the buffer in a string
	 * 
	 * @return the settings in a string.
	 */
	public String getSettings() {
		return sb.toString();
	}

	/**
	 * This method resets the separator between settings to a specific string.
	 * By default, a line-separator is used.
	 * 
	 * @param s
	 *            new line separator
	 */
	public void setSeparator(String s) {
		ls = s;
	}

	/**
	 * Sets the initial indentation level, a number of spaces.
	 * 
	 * @param ii
	 *            initial indentation
	 */
	public void setShift(String ii) {
		shift = ii;
	}

	/**
	 * Sets the indentation, a number of spaces.
	 * 
	 * @param indent
	 *            indentation
	 */
	public void setIndent(String indent) {
		this.indent = indent;
	}

	/**
	 * Shifts the text an indent to the right (open a new field).
	 */
	public void open() {
		shift = shift + indent;
	}

	/**
	 * Resets the string buffer to zero. Calling this method erases all the
	 * settings.
	 */
	public void clear() {
		sb.delete(0, sb.length());
		v.removeAllElements();
	}

	/**
	 * Add a value (or eventually a sub-set of valid XML stuff) to the current
	 * buffer.
	 * 
	 * @param s
	 *            string with settings.
	 */
	public void addSettings(String s) {
		sb.append(s);
	}

	/**
	 * Set an attribute of a setting. Using this method with e.g. "foo" and
	 * "bar" will write, for the next call to set("key"), something like <key
	 * foo="bar"> to the settings buffer.
	 * 
	 * @param attr
	 *            name of the attribute
	 * @param val
	 *            value of the attribute
	 */
	public void add(String attr, String val) {
		v.add(attr);
		v.add(val);
	}

	/**
	 * Adds data to the buffer. You generally use this method between a
	 * open(key) and close() method.
	 * 
	 * @see #open(String)
	 * @see #close()
	 * @see #setData(String,String)
	 * @param data
	 *            data or values
	 */
	public void addData(String data) {
		sb.append(shift).append(data).append(ls);
	}

	/**
	 * Adds data between within the scope of a tag. You can obtain this also by
	 * using the methods open(), addData() and close(), but this method writes
	 * the tags and data on a single line. You therefore prefer this method for
	 * short (one-word) data.
	 * 
	 * @see #open(String)
	 * @see #addData(String)
	 * @see #close()
	 * @param key
	 *            key or name of the XML item
	 */
	public void setData(String key, String data) {
		addSetting(key, false, false);
		sb.append(data);
		sb.append("</").append(key).append(">").append(ls);
	}

	/**
	 * Adds data between within the scope of a tag. You can obtain this also by
	 * using the methods open(), addData() and close(), but this method writes
	 * the tags and data on a single line. You therefore prefer this method for
	 * short (one-word) data.
	 * 
	 * @see #open(String)
	 * @see #addData(String)
	 * @see #close()
	 * @param key
	 *            key or name of the XML item
	 */
	public void setData(String key, double double_data) {
		String data = Double.toString(double_data);
		addSetting(key, false, false);
		sb.append(data);
		sb.append("</").append(key).append(">").append(ls);
	}

	/**
	 * Adds data between within the scope of a tag. You can obtain this also by
	 * using the methods open(), addData() and close(), but this method writes
	 * the tags and data on a single line. You therefore prefer this method for
	 * short (one-word) data.
	 * 
	 * @see #open(String)
	 * @see #addData(String)
	 * @see #close()
	 * @param key
	 *            key or name of the XML item
	 */
	public void setData(String key, int integer_data) {
		String data = Integer.toString(integer_data);
		addSetting(key, false, false);
		sb.append(data);
		sb.append("</").append(key).append(">").append(ls);
	}

}

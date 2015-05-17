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

import java.io.*;
import java.util.*;
import java.awt.*;

/**
 * XML reading utilities. The class provides a number of methods to read a
 * script in XML format and get the useful numbers out it. Why not using an
 * existing XML parser? Because I couldn't figure out how to use thes parsers
 * without a complex XSL sheet to do the job properly and fast.
 * 
 * <p>
 * The XMLRead and XMLWrite classes are simple to use and efficient. XMLWrite
 * can be used to create an XML-formatted file with settings of any software.
 * XMLRead, this class, helps in reading the file and provide methods to access
 * the elements and their values. The class is typically used for reading
 * scripts, for example:
 * 
 * <p>
 * <code> 
 * &lt;axes-ratio fix="false" value="0.65"/&gt;<br>
 * &lt;axes-color red="0" green="0" blue="0"/&gt;<br>
 * &lt;axis show="true"&gt;<br>
 *    &lt;range min="0.0" max="10.0"/&gt;<br>
 *    &lt;scaling log="false" offset="0.0"/&gt;<br>
 * &lt;/axis&gt;<br>
 * </code>
 * 
 * <p>
 * The data can be accessed via the different get-methods, e.g.
 * 
 * <p>
 * <code>
 * XMLRead xr("myscript.xml");<br>
 * boolean fix=false;<br>
 * fix = xr.getBoolean("axes-ratio/fix",fix);<br>
 * float ratio=1.0;
 * ratio = xr.getFloat("axes-ratio/value",ratio);<br>
 * </code>
 * 
 * <p>
 * All elements can be accessed randomly, e.g.
 * <p>
 * <code>
 * min = xr.getFloat("axis/range/min",min);<br>
 * </code>
 * 
 * <p>
 * is perfectly valid code. To avoid long strings, it is often more practical to
 * set a local scope with the 'open' method:
 * 
 * <p>
 * <code>
 * xr.open("axis");<br>
 * min = xr.getFloat("range/min",min);<br>
 * max = xr.getFloat("range/max",max);<br>
 * xr.close();  // closes 'axis'<br>
 * </code>
 * 
 * <p>
 * Note that all elements beyond the local scope remain invisible until the
 * scope is closed using the 'close()' method. Many more methods are available:
 * check the methods below for more details.
 * 
 * @see XMLWrite
 * @author Jan van der Lee (jan.vanderlee@ensmp.fr)
 */
public class XMLRead {

	private String globalContext;

	private Vector vectors;
	private Vector v;

	/**
	 * Builds the XML tool class.
	 */
	public XMLRead() {
		globalContext = "";
		vectors = new Vector();
		v = new Vector();
		vectors.add(v);
	}

	private boolean isKey(String s, String key) {
		return s.length() > key.length() + 1
				&& s.substring(1, key.length() + 1).equals(key);
	}

	private boolean isClosingKey(String s, String key) {
		return s.length() > key.length() + 2 && s.charAt(1) == '/'
				&& s.substring(2, key.length() + 2).equals(key);
	}

	private void syntaxError(String s) {
		System.out.println("Syntax error occured in this line:\n\t" + s + "\n");
	}

	private String[] toTokens(String s) {
		Vector v = new Vector();
		StringBuffer sb = new StringBuffer();
		boolean inQuotes = false;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c == '"')
				inQuotes = inQuotes ? false : true;
			// else if ((c != ' ' && c != '=') || inQuotes) sb.append(c);
			else if ((c != ' ') || inQuotes)
				sb.append(c);
			else {
				v.add(sb.toString());
				sb.delete(0, sb.length());
			}
		}
		v.add(sb.toString());
		String[] array = new String[v.size()];
		return (String[]) v.toArray(array);
	}

	/**
	 * Reads a (part of a) file and stores the XML stuff in a vector. Only the
	 * text within the specified context is considered, thus allowing to process
	 * part of a larger script. For an empty context ("") or something like "/",
	 * the entire file is read.
	 * 
	 * @param file
	 *            file which contains the XML script
	 * @param context
	 *            tag with the global context, i.e. only the stuff within this
	 *            context is actually read.
	 */
	public boolean parse(File file, String context) {

		boolean readAll = false;
		if (context.equals("/") || context.equals(""))
			readAll = true;
		globalContext = (readAll) ? "" : context;

		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			parse(in, context);
		} catch (IOException e) {
			Utils.oops(null,
					"Can't parse XML settings from file " + file.toString());
			return false;
		}

		return true;

	}

	/**
	 * Reads a (part of a) file and stores the XML stuff in a vector. Only the
	 * text within the specified context is considered, thus allowing to process
	 * part of a larger script. For an empty context ("") or something like "/",
	 * the entire file is read.
	 * 
	 * @param in
	 *            BufferedReader which contains the XML script
	 * @param context
	 *            tag with the global context, i.e. only the stuff within this
	 *            context is actually read.
	 */
	public boolean parse(BufferedReader in, String context) {
		boolean readAll = false;
		if (context.equals("/") || context.equals(""))
			readAll = true;
		globalContext = (readAll) ? "" : context;
		String s;
		try {
			while ((s = in.readLine()) != null) {
				s = s.trim();
				if (s.length() < 1)
					continue;
				if (readAll || isKey(s, context)) {

					// context found. Parse all the stuff until end of context:
					while ((s = in.readLine()) != null) {
						s = s.trim();
						if (s.length() < 1)
							continue;
						if (readAll || isClosingKey(s, context))
							break;

						if (s.charAt(0) != '<') { // if this is true, the line
													// is data
							v.add(s);
							continue;
						}

						// we'll add this to the vector but in a more rigid
						// format,
						// e.g. <key value="a"/> will be split in five items:
						// key<,
						// value<,a,value>,key>. Another example:
						// <color red="0" green="100" blue="200"/> becomes
						// color<,red<,0,red>,green<,100,green>,blue<,200,>blue,color>.
						// We skip some '>' and '<' for more internal
						// efficiency.

						// first, eliminate the '<' and '>' from the string:
						int len = s.indexOf('>');
						String s2 = s.substring(1, len);

						// the following breaks the string in tokens, using ' '
						// as
						// delimiter and respecting spaces in values between
						// quotes:
						String[] tokens = toTokens(s2);
						if (tokens[0].startsWith("/")) {
							tokens[0] = tokens[0].substring(1,
									tokens[0].length());
						}

						// more than one tokens: this element has some
						// attributes
						if (tokens.length > 1) {
							// first token *must* be the tag:
							v.add(tokens[0] + "<");
							for (int i = 1; i < tokens.length; i++) {
								int k = tokens[i].indexOf('=');
								String key = tokens[i].substring(0, k);
								v.add(key + "<");
								int end = tokens[i].length();
								if (tokens[i].charAt(end - 1) == '/')
									end--;
								// chekanov
								// String ss=tokens[i].substring(k+1,end);
								// ss=ss.trim();
								// v.add(ss);
								v.add(tokens[i].substring(k + 1, end)); // add
																		// the
																		// value
								v.add(key + ">");
							}
							if (s2.endsWith("/"))
								v.add(tokens[0] + ">");
						} else {
							if (s.charAt(1) == '/')
								v.add(tokens[0] + ">");
							else {
								v.add(tokens[0] + "<");

								// the following means that we are having a case
								// something like this: <key> data </key>
								if (len < s.length() - 1) {
									int k = s.lastIndexOf('<');
									if (k < len)
										k = s.length();
									v.add(s.substring(len + 1, k).trim());
								}
							}
						}
					}
					break;
				}
			}
			in.close();
		} catch (IOException e) {
			Utils.oops(null, "Can't parse XML settings from BufferedReader");
			return false;
		}
		// open();
		return true;
	}

	public void parse(File file) {
		parse(file, "/");
	}

	/**
	 * Opens a specific context for finding elements. Once opened with a
	 * specific context, all requests for elements and values will be search for
	 * within the context only. This allows to focuss on specific parts of a
	 * file only. Subseqent calls to open allow to focus on nested elements. The
	 * context is generally the name of an element (context = foo), but it can
	 * be a nested element as well (foo/bar) and even an element with members
	 * with specific values (foo/bar=true).
	 * 
	 * @see #close()
	 * @param context
	 *            context element name.
	 * @return false if the element was not found, true otherwise.
	 */
	public boolean open(String context) {
		// System.out.println("********* trying to open " + context);
		boolean res = false;
		globalContext = context;
		Vector a = new Vector();
		boolean opened = false;
		String[] tokens = context.split("/");
		int i = 0;
		// for (int k=last; k<v.size(); k++) {
		// String s = (String)v.get(k);
		for (Enumeration e = v.elements(); e.hasMoreElements();) {
			String s = (String) e.nextElement();
			if (s.endsWith("<")
					&& s.substring(0, s.length() - 1).equals(tokens[i])) {
				if (i == tokens.length - 1)
					opened = true;
				else
					i++;
			} else if (opened && s.endsWith(">")
					&& s.substring(0, s.length() - 1).equals(tokens[i])) {
				if (i == 0) {
					opened = false;
					break; // needed??
				} else
					i--;
			} else if (opened)
				a.add(s);
		}
		if (a.size() > 0) {
			vectors.add(a);
			v = a;
			res = true;
		}
		// if (res) {System.out.println("********* Final vector:");
		// printVector();}
		return res;
	}

	// /**
	// * Opens a specific context for finding elements. Once opened with a
	// * specific context, all requests for elements and values will be
	// * search for within the context only. This allows to focuss on
	// * specific parts of a file only. Subseqent calls to open allow to
	// * focus on nested elements. The context is generally the name of
	// * an element (context = foo), but it can be a nested element as
	// * well (foo/bar) and even an element with members with specific
	// * values (foo/bar=true).
	// * @see #close()
	// * @see #open(String context)
	// * @param context context element name.
	// * @param val element with a specific value
	// * @return false if the element was not found or if the value wasn't
	// * something like 'bar=foo', true otherwise.
	// */
	// public boolean open(String context, String val) {
	// // classical opening of the context:
	// if (!open(context)) return false;
	// String[] token = val.split("=");
	// if (token.length < 2) return false;
	// String element = token[0];
	// String value = token[1];
	// //-----------------------------------------------------------
	// // case 'location=bottom':
	// // - find 'location' (there are maybe several of them)
	// // - if 'location' found, check it's value (next token)
	// // if this token fits 'bottom', then turn a flag to true
	// // - reject the entire vector if the flag is not true.
	// //-----------------------------------------------------------
	// for (Enumeration e=v.elements(); e.hasMoreElements();) {
	// String s = (String)e.nextElement();
	// if (s.endsWith("<") &&
	// s.substring(0,s.length()-1).equals(element)) {
	// if (i == tokens.length - 1) opened = true;
	// else i++;
	// }
	// else if (s.endsWith(">") &&
	// s.substring(0,s.length()-1).equals(tokens[i])) {
	// if (i == 0) opened = false;
	// else i--;
	// }
	// else if (opened) a.add(s);
	// }
	// if (a.size() > 0) {
	// vectors.add(a);
	// v = a;
	// }
	// else return false;
	// System.out.println("********* Final vector:"); printVector();
	// return true;
	// }

	/**
	 * Closes the current context and moves back to a more global one. Closing
	 * the global context default back to the entire script. Each call to 'open'
	 * should have a corresponding 'close'. The order of opening defines the
	 * order of closing.
	 * 
	 * @see #open(String context)
	 */
	public void close() {
		if (vectors.size() > 1) {
			vectors.remove(v);
			v = (Vector) vectors.lastElement();
		}
	}

	/**
	 * Hides an element or a context from the current vector. This method will
	 * hide the first occurence of the element from the next call to 'open'. The
	 * hided elements can be 'unhided' using the 'unhide' method. Does nothing
	 * if the element is not found.
	 * 
	 * @param context
	 *            name of the element
	 */
	public void hide(String context) {
		String[] tokens = context.split("/");
		boolean opened = false;
		int i = 0;
		for (int k = 0; k < v.size(); k++) {
			String s = (String) v.get(k);
			// for (Enumeration e=v.elements(); e.hasMoreElements();) {
			// String s = (String)e.nextElement();
			if (s.endsWith("<")
					&& s.substring(0, s.length() - 1).equals(tokens[i])) {
				if (i == tokens.length - 1) {
					s = "gone***" + s;
					v.set(k, s);
					break;
				} else
					i++;
			}
		}
	}

	/**
	 * Unsets all the hidden contexts.
	 * 
	 * @see #hide(String)
	 */
	public void unHide() {
		for (Enumeration e = v.elements(); e.hasMoreElements();) {
			String s = (String) e.nextElement();
			if (s.startsWith("gone***"))
				s = s.substring(7, s.length());
		}
	}

	public void printVector() {
		int i = 0;
		for (Enumeration e = v.elements(); e.hasMoreElements(); i++) {
			String s = (String) e.nextElement();
			System.out.print(s + "\t");
			if (s.endsWith(">"))
				System.out.println();
		}
	}

	/**
	 * get all lines between the attribute
	 * 
	 * @return Vector data
	 */
	public Vector<String> getData() {
		return v;
	}

	private boolean gotoElement(Enumeration e, String element) {
		boolean found = false;
		String[] tokens = element.split("/");
		for (int i = 0; i < tokens.length; i++) {
			found = false;
			while (e.hasMoreElements()) {
				String s = (String) e.nextElement();
				if (s.substring(0, s.length() - 1).equals(tokens[i])) {
					found = true;
					break;
				}
			}
		}
		return found;
	}

	private String get(String context, Enumeration e, String element) {
		String res = "";
		boolean found = false;
		while (e.hasMoreElements()) {
			String s = (String) e.nextElement();
			String sub = s.substring(0, s.length() - 1);
			if (sub.equals(element)) {
				found = true;
				break;
			} else if (sub.equals(context))
				break;
		}
		if (found) {
			res = (String) e.nextElement();
			if (e.hasMoreElements())
				e.nextElement();
		}
		return res;
	}

	/**
	 * Returns the value of a specific element, which must be a dimension. The
	 * element is sought for in the current context only, as set by 'open'.
	 * 
	 * @see #open(String context)
	 * @param element
	 *            element or search path
	 * @param d
	 *            dimension, used to recycle memory
	 */
	public Dimension getDimension(String element, Dimension d) {
		Enumeration e = v.elements();
		gotoElement(e, element);

		// the rest must be a dimension, width and height. If the parameters
		// are not found, return the ancient dimension:
		if (!e.hasMoreElements())
			return d;
		String w = get(element, e, "width");
		if (w.equals(""))
			return d;
		String h = get(element, e, "height");
		if (h.equals(""))
			return d;
		d.width = Integer.parseInt(w);
		d.height = Integer.parseInt(h);
		return d;
	}

	/**
	 * Returns the value of a color element: it must be a color. The element is
	 * sought for in the current context only, as set by 'open'. The current
	 * color (c) is not changed if the color is not found or if the color
	 * (red,green,blue) is not written in a proper format. NOTE: Color is a
	 * final object, cannot recycle memory in a clever way as we do for
	 * dimension!
	 * 
	 * @see #open(String context)
	 * @param element
	 *            element or search path.
	 * @param c
	 *            color, default in case we didn't find the color in the script.
	 * @return the color as found in the settings.
	 */
	public Color getColor(String element, Color c) {
		Enumeration e = v.elements();
		gotoElement(e, element);
		if (!e.hasMoreElements())
			return c;
		String r = get(element, e, "red");
		if (r.equals(""))
			return c;
		String g = get(element, e, "green");
		if (r.equals(""))
			return c;
		String b = get(element, e, "blue");
		if (b.equals(""))
			return c;
		return new Color(Integer.parseInt(r), Integer.parseInt(g),
				Integer.parseInt(b));
	}

	/**
	 * Returns Stroke
	 */
	public Stroke getStroke(String element, BasicStroke c) {

		Enumeration e = v.elements();
		gotoElement(e, element);
		if (!e.hasMoreElements())
			return c;

		String thikness = get(element, e, "thikness");
		if (thikness.equals(""))
			return c;
		String lineJoin = get(element, e, "lineJoin");
		if (lineJoin.equals(""))
			return c;
		String miterLimit = get(element, e, "miterLimit");
		if (miterLimit.equals(""))
			return c;
		String dashPhase = get(element, e, "dashPhase");
		if (dashPhase.equals(""))
			return c;
		String endCap = get(element, e, "endCap");
		if (endCap.equals(""))
			return c;
		String dashArray = get(element, e, "dashArray");
		if (dashArray.equals(""))
			return c;

		float[] d = new float[] { Float.parseFloat(dashArray) };

		Stroke strock = new BasicStroke(Float.parseFloat(thikness),
				Integer.parseInt(endCap), Integer.parseInt(lineJoin),
				Float.parseFloat(miterLimit), d, Float.parseFloat(dashPhase));

		return strock;
	}

	/**
	 * Returns the value of a font element: it must be a font. The element is
	 * sought for in the current context only, as set by 'open'. The font (f) is
	 * not changed if the font is not found or if the font (name,style,size) is
	 * not written in a proper format. NOTE: Font is a final object, cannot
	 * recycle memory in a clever way as we do for dimension!
	 * 
	 * @see #open(String context)
	 * @param element
	 *            element or search path, general a name such as 'font'
	 * @param f
	 *            font, place-holder and initial font
	 * @return the font as found in the settings.
	 */
	public Font getFont(String element, Font f) {
		Enumeration e = v.elements();
		gotoElement(e, element);
		if (!e.hasMoreElements())
			return f;
		String name = get(element, e, "name");
		if (name.equals(""))
			return f;
		String style = get(element, e, "style");
		if (style.equals(""))
			return f;
		String size = get(element, e, "size");
		if (size.equals(""))
			return f;
		return new Font(name, Integer.parseInt(style), Integer.parseInt(size));
	}

	/**
	 * Returns a value of a number (integer) element. If the element is not
	 * found, of if the element value cannot be converted to the desired format,
	 * the default value 'val' is returned.
	 * 
	 * @param element
	 *            element or search path, such as foo or foo/bar
	 * @param val
	 *            default value, returned if some error occured
	 * @return the value of the element, as an integer
	 */
	public int getInt(String element, int val) {
		int res = val;

		Enumeration e = v.elements();
		if (gotoElement(e, element)) {
			if (e.hasMoreElements()) {

				String ss = (String) e.nextElement();
				ss = ss.trim();
				try {
					res = Integer.parseInt(ss);
				} catch (NumberFormatException ex) {
				}
				if (e.hasMoreElements())
					if (e.hasMoreElements())
						e.nextElement();
			}
		}
		return res;
	}

	/**
	 * Returns the value of a number (double) element. If the element is not
	 * found, of if the element value cannot be converted to the desired format,
	 * the default value 'val' is returned.
	 * 
	 * @param element
	 *            element or search path, such as foo or foo/bar
	 * @param val
	 *            default value, returned if some error occured
	 * @return the value of the element, as a double
	 */
	public double getDouble(String element, double val) {
		double res = val;
		Enumeration e = v.elements();
		if (gotoElement(e, element)) {
			if (e.hasMoreElements()) {

				String ss = (String) e.nextElement();
				ss = ss.trim();

				try {
					res = Double.parseDouble(ss);
				} catch (NumberFormatException ex) {
				}
				if (e.hasMoreElements())
					if (e.hasMoreElements())
						e.nextElement();
			}
		}
		return res;
	}

	/**
	 * Returns the value of a number (float) element. If the element is not
	 * found, of if the element value cannot be converted to the desired format,
	 * the default value 'val' is returned.
	 * 
	 * @param element
	 *            element or search path, such as foo or foo/bar
	 * @param val
	 *            default value, returned if some error occured
	 * @return the value of the element, as a double
	 */
	public float getFloat(String element, float val) {
		float res = val;
		Enumeration e = v.elements();
		if (gotoElement(e, element)) {
			if (e.hasMoreElements()) {

				String ss = (String) e.nextElement();
				ss = ss.trim();

				try {
					res = Float.parseFloat(ss);
				} catch (NumberFormatException ex) {
				}
				if (e.hasMoreElements())
					if (e.hasMoreElements())
						e.nextElement();
			}
		}
		return res;
	}

	/**
	 * Returns the value of a string element. If the element is not found, of if
	 * the element value cannot be converted to the desired format, the default
	 * value 'val' is returned.
	 * 
	 * @param element
	 *            element or search path, such as foo or foo/bar
	 * @param val
	 *            default value, returned if some error occured
	 * @return the value of the element, in a string
	 */
	public String getString(String element, String val) {
		String res = val;
		Enumeration e = v.elements();
		if (gotoElement(e, element)) {
			if (e.hasMoreElements()) {
				try {
					res = (String) e.nextElement();
				} catch (NumberFormatException ex) {
				}
				if (e.hasMoreElements())
					if (e.hasMoreElements())
						e.nextElement();
			}
		}
		return res;
	}

	/**
	 * Returns the value of a boolean element (true/false). If the element is
	 * not found, of if the element value cannot be converted to the desired
	 * format, the default value 'val' is returned.
	 * 
	 * @param element
	 *            element or search path, such as foo or foo/bar
	 * @param val
	 *            default value, returned if some error occured
	 * @return the value of the element, in a string
	 */
	public boolean getBoolean(String element, boolean val) {
		boolean res = val;
		Enumeration e = v.elements();
		if (gotoElement(e, element)) {
			if (e.hasMoreElements()) {
				try {
					res = Boolean.valueOf((String) e.nextElement())
							.booleanValue();
				} catch (NumberFormatException ex) {
				}
				if (e.hasMoreElements())
					if (e.hasMoreElements())
						e.nextElement();
			}
		}
		return res;
	}
}

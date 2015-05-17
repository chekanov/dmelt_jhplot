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

import jplot.*;
import java.awt.*;
import java.io.Serializable;

/**
 * Create an interactive key label in the USER or NDC coordinates. The label can
 * be defined in the NDC (normalized coordinate system) or the user coordinate.
 * Only one line of the text can be shown. The key is not related to any data
 * set, so you can plot this key even if no data are shown.
 * 
 * @author S.Chekanov
 * 
 */

public class HKey implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected String text;
	protected Font font;
	protected Color color;
	protected float transp;

	protected int key_type;
	protected Color key_color;
	protected float key_size; // length of the key
	protected float key_space; // space between text and key
	protected float key_line_width; // line width

	protected GraphLabel label;
	protected double Xpos, Ypos;
	protected int usePosition = 0; // 2- user, 1-NDC: position normalised, 2 USER: user coordinates
	protected boolean defaultPosition = false;
	protected float ySeparation = 0.06f;
	protected float defaultKeySize=4f;

	

	/**
	 * Create a key label.
	 * 
	 * @param s
	 *            Text
	 * @param f
	 *            Font
	 * @param c
	 *            Color
	 */

	public HKey(String s, Font f, Color c) {
		text = s;
		font = f;
		color = c;
		transp = 1.0f;
		Xpos = 0;
		Ypos = 0;
		usePosition = 2;
		key_type = 0;
		key_color = Color.blue;
		key_size = defaultKeySize;
		key_space = 6.0f;
		key_line_width = 2.0f;
		label = new GraphLabel(GraphLabel.KEY, s, f, c);
		label.setKey(key_type, key_size, key_color);
		label.setKeyLineWidth(key_line_width);
		label.setKeySpace(key_space);
		// setDefaultPosition(true);
		ySeparation = 0.05f;

	}

	/**
	 * Make a key for a function, data points or histograms (F1D, H1D, P1D). All
	 * objects should inherent DrawOptions class. All drawing attributes will be
	 * copied to the key.
	 * 
	 * @param text
	 *            key text
	 * @param x
	 *            position X
	 * @param y
	 *            position Y
	 * @param font
	 *            font of the key text
	 * @param color
	 *            color of the key text
	 * @param howToSet
	 *            use either USER or NDC coordinators
	 * @param obj
	 *            input objects, such as F1D, H1D, P1D
	 */
	public HKey(String text, double x, double y, Font font, Color color,
			String howToSet, Object obj) {

		this.text = text;
		this.font = font;
		this.color = color;
		this.transp = 1.0f;
		Xpos = x;
		Ypos = y;
		key_type = 0;
		key_color = Color.blue;
		key_size = defaultKeySize;
		key_space = 6.0f;
		key_line_width = 2.0f;
		setDefaultPosition(false);

		usePosition = 2;
		if (howToSet.equalsIgnoreCase("USER"))
			usePosition = 2;
		if (howToSet.equalsIgnoreCase("NDC")) {
			usePosition = 1;
			if (x > 1)
				x = 1;
			if (y > 1)
				y = 1;
			if (x < 0)
				x = 0;
			if (y < 0)
				y = 0;
		}

		String name = obj.getClass().getName();
		if (name.equalsIgnoreCase("jhplot.P1D")) {
			P1D p = (P1D) obj;
			LinePars lp = p.getDrawOption();
			key_color = lp.getColor();
			key_line_width = lp.getPenWidth();

			// this is symbol
			if (lp.isDrawSymbol()) {
				key_type = lp.getSymbol();
				key_size = lp.getSymbolSize()*0.2f;
				key_space= key_space+1.5f*defaultKeySize-key_size;
			}
			if (lp.isDrawLine()) {
				key_type = 30; // this is a line
				if (lp.getDashLength() > 0) {
					key_type = 31; // this is a dashed line
				}

				if (lp.getDashLength() > 3) {
					key_type = 32; // this is a line
				}
			}

		} else if (name.equalsIgnoreCase("jhplot.F1D")) {

			F1D p = (F1D) obj;
			LinePars lp = p.getDrawOption();
			key_color = lp.getColor();
			key_line_width = lp.getPenWidth();
			key_type = 30; // this is a line
			key_size = 6;
			if (lp.getDashLength() > 0) {
				key_type = 31; // this is a dashed line
			}

			if (lp.getDashLength() > 3) {
				key_type = 32; // this is a line
			}

		} else if (name.equalsIgnoreCase("jhplot.H1D")) {

			H1D p = (H1D) obj;
			LinePars lp = p.getDrawOption();
			key_color = lp.getColor();
			key_line_width = lp.getPenWidth();
			key_size = 6;

			if (lp.fill()) {
				this.key_color = lp.getFillColor();
				this.transp = lp.getFillColorTransparency();
				key_type = 20; // filled box
			} else {
				key_type = 21; // open box
			}

		}

		// build an object
		label = new GraphLabel(GraphLabel.KEY, text, font, color);
		label.setKey(key_type, key_size, key_color);
		label.setKeyLineWidth(key_line_width);
		label.setKeySpace(key_space);

	}

	/**
	 * Make a key for a function, data points or histograms (F1D, H1D, P1D). All
	 * objects should inherent DrawOptions class. All drawing attributes will be
	 * copied to the key.
	 * 
	 * @param text
	 *            key text
	 * @param x
	 *            position X
	 * @param y
	 *            position Y
	 * @param font
	 *            font of the key text
	 * @param howToSet
	 *            use either USER or NDC coordinators
	 * @param obj
	 *            input objects, such as F1D, H1D, P1D
	 */
	public HKey(String text, double x, double y, Font font, String howToSet,
			Object obj) {

		this(text, x, y, font, Color.black, howToSet, obj);
	}

	/**
	 * Make a key for a function, data points or histograms (F1D, H1D, P1D). All
	 * objects should inherent DrawOptions class. All drawing attributes will be
	 * copied to the key.
	 * 
	 * @param text
	 *            key text
	 * @param x
	 *            position X
	 * @param y
	 *            position Y
	 * @param howToSet
	 *            use either USER or NDC coordinators
	 * @param obj
	 *            input objects, such as F1D, H1D, P1D
	 */
	public HKey(String text, double x, double y, String howToSet, Object obj) {

		this(text, x, y, new Font("Arial", Font.BOLD, 14), Color.black,
				howToSet, obj);
	}

	/**
	 * Make a key for a function, data points or histograms (F1D, H1D, P1D). The
	 * default position is NDC All objects should inherent DrawOptions class.
	 * All drawing attributes will be copied to the key.
	 * 
	 * @param text
	 *            key text
	 * @param x
	 *            position X
	 * @param y
	 *            position Y
	 * @param obj
	 *            input objects, such as F1D, H1D, P1D
	 */
	public HKey(String text, double x, double y, Object obj) {

		this(text, x, y, new Font("Arial", Font.BOLD, 14), Color.black, "NDC",
				obj);
	}

	/**
	 * Make a key for a function, data points or histograms (F1D, H1D, P1D) at
	 * the default position. 0.25,0.85 All objects should inherent DrawOptions
	 * class. All drawing attributes will be copied to the key.
	 * 
	 * @param text
	 *            key text
	 * @param x
	 *            position X
	 * @param y
	 *            position Y
	 * @param obj
	 *            input objects, such as F1D, H1D, P1D
	 */
	public HKey(String text, Object obj) {
		this(text, 0.16, 0.82, new Font("Arial", Font.BOLD, 14), Color.black,
				"NDC", obj);

		setDefaultPosition(true);

	}

	/**
	 * Make a key label (color- black)
	 * 
	 * @param s
	 *            Text
	 * @param f
	 *            Font
	 */

	public HKey(String s, Font f) {
		this(s, f, Color.BLACK);
	}

	/**
	 * Make a key label with default attributes
	 * 
	 * @param s
	 *            text
	 */
	public HKey(String s) {
		this(s, new Font("Arial", Font.BOLD, 14), Color.BLACK);
	}

	/**
	 * Make a key label with specific coordinated in the data system
	 * 
	 * @param s
	 *            Text
	 * @param x
	 *            Position in X
	 * @param y
	 *            Position in Y
	 */

	public HKey(String s, double x, double y) {
		this(s);
		usePosition = 2;
		Xpos = x;
		Ypos = y;
		text = s;
	}

	/**
	 * Make a label with specific coordinates.
	 * 
	 * @param s
	 *            Text
	 * @param x
	 *            position in X
	 * @param y
	 *            position in Y
	 * @param howToSet
	 *            set it to "NDC" for normalized coordinates (in the range 0-1).
	 *            This is a data independent position set it to "USER" for the
	 *            user coordinates
	 */

	public HKey(String s, double x, double y, String howToSet) {

		this(s);
		usePosition = 2;
		if (howToSet.equalsIgnoreCase("USER"))
			usePosition = 2;
		if (howToSet.equalsIgnoreCase("NDC")) {
			usePosition = 1;
			if (x > 1)
				x = 1;
			if (y > 1)
				y = 1;
			if (x < 0)
				x = 0;
			if (y < 0)
				y = 0;
		}
		Xpos = x;
		Ypos = y;
	}

	/**
	 * Sets a text of the label.
	 * 
	 * @param s
	 *            new text for the label
	 */
	public void setText(String s) {
		text = s;
		label.setText(s);
	}

	/**
	 * Get the text of the label.
	 * 
	 * @return the current text of the label
	 */
	public String getText() {

		return label.getText();

	}

	/**
	 * Sets the color to a specific value
	 * 
	 * @param c
	 *            color used to draw the label
	 */
	public void setColor(Color c) {
		label.setColor(c);
		color = c;
	}

	/**
	 * Get color of a label.
	 * 
	 * @return the color used to draw the label
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * Sets the font to a specific value
	 * 
	 * @param f
	 *            font used to draw the label
	 */
	public void setFont(Font f) {
		font = f;
		label.setFont(f);
	}

	/**
	 * Get the font of the label text.
	 * 
	 * @return the font used to draw the label
	 */
	public Font getFont() {
		return font;
	}

	/**
	 * Sets the rotation angle. Although all kind of angles (in PI-units) are
	 * allowed, internally we only use the interval 0-2pi hence we take care of
	 * all other cases.
	 * 
	 * @param r
	 *            angle for this label
	 */
	public void setRotation(double r) {
		label.setRotation(r);
	}

	/**
	 * Returns the rotation angle.
	 * 
	 * @return angle for this label
	 */
	public double getRotation() {
		return label.getRotation();
	}

	/**
	 * Sets the location of the label in data coordinates. Ones set this way,
	 * you cannot plot the label, it must first define the location in
	 * pixel-coordinates (setLocation(x,y));
	 * 
	 * @param x
	 *            x-position of the lower-left corner of the text
	 * @param y
	 *            y-position of the lower-left corner of the text
	 */
	public void setLocation(double x, double y) {
		usePosition = 2;
		Xpos = x;
		Ypos = y;
		setDefaultPosition(false);
	}

	/**
	 * Sets a key
	 * 
	 * @param key_type
	 *            type of the key. 0 means a box
	 * @param key_size
	 *            size (i.e. length) a key in terms of character width
	 * @param key_color
	 *            color of the key
	 */
	public void setKey(int key_type, float key_size, Color key_color) {
		this.key_type = key_type;
		this.key_size = key_size;
		this.key_color = key_color;
		label.setKey(key_type, (float) key_size, key_color);
		// setDefaultPosition(true);
	}

	/**
	 * Sets key line width
	 * 
	 * @param key_line_width
	 *            line width of the key
	 */
	public void setLineWidth(int key_line_width) {
		this.key_line_width = key_line_width;
		label.setKeyLineWidth((float) key_line_width);
	}

	/**
	 * Sets a space between the key and the text describing the key
	 * 
	 * @param key_space
	 *            key space in terms of character width
	 */
	public void setKeySpace(float key_space) {
		this.key_space = key_space;
		label.setKeySpace((float) key_space);
	}

	/**
	 * Get a space between the key and the text describing the key
	 * 
	 * @return key space in terms of character width
	 */
	public double getKeySpace() {
		return label.getKeySpace();
	}

	/**
	 * Get a key line width
	 * 
	 * @return key line width
	 */
	public double getLineWidth() {
		return label.getKeyLineWidth();
	}

	/**
	 * Get key type
	 * 
	 * @return key type
	 */
	public int getKeyType() {
		return label.getKeyType();
	}

	/**
	 * Get key color
	 * 
	 * @return key color
	 */
	public Color getKeyColor() {
		return label.getKeyColor();
	}

	/**
	 * Get key size
	 * 
	 * @return key size
	 */
	public float getKeySize() {
		return label.getKeySize();
	}

	/**
	 * Sets the location of the label in data coordinates. Ones set this way,
	 * you cannot plot the label, it must first define the location in
	 * pixel-coordinates (setLocation(x,y));
	 * 
	 * @param x
	 *            x-position of the lower-left corner of the text
	 * @param y
	 *            y-position of the lower-left corner of the text
	 * @param howToSet
	 *            set it to "NDC" for normalized coordinates (in the range 0-1).
	 *            This is a data independent position set it to "USER" for the
	 *            user coordinates
	 * 
	 */
	public void setLocation(double x, double y, String howToSet) {

		usePosition = 2;
		if (howToSet.equalsIgnoreCase("USER"))
			usePosition = 2;
		if (howToSet.equalsIgnoreCase("NDC")) {
			usePosition = 1;
			if (x > 1)
				x = 1;
			if (y > 1)
				y = 1;
			if (x < 0)
				x = 0;
			if (y < 0)
				y = 0;
		}

		Xpos = x;
		Ypos = y;
		setDefaultPosition(false);

	}

	/**
	 * Is the position set?
	 * 
	 * @return zero if location is not defined of unity if the position is
	 *         defined in the NDC system 2 if the location is defined in the
	 *         user coordinates
	 * 
	 */
	public int getPositionCoordinate() {
		return usePosition;
	}

	/**
	 * Returns the X position of the text.
	 * 
	 * @return the x-position of the text
	 */
	public double getX() {
		return Xpos;
	}

	/**
	 * Returns the Y position of the text.
	 * 
	 * @return the y-position of the text
	 */
	public double getY() {
		return Ypos;
	}

	/**
	 * Get a label with default attributes
	 * 
	 * @return get GraphLabel
	 */
	public GraphLabel getGraphLabel() {

		return label;

	}

	/**
	 * Set Y-separation of the keys in the default location mode
	 * 
	 * @param y
	 *            separation between 0 and 1. NDC system is used.
	 */
	public void setSeparation(float y) {

		ySeparation = y;
	}

	/**
	 * Return vertical separation between different keys.
	 * 
	 * @param separation
	 *            between keys
	 */
	public float getSeparation() {
		return ySeparation;
	}

	/**
	 * Is this key should be set at a default position?
	 * 
	 * @return
	 */
	public boolean isDefaultPosition() {
		return defaultPosition;
	}

	/**
	 * Is this key should be set at a default position?
	 * 
	 * @param isdefault
	 *            true if the key are set at default location
	 * @return
	 */
	public void setDefaultPosition(boolean isdefault) {
		defaultPosition = isdefault;
		if (defaultPosition == true)
			usePosition = 1; // use always NDC
	}

}

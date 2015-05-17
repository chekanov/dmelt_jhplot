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

import java.awt.font.*;
import java.awt.*;
import java.text.*;
import javax.swing.*;
import java.io.*;

/**
 * This class defines a label in terms of the actual text, font, color and
 * position but also stuff like rotation, whether to hide or show etc. Some
 * special treatment for standard X, Y labels and a title. Random labels are
 * considered to be of type OTHER, which places them, by default, in the
 * lower-left corner of the graph.
 * 
 * Extends Rectangle, since a label has a rectangular bounding box, used to
 * select, drag and drop the label. Note that the bounding box is defined by the
 * upper-left corner, width (to the right) and height (directed towards the
 * bottom).
 */
public class GraphLabel implements Serializable {

	private static final long serialVersionUID = 1L;
	// / Unknown label type
	static public final int UNKNOWN = -1;
	static public final int XLABEL = 0; // X label for a 2D graph
	static public final int YLABEL = 1; // Y label for a 2D graph
	static public final int TITLE = 2; // title for a 2D graph
	static public final int PIPER_X1 = 3; // first X label for a piper diagram
	static public final int PIPER_X2 = 4; // second X label for a piper diagram
	static public final int PIPER_Y1 = 5; // lower-left Y label for a piper
											// diagram
	static public final int PIPER_Y2 = 6; // lower-right Y label for a piper
											// diagram
	static public final int PIPER_Y3 = 7; // upper-left Y label for a piper
											// diagram
	static public final int PIPER_Y4 = 8; // upper-right Y label for a piper
											// diagram
	static public final int DATA = 9; // labels defined by the data x,y
										// coordinates
	static public final int OTHER = 10; // random labels
	static public final int CHECK = 11; // DATA labels which must be checked
	static public final int STATBOX = 12; // statistical box
	static public final int KEY = 13; // labels with a key

	private int key_type;
	private Color key_color;
	private float key_size;
	private float key_space;
	private float key_line_width;

	private String[] multitext;
	private int multilines;

	private String text;
	private Font font;
	private Color color;
	private boolean active;
	private boolean usePos;
	private boolean usePosData;
	private FontMetrics fm;
	private double rotation;
	private int whoAmI;
	private double xPos; // x-position of the text
	private double yPos; // y-position of the text
	private double bbX; // x-position of the bounding box
	private double bbY; // x-position of the bounding box
	private double textWidth;
	private double textHeight;
	private double textDescent;
	private double bbWidth;
	private double bbHeight;
	private double sin, cos;
	private AttributedString attributedString;
	private File file; // label belongs to this file
	private boolean hideLabel;

	// private final String lf = System.getProperty("line.separator");

	/**
	 * Constructor, sets the text of the label.
	 * 
	 * @param t
	 *            type of this label
	 * @param s
	 *            text of the label
	 * @param f
	 *            font of the label
	 * @param c
	 *            text color of the label
	 */
	public GraphLabel(int t, String s, Font f, Color c) {

		font = f;
		color = c;
		usePos = false;
		usePosData = false;
		active = false;
		rotation = 0.0;
		multitext = new String[20]; // 20 lines of the text
		multilines = 20;
		sin = 0.0;
		hideLabel = false;
		cos = 1.0;
		xPos = yPos = 0.0;
		bbX = bbY = 0.0;
		bbWidth = bbHeight = 0.0;
		file = null;

		key_type = 0;
		key_color = Color.blue;
		key_size = 5.0f;
		key_space = 2.0f;
		key_line_width = 2.0f;

		setID(t); // might change rotate if it is an YLABEL
		setFont(f);
		setText(s); // should be at the end
	}

	/**
	 * Constructor, sets the text of the label.
	 * 
	 * @param t
	 *            type of this label
	 * @param f
	 *            font of the label
	 * @param c
	 *            text color of the label
	 */
	public GraphLabel(int t, Font f, Color c) {
		this(t, " ", f, c);
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
		setSpace();
	}

	/**
	 * Sets a key line width
	 * 
	 * @param key_line_width
	 *            line width to draw the key
	 * 
	 */
	public void setKeyLineWidth(float key_line_width) {
		this.key_line_width = key_line_width;
	}

	// get key color
	public Color getKeyColor() {
		return this.key_color;
	}

	// get key size
	public float getKeySize() {
		return this.key_size;
	}

	// get key line width
	public float getKeyLineWidth() {
		return this.key_line_width;
	}

	// get key type
	public int getKeyType() {
		return this.key_type;
	}

	// get key space
	public float getKeySpace() {
		return this.key_space;
	}

	/**
	 * Sets a space between the key and the text describing the key
	 * 
	 * @param key_space
	 *            key space in terms of character width
	 */
	public void setKeySpace(float key_space) {
		this.key_space = key_space;
		setSpace();
	}

	/**
	 * Sets the size of the bounding box. Note that this doesn't work perfectly
	 * well since rotated text is set closer together, at least in the current
	 * jvm (1.3). Hence we have to introduce some ugly empirical corrections.
	 **/
	private void setBoundingBox() {

		// in case of stat box, make large box
		if (getID() == STATBOX) {
			setSize(textWidth, getMultiLines() * textHeight);
			return;
		}

		// other staff
		if (rotation != 0.0) {
			setSize(textWidth * cos + textHeight * sin, textHeight * cos
					+ textWidth * sin);
		} else
			setSize(textWidth, textHeight);

	}

	/*
	 * Determine the font metrics of the current text. Wouldn't it be nice if I
	 * could get the font-metrics of the text *without* this ugly memory
	 * allocation???
	 */
	private void setTextMetrics() {

		if (getID() == STATBOX) {

			/*
			 * String jplot3d=multitext[0];
			 * 
			 * if (jplot3d != null ) { attributedString = new
			 * AttributedString(jplot3d);
			 * attributedString.addAttribute(TextAttribute.FONT,font); if (fm ==
			 * null) fm = (new JPanel()).getFontMetrics(font); // textWidth =
			 * fm.stringWidth(text); // in case of greek letters, the bounding
			 * box should be smaller! // chekanov String stmp=Translate.shrink(
			 * jplot3d ); textWidth = fm.stringWidth(stmp); textHeight =
			 * fm.getHeight(); setBoundingBox(); }
			 */
			// find maximum width
			String[] ss = getMultiText();

			if (ss[0] != null) {
				String tmp0 = Translate.shrink(ss[0]);
				double width = fm.stringWidth(tmp0);
				attributedString = new AttributedString(tmp0);
				attributedString.addAttribute(TextAttribute.FONT, font);
				if (fm == null)
					fm = (new JPanel()).getFontMetrics(font);
				textHeight = fm.getHeight();
				textDescent = fm.getDescent();

				// find max width
				for (int i = 1; i < ss.length; i++) {

					String stmp = Translate.shrink(ss[i]);
					double dd = fm.stringWidth(stmp);
					if (dd > width)
						width = dd;
				}
				textWidth = width;
				setBoundingBox();
			}

		} else {

			if (text != null && !text.equals("")) {
				attributedString = new AttributedString(text);
				attributedString.addAttribute(TextAttribute.FONT, font);
				if (fm == null)
					fm = (new JPanel()).getFontMetrics(font);
				// textWidth = fm.stringWidth(text);
				// in case of greek letters, the bounding box should be smaller!
				// chekanov
				String tmp = Translate.shrink(text);
				textWidth = fm.stringWidth(tmp);
				textHeight = fm.getHeight();
				textDescent = fm.getDescent();
				setBoundingBox();
			} else
				textWidth = textHeight = 0.0;

		} // end else

	}

	/**
	 * Constructor, sets the text of the label.
	 * 
	 * @param s
	 *            new text of the label
	 */
	public GraphLabel(int t, String s) {
		this(t, s, Utils.getDefaultFont(), Color.black);
	}

	/**
	 * Constructor, sets the text of the label.
	 * 
	 * @param s
	 *            new text of the label
	 */
	public GraphLabel(String s) {
		this(UNKNOWN, s);
	}

	/**
	 * Constructor, sets the ID of the label but no text.
	 * 
	 * @param t
	 *            ID of the label
	 */
	public GraphLabel(int t) {
		this(t, "");
	}

	/**
	 * Constructor, does nothing but initializing.
	 */
	public GraphLabel() {
		this("");
	}

	/**
	 * Constructor, builds the class with another graph label.
	 */
	public GraphLabel(GraphLabel gl) {
		copy(gl);
	}

	/**
	 * Constructor, builds the class with another graph label.
	 */
	public void copy(GraphLabel gl) {
		bbX = gl.getX();
		bbY = gl.getY();
		bbWidth = gl.getWidth();
		bbHeight = gl.getHeight();
		active = gl.isActive();
		usePos = gl.usePosition();
		setRotation(gl.getRotation());
		setID(gl.getID());
		hideLabel = gl.hide();
		xPos = gl.getXPos();
		yPos = gl.getYPos();

		key_size = gl.getKeySize();
		key_color = gl.getKeyColor();
		key_type = gl.getKeyType();
		key_space = gl.getKeySpace();
		key_line_width = gl.getKeyLineWidth();

		multitext = gl.getMultiText();
		textWidth = gl.getTextWidth();
		textHeight = gl.getTextHeight();
		textDescent = gl.getTextDescent();
		file = gl.getFile();
		setFont(new Font(gl.getFont().getName(), gl.getFont().getStyle(), gl
				.getFont().getSize()));
		color = new Color(gl.getColor().getRed(), gl.getColor().getGreen(), gl
				.getColor().getBlue());
		setText(gl.getText());
	}

	/**
	 * add a space
	 * 
	 * @param s
	 *            new text for the label
	 */
	public void setSpace() {

		// add some space where key will be drawn
		if (getID() != KEY)
			return;

		// trim from the left
		text = text.replaceAll("^\\s+", "");

		String tmp = "";
		for (int k = 0; k < (key_size + key_space); k++)
			tmp = tmp + " ";

		text = tmp + text;
		setTextMetrics();
		setLocation(bbX, bbY);

	}

	/**
	 * Sets the text of the label to something new.
	 * 
	 * @param s
	 *            new text for the label
	 */
	public void setText(String s) {
		text = s;
		setTextMetrics();
		setLocation(bbX, bbY);
	}

	/**
	 * Sets the multi text of the label to something new.
	 * 
	 * @param s
	 *            [] new text for the label
	 */
	public void setText(String[] s) {

		if (s.length < 20) {
			multilines = s.length;
			multitext = s;
			setTextMetrics();
			setLocation(bbX, bbY);
		} else {
			System.out.println("too many lines for multilabel");
		}

	}

	/**
	 * Get the multi text of the label
	 */
	public String[] getMultiText() {
		return multitext;
	}

	/**
	 * Get the multi text of the label
	 */
	public int getMultiLines() {
		return multilines;
	}

	/**
	 * @return the current text of the label
	 */
	public String getText() {

		String tmp = text;

		if (getID() == STATBOX) {
			String[] ss = getMultiText();
			String tt = "";
			for (int i = 0; i < ss.length; i++) {
				tt = tt + ss[i] + " ";
			}
			tmp = tt;
		}

		return tmp;

	}

	/**
	 * Returns an attributedCharacterIterator instance of the current text.
	 * 
	 * @return an attributedCharacterIterator instance of the current text
	 */
	public AttributedCharacterIterator getCharIterator() {
		return attributedString.getIterator();
	}

	/**
	 * Sets the color to a specific value
	 * 
	 * @param c
	 *            color used to draw the label
	 */
	public void setColor(Color c) {
		color = c;
	}

	/**
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
		fm = (new JPanel()).getFontMetrics(font);
		setTextMetrics();
	}

	/**
	 * Sets the font to a specific value
	 * 
	 * @return the font used to draw the label
	 */
	public Font getFont() {
		return font;
	}

	/**
	 * @param b
	 *            true if the user starts to draw the label
	 */
	public void setActive(boolean b) {
		active = b;
	}

	/**
	 * @return true if the label is actually being drawn by the mouse
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * Allows the user to used the current x- and y-positions, will use default
	 * positions otherwise. This uses positions in picxel coordinates
	 * 
	 * @param b
	 *            true if the positions should be used
	 */
	public void setUsePosition(boolean b) {
		usePos = b;
	}

	/**
	 * Allows the user to used the current x- and y-positions using the data
	 * coordinates, will use default positions otherwise
	 * 
	 * @param b
	 *            true if the positions should be used
	 */
	public void setUseDataPosition(boolean b) {
		usePosData = b;
	}

	/**
	 * @return true if the positions should be used
	 */
	public boolean usePosition() {
		return usePos;
	}

	/**
	 * @return true if the data positions should be used
	 */
	public boolean useDataPosition() {
		return usePosData;
	}

	/**
	 * @return true if we should hide this label (do not show)
	 */
	public boolean hide() {
		return hideLabel;
	}

	/**
	 * sets the hide attribute of this label
	 * 
	 * @param h
	 *            flag, true if the label should'nt show
	 */
	public void hide(boolean h) {
		hideLabel = h;
	}

	/**
	 * returns true if the current label corresponds to one of the pre-defined
	 * label types (X-label, Y-label etc).
	 * 
	 * @param type
	 *            type to compare with (must be an int)
	 * @return true if 'type' corresponds to the current 'whoAmI'
	 */
	public boolean equals(int type) {
		return (type == whoAmI);
	}

	/**
	 * returns true if the current label corresponds to one of the pre-defined
	 * label types (X-label, Y-label etc).
	 * 
	 * @param name
	 *            name to compare with (must be a string)
	 * @return true if 'name' corresponds to the current label text
	 */
	public boolean equals(String name) {
		return text.equals(name);
	}

	/**
	 * Sets the current identity of the label to some specific value The
	 * idientity must be an int and, in principle, something predefined such as
	 * GraphLabel.XLABEL).
	 * 
	 * @param type
	 *            type of the current label
	 */
	public void setID(int type) {
		whoAmI = type;
	}

	/**
	 * @return the current identity of the label to some specific value.
	 */
	public int getID() {
		return whoAmI;
	}

	/**
	 * Sets the rotation angle.
	 * 
	 * @param r
	 *            angle for this label
	 */
	public void setDataRotation(double r) {
		rotation = r;
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
		rotation = r;
		while (rotation < 0.0)
			rotation += 2.0 * Math.PI;
		while (rotation > 2.0 * Math.PI)
			rotation -= 2.0 * Math.PI;
		if (rotation != 0.0) {
			sin = Math.abs(Math.sin(rotation));
			cos = Math.abs(Math.cos(rotation));
		} else {
			sin = 0.0;
			cos = 1.0;
		}
		setBoundingBox();
		setLocation(bbX, bbY);
	}

	/**
	 * Returns the rotation angle.
	 * 
	 * @return angle for this label
	 */
	public double getRotation() {
		return rotation;
	}

	/**
	 * This function sets the text location of the label text itself, slightly
	 * different from the x,y position of the box. Optimize this function since
	 * it is used for drag'n drop.
	 * 
	 * @param x
	 *            x-position of the lower-left corner of the text
	 * @param y
	 *            y-position of the lower-left corner of the text
	 */
	public void setLocation(double x, double y) {
		bbX = x;
		bbY = y;

		// System.out.println(rotation);

		/*
		 * // this is a fix for HKey and HLabels if ((equals(GraphLabel.OTHER)
		 * || equals(GraphLabel.KEY)) && rotation <= Math.PI/2.0){ xPos = x;
		 * yPos = y+textHeight*cos; // bbX = x+textHeight; // bbY =
		 * y-textHeight; return; }
		 */

		if (rotation <= Math.PI / 2.0) {
			xPos = x;
			yPos = y + textHeight * cos;
			// System.out.println("OK2");
			return;
		}
		if (rotation <= Math.PI) {
			xPos = x + getWidth() - textHeight * sin;
			yPos = y;
			// System.out.println("OK3");
			return;
		}
		if (rotation <= 3.0 * Math.PI / 2.0) {
			xPos = x + getWidth();
			yPos = y + getHeight() - textHeight * cos;
			// System.out.println("OK4");
			return;
		} else if (rotation <= 2.0 * Math.PI) {
			xPos = x + textHeight * sin;
			yPos = y + getHeight();
			// System.out.println("OK5");
			return;
		}

	}

	/**
	 * Sets the location of this label in data coordinates. Ones set this way,
	 * you cannot plot the label, it must first define the location in
	 * pixel-coordinates (setLocation(x,y));
	 * 
	 * @param x
	 *            x-position of the lower-left corner of the text
	 * @param y
	 *            y-position of the lower-left corner of the text
	 */
	public void setDataLocation(double x, double y) {
		xPos = x;
		yPos = y;
	}

	/**
	 * Returns the X position of the text.
	 * 
	 * @return the x-position of the text
	 */
	public double getXPos() {
		return xPos;
	}

	/**
	 * Returns the Y position of the text.
	 * 
	 * @return the y-position of the text
	 */
	public double getYPos() {
		return yPos;
	}

	/**
	 * Returns the X position of the bounding box.
	 * 
	 * @return the x-position of the bounding box
	 */
	public double getX() {
		return bbX;
	}

	/**
	 * Returns the Y position of the bounding box.
	 * 
	 * @return the y-position of the bounding box
	 */
	public double getY() {
		return bbY;
	}

	/**
	 * Sets the size of the bounding box:
	 * 
	 * @param w
	 *            width of the bb
	 * @param h
	 *            height of the bb
	 */
	public void setSize(double w, double h) {
		bbWidth = w;
		bbHeight = h;
	}

	/**
	 * Sets the size of the text-label:
	 * 
	 * @param w
	 *            width of the label
	 * @param h
	 *            height of the label
	 */
	public void setTextSize(double w, double h) {
		textWidth = w;
		textHeight = h;
	}

	/**
	 * Returns the width of the bounding box of the label
	 * 
	 * @return the width of the bounding box
	 */
	public double getWidth() {
		return bbWidth;
	}

	/**
	 * Returns the height of the bounding box of the label
	 * 
	 * @return the height of the bounding box
	 */
	public double getHeight() {
		return bbHeight;
	}

	/**
	 * Returns the width of the text of the label
	 * 
	 * @return the width of the text
	 */
	public double getTextWidth() {

		return textWidth;
	}

	/**
	 * Returns the height of the text of the label
	 * 
	 * @return the height of the text
	 */
	public double getTextHeight() {
		return textHeight;
	}

	/**
	 * Returns the text descent
	 * 
	 * @return the textDescent
	 */
	public double getTextDescent() {
		return textDescent;
	}

	/**
	 * @return the file to which the label belongs
	 */
	public File getFile() {
		return file;
	}

	/**
	 * Sets the file to which the label belongs. Can be null.
	 * 
	 * @param f
	 *            file to which the label belongs
	 */
	public void setFile(File f) {
		file = f;
	}

	/**
	 * @return a string representation of this label, the text.
	 */
	public String toString() {
		return text;
	}

	/**
	 * Checks whether x,y falls within the bounding box of the current text.
	 * 
	 * @param x
	 *            x-position
	 * @param y
	 *            y-position
	 * @return true if the point x,y falls withing the bounding box.
	 */
	public boolean contains(double x, double y) {
		return (x > bbX && x < bbX + bbWidth && y > bbY && y < bbY + bbHeight);
	}

	public boolean contains(double x, double y, float scale) {
		return (x > bbX && x < bbX + bbWidth * scale && y > bbY && y < bbY
				+ bbHeight * scale);
	}

	/**
	 * Returns the settings of the current label in a XML instance.
	 * 
	 * @param xw
	 *            instance of the class containing the settings.
	 */
	public void getSettings(XMLWrite xw) {
		xw.add("name", String.valueOf(text));
		String type = "";
		if (whoAmI == 0)
			type = "x-label";
		else if (whoAmI == 1)
			type = "y-label";
		else if (whoAmI == 2)
			type = "title";
		else if (whoAmI == 3)
			type = "piper-x1";
		else if (whoAmI == 4)
			type = "piper-x2";
		else if (whoAmI == 5)
			type = "piper-y1";
		else if (whoAmI == 6)
			type = "piper-y2";
		else if (whoAmI == 7)
			type = "piper-y3";
		else if (whoAmI == 8)
			type = "piper-y4";
		else if (whoAmI == 9)
			type = "data";
		else if (whoAmI == 10)
			type = "other";
		else if (whoAmI == 11)
			type = "check";
		else if (whoAmI == 12)
			type = "statbox";
		else if (whoAmI == 13)
			type = "key";
		xw.add("type", type);
		xw.open("label");
		xw.set("color", color);
		xw.set("font", font);
		xw.add("fix", String.valueOf(usePos));
		xw.add("x", String.valueOf((int) bbX));
		xw.add("y", String.valueOf((int) bbY));

		xw.set("key-color", key_color);
		xw.add("key-type", String.valueOf(key_type));
		xw.add("key-size", String.valueOf(key_size));
		xw.add("key-space", String.valueOf(key_space));
		xw.add("key-line_width", String.valueOf(key_line_width));

		xw.set("position");
		xw.add("degrees", String.valueOf((int) (rotation / Math.PI * 180.0)));
		xw.set("rotation");
		xw.close();
	}

	/**
	 * Update the current label with the settings. These settings are formatted
	 * in XML and stored in an XMLRead instance.
	 * 
	 * @param xr
	 *            instance of the reader class, containing the settings.
	 */
	public void updateSettings(XMLRead xr) {
		String type = xr.getString("type", "");
		if (type.equals("x-label"))
			whoAmI = 0;
		if (type.equals("y-label"))
			whoAmI = 1;
		if (type.equals("title"))
			whoAmI = 2;
		if (type.equals("piper-x1"))
			whoAmI = 3;
		if (type.equals("piper-x2"))
			whoAmI = 4;
		if (type.equals("piper-y1"))
			whoAmI = 5;
		if (type.equals("piper-y2"))
			whoAmI = 6;
		if (type.equals("piper-y3"))
			whoAmI = 7;
		if (type.equals("piper-y4"))
			whoAmI = 8;
		if (type.equals("data"))
			whoAmI = 9;
		if (type.equals("other"))
			whoAmI = 10;
		if (type.equals("check"))
			whoAmI = 11;
		if (type.equals("statbox"))
			whoAmI = 12;
		if (type.equals("key"))
			whoAmI = 13;

		color = xr.getColor("color", color);

		font = xr.getFont("font", font);
		fm = (new JPanel()).getFontMetrics(font);

		usePos = xr.getBoolean("position/fix", usePos);
		bbX = xr.getDouble("position/x", bbX);
		bbY = xr.getDouble("position/y", bbY);

		key_size = (float) xr.getDouble("key-size", key_size);
		key_line_width = (float) xr.getDouble("key-line_width", key_line_width);
		key_space = (float) xr.getDouble("key-space", key_space);
		key_type = xr.getInt("key-type", key_type);
		key_color = xr.getColor("key-color", key_color);

		// get the rotation. Use the 'setRotation' method to initialize
		// the label class properly (don't set rotation directly!)
		double rot = xr.getDouble("rotation/degrees", 0.0);
		setRotation(rot * Math.PI / 180.0);

		// set the location of the label (bbX, bbY are the x,y of the
		// bounding box of the label, not of the text itself):
		setLocation(bbX, bbY);

		// set the text at the end, since setting the text will also
		// evaluate the metrix and bounding box, for which the rotation,
		// font etc. must be known:
		setText(xr.getString("name", ""));
	}
}

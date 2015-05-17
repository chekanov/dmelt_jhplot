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

import java.awt.*;
import javax.swing.*;
import java.awt.geom.*;

/**
 * Linestyle is a class which shows the current drawing style. Returns a panel
 * or canvas on which is paint the drawing style according to the parameters
 * past to the class.
 */
public class LineStyleButton extends JButton {

	private static final long serialVersionUID = 1L;
	private Color backgroundColor;
	private Stroke stroke;
	private Stroke pointStroke;
	private Dimension panelSize;
	private double lineLength;
	private LinePars lp;

	// private Rectangle rectangle;

	public LineStyleButton() {
		super();
		// backgroundColor = UIManager.getColor("Button.background");
		super.setBackground(Color.white);
		updateUI();
		setOpaque(true);
	}

	public LineStyleButton(LinePars lp) {
		this();
		this.lp = lp;
	}

	private void initialize() {
		float[] dash = { lp.getDashLength() };
		pointStroke = new BasicStroke(lp.getPenWidth());
		if (dash[0] < 0.0f)
			stroke = new BasicStroke(0.0f);
		else if (dash[0] == 0.0f)
			stroke = pointStroke;
		else
			stroke = new BasicStroke(lp.getPenWidth(), BasicStroke.CAP_SQUARE,
					BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
	}

	public void reset(LinePars lp) {
		this.lp = lp;
		initialize();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		// take the current length of the panel for the linelength:
		panelSize = getSize(panelSize);
		lineLength = 8 * panelSize.width / 10;
		double x_inset = panelSize.width / 10;
		double y_inset = panelSize.height / 2.0;

		// plot the linestyle:
		initialize();
		g2.setColor(lp.getColor());
		if (lp.drawLine()) {
			g2.setStroke(stroke);
			g2.draw(new Line2D.Double(x_inset, y_inset, lineLength + x_inset,
					y_inset));
		}
		if (lp.getSymbol() < 13 && lp.getSymbolSize() > 0.0f) {
			g2.setStroke(pointStroke);
			if (lp.drawSymbol()) {
				GPoints.drawPointType(lp.getSymbol(), g2, x_inset + lineLength
						/ 2.0, y_inset, lp.getSymbolSize());
			}
		}
	}

	// public Dimension getPreferredSize() {
	// return panelSize;
	// }

	public LinePars getLinePars() {
		return lp;
	}

	public void setLinePars(LinePars lp) {
		this.lp = lp;
	}

	public void setColor(Color c) {
		lp.setColor(c);
	}

	public void setPenWidth(float f) {
		lp.setPenWidth(f);
	}

	public void setBackground(Color c) {
		backgroundColor = c;
		super.setBackground(c);
	}
}

/**
 *    Copyright (C)  DataMelt project. The jHPLot package.
 *    S.Chekanov (1999)
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
import java.awt.geom.*;

/**
 * Draws a point-type corresponding to a symbolic constant, yielding values.
 * 
 * @author S.Chekanov
 * 
 */
public class GPoints {
	/**
	 * Draws a point-type corresponding to a symbolic constant, yielding values
	 * from 0 to <a href="GraphSettings.html#NO_SYMBOL">NO_SYMBOL</a> (==13).
	 * 
	 * @param index
	 *            symbolic constant
	 * @param g2
	 *            graphics 2D instance
	 * @param x
	 *            x-coordinate of the midpoint of the point
	 * @param y
	 *            y-coordinate of the midpoint of the point
	 * @param ps
	 *            size of the point in pixels
	 */
	public static void drawPointType(int index, Graphics2D g2, double _x,
			double _y, float ps) {

		if (ps <= 0.0f)
			return;
		Color cold = g2.getColor();

		float x = (float) (_x) - ps / 2.0f;
		float y = (float) (_y) - ps / 2.0f;
		if (index == 0) {
			g2.draw(new Ellipse2D.Float(x, y, ps, ps));
			return;
		} else if (index == 1) {
			g2.draw(new Rectangle2D.Float(x, y, ps, ps));
			return;
		} else if (index == 2) {
			GeneralPath gp = new GeneralPath();
			gp.moveTo(x, y + ps / 2.0f);
			gp.lineTo(x + ps / 2.0f, y + ps);
			gp.lineTo(x + ps, y + ps / 2.0f);
			gp.lineTo(x + ps / 2.0f, y);
			gp.lineTo(x, y + ps / 2.0f);
			g2.draw(gp);
			return;
		} else if (index == 3) {
			GeneralPath gp = new GeneralPath();
			gp.moveTo(x, y + ps);
			gp.lineTo(x + ps, y + ps);
			gp.lineTo(x + ps / 2.0f, y);
			gp.lineTo(x, y + ps);
			g2.draw(gp);
			return;
		} else if (index == 4) {
			g2.draw(new Ellipse2D.Float(x, y, ps, ps));
			g2.fill(new Ellipse2D.Float(x, y, ps, ps));
			return;
		} else if (index == 5) {
			g2.draw(new Rectangle2D.Float(x, y, ps, ps));
			g2.fill(new Rectangle2D.Float(x, y, ps, ps));
			return;
		} else if (index == 6) {
			GeneralPath gp = new GeneralPath();
			gp.moveTo(x, y + ps / 2.0f);
			gp.lineTo(x + ps / 2.0f, y + ps);
			gp.lineTo(x + ps, y + ps / 2.0f);
			gp.lineTo(x + ps / 2.0f, y);
			gp.lineTo(x, y + ps / 2.0f);
			g2.draw(gp);
			g2.fill(gp);
			return;
		} else if (index == 7) {
			GeneralPath gp = new GeneralPath();
			gp.moveTo(x, y + ps);
			gp.lineTo(x + ps, y + ps);
			gp.lineTo(x + ps / 2.0f, y);
			gp.lineTo(x, y + ps);
			g2.draw(gp);
			g2.fill(gp);
			return;
		} else if (index == 8) {
			g2.draw(new Line2D.Float(x, y + ps / 2.0f, x + ps, y + ps / 2.0f));
			g2.draw(new Line2D.Float(x + ps / 2.0f, y, x + ps / 2.0f, y + ps));
			return;
		} else if (index == 9) {
			g2.draw(new Line2D.Float(x, y, x + ps, y + ps));
			g2.draw(new Line2D.Float(x + ps, y, x, y + ps));
			return;
		} else if (index == 10) {
			g2.draw(new Line2D.Float(x, y + ps / 2.0f, x + ps, y + ps / 2.0f));
			g2.draw(new Line2D.Float(x + ps / 2.0f, y, x + ps / 2.0f, y + ps));
			g2.draw(new Line2D.Float(x, y, x + ps, y + ps));
			g2.draw(new Line2D.Float(x + ps, y, x, y + ps));
			return;
		} else if (index == 11) {
			ps = 2.0f;
			x = (float) (_x) - ps;
			y = (float) (_y) - ps;
			g2.fill(new Ellipse2D.Float(x, y, ps, ps));
			return;
		} else if (index == 12) {
			g2.draw(new Line2D.Float(x, y + ps / 3.0f, x + ps, y + ps / 3.0f));
			g2.draw(new Line2D.Float(x, y + 2.0f * ps / 3.0f, x + ps, y + 2.0f
					* ps / 3.0f));
			g2.draw(new Line2D.Float(x + ps / 3.0f, y, x + ps / 3.0f, y + ps));
			g2.draw(new Line2D.Float(x + 2.0f * ps / 3.0f, y, x + 2.0f * ps
					/ 3.0f, y + ps));
			return;
		} else if (index == 20) {
			g2.draw(new Line2D.Float(x - 1.0f * ps, y + 0.5f * ps, x + 0.5f
					* ps, y + 0.5f * ps));
			return;
			// non-filled
		} else if (index == 21) {
			g2.draw(new Rectangle2D.Float(x - 1.0f * ps, y, 2 * ps, ps));
			return;
		} else if (index == 22) {
			g2.fill(new Rectangle2D.Float(x - 1.0f * ps, y, 2 * ps, ps));
			return;
		} else if (index == 23) {
			g2.fill(new Rectangle2D.Float(x - 1.0f * ps, y, 2 * ps, ps));
			g2.setColor(Color.black);
			g2.draw(new Rectangle2D.Float(x - 1.0f * ps, y, 2 * ps, ps));
			g2.setColor(cold);
		}

	}

}

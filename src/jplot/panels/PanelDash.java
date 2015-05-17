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

package jplot.panels;

import java.awt.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

import java.awt.event.*;

import jplot.GraphSettings;
import jplot.LinePars;
import jplot.MyUtils;
import jplot.SmallScrollPane;

/**
 * Builds a line-type (dash strokes) chooser panel, using a number of
 * pre-defined strokes.
 */
public class PanelDash extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JComboBox cb_lw;
	// String[] zero2ten = {"0","1","2","3","4","5","6","7","8","9"};
	private String[] ten = { "10", "9", "8", "7", "6", "5", "4", "3", "2", "1" };
	private SmallScrollPane ssp;
	private LinePars lp;
	private DashSwatches swatches;
	boolean actionNeeded;

	public PanelDash(LinePars l) {
		lp = l;
		actionNeeded = false;
		setLayout(new BorderLayout());
		setBorder(new TitledBorder(new EtchedBorder(), "Lines"));
		JPanel p = new JPanel(new FlowLayout());
		swatches = new DashSwatches();
		p.add(swatches);
		add(p, BorderLayout.CENTER);
		p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.add(MyUtils.getLabel("pen width"));
		p.add(Box.createHorizontalStrut(4));
		ssp = new SmallScrollPane(ten);
		ssp.getViewport().addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (actionNeeded) {
					lp.setPenWidth(Float.parseFloat(ten[ssp.getSelectedIndex()]));
				}
			}
		});
		p.add(ssp);
		add(p, BorderLayout.SOUTH);
		// ssp.setSelected(Integer.toString((int)lp.getPenWidth()));
	}

	/**
	 * Refreshes the panel with the current data.
	 * 
	 * @param l
	 *            line parameter class
	 */
	public void refresh(LinePars l) {
		lp = l;
		actionNeeded = true;
		ssp.setSelectedIndex(ten.length - (int) lp.getPenWidth());
		// ssp.setSelected(Integer.toString((int)lp.getPenWidth()));
		swatches.setSelectedSwatch(lp.getDashIndex());
	}

	/**
	 * Builds a line-type (dash strokes) chooser panel, using a number of
	 * pre-defined strokes.
	 */
	class DashSwatches extends PanelSeeOption {

		/**
	 * 
	 */
		private static final long serialVersionUID = 1L;
		protected float[] dash;
		protected float penWidth;

		// protected float selectedDash;

		DashSwatches() {
			// super(lp.getDashIndex());
			setSelectedSwatch(lp.getDashIndex());
			addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 1) {
						int index = getIndex(e.getX(), e.getY());
						setSelectedSwatch(index);
						lp.setDashLength(GraphSettings.dashLengths[index],
								index);
					}
				}
			});
		}

		protected void initValues() {
			dash = new float[1];
			dash[0] = 0.0f;
			penWidth = 1.0f;
			soptionSize = new Dimension(40, 14);
			numSOption = new Dimension(2, 4);
		}

		protected void paintIt(Graphics2D g2, int column, int row, int x, int y) {
			dash[0] = GraphSettings.dashLengths[(row * numSOption.width)
					+ column];
			if (dash[0] < 0.0f)
				return;
			g2.setColor(Color.black);
			if (dash[0] == 0.0f)
				g2.setStroke(new BasicStroke(penWidth));
			else {
				g2.setStroke(new BasicStroke(penWidth, BasicStroke.CAP_SQUARE,
						BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
			}
			g2.drawLine(x + 2, y + soptionSize.height / 2, x + soptionSize.width
					- 2, y + soptionSize.height / 2);
			g2.setStroke(new BasicStroke());
		}

		public String getToolTipText(MouseEvent e) {
			int i = getIndex(e.getX(), e.getY());
			if (i >= 0 && i < Ns) {
				return "dash type " + new Integer(i).toString();
			}
			return "no line";
		}
	}
}

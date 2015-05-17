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

/**
 * Builds a chooser panel from a series of rectangular chooseable 'buttons'.
 * Note that this class must be derived in order to be used properly.
 */
class PanelSeeOption extends JPanel {

	private static final long serialVersionUID = 1L;
	protected Dimension soptionSize;
	protected Dimension numSOption;
	protected Dimension gap;
	protected int Ns;
	Color[] lowerLeftLineColor;
	Color[] upperRightLineColor;
	protected int width, height;

	/**
	 * Constructs the panel
	 * 
	 * @param selectedIndex
	 *            index of the current selected index
	 */
	public PanelSeeOption(int selectedIndex) {
		initValues();
		Ns = numSOption.width * numSOption.height;
		setToolTipText("");
		setOpaque(true);
		gap = new Dimension(2, 2);
		lowerLeftLineColor = new Color[Ns];
		upperRightLineColor = new Color[Ns];
		setRequestFocusEnabled(false);
		resetBackgrounds();
		if (selectedIndex >= 0) {
			lowerLeftLineColor[selectedIndex] = Color.white;
			upperRightLineColor[selectedIndex] = Color.black;
		}
		width = numSOption.width * (soptionSize.width + gap.width) - 1;
		height = numSOption.height * (soptionSize.height + gap.height) - 1;
	}

	/**
	 * Constructs the panel.
	 */
	public PanelSeeOption() {
		this(0);
	}

	protected void initValues() {
		soptionSize = new Dimension(1, 1);
		numSOption = new Dimension(1, 1);
	}

	protected void resetBackgrounds() {
		for (int i = 0; i < Ns; i++) {
			lowerLeftLineColor[i] = Color.black;
			upperRightLineColor[i] = Color.white;
		}
	}

	/**
	 * Presses the specified index, which correspond to the actually selected
	 * option.
	 * 
	 * @param index
	 *            index of the selected option.
	 */
	protected void setSelectedSwatch(int index) {
		for (int i = 0; i < Ns; i++) {
			lowerLeftLineColor[i] = Color.black;
			upperRightLineColor[i] = Color.white;
		}
		if (index >= 0) {
			lowerLeftLineColor[index] = Color.white;
			upperRightLineColor[index] = Color.black;
		}
		repaint();
	}

	/**
	 * Paint the pane.
	 * 
	 * @param g2
	 * @param column
	 * @param row
	 * @param x
	 * @param y
	 */
	protected void paintIt(Graphics2D g2, int column, int row, int x, int y) {
	}

	/**
	 * Paint the graphics.
	 */
	public void paintComponent(Graphics g) {
		if (Ns == 0)
			return;
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(getBackground());
		g2.fillRect(0, 0, getWidth(), getHeight());
		for (int row = 0; row < numSOption.height; row++) {
			for (int column = 0; column < numSOption.width; column++) {
				int x = column * (soptionSize.width + gap.width);
				int y = row * (soptionSize.height + gap.height);
				int index = row * numSOption.width + column;
				paintIt(g2, column, row, x, y);
				g2.setColor(lowerLeftLineColor[index]);
				g2.drawLine(x + soptionSize.width - 1, y, x + soptionSize.width
						- 1, y + soptionSize.height - 1);
				g2.drawLine(x, y + soptionSize.height - 1, x
						+ soptionSize.width - 1, y + soptionSize.height - 1);
				g2.setColor(upperRightLineColor[index]);
				g2.drawLine(x, y, x + soptionSize.width - 1, y);
				g2.drawLine(x, y, x, y + soptionSize.height - 1);
			}
		}
	}

	public Dimension getPreferredSize() {
		return new Dimension(width, height);
	}

	public Dimension getMaximumSize() {
		return getPreferredSize();
	}

	public Dimension getMinimumSize() {
		return getPreferredSize();
	}

	public int getIndex(int x, int y) {
		return (getRow(y) * numSOption.width) + getColumn(x);
	}

	public int getColumn(int x) {
		return x / (soptionSize.width + gap.width);
	}

	public int getRow(int y) {
		return y / (soptionSize.height + gap.height);
	}
}

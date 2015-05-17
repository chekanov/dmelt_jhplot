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

import javax.swing.*;
import java.awt.*;

public class PanelGridUI extends JPanel {

	private static final long serialVersionUID = 1L;
	private GridBagConstraints constraints;

	// default constraints value definitions
	private static final int C_HORZ = GridBagConstraints.HORIZONTAL;
	private static final int C_NONE = GridBagConstraints.NONE;
	private static final int C_WEST = GridBagConstraints.WEST;
	private static final int C_WIDTH = 1;
	private static final int C_HEIGHT = 1;

	// Create a GridBagLayout panel using a default insets constraint.
	public PanelGridUI() {
		this(new Insets(2, 2, 2, 2));
	}

	// Create a GridBagLayout panel using the specified insets
	// constraint.
	public PanelGridUI(Insets insets) {
		super(new GridBagLayout());
		constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = insets;
	}

	// Add a component to specified row and col.
	public void addComponent(JComponent component, int row, int col) {
		addComponent(component, row, col, C_WIDTH, C_HEIGHT, C_WEST, C_NONE);
	}

	// Add component to specified row and col, spanning across
	// a specified number of columns and rows.
	public void addComponent(JComponent component, int row, int col, int width,
			int height) {
		addComponent(component, row, col, width, height, C_WEST, C_NONE);
	}

	// Add component to specified row and col, using a specified
	// anchor constraint
	public void addAnchoredComponent(JComponent component, int row, int col,
			int anchor) {
		addComponent(component, row, col, C_WIDTH, C_HEIGHT, anchor, C_NONE);
	}

	// Add component to specified row and col, spanning across
	// a specified number of columns and rows, using a specified
	// anchor constraint
	public void addAnchoredComponent(JComponent component, int row, int col,
			int width, int height, int anchor) {
		addComponent(component, row, col, width, height, anchor, C_NONE);
	}

	// Add component to specified row and col
	// filling the column horizontally.
	public void addFilledComponent(JComponent component, int row, int col) {
		addComponent(component, row, col, C_WIDTH, C_HEIGHT, C_WEST, C_HORZ);
	}

	// Add component to the specified row and col
	// with the specified fill constraint.
	public void addFilledComponent(JComponent component, int row, int col,
			int fill) {
		addComponent(component, row, col, C_WIDTH, C_HEIGHT, C_WEST, fill);
	}

	// Add component to the specified row and col,
	// spanning a specified number of columns and rows,
	// with specified fill constraint
	public void addFilledComponent(JComponent component, int row, int col,
			int width, int height, int fill) {
		addComponent(component, row, col, width, height, C_WEST, fill);
	}

	// Add component to the specified row and col,
	// spanning specified number of columns and rows, with
	// specified fill and anchor constraints
	public void addComponent(JComponent component, int row, int col, int width,
			int height, int anchor, int fill) {
		constraints.gridx = col;
		constraints.gridy = row;
		constraints.gridwidth = width;
		constraints.gridheight = height;
		constraints.anchor = anchor;
		double weightx = 0.0;
		double weighty = 0.0;

		// only use extra horizontal or vertical space if component
		// spans more than one column and/or row.
		if (width > 1)
			weightx = 1.0;
		if (height > 1)
			weighty = 1.0;

		switch (fill) {
		case GridBagConstraints.HORIZONTAL:
			constraints.weightx = weightx;
			constraints.weighty = 0.0;
			break;
		case GridBagConstraints.VERTICAL:
			constraints.weighty = weighty;
			constraints.weightx = 0.0;
			break;
		case GridBagConstraints.BOTH:
			constraints.weightx = weightx;
			constraints.weighty = weighty;
			break;
		case GridBagConstraints.NONE:
			constraints.weightx = 0.0;
			constraints.weighty = 0.0;
			break;
		default:
			break;
		}
		constraints.fill = fill;
		add(component, constraints);
	}
}

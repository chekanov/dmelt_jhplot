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

import java.awt.event.*;

import javax.swing.border.*;

import jplot.GraphGeneral;
import jplot.Utils;

/**
 * SavePanel build a panel used to save the graph to some format (JPEG for the
 * time being, waiting for a decent EPS/PS decoder).
 * 
 * Extends JPanel and not JDialog since this allows to include, if needed later,
 * the panel within another widget. Calling the <code>show</code> method pops up
 * a frame (dialog) which includes the panel.
 */
public class PanelSave extends PanelGridUI {

	private static final long serialVersionUID = 1L;

	private JDialog dialog;
	private JComboBox format;
	private JTextField saveTo;
	private JTextField imageWidth;
	private JTextField imageHeight;
	private final String[] formats = { "jpeg" };
	private final Dimension mediumField = new Dimension(160, 26);
	private final Dimension shortField = new Dimension(70, 26);

	/**
	 * Principal constructor, builds the panel.
	 */
	public PanelSave(GraphGeneral g) {
		dialog = null;
		// setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));

		// build the widgets:
		EmptyBorder border = new EmptyBorder(new Insets(0, 0, 0, 10));
		JLabel label = new JLabel("Format:");
		label.setBorder(border);
		format = new JComboBox(formats);
		format.setPreferredSize(mediumField);
		format.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO: add action
			}
		});
		addComponent(label, 1, 1);
		addFilledComponent(format, 1, 2, 2, 1, GridBagConstraints.HORIZONTAL);

		label = new JLabel("Save to:");
		label.setBorder(border);
		saveTo = new JTextField("out.jpg");
		saveTo.setPreferredSize(mediumField);
		addComponent(label, 2, 1);
		addFilledComponent(saveTo, 2, 2, 2, 1, GridBagConstraints.HORIZONTAL);
		// addComponent(saveTo,2,2);

		label = new JLabel("Image size:");
		label.setBorder(border);
		JPanel p = new JPanel(new BorderLayout());
		p.add(Utils.makeLabel("width"), BorderLayout.WEST);
		imageWidth = new JTextField("570");
		p.add(imageWidth, BorderLayout.CENTER);
		p.setPreferredSize(shortField);
		addComponent(label, 3, 1);
		addComponent(p, 3, 2);

		p = new JPanel(new BorderLayout());
		p.add(Utils.makeLabel("height"), BorderLayout.WEST);
		imageHeight = new JTextField("410");
		p.add(imageHeight, BorderLayout.CENTER);
		p.setPreferredSize(shortField);
		addComponent(p, 3, 3);
	}


	

	/**
	 * Updates the panel with the current graphical parameters.
	 */
	public void refresh() {
		refresh();
	}

	

	/**
	 * Return a modal JDialog.
	 * 
	 * @param parent
	 *            frame which is the parent of this dialog.
	 * @param x
	 *            x-position of the upper left corner
	 * @param y
	 *            y-position of the upper left corner
	 */
	public void show(Frame parent, int x, int y) {
		if (dialog == null) {
			dialog = new JDialog(parent, "Save graph", false);
			dialog.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					dialog.dispose();
				}
			});

			JPanel p = new JPanel(new FlowLayout());
			p.setBorder(BorderFactory.createEtchedBorder());
			JButton b = new JButton("Apply");
			b.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dialog.dispose();
				}
			});
			p.add(b);
			b = new JButton("Dismiss");
			b.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dialog.dispose();
				}
			});
			p.add(b);
			dialog.getContentPane().add(this, BorderLayout.CENTER);
			dialog.getContentPane().add(p, BorderLayout.SOUTH);
			dialog.setLocation(x, y);
			dialog.pack();
		}
		refresh();
		dialog.setVisible(true); // blocks until user brings dialog down.
	}
}

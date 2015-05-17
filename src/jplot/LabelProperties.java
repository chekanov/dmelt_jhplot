/**
 *    Copyright (C)  DataMelt project. 
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

package jplot;

import java.awt.*;

import javax.swing.*;

import java.awt.event.*;

import javax.swing.border.*;

import jplot.panels.PanelFonts;
import jplot.panels.PanelGridUI;

/**
 * This class pops up a panel with settings used by the labels. Large part of
 * the panel concerns the font and text color, other parts define the exact
 * location (x,y in pixels) and rotation of the label.
 */
public class LabelProperties extends PanelGridUI {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField labelText;
	private PanelFonts fontPanel;
	private JCheckBox autoPos, manuPos;
	private JRadioButton hide;
	private JTextField xPosField, yPosField, rotField;
	private JDialog dialog;
	private boolean result;
	private JPlot jplot;
	private int type; // type of the label, i.e., TITLE, XLABEL, YLABEL...

	
	/**
	 * Principal constructor. Builds a panel with widgets which set the settings
	 * for one specific label.
	 */
	public LabelProperties(JPlot jp) {

		jplot = jp;
		// point = new Point(0,0);
		// rotation = 0.0;

		// make the widgets:
		fontPanel = new PanelFonts(jplot, Utils.getDefaultFont(), Color.black);

		// layout parameters:
		Dimension longField = new Dimension(180, 22);
		Dimension shortField = new Dimension(80, 22);
		Dimension cbField = new Dimension(110, 20);
		EmptyBorder border = new EmptyBorder(new Insets(5, 5, 5, 5));

		PanelGridUI labelPanel = new PanelGridUI();
		labelPanel.setBorder(new EtchedBorder());

		// lay out the label text:
		JLabel label = new JLabel("Label:");
		labelText = new JTextField();
		labelText.setPreferredSize(longField);
		label.setBorder(border);
		labelPanel.addComponent(label, 1, 1);
		labelPanel.addFilledComponent(labelText, 1, 2, 2, 1,
				GridBagConstraints.HORIZONTAL);
		// addFilledComponent(labelText,1,2);

		// rotation:
		label = new JLabel("Rotation:");
		label.setBorder(border);
		labelPanel.addComponent(label, 2, 1);
		rotField = new JTextField("0.0");
		rotField.setEnabled(true);
		rotField.setPreferredSize(shortField);
		labelPanel.addComponent(rotField, 2, 2);

		// put location stuff:
		label = new JLabel("Position:");
		label.setBorder(border);
		labelPanel.addComponent(label, 3, 1);

		ButtonGroup bg = new ButtonGroup();
		autoPos = new JCheckBox("automatic", true);
		autoPos.setPreferredSize(cbField);
		autoPos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				xPosField.setEnabled(false);
				yPosField.setEnabled(false);
			}
		});
		bg.add(autoPos);
		labelPanel.addComponent(autoPos, 3, 2);

		manuPos = new JCheckBox("manual", false);
		manuPos.setPreferredSize(cbField);
		manuPos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				xPosField.setEnabled(true);
				yPosField.setEnabled(true);
			}
		});
		bg.add(manuPos);
		labelPanel.addComponent(manuPos, 3, 3);

		JPanel posPanel = new JPanel(new BorderLayout());
		posPanel.add(new JLabel("x: "), BorderLayout.WEST);
		xPosField = new JTextField("0.0");
		xPosField.setEnabled(true);
		posPanel.add(xPosField, BorderLayout.CENTER);
		posPanel.setPreferredSize(shortField);
		labelPanel.addComponent(posPanel, 4, 2);

		posPanel = new JPanel(new BorderLayout());
		posPanel.add(new JLabel("y: "), BorderLayout.WEST);
		yPosField = new JTextField("0.0");
		yPosField.setEnabled(true);
		posPanel.add(yPosField, BorderLayout.CENTER);
		posPanel.setPreferredSize(shortField);
		labelPanel.addComponent(posPanel, 4, 3);

		hide = new JRadioButton("hide", false);
		hide.setPreferredSize(cbField);
		labelPanel.addComponent(hide, 5, 2);

		addComponent(labelPanel, 1, 1);

		// and add the font panel:
		addFilledComponent(fontPanel, 2, 1, 3, 1, GridBagConstraints.VERTICAL);
	}

	/**
	 * @return the current properties of this label in terms of a new graph
	 *         label object.
	 */
	public GraphLabel getGraphLabel() {
		GraphLabel gl = new GraphLabel(type, labelText.getText(),
				fontPanel.getSelectedFont(), fontPanel.getSelectedColor());
		gl.setRotation(Double.parseDouble(rotField.getText()) * Math.PI / 180);
		if (manuPos.isSelected()) {
			gl.setLocation(Double.parseDouble(xPosField.getText()),
					Double.parseDouble(yPosField.getText()));
		}
		gl.setUsePosition(!autoPos.isSelected());
		gl.hide(hide.isSelected());
		return gl;
	}

	/**
	 * Updates the panel with a graph label object.
	 * 
	 * @param gl
	 *            graph label object used to refresh this class.
	 */
	public void refresh(GraphLabel gl) {
		type = gl.getID();
		labelText.setText(gl.getText());
		xPosField.setText(Integer.toString((int) gl.getX()));
		yPosField.setText(Integer.toString((int) gl.getY()));
		rotField.setText(GraphGeneral.formatNumber(gl.getRotation() / Math.PI * 180, 4));
		autoPos.setSelected(!gl.usePosition());
		manuPos.setSelected(gl.usePosition());
		hide.setSelected(gl.hide());
		fontPanel.refresh(gl.getFont(), gl.getColor());
	}

	/**
	 * Pops up a modal frame including the font panel. Returns the actual font
	 * or null if the user pressed the cancel button.
	 * 
	 * @param parent
	 *            parent frame
	 * @param x
	 *            x-position of the dialog
	 * @param y
	 *            y-position of the dialog
	 * @return true if the user validated the current stuff, false otherwise
	 */
	public boolean show(Frame parent, int x, int y) {
		if (dialog == null) {
			JPanel panel = new JPanel(new BorderLayout());
			dialog = new JDialog(parent, "Textlabel settings", true);
			dialog.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					result = false;
					dialog.dispose();
				}
			});
			JPanel p = new JPanel(new FlowLayout());
			p.setBorder(BorderFactory.createEtchedBorder());
			JButton b = new JButton("Apply");
			b.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					result = true;
					dialog.dispose();
				}
			});
			p.add(b);
			b = new JButton("Cancel");
			b.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					result = false;
					dialog.dispose();
				}
			});
			p.add(b);
			panel.add(this, BorderLayout.CENTER);
			panel.add(p, BorderLayout.SOUTH);
			dialog.getContentPane().add(panel);
			dialog.setLocation(x, y);
			dialog.pack();
		}
		dialog.setVisible(true); // blocks until user brings dialog down.
		return result;
	}
}

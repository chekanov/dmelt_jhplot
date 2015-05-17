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
import java.util.*;

import javax.swing.*;

import java.awt.geom.*;
import java.awt.event.*;

import javax.swing.border.*;
import javax.swing.colorchooser.*;

import jplot.GraphSettings;
import jplot.JPlot;
import jplot.Utils;

/**
 * ScalingPanel creates a panel which allows a user to modify the way the graph
 * is scaled on the axes. For example, by setting the maximum and minimum values
 * of the X-axis instead of using an automatic scaling. Calling the
 * <code>show</code> method pops up a frame (dialog) which includes the panel.
 */
public class PanelScaling extends JPanel {

	private static final long serialVersionUID = 1L;
	private JDialog dialog;
	private JCheckBox[] cb_auto = new JCheckBox[GraphSettings.N_AXES];
	private JCheckBox[] cb_manu = new JCheckBox[GraphSettings.N_AXES];
	private JCheckBox[] cb_log = new JCheckBox[GraphSettings.N_AXES];
	private JCheckBox[] cb_lin = new JCheckBox[GraphSettings.N_AXES];
	private JTextField[] to = new JTextField[GraphSettings.N_AXES];
	private JTextField[] from = new JTextField[GraphSettings.N_AXES];
	private GraphSettings gs;
	private JPlot jplot;

	/**
	 * Principal constructor. Builds a panel with X and Y options
	 * 
	 * @param jp
	 *            instance of the parent (generally JPlot)
	 * @param gs
	 *            instance of the object containing all graphical parameters
	 */
	public PanelScaling(JPlot jp, GraphSettings gs) {
		this.gs = gs;
		jplot = jp;
		dialog = null;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		for (int k = 0; k < gs.N_AXES; k++)
			add(getPanel(k));
	}

	/**
	 * Builds a panel with the scaling options for one of the axes.
	 * 
	 * @param axis
	 *            axis for which this panel is built.
	 */
	private PanelGridUI getPanel(final int axis) {
		String labelText = (axis == gs.X_AXIS) ? "X-scaling:" : "Y-scaling:";

		PanelGridUI p = new PanelGridUI();
		p.setBorder(new EmptyBorder(new Insets(5, 5, 5, 5)));

		Dimension cbField = new Dimension(110, 20);
		Dimension shortField = new Dimension(80, 22);

		// build the widgets:
		ButtonGroup bg = new ButtonGroup();
		cb_lin[axis] = new JCheckBox("linear", !gs.useLogScale(axis));
		cb_lin[axis].setPreferredSize(cbField);
		bg.add(cb_lin[axis]);
		cb_log[axis] = new JCheckBox("logarithmic", gs.useLogScale(axis));
		cb_log[axis].setPreferredSize(cbField);
		bg.add(cb_log[axis]);

		// divide[axis] = new JTextField();
		// offset[axis] = new JTextField();

		bg = new ButtonGroup();
		cb_auto[axis] = new JCheckBox("automatic", gs.autoRange(axis));
		cb_auto[axis].setPreferredSize(cbField);
		cb_auto[axis].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				from[axis].setEnabled(false);
				to[axis].setEnabled(false);
			}
		});
		bg.add(cb_auto[axis]);
		cb_manu[axis] = new JCheckBox("manual", !gs.autoRange(axis));
		cb_manu[axis].setPreferredSize(cbField);
		cb_manu[axis].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				from[axis].setEnabled(true);
				to[axis].setEnabled(true);
			}
		});
		bg.add(cb_manu[axis]);

		JPanel fromPanel = new JPanel(new BorderLayout());
		fromPanel.add(Utils.makeLabel("from"), BorderLayout.WEST);
		from[axis] = new JTextField(
				(new Float(gs.getMinValue(axis))).toString());
		from[axis].setEnabled(cb_manu[axis].isSelected());
		fromPanel.add(from[axis], BorderLayout.CENTER);
		fromPanel.setPreferredSize(shortField);

		JPanel toPanel = new JPanel(new BorderLayout());
		toPanel.add(Utils.makeLabel("to"), BorderLayout.WEST);
		to[axis] = new JTextField((new Float(gs.getMaxValue(axis))).toString());
		to[axis].setEnabled(cb_manu[axis].isSelected());
		toPanel.add(to[axis], BorderLayout.CENTER);
		toPanel.setPreferredSize(shortField);

		// spacing between labels and fields:
		EmptyBorder border = new EmptyBorder(new Insets(0, 0, 0, 10));

		// place the widgets in the panel:
		JLabel label = new JLabel(labelText);
		label.setBorder(border);
		p.addComponent(label, 1, 1);
		p.addComponent(cb_lin[axis], 1, 2);
		p.addComponent(cb_log[axis], 1, 3);
		// label = new JLabel("divide by **CHECK** ");
		// label.setBorder(border);
		// p.addComponent(label,2,2);
		// p.addFilledComponent(divide[axis],2,3);
		// label = new JLabel("offset **CHECK**");
		// label.setBorder(border);
		// p.addComponent(label,3,2);
		// p.addFilledComponent(offset[axis],3,3);
		p.addComponent(cb_auto[axis], 4, 2);
		p.addComponent(cb_manu[axis], 4, 3);
		p.addFilledComponent(fromPanel, 5, 2);
		p.addFilledComponent(toPanel, 5, 3);
		return p;
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
	public void showIt(Frame parent, int x, int y) {
		if (dialog == null) {
			dialog = new JDialog(parent, "Scaling options", false);
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
					setValues();
					gs.setDataChanged(true);
					jplot.updateGraphIfShowing();
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
	
	
	
	
	
	
	
	
	
	
	/**
	 * Updates the panel with the current graphical parameters.
	 */
	public void refresh() {
		for (int axis = 0; axis < gs.N_AXES; axis++) {
			cb_auto[axis].setSelected(gs.autoRange(axis));
			cb_manu[axis].setSelected(!gs.autoRange(axis));
			from[axis].setText(Float.toString((float) gs.getMinValue(axis)));
			from[axis].setEnabled(!cb_auto[axis].isSelected());
			to[axis].setText(Float.toString((float) gs.getMaxValue(axis)));
			to[axis].setEnabled(!cb_auto[axis].isSelected());
			cb_log[axis].setSelected(gs.useLogScale(axis));
			cb_lin[axis].setSelected(!gs.useLogScale(axis));
			// divide[axis].setText(Float.toString(gs.globalDivider[axis]));
			// offset[axis].setText(Float.toString(gs.globalOffset[axis]));
		}
	}

	public void setValues() {
		for (int k = 0; k < gs.N_AXES; k++) {
			gs.setAutoRange(k, cb_auto[k].isSelected());
			gs.setUseLogScale(k, cb_log[k].isSelected());
			// gs.globalDivider[k] = Float.parseFloat(divide[k].getText());
			// gs.globalOffset[k] = Float.parseFloat(offset[k].getText());
			if (!cb_auto[k].isSelected()) {
				if (!from[k].getText().equals("")) {
					gs.setMinValue(k,
							(new Double(from[k].getText())).doubleValue());
				}
				if (!to[k].getText().equals("")) {
					gs.setMaxValue(k,
							(new Double(to[k].getText())).doubleValue());
				}
				if (cb_log[k].isSelected()
						&& (gs.getMinValue(k) <= 0.0 || gs.getMaxValue(k) <= 0.0)) {
					Utils.bummer(jplot.frame,
							"Don't do this!\nLogarithmic axes require positive data.\n Ignoring...\n");
					gs.setUseLogScale(k, false);
				}
			}
		}
	}

	
}

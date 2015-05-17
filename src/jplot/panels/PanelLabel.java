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

import java.awt.event.*;

import javax.swing.border.*;

import jplot.GraphLabel;
import jplot.GraphSettings;
import jplot.JPlot;
import jplot.LabelProperties;

/**
 * Create a panel with options to set labels (X-, Y-axis labels, a title, random
 * labels or text in general). Note that the number of labels and names of the
 * pre-defined labels may change as a function of the graph type: for ordinary
 * 2D graphs, we typically pre-define a title, y-label and x-label but for a
 * piper diagram we have 6 pre-defined labels...
 * 
 * Calling the <code>show</code> method pops up a frame (dialog) which includes
 * the panel.
 */
public class PanelLabel extends PanelGridUI {

	private static final long serialVersionUID = 1L;
	private int N;
	private JTextField[] textField;
	// this list contains the temporary labels with properties,
	// replaces the list in the Data-class only if the user validates
	// the current choice ---adds a kind of poor-man's undo capacity.
	private GraphLabel[] gLabel;
	// all other 'random' labels go in this vector and list
	private Vector otherLabels = new Vector();
	private JList labelList;
	private JDialog dialog;
	private LabelProperties labelProperties;
	private int actualItemIndex;
	private int[] actualItemIndices;

	// buttons and tooltips which allow to set the label text properties:
	private JButton[] edit;
	private String[] toolTip;
	private JLabel[] label;

	// same thing for the otherLabels:
	private JButton olAdd, olEdit, olDelete, xTicFont, yTicFont;
	private JRadioButton xTicLabels, yTicLabels;
	private PanelFonts ticFontPanel;

	// panels on which we pin the textfields and buttons:
	private JPanel[] labelPanel;
	private JPanel oLabelPanel;

	private GraphSettings gs;
	private JRadioButton rb_reset; // resets all label positions

	private final Dimension myDimension = new Dimension(100, 50);
	private final Insets insets = new Insets(0, 0, 0, 0);

	private JPlot jplot;

	/**
	 * Principal constructor. Builds a panel from which the user can set text
	 * labels which will be drawn inside the graph.
	 * 
	 * @param jp
	 *            instance of the parent of this class (generally JPlot).
	 * @param settings
	 *            instance of the object containing all graphical parameters
	 */
	public PanelLabel(JPlot jp, GraphSettings settings) {
		int k = 0;
		gs = settings;
		jplot = jp;
		dialog = null;
		setBorder(new EmptyBorder(new Insets(5, 5, 5, 5)));

		rb_reset = new JRadioButton("reset all label positions");

		// spacing between labels and fields:
		EmptyBorder border = new EmptyBorder(new Insets(0, 0, 0, 10));
		ImageIcon editIcon = jplot.getImageIcon("Edit16.gif");

		N = (gs.getGraphType() == gs.GRAPHTYPE_PIPER) ? 6 : 3;
		textField = new JTextField[N];
		gLabel = new GraphLabel[N];
		edit = new JButton[N];
		toolTip = new String[N];
		label = new JLabel[N];
		labelPanel = new JPanel[N];

		// make the labels in case it is a piper graph:
		if (gs.getGraphType() == gs.GRAPHTYPE_PIPER) {
			gLabel[0] = new GraphLabel(GraphLabel.PIPER_X1);
			toolTip[0] = new String("Left X-label");
			label[0] = new JLabel("X1-label:");
			gLabel[1] = new GraphLabel(GraphLabel.PIPER_X2);
			toolTip[1] = new String("Right X-label");
			label[1] = new JLabel("X2-label:");
			gLabel[2] = new GraphLabel(GraphLabel.PIPER_Y1);
			toolTip[2] = new String("Lower left Y-label");
			label[2] = new JLabel("Y1-label:");
			gLabel[3] = new GraphLabel(GraphLabel.PIPER_Y2);
			toolTip[3] = new String("Lower right Y-label");
			label[3] = new JLabel("Y2-label:");
			gLabel[4] = new GraphLabel(GraphLabel.PIPER_Y3);
			toolTip[4] = new String("Upper left Y-label");
			label[4] = new JLabel("Y3-label:");
			gLabel[5] = new GraphLabel(GraphLabel.PIPER_Y4);
			toolTip[5] = new String("Upper right Y-label");
			label[5] = new JLabel("Y4-label:");
		}

		// make the labels in case it is a 2D graph:
		else {
			gLabel[0] = new GraphLabel(GraphLabel.TITLE);
			gLabel[0].setFont(new Font("Helvetica", Font.BOLD, 12));
			toolTip[0] = new String("Edit the title properties");
			label[0] = new JLabel("Graph title");
			gLabel[1] = new GraphLabel(GraphLabel.XLABEL);
			toolTip[1] = new String("Edit the X-label properties");
			label[1] = new JLabel("X-axis label");
			gLabel[2] = new GraphLabel(GraphLabel.YLABEL);
			toolTip[2] = new String("Edit the Y-label properties");
			label[2] = new JLabel("Y-axis label");
			// set some default label properties:
			gLabel[2].setRotation(-Math.PI / 2.0);
		}

		// build the label-properties pop-up dialog:
		labelProperties = new LabelProperties(jplot);

		for (int i = 0; i < N; i++, k++) {
			final int j = i;
			textField[i] = new JTextField();
			edit[i] = new JButton(editIcon);
			edit[i].setMargin(insets);
			edit[i].setToolTipText(toolTip[i]);
			edit[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					gLabel[j].setText(textField[j].getText());
					labelProperties.refresh(gLabel[j]);
					if (labelProperties.show(jplot.frame, 100, 100)) {
						gLabel[j] = labelProperties.getGraphLabel();
						setValues(); // added for 1.3: immediate update
						update();
					}
				}
			});
			labelPanel[i] = new JPanel(new BorderLayout());
			labelPanel[i].add(textField[i], BorderLayout.CENTER);
			labelPanel[i].add(edit[i], BorderLayout.EAST);

			label[i].setBorder(border);
			addComponent(label[i], i, 1);
			addFilledComponent(labelPanel[i], i, 2, 3, 1,
					GridBagConstraints.HORIZONTAL);
		}

		// list of 'other' (random) labels
		actualItemIndex = -1;
		otherLabels = new Vector();
		labelList = new JList(new DefaultListModel());
		labelList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				actualItemIndex = labelList.getSelectedIndex();
				actualItemIndices = labelList.getSelectedIndices();
				if (e.getClickCount() == 2) {
					editOtherLabel();
				}
			}
		});
		JScrollPane sp_otherLabels = new JScrollPane(labelList);
		sp_otherLabels.setPreferredSize(myDimension);
		oLabelPanel = new JPanel(new BorderLayout());
		oLabelPanel.add(sp_otherLabels, BorderLayout.CENTER);

		// make a panel for the buttons:
		JPanel bPanel = new JPanel(new GridLayout(3, 1));

		// add button for the 'other' labels
		olAdd = new JButton(jplot.getImageIcon("insert.png"));
		olAdd.setMargin(insets);
		olAdd.setToolTipText("Add a random label");
		olAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				labelProperties.refresh(new GraphLabel(GraphLabel.OTHER));
				if (labelProperties.show(jplot.frame, 100, 100)) {
					otherLabels.add(labelProperties.getGraphLabel());
					setValues(); // added for 1.3: immediate update
					update();
				}
			}
		});
		bPanel.add(olAdd);

		// edit button for the 'other' labels
		olEdit = new JButton(editIcon);
		olEdit.setMargin(insets);
		olEdit.setToolTipText("Edit the selected label properties");
		olEdit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editOtherLabel();
			}
		});
		bPanel.add(olEdit);

		// delete button for the 'other' labels
		olDelete = new JButton(jplot.getImageIcon("Remove16.gif"));
		olDelete.setMargin(insets);
		olDelete.setToolTipText("Remove the selected label(s)");
		olDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (actualItemIndices != null) {
					for (int i = actualItemIndices.length - 1; i >= 0; i--) {
						otherLabels.removeElementAt(actualItemIndices[i]);
					}
					update();
				}
			}
		});
		bPanel.add(olDelete);
		oLabelPanel.add(bPanel, BorderLayout.EAST);

		JLabel l = new JLabel("Random labels");
		l.setBorder(border);
		addComponent(l, k, 1);
		addFilledComponent(oLabelPanel, k, 2, 3, 3,
				GridBagConstraints.HORIZONTAL);
		k += 4;
		addComponent(rb_reset, k, 2, 2, 1);

		k++;
		l = new JLabel("X tic-labels");
		l.setBorder(border);
		addComponent(l, k, 1);
		xTicLabels = new JRadioButton("show");
		xTicLabels.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gs.setDrawTicLabels(gs.X_AXIS, xTicLabels.isSelected());
			}
		});
		xTicLabels.setSelected(true);
		addComponent(xTicLabels, k, 2);

		xTicFont = new JButton("Change font...", editIcon);
		// xTicFont.setMargin(insets);
		// xTicFont.setPreferredSize(new Dimension(26,80));
		xTicFont.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (ticFontPanel == null) {
					ticFontPanel = new PanelFonts(jplot, gs
							.getTicFont(gs.X_AXIS), gs.getTicColor(gs.X_AXIS));
				} else
					ticFontPanel.refresh(gs.getTicFont(gs.X_AXIS),
							gs.getTicColor(gs.X_AXIS));
				ticFontPanel.show(jplot.frame, 150, 150);
				gs.setTicFont(gs.X_AXIS, ticFontPanel.getSelectedFont());
				gs.setTicColor(gs.X_AXIS, ticFontPanel.getSelectedColor());
			}
		});
		addComponent(xTicFont, k, 3);

		k++;
		l = new JLabel("Y tic-labels");
		l.setBorder(border);
		addComponent(l, k, 1);
		yTicLabels = new JRadioButton("show");
		yTicLabels.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gs.setDrawTicLabels(gs.Y_AXIS, yTicLabels.isSelected());
			}
		});
		yTicLabels.setSelected(true);
		addComponent(yTicLabels, k, 2);

		yTicFont = new JButton("Change font...", editIcon);
		// yTicFont.setMargin(insets);
		// yTicFont.setPreferredSize(new Dimension(26,80));
		yTicFont.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (ticFontPanel == null) {
					ticFontPanel = new PanelFonts(jplot, gs
							.getTicFont(gs.Y_AXIS), gs.getTicColor(gs.Y_AXIS));
				} else
					ticFontPanel.refresh(gs.getTicFont(gs.Y_AXIS),
							gs.getTicColor(gs.Y_AXIS));
				ticFontPanel.show(jplot.frame, 150, 150);
				gs.setTicFont(gs.Y_AXIS, ticFontPanel.getSelectedFont());
				gs.setTicColor(gs.Y_AXIS, ticFontPanel.getSelectedColor());
			}
		});
		addComponent(yTicFont, k, 3);
	}

	/*
	 * Open an editor for editing one of the random labels. Searches the label
	 * in the list of labels.
	 */
	private void editOtherLabel() {
		if (actualItemIndex > -1 && actualItemIndex < otherLabels.size()) {
			labelProperties.refresh((GraphLabel) otherLabels
					.get(actualItemIndex));
			if (labelProperties.show(jplot.frame, 100, 100)) {
				otherLabels.set(actualItemIndex,
						labelProperties.getGraphLabel());
			}
		}
	}

	/*
	 * Update the label fields with the current labels.
	 */
	private void update() {
		for (int i = 0; i < N; i++) {
			textField[i].setText(gLabel[i].getText());
			textField[i].setFont(gLabel[i].getFont());
			textField[i].setForeground(gLabel[i].getColor());
			if (gLabel[i].getFont().getSize() > 14) {
				textField[i].setFont(gLabel[i].getFont().deriveFont(14f));
			}
		}
		labelList.setListData(otherLabels);
	}

	/**
	 * Updates the panel with graph parameters.
	 */
	public void refresh() {
		otherLabels.clear();
		for (Enumeration el = gs.getLabels().elements(); el.hasMoreElements();) {
			GraphLabel gl = (GraphLabel) el.nextElement();
			if (gs.getGraphType() == gs.GRAPHTYPE_PIPER) {
				if (gl.getID() == GraphLabel.PIPER_X1) {
					gLabel[0] = new GraphLabel(gl);
				} else if (gl.getID() == GraphLabel.PIPER_X2) {
					gLabel[1] = new GraphLabel(gl);
				} else if (gl.getID() == GraphLabel.PIPER_Y1) {
					gLabel[2] = new GraphLabel(gl);
				} else if (gl.getID() == GraphLabel.PIPER_Y2) {
					gLabel[3] = new GraphLabel(gl);
				} else if (gl.getID() == GraphLabel.PIPER_Y3) {
					gLabel[4] = new GraphLabel(gl);
				} else if (gl.getID() == GraphLabel.PIPER_Y4) {
					gLabel[5] = new GraphLabel(gl);
				} else
					otherLabels.add(new GraphLabel(gl));
			} else {
				if (gl.getID() == GraphLabel.TITLE) {
					gLabel[0] = new GraphLabel(gl);
				} else if (gl.getID() == GraphLabel.XLABEL) {
					gLabel[1] = new GraphLabel(gl);
				} else if (gl.getID() == GraphLabel.YLABEL) {
					gLabel[2] = new GraphLabel(gl);
				} else
					otherLabels.add(new GraphLabel(gl));
			}
		}
		update();
		rb_reset.setSelected(false);
	}

	/**
	 * Return the current values. Note that the user may type the label text
	 * directly in the fields for xLabel, yLabel and title. If so, this text is
	 * copied in the label object.
	 */
	public void setValues() {
		gs.getLabels().clear();
		for (int i = 0; i < N; i++) {
			gLabel[i].setText(textField[i].getText());
			if (!gLabel[i].getText().equals(""))
				gs.addLabel(gLabel[i]);
		}
		for (Enumeration e = otherLabels.elements(); e.hasMoreElements();) {
			GraphLabel gl = (GraphLabel) e.nextElement();
			gs.addLabel(gl);
		}
		if (rb_reset.isSelected()) {
			gs.resetLabels();
			rb_reset.setSelected(false);
		}
		gs.setDataChanged(true);
		jplot.updateGraphIfShowing();
	}

	/**
	 * Return a modal JDialog.
	 */
	public void show(Frame parent, int x, int y) {
		if (dialog == null) {
			dialog = new JDialog(parent, "Labels", false);
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

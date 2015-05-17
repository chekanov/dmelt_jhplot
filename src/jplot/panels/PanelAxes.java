/**
 *    Copyright (C)  DataMelt project. The jHPLot package.
 *    Includes coding developed for Centre d'Informatique Geologique                 
 *    Ecole des Mines de Paris, Fontainebleau, France by J.V.Lee         
 *    priory 2000 GNU license.          
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
import javax.swing.border.*;

import java.awt.*;
import java.awt.event.*;

import jplot.GraphSettings;
import jplot.JPlot;
import jplot.LinePars;
import jplot.LineStyle;
import jplot.SmallColorPreview;
import jplot.Utils;

/**
 * Builds a panel with Tabs, containing optional parameters for labels, legends,
 * colors, etc. Calling the <code>show</code> method shows a dialog with apply
 * and cancel buttons.
 */
public class PanelAxes extends JPanel {

	
	private static final long serialVersionUID = 1L;
	private JTabbedPane tabbedPane;
	private JDialog dialog;
	final private String title = "Axes settings";

	private PanelColor colorPanel;
	private AxesOptions axesPanel;
	private GridOptions gridPanel;
	private BoxOptions boxPanel;
	private GraphSettings gs;
	private JPlot jplot;

	public PanelAxes(JPlot jp, GraphSettings _gs) {
		gs = _gs;
		jplot = jp;

		tabbedPane = new JTabbedPane();
		// tabbedPane.setTabPlacement(SwingConstants.LEFT);

		// add the options tabs:
		axesPanel = new AxesOptions();
		tabbedPane.add("Axes and tics", axesPanel);
		gridPanel = new GridOptions();
		tabbedPane.add("Grid options", gridPanel);
		boxPanel = new BoxOptions();
		tabbedPane.add("Boxes and fillcolors", boxPanel);
		add(tabbedPane);

		// build the dialog
		JPanel panel = new JPanel(new BorderLayout());
		dialog = new JDialog(jplot.frame, title, false);
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
		panel.add(this, BorderLayout.CENTER);
		panel.add(p, BorderLayout.SOUTH);
		dialog.getContentPane().add(panel);
	}

	public void setValues() {
		axesPanel.setValues();
		gridPanel.setValues();
		boxPanel.setValues();
	}

	public void refresh() {
		axesPanel.refresh();
		gridPanel.refresh();
		boxPanel.refresh();
	}

	public void show(int x, int y) {
		refresh();
		dialog.setLocation(x, y);
		dialog.pack();
		dialog.setVisible(true);
	}

	private Color getColor(Color color) {
		if (colorPanel == null) {
			colorPanel = new PanelColor(jplot.frame, color);
		} else
			colorPanel.refresh(color);
		return colorPanel.show(jplot.frame, color, 100, 100);
	}

	public class AxesOptions extends JPanel {

		private JCheckBox[] cb_showAxis = new JCheckBox[gs.N_AXES];
		private JCheckBox[] cb_mirrorAxis = new JCheckBox[gs.N_AXES];
		private JCheckBox[] cb_showTics = new JCheckBox[gs.N_AXES];
		private JCheckBox[] cb_mirrorTics = new JCheckBox[gs.N_AXES];
		private JCheckBox[] cb_rotateTics = new JCheckBox[gs.N_AXES];
		private JCheckBox[] cb_numTics = new JCheckBox[gs.N_AXES];
		private JTextField[] numTics = new JTextField[gs.N_AXES];
		private JTextField[] tlField = new JTextField[gs.N_AXES];

		private LineStyle lineStyle;
		private Color axesColor;
		private JPanel linePanel;

		private JCheckBox cb_autoRatio;
		private JCheckBox cb_manuRatio;
		private JTextField ratio;

		/**
		 * Principal constructor. Builds a panel with axes options.
		 * 
		 * @param gp
		 *            instance of the object containing all graphical parameters
		 */
		public AxesOptions() {
			EmptyBorder border = new EmptyBorder(new Insets(0, 0, 0, 10));
			setBorder(new EmptyBorder(new Insets(5, 5, 5, 5)));
			Dimension mediumField = new Dimension(100, 20);
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			JLabel label;
			PanelGridUI p;

			// make the widgets:
			makeRatio();
			for (int k = 0; k < gs.N_AXES; k++) {
				makeWidgets(k);
				p = new PanelGridUI();
				if (k == 0)
					p.setBorder(new TitledBorder(new EtchedBorder(), "X-Axis"));
				else
					p.setBorder(new TitledBorder(new EtchedBorder(), "Y-Axis"));
				label = new JLabel("Axis:");
				label.setBorder(border);
				p.addComponent(label, 1, 1);
				p.addComponent(cb_showAxis[k], 1, 2);
				p.addComponent(cb_mirrorAxis[k], 1, 3);

				label = new JLabel("Tics:");
				label.setBorder(border);
				p.addComponent(label, 2, 1);
				p.addComponent(cb_showTics[k], 2, 2);
				p.addComponent(cb_mirrorTics[k], 2, 3);
				p.addComponent(cb_rotateTics[k], 2, 4);

				p.addComponent(cb_numTics[k], 3, 2);
				p.addComponent(numTics[k], 3, 3);
				JPanel lPanel = new JPanel(new BorderLayout());
				lPanel.add(Utils.makeLabel("length"), BorderLayout.WEST);
				lPanel.add(tlField[k], BorderLayout.CENTER);
				p.addComponent(lPanel, 3, 4);
				add(p);
			}

			p = new PanelGridUI();
			p.setBorder(new EtchedBorder());
			label = new JLabel("X/Y ratio:");
			label.setBorder(border);
			p.addComponent(label, 1, 1);
			p.addComponent(cb_autoRatio, 1, 2);
			p.addComponent(cb_manuRatio, 1, 3);
			p.addFilledComponent(ratio, 1, 4);

			JButton b_color = new JButton(jplot.getImageIcon("color.jpg"));
			b_color.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Color color = getColor(axesColor);
					if (color != null) {
						axesColor = color;
						lineStyle.setColor(axesColor);
						linePanel.repaint();
						setValues();
						jplot.updateGraphIfShowing();
						gs.setDataChanged(true);
					}
				}
			});
			b_color.setMargin(new Insets(0, 0, 0, 0));
			b_color.setToolTipText("Set the box drawing color");

			lineStyle = new LineStyle(new LinePars());
			lineStyle.setColor(axesColor);
			linePanel = new JPanel(new FlowLayout());
			linePanel.add(lineStyle);
			linePanel.add(b_color);

			label = new JLabel("Axes color:");
			label.setBorder(border);
			p.addComponent(label, 2, 1);
			p.addComponent(linePanel, 2, 2);
			add(p);
		}

		private void makeRatio() {
			ButtonGroup bg = new ButtonGroup();
			cb_autoRatio = new JCheckBox("automatic", !gs.useAxesRatio());
			cb_autoRatio.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ratio.setEnabled(false);
				}
			});
			cb_manuRatio = new JCheckBox("manual:", gs.useAxesRatio());
			cb_manuRatio.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ratio.setEnabled(true);
				}
			});
			bg.add(cb_autoRatio);
			bg.add(cb_manuRatio);
			ratio = new JTextField(Float.toString((float) gs.getAxesRatio()));
			ratio.setColumns(7);
			ratio.setEnabled(false);
		}

		/**
		 * Builds a panel with the scaling options for the axes.
		 * 
		 * @param axis
		 *            axis for which this panel is built.
		 */
		private void makeWidgets(final int axis) {
			cb_showTics[axis] = new JCheckBox("show", gs.drawTics(axis));
			cb_showTics[axis].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					boolean b = cb_showTics[axis].isSelected() ? true : false;
					tlField[axis].setEnabled(b);
					cb_numTics[axis].setEnabled(b);
					numTics[axis].setEnabled(b);
					cb_mirrorTics[axis].setEnabled(b);
					cb_rotateTics[axis].setEnabled(b);
				}
			});

			cb_showAxis[axis] = new JCheckBox("show", gs.drawAxis(axis));
			cb_showAxis[axis].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					boolean b = cb_showAxis[axis].isSelected() ? true : false;
					cb_mirrorAxis[axis].setEnabled(b);
				}
			});

			tlField[axis] = new JTextField();
			tlField[axis].setColumns(4);

			cb_mirrorAxis[axis] = new JCheckBox("mirror",
					gs.drawMirrorAxis(axis));
			cb_mirrorTics[axis] = new JCheckBox("mirror",
					gs.drawMirrorTics(axis));
			cb_rotateTics[axis] = new JCheckBox("rotate", gs.rotateTics(axis));

			cb_numTics[axis] = new JCheckBox("set number:",
					gs.useNumberOfTics(axis));
			cb_numTics[axis].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					numTics[axis].setEnabled(cb_numTics[axis].isSelected());
				}
			});
			numTics[axis] = new JTextField(Integer.toString(gs
					.getNumberOfTics(axis)));
			numTics[axis].setColumns(4);
			numTics[axis].setEnabled(cb_numTics[axis].isSelected());
		}

		/**
		 * Updates the panel with the current graphical parameters.
		 */
		public void refresh() {
			for (int k = 0; k < gs.N_AXES; k++) {
				cb_showAxis[k].setSelected(gs.drawAxis(k));
				cb_mirrorAxis[k].setSelected(gs.drawMirrorAxis(k));
				cb_showTics[k].setSelected(gs.drawTics(k));
				cb_mirrorTics[k].setSelected(gs.drawMirrorTics(k));
				tlField[k].setText(Float.toString((float) gs.getTicLength(k)));
				cb_numTics[k].setSelected(gs.useNumberOfTics(k));
				numTics[k].setText(Integer.toString(gs.getNumberOfTics(k)));
				cb_rotateTics[k].setSelected(gs.rotateTics(k));
			}
			cb_manuRatio.setSelected(gs.useAxesRatio());
			cb_autoRatio.setSelected(!gs.useAxesRatio());
			ratio.setText(Float.toString((float) gs.getAxesRatio()));
			axesColor = gs.getAxesColor();
			lineStyle.setColor(axesColor);
		}

		public void setValues() {
			for (int k = 0; k < gs.N_AXES; k++) {
				gs.setDrawAxis(k, cb_showAxis[k].isSelected());
				gs.setDrawMirrorAxis(k, cb_mirrorAxis[k].isSelected());
				gs.setDrawTics(k, cb_showTics[k].isSelected());
				gs.setDrawMirrorTics(k, cb_mirrorTics[k].isSelected());
				gs.setTicLength(k, Float.parseFloat(tlField[k].getText()));
				gs.setUseNumberOfTics(k, cb_numTics[k].isSelected());
				gs.setRotateTics(k, cb_rotateTics[k].isSelected());

				if (cb_numTics[k].isSelected()) {
					if (!numTics[k].getText().equals("")) {
						gs.setNumberOfTics(k,
								Integer.parseInt(numTics[k].getText()));
					}
				}
			}
			gs.setUseAxesRatio(cb_manuRatio.isSelected());
			if (!ratio.getText().equals("")) {
				gs.setAxesRatio(Double.parseDouble(ratio.getText()));
			}
			gs.setAxesColor(axesColor);
			jplot.updateGraphIfShowing();
			gs.setDataChanged(true);
		}
	}

	public class GridOptions extends PanelGridUI {

		private JCheckBox cb_hg, cb_vg, cb_front, cb_back;
		private JPanel gridStylePanel, boxFillPanel, innerPanel;
		private LineStyle lineStyle;
		private Color actualColor;

		/**
		 * Principal constructor. Builds a panel with axes options.
		 * 
		 * @param gp
		 *            instance of the object containing all graphical parameters
		 */

		public GridOptions() {
			setBorder(new EmptyBorder(new Insets(5, 5, 5, 5)));
			EmptyBorder border = new EmptyBorder(new Insets(0, 0, 0, 10));

			cb_hg = new JCheckBox("y-axis", gs.drawGrid(gs.Y_AXIS));
			cb_vg = new JCheckBox("x-axis", gs.drawGrid(gs.X_AXIS));
			cb_back = new JCheckBox("back", !gs.gridToFront());
			cb_front = new JCheckBox("front", gs.gridToFront());

			ButtonGroup bg = new ButtonGroup();
			bg.add(cb_back);
			bg.add(cb_front);

			// linestyle and color panel for grid:
			lineStyle = new LineStyle(new LinePars());
			lineStyle.setColor(actualColor);
			JButton b_color = new JButton(jplot.getImageIcon("color.jpg"));
			b_color.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Color color = getColor(actualColor);
					if (color != null) {
						actualColor = color;
						lineStyle.setColor(actualColor);
						lineStyle.repaint();
						setValues();
					}
				}
			});
			b_color.setMargin(new Insets(0, 0, 0, 0));
			b_color.setToolTipText("Gridline color");
			gridStylePanel = new JPanel(new FlowLayout());
			gridStylePanel.add(lineStyle);
			gridStylePanel.add(b_color);

			JLabel label = new JLabel("Grid lines:");
			label.setBorder(border);
			addComponent(label, 1, 1);
			addComponent(cb_hg, 1, 2);
			addComponent(cb_vg, 1, 3);
			label = new JLabel("Drawing:");
			label.setBorder(border);
			addComponent(label, 2, 1);
			addComponent(cb_front, 2, 2);
			addComponent(cb_back, 2, 3);
			label = new JLabel("Color:");
			label.setBorder(border);
			addComponent(label, 3, 1);
			addComponent(gridStylePanel, 3, 2);
		}

		/**
		 * Updates the panel with the current graphical parameters.
		 */
		public void refresh() {
			cb_hg.setSelected(gs.drawGrid(gs.Y_AXIS));
			cb_vg.setSelected(gs.drawGrid(gs.X_AXIS));
			cb_back.setSelected(!gs.gridToFront());
			cb_front.setSelected(gs.gridToFront());
			actualColor = gs.getGridColor();
			lineStyle.setColor(actualColor);
		}

		public void setValues() {
			gs.setGridToFront(cb_front.isSelected());
			gs.setGridColor(actualColor);
			gs.setDrawGrid(gs.X_AXIS, cb_vg.isSelected());
			gs.setDrawGrid(gs.Y_AXIS, cb_hg.isSelected());
			jplot.updateGraphIfShowing();
			gs.setDataChanged(true);
		}
	}

	public class BoxOptions extends PanelGridUI {
		private SmallColorPreview graphFillColorPreview,
				triangleFillColorPreview;
		private SmallColorPreview boxFillColorPreview, panelFillColorPreview;
		private Color boxFillColor, triangleFillColor;
		private Color graphFillColor, panelFillColor;
		private Color boxLineColor;
		private JCheckBox cb_showBox, cb_hideBox;
		private JCheckBox cb_showInner, cb_hideInner;

		private JTextField offsetField;

		private JPanel graphFillPanel, boxFillPanel, panelFillPanel;
		private JPanel triangleFillPanel;
		private LineStyle lineStyle;

		// private GriddedPanel p;

		/**
		 * Principal constructor. Builds a panel with axes options.
		 * 
		 * @param gp
		 *            instance of the object containing all graphical parameters
		 */
		public BoxOptions() {
			setBorder(new EmptyBorder(new Insets(5, 5, 5, 5)));
			EmptyBorder border = new EmptyBorder(new Insets(0, 0, 0, 10));

			JButton b_color = new JButton(jplot.getImageIcon("color.jpg"));
			b_color.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Color color = getColor(panelFillColor);
					if (color != null) {
						panelFillColor = color;
						panelFillColorPreview.setColor(panelFillColor);
						panelFillPanel.repaint();
						setValues();
					}
				}
			});
			b_color.setMargin(new Insets(0, 0, 0, 0));
			b_color.setToolTipText("Set the overall background fill color");
			panelFillColorPreview = new SmallColorPreview(panelFillColor, 50,
					18);
			panelFillPanel = new JPanel(new FlowLayout());
			panelFillPanel.add(panelFillColorPreview);
			panelFillPanel.add(b_color);

			JLabel label = new JLabel("Panel background:");
			label.setBorder(border);
			addComponent(label, 1, 1);
			addComponent(panelFillPanel, 1, 2);

			b_color = new JButton(jplot.getImageIcon("color.jpg"));
			b_color.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Color color = getColor(graphFillColor);
					if (color != null) {
						graphFillColor = color;
						graphFillColorPreview.setColor(graphFillColor);
						graphFillPanel.repaint();
						setValues();
					}
				}
			});
			b_color.setMargin(new Insets(0, 0, 0, 0));
			b_color.setToolTipText("Set the graph background fill color");
			graphFillColorPreview = new SmallColorPreview(graphFillColor, 50,
					18);
			graphFillPanel = new JPanel(new FlowLayout());
			graphFillPanel.add(graphFillColorPreview);
			graphFillPanel.add(b_color);

			label = new JLabel("Graph background:");
			label.setBorder(border);
			addComponent(label, 2, 1);
			addComponent(graphFillPanel, 2, 2);

			b_color = new JButton(jplot.getImageIcon("color.jpg"));
			b_color.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Color color = getColor(boxFillColor);
					if (color != null) {
						boxFillColor = color;
						boxFillColorPreview.setColor(boxFillColor);
						boxFillPanel.repaint();
						setValues();
					}
				}
			});
			b_color.setMargin(new Insets(0, 0, 0, 0));
			b_color.setToolTipText("Set the box fill color");
			boxFillColorPreview = new SmallColorPreview(boxFillColor, 50, 18);
			boxFillPanel = new JPanel(new FlowLayout());
			boxFillPanel.add(boxFillColorPreview);
			boxFillPanel.add(b_color);

			b_color = new JButton(jplot.getImageIcon("color.jpg"));
			b_color.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Color color = getColor(boxLineColor);
					if (color != null) {
						boxLineColor = color;
						lineStyle.setColor(boxLineColor);
						lineStyle.repaint();
						setValues();
					}
				}
			});
			b_color.setMargin(new Insets(0, 0, 0, 0));
			b_color.setToolTipText("Set the axes color");

			lineStyle = new LineStyle(new LinePars());
			lineStyle.setColor(boxLineColor);
			JPanel linePanel = new JPanel(new FlowLayout());
			linePanel.add(lineStyle);
			linePanel.add(b_color);

			offsetField = new JTextField();
			offsetField.setColumns(4);
			JPanel offsetPanel = new JPanel(new BorderLayout());
			offsetPanel.add(Utils.makeLabel("offset"), BorderLayout.WEST);
			offsetPanel.add(offsetField, BorderLayout.CENTER);

			ButtonGroup bg = new ButtonGroup();
			cb_showBox = new JCheckBox("Show");
			cb_hideBox = new JCheckBox("Hide");
			bg.add(cb_showBox);
			bg.add(cb_hideBox);
			label = new JLabel("Bounding box:");
			label.setBorder(border);
			addComponent(label, 3, 1);
			addComponent(cb_showBox, 3, 2);
			addComponent(cb_hideBox, 3, 3);
			addComponent(offsetPanel, 4, 2);
			addComponent(boxFillPanel, 5, 2);
			addComponent(linePanel, 5, 3);

			if (gs.getGraphType() == gs.GRAPHTYPE_PIPER) {
				b_color = new JButton(jplot.getImageIcon("color.jpg"));
				b_color.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						Color color = getColor(triangleFillColor);
						if (color != null) {
							triangleFillColor = color;
							triangleFillColorPreview
									.setColor(triangleFillColor);
							triangleFillPanel.repaint();
							setValues();
						}
					}
				});
				b_color.setMargin(new Insets(0, 0, 0, 0));
				b_color.setToolTipText("Set the box fill color");
				triangleFillColorPreview = new SmallColorPreview(
						triangleFillColor, 50, 18);
				triangleFillPanel = new JPanel(new FlowLayout());
				triangleFillPanel.add(triangleFillColorPreview);
				triangleFillPanel.add(b_color);

				bg = new ButtonGroup();
				cb_showInner = new JCheckBox("Show");
				cb_hideInner = new JCheckBox("Hide");
				bg.add(cb_showInner);
				bg.add(cb_hideInner);
				label = new JLabel("Inner triangles:");
				label.setBorder(border);
				addComponent(label, 5, 1);
				addComponent(cb_showInner, 5, 2);
				addComponent(cb_hideInner, 5, 3);
				addComponent(triangleFillPanel, 6, 2);
			}
		}

		/**
		 * Updates the panel with the current graphical parameters.
		 */
		public void refresh() {
			panelFillColor = gs.getBackgroundColor();
			panelFillColorPreview.setColor(panelFillColor);
			graphFillColor = gs.getGraphBackgroundColor();
			graphFillColorPreview.setColor(graphFillColor);
			boxFillColor = gs.getBoxFillColor();
			boxFillColorPreview.setColor(boxFillColor);
			boxLineColor = gs.getBoxColor();
			lineStyle.setColor(boxLineColor);
			cb_showBox.setSelected(gs.drawBox());
			cb_hideBox.setSelected(!gs.drawBox());
			offsetField.setText(Float.toString(gs.getBoxOffset()));
			if (gs.getGraphType() == gs.GRAPHTYPE_PIPER) {
				triangleFillColor = gs.getInnerColor();
				triangleFillColorPreview.setColor(triangleFillColor);
				cb_showInner.setSelected(gs.drawInner());
				cb_hideInner.setSelected(!gs.drawInner());
			}
		}

		public void setValues() {
			gs.setBackgroundColor(panelFillColor);
			gs.setGraphBackgroundColor(graphFillColor);
			gs.setBoxFillColor(boxFillColor);
			gs.setBoxColor(boxLineColor);
			gs.setDrawBox(cb_showBox.isSelected());
			gs.setBoxOffset(Float.parseFloat(offsetField.getText()));
			if (gs.getGraphType() == gs.GRAPHTYPE_PIPER) {
				gs.setInnerColor(triangleFillColor);
				gs.setDrawInner(cb_showInner.isSelected());
			}
			jplot.updateGraphIfShowing();
			gs.setDataChanged(true);
		}
	}
}

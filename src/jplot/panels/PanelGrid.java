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

import jplot.GraphSettings;
import jplot.JPlot;
import jplot.LinePars;
import jplot.LineStyle;

/**
 * Creates a panel with options to set or use grid lines in the graph.
 * Calling the <code>show</code> method pops up a frame (dialog) which
 * includes the panel.
 */
public class PanelGrid extends PanelGridUI {

 private static final long serialVersionUID = 1L;
   private JDialog dialog;
  private JCheckBox cb_hg;
  private JCheckBox cb_vg;
  private JButton b_color;
  private Color actualColor;
  private PanelColor colorPanel;
  private LineStyle lineStyle;
  private JPanel stylePanel;
  private GraphSettings gs;
  private JPlot jplot;
  
  /**
   * Principal constructor. Builds a panel with grid options.
   * @param jp jplot instance
   * @param gs class with the global graphical parameters
   */
  public PanelGrid (JPlot jp, GraphSettings gs) {
    this.gs = gs;
    dialog = null;
    colorPanel = new PanelColor(jplot.frame);
    actualColor = gs.getGridColor();
    jplot = jp;

    EmptyBorder border = new EmptyBorder(new Insets(0,0,0,10));
    Dimension cbField = new Dimension(110,24);

    // build the widgets:
    cb_hg = new JCheckBox("Horizontal",gs.drawGrid(gs.Y_AXIS));
    cb_vg = new JCheckBox("Vertical",gs.drawGrid(gs.X_AXIS));
    lineStyle = new LineStyle(new LinePars());
    lineStyle.setColor(actualColor);
    b_color = new JButton(new ImageIcon("color.jpg"));
    b_color.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	Color color = colorPanel.show(jplot.frame,actualColor,100,100);
	if (color != null) {
	  actualColor = color;
	  lineStyle.setColor(actualColor);
	  lineStyle.repaint();
	}
      }
    });
    b_color.setMargin(new Insets(0,0,0,0));
    b_color.setToolTipText("Gridline color");
    stylePanel = new JPanel(new FlowLayout());
    stylePanel.add(lineStyle);
    stylePanel.add(b_color);

    // grid layout:
    JLabel label = new JLabel("Gridlines:");
    label.setBorder(border);
    addComponent(label,1,1);
    addComponent(cb_hg,1,2);
    addComponent(cb_vg,1,3);

    label = new JLabel("Gridcolor:");
    label.setBorder(border);
    addComponent(label,2,1);
    addComponent(stylePanel,2,2);
  }

  /**
   * Updates the panel with graph parameters.
   */
  public void refresh(GraphSettings gp) {
    cb_hg.setSelected(gs.drawGrid(gs.Y_AXIS));
    cb_vg.setSelected(gs.drawGrid(gs.X_AXIS));
  }

  /**
   * Return the current values
   */
  public void setValues(GraphSettings gp) {
    gs.setGridColor(actualColor);
    gs.setDrawGrid(gs.X_AXIS,cb_vg.isSelected());
    gs.setDrawGrid(gs.Y_AXIS,cb_hg.isSelected());
  }
  
  /**
   * Return a modal JDialog.
   */
  public void show(Frame parent, int x, int y) {
    if (dialog == null) {
      dialog = new JDialog(parent,"Labels",true);
      dialog.addWindowListener(new WindowAdapter() {
	public void windowClosing(WindowEvent e) {
	  dialog.dispose();
	}
      });
      dialog.getContentPane().add(this);
      dialog.setLocation(x,y);
      dialog.pack();
    }
    dialog.setVisible(true);  // blocks until user brings dialog down.
  }
}

